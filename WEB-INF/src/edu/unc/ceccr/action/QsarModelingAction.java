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
			session.setAttribute("KnnType", formBean.getKnnType());
			try
			{				
				User user = (User) session.getAttribute("user");
				
				QsarModelingTask executeAntWorkflow = null;
				
				boolean isUserFile=Utility.checkIfUserZfile(user.getUserName(), formBean.getFile(),formBean.getKnnType());

				Utility.writeToDebug("Setting up task", user.getUserName(), formBean.getJobName());
				
				//owww... huge function call. My head hurts.
				
				executeAntWorkflow = new QsarModelingTask(
						user.getUserName(), 
						formBean.getJobName(), 
						formBean.getNumCompoundsExternalSet(), 
						formBean.getKnnType(), 
						formBean.getDescriptorGenerationType(), 
						formBean.getMaxNumDescriptors(), 
						formBean.getMinNumDescriptors(), 
						formBean.getStepSize(), 
						formBean.getNumCycles(),
						formBean.getNumMutations(), 
						formBean.getMinAccTest(), 
						formBean.getMinAccTraining(), 
						formBean.getCutoff(),
						formBean.getMu(), 
						formBean.getNumRuns(), 
						formBean.getNearest_Neighbors(), 
						formBean.getPseudo_Neighbors(), 
						formBean.getT1(),
						formBean.getT2(), 
						formBean.getTcOverTb(), 
						formBean.getMinSlopes(), 
						formBean.getMaxSlopes(),
						formBean.getRelativeDiffRR0(), 
						formBean.getDiffR01R02(), 
						formBean.getStop_cond(),
						formBean.getKnnCategoryOptimization(), 
						formBean.getNumSphereRadii(), 
						formBean.getSelectionNextTrainPt(), 
						formBean.getNumStartingPoints(),
						formBean.getSelectedDatasetId());
				
				Utility.writeToMSDebug("QSARMODELLING DATASET ID::"+formBean.getSelectedDatasetId());
			
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
