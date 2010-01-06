package edu.unc.ceccr.action;

import java.util.ArrayList;
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
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.task.Task;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;

public class JobsActions extends ActionSupport {

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

		Thread.sleep(500);
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		Session session = HibernateUtil.getSession();
		userDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, true, session);
		userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, true, session));
		userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.PREDICTION, true, session));
		userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), true, false, session);
		userPredictions = PopulateDataObjects.populatePredictions(user.getUserName(), false, session);
		
		//load the queue
		userQueueTasks = new ArrayList<QueueTask>();
		if(Queue.getInstance().runningTask != null){
			QueueTask t = Queue.getInstance().runningTask;
			if(t != null && t.task != null){
				Utility.writeToDebug("running task: " + t.jobName);
				if(t.task != null){
					t.setMessage(t.task.getProgress());
					userQueueTasks.add(Queue.getInstance().runningTask);
				}
			}
		}
		Iterator<QueueTask> queuedTasks = Queue.queue.iterator();
		while(queuedTasks.hasNext()){
			QueueTask qt = PopulateDataObjects.getTaskById(queuedTasks.next().id, session);
			if(qt != null){
				Utility.writeToDebug("queued task: " + qt.jobName);
				userQueueTasks.add(qt);
			}
		}
		Iterator<QueueTask> errorTasks = Queue.errorqueue.iterator();
		while(errorTasks.hasNext()){
			QueueTask qt = PopulateDataObjects.getTaskById(errorTasks.next().id, session);
			if(qt != null){
				Utility.writeToDebug("error task: " + qt.jobName);
				userQueueTasks.add(qt);
			}
		}	
		session.close();
		
		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to jobs page.");
		}
		else{
			Utility.writeToStrutsDebug("Cannot load page.");
		}

		Utility.writeToDebug("finished loading jobs page.");
		//go to the page
		return result;
	}
	public String execute() throws Exception {
		return SUCCESS;
	}
	
	//====== variables used for displaying the JSP =====//
	private User user;
		
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	//====== variables used to hold the queue and finished jobs information =====//
	private List<DataSet> userDatasets;
	private List<Predictor> userPredictors;
	private List<Prediction> userPredictions;
	private List<QueueTask> userQueueTasks;
	
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
		
	public List<QueueTask> getUserQueueTasks(){
		return userQueueTasks;
	}
	public void setUserQueueTasks(List<QueueTask> userQueueTasks) {
		this.userQueueTasks = userQueueTasks;
	}

	
}