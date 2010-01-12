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
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
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
		userPredictionNames = PopulateDataObjects.populatePredictionNames(user.getUserName(), true, session);
		userTaskNames = PopulateDataObjects.populateTaskNames(user.getUserName(), false, session);
		
		userPredictorList = PopulateDataObjects.populatePredictors(user.getUserName(), true, true, session);
		userContinuousDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS,true, session);
		userCategoryDatasets = PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY,true, session);
		session.close();

		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to modeling page.");
		}
		else{
			Utility.writeToStrutsDebug("Cannot load page.");
		}
		
		//load default tab selections
		modelingType = Constants.KNN;
		
		//go to the page
		return result;
	}
	
	public String execute() throws Exception {
		//form has been submitted

		Session session = HibernateUtil.getSession();
		
		//debug output
		String s = "";
		s += "\n Job Name: " + jobName;
		s += "\n Dataset ID: " + selectedDatasetId;
		s += "\n Dataset Name: " + PopulateDataObjects.getDataSetById(selectedDatasetId, session).getFileName();
		s += "\n Descriptor Type: " + descriptorGenerationType;
		s += "\n (Sphere Exclusion) Split Includes Min: " + splitIncludesMin;
		s += "\n (Random Internal Split) Max. Test Set Size: " + randomSplitMaxTestSize;
		s += "\n knnCategoryOptimization: " + knnCategoryOptimization;
		
		Utility.writeToDebug(s);
		
		//get user
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		
		//set up job
		try{
			Queue tasklist;
			QsarModelingTask modelingTask = new QsarModelingTask(user.getUserName(), this);
			Utility.writeToDebug("Setting up task", user.getUserName(), this.getJobName());
			modelingTask.setUp();
			Utility.writeToDebug("done Setting up task", user.getUserName(), this.getJobName());
			tasklist = Queue.getInstance();
			int numCompounds = PopulateDataObjects.getDataSetById(selectedDatasetId, session).getNumCompound();
			
			session.close();
			
			//count the number of models that will be generated
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
			
			//add job to queue
			tasklist.addJob(modelingTask, user.getUserName(), this.getJobName(), numCompounds, numModels);

			Utility.writeToUsageLog("Started modeling job", user.getUserName());
			
			Utility.writeToDebug("Task added to queue", user.getUserName(), this.getJobName());
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		return SUCCESS;
	}

	public String ajaxLoadKnn() throws Exception {

		knnCategoryOptimizations = new HashMap<String, String>();
		knnCategoryOptimizations.put("1", "<img src=\"/theme/img/formula01.gif\" />");
		knnCategoryOptimizations.put("2", "<img src=\"/theme/img/formula02.gif\" />");
		knnCategoryOptimizations.put("3", "<img src=\"/theme/img/formula03.gif\" />");
		knnCategoryOptimizations.put("4", "<img src=\"/theme/img/formula04.gif\" />");
		
		return SUCCESS;
	}
	public String ajaxLoadSvm() throws Exception {
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

	//begin descriptor parameters
	private String descriptorGenerationType = Constants.MOLCONNZ;
	private String scalingType = Constants.RANGESCALING;
	private String stdDevCutoff = "0.0";
	private String corellationCutoff = "1.0";
	//end descriptor parameters
		
	// being train-test split parameters
	private String trainTestSplitType = Constants.RANDOM;

		//if random split
		private String numSplitsInternalRandom = "5";
		private String randomSplitMinTestSize = "20";
		private String randomSplitMaxTestSize = "30";
		
		//if sphere exclusion
		private String numSplitsInternalSphere = "5";
		private String sphereSplitMinTestSize = "25";
		private String splitIncludesMin = "true";
		private String splitIncludesMax = "true";
		private String selectionNextTrainPt = "0";
		
	// end train-test split parameters
	
	private String modelingType;	
		
	//kNN Parameters
	
	private String actFileDataType = Constants.CONTINUOUS; //used in the 2 radio buttons
	private String minNumDescriptors = "5";
	private String maxNumDescriptors = "10";
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
	private String cutoff = "1.0";
	private String minAccTraining = "0.6";
	private String minAccTest = "0.6";
	private String minSlopes = "0";
	private String maxSlopes = "1";
	private String relativeDiffRR0 = "500.0";
	private String diffR01R02 = "0.9";
	private String stop_cond = "50";
	private String knnCategoryOptimization = "1";
	// end kNN Parameters

	//SVM Parameters
	private String svmTypeCategory = "0";
	private String svmTypeContinuous = "0";
	private String svmKernel = "2";
	
	private String svmDegreeFrom = "1";
	private String svmDegreeTo = "8";
	private String svmDegreeStep = "2";

	private String svmGammaFrom = "-15";
	private String svmGammaTo = "8";
	private String svmGammaStep = "2";
	
	private String svmCostFrom = "-10";
	private String svmCostTo = "15";
	private String svmCostStep = "1";

	private String svmNuFrom = "0.1";
	private String svmNuTo = "2.5";
	private String svmNuStep = "0.5";

	private String svmPEpsilonFrom = "-5";
	private String svmPEpsilonTo = "5";
	private String svmPEpsilonStep = "2";
	
	private String svmEEpsilon = "0.001";
	private String svmHeuristics = "true";
	private String svmProbability = "false";
	private String svmWeight ="1";
	private String svmCrossValidation = "0";
	//end SVM Parameters
	
	private String jobName;
	private String textValue;
	private String dataSetDescription;
	private String datasetName;
	private String message;

	
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
	
	public String getDatasetname() {
		return datasetName;
	}
	public void setDatasetname(String datasetName) {
		this.datasetName = datasetName;
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
	public void setSvmPEpsilon(String svmPEpsilonFrom) {
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
	//end SVM
	
	
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
	
}