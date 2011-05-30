package edu.unc.ceccr.action;

import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.ErrorMessages;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.CreateDatasetTask;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
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
	
	public String emailAllUsers() throws Exception {
			Session s = HibernateUtil.getSession();
			List<User> userList= PopulateDataObjects.getAllUsers(s);
			s.close();
			
			Iterator it=userList.iterator();
			while(it.hasNext()){
				User userInfo = (User)it.next();
				SendEmails.sendEmail(userInfo.getEmail(), "", "", emailSubject, emailMessage);
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

}