package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
				return result;
			}
		}

		Session session = HibernateUtil.getSession();
		userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), true, false, session);
		session.close();
		
		return result;
	}
	
	public String makeSmilesPrediction() throws Exception{
		String result = SUCCESS;
		
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		//use the same session for all data requests
		Session session = HibernateUtil.getSession();

		try{
			Map k = context.getParameters();
			Utility.writeToDebug("starting params");
			for(Object key : k.keySet()){
				Utility.writeToDebug(key.toString() + " : " + k.get(key).toString());
			}
			Utility.writeToDebug("ending params");
			
		String smiles = (String) context.getParameters().get("smiles");
		String cutoff = (String) context.getParameters().get("cutoff");
		
		Utility.writeToDebug(" 1: " + smiles + " 2: " + cutoff);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		try{
		String smiles3 = context.getValueStack().findString("smiles").toString();
		String cutoff3 = context.getValueStack().findString("cutoff").toString();
		
		Utility.writeToDebug(" 5: " + smiles3 + " 6: " + cutoff3);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	
		try{
		String smiles4 = context.getContextMap().get("smiles").toString();
		String cutoff4 = context.getContextMap().get("cutoff").toString();
		
		Utility.writeToDebug(" 7: " + smiles4 + " 8: " + cutoff4);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		try{
		String smiles5 = context.getValueStack().pop().toString();
		String cutoff5 = context.getValueStack().pop().toString();
		
		Utility.writeToDebug(" 9: " + smiles5 + " 0: " + cutoff5);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		try{
		String smiles2 = context.get("smiles").toString();
		String cutoff2 = context.get("cutoff").toString();
		
		Utility.writeToDebug(" 3: " + smiles2 + " 4: " + cutoff2);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		/*		
		Utility.writeToDebug(user.getUserName());
		Utility.writeToDebug("SMILES predids: " + selectedPredictorIds);

		int numCompounds = predictionDataset.getNumCompound();
		String[] ids = selectedPredictorIds.split("\\s+");
		int numModels = 0;
		for(int i = 0; i < ids.length; i++){
			numModels += PopulateDataObjects.getPredictorById(Long.parseLong(ids[i]), session).getNumTestModels();
		}
		Queue.getInstance().addJob(predTask,user.getUserName(), jobName, numCompounds, numModels);
		 */
		//give back the session at the end
		session.close();
		
		return result;
	}

	public String loadMakePredictionsPage() throws Exception{
		
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
		
		
		//use the same session for all data requests
		Session session = HibernateUtil.getSession();

		//get list of predictor IDs from the checked checkboxes
		selectedPredictorIds = predictorCheckBoxes.replaceAll(",", " ");
		String[] predictorIds = selectedPredictorIds.split("\\s+");
		if(predictorIds.length == 0){
			Utility.writeToStrutsDebug("no predictor chosen!");
			result = ERROR;
		}
		for(int i = 0; i < predictorIds.length; i++){
			Predictor p = PopulateDataObjects.getPredictorById(Long.parseLong(predictorIds[i]), session);
			selectedPredictors.add(p);
		}
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		userDatasetNames = PopulateDataObjects.populateDatasetNames(user.getUserName(), true, session);
		userPredictorNames = PopulateDataObjects.populatePredictorNames(user.getUserName(), true, session);
		userPredictionNames = PopulateDataObjects.populatePredictionNames(user.getUserName(), true, session);
		userTaskNames = PopulateDataObjects.populateTaskNames(user.getUserName(), false, session);
		userDatasets = PopulateDataObjects.populateDatasetsForPrediction(user.getUserName(), true, session);
		
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
		Utility.writeToDebug("predids: " + selectedPredictorIds);

		QsarPredictionTask predTask = new QsarPredictionTask(user.getUserName(), jobName, sdf, 
				cutOff, selectedPredictorIds, predictionDataset);

		predTask.setUp();
		int numCompounds = predictionDataset.getNumCompound();
		String[] ids = selectedPredictorIds.split("\\s+");
		int numModels = 0;
		for(int i = 0; i < ids.length; i++){
			numModels += PopulateDataObjects.getPredictorById(Long.parseLong(ids[i]), session).getNumTestModels();
		}
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
	private List<DataSet> userDatasets;
	private String predictorCheckBoxes;
	private List<Predictor> selectedPredictors = new ArrayList<Predictor>();
	
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
	
	public List<DataSet> getUserDatasets(){
		return userDatasets;
	}
	public void setUserDatasets(List<DataSet> userDatasets) {
		this.userDatasets = userDatasets;
	}
	
	public List<Predictor> getSelectedPredictors() {
		return selectedPredictors;
	}
	public void setSelectedPredictors(List<Predictor> selectedPredictors) {
		this.selectedPredictors = selectedPredictors;
	}
	
	//populated by the JSP form
	private Long selectedDatasetId;
	private String cutOff = "0.5";
	private String jobName;
	private String selectedPredictorIds;
	
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
	
	public String getPredictorCheckBoxes() {
		return predictorCheckBoxes;
	}
	public void setPredictorCheckBoxes(String predictorCheckBoxes) {
		this.predictorCheckBoxes = predictorCheckBoxes;
	}

	public String getSelectedPredictorIds() {
		return selectedPredictorIds;
	}
	public void setSelectedPredictorIds(String selectedPredictorIds) {
		this.selectedPredictorIds = selectedPredictorIds;
	}

}