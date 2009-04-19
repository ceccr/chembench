package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.formbean.LoginFormBean;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;

public class TaskListChangeAction extends Action {

	Queue tasklist = Queue.getInstance();

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); // return value

		// get session state
		HttpSession session = request.getSession(false); // edited to generalize

		User user = (User) session.getAttribute("user");
		if(user==null)
			return mapping.findForward("login");

		boolean bool=tasklist.testFlag(user.getUserName());
		session.setAttribute("change", bool);
		forward = mapping.findForward("success");
		return forward;
	}
}
