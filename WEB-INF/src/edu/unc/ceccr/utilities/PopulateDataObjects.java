package edu.unc.ceccr.utilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelingTask;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionTask;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.VisualizationTask;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.utilities.Utility;

public class PopulateDataObjects {


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
							.addOrder(Order.asc("fileName")).list();
				
				usersDataSet = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.or(Expression.eq("modelType",Constants.PREDICTION), Expression.or(Expression.eq("modelType",Constants.CONTINUOUS), Expression.eq("modelType",Constants.CATEGORY))))
							.addOrder(Order.asc("fileName")).list();
			}
			else {
				dataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.or(Expression.eq("modelType",Constants.PREDICTION), Expression.or(Expression.eq("modelType",Constants.CONTINUOUS), Expression.eq("modelType",Constants.CATEGORY))))
							.addOrder(Order.asc("fileName")).list();
			}
			tx.commit();
			if(usersDataSet != null){
				dataSets.addAll(usersDataSet);
			}
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
			if (tx != null)
				tx.rollback();
		} finally{; }
			
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
							.addOrder(Order.asc("fileName")).list();
				usersDataSet = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.eq("modelType",modelType))
							.addOrder(Order.asc("fileName")).list();
			}
			else {
				dataSets = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.add(Expression.eq("modelType",modelType))
							.addOrder(Order.asc("fileName")).list();
			}
			tx.commit();
			if(usersDataSet != null){
				dataSets.addAll(usersDataSet);
			}
		} catch (Exception ex) {
			Utility.writeToDebug(ex);
			if (tx != null)
				tx.rollback();
		} finally{; }
			
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
							.addOrder(Order.asc("fileName")).list();
				
				usersDataSet = session.createCriteria(DataSet.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("fileName")).list();
				
			}
			else usersDataSet = session.createCriteria(DataSet.class).add(Expression.eq("userName", userName))
							.addOrder(Order.asc("fileName")).list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally{; }

		
		List <String> datasetNames = new ArrayList<String>();
		try{
			if(allUserDataSets != null){
				Iterator i = allUserDataSets.iterator();
		        while(i.hasNext())
		        {
		        	DataSet di = (DataSet) i.next();
		        	datasetNames.add(di.getFileName()/* + " (public)"*/);	        
		        }
			}
	       
	        if(usersDataSet != null){
		    	Iterator j = usersDataSet.iterator();
		    	while(j.hasNext()){
		    		DataSet dj = (DataSet) j.next();
		    		datasetNames.add(dj.getFileName()/* + " (private)"*/);	
		    	}
	        }
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
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
							.addOrder(Order.asc("name")).list();
				userPredictors = session.createCriteria(Predictor.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("name")).list();
			}
			else userPredictors = session.createCriteria(Predictor.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("name")).list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally{; }


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
							.addOrder(Order.asc("jobName")).list();
				userPredictions = session.createCriteria(Prediction.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("jobName")).list();
			}
			else userPredictions = session.createCriteria(Prediction.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("jobName")).list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally{; }

		List <String> predictionNames = new ArrayList<String>();
		try{
			if(userPredictions != null){
				Iterator i = userPredictions.iterator();
		        while(i.hasNext())
		        {
		        	Prediction pi = (Prediction) i.next();
		        	predictionNames.add(pi.getJobName());	        
		        }
			}
	       
	        if(allUserPredictions != null){
		    	Iterator j = allUserPredictions.iterator();
		    	while(j.hasNext()){
		    		Prediction pj = (Prediction) j.next();
		    		predictionNames.add(pj.getJobName());	
		    	}
	        }
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		return predictionNames;
	}
	
	@SuppressWarnings("unchecked")
	public static List populatePredictors(String userName, boolean isAllUserIncludes, boolean onlySaved, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		
 		List<Predictor> predictors = new ArrayList();
 		List privatePredictors = null;
 		Transaction tx = null;
 		try {
 			tx = session.beginTransaction();
 			if(onlySaved) privatePredictors = session.createCriteria(Predictor.class)
 							.add(Expression.eq("userName", userName))
 							.add(Expression.eq("status","saved")).list();
 			else privatePredictors = session.createCriteria(Predictor.class)
				.add(Expression.eq("userName", userName))
				.list();
 			tx.commit();
 		} catch (RuntimeException e) {
 			if (tx != null)
 				tx.rollback();
 			Utility.writeToDebug(e);
 		} finally {
 			; 
 		}
 		predictors.addAll(privatePredictors);
 		
 		List ADMEToxPredictors = null;
 		session = HibernateUtil.getSession();
 		tx = null;
 		try {
 			tx = session.beginTransaction();
 			if(onlySaved) ADMEToxPredictors = session.createCriteria(Predictor.class)
 							.add(Expression.eq("predictorType", "ADMETox"))
 							.add(Expression.eq("status","saved")).list();
 			else ADMEToxPredictors = session.createCriteria(Predictor.class)
				.add(Expression.eq("predictorType", "ADMETox"))
				.list();
 			tx.commit();
 		} catch (RuntimeException e) {
 			if (tx != null)
 				tx.rollback();
 			Utility.writeToDebug(e);
 		} finally {
 			; 
 		}
 		predictors.addAll(ADMEToxPredictors);
 		
 		List DrugDiscoveryPredictors = null;
 		session = HibernateUtil.getSession();
 		tx = null;
 		try {
 			tx = session.beginTransaction();
 			if(onlySaved) DrugDiscoveryPredictors = session.createCriteria(Predictor.class)
 							.add(Expression.eq("predictorType", "DrugDiscovery"))
 							.add(Expression.eq("status","saved")).list();
 			else DrugDiscoveryPredictors = session.createCriteria(Predictor.class)
				.add(Expression.eq("predictorType", "DrugDiscovery"))
				.list();
 			tx.commit();
 		} catch (RuntimeException e) {
 			if (tx != null)
 				tx.rollback();
 			Utility.writeToDebug(e);
 		} finally {
 			; 
 		}
 		predictors.addAll(DrugDiscoveryPredictors);

		Utility.writeToDebug("going through predictors");
 		for(int i = 0; i < predictors.size(); i++){
 			Utility.writeToDebug("predictor name: " + predictors.get(i).getName());
 			if(predictors.get(i).getDatasetId() != null){
 				predictors.get(i).setDatasetDisplay(PopulateDataObjects.getDataSetById(predictors.get(i).getDatasetId(), session).getFileName());
 			}
 		}
		Utility.writeToDebug("done");
 		
		return predictors;
	}
	

	@SuppressWarnings("unchecked")
	public static List populatePredictions(String userName, boolean onlySaved, Session session) {
		
		List<Prediction> predictions = null;
		try 
		{
			//Utility.writeToDebug("Populating a list of the saved prediction results.", userName, "null");
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				if(onlySaved) predictions = session.createCriteria(Prediction.class)
							.add(Expression.or(Expression.eq("userName", userName),Expression.eq("userName", Constants.ALL_USERS_USERNAME)))
							.add(Expression.eq("status","saved")).list();
				else predictions = session.createCriteria(Prediction.class)
				.add(Expression.or(Expression.eq("userName", userName),Expression.eq("userName", Constants.ALL_USERS_USERNAME)))
				.list();
				tx.commit();
			} catch (RuntimeException e) {
				Utility.writeToDebug(e);
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {
				;
				
			}

			for (Prediction p : predictions) {
				p.setPredictorName(getPredictorById(p.getPredictorId(), session).getName());
				p.setDatabase(Utility.wrapFileName(p.getDatabase()));
	 			if(p.getDatasetId() != null){
	 				p.setDatasetDisplay(getDataSetById(p.getDatasetId(), session).getFileName());
	 			}
			}
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		return predictions;
	}
	
	public static String getSdfFileForDataset(String datasetName, String userName, Session session) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataset = (DataSet) session.createCriteria(DataSet.class)
					.add(Expression.eq("fileName", datasetName))
					.add(Expression.eq("userName", userName))
					.uniqueResult();

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}

		return dataset.getSdfFile();
	}
	
	public static DataSet getDataSetByName(String datasetName, String userName, Session session) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataset = (DataSet) session.createCriteria(DataSet.class)
					.add(Expression.eq("fileName", datasetName))
					.add(Expression.eq("userName", userName))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		return dataset;
	}
	
	public static DataSet getDataSetById(Long fileId, Session session) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataset = (DataSet) session.createCriteria(DataSet.class)
					.add(Expression.eq("fileId", fileId))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		
		return dataset;
	}

	public static Predictor getPredictorById(Long predictorId, Session session) throws ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)
					.add(Expression.eq("predictorId", predictorId))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}

		if(predictor.getDatasetId() != null){
			predictor.setDatasetDisplay(PopulateDataObjects.getDataSetById(predictor.getDatasetId(), session).getFileName());
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
					.add(Expression.eq("predictionId", predictionId))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}

		prediction.setPredictorName(getPredictorById(prediction.getPredictorId(), session).getName());
		prediction.setDatabase(Utility.wrapFileName(prediction.getDatabase()));

		if(prediction.getDatasetId() != null){
			prediction.setDatasetDisplay(getDataSetById(prediction.getDatasetId(), session).getFileName());
		}
		
		return prediction;
	}
	
	public static List<Model> getModelsByPredictorId(Long predictorId, Session session)  throws ClassNotFoundException, SQLException {
		List<Model> models = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			models = session.createCriteria(Model.class)
					.add(Expression.eq("predictor", getPredictorById(predictorId, session))).list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		return models;
	}
	
	
	public static Predictor getPredictorByName(String selectedPredictorName, String user, Session session)	throws ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)	.add(Expression.eq("name", selectedPredictorName))
					.add(Expression.eq("userName", user)).uniqueResult();
			
			predictor.getExternalValidationResults().size();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}

		if(predictor.getDatasetId() != null){
			predictor.setDatasetDisplay(PopulateDataObjects.getDataSetById(predictor.getDatasetId(), session).getFileName());
		}
		return predictor;
	}
	
	@SuppressWarnings("unchecked")
	public static List getExternalValidationValues(Predictor pred, Session session)throws ClassNotFoundException, SQLException 
	{

		List<ExternalValidation> externalValValues = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			externalValValues = session.createCriteria(ExternalValidation.class).add(Expression.eq("predictor", pred)).addOrder(Order.asc("predictedValue")).list();

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}

		return externalValValues;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> populateTaskNames(String userName, boolean justRunning, Session session) {
		
		List<String> taskNames = new ArrayList<String>();
		List<QueueTask> tasks = null;
		try 
		{
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				tasks = session.createCriteria(QueueTask.class).add(
						Expression.eq("userName", userName)).list();
				tx.commit();
			} catch (RuntimeException e) {
				Utility.writeToDebug(e);
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {
				; 
			}
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		try{
			if(tasks != null){
				Iterator i = tasks.iterator();
		        while(i.hasNext())
		        {
		        	QueueTask ti = (QueueTask) i.next();
		        	if(!justRunning) taskNames.add(ti.getJobName());
		        	else if(ti.getState()==QueueTask.State.started)
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
		
		List<QueueTask> tasks = null;
		try 
		{
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				if(justRunning)
					tasks = session.createCriteria(QueueTask.class).
						add(Expression.eq("userName", userName)).
						add(Expression.eq("state", QueueTask.State.started)).
						list();
				else
					tasks = session.createCriteria(QueueTask.class).
					add(Expression.eq("userName", userName)).
					list();
				tx.commit();
			} catch (RuntimeException e) {
				Utility.writeToDebug(e);
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {
				; 
			}
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		return tasks;
	}
	
	
	public static QueueTask getTaskById(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		QueueTask task = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			task = (QueueTask) session.createCriteria(QueueTask.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		return task;
	}

	@SuppressWarnings("unchecked")
	public static List<Long> getModelingTasksIdByDatasetId(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException {
		List<ModelingTask> mTasks = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			mTasks = session.createCriteria(ModelingTask.class)
					.add(Expression.eq("datasetId", id))
					.list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		if(mTasks!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Iterator<ModelingTask> i = mTasks.iterator();i.hasNext();){
				ids.add(i.next().getId());
			}
			return ids;
		}
		else return null;
	}
	
	public static PredictionTask getPredictionTaskById(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException{
		PredictionTask pTask = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			pTask = (PredictionTask) session.createCriteria(PredictionTask.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		if(pTask!=null) return pTask;
		else return null;
	}

	public static ModelingTask getModelingTaskById(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException {
		ModelingTask mTask = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			mTask = (ModelingTask) session.createCriteria(ModelingTask.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		if(mTask!=null) return mTask;
		else return null;
	}

	public static VisualizationTask getVisualizationTaskById(Long id, Session session) throws HibernateException, ClassNotFoundException, SQLException {
		VisualizationTask vTask = null;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			vTask = (VisualizationTask) session.createCriteria(VisualizationTask.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			; 
		}
		if(vTask!=null) return vTask;
		else return null;
	}
}