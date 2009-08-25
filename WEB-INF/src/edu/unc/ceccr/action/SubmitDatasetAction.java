package edu.unc.ceccr.action;



import java.io.File;

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


import edu.unc.ceccr.formbean.DatasetFormBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.messages.ErrorMessages;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.GenerateSketchesTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;


public class SubmitDatasetAction extends Action {

	ActionForward forward;

	ActionMapping mapping;

	//private Queue tasklist;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		
		forward = mapping.findForward("success");
		
		ActionErrors errors = new ActionErrors();
		
		DatasetFormBean formBean = (DatasetFormBean) form;
		
			
	//	FormFile sdFile = formBean.getSdFile();
	//	Utility.writeToMSDebug("ActionForward execute:sd:"+sdFile.getFileName());
	//	FormFile actFile = formBean.getActFile();
		
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {
			try {
				
				FormFile sdFile = null;
				FormFile actFile = null;
				String knnType = null;
				
				if(formBean.getUpload().equals("MODELING")){
					sdFile = formBean.getSdFileModeling();
					actFile = formBean.getActFile();
					knnType = formBean.getKnnType(); 
				}
				else{
					sdFile = formBean.getSdFilePrediction();
					knnType = Constants.PREDICTION;
				}
				String datasetName = formBean.getDatasetname();
				Utility.writeToMSDebug("ActionForward execute:dsn:"+datasetName);
				Utility.writeToMSDebug("ActionForward execute:sdf:"+sdFile.getFileName());
				Utility.writeToMSDebug("ActionForward execute:act:"+actFile);
				Utility.writeToMSDebug("ActionForward execute:knn:"+knnType);
				//Utility.writeToDebug("Creating " + knnType + " dataset \"" + datasetName + "\" with files: " + sdFile.getFileName() + " " + actFile.getFileName());
				
				User user = (User) session.getAttribute("user");
				String userName = user.getUserName();
				
				session.setAttribute("KnnType", knnType);
				session.setAttribute("datasetName", datasetName);
				
				//saving files to username/DATASETS/datasetName/ folder
				String msg = DatasetFileOperations.uploadDataset(userName, sdFile, actFile, datasetName, formBean.getDataSetDescription(), knnType);
    			
				if(msg!=""){
					// If the file system already contains a dataset there is no need to delete it
					if(msg!=ErrorMessages.FILESYSTEM_CONTAINS_DATASET){
						FileAndDirOperations.deleteDir(new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+datasetName));
					}
					Utility.writeToMSDebug("Error::"+msg);
					request.removeAttribute("validationMsg");
					request.setAttribute("validationMsg", msg);
					return forward = mapping.findForward("failure");
				}
				
				GenerateSketchesTask sketchTask = new GenerateSketchesTask(userName, datasetName, Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+datasetName+"/",sdFile.getFileName(),"Visualization/Structures/", "Visualization/Sketches/");
				 try {
					 	sketchTask.setUp();
						Queue.getInstance().addJob(sketchTask, userName, datasetName);
					} catch (Exception e) {
						Utility.writeToMSDebug(e.getMessage());
						msg = "Sketch generation caused an error: "+ e.getMessage();
					}
				
				
			} catch (Exception e) {
				
				errors.add("RUNTIME", new ActionMessage("error.RUNTIME"));
				addErrors(request, errors);
				Utility.writeToDebug(e);
				// Deleting the dataset folder to give a user a chance to upload it again
				FileAndDirOperations.deleteDir(new File(Constants.CECCR_USER_BASE_PATH+((User) session.getAttribute("user")).getUserName()+"/DATASETS/"+formBean.getDatasetname()));
				forward = mapping.findForward("failure");
				Utility.writeToMSDebug(e.getMessage());
			}

		}

		return (forward);

	}
		
}
