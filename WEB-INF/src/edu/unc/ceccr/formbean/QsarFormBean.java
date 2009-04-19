package edu.unc.ceccr.formbean;

import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;

import edu.unc.ceccr.global.Constants;

public class QsarFormBean extends ValidatorForm {
	
	private Long selectedPredictorId;
	
	private String selectedPredictorName;
	
	private Long selectedDatasetId;
	
	private String selectedDatasetName;
	
	private String knnType = "CONTINUOUS"; //used in the 2 radio buttons

	private String minNumDescriptors = "10";

	private String stepSize = "5";

	private String numCycles = "100";

	private String maxNumDescriptors = "10";

	private String Nearest_Neighbors = "5";

	private String Pseudo_Neighbors = "100";

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

	private String numCompoundsExternalSet;

	private String knnCategoryOptimization = "1";

	// end kNN Parameters

	// being SE Parameters

	private String descriptorGenerationType = Constants.MOLCONNZ;

	private String numSphereRadii = "1";

	private String selectionNextTrainPt = "3";

	private String numStartingPoints = "2";

	// Need to add in logic to support these functions
	private String startingCompounds;

	private String mostActiveStartingComp;

	// end SE Parameters

	private String jobName;

	private FormFile sdFile = null;

	private FormFile actFile = null;

	private String file = null;

	private int upload = 0;
	
	private String textValue;
	
	private String dataSetDescription;
	
	private String datasetname;
	
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

	public FormFile getActFile() {
		return actFile;
	}

	private String message;

	public void setActFile(FormFile actFile) {
		this.actFile = actFile;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public FormFile getSdFile() {
		return sdFile;
	}

	public void setSdFile(FormFile sdFile) {
		this.sdFile = sdFile;
	}

	public String getNumCompoundsExternalSet() {
		return numCompoundsExternalSet;
	}

	public void setNumCompoundsExternalSet(String numCompoundsExternalSet) {
		this.numCompoundsExternalSet = numCompoundsExternalSet;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}

	public String getKnnType() {
		return knnType;
	}

	public void setKnnType(String knnType) {
		this.knnType = knnType;
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

	public String getNumSphereRadii() {
		return numSphereRadii;
	}

	public void setNumSphereRadii(String numSphereRadii) {
		this.numSphereRadii = numSphereRadii;
	}

	public String getNumStartingPoints() {
		return numStartingPoints;
	}

	public void setNumStartingPoints(String numStartingPoints) {
		this.numStartingPoints = numStartingPoints;
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

	public String getMostActiveStartingComp() {
		return mostActiveStartingComp;
	}

	public void setMostActiveStartingComp(String mostActiveStartingComp) {
		this.mostActiveStartingComp = mostActiveStartingComp;
	}

	public String getNearest_Neighbors() {
		return Nearest_Neighbors;
	}

	public void setNearest_Neighbors(String nearest_Neighbors) {
		Nearest_Neighbors = nearest_Neighbors;
	}

	public String getPseudo_Neighbors() {
		return Pseudo_Neighbors;
	}

	public void setPseudo_Neighbors(String pseudo_Neighbors) {
		Pseudo_Neighbors = pseudo_Neighbors;
	}

	public String getRelativeDiffRR0() {
		return relativeDiffRR0;
	}

	public void setRelativeDiffRR0(String relative_diff_R_R0) {
		relativeDiffRR0 = relative_diff_R_R0;
	}

	public String getStartingCompounds() {
		return startingCompounds;
	}

	public void setStartingCompounds(String startingCompounds) {
		this.startingCompounds = startingCompounds;
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