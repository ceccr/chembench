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

public class RandomForestWorkflow{

	public static void buildRandomForestModels(RandomForestParameters randomForestParameters, String actFileDataType, String scalingType, String workingDir) throws Exception{
//		String trainingXFile = workingDir + Constants.MODELING_SET_X_FILE;
//		String externalXFile = workingDir + Constants.EXTERNAL_SET_X_FILE;
		String logsDir = workingDir + "Logs/";
		String command = "";
		Utility.writeToDebug("Running Random Forest Modeling...");
//		Utility.writeToDebug("scalingType: " + scalingType);
//		Utility.writeToDebug("Constants.NOSCALING: " + Constants.NOSCALING);
		if(!scalingType.equals(Constants.NOSCALING)){
			//the last two lines of the .x file need to be removed
			
			Utility.writeToDebug("Removing last 2 lines from " + Constants.MODELING_SET_X_FILE);
			command = "rm2LastLines.sh " + Constants.MODELING_SET_X_FILE + ".s";
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
//			Utility.writeProgramLogfile(workingDir, "rm2LastLines_" + Constants.MODELING_SET_X_FILE, p.getInputStream(), p.getErrorStream());
			Utility.writeProgramLogfile(workingDir, "rm2LastLines", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Exit value: " + p.exitValue());
			if(p.exitValue() != 0)
			{
				Utility.writeToDebug("	See error log");
			}
			
			Utility.writeToDebug("Removing last 2 lines from " + Constants.EXTERNAL_SET_X_FILE);
			command = "rm2LastLines.sh " + Constants.EXTERNAL_SET_X_FILE + ".s";
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			p = Runtime.getRuntime().exec(command, null, new File(workingDir));
//			Utility.writeProgramLogfile(workingDir, "rm2LastLines_" + Constants.EXTERNAL_SET_X_FILE, p.getInputStream(), p.getErrorStream());
			Utility.writeProgramLogfile(workingDir, "rm2LastLines", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Exit value: " + p.exitValue());
			if(p.exitValue() != 0)
			{
				Utility.writeToDebug("	See error log");
			}
		}
		
		Utility.writeToDebug("numTrees: " + randomForestParameters.getNumTrees());
		Utility.writeToDebug("trainSetSize: " + randomForestParameters.getTrainSetSize());
		Utility.writeToDebug("descriptorsPerTree: " + randomForestParameters.getDescriptorsPerTree());
		Utility.writeToDebug("sampleWithReplacement: " + randomForestParameters.getSampleWithReplacement());
		Utility.writeToDebug("classWeights: " + randomForestParameters.getClassWeights());
	}

	public static void runRandomForestPrediction() throws Exception{
		
	}
}