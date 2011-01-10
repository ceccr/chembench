package edu.unc.ceccr.persistence;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;


public class Compound implements java.io.Serializable{

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