package edu.unc.ceccr.session;

import java.io.Serializable;
import java.util.Date;

public class PredictorDatabaseSessionObj implements Serializable{

	private String databaseName;
	
	private int numberOfCompounds;
	
	private String displayName;
	
	private Date dateCreated;
	
	private String userName;
			
	public PredictorDatabaseSessionObj(String databaseName, int numberOfCompounds, Date date,String userName) 
	{
		super();
		this.databaseName = databaseName;
		this.dateCreated=date;
		this.userName=userName;
		this.numberOfCompounds = numberOfCompounds;
	}

	public Date getDateCreated()
	{
		return this.dateCreated;
	}
	public void setDateCreated(Date date)
	{
		this.dateCreated=date;
	}
	
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	
	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDisplayName() {
		return databaseName + " " +"(" +numberOfCompounds+" compounds)" ;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getNumberOfCompounds() {
		return numberOfCompounds;
	}

	public void setNumberOfCompounds(int numberOfCompounds) {
		this.numberOfCompounds = numberOfCompounds;
	}
	
	
	
}
