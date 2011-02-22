package edu.unc.ceccr.action;

import java.util.ArrayList;
import java.util.Date;
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
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.taskObjects.WorkflowTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;


public class ModelingFormActions extends ActionSupport{
	
	public String loadPage() throws Exception{
		
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
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		Session session = HibernateUtil.getSession();
		userDatasetNames = PopulateDataObjects.populateDatasetNames(user.getUserName(), true, session);
		userPredictorNames = PopulateDataObjects.populatePredictorNames(user.getUserName(), true, session);
		
		//also get the base names for nfold predictors. if a user has "mypredictor_fold_1_of_5", 
		//we want "mypredictor" in the list of used names as well.
		ArrayList<String> foldedPredictorNames = new ArrayList<String>();
		for(String predictorName: userPredictorNames){
			if(predictorName.matches(".*_fold_(\\d+)_of_(\\d+)")){
				int pos = predictorName.lastIndexOf("_fold");
				foldedPredictorNames.add(predictorName.substring(0,pos));
			}
		}
		userPredictorNames.addAll(foldedPredictorNames);
		
		userPredictionNames = PopulateDataObjects.populatePredictionNames(user.getUserName(), true, session);
		userTaskNames = PopulateDataObjects.populateTaskNames(user.getUserName(), false, session);
		
		userPredictorList = PopulateDataObjects.populatePredictors(user.getUserName(), true, true, session);
		
		if(user.getShowPublicDatasets().equals(Constants.ALL)){
			//get user and public datasets
			userContinuousDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS,true, session);
			userCategoryDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY,true, session);
		}
		else if(user.getShowPublicDatasets().equals(Constants.NONE)){
			//just get user datasets
			userContinuousDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS,false, session);
			userCategoryDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY,false, session);
		}
		else if(user.getShowPublicDatasets().equals(Constants.SOME)){
			//get all datasets and filter out all the public ones that aren't "show by default"
			userContinuousDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS,true, session);
			userCategoryDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY,true, session);
			
			for(int i = 0; i < userContinuousDatasets.size(); i++){
				String s = userContinuousDatasets.get(i).getShowByDefault();
				if(s != null && s.equals(Constants.NO)){
					userContinuousDatasets.remove(i);
					i--;
				}
			}
			
			for(int i = 0; i < userCategoryDatasets.size(); i++){
				String s = userCategoryDatasets.get(i).getShowByDefault();
				if(s != null && s.equals(Constants.NO)){
					userCategoryDatasets.remove(i);
					i--;
				}
			}
		}
		session.close();

		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to modeling page.");
		}
		else{
			Utility.writeToStrutsDebug("Cannot load page.");
		}
		
		//load default tab selections
		modelingType = Constants.RANDOMFOREST; 
		
		//go to the page
		return result;
	}
	
	Session executeSession = null; //specialized session variable used only in this function
	boolean closeSessionAtEnd = true;
	public String execute() throws Exception {
		//form has been submitted

		if(executeSession == null){
			executeSession = HibernateUtil.getSession();
		}
		
		//get user
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		
		if(jobName != null){
			jobName = jobName.replaceAll(" ", "_");
			jobName = jobName.replaceAll("\\(", "_");
			jobName = jobName.replaceAll("\\)", "_");
			jobName = jobName.replaceAll("\\[", "_");
			jobName = jobName.replaceAll("\\]", "_");
		}
		
		Utility.writeToDebug("Submitting modeling job with dataset id: " + selectedDatasetId);
		if(selectedDatasetId == null || PopulateDataObjects.getDataSetById(selectedDatasetId, executeSession) == null ||
				PopulateDataObjects.getDataSetById(selectedDatasetId, executeSession).getFileName() == null ||
				PopulateDataObjects.getDataSetById(selectedDatasetId, executeSession).getJobCompleted().equals(Constants.NO)){
			return "";
		}
		
		DataSet ds = PopulateDataObjects.getDataSetById(selectedDatasetId, executeSession);
		if((ds.getFileName().equals("all-datasets") )){
			//Launch modeling on every dataset the user owns (except for this one).
			closeSessionAtEnd = false;
			ArrayList<DataSet> datasetList = new ArrayList<DataSet>();
			datasetList.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, false, executeSession));
			datasetList.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, false, executeSession));
			
			Long allDatasetsId = selectedDatasetId;
			String originalJobName = jobName;
			
			for(int i = 0; i < datasetList.size(); i++){
				if(! datasetList.get(i).getFileId().equals(allDatasetsId) && 
					! datasetList.get(i).getFileName().equals("all-datasets")){
					actFileDataType = datasetList.get(i).getModelType();
					selectedDatasetId = datasetList.get(i).getFileId();
					jobName = originalJobName + datasetList.get(i).getFileName();
					execute();
				}
			}
			if(executeSession.isOpen()){
				executeSession.close();
			}
			return SUCCESS;
		}
		//print debug output
		String s = "";
		s += "\n Job Name: " + jobName;
		s += "\n Dataset ID: " + selectedDatasetId;
		s += "\n Dataset Name: " + PopulateDataObjects.getDataSetById(selectedDatasetId, executeSession).getFileName();
		s += "\n Descriptor Type: " + descriptorGenerationType;
		s += "\n (Sphere Exclusion) Split Includes Min: " + splitIncludesMin;
		s += "\n (Random Internal Split) Max. Test Set Size: " + randomSplitMaxTestSize;
		
		Utility.writeToDebug(s);
		
		//set up job
		try{
			//unsplit any variables repeated between knn-GA and knn-SA
			int index = 0;
			if(modelingType.equals(Constants.KNNGA)){
				index = 1;
			}
			if(knnMinNumDescriptors.split("\\, ").length > 1){
				knnMinNumDescriptors = knnMinNumDescriptors.split("\\, ")[index];
				knnMaxNumDescriptors = knnMaxNumDescriptors.split("\\, ")[index];
				knnMinNearestNeighbors = knnMinNearestNeighbors.split("\\, ")[index];
				knnMaxNearestNeighbors = knnMaxNearestNeighbors.split("\\, ")[index];
				knnApplicabilityDomain = knnApplicabilityDomain.split("\\, ")[index];
				knnMinTraining = knnMinTraining.split("\\, ")[index];
				knnMinTest = knnMinTest.split("\\, ")[index];
			}
			
			if(ds.getSplitType().equals(Constants.NFOLD)){
				//start n jobs, 1 for each fold.
				int numExternalFolds = Integer.parseInt(ds.getNumExternalFolds());
				String baseJobName = jobName;
				int numCompounds = ds.getNumCompound();
				String childPredictorIds = "";
				for(int i = 0; i < numExternalFolds; i++){
					
					//count the number of models that will be generated
					int numModels = getNumModels();

					//set up the job
					jobName = baseJobName + "_fold_" +(i+1) + "_of_" + numExternalFolds;
					QsarModelingTask modelingTask = new QsarModelingTask(user.getUserName(), this);
					childPredictorIds += modelingTask.setUp() + " ";
					
					//add job to incoming joblist so it will start
					CentralDogma centralDogma = CentralDogma.getInstance();
					centralDogma.addJobToIncomingList(user.getUserName(), jobName, modelingTask, numCompounds, numModels, emailOnCompletion);
					
					Utility.writeToUsageLog("Added modeling job", user.getUserName());
					Utility.writeToDebug("Modeling job added to queue", user.getUserName(), this.getJobName());
				}
				
				//make a "parent" predictor to contain each of the "child" predictors
				Predictor p = new Predictor();
				p.setChildIds(childPredictorIds);
				p.setChildType(Constants.NFOLD);
				p.setName(baseJobName);
				p.setJobCompleted(Constants.NO);
				p.setHasBeenViewed(Constants.NO);
				p.setDatasetId(selectedDatasetId);
				p.setUserName(user.getUserName());
				p.setModelMethod(modelingType);
				p.setPredictorType(Constants.PRIVATE);
				p.setDescriptorGeneration(descriptorGenerationType);
				p.setActivityType(actFileDataType);
			
				Transaction tx = null;
				try {
					tx = executeSession.beginTransaction();
					executeSession.save(p);
					tx.commit();
				} catch (Exception ex) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(ex);
				}
				
				if(closeSessionAtEnd){
					executeSession.close();
				}
			}
			else{
				QsarModelingTask modelingTask = new QsarModelingTask(user.getUserName(), this);
				modelingTask.setUp();
				int numCompounds = ds.getNumCompound();
				
				if(closeSessionAtEnd){
					executeSession.close();
				}
				
				//count the number of models that will be generated
				int numModels = getNumModels();
				
				//make job and add to incoming joblist
				CentralDogma centralDogma = CentralDogma.getInstance();
				centralDogma.addJobToIncomingList(user.getUserName(), jobName, modelingTask, numCompounds, numModels, emailOnCompletion);
				
				Utility.writeToUsageLog("Added modeling job", user.getUserName());
				Utility.writeToDebug("Task added to queue", user.getUserName(), this.getJobName());
				
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
			if(closeSessionAtEnd){
				executeSession.close();
			}
		}
		return SUCCESS;
	}
	
	private int getNumModels(){
		int numModels = 0;
		if(trainTestSplitType.equals(Constants.RANDOM)){
			numModels = Integer.parseInt(numSplitsInternalRandom);
		}
		else if(trainTestSplitType.equals(Constants.SPHEREEXCLUSION)){
			numModels = Integer.parseInt(numSplitsInternalSphere);
		}
	
		if(modelingType.equals(Constants.KNN)){
			numModels *= Integer.parseInt(numRuns);
			int numDescriptorSizes = 0;
			for(int i = Integer.parseInt(minNumDescriptors); i <= Integer.parseInt(maxNumDescriptors); i += Integer.parseInt(stepSize)){
				numDescriptorSizes++;
			}
			numModels *= numDescriptorSizes;
		}
		else if(modelingType.equals(Constants.KNNSA)){
			numModels *= Integer.parseInt(saNumRuns);
			numModels *= Integer.parseInt(saNumBestModels);
			int numDescriptorSizes = 0;
			for(int i = Integer.parseInt(knnMinNumDescriptors); i <= Integer.parseInt(knnMaxNumDescriptors); i += Integer.parseInt(knnDescriptorStepSize)){
				numDescriptorSizes++;
			}
			numModels *= numDescriptorSizes;
		}
		else if(modelingType.equals(Constants.KNNGA)){
			//no changes; parameters affect generation time of 
			//each model but not the total number of models to be generated
		}
		else if(modelingType.equals(Constants.SVM)){
			Double numDifferentCosts = Math.ceil((Double.parseDouble(svmCostTo) - 
					Double.parseDouble(svmCostFrom)) / 
					Double.parseDouble(svmCostStep) + 0.0001);
	
			Double numDifferentDegrees = Math.ceil((Double.parseDouble(svmDegreeTo) - 
					Double.parseDouble(svmDegreeFrom)) / 
					Double.parseDouble(svmDegreeStep) + 0.0001);
	
			Double numDifferentGammas = Math.ceil((Double.parseDouble(svmGammaTo) - 
					Double.parseDouble(svmGammaFrom)) / 
					Double.parseDouble(svmGammaStep) + 0.0001);
	
			Double numDifferentNus = Math.ceil((Double.parseDouble(svmNuTo) - 
					Double.parseDouble(svmNuFrom)) / 
					Double.parseDouble(svmNuStep) + 0.0001);
	
			Double numDifferentPEpsilons = Math.ceil((Double.parseDouble(svmPEpsilonTo) - 
					Double.parseDouble(svmPEpsilonFrom)) / 
					Double.parseDouble(svmPEpsilonStep) + 0.0001);
			
			numModels *= numDifferentCosts * numDifferentDegrees * numDifferentGammas * numDifferentNus * numDifferentPEpsilons;
		}
		return numModels;
	}
	
	public String ajaxLoadKnn() throws Exception {
		ActionContext context = ActionContext.getContext();
		if(context != null){
			user = (User) context.getSession().get("user");
		}
		
		knnCategoryOptimizations = new HashMap<String, String>();
		knnCategoryOptimizations.put("1", "<img src=\"/theme/img/formula01.gif\" />");
		knnCategoryOptimizations.put("2", "<img src=\"/theme/img/formula02.gif\" />");
		knnCategoryOptimizations.put("3", "<img src=\"/theme/img/formula03.gif\" />");
		knnCategoryOptimizations.put("4", "<img src=\"/theme/img/formula04.gif\" />");
		
		return SUCCESS;
	}
	
	public String ajaxLoadKnnPlus() throws Exception{
		return SUCCESS;
	}

	public String ajaxLoadSvm() throws Exception {
		return SUCCESS;
	}

	public String ajaxLoadRandomForest() throws Exception {
		return SUCCESS;
	}
	
	public String ajaxLoadRandomSplit() throws Exception {
		return SUCCESS;
	}
	public String ajaxLoadSphereSplit() throws Exception {
		return SUCCESS;
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
	
	private Map<String, String> knnCategoryOptimizations;
	
	public Map<String, String> getKnnCategoryOptimizations(){
		return knnCategoryOptimizations;
	}
	public void setKnnCategoryOptimizations(Map<String, String> knnCategoryOptimizations) {
		this.knnCategoryOptimizations = knnCategoryOptimizations;
	}
	
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
	
	//====== variables populated by the forms on the JSP =====//
	
	
	private Long selectedPredictorId;
	private String selectedPredictorName;
	private Long selectedDatasetId;
	private String selectedDatasetName;
	
	//begin dataset selection parameters
	private String datasetName;
	private String actFileDataType = Constants.CONTINUOUS; //set by the radio buttons
	private String categoryWeights = Constants.INVERSEOFSIZE;
	
	//end dataset selection parameters
	
	//begin descriptor parameters
	private String descriptorGenerationType = Constants.MOLCONNZ;
	private String scalingType = Constants.RANGESCALING;
	private String stdDevCutoff = "0.0";
	private String corellationCutoff = "1.0";
	//end descriptor parameters
		
	// being train-test split parameters
	private String trainTestSplitType = Constants.RANDOM;

		//if random split
		private String numSplitsInternalRandom = "20";
		private String randomSplitMinTestSize = "20";
		private String randomSplitMaxTestSize = "30";
		private String randomSplitSampleWithReplacement = "FALSE";
		
		//if sphere exclusion
		private String numSplitsInternalSphere = "20";
		private String sphereSplitMinTestSize = "25";
		private String splitIncludesMin = "true";
		private String splitIncludesMax = "true";
		private String selectionNextTrainPt = "0";
		
	// end train-test split parameters
	
	private String modelingType;	
		
	//kNN Parameters
	
	private String minNumDescriptors = "5";
	private String maxNumDescriptors = "30";
	private String stepSize = "5";
	private String numCycles = "100";
	private String nearest_Neighbors = "5";
	private String pseudo_Neighbors = "100";
	private String numRuns = "10";
	private String numMutations = "2";
	private String T1 = "100";
	private String T2 = "-5.0";
	private String mu = "0.9";
	private String TcOverTb = "-6.0";
	private String cutoff = "0.5";
	private String minAccTraining = "0.6";
	private String minAccTest = "0.6";
	private String minSlopes = "0";
	private String maxSlopes = "1";
	private String relativeDiffRR0 = "500.0";
	private String diffR01R02 = "0.9";
	private String stop_cond = "50";
	private String knnCategoryOptimization = "1";
	// end kNN Parameters

	//begin knn+ parameters
	private String knnMinNumDescriptors = "15";
	private String knnMaxNumDescriptors = "30";
	private String knnDescriptorStepSize = "5";
	private String knnMinNearestNeighbors = "1";
	private String knnMaxNearestNeighbors = "6";

	private String saNumRuns = "3";
	private String saMutationProbabilityPerDescriptor = "0.2";
	private String saNumBestModels = "2";
	private String saTempDecreaseCoefficient = "0.75";
	private String saLogInitialTemp = "5";
	private String saFinalTemp = "5";
	private String saTempConvergence = "5";

	private String gaPopulationSize = "250";
	private String gaMaxNumGenerations = "500";
	private String gaNumStableGenerations = "10";
	private String gaTournamentGroupSize = "7";
	private String gaMinFitnessDifference = "-4";

	private String knnApplicabilityDomain = "0.5";
	private String knnMinTraining = "0.6";
	private String knnMinTest = "0.6";
	private String knnSaErrorBasedFit = "false";
	private String knnGaErrorBasedFit = "false";

	//end knn+ parameters
	
	//SVM Parameters
	private String svmTypeCategory = "0";
	private String svmTypeContinuous = "3";
	private String svmKernel = "0";

	//must be > 0
	private String svmDegreeFrom = "2";
	private String svmDegreeTo = "8";
	private String svmDegreeStep = "3";

	//must be >= 0
	private String svmGammaFrom = "0";
	private String svmGammaTo = "8";
	private String svmGammaStep = "4";

	//must be > 0
	private String svmCostFrom = "2";
	private String svmCostTo = "10";
	private String svmCostStep = "4";

	//must be > 0 and <= 1
	private String svmNuFrom = "0.1";
	private String svmNuTo = "1";
	private String svmNuStep = "0.3";

	//must be >= 0
	private String svmPEpsilonFrom = "0";
	private String svmPEpsilonTo = "5";
	private String svmPEpsilonStep = "5";
	
	private String svmEEpsilon = "0.001";
	private String svmHeuristics = "1";
	private String svmProbability = "0";
	private String svmWeight ="1";
	private String svmCrossValidation = "0";
	private String svmCutoff ="0.6";
	//end SVM Parameters
	
	//Random Forest parameters
	private String numTrees = "50";
	private String minTerminalNodeSize = "1";
	private String maxNumTerminalNodes = "0";
	private String descriptorsPerTree = "25";
	
	//end Random Forest parameters
	private String jobName;
	private String textValue;
	private String dataSetDescription;
	private String message;
	private String emailOnCompletion = "false";

	public String getStdDevCutoff() {
		return stdDevCutoff;
	}
	public void setStdDevCutoff(String stdDevCutoff) {
		this.stdDevCutoff = stdDevCutoff;
	}
	
	public String getCorellationCutoff() {
		return corellationCutoff;
	}
	public void setCorellationCutoff(String corellationCutoff) {
		this.corellationCutoff = corellationCutoff;
	}
	
	public String getModelingType() {
		return modelingType;
	}
	public void setModelingType(String modelingType) {
		this.modelingType = modelingType;
	}
	
	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	
	public String getCategoryWeights() {
		return categoryWeights;
	}
	public void setCategoryWeights(String categoryWeights) {
		this.categoryWeights = categoryWeights;
	}

	public String getDataSetDescription() {
		return dataSetDescription;
	}
	public void setDataSetDescription(String dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
	}
	
	public String getTextValue()
	{
		return this.textValue;
	}
	public void setTextValue(String textValue)
	{
		this.textValue=textValue;
	}

	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getScalingType() {
		return scalingType;
	}
	public void setScalingType(String scalingType) {
		this.scalingType = scalingType;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getSplitIncludesMin() {
		return splitIncludesMin;
	}
	public void setSplitIncludesMin(String splitIncludesMin) {
		this.splitIncludesMin = splitIncludesMin;
	}
	
	public String getSplitIncludesMax() {
		return splitIncludesMax;
	}
	public void setSplitIncludesMax(String splitIncludesMax) {
		this.splitIncludesMax = splitIncludesMax;
	}
	
	public String getSphereSplitMinTestSize() {
		return sphereSplitMinTestSize;
	}
	public void setSphereSplitMinTestSize(String sphereSplitMinTestSize) {
		this.sphereSplitMinTestSize = sphereSplitMinTestSize;
	}
	
	
	//kNN
	public String getActFileDataType() {
		return actFileDataType;
	}
	public void setActFileDataType(String actFileDataType) {
		this.actFileDataType = actFileDataType;
	}

	public String getDescriptorGenerationType() {
		return descriptorGenerationType;
	}
	public void setDescriptorGenerationType(String descriptorGenerationType) {
		this.descriptorGenerationType = descriptorGenerationType;
	}

	public String getCutoff() {
		return cutoff;
	}
	public void setCutoff(String cutoff) {
		this.cutoff = cutoff;
	}

	public String getKnnCategoryOptimization() {
		return knnCategoryOptimization;
	}
	public void setKnnCategoryOptimization(String knnCategoryOptimization) {
		this.knnCategoryOptimization = knnCategoryOptimization;
	}

	public String getMaxNumDescriptors() {
		return maxNumDescriptors;
	}
	public void setMaxNumDescriptors(String maxNumDescriptors) {
		this.maxNumDescriptors = maxNumDescriptors;
	}

	public String getMinAccTest() {
		return minAccTest;
	}
	public void setMinAccTest(String minAccTest) {
		this.minAccTest = minAccTest;
	}

	public String getMinAccTraining() {
		return minAccTraining;
	}
	public void setMinAccTraining(String minAccTraining) {
		this.minAccTraining = minAccTraining;
	}

	public String getMinNumDescriptors() {
		return minNumDescriptors;
	}
	public void setMinNumDescriptors(String minNumDescriptors) {
		this.minNumDescriptors = minNumDescriptors;
	}

	public String getNumMutations() {
		return numMutations;
	}
	public void setNumMutations(String numMutations) {
		this.numMutations = numMutations;
	}

	public String getStepSize() {
		return stepSize;
	}
	public void setStepSize(String stepSize) {
		this.stepSize = stepSize;
	}

	public String getMu() {
		return mu;
	}
	public void setMu(String mu) {
		this.mu = mu;
	}

	public String getNumRuns() {
		return numRuns;
	}
	public void setNumRuns(String numRuns) {
		this.numRuns = numRuns;
	}
	
	public String getRandomSplitMinTestSize() {
		return randomSplitMinTestSize;
	}
	public void setRandomSplitMinTestSize(String randomSplitMinTestSize) {
		this.randomSplitMinTestSize = randomSplitMinTestSize;
	}

	public String getRandomSplitMaxTestSize() {
		return randomSplitMaxTestSize;
	}
	public void setRandomSplitMaxTestSize(String randomSplitMaxTestSize) {
		this.randomSplitMaxTestSize = randomSplitMaxTestSize;
	}

	public String getRandomSplitSampleWithReplacement() {
		return randomSplitSampleWithReplacement;
	}
	public void setRandomSplitSampleWithReplacement(
			String randomSplitSampleWithReplacement) {
		this.randomSplitSampleWithReplacement = randomSplitSampleWithReplacement;
	}

	public String getSelectionNextTrainPt() {
		return selectionNextTrainPt;
	}
	public void setSelectionNextTrainPt(String selectionNextTrainPt) {
		this.selectionNextTrainPt = selectionNextTrainPt;
	}
	
	public String getDiffR01R02() {
		return diffR01R02;
	}

	public void setDiffR01R02(String diff_R01_R02) {
		diffR01R02 = diff_R01_R02;
	}

	public String getNumSplitsInternalRandom() {
		return numSplitsInternalRandom;
	}

	public void setNumSplitsInternalRandom(String numSplitsInternalRandom) {
		this.numSplitsInternalRandom = numSplitsInternalRandom;
	}
	
	public String getNumSplitsInternalSphere() {
		return numSplitsInternalSphere;
	}

	public void setNumSplitsInternalSphere(String numSplitsInternalSphere) {
		this.numSplitsInternalSphere = numSplitsInternalSphere;
	}

	public String getNearest_Neighbors() {
		return nearest_Neighbors;
	}

	public void setNearest_Neighbors(String nearest_Neighbors) {
		this.nearest_Neighbors = nearest_Neighbors;
	}

	public String getPseudo_Neighbors() {
		return pseudo_Neighbors;
	}

	public void setPseudo_Neighbors(String pseudo_Neighbors) {
		this.pseudo_Neighbors = pseudo_Neighbors;
	}

	public String getRelativeDiffRR0() {
		return relativeDiffRR0;
	}

	public void setRelativeDiffRR0(String relative_diff_R_R0) {
		relativeDiffRR0 = relative_diff_R_R0;
	}
	
	public String getTrainTestSplitType() {
		return trainTestSplitType;
	}

	public void setTrainTestSplitType(String trainTestSplitType) {
		this.trainTestSplitType = trainTestSplitType;
	}

	public String getStop_cond() {
		return stop_cond;
	}

	public void setStop_cond(String stop_cond) {
		this.stop_cond = stop_cond;
	}

	public String getT1() {
		return T1;
	}

	public void setT1(String t1) {
		T1 = t1;
	}

	public String getT2() {
		return T2;
	}

	public void setT2(String t2) {
		T2 = t2;
	}

	public String getTcOverTb() {
		return TcOverTb;
	}

	public void setTcOverTb(String tcOverTb) {
		TcOverTb = tcOverTb;
	}

	public String getNumCycles() {
		return numCycles;
	}

	public void setNumCycles(String numCycles) {
		this.numCycles = numCycles;
	}

	public String getMaxSlopes() {
		return maxSlopes;
	}

	public void setMaxSlopes(String maxSlopes) {
		this.maxSlopes = maxSlopes;
	}

	public String getMinSlopes() {
		return minSlopes;
	}

	public void setMinSlopes(String minSlopes) {
		this.minSlopes = minSlopes;
	}
	//end kNN
	
	//knn+
	
	public String getKnnMinNumDescriptors() {
		return knnMinNumDescriptors;
	}

	public void setKnnMinNumDescriptors(String knnMinNumDescriptors) {
		this.knnMinNumDescriptors = knnMinNumDescriptors;
	}

	public String getKnnMaxNumDescriptors() {
		return knnMaxNumDescriptors;
	}

	public void setKnnMaxNumDescriptors(String knnMaxNumDescriptors) {
		this.knnMaxNumDescriptors = knnMaxNumDescriptors;
	}

	public String getKnnDescriptorStepSize() {
		return knnDescriptorStepSize;
	}

	public void setKnnDescriptorStepSize(String knnDescriptorStepSize) {
		this.knnDescriptorStepSize = knnDescriptorStepSize;
	}

	public String getKnnMinNearestNeighbors() {
		return knnMinNearestNeighbors;
	}

	public void setKnnMinNearestNeighbors(String knnMinNearestNeighbors) {
		this.knnMinNearestNeighbors = knnMinNearestNeighbors;
	}

	public String getKnnMaxNearestNeighbors() {
		return knnMaxNearestNeighbors;
	}

	public void setKnnMaxNearestNeighbors(String knnMaxNearestNeighbors) {
		this.knnMaxNearestNeighbors = knnMaxNearestNeighbors;
	}

	public String getSaNumRuns() {
		return saNumRuns;
	}

	public void setSaNumRuns(String saNumRuns) {
		this.saNumRuns = saNumRuns;
	}

	public String getSaMutationProbabilityPerDescriptor() {
		return saMutationProbabilityPerDescriptor;
	}

	public void setSaMutationProbabilityPerDescriptor(
			String saMutationProbabilityPerDescriptor) {
		this.saMutationProbabilityPerDescriptor = saMutationProbabilityPerDescriptor;
	}

	public String getSaNumBestModels() {
		return saNumBestModels;
	}

	public void setSaNumBestModels(String saNumBestModels) {
		this.saNumBestModels = saNumBestModels;
	}

	public String getSaTempDecreaseCoefficient() {
		return saTempDecreaseCoefficient;
	}

	public void setSaTempDecreaseCoefficient(String saTempDecreaseCoefficient) {
		this.saTempDecreaseCoefficient = saTempDecreaseCoefficient;
	}

	public String getSaLogInitialTemp() {
		return saLogInitialTemp;
	}

	public void setSaLogInitialTemp(String saLogInitialTemp) {
		this.saLogInitialTemp = saLogInitialTemp;
	}

	public String getSaFinalTemp() {
		return saFinalTemp;
	}

	public void setSaFinalTemp(String saFinalTemp) {
		this.saFinalTemp = saFinalTemp;
	}

	public String getSaTempConvergence() {
		return saTempConvergence;
	}

	public void setSaTempConvergence(String saTempConvergence) {
		this.saTempConvergence = saTempConvergence;
	}

	public String getGaPopulationSize() {
		return gaPopulationSize;
	}

	public void setGaPopulationSize(String gaPopulationSize) {
		this.gaPopulationSize = gaPopulationSize;
	}

	public String getGaMaxNumGenerations() {
		return gaMaxNumGenerations;
	}

	public void setGaMaxNumGenerations(String gaMaxNumGenerations) {
		this.gaMaxNumGenerations = gaMaxNumGenerations;
	}

	public String getGaNumStableGenerations() {
		return gaNumStableGenerations;
	}

	public void setGaNumStableGenerations(String gaNumStableGenerations) {
		this.gaNumStableGenerations = gaNumStableGenerations;
	}

	public String getGaTournamentGroupSize() {
		return gaTournamentGroupSize;
	}

	public void setGaTournamentGroupSize(String gaTournamentGroupSize) {
		this.gaTournamentGroupSize = gaTournamentGroupSize;
	}

	public String getGaMinFitnessDifference() {
		return gaMinFitnessDifference;
	}

	public void setGaMinFitnessDifference(String gaMinFitnessDifference) {
		this.gaMinFitnessDifference = gaMinFitnessDifference;
	}

	public String getKnnApplicabilityDomain() {
		return knnApplicabilityDomain;
	}

	public void setKnnApplicabilityDomain(String knnApplicabilityDomain) {
		this.knnApplicabilityDomain = knnApplicabilityDomain;
	}

	public String getKnnMinTraining() {
		return knnMinTraining;
	}

	public void setKnnMinTraining(String knnMinTraining) {
		this.knnMinTraining = knnMinTraining;
	}

	public String getKnnMinTest() {
		return knnMinTest;
	}

	public void setKnnMinTest(String knnMinTest) {
		this.knnMinTest = knnMinTest;
	}

	public String getKnnSaErrorBasedFit() {
		return knnSaErrorBasedFit;
	}

	public void setKnnSaErrorBasedFit(String knnSaErrorBasedFit) {
		this.knnSaErrorBasedFit = knnSaErrorBasedFit;
	}

	public String getKnnGaErrorBasedFit() {
		return knnGaErrorBasedFit;
	}

	public void setKnnGaErrorBasedFit(String knnGaErrorBasedFit) {
		this.knnGaErrorBasedFit = knnGaErrorBasedFit;
	}

	//end knn+
	
	//SVM
	public String getSvmTypeCategory() {
		return svmTypeCategory;
	}
	public void setSvmTypeCategory(String svmTypeCategory) {
		this.svmTypeCategory = svmTypeCategory;
	}
	public String getSvmTypeContinuous() {
		return svmTypeContinuous;
	}
	public void setSvmTypeContinuous(String svmTypeContinuous) {
		this.svmTypeContinuous = svmTypeContinuous;
	}
	public String getSvmKernel() {
		return svmKernel;
	}
	public void setSvmKernel(String svmKernel) {
		this.svmKernel = svmKernel;
	}
	
	public String getSvmDegreeFrom() {
		return svmDegreeFrom;
	}
	public void setSvmDegreeFrom(String svmDegreeFrom) {
		this.svmDegreeFrom = svmDegreeFrom;
	}
	public String getSvmDegreeTo() {
		return svmDegreeTo;
	}
	public void setSvmDegreeTo(String svmDegreeTo) {
		this.svmDegreeTo = svmDegreeTo;
	}
	public String getSvmDegreeStep() {
		return svmDegreeStep;
	}
	public void setSvmDegreeStep(String svmDegreeStep) {
		this.svmDegreeStep = svmDegreeStep;
	}

	public void setSvmGammaFrom(String svmGammaFrom) {
		this.svmGammaFrom = svmGammaFrom;
	}
	public String getSvmGammaFrom() {
		return svmGammaFrom;
	}
	public void setSvmGammaTo(String svmGammaTo) {
		this.svmGammaTo = svmGammaTo;
	}
	public String getSvmGammaTo() {
		return svmGammaTo;
	}
	public void setSvmGammaStep(String svmGammaStep) {
		this.svmGammaStep = svmGammaStep;
	}
	public String getSvmGammaStep() {
		return svmGammaStep;
	}

	public String getSvmCostFrom() {
		return svmCostFrom;
	}
	public void setSvmCostFrom(String svmCostFrom) {
		this.svmCostFrom = svmCostFrom;
	}
	public String getSvmCostTo() {
		return svmCostTo;
	}
	public void setSvmCostTo(String svmCostTo) {
		this.svmCostTo = svmCostTo;
	}
	public String getSvmCostStep() {
		return svmCostStep;
	}
	public void setSvmCostStep(String svmCostStep) {
		this.svmCostStep = svmCostStep;
	}

	public String getSvmNuFrom() {
		return svmNuFrom;
	}
	public void setSvmNuFrom(String svmNuFrom) {
		this.svmNuFrom = svmNuFrom;
	}
	public String getSvmNuTo() {
		return svmNuTo;
	}
	public void setSvmNuTo(String svmNuTo) {
		this.svmNuTo = svmNuTo;
	}
	public String getSvmNuStep() {
		return svmNuStep;
	}
	public void setSvmNuStep(String svmNuStep) {
		this.svmNuStep = svmNuStep;
	}

	public String getSvmPEpsilonFrom() {
		return svmPEpsilonFrom;
	}
	public void setSvmPEpsilonFrom(String svmPEpsilonFrom) {
		this.svmPEpsilonFrom = svmPEpsilonFrom;
	}

	public String getSvmPEpsilonTo() {
		return svmPEpsilonTo;
	}
	public void setSvmPEpsilonTo(String svmPEpsilonTo) {
		this.svmPEpsilonTo = svmPEpsilonTo;
	}
	public String getSvmPEpsilonStep() {
		return svmPEpsilonStep;
	}
	public void setSvmPEpsilonStep(String svmPEpsilonStep) {
		this.svmPEpsilonStep = svmPEpsilonStep;
	}
	
	public String getSvmEEpsilon() {
		return svmEEpsilon;
	}
	public void setSvmEEpsilon(String svmEEpsilon) {
		this.svmEEpsilon = svmEEpsilon;
	}
	public String getSvmHeuristics() {
		return svmHeuristics;
	}
	public void setSvmHeuristics(String svmHeuristics) {
		this.svmHeuristics = svmHeuristics;
	}
	public String getSvmProbability() {
		return svmProbability;
	}
	public void setSvmProbability(String svmProbability) {
		this.svmProbability = svmProbability;
	}
	public String getSvmWeight() {
		return svmWeight;
	}
	public void setSvmWeight(String svmWeight) {
		this.svmWeight = svmWeight;
	}
	public String getSvmCrossValidation() {
		return svmCrossValidation;
	}
	public void setSvmCrossValidation(String svmCrossValidation) {
		this.svmCrossValidation = svmCrossValidation;
	}
	
	public String getSvmCutoff() {
		return svmCutoff;
	}
	public void setSvmCutoff(String svmCutoff) {
		this.svmCutoff = svmCutoff;
	}
	//end SVM
	

	//start RF
	public String getNumTrees() {
		return numTrees;
	}
	public void setNumTrees(String numTrees) {
		this.numTrees = numTrees;
	}

	public String getMinTerminalNodeSize() {
		return minTerminalNodeSize;
	}
	public void setMinTerminalNodeSize(String minTerminalNodeSize) {
		this.minTerminalNodeSize = minTerminalNodeSize;
	}

	public String getMaxNumTerminalNodes() {
		return maxNumTerminalNodes;
	}
	public void setMaxNumTerminalNodes(String maxNumTerminalNodes) {
		this.maxNumTerminalNodes = maxNumTerminalNodes;
	}

	public String getDescriptorsPerTree() {
		return descriptorsPerTree;
	}
	public void setDescriptorsPerTree(String descriptorsPerTree) {
		this.descriptorsPerTree = descriptorsPerTree;
	}
	//end RF
	
	
	public Long getSelectedPredictorId() {
		return selectedPredictorId;
	}

	public void setSelectedPredictorId(Long selectedPredictorId) {
		this.selectedPredictorId = selectedPredictorId;
	}

	public String getSelectedPredictorName() {
		return selectedPredictorName;
	}

	public void setSelectedPredictorName(String selectedPredictorName) {
		this.selectedPredictorName = selectedPredictorName;
	}
	
	public Long getSelectedDatasetId() {
		return selectedDatasetId;
	}

	public void setSelectedDatasetId(Long selectedPredictorId) {
		this.selectedDatasetId = selectedPredictorId;
	}

	public String getSelectedDatasetName() {
		return selectedDatasetName;
	}
	public void setSelectedDatasetName(String selectedDatasetName) {
		this.selectedDatasetName = selectedDatasetName;
	}

	public String getEmailOnCompletion() {
		return emailOnCompletion;
	}
	public void setEmailOnCompletion(String emailOnCompletion) {
		this.emailOnCompletion = emailOnCompletion;
	}
}