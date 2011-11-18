package edu.unc.ceccr.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;

public class AdminAction extends ActionSupport{

	User user;
	String buildDate;
	ArrayList<User> users;
	
	//for sending email to all users
	String emailMessage;
	String emailSubject;
	String sendTo;
	
	public String loadPage() throws Exception {

		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}

		//log the results
		Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to admin page.");
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)

		// open database connection
		Session session = HibernateUtil.getSession();
	
		// Latest Build Date
		buildDate = Constants.BUILD_DATE;
	
		// list of users
		users = PopulateDataObjects.getAllUsers(session);	
		session.close();

		//go to the page
		return result;
	}
	
	public String loadEmailAllUsersPage() throws Exception {
		sendTo = "JUSTME";
		return SUCCESS;
	}
	
	public String emailSelectedUsers() throws Exception {
		//check that the user is logged in
		Utility.writeToDebug("emailing SELECTED user(s)");
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				return LOGIN;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				Utility.writeToDebug("user " + user.getUserName() + " isn't an admin");
				return ERROR;
			}
		}
		if(!sendTo.trim().isEmpty() && !emailMessage.trim().isEmpty() && !emailSubject.trim().isEmpty()){
			List<String> emails = Arrays.asList(sendTo.split(";"));
			Iterator<String> it=emails.iterator();
			while(it.hasNext()){
				String email = it.next();
				if(!email.trim().isEmpty()) SendEmails.sendEmail(email, "", "", emailSubject, emailMessage);
			}
		}
		return SUCCESS;
	}
	
	public String emailAllUsers() throws Exception {
		//check that the user is logged in
		Utility.writeToDebug("emailing user(s)");
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				return LOGIN;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				Utility.writeToDebug("user " + user.getUserName() + " isn't an admin");
				return ERROR;
			}
		}
		
		Session s = HibernateUtil.getSession();
		List<User> userList= PopulateDataObjects.getAllUsers(s);
		s.close();
			
		if(sendTo.equals("ALLUSERS") && ! emailMessage.trim().isEmpty() && ! emailSubject.trim().isEmpty()){
			Iterator<User> it=userList.iterator();
			while(it.hasNext()){
				User userInfo = it.next();
				SendEmails.sendEmail(userInfo.getEmail(), "", "", emailSubject, emailMessage);
			}
		}
		else if(sendTo.equals("JUSTME") && ! emailMessage.trim().isEmpty() && ! emailSubject.trim().isEmpty()){
			SendEmails.sendEmail(user.getEmail(), "", "", emailSubject, emailMessage);
		}
		return SUCCESS;
	}
	
	public String changeUserAdminStatus() throws Exception{
		//get the current user and the username of the user to be altered
		String result = SUCCESS;
		ActionContext context = ActionContext.getContext();

		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}
		String userToChange = ((String[]) context.getParameters().get("userToChange"))[0];

		Session s = HibernateUtil.getSession();
		User toChange = null;
		if(userToChange.equals(user.getUserName())){
			toChange = user;
		}
		else{
			toChange = PopulateDataObjects.getUserByUserName(userToChange, s);
		}
		
		if(toChange.getIsAdmin().equals(Constants.YES)){
			toChange.setIsAdmin(Constants.NO);
		}
		else{
			toChange.setIsAdmin(Constants.YES);
		}
		
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(toChange);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		
		return result;
	}
	
	public String changeUserDescriptorDownloadStatus() throws Exception{
		//get the current user and the username of the user to be altered
		String result = SUCCESS;
		ActionContext context = ActionContext.getContext();

		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}

		String userToChange = ((String[]) context.getParameters().get("userToChange"))[0];

		Session s = HibernateUtil.getSession();
		User toChange = null;
		if(userToChange.equals(user.getUserName())){
			toChange = user;
		}
		else{
			toChange = PopulateDataObjects.getUserByUserName(userToChange, s);
		}
		
		if(toChange.getCanDownloadDescriptors().equals(Constants.YES)){
			toChange.setCanDownloadDescriptors(Constants.NO);
		}
		else{
			toChange.setCanDownloadDescriptors(Constants.YES);
		}

		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(toChange);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		return result;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public String getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getSendTo() {
		return sendTo;
	}
	
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

}