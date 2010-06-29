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
		String command = "";
		Utility.writeToDebug("Running Random Forest Modeling...");
//		Utility.writeToDebug("scalingType: " + scalingType);
//		Utility.writeToDebug("Constants.NOSCALING: " + Constants.NOSCALING);
		if(!scalingType.equals(Constants.NOSCALING)){
			//the last two lines of the .x file need to be removed
			/*
			Utility.writeToDebug("Removing last 2 lines from " + trainingXFile);
			command = "sed 'N;$!P;$!D;$d' < " + trainingXFile + " > " + trainingXFile + ".tmp : mv " + trainingXFile + ".tmp " + trainingXFile;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			p.waitFor();
			
			Utility.writeToDebug("Removing last 2 lines from " + externalXFile);
			command = "sed 'N;$!P;$!D;$d' < " + trainingXFile + " > " + trainingXFile + ".tmp : mv " + trainingXFile + ".tmp " + trainingXFile;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			p.waitFor();
			*/
			
			Utility.writeToDebug("Removing last 2 lines from " + Constants.MODELING_SET_X_FILE);
			command = "sed 'N;$!P;$!D;$d' < " + Constants.MODELING_SET_X_FILE + " > " + Constants.MODELING_SET_X_FILE + ".tmp : mv " + Constants.MODELING_SET_X_FILE + ".tmp " + Constants.MODELING_SET_X_FILE;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			p.waitFor();
			
			Utility.writeToDebug("Removing last 2 lines from " + Constants.EXTERNAL_SET_X_FILE);
			command = "sed 'N;$!P;$!D;$d' < " + Constants.EXTERNAL_SET_X_FILE + " > " + Constants.EXTERNAL_SET_X_FILE + ".tmp : mv " + Constants.EXTERNAL_SET_X_FILE + ".tmp " + Constants.EXTERNAL_SET_X_FILE;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			p.waitFor();
		}
	}

	public static void runRandomForestPrediction() throws Exception{
		
	}
}