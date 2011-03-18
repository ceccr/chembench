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
import edu.unc.ceccr.persistence.*;
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
		/*
		//Job
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(Job.class, session);
			printObjectsAsCsv(list, basePath + "cbench_job.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//User
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(User.class, session);
			printObjectsAsCsv(list, basePath + "cbench_user.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//JobStats
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(JobStats.class, session);
			printObjectsAsCsv(list, basePath + "cbench_jobStats.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//SoftwareLink
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(SoftwareLink.class, session);
			printObjectsAsCsv(list, basePath + "cbench_softwareLink.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//AdminSettings
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(AdminSettings.class, session);
			printObjectsAsCsv(list, basePath + "cbench_adminSettings.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//ExternalValidation
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(ExternalValidation.class, session);
			printObjectsAsCsv(list, basePath + "cbench_externalValidation.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		*/
		/*
		//DataSet
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(DataSet.class, session);
			printObjectsAsCsv(list, basePath + "cbench_dataset.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		*/
		/*
		//KnnModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnModel.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnPlusModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnPlusModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnPlusModel.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnParameters.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnPlusParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnPlusParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnPlusParameters.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		*/
		/*
		//Prediction
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(Prediction.class, session);
			printObjectsAsCsv(list, basePath + "cbench_prediction.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		*/
		//PredictionValue
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(PredictionValue.class, session);
			printObjectsAsCsv(list, basePath + "cbench_predictionValue.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//SvmParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(SvmParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_svmParameters.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//RandomForestParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(RandomForestParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestParameters.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//Predictor
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(Predictor.class, session);
			printObjectsAsCsv(list, basePath + "cbench_predictor.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		

		//RandomForestGrove
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(RandomForestGrove.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestGrove.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//RandomForestTree
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(RandomForestTree.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestTree.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//SvmModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(SvmModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_svmModel.csv");
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
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
		
		return SUCCESS;
	}
}