package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestModel;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.print.attribute.standard.JobName;

public class RandomForestWorkflow{

	public static void buildRandomForestModels(RandomForestParameters randomForestParameters, String actFileDataType, String scalingType, String workingDir, String jobName) throws Exception{
		String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
		
		String command = "";
		Utility.writeToDebug("Running Random Forest Modeling...");
		preProcessXFile(scalingType, Constants.EXTERNAL_SET_X_FILE, newExternalXFile, workingDir);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + "RF_RAND_sets.list"));
		BufferedReader in = new BufferedReader(new FileReader(workingDir + "RAND_sets.list"));
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String[] data = inputString.split("\\s+");
			preProcessXFile(scalingType, data[0], "RF_" + data[0], workingDir);
			preProcessXFile(scalingType, data[3], "RF_" + data[3], workingDir);
			out.write(inputString.replace(data[0], "RF_" + data[0]).replace(data[3], "RF_" + data[3]) + System.getProperty("line.separator"));
		}
		in.close();
		out.flush();
		out.close();
		
		
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH + "/";
		String buildModelScript = scriptDir + Constants.RF_BUILD_MODEL_RSCRIPT;
		
		// build model script parameter
		String type = actFileDataType.equals(Constants.CATEGORY) ? "classification" : "regression";
		String ntree = randomForestParameters.getNumTrees().trim();
		String mtry = randomForestParameters.getDescriptorsPerTree().trim();
		String classwt = randomForestParameters.getClassWeights().trim().equals("") ? "NULL" : randomForestParameters.getClassWeights().trim();
		command = "Rscript --vanilla " + buildModelScript
					   + " --scriptsDir " + scriptDir
					   + " --workDir " + workingDir
					   + " --externalXFile " + newExternalXFile
					   + " --dataSplitsListFile " + "RF_RAND_sets.list"
					   + " --type " + type
					   + " --ntree " + ntree
					   + " --mtry " + mtry
					   + " --classwt " + classwt;
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
		String modelsListFile = "models.list";
		String command = "Rscript --vanilla " + predictScript
							  + " --scriptsDir " + scriptDir
							  + " --workDir " + workingDir
							  + " --modelsListFile " + modelsListFile
							  + " --xFile " + newXFile;
		
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

	public static ArrayList<RandomForestModel> readRandomForestModels(){
		return null;
	}
	
	public static ArrayList<RandomForestTree> readRandomForestTrees(){
		return null;
	}
	
	public static ArrayList<ExternalValidation> readExternalSetPredictionOutput(String workingDir, Predictor predictor) throws Exception
	{
		ArrayList<ExternalValidation> allExternalValues = new ArrayList<ExternalValidation>();
		BufferedReader in = new BufferedReader(new FileReader(workingDir + Constants.EXTERNAL_SET_A_FILE));
		String inputString;
		
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String data[] = inputString.split("\\s+"); //Note: [0] is the compound name and [1] is the activity value.
			ExternalValidation externalValidationValue = new ExternalValidation();
			externalValidationValue.setPredictor(predictor);
			externalValidationValue.setCompoundId(data[0]);
			externalValidationValue.setActualValue(new Float(data[1]).floatValue());
			allExternalValues.add(externalValidationValue);
		}
		in.close();
		
		in = new BufferedReader(new FileReader(workingDir + "RF_" + Constants.EXTERNAL_SET_X_FILE.replace(".x", ".pred")));
		inputString = in.readLine(); // header
		for(int i=0; i<allExternalValues.size(); i++)
		{
			ExternalValidation externalValidationValue = allExternalValues.get(i);
			inputString = in.readLine();
			String[] data = inputString.split("\\s+"); //Note: [0] is the compound name and the following are the predicted values.
			
			Float[] compoundPredictedValues = new Float[data.length -1];
			
			externalValidationValue.setNumModels(compoundPredictedValues.length);
			
			float sum=0;
			for(int j=0; j<compoundPredictedValues.length; j++)
			{
				compoundPredictedValues[j] = new Float(data[j+1]);
				sum += compoundPredictedValues[j].floatValue();
			}
			
			float mean = sum / compoundPredictedValues.length;
			externalValidationValue.setPredictedValue((new Float(mean)));
			
			double sumDistFromMeanSquared = 0.0;
			for(int j=0; j<compoundPredictedValues.length; j++)
			{
				double distFromMean = compoundPredictedValues[j].doubleValue() - (double)mean;
				sumDistFromMeanSquared += Math.pow(distFromMean, (double)2);
			}
			double stdDev = Math.sqrt(sumDistFromMeanSquared/(double)compoundPredictedValues.length);
			externalValidationValue.setStandDev(Double.toString(stdDev));
		}
		
		return allExternalValues;
	}
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>(); //holds objects to be returned
		
		// Get the predicted values of the forest
		String outputFile = Constants.PRED_OUTPUT_FILE + ".preds";
		Utility.writeToDebug("Reading consensus prediction file: " + workingDir + outputFile);
		BufferedReader in = new BufferedReader(new FileReader(workingDir + outputFile));
		String inputString;
		
		in.readLine(); // first line is the header with the model name
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String[] data = inputString.split("\\s+"); //Note: [0] is the compound name and the following are the predicted values.
			
			PredictionValue p = new  PredictionValue();
			p.setPredictorId(predictorId);
			p.setCompoundName(data[0]);
			
			Float[] compoundPredictedValues = new Float[data.length -1];
			p.setNumTotalModels(compoundPredictedValues.length);
			p.setNumModelsUsed(compoundPredictedValues.length);
			float sum=0;
			for(int i=0; i<compoundPredictedValues.length; i++)
			{
				compoundPredictedValues[i] = new Float(data[i+1]);
				sum += compoundPredictedValues[i].floatValue();
			}
			float mean = sum / compoundPredictedValues.length;
			p.setPredictedValue((new Float(mean)));
			
			double sumDistFromMeanSquared = 0.0;
			for(int i=0; i<compoundPredictedValues.length; i++)
			{
				double distFromMean = compoundPredictedValues[i].doubleValue() - (double)mean;
				sumDistFromMeanSquared += Math.pow(distFromMean, (double)2);
			}
			double stdDev = Math.sqrt(sumDistFromMeanSquared/(double)compoundPredictedValues.length);
			p.setStandardDeviation(new Float(stdDev));
			
			predictionValues.add(p);
		}
		in.close();
		
		return predictionValues;
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