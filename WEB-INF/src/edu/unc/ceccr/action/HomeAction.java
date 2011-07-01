package edu.unc.ceccr.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.ActiveUser;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class HomeAction extends ActionSupport implements ServletResponseAware {
	
	//loads home page
	
	protected HttpServletResponse servletResponse;
	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}
	
	String visitors;
	String userStats;
	String jobStats;
	String cpuStats;
	String activeUsers;
	String runningJobs;
	String loginFailed = Constants.NO;
	User user;
	
	String username;
	String password;
	
	String showStatistics = Constants.YES; 
	
	public String loadPage(){
		try {
			
			//stuff that needs to happen on server startup
			String debugText = "";
			if(Constants.doneReadingConfigFile)
			{
				debugText = "already read config file (?)";
			}
			else{
				try{
					//STATIC PATH we didn't know how to make it dynamic in Struts 2
					String path = "/usr/local/ceccr/tomcat6/webapps/ROOT/WEB-INF/systemConfig.xml";
					Utility.readBuildDateAndSystemConfig(path);
				}
				catch(Exception ex){
					debugText += ex.getMessage();
				}
			}
			FileAndDirOperations.writeStringToFile(debugText, "/usr/local/ceccr/deploy/debug-log.txt");
			
			//start up the queues, if they're not running yet
			CentralDogma.getInstance();
			
			//check if user is logged in
			ActionContext context = ActionContext.getContext();
			user = (User) context.getSession().get("user");

			//populate each string for the statistics section
			Session s = HibernateUtil.getSession();
			int numJobs = PopulateDataObjects.populateClass(Job.class, s).size();
			List<User> users = PopulateDataObjects.getUsers(s);
			List<JobStats> jobStatList = PopulateDataObjects.getJobStats(s);
			s.close();
			
			// cumulative visitors to the site
			int counter = 0;
			File counterFile = new File(Constants.CECCR_USER_BASE_PATH + "counter.txt");
			if (counterFile.exists()) {
				String counterStr = FileAndDirOperations.readFileIntoString(counterFile.getAbsolutePath()).trim();
				counter = Integer.parseInt(counterStr);
				FileAndDirOperations.writeStringToFile("" + (counter+1), counterFile.getAbsolutePath());
			}
			visitors = "Visitors: " + Integer.toString(counter);
			
			// number of registered users
			userStats = "Users: " + users.size();
	
			// finished jobs
			int numFinishedJobs = jobStatList.size();
			jobStats = "Jobs completed: "  + numFinishedJobs;
	
			// CPU statistics
			int computeHours = 0;
			String computeYearsStr = "";
			long timeDiffs = 0;
				
			for(JobStats js: jobStatList){
				if(js.getTimeFinished() != null && js.getTimeStarted() != null){
					timeDiffs += js.getTimeFinished().getTime() - js.getTimeCreated().getTime();	
				}
			}
			int timeDiffInHours = Math.round(timeDiffs / 1000 / 60 / 60);
			computeHours = timeDiffInHours;
			float computeHoursf = computeHours;
			float computeYears = computeHoursf / new Float(24.0*365.0);
			computeYearsStr = Utility.floatToString(computeYears);
			Utility.roundSignificantFigures(computeYearsStr, 4);
			cpuStats = "Compute time used: "  + computeYearsStr + " years";
			
			// current users
			activeUsers = "Current Users: " + ActiveUser.getActiveSessions();
	
			// current number of jobs
			runningJobs = "Running Jobs: " + numJobs;

		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
			showStatistics = "NO";
		}
		return SUCCESS;
	}
	
	public String execute() throws Exception {
		//log the user in
		String result = SUCCESS; 

		//check username and password
		ActionContext context = ActionContext.getContext();
		
		if(context.getParameters().get("username") != null){
			username = ((String[]) context.getParameters().get("username"))[0];
		}
		
		Session s = HibernateUtil.getSession();
		User user = PopulateDataObjects.getUserByUserName(username, s);
		s.close();
		
		if(user != null){
			String realPasswordHash = user.getPassword();
			
			if (password != null && Utility.encrypt(password).equals(realPasswordHash)){
				context.getSession().put("user", user);
				Cookie ckie=new Cookie("login","true");
				servletResponse.addCookie(ckie);
				
				Utility.writeToUsageLog("Logged in", user.getUserName());
			}
			else if(user.getUserName().equals("guest")){
				Utility.writeToUsageLog("Logged in", user.getUserName());
				context.getSession().put("user", user);
				Cookie ckie=new Cookie("login","true");
				servletResponse.addCookie(ckie);
				
				Utility.writeToUsageLog("Logged in", user.getUserName());
			}
			else{
				loginFailed = Constants.YES;
			}
		}
		else{
			loginFailed = Constants.YES;
		}
		loadPage();
		return result;
	}
	
	public String logout() throws Exception{
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		if(user != null){
			Utility.writeToUsageLog("Logged out.", user.getUserName());
		}
		
		context.getSession().clear();
		
		Cookie ckie=new Cookie("login","false");
		servletResponse.addCookie(ckie);
		
		loadPage();
		return SUCCESS;
	}
	

	public String getVisitors() {
		return visitors;
	}

	public void setVisitors(String visitors) {
		this.visitors = visitors;
	}

	public String getUserStats() {
		return userStats;
	}

	public void setUserStats(String userStats) {
		this.userStats = userStats;
	}

	public String getJobStats() {
		return jobStats;
	}

	public void setJobStats(String jobStats) {
		this.jobStats = jobStats;
	}

	public String getCpuStats() {
		return cpuStats;
	}

	public void setCpuStats(String cpuStats) {
		this.cpuStats = cpuStats;
	}

	public String getActiveUsers() {
		return activeUsers;
	}

	public void setActiveUsers(String activeUsers) {
		this.activeUsers = activeUsers;
	}

	public String getRunningJobs() {
		return runningJobs;
	}

	public void setRunningJobs(String runningJobs) {
		this.runningJobs = runningJobs;
	}

	public String getShowStatistics() {
		return showStatistics;
	}

	public void setShowStatistics(String showStatistics) {
		this.showStatistics = showStatistics;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginFailed() {
		return loginFailed;
	}

	public void setLoginFailed(String loginFailed) {
		this.loginFailed = loginFailed;
	}
	
}