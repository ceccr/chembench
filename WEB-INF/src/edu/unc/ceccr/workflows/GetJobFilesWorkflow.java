package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.global.Constants;

public class GetJobFilesWorkflow{
	
	public static void getDatasetFiles(String userName, DataSet dataset, String toDir) throws Exception{
		//gathers the dataset files needed for a modeling or prediction run
		
		String allUserDir = Constants.CECCR_USER_BASE_PATH + "all-users/DATASETS/" + dataset.getFileName() + "/";
		String userFilesDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + dataset.getFileName() + "/";
		
		String fromDir = "";
		if(dataset.getUserName().equals(userName)){
			//get it from the user's DATASET directory
			fromDir = userFilesDir;
		}
		else{
			fromDir = allUserDir;
		}
		
		String sdFile = dataset.getSdfFile();
		String actFile = dataset.getActFile();
		String xFile = dataset.getXFile();
		String externalSplitXFile = "ext_0.x";
		
		Utility.writeToDebug("Fetching dataset files from " + userFilesDir, userName, "");
		if(!sdFile.equals("")){
			FileAndDirOperations.copyFile(fromDir + sdFile, toDir + sdFile);
		}
		if(!actFile.equals("")){
			FileAndDirOperations.copyFile(fromDir + actFile, toDir + actFile);
		}
		if(!xFile.equals("")){
			FileAndDirOperations.copyFile(fromDir + xFile, toDir + xFile);
		}
		if(!externalSplitXFile.equals("")){
			FileAndDirOperations.copyFile(fromDir + externalSplitXFile, toDir + externalSplitXFile);
		}
	}
	
	public static void getPredictorFiles(String userName, Predictor predictor, String toDir) throws Exception{
		//gathers the predictor files needed for a prediction run
		String fromDir;
		
		if(predictor.getUserName().equals(userName)){
			fromDir = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + predictor.getName();
		}
		else{
			fromDir = Constants.CECCR_USER_BASE_PATH + "all-users/PREDICTORS/" + predictor.getName();
		}
		Utility.writeToDebug("Copying predictor from " + fromDir, userName, "");
		FileAndDirOperations.copyDirContents(fromDir, toDir, false);
	}
}