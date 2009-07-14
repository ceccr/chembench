package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.Utility;

public class QsarModelingAction extends Action {

	ActionForward forward;

	ActionMapping mapping;

	private Queue tasklist;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
		
		ActionErrors errors=new ActionErrors();
		
		QsarFormBean formBean = (QsarFormBean) form;
		
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}
		else
		{
			session.setAttribute("KnnType", formBean.getDatasetType());
			try
			{				
				User user = (User) session.getAttribute("user");
				
				QsarModelingTask executeAntWorkflow = null;
				
				Utility.writeToDebug("Setting up task", user.getUserName(), formBean.getJobName());
				
				executeAntWorkflow = new QsarModelingTask(user.getUserName(), formBean);

				executeAntWorkflow.setUp();

				tasklist = Queue.getInstance();

				tasklist.addJob(executeAntWorkflow, user.getUserName(), formBean.getJobName());
				Utility.writeToDebug("Task added to queue", user.getUserName(), formBean.getJobName());

			} catch (Exception ex) {
				
				errors.add("RUNTIME", new ActionMessage("error.RUNTIME"));
				addErrors(request,errors);
				
				forward = mapping.findForward("failure");
				Utility.writeToDebug(ex);
			}
		}
		return (forward);
	}
	
}
