package edu.unc.ceccr.persistence;


@SuppressWarnings("serial")
public class Compound implements java.io.Serializable {

    //not yet saved to DB, but someday it will be

    private String compoundId;
    private String activityValue;
    //private String url_friendly_id;

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

    public String getUrl_friendly_id() {
        return compoundId.replaceAll("%", "%25");
    }
    /*
    public void setUrl_friendly_id(String url_friendly_id) {
		this.url_friendly_id = url_friendly_id;
	}
	*/


}
