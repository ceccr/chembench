package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.ActiveUser;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.hibernate.Transaction;


@SuppressWarnings("serial")
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
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						clearOldGuestData();
						
					}
				}).start();
				
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
	
	public void clearOldGuestData(){
		try{
			Date current_date = new Date();
			Session session = HibernateUtil.getSession();
			String guestPath = Constants.CECCR_USER_BASE_PATH+"workflow-users/guest/";
			//getting all jobs for guest user
			List<JobStats> jobStatList = PopulateDataObjects.getJobStatsByUserName(session, "guest");
			List<JobStats> datasetJobs = new ArrayList<JobStats>();
			List<JobStats> modelingJobs = new ArrayList<JobStats>();
			List<JobStats> predictionJobs = new ArrayList<JobStats>();
			//populating a list of all the jobs for user guest that are older than 3 hours
			for(JobStats js: jobStatList){
				if(js.getTimeFinished() != null && js.getTimeStarted() != null){
					if(js.getTimeFinished().getTime() < current_date.getTime()-Constants.GUEST_DATA_EXPIRATION_TIME){
						if(js.getJobType().equals("DATASET")) datasetJobs.add(js);
						if(js.getJobType().equals("MODELING")) modelingJobs.add(js);
						if(js.getJobType().equals("PREDICTION")) predictionJobs.add(js);
					}
					
				}
			}
			
			//get all predictions based on the previous data
			for(JobStats js: predictionJobs){
				Utility.writeToDebug("+++++++++++++++DELETE_PREDICTIONS++++++++++++++");
				if(new File(guestPath+"PREDICTIONS/"+js.getJobName()).exists()) Utility.writeToDebug("DELETE:::"+guestPath+"PREDICTIONS/"+js.getJobName());//FileAndDirOperations.deleteDir(new File(guestPath+"PREDICTIONS/"+js.getJobName()));
				Prediction p = PopulateDataObjects.getPredictionByName(js.getJobName(),"guest",session);
				if(p!=null){
					ArrayList<PredictionValue> pvs = (ArrayList<PredictionValue>) PopulateDataObjects.getPredictionValuesByPredictionId(p.getId(), session);
					Utility.writeToDebug("DELETE_DB:::"+p.getName()+"......"+pvs.size());
					if(pvs != null){
						for(PredictionValue pv : pvs){
							Transaction tx = null;
							try{
								tx = session.beginTransaction();
							    session.delete(pv);
								tx.commit();
							}
							catch (RuntimeException e) {
								if (tx != null)
									tx.rollback();
								Utility.writeToDebug(e);
							}
						}
					}
					
					//delete the database entry for the prediction
					Transaction tx = null;
					try{
						tx = session.beginTransaction();
					    session.delete(p);
						tx.commit();
					}catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);
					}
					session.close();
				}
					
			}
			
			//get all predictors based on the previous data
			for(JobStats js: modelingJobs){
				Utility.writeToDebug("+++++++++++++++DELETE_PREDICTORS++++++++++++++");
				if(new File(guestPath+"PREDICTORS/"+js.getJobName()).exists()) Utility.writeToDebug("DELETE:::"+guestPath+"PREDICTORS/"+js.getJobName());//FileAndDirOperations.deleteDir(new File(guestPath+"PREDICTIONS/"+js.getJobName()));
				Predictor p = PopulateDataObjects.getPredictorByName(js.getJobName(),"guest",session);
				if(p!=null){
					Utility.writeToDebug("DELETE_DB:::"+p.getName());
					new DeleteAction().deletePredictor(p, session);
				}
	
			}
			
			//get all datasets based on the previous data
			for(JobStats js: datasetJobs){
				Utility.writeToDebug("+++++++++++++++DELETE_DATASETS++++++++++++++");
				if(new File(guestPath+"DATASETS/"+js.getJobName()).exists()) Utility.writeToDebug("DELETE:::"+guestPath+"DATASETS/"+js.getJobName());//FileAndDirOperations.deleteDir(new File(guestPath+"PREDICTIONS/"+js.getJobName()));
				DataSet ds = PopulateDataObjects.getDataSetByName(js.getJobName(),"guest",session);
				if(ds!=null){
					Utility.writeToDebug("DELETE_DB:::"+ds.getName());
					Transaction tx = null;
					try{
						tx = session.beginTransaction();
					    session.delete(ds);
						tx.commit();
					}catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);
					}
	
					session.close();
				}
			}
		
			
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
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