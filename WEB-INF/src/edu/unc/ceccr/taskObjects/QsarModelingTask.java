package edu.unc.ceccr.taskObjects;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import java.io.PrintStream;

import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.action.ModelingFormActions;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.KnnOutputComparator;
import edu.unc.ceccr.global.CategoryKNNComparator;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.DataSplitWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnModelBuildingWorkflow;
import edu.unc.ceccr.workflows.ReadDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.WriteDescriptorsFileWorkflow;

public class QsarModelingTask extends WorkflowTask {

	//job details
	private String sdFileName;
	private String actFileName;
	private String user_path;
	private String userName;
	private String jobName;
	private String modelType; // (svm || knn) 
	
	//dataset
	private String datasetName;
	private Long datasetID;
	private String filePath;
	private String datasetPath;
	private String actFileDataType;
	private DataSet dataset;
	
	//descriptors
	private String descriptorGenerationType;
	private String scalingType;
	private String stdDevCutoff;
	private String correlationCutoff;
	
	//datasplit
	private String numSplits;
	private String trainTestSplitType;
		
		//if random split
		private String randomSplitMinTestSize;
		private String randomSplitMaxTestSize;		
	
		//if sphere exclusion
		private String splitIncludesMin;
		private String splitIncludesMax;
		private String sphereSplitMinTestSize;
		private String selectionNextTrainPt;
		
	//knn	
	int numTotalModels;
	int numTrainModels;
	int numTestModels;
	int yTotalModels;
	int yTrainModels;
	int yTestModels;
	private String knnCategoryOptimization;

	private String minNumDescriptors;
	private String stepSize;
	private String numCycles;
	private String maxNumDescriptors;
	private String nearestNeighbors;
	private String pseudoNeighbors;
	
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
	
	//svm - not yet used
	private String svmDegreeFrom;
	private String svmDegreeTo;
	private String svmDegreeStep;

	private String svmGammaFrom;
	private String svmGammaTo;
	private String svmGammaStep;
	
	private String svmCostFrom;
	private String svmCostTo;
	private String svmCostStep;

	private String svmNuFrom;
	private String svmNuTo;
	private String svmNuStep;

	private String svmPEpsilonFrom;
	private String svmPEpsilonTo;
	private String svmPEpsilonStep;
	
	private String svmCrossValidation;
	private String svmEEpsilon;
	private String svmHeuristics;
	private String svmKernel;
	private String svmProbability;
	private String svmTypeCategory;
	private String svmTypeContinuous;
	private String svmWeight;
	
	//predictor object created during task
	private Predictor predictor;

	//output
	ArrayList<Model> allkNNValues=null;
	ArrayList<Model> mainKNNValues=null;
	ArrayList<Model> randomKNNValues=null;
	ArrayList<Model> sortedYRKNNValues=null;
	ArrayList<Model> sortedkNNValues = null;
	String[] externalValues = null;
	ArrayList<ExternalValidation> allExternalValues = null;
	private boolean noModelsGenerated;
	
	private String step = Constants.SETUP; //stores what step we're on 
	
	
	
	public String getProgress(){

		String percent = "";
		if(step.equals(Constants.MODELS) || step.equals(Constants.YMODELS)){
			String workingDir = "";
			if(jobList.equals(Constants.LSF)){
				//running on LSF so check LSF dir
				
			}
			else{
				//running locally so check local dir
				 workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
				 
			}
			if(step.equals(Constants.YMODELS)){
			 	workingDir += "yRandom/";
			 }
			
			float p = FileAndDirOperations.countFilesInDirMatchingPattern(workingDir, ".*mod");
			//divide by the number of models to be built
			
			//p /= Queue.getInstance().runningTask.getNumModels();
			p *= 100; //it's a percent
			percent = " (" + Math.round(p) + "%)";
		}
		
		return step + percent;
	}
	
