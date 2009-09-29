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

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
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
			}
		}

		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		userDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, true);
		userDatasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, true));
		userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), true, false);
		userPredictions = PopulateDataObjects.populatePredictions(user.getUserName(), false);
		
		userQueueTasks = new ArrayList<QueueTask>();
		Iterator<QueueTask> runningTasks = Queue.queue.iterator();
		while(runningTasks.hasNext()){
			QueueTask qt = runningTasks.next();
			userQueueTasks.add(qt);
		}
		Iterator<QueueTask> finishedTasks = Queue.finished.iterator();
		while(finishedTasks.hasNext()){
			QueueTask qt = finishedTasks.next();
			//if(qt.getState().compareTo(QueueTask.State.finished) != 0){
				//finished tasks are handled elsewhere on the page. (Actually they shouldn't exist as tasks... but we'll fix that later.)
				userQueueTasks.add(qt);
			//}
		}	
		if(Queue.getInstance().runningTask != null){
			userQueueTasks.add(Queue.getInstance().runningTask);
		}
		
		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to jobs page.");
		}
		else{
			Utility.writeToStrutsDebug("Cannot load page.");
		}
		
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