package edu.unc.ceccr.formbean;

import org.apache.struts.validator.ValidatorForm;

import edu.unc.ceccr.global.Constants;

public class QsarFormBean extends ValidatorForm {
	
	private Long selectedPredictorId;
	private String selectedPredictorName;
	private Long selectedDatasetId;
	private String selectedDatasetName;

	private String modelingType;

	private String descriptorGenerationType = Constants.MOLCONNZ;
	private String scalingType = Constants.RANGESCALING;


	//begin modeling-external split parameters
	private String numCompoundsExternalSet = "5";
	private String externalRandomSeed = "" + Math.round(Math.random() * 16000);
	
	// end modeling-external split parameters
	
	// being train-test split parameters

	private String numSplits = "25";
	private String trainTestSplitType = "0";

		//if random split
		private String randomSplitMinTestSize = "20";
		private String randomSplitMaxTestSize = "30";
	
		//if sphere exclusion
		private String splitIncludesMin = "1";
		private String splitIncludesMax = "1";
		private String sphereSplitMinTestSize = "25";
		private String selectionNextTrainPt = "0";
		
	// end train-test split parameters
	
	
	//kNN Parameters
	
	private String datasetType = "CONTINUOUS"; //used in the 2 radio buttons
	private String minNumDescriptors = "10";
	private String stepSize = "5";
	private String numCycles = "100";
	private String maxNumDescriptors = "10";
	private String nearest_Neighbors = "5";
	private String pseudo_Neighbors = "100";
	private String numRuns = "10";
	private String numMutations = "2";
	private String T1 = "100";
	private String T2 = "-5.0";
	private String mu = "0.9";
	private String TcOverTb = "-6.0";
	private String cutoff = "1.0";
	private String minAccTraining = "-1";
	private String minAccTest = "-1";
	private String minSlopes = "-10";
	private String maxSlopes = "10";
	private String relativeDiffRR0 = "500.0";
	private String diffR01R02 = "0.9";
	private String stop_cond = "50";
	private String knnCategoryOptimization = "1";
	
	// end kNN Parameters

	//SVM Parameters
	private String svmTypeCategory = "0";
	private String svmTypeContinuous = "0";
	private String svmKernel = "2";
	private String svmDegree = "3";
	private String svmGamma = "1/k";
	private String svmCost = "1";
	private String svmNu = "0.5";
	private String svmPEpsilon = "0.1";
	private String svmEEpsilon = "0.001";
	private String svmHeuristics = "1";
	private String svmProbability = "0";
	private String svmWeight ="1";
	private String svmCrossValidation = "0";
	//end SVM Parameters
	
	private String jobName;
	private String textValue;
	private String dataSetDescription;
	private String datasetname;
	private String message;
	
	public String getModelingType() {
		return modelingType;
	}
	public void setModelingType(String modelingType) {
		this.modelingType = modelingType;
	}
	
	public String getDatasetname() {
		return datasetname;
	}
	public void setDatasetname(String datasetname) {
		this.datasetname = datasetname;
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

	public String getNumCompoundsExternalSet() {
		return numCompoundsExternalSet;
	}
	public void setNumCompoundsExternalSet(String numCompoundsExternalSet) {
		this.numCompoundsExternalSet = numCompoundsExternalSet;
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
	public String getDatasetType() {
		return datasetType;
	}
	public void setDatasetType(String datasetType) {
		this.datasetType = datasetType;
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
	
	public String getExternalRandomSeed() {
		return externalRandomSeed;
	}
	public void setExternalRandomSeed(String externalRandomSeed) {
		this.externalRandomSeed = externalRandomSeed;
	}
	
	
	public String getDiffR01R02() {
		return diffR01R02;
	}

	public void setDiffR01R02(String diff_R01_R02) {
		diffR01R02 = diff_R01_R02;
	}
	
	public String getNumSplits() {
		return numSplits;
	}

	public void setNumSplits(String numSplits) {
		this.numSplits = numSplits;
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
	public String getSvmDegree() {
		return svmDegree;
	}
	public void setSvmDegree(String svmDegree) {
		this.svmDegree = svmDegree;
	}
	public void setSvmGamma(String svmGamma) {
		this.svmGamma = svmGamma;
	}
	public String getSvmGamma() {
		return svmGamma;
	}
	public String getSvmCost() {
		return svmCost;
	}
	public void setSvmCost(String svmCost) {
		this.svmCost = svmCost;
	}
	public String getSvmNu() {
		return svmNu;
	}
	public void setSvmNu(String svmNu) {
		this.svmNu = svmNu;
	}
	public String getSvmPEpsilon() {
		return svmPEpsilon;
	}
	public void setSvmPEpsilon(String svmPEpsilon) {
		this.svmPEpsilon = svmPEpsilon;
	}
	public String getSvmEEpsilon() {
		return svmEEpsilon;
	}
	public void setSvmEEpsilon(String svmEEpsilon) {
		this.svmEEpsilon = svmEEpsilon;
	}
	public void setSvmHeuristics(String svmHeuristics) {
		this.svmHeuristics = svmHeuristics;
	}
	public String getSvmHeuristics() {
		return svmHeuristics;
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