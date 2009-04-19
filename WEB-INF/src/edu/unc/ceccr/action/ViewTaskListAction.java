package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class ViewTaskListAction extends Action {

	Queue tasklist = Queue.getInstance();

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); // return value

		// get session state
		HttpSession session = request.getSession(false); // edited to
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			User user = (User) session.getAttribute("user");
			if (user == null)
				return mapping.findForward("login");

			//Allow the database a little time to finish any commits it was doing.
			//Without this sleep, the jobs list sometimes doesn't reflect recent changes.
			Thread.sleep(1500);
			
			session.setAttribute("myTasks", tasklist.getUserTasks(user.getUserName()));
			
			/*
			for (Queue.Task t: tasklist.getUserTasks(user.getUserName())){
				Utility.writeToDebug("Task: " + t.getJobName() + " is in state: " + t.getState(), user.getUserName(), t.getJobName());
			}*/
			
			tasklist.setFlag(user.getUserName());
			session.removeAttribute("tasks");
			session.setAttribute("tasks", tasklist.totalTasksInQ());
			forward = mapping.findForward("success");
		}
		return forward;
	}
}
