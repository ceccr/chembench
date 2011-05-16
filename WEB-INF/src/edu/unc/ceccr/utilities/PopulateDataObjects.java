package edu.unc.ceccr.utilities;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import edu.unc.ceccr.persistence.CompoundPredictions;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SoftwareLink;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;

public class PopulateDataObjects {

	//Every time we need to get an object or set of objects from the database
	//we do it from here.

	public static ArrayList populateClass(Class c, Session session){
		//gets all of any one object from the database, returns it as a list
		ArrayList list = null;
		Transaction tx = null;
		try {
 			tx = session.beginTransaction();
 			list = (ArrayList) session.createCriteria(c).list();
 			tx.commit();
 		} catch (Exception e) {
 			Utility.writeToDebug(e);
 		} 
 		return list;
	}

	public static ArrayList populateClassInChunks(Class c, int chunkSize, int chunkIndex, Session session){
		//gets a bunch of any one object from the database, returns it as a list
		ArrayList list = null;
		Transaction tx = null;
		Utility.writeToDebug("PopulateClassInChunks called with chunkSize " + chunkSize + " and chunkIndex " + chunkIndex);
		Utility.writeToDebug("maxResults " + chunkSize + " firstResult: " + (chunkSize*chunkIndex));
		try {
 			tx = session.beginTransaction();
 			list = (ArrayList) session.createCriteria(c).setFirstResult(chunkSize*chunkIndex).setMaxResults(chunkSize).list();
 			tx.commit();
  		} catch (Exception e) {
 			Utility.writeToDebug(e);
 		} 
  		if(list == null || list.isEmpty()){
			list = null;
		}
  		return list;
	}

	@SuppressWarnings("unchecked")
	public static PredictionValue getFirstPredictionValueByPredictionIdAndPredictorId(Long predictionId, Long predictorId, Session session) throws Exception{
		PredictionValue predictionValue = null;
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			predictionValue = (PredictionValue) session.createCriteria(PredictionValue.class)
			.add(Expression.eq("predictionId", predictionId))
			.add(Expression.eq("predictorId", predictorId)).setMaxResults(1).uniqueResult();
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		} 
				
