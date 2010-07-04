package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.print.attribute.standard.JobName;

public class RandomForestWorkflow{

	public static void buildRandomForestModels(RandomForestParameters randomForestParameters, String actFileDataType, String scalingType, String workingDir, String jobName) throws Exception{
		String newTrainingXFile = "RF_" +Constants.MODELING_SET_X_FILE;
		String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
		
		String command = "";
		Utility.writeToDebug("Running Random Forest Modeling...");
		preProcessXFile(scalingType, Constants.MODELING_SET_X_FILE, newTrainingXFile, workingDir);
		preProcessXFile(scalingType, Constants.EXTERNAL_SET_X_FILE, newExternalXFile, workingDir);
		/*
		if(scalingType.equals(Constants.NOSCALING))
		{
			preProcessScript = "copy.sh ";
			preProcessMsg = "Copy: ";
		}
		else
		{
			preProcessScript = "rm2LastLines.sh ";
			preProcessMsg = "Copy and remove last 2 lines: ";
		}
		Utility.writeToDebug(preProcessMsg + Constants.MODELING_SET_X_FILE + " to " + trainingXFileForRF);
		command = preProcessScript + Constants.MODELING_SET_X_FILE + " " + trainingXFileForRF;
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, preProcessScript.replace(".sh", "_") + Constants.MODELING_SET_X_FILE, p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
		
		Utility.writeToDebug(preProcessMsg + Constants.EXTERNAL_SET_X_FILE + " to " + externalXFileForRF);
		command = preProcessScript + Constants.EXTERNAL_SET_X_FILE + " " + externalXFileForRF;
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, preProcessScript.replace(".sh", "_") + Constants.EXTERNAL_SET_X_FILE, p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
		*/
		
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
		String buildModelScript = scriptDir + Constants.RF_BUILD_MODEL_RSCRIPT;
		
		// build model script parameter
		String modelFile = jobName + ".RData";
		String modelName = jobName;
		String type = actFileDataType.equals(Constants.CATEGORY) ? "classification" : "regression";
		String ntree = randomForestParameters.getNumTrees().trim();
		String mtry = randomForestParameters.getDescriptorsPerTree().trim();
		String replace = randomForestParameters.getSampleWithReplacement().trim().toUpperCase();
		String classwt = randomForestParameters.getClassWeights().trim().equals("") ? "NULL" : randomForestParameters.getClassWeights().trim();
		String sampsize = randomForestParameters.getTrainSetSize().trim();
		command = "Rscript --vanilla " + buildModelScript
					   + " --scriptsDir " + scriptDir
					   + " --trainingXFile " + newTrainingXFile
					   + " --trainingActFile " + Constants.MODELING_SET_A_FILE
					   + " --externalXFile " + newExternalXFile
					   + " --externalActFile " + Constants.EXTERNAL_SET_A_FILE
					   + " --modelFile " + modelFile
					   + " --modelName " + modelName
					   + " --type " + type
					   + " --ntree " + ntree
					   + " --mtry " + mtry
					   + " --replace " + replace
					   + " --classwt " + classwt
					   + " --sampsize " + sampsize
					   + " --keep.forest TRUE";
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "randomForestBuildModel", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
	}

	public static void runRandomForestPrediction(String workingDir, String jobName, String sdfile, Predictor predictor) throws Exception{
		String xFile = sdfile + ".renorm.x";
		String newXFile = "RF_" + xFile;
		preProcessXFile(predictor.getScalingType(), xFile, newXFile, workingDir);
		
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
		String predictScript = scriptDir + Constants.RF_PREDICT_RSCRIPT;
		String modelName = predictor.getName();
		String modelFile = modelName + ".RData";
		
		String predictionFile = jobName + ".pred";
		String command = "Rscript --vanilla " + predictScript
							  + " --scriptsDir " + scriptDir
							  + " --modelFile " + modelFile
							  + " --modelName " + modelName
							  + " --xFile " + newXFile
							  + " --predictionFile " + predictionFile;
		
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "randomForestPredict", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
	}
	
	public static String readConfusionMatrix(String workingDir){
		return "";
	}
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		//see readPredictionOutput implementation in KnnPredictionWorkflow.java
		//to get an idea of how this works
		
		return null;
	}
	
	private static void preProcessXFile(String scalingType, String xFile, String newXFile, String workingDir) throws Exception
	{
		String preProcessScript;
		String preProcessMsg;
		String command;
		if(scalingType.equals(Constants.NOSCALING))
		{
			preProcessScript = "copy.sh ";
			preProcessMsg = "Copy: ";
		}
		else
		{
			preProcessScript = "rm2LastLines.sh ";
			preProcessMsg = "Copy and remove last 2 lines: ";
		}
		Utility.writeToDebug(preProcessMsg + xFile + " to " + newXFile);
		command = preProcessScript + xFile + " " + newXFile;
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, preProcessScript.replace(".sh", "_") + xFile, p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
	}
}