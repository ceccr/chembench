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
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.DataSplitWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnModelBuildingWorkflow;
import edu.unc.ceccr.workflows.KnnModelingLsfWorkflow;
import edu.unc.ceccr.workflows.KnnPlusWorkflow;
import edu.unc.ceccr.workflows.RandomForestWorkflow;
import edu.unc.ceccr.workflows.ReadDescriptorsFileWorkflow;
import edu.unc.ceccr.workflows.SvmWorkflow;
import edu.unc.ceccr.workflows.WriteDescriptorsFileWorkflow;

public class QsarModelingTask extends WorkflowTask {

	//job details
	private String sdFileName;
	private String actFileName;
	private String user_path;
	private String userName;
	private String jobName;
	private String modelType; // (svm, knn, etc) 
	
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

	private KnnParameters knnParameters;
	private SvmParameters svmParameters;
	private RandomForestParameters randomForestParameters;
	private KnnPlusParameters knnPlusParameters;
		
	//technically these are probably kNN parameters? Not sure what they're for.
	int numTotalModels;
	int numTestModels;
	int numTrainModels;
	int yTotalModels;
	int yTestModels;
	int yTrainModels;
	//end 
	
	
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
		if(step.equals(Constants.MODELS)){
			String workingDir = "";
			if(jobList.equals(Constants.LSF)){
				//running on LSF so check LSF dir
				workingDir = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			}
			else{
				//running locally so check local dir
				 workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/"; 
			}
			
			float p = FileAndDirOperations.countFilesInDirMatchingPattern(workingDir, ".*mod");
		 	workingDir += "yRandom/";
			p += FileAndDirOperations.countFilesInDirMatchingPattern(workingDir, ".*mod");
			//divide by the number of models to be built

			int numModels = getNumTotalModels();
			numModels *= 2; //include yRandom models also
			p /= numModels;
			p *= 100; //it's a percent
			percent = " (" + Math.round(p) + "%)";
		}
		
