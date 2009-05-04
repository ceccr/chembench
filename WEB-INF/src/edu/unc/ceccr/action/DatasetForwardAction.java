package edu.unc.ceccr.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class DatasetForwardAction extends Action {

	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping actionMapping, ActionForm arg1,HttpServletRequest request, HttpServletResponse arg3)
			throws Exception {
		
		ActionForward forward = actionMapping.findForward("success");

		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = actionMapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = actionMapping.findForward("login");
		} else {
			
			String  userName=((User)session.getAttribute("user")).getUserName();
			
			session.removeAttribute("userDatasets");
			
			List<DataSet> datasets = PopulateDataObjects.populateDataset(userName, Constants.CONTINUOUS,false);
			datasets.addAll(PopulateDataObjects.populateDataset(userName, Constants.CATEGORY,false));
			session.setAttribute("userDatasets", datasets);
			
			session.removeAttribute("publicDatasets");
			
			List<DataSet> publicdatasets = PopulateDataObjects.populateDataset(Constants.ALL_USERS_USERNAME, Constants.CONTINUOUS,false);
			publicdatasets.addAll(PopulateDataObjects.populateDataset(Constants.ALL_USERS_USERNAME, Constants.CATEGORY,false));
			session.setAttribute("publicDatasets", publicdatasets);
			
			session.removeAttribute("userPredictionFiles");
			session.removeAttribute("datasetNames");
			session.removeAttribute("predictorNames");
			session.removeAttribute("predictionNames");
			session.removeAttribute("taskNames");
			
			Utility.writeToMSDebug("Prediction.length:::"+PopulateDataObjects.populateDataset(userName, Constants.PREDICTION, false));
			session.setAttribute("userPredictionFiles", PopulateDataObjects.populateDataset(userName, Constants.PREDICTION, false));
			session.setAttribute("datasetNames", PopulateDataObjects.populateDatasetNames(userName, true));
			session.setAttribute("predictorNames", PopulateDataObjects.populatePredictorNames(userName, true));
			session.setAttribute("predictionNames", PopulateDataObjects.populatePredictionNames(userName, true));
			session.setAttribute("taskNames", PopulateDataObjects.populateTaskNames(userName, false));
			
		}
	

		return forward;

	}
	
 
	
}
