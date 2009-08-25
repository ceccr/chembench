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

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
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
	public String ajaxLoadAutoSplit() throws Exception {
		return SUCCESS;
	}
	public String ajaxLoadManualSplit() throws Exception {
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
		
		dataTypeModPred = Constants.CONTINUOUS;
		//go to the page
		return result;
	}
	public String execute() throws Exception {
		
		try{
			Utility.writeToDebug("Starting dataset task");
			Utility.writeToDebug("datasetName: " + datasetName);
			Utility.writeToDebug("useActivityBinning: " + useActivityBinning);
			Utility.writeToDebug("sdfFile: " + sdfFileModPredFileName);
			Utility.writeToDebug("actFile: " + actFileModPredFileName);
			Utility.writeToDebug("Starting dataset task");
			
			String fullFileName = "c:/upload/myfile.txt";
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		return SUCCESS;
	}

	private String dataTypeModPred = Constants.CONTINUOUS;
	private String dataTypeModOnly = Constants.CONTINUOUS;
	private String datasetName = "";
	private String dataSetDescription = "";
	private String externalCompoundList = "";
	private String useActivityBinning = "true";
	private String paperReference = "";
	

	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	
	public String getDataTypeModPred() {
		return dataTypeModPred;
	}
	public void setDataTypeModPred(String dataTypeModPred) {
		this.dataTypeModPred = dataTypeModPred;
	}
	
	public String getDataTypeModOnly() {
		return dataTypeModOnly;
	}
	public void setDataTypeModOnly(String dataTypeModOnly) {
		this.dataTypeModOnly = dataTypeModOnly;
	}
	
	public String getDataSetDescription() {
		return dataSetDescription;
	}
	public void setDataSetDescription(String dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
	}
	
	public String getExternalCompoundList() {
		return externalCompoundList;
	}
	public void setExternalCompoundList(String externalCompoundList) {
		this.externalCompoundList = externalCompoundList;
	}
	public String getUseActivityBinning() {
		return useActivityBinning;
	}
	public void setUseActivityBinning(String useActivityBinning) {
		this.useActivityBinning = useActivityBinning;
	}
	
	public String getPaperReference() {
		return paperReference;
	}
	public void setPaperReference(String paperReference) {
		this.paperReference = paperReference;
	}
	
	//file upload stuff
	private File sdfFileModPred = null;
	private String sdfFileModPredContentType = "";
	private String sdfFileModPredFileName = ""; 

	private File sdfFileModOnly = null;
	private String sdfFileModOnlyContentType = "";
	private String sdfFileModOnlyFileName = ""; 
	
	private File sdfFilePredOnly = null;
	private String sdfFilePredOnlyContentType = "";
	private String sdfFilePredOnlyFileName = ""; 

	private File actFileModPred = null;
	private String actFileModPredContentType = "";
	private String actFileModPredFileName = "";

	private File actFileModOnly = null;
	private String actFileModOnlyContentType = "";
	private String actFileModOnlyFileName = "";
	
	private File xFileModOnly = null;
	private String xFileModOnlyContentType= "";
	private String xFileModOnlyFileName = "";
	
	public File getSdfFileModPred() {
		return sdfFileModPred;
	}
	public void setSdfFileModPred(File sdfFileModPred) {
		this.sdfFileModPred = sdfFileModPred;
	}
	public String getSdfFileModPredContentType() {
		return sdfFileModPredContentType;
	}
	public void setSdfFileModPredContentType(String sdfFileModPredContentType) {
		this.sdfFileModPredContentType = sdfFileModPredContentType;
	}
	public String getSdfFileModPredFileName() {
		return sdfFileModPredFileName;
	}
	public void setSdfFileModPredFileName(String sdfFileModPredFileName) {
		this.sdfFileModPredFileName = sdfFileModPredFileName;
	}
	public File getSdfFileModOnly() {
		return sdfFileModOnly;
	}
	public void setSdfFileModOnly(File sdfFileModOnly) {
		this.sdfFileModOnly = sdfFileModOnly;
	}
	public String getSdfFileModOnlyContentType() {
		return sdfFileModOnlyContentType;
	}
	public void setSdfFileModOnlyContentType(String sdfFileModOnlyContentType) {
		this.sdfFileModOnlyContentType = sdfFileModOnlyContentType;
	}
	public String getSdfFileModOnlyFileName() {
		return sdfFileModOnlyFileName;
	}
	public void setSdfFileModOnlyFileName(String sdfFileModOnlyFileName) {
		this.sdfFileModOnlyFileName = sdfFileModOnlyFileName;
	}
	public File getSdfFilePredOnly() {
		return sdfFilePredOnly;
	}
	public void setSdfFilePredOnly(File sdfFilePredOnly) {
		this.sdfFilePredOnly = sdfFilePredOnly;
	}
	public String getSdfFilePredOnlyContentType() {
		return sdfFilePredOnlyContentType;
	}
	public void setSdfFilePredOnlyContentType(String sdfFilePredOnlyContentType) {
		this.sdfFilePredOnlyContentType = sdfFilePredOnlyContentType;
	}
	public String getSdfFilePredOnlyFileName() {
		return sdfFilePredOnlyFileName;
	}
	public void setSdfFilePredOnlyFileName(String sdfFilePredOnlyFileName) {
		this.sdfFilePredOnlyFileName = sdfFilePredOnlyFileName;
	}
	public File getActFileModPred() {
		return actFileModPred;
	}
	public void setActFileModPred(File actFileModPred) {
		this.actFileModPred = actFileModPred;
	}
	public String getActFileModPredContentType() {
		return actFileModPredContentType;
	}
	public void setActFileModPredContentType(String actFileModPredContentType) {
		this.actFileModPredContentType = actFileModPredContentType;
	}
	public String getActFileModPredFileName() {
		return actFileModPredFileName;
	}
	public void setActFileModPredFileName(String actFileModPredFileName) {
		this.actFileModPredFileName = actFileModPredFileName;
	}
	public File getActFileModOnly() {
		return actFileModOnly;
	}
	public void setActFileModOnly(File actFileModOnly) {
		this.actFileModOnly = actFileModOnly;
	}
	public String getActFileModOnlyContentType() {
		return actFileModOnlyContentType;
	}
	public void setActFileModOnlyContentType(String actFileModOnlyContentType) {
		this.actFileModOnlyContentType = actFileModOnlyContentType;
	}
	public String getActFileModOnlyFileName() {
		return actFileModOnlyFileName;
	}
	public void setActFileModOnlyFileName(String actFileModOnlyFileName) {
		this.actFileModOnlyFileName = actFileModOnlyFileName;
	}
	public File getXFileModOnly() {
		return xFileModOnly;
	}
	public void setXFileModOnly(File xFileModOnly) {
		this.xFileModOnly = xFileModOnly;
	}
	public String getXFileModOnlyContentType() {
		return xFileModOnlyContentType;
	}
	public void setXFileModOnlyContentType(String fileContentType) {
		xFileModOnlyContentType = fileContentType;
	}
	public String getXFileModOnlyFileName() {
		return xFileModOnlyFileName;
	}
	public void setXFileModOnlyFileName(String fileName) {
		xFileModOnlyFileName = fileName;
	}
	//end file upload stuff
	
	
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