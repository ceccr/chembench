package edu.unc.ceccr.action.RegisterActions;

import java.util.Date;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

public class AgreeAction extends Action {

	@SuppressWarnings("static-access")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Utility utility=new Utility();
		ActionForward forward = new ActionForward(); // return value
		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {

			try {
				Session s = HibernateUtil.getSession();// query
				User userInfo=(User)session.getAttribute("newUserInfo");
				String password=utility.randomPassword();
				
				userInfo.setStatus("agree");
				userInfo.setPassword(utility.encrypt(password));
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					s.saveOrUpdate(userInfo);
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e); 
					forward = mapping.findForward("failure");
				} finally {s.close();
				
				try{
					String HtmlBody="Thank you for you interest in CECCR's C-Chembench. <br/>Your account has been approved.<br/>"
						+"<br/> Your user name : "+userInfo.getUserName()
						+"<br/> Your temporary password : " +password
						+"<br/> Please note that passwords are case sensitive. "
						+"<br/> In order to change your password, log in to C-Chembench ("+ Constants.WEBADDRESS+") and click the 'My Password' button at the upper right. <br/> It will take you to the change password page.  You may change your password any time through these pages."
						+"<br/><br/> If you forget your password, click the 'User' button next to the login.  Your password will be reset and the new password will be sent to you."
						+"<br/>We hope that you find C-Chembench to be a useful tool. <br/>If you have any problems or suggestions for improvements, please contact us at : "+Constants.WEBSITEEMAIL
						+"<br/><br/>Thank you. <br/>The C-Chembench Team<br/>"+ new Date();
						
					SendEmails.sendEmail(userInfo.getEmail(), "", "", "Congratulations, "+userInfo.getFirstName(), HtmlBody);
					
					
					forward=mapping.findForward("success");
				}catch(Exception e){ 
					forward = mapping.findForward("failure");}
				}
				
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		
		return forward;
	}		
}
