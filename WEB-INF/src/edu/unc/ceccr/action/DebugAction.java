package edu.unc.ceccr.action;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.ClassUtils;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;


public class DebugAction extends ActionSupport{
	
	public static void printObjectsAsCsv(ArrayList<Object> objects, String path) throws Exception{
		BufferedWriter out = new BufferedWriter(new FileWriter(path));
		
		boolean headerDone = false;
		for(Object o: objects){
			if(!headerDone){
				out.write(ClassUtils.varNamesToString(o) + "\n");
				headerDone = true;
			}
			out.write(ClassUtils.varValuesToString(o) + "\n");
		}
		out.close();
	}
	
	public static String printDatabaseTables(){
		//prints every database table it can get out to individual files

		String basePath = Constants.CECCR_BASE_PATH + "theo/";
		
		//Job
		//ExternalValidation
		try{
			Session session = HibernateUtil.getSession();
			ArrayList jobs = PopulateDataObjects.populateJobs(session);
			printObjectsAsCsv(jobs, basePath + "cbench_job.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		

		//User
		//JobStats
		//AdminSettings
		//DataSet
		//KnnModel
		//KnnPlusModel
		//KnnParameters
		//KnnPlusParameters
		//ModelPredictionValue
		//Prediction
		//PredictionValue
		//Predictor
		//RandomForestGrove
		//RandomForestParameters
		//RandomForestTree
		//SoftwareExpiration
		//SoftwareLink
		//SvmModel
		//SvmParameters
		

		
		
		/*
		Utility.writeToDebug("userName, password, email, bench, status, firstname, lastname, orgtype, orgname, " +
				"orgposition, zipcode, state, country, address, city, showPublicDatasets, showPublicPredictors, " +
				"viewDatasetCompoundsPerPage, viewPredictionCompoundsPerPage, showAdvancedKnnModeling, isAdmin, canDownloadDescriptors");
		ArrayList<User> users = PopulateDataObjects.getAllUsers(s);
		for(User u: users){
			String str = 
				u.getUserName() + ", " +
				u.getPassword() + ", " +
				u.getEmail() + ", " +
				u.getWorkbench() + ", " +
				u.getStatus() + ", " +
				
				//professional information
				u.getFirstName() + ", " +
				u.getLastName() + ", " +
				u.getOrgType() + ", " +
				u.getOrgName() + ", " +
				u.getOrgPosition() + ", " +
				
				//mostly just for stalking
				u.getZipCode() + ", " +
				u.getState() + ", " +
				u.getPhone() + ", " +
				u.getCountry() + ", " +
				u.getAddress() + ", " +
				u.getCity() + ", " +
				
				//user options (may eventually become a new table of its own)
				u.getShowPublicDatasets() + ", " +
				u.getShowPublicPredictors() + ", " +
				u.getViewDatasetCompoundsPerPage() + ", " +
				u.getViewPredictionCompoundsPerPage() + ", " +
				u.getShowAdvancedKnnModeling() + ", " +
				
				//user privileges 
				u.getIsAdmin() + ", " +
				u.getCanDownloadDescriptors();
				Utility.writeToDebug(str);
		}
		FileAndDirOperations.writeStringToFile(text, Constants.CECCR_BASE_PATH);
		*/
		 */
		
		return SUCCESS;
	}
}