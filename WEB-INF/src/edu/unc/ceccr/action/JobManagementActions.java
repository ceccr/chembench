package edu.unc.ceccr.action;

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
import edu.unc.ceccr.utilities.Utility;


public class JobManagementActions extends ActionSupport{
	
	public String fixBrokenPredictors() throws Exception{
		//String ids = "6725 6728 6729 6730 6760 6763 6792 6803 6806 6807 6809 6816 6820 6825 6833 6844 6849 6857 6879 7331 7332 7333 7334 7335 7336 7337 7338 7339 7340 7347 7348 7431 7437 7463 7469";
		String ids="1636";
		String[] idArray = ids.split("\\s+");
		Session s = HibernateUtil.getSession();
		Utility.writeToDebug("Starting fixes..");
		for(String id: idArray){
			
			Predictor predictor = PopulateDataObjects.getPredictorById(Long.parseLong(id), s);

			Utility.writeToDebug("Fixing " + predictor.getUserName() + "'s predictor '" + predictor.getName() + "' with id " + id);
			if(predictor.getChildIds() != null && !predictor.getChildIds().trim().isEmpty()){
				
			}
			else{
				QsarModelingTask qst = new QsarModelingTask(predictor);
				qst.jobList = "LOCAL";
				qst.postProcess();
			}
		}
		return SUCCESS;
	}
}