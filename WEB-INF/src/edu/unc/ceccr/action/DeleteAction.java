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
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;


public class DeleteAction extends ActionSupport{
	
	public ArrayList<String> errorStrings = new ArrayList<String>();

	private void checkDatasetDependencies(DataSet ds)throws ClassNotFoundException, SQLException{
		//make sure there are no predictors, predictions, or jobs that depend on this dataset
		Utility.writeToDebug("checking dataset dependencies");
		
		Session session = HibernateUtil.getSession();
		String userName = ds.getUserName();
		ArrayList<Predictor> userPredictors = (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(userName, true, false, session);
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);
		
		//check each predictor
		for(int i = 0; i < userPredictors.size();i++){
			Utility.writeToDebug("predictor id: " + userPredictors.get(i).getDatasetId() + " dataset id: " + ds.getFileId());
			if(userPredictors.get(i).getDatasetId() != null && userPredictors.get(i).getDatasetId().equals(ds.getFileId())){
				errorStrings.add("The predictor '" + userPredictors.get(i).getName() + "' depends on this dataset. Please delete it first.\n");
			}
		}
		
		//check each prediction
		for(int i = 0; i < userPredictions.size();i++){
			Utility.writeToDebug("Prediction id: " + userPredictions.get(i).getDatasetId() + " dataset id: " + ds.getFileId());
			if(userPredictions.get(i).getDatasetId() != null && userPredictions.get(i).getDatasetId().equals(ds.getFileId())){
				Utility.writeToDebug("");
				errorStrings.add("The prediction '" + userPredictions.get(i).getJobName() + "' depends on this dataset. Please delete it first.\n");
			}
		}
		
		//check each job
		//Actually, we don't need to check the jobs.
		//When a modeling or prediction job runs, it creates a Predictor or Prediction entry in the database
		//and that's enough to catch the dependency.

	}

	private void checkPredictorDependencies(Predictor p)throws ClassNotFoundException, SQLException{
		//make sure there are no predictions or prediction jobs that depend on this predictor
		
		String userName = p.getUserName();
		Session session = HibernateUtil.getSession();
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);
		session.close();
		
		//check each prediction
		for(int i = 0; i < userPredictions.size();i++){
			Prediction prediction = userPredictions.get(i);
			String[] predictorIds = prediction.getPredictorIds().split("\\s+");
			for(int j = 0; j < predictorIds.length; j++){
				if(Long.parseLong(predictorIds[j]) == p.getPredictorId()){
					errorStrings.add("The prediction '" + userPredictions.get(i).getJobName() + "' depends on this predictor. Please delete it first.\n");
				}
			}
		}
		
		//check running jobs 
		//Actually, we don't need to check the jobs.
		//When a prediction job runs, it creates a Prediction entry in the database
		//and that's enough to catch the dependency.
		
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
			errorStrings.add("No dataset ID supplied.");
			return ERROR;
		}

		Session session = HibernateUtil.getSession();
		ds = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId),session);
		
		if(ds == null){
			errorStrings.add("Invalid dataset ID supplied.");
			return ERROR;
		}
		
		if(! checkPermissions(ds.getUserName())){
			errorStrings.add("Error: You do not have the permissions needed to delete this dataset.");
			return ERROR;
		}
		
		//make sure nothing else depends on this dataset existing
		checkDatasetDependencies(ds);
		if(! errorStrings.isEmpty()){
			return ERROR;
		}

		//delete the files associated with this dataset
		String dir = Constants.CECCR_USER_BASE_PATH+ds.getUserName()+"/DATASETS/"+ds.getFileName();
		if((new File(dir)).exists()){
			if(! FileAndDirOperations.deleteDir(new File(dir))){
				Utility.writeToStrutsDebug("error deleting dir: " + dir);
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

		session.close();
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
			errorStrings.add("Invalid predictor ID supplied.");
			return ERROR;
		}
		
		if(! checkPermissions(p.getUserName())){
			errorStrings.add("You do not have the permissions needed to delete this predictor.");
			return ERROR;
		}
		
		//make sure nothing else depends on this predictor existing
		checkPredictorDependencies(p);
		if(! errorStrings.isEmpty()){
			return ERROR;
		}

		//delete the files associated with this predictor
		String dir = Constants.CECCR_USER_BASE_PATH+p.getUserName()+"/PREDICTORS/"+p.getName();
		if(! FileAndDirOperations.deleteDir(new File(dir))){
			Utility.writeToStrutsDebug("error deleting dir: " + dir);
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
		session.close();
		
		return SUCCESS;
	}

	public String deletePrediction() throws Exception{

		ActionContext context = ActionContext.getContext();
		
		String predictionId;
		Prediction p = null;
		
		predictionId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting prediction with id: " + predictionId);

		if(predictionId == null){
			errorStrings.add("No prediction ID supplied.");
			return ERROR;
		}

		Session session = HibernateUtil.getSession();
		p = PopulateDataObjects.getPredictionById(Long.parseLong(predictionId), session);
		if(p == null){
			errorStrings.add("Invalid prediction ID.");
			return ERROR;
		}
		
		if(! checkPermissions(p.getUserName())){
			errorStrings.add("You do not have the permissions needed to delete this prediction.");
			return ERROR;
		}
		
		//delete the files associated with this prediction
		String dir = Constants.CECCR_USER_BASE_PATH+p.getUserName()+"/PREDICTIONS/"+p.getJobName();
		if(! FileAndDirOperations.deleteDir(new File(dir))){
			Utility.writeToStrutsDebug("error deleting dir: " + dir);
		}
		
		//delete the prediction values associated with the prediction
		ArrayList<PredictionValue> pvs = (ArrayList<PredictionValue>) PopulateDataObjects.getPredictionValuesByPredictionId(p.getPredictionId(), session);
		
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
		
		return SUCCESS;
	}
	
	public String deleteJob() throws Exception{
		//stops the job and removes all associated files
		
		ActionContext context = ActionContext.getContext();
		
		String taskId;
		
		taskId = ((String[]) context.getParameters().get("id"))[0];
		Utility.writeToStrutsDebug("Deleting job with id: " + taskId);
		
		try{
			CentralDogma.getInstance().cancelJob(Long.parseLong(taskId));
		}
		catch(Exception ex){
			//if it failed, no big deal - just write out the exception.
			Utility.writeToDebug(ex);
		}
		return SUCCESS;
		
	}

	public ArrayList<String> getErrorStrings() {
		return errorStrings;
	}
	public void setErrorStrings(ArrayList<String> errorStrings) {
		this.errorStrings = errorStrings;
	}
	
}
