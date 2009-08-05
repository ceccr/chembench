package edu.unc.ceccr.action;

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

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;


public class DatasetFormActions extends ActionSupport{
	public String ajaxLoadPredOnly() throws Exception {
		return SUCCESS;
	}
	public String ajaxLoadModAndPred() throws Exception {
		return SUCCESS;
	}
	public String ajaxLoadModOnly() throws Exception {
		return SUCCESS;
	}

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
		
		userDatasetNames = PopulateDataObjects.populateDatasetNames(user.getUserName(), true);
		userPredictorNames = PopulateDataObjects.populatePredictorNames(user.getUserName(), true);
		userPredictionNames = PopulateDataObjects.populatePredictionNames(user.getUserName(), true);
		userTaskNames = PopulateDataObjects.populateTaskNames(user.getUserName(), false);
		
		userPredictorList = PopulateDataObjects.populatePredictors(user.getUserName(), true, true);
		userContinuousDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS,true);
		userCategoryDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY,true);

		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to dataset page.");
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
	
	private String knnType;
	private String upload;
	private String datasetname;
	private FormFile sdFileModeling = null;
	private FormFile actFile = null;
	private FormFile sdFilePrediction= null;
	private String dataSetDescription;
	private String message;
	
	public String getDataSetDescription() {
		return dataSetDescription;
	}
	public void setDataSetDescription(String dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
	}
	
	public FormFile getActFile() {
		return actFile;
	}
	public void setActFile(FormFile actFile) {
		this.actFile = actFile;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getUpload() {
		return upload;
	}
	public void setUpload(String upload) {
		this.upload = upload;
	}
	
	public FormFile getSdFileModeling() {
		return sdFileModeling;
	}
	public void setSdFileModeling(FormFile sdFileModeling) {
		this.sdFileModeling = sdFileModeling;
	}
	
	public FormFile getSdFilePrediction() {
		return sdFilePrediction;
	}
	public void setSdFilePrediction(FormFile sdFilePrediction) {
		this.sdFilePrediction = sdFilePrediction;
	}
	
	public String getKnnType() {
		return knnType;
	}
	public void setKnnType(String knnType) {
		this.knnType = knnType;
	}
	
	public String getDatasetname() {
		return datasetname;
	}
	public void setDatasetname(String datasetname) {
		this.datasetname = datasetname;
	}
	
	//====== variables used for display on the JSP =====//
	private User user;
	
	private List<String> userDatasetNames;
	private List<String> userPredictorNames;
	private List<String> userPredictionNames;
	private List<String> userTaskNames;
	
	private List<Predictor> userPredictorList;
	private List<DataSet> userContinuousDatasets;
	private List<DataSet> userCategoryDatasets;

	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
	
	
	public List<Predictor> getUserPredictorList(){
		return userPredictorList;
	}
	public void setUserPredictorList(List<Predictor> userPredictorList) {
		this.userPredictorList = userPredictorList;
	}
	
	public List<DataSet> getUserContinuousDatasets(){
		return userContinuousDatasets;
	}
	public void setUserContinuousDatasets(List<DataSet> userContinuousDatasets) {
		this.userContinuousDatasets = userContinuousDatasets;
	}
	
	public List<DataSet> getUserCategoryDatasets(){
		return userCategoryDatasets;
	}
	public void setUserCategoryDatasets(List<DataSet> userCategoryDatasets) {
		this.userCategoryDatasets = userCategoryDatasets;
	}
	
}