	public QsarModelingTask(Predictor predictor) throws Exception{
		
		//get dataset
		datasetID = predictor.getDatasetId();
		Session s = HibernateUtil.getSession();
		dataset = PopulateDataObjects.getDataSetById(datasetID, s);
		datasetName = dataset.getFileName();
		sdFileName = dataset.getSdfFile();
		actFileName = dataset.getActFile();
		actFileDataType = dataset.getModelType();
		
		if(dataset.getUserName().equalsIgnoreCase("_all")){
			datasetPath += "all-users";
		}
		else{
			datasetPath += dataset.getUserName();
		}
		datasetPath += "/DATASETS/" + datasetName + "/";

		userName = predictor.getUserName();
		jobName = predictor.getName();
		user_path = userName + "/" + jobName + "/";
		filePath = Constants.CECCR_USER_BASE_PATH + user_path;
		
		modelType = predictor.getModelMethod();
		
		
		//descriptors
		descriptorGenerationType = predictor.getDescriptorGeneration();
		scalingType = predictor.getScalingType();
		stdDevCutoff = predictor.getStdDevCutoff();
		correlationCutoff = predictor.getCorrelationCutoff();
		
		//datasplit
		numSplits = predictor.getNumSplits();
		trainTestSplitType = predictor.getTrainTestSplitType();
			
			//if random split
			randomSplitMinTestSize = predictor.getRandomSplitMinTestSize();
			randomSplitMaxTestSize = predictor.getRandomSplitMaxTestSize();
			
			//if sphere exclusion
			splitIncludesMin = predictor.getSplitIncludesMin();
			splitIncludesMax = predictor.getSplitIncludesMax();
			sphereSplitMinTestSize = predictor.getSphereSplitMinTestSize();
			selectionNextTrainPt = predictor.getSelectionNextTrainPt();
			
		//knn params
		knnCategoryOptimization = predictor.getKnnCategoryOptimization();
		minNumDescriptors = predictor.getMinNumDescriptors();
		stepSize = predictor.getStepSize();
		maxNumDescriptors = predictor.getMaxNumDescriptors();
		numCycles = predictor.getNumCycles();
		nearestNeighbors = predictor.getNearestNeighbors();
		pseudoNeighbors = predictor.getPseudoNeighbors();
		
		numRuns = predictor.getNumRuns();
		numMutations = predictor.getNumMutations();
		T1 = predictor.getT1();
		T2 = predictor.getT2();
		mu = predictor.getMu();
		TcOverTb = predictor.getTcOverTb();
		cutoff = predictor.getCutoff();
		stdDevCutoff = predictor.getStdDevCutoff();
		correlationCutoff = predictor.getCorrelationCutoff();
		minAccTraining = predictor.getMinAccTraining();
		minAccTest = predictor.getMinAccTest();
		minSlopes = predictor.getMinSlopes();
		maxSlopes = predictor.getMaxSlopes();
		Relative_diff_R_R0 = predictor.getRelativeDiffRR0();
		Diff_R01_R02 = predictor.getDiffR01R02();
		stop_cond = predictor.getStopCond();
		
		//skipping SVM params for now. Will do that later.
		
		//other things in constructor that need doing?
	}
	
