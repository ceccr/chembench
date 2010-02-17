package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.SmilesPredictionWorkflow;

public class UserRegistrationAndProfileActions extends ActionSupport{

	/* USER FUNCTIONS */
	
	public String loadUserRegistration() throws Exception{
		String result = SUCCESS;
		organizationType = "Academia";
		return result;
	}
	
	public String loadEditProfilePage() throws Exception{
		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		if(Utility.isAdmin(user.getUserName())){
			userIsAdmin = true;
		}
		return result;
	}
	
	public String RegisterUser() throws Exception{
		ActionContext context = ActionContext.getContext();
		String result = SUCCESS;
		Utility.writeToDebug("hrm?");
		//form validation
			//Validate that each required field has something in it.
			validateUserInfo(); //this function will populate the errorMessages arraylist.
			
			if(newUserName.isEmpty()){
		    	errorMessages.add("Please enter a user name.");
			}	
			
			if(errorMessages.isEmpty()){
				result = ERROR;
			}
			Utility.writeToDebug("hrm: " + errorMessages);
			
			//Check whether the username already exists 
			//(queries database)
			if(!newUserName.equals("") && UserExists(newUserName)){
		    	errorMessages.add("The user name '"+newUserName+"' is already in use.");
				result = ERROR;
			}
			
			//check CAPTCHA
			ReCaptcha captcha = ReCaptchaFactory.newReCaptcha(Constants.RECAPTCHA_PUBLICKEY,Constants.RECAPTCHA_PRIVATEKEY, false);

			ReCaptchaResponse resp = captcha.checkAnswer("127.0.0.1", 
	        		((String[])context.getParameters().get("recaptcha_challenge_field"))[0], 
	        		((String[])context.getParameters().get("recaptcha_response_field"))[0]);
	        
		    if (!resp.isValid()) {
		    	errorMessages.add("The text you typed for the CAPTCHA test did not match the picture. Try again.");
	        	result = ERROR;
	        }

			Utility.writeToDebug("hrm 2: " + errorMessages);
			
			if(result.equals(ERROR)){
				return result;
			}
				
			//make user
			user = new User();

			user.setUserName(newUserName);
			user.setEmail(email);
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setOrgName(organizationName);
			user.setOrgType(organizationType);
			user.setOrgPosition(organizationPosition);
			user.setPhone(phoneNumber);
			user.setAddress(address);
			user.setState(stateOrProvince);
			user.setCity(city);
			user.setCountry(country);
			user.setZipCode(zipCode);
			user.setWorkbench(workBench); //deprecated, but some people think it's still important
			
			//options
			user.setShowPublicDatasets(Constants.SOME);
			user.setShowPublicPredictors(Constants.ALL);
			user.setViewDatasetCompoundsPerPage(Constants.TWENTYFIVE);
			user.setViewPredictionCompoundsPerPage(Constants.TWENTYFIVE);

			String password = Utility.randomPassword();
			user.setPassword(Utility.encrypt(password));
			
			if(Constants.ACCEPTANCE.contains("manual")){
				user.setStatus("NOTSET");
			}
			else{
				user.setStatus("agree");
			}
				
			Session s = HibernateUtil.getSession();
			Transaction tx = null;
		
		//commit user to DB
			
			try {
				tx = s.beginTransaction();
				s.saveOrUpdate(user);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {s.close();}
			
		//send user an email
			
			if(Constants.ACCEPTANCE.contains("manual")){
		    	//user needs approval; contact admins
				user.setStatus("NOTSET");

				outputMessage = "A C-Chembench administrator will process your user request. " +
				"If you are approved, you will be emailed a password so that you may log in.";
				 
				String HtmlBody = "(This message is automatically generated by the C-Chembench system.  Please do NOT reply.) "
					+"<br/><br/><br/>A new user, <b>" + user.getUserName() + "</b>, has requested a login for C-Chembench."
					+"<br/> <br/>To view this user's user, please log on to C-Chembench."
					+"<br/><br/> Thank you"
					+"<br/><br/><br/>"
					+ new Date();
				
				SendEmails.sendEmailToAdmins("New user registration", HtmlBody);
			}
			else{
				//user is auto-approved; email them a temp password

				outputMessage = "Your account has been created! " +
				"An email containing your password has been sent to " + email + 
				". Please check your email and log in to Chembench. " +
				"Note: Email delivery may be delayed up to 15 minutes depending on email server load.";
				
				String HtmlBody = "Thank you for you interest in CECCR's C-Chembench. <br/>Your account has been approved.<br/>"
		  			+"<br/> Your user name : <font color=red>"+ user.getUserName() + "</font>"
		  			+"<br/> Your temporary password : <font color=red>" + password + "</font>" 
		  			+"<br/> Please note that passwords are case sensitive. "
		  			+"<br/> In order to change your password,  log in to C-Chembench and click the 'edit profile' link at the upper right."
		  			+"<br/>"
		  			+"<br/>We hope that you find C-Chembench to be a useful tool. <br/>If you have any problems or suggestions for improvements, please contact us at : "+Constants.WEBSITEEMAIL
		  			+"<br/><br/>Thank you. <br/>The C-Chembench Team<br/>";
		
	  			SendEmails.sendEmail(user.getEmail(), "", "", "Chembench User Registration", HtmlBody);

	  			Utility.writeToUsageLog("just registered!", newUserName);
		  		Utility.writeToDebug("In case email failed, temp password for user: " + user.getUserName() + " is: " + password);
		  	}
		return result;
	}
	
	public String loadChangePassword() throws Exception{
		String result = SUCCESS;
		return result;
	}
	
	public String ChangePassword() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		
		byte[] password=user.getPassword();
		
		if (! Utility.compareEncryption(Utility.encrypt(oldPassword),password))
		{
			errorMessages.add("You entered your old password incorrectly. Your password was not changed. Please try again.");
		}
		
		if(!errorMessages.isEmpty()){
			errorMessages.add(0, "Error changing password.");
			return ERROR;
		}
			
		// Change user object to have new password
		Utility.writeToDebug("Changing user password");
		user.setPassword(Utility.encrypt(newPassword));
		
		// Commit changes
		
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
	
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(user);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		
		return result;
	}

	public String loadUpdateUserInformation() throws Exception{
		String result = SUCCESS;
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		
		address = user.getAddress();
		city = user.getCity();
		country = user.getCountry();
		email = user.getEmail();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		organizationName = user.getOrgName();
		organizationType = user.getOrgType();
		organizationPosition = user.getOrgType();
		phoneNumber = user.getPhone();
		stateOrProvince = user.getState();
		zipCode = user.getZipCode();
		workBench = user.getWorkbench();
		
		return result;
	}
	
	public String UpdateUserInformation() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		
		//validate each field
		validateUserInfo();
		if(! errorMessages.isEmpty()){
			return ERROR;
		}
		
		// Change user object according to edited fields
		Utility.writeToDebug("Updating user information");
		user.setEmail(email);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setOrgName(organizationName);
		user.setOrgType(organizationType);
		user.setOrgPosition(organizationPosition);
		user.setPhone(phoneNumber);
		user.setAddress(address);
		user.setState(stateOrProvince);
		user.setCity(city);
		user.setCountry(country);
		user.setZipCode(zipCode);
		user.setWorkbench(workBench); //deprecated, but some people think it's still important
		
		// Commit changes
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(user);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		return result;
	}

	public String loadUpdateUserOptions() throws Exception{
		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		showPublicDatasets = user.getShowPublicDatasets();
		showPublicPredictors = user.getShowPublicPredictors();
		viewDatasetCompoundsPerPage = user.getViewDatasetCompoundsPerPage();
		viewPredictionCompoundsPerPage = user.getViewPredictionCompoundsPerPage();
		
		return result;
	}
	
	public String UpdateUserOptions() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		
		// Change user object according to edited fields
		Utility.writeToDebug("Changing user options");
		user.setShowPublicDatasets(showPublicDatasets);
		user.setShowPublicPredictors(showPublicPredictors);
		user.setViewDatasetCompoundsPerPage(viewDatasetCompoundsPerPage);
		user.setViewPredictionCompoundsPerPage(viewPredictionCompoundsPerPage);
		
		// Commit changes
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(user);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		return result;
	}
	
	/* USER FUNCTIONS */
	
	/* ADMIN-ONLY FUNCTIONS */
	//These do not belong here. They should be moved when 
	//the Admin page has been struts2ified.
	/*
	public String ChangeModelingLimits() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in and is an admin
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		if(! Utility.isAdmin(user.getUserName())){
			return LOGIN;
		}
		
		
		
		return result;
	}
	
	public String UpdateSoftwareExpiration() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in and is an admin
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		if(! Utility.isAdmin(user.getUserName())){
			return LOGIN;
		}
		
		
		
		return result;
	}
	
	public String DenyJob() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in and is an admin
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		if(! Utility.isAdmin(user.getUserName())){
			return LOGIN;
		}
		
		
		
		return result;
	}
	
	public String PermitJob() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in and is an admin
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		if(! Utility.isAdmin(user.getUserName())){
			return LOGIN;
		}
		
		
		
		return result;
	}
	*/
	/* END ADMIN-ONLY FUNCTIONS */
	
	/* HELPER FUNCTIONS */
	
	private User getLoggedInUser(ActionContext context){
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
			return null;
		}
		else{
			user = (User) context.getSession().get("user");
			return user;
		}
	}
	
	private boolean UserExists(String userName)throws Exception
	{
		Session s = HibernateUtil.getSession();// query
		Transaction tx = null;
		User user=null;
		User userInfo=null;
		try {
			tx = s.beginTransaction();
			user=(User)s.createCriteria(User.class).add(Expression.eq("userName",userName))
			      .uniqueResult();
			userInfo=(User)s.createCriteria(User.class)
			       .add(Expression.eq("userName", userName)).uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			s.close();
		}
		if(user==null&&userInfo==null)
		{
			return false;
		}else{return true;}
	}
	
	private boolean IsValid(String name)
	{
		if(name.length() == 0){
			return false;
		}
		//check that no illegal characters are in the string
		String validatorStr=Constants.VALIDATOR_STRING;
		char[]validator=validatorStr.toCharArray();
		char[] nameArray=name.toCharArray();
		for(int i=0;i<nameArray.length;i++)
		{
			for(int j=0;j<validator.length;j++)
			{
				if(nameArray[i]==validator[j])
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void validateUserInfo(){
		if(firstName.isEmpty()){
	    	errorMessages.add("Please enter your first name.");
		}
		if(lastName.isEmpty()){
	    	errorMessages.add("Please enter your last name.");
		}
		if(organizationName.isEmpty()){
	    	errorMessages.add("Please enter your organization name.");
		}
		if(organizationPosition.isEmpty()){
	    	errorMessages.add("Please enter your organization position.");
		}
		if(email.isEmpty() || ! email.contains("@") || ! email.contains(".")){
	    	errorMessages.add("Please enter a valid email address.");
		}
		if(city.isEmpty()){
	    	errorMessages.add("Please enter your city.");
		}
		if(country.isEmpty()){
	    	errorMessages.add("Please enter your country.");
		}
	}
	
	/* END HELPER FUNCTIONS */
		
	
	/* DATA OBJECTS, GETTERS, AND SETTERS */
	
	private User user;

	/* Variables used for user registration and updates */
	private String recaptchaPublicKey = Constants.RECAPTCHA_PUBLICKEY;
	private ArrayList<String> errorMessages = new ArrayList<String>();
	private String outputMessage;
	
	private String newUserName;
	private String address;
	private String city;
	private String country;
	private String email;
	private String firstName;
	private String lastName;
	private String organizationName;
	private String organizationType;
	private String organizationPosition;
	private String phoneNumber;
	private String stateOrProvince;
	private String zipCode;
	private String workBench; //deprecated, but some people think it's still important
	/* End Variables used for user registration and updates */
	
	/* Variables used in password changes and user options */
	private String oldPassword;
	private String newPassword;
	private String showPublicDatasets;
	private String showPublicPredictors;
	private String viewDatasetCompoundsPerPage;
	private String viewPredictorModels;
	private String viewPredictionCompoundsPerPage;
	private boolean userIsAdmin = false;
	
	/* End Variables used in password changes and user options */
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	/* Variables used for user registration and updates */
	public String getRecaptchaPublicKey() {
		return recaptchaPublicKey;
	}
	public void setRecaptchaPublicKey(String recaptchaPublicKey) {
		this.recaptchaPublicKey = recaptchaPublicKey;
	}	
	
	public ArrayList<String> getErrorMessages() {
		return errorMessages;
	}
	public void setErrorMessages(ArrayList<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public String getOutputMessage() {
		return outputMessage;
	}
	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}

	public String getNewUserName() {
		return newUserName;
	}
	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationType() {
		return organizationType;
	}
	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	public String getOrganizationPosition() {
		return organizationPosition;
	}
	public void setOrganizationPosition(String organizationPosition) {
		this.organizationPosition = organizationPosition;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStateOrProvince() {
		return stateOrProvince;
	}
	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}

	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getWorkBench() {
		return workBench;
	}
	public void setWorkBench(String workBench) {
		this.workBench = workBench;
	}
	/* End Variables used for user registration and updates */
	
	
	/* Variables used in password changes and user options */
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getShowPublicDatasets() {
		return showPublicDatasets;
	}
	public void setShowPublicDatasets(String showPublicDatasets) {
		this.showPublicDatasets = showPublicDatasets;
	}

	public String getShowPublicPredictors() {
		return showPublicPredictors;
	}
	public void setShowPublicPredictors(String showPublicPredictors) {
		this.showPublicPredictors = showPublicPredictors;
	}
	
	public String getViewDatasetCompoundsPerPage() {
		return viewDatasetCompoundsPerPage;
	}
	public void setViewDatasetCompoundsPerPage(String viewDatasetCompoundsPerPage) {
		this.viewDatasetCompoundsPerPage = viewDatasetCompoundsPerPage;
	}

	public String getViewPredictorModels() {
		return viewPredictorModels;
	}
	public void setViewPredictorModels(String viewPredictorModels) {
		this.viewPredictorModels = viewPredictorModels;
	}

	public String getViewPredictionCompoundsPerPage() {
		return viewPredictionCompoundsPerPage;
	}
	public void setViewPredictionCompoundsPerPage(String viewPredictionCompoundsPerPage) {
		this.viewPredictionCompoundsPerPage = viewPredictionCompoundsPerPage;
	}
	
	public boolean isUserIsAdmin() {
		return userIsAdmin;
	}
	public void setUserIsAdmin(boolean userIsAdmin) {
		this.userIsAdmin = userIsAdmin;
	}
	/* End Variables used in password changes and user options */
	
	/* END DATA OBJECTS, GETTERS, AND SETTERS */
}	