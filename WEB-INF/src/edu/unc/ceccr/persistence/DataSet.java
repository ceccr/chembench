package edu.unc.ceccr.persistence;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

@SuppressWarnings("serial")
@Entity
@Table(name="cbench_dataset")
public class DataSet implements java.io.Serializable{
	
	private Long id;
	private String name;
	private String userName;
	private String actFile;
	private String sdfFile;
	private String xFile;
	private String modelType; //continuous or category
	private String datasetType; //prediction, modeling, predictionwithdescriptors, modelingwithdescriptors
	private String uploadedDescriptorType; //used for predictionwithdescriptors and modelingwithdescriptors
	private int numCompound;
	private Date createdTime;
	private String description;
	private String actFormula;
	private String showByDefault;
	private String paperReference;
	private String hasBeenViewed;
	private String availableDescriptors;
	private String jobCompleted; //Initially NO; YES on completion.

	private String standardize; 
	private String splitType; 
	private String hasBeenScaled; 
	private String numExternalCompounds; 
	private String useActivityBinning; 
	private String externalCompoundList;
	private String numExternalFolds; 

	public DataSet(){}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId()
	{
		return this.id;
	}
	public void setId(Long id)
	{
		this.id=id;
	}
	
	@Column(name="name")
	public String getName()
	{
		return this.name;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	
	@Column(name="username")
	public String getUserName()
	{
		return this.userName;
	}
	public void setUserName( String userName)
	{
		this.userName=userName;
	}
	
	@Column(name="actFile")
	public String getActFile()
	{
		return this.actFile;
	}
	public void setActFile(String actFile)
	{
		this.actFile=actFile;
	}
	
	@Column(name="sdfFile")
	public String getSdfFile()
	{
		return this.sdfFile;
	}
	public void setSdfFile(String sdfFile)
	{
		this.sdfFile=sdfFile;
	}

	@Column(name="xFile")
	public String getXFile() {
		return xFile;
	}
	public void setXFile(String file) {
		xFile = file;
	}

	@Column(name="datasetType")
	public String getDatasetType() {
		return datasetType;
	}

	public void setDatasetType(String datasetType) {
		this.datasetType = datasetType;
	}
	
	@Column(name="modelType")
	public String getModelType()
	{
		return this.modelType;
	}
	public void setModelType(String modelType)
	{
		this.modelType=modelType;
	}
	
	@Column(name="uploadedDescriptorType")
	public String getUploadedDescriptorType() {
		return uploadedDescriptorType;
	}
	public void setUploadedDescriptorType(String uploadedDescriptorType) {
		this.uploadedDescriptorType = uploadedDescriptorType;
	}
	
	@Column(name="numCompound")
	public int getNumCompound()
	{
		return this.numCompound;
	}
	public void setNumCompound(int num)
	{
		this.numCompound=num;
	}
	
	@Column(name="createdTime")
	public Date getCreatedTime()
	{
		return this.createdTime;
	}
	public void setCreatedTime(Date date)
	{
		this.createdTime=date;
	}
	
	@Column(name="description")
	public String getDescription()
	{
		return this.description;
	}
	public void setDescription(String description)
	{
		if(description == null){
		description = "";
		}
		this.description=description;
	}
	
	@Column(name="actFormula")
	public String getActFormula() {
		return actFormula;
	}
	public void setActFormula(String actFormula) {
		this.actFormula = actFormula;
	}

	@Column(name="showByDefault")
	public String getShowByDefault() {
		return showByDefault;
	}
	public void setShowByDefault(String showByDefault) {
		this.showByDefault = showByDefault;
	}

	@Column(name="paperReference")
	public String getPaperReference() {
		return paperReference;
	}
	public void setPaperReference(String paperReference) {
		this.paperReference = paperReference;
	}
	
	@Column(name = "hasBeenViewed")
	public String getHasBeenViewed() {
		return hasBeenViewed;
	}
	public void setHasBeenViewed(String hasBeenViewed) {
		this.hasBeenViewed = hasBeenViewed;
	}

	@Column(name = "availableDescriptors")
	public String getAvailableDescriptors() {
		return availableDescriptors;
	}
	public void setAvailableDescriptors(String availableDescriptors) {
		this.availableDescriptors = availableDescriptors;
	}

	@Column(name = "jobCompleted")
	public String getJobCompleted() {
		return jobCompleted;
	}
	public void setJobCompleted(String jobCompleted) {
		this.jobCompleted = jobCompleted;
	}

	@Column(name = "standardize")
	public String getStandardize() {
		return standardize;
	}
	public void setStandardize(String standardize) {
		this.standardize = standardize;
	}

	@Column(name = "splitType")
	public String getSplitType() {
		return splitType;
	}
	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}

	@Column(name = "hasBeenScaled")
	public String getHasBeenScaled() {
		return hasBeenScaled;
	}
	public void setHasBeenScaled(String hasBeenScaled) {
		this.hasBeenScaled = hasBeenScaled;
	}

	@Column(name = "numExternalCompounds")
	public String getNumExternalCompounds() {
		return numExternalCompounds;
	}
	public void setNumExternalCompounds(String numExternalCompounds) {
		this.numExternalCompounds = numExternalCompounds;
	}

	@Column(name = "useActivityBinning")
	public String getUseActivityBinning() {
		return useActivityBinning;
	}
	public void setUseActivityBinning(String useActivityBinning) {
		this.useActivityBinning = useActivityBinning;
	}

	@Column(name = "externalCompoundList")
	public String getExternalCompoundList() {
		return externalCompoundList;
	}
	public void setExternalCompoundList(String externalCompoundList) {
		this.externalCompoundList = externalCompoundList;
	}

	@Column(name = "numExternalFolds")
	public String getNumExternalFolds() {
		return numExternalFolds;
	}
	public void setNumExternalFolds(String numExternalFolds) {
		this.numExternalFolds = numExternalFolds;
	}
	
}
