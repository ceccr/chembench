package edu.unc.ceccr.taskObjects;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import java.io.PrintStream;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.Constants.DescriptorEnumeration;
import edu.unc.ceccr.global.Constants.KnnEnumeration;
import edu.unc.ceccr.global.KnnOutputComparator;
import edu.unc.ceccr.global.CategoryKNNComparator;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.task.WTSequence;
import edu.unc.ceccr.task.WorkflowTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.DragonToDescriptors;
import edu.unc.ceccr.workflows.GenerateDescriptorWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnModelBuildingWorkflow;
import edu.unc.ceccr.workflows.MolconnZToDescriptors;
import edu.unc.ceccr.workflows.ReadDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.SdfToJpgWorkflow;
import edu.unc.ceccr.workflows.WriteDescriptorsFileWorkflow;

public class QsarModelingTask implements WorkflowTask {

	//knn parameters
	private String knnType;
	private String minNumDescriptors;
	private String stepSize;
	private String numCycles;
	private String maxNumDescriptors;
	private String Nearest_Neighbors;
	private String Pseudo_Neighbors;	
	private String numRuns;	
	private String numMutations;
	private String T1;
	private String T2;
	private String mu;
	private String TcOverTb;
	private String cutoff;
	private String minAccTraining;
	private String minAccTest;
	private String minSlopes;
	private String maxSlopes;
	private String Relative_diff_R_R0;
	private String Diff_R01_R02;
	private String stop_cond;
	
	@SuppressWarnings("unused")
	private String knnCategoryOptimization;
	
	private String numCompoundsExternalSet;
	String filePath;
	String datasetPath;
	ArrayList<Model> allkNNValues=null;
	ArrayList<Model> mainKNNValues=null;
	ArrayList<Model> randomKNNValues=null;
	ArrayList<Model> sortedYRKNNValues=null;
	ArrayList<Model> sortedkNNValues = null;
	String[] externalValues = null;
	ArrayList<ExternalValidation> allExternalValues = null;
	private String sdFileName;
	private String actFileName;
	private String user_path;
	private String userName;
	private String jobName;
	int numTotalModels;
	int numTrainModels;
	int numTestModels;
	int yTotalModels;
	
	int yTrainModels;
	int yTestModels;
	private boolean noModelsGenerated;
	private boolean isAllUser;
	private KnnEnumeration knnEnum;
	private String descriptorGenerationType;
	private DescriptorEnumeration descriptorEnum;
	private WorkflowTask executeAntWorkflow;	
	private ExecuteExternal executeExternalPrediction;
	private String numSphereRadii;
	private String selectionNextTrainPt;
	private String numStartingPoints;
	private String activityType;
	private String datasetName;
	private Long datasetID;

