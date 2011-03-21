package edu.unc.ceccr.action.RegisterActions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.sql.SQLException;

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
import org.hibernate.criterion.Expression;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.persistence.HibernateUtil;

public class RetrievePasswordAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); 
		HttpSession session = request.getSession(false);
		String email=(String)request.getParameter("email");
		String userName=(String)request.getParameter("userName");
		String MSG="";
		session.removeAttribute("MSG");
		if(email==null)
		{
			MSG="Please input your registered email!"+"<br/><br/><br/><a href='getPassword.do'><font size=4 color='red'><b>BACK</b></font></a>";
			session.setAttribute("MSG",MSG);
			forward=mapping.findForward("failure");
			return forward;
		}
		        User user=new User();
				Session s = HibernateUtil.getSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					user=(User)s.createCriteria(User.class).add(Expression.eq("userName",userName)).uniqueResult();
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();e.printStackTrace(); MSG="Error !";
					session.setAttribute("MSG",MSG);
					forward = mapping.findForward("failure");
				} finally {s.close(); }
				
				
				if(user==null){
					MSG="The email: <font color=red>"+email+"</font> is not found!"+"<br/><br/><br/><a href='getPassword.do'><font size=4 color='red'><b>BACK</b></font></a>";
					session.setAttribute("MSG",MSG);
					forward=mapping.findForward("failure");
				}
				else{ 
					String newpassword = updateDB(user);
					
					

					s = HibernateUtil.getSession();
					tx = null;
					List<User> userList = null;
					try {
						tx = s.beginTransaction();
						userList=s.createCriteria(User.class).add(Expression.eq("status","agree")).list();
						tx.commit();
					} catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);				
						forward = mapping.findForward("failure");
					} finally {	s.close(); }
					Iterator it=userList.iterator();
					while(it.hasNext())
					{
						updateDB((User)it.next());
					}
					
					
					
					
					//For debugging, you may want to write the newly generated password
					//to the page. (Never do this in a production build, of course...)
				    
					MSG = "<font size=2 face=arial>Your password has been reset. <br />" +
					"An email containing the password has been sent to </font><font size=2 face=arial color=red>" + user.getEmail()
					+"</font><font size=2 face=arial>.<br />When the email arrives, you'll want to <a href='/home.do'>return to Home page</a> and log in. <br />"
					+"You may change your password from the 'edit profile' page when you are logged in.</font>";
					
					session.setAttribute("MSG",MSG);
					forward=mapping.findForward("success");
				}
				
		return forward;
	}
	
	public String updateDB(User user)throws SQLException, ClassNotFoundException,Exception
	{
		String randomPassword=Utility.randomPassword();
		user.setPassword(Utility.encrypt(randomPassword));
	
		Session s = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(user);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace(); 
		} finally {s.close();}
		
		String HtmlBody=user.getFirstName()+", your Chembench password has been reset."+"<br/>"+"Your username: "+user.getUserName()
		+"<br/> Your new password is: "+randomPassword+"<br/><br/><br/>"
		+"You may login from "+Constants.WEBADDRESS+".<br/> <br/><br/>"
		+"Once you are logged in, you may change your password from the 'edit profile' page.";
		
		SendEmails.sendEmail(user.getEmail(), "", "", "Chembench Password Reset", HtmlBody);
		return randomPassword;
	}
		
}
