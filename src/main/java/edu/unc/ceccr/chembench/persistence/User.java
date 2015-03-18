package edu.unc.ceccr.chembench.persistence;


import com.opensymphony.xwork2.ActionContext;
import edu.unc.ceccr.chembench.utilities.Utility;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.beans.Transient;
import java.util.Date;

@Entity
@Table(name = "cbench_user")
@SuppressWarnings("serial")
public class User implements java.io.Serializable {

    //system-relevant stuff
    private String userName;
    private String password;
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
    private String viewPredictionCompoundsPerPage;
    private String showAdvancedKnnModeling;

    //user privileges
    private String isAdmin;
    private String canDownloadDescriptors;

    //users last login time
    private Date lastLogintime;

    // user creation time
    private Date creationTime;

    public User() {
    }

    ;

    public User(String userName) {
        this.userName = userName;
    }

    @Transient
    public static User getCurrentUser() {
        ActionContext context = ActionContext.getContext();
        User user = (User) context.getSession().get("user");
        return user;
    }

    //system-relevant get-sets
    @Id
    @Column(name = "username")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = Utility.truncateString(userName, 55);
        this.userName = userName;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email")
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        email = Utility.truncateString(email, 55);
        this.email = email;
    }

    @Column(name = "bench")
    public String getWorkbench() {
        return this.workbench;
    }

    public void setWorkbench(String bench) {
        this.workbench = bench;
    }

    @Column(name = "status")
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //professional info get-sets
    @Column(name = "firstname")
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        firstName = Utility.truncateString(firstName, 29);
        this.firstName = firstName;
    }

    @Column(name = "lastname")
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        email = Utility.truncateString(email, 55);
        this.lastName = lastName;
    }

    @Column(name = "orgname")
    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        orgName = Utility.truncateString(orgName, 55);
        this.orgName = orgName;
    }

    @Column(name = "orgtype")
    public String getOrgType() {
        return this.orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    @Column(name = "orgposition")
    public String getOrgPosition() {
        return this.orgPosition;
    }

    public void setOrgPosition(String orgPosition) {
        orgPosition = Utility.truncateString(orgPosition, 55);
        this.orgPosition = orgPosition;
    }

    //stalker info get-sets

    @Column(name = "address")
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        address = Utility.truncateString(address, 55);
        this.address = address;
    }

    @Column(name = "city")
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        city = Utility.truncateString(city, 55);
        this.city = city;
    }

    @Column(name = "state")
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        state = Utility.truncateString(state, 55);
        this.state = state;
    }

    @Column(name = "country")
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        country = Utility.truncateString(country, 55);
        this.country = country;
    }

    @Column(name = "zip")
    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        zipCode = Utility.truncateString(zipCode, 55);
        this.zipCode = zipCode;
    }

    @Column(name = "phone")
    public String getPhone() {
        phone = Utility.truncateString(phone, 55);
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    //user options

    @Column(name = "showPublicDatasets")
    public String getShowPublicDatasets() {
        return showPublicDatasets;
    }

    public void setShowPublicDatasets(String showPublicDatasets) {
        this.showPublicDatasets = showPublicDatasets;
    }

    @Column(name = "showPublicPredictors")
    public String getShowPublicPredictors() {
        return showPublicPredictors;
    }

    public void setShowPublicPredictors(String showPublicPredictors) {
        this.showPublicPredictors = showPublicPredictors;
    }

    @Column(name = "viewDatasetCompoundsPerPage")
    public String getViewDatasetCompoundsPerPage() {
        return viewDatasetCompoundsPerPage;
    }

    public void setViewDatasetCompoundsPerPage(String viewDatasetCompoundsPerPage) {
        this.viewDatasetCompoundsPerPage = viewDatasetCompoundsPerPage;
    }

    @Column(name = "viewPredictionCompoundsPerPage")
    public String getViewPredictionCompoundsPerPage() {
        return viewPredictionCompoundsPerPage;
    }

    public void setViewPredictionCompoundsPerPage(String viewPredictionCompoundsPerPage) {
        this.viewPredictionCompoundsPerPage = viewPredictionCompoundsPerPage;
    }

    @Column(name = "showAdvancedKnnModeling")
    public String getShowAdvancedKnnModeling() {
        return showAdvancedKnnModeling;
    }

    public void setShowAdvancedKnnModeling(String showAdvancedKnnModeling) {
        this.showAdvancedKnnModeling = showAdvancedKnnModeling;
    }

    @Column(name = "isAdmin")
    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Column(name = "canDownloadDescriptors")
    public String getCanDownloadDescriptors() {
        return canDownloadDescriptors;
    }

    public void setCanDownloadDescriptors(String canDownloadDescriptors) {
        this.canDownloadDescriptors = canDownloadDescriptors;
    }

    @Column(name = "lastLoginTime")
    public Date getLastLogintime() {
        return lastLogintime;
    }

    public void setLastLogintime(Date lastLogintime) {
        this.lastLogintime = lastLogintime;
    }

    @Column(name = "creationTime")
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
}
