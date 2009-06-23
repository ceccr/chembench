package edu.unc.ceccr.action.RegisterActions;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;

public class DenyAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

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
				
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					s.delete(userInfo);
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e);				
					forward = mapping.findForward("failure");
				} finally {
					s.close(); }
				
				String HtmlBody="Sorry,"+userInfo.getFirstName()+",<br/>"+"You are not qualified to be a member of CECCR......."
				+"<br/>For the reasons list below: "
				+"<br/> " 
				+"<br/> "
				+"<br/> If you have any question, please contact us at <u><a href='mailto:"+Constants.WEBSITEEMAIL+"'>"+Constants.WEBSITEEMAIL+"</a></u>"
				+"<br/><br/> Thank you,"
				+"<br/><br/><br/>"
				+"Administrator <br/>"+ new Date();
				SendEmails.sendEmail(userInfo.getEmail(), "", "", "Sorry,"+userInfo.getFirstName(), HtmlBody);
				
				forward=mapping.findForward("success");
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		
		return forward;
	}
	
		
}
