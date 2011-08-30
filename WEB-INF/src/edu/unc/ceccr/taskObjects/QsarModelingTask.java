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
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.calculations.RSquaredAndCCR;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.workflows.descriptors.WriteDescriptors;
import edu.unc.ceccr.workflows.modelingPrediction.DataSplit;
import edu.unc.ceccr.workflows.modelingPrediction.ModelingUtilities;
import edu.unc.ceccr.workflows.modelingPrediction.LsfUtilities;
import edu.unc.ceccr.workflows.modelingPrediction.KnnPlus;
import edu.unc.ceccr.workflows.modelingPrediction.RandomForest;
import edu.unc.ceccr.workflows.modelingPrediction.Svm;
import edu.unc.ceccr.workflows.utilities.CreateJobDirectories;
import edu.unc.ceccr.workflows.utilities.CopyJobFiles;

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
	ArrayList<ExternalValidation> externalSetPredictions = new ArrayList<ExternalValidation>();	
	
	//predictor object created during task
	private Predictor predictor;
	private int numExternalCompounds = 0;

	//output
	private boolean noModelsGenerated;
	
	private String step = Constants.SETUP; //stores what step we're on 
	
	public String getProgress(String user){
		try{
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
				
				if(modelType.equals(Constants.KNNSA)){
					//number of models produced so far can be gotten by:
					//cat knn+_models.log | grep q2= | wc 
					
					float p = KnnPlus.getSaModelingProgress(workingDir);
					p += KnnPlus.getSaModelingProgress(workingDir + "yRandom/");
					p /= (getNumTotalModels() * 2);
					p *= 100; //it's a percent
					if(p > 100){
						p = 100;
					}
					percent = " (" + Math.round(p) + "%)";
				}
				else if(modelType.equals(Constants.KNNGA)){
					percent = "";
					float p = KnnPlus.getGaModelingProgress(workingDir);
					p += KnnPlus.getGaModelingProgress(workingDir + "yRandom/");
					p /= (getNumTotalModels() * 2);
					p *= 100; //it's a percent
					if(p < 0){
						p = 0;
					}
					if(p > 100){
						p = 100;
					}
					percent = " (" + Math.round(p) + "%)";
				}
				else if(modelType.equals(Constants.RANDOMFOREST)){
					File dir = new File(workingDir);
					//get num of trees produced so far
					float p = (dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".tree");}}).length);
					dir = new File(workingDir + "yRandom/");
					p += (dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".tree");}}).length);
					//divide by (number of models * trees per model * 2 because of yRandom)
					p /= (getNumTotalModels() * Integer.parseInt(randomForestParameters.getNumTrees()) * 2);
					p *= 100;
					if(p > 100){
						p = 100;
					}
					percent = " (" + Math.round(p) + "%)";
				}
				else if(modelType.equals(Constants.SVM)){
					//get num of models produced so far
					float p = 0;
					if(new File(workingDir + "svm-results.txt").exists()){
						p += FileAndDirOperations.getNumLinesInFile(workingDir + "svm-results.txt");
					}
					if(new File(workingDir + "yRandom/svm-results.txt").exists()){
						p += FileAndDirOperations.getNumLinesInFile(workingDir + "yRandom/svm-results.txt");
					}
					//divide by (number of models * 2 because of yRandom)
					p /= (getNumTotalModels() * 2);
					p *= 100;
					if(p > 100){
						p = 100;
					}
					percent = " (" + Math.round(p) + "%)";
				}
				
			}
			return step + percent;
		}
		catch(Exception ex){
			//checking progress is nonessential, it shouldn't be able to
			//throw exceptions or anything. 
			return step;
		}
	}
	
	public QsarModelingTask(Predictor predictor) throws Exception{
		Utility.writeToDebug("Recovering job from predictor: " + predictor.getName(), userName, jobName);
		this.predictor = predictor;
		
		//get dataset
		datasetID = predictor.getDatasetId();
		Session s = HibernateUtil.getSession();
		dataset = PopulateDataObjects.getDataSetById(datasetID, s);	
		categoryWeights = predictor.getCategoryWeights();
		datasetName = dataset.getName();
		sdFileName = dataset.getSdfFile();
		actFileName = dataset.getActFile();
		actFileDataType = dataset.getModelType();
		datasetPath += dataset.getUserName();
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
		
		if((new File(filePath + "ext_0.x")).exists()){
			ArrayList<String> extCompoundArray = DatasetFileOperations.getXCompoundNames(filePath + "ext_0.x");
			numExternalCompounds = extCompoundArray.size();
			Utility.writeToDebug("Recovering: numExternalCompounds set to " + numExternalCompounds, userName, jobName);
		}
		else{
			Utility.writeToDebug("Recovering: could not find " + filePath + "ext_0.x . numExternalCompounds set to 0.", userName, jobName);
			numExternalCompounds = 0;
		}
			
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
		Utility.writeToDebug("[[Modeling Type: " + ModelingForm.getModelingType(), userName, jobName);
		modelType = ModelingForm.getModelingType();
		scalingType = ModelingForm.getScalingType();
		Utility.writeToDebug("scalingType in QsarModelingTask: " + scalingType);
		
		stdDevCutoff = ModelingForm.getStdDevCutoff();
		correlationCutoff = ModelingForm.getCorrelationCutoff();
		
		Session session = HibernateUtil.getSession();
		dataset = PopulateDataObjects.getDataSetById(ModelingForm.getSelectedDatasetId(),session);
		session.close();
		
		this.userName = userName;
		jobName = ModelingForm.getJobName();
		actFileName = dataset.getActFile();
		sdFileName = dataset.getSdfFile();
		datasetName = dataset.getName();
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
			svmParameters.setSvmDegreeFrom(ModelingForm.getSvmDegreeFrom());
			svmParameters.setSvmDegreeTo(ModelingForm.getSvmDegreeTo());
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
			svmParameters.setSvmCutoff(ModelingForm.getSvmCutoff());
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
		datasetPath += dataset.getUserName();
		datasetPath += "/DATASETS/" + datasetName + "/";
	}

	public Long setUp() throws Exception {
		
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
			Utility.writeToDebug(e, userName, jobName);
		}
		
		//set modeling params id in predictor
		if(modelType.equals(Constants.SVM)){
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
			Utility.writeToDebug(e, userName, jobName);
		} finally {
			session.close();
		}

		lookupId = predictor.getId();
		jobType = Constants.MODELING;
		
		//make sure job dir exists and is empty
		CreateJobDirectories.createDirs(userName, jobName);
		FileAndDirOperations.deleteDirContents(Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/");
		
		return lookupId;
	}
	
	public void preProcess() throws Exception{
		
		//copy the dataset files to the working directory
		step = Constants.SETUP;
		
		CopyJobFiles.getDatasetFiles(userName, dataset, Constants.MODELING, filePath);

		//read in the descriptors for the dataset
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>(); 
		ArrayList<String> chemicalNames = DatasetFileOperations.getACTCompoundNames(filePath + actFileName);
		
		Session session = HibernateUtil.getSession();
		DataSet dataset = PopulateDataObjects.getDataSetById(datasetID,session);
		session.close();

		String xFileName = "";
		
		//read in descriptors from the dataset
		step = Constants.PROCDESCRIPTORS;
		if (descriptorGenerationType.equals(Constants.MOLCONNZ)){
			Utility.writeToDebug("Converting MolconnZ output to .x format and reading", userName, jobName);
			ReadDescriptors.readMolconnZDescriptors(filePath + sdFileName + ".molconnz", descriptorNames, descriptorValueMatrix);

			//ReadDescriptorsFileWorkflow.convertMzToX(filePath + sdFileName + ".mz", filePath);
			//ReadDescriptorsFileWorkflow.readXDescriptors(filePath + sdFileName + ".mz.x", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.CDK)){
			Utility.writeToDebug("Processing CDK descriptors", userName, jobName);
			
			ReadDescriptors.convertCDKToX(filePath + sdFileName + ".cdk", filePath);
			ReadDescriptors.readXDescriptors(filePath + sdFileName + ".cdk.x", descriptorNames, descriptorValueMatrix);
			
			//for CDK descriptors, compounds with errors are skipped.
			//Make sure that any skipped compounds are removed from the list of external compounds
			DatasetFileOperations.removeSkippedCompoundsFromExternalSetList(sdFileName + ".cdk.x", filePath, "ext_0.x");
			DatasetFileOperations.removeSkippedCompoundsFromActFile(sdFileName + ".cdk.x", filePath, actFileName);
			chemicalNames = DatasetFileOperations.getACTCompoundNames(filePath + actFileName);
		}
		else if (descriptorGenerationType.equals(Constants.DRAGONH)){
			Utility.writeToDebug("Processing DragonH descriptors", userName, jobName);
			ReadDescriptors.readDragonDescriptors(filePath + sdFileName + ".dragonH", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.DRAGONNOH)){
			Utility.writeToDebug("Processing DragonNoH descriptors", userName, jobName);
			ReadDescriptors.readDragonDescriptors(filePath + sdFileName + ".dragonNoH", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.MOE2D)){
			Utility.writeToDebug("Processing MOE2D descriptors", userName, jobName);
			ReadDescriptors.readMoe2DDescriptors(filePath + sdFileName + ".moe2D", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.MACCS)){
			Utility.writeToDebug("Processing MACCS descriptors", userName, jobName);
			ReadDescriptors.readMaccsDescriptors(filePath + sdFileName + ".maccs", descriptorNames, descriptorValueMatrix);
		}
		else if (descriptorGenerationType.equals(Constants.UPLOADED)){
			Utility.writeToDebug("Processing UPLOADED descriptors", userName, jobName);
			ReadDescriptors.readXDescriptors(filePath + dataset.getxFile(), descriptorNames, descriptorValueMatrix);
		}
		
		//write out the descriptors into a .x file for modeling
		if(descriptorGenerationType.equals(Constants.UPLOADED)){
			xFileName = dataset.getxFile();
		}
		else{
			xFileName = sdFileName + ".x";
		}
		String descriptorString = Utility.StringArrayListToString(descriptorNames);
		
		WriteDescriptors.writeModelingXFile(chemicalNames, descriptorValueMatrix, descriptorString, filePath + xFileName, scalingType, stdDevCutoff, correlationCutoff);
	
		//apply the dataset's external split(s) to the generated .X file
		step = Constants.SPLITDATA;
		
		ArrayList<String> extCompoundArray = DatasetFileOperations.getXCompoundNames(filePath + "ext_0.x");
		numExternalCompounds = extCompoundArray.size();
		String externalCompoundIdString = Utility.StringArrayListToString(extCompoundArray);
		DataSplit.splitModelingExternalGivenList(filePath, actFileName, xFileName, externalCompoundIdString);
		
		//make internal training / test sets for each model
		if(trainTestSplitType.equals(Constants.RANDOM)){
			DataSplit.SplitTrainTestRandom(userName, jobName, numSplits, randomSplitMinTestSize, randomSplitMaxTestSize, randomSplitSampleWithReplacement);
		}
		else if(trainTestSplitType.equals(Constants.SPHEREEXCLUSION)){
			DataSplit.SplitTrainTestSphereExclusion(userName, jobName, numSplits, splitIncludesMin, splitIncludesMax, sphereSplitMinTestSize, selectionNextTrainPt);
		}
		
		if(jobList.equals(Constants.LSF)){
			String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			
			//get y-randomization ready
			step = Constants.YRANDOMSETUP;
			
			if(modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)){
				ModelingUtilities.SetUpYRandomization(userName, jobName);
				ModelingUtilities.YRandomization(userName, jobName);
			}
			else if(modelType.equals(Constants.RANDOMFOREST)){
				RandomForest.makeRandomForestXFiles(scalingType, Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/");
				RandomForest.SetUpYRandomization(userName, jobName);
			}
			else if(modelType.equals(Constants.SVM)){
				ModelingUtilities.SetUpYRandomization(userName, jobName);
				ModelingUtilities.YRandomization(userName, jobName);
				Svm.writeSvmModelingParamsFile(svmParameters, actFileDataType, filePath+"svm-params.txt", lsfPath);
				Svm.svmPreProcess(svmParameters, actFileDataType, filePath);
				Svm.svmPreProcess(svmParameters, actFileDataType, filePath + "yRandom/");
			}
			//copy needed files out to LSF
			LsfUtilities.makeLsfModelingDirectory(filePath, lsfPath);
		}
	}

	public String executeLSF() throws Exception{
		
		//this function will submit a single LSF job.
		//To submit this workflowTask as multiple jobs (to distribute the computation)
		//change this function and the LsfProcessingThread so that it will work with
		//an LSF jobArray instead.
		
		String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
		String lsfJobId = "";

		step = Constants.MODELS;
		if(modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)){
			lsfJobId = KnnPlus.buildKnnPlusModelsLsf(knnPlusParameters, actFileDataType, modelType, userName, jobName, lsfPath);
		}
		else if(modelType.equals(Constants.SVM)){
			lsfJobId = Svm.buildSvmModelsLsf(lsfPath, userName, jobName);
		}
		
		return lsfJobId;
	}
	
	@SuppressWarnings("unchecked")
	public void executeLocal() throws Exception {
		String path = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";

		//Run modeling process
		if(modelType.equals(Constants.SVM)){
			step = Constants.YRANDOMSETUP;
			ModelingUtilities.SetUpYRandomization(userName, jobName);
			ModelingUtilities.YRandomization(userName, jobName);
			
			Svm.svmPreProcess(svmParameters, actFileDataType, filePath);
			Svm.svmPreProcess(svmParameters, actFileDataType, filePath + "yRandom/");
			Svm.writeSvmModelingParamsFile(svmParameters, actFileDataType, filePath+"svm-params.txt", filePath);
			
			step = Constants.MODELS;
			Svm.buildSvmModels(filePath);

			step = Constants.PREDEXT;
			if(numExternalCompounds > 0){
				Svm.runSvmPrediction(path, "ext_0.x");
			}
			
		}
		else if(modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)){
			step = Constants.YRANDOMSETUP;
			ModelingUtilities.SetUpYRandomization(userName, jobName);
			ModelingUtilities.YRandomization(userName, jobName);
		
			KnnPlus.buildKnnPlusModels(knnPlusParameters, actFileDataType, modelType, path);
			
			step = Constants.PREDEXT;
			if(numExternalCompounds > 0){
				KnnPlus.predictExternalSet(userName, jobName, path, knnPlusParameters.getKnnApplicabilityDomain());
			}
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			step = Constants.YRANDOMSETUP;
			Utility.writeToDebug("making X files", userName, jobName);
			RandomForest.makeRandomForestXFiles(scalingType, Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/");
			Utility.writeToDebug("setting up y-randomization", userName, jobName);
			RandomForest.SetUpYRandomization(userName, jobName);
			
			step = Constants.MODELS;
			Utility.writeToDebug("building models", userName, jobName);
			RandomForest.buildRandomForestModels(randomForestParameters, actFileDataType, scalingType, categoryWeights, path, jobName);
			Utility.writeToDebug("building y-random models", userName, jobName);
			RandomForest.buildRandomForestModels(randomForestParameters, actFileDataType, scalingType, categoryWeights, path + "yRandom/", jobName);
			Utility.writeToDebug("modeling phase done", userName, jobName);
		}
	}
	
	public void postProcess() throws Exception {
		step = Constants.READING;
		//done with modeling. Read output files. 

		
		//first, copy needed files back from LSF if needed
		if(jobList.equals(Constants.LSF)){
			
			String lsfPath = Constants.LSFJOBPATH + userName + "/" + jobName + "/";
			LsfUtilities.retrieveCompletedPredictor(filePath, lsfPath);

			if(numExternalCompounds > 0){
				step = Constants.PREDEXT;
				if(modelType.equals(Constants.KNNSA) || modelType.equals(Constants.KNNGA)){
					KnnPlus.predictExternalSet(userName, jobName, filePath, knnPlusParameters.getKnnApplicabilityDomain());
				}
				else if(modelType.equals(Constants.SVM)){
					Svm.runSvmPrediction(filePath, "ext_0.x");
				}
			}
		}
		
		//the next step is to read in the results from the modeling program,
		//getting data about the models and external prediction values so we can
		//save it to the database.
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		
		ArrayList<KnnModel> knnModels = null;
		ArrayList<KnnPlusModel> knnPlusModels = null;
		ArrayList<SvmModel> svmModels = null;
		ArrayList<RandomForestGrove> randomForestGroves = null;
		ArrayList<RandomForestTree> randomForestTrees = null;
		ArrayList<RandomForestGrove> randomForestYRandomGroves = null;
		ArrayList<RandomForestTree> randomForestYRandomTrees = null;
		
		if(modelType.equals(Constants.KNNGA) || modelType.equals(Constants.KNNSA)){
			//read external set predictions
			if(numExternalCompounds > 0){
				externalSetPredictions = KnnPlus.readExternalPredictionOutput(filePath, predictor);
			}
			
			//read in models and associate them with the predictor
			knnPlusModels = KnnPlus.readModelsFile(filePath, predictor, Constants.NO);
			ArrayList<KnnPlusModel> knnPlusYRandomModels = KnnPlus.readModelsFile(filePath + "yRandom/", predictor, Constants.YES);
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
			randomForestGroves = RandomForest.readRandomForestGroves(filePath, predictor, Constants.NO);
			
			//commit models to database so we get the model id back so we can use it in the trees
			try{
				tx = session.beginTransaction();
				for(RandomForestGrove m: randomForestGroves){
					session.saveOrUpdate(m);
				}
				tx.commit();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex, userName, jobName);
				tx.rollback();
			}

			//read in trees and associate them with each model
			randomForestTrees = new ArrayList<RandomForestTree>();
			for(RandomForestGrove grove: randomForestGroves){
				randomForestTrees.addAll(RandomForest.readRandomForestTrees(filePath, predictor, grove, actFileDataType));
			}
			
			//now do the same for the yRandom run
			//read in models for yRandom and associate them with the predictor
			randomForestYRandomGroves = RandomForest.readRandomForestGroves(filePath + "yRandom/", predictor, Constants.YES);
			
			//commit models to database so we get the model id back so we can use it in the trees
			try{
				tx = session.beginTransaction();
				for(RandomForestGrove m: randomForestYRandomGroves){
					session.saveOrUpdate(m);
				}
				tx.commit();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex, userName, jobName);
				tx.rollback();
			}

			//read in yRandom trees and associate them with each model
			for(RandomForestGrove grove: randomForestYRandomGroves){
				randomForestTrees.addAll(RandomForest.readRandomForestTrees(filePath + "yRandom/", predictor, grove, actFileDataType));
			}
			
			//read external set predictions
			if(numExternalCompounds > 0){
				externalSetPredictions = RandomForest.readExternalSetPredictionOutput(filePath, predictor);
			}
			else{
				Utility.writeToDebug("No external compounds; skipping external set prediction!");
			}
		
			predictor.setNumTotalModels(getNumTotalModels());

			//numTestModels is what's displayed on the output webpage
			//reason is, we may decide to discard some of the models so they
			//will not be used in external set prediction
			//hence, numTestModels may not equal numTotalModels in future.
			predictor.setNumTestModels(getNumTotalModels()); 
		}
		else if(modelType.equals(Constants.SVM)){
			//read in models and associate them with the predictor
			svmModels = new ArrayList<SvmModel>();
			svmModels.addAll(Svm.readSvmModels(filePath, svmParameters.getSvmCutoff()));
			svmModels.addAll(Svm.readSvmModels(filePath + "yRandom/", svmParameters.getSvmCutoff()));

			//get num models info for predictor
			predictor.setNumTotalModels(getNumTotalModels());
			File dir = new File(filePath);
			int numTestModels = (dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}}).length);
			predictor.setNumTestModels(numTestModels);

			predictor.setNumyTotalModels(getNumTotalModels());
			File ydir = new File(filePath + "yRandom/");
			int numYTestModels = (ydir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}}).length);
			predictor.setNumyTestModels(numYTestModels);
			
			//read external set predictions
			if(numExternalCompounds > 0){
				externalSetPredictions = Svm.readExternalPredictionOutput(filePath, predictor.getId());
			}
			
			//clean junk
			Svm.cleanExcessFilesFromDir(filePath);
			Svm.cleanExcessFilesFromDir(filePath + "yRandom/");
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

		if(dataset.getSplitType().equals(Constants.NFOLD)){
			predictor.setPredictorType(Constants.HIDDEN);
		}
		else{
			predictor.setPredictorType(Constants.PRIVATE);
		}
		predictor.setDatasetId(datasetID);
		predictor.setHasBeenViewed(Constants.NO);
		predictor.setJobCompleted(Constants.YES);
		
		//commit the predictor, models, and external set predictions
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(predictor);
			
			if(knnModels != null){
				for(KnnModel m: knnModels){
					m.setPredictorId(predictor.getId());
					session.saveOrUpdate(m);
				}
			}
			else if(knnPlusModels != null){
				for(KnnPlusModel m: knnPlusModels){
					m.setPredictorId(predictor.getId());
					session.saveOrUpdate(m);
				}
			}
			else if(svmModels != null){
				for(SvmModel m: svmModels){
					m.setPredictorId(predictor.getId());
					session.saveOrUpdate(m);
				}
			}
			else if(randomForestTrees != null){
				for(RandomForestTree t: randomForestTrees){
					session.saveOrUpdate(t);
				}
			}
			for(ExternalValidation ev: externalSetPredictions){
				session.saveOrUpdate(ev);
			}
			tx.commit();
		} catch (RuntimeException e) {
			Utility.writeToDebug(e, userName, jobName);
			if (tx != null)
				tx.rollback();
		}
		
		//clean up dirs
		if(modelType.equals(Constants.RANDOMFOREST)){
			RandomForest.cleanUpExcessFiles(Constants.CECCR_USER_BASE_PATH+userName+"/"+jobName +"/");
		}

		//calculate outputs based on ext set predictions and save
		RSquaredAndCCR.addRSquaredAndCCRToPredictor(predictor, session);
		try{
			tx = session.beginTransaction();
			session.saveOrUpdate(predictor);
			tx.commit();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex, userName, jobName);
			tx.rollback();
		}

		if(dataset.getSplitType().equals(Constants.NFOLD)){
			//find parent predictor
			String parentPredictorName = jobName.substring(0,jobName.lastIndexOf("_fold"));
			Predictor parentPredictor = PopulateDataObjects.getPredictorByName(parentPredictorName, predictor.getUserName(), session);
			if(parentPredictor != null && parentPredictor.getJobCompleted() != null){
				//check if all its other children are completed. 
				String[] childIdArray = parentPredictor.getChildIds().split("\\s+");
				int finishedChildPredictors = 0;
				int numTotalModelsTotal = 0;
				for(String childId : childIdArray){
					Predictor childPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(childId), session);
					if(childPredictor.getJobCompleted().equals(Constants.YES)){
						numTotalModelsTotal += childPredictor.getNumTotalModels();
						finishedChildPredictors++;
					}
				}
				int numFolds = Integer.parseInt(dataset.getNumExternalFolds());
				if(finishedChildPredictors == numFolds){
					//if all children are now done, set jobCompleted to YES in the parent predictor.
					parentPredictor.setJobCompleted(Constants.YES);
					parentPredictor.setNumTotalModels(finishedChildPredictors);
					parentPredictor.setModelingParametersId(predictor.getModelingParametersId());
				}
			}
			
			predictor.setParentId(parentPredictor.getId());
			
			//calc r^2 etc for parent as well
			RSquaredAndCCR.addRSquaredAndCCRToPredictor(parentPredictor, session);
			
			//save
			try{
				tx = session.beginTransaction();
				session.saveOrUpdate(parentPredictor);
				session.saveOrUpdate(predictor);
				tx.commit();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex, userName, jobName);
			}
			
			ModelingUtilities.MoveToPredictorsDir(userName, jobName, parentPredictorName);
		}
		else{
			ModelingUtilities.MoveToPredictorsDir(userName, jobName, "");
		}
		session.close();
	}

	public void delete() throws Exception {
		
	}
	
	public String getStatus(){
		return step;
	}
	
	
	//helper functions and get/sets defined below this point.
	
	private int getNumTotalModels(){
		if(numSplits == null){
			return 0;
		}
		int numModels = Integer.parseInt(numSplits);
		
		if(modelType.equals(Constants.KNNSA)){
			numModels *= Integer.parseInt(knnPlusParameters.getSaNumRuns());
			numModels *= Integer.parseInt(knnPlusParameters.getSaNumBestModels());
			
			int numDescriptorSizes = 1;
			if(Integer.parseInt(knnPlusParameters.getKnnDescriptorStepSize()) != 0){
				numDescriptorSizes += (Integer.parseInt(knnPlusParameters.getKnnMaxNumDescriptors()) - 
						Integer.parseInt(knnPlusParameters.getKnnMinNumDescriptors())) /
						Integer.parseInt(knnPlusParameters.getKnnDescriptorStepSize());
			}
			numModels *= numDescriptorSizes;
		}
		else if(modelType.equals(Constants.RANDOMFOREST)){
			numModels = Integer.parseInt(predictor.getNumSplits());
		}
		else if(modelType.equals(Constants.SVM)){
			numModels = Integer.parseInt(predictor.getNumSplits());
			Double numDifferentCosts = Math.ceil((Double.parseDouble(svmParameters.getSvmCostTo()) - 
					Double.parseDouble(svmParameters.getSvmCostFrom())) / 
					Double.parseDouble(svmParameters.getSvmCostStep()) + 0.0001);

			Double numDifferentDegrees = Math.ceil((Double.parseDouble(svmParameters.getSvmDegreeTo()) - 
					Double.parseDouble(svmParameters.getSvmDegreeFrom())) / 
					Double.parseDouble(svmParameters.getSvmDegreeStep()) + 0.0001);

			Double numDifferentGammas = Math.ceil((Double.parseDouble(svmParameters.getSvmGammaTo()) - 
					Double.parseDouble(svmParameters.getSvmGammaFrom())) / 
					Double.parseDouble(svmParameters.getSvmGammaStep()) + 0.0001);

			Double numDifferentNus = Math.ceil((Double.parseDouble(svmParameters.getSvmNuTo()) - 
					Double.parseDouble(svmParameters.getSvmNuFrom())) / 
					Double.parseDouble(svmParameters.getSvmNuStep()) + 0.0001);

			Double numDifferentPEpsilons = Math.ceil((Double.parseDouble(svmParameters.getSvmPEpsilonTo()) - 
					Double.parseDouble(svmParameters.getSvmPEpsilonFrom())) / 
					Double.parseDouble(svmParameters.getSvmPEpsilonStep()) + 0.0001);
			
			String svmType = "";
			if(actFileDataType.equals(Constants.CATEGORY)){
				svmType = svmParameters.getSvmTypeCategory();
			}
			else{
				svmType = svmParameters.getSvmTypeContinuous();
			}
			
			if(svmType.equals("0")){
				numDifferentPEpsilons = 1.0;
				numDifferentNus = 1.0;
			}
			else if(svmType.equals("1")){
				numDifferentPEpsilons = 1.0;
				numDifferentCosts = 1.0;
			}
			else if(svmType.equals("3")){
				numDifferentNus = 1.0;
			}
			else if(svmType.equals("4")){
				numDifferentPEpsilons = 1.0;
			}
			
			if(svmParameters.getSvmKernel().equals("0")){
				numDifferentGammas = 1.0;
				numDifferentDegrees = 1.0;
			}
			else if(svmParameters.getSvmKernel().equals("1")){
				//no change
			}
			else if(svmParameters.getSvmKernel().equals("2")){
				numDifferentDegrees = 1.0;
			}
			else if(svmParameters.getSvmKernel().equals("3")){
				numDifferentDegrees = 1.0;
			}
			
			numModels *= numDifferentCosts * numDifferentDegrees * numDifferentGammas * numDifferentNus * numDifferentPEpsilons;
			
		}
		return numModels;
	}
	
    public void setStep(String step){
    	this.step = step;
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