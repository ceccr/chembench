package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

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
//		String trainingXFile = workingDir + Constants.MODELING_SET_X_FILE;
//		String externalXFile = workingDir + Constants.EXTERNAL_SET_X_FILE;

		String command = "";
		Utility.writeToDebug("Running Random Forest Modeling...");
//		Utility.writeToDebug("scalingType: " + scalingType);
//		Utility.writeToDebug("Constants.NOSCALING: " + Constants.NOSCALING);
		if(!scalingType.equals(Constants.NOSCALING)){
			//the last two lines of the .x file need to be removed
			
			Utility.writeToDebug("Removing last 2 lines from " + Constants.MODELING_SET_X_FILE);
			command = "rm2LastLines.sh " + Constants.MODELING_SET_X_FILE;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "rm2LastLines_" + Constants.MODELING_SET_X_FILE, p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Exit value: " + p.exitValue());
			if(p.exitValue() != 0)
			{
				Utility.writeToDebug("	See error log");
			}
			
			Utility.writeToDebug("Removing last 2 lines from " + Constants.EXTERNAL_SET_X_FILE);
			command = "rm2LastLines.sh " + Constants.EXTERNAL_SET_X_FILE;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "rm2LastLines_" + Constants.EXTERNAL_SET_X_FILE, p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Exit value: " + p.exitValue());
			if(p.exitValue() != 0)
			{
				Utility.writeToDebug("	See error log");
			}
		}
		
//		Utility.writeToDebug("numTrees: " + randomForestParameters.getNumTrees());
//		Utility.writeToDebug("trainSetSize: " + randomForestParameters.getTrainSetSize());
//		Utility.writeToDebug("descriptorsPerTree: " + randomForestParameters.getDescriptorsPerTree());
//		Utility.writeToDebug("sampleWithReplacement: " + randomForestParameters.getSampleWithReplacement());
//		Utility.writeToDebug("classWeights: " + randomForestParameters.getClassWeights());
		
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
//		Utility.writeToDebug("scriptDir: " + scriptDir);
		String buildModelScript = scriptDir + Constants.RF_BUILD_MODEL_RSCRIPT;
//		Utility.writeToDebug("buildModelScript: " + buildModelScript);
		
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
					   + " --trainingFile " + Constants.MODELING_SET_X_FILE
					   + " --activityFile " + Constants.MODELING_SET_A_FILE
					   + " --modelFile " + modelFile
					   + " --modelName " + modelName
					   + " --type " + type
					   + " --ntree " + ntree
					   + " --mtry " + mtry
					   + " --replace " + replace
					   + " --classwt " + classwt
					   + " --sampsize " + sampsize;
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

	public static void runRandomForestPrediction(String workingDir, String jobName) throws Exception{
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
		String predictScript = scriptDir + Constants.RF_PREDICT_RSCRIPT;
		String modelFile = jobName + ".RData";
		String modelName = jobName;
		String predictionFile = jobName + ".pred";
		String command = "Rscript --vanilla " + predictScript
							  + " --scriptsDir " + scriptDir
							  + " --modelFile " + modelFile
							  + " --modelName " + modelName
							  + " --externalFile " + Constants.EXTERNAL_SET_X_FILE
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
}