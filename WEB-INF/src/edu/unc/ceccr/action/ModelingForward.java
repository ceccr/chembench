package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;

public class ModelingForward extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); 
		
		HttpSession session = request.getSession(false); 
		
		if (session == null ){
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}
		else
		{
			User user = (User) session.getAttribute("user");
			
			session.removeAttribute("continuousDatasets");
			session.removeAttribute("categoryDatasets");
			session.removeAttribute("datasetNames");
			session.removeAttribute("predictorNames");
			session.removeAttribute("predictionNames");
			session.removeAttribute("taskNames");
			session.removeAttribute("predictors");
			
			session.setAttribute("predictors", PopulateDataObjects.populatePredictors(user.getUserName(), true));
			session.setAttribute("continuousDatasets", PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS,true));
			session.setAttribute("categoryDatasets", PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY,true));
			session.setAttribute("datasetNames", PopulateDataObjects.populateDatasetNames(user.getUserName(), true));
			session.setAttribute("predictorNames", PopulateDataObjects.populatePredictorNames(user.getUserName(), true));
			session.setAttribute("predictionNames", PopulateDataObjects.populatePredictionNames(user.getUserName(), true));
			session.setAttribute("taskNames", PopulateDataObjects.populateTaskNames(user.getUserName()));
			
			
			forward = mapping.findForward("success");
		}
		
		return forward;
	}
}
