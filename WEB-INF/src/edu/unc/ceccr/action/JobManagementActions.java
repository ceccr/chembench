package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.taskObjects.WorkflowTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;


public class JobManagementActions extends ActionSupport{
	
	public String fixBrokenPredictors() throws Exception{
		//String ids = "6725 6728 6729 6730 6760 6763 6792 6803 6806 6807 6809 6816 6820 6825 6833 6844 6849 6857 6879 7331 7332 7333 7334 7335 7336 7337 7338 7339 7340 7347 7348 7431 7437 7463 7469";
		String ids="1635 1642";
		String[] idArray = ids.split("\\s+");
		Session s = HibernateUtil.getSession();
		Utility.writeToDebug("Starting fixes..");
		for(String id: idArray){
			
			Predictor predictor = PopulateDataObjects.getPredictorById(Long.parseLong(id), s);

			Utility.writeToDebug("Fixing " + predictor.getUserName() + "'s predictor '" + predictor.getName() + "' with id " + id);
			
			if(predictor.getChildIds() != null && ! predictor.getChildIds().trim().isEmpty()){
				String[] childIds = predictor.getChildIds().split("\\s+");
				
				ArrayList<Predictor> childPredictors = new ArrayList<Predictor>();
				
				for(String childId: childIds){
					Predictor childPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(childId), s);
					childPredictors.add(childPredictor);
				}
				for(Predictor childPredictor: childPredictors){
					Utility.writeToDebug("Fixing " + childPredictor.getUserName() + "'s child predictor '" + childPredictor.getName() + "' with id " + id);
					UndoMoveToPredictorsDir(predictor.getUserName(), childPredictor.getName(), predictor.getName());
					
					QsarModelingTask qst = new QsarModelingTask(childPredictor);
					qst.jobList = "LSF";
					qst.postProcess();
				}
			}
			else{			
				UndoMoveToPredictorsDir(predictor.getUserName(), predictor.getName(), "");
				QsarModelingTask qst = new QsarModelingTask(predictor);
				qst.jobList = "LSF";
				qst.postProcess();
			}
		}
		return SUCCESS;
	}

	public static void UndoMoveToPredictorsDir(String userName, String jobName, String parentPredictorName) throws Exception{
		//do the opposite of:
		//When the job is finished, move all the files over to the PREDICTORS dir.
		String moveFrom;
		if(parentPredictorName.isEmpty()){
			moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + jobName;
		}
		else{
			moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + parentPredictorName + "/" + jobName;
		}
		
		String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName;
		String execstr = "mv " + moveFrom + " " + moveTo;
		RunExternalProgram.runCommand(execstr, "");  
	}
}