	public QsarModelingTask(String userName, ModelingFormActions ModelingForm) throws Exception {
		
		//This function just loads all the ModelingForm parameters into local variables
		Utility.writeToDebug("[[Modeling Type: " + ModelingForm.getModelingType());
		if(ModelingForm.getModelingType().equalsIgnoreCase(Constants.KNN)){
			modelType = Constants.KNN;
		}
		else{ //if(modelingType.equalsIgnoreCase(Constants.SVM))
			modelType = Constants.SVM;
		}
		
		scalingType = ModelingForm.getScalingType();
		
		stdDevCutoff = ModelingForm.getStdDevCutoff();
		correlationCutoff = ModelingForm.getCorellationCutoff();
		
		Session session = HibernateUtil.getSession();
		dataset = PopulateDataObjects.getDataSetById(ModelingForm.getSelectedDatasetId(),session);
		session.close();
		
		this.userName = userName;
		jobName = ModelingForm.getJobName();
		actFileName = dataset.getActFile();
		sdFileName = dataset.getSdfFile();
		datasetName = dataset.getFileName();
		
		actFileDataType = ModelingForm.getActFileDataType();
		descriptorGenerationType = ModelingForm.getDescriptorGenerationType();
		
		//start datasplit parameters
		selectionNextTrainPt = ModelingForm.getSelectionNextTrainPt();
		
		trainTestSplitType = ModelingForm.getTrainTestSplitType();
		if(trainTestSplitType.equalsIgnoreCase(Constants.RANDOM)){
			//random datasplit params
			numSplits = ModelingForm.getNumSplitsInternalRandom();
			randomSplitMinTestSize = ModelingForm.getRandomSplitMinTestSize();
			randomSplitMaxTestSize = ModelingForm.getRandomSplitMaxTestSize();	
		}
		else if(trainTestSplitType.equalsIgnoreCase(Constants.SPHEREEXCLUSION)){
			//sphere exclusion datasplit params
			numSplits = ModelingForm.getNumSplitsInternalSphere();
			splitIncludesMin = ModelingForm.getSplitIncludesMin();
			splitIncludesMax = ModelingForm.getSplitIncludesMax();
			sphereSplitMinTestSize = ModelingForm.getSphereSplitMinTestSize();
			selectionNextTrainPt = ModelingForm.getSelectionNextTrainPt();
		}
						
		//end datasplit parameters
		
		//start kNN parameters
		T1 = ModelingForm.getT1();
		T2 = ModelingForm.getT2();
		TcOverTb = ModelingForm.getTcOverTb();
		minSlopes = ModelingForm.getMinSlopes();
		maxSlopes = ModelingForm.getMaxSlopes();
		Relative_diff_R_R0 = ModelingForm.getRelativeDiffRR0();
		Diff_R01_R02 = ModelingForm.getDiffR01R02();
		knnCategoryOptimization = ModelingForm.getKnnCategoryOptimization();
		maxNumDescriptors = ModelingForm.getMaxNumDescriptors();
		minNumDescriptors = ModelingForm.getMinNumDescriptors();
		stepSize = ModelingForm.getStepSize();
		numCycles = ModelingForm.getNumCycles();
		numMutations = ModelingForm.getNumMutations();
		minAccTraining = ModelingForm.getMinAccTraining();
		minAccTest = ModelingForm.getMinAccTest();
		cutoff = ModelingForm.getCutoff();
		mu = ModelingForm.getMu();
		numRuns = ModelingForm.getNumRuns();
		nearestNeighbors = ModelingForm.getNearest_Neighbors();
		pseudoNeighbors = ModelingForm.getPseudo_Neighbors();
		stop_cond = ModelingForm.getStop_cond();
		datasetID = ModelingForm.getSelectedDatasetId();
		//end kNN parameters
		
		//start SVM parameters
		svmDegreeFrom = ModelingForm.getSvmCostFrom();
		svmDegreeTo = ModelingForm.getSvmCostTo();
		svmDegreeStep = ModelingForm.getSvmDegreeStep();
		
		svmGammaFrom = ModelingForm.getSvmGammaFrom();
		svmGammaTo = ModelingForm.getSvmGammaTo();
		svmGammaStep = ModelingForm.getSvmGammaStep();
		
		svmCostFrom = ModelingForm.getSvmCostFrom();
		svmCostTo = ModelingForm.getSvmCostTo();
		svmCostStep = ModelingForm.getSvmCostStep();
		
		svmNuFrom = ModelingForm.getSvmNuFrom();
		svmNuTo = ModelingForm.getSvmNuTo();
		svmNuStep = ModelingForm.getSvmNuStep();
		
		svmPEpsilonFrom = ModelingForm.getSvmPEpsilonFrom();
		svmPEpsilonTo = ModelingForm.getSvmPEpsilonTo();
		svmPEpsilonStep = ModelingForm.getSvmPEpsilonStep();
		
		svmCrossValidation = ModelingForm.getSvmCrossValidation();
		svmEEpsilon = ModelingForm.getSvmEEpsilon();
		svmHeuristics = ModelingForm.getSvmHeuristics();
		svmKernel = ModelingForm.getSvmKernel();
		svmProbability = ModelingForm.getSvmProbability();
		svmTypeCategory = ModelingForm.getSvmTypeCategory();
		svmTypeContinuous = ModelingForm.getSvmTypeContinuous();
		svmWeight = ModelingForm.getSvmWeight();
		//end SVM parameters

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
		
	}

