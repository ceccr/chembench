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
		
		String userName = ds.getUserName();
		ArrayList<Predictor> userPredictors = (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(userName, true, false);
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false);
		
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
		List<String> jobnames = PopulateDataObjects.populateTaskNames(userName, true);
		List<QueueTask> queuedtasks  = PopulateDataObjects.populateTasks(userName, false);
		
		//todo: Actually check the jobs! Needs some revision of how jobs work first.
		/*
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
		*/
		return dependsMsg;
	}

	private String checkPredictorDependencies(Predictor p)throws ClassNotFoundException, SQLException{
		//make sure there are no predictions or prediction jobs that depend on this predictor
		
		String userName = p.getUserName();
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false);
		String dependsMsg = "";
		
		//check each prediction
		for(int i = 0; i < userPredictions.size();i++){
			if(userPredictions.get(i).getPredictorId() == p.getPredictorId()){
				dependsMsg += "The prediction '" + userPredictions.get(i).getJobName() + "' depends on this predictor.\n";
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

		p = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId));
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
		Session session = HibernateUtil.getSession();
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

		p = PopulateDataObjects.getPredictionById(Long.parseLong(predictionId));
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
		Session session = HibernateUtil.getSession();
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
		
		return SUCCESS;
	}
	
	public String deleteJob() throws Exception{
		//stops the job and removes all associated files

		//Food for thought: What if a dataset job is deleted partway through executing?
		//Could be a problem.
		
		ActionContext context = ActionContext.getContext();
		
		String taskId;
		
		taskId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting job with id: " + taskId);
		
		QueueTask task = PopulateDataObjects.getTaskById(Long.parseLong(taskId));
		Queue queue = Queue.getInstance();
		
		
		if(queue.runningTask != null && task.jobName.equals(queue.runningTask.jobName) && task.getUserName().equals( queue.runningTask.getUserName())){
			Utility.writeToDebug("Job " + task.jobName + " is currently executing.");
			//Right now there's no way to kill running jobs. 
			
			//check if kNN is running. Kill it if it is.
			/*if(){
				
			}
			else{
				
			}*/
		}
		else{
			Utility.writeToDebug("Job " + task.jobName + " is not executing.");
		}

		//remove associated files
		String BASE=Constants.CECCR_USER_BASE_PATH;
		File file=new File(BASE+task.getUserName()+"/"+task.jobName);
		FileAndDirOperations.deleteDir(file);

		file=new File(BASE+task.getUserName()+"/DATASETS/"+task.jobName);
		FileAndDirOperations.deleteDir(file);

		file=new File(BASE+task.getUserName()+"/PREDICTORS/"+task.jobName);
		FileAndDirOperations.deleteDir(file);
		
		file=new File(BASE+task.getUserName()+"/PREDICTIONS/"+task.jobName);
		FileAndDirOperations.deleteDir(file);

		//remove the task. Gotta do this last.
		queue.deleteTask(task);
		
		return SUCCESS;
		
		/*public class DeleteRecordAction extends Action {

	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward();

		HttpSession session = request.getSession(false); 
		if (session == null ){
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}
		else{
			String taskName=request.getParameter("jobName");
			try{
				deleteTask(taskName);
			}catch(SQLException e)
			{Utility.writeToDebug(e);
			forward = mapping.findForward("failure");
			}catch(HibernateException e)
			{
				Utility.writeToDebug(e);
				forward = mapping.findForward("failure");
			}
			
			forward = mapping.findForward("success");
			}
		return forward;
	}
	
	public void deleteTask(String taskName)throws HibernateException,ClassNotFoundException, SQLException
	{
		Queue.QueueTask task=null;
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			task=(QueueTask)s.createCriteria(QueueTask.class).add(Expression.eq("jobName", taskName)).uniqueResult();
			s.delete(task);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
	}
	
}*/
		
	}
	
}