		return step + percent;
	}
	
	public QsarModelingTask(Predictor predictor) throws Exception{
		this.predictor = predictor;
		
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
			
		//modeling params
		if(predictor.getModelMethod().equals(Constants.KNN)){
			knnParameters = PopulateDataObjects.getKnnParametersById(predictor.getModelingParametersId(), s);
		}
		else if(predictor.getModelMethod().equals(Constants.SVM)){
			svmParameters = PopulateDataObjects.getSvmParametersById(predictor.getModelingParametersId(), s);
		}
		else if(predictor.getModelMethod().equals(Constants.KNNSA) || 
				predictor.getModelMethod().equals(Constants.KNNGA)){
			knnPlusParameters = PopulateDataObjects.getKnnPlusParametersById(predictor.getModelingParametersId(), s);
		}
		else if(predictor.getModelMethod().equals(Constants.RANDOMFOREST)){
			randomForestParameters = PopulateDataObjects.getRandomForestParametersById(predictor.getModelingParametersId(), s);
		}
		s.close();
		
	}
	
	public QsarModelingTask(String userName, ModelingFormActions ModelingForm) throws Exception {
		
		//This function just loads all the ModelingForm parameters into local variables
		Utility.writeToDebug("[[Modeling Type: " + ModelingForm.getModelingType());
		modelType = ModelingForm.getModelingType();
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
		datasetID = ModelingForm.getSelectedDatasetId();
		
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
		
		//load modeling parameters from form
		if(ModelingForm.getModelingType().equals(Constants.KNN)){
			knnParameters = new KnnParameters();
			
			knnParameters.setT1(ModelingForm.getT1());
			knnParameters.setT2(ModelingForm.getT2());
			knnParameters.setTcOverTb(ModelingForm.getTcOverTb());
			knnParameters.setMinSlopes(ModelingForm.getMinSlopes());
			knnParameters.setMaxSlopes(ModelingForm.getMaxSlopes());
			knnParameters.setRelativeDiffRR0(ModelingForm.getRelativeDiffRR0());
			knnParameters.setDiffR01R02(ModelingForm.getDiffR01R02());
			knnParameters.setKnnCategoryOptimization(ModelingForm.getKnnCategoryOptimization());
			knnParameters.setMaxNumDescriptors(ModelingForm.getMaxNumDescriptors());
			knnParameters.setMinNumDescriptors(ModelingForm.getMinNumDescriptors());
			knnParameters.setStepSize(ModelingForm.getStepSize());
			knnParameters.setNumCycles(ModelingForm.getNumCycles());
			knnParameters.setNumMutations(ModelingForm.getNumMutations());
			knnParameters.setMinAccTraining(ModelingForm.getMinAccTraining());
			knnParameters.setMinAccTest(ModelingForm.getMinAccTest());
			knnParameters.setCutoff(ModelingForm.getCutoff());
			knnParameters.setMu(ModelingForm.getMu());
			knnParameters.setNumRuns(ModelingForm.getNumRuns());
			knnParameters.setNearestNeighbors(ModelingForm.getNearest_Neighbors());
			knnParameters.setPseudoNeighbors(ModelingForm.getPseudo_Neighbors());
			knnParameters.setStopCond(ModelingForm.getStop_cond());
		}
		else if(ModelingForm.getModelingType().equals(Constants.SVM)){
			svmParameters = new SvmParameters();

			svmParameters.setSvmDegreeFrom(ModelingForm.getSvmCostFrom());
			svmParameters.setSvmDegreeTo(ModelingForm.getSvmCostTo());
			svmParameters.setSvmDegreeStep(ModelingForm.getSvmDegreeStep());
			svmParameters.setSvmGammaFrom(ModelingForm.getSvmGammaFrom());
			svmParameters.setSvmGammaTo(ModelingForm.getSvmGammaTo());
			svmParameters.setSvmGammaStep(ModelingForm.getSvmGammaStep());
			svmParameters.setSvmCostFrom(ModelingForm.getSvmCostFrom());
			svmParameters.setSvmCostTo(ModelingForm.getSvmCostTo());
			svmParameters.setSvmCostStep(ModelingForm.getSvmCostStep());
			svmParameters.setSvmNuFrom(ModelingForm.getSvmNuFrom());
			svmParameters.setSvmNuTo(ModelingForm.getSvmNuTo());
			svmParameters.setSvmNuStep(ModelingForm.getSvmNuStep());
			svmParameters.setSvmPEpsilonFrom(ModelingForm.getSvmPEpsilonFrom());
			svmParameters.setSvmPEpsilonTo(ModelingForm.getSvmPEpsilonTo());
			svmParameters.setSvmPEpsilonStep(ModelingForm.getSvmPEpsilonStep());
			svmParameters.setSvmCrossValidation(ModelingForm.getSvmCrossValidation());
			svmParameters.setSvmEEpsilon(ModelingForm.getSvmEEpsilon());
			svmParameters.setSvmHeuristics(ModelingForm.getSvmHeuristics());
			svmParameters.setSvmKernel(ModelingForm.getSvmKernel());
			svmParameters.setSvmProbability(ModelingForm.getSvmProbability());
			svmParameters.setSvmTypeCategory(ModelingForm.getSvmTypeCategory());
			svmParameters.setSvmTypeContinuous(ModelingForm.getSvmTypeContinuous());
			svmParameters.setSvmWeight(ModelingForm.getSvmWeight());
		}
		else if(ModelingForm.getModelingType().equals(Constants.KNNSA) || 
				ModelingForm.getModelingType().equals(Constants.KNNGA)){
			knnPlusParameters = new KnnPlusParameters();
			
			//DEBUG - print out knnPlus params from both forms, check which one
			//gets taken in case of duplicates. (May need to compress 2 tabs into one.)
			Utility.writeToDebug("KNN PLUS PARAMETERS:\n" +
					"Applicability Domain: " + ModelingForm.getKnnApplicabilityDomain() + "\n" +
					"Min Training: " + ModelingForm.getKnnMinTraining() + "\n" +
					"Min Test: " + ModelingForm.getKnnMinTest());
		}
		else if(ModelingForm.getModelingType().equals(Constants.RANDOMFOREST)){
			randomForestParameters = new RandomForestParameters();

			randomForestParameters.setClassWeights(ModelingForm.getClassWeights());
			randomForestParameters.setDescriptorsPerTree(ModelingForm.getDescriptorsPerTree());
			randomForestParameters.setNumTrees(ModelingForm.getNumTrees());
			randomForestParameters.setSampleWithReplacement(ModelingForm.getSampleWithReplacement());
			randomForestParameters.setTrainSetSize(ModelingForm.getTrainSetSize());
		}
		
		//end load modeling parameters from form

		this.predictor = new Predictor();
		
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
		predictor.setJobCompleted(Constants.NO);
		
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
			
			
		//save modeling params to database
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			if(knnParameters != null)
				session.saveOrUpdate(knnParameters);
			if(svmParameters != null)
				session.saveOrUpdate(svmParameters);
			if(knnPlusParameters != null)
				session.saveOrUpdate(knnPlusParameters);
			if(randomForestParameters != null)
				session.saveOrUpdate(randomForestParameters);
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		}
		
		//set modeling params id in predictor
		if(modelType.equals(Constants.KNN)){
			
			predictor.setModelingParametersId(knnParameters.getId());	
		}
		else if(modelType.equals(Constants.SVM)){
			predictor.setModelingParametersId(svmParameters.getId());	
		}
		else if(modelType.equals(Constants.KNNGA) ||
				modelType.equals(Constants.KNNSA)){
			predictor.setModelingParametersId(knnPlusParameters.getId());	
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			predictor.setModelingParametersId(randomForestParameters.getId());	
		}

		//save predictor to DB
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

		lookupId = predictor.getPredictorId();
		jobType = Constants.MODELING;
		
		//make sure directories exist
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		if(modelType.equals(Constants.KNN)){
			if (actFileDataType.equals(Constants.CONTINUOUS)){
				KnnModelBuildingWorkflow.writeKnnContinuousDefaultFile(filePath + Constants.KNN_DEFAULT_FILENAME, knnParameters);
			}
			else if (actFileDataType.equals(Constants.CATEGORY)){
				KnnModelBuildingWorkflow.writeKnnCategoryDefaultFile(filePath + Constants.KNN_CATEGORY_DEFAULT_FILENAME, knnParameters);
			}
		}
	}
	
	public void preProcess() throws Exception{
		
		//copy the dataset files to the working directory
		step = Constants.SETUP;
		
		GetJobFilesWorkflow.getDatasetFiles(userName, dataset, filePath);

		//create the descriptors for the dataset and read them in
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>(); 
		ArrayList<String> chemicalNames = DatasetFileOperations.getSDFCompoundList(filePath + sdFileName);
		
		Session session = HibernateUtil.getSession();
		DataSet dataset = PopulateDataObjects.getDataSetById(datasetID,session);
		session.close();
		
		String xFileName = "";
		
		if(dataset.getDatasetType().equals(Constants.MODELING)){
			//read in descriptors from the dataset
			step = Constants.PROCDESCRIPTORS;
			if (descriptorGenerationType.equals(Constants.MOLCONNZ)){
				Utility.writeToDebug("Converting MolconnZ output to .x format", userName, jobName);
				ReadDescriptorsFileWorkflow.readMolconnZDescriptors(filePath + sdFileName + ".molconnz", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.DRAGONH)){
				Utility.writeToDebug("Processing DragonH descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readDragonDescriptors(filePath + sdFileName + ".dragonH", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.DRAGONNOH)){
				Utility.writeToDebug("Processing DragonNoH descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readDragonDescriptors(filePath + sdFileName + ".dragonNoH", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.MOE2D)){
				Utility.writeToDebug("Processing MOE2D descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMoe2DDescriptors(filePath + sdFileName + ".moe2D", descriptorNames, descriptorValueMatrix);
			}
			else if (descriptorGenerationType.equals(Constants.MACCS)){
				Utility.writeToDebug("Processing MACCS descriptors", userName, jobName);
				ReadDescriptorsFileWorkflow.readMaccsDescriptors(filePath + sdFileName + ".maccs", descriptorNames, descriptorValueMatrix);
			}
			
			//write out the descriptors into a .x file for modeling
			xFileName = sdFileName + ".x";
			String descriptorString = Utility.StringArrayListToString(descriptorNames);
			
			WriteDescriptorsFileWorkflow.writeModelingXFile(chemicalNames, descriptorValueMatrix, descriptorString, filePath + xFileName, scalingType, stdDevCutoff, correlationCutoff);
		}
		else if(dataset.getDatasetType().equals(Constants.MODELINGWITHDESCRIPTORS)){
			//dataset has .x file already, we're done
			xFileName = dataset.getXFile();
		}
		
		//apply the dataset's external split to the generated .X file
		step = Constants.SPLITDATA;
		ArrayList<String> extCompoundArray = DatasetFileOperations.getXCompoundList(filePath + "ext_0.x");
		String externalCompoundIdString = Utility.StringArrayListToString(extCompoundArray);
		DataSplitWorkflow.splitModelingExternalGivenList(filePath, actFileName, xFileName, externalCompoundIdString);
		
		//make internal training / test sets for each model
		if(trainTestSplitType.equals(Constants.RANDOM)){
			DataSplitWorkflow.SplitTrainTestRandom(userName, jobName, numSplits, randomSplitMinTestSize, randomSplitMaxTestSize);
		}
		else{
			DataSplitWorkflow.SplitTrainTestSphereExclusion(userName, jobName, numSplits, splitIncludesMin, splitIncludesMax, sphereSplitMinTestSize, selectionNextTrainPt);
		}
		
		if(jobList.equals(Constants.LSF)){
			//copy needed files out to LSF
			String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			
			if(modelType.equals(Constants.KNN)){
				step = Constants.YRANDOMSETUP;
				KnnModelBuildingWorkflow.SetUpYRandomization(userName, jobName);
				KnnModelBuildingWorkflow.YRandomization(userName, jobName);
			}

			KnnModelingLsfWorkflow.makeLsfModelingDirectory(filePath, lsfPath);
		}
	}

	public String executeLSF() throws Exception{
		
		//this function will submit a single LSF job.
		//To submit this workflowTask as multiple jobs (to distribute the computation)
		//change this function and the LsfProcessingThread so that it will work with
		//an LSF jobArray instead.
		
		String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
		String lsfJobId = "";
		
		if(modelType.equals(Constants.KNN)){
			step = Constants.MODELS;
			if(actFileDataType.equals(Constants.CONTINUOUS)){
				lsfJobId = KnnModelingLsfWorkflow.buildKnnContinuousModel(userName, jobName, lsfPath);
			}
			else if(actFileDataType.equals(Constants.CATEGORY)){
				lsfJobId = KnnModelingLsfWorkflow.buildKnnCategoryModel(userName, jobName, knnParameters.getKnnCategoryOptimization(), lsfPath);
			}
		}
		else {//if(modelType.equals(Constants.SVM)){
			throw new Exception("SVM behaviour is still undefined -- don't use it yet!");
		}
		
		return lsfJobId;
	}
	
	@SuppressWarnings("unchecked")
	public void executeLocal() throws Exception {
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//Run modeling process
		if(modelType.equals(Constants.KNN)){
			step = Constants.YRANDOMSETUP;
			KnnModelBuildingWorkflow.SetUpYRandomization(userName, jobName);
			KnnModelBuildingWorkflow.YRandomization(userName, jobName);

			step = Constants.MODELS;
			if(actFileDataType.equals(Constants.CATEGORY)){
				KnnModelBuildingWorkflow.buildKnnCategoryModel(userName, jobName, knnParameters.getKnnCategoryOptimization(), path);
			}else if(actFileDataType.equals(Constants.CONTINUOUS)){
				KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path);
			}
			
			if(actFileDataType.equals(Constants.CATEGORY)){
				KnnModelBuildingWorkflow.buildKnnCategoryModel(userName, jobName, knnParameters.getKnnCategoryOptimization(), path + "yRandom/");
			}else if(actFileDataType.equals(Constants.CONTINUOUS)){
				KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path + "yRandom/");
			}
			
			step = Constants.PREDEXT;
			KnnModelBuildingWorkflow.RunExternalSet(userName, jobName, sdFileName, actFileName);
			
		}
		else if(modelType.equals(Constants.SVM)){
			SvmWorkflow.buildSvmModels(svmParameters, actFileDataType, path);
		}
		else if(modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)){
			KnnPlusWorkflow.buildKnnPlusModels(knnPlusParameters, actFileDataType, path);
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			RandomForestWorkflow.buildRandomForestModels(randomForestParameters, actFileDataType, path);
		}
	}
	
	public void postProcess() throws Exception {
		step = Constants.READING;
		//done with modeling. Read output files. 
		
		if(jobList.equals(Constants.LSF)){
			//copy needed files back from LSF
			String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			KnnModelingLsfWorkflow.retrieveCompletedPredictor(filePath, lsfPath);
			
			if(modelType.equals(Constants.KNN)){
				step = Constants.PREDEXT;
				KnnModelBuildingWorkflow.RunExternalSet(userName, jobName, sdFileName, actFileName);
			}
		}
		
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
		   
		//save updated predictor to database
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
		predictor.setJobCompleted(Constants.YES);

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
	
	public String getStatus(){
		return step;
	}
	
	
	//helper functions and member variables defined below this point.
	//Most of these should be moved into Workflows package.
	
	private int getNumTotalModels(){
		 int numModels = Integer.parseInt(numSplits);
		if(modelType.equals(Constants.KNN)){
			numModels *= Integer.parseInt(knnParameters.getNumRuns());
			int numDescriptorSizes = 0;
			for(int i = Integer.parseInt(knnParameters.getMinNumDescriptors()); i <= Integer.parseInt(knnParameters.getMaxNumDescriptors()); i += Integer.parseInt(knnParameters.getStepSize())){
				numDescriptorSizes++;
			}
			numModels *= numDescriptorSizes;
		}
		
		return numModels;
	}
	
	private void setParameters(String path, ArrayList<Model> KNNValues, String flow) {
		File dir;
		dir = new File(path);
        int total, test, train;
        
        total= dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}}).length;
        total = getNumTotalModels();
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
		
		//Utility.writeToDebug(((Integer)Constants.NUM_MODELS).toString());
		
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
	
	public Long getDatasetID() {
		return datasetID;
	}
	public void setDatasetID(Long datasetID) {
		this.datasetID = datasetID;
	}

	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	
	

}