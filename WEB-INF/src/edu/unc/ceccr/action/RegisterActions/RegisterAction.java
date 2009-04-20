package edu.unc.ceccr.action.RegisterActions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Iterator;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.Transaction;

import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.formbean.UserInfoFormBean;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;
import net.tanesha.recaptcha.*;

public class RegisterAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); 
		
		forward = mapping.findForward("success");
		
		UserInfoFormBean registerForm=(UserInfoFormBean)form;
		
		HttpSession session = request.getSession(false);
		
		 session.removeAttribute("error1");
		 session.removeAttribute("error2");
		 session.removeAttribute("error3");
		 
		boolean valid=true;
		String error1="",error2="",error3="";
		
		//check if CAPTCHA was passed
		ReCaptcha captcha = ReCaptchaFactory.newReCaptcha(Constants.RECAPTCHA_PUBLICKEY,Constants.RECAPTCHA_PRIVATEKEY, false);
        ReCaptchaResponse resp = captcha.checkAnswer(request.getRemoteAddr(), request.getParameter("recaptcha_challenge_field"), request.getParameter("recaptcha_response_field"));

        if (!resp.isValid()) {
        	session.removeAttribute("notValid");
			session.setAttribute("notValid","true");
        	forward = mapping.findForward("back");
        }
        else{
        	//CAPTCHA passed. Validate first name.
		if(!IsValid(registerForm.getFirstName()))
		{
			 error1="This is not a valid first name : <font color=red><u>"+registerForm.getFirstName()+"</u></font>";
			 session.setAttribute("error1",error1);
			valid=false;
		}
    		//Validate last name.
		if(!IsValid(registerForm.getLastName()))
		{
			error2="This is not a valid last name : <font color=red><u>"+registerForm.getLastName()+"</u></font>";
			 session.setAttribute("error2",error2);
			valid=false;
		}
			//Check whether the username already exists 
			//(queries database)
		if(UserExists(registerForm.getUserName()))
		{
			error3="The user name <font color=red><u>"+registerForm.getUserName()+"</u></font>"+" already in use.";
			 session.setAttribute("error3",error3);
			valid=false;
		}
		if(!valid)
		{
			forward = mapping.findForward("failure"); return forward;
		}
				
		User information=new User();
		information.setAddress(registerForm.getAddress());
		information.setCity(registerForm.getCity());
		information.setCountry(registerForm.getCountry());
		information.setEmail(registerForm.getEmail());
		information.setFirstName(registerForm.getFirstName());
		information.setLastName(registerForm.getLastName());
		
		information.setOrgName(registerForm.getNameOfOrg());
		information.setOrgType(registerForm.getOrganization());
		
		information.setPhone(registerForm.getPhone());
		information.setOrgPosition(registerForm.getPosition());
		information.setState(registerForm.getState());
		information.setZipCode(registerForm.getZipCode());
		information.setUserName(registerForm.getUserName());
		information.setWorkbench(registerForm.getWorkbench());
		information.setStatus("NOTSET");
		
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.save(information);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
			forward = mapping.findForward("failure"); 
			return forward;
		} finally {
			s.close();
		}
  	  String errormessage = "error"; //used in case emailing their password to them won't work (e.g. mail server dead)
      try{
    	  String setting=Constants.ACCEPTANCE;
    	  if(setting.contains("manual"))
    	  {
    		 errormessage = "An error occurred while processing your request. Please contact " +
  		 		"an administrator to finish setting up your account.";
    		 
    		 sendEmail2Admin(information);
      	  }else{ 
      		 String password=Utility.randomPassword();
      		 errormessage = "Thank you for you interest in CECCR's C-Chembench. <br/>Your account has been approved.<br/>"
      			+"<br/> Your user name : <font color=red>"+ information.getUserName() + "</font>"
      			+"<br/> Your temporary password : <font color=red>" + password + "</font>" 
      			+"<br/> Please note that passwords are case sensitive. "
      			+"<br/> In order to change your password,  log in to C-Chembench and click the 'edit profile' link at the upper right."
      			+"<br/>"
      			+"<br/>We hope that you find C-Chembench to be a useful tool. <br/>If you have any problems or suggestions for improvements, please contact us at : "+Constants.WEBSITEEMAIL
      			+"<br/><br/>Thank you. <br/>The C-Chembench Team<br/>";
      		 
      		 sendEmail2User(information, password);
      }
    	
      }catch(Exception ex){
    	  Utility.writeToDebug("Failed to send email for user registration: " + information.getUserName());
    	  Utility.writeToDebug(ex);
    	  Utility.writeToDebug("Error message: " + errormessage);
 		  session.removeAttribute("error1");
 		  session.setAttribute("error1", errormessage);
 		  session.removeAttribute("user");
    	  forward = mapping.findForward("failure"); 
    	  return forward;
      }
		}
		return forward;
	}
	
	
	public void sendEmail2User(User userInfo, String password)throws Exception
	{
		userInfo.setStatus("agree");
		userInfo.setPassword(Utility.encrypt(password));
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(userInfo);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		Properties props=System.getProperties();
		props.put(Constants.MAILHOST,Constants.MAILSERVER);
		javax.mail.Session session=javax.mail.Session.getInstance(props,null);
		Message message=new MimeMessage(session);
		message.setFrom(new InternetAddress(Constants.WEBSITEEMAIL));
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(userInfo.getEmail()));
		message.setSubject("Congratulations, "+userInfo.getFirstName());
		String HtmlBody="Thank you for you interest in CECCR's C-Chembench. <br/>Your account has been approved.<br/>"
		+"<br/> Your user name : "+userInfo.getUserName()
		+"<br/> Your temporary password : " +password
		+"<br/> Please note that passwords are case sensitive. "
		+"<br/> In order to change your password,  log in to C-Chembench and click the 'edit profile' link at the upper right. <br/> You may change your password any time."
		+"<br/>"
		+"<br/>We hope that you find C-Chembench to be a useful tool. <br/>If you have any problems or suggestions for improvements, please contact us at : "+Constants.WEBSITEEMAIL
		+"<br/><br/>Thank you. <br/>The C-Chembench Team<br/>"+ new Date();
		
		message.setContent(HtmlBody, "text/html");
			
		Transport.send(message);
		Utility.writeToDebug("In case email failed: Temporary password for user '" + userInfo.getUserName() + "' is: " + password);

	}
	
	public void sendEmail2Admin(User userInfo)throws Exception
	{
		Properties props=System.getProperties();
		props.put(Constants.MAILHOST,Constants.MAILSERVER);
		javax.mail.Session session=javax.mail.Session.getInstance(props,null);
		Message message=new MimeMessage(session);
		message.setFrom(new InternetAddress(Constants.WEBSITEEMAIL));
		
		Iterator it=Constants.ADMINEMAIL_LIST.iterator();
		while(it.hasNext())
		{
			message.addRecipient(Message.RecipientType.TO,new InternetAddress((String)it.next()));
		}
		
		message.setSubject("New user registered");
		String HtmlBody="(This message is automatically generated by the C-Chembench system.  Please do NOT reply.) "
		+"<br/><br/><br/>A new user has requested a login for C-Chembench."
		+"<br/> <br/>To view this user's information,  please log on to C-Chembench."
		+"<br/><br/> Thank you,"
		+"<br/><br/><br/>"
		+ new Date();
		
		message.setContent(HtmlBody, "text/html");
		Transport.send(message);
		String errormessage = "A C-Chembench administrator will process your user request. " +
			"If you are approved, you will be given a password to log in.";

	}	
	public boolean IsValid(String name)
	{
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
	public boolean UserExists(String userName)throws Exception
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
}
