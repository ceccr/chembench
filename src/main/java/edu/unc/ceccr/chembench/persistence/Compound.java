package edu.unc.ceccr.chembench.persistence;


@SuppressWarnings("serial")
public class Compound implements java.io.Serializable {

    //not yet saved to DB, but someday it will be

    private String compoundId;
    private String activityValue;

    public String getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(String compoundId) {
        this.compoundId = compoundId;
    }

    public String getActivityValue() {
        return activityValue;
    }

    public void setActivityValue(String activityValue) {
        this.activityValue = activityValue;
    }
}
