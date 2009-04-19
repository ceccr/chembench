package edu.unc.ceccr.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.formbean.LoginFormBean;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;

public class LogoutAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); // return value

		// get session state
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			session.removeAttribute("user");
		    session.invalidate();
		}
		
		Cookie ckie=new Cookie("login","false");
		response.addCookie(ckie);
		forward = mapping.findForward("success");

		return forward;
	}
}
