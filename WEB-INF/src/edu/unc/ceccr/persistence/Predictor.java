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
import edu.unc.ceccr.global.Constants.DescriptorEnumeration;
import edu.unc.ceccr.global.Constants.DataTypeEnumeration;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

@Entity
@Table(name = "cbench_predictor")
public class Predictor implements java.io.Serializable {

	// Fields

	private Constants.DataTypeEnumeration modelMethod;

	private Constants.DescriptorEnumeration descriptorGeneration;

	private String userName;

	private Long predictorId;
	
	private Long datasetId;

	private String name;

	private String sdFileName, actFileName;

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
	
	private String activityType;

	private String predictorType;
	
	private int numPredictions;

	private String description;
	
	private String paperReference;
	
	private String scalingType;
	
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

	@Enumerated(EnumType.STRING)
	@Column(name = "model_descriptors")
	public Constants.DescriptorEnumeration getDescriptorGeneration() {
		return descriptorGeneration;
	}

	public void setDescriptorGeneration(Constants.DescriptorEnumeration descriptorGeneration) {
		this.descriptorGeneration = descriptorGeneration;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "model_method")
	public Constants.DataTypeEnumeration getModelMethod() {
		return modelMethod;
	}

	public void setModelMethod(Constants.DataTypeEnumeration modelMethod) {
		this.modelMethod = modelMethod;
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

	private String descriptorGenerationDisplay;
	@Transient
	public String getDescriptorGenerationDisplay() {
		Constants.DescriptorEnumeration de = getDescriptorGeneration();
		switch (de) {
		case MOLCONNZ:
			return "MOLCONNZ";
		case DRAGON:
			return "DRAGON";
		case MOE2D:
			return "MOE2D";
		case MACCS:
			return "MACCS";
		default:
			return "ERROR";
		}
	}

	public void setDescriptorGenerationDisplay(
			String descriptorGenerationDisplay) {
		this.descriptorGenerationDisplay = descriptorGenerationDisplay;
	}

	private String modelMethodDisplay;
	@Transient
	public String getModelMethodDisplay() {
		Constants.DataTypeEnumeration ke = getModelMethod();
		switch (ke) {
		case CONTINUOUS:
			return "QSAR Continuous";
		case CATEGORY:
			return "QSAR Category";
		default:
			return "Error";
		}
	}

	public void setModelMethodDisplay(String modelMethodDisplay) {
		this.modelMethodDisplay = modelMethodDisplay;
	}

	private String datasetDisplay;
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