	public void setUp() throws Exception {
		
		//create Predictor object in DB to allow for recovery of this job if it fails.

		predictor.setName(jobName);
		predictor.setUserName(userName);
		
		predictor.setDatasetId(datasetID);
		predictor.setSdFileName(dataset.getSdfFile());
		predictor.setActFileName(dataset.getActFile());
		predictor.setActivityType(actFileDataType);
		predictor.setModelMethod(modelType);
		
		//descriptors
		predictor.setDescriptorGeneration(descriptorGenerationType);
		predictor.setScalingType(scalingType);
		predictor.setStdDevCutoff(stdDevCutoff);
		predictor.setCorrelationCutoff(correlationCutoff);
		
		//datasplit
		predictor.setNumSplits(numSplits);
		predictor.setTrainTestSplitType(trainTestSplitType);
		
			//if random split
			predictor.setRandomSplitMinTestSize(randomSplitMinTestSize);
			predictor.setRandomSplitMaxTestSize(randomSplitMaxTestSize);
			
			//if sphere exclusion
			predictor.setSplitIncludesMin(splitIncludesMin);
			predictor.setSplitIncludesMax(splitIncludesMax);
			predictor.setSphereSplitMinTestSize(sphereSplitMinTestSize);
			predictor.setSelectionNextTrainPt(selectionNextTrainPt);
			
		//knn params
		predictor.setKnnCategoryOptimization(knnCategoryOptimization);
		predictor.setMinNumDescriptors(minNumDescriptors);
		predictor.setStepSize(stepSize);
		predictor.setMaxNumDescriptors(maxNumDescriptors);
		predictor.setNumCycles(numCycles);
		
		predictor.setNearestNeighbors(nearestNeighbors);
		predictor.setPseudoNeighbors(pseudoNeighbors);
		
		predictor.setNumRuns(numRuns);
		predictor.setNumMutations(numMutations);
		predictor.setT1(T1);
		predictor.setT2(T2);
		predictor.setMu(mu);
		predictor.setTcOverTb(TcOverTb);
		predictor.setCutoff(cutoff);
		predictor.setMinAccTraining(minAccTraining);
		predictor.setMinAccTest(minAccTest);
		predictor.setMinSlopes(minSlopes);
		predictor.setMaxSlopes(maxSlopes);
		predictor.setRelativeDiffRR0(Relative_diff_R_R0);
		predictor.setDiffR01R02(Diff_R01_R02);
		predictor.setStopCond(stop_cond);
		
		Utility.writeToDebug("DEBUG: actFileDataType is " + actFileDataType);
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		if(modelType == Constants.KNN){
			if (actFileDataType.equals(Constants.CONTINUOUS)){
				writeKnnContinuousDefaultFile(filePath + Constants.KNN_DEFAULT_FILENAME);
			}
			else if (actFileDataType.equals(Constants.CATEGORY)){
				writeKnnCategoryDefaultFile(filePath + Constants.KNN_CATEGORY_DEFAULT_FILENAME);
			}
		}
	}
	
