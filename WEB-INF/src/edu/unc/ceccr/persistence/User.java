package edu.unc.ceccr.persistence;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "cbench_user")
@SuppressWarnings("serial")
public class User implements java.io.Serializable{

	//system-relevant stuff
	private String userName;
	private byte[] password;
	private String email;
	private String workbench;
	private String status;
	
	//professional information
	private String firstName;
	private String lastName;
	private String orgType;
	private String orgName;
	private String orgPosition;
	
	//mostly just for stalking
	private String zipCode;
	private String state;
	private String phone;
	private String country;
	private String address;
	private String city;
	
	//user options (may eventually become a new table of its own)
	private String showPublicDatasets;
	private String showPublicPredictors;
	private String viewDatasetCompoundsPerPage;
	private String viewPredictorModels;
	private String viewPredictionCompoundsPerPage;
	
	public User(){};
    public User(String userName)
    {
     this.userName=userName;
    }
    
	//system-relevant get-sets
	@Id
	@Column(name="username")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name="password")
	public byte[] getPassword() {
		return password;
	}
	public void setPassword(byte[] password) {
		this.password = password;
	}

	@Column(name="email")
	public String getEmail()
	{
		return this.email;
	}
	public void setEmail(String  email)
	{
		this.email=email;
	}
	
	@Column(name="bench")
	public String getWorkbench()
	{
		return this.workbench;
	}
	public void setWorkbench(String bench)
	{
		this.workbench=bench;
	}

	@Column(name="status")
	public String getStatus()
	{
		return this.status;
	}
	public void setStatus(String status)
	{
		this.status=status;
	}
	
	//professional info get-sets
	@Column(name="firstname")
	public String getFirstName()
	{
		return this.firstName;
	}
	public void setFirstName(String firstName)
	{
		this.firstName=firstName;
	}
	
	@Column(name="lastname")
	public String getLastName()
	{
		return this.lastName;
	}
	public void setLastName(String lastName)
	{
		this.lastName=lastName;
	}
	
	@Column(name="orgname")
	public String getOrgName(){
		return this.orgName;
	}
	public void setOrgName(String orgName)
	{
		this.orgName=orgName;
	}

	@Column(name="orgtype")
	public String getOrgType()
	{
		return this.orgType;
	}
	public void setOrgType(String orgType)
	{
		this.orgType=orgType;
	}

	@Column(name="orgposition")
	public String getOrgPosition()
	{
		return this.orgPosition;
	}
	public void setOrgPosition(String orgPosition)
	{
		this.orgPosition=orgPosition;
	}
	
	
	
	
	//stalker info get-sets

	@Column(name="address")
	public String getAddress()
	{
		return this.address;
	}
	public void setAddress(String address)
	{
		this.address=address;
	}
	@Column(name="city")
	public String getCity()
	{
		return this.city;
	}
	public void setCity(String city)
	{
		this.city=city;
	}
	
	@Column(name="state")
	public String getState()
	{
		return this.state;
	}
	public void setState(String state)
	{
		this.state=state;
	}
	
	@Column(name="country")
	public String getCountry()
	{
		return this.country;
	}
	public void setCountry(String country)
	{
		this.country=country;
	}
	
	@Column(name="zip")
	public String getZipCode()
	{
		return this.zipCode;
	}
	public void setZipCode(String zipCode)
	{
		this.zipCode=zipCode;
	}
	
	@Column(name="phone")
	public String getPhone()
	{
		return this.phone;
	}
	public void setPhone(String phone)
	{
		this.phone=phone;
	}
	
	//user options
	
	@Column(name="showPublicDatasets")
	public String getShowPublicDatasets() {
		return showPublicDatasets;
	}
	public void setShowPublicDatasets(String showPublicDatasets) {
		this.showPublicDatasets = showPublicDatasets;
	}

	@Column(name="showPublicPredictors")
	public String getShowPublicPredictors() {
		return showPublicPredictors;
	}
	public void setShowPublicPredictors(String showPublicPredictors) {
		this.showPublicPredictors = showPublicPredictors;
	}

	@Column(name="viewDatasetCompoundsPerPage")
	public String getViewDatasetCompoundsPerPage() {
		return viewDatasetCompoundsPerPage;
	}
	public void setViewDatasetCompoundsPerPage(String viewDatasetCompoundsPerPage) {
		this.viewDatasetCompoundsPerPage = viewDatasetCompoundsPerPage;
	}
	
	@Column(name="viewPredictorModels")
	public String getViewPredictorModels() {
		return viewPredictorModels;
	}
	public void setViewPredictorModels(String viewPredictorModels) {
		this.viewPredictorModels = viewPredictorModels;
	}
	
	@Column(name="viewPredictionCompoundsPerPage")
	public String getViewPredictionCompoundsPerPage() {
		return viewPredictionCompoundsPerPage;
	}
	public void setViewPredictionCompoundsPerPage(
			String viewPredictionCompoundsPerPage) {
		this.viewPredictionCompoundsPerPage = viewPredictionCompoundsPerPage;
	}
	
}