	public QsarModelingTask(
			String userName, 
			String jobName,
			String numCompoundsExternalSet,
			String knnType, 
			String descriptorType, 
			String maxNumDescriptors,
			String minNumDescriptors, 
			String stepSize, 
			String numCycles, 
			String numMutations,
			String minAccTest, 
			String minAccTraining, 
			String cutoff, 
			String mu,
			String numRuns, 
			String Nearest_Neighbors, 
			String Pseudo_Neighbors,	
			String T1, 
			String T2, 
			String TcOverTb, 
			String minSlopes, 
			String maxSlopes,
			String Relative_diff_R_R0, 
			String Diff_R01_R02, 
			String stop_cond,	
			String knnCategoryOptimization, 
			String numSphereRadii,
			String selectionNextTrainPt, 
			String numStartingPoints, 
			Long selectedDatasetId)
			throws Exception	{
		
		DataSet dataset = PopulateDataObjects.getDataSetById(selectedDatasetId);
		
		this.userName = userName;
		this.jobName = jobName;
		this.actFileName = dataset.getActFile();
		this.sdFileName = dataset.getSdfFile();
		this.isAllUser = false;
		if(dataset.getUserName().equalsIgnoreCase("_all")){
			this.isAllUser = true;
		}
		this.datasetName = dataset.getFileName();
		
		this.numCompoundsExternalSet = numCompoundsExternalSet;
		this.knnType = knnType;
		this.descriptorGenerationType = descriptorType;
		this.maxNumDescriptors = maxNumDescriptors;
		this.minNumDescriptors = minNumDescriptors;
		this.stepSize = stepSize;
		this.numCycles = numCycles;
		this.numMutations = numMutations;
		this.minAccTraining=minAccTraining;
		this.minAccTest=minAccTest;
		this.cutoff = cutoff;
		this.mu = mu;
		this.numRuns = numRuns;
		this.Nearest_Neighbors = Nearest_Neighbors;
		this.Pseudo_Neighbors = Pseudo_Neighbors;
		this.T1 = T1;
		this.T2 = T2;
		this.TcOverTb = TcOverTb;
		this.minSlopes = minSlopes;
		this.maxSlopes = maxSlopes;
		this.Relative_diff_R_R0 = Relative_diff_R_R0;
		this.Diff_R01_R02 = Diff_R01_R02;
		this.knnCategoryOptimization = knnCategoryOptimization;
		this.numSphereRadii = numSphereRadii;
		this.selectionNextTrainPt = selectionNextTrainPt;
		this.numStartingPoints = numStartingPoints;
		this.stop_cond = stop_cond;
		this.datasetID = selectedDatasetId;

		user_path = userName + "/" + jobName + "/";
		filePath = Constants.CECCR_USER_BASE_PATH + user_path;
		datasetPath = Constants.CECCR_USER_BASE_PATH;
		if(dataset.getUserName().equalsIgnoreCase("_all")){
			datasetPath += "all-users";
		}
		else{
			datasetPath += dataset.getUserName();
		}
		datasetPath += "/DATASETS/" + datasetName + "/";
		
		if (knnType.equals(Constants.CATEGORY)){
			knnEnum = KnnEnumeration.CATEGORY;
		}else if (knnType.equals(Constants.CONTINUOUS)){
			knnEnum = KnnEnumeration.CONTINUOUS;
		}
		
	}

	public void setUp() throws Exception {

		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		
		if (knnType.equals(Constants.CONTINUOUS)){
			writeKnnContinuousDefaultFile(filePath + Constants.KNN_DEFAULT_FILENAME);
		}
		else if (knnType.equals(Constants.CATEGORY)){
			writeKnnCategoryDefaultFile(filePath + Constants.KNN_CATEGORY_DEFAULT_FILENAME);
		}
		
		writeSEDefaultFile(filePath + Constants.SE_DEFAULT_FILENAME);
		
	}
 
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		//copy the dataset files to the working directory
		queue.runningTask.setMessage("Copying files");
		GetJobFilesWorkflow.GetKnnFiles(userName, jobName, sdFileName, actFileName, isAllUser, knnType, datasetName);

		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";

		//create the descriptors for the dataset and read them in
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		ArrayList<String> chemicalNames = DatasetFileOperations.getChemicalNamesFromSdf(path + sdFileName);
		
