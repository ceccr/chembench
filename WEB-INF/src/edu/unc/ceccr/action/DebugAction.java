package edu.unc.ceccr.action;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.opensymphony.xwork2.ActionSupport;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SoftwareLink;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.ClassUtils;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.calculations.ConfusionMatrix;
import edu.unc.ceccr.workflows.calculations.RSquaredAndCCR;
import edu.unc.ceccr.workflows.visualization.ExternalValidationChart;
//struts2


public class DebugAction extends ActionSupport{

	// Various methods useful in debugging and fixing user data when something goes wrong.
	// Any of these methods can be deleted without impacting the system. I found them pretty handy though.
	
	private static final long serialVersionUID = 1L;
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
								ExternalValidationChart.createChart(selectedPredictor, ""+(i+1));
							}
							externalValValues.addAll(childExtVals);
						}
					}

					Double mean = childAccuracies.getMean();
					Double stddev = childAccuracies.getStandardDeviation();
					
					if(selectedPredictor.getActivityType().equals(Constants.CONTINUOUS)){
						rSquaredAverageAndStddev = Utility.roundSignificantFigures(""+mean, Constants.REPORTED_SIGNIFICANT_FIGURES);
						rSquaredAverageAndStddev += " \u00B1 ";
						rSquaredAverageAndStddev += Utility.roundSignificantFigures(""+stddev, Constants.REPORTED_SIGNIFICANT_FIGURES);
						Utility.writeToDebug("rsquared avg and stddev: " + rSquaredAverageAndStddev);
						selectedPredictor.setExternalPredictionAccuracyAvg(rSquaredAverageAndStddev);
						//make main ext validation chart
						ExternalValidationChart.createChart(selectedPredictor, "0");
					}
					else if(selectedPredictor.getActivityType().equals(Constants.CATEGORY)){
						ccrAverageAndStddev = Utility.roundSignificantFigures(""+mean, Constants.REPORTED_SIGNIFICANT_FIGURES);
						ccrAverageAndStddev += " \u00B1 ";
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
	

	public String fixBrokenPredictors() throws Exception{
		/*
		 Sometimes a job will fail in external set prediction but is otherwise fine. This function will predict
		 external sets and read in the output for any number of predictor IDs. 
		 */
		
		String ids = "";
		//Example: String ids="1635 1642";
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
	
	
	public static String printDatabaseTables() throws Exception{
		//prints every database table it can get out to individual files
		Utility.writeToDebug("OWLSOWLSOWLS");

		boolean append = false;
		String basePath = Constants.CECCR_BASE_PATH + "theo/";

		//Job
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(Job.class, session);
			printObjectsAsCsv(list, basePath + "cbench_job.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//JobStats
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(JobStats.class, session);
			printObjectsAsCsv(list, basePath + "cbench_jobStats.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//ExternalValidation
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(ExternalValidation.class, session);
			printObjectsAsCsv(list, basePath + "cbench_externalValidation.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//KnnModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(KnnModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnModel.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnPlusModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(KnnPlusModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnPlusModel.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(KnnParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//KnnPlusParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(KnnPlusParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_knnPlusParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//SvmParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(SvmParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_svmParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//RandomForestParameters
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(RandomForestParameters.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestParameters.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//SvmModel
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(SvmModel.class, session);
			printObjectsAsCsv(list, basePath + "cbench_svmModel.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//SoftwareLink
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(SoftwareLink.class, session);
			printObjectsAsCsv(list, basePath + "cbench_softwareLink.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//Predictor
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(Predictor.class, session);
			printObjectsAsCsv(list, basePath + "cbench_predictor.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//User
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(User.class, session);
			printObjectsAsCsv(list, basePath + "cbench_user.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		//RandomForestGrove
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = PopulateDataObjects.populateClass(RandomForestGrove.class, session);
			printObjectsAsCsv(list, basePath + "cbench_randomForestGrove.csv", append);
			session.close();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		int chunkSize = 100;

		//RandomForestTree
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = new ArrayList<Object>();
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
		
		//PredictionValue
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = new ArrayList<Object>();
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
		
		append = true;

		//DataSet
		try{
			Session session = HibernateUtil.getSession();
			ArrayList<Object> list = new ArrayList<Object>();
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
			ArrayList<Object> list = new ArrayList<Object>();
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
		
		Utility.writeToDebug("userName, password, email, bench, status, firstname, lastname, orgtype, orgname, " +
				"orgposition, zipcode, state, country, address, city, showPublicDatasets, showPublicPredictors, " +
				"viewDatasetCompoundsPerPage, viewPredictionCompoundsPerPage, showAdvancedKnnModeling, isAdmin, canDownloadDescriptors");
		ArrayList<User> users = PopulateDataObjects.getAllUsers(HibernateUtil.getSession());
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
		FileAndDirOperations.writeStringToFile("", Constants.CECCR_BASE_PATH);
		return SUCCESS;
	}
	
	public static String restoreData(){
		//read CSVs and repopulate data into DB
		
		return SUCCESS;
	}
	
	public static void restoreTable(Class<?> c, String fileName){
		
	}
	
	public static String restoreDatasets(){
	
		return SUCCESS;
	}
	public static String restorePredictions(){
		
		return SUCCESS;
	}

}