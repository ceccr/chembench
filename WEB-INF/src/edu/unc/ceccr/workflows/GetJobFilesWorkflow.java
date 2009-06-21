package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.global.Constants;

public class GetJobFilesWorkflow{
	
	public static void getDatasetFiles(String userName, String jobName, String sdFile, String actFile, boolean isAllUser, String dataType, String datasetName) throws Exception{
		//gathers the files needed for a modeling run
		
		String jobDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String allUserDir = Constants.CECCR_USER_BASE_PATH + "all-users/DATASETS/" + datasetName + "/";
		String userFilesDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + datasetName + "/";
		
		if(! isAllUser){
			//get it from the user's DATASET directory
			Utility.writeToDebug("Fetching data from " + userFilesDir, userName, jobName);
			FileAndDirOperations.copyFile(userFilesDir + sdFile, jobDir + sdFile);
			FileAndDirOperations.copyFile(userFilesDir + actFile, jobDir + actFile);
		}
		else{
			//get it from the all-users directory
			Utility.writeToDebug("Fetching data from " + allUserDir, userName, jobName);
			FileAndDirOperations.copyFile(allUserDir + sdFile, jobDir + sdFile);
			FileAndDirOperations.copyFile(allUserDir + actFile, jobDir + actFile);
		}
	}
	
	public static void GetKnnPredictionFiles(String userName, String jobName, String sdFile, boolean sdfIsAllUser, boolean predictorIsAllUser, String predictorName, String datasetName) throws Exception{
		//if the user selected a dataset instead of uploading
		//we need to copy the .SDF they requested into their current dir.
		String jobDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String allUserDir = Constants.CECCR_USER_BASE_PATH + "all-users/DATASETS/" + datasetName + "/";
		String userFilesDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + datasetName + "/";
		
		Utility.writeToDebug("Called GetKnnPredictionFiles with args: " + userName + " " + jobName + " " + sdFile + " " + sdfIsAllUser + " " + predictorIsAllUser + " " + predictorName + " " + datasetName);
		//Get the SD file
		if(! sdfIsAllUser){
			//copy SDF from the user's DATASETS directory
			FileAndDirOperations.copyFile(userFilesDir + sdFile, jobDir + sdFile);
		}
		else{
			//get it from the all-users/DATASETS directory
			FileAndDirOperations.copyFile(allUserDir + sdFile, jobDir + sdFile);
		}
		
		//Get the predictor
		String fromDir;
		String toDir = jobDir;
		
		if(predictorIsAllUser){
			fromDir = Constants.CECCR_USER_BASE_PATH + "all-users/PREDICTORS/" + predictorName;
		}
		else{
			fromDir = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + predictorName;
		}
		Utility.writeToDebug("Copying predictor from " + fromDir);
		FileAndDirOperations.copyDirContents(fromDir, toDir, false);
	}
}