		if (descriptorGenerationType.equals(Constants.MOLCONNZ)){
			descriptorEnum = DescriptorEnumeration.MOLCONNZ;
			
			queue.runningTask.setMessage("Generating MolconnZ descriptors");
			Utility.writeToDebug("Generating MolconnZ Descriptors", userName, jobName);
			Utility.writeToMSDebug("Generating MolconnZ Descriptors::"+ path);
			GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(path + sdFileName, path + sdFileName + ".S");

			queue.runningTask.setMessage("Processing MolconnZ descriptors");
			Utility.writeToDebug("Converting MolconnZ output to .x format", userName, jobName);
			ReadDescriptorsFileWorkflow.readMolconnZDescriptors(path + sdFileName + ".S", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.DRAGON)){
			descriptorEnum = DescriptorEnumeration.DRAGON;
			
			queue.runningTask.setMessage("Generating Dragon descriptors");
			Utility.writeToDebug("Generating Dragon Descriptors", userName, jobName);
			Utility.writeToMSDebug("Generating Dragon Descriptors::"+ path);
			GenerateDescriptorWorkflow.GenerateDragonDescriptors(path + sdFileName, path + sdFileName + ".dragon");
			
			queue.runningTask.setMessage("Processing Dragon descriptors");
			Utility.writeToDebug("Processing Dragon descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readDragonDescriptors(path + sdFileName + ".dragon", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.MOE2D)){
			descriptorEnum = DescriptorEnumeration.MOE2D;
			
			queue.runningTask.setMessage("Generating MOE2D descriptors");
			Utility.writeToDebug("Generating MOE2D Descriptors", userName, jobName);
			Utility.writeToMSDebug("Generating MOE2D Descriptors::"+ path);
			GenerateDescriptorWorkflow.GenerateMoe2DDescriptors(path + sdFileName, path + sdFileName + ".moe2d");
			
			queue.runningTask.setMessage("Processing MOE2D descriptors");
			Utility.writeToDebug("Processing MOE2D descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readMoe2DDescriptors(path + sdFileName + ".moe2D", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.MACCS)){
			descriptorEnum = DescriptorEnumeration.MACCS;
			
			queue.runningTask.setMessage("Generating MACCS descriptors");
			Utility.writeToDebug("Generating MACCS Descriptors", userName, jobName);
			Utility.writeToMSDebug("Generating MACCS Descriptors::" + path);
			GenerateDescriptorWorkflow.GenerateMaccsDescriptors(path + sdFileName, path + sdFileName + ".maccs");
			
			queue.runningTask.setMessage("Processing MACCS descriptors");
			Utility.writeToDebug("Processing MACCS descriptors", userName, jobName);
			ReadDescriptorsFileWorkflow.readMaccsDescriptors(path + sdFileName + ".maccs", descriptorNames, descriptorValueMatrix);
		}
		
		//write out the descriptors for modeling
		String descriptorString = descriptorNames.toString().replaceAll("[,\\[\\]]", "");
		WriteDescriptorsFileWorkflow.writeModelingXFile(chemicalNames, descriptorValueMatrix, descriptorString, path + sdFileName + ".x");

		
		//wtsequence.add(executePostDescriptorWorkflow);
		queue.runningTask.setMessage("Splitting data");
		KnnModelBuildingWorkflow.SplitData(userName, jobName, sdFileName, actFileName, numCompoundsExternalSet);

		//wtsequence.add(executeRandomizationWorkflow);
		queue.runningTask.setMessage("Y-Randomization Setup");
		Utility.writeToDebug("ExecuteYRandomization", userName, jobName);
		KnnModelBuildingWorkflow.YRandomization(userName, jobName);
		
		//wtsequence.add(executeKnnWorkflow);	
		queue.runningTask.setMessage("kNN Modeling");
		if(knnEnum == KnnEnumeration.CATEGORY){
			KnnModelBuildingWorkflow.buildKnnCategoryModel(userName, jobName, knnCategoryOptimization, path);
		}else if(knnEnum == KnnEnumeration.CONTINUOUS){
			KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path);
		}
		
		//wtsequence.add(executeYRandomKnnWorkflow);
		queue.runningTask.setMessage("y-Randomization Modeling");
		if(knnEnum == KnnEnumeration.CATEGORY){
			KnnModelBuildingWorkflow.buildKnnCategoryModel(userName, jobName, knnCategoryOptimization, path + "yRandom/");
		}else if(knnEnum == KnnEnumeration.CONTINUOUS){
			KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path + "yRandom/");
		}
		
		//wtsequence.add(executeExternalPrediction);
		queue.runningTask.setMessage("Predicting external set");
		executeExternalPrediction = new ExecuteExternal(userName, jobName, sdFileName, actFileName);
		WTSequence wtsequence = new WTSequence();
		wtsequence.add(executeExternalPrediction);
		executeAntWorkflow = wtsequence;
		executeAntWorkflow.execute();
		
		//done with modeling. Read output files. 
		queue.runningTask.setMessage("Reading kNN output");
		if (knnType.equals(Constants.CATEGORY)){
			parseCategorykNNOutput(filePath+Constants.kNN_OUTPUT_FILE, Constants.MAINKNN);
			parseCategorykNNOutput(filePath+"yRandom/"+Constants.kNN_OUTPUT_FILE, Constants.RANDOMKNN);
		}else if (knnType.equals(Constants.CONTINUOUS)){
			parseContinuouskNNOutput(filePath+Constants.kNN_OUTPUT_FILE, Constants.MAINKNN);
			parseContinuouskNNOutput(filePath+"yRandom/"+Constants.kNN_OUTPUT_FILE, Constants.RANDOMKNN);
		}