		if(predictionValue != null){
			int numTotalModels = getPredictorById(predictionValue.getPredictorId(), session).getNumTestModels();
			predictionValue.setNumTotalModels(numTotalModels);
		}
		return predictionValue;
	}
	
	@SuppressWarnings("unchecked")
	public static List<PredictionValue> getPredictionValuesByPredictionIdAndPredictorId(Long predictionId, Long predictorId, Session session) throws Exception{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			predictionValues = (ArrayList<PredictionValue>) session.createCriteria(PredictionValue.class)
			.add(Expression.eq("predictionId", predictionId))
			.add(Expression.eq("predictorId", predictorId))
			.list();
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		} 

		for(PredictionValue pv : predictionValues){
			Predictor p = getPredictorById(pv.getPredictorId(), session);
			int numTotalModels = 0;
			if(p.getChildType() != null && p.getChildType().equals(Constants.NFOLD)){
				numTotalModels = p.getNumTotalModels();
			}
			else{
				numTotalModels = p.getNumTestModels();
			}
			pv.setNumTotalModels(numTotalModels);
		}
		return predictionValues;
	}
	
	@SuppressWarnings("unchecked")
	public static List<PredictionValue> getPredictionValuesByPredictionId(Long predictionId, Session session) throws Exception{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
		Prediction prediction = getPredictionById(predictionId, session);
		String[] predictorIds = prediction.getPredictorIds().split("\\s+");
		
		for(String predictorId : predictorIds){
			Transaction tx = null;
			try
			{
				tx = session.beginTransaction();
				ArrayList<PredictionValue> predictorPredictionValues = (ArrayList<PredictionValue>) session.createCriteria(PredictionValue.class)
				.add(Expression.eq("predictionId", predictionId))
				.add(Expression.eq("predictorId", Long.parseLong(predictorId)))
				.list();
				
				for(PredictionValue pv : predictorPredictionValues){
					Predictor p = getPredictorById(Long.parseLong(predictorId), session);
					int numTotalModels = 0;
					if(p.getChildType() != null && p.getChildType().equals(Constants.NFOLD)){
						numTotalModels = p.getNumTotalModels();
					}
					else{
						numTotalModels = p.getNumTestModels();
					}
					pv.setNumTotalModels(numTotalModels);
				}
				
				predictionValues.addAll(predictorPredictionValues);
				
			} catch (Exception ex) {
				Utility.writeToDebug(ex);
			} 
		}
		
		return predictionValues;
	}
	
	public static ArrayList<CompoundPredictions> populateCompoundPredictionValues(Long datasetId, Long predictionId, Session session) throws Exception{
		DataSet dataset = getDataSetById(datasetId, session);
		
		//get compounds from SDF
		String datasetDir = "";
		datasetDir = Constants.CECCR_USER_BASE_PATH + dataset.getUserName() + "/DATASETS/" + dataset.getName() + "/";
		
		ArrayList<String> compounds = null;
		
		if(dataset.getXFile() != null && ! dataset.getXFile().isEmpty()){
			compounds = DatasetFileOperations.getXCompoundNames(datasetDir + dataset.getXFile());
			Utility.writeToDebug("" + compounds.size() + " compounds found in X file.");
		}
		else{
			compounds = DatasetFileOperations.getSDFCompoundNames(datasetDir + dataset.getSdfFile());
			Utility.writeToDebug("" + compounds.size() + " compounds found in SDF.");
		}
		
		Utility.writeToDebug("getting from db");
		ArrayList<PredictionValue> predictorPredictionValues = (ArrayList<PredictionValue>) PopulateDataObjects.getPredictionValuesByPredictionId(predictionId, session);
		Utility.writeToDebug("done getting from db");

		//sort the by predictor ID
		Utility.writeToDebug("Sorting");
		Collections.sort(predictorPredictionValues, new Comparator<PredictionValue>(){
				public int compare(PredictionValue p1, PredictionValue p2) {
		    		return p1.getPredictorId().compareTo(p2.getPredictorId());
			    }});
		Utility.writeToDebug("Done sorting");

		Utility.writeToDebug("building hashmap");
		HashMap<String, ArrayList<PredictionValue>> predictionValueMap = new HashMap<String, ArrayList<PredictionValue>>();
		for(PredictionValue pv: predictorPredictionValues){
			ArrayList<PredictionValue> compoundPredValues = predictionValueMap.get(pv.getCompoundName());
			if(compoundPredValues == null){
				compoundPredValues = new ArrayList<PredictionValue>();
			}
			compoundPredValues.add(pv);
			predictionValueMap.put(pv.getCompoundName(), compoundPredValues);
		}
		Utility.writeToDebug("done building hashmap");
		
		ArrayList<CompoundPredictions> compoundPredictionValues = new ArrayList<CompoundPredictions>();
		//get prediction values for each compound
		for(int i = 0; i < compounds.size(); i++){
			CompoundPredictions cp = new CompoundPredictions();
			cp.setCompound(compounds.get(i));
			
			//get the prediction values for this compound
			cp.setPredictionValues(predictionValueMap.get(cp.getCompound()));
			
			//round them to a reasonable number of significant figures
			if(cp.getPredictionValues() != null){
				for(PredictionValue pv : cp.getPredictionValues()){
					int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
					if(pv.getPredictedValue() != null){
						String predictedValue = DecimalFormat.getInstance().format(pv.getPredictedValue()).replaceAll(",", "");
						pv.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));
					}
					if(pv.getStandardDeviation() != null){
						String stddev = DecimalFormat.getInstance().format(pv.getStandardDeviation()).replaceAll(",", "");
						pv.setStandardDeviation(Float.parseFloat(Utility.roundSignificantFigures(stddev, sigfigs)));
					}
				}
			}
			compoundPredictionValues.add(cp);
		}
		return compoundPredictionValues;
	}

	
	@SuppressWarnings("unchecked")
	public static List populateDatasetsForPrediction(String userName, boolean isAllUserIncludes, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		List <DataSet> dataSets = null;
		List <DataSet> usersDataSet = null;
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			if(isAllUserIncludes){
				//get both modeling AND prediction datasets, since modeling datasets are possible to predict as well.
				dataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", Constants.ALL_USERS_USERNAME))
							.add(Expression.or(Expression.eq("modelType",Constants.PREDICTION), Expression.or(Expression.eq("modelType",Constants.CONTINUOUS), Expression.eq("modelType",Constants.CATEGORY))))
							.addOrder(Order.asc("name")).list();
				
				usersDataSet = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.eq("jobCompleted", Constants.YES))
							.add(Expression.or(Expression.eq("modelType",Constants.PREDICTION), Expression.or(Expression.eq("modelType",Constants.CONTINUOUS), Expression.eq("modelType",Constants.CATEGORY))))
							.addOrder(Order.asc("name")).list();
			}
			else {
				dataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.eq("jobCompleted", Constants.YES))
							.add(Expression.or(Expression.eq("modelType",Constants.PREDICTION), Expression.or(Expression.eq("modelType",Constants.CONTINUOUS), Expression.eq("modelType",Constants.CATEGORY))))
							.addOrder(Order.asc("name")).list();
			}
			tx.commit();
			if(usersDataSet != null){
				dataSets.addAll(usersDataSet);
			}
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		} 
			
		return dataSets;
	}
	
	public static List populateDataset(String userName,String modelType, boolean isAllUserIncludes, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		//returns a list of datasets.
		//Used to populate the dropdowns on the Modeling and Dataset pages.
		
		List <DataSet> dataSets = null;
		List <DataSet> usersDataSet = null;
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			if(isAllUserIncludes){
				dataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", Constants.ALL_USERS_USERNAME))
							.add(Expression.eq("modelType",modelType))
							.addOrder(Order.desc("name")).list();
				usersDataSet = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.eq("jobCompleted", Constants.YES))
							.add(Expression.eq("modelType",modelType))
							.addOrder(Order.desc("name")).list();
			}
			else {
				dataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.eq("jobCompleted", Constants.YES))
							.add(Expression.eq("modelType",modelType))
							.addOrder(Order.desc("name")).list();
			}
			tx.commit();
			if(usersDataSet != null){
				dataSets.addAll(usersDataSet);
			}
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
		}
		Collections.reverse(dataSets);
		return dataSets;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> populateDatasetNames(String userName, boolean isAllUserIncludes, Session session) throws HibernateException, ClassNotFoundException, SQLException{

		//returns a list of strings. Used in form validation, to make sure a user doesn't reuse an existing name.
		
		List <DataSet> allUserDataSets = null;
		List <DataSet> usersDataSet = null;
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			if(isAllUserIncludes){
				allUserDataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", Constants.ALL_USERS_USERNAME))
							.addOrder(Order.desc("name")).list();
				
				usersDataSet = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.desc("name")).list();
				
			}
			else usersDataSet = session.createCriteria(DataSet.class).add(Expression.eq("userName", userName))
							.addOrder(Order.desc("name")).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}

		
		List <String> datasetNames = new ArrayList<String>();
		try{
			if(allUserDataSets != null){
				Iterator i = allUserDataSets.iterator();
		        while(i.hasNext())
		        {
		        	DataSet di = (DataSet) i.next();
		        	datasetNames.add(di.getName()/* + " (public)"*/);	        
		        }
			}
	       
	        if(usersDataSet != null){
		    	Iterator j = usersDataSet.iterator();
		    	while(j.hasNext()){
		    		DataSet dj = (DataSet) j.next();
		    		datasetNames.add(dj.getName()/* + " (private)"*/);	
		    	}
	        }
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}

		Collections.reverse(datasetNames);
		return datasetNames;
	}
	
	public static List<String> populatePredictorNames(String userName, boolean isAllUserIncludes, Session session)  throws HibernateException, ClassNotFoundException, SQLException{
		
		//returns a list of strings. Used in form validation, to make sure a user doesn't reuse an existing name.
		
		List <Predictor> userPredictors = null;
		List <Predictor> allUserPredictors = null;
		
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			if(isAllUserIncludes){
				allUserPredictors = session.createCriteria(Predictor.class)
							.add(Expression.eq("userName", Constants.ALL_USERS_USERNAME))
							.addOrder(Order.desc("name")).list();
				userPredictors = session.createCriteria(Predictor.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.desc("name")).list();
			}
			else userPredictors = session.createCriteria(Predictor.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.desc("name")).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 


		List <String> predictorNames = new ArrayList<String>();
		try{
			if(userPredictors != null){
				Iterator i = userPredictors.iterator();
		        while(i.hasNext())
		        {
		        	Predictor pi = (Predictor) i.next();
		        	predictorNames.add(pi.getName());	        
		        }
			}
	       
	        if(allUserPredictors != null){
		    	Iterator j = allUserPredictors.iterator();
		    	while(j.hasNext()){
		    		Predictor pj = (Predictor) j.next();
		    		predictorNames.add(pj.getName());	
		    	}
	        }
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		Collections.reverse(predictorNames);
		return predictorNames;
	}	
	
	public static List<String> populatePredictionNames(String userName, boolean isAllUserIncludes, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		
		//returns a list of strings. Used in form validation, to make sure a user doesn't reuse an existing name.
		
		List<Prediction> userPredictions = null;
		List<Prediction> allUserPredictions = null;
		
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			if(isAllUserIncludes){
				allUserPredictions = session.createCriteria(Prediction.class)
							.add(Expression.eq("userName", Constants.ALL_USERS_USERNAME))
							.addOrder(Order.desc("name")).list();
				userPredictions = session.createCriteria(Prediction.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.desc("name")).list();
			}
			else userPredictions = session.createCriteria(Prediction.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.desc("name")).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}

		List <String> predictionNames = new ArrayList<String>();
		try{
			if(userPredictions != null){
				Iterator i = userPredictions.iterator();
		        while(i.hasNext())
		        {
		        	Prediction pi = (Prediction) i.next();
		        	predictionNames.add(pi.getName());	        
		        }
			}
	       
	        if(allUserPredictions != null){
		    	Iterator j = allUserPredictions.iterator();
		    	while(j.hasNext()){
		    		Prediction pj = (Prediction) j.next();
		    		predictionNames.add(pj.getName());	
		    	}
	        }
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		Collections.reverse(predictionNames);
		return predictionNames;
	}
	
	@SuppressWarnings("unchecked")
	public static List populatePredictors(String userName, boolean includePublic, boolean onlyCompleted, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		
 		List<Predictor> predictors = new ArrayList();
 		List privatePredictors = null;
 		Transaction tx = null;
 		try {
 			tx = session.beginTransaction();
 			if(onlyCompleted){
 				if(userName.equals("ALLOFTHEM")){ //silly kludge, remove when done
	 				privatePredictors = session.createCriteria(Predictor.class)
						.add(Expression.eq("jobCompleted", Constants.YES))
						.add(Expression.ne("predictorType", Constants.HIDDEN))
						.addOrder(Order.desc("name")).list();
 				}
 				else{
	 				privatePredictors = session.createCriteria(Predictor.class)
						.add(Expression.eq("userName", userName))
						.add(Expression.eq("jobCompleted", Constants.YES))
						.add(Expression.ne("predictorType", Constants.HIDDEN))
						.addOrder(Order.desc("name")).list();
 				}
 			}

 			else privatePredictors = session.createCriteria(Predictor.class)
				.add(Expression.eq("userName", userName))
				.add(Expression.ne("predictorType", Constants.HIDDEN))
				.list();
 			tx.commit();
 		} catch (Exception e) {
 			Utility.writeToDebug(e);
 		} 
 		predictors.addAll(privatePredictors);
 		
 		//adme
 		if(includePublic){
	 		List ADMEPredictors = null;
	 		session = HibernateUtil.getSession();
	 		tx = null;
	 		try {
	 			tx = session.beginTransaction();
	 			if(onlyCompleted) ADMEPredictors = session.createCriteria(Predictor.class)
	 							.add(Expression.eq("predictorType", Constants.ADME))
								.add(Expression.eq("jobCompleted", Constants.YES))
	 							.addOrder(Order.desc("name")).list();
	 			else ADMEPredictors = session.createCriteria(Predictor.class)
					.add(Expression.eq("predictorType", Constants.ADME))
					.list();
	 			tx.commit();
	 		} catch (Exception e) {
	 			Utility.writeToDebug(e);
	 		} 
	 		predictors.addAll(ADMEPredictors);
 		}
 		
 		//tox
 		if(includePublic){
	 		List ToxicityPredictors = null;
	 		session = HibernateUtil.getSession();
	 		tx = null;
	 		try {
	 			tx = session.beginTransaction();
	 			if(onlyCompleted) ToxicityPredictors = session.createCriteria(Predictor.class)
	 							.add(Expression.eq("predictorType", Constants.TOXICITY))
								.add(Expression.eq("jobCompleted", Constants.YES))
	 							.addOrder(Order.desc("name")).list();
	 			else ToxicityPredictors = session.createCriteria(Predictor.class)
					.add(Expression.eq("predictorType", Constants.TOXICITY))
					.list();
	 			tx.commit();
	 		} catch (Exception e) {
	 			Utility.writeToDebug(e);
	 		} 
	 		predictors.addAll(ToxicityPredictors);
 		}
 		
 		//drugdiscovery
 		if(includePublic){
	 		List DrugDiscoveryPredictors = null;
	 		session = HibernateUtil.getSession();
	 		tx = null;
	 		try {
	 			tx = session.beginTransaction();
	 			if(onlyCompleted) DrugDiscoveryPredictors = session.createCriteria(Predictor.class)
	 							.add(Expression.eq("predictorType", Constants.DRUGDISCOVERY))
								.add(Expression.eq("jobCompleted", Constants.YES))
	 							.addOrder(Order.desc("name")).list();
	 			else DrugDiscoveryPredictors = session.createCriteria(Predictor.class)
					.add(Expression.eq("predictorType", Constants.DRUGDISCOVERY))
					.list();
	 			tx.commit();
	 		} catch (Exception e) {
	 			Utility.writeToDebug(e);
	 		} 
	 		predictors.addAll(DrugDiscoveryPredictors);
		}

 		for(int i = 0; i < predictors.size(); i++){
 			if(predictors.get(i).getDatasetId() != null && getDataSetById(predictors.get(i).getDatasetId(), session) != null){
 				predictors.get(i).setDatasetDisplay(PopulateDataObjects.getDataSetById(predictors.get(i).getDatasetId(), session).getName());
 			}
 		}
 		
 		Collections.reverse(predictors);
		return predictors;
	}
	

	@SuppressWarnings("unchecked")
	public static List populatePredictions(String userName, boolean onlySaved, Session session) {
		
		List<Prediction> predictions = null;
		try 
		{
			//Utility.writeToDebug("Populating a list of the prediction results.", userName, "null");
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				predictions = session.createCriteria(Prediction.class)
					.add(Expression.eq("jobCompleted", Constants.YES))
					.add(Expression.or(Expression.eq("userName", userName),Expression.eq("userName", Constants.ALL_USERS_USERNAME)))
					.addOrder(Order.desc("name")).list();
				tx.commit();
			} catch (Exception e) {
				Utility.writeToDebug(e);
			} 

			for (Prediction p : predictions) {
				String predictorNames = "";
				String[] predictorIds = p.getPredictorIds().split("\\s+");
				for(int i = 0; i < predictorIds.length; i++){
					predictorNames += getPredictorById(Long.parseLong(predictorIds[i]), session).getName() + " ";
				}
				p.setPredictorNames(predictorNames);
	 			if(p.getDatasetId() != null && getDataSetById(p.getDatasetId(), session) != null){
	 				p.setDatasetDisplay(getDataSetById(p.getDatasetId(), session).getName());
	 			}
			}

		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		Collections.reverse(predictions);
		return predictions;
	}
	

	public static Job getJobById(Long jobId, Session session) throws ClassNotFoundException, SQLException {
		Job job = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			job = (Job) session.createCriteria(Job.class)
					.add(Expression.eq("id", jobId))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return job;
	}
	
	public static Job getJobByNameAndUsername(String name, String userName, Session session){
		Job job = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			job = (Job) session.createCriteria(Job.class)
					.add(Expression.eq("jobName", name))
					.add(Expression.eq("userName", userName))
					.uniqueResult();

			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return job;
	}
	
	public static String getSdfFileForDataset(String datasetName, String userName, Session session) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataset = (DataSet) session.createCriteria(DataSet.class)
					.add(Expression.eq("name", datasetName))
					.add(Expression.eq("userName", userName))
					.uniqueResult();

			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 

		return dataset.getSdfFile();
	}
	
	public static DataSet getDataSetByName(String datasetName, String userName, Session session) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataset = (DataSet) session.createCriteria(DataSet.class)
					.add(Expression.eq("name", datasetName))
					.add(Expression.eq("userName", userName))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		return dataset;
	}
	
	public static DataSet getDataSetById(Long id, Session session) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataset = (DataSet) session.createCriteria(DataSet.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return dataset;
	}

	public static Predictor getPredictorById(Long predictorId, Session session) throws ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)
					.add(Expression.eq("id", predictorId))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 

		if(predictor != null && predictor.getDatasetId() != null){
			if(getDataSetById(predictor.getDatasetId(), session) != null){
				predictor.setDatasetDisplay(PopulateDataObjects.getDataSetById(predictor.getDatasetId(), session).getName());
			}
		}
		return predictor;
	}
	

	@SuppressWarnings("unchecked")
	public static Prediction getPredictionById(Long predictionId, Session session) throws Exception{
		Prediction prediction = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			prediction = (Prediction) session.createCriteria(Prediction.class)
					.add(Expression.eq("id", predictionId))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 

		String predictorNames = "";
		String[] predictorIds = prediction.getPredictorIds().split("\\s+");
		for(int i = 0; i < predictorIds.length; i++){
			predictorNames += getPredictorById(Long.parseLong(predictorIds[i]), session).getName() + " ";
		}
		prediction.setPredictorNames(predictorNames);
		prediction.setDatabase(prediction.getDatabase());

		if(prediction.getDatasetId() != null && getDataSetById(prediction.getDatasetId(), session) != null){
			prediction.setDatasetDisplay(getDataSetById(prediction.getDatasetId(), session).getName());
		}
		
		return prediction;
	}
	
	@SuppressWarnings("unchecked")
	public static Prediction getPredictionByName(String jobName, String userName, Session session) throws Exception{
		Prediction prediction = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			prediction = (Prediction) session.createCriteria(Prediction.class)
					.add(Expression.eq("userName", userName))
					.add(Expression.eq("name", jobName))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 

		String predictorNames = "";
		String[] predictorIds = prediction.getPredictorIds().split("\\s+");
		for(int i = 0; i < predictorIds.length; i++){
			predictorNames += getPredictorById(Long.parseLong(predictorIds[i]), session).getName() + " ";
		}
		prediction.setPredictorNames(predictorNames);
		prediction.setDatabase(prediction.getDatabase());

		if(prediction.getDatasetId() != null && getDataSetById(prediction.getDatasetId(), session) != null){
			prediction.setDatasetDisplay(getDataSetById(prediction.getDatasetId(), session).getName());
		}
		
		return prediction;
	}

	public static User getUserByUserName(String userName, Session session){
		User user = null;
		
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			user = (User) session.createCriteria(User.class)
					.add(Expression.eq("userName", userName)).uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return user;
	}

	public static ArrayList<User> getAllUsers(Session session){
		ArrayList<User> users = new ArrayList<User>();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			users = (ArrayList<User>) session.createCriteria(User.class).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return users;
	}
	
	public static List<User> getUsers(Session session){
		List<User> users = null;
		
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			users = (List<User>) session.createCriteria(User.class).list();
			tx.commit();
		} 
		catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		return users;
	}
	
	public static List<JobStats> getJobStats(Session session){
		List<JobStats> jobStats = null;
		
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			jobStats = session.createCriteria(JobStats.class).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return jobStats;
	}

	public static List<RandomForestGrove> getRandomForestGrovesByPredictorId(Long predictorId, Session session) throws Exception{
		
		List<RandomForestGrove> groves = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			groves = session.createCriteria(RandomForestGrove.class)
					.add(Expression.eq("predictorId", predictorId)).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return groves;
	}

	public static List<RandomForestTree> getRandomForestTreesByGroveId(Long groveId, Session session) throws Exception{
		
		List<RandomForestTree> trees = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			trees = session.createCriteria(RandomForestTree.class)
					.add(Expression.eq("randomForestGroveId", groveId)).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return trees;
	}

	public static List<KnnPlusModel> getKnnPlusModelsByPredictorId(Long predictorId, Session session)  throws ClassNotFoundException, SQLException {
		List<KnnPlusModel> models = new ArrayList<KnnPlusModel>();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			models = session.createCriteria(KnnPlusModel.class)
					.add(Expression.eq("predictorId", predictorId)).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return models;
	}
	
	public static List<SvmModel> getSvmModelsByPredictorId(Long predictorId, Session session)  throws ClassNotFoundException, SQLException {
		//Utility.writeToDebug("getting models for predictorId: " + predictorId);
		
		List<SvmModel> models = new ArrayList<SvmModel>();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			models = session.createCriteria(SvmModel.class)
					.add(Expression.eq("predictorId", predictorId)).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		return models;
	}
	
	
	public static List<KnnModel> getModelsByPredictorId(Long predictorId, Session session)  throws ClassNotFoundException, SQLException {
		//Utility.writeToDebug("getting models for predictorId: " + predictorId);
		Predictor predictor = getPredictorById(predictorId, session);
		
		List<KnnModel> models = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			models = session.createCriteria(KnnModel.class)
					.add(Expression.eq("predictor", predictor)).list();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		
		//if the model type is continuous,
		//sort models in decreasing order by r^2 value
		//(used when displaying top 10 models on view predictor page)
		if(predictor.getActivityType().equals(Constants.CONTINUOUS) && models != null && models.size() > 1){
			Collections.sort(models, new Comparator<KnnModel>() {
			    public int compare(KnnModel o1, KnnModel o2) {
		    		return (o2.getRSquared() > o1.getRSquared()? 1:-1);
			    }});
		}
		return models;
	}
	
	public static Predictor getPredictorByName(String selectedPredictorName, String user, Session session)	throws ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)
					.add(Expression.eq("name", selectedPredictorName))
					.add(Expression.eq("userName", user))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 

		try{

			if(predictor != null && predictor.getDatasetId() != null){
				if(getDataSetById(predictor.getDatasetId(), session) != null){
					predictor.setDatasetDisplay(PopulateDataObjects.getDataSetById(predictor.getDatasetId(), session).getName());
				}
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		return predictor;
	}
	
	public static ArrayList<Predictor> getChildPredictors(Predictor predictor, Session session){
		ArrayList<Predictor> childPredictors = new ArrayList<Predictor>();
		
		String[] childPredictorIds;
		if(predictor.getChildIds() != null && !predictor.getChildIds().trim().equals("")){
			//get external validation from each child predictor
			childPredictorIds = predictor.getChildIds().split("\\s+");
		}
		else{
			return childPredictors;
		}
		
		Transaction tx = null;
		try {
			for(String childPredictorId: childPredictorIds){
				tx = session.beginTransaction();
				Predictor childPredictor = (Predictor) session.createCriteria(Predictor.class)
					.add(Expression.eq("id", Long.parseLong(childPredictorId))).uniqueResult();
				tx.commit();
				childPredictors.add(childPredictor);
			}
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return childPredictors;
	}
	
	@SuppressWarnings("unchecked")
	public static List getExternalValidationValues(Long predictorId, Session session){
		
		List<ExternalValidation> externalValValues = new ArrayList<ExternalValidation>();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			externalValValues = session.createCriteria(ExternalValidation.class)
				.add(Expression.eq("predictorId", predictorId))
				.addOrder(Order.asc("predictedValue")).list();

			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 

		return externalValValues;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> populateTaskNames(String userName, boolean justRunning, Session session) {
		
		List<String> taskNames = new ArrayList<String>();
		List<Job> tasks = null;
		try 
		{
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				tasks = session.createCriteria(Job.class).add(
						Expression.eq("userName", userName))
						.list();
				tx.commit();
			} catch (Exception e) {
				Utility.writeToDebug(e);
			}
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		try{
			if(tasks != null){
				Iterator i = tasks.iterator();
		        while(i.hasNext())
		        {
		        	Job ti = (Job) i.next();
		        	if(!justRunning) taskNames.add(ti.getJobName());
		        	else if(!ti.getStatus().equals(Constants.QUEUED))
		        			taskNames.add(ti.getJobName());
		        }
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		return taskNames;
	}
	
	@SuppressWarnings("unchecked")
	public static List populateTasks(String userName, boolean justRunning, Session session) {
		
		List<Job> tasks = null;
		try 
		{
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				if(justRunning)
					tasks = session.createCriteria(Job.class).
						add(Expression.eq("userName", userName)).
						add(Expression.eq("state", Constants.QUEUED)).
						list();
				else
					tasks = session.createCriteria(Job.class).
					add(Expression.eq("userName", userName)).
					list();
				tx.commit();
			} catch (Exception e) {
				Utility.writeToDebug(e);
			} 
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		return tasks;
	}
	
	public static Job getTaskById(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		Job task = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			task = (Job) session.createCriteria(Job.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return task;
	}

	@SuppressWarnings("unchecked")
	public static List populateSoftwareLinks(Session session) {
		
		List<SoftwareLink> softwareLinks = null;
		try 
		{
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				softwareLinks = session.createCriteria(SoftwareLink.class).list();
				tx.commit();
			} catch (Exception e) {
				Utility.writeToDebug(e);
			} 
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		Collections.sort(softwareLinks, new Comparator<SoftwareLink>() {
		    public int compare(SoftwareLink sl1, SoftwareLink sl2) {
	    		return sl1.getName().toLowerCase().compareTo(sl2.getName().toLowerCase());
		    }});
		return softwareLinks;
	}
	
	public static SoftwareLink getSoftwareLinkById(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		SoftwareLink sl = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			sl = (SoftwareLink) session.createCriteria(SoftwareLink.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return sl;
	}
	

	public static RandomForestParameters getRandomForestParametersById(Long id, Session session) throws Exception {
		RandomForestParameters params = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			params = (RandomForestParameters) session.createCriteria(RandomForestParameters.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return params;
	}
	
	public static KnnParameters getKnnParametersById(Long id, Session session) throws Exception {
		KnnParameters params = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			params = (KnnParameters) session.createCriteria(KnnParameters.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return params;
	}
	
	public static KnnPlusParameters getKnnPlusParametersById(Long id, Session session) throws Exception {
		KnnPlusParameters params = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			params = (KnnPlusParameters) session.createCriteria(KnnPlusParameters.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return params;
	}
	
	public static SvmParameters getSvmParametersById(Long id, Session session) throws Exception {
		SvmParameters params = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			params = (SvmParameters) session.createCriteria(SvmParameters.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} 
		return params;
	}
	
	
}