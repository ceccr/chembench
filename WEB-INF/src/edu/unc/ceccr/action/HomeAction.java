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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

public class HomeAction extends ActionSupport {
	//loads home page

	String visitors;
	String userStats;
	String jobStats;
	String cpuStats;
	String activeUsers;
	String runningJobs;
	
	User user;
	
	String showStatistics = "YES"; 
	
	public String loadPage(){
		try {
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
				String counterStr = FileAndDirOperations.readFileIntoString(counterFile.getAbsolutePath());
				counter = Integer.parseInt(counterStr);
				FileAndDirOperations.writeStringToFile("" + (counter+1), counterFile.getAbsolutePath());
			}
			visitors = "Visitors: " + Integer.toString(counter);
			
			// number of registereed users
			userStats = "Users: " + users.size();
	
			// finished jobs
			int numFinishedJobs = jobStatList.size();
			jobStats = "Jobs completed: "  + numJobs;
	
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
			activeUsers = "Current Users: ";
	
			// current number of jobs
			runningJobs = "Running Jobs: " + numJobs;

		}
		catch(Exception ex){
			showStatistics = "NO";
		}
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
	
}