	public void preProcess() throws Exception{

		//copy the dataset files to the working directory
		step = Constants.SETUP;
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		GetJobFilesWorkflow.getDatasetFiles(userName, dataset, path);

		//create the descriptors for the dataset and read them in
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		ArrayList<String> chemicalNames = DatasetFileOperations.getSDFCompoundList(path + sdFileName);
		
		Session session = HibernateUtil.getSession();
		DataSet dataset = PopulateDataObjects.getDataSetById(datasetID,session);
		session.close();
		
		String xFileName = "";
		
		/*
		 I bet we could do some awesome stuff with feature selection here.
		 
		Context-dependent feature selection in the context of dataset->descriptor selection:
		- Find some overarching properties of the traintest dataset (activity cliffs? Diversity / Tanimoto coefficient? Number & size of clusters?)
		- Apply those to feature selection. (Which descriptors corellate best to activity? Which ones separate clusters? Which are useful for low/high Tanimoto values?)
		*/
		if(dataset.getDatasetType().equals(Constants.MODELING)){
			//the dataset did not include descriptors so we need to generate them
			step = Constants.PROCDESCRIPTORS;
			if (descriptorGenerationType.equals(Constants.MOLCONNZ)){
				Utility.writeToDebug("Converting MolconnZ output to .x format", userName, jobName);
				ReadDescriptorsFileWorkflow.readMolconnZDescriptors(path + sdFileName + ".molconnz", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.DRAGONH)){
				Utility.writeToDebug("Processing DragonH descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readDragonDescriptors(path + sdFileName + ".dragonH", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.DRAGONNOH)){
				Utility.writeToDebug("Processing DragonNoH descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readDragonDescriptors(path + sdFileName + ".dragonNoH", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.MOE2D)){
				Utility.writeToDebug("Processing MOE2D descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMoe2DDescriptors(path + sdFileName + ".moe2D", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.MACCS)){
				Utility.writeToDebug("Processing MACCS descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMaccsDescriptors(path + sdFileName + ".maccs", descriptorNames, descriptorValueMatrix);
			}
			
			//write out the descriptors for modeling
			xFileName = sdFileName + ".x";
			String descriptorString = Utility.StringArrayListToString(descriptorNames);
			
			WriteDescriptorsFileWorkflow.writeModelingXFile(chemicalNames, descriptorValueMatrix, descriptorString, path + xFileName, scalingType, stdDevCutoff, correlationCutoff);
		}
		else if(dataset.getDatasetType().equals(Constants.MODELINGWITHDESCRIPTORS)){
			//dataset has descriptors already, we don't need to do anything
			xFileName = dataset.getXFile();
		}
		
		//apply the dataset's external split to the generated .X file
		step = Constants.SPLITDATA;
		ArrayList<String> extCompoundArray = DatasetFileOperations.getXCompoundList(path + "ext_0.x");
		String externalCompoundIdString = Utility.StringArrayListToString(extCompoundArray);
		DataSplitWorkflow.splitModelingExternalGivenList(path, actFileName, xFileName, externalCompoundIdString);
		
		//make internal training / test sets for each model
		if(trainTestSplitType.equals(Constants.RANDOM)){
			DataSplitWorkflow.SplitTrainTestRandom(userName, jobName, numSplits, randomSplitMinTestSize, randomSplitMaxTestSize);
		}
		else{
			DataSplitWorkflow.SplitTrainTestSphereExclusion(userName, jobName, numSplits, splitIncludesMin, splitIncludesMax, sphereSplitMinTestSize, selectionNextTrainPt);
		}
		

		if(jobList.equals(Constants.LSF)){
			//copy needed files out to LSF
		}
	}

	public void executeLSF() throws Exception{
		
	}
	
	@SuppressWarnings("unchecked")
	public void executeLocal() throws Exception {
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//Run modeling process
		if(modelType == Constants.KNN){
			step = Constants.YRANDOMSETUP;
			KnnModelBuildingWorkflow.SetUpYRandomization(userName, jobName);
			
			KnnModelBuildingWorkflow.YRandomization(userName, jobName);

			step = Constants.MODELS;
			if(actFileDataType.equals(Constants.CATEGORY)){
				KnnModelBuildingWorkflow.buildKnnCategoryModel(userName, jobName, knnCategoryOptimization, path);
			}else if(actFileDataType.equals(Constants.CONTINUOUS)){
				KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path);
			}

			step = Constants.YMODELS;
			if(actFileDataType.equals(Constants.CATEGORY)){
				KnnModelBuildingWorkflow.buildKnnCategoryModel(userName, jobName, knnCategoryOptimization, path + "yRandom/");
			}else if(actFileDataType.equals(Constants.CONTINUOUS)){
				KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path + "yRandom/");
			}
			
			step = Constants.PREDEXT;
			KnnModelBuildingWorkflow.RunExternalSet(userName, jobName, sdFileName, actFileName);
			
		}
		else { //if(modelType == Constants.SVM){
			throw new Exception("SVM behaviour is still undefined -- don't use it yet!");
		}
	}
	
	public void postProcess() throws Exception {
		if(jobList.equals(Constants.LSF)){
			//copy needed files back from LSF
		}

		//done with modeling. Read output files. 
		step = Constants.READING;
		if (actFileDataType.equals(Constants.CATEGORY)){
			parseCategorykNNOutput(filePath, Constants.MAINKNN);
			parseCategorykNNOutput(filePath+"yRandom/", Constants.RANDOMKNN);
		}else if (actFileDataType.equals(Constants.CONTINUOUS)){
			parseContinuouskNNOutput(filePath, Constants.MAINKNN);
			parseContinuouskNNOutput(filePath+"yRandom/", Constants.RANDOMKNN);
		}

		noModelsGenerated = mainKNNValues.isEmpty();
		if (!noModelsGenerated)
		{
			allExternalValues = parseExternalValidationOutput(filePath + Constants.EXTERNAL_VALIDATION_OUTPUT_FILE, user_path);
			addStdDeviation(allExternalValues,parseConpredStdDev(filePath + Constants.PRED_OUTPUT_FILE));
			sortModels();
		}

		setParameters(filePath, mainKNNValues, Constants.MAINKNN);
		setParameters(filePath+"yRandom/", randomKNNValues, Constants.RANDOMKNN);
		
		//save output to database
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

		predictor.setScalingType(scalingType);
		predictor.setDescriptorGeneration(descriptorGenerationType);
		predictor.setModelMethod(modelType);
		predictor.setName(jobName);
		predictor.setUserName(userName);
		predictor.setActFileName(actFileName);
		predictor.setSdFileName(sdFileName);
		predictor.setNumTotalModels(numTotalModels);
		predictor.setNumTestModels(numTestModels);
		predictor.setNumTrainModels(numTrainModels);

		predictor.setNumyTestModels(yTestModels);
		predictor.setNumyTrainModels(yTrainModels);
		predictor.setNumyTotalModels(yTotalModels);
		predictor.setActivityType(actFileDataType);
		predictor.setStatus("saved");
		predictor.setPredictorType("Private");
		predictor.setDatasetId(datasetID);
		predictor.setHasBeenViewed(Constants.NO);

		if(allkNNValues.size()<1){}else
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
			session.saveOrUpdate(predictor);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		
		File dir=new File(Constants.CECCR_USER_BASE_PATH+userName+"/"+jobName);
		FileAndDirOperations.deleteDir(dir);
	}

	public void delete() throws Exception {
		
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
		BufferedReader in = new BufferedReader(new FileReader(fileLocation + Constants.kNN_OUTPUT_FILE));
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
				Model knnOutput = createContinuousKnnOutputObject(fileLocation, kNNValues, flowType);
				if(flowType.equals(Constants.MAINKNN) ){ mainKNNValues.add(knnOutput);}
				else{ randomKNNValues.add(knnOutput);}
			}
		}
		in.close();
	}
	
