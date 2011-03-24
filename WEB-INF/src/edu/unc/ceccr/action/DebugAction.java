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

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
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

import edu.unc.ceccr.calculations.ConfusionMatrix;
import edu.unc.ceccr.calculations.RSquaredAndCCR;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.*;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.ClassUtils;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateExtValidationChartWorkflow;


public class DebugAction extends ActionSupport{

	private static void savePredictor(Predictor p, Session s){
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(p);
			tx.commit();
		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e); 
		}
	}
	
	public static String addExternalAccuracies() throws Exception{ 
			Session session = HibernateUtil.getSession();
			List<Predictor> predictors = PopulateDataObjects.populatePredictors("ALLOFTHEM", false, true, session);
		
			for(Predictor selectedPredictor : predictors){
				Utility.writeToStrutsDebug("Adding ext set prediction summary to predictor " + selectedPredictor.getId());
				try{
				ConfusionMatrix confusionMatrix;
				String rSquared = "";
				String rSquaredAverageAndStddev = "";
				String ccrAverageAndStddev = "";
				ArrayList<ExternalValidation> externalValValues = null;
				ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(selectedPredictor, session);
				
				//get external validation compounds of predictor
				if(childPredictors.size() != 0){

					//get external set for each
					externalValValues = new ArrayList<ExternalValidation>();
					SummaryStatistics childAccuracies = new SummaryStatistics(); //contains the ccr or r^2 of each child
					
					for(int i = 0; i < childPredictors.size(); i++){
						Predictor cp = childPredictors.get(i);
						ArrayList<ExternalValidation> childExtVals = (ArrayList<ExternalValidation>) PopulateDataObjects.getExternalValidationValues(cp.getId(), session);
						
						//calculate r^2 / ccr for this child
						if(childExtVals != null){
							if(selectedPredictor.getActivityType().equals(Constants.CATEGORY)){
								Double childCcr = (RSquaredAndCCR.calculateConfusionMatrix(childExtVals)).getCcr();
								childAccuracies.addValue(childCcr);
							}
							else if(selectedPredictor.getActivityType().equals(Constants.CONTINUOUS)){
								ArrayList<Double> childResiduals = RSquaredAndCCR.calculateResiduals(childExtVals);
								Double childRSquared = RSquaredAndCCR.calculateRSquared(childExtVals, childResiduals);
								childAccuracies.addValue(childRSquared);
								CreateExtValidationChartWorkflow.createChart(selectedPredictor, ""+(i+1));
							}
							externalValValues.addAll(childExtVals);
						}
					}

					Double mean = childAccuracies.getMean();
					Double stddev = childAccuracies.getStandardDeviation();
					
					if(selectedPredictor.getActivityType().equals(Constants.CONTINUOUS)){
						rSquaredAverageAndStddev = Utility.roundSignificantFigures(""+mean, Constants.REPORTED_SIGNIFICANT_FIGURES);
						rSquaredAverageAndStddev += " ± ";
						rSquaredAverageAndStddev += Utility.roundSignificantFigures(""+stddev, Constants.REPORTED_SIGNIFICANT_FIGURES);
						Utility.writeToDebug("rsquared avg and stddev: " + rSquaredAverageAndStddev);
						selectedPredictor.setExternalPredictionAccuracyAvg(rSquaredAverageAndStddev);
						//make main ext validation chart
						CreateExtValidationChartWorkflow.createChart(selectedPredictor, "0");
					}
					else if(selectedPredictor.getActivityType().equals(Constants.CATEGORY)){
						ccrAverageAndStddev = Utility.roundSignificantFigures(""+mean, Constants.REPORTED_SIGNIFICANT_FIGURES);
						ccrAverageAndStddev += " ± ";
						ccrAverageAndStddev += Utility.roundSignificantFigures(""+stddev, Constants.REPORTED_SIGNIFICANT_FIGURES);
						Utility.writeToDebug("ccr avg and stddev: " + ccrAverageAndStddev);
						selectedPredictor.setExternalPredictionAccuracyAvg(ccrAverageAndStddev);
					}
				}
				else{
					externalValValues= (ArrayList<ExternalValidation>) PopulateDataObjects.getExternalValidationValues(selectedPredictor.getId(), session);
				}
				
				if(externalValValues == null || externalValValues.isEmpty()){
					Utility.writeToDebug("ext validation set empty!");
					externalValValues = new ArrayList<ExternalValidation>();
					continue;
				}
				
				
				//calculate residuals and fix significant figures on output data
				ArrayList<Double> residualsAsDouble = RSquaredAndCCR.calculateResiduals(externalValValues);
				ArrayList<String> residuals = new ArrayList<String>();
				if(residualsAsDouble.size() > 0){
					for(Double residual: residualsAsDouble){
						if(residual.isNaN()){
							residuals.add("");
						}
						else{
							//if at least one residual exists, there must have been a good model
							residuals.add(Utility.roundSignificantFigures(""+residual, Constants.REPORTED_SIGNIFICANT_FIGURES));
						}
					}
				}
				else{
					continue;
				}
				
				if(selectedPredictor.getActivityType().equals(Constants.CATEGORY)){
					//if category model, create confusion matrix.
					//round off the predicted values to nearest integer.
					confusionMatrix = RSquaredAndCCR.calculateConfusionMatrix(externalValValues);
					selectedPredictor.setExternalPredictionAccuracy(confusionMatrix.getCcrAsString());
				}
				else if(selectedPredictor.getActivityType().equals(Constants.CONTINUOUS) && externalValValues.size() > 1){
					//if continuous, calculate overall r^2 and... r0^2? or something? 
					//just r^2 for now, more later.
					Double rSquaredDouble = RSquaredAndCCR.calculateRSquared(externalValValues, residualsAsDouble);
					rSquared = Utility.roundSignificantFigures("" + rSquaredDouble, Constants.REPORTED_SIGNIFICANT_FIGURES);
					selectedPredictor.setExternalPredictionAccuracy(rSquared);
				}
				savePredictor(selectedPredictor, session);
				
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		session.close();
		return SUCCESS;
	}
	
	
	public static void printObjectsAsCsv(ArrayList<Object> objects, String path, boolean append) throws Exception{
		BufferedWriter out = new BufferedWriter(new FileWriter(path, append));
		String tableName = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf(".csv"));
		
		boolean headerDone = false;
		for(Object o: objects){
			if(!headerDone){
				//out.write(ClassUtils.varNamesToString(o) + "\n");
				headerDone = true;
			}
			String values = ClassUtils.varValuesToString(o);
			values = values.substring(1, values.length() - 1);
			values = "INSERT INTO " + tableName + " VALUES(" + values + ");\n";
			out.write(values);
		}
		out.close();
	}
	
	public static String printDatabaseTables(){
		//prints every database table it can get out to individual files
		Utility.writeToDebug("OWLSOWLSOWLS");
		
		/*
		//Job
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(Job.class, session);
			printObjectsAsCsv(list, basePath + "cbench_job.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//JobStats
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(JobStats.class, session);
			printObjectsAsCsv(list, basePath + "cbench_jobStats.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//AdminSettings
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(AdminSettings.class, session);
			printObjectsAsCsv(list, basePath + "cbench_adminSettings.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//ExternalValidation
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(ExternalValidation.class, session);
			printObjectsAsCsv(list, basePath + "cbench_externalValidation.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//KnnModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnModel.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnPlusModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnPlusModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnPlusModel.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnPlusParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(KnnPlusParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnPlusParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//SvmParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(SvmParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_svmParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//RandomForestParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(RandomForestParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//SvmModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(SvmModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_svmModel.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//SoftwareLink
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(SoftwareLink.class, session);
			printObjectsAsCsv(list, basePath + "cbench_softwareLink.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//Predictor
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(Predictor.class, session);
			printObjectsAsCsv(list, basePath + "cbench_predictor.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//User
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(User.class, session);
			printObjectsAsCsv(list, basePath + "cbench_user.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//RandomForestGrove
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = PopulateDataObjects.populateClass(RandomForestGrove.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestGrove.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		*/

		/*
		//RandomForestTree
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = new ArrayList();
			int chunkIndex = 0;
			while((list = PopulateDataObjects.populateClassInChunks(RandomForestTree.class, chunkSize, chunkIndex, session))!= null){
				printObjectsAsCsv(list, basePath + "cbench_randomForestTree.csv", append);
				list.clear();
				session.close();
				session = HibernateUtil.getSession();
				chunkIndex++;
			}
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		*/

		/*
		//PredictionValue
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = new ArrayList();
			int chunkIndex = 0;
			while((list = PopulateDataObjects.populateClassInChunks(PredictionValue.class, chunkSize, chunkIndex, session))!= null){
				printObjectsAsCsv(list, basePath + "cbench_predictionValue.csv", append);
				list.clear();
				session.close();
				session = HibernateUtil.getSession();
				chunkIndex++;
			}
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		 */

		boolean append = false;
		String basePath = Constants.CECCR_BASE_PATH + "theo/";

		int chunkSize = 1;
		append = true;

		//DataSet
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = new ArrayList();
			int chunkIndex = 0;
			while((list = PopulateDataObjects.populateClassInChunks(DataSet.class, chunkSize, chunkIndex, session))!= null){
				printObjectsAsCsv(list, basePath + "cbench_dataset.csv", append);
				list.clear();
				session.close();
				session = HibernateUtil.getSession();
				chunkIndex++;
			}
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//Prediction
		try{
			Session session = HibernateUtil.getSession();
			ArrayList list = new ArrayList();
			int chunkIndex = 0;
			while((list = PopulateDataObjects.populateClassInChunks(Prediction.class, chunkSize, chunkIndex, session))!= null){
				printObjectsAsCsv(list, basePath + "cbench_prediction.csv", append);
				chunkIndex++;
			}
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
	
	public static String restoreData(){
		//read CSVs and repopulate data into DB
		
		return SUCCESS;
	}
	
	public static void restoreTable(Class c, String fileName){
		
	}
	
	public static String restoreDatasets(){
	
		return SUCCESS;
	}
	public static String restorePredictions(){
		
		return SUCCESS;
	}

}