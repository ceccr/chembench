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
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.ModellingTask;
import edu.unc.ceccr.persistence.PredictionJob;
import edu.unc.ceccr.persistence.PredictionTask;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.VisualizationTask;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.utilities.Utility;

public class PopulateDataObjects {


	@SuppressWarnings("unchecked")
	public static List populateDatasetsForPrediction(String userName, boolean isAllUserIncludes) throws HibernateException, ClassNotFoundException, SQLException{
		List <DataSet> dataSets = null;
		List <DataSet> usersDataSet = null;
		Session session = HibernateUtil.getSession();
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
		} finally{session.close();}
			
		return dataSets;
	}
	
	public static List populateDataset(String userName,String modelType, boolean isAllUserIncludes) throws HibernateException, ClassNotFoundException, SQLException{
		//returns a list of datasets.
		//Used to populate the dropdowns on the Modeling and Dataset pages.
		
		List <DataSet> dataSets = null;
		List <DataSet> usersDataSet = null;
		Session session = HibernateUtil.getSession();
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
		} finally{session.close();}
			
		return dataSets;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> populateDatasetNames(String userName, boolean isAllUserIncludes) throws HibernateException, ClassNotFoundException, SQLException{

		//returns a list of strings. Used in form validation, to make sure a user doesn't reuse an existing name.
		
		List <DataSet> allUserDataSets = null;
		List <DataSet> usersDataSet = null;
		Session session = HibernateUtil.getSession();
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
		} finally{session.close();}

		
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
	
	public static List<String> populatePredictorNames(String userName, boolean isAllUserIncludes)  throws HibernateException, ClassNotFoundException, SQLException{
		
		//returns a list of strings. Used in form validation, to make sure a user doesn't reuse an existing name.
		
		List <Predictor> userPredictors = null;
		List <Predictor> allUserPredictors = null;
		
		Session session = HibernateUtil.getSession();
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
		} finally{session.close();}


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
	
	public static List<String> populatePredictionNames(String userName, boolean isAllUserIncludes) throws HibernateException, ClassNotFoundException, SQLException{
		
		//returns a list of strings. Used in form validation, to make sure a user doesn't reuse an existing name.
		
		List<PredictionJob> userPredictions = null;
		List<PredictionJob> allUserPredictions = null;
		
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();
			if(isAllUserIncludes){
				allUserPredictions = session.createCriteria(PredictionJob.class)
							.add(Expression.eq("userName", Constants.ALL_USERS_USERNAME))
							.addOrder(Order.asc("jobName")).list();
				userPredictions = session.createCriteria(PredictionJob.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("jobName")).list();
			}
			else userPredictions = session.createCriteria(PredictionJob.class)
							.add(Expression.eq("userName", userName))
							.addOrder(Order.asc("jobName")).list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally{session.close();}

		List <String> predictionNames = new ArrayList<String>();
		try{
			if(userPredictions != null){
				Iterator i = userPredictions.iterator();
		        while(i.hasNext())
		        {
		        	PredictionJob pi = (PredictionJob) i.next();
		        	predictionNames.add(pi.getJobName());	        
		        }
			}
	       
	        if(allUserPredictions != null){
		    	Iterator j = allUserPredictions.iterator();
		    	while(j.hasNext()){
		    		PredictionJob pj = (PredictionJob) j.next();
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
	public static List populatePredictors(String userName, boolean isAllUserIncludes, boolean onlySaved) throws HibernateException, ClassNotFoundException, SQLException{
		
 		List<Predictor> predictors = new ArrayList();
 		List privatePredictors = null;
 		Session session = HibernateUtil.getSession();
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
 			session.close();
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
 			session.close();
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
 			session.close();
 		}
 		predictors.addAll(DrugDiscoveryPredictors);
     
		return predictors;
	}
	

	@SuppressWarnings("unchecked")
	public static List populatePredictions(String userName, boolean onlySaved) {
		
		List<PredictionJob> predictions = null;
		try 
		{
			//Utility.writeToDebug("Populating a list of the saved prediction results.", userName, "null");
			Session session = HibernateUtil.getSession();
			Transaction tx = null;
			try 
			{
				tx = session.beginTransaction();
				if(onlySaved) predictions = session.createCriteria(PredictionJob.class)
							.add(Expression.or(Expression.eq("userName", userName),Expression.eq("userName", Constants.ALL_USERS_USERNAME)))
							.add(Expression.eq("status","saved")).list();
				else predictions = session.createCriteria(PredictionJob.class)
				.add(Expression.or(Expression.eq("userName", userName),Expression.eq("userName", Constants.ALL_USERS_USERNAME)))
				.list();
				tx.commit();
			} catch (RuntimeException e) {
				Utility.writeToDebug(e);
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {
				session.close();
			}

			
			for (PredictionJob p : predictions) {
				p.setPredictorName(getPredictor(p.getPredictorId()));

				p.setDatabase(Utility.wrapFileName(p.getDatabase()));
			}
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		return predictions;
	}

	
	
	
	protected static String getPredictor(Long predictorIdUsed) throws ClassNotFoundException, SQLException {

		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			predictor = (Predictor) session.createCriteria(Predictor.class)
					.add(Expression.eq("predictorId", predictorIdUsed))
					.uniqueResult();

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		return predictor.getName();
	}
	
	public static String getSdfFileForDataset(String datasetName, String userName) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}

		return dataset.getSdfFile();
	}
	
	public static DataSet getDataSetByName(String datasetName, String userName) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}
		return dataset;
	}
	
	public static DataSet getDataSetById(Long fileId) throws ClassNotFoundException, SQLException {
		DataSet dataset = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}
		
		return dataset;
	}

	public static Predictor getPredictorById(Long predictorId) throws ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}
		return predictor;
	}
	
	public static Predictor getPredictorByName(String selectedPredictorName, String user)	throws ClassNotFoundException, SQLException {
		Predictor predictor = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}

		return predictor;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> populateTaskNames(String userName, boolean justRunning) {
		
		List<String> taskNames = new ArrayList<String>();
		List<QueueTask> tasks = null;
		try 
		{
			Session session = HibernateUtil.getSession();
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
				session.close();
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
	public static List populateTasks(String userName, boolean justRunning) {
		
		List<QueueTask> tasks = null;
		try 
		{
			Session session = HibernateUtil.getSession();
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
				session.close();
			}
			
		} catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		return tasks;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Long> getPredictionTasksIdByDatasetId(Long id) throws HibernateException, ClassNotFoundException, SQLException{
		List<PredictionTask> pTasks = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			pTasks = session.createCriteria(PredictionTask.class)
					.add(Expression.eq("datasetId", id))
					.list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		if(pTasks!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Iterator<PredictionTask> i = pTasks.iterator();i.hasNext();){
				ids.add(i.next().getId());
			}
			return ids;
		}
		else return null;
	}
	
	public static QueueTask getTaskById(Long id) throws HibernateException, ClassNotFoundException, SQLException{
		QueueTask task = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}
		if(task!=null) return task;
		else return null;
	}

	@SuppressWarnings("unchecked")
	public static List<Long> getModelingTasksIdByDatasetId(Long id) throws HibernateException, ClassNotFoundException, SQLException {
		List<ModellingTask> mTasks = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			mTasks = session.createCriteria(ModellingTask.class)
					.add(Expression.eq("datasetId", id))
					.list();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		if(mTasks!=null){
			List<Long> ids = new ArrayList<Long>();
			for(Iterator<ModellingTask> i = mTasks.iterator();i.hasNext();){
				ids.add(i.next().getId());
			}
			return ids;
		}
		else return null;
	}
	
	public static PredictionTask getPredictionTaskById(Long id) throws HibernateException, ClassNotFoundException, SQLException{
		PredictionTask pTask = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}
		if(pTask!=null) return pTask;
		else return null;
	}

	public static ModellingTask getModelingTaskById(Long id) throws HibernateException, ClassNotFoundException, SQLException {
		ModellingTask mTask = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			mTask = (ModellingTask) session.createCriteria(ModellingTask.class)
					.add(Expression.eq("id", id))
					.uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		if(mTask!=null) return mTask;
		else return null;
	}

	public static VisualizationTask getVisualizationTaskById(Long id) throws HibernateException, ClassNotFoundException, SQLException {
		VisualizationTask vTask = null;
		Session session = HibernateUtil.getSession();
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
			session.close();
		}
		if(vTask!=null) return vTask;
		else return null;
	}
}
