package edu.unc.ceccr.persistence;


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
@Table(name = "cbench_prediction")
public class Prediction implements java.io.Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String predictorIds;
    private Long datasetId;
    private String database;
    private Float similarityCutoff;
    private String userName;
    private Date dateCreated;
    private String hasBeenViewed;
    private String jobCompleted; //Initially NO; YES on completion.
    private String computeZscore;

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
    @Column(name = "id")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "prediction_database") //the name of the *SDF* being predicted
    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }


    @Column(name = "predictor_ids")
    public String getPredictorIds() {
        return predictorIds;
    }
    public void setPredictorIds(String predictorIds) {
        this.predictorIds = predictorIds;
    }

    @Column(name = "cutoff_value")
    public Float getSimilarityCutoff() {
        return similarityCutoff;
    }

    public void setSimilarityCutoff(Float similarityCutoff) {
        this.similarityCutoff = similarityCutoff;
    }

    @Column(name = "computeZscore")
    public String getComputeZscore() {
        return computeZscore;
    }

     public void setComputeZscore(String computeZscore){
     this.computeZscore = computeZscore;
     }

    @Column(name = "username")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
