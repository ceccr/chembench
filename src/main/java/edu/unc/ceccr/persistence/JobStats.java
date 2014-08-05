package edu.unc.ceccr.persistence;

import javax.persistence.*;
import java.util.Date;

@Entity()
@Table(name = "cbench_jobStats")
public class JobStats {

    public Long id;
    private String userName;
    private String jobName;
    private String jobType;
    private String modelingMethod;
    private int numCompounds;
    private int numModels;

    private Date timeCreated;
    private Date timeStarted;
    private Date timeStartedByLsf; //jobs may remain pending in LSF for a long time before actually started.
    private Date timeFinished;

    public JobStats() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "userName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "jobName")
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Column(name = "jobType")
    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    @Column(name = "modelingMethod")
    public String getModelingMethod() {
        return modelingMethod;
    }

    public void setModelingMethod(String modelingMethod) {
        this.modelingMethod = modelingMethod;
    }

    @Column(name = "numCompounds")
    public int getNumCompounds() {
        return numCompounds;
    }

    public void setNumCompounds(int numCompounds) {
        this.numCompounds = numCompounds;
    }

    @Column(name = "numModels")
    public int getNumModels() {
        return numModels;
    }

    public void setNumModels(int numModels) {
        this.numModels = numModels;
    }

    @Column(name = "timeCreated")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "timeStarted")
    public Date getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(Date timeStarted) {
        this.timeStarted = timeStarted;
    }

    @Column(name = "timeStartedByLsf")
    public Date getTimeStartedByLsf() {
        return timeStartedByLsf;
    }

    public void setTimeStartedByLsf(Date timeStartedByLsf) {
        this.timeStartedByLsf = timeStartedByLsf;
    }

    @Column(name = "timeFinished")
    public Date getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(Date timeFinished) {
        this.timeFinished = timeFinished;
    }
}