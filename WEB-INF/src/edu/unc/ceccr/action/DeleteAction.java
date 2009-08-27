package edu.unc.ceccr.action;

import java.io.File;
import java.sql.SQLException;
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
import org.hibernate.Transaction;

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;


public class DeleteAction extends ActionSupport{

	
	private String checkDatasetDependencies(DataSet ds)throws ClassNotFoundException, SQLException{
		//make sure there are no predictors, predictions, or jobs that depend on this dataset
		String dependsMsg = "";
		
		ArrayList<Predictor> userPredictors = (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(ds.getUserName(), true, false);
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(ds.getUserName(), false);
		
		for(int i = 0; i < userPredictors.size();i++){
			if(userPredictors.get(i).getDatasetId() == ds.getFileId()){
				dependsMsg +=;
			}
		}
		
		List<String> jobnames = PopulateDataObjects.populateTaskNames(userName, true);
		List<QueueTask> queuedtasks  = PopulateDataObjects.populateTasks(userName, false);
		for(int i=0;i<queuedtasks.size();i++){
			Utility.writeToMSDebug("JobNames::"+queuedtasks.get(i).getJobName()+"=="+queuedtasks.get(i).task);
			if(queuedtasks.get(i).getJobName().equals(fileName) && queuedtasks.get(i).getState().equals(Queue.QueueTask.State.ready)){
				return fileName; 
			}
			else if(queuedtasks.get(i).getJobName().equals(fileName + "_sketches_generation")&& queuedtasks.get(i).getState().equals(Queue.QueueTask.State.ready)){
				return fileName+"_sketches_generation"; 
			}
		}
		
		for(int i=0;i<jobnames.size();i++){
			Utility.writeToMSDebug("RunningJobNames::"+queuedtasks.get(i).getJobName());
			if(jobnames.get(i).equals(fileName)){
				return fileName; 
			}
			else if(jobnames.get(i).equals(fileName+"_sketches_generation")){
				return fileName+"_sketches_generation"; 
			}
		}
		return dependsMsg;
	}

	private String checkPredictorDependencies(DataSet ds)throws ClassNotFoundException, SQLException{
		//make sure there are no predictions or prediction jobs that depend on this predictor
		
		return "";
	}
	
	
	private boolean checkPermissions(String objectUser){
		//make sure the user has permissions to delete this object
		
		ActionContext context = ActionContext.getContext();
		
		//check that there is a user logged in
		User user = null;
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
			return false;
		}
		user = (User) context.getSession().get("user");
		if(user == null){
			Utility.writeToStrutsDebug("No user logged in.");
			return false;
		}
		
		//make sure the user can actually delete this object
		if(user.getUserName().equalsIgnoreCase(objectUser)){
			return true;
		}
		
		return false;
	}
	
	public String deleteDatasetAction() throws Exception{

		ActionContext context = ActionContext.getContext();
		
		String datasetId;
		DataSet ds = null;
		
		datasetId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting dataset with id: " + datasetId);

		if(datasetId == null){
			Utility.writeToStrutsDebug("No dataset ID supplied.");
			return ERROR;
		}

		ds = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId));
		if(ds == null){
			Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
		}
		
		if(! checkPermissions(ds.getUserName())){
			Utility.writeToStrutsDebug("User does not own this dataset - cannot delete.");
			return ERROR;
		}
		
		//make sure nothing else depends on this dataset existing
		String depends = checkDatasetDependencies(ds);
		if(! depends.equals("")){
			Utility.writeToStrutsDebug(depends);
			return ERROR;
		}

		//delete the files associated with this dataset
		String dir = Constants.CECCR_USER_BASE_PATH+ds.getUserName()+"/DATASETS/"+ds.getFileName();
		if(! FileAndDirOperations.deleteDir(new File(dir))){
			Utility.writeToStrutsDebug("error deleting dir: " + dir);
			return ERROR;
		}
		
		//delete the database entry for the dataset
		Session session = HibernateUtil.getSession();
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
		
		return SUCCESS;
	}
	
	public String deletePredictorAction(){
		
		
		return SUCCESS;
	}

	public String deletePredictionAction(){
		
		
		return SUCCESS;
	}
	
	public String deleteJobAction(){
		
		
		return SUCCESS;
	}
	
}
