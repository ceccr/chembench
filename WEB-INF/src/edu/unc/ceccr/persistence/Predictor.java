package edu.unc.ceccr.persistence;

// default package
// Generated Jun 20, 2006 1:22:16 PM by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "cbench_predictor")
public class Predictor implements java.io.Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String userName;
	private Long id;
	private Long datasetId;
	private String name;

	private Long parentId; //reference to a parent predictor
	private String childIds; //refs to child predictors
	private String childType; //NFOLD (or COMBI, which is not implemented yet).
	
	private String sdFileName;
	private String actFileName;
	
	private String categoryWeights;
	
	private Date dateCreated;
	private Date dateUpdated;
	
	private int numTotalModels;
	private int numTrainModels;
	private int numTestModels;
	private String status;
	
	private int numyTotalModels;
	private int numyTrainModels;
	private int numyTestModels;

	private String modelMethod; //knn, svm...
	private String descriptorGeneration;
	private String stdDevCutoff;
	private String correlationCutoff;
	private String activityType;
	private String predictorType;
	private String scalingType;
	private String hasBeenViewed;
	private String externalPredictionAccuracy; 
	private String externalPredictionAccuracyAvg; //used on parent predictors, e.g. NFOLD
	private String jobCompleted; //Initially NO; YES on completion.
	
	private int numPredictions;
	private String description;
	private String paperReference;
	private String confusionMatrix;
	

	//references a row in KnnParameters, SvmParameters, RandomForestParameters, 
	//or KnnPlusParameters depending on modelMethod.
	private Long modelingParametersId; 
	
	//datasplit

	private String numSplits;
	private String trainTestSplitType;
		
		//if random split
		private String randomSplitMinTestSize;
		private String randomSplitMaxTestSize;		
		private String randomSplitSampleWithReplacement;
		
		//if sphere exclusion
		private String splitIncludesMin;
		private String splitIncludesMax;
		private String sphereSplitMinTestSize;
		private String selectionNextTrainPt;
		
	//uploaded descriptors type
	private String uploadedDescriptorType;
	
	// Constructors

	/** default constructor */
	public Predictor() {
	
	}

	/** minimal constructor */
	public Predictor(Long id, String name, String userName) {
		this.id = id;
		this.name = name;
		this.userName = userName;
	}

	/** full constructor */
	public Predictor(Long id, String name,
			String sdFileName, String actFileName, String userName) {
		this.id = id;
		this.name = name;
		this.sdFileName = sdFileName;
		this.actFileName = actFileName;
		this.userName = userName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name")
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTFileName")
	public String getActFileName() {
		return actFileName;
	}

	public void setActFileName(String actFileName) {
		this.actFileName = actFileName;
	}

	@Column(name = "categoryWeights")
	public String getCategoryWeights() {
		return categoryWeights;
	}

	public void setCategoryWeights(String categoryWeights) {
		this.categoryWeights = categoryWeights;
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
	
	@Column(name = "parentId")
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Column(name = "childIds")
	public String getChildIds() {
		return childIds;
	}
	public void setChildIds(String childIds) {
		this.childIds = childIds;
	}

	@Column(name = "childType")
	public String getChildType() {
		return childType;
	}
	public void setChildType(String childType) {
		this.childType = childType;
	}

	@Transient
	public String toString() {
		return "Predictor: " + name + " Username: " + userName
				+ " id: " + id + " SDFile: " + sdFileName
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
	
	@Column(name = "num_models_test")	
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

	@Column(name="confusionMatrix")
	public String getConfusionMatrix() {
		return confusionMatrix;
	}
	public void setConfusionMatrix(String confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}

	@Column(name="scalingtype")
	public String getScalingType()
	{
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

	@Column(name = "externalPredictionAccuracy")
	public String getExternalPredictionAccuracy() {
		return externalPredictionAccuracy;
	}
	public void setExternalPredictionAccuracy(String externalPredictionAccuracy) {
		this.externalPredictionAccuracy = externalPredictionAccuracy;
	}

	@Column(name = "externalPredictionAccuracyAvg")
	public String getExternalPredictionAccuracyAvg() {
		return externalPredictionAccuracyAvg;
	}
	public void setExternalPredictionAccuracyAvg(
			String externalPredictionAccuracyAvg) {
		this.externalPredictionAccuracyAvg = externalPredictionAccuracyAvg;
	}

	@Column(name = "jobCompleted")
	public String getJobCompleted() {
		return jobCompleted;
	}
	public void setJobCompleted(String jobCompleted) {
		this.jobCompleted = jobCompleted;
	}

	@Column(name = "modelingParametersId")
	public Long getModelingParametersId() {
		return modelingParametersId;
	}
	public void setModelingParametersId(Long modelingParametersId) {
		this.modelingParametersId = modelingParametersId;
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

	@Column(name = "randomSplitSampleWithReplacement")
	public String getRandomSplitSampleWithReplacement() {
		return randomSplitSampleWithReplacement;
	}
	public void setRandomSplitSampleWithReplacement(
			String randomSplitSampleWithReplacement) {
		this.randomSplitSampleWithReplacement = randomSplitSampleWithReplacement;
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
	
	@Column(name = "uploadedDescriptorType")
	public String getUploadedDescriptorType() {
		return uploadedDescriptorType;
	}

	public void setUploadedDescriptorType(String uploadedDescriptorType) {
		this.uploadedDescriptorType = uploadedDescriptorType;
	}

//END datasplit parameters
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
