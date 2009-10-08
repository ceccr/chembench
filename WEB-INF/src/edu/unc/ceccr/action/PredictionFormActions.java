package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;


public class PredictionFormActions extends ActionSupport{

	public String loadSelectPredictorPage() throws Exception{
		
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

		Session session = HibernateUtil.getSession();
		userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), true, false, session);
		session.close();
		
		return result;
	}

	public String loadMakePredictionsPage() throws Exception{
		
		Utility.writeToDebug("check boxes say: " + predictorCheckBoxes);
		
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

		//use the same session for all data requests
		Session session = HibernateUtil.getSession();
		
		//get the selected predictor
		String predictorId = ((String[]) context.getParameters().get("id"))[0];
		if(predictorId == null){
			Utility.writeToStrutsDebug("no predictor id");
		}
		selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
		if(selectedPredictor == null){
			Utility.writeToStrutsDebug("invalid predictor id: " + predictorId);
		}
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		userDatasetNames = PopulateDataObjects.populateDatasetNames(user.getUserName(), true, session);
		userPredictorNames = PopulateDataObjects.populatePredictorNames(user.getUserName(), true, session);
		userPredictionNames = PopulateDataObjects.populatePredictionNames(user.getUserName(), true, session);
		userTaskNames = PopulateDataObjects.populateTaskNames(user.getUserName(), false, session);
		
		userDatasets = PopulateDataObjects.populateDatasetsForPrediction(user.getUserName(), true, session);
		selectedPredictorId = "" + selectedPredictor.getPredictorId();
		
		//give back the session at the end
		session.close();
		return result;
	}

	public String makeDatasetPrediction() throws Exception {
		//prediction form submitted, so create a new prediction task and run it
	
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		
		//use the same session for all data requests
		Session session = HibernateUtil.getSession();
		
		DataSet predictionDataset = PopulateDataObjects.getDataSetById(selectedDatasetId, session);
		String sdf = predictionDataset.getSdfFile();
		
		Utility.writeToDebug(user.getUserName());
		Utility.writeToDebug("predid: " + selectedPredictorId);

		
		QsarPredictionTask predTask = new QsarPredictionTask(user.getUserName(), jobName, sdf, 
				cutOff, Long.parseLong(selectedPredictorId), predictionDataset);

		predTask.setUp();
		int numCompounds = predictionDataset.getNumCompound();
		int numModels = PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorId), session).getNumTestModels();
		Queue.getInstance().addJob(predTask,user.getUserName(), jobName, numCompounds, numModels);

		//give back the session at the end
		session.close();
		return SUCCESS;
	}	
	
	public String execute() throws Exception {
		return SUCCESS;
	}	
	
	//variables used for JSP display
	private User user;
	private List<Predictor> userPredictors;
	private List<String> userDatasetNames;
	private List<String> userPredictorNames;
	private List<String> userPredictionNames;
	private List<String> userTaskNames;
	private Predictor selectedPredictor;
	private List<Predictor> selectedPredictors;
	private List<DataSet> userDatasets;
	private String predictorCheckBoxes;
	


	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<Predictor> getUserPredictors(){
		return userPredictors;
	}
	public void setUserPredictors(List<Predictor> userPredictors) {
		this.userPredictors = userPredictors;
	}
	
	public List<String> getUserDatasetNames(){
		return userDatasetNames;
	}
	public void setUserDatasetNames(List<String> userDatasetNames) {
		this.userDatasetNames = userDatasetNames;
	}
	
	public List<String> getUserPredictorNames(){
		return userPredictorNames;
	}
	public void setUserPredictorNames(List<String> userPredictorNames) {
		this.userPredictorNames = userPredictorNames;
	}
	
	public List<String> getUserPredictionNames(){
		return userPredictionNames;
	}
	public void setUserPredictionNames(List<String> userPredictionNames) {
		this.userPredictionNames = userPredictionNames;
	}
	
	public List<String> getUserTaskNames(){
		return userTaskNames;
	}
	public void setUserTaskNames(List<String> userTaskNames) {
		this.userTaskNames = userTaskNames;
	}
	
	public Predictor getSelectedPredictor() {
		return selectedPredictor;
	}
	public void setSelectedPredictor(Predictor selectedPredictor) {
		this.selectedPredictor = selectedPredictor;
	}
	
	public List<DataSet> getUserDatasets(){
		return userDatasets;
	}
	public void setUserDatasets(List<DataSet> userDatasets) {
		this.userDatasets = userDatasets;
	}
	
	//populated by the JSP form
	private Long selectedDatasetId;
	private String cutOff = "0.5";
	private String jobName;
	private String selectedPredictorId;
	
	public Long getSelectedDatasetId() {
		return selectedDatasetId;
	}
	public void setSelectedDatasetId(Long selectedDatasetId) {
		this.selectedDatasetId = selectedDatasetId;
	}
	
	public String getCutOff() {
		return cutOff;
	}
	public void setCutOff(String cutOff) {
		this.cutOff = cutOff;
	}

	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getSelectedPredictorId() {
		return selectedPredictorId;
	}
	public void setSelectedPredictorId(String selectedPredictorId) {
		this.selectedPredictorId = selectedPredictorId;
	}
	
	public String getPredictorCheckBoxes() {
		return predictorCheckBoxes;
	}
	public void setPredictorCheckBoxes(String predictorCheckBoxes) {
		this.predictorCheckBoxes = predictorCheckBoxes;
	}
}