	private void parseCategorykNNOutput(String fileLocation, String flowType) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation + Constants.kNN_OUTPUT_FILE));
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
				Model knnOutput = createCategoryKnnOutputObject(fileLocation, kNNValues, flowType);
				if(flowType.equals(Constants.MAINKNN) ){ mainKNNValues.add(knnOutput);}
				else{ randomKNNValues.add(knnOutput);}
			}
		}
		in.close();
	}

	@SuppressWarnings("unchecked")
	private void sortModels() {
		
		java.util.Comparator knnOutputComparator;
		
		if(actFileDataType.equals(Constants.CONTINUOUS))
		{knnOutputComparator = new KnnOutputComparator();}
		else{knnOutputComparator = new CategoryKNNComparator();}
		
		Collections.sort(mainKNNValues, knnOutputComparator);
		Collections.sort(randomKNNValues, knnOutputComparator);
		
		// Get all the models for the database. 
		sortedkNNValues = new ArrayList<Model>();
		sortedYRKNNValues=new ArrayList<Model>();
		
		for (int i = mainKNNValues.size(); i > 0; i--) {
			sortedkNNValues.add(mainKNNValues.get(i - 1));
		}
		for (int i = randomKNNValues.size(); i > 0; i--) {
			sortedYRKNNValues.add(randomKNNValues.get(i - 1));
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
				if(GenericValidator.isFloat(externalValues[Constants.STD_DEVIATION])){
					stdDevValues.add(externalValues[Constants.STD_DEVIATION]);}
				else{
					stdDevValues.add("No value");
				}
			}
			else{
				if(externalValues.length==3){
					stdDevValues.add("No value");
				}
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

	public static Model createContinuousKnnOutputObject(String filePath, String[] kNNValues, String flowType) {
		// The values array starts at 1 - not 0!
		if (kNNValues == null) {
			return null;
		}
		if (kNNValues.length <= 1) {
			return null;
		}
		Model knnOutput = new Model();
		knnOutput.setKnnType(Constants.CONTINUOUS);
		knnOutput.setNnn(Integer.parseInt(kNNValues[Constants.CONTINUOUS_NNN_LOCATION]));
		knnOutput.setQSquared(Float.parseFloat(kNNValues[Constants.CONTINUOUS_Q_SQUARED_LOCATION]));
		knnOutput.setN(Integer.parseInt(kNNValues[Constants.CONTINUOUS_N_LOCATION]));
		knnOutput.setR(Float.parseFloat(kNNValues[Constants.CONTINUOUS_R_LOCATION]));
		knnOutput.setRSquared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R_SQUARED_LOCATION]));
		knnOutput.setR01Squared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R01_SQUARED_LOCATION]));
		knnOutput.setR02Squared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R02_SQUARED_LOCATION]));
		knnOutput.setK1(Float.parseFloat(kNNValues[Constants.CONTINUOUS_K1_LOCATION]));
		knnOutput.setK2(Float.parseFloat(kNNValues[Constants.CONTINUOUS_K2_LOCATION]));
		knnOutput.setFlowType(flowType);
		
		String fileName = kNNValues[25];
		knnOutput.setFile(fileName);
		
		//get descriptors from model file
		//noncritical; if this fails, just write out the error, don't fail the job
		try{
			//fileName contains the .pred file, but we want the .mod file
			fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".mod";
			
			File modelFile = new File(filePath + fileName);
			BufferedReader br = new BufferedReader(new FileReader(modelFile));
			br.readLine(); 
			br.readLine();
			//descriptor names are the third line of the model file
			knnOutput.setDescriptorsUsed(br.readLine());
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		return knnOutput;
	}

	public static Model createCategoryKnnOutputObject(String filePath, String[] kNNValues, String flowType) {
		// The values array starts at 1 - not 0!
		if (kNNValues == null) {
			return null;
		}
		if (kNNValues.length <= 1) {
			return null;
		}
		Model knnOutput = new Model();
		knnOutput.setKnnType(Constants.CATEGORY);
		knnOutput.setNnn(Integer.parseInt(kNNValues[Constants.CATEGORY_NNN_LOCATION]));
		knnOutput.setTrainingAcc(Float
				.parseFloat(kNNValues[Constants.CATEGORY_TRAINING_ACC_LOCATION]));
		knnOutput.setNormalizedTrainingAcc(Float.parseFloat(kNNValues[Constants.CATEGORY_NORMALIZED_TRAINING_ACC_LOCATION]));
		
		knnOutput.setTestAcc(Float.parseFloat(kNNValues[Constants.CATEGORY_TEST_ACC_LOCATION]));
		knnOutput.setNormalizedTestAcc(Float
				.parseFloat(kNNValues[Constants.CATEGORY_NORMALIZED_TEST_ACC_LOCATION]));

		knnOutput.setFlowType(flowType);
		
		String fileName = kNNValues[12];
		knnOutput.setFile(fileName);
		
		//get descriptors from model file
		//noncritical; if this fails, just write out the error, don't fail the job
		try{
			//fileName contains the .pred file, but we want the .mod file
			fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".mod";
			File modelFile = new File(filePath + fileName);
			BufferedReader br = new BufferedReader(new FileReader(modelFile));
			br.readLine(); 
			br.readLine();
			//descriptor names are the third line of the model file
			knnOutput.setDescriptorsUsed(br.readLine());
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
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
		    out.println("Number_Of_Neares_Neighbors: " + nearestNeighbors);
		    out.println("Number_Of_Pseudo_Neighbors: " + pseudoNeighbors);
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
		    out.println("Number_Of_Neares_Neighbors: " + nearestNeighbors);
		    out.println("Number_Of_Pseudo_Neighbors: " + pseudoNeighbors);
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
	
	public Long getDatasetID() {
		return datasetID;
	}
	public void setDatasetID(Long datasetID) {
		this.datasetID = datasetID;
	}
	
	}