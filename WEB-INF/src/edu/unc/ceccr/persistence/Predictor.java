package edu.unc.ceccr.persistence;

// default package
// Generated Jun 20, 2006 1:22:16 PM by Hibernate Tools 3.1.0.beta5

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

@Entity
@Table(name = "cbench_predictor")
public class Predictor implements java.io.Serializable {

	// Fields

	private String userName;
	private Long predictorId;
	private Long datasetId;
	private String name;
	
	private String sdFileName;
	private String actFileName;
	
	private Set<Model> models = new HashSet<Model>(0);
	private Set<ExternalValidation> externalValidationResults = new HashSet<ExternalValidation>(0);
	
	private Date dateCreated;
	private Date dateUpdated;
	
	private int numTotalModels;
	private int numTrainModels;
	private int numTestModels;
	private String status;
	
	private int numyTotalModels;
	private int numyTrainModels;
	private int numyTestModels;

	private String modelMethod;
	private String descriptorGeneration;
	private String stdDevCutoff;
	private String correlationCutoff;
	private String activityType;
	private String predictorType;
	private String scalingType;
	private String hasBeenViewed;
	private String jobCompleted; //Initially NO; YES on completion.
	
	private int numPredictions;
	private String description;
	private String paperReference;
	
	//datasplit

	private String numSplits;
	private String trainTestSplitType;
		
		//if random split
		private String randomSplitMinTestSize;
		private String randomSplitMaxTestSize;		
	
		//if sphere exclusion
		private String splitIncludesMin;
		private String splitIncludesMax;
		private String sphereSplitMinTestSize;
		private String selectionNextTrainPt;
	
	private String minNumDescriptors;
	private String stepSize;
	private String maxNumDescriptors;
	private String knnCategoryOptimization;
	private String numCycles;
	private String nearestNeighbors;
	private String pseudoNeighbors;
	private String numRuns;
	private String numMutations;
	private String T1;
	private String T2;
	private String mu;
	private String TcOverTb;
	private String cutoff;
	private String minAccTraining;
	private String minAccTest;
	private String minSlopes;
	private String maxSlopes;
	private String relativeDiffRR0;
	private String diffR01R02;
	private String stopCond;

	// Constructors

	/** default constructor */
	public Predictor() {
	
	}

	/** minimal constructor */
	public Predictor(Long predictorId, String name, String userName) {
		this.predictorId = predictorId;
		this.name = name;
		this.userName = userName;
	}

	/** full constructor */
	public Predictor(Long predictorId, String name, Set<Model> models,
			String sdFileName, String actFileName, String userName) {
		this.predictorId = predictorId;
		this.name = name;
		this.models = models;
		this.sdFileName = sdFileName;
		this.actFileName = actFileName;
		this.userName = userName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "predictor_id")
	// Property accessors
	public Long getPredictorId() {
		return this.predictorId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "predictor", cascade = CascadeType.ALL)
	public Set<Model> getModels() {
		return this.models;
	}

	public void setModels(Set<Model> models) {
		this.models = models;
	}
	
	@OneToMany(mappedBy = "predictor", cascade = CascadeType.ALL)
	public Set<ExternalValidation> getExternalValidationResults() {
		return externalValidationResults;
	}

	public void setExternalValidationResults(
			Set<ExternalValidation> externalValidationResults) {
		this.externalValidationResults = externalValidationResults;
	}

	public void setPredictorId(Long predictorId) {
		this.predictorId = predictorId;
	}

	@Column(name = "ACTFileName")
	public String getActFileName() {
		return actFileName;
	}

	public void setActFileName(String actFileName) {
		this.actFileName = actFileName;
	}

	@Column(name = "SDFileName")
	public String getSdFileName() {
		return sdFileName;
	}

	public void setSdFileName(String sdFileName) {
		this.sdFileName = sdFileName;
	}

	@Column(name = "username")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "dataset_id")
	public Long getDatasetId() {
		return this.datasetId;
	}
	
	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}
	
	@Transient
	public String toString() {
		return "Predictor: " + name + " Username: " + userName
				+ " PredictorId: " + predictorId + " SDFile: " + sdFileName
				+ " ACTFile: " + actFileName;
	}

	@Column(name = "model_descriptors")
	public String getDescriptorGeneration() {
		return descriptorGeneration;
	}

	public void setDescriptorGeneration(String descriptorGeneration) {
		this.descriptorGeneration = descriptorGeneration;
	}

	@Column(name = "model_method")
	public String getModelMethod() {
		return modelMethod;
	}

	public void setModelMethod(String modelMethod) {
		this.modelMethod = modelMethod;
	}
	
	@Column(name = "stdDevCutoff")
	public String getStdDevCutoff() {
		return stdDevCutoff;
	}
	public void setStdDevCutoff(String stdDevCutoff) {
		this.stdDevCutoff = stdDevCutoff;
	}

	@Column(name = "correlationCutoff")
	public String getCorrelationCutoff() {
		return correlationCutoff;
	}
	public void setCorrelationCutoff(String correlationCutoff) {
		this.correlationCutoff = correlationCutoff;
	}

	@Column(name = "created_datetime", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Column(name = "updated_datetime")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateUpdated() {
		return dateUpdated;
	}
	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	@Column(name = " num_models_test")	
	public int getNumTestModels() {
		return numTestModels;
	}
	public void setNumTestModels(int numTestModels) {
		this.numTestModels = numTestModels;
	}

	@Column(name = "num_models_total")	
	public int getNumTotalModels() {
		return numTotalModels;
	}
	public void setNumTotalModels(int numTotalModels) {
		this.numTotalModels = numTotalModels;
	}

	@Column(name = "num_models_train")
	public int getNumTrainModels() {
		return numTrainModels;
	}
	public void setNumTrainModels(int numTrainModels) {
		this.numTrainModels = numTrainModels;
	}
	
	@Column(name="y_random_total")
	public int getNumyTotalModels()
	{
		return numyTotalModels;
	}
	public void setNumyTotalModels(int total)
	{
		this.numyTotalModels=total;
	}
	
	@Column(name="y_random_train")
	public int getNumyTrainModels()
	{
		return numyTrainModels;
	}
	public void setNumyTrainModels(int train)
	{
		this.numyTrainModels=train;
	}
	
	@Column(name="y_random_test")
	public int getNumyTestModels()
	{
		return numyTestModels;
	}
	public void setNumyTestModels(int test)
	{
		this.numyTestModels=test;
	}

	@Column(name="status")
	public String getStatus()
	{
		return this.status;
	}
	public void setStatus(String status)
	{
		this.status=status;
	}
	
	@Column(name="act_type")
	public String getActivityType()
	{
		return this.activityType;
	}
	public void setActivityType(String type)
	{
		this.activityType=type;
	}

	@Column(name="type")
	public String getPredictorType()
	{
		//public? private? etc
		return this.predictorType;
	}
	public void setPredictorType(String type)
	{
		this.predictorType=type;
	}
	

	@Column(name="scalingtype")
	public String getScalingType()
	{
		//public? private? etc
		return this.scalingType;
	}
	public void setScalingType(String scalingtype)
	{
		this.scalingType=scalingtype;
	}
	
	@Column(name="num_predictions_made")
	public int getNumPredictions()
	{
		return numPredictions;
	}
	public void setNumPredictions(int numPredictions)
	{
		this.numPredictions=numPredictions;
	}
	
	@Column(name="description")
	public String getDescription()
	{
		//description of what the predictor predicts, where it's from, assays, etc.
		return this.description;
	}
	public void setDescription(String description)
	{
		this.description=description;
	}
	
	@Column(name="paper_reference")
	public String getPaperReference()
	{
		return this.paperReference;
	}
	public void setPaperReference(String paperReference)
	{
		this.paperReference=paperReference;
	}
	
	@Column(name = "hasBeenViewed")
	public String getHasBeenViewed() {
		return hasBeenViewed;
	}
	public void setHasBeenViewed(String hasBeenViewed) {
		this.hasBeenViewed = hasBeenViewed;
	}

	@Column(name = "jobCompleted")
	public String getJobCompleted() {
		return jobCompleted;
	}
	public void setJobCompleted(String jobCompleted) {
		this.jobCompleted = jobCompleted;
	}

//datasplit parameters

	@Column(name = "numSplits")
	public String getNumSplits() {
		return numSplits;
	}
	public void setNumSplits(String numSplits) {
		this.numSplits = numSplits;
	}

	@Column(name = "trainTestSplitType")
	public String getTrainTestSplitType() {
		return trainTestSplitType;
	}
	public void setTrainTestSplitType(String trainTestSplitType) {
		this.trainTestSplitType = trainTestSplitType;
	}

	@Column(name = "randomSplitMinTestSize")
	public String getRandomSplitMinTestSize() {
		return randomSplitMinTestSize;
	}
	public void setRandomSplitMinTestSize(String randomSplitMinTestSize) {
		this.randomSplitMinTestSize = randomSplitMinTestSize;
	}

	@Column(name = "randomSplitMaxTestSize")
	public String getRandomSplitMaxTestSize() {
		return randomSplitMaxTestSize;
	}
	public void setRandomSplitMaxTestSize(String randomSplitMaxTestSize) {
		this.randomSplitMaxTestSize = randomSplitMaxTestSize;
	}

	@Column(name = "splitIncludesMin")
	public String getSplitIncludesMin() {
		return splitIncludesMin;
	}
	public void setSplitIncludesMin(String splitIncludesMin) {
		this.splitIncludesMin = splitIncludesMin;
	}

	@Column(name = "splitIncludesMax")
	public String getSplitIncludesMax() {
		return splitIncludesMax;
	}
	public void setSplitIncludesMax(String splitIncludesMax) {
		this.splitIncludesMax = splitIncludesMax;
	}

	@Column(name = "sphereSplitMinTestSize")
	public String getSphereSplitMinTestSize() {
		return sphereSplitMinTestSize;
	}
	public void setSphereSplitMinTestSize(String sphereSplitMinTestSize) {
		this.sphereSplitMinTestSize = sphereSplitMinTestSize;
	}

	@Column(name = "selectionNextTrainPt")
	public String getSelectionNextTrainPt() {
		return selectionNextTrainPt;
	}
	public void setSelectionNextTrainPt(String selectionNextTrainPt) {
		this.selectionNextTrainPt = selectionNextTrainPt;
	}
//END datasplit parameters

	@Column(name = "minNumDescriptors")
	public String getMinNumDescriptors() {
		return minNumDescriptors;
	}
	public void setMinNumDescriptors(String minNumDescriptors) {
		this.minNumDescriptors = minNumDescriptors;
	}

	@Column(name = "stepSize")
	public String getStepSize() {
		return stepSize;
	}
	public void setStepSize(String stepSize) {
		this.stepSize = stepSize;
	}

	@Column(name = "maxNumDescriptors")
	public String getMaxNumDescriptors() {
		return maxNumDescriptors;
	}
	public void setMaxNumDescriptors(String maxNumDescriptors) {
		this.maxNumDescriptors = maxNumDescriptors;
	}

	@Column(name = "knnCategoryOptimization")
	public String getKnnCategoryOptimization() {
		return knnCategoryOptimization;
	}
	public void setKnnCategoryOptimization(String knnCategoryOptimization) {
		this.knnCategoryOptimization = knnCategoryOptimization;
	}

	@Column(name = "numCycles")
	public String getNumCycles() {
		return numCycles;
	}
	public void setNumCycles(String numCycles) {
		this.numCycles = numCycles;
	}

	@Column(name = "nearestNeighbors")
	public String getNearestNeighbors() {
		return nearestNeighbors;
	}
	public void setNearestNeighbors(String nearestNeighbors) {
		this.nearestNeighbors = nearestNeighbors;
	}

	@Column(name = "pseudoNeighbors")
	public String getPseudoNeighbors() {
		return pseudoNeighbors;
	}
	public void setPseudoNeighbors(String pseudoNeighbors) {
		this.pseudoNeighbors = pseudoNeighbors;
	}

	@Column(name = "numRuns")
	public String getNumRuns() {
		return numRuns;
	}
	public void setNumRuns(String numRuns) {
		this.numRuns = numRuns;
	}

	@Column(name = "numMutations")
	public String getNumMutations() {
		return numMutations;
	}
	public void setNumMutations(String numMutations) {
		this.numMutations = numMutations;
	}

	@Column(name = "T1")
	public String getT1() {
		return T1;
	}
	public void setT1(String t1) {
		T1 = t1;
	}

	@Column(name = "T2")
	public String getT2() {
		return T2;
	}
	public void setT2(String t2) {
		T2 = t2;
	}

	@Column(name = "mu")
	public String getMu() {
		return mu;
	}
	public void setMu(String mu) {
		this.mu = mu;
	}

	@Column(name = "TcOverTb")
	public String getTcOverTb() {
		return TcOverTb;
	}
	public void setTcOverTb(String tcOverTb) {
		TcOverTb = tcOverTb;
	}

	@Column(name = "cutoff")
	public String getCutoff() {
		return cutoff;
	}
	public void setCutoff(String cutoff) {
		this.cutoff = cutoff;
	}

	@Column(name = "minAccTraining")
	public String getMinAccTraining() {
		return minAccTraining;
	}
	public void setMinAccTraining(String minAccTraining) {
		this.minAccTraining = minAccTraining;
	}

	@Column(name = "minAccTest")
	public String getMinAccTest() {
		return minAccTest;
	}
	public void setMinAccTest(String minAccTest) {
		this.minAccTest = minAccTest;
	}

	@Column(name = "minSlopes")
	public String getMinSlopes() {
		return minSlopes;
	}
	public void setMinSlopes(String minSlopes) {
		this.minSlopes = minSlopes;
	}

	@Column(name = "maxSlopes")
	public String getMaxSlopes() {
		return maxSlopes;
	}
	public void setMaxSlopes(String maxSlopes) {
		this.maxSlopes = maxSlopes;
	}

	@Column(name = "relativeDiffRR0")
	public String getRelativeDiffRR0() {
		return relativeDiffRR0;
	}
	public void setRelativeDiffRR0(String relativeDiffRR0) {
		this.relativeDiffRR0 = relativeDiffRR0;
	}

	@Column(name = "diffR01R02")
	public String getDiffR01R02() {
		return diffR01R02;
	}
	public void setDiffR01R02(String diffR01R02) {
		this.diffR01R02 = diffR01R02;
	}

	@Column(name = "stopCond")
	public String getStopCond() {
		return stopCond;
	}
	public void setStopCond(String stopCond) {
		this.stopCond = stopCond;
	}

	private String datasetDisplay = "";
	@Transient
	//this needs to be manually set before it can be returned
	//since it does not correspond to any database field
	public String getDatasetDisplay() {
		return datasetDisplay;
	}
	public void setDatasetDisplay(String datasetDisplay) {
		this.datasetDisplay = datasetDisplay;
	}
	
}
