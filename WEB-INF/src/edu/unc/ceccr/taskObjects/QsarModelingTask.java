package edu.unc.ceccr.taskObjects;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import java.io.PrintStream;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.action.ModelingFormActions;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateDirectoriesWorkflow;
import edu.unc.ceccr.workflows.DataSplitWorkflow;
import edu.unc.ceccr.workflows.GetJobFilesWorkflow;
import edu.unc.ceccr.workflows.KnnModelBuildingWorkflow;
import edu.unc.ceccr.workflows.KnnOutputWorkflow;
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
	private String categoryWeights;
	
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
		private String randomSplitSampleWithReplacement;		
	
		//if sphere exclusion
		private String splitIncludesMin;
		private String splitIncludesMax;
		private String sphereSplitMinTestSize;
		private String selectionNextTrainPt;

	//sets of input parameters
	private KnnParameters knnParameters;
	private SvmParameters svmParameters;
	private RandomForestParameters randomForestParameters;
	private KnnPlusParameters knnPlusParameters;
	
	//predicted external set values
	ArrayList<ExternalValidation> externalSetPredictions = null;	
	
	//predictor object created during task
	private Predictor predictor;

	//output
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
			
			if(modelType.equals(Constants.KNN)){
				
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
			else if(modelType.equals(Constants.KNNSA)){
				//number of models produced so far can be gotten by:
				//cat knn+.log | grep q2= | wc 
				//which is in a script "checkKnnSaProgress.sh" in mmlsoft/bin.
				
				float p = KnnPlusWorkflow.getSaModelingProgress(workingDir);
				p /= getNumTotalModels();
				p *= 100; //it's a percent
				percent = " (" + Math.round(p) + "%)";
			}
			else if(modelType.equals(Constants.KNNGA)){
				float p = KnnPlusWorkflow.getGaModelingProgress(workingDir);
				p /= getNumTotalModels();
				p *= 100; //it's a percent
				if(p < 0){
					p = 0;
				}
				percent = " (" + Math.round(p) + "%)";
			}
			else if(modelType.equals(Constants.RANDOMFOREST)){
				File dir = new File(workingDir);
				//get num of trees produced so far
				float p = (dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".tree");}}).length);
				//divide by (number of models * trees per model)
				p /= (getNumTotalModels() * Integer.parseInt(randomForestParameters.getNumTrees()));
				p *= 100;
				percent = " (" + Math.round(p) + "%)";
			}
		}
		
		return step + percent;
	}
	
	public QsarModelingTask(Predictor predictor) throws Exception{
		this.predictor = predictor;
		
		//get dataset
		datasetID = predictor.getDatasetId();
		Session s = HibernateUtil.getSession();
		dataset = PopulateDataObjects.getDataSetById(datasetID, s);	
		categoryWeights = predictor.getCategoryWeights();
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
		filePath = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
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
			randomSplitSampleWithReplacement = predictor.getRandomSplitSampleWithReplacement();
			
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
		
		categoryWeights = ModelingForm.getCategoryWeights();
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
			randomSplitSampleWithReplacement = ModelingForm.getRandomSplitSampleWithReplacement();
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
			knnParameters.setMinNumDescriptors(ModelingForm.getMinNumDescriptors());
			knnParameters.setMaxNumDescriptors(ModelingForm.getMaxNumDescriptors());
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
			knnPlusParameters.setGaMaxNumGenerations(ModelingForm.getGaMaxNumGenerations());
			knnPlusParameters.setGaMinFitnessDifference(ModelingForm.getGaMinFitnessDifference());
			knnPlusParameters.setGaNumStableGenerations(ModelingForm.getGaNumStableGenerations());
			knnPlusParameters.setGaPopulationSize(ModelingForm.getGaPopulationSize());
			knnPlusParameters.setGaTournamentGroupSize(ModelingForm.getGaTournamentGroupSize());
			knnPlusParameters.setKnnApplicabilityDomain(ModelingForm.getKnnApplicabilityDomain());
			knnPlusParameters.setKnnDescriptorStepSize(ModelingForm.getKnnDescriptorStepSize());
			knnPlusParameters.setKnnSaErrorBasedFit(ModelingForm.getKnnSaErrorBasedFit());
			knnPlusParameters.setKnnGaErrorBasedFit(ModelingForm.getKnnGaErrorBasedFit());
			knnPlusParameters.setKnnMaxNearestNeighbors(ModelingForm.getKnnMaxNearestNeighbors());
			knnPlusParameters.setKnnMinNearestNeighbors(ModelingForm.getKnnMinNearestNeighbors());
			knnPlusParameters.setKnnMaxNumDescriptors(ModelingForm.getKnnMaxNumDescriptors());
			knnPlusParameters.setKnnMinNumDescriptors(ModelingForm.getKnnMinNumDescriptors());
			knnPlusParameters.setKnnMinTest(ModelingForm.getKnnMinTest());
			knnPlusParameters.setKnnMinTraining(ModelingForm.getKnnMinTraining());
			knnPlusParameters.setSaFinalTemp(ModelingForm.getSaFinalTemp());
			knnPlusParameters.setSaLogInitialTemp(ModelingForm.getSaLogInitialTemp());
			knnPlusParameters.setSaMutationProbabilityPerDescriptor(ModelingForm.getSaMutationProbabilityPerDescriptor());
			knnPlusParameters.setSaNumBestModels(ModelingForm.getSaNumBestModels());
			knnPlusParameters.setSaNumRuns(ModelingForm.getSaNumRuns());
			knnPlusParameters.setSaTempConvergence(ModelingForm.getSaTempConvergence());
			knnPlusParameters.setSaTempDecreaseCoefficient(ModelingForm.getSaTempDecreaseCoefficient());
		}
		else if(ModelingForm.getModelingType().equals(Constants.RANDOMFOREST)){
			randomForestParameters = new RandomForestParameters();

			randomForestParameters.setDescriptorsPerTree(ModelingForm.getDescriptorsPerTree());
			randomForestParameters.setNumTrees(ModelingForm.getNumTrees());
			randomForestParameters.setMaxNumTerminalNodes(ModelingForm.getMaxNumTerminalNodes());
			randomForestParameters.setMinTerminalNodeSize(ModelingForm.getMinTerminalNodeSize());
		}
		
		//end load modeling parameters from form

		this.predictor = new Predictor();
		
		filePath = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
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
		predictor.setCategoryWeights(categoryWeights);
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
			predictor.setRandomSplitSampleWithReplacement(randomSplitSampleWithReplacement);
			
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
		
		//make sure job dir exists and is empty
		CreateDirectoriesWorkflow.createDirs(userName, jobName);
		FileAndDirOperations.deleteDirContents(Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/");
		
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
			DataSplitWorkflow.SplitTrainTestRandom(userName, jobName, numSplits, randomSplitMinTestSize, randomSplitMaxTestSize, randomSplitSampleWithReplacement);
		}
		else{
			DataSplitWorkflow.SplitTrainTestSphereExclusion(userName, jobName, numSplits, splitIncludesMin, splitIncludesMax, sphereSplitMinTestSize, selectionNextTrainPt);
		}
		
		if(jobList.equals(Constants.LSF)){
			String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			
			//get y-randomization ready
			step = Constants.YRANDOMSETUP;
			KnnModelBuildingWorkflow.SetUpYRandomization(userName, jobName);
			KnnModelBuildingWorkflow.YRandomization(userName, jobName);

			//copy needed files out to LSF
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
		else if(modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)){
			step = Constants.MODELS;
			lsfJobId = KnnPlusWorkflow.buildKnnPlusModelsLsf(knnPlusParameters, actFileDataType, modelType, userName, jobName, lsfPath);
		}
		else {//if(modelType.equals(Constants.SVM)){
			throw new Exception("SVM behaviour is still undefined -- don't use it yet!");
		}
		
		return lsfJobId;
	}
	
	@SuppressWarnings("unchecked")
	public void executeLocal() throws Exception {
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";

		step = Constants.MODELS;
		
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
			}
			else if(actFileDataType.equals(Constants.CONTINUOUS)){
				KnnModelBuildingWorkflow.buildKnnContinuousModel(userName, jobName, path + "yRandom/");
			}
			
			step = Constants.PREDEXT;
			KnnModelBuildingWorkflow.RunExternalSet(userName, jobName, sdFileName, actFileName);
			
		}
		else if(modelType.equals(Constants.SVM)){
			SvmWorkflow.buildSvmModels(svmParameters, actFileDataType, path);
		}
		else if(modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)){
			step = Constants.YRANDOMSETUP;
			KnnModelBuildingWorkflow.SetUpYRandomization(userName, jobName);
			KnnModelBuildingWorkflow.YRandomization(userName, jobName);
		
			KnnPlusWorkflow.buildKnnPlusModels(knnPlusParameters, actFileDataType, modelType, path);
			
			step = Constants.PREDEXT;
			KnnPlusWorkflow.predictExternalSet(userName, jobName, path, knnPlusParameters.getKnnApplicabilityDomain());
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			RandomForestWorkflow.buildRandomForestModels(randomForestParameters, actFileDataType, scalingType, categoryWeights, path, jobName);
			RandomForestWorkflow.SetUpYRandomization(userName, jobName);
			RandomForestWorkflow.YRandomization(userName, jobName, actFileDataType, randomForestParameters);
		}
	}
	
	public void postProcess() throws Exception {
		step = Constants.READING;
		//done with modeling. Read output files. 

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		
		//first, copy needed files back from LSF if needed
		if(jobList.equals(Constants.LSF)){
			String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			KnnModelingLsfWorkflow.retrieveCompletedPredictor(filePath, lsfPath);
			
			if(modelType.equals(Constants.KNN)){
				step = Constants.PREDEXT;
				KnnModelBuildingWorkflow.RunExternalSet(userName, jobName, sdFileName, actFileName);
			}
			else if(modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)){
				step = Constants.PREDEXT;
				KnnPlusWorkflow.predictExternalSet(userName, jobName, filePath, knnPlusParameters.getKnnApplicabilityDomain());
			}
		}
		
		//the next step is to read in the results from the modeling program,
		//getting data about the models and external prediction values so we can
		//save it to the database.

		ArrayList<KnnModel> knnModels = null;
		ArrayList<KnnPlusModel> knnPlusModels = null;
		ArrayList<SvmModel> svmModels = null;
		ArrayList<RandomForestGrove> randomForestGroves = null;
		ArrayList<RandomForestTree> randomForestTrees = null;
		ArrayList<RandomForestGrove> randomForestYRandomGroves = null;
		ArrayList<RandomForestTree> randomForestYRandomTrees = null;
		
		if(modelType.equals(Constants.KNN)){
			ArrayList<KnnModel> yRandomModels = null;
			
			if (actFileDataType.equals(Constants.CATEGORY)){
				knnModels = KnnOutputWorkflow.parseCategorykNNOutput(filePath, Constants.MAINKNN);
				yRandomModels = KnnOutputWorkflow.parseCategorykNNOutput(filePath+"yRandom/", Constants.RANDOMKNN);
			} 
			else if (actFileDataType.equals(Constants.CONTINUOUS)){
				knnModels = KnnOutputWorkflow.parseContinuouskNNOutput(filePath, Constants.MAINKNN);
				yRandomModels = KnnOutputWorkflow.parseContinuouskNNOutput(filePath+"yRandom/", Constants.RANDOMKNN);
			}
			
			//get counts of the number of models that were created
			File dir;
			dir = new File(filePath);
			File yRandomDir = new File(filePath + "yRandom/");
			
	        predictor.setNumTotalModels(dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}}).length);
	        predictor.setNumTestModels(knnModels.size());
	        predictor.setNumTrainModels(dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}	}).length - knnModels.size());
	        
	        predictor.setNumyTotalModels(yRandomDir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}}).length);
	        predictor.setNumyTestModels(yRandomModels.size());
			predictor.setNumyTrainModels(yRandomDir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}	}).length - yRandomModels.size());
	        
			
			if(knnModels.isEmpty()){
				noModelsGenerated = true;
				Utility.writeToDebug("Warning: No models were generated.");
			}
			
			//Add the yRandom models into the knnModels list
			knnModels.addAll(yRandomModels);
			
			//Sort the models in decreasing order by r^2 (continuous) or test set accuracy (category)
			if(actFileDataType.equals(Constants.CONTINUOUS)){ 
				Collections.sort(knnModels, new Comparator<KnnModel>() {
				    public int compare(KnnModel one, KnnModel two) {
						return (one.getRSquared().compareTo(two.getRSquared()));
				    }});
				
			}
			else{
				Collections.sort(knnModels, new Comparator<KnnModel>() {
				    public int compare(KnnModel one, KnnModel two) {
						return (one.getNormalizedTestAcc().compareTo(two.getNormalizedTestAcc()));
				    }});
			}
			
			//read external validation set predictions
			if (!noModelsGenerated) {
				externalSetPredictions = KnnOutputWorkflow.parseExternalValidationOutput(filePath + Constants.EXTERNAL_VALIDATION_OUTPUT_FILE);
				KnnOutputWorkflow.addStdDeviation(externalSetPredictions, filePath + Constants.PRED_OUTPUT_FILE);

				for (ExternalValidation ev : externalSetPredictions){
					ev.setPredictor(predictor);
				}
			}
		}
		else if(modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)){
			//read external set predictions
			externalSetPredictions = KnnPlusWorkflow.readExternalPredictionOutput(filePath, predictor);
			
			//read in models and associate them with the predictor
			knnPlusModels = KnnPlusWorkflow.readModelsFile(filePath, predictor, Constants.NO);
			ArrayList<KnnPlusModel> knnPlusYRandomModels = KnnPlusWorkflow.readModelsFile(filePath + "yRandom/", predictor, Constants.YES);
			predictor.setNumTotalModels(getNumTotalModels());
			predictor.setNumTestModels(knnPlusModels.size());
			predictor.setNumyTotalModels(getNumTotalModels());
			predictor.setNumyTestModels(knnPlusYRandomModels.size());
			
			if(! knnPlusYRandomModels.isEmpty()){
				knnPlusModels.addAll(knnPlusYRandomModels);
			}
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			//read in models and associate them with the predictor
			randomForestGroves = RandomForestWorkflow.readRandomForestGroves(filePath, predictor, Constants.NO);
			
			//commit models to database so we get the model id back so we can use it in the trees
			try{
				tx = session.beginTransaction();
				for(RandomForestGrove m: randomForestGroves){
					session.saveOrUpdate(m);
				}
				tx.commit();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
				tx.rollback();
			}

			//read in trees and associate them with each model
			randomForestTrees = new ArrayList<RandomForestTree>();
			for(RandomForestGrove grove: randomForestGroves){
				randomForestTrees.addAll(RandomForestWorkflow.readRandomForestTrees(filePath, predictor, grove, actFileDataType));
			}
			
			//now do the same for the yRandom run
			//read in models for yRandom and associate them with the predictor
			String yRandomDir = filePath + "yRandom/";
			randomForestYRandomGroves = RandomForestWorkflow.readRandomForestGroves(filePath, predictor, Constants.YES);
			
			//commit models to database so we get the model id back so we can use it in the trees
			try{
				tx = session.beginTransaction();
				for(RandomForestGrove m: randomForestYRandomGroves){
					session.saveOrUpdate(m);
				}
				tx.commit();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
				tx.rollback();
			}

			//read in yRandom trees and associate them with each model
			for(RandomForestGrove grove: randomForestYRandomGroves){
				randomForestTrees.addAll(RandomForestWorkflow.readRandomForestTrees(filePath, predictor, grove, actFileDataType));
			}
			
			//read external set predictions
			externalSetPredictions = RandomForestWorkflow.readExternalSetPredictionOutput(filePath, predictor);
			File dir;
			dir = new File(filePath);
		
			predictor.setNumTotalModels(getNumTotalModels());

			//numTestModels is what's displayed on the output webpage
			//reason is, we may decide to discard some of the models so they
			//will not be used in external set prediction
			//hence, numTestModels may not equal numTotalModels in future.
			predictor.setNumTestModels(getNumTotalModels()); 
		}
		else if(modelType.equals(Constants.SVM)){
			//read in models and associate them with the predictor
			svmModels = SvmWorkflow.readSvmModels();

			//read external set predictions
			externalSetPredictions = null;
		}
		
		//save updated predictor to database
		predictor.setScalingType(scalingType);
		predictor.setCategoryWeights(categoryWeights);
		predictor.setDescriptorGeneration(descriptorGenerationType);
		predictor.setModelMethod(modelType);
		predictor.setName(jobName);
		predictor.setUserName(userName);
		predictor.setActFileName(actFileName);
		predictor.setSdFileName(sdFileName);
		predictor.setActivityType(actFileDataType);
		predictor.setStatus("saved");
		predictor.setPredictorType("Private");
		predictor.setDatasetId(datasetID);
		predictor.setHasBeenViewed(Constants.NO);
		predictor.setJobCompleted(Constants.YES);
		
		if(externalSetPredictions != null){
			predictor.setExternalValidationResults(new HashSet<ExternalValidation>(externalSetPredictions));
		}
		
		//commit the predictor and models
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(predictor);
			
			if(knnModels != null){
				for(KnnModel m: knnModels){
					m.setPredictor(predictor);
					session.saveOrUpdate(m);
				}
			}
			else if(knnPlusModels != null){
				for(KnnPlusModel m: knnPlusModels){
					m.setPredictor(predictor);
					session.saveOrUpdate(m);
				}
			}
			else if(svmModels != null){
				for(SvmModel m: svmModels){
					session.saveOrUpdate(m);
				}
			}
			else if(randomForestTrees != null){
				for(RandomForestTree t: randomForestTrees){
					session.saveOrUpdate(t);
				}
			}
			
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
		
		//clean up dirs
		KnnModelBuildingWorkflow.MoveToPredictorsDir(userName, jobName);
		FileAndDirOperations.deleteDir(new File(Constants.CECCR_USER_BASE_PATH+userName+"/"+jobName));
	}

	public void delete() throws Exception {
		
	}
	
	public String getStatus(){
		return step;
	}
	
	
	//helper functions and get/sets defined below this point.
	
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
		else if(modelType.equals(Constants.KNNSA)){
			numModels *= Integer.parseInt(knnPlusParameters.getSaNumRuns());
			numModels *= Integer.parseInt(knnPlusParameters.getSaNumBestModels());
			int numDescriptorSizes = 0;
			for(int i = Integer.parseInt(knnPlusParameters.getKnnMinNumDescriptors()); 
			i <= Integer.parseInt(knnPlusParameters.getKnnMaxNumDescriptors()); 
			i += Integer.parseInt(knnPlusParameters.getKnnDescriptorStepSize())){
				numDescriptorSizes++;
			}
			numModels *= numDescriptorSizes;
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			numModels = Integer.parseInt(predictor.getNumSplits());
		}
		
		return numModels;
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