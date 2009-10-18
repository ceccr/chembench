package edu.unc.ceccr.persistence;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.Session;

import edu.unc.ceccr.utilities.PopulateDataObjects;

@Entity
@Table(name = "cbench_prediction")
public class Prediction implements java.io.Serializable{
	
	private Long predictionId;
	private String jobName;
	private String predictorIds;
	private Long datasetId;
	private String database;
	private float similarityCutoff;
	private String userName;
	private Date dateCreated;

	private List<PredictionValue> predictedValues = new ArrayList<PredictionValue>(0);
	
	private String status;
	@Column(name="status")
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status=status;
	}

	private String predictorNames = "";
	@Transient
	public String getPredictorNames() {
		//this needs to be manually set before it can be returned
		//since it does not correspond to any database field
		return predictorNames;
	}

	public void setPredictorNames(String predictorNames) {
		this.predictorNames = predictorNames;
	}

	private String datasetDisplay = "";
	@Transient
	//this needs to be manually set before it can be returned
	//since it does not correspond to any database field
	public String getDatasetDisplay() { //the name of the *dataset* being predicted
		return datasetDisplay;
	}

	public void setDatasetDisplay(String datasetDisplay) {
		this.datasetDisplay = datasetDisplay;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "prediction_id")
	public Long getPredictionId() {
		return predictionId;
	}

	public void setPredictionId(Long predictionId) {
		this.predictionId = predictionId;
	}
	
	@Column(name = "prediction_database") //the name of the *SDF* being predicted
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	@Column(name = "prediction_name")
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}


	@Column(name = "predictor_ids")
	public String getPredictorIds() {
		return predictorIds;
	}

	public void setPredictorIds(String predictorId) {
		this.predictorIds = predictorIds;
	}

	@Column(name = "cutoff_value")
	public float getSimilarityCutoff() {
		return similarityCutoff;
	}

	public void setSimilarityCutoff(float similarityCutoff) {
		this.similarityCutoff = similarityCutoff;
	}

	@Column(name = "username")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@OneToMany(mappedBy = "predictionJob", cascade = CascadeType.ALL)
	public List<PredictionValue> getPredictedValues() {
		return predictedValues;
	}

	public void setPredictedValues(List<PredictionValue> predictedValues) {
		this.predictedValues = predictedValues;
	}

	@Column(name = "created_datetime", updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	@Column(name = "dataset_id")
	public Long getDatasetId() {
		return this.datasetId;
	}
	
	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}
	

}
