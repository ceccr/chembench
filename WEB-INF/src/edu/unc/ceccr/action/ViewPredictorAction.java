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
import edu.unc.ceccr.task.Task;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;

public class ViewPredictorAction extends ActionSupport {

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
			}
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			else{
				Utility.writeToStrutsDebug("predictor id: " + predictorId);
				selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
				if(selectedPredictor == null){
					Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
				}
			}
			
			//get models associated with predictor
			dataType = selectedPredictor.getModelMethod().toString();
			models = new ArrayList<Model>();
			randomModels = new ArrayList<Model>();
			ArrayList<Model> allModels = new ArrayList<Model>();
			allModels.addAll(PopulateDataObjects.getModelsByPredictorId(Long.parseLong(predictorId), session));
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
			
			Utility.writeToStrutsDebug("Got " + allModels.size() + " models and " + randomModels.size() + " random models.");
			
			//get external validation compounds of predictor
			externalValValues = PopulateDataObjects.getExternalValidationValues(selectedPredictor, session);
			
			//calculate residuals and fix significant figures on output data
			residuals = new ArrayList<String>();
			Iterator<ExternalValidation> eit = externalValValues.iterator();
			int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
			while(eit.hasNext()){
				ExternalValidation e = eit.next();
				residuals.add(Utility.roundSignificantFigures("" + (e.getActualValue() - e.getPredictedValue()), sigfigs));
				e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(""+e.getPredictedValue(), sigfigs)));  
				if(! e.getStandDev().equalsIgnoreCase("No value")){
					e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
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
	
	private User user;
	private Predictor selectedPredictor;
	private String predictorId;
	private List<Model> models;
	private List<Model> randomModels;
	private List<ExternalValidation> externalValValues;
	private List<String> residuals;
	
	private String dataType;
	
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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