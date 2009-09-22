package edu.unc.ceccr.action;

import java.io.File;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.formbean.PredictorFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.ErrorMessages;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class QsarPredictionAction extends Action {

	ActionMapping mapping;

	private ActionForward forward;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			User user = (User) session.getAttribute("user");
			
			try {
				PredictorFormBean formBean = (PredictorFormBean) form;
				int upload = formBean.getUploadOrSelect();
				InputStream is = null;
				String datasetName = null;
				String file=null;
				DataSet predictionDataset = PopulateDataObjects.getDataSetById(formBean.getSelectedDatasetID());
				
					datasetName = predictionDataset.getFileName();
				
				try{
					Utility.writeToMSDebug(">>>>>>>>>>>>>>>>>>>>>>>>"+predictionDataset.getFileName()+"::"+formBean.getJobName()+"::"+formBean.getPredictorName()+"::"+formBean.getSdFile());
					//String datasetName = formBean.getSdFile().getFileName();
					file = predictionDataset.getSdfFile();
					new File(Constants.CECCR_USER_BASE_PATH + user.getUserName() + "/"+ formBean.getJobName()).mkdir();
					FileAndDirOperations.copyFile(
						Constants.CECCR_USER_BASE_PATH + user.getUserName() + "/DATASETS/"+datasetName+"/"+file, 
						Constants.CECCR_USER_BASE_PATH + user.getUserName() + "/"+ formBean.getJobName() + "/"+file
						);
				}
				catch(Exception e){
					Utility.writeToMSDebug(e.getMessage());
				}
				Utility.writeToMSDebug("Files copied");
				/*QsarPredictionTask executeAntWorkflow = new QsarPredictionTask(
						user.getUserName(), formBean.getJobName(), file,
						formBean.getCutOff(), is, formBean.getUploadOrSelect(),
						formBean.getSelectedPredictorId(), predictionDataset);

				executeAntWorkflow.setUp();

				Queue.getInstance().addJob(executeAntWorkflow,
						user.getUserName(), formBean.getJobName());
					*/	
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return (forward);

	}

}
