package edu.unc.ceccr.action.RegisterActions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class ViewUsersAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); // return value

		// get session state
		HttpSession session = request.getSession(false); // edited to generalize
		if (session == null ){
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}
		else{
			List users = null;
			org.hibernate.Session s= HibernateUtil.getSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				users = s.createCriteria(User.class).list();
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
				forward = mapping.findForward("failure");
				
			} finally {	s.close();	}
			
			session.removeAttribute("viewUsers");
			session.setAttribute("viewUsers",users);
			session.removeAttribute("totalUser");
			session.setAttribute("totalUser",users.size());
			forward = mapping.findForward("success");
			}
		return forward;
	}
}
