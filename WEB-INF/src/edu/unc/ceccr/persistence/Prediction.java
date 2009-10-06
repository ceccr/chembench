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
	
	private Long predictorId;
	
	private Long datasetId;
	
	private String database;
	
	private float similarityCutoff;
	
	private String userName;
	
	private Date dateCreated;

	private String datasetDisplay;
	
	private List<PredictionValue> predictedValues = new ArrayList<PredictionValue>(0);
	
	private String predictorName;
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

	@Transient
	public String getPredictorName() {
		try{
			Session session = HibernateUtil.getSession();
			String name = PopulateDataObjects.getPredictorById(predictorId, session).getName();
			session.close();
			return name;
		}
		catch(Exception ex){
			return "";
		}
	}

	public void setPredictorName(String predictorName) {
		this.predictorName = predictorName;
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
	
	@Column(name = "prediction_database")
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


	@Column(name = "predictor_id")
	public Long getPredictorId() {
		return predictorId;
	}

	public void setPredictorId(Long predictorId) {
		this.predictorId = predictorId;
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
	

	@Transient
	public String getDatasetDisplay() {
		try{
			Session session = HibernateUtil.getSession();
			String name = PopulateDataObjects.getDataSetById(this.datasetId, session).getFileName();
			session.close();
			return name;
		}
		catch(Exception ex){
			//Utility.writeToDebug(ex);
			return "";
		}
	}

	public void setDatasetDisplay(String datasetDisplay) {
		this.datasetDisplay = datasetDisplay;
	}
}
