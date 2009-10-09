package edu.unc.ceccr.taskObjects;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.Constants.DescriptorEnumeration;
import edu.unc.ceccr.global.Constants.ScalingTypeEnumeration;
import edu.unc.ceccr.outputObjects.Pred_Output;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.GenerateDescriptorWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnPredictionWorkflow;
import edu.unc.ceccr.workflows.ReadDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.WriteDescriptorsFileWorkflow;

public class QsarPredictionTask implements WorkflowTask {

	private String filePath;
	private ArrayList<Pred_Output> allPredValue;
	private String jobName;
	private String sdf;
	private String cutoff;
	private String userName;
	private String selectedPredictorIds;
	private DataSet predictionDataset;
	private String step = Constants.SETUP; //stores what step we're on 
	
	public String getProgress(){
		String percent = "";
		if(step.equals(Constants.PREDICTING)){
			//count the number of *.pred files in the working directory
			float p = FileAndDirOperations.countFilesInDirMatchingPattern(filePath, ".*pred");
			//divide by the number of compounds in the dataset
			p /= predictionDataset.getNumCompound();
			p *= 100; //it's a percent
			percent = " (" + Math.round(p) + "%)"; 
		}
		return step + percent;
	}
		
	public QsarPredictionTask(String userName, String jobName, String sdf, String cutoff,
			String selectedPredictorIds, DataSet predictionDataset) throws Exception {
		
		Utility.writeToMSDebug("Start ExecPredictorActionTask Constructor");
		this.predictionDataset = predictionDataset;
		this.jobName = jobName;
		this.userName = userName;
		this.sdf = sdf;
		this.cutoff = cutoff;
		this.selectedPredictorIds = selectedPredictorIds;
		this.filePath = Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/";
		
		Utility.writeToMSDebug("Finish QsarPredictionTask Constructor");
	}

	public void execute() throws Exception {

		Utility.writeToDebug("QsarPredictionTask: ExecutePredictor",userName,jobName);
		Utility.writeToMSDebug("QsarPredictionTask: Start"+userName+" "+jobName);

		Session s = HibernateUtil.getSession();
		
		ArrayList<Predictor> selectedPredictors = new ArrayList<Predictor>();
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
			} finally {
				s.close();
			}

