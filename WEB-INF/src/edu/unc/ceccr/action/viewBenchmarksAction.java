package edu.unc.ceccr.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class viewBenchmarksAction extends ActionSupport {

	public String loadPage() throws Exception {
		
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
		}
		
		if(Utility.isAdmin(user.getUserName())){
			adminUser = true;
		}
		else{
			adminUser = false;
		}
		
		Thread.sleep(200);
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		Session session = HibernateUtil.getSession();
		
		//get datasets
		if(user.getShowPublicDatasets().equals(Constants.ALL)){	
			//get the user's datasets and all public ones
			userDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, true, session);
			userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, true, session));
			userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.PREDICTION, true, session));
		}
		else if(user.getShowPublicDatasets().equals(Constants.NONE)){
			//just get the user's datasets
			userDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, false, session);
			userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, false, session));
			userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.PREDICTION, false, session));
		}
		else if(user.getShowPublicDatasets().equals(Constants.SOME)){
			//get all the datasets and filter out all the public ones that aren't "show by default"
			userDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, true, session);
			userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, true, session));
			userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.PREDICTION, true, session));
			
			for(int i = 0; i < userDatasets.size(); i++){
				String s = userDatasets.get(i).getShowByDefault();
				if(s != null && s.equals(Constants.NO)){
					userDatasets.remove(i);
					i--;
				}
			}
		}
		if(userDatasets != null){
			Collections.sort(userDatasets, new Comparator<DataSet>() {
			    public int compare(DataSet d1, DataSet d2) {
		    		return d1.getName().toLowerCase().compareTo(d2.getName().toLowerCase());
			    }});
	
			for(int i = 0; i < userDatasets.size(); i++){
				if(userDatasets.get(i).getJobCompleted() == null || userDatasets.get(i).getJobCompleted().equals(Constants.NO)){
					userDatasets.remove(i);
					i--;
				}
			}
		}		
		//get predictors
		if(user.getShowPublicPredictors().equals(Constants.ALL)){	
			//get the user's predictors and all public ones
			userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), true, true, session);
		}
		else{
			//just get the user's predictors
			userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), false, true, session);
		}
		if(userPredictors != null){
			Collections.sort(userPredictors, new Comparator<Predictor>() {
			    public int compare(Predictor p1, Predictor p2) {
		    		return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
			    }});
		}
		
		//get predictions
		userPredictions = PopulateDataObjects.populatePredictions(user.getUserName(), false, session);
		if(userPredictions != null){
			Collections.sort(userPredictions, new Comparator<Prediction>() {
			    public int compare(Prediction p1, Prediction p2) {
		    		return p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
			    }});
			
			for(int i = 0; i < userPredictions.size(); i++){
				if(userPredictions.get(i).getJobCompleted() == null || userPredictions.get(i).getJobCompleted().equals(Constants.NO)){
					userPredictions.remove(i);
					i--;
				}
			}
		}
		
		//get local jobs
		localJobs = CentralDogma.getInstance().localJobs.getReadOnlyCopy();
		for(Job j : localJobs){
			if(j.workflowTask != null){
				j.setMessage(j.workflowTask.getProgress(user.getUserName()));
			}
			else{
				j.setMessage("Could not resume task.");
			}
		}
		
		//get lsf jobs
		lsfJobs = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();
		for(Job j : lsfJobs){
			if(j.workflowTask != null){
				j.setMessage(j.workflowTask.getProgress(user.getUserName()));
			}
			else{
				j.setMessage("Could not resume task.");
			}
		}

		//get incoming jobs
		incomingJobs = CentralDogma.getInstance().incomingJobs.getReadOnlyCopy();
		for(Job j : incomingJobs){
			if(j.workflowTask != null){
				j.setMessage("Waiting in queue");
			}
		}
		
		//get error jobs
		errorJobs = CentralDogma.getInstance().errorJobs.getReadOnlyCopy();

		//error jobs should be only appear to admins and to the user that
		//created the job.
		if(! Utility.isAdmin(user.getUserName())){
			for(int i = 0; i < errorJobs.size(); i++){
				if(! errorJobs.get(i).getUserName().equals(user.getUserName())){
					errorJobs.remove(i);
					i--;
				}
			}
		}		
		session.close();

		Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to jobs page.");
		
		return result;
	}
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	//====== variables used for displaying the JSP =====//
	private User user;
	private boolean adminUser;	
	
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isAdminUser() {
		return adminUser;
	}
	public void setAdminUser(boolean adminUser) {
		this.adminUser = adminUser;
	}
	

	//====== variables used to hold the queue and finished jobs information =====//
	private List<DataSet> userDatasets;
	private List<Predictor> userPredictors;
	private List<Prediction> userPredictions;

	private List<Job> incomingJobs;
	private List<Job> lsfJobs;
	private List<Job> localJobs;
	private List<Job> errorJobs;
	
	
	
	public List<DataSet> getUserDatasets(){
		return userDatasets;
	}
	public void setUserDatasets(List<DataSet> userDatasets) {
		this.userDatasets = userDatasets;
	}
	
	public List<Predictor> getUserPredictors(){
		return userPredictors;
	}
	public void setUserPredictors(List<Predictor> userPredictors) {
		this.userPredictors = userPredictors;
	}
	
	public List<Prediction> getUserPredictions(){
		return userPredictions;
	}
	public void setUserPredictions(List<Prediction> userPredictions) {
		this.userPredictions = userPredictions;
	}
	
	public List<Job> getIncomingJobs() {
		return incomingJobs;
	}
	public void setIncomingJobs(List<Job> incomingJobs) {
		this.incomingJobs = incomingJobs;
	}
	
	public List<Job> getLsfJobs() {
		return lsfJobs;
	}
	public void setLsfJobs(List<Job> lsfJobs) {
		this.lsfJobs = lsfJobs;
	}
	
	public List<Job> getLocalJobs() {
		return localJobs;
	}
	public void setLocalJobs(List<Job> localJobs) {
		this.localJobs = localJobs;
	}

	public List<Job> getErrorJobs() {
		return errorJobs;
	}
	public void setErrorJobs(List<Job> errorJobs) {
		this.errorJobs = errorJobs;
	}
}