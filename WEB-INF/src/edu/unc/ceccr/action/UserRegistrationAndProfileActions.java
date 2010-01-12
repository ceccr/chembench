package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		return result;
	}
	
	public String RegisterUser() throws Exception{
		ActionContext context = ActionContext.getContext();
		String result = SUCCESS;
		
		//form validation
			//Validate that each required field has something in it.
			if(firstName.isEmpty()){
		    	errorMessages.add("Please enter your first name.");
				result = ERROR;
			}
			if(lastName.isEmpty()){
		    	errorMessages.add("Please enter your last name.");
				result = ERROR;
			}
			if(organizationName.isEmpty()){
		    	errorMessages.add("Please enter your organization name.");
				result = ERROR;
			}
			if(organizationPosition.isEmpty()){
		    	errorMessages.add("Please enter your organization position.");
				result = ERROR;
			}
			if(email.isEmpty() || ! email.contains("@") || ! email.contains(".")){
		    	errorMessages.add("Please enter a valid email address.");
				result = ERROR;
			}
			if(city.isEmpty()){
		    	errorMessages.add("Please enter your city.");
				result = ERROR;
			}
			if(country.isEmpty()){
		    	errorMessages.add("Please enter your country.");
				result = ERROR;
			}
			if(newUserName.isEmpty()){
		    	errorMessages.add("Please enter a user name.");
				result = ERROR;
			}
			
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
	
	public String ChangePassword() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		
		// Change user object to have new password
		if(! newPassword1.equals(newPassword2)){
			errorMessage = "Error: Passwords do not match.";
		}
		
		// Commit changes

		
		return result;
	}
	
	public String EditUserInformation() throws Exception{
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		user = getLoggedInUser(context);
		if(user == null){
			return LOGIN;
		}
		
		// Change user object according to edited fields
		
		
		// Commit changes
		
		
		return result;
	}
	
	/* USER FUNCTIONS */
	
	/* ADMIN-ONLY FUNCTIONS */
	
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
	
	/* END DATA OBJECTS, GETTERS, AND SETTERS */
}	