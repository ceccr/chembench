package edu.unc.ceccr.action.RegisterActions;

import java.util.Date;
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
					updateDB(user);
				    MSG="Your password has been successfully sent to:<br/><br/> "+user.getEmail()
					+"<br/><br/><br/><a href='login.do'><font face='GoudyOlSt BT' size='3' color='red'>Click here to login</font></a>";
					session.setAttribute("MSG",MSG);
					forward=mapping.findForward("success");
				}
				
		return forward;
	}
	
	public void updateDB(User user)throws SQLException, ClassNotFoundException,Exception
	{
		Utility utility=new Utility();
		String randomPassword=utility.randomPassword();
		user.setPassword(utility.encrypt(randomPassword));
		
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
			
			String HtmlBody="Hi,"+user.getFirstName()+",<br/>"+"Your user Name: "+user.getUserName()
			+"<br/> Your password: "+randomPassword+"<br/><br/><br/>"
			+"You may login from "+Constants.WEBADDRESS+".<br/> <br/><br/>"
			+"Administrator <br/>"+ new Date();
			
			SendEmails.sendEmail(user.getEmail(), "", "", "Your Chembench password", HtmlBody);
	}
	
	
		
}
