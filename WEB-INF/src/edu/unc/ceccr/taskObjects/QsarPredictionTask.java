package edu.unc.ceccr.taskObjects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.ConvertDescriptorsToXAndScaleWorkflow;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.GenerateDescriptorWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnPlusWorkflow;
import edu.unc.ceccr.workflows.KnnPredictionWorkflow;
import edu.unc.ceccr.workflows.RandomForestWorkflow;
import edu.unc.ceccr.workflows.ReadDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.SvmWorkflow;
import edu.unc.ceccr.workflows.WriteDescriptorsFileWorkflow;

public class QsarPredictionTask extends WorkflowTask {

	private String filePath;
	private String jobName;
	private String sdf;
	private String cutoff;
	private String userName;
	private String selectedPredictorIds;
	private DataSet predictionDataset;
	private String step = Constants.SETUP; //stores what step we're on 
	private int allPredsTotalModels = -1; //used by getProgress function
	private ArrayList<String> selectedPredictorNames = new ArrayList<String>(); //used by getProgress function
	private Prediction prediction;

	//for internal use only
	ArrayList<Predictor> selectedPredictors = null; 
	
	public String getProgress(String userName) {
		
		try{
			if(! step.equals(Constants.PREDICTING)){
				return step; 
			}
			else{
				//get the % done of the overall prediction

				if(allPredsTotalModels < 0){
					//we haven't read the needed predictor data yet
					//get the number of models in all predictors, and their names
					Session s = HibernateUtil.getSession();
					allPredsTotalModels = 0;
					String[] selectedPredictorIdArray = selectedPredictorIds.split("\\s+");
					ArrayList<String> selectedPredictorIds = new ArrayList<String>(Arrays.asList(selectedPredictorIdArray));
					Collections.sort(selectedPredictorIds);
					for(int i = 0; i < selectedPredictorIds.size(); i++){
						Predictor selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorIds.get(i)), s);
						allPredsTotalModels += selectedPredictor.getNumTestModels();
						selectedPredictorNames.add(selectedPredictor.getName());
					}
					
					s.close();
				}

				float modelsPredictedSoFar = 0;
				for(int i = 0; i < selectedPredictorNames.size(); i++){
					File predOutFile = null; 
					
					if(predictionDataset.getDatasetType().equals(Constants.PREDICTION) || 
							predictionDataset.getDatasetType().equals(Constants.MODELING)){
						predOutFile = new File(filePath + selectedPredictorNames.get(i) + "/" + Constants.PRED_OUTPUT_FILE + "_vs_" + predictionDataset.getSdfFile().toLowerCase() + ".renorm.preds");
					}
					else if(predictionDataset.getDatasetType().equals(Constants.PREDICTIONWITHDESCRIPTORS) || 
							predictionDataset.getDatasetType().equals(Constants.MODELINGWITHDESCRIPTORS)){
						predOutFile = new File(filePath + selectedPredictorNames.get(i) + "/" + Constants.PRED_OUTPUT_FILE + "_vs_" + predictionDataset.getXFile().toLowerCase() + ".renorm.preds");
					}
					
					if(predOutFile.exists()){
						//quickly count the number of lines in the output file for this predictor
						//there are 4 header lines 
						modelsPredictedSoFar += FileAndDirOperations.getNumLinesInFile(predOutFile.getAbsolutePath()) - 4; 
					}
					else{
						//SVM will just have a bunch of files ending in ".pred". Count them to get progress.
						try{
							File dir = new File(filePath + selectedPredictorNames.get(i) + "/");
							modelsPredictedSoFar += (dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {
								return arg1.endsWith(".pred");}}).length);
						}
						catch(Exception ex){
							//whatever...
						}
					}
					
				}
				if(allPredsTotalModels == 0){
					return Constants.PREDICTING; //missing database information, probably
				}
				float progress = modelsPredictedSoFar / allPredsTotalModels;
				progress *= 100; //it's a percent
				return step + " (" + Math.round(progress) + "%)"; 
			}
			
		}catch(Exception ex){
			Utility.writeToDebug(ex, userName, jobName);
			return "";
		}
	}
		
	public QsarPredictionTask(String userName, String jobName, String sdf, String cutoff,
			String selectedPredictorIds, DataSet predictionDataset) throws Exception {
		this.predictionDataset = predictionDataset;
		this.jobName = jobName;
		this.userName = userName;
		this.sdf = sdf;
		this.cutoff = cutoff;
		this.selectedPredictorIds = selectedPredictorIds;
		this.filePath = Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/";
		prediction = new Prediction();

		Session s = HibernateUtil.getSession();
		
		selectedPredictors = new ArrayList<Predictor>();
		String[] selectedPredictorIdArray = selectedPredictorIds.split("\\s+");

		for(int i = 0; i < selectedPredictorIdArray.length; i++){
			Predictor p = PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorIdArray[i]), s);
			selectedPredictors.add(p);
		}
		Collections.sort(selectedPredictors, new Comparator<Predictor>(){
			public int compare(Predictor p1, Predictor p2) {
	    		return p1.getId().compareTo(p2.getId());
		    }});

		s.close();
	}
	
	public QsarPredictionTask(Prediction prediction) throws Exception{
		//used when job is recovered on server restart
		
		this.prediction = prediction;
		Long datasetId = prediction.getDatasetId();
		try{
			Session session = HibernateUtil.getSession();
			this.predictionDataset = PopulateDataObjects.getDataSetById(datasetId, session);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex, userName, jobName);
		}
		this.jobName = prediction.getName();
		this.userName = prediction.getUserName();
		if(predictionDataset.getSdfFile() != null){
			this.sdf = predictionDataset.getSdfFile();
		}
		this.cutoff = "" + prediction.getSimilarityCutoff();
		this.selectedPredictorIds = prediction.getPredictorIds();
		this.filePath = Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/";
		
		
		Session s = HibernateUtil.getSession();
		
		selectedPredictors = new ArrayList<Predictor>();
		String[] selectedPredictorIdArray = selectedPredictorIds.split("\\s+");

		//load list of predictors. Remove any predictors that have already completed their predictions.
		for(int i = 0; i < selectedPredictorIdArray.length; i++){
			Predictor p = PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorIdArray[i]), s);
			
			PredictionValue pvalue = PopulateDataObjects.getFirstPredictionValueByPredictionIdAndPredictorId(prediction.getId(),
					p.getId(), s);
			if(pvalue == null){
				selectedPredictors.add(p);
			}
		}
		Collections.sort(selectedPredictors, new Comparator<Predictor>(){
			public int compare(Predictor p1, Predictor p2) {
	    		return p1.getId().compareTo(p2.getId());
		    }});
		
		s.close();
	}

	public Long setUp() throws Exception {
		//create Prediction object in DB to allow for recovery of this job if it fails.
		
		if(prediction == null){
			prediction = new Prediction();
		}
		
		prediction.setDatabase(this.sdf);
		prediction.setUserName(this.userName);
		prediction.setSimilarityCutoff(new Float(this.cutoff));
		prediction.setPredictorIds(this.selectedPredictorIds);
		prediction.setName(this.jobName);
		prediction.setDatasetId(predictionDataset.getId());
		prediction.setHasBeenViewed(Constants.NO);
		prediction.setJobCompleted(Constants.NO);

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(prediction);		
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e, userName, jobName);
		} finally {
			session.close();
		}
		
		lookupId = prediction.getId();
		jobType = Constants.PREDICTION;
		
		Utility.writeToDebug("Setting up prediction task", userName, jobName);
		try{
			new File(Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName).mkdir();
			
			if(predictionDataset.getUserName().equals(userName)){
				
				if(sdf != null && !sdf.isEmpty()){
					FileAndDirOperations.copyFile(
							Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/"+predictionDataset.getName()+"/"+sdf, 
							Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/"+sdf
							);
					
				}
				if(predictionDataset.getXFile() != null && ! predictionDataset.getXFile().isEmpty()){
					FileAndDirOperations.copyFile(
							Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/"+predictionDataset.getName()+"/"+predictionDataset.getXFile(), 
							Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/"+predictionDataset.getXFile()
							);
				}
			}
			else{
				//public datasets always have SDFs
				FileAndDirOperations.copyFile(
						Constants.CECCR_USER_BASE_PATH + "all-users" + "/DATASETS/"+predictionDataset.getName()+"/"+sdf, 
						Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/"+sdf
						);
			}			
		}
		catch(Exception e){
			Utility.writeToDebug(e, userName, jobName);
		}
		
		return lookupId;
	}

	public void preProcess() throws Exception {

		Session s = HibernateUtil.getSession();
		
		for(int i = 0; i < selectedPredictors.size(); i++){
			Predictor selectedPredictor = selectedPredictors.get(i);
			
			//We're keeping a count of how many times each predictor was used.
	        //So, increment number of times used on each and save each predictor object.
			
	        selectedPredictor.setNumPredictions(selectedPredictor.getNumPredictions() + 1);
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				s.saveOrUpdate(selectedPredictor);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e, userName, jobName);
			}
		}

		//Now, make the prediction with each predictor. 
		//First, copy dataset into jobDir. 
		
		step = Constants.SETUP;
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String sdfile = predictionDataset.getSdfFile();
		
		GetJobFilesWorkflow.getDatasetFiles(userName, predictionDataset, Constants.PREDICTION, path);
		
		if(jobList.equals(Constants.LSF)){
			//move files out to LSF
		}
	}
	
	public String executeLSF() throws Exception {
		return "";
	}
	
	private ArrayList<PredictionValue> makePredictions(Predictor predictor, String sdfile, String basePath, String datasetPath) throws Exception{
		
		ArrayList<PredictionValue> predValues = null;
		String predictionDir = basePath + predictor.getName() + "/";

		Session s = HibernateUtil.getSession();
		ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(predictor, s);
		s.close();
		
		if(childPredictors.size() > 0){
			//recurse. Call this function for each childPredictor (if there are any).
			ArrayList<ArrayList<PredictionValue>> childResults = new ArrayList<ArrayList<PredictionValue>>();
			for(Predictor childPredictor : childPredictors){
				childResults.add(makePredictions(childPredictor, sdfile, predictionDir, datasetPath));
			}
			
			//average the results from the child predictions and return them
			//assumes that all children return results of the same size, i.e., 
			//they all have the same number of compounds that they attempt to predict.
			predValues = new ArrayList<PredictionValue>();
			
			ArrayList<PredictionValue> firstChildResults = childResults.get(0);
			for(PredictionValue pv : firstChildResults){
				PredictionValue parentPredictionValue = new PredictionValue();
				parentPredictionValue.setCompoundName(pv.getCompoundName());
				parentPredictionValue.setNumModelsUsed(childResults.size());
				parentPredictionValue.setNumTotalModels(childResults.size());
				parentPredictionValue.setObservedValue(pv.getObservedValue());
				parentPredictionValue.setPredictorId(predictor.getId());
				predValues.add(parentPredictionValue);
			}
			//calculate average predicted value and stddev over each child
			for(int i = 0; i < firstChildResults.size(); i++){
				SummaryStatistics compoundPredictedValues = new SummaryStatistics();
				for(ArrayList<PredictionValue> childResult: childResults){
					if(childResult.get(i).getPredictedValue() != null){
						compoundPredictedValues.addValue(childResult.get(i).getPredictedValue());
					}
				}
				if(! Double.isNaN(compoundPredictedValues.getMean())){
					predValues.get(i).setPredictedValue(new Float(compoundPredictedValues.getMean()));
					predValues.get(i).setStandardDeviation(new Float(compoundPredictedValues.getStandardDeviation()));
				}
			}
			
			//commit predValues to DB
			s = HibernateUtil.getSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				for(PredictionValue pv : predValues){
					pv.setPredictionId(prediction.getId());
					s.saveOrUpdate(pv);
				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e, userName, jobName);
			} finally {
				s.close();
			}
			return predValues;
		}
		else{
			//no child predictors, so just make a prediction
			
			//2. copy predictor into jobDir/predictorDir
			new File(predictionDir).mkdirs();
			
			step = Constants.COPYPREDICTOR;
			GetJobFilesWorkflow.getPredictorFiles(userName, predictor, predictionDir);
	
			//  done with 2. (copy predictor into jobDir/predictorDir)
			
			//	3. copy dataset from jobDir to jobDir/predictorDir. Scale descriptors to fit predictor.
			FileAndDirOperations.copyDirContents(datasetPath, predictionDir, false);
			
			if(predictor.getDescriptorGeneration().equals(Constants.UPLOADED)){
				//the prediction descriptors file name is different if the user provided a .x file.
				sdfile = predictionDataset.getXFile();
			}
			
			step = Constants.PROCDESCRIPTORS;
			
			ConvertDescriptorsToXAndScaleWorkflow.convertDescriptorsToXAndScale(predictionDir,
					sdfile, "train_0.x", sdfile + ".renorm.x", 
					predictor.getDescriptorGeneration(), predictor.getScalingType(), 
					predictionDataset.getNumCompound());
			
			//  done with 3. (copy dataset from jobDir to jobDir/predictorDir. Scale descriptors to fit predictor.)
			
			//	4. make predictions in jobDir/predictorDir
	
			step = Constants.PREDICTING;
			Utility.writeToDebug("ExecutePredictor: Making predictions", userName, jobName);
			
			if(predictor.getModelMethod().equals(Constants.KNN)){
				KnnPredictionWorkflow.RunKnnPlusPrediction(userName, jobName, predictionDir, sdfile, Float.parseFloat(cutoff) );
			}
			else if(predictor.getModelMethod().equals(Constants.SVM)){
				SvmWorkflow.runSvmPrediction(predictionDir, sdfile + ".renorm.x");
			}
			else if(predictor.getModelMethod().equals(Constants.KNNGA) || 
					predictor.getModelMethod().equals(Constants.KNNSA)){
				KnnPlusWorkflow.runKnnPlusPrediction(predictionDir, sdfile, cutoff);
			}
			else if(predictor.getModelMethod().equals(Constants.RANDOMFOREST)){
				RandomForestWorkflow.runRandomForestPrediction(predictionDir, jobName, sdfile, predictor);
			}
			//  done with 4. (make predictions in jobDir/predictorDir)
			
			//	5. get output, put it into predictionValue objects and save them
			
			step = Constants.READPRED;
			
			if(predictor.getModelMethod().equals(Constants.KNN)){
				predValues = KnnPredictionWorkflow.readPredictionOutput(predictionDir, predictor.getId(), sdfile);
			}
			else if(predictor.getModelMethod().equals(Constants.SVM)){
				predValues = SvmWorkflow.readPredictionOutput(predictionDir, sdfile + ".renorm.x", predictor.getId());
			}
			else if(predictor.getModelMethod().equals(Constants.KNNGA) ||
					predictor.getModelMethod().equals(Constants.KNNSA)){
				predValues = KnnPlusWorkflow.readPredictionOutput(predictionDir, predictor.getId(), sdfile + ".renorm.x");
			}
			else if(predictor.getModelMethod().equals(Constants.RANDOMFOREST)){
				predValues = RandomForestWorkflow.readPredictionOutput(predictionDir, predictor.getId());
			}
			
			s = HibernateUtil.getSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				for(PredictionValue pv : predValues){
					pv.setPredictionId(prediction.getId());
					s.saveOrUpdate(pv);
				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e, userName, jobName);
			} finally {
				s.close();
			}
			
			//  done with 5. (get output, put it into predictionValue objects and save them)
			
			//remove copied dataset and predictor; they are redundant
			String[] datasetDirFiles = new File(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + predictionDataset.getName() + "/").list();
			String[] datasetDescDirFiles = new File(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + predictionDataset.getName() + "/Descriptors/").list();
			String[] predictorDirFiles = new File(Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + predictor.getName() + "/").list();
			try{
				for(String fileName : datasetDirFiles){
					if(new File(predictionDir + fileName).exists()){
						FileAndDirOperations.deleteFile(predictionDir + fileName);
					}
				}
				for(String fileName : datasetDescDirFiles){
					if(new File(predictionDir + fileName).exists()){
						FileAndDirOperations.deleteFile(predictionDir + fileName);
					}
				}
				for(String fileName : predictorDirFiles){
					if(new File(predictionDir + fileName).exists()){
							FileAndDirOperations.deleteFile(predictionDir + fileName);
					}
				}
			}
			catch(Exception ex){
				Utility.writeToDebug(ex, userName, jobName);
			}
		}
		return predValues;
	}
	
	public void executeLocal() throws Exception {

		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String sdfile = predictionDataset.getSdfFile();
		
		//Workflow for this section will be:
		//for each predictor do {
		//	2. copy predictor into jobDir/predictorDir
		//	3. copy dataset from jobDir to jobDir/predictorDir. Scale descriptors to fit predictor.
		//	4. make predictions in jobDir/predictorDir
		//	5. get output, put it into predictionValue objects and save them
		//}
		
		//this is gonna need some major changes if we ever want it to work with LSF.
		//basically the workflow will need to be written into a shell script that LSF can execute
		
		if(predictionDataset.getNumCompound() > 10000){
			//We will probably run out of memory if we try to process this job in Java. 
			Utility.writeToDebug("WARNING: Prediction set too large!", userName, jobName);
		}

		//for each predictor do {
		for(int i = 0; i < selectedPredictors.size(); i++){
			Predictor predictor = selectedPredictors.get(i);
			makePredictions(predictor, sdfile, path, path);
		}
		
		//remove prediction dataset descriptors from prediction output dir;
		//they are not needed
		try{
			String[] baseDirFiles = new File(Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/").list();
			for(String fileName : baseDirFiles){
				if(new File(Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/" + fileName).exists()){
					FileAndDirOperations.deleteFile(Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/" + fileName);
				}
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex, userName, jobName);
		}
	}
	
	public void postProcess() throws Exception {

		if(jobList.equals(Constants.LSF)){
			//move files back from LSF
		}
		
		KnnPredictionWorkflow.MoveToPredictionsDir(userName, jobName);
		
		try{

			
			prediction.setJobCompleted(Constants.YES);
			prediction.setStatus("saved");

			Session session = HibernateUtil.getSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				session.saveOrUpdate(prediction);		
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e, userName, jobName);
			}
			
			File dir=new File(Constants.CECCR_USER_BASE_PATH+this.userName+"/"+this.jobName+"/");
			FileAndDirOperations.deleteDir(dir);
			
			}
			catch(Exception ex){
				Utility.writeToDebug(ex, userName, jobName);
			}
	}
	
	public void delete() throws Exception {
		
	}

	public String getStatus(){
		return step;
	}
	
	
	//helpers below this point.
	
	protected static Predictor getPredictor(
			Long selectedPredictorId) throws ClassNotFoundException,
			SQLException {

		Predictor pred = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			pred = (Predictor) session.createCriteria(Predictor.class).add(Expression.eq("predictorId", selectedPredictorId)).uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		return pred;
	}
	
	private static PredictionValue createPredObject(String[] extValues) {

		if (extValues == null) {
			return null;
		}
		int arraySize = extValues.length;
		
		PredictionValue predOutput = new PredictionValue();
		predOutput.setCompoundName(extValues[0]);
		try{
			predOutput.setNumModelsUsed(Integer.parseInt(extValues[1]));
			predOutput.setPredictedValue(Float.parseFloat(extValues[2]));
			if (arraySize > 3){
				predOutput.setStandardDeviation(Float.parseFloat(extValues[3]));
			}
		}
		catch(Exception ex){
			//if it couldn't get the information, then there is no prediction for this compound.
			//Don't worry about the NumberFormatException, it doesn't matter.
		}
		
		return predOutput;

	}
	
    @SuppressWarnings("unchecked")
	public static ArrayList<PredictionValue> parsePredOutput(String fileLocation, Long predictorId) throws IOException {
		Utility.writeToDebug("Reading prediction output from " + fileLocation);
		ArrayList<PredictionValue> allPredValue = new ArrayList<PredictionValue>();
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileLocation));
			String inputString;
	
			//skip all the non-blank lines with junk in them
			while (!(inputString = in.readLine()).equals(""))
				;
			//now skip some blank lines
			while ((inputString = in.readLine()).equals(""))
				;
			//now we're at the data we need
			do {
				String[] predValues = inputString.split("\\s+");
				PredictionValue extValOutput = createPredObject(predValues);
				extValOutput.setPredictorId(predictorId);
				allPredValue.add(extValOutput);
			} while ((inputString = in.readLine()) != null);
		} catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		if(allPredValue == null){
			Utility.writeToDebug("Warning: parsePredOutput returned null.");
		}
		return allPredValue;
	}

    public void setStep(String step){
    	this.step = step;
    }
	
	public String getJobName() {
		return jobName;
	}
	
	public DataSet getPredictionDataset() {
		return predictionDataset;
	}
	public void setPredictionDataset(DataSet predictionDataset) {
		this.predictionDataset = predictionDataset;
	}

}
