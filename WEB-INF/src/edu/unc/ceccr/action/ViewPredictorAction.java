package edu.unc.ceccr.action;

import java.text.DecimalFormat;
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
import edu.unc.ceccr.task.Task;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;

public class ViewPredictorAction extends ActionSupport {

	private User user;
	private Predictor selectedPredictor;
	private String predictorId;
	private List<Model> models;
	private List<Model> randomModels;
	private List<ExternalValidation> externalValValues;
	private List<String> residuals;
	
	
	public String loadExternalValidationSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
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
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			

			//get external validation compounds of predictor
			externalValValues = PopulateDataObjects.getExternalValidationValues(selectedPredictor, session);
			
			//calculate residuals and fix significant figures on output data
			residuals = new ArrayList<String>();
			Iterator<ExternalValidation> eit = externalValValues.iterator();
			int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
			while(eit.hasNext()){
				ExternalValidation e = eit.next();
				if(e.getNumModels() != 0){
					String residual = DecimalFormat.getInstance().format(e.getActualValue() - e.getPredictedValue()).replaceAll(",", "");
					residuals.add(Utility.roundSignificantFigures(residual, sigfigs));
				}
				else{
					residuals.add("");
				}
				String predictedValue = DecimalFormat.getInstance().format(e.getPredictedValue()).replaceAll(",", "");
				e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));  
				if(! e.getStandDev().equalsIgnoreCase("No value")){
					e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
				}
			}
		}
		
		return result;
	}
	
	
	public String loadModelsSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
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
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			String numModelsToShow = user.getViewPredictorModels(); //maybe another pagination trick thingy.
			
			if(selectedPredictor == null){
				Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
			}
			
			Utility.writeToDebug("getting predictor models");
			//get models associated with predictor
			if(selectedPredictor.getDatasetId() != null){
				datasetUserName = PopulateDataObjects.getDataSetById(selectedPredictor.getDatasetId(), session).getUserName();
			}
			dataType = selectedPredictor.getModelMethod().toString();
			models = new ArrayList<Model>();
			randomModels = new ArrayList<Model>();
			ArrayList<Model> allModels = new ArrayList<Model>();
			List temp = PopulateDataObjects.getModelsByPredictorId(Long.parseLong(predictorId), session);
			if(temp != null){
				allModels.addAll(temp);

				Iterator<Model> it = allModels.iterator();
				while(it.hasNext()){
					Model m = it.next();
					if(m.getFlowType().equalsIgnoreCase(Constants.MAINKNN)){
						models.add(m);
					}
					else{
						randomModels.add(m);
					}
				}
			}
			
			Utility.writeToStrutsDebug("Got " + allModels.size() + " models and " + randomModels.size() + " random models.");
		}
		
		return result;
	}
	
	
	public String loadYRandomSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
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
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
		
			Utility.writeToDebug("getting predictor models");
			//get models associated with predictor
			if(selectedPredictor.getDatasetId() != null){
				datasetUserName = PopulateDataObjects.getDataSetById(selectedPredictor.getDatasetId(), session).getUserName();
			}
			dataType = selectedPredictor.getModelMethod().toString();
			models = new ArrayList<Model>();
			randomModels = new ArrayList<Model>();
			ArrayList<Model> allModels = new ArrayList<Model>();
			List temp = PopulateDataObjects.getModelsByPredictorId(Long.parseLong(predictorId), session);
			if(temp != null){
				allModels.addAll(temp);
	
				Iterator<Model> it = allModels.iterator();
				while(it.hasNext()){
					Model m = it.next();
					if(m.getFlowType().equalsIgnoreCase(Constants.MAINKNN)){
						models.add(m);
					}
					else{
						randomModels.add(m);
					}
				}
			}
			
			Utility.writeToStrutsDebug("Got " + allModels.size() + " models and " + randomModels.size() + " random models.");
		}
		
		return result;
	}

	public String loadWarningsSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
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
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
		}
		return result;
	}
	
	
	public String loadPage() throws Exception {

		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			else{
				Utility.writeToStrutsDebug("predictor id: " + predictorId);
				selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
				String numModelsToShow = user.getViewPredictorModels(); 
				
				if(selectedPredictor == null){
					Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
				}
			}
			
			
			//the predictor has now been viewed. Update DB accordingly.
			if(! selectedPredictor.getHasBeenViewed().equals(Constants.YES)){
				selectedPredictor.setHasBeenViewed(Constants.YES);
				Transaction tx = null;
				try {
					tx = session.beginTransaction();
					session.saveOrUpdate(selectedPredictor);
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e);
				}
			}
		}

		session.close();
		
		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to viewPredictor page.");
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
	
	private String dataType;
	
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	String datasetUserName;
	public String getDatasetUserName() {
		return datasetUserName;
	}
	public void setDatasetUserName(String datasetUserName) {
		this.datasetUserName = datasetUserName;
	}
	
	public Predictor getSelectedPredictor() {
		return selectedPredictor;
	}
	public void setSelectedPredictor(Predictor selectedPredictor) {
		this.selectedPredictor = selectedPredictor;
	}
	
	public String getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(String predictorId) {
		this.predictorId = predictorId;
	}
	
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	
	public List<Model> getRandomModels() {
		return randomModels;
	}
	public void setRandomModels(List<Model> randomModels) {
		this.randomModels = randomModels;
	}
	
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public List<ExternalValidation> getExternalValValues() {
		return externalValValues;
	}
	public void setExternalValValues(List<ExternalValidation> externalValValues) {
		this.externalValValues = externalValValues;
	}
	public List<String> getResiduals() {
		return residuals;
	}
	public void setResiduals(List<String> residuals) {
		this.residuals = residuals;
	}

	
	
}