		this.noModelsGenerated = this.mainKNNValues.isEmpty();
		if (!noModelsGenerated)
		{
			allExternalValues = parseExternalValidationOutput(filePath + Constants.EXTERNAL_VALIDATION_OUTPUT_FILE, user_path);
			addStdDeviation(allExternalValues,parseConpredStdDev(filePath + Constants.PRED_OUTPUT_FILE));
		}

		if (!noModelsGenerated)
				sortModels();

		setParameters(filePath, mainKNNValues, Constants.MAINKNN);
		setParameters(filePath+"yRandom/", randomKNNValues, Constants.RANDOMKNN);
	}

	Queue queue = Queue.getInstance();

	public void cleanUp() throws Exception {
		Utility.writeToDebug("Cleaning up.", userName, jobName);
		queue.deleteTask(this);
	}

	public void save() throws Exception {

		Utility.writeToDebug("SubmitQsarWorkflowActionTask: save()", userName, jobName);
		KnnModelBuildingWorkflow.MoveToPredictorsDir(userName, jobName);

		allkNNValues=new ArrayList<Model>();
      
       if(sortedkNNValues == null){
    	   Utility.writeToDebug("Warning: No models were generated.");
       }
       else{
    	   allkNNValues.addAll(sortedkNNValues);
    	   allkNNValues.addAll(sortedYRKNNValues);
       }
       
		Predictor predictor = new Predictor();

		predictor.setDescriptorGeneration(descriptorEnum);
		predictor.setModelMethod(knnEnum);
		predictor.setName(this.jobName);
		predictor.setUserName(this.userName);
		predictor.setActFileName(this.actFileName);
		predictor.setSdFileName(this.sdFileName);
		predictor.setNumTotalModels(this.numTotalModels);
		predictor.setNumTestModels(this.numTestModels);
		predictor.setNumTrainModels(this.numTrainModels);
		
		predictor.setNumyTestModels(this.yTestModels);
		predictor.setNumyTrainModels(this.yTrainModels);
		predictor.setNumyTotalModels(this.yTotalModels);
		predictor.setActivityType(activityType);
		predictor.setStatus("NOTSET");
		predictor.setPredictorType("Unsaved");
		predictor.setDatasetId(datasetID);
		
		if(this.allkNNValues.size()<1){}else
		{for (ModelInterface m : allkNNValues)
			m.setPredictor(predictor);
		
		for (ExternalValidation ev : allExternalValues)
			ev.setPredictor(predictor);
		
		predictor.setModels(new HashSet<Model>(allkNNValues));
		predictor.setExternalValidationResults(new HashSet<ExternalValidation>(allExternalValues));}

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(predictor);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		
		File dir=new File(Constants.CECCR_USER_BASE_PATH+this.userName+"/"+this.jobName);
		FileAndDirOperations.deleteDir(dir);
	}
	
	private void setParameters(String path, ArrayList<Model> KNNValues, String flow) {
		File dir;
		dir = new File(path);
        int total, test, train;
        total= dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}}).length;
        
        test=KNNValues.size();
        train=dir.list(new FilenameFilter() {	public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}	}).length - test;
        if(flow.equals(Constants.MAINKNN))
        {
        	numTotalModels=total;
        	numTestModels=test;
        	numTrainModels=train;
        }else
        {
        	yTotalModels=total;
        	yTestModels=test;
        	yTrainModels=train;
        }
        
	}

	private void parseContinuouskNNOutput(String fileLocation, String flowType) throws IOException {
	BufferedReader in = new BufferedReader(new FileReader(fileLocation));
	String inputString;
	String[] kNNValues = null;
	if(flowType.equals(Constants.MAINKNN)){ mainKNNValues=new ArrayList<Model>();}
	else{ randomKNNValues=new ArrayList<Model>();}

	while ((inputString = in.readLine()) != null) {
		// 5 types of lines found in the knn-outputsort.tbl file:
		// | STATISTICS OF REGRESSION LINES Y = b11*X + b01 AND X =
		// b12*numTrainModels + b02 ...
		// nnn q^2 n ...
		// ----------------------------------- ...
		// blank lines
		// data lines (keep only the data)
		if (inputString.trim().startsWith("|")
				|| inputString.trim().startsWith("-")
				|| inputString.trim().equals("")
				|| inputString.trim().startsWith("nnn")) {
			// skip all rows that don't have data
		} else {
			kNNValues = inputString.split("\\s+");
			Model knnOutput = createContinuousKnnOutputObject(kNNValues, flowType);
			if(flowType.equals(Constants.MAINKNN) ){ mainKNNValues.add(knnOutput);}
			else{ randomKNNValues.add(knnOutput);}
		}
	}
	in.close();
}
	private void parseCategorykNNOutput(String fileLocation, String flowType) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation));
		String inputString;
		String[] kNNValues = null;
		if(flowType.equals(Constants.MAINKNN)){ mainKNNValues=new ArrayList<Model>();}
		else{ randomKNNValues=new ArrayList<Model>();}

		while ((inputString = in.readLine()) != null) 
		{
			// data lines (keep only the data)
			if (inputString.trim().startsWith("|")
					|| inputString.trim().startsWith("-")
					|| inputString.trim().equals("")
					|| inputString.trim().startsWith("STATISTICS")
					|| inputString.trim().startsWith("NCompTrain")) 
			{
				// skip all rows that don't have data
			} else {
				kNNValues = inputString.split("\\s+");
				Model knnOutput = createCategoryKnnOutputObject(kNNValues, flowType);
				if(flowType.equals(Constants.MAINKNN) ){ mainKNNValues.add(knnOutput);}
				else{ randomKNNValues.add(knnOutput);}
			}
		}
		in.close();
	}

	@SuppressWarnings("unchecked")
	private void sortModels() {
		
		java.util.Comparator knnOutputComparator;
		
		if(this.knnType.equals(Constants.CONTINUOUS))
		{knnOutputComparator = new KnnOutputComparator();}
		else{knnOutputComparator = new CategoryKNNComparator();}
		
	
		Collections.sort(mainKNNValues, knnOutputComparator);
		Collections.sort(randomKNNValues, knnOutputComparator);
		
		// only keep top 10 models with the largest r-squared values
		sortedkNNValues = new ArrayList<Model>();
		sortedYRKNNValues=new ArrayList<Model>();
		
		if (mainKNNValues.size() >= 10) {
			for (int i =mainKNNValues.size(); i > (mainKNNValues.size() - 10); i--) {
				sortedkNNValues.add(mainKNNValues.get(i - 1));
			}
		} else {
			for (int i = mainKNNValues.size(); i > 0; i--) {
				sortedkNNValues.add(mainKNNValues.get(i - 1));
			}
		}
		
		if (randomKNNValues.size() >= 10) {
			for (int i =randomKNNValues.size(); i > (randomKNNValues.size() - 10); i--) {
				sortedYRKNNValues.add(randomKNNValues.get(i - 1));
			}
		} else {
			for (int i = randomKNNValues.size(); i > 0; i--) {
				sortedYRKNNValues.add(randomKNNValues.get(i - 1));
			}
		}
	}
	
	protected void addStdDeviation(ArrayList<ExternalValidation> allExternalValue, ArrayList<String> stdDevList)
	{
		Iterator it1=allExternalValues.iterator();
		Iterator it2=stdDevList.iterator();
		while(it1.hasNext()&&it2.hasNext())
		{
			((ExternalValidation)it1.next()).setStandDev((String)it2.next());
		}
	}

	public static ArrayList parseConpredStdDev(String path)throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(path));
		String inputString;
		ArrayList<String> stdDevValues = new ArrayList<String>();
		while ((inputString = in.readLine()) != null) {
			String[] externalValues = inputString.split("\\s+");
			
			if(externalValues.length==4)
			{
				if(GenericValidator.isFloat(externalValues[Constants.STD_DEVIATION]))
				{stdDevValues.add(externalValues[Constants.STD_DEVIATION]);}
				else{
					stdDevValues.add("No value");
				}
				}
			else{
				if(externalValues.length==3)
				{stdDevValues.add("No value");}
			}
		}
		return stdDevValues;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList parseExternalValidationOutput(String fileLocation,
			String file_path) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation));
		String inputString;

		ArrayList allExternalValues = new ArrayList();
		while ((inputString = in.readLine()) != null) {
			String[] externalValues = inputString.split("\\s+");
			ExternalValidation extValOutput = createExternalValidationObject(
					file_path, externalValues);
			allExternalValues.add(extValOutput);
		}
		
		return allExternalValues;
	}
	
	private static ExternalValidation createExternalValidationObject(
			String file_path, String[] extValues) throws FileNotFoundException,IOException {
		if (extValues == null) {
			return null;
		}
		ExternalValidation extValOutput = new ExternalValidation();
		extValOutput.setCompoundId(extValues[Constants.COMPOUND_ID]);
	
		extValOutput.setActualValue(Float.parseFloat(extValues[Constants.ACTUAL]));
		if (GenericValidator.isFloat(extValues[Constants.PREDICTED]))
			extValOutput.setPredictedValue(Float.parseFloat(extValues[Constants.PREDICTED]));
		
		Utility.writeToDebug(((Integer)Constants.NUM_MODELS).toString());
		
		if (GenericValidator.isFloat(extValues[Constants.PREDICTED]))
			extValOutput.setNumModels(Integer.parseInt(extValues[Constants.NUM_MODELS]));
		
		return extValOutput;
	}

	public static Model createContinuousKnnOutputObject(String[] kNNValues, String flowType) {
		// The values array starts at 1 - not 0!
		if (kNNValues == null) {
			return null;
		}
		if (kNNValues.length <= 1) {
			return null;
		}
		Model knnOutput = new Model();
		knnOutput.setKnnType(Constants.CONTINUOUS);
		knnOutput.setNnn(Float.parseFloat(kNNValues[Constants.CONTINUOUS_NNN_LOCATION]));
		knnOutput.setQSquared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_Q_SQUARED_LOCATION]));
		knnOutput.setN(Float.parseFloat(kNNValues[Constants.CONTINUOUS_N_LOCATION]));
		knnOutput.setR(Float.parseFloat(kNNValues[Constants.CONTINUOUS_R_LOCATION]));
		knnOutput.setRSquared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R_SQUARED_LOCATION]));
		knnOutput.setR01Squared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R01_SQUARED_LOCATION]));
		knnOutput.setR02Squared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R02_SQUARED_LOCATION]));
		knnOutput.setK1(Float.parseFloat(kNNValues[Constants.CONTINUOUS_K1_LOCATION]));
		knnOutput.setK2(Float.parseFloat(kNNValues[Constants.CONTINUOUS_K2_LOCATION]));
		knnOutput.setFile(kNNValues[25]);
		knnOutput.setFlowType(flowType);
		return knnOutput;
	}

	public static Model createCategoryKnnOutputObject(String[] kNNValues, String flowType) {
		// The values array starts at 1 - not 0!
		if (kNNValues == null) {
			return null;
		}
		if (kNNValues.length <= 1) {
			return null;
		}
		Model knnOutput = new Model();
		knnOutput.setKnnType(Constants.CATEGORY);
		knnOutput.setNnn(Float.parseFloat(kNNValues[Constants.CATEGORY_NNN_LOCATION]));
		knnOutput.setTrainingAcc(Float
				.parseFloat(kNNValues[Constants.CATEGORY_TRAINING_ACC_LOCATION]));
		knnOutput.setNormalizedTrainingAcc(Float.parseFloat(kNNValues[Constants.CATEGORY_NORMALIZED_TRAINING_ACC_LOCATION]));
		
		knnOutput.setTestAcc(Float.parseFloat(kNNValues[Constants.CATEGORY_TEST_ACC_LOCATION]));
		knnOutput.setNormalizedTestAcc(Float
				.parseFloat(kNNValues[Constants.CATEGORY_NORMALIZED_TEST_ACC_LOCATION]));

		knnOutput.setFile(kNNValues[12]);
		knnOutput.setFlowType(flowType);
		return knnOutput;
	}

	
	private void writeKnnContinuousDefaultFile(String fullFileLocation)
			throws IOException {
		
		FileOutputStream fout;	
		PrintStream out;
		try
		{
		    fout = new FileOutputStream (fullFileLocation);
		    out = new PrintStream(fout);

		    out.println("Min_Number_Of_Descriptors: " + minNumDescriptors);
		    out.println("Step: " + stepSize);
		    out.println("Number_Of_Steps: " + ((new Integer(maxNumDescriptors).intValue() - new Integer(minNumDescriptors).intValue())/new Integer(stepSize).intValue()));
		    out.println("Number_Of_Cycles: " + numCycles);
		    out.println("Number_Of_Neares_Neighbors: " + Nearest_Neighbors);
		    out.println("Number_Of_Pseudo_Neighbors: " + Pseudo_Neighbors);
		    out.println("Number_Of_Mutations: " + numMutations);
		    out.println("Runs_For_Each_Set_Of_Parameters: " + numRuns);
		    out.println("T1: " + T1);
		    out.println("T2: " + T2);
		    out.println("Mu: " + mu);
		    out.println("TcOverTb: " + TcOverTb);
		    out.println("CutOff: " + cutoff);
		    out.println("Minimum_q2: " + minAccTraining);
		    out.println("Minimum_R2: " + minAccTest);
		    out.println("Minimum_and_maximum_slopes: " + minSlopes + " " + maxSlopes);
		    out.println("Relative_diff_R_R0: " + Relative_diff_R_R0);
		    out.println("Diff_R01_R02: " + Diff_R01_R02);
		    out.println("stop_cond: " + stop_cond);

		    out.close();
		    fout.close();	
		    } catch (IOException e) {
		    	Utility.writeToDebug(e);
		    }
	}

	private void writeKnnCategoryDefaultFile(String fullFileLocation)
			throws IOException {

		FileOutputStream fout;
		PrintStream out;
		try {
			fout = new FileOutputStream(fullFileLocation);
			out = new PrintStream(fout);
			out.println("Min_Number_Of_Descriptors: " + minNumDescriptors);
			out.println("Step: " + stepSize);
			out.println("Number_Of_Steps: "
							+ ((new Integer(maxNumDescriptors).intValue() - new Integer(
									minNumDescriptors).intValue()) / new Integer(
									stepSize).intValue()));
		    out.println("Number_Of_Cycles: " + numCycles);
		    out.println("Number_Of_Neares_Neighbors: " + Nearest_Neighbors);
		    out.println("Number_Of_Pseudo_Neighbors: " + Pseudo_Neighbors);
			out.println("Number_Of_Mutations: " + numMutations);
			out.println("Runs_For_Each_Set_Of_Parameters: " + numRuns);
		    out.println("T1: " + T1);
		    out.println("T2: " + T2);
			out.println("Mu: " + mu);
		    out.println("TcOverTb: " + TcOverTb);
			out.println("Minimum_acc_train: " + minAccTraining);
			out.println("Minimum_acc_test: " + minAccTest);			
			out.println("CutOff: " + cutoff);
			out.println("Stop: " + stop_cond);

			out.close();
			fout.close();
		} catch (IOException e) {
			Utility.writeToDebug(e);
		}
	}

	private void writeSEDefaultFile(String fullFileLocation)
			throws IOException {

		FileOutputStream fout;
		PrintStream out;
		try {
			fout = new FileOutputStream(fullFileLocation);
			out = new PrintStream(fout);

			out.print("# -------------------------------------------------------------------\n"+
			 "#\n"+
			 "# DEFAULT PARAMETER FILE FOR SE8\n"+
			 "#\n"+
			 "# -------------------------------------------------------------------\n"+
			 "#\n"+
			 "# THIS FILE WAS GENERATED  8/ 1/2005 21: 5:52 GMT BY SE6 SOFTWARE\n"+
			 "#\n"+
			 "# Copyright (C) 2002 A.Golbraikh & A.Tropsha\n"+
			 "# School of Pharmacy CB #7360 Beard Hall\n"+
			 "# University of North Carolina at Chapel Hill\n"+
			 "# Chapel Hill, NC 27599-7360 USA\n"+
			 "#\n"+
			 "# -------------------------------------------------------------------\n"+
			 "#\n"); 
			out.println("# MINIMUM NUMBER OF COMPOUNDS IN THE TRAINING OR THE TEST SET");
			out.println("5");
			out.println("# MINIMUM PERCENT OF COMPOUNDS IN THE TRAINING OR THE TEST SET");
			out.println("6");
			out.println("# DIVISION IS BASED ON:");
			out.println("# 1 - DISSIMILARITY LEVELS");
			out.println("# 2 - DISTANCES BETWEEN POINTS");
			out.println("1");
			out.println("# MINIMUM DISSIMILARITY LEVEL (USE IF 1 - DISSIMILARITY LEVELS)");
			out.println("0.200000");
			out.println("# MAXIMUM DISSIMILARITY LEVEL (USE IF 1 - DISSIMILARITY LEVELS)");
			out.println("5.200000");
			out.println("# THE NUMBER OF STEPS (IF 1 - DISSIMILARITY LEVELS)");
			out.println("# THE NUMBER OF SPHERE RADII (IF 2 - DISTANCES BETWEEN POINTS)");
			out.println(numSphereRadii);
			out.println("# THE MAXIMUM NUMBER OF COMPOUNDS ASSIGNED TO THE TEST SET IN A ROW");
			out.println("# THE MAXIMUM NUMBER OF COMPOUNDS ASSIGNED TO THE TRAINING SET IN A ROW");
			out.println("1 1");
			out.println("# 1 - SELECTION OF THE NEXT TRAINING SET POINT IS BASED ON THE MINIMUM SPHERE CENTER DISTANCES");
			out.println("# 2 - SELECTION OF THE NEXT TRAINING SET POINT IS BASED ON THE MAXIMUM SPHERE CENTER DISTANCES");
			out.println("# 3 - RANDOM SELECTION OF THE NEXT TRAINING SET POINT");
			out.println(selectionNextTrainPt);
			out.println("# THE NUMBER OF STARTING POINTS IN THE TRAINING SET");
			out.println(numStartingPoints);
			out.println("# 1 - MOST ACTIVE STARTING COMPOUNDS");
			out.println("# 2 - THE NUMBERS WILL BE ENTERED");
			out.println("# 3 - STARTING COMPOUNDS WILL BE SELECTED RANDOMLY");
			out.println("# 4 - MOST ACTIVE AND MOST INACTIVE STARTING COMPOUNDS (THE NUMBER OF STARTING POINTS SHOULD BE 2)");
			out.println("4");
			out.println("# USE IF 1 - MOST ACTIVE STARTING COMPOUND:");
			out.println("# 1 - MOST ACTIVE COMPOUND HAS THE HIGHEST ACTIVITY");
			out.println("# 2 - MOST ACTIVE COMPOUND HAS THE LOWEST ACTIVITY");
			out.println("# 1");
			out.println("# NUMBERS OF COMPOUNDS (IF 2 - THE NUMBERS WILL BE ENTERED)");
			out.println("# THE NUMBER OF ENTRIES BELOW MUST BE EQUAL TO THE NUMBER OF STARTING POINTS");
			out.println("#");
			out.println("");
			out.close();
			fout.close();
		} catch (IOException e) {
			Utility.writeToDebug(e);
		}
}
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getActFileName() {
		return actFileName;
	}

	public void setActFileName(String actFileName) {
		this.actFileName = actFileName;
	}

	public String getSdFileName() {
		return sdFileName;
	}

	public void setSdFileName(String sdFileName) {
		this.sdFileName = sdFileName;
	}
	
	public int getYTotalModels()
	{
		return yTotalModels;
	}
	public void setYTotalModels(int total)
	{
		this.yTotalModels=total;
	}
	
	public int getYTrainModels()
	{
		return yTrainModels;
	}
	public void setYTrainModels(int train)
	{
		this.yTrainModels=train;
	}
	
	public int getYTestModels()
	{
		return yTestModels;
	}
	public void setYTestModels(int test)
	{
		this.yTestModels=test;
	}
	
	public String getActivityType()
	{
		return this.activityType;
	}
	public void setActivityType(String type)
	{
		this.activityType=type;
	}
	public String getMaxNumDescriptors()
	{
		return this.maxNumDescriptors;
	}
	public void setMaxNumDescriptors(String maxNum)
	{
		this.maxNumDescriptors=maxNum;
	}
	
	public String getMinNumDescriptors()
	{
		return this.minNumDescriptors;
	}
	public void setMinNumDescriptors(String maxNum)
	{
		this.minNumDescriptors=maxNum;
	}
	
	public String getStepSize()
	{
		return this.stepSize;
	}
	public void setStepSize(String size){
		this.stepSize=size;
	}
	
	public String getNumRuns()
	{
		return this.numRuns;
	}
	public void setNumRuns(String runs)
	{
		this.numRuns=runs;
	}
	
	public String getNumSphereRadii()
	{
		return this.numSphereRadii;
	}
	public void setNumSphereRadii(String radii)
	{
		this.numSphereRadii=radii;
	}

	public Long getDatasetID() {
		return datasetID;
	}

	public void setDatasetID(Long datasetID) {
		this.datasetID = datasetID;
	}
	
	
	
}
