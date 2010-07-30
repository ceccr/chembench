package edu.unc.ceccr.taskObjects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

	ArrayList<PredictionValue> allPredValues = new ArrayList<PredictionValue>();
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
	
	public String getProgress() {
		
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
					ArrayList<String> selectedPredictorIds = (ArrayList<String>) Arrays.asList(selectedPredictorIdArray);
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
					File predOutFile = new File(filePath + selectedPredictorNames.get(i) + "/" + Constants.PRED_OUTPUT_FILE + "_vs_" + predictionDataset.getSdfFile().toLowerCase() + ".renorm.preds");
					if(predOutFile.exists()){
						//quickly count the number of lines in the output file for this predictor
						InputStream is = new BufferedInputStream(new FileInputStream(predOutFile));
					    byte[] c = new byte[1024];
					    int count = 0;
					    int readChars = 0;
					    while ((readChars = is.read(c)) != -1) {
					        for (int j = 0; j < readChars; ++j) {
					            if (c[j] == '\n')
					                ++count;
					        }
					    }
					    modelsPredictedSoFar += count - 4; //there are 4 header lines 
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
			Utility.writeToDebug(ex);
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
		
	}
	
	public QsarPredictionTask(Prediction prediction){
		this.prediction = prediction;
		Long fileId = prediction.getDatasetId();
		try{
			Session session = HibernateUtil.getSession();
			this.predictionDataset = PopulateDataObjects.getDataSetById(fileId, session);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		this.jobName = prediction.getJobName();
		this.userName = prediction.getUserName();
		this.sdf = predictionDataset.getSdfFile();
		this.cutoff = "" + prediction.getSimilarityCutoff();
		this.selectedPredictorIds = prediction.getPredictorIds();
		this.filePath = Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/";
		
	}

	public void setUp() throws Exception {
		//create Prediction object in DB to allow for recovery of this job if it fails.
		
		if(prediction == null){
			prediction = new Prediction();
		}
		
		prediction.setDatabase(this.sdf);
		prediction.setUserName(this.userName);
		prediction.setSimilarityCutoff(new Float(this.cutoff));
		prediction.setPredictorIds(this.selectedPredictorIds);
		prediction.setJobName(this.jobName);
		prediction.setDatasetId(predictionDataset.getFileId());
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
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		
		lookupId = prediction.getPredictionId();
		jobType = Constants.PREDICTION;
		
		
		
		Utility.writeToDebug("Setting up prediction task", userName, jobName);
		try{
			new File(Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName).mkdir();
			
			if(predictionDataset.getUserName().equals(userName)){
				FileAndDirOperations.copyFile(
						Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/"+predictionDataset.getFileName()+"/"+sdf, 
						Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/"+sdf
						);
			}
			else{
				FileAndDirOperations.copyFile(
						Constants.CECCR_USER_BASE_PATH + "all-users" + "/DATASETS/"+predictionDataset.getFileName()+"/"+sdf, 
						Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/"+sdf
						);
			}			
		}
		catch(Exception e){
			Utility.writeToDebug(e);
		}
	}

	public void preProcess() throws Exception {

		Session s = HibernateUtil.getSession();
		
		selectedPredictors = new ArrayList<Predictor>();
		String[] selectedPredictorIdArray = selectedPredictorIds.split("\\s+");

		for(int i = 0; i < selectedPredictorIdArray.length; i++){
			Predictor selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorIdArray[i]), s);
			
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
				Utility.writeToDebug(e);
			}
			
			selectedPredictors.add(selectedPredictor);
		}

		
		//Now, make the prediction with each predictor. 
		//First, copy dataset into jobDir. 
		
		step = Constants.SETUP;
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String sdfile = predictionDataset.getSdfFile();
		
		GetJobFilesWorkflow.getDatasetFiles(userName, predictionDataset, path);
		
		if(jobList.equals(Constants.LSF)){
			//move files out to LSF
		}
	}
	
	public String executeLSF() throws Exception {
		return "";
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
		//basically the workflow will need to be written into a shell script the LSF can execute
		
		//for each predictor do {
		for(int i = 0; i < selectedPredictors.size(); i++){
			Predictor selectedPredictor = selectedPredictors.get(i);
			
			//	2. copy predictor into jobDir/predictorDir
			
			String predictionDir = path + selectedPredictor.getName() + "/";
			new File(predictionDir).mkdirs();
			
			step = Constants.COPYPREDICTOR;
			GetJobFilesWorkflow.getPredictorFiles(userName, selectedPredictor, predictionDir);

			//  done with 2. (copy predictor into jobDir/predictorDir)
			
			//	3. copy dataset from jobDir to jobDir/predictorDir. Scale descriptors to fit predictor.
			FileAndDirOperations.copyDirContents(path, predictionDir, false);
			ArrayList<String> descriptorNames = new ArrayList<String>();
			ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
			ArrayList<String> chemicalNames = DatasetFileOperations.getSDFCompoundNames(path + sdfile);
			
			step = Constants.PROCDESCRIPTORS;
			
			if(selectedPredictor.getDescriptorGeneration().equals(Constants.MOLCONNZ)){
				Utility.writeToDebug("ExecutePredictor: Processing MolconnZ Descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMolconnZDescriptors(predictionDir + sdfile + ".molconnz", descriptorNames, descriptorValueMatrix);
			}
			else if(selectedPredictor.getDescriptorGeneration().equals(Constants.DRAGONH)){
				Utility.writeToDebug("ExecutePredictor: Processing DragonH Descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readDragonDescriptors(predictionDir + sdfile + ".dragonH", descriptorNames, descriptorValueMatrix);
			}
			else if(selectedPredictor.getDescriptorGeneration().equals(Constants.DRAGONNOH)){
				Utility.writeToDebug("ExecutePredictor: Processing DragonNoH Descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readDragonDescriptors(predictionDir + sdfile + ".dragonNoH", descriptorNames, descriptorValueMatrix);
			}
			else if(selectedPredictor.getDescriptorGeneration().equals(Constants.MOE2D)){
				Utility.writeToDebug("ExecutePredictor: Processing Moe2D Descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMoe2DDescriptors(predictionDir + sdfile + ".moe2D", descriptorNames, descriptorValueMatrix);
			}
			else if(selectedPredictor.getDescriptorGeneration().equals(Constants.MACCS)){
				Utility.writeToDebug("ExecutePredictor: Processing MACCS Descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMaccsDescriptors(predictionDir + sdfile + ".maccs", descriptorNames, descriptorValueMatrix);
			}
			
			String descriptorString = Utility.StringArrayListToString(descriptorNames);
			WriteDescriptorsFileWorkflow.writePredictionXFile(
					chemicalNames, 
					descriptorValueMatrix, 
					descriptorString, 
					predictionDir + sdfile + ".renorm.x", 
					predictionDir + "train_0.x", 
					selectedPredictor.getScalingType());
			
			//  done with 3. (copy dataset from jobDir to jobDir/predictorDir. Scale descriptors to fit predictor.)
			
			//	4. make predictions in jobDir/predictorDir

			step = Constants.PREDICTING;
			Utility.writeToDebug("ExecutePredictor: Making predictions", userName, jobName);
			
			if(selectedPredictor.getModelMethod().equals(Constants.KNN)){
				KnnPredictionWorkflow.RunKnnPlusPrediction(userName, jobName, predictionDir, sdfile, Float.parseFloat(cutoff) );
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.SVM)){
				SvmWorkflow.runSvmPrediction();
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.KNNGA) || 
					selectedPredictor.getModelMethod().equals(Constants.KNNSA)){
				KnnPlusWorkflow.runKnnPlusPrediction(predictionDir, sdfile, cutoff);
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.RANDOMFOREST)){
				RandomForestWorkflow.runRandomForestPrediction(predictionDir, jobName, sdfile, selectedPredictor);
			}
			//  done with 4. (make predictions in jobDir/predictorDir)
			
			//	5. get output, put it into predictionValue objects and save them
			
			step = Constants.READPRED;
			
			ArrayList<PredictionValue> predValues = null;
			if(selectedPredictor.getModelMethod().equals(Constants.KNN)){
				predValues = KnnPredictionWorkflow.readPredictionOutput(predictionDir, selectedPredictor.getPredictorId(), sdfile);
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.SVM)){
				predValues = SvmWorkflow.readPredictionOutput(predictionDir, selectedPredictor.getPredictorId());
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.KNNGA) ||
					selectedPredictor.getModelMethod().equals(Constants.KNNSA)){
				predValues = KnnPlusWorkflow.readPredictionOutput(predictionDir, selectedPredictor.getPredictorId(), sdfile);
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.RANDOMFOREST)){
				predValues = RandomForestWorkflow.readPredictionOutput(predictionDir, selectedPredictor.getPredictorId());
			}
			
			//ArrayList<PredictionValue> predValues = parsePredOutput(predictionDir + Constants.PRED_OUTPUT_FILE, selectedPredictor.getPredictorId());
			Utility.writeToDebug("ExecPredictorActionTask: Complete", userName, jobName);
			
			if(predValues != null){
				allPredValues.addAll(predValues);
			}
			//  done with 5. (get output, put it into predictionValue objects and save them)
			
		}
		//}
		
		
	}
	
	public void postProcess() throws Exception {

		if(jobList.equals(Constants.LSF)){
			//move files back from LSF
		}
		
		KnnPredictionWorkflow.MoveToPredictionsDir(userName, jobName);
		
		try{

			if(this.allPredValues == null){
				Utility.writeToDebug("Warning: allPredValue is null.");
			}
			else{
				Utility.writeToDebug("Saving prediction to database.");
			}
			
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
				Utility.writeToDebug(e);
			}
			
			try {
				tx = session.beginTransaction();
				for (PredictionValue predOutput : this.allPredValues){
					predOutput.setPredictionId(prediction.getPredictionId());
					session.saveOrUpdate(predOutput);
				}		
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {
				session.close();
			}

			File dir=new File(Constants.CECCR_USER_BASE_PATH+this.userName+"/"+this.jobName+"/");
			FileAndDirOperations.deleteDir(dir);
			
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
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