			selectedPredictors.add(selectedPredictor);
		}

		
		//Right... Now, make the prediction with each predictor. Why am I doing it this way..?
		step = Constants.SETUP;
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		
		String datasetPath = Constants.CECCR_USER_BASE_PATH;
		if(predictionDataset.getUserName().equalsIgnoreCase("_all")){
			datasetPath += "all-users";
		}
		else{
			datasetPath += predictionDataset.getUserName();
		}
		datasetPath += "/DATASETS/" + predictionDataset.getFileName() + "/";
		
		boolean predictorIsAllUser = selectedPredictor.getUserName().equals(Constants.ALL_USERS_USERNAME);
		boolean sdfIsAllUser = predictionDataset.getUserName().equals(Constants.ALL_USERS_USERNAME);
		String sdfile = predictionDataset.getSdfFile();
		
		step = Constants.COPYPREDICTOR;
		GetJobFilesWorkflow.GetKnnPredictionFiles(userName, jobName, sdfile, sdfIsAllUser, predictorIsAllUser, selectedPredictor.getName(), predictionDataset.getFileName());

		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//create the descriptors for the dataset and read them in
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		ArrayList<String> chemicalNames = DatasetFileOperations.getSDFCompoundList(path + sdfile);
		
		if(selectedPredictor.getDescriptorGeneration().equals(DescriptorEnumeration.MOLCONNZ)){
			step = Constants.DESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Generating MolconnZ Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(path + sdfile, path + sdfile + ".S");
			
			step = Constants.PROCDESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Processing MolconnZ Descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readMolconnZDescriptors(path + sdfile + ".S", descriptorNames, descriptorValueMatrix);
		}
		else if(selectedPredictor.getDescriptorGeneration().equals(DescriptorEnumeration.DRAGON)){
			step = Constants.DESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Generating Dragon Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateDragonDescriptors(path + sdfile, path + sdfile + ".dragon");
			
			step = Constants.PROCDESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Processing Dragon Descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readDragonDescriptors(path + sdfile + ".dragon", descriptorNames, descriptorValueMatrix);
		}
		else if(selectedPredictor.getDescriptorGeneration().equals(DescriptorEnumeration.MOE2D)){
			step = Constants.DESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Generating Moe2D Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMoe2DDescriptors(path + sdfile, path + sdfile + ".moe2D");
			
			step = Constants.PROCDESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Processing Moe2D Descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readMoe2DDescriptors(path + sdfile + ".moe2D", descriptorNames, descriptorValueMatrix);
		}
		else if(selectedPredictor.getDescriptorGeneration().equals(DescriptorEnumeration.MACCS)){
			step = Constants.DESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Generating MACCS Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMaccsDescriptors(path + sdfile, path + sdfile + ".maccs");
			
			step = Constants.PROCDESCRIPTORS;
			Utility.writeToDebug("ExecutePredictor: Processing MACCS Descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readMaccsDescriptors(path + sdfile + ".maccs", descriptorNames, descriptorValueMatrix);
		}
		
		String descriptorString = descriptorNames.toString().replaceAll("[,\\[\\]]", "");
		WriteDescriptorsFileWorkflow.writePredictionXFile(
				chemicalNames, 
				descriptorValueMatrix, 
				descriptorString, 
				path + sdfile + ".renorm.x", 
				path + "train_0.x", 
				selectedPredictor.getScalingType());
		
		step = Constants.PREDICTING;
		Utility.writeToDebug("ExecutePredictor: Making predictions", userName, jobName);
		KnnPredictionWorkflow.RunKnnPrediction(userName, jobName, sdfile, Float.parseFloat(cutoff) );

		step = Constants.READPRED;
		this.allPredValue = parsePredOutput(this.filePath + Constants.PRED_OUTPUT_FILE);
		KnnPredictionWorkflow.MoveToPredictionsDir(userName, jobName);
		Utility.writeToDebug("ExecPredictorActionTask: Complete", userName, jobName);
	}
	
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
	
	private static Pred_Output createPredObject(String[] extValues) {

		if (extValues == null) {
			return null;
		}
		int arraySize = extValues.length;
		
		Pred_Output predOutput = new Pred_Output();
		predOutput.setCompoundID(extValues[0]);
		predOutput.setNumOfModels(extValues[1]);
		predOutput.setPredictedValue(extValues[2]);
		if (arraySize > 3){
			predOutput.setStandardDeviation(extValues[3]);
		}
		return predOutput;

	}
	
    @SuppressWarnings("unchecked")
	public static ArrayList<Pred_Output> parsePredOutput(String fileLocation) throws IOException {
		Utility.writeToDebug("Reading prediction output from " + fileLocation);
		ArrayList<Pred_Output> allPredValue = new ArrayList<Pred_Output>();
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
				Pred_Output extValOutput = createPredObject(predValues);
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
	
		
	public void cleanUp() throws Exception {
		Queue.getInstance().deleteTask(this);
	}

	public ArrayList getAllPredValue() {
		return this.allPredValue;
	}

	public void setUp() throws Exception {

		Utility.writeToDebug("Setting up prediction task", userName, jobName);
		try{
			new File(Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName).mkdir();
			FileAndDirOperations.copyFile(
				Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/"+predictionDataset.getFileName()+"/"+sdf, 
				Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/"+sdf
				);
		}
		catch(Exception e){
			Utility.writeToMSDebug(e.getMessage());
			Utility.writeToDebug(e);
		}
		Utility.writeToMSDebug("Files copied");
	}

	public void save(){
		try{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
		Prediction predictionJob = new Prediction();
		predictionJob.setDatabase(this.sdf);
		predictionJob.setUserName(this.userName);
		predictionJob.setSimilarityCutoff(new Float(this.cutoff));
		predictionJob.setPredictorIds(this.selectedPredictorIds);
		predictionJob.setJobName(this.jobName);
		predictionJob.setStatus("NOTSET");
		predictionJob.setDatasetId(predictionDataset.getFileId());

		if(this.allPredValue == null){
			Utility.writeToDebug("Warning: allPredValue is null.");
		}
		else{
			Utility.writeToDebug("Saving prediction to database.");
		}
		
		for (Pred_Output predOutput : this.allPredValue){
			
			PredictionValue predValue = new PredictionValue();

			predValue.setCompoundName(predOutput.getCompoundID());
			
			try{
				predValue.setNumModelsUsed(new Integer(predOutput.getNumOfModels()));	
				
			}catch (NumberFormatException e){
				predValue.setNumModelsUsed(0);	
			}
			
			if (predOutput.getStandardDeviation() != null){
				try{
					predValue.setStandardDeviation(new Float(predOutput.getStandardDeviation()));
					
				}catch (NumberFormatException e){
					predValue.setStandardDeviation(null);	
				}
			}else{
				predValue.setStandardDeviation(null);	
			}

			predValue.setPredictionJob(predictionJob);
			predictionValues.add(predValue);
		
		}
		
		predictionJob.setPredictedValues(new ArrayList<PredictionValue>(predictionValues));

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(predictionJob);		
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
