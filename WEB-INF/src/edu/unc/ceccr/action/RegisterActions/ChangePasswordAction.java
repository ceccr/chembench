package edu.unc.ceccr.action.RegisterActions;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.criterion.*;
import org.hibernate.Transaction;

import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.persistence.HibernateUtil;

public class ChangePasswordAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); 
		HttpSession session=request.getSession(false);
				
		String userName=(String)request.getParameter("userName");
		String oldPassword=(String)request.getParameter("oldPs");
		String newPassword=(String)request.getParameter("newPs");
		String MSG="";
		session.removeAttribute("MSG");
		User user=null;
             
			try {
				Session s = HibernateUtil.getSession();// query
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					user=(User)s.createCriteria(User.class)
					         .add(Expression.eq("userName", userName)).uniqueResult();
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e);	forward = mapping.findForward("failure");
				} finally {
					s.close(); }
				
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
			
			
			if(user!=null)
			   {
				if(SaveNewPassword(user, newPassword,oldPassword))
					{forward=mapping.findForward("success"); 
					 MSG="You have successfully changed your password.<br/> <br/><a href='logout.do'><font face='GoudyOlSt BT' size='3' color='red'>Click here to login</font></a>";
					 session.setAttribute("MSG",MSG);
					}
				     else{forward=mapping.findForward("failure");
				     MSG="There is an <font color='red'>ERROR</font> while changing your password! "+
				     "<br/><br/> Please try again!"
						+"<br/><br/><br/><a href='forUser.do'><font size=4 color='red'><b>BACK</b></font></a>";
				     session.setAttribute("MSG",MSG);
				     }
			   }else
			   {
				forward=mapping.findForward("NoUser");
				MSG="Can not find the user with<br/> User Name:  <font color='red'>"+userName+"</font><br/> Password:  <font color='red'>"+oldPassword+"</font><br/><br/> Please try again!"
				+"<br/><br/><br/><a href='forUser.do'><font size=4 color='red'><b>BACK</b></font></a>";
				session.setAttribute("MSG",MSG);
			   }
		
		return forward;
	}
	
	public boolean SaveNewPassword(User user,String newPassword ,String oldPassword)throws HibernateException,ClassNotFoundException,SQLException,
	NoSuchAlgorithmException
	{

		Utility utility=new Utility();
		if(utility.compareEncryption(utility.encrypt(oldPassword), user.getPassword()))
		{}else{return false;}
			Session s = HibernateUtil.getSession();
			user.setPassword(utility.encrypt(newPassword));
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				s.saveOrUpdate(user);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);	
				return false;		
			} finally {
				s.close();}
			
		return true;
	}
}
		

