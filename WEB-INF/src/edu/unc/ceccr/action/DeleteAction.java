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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

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

		Session session = HibernateUtil.getSession();
		String userName = ds.getUserName();
		ArrayList<Predictor> userPredictors = (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(userName, true, false, session);
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);
		
		//check each predictor
		for(int i = 0; i < userPredictors.size();i++){
			if(userPredictors.get(i).getDatasetId() == ds.getFileId()){
				dependsMsg += "The predictor '" + userPredictors.get(i).getName() + "' depends on this dataset.\n";
			}
		}
		
		//check each prediction
		for(int i = 0; i < userPredictions.size();i++){
			if(userPredictions.get(i).getDatasetId() == ds.getFileId()){
				dependsMsg += "The prediction '" + userPredictions.get(i).getJobName() + "' depends on this dataset.\n";
			}
		}
		
		//check each job
		List<String> jobnames = PopulateDataObjects.populateTaskNames(userName, true, session);
		List<QueueTask> queuedtasks  = PopulateDataObjects.populateTasks(userName, false, session);
		session.close();
		
		//todo: Actually check the jobs! Needs some revision of how jobs work first.
		
		return dependsMsg;
	}

	private String checkPredictorDependencies(Predictor p)throws ClassNotFoundException, SQLException{
		//make sure there are no predictions or prediction jobs that depend on this predictor
		
		String userName = p.getUserName();
		Session session = HibernateUtil.getSession();
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);
		session.close();
		String dependsMsg = "";
		
		//check each prediction
		for(int i = 0; i < userPredictions.size();i++){
			Prediction prediction = userPredictions.get(i);
			String[] predictorIds = prediction.getPredictorIds().split("\\s+");
			for(int j = 0; j < predictorIds.length; j++){
				if(Long.parseLong(predictorIds[j]) == p.getPredictorId()){
					dependsMsg += "The prediction '" + userPredictions.get(i).getJobName() + "' depends on this predictor.\n";
				}
			}
		}
		
		//todo: check running jobs (once jobs have been fixed up)
		
		return dependsMsg;
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
	
	public String deleteDataset() throws Exception{

		ActionContext context = ActionContext.getContext();
		
		String datasetId;
		DataSet ds = null;
		
		datasetId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting dataset with id: " + datasetId);

		if(datasetId == null){
			Utility.writeToStrutsDebug("No dataset ID supplied.");
			return ERROR;
		}

		Session session = HibernateUtil.getSession();
		ds = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId),session);
		
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
		if((new File(dir)).exists()){
			if(! FileAndDirOperations.deleteDir(new File(dir))){
				Utility.writeToStrutsDebug("error deleting dir: " + dir);
				return ERROR;
			}
		}
		
		//delete the database entry for the dataset
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
		    session.delete(ds);
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
			return ERROR;
		}
		finally{
			session.close();
		}
		
		return SUCCESS;
	}
	
	public String deletePredictor() throws Exception{

		ActionContext context = ActionContext.getContext();
		
		String predictorId;
		Predictor p = null;
		
		predictorId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting predictor with id: " + predictorId);

		if(predictorId == null){
			Utility.writeToStrutsDebug("No predictor ID supplied.");
			return ERROR;
		}

		Session session = HibernateUtil.getSession();
		p = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
		
		if(p == null){
			Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
		}
		
		if(! checkPermissions(p.getUserName())){
			Utility.writeToStrutsDebug("User does not own this predictor - cannot delete.");
			return ERROR;
		}
		
		//make sure nothing else depends on this predictor existing
		String depends = checkPredictorDependencies(p);
		if(! depends.equals("")){
			Utility.writeToStrutsDebug(depends);
			return ERROR;
		}

		//delete the files associated with this predictor
		String dir = Constants.CECCR_USER_BASE_PATH+p.getUserName()+"/PREDICTORS/"+p.getName();
		if(! FileAndDirOperations.deleteDir(new File(dir))){
			Utility.writeToStrutsDebug("error deleting dir: " + dir);
			return ERROR;
		}
		
		//delete the database entry for the dataset
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
		finally{
			session.close();
		}
		
		
		return SUCCESS;
	}

	public String deletePrediction() throws Exception{

		ActionContext context = ActionContext.getContext();
		
		String predictionId;
		Prediction p = null;
		
		predictionId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting prediction with id: " + predictionId);

		if(predictionId == null){
			Utility.writeToStrutsDebug("No prediction ID supplied.");
			return ERROR;
		}

		Session session = HibernateUtil.getSession();
		p = PopulateDataObjects.getPredictionById(Long.parseLong(predictionId), session);
		if(p == null){
			Utility.writeToStrutsDebug("Invalid prediction ID supplied.");
		}
		
		if(! checkPermissions(p.getUserName())){
			Utility.writeToStrutsDebug("User does not own this prediction - cannot delete.");
			return ERROR;
		}
		
		//delete the files associated with this prediction
		String dir = Constants.CECCR_USER_BASE_PATH+p.getUserName()+"/PREDICTIONS/"+p.getJobName();
		if(! FileAndDirOperations.deleteDir(new File(dir))){
			Utility.writeToStrutsDebug("error deleting dir: " + dir);
			return ERROR;
		}
		
		//delete the database entry for the dataset
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
		finally{
			session.close();
		}
		
		return SUCCESS;
	}
	
	public String deleteJob() throws Exception{
		//stops the job and removes all associated files
		
		ActionContext context = ActionContext.getContext();
		
		String taskId;
		
		taskId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting job with id: " + taskId);

		Session session = HibernateUtil.getSession();
		QueueTask task = PopulateDataObjects.getTaskById(Long.parseLong(taskId), session);
		Queue queue = Queue.getInstance();
		task.setState(QueueTask.State.deleted);
		
		//remove associated files
		//this has a side-effect. If any programs are operating on these files
		//(such as sketch generation or kNN processes)
		//the deletion of the files will, in practice, kill the kNN process and free up
		//the processing resources for something else.
		//It's dirty, but it works. 
		String BASE=Constants.CECCR_USER_BASE_PATH;
		File file=new File(BASE+task.getUserName()+"/"+task.jobName);
		FileAndDirOperations.deleteDir(file);

		file=new File(BASE+task.getUserName()+"/DATASETS/"+task.jobName);
		FileAndDirOperations.deleteDir(file);

		file=new File(BASE+task.getUserName()+"/PREDICTORS/"+task.jobName);
		FileAndDirOperations.deleteDir(file);
		
		file=new File(BASE+task.getUserName()+"/PREDICTIONS/"+task.jobName);
		FileAndDirOperations.deleteDir(file);

		//Once the files are removed, whatever program is running will soon die.
		//flag the task for removal so it will be cleaned up instead of sitting around
		//as an "error".
		queue.deleteTask(task);
		return SUCCESS;
		
	}
	
}
