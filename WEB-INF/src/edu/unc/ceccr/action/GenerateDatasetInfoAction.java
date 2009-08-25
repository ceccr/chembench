package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.distance.DistanceMeasure;
import edu.unc.ceccr.distance.MahalanobisDistanceMeasure;
import edu.unc.ceccr.distance.TanimotoDistanceMeasure;
import edu.unc.ceccr.formbean.DatasetFilesBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.GenerateDatasetInfoActionTask;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CSV_X_Workflow;

/**
 * Class which should call all methods for generation of the additional files 
 * for dataset visualization.
 * @author msypa
 * @date 12/05/08
 */
public class GenerateDatasetInfoAction extends Action {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = mapping.findForward("success");	
		HttpSession session = request.getSession();
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} 
		else{
			User user = (User) session.getAttribute("user");
			Utility.writeToMSDebug("sessionUser:::"+user.getUserName());
			DatasetFilesBean formBean = (DatasetFilesBean) form;
			String datasetName = formBean.getDatasetName();
			Utility.writeToMSDebug("sessionDS:::"+datasetName);
			
			
			
			GenerateDatasetInfoActionTask task = new GenerateDatasetInfoActionTask(datasetName,
					formBean.getRepresent(), formBean.getSimilarity_measure(), /*formBean.getSketches(),*/
					user.getUserName(), formBean.getSdfName(),formBean.getActName());
				
			try {
				task.setUp();
				Queue.getInstance().addJob(task, user.getUserName(), datasetName);
			} catch (Exception e) {
				request.setAttribute("message", e.getMessage());
				forward = mapping.findForward("failure"); 
				}
		
		}
		
		
		return forward;
		
	}

}
