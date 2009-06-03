package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.SmilesPredictionWorkflow;

public class SmilesPredictAction extends Action {

	ActionForward forward;

	ActionMapping mapping;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		
		forward = mapping.findForward("success");

		HttpSession session = request.getSession(false);

		
		try {
			User user = (User) session.getAttribute("user");
			String userName = user.getUserName();
			Predictor predictor = (Predictor) session.getAttribute("selectedPredictor");
			String smiles = request.getParameter("smiles");
			String cutoff = request.getParameter("cutoff");
			Utility.writeToDebug("Called SMILES predict action. Predictor ID: " + predictor+" SMILES: "+smiles);
			Utility.writeToMSDebug("user::"+userName+" predictor::"+predictor+" smiles::"+smiles);
			
			String smilesDir = Constants.CECCR_USER_BASE_PATH + userName + "/SMILES/";
			
			//make sure there's nothing in the dir already.
			FileAndDirOperations.deleteDirContents(smilesDir);
			
			//generate an SDF from this SMILES string
			SmilesPredictionWorkflow.smilesToSDF(smiles, smilesDir);
			
			//create descriptors for the SDF, normalize them, and make a prediction
			String[] predValues = SmilesPredictionWorkflow.PredictSmilesSDF(smilesDir, userName, predictor, Float.parseFloat(cutoff));
			
			session.removeAttribute("SmilesPredictPredictor");
			session.removeAttribute("SmilesPredictSmiles");
			session.removeAttribute("SmilesCutoff");
			session.removeAttribute("SmilesUsedModels");
			session.removeAttribute("SmilesTotalModels");
			session.removeAttribute("SmilesStdDev");
			session.removeAttribute("SmilesPredictedValue");
			
			session.setAttribute("SmilesPredictPredictor", predictor.getName());
			session.setAttribute("SmilesPredictSmiles", smiles);
			session.setAttribute("SmilesCutoff", cutoff);
			
			if(predValues[2].equalsIgnoreCase("no")){
				//no prediction.
				session.setAttribute("SmilesUsedModels", "0");
				session.setAttribute("SmilesPredictedValue", "Molecule is outside the domain of this predictor. Use a higher cutoff value to force a low-confidence prediction.");
				session.setAttribute("SmilesStdDev", "N/A");
				session.setAttribute("SmilesTotalModels", predictor.getNumTotalModels());
			}
			else{
				session.setAttribute("SmilesUsedModels", predValues[1]);
				session.setAttribute("SmilesPredictedValue", predValues[2]);
				if(predValues.length >= 3){
					//Standard deviation will only be calculated if there's more than one pred value
					session.setAttribute("SmilesStdDev", predValues[3]);
				}
				session.setAttribute("SmilesTotalModels", predictor.getNumTotalModels());
			}
			
		} catch (Exception e) {
			Utility.writeToMSDebug("Error::"+e.getMessage());
			forward = mapping.findForward("failure");
			Utility.writeToDebug(e);
		}
		return forward;

	}
}
