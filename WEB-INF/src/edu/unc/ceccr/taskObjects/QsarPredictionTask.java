package edu.unc.ceccr.taskObjects;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.Constants.DescriptorEnumeration;
import edu.unc.ceccr.outputObjects.Pred_Output;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.PredictionJob;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.GenerateDescriptorWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnPredictionWorkflow;
import edu.unc.ceccr.workflows.MolconnZToDescriptors;

public class QsarPredictionTask implements WorkflowTask {

	private String filePath;
	private ArrayList<Pred_Output> allPredValue;
	private InputStream is;
	private String jobName;
	private String fileOrDatabaseName;
	private String cutoff;
	private String userName;
	private Long selectedPredictorId;
	private DataSet predictionDataset;
	
	private Queue queue = Queue.getInstance();
	
	public QsarPredictionTask(String userName, String jobName,String fileOrDatabaseName, String cutoff,
			InputStream is, int uploadOrSelect, Long selectedPredictorId, DataSet predictionDataset) throws Exception {
		
		Utility.writeToMSDebug("Start ExecPredictorActionTask Constructor");
		this.predictionDataset = predictionDataset;
		this.jobName = jobName;
		this.userName = userName;
		this.fileOrDatabaseName = fileOrDatabaseName;
		this.cutoff = cutoff;
		this.selectedPredictorId = selectedPredictorId;
		this.filePath = Constants.CECCR_USER_BASE_PATH + userName + "/"+ jobName + "/";
		this.is = is;

		Utility.writeToMSDebug("Finish ExecPredictorActionTask Constructor");
	}

	public void execute() throws Exception {

		Utility.writeToDebug("ExecPredictorActionTask: ExecutePredictor",userName,jobName);
		Utility.writeToMSDebug("ExecPredictorActionTask: Start"+userName+" "+jobName);
		
        Predictor selectedPredictor = getPredictor(this.selectedPredictorId);

        Utility.writeToMSDebug("execute::"+selectedPredictor.getName());
		
        //We're keeping a count of how many times each predictor was used.
        //So, increment number of times used and save predictor object.
        selectedPredictor.setNumPredictions(selectedPredictor.getNumPredictions() + 1);
		Session s = HibernateUtil.getSession();
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

		queue.runningTask.setMessage("Creating file structure");
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

		queue.runningTask.setMessage("Copying predictor");
		GetJobFilesWorkflow.GetKnnPredictionFiles(userName, jobName, sdfile, sdfIsAllUser, predictorIsAllUser, selectedPredictor.getName(), predictionDataset.getFileName());

		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		if(selectedPredictor.getDescriptorGeneration().equals(DescriptorEnumeration.MOLCONNZ)){
			queue.runningTask.setMessage("Generating molconnZ descriptors");
			Utility.writeToDebug("ExecutePredictor: Generating molconnZ Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(path + sdfile, path + sdfile + ".S");
			
			queue.runningTask.setMessage("Normalizing descriptors");
			Utility.writeToDebug("ExecutePredictor: Normalizing Descriptors", userName, jobName);
			MolconnZToDescriptors.MakePredictionDescriptors(path + sdfile + ".S", path + "train_0.x", path + sdfile + ".renorm.x");
		}
		else{
			queue.runningTask.setMessage("Generating Dragon descriptors");
			Utility.writeToDebug("ExecutePredictor: Generating Dragon Descriptors", userName, jobName);
			GenerateDescriptorWorkflow.GenerateDragonDescriptors(path + sdfile, path + sdfile + ".dragon");
			
			//NEED TO CHANGE THIS FOR DRAGON
			queue.runningTask.setMessage("Normalizing descriptors");
			Utility.writeToDebug("ExecutePredictor: Normalizing Descriptors", userName, jobName);
			MolconnZToDescriptors.MakePredictionDescriptors(path + sdfile + ".dragon", path + "train_0.x", path + sdfile + ".renorm.x");
		}
		
		queue.runningTask.setMessage("Making predictions");
		Utility.writeToDebug("ExecutePredictor: Making predictions", userName, jobName);
		KnnPredictionWorkflow.RunKnnPrediction(userName, jobName, sdfile, Float.parseFloat(cutoff) );

		queue.runningTask.setMessage("Reading predictions");
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
	
	private Pred_Output createPredObject(String[] extValues) {

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
	private ArrayList<Pred_Output> parsePredOutput(String fileLocation) throws IOException {
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
	
	
	private void writeFileToDB(InputStream is, String fileOrDatabaseName)throws IOException {

		String dir=Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/";
		File fileDir=new File(dir);
		if(!fileDir.exists())
		{
			@SuppressWarnings("unused")
			boolean success=fileDir.mkdirs();
		}
		
		String fullFileLocation=dir+fileOrDatabaseName;
		
		Utility.writeFiles(is, fullFileLocation);
	}

		
	public void cleanUp() throws Exception {
		//this.executeAntWorkflow.cleanUp();
		queue.deleteTask(this);
	}

	public ArrayList getAllPredValue() {
		return this.allPredValue;
	}

	public void setUp() throws Exception {
		
		if (is != null)
		{
			writeFileToDB(is, fileOrDatabaseName);
			}
	}

	public void save(){
		try{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
		PredictionJob predictionJob = new PredictionJob();
		predictionJob.setDatabase(this.fileOrDatabaseName);
		predictionJob.setUserName(this.userName);
		predictionJob.setSimilarityCutoff(new Float(this.cutoff));
		predictionJob.setPredictorId(this.selectedPredictorId);
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
			
			try{
				predValue.setPredictedValue(Math.abs(new Float(predOutput.getPredictedValue())));
				
			}catch (NumberFormatException e){
				predValue.setPredictedValue(null);	
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
		Utility.deleteDir(dir);
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
