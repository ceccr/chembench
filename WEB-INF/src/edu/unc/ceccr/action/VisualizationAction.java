package edu.unc.ceccr.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import edu.unc.ceccr.formbean.PredictorFormBean;
import edu.unc.ceccr.formbean.QsarFormBean;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class VisualizationAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward();
		
		try{

		HttpSession session = request.getSession(false);

		ActionErrors errors = new ActionErrors();

		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {
			String userName = ((User) session.getAttribute("user"))
					.getUserName();
			
			Long datasetID = Long.parseLong(request.getParameter("datasetID"));
			
			DataSet selectedDataSet = PopulateDataObjects.getDataSetById(datasetID);
			
			String fullPath = Constants.CECCR_USER_BASE_PATH;
			
			String userDir;
			if(selectedDataSet.getUserName().equalsIgnoreCase("_all")){
				userDir = "all-users";
			}
			else{
				userDir = selectedDataSet.getUserName();
			}
			fullPath += userDir + "/DATASETS/" + selectedDataSet.getFileName() + "/" + selectedDataSet.getActFile();
			
			Utility.writeToDebug("Generating Activity Histogram for Dataset: " + datasetID + " from ACT file: " + fullPath);
			
			HashMap hashmap = new HashMap();

			hashmap = createDataset(fullPath);

			session.removeAttribute("ACTDataSet");

			session.setAttribute("ACTDataSet", hashmap);

			forward = mapping.findForward("success");
		}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
			forward = mapping.findForward("failure");
			return forward;
		}
		return forward;
	}

	public HashMap createDataset(String filePath) {

		HashMap hm = new HashMap();
		try {
			hm = DatasetFileOperations.parseActFile(filePath);

		} catch (IOException e) {
			Utility.writeToDebug(e);
		}
		//set hashmap into session variables
		//it will be read by the chart servlet
		return hm;
	}

}
