package edu.unc.ceccr.action.RegisterActions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class CheckNewUserAction extends Action {

	ActionMapping mapping;

	private ActionForward forward;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
		
	HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			try {
				
				User user=null;
				Session s = HibernateUtil.getSession();
				Integer number=0;
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					user= (User) s.createCriteria(User.class).add(
							Expression.eq("status", "NOTSET")).setMaxResults(1).uniqueResult();
					 number=(Integer)s.createCriteria(User.class)
					        .setProjection(Projections.rowCount()).add(Expression.eq("status","NOTSET"))
					        .uniqueResult();
					
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e);
				} finally {
					s.close(); 
				}
				if(user!=null)
				{
					session.removeAttribute("newUserInfo");
					session.setAttribute("newUserInfo",user);
					session.removeAttribute("number");
					session.setAttribute("number",number);
				}
				else{
					session.removeAttribute("number");
					session.setAttribute("number",0);
				}
				
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return (forward);

	}

}
