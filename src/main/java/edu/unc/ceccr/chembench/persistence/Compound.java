package edu.unc.ceccr.chembench.persistence;


public class Compound implements java.io.Serializable {

    //not yet saved to DB, but someday it will be

    private String compoundId;
    private Double activityValue;

    public String getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(String compoundId) {
        this.compoundId = compoundId;
    }

    public Double getActivityValue() {
        return activityValue;
    }

    public void setActivityValue(Double activityValue) {
        this.activityValue = activityValue;
    }
}
