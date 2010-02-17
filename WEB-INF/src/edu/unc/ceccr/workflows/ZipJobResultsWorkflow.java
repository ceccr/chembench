
package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

import java.util.ArrayList;
import java.util.zip.*;

public class ZipJobResultsWorkflow{

	public static void ZipEntireDirectory(String workingDir, String projectDir, String zipFile) throws Exception{
		//will be used for MML members - they can access all files on every project type

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> dirNames = new ArrayList<String>();
		dirNames.add(projectDir);
		
		for(int i = 0; i < dirNames.size(); i++){
			//read through the directory's files.
			//Add each subdirectory to dirNames.
			//Add each file to fileNames.
			
			File dirFile = new File(workingDir + dirNames.get(i));
			String[] dirFilenames = dirFile.list();

			int x = 0;
			while(dirFilenames != null && x<dirFilenames.length){
				if((new File(workingDir + dirNames.get(i) + dirFilenames[x])).isDirectory()){
					dirNames.add(dirNames.get(i) + dirFilenames[x] + "/");
				}
				else{
					fileNames.add(dirNames.get(i) + dirFilenames[x]);
				}
				x++;
			}
		}
		Utility.writeToDebug("Compressing " + workingDir + projectDir + " : " + fileNames.size() + " files into " + zipFile);
		
		
		for(String fileName : fileNames){
			try{
				FileInputStream in = new FileInputStream(workingDir + fileName);
				out.putNextEntry(new ZipEntry(fileName));
				int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		out.close();
	}
	
	public static void ZipDatasets(String userName, String datasetName, String zipFile) throws Exception{
		Utility.writeToDebug("Creating archive of dataset: " + datasetName);
	    // These are the files to include in the ZIP file
		if(userName.equals(Constants.ALL_USERS_USERNAME)){
			userName = "all-users";
		}
		String projectSubDir = userName + "/DATASETS/" + datasetName + "/";
		if(projectSubDir.contains("..") || projectSubDir.contains("~")){
			//someone's trying to download something they shouldn't be!
			return;
		}
		String projectDir = Constants.CECCR_USER_BASE_PATH + projectSubDir;
		
		File file = new File(zipFile);
		if(file.exists()){
			FileAndDirOperations.deleteFile(zipFile);
		}
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		
		/*
			Dataset Output zipfile should contain:
			train_0.a
			ext_0.a
			*.act
			*.sdf
			*.x
			Logs/*
			Visualization/Structures/*
			Visualization/Sketches/*
		*/
		
		ArrayList<String> datasetFiles = new ArrayList<String>();
		
		//add in the basic dataset files
		datasetFiles.add(Constants.MODELING_SET_A_FILE);
		datasetFiles.add(Constants.EXTERNAL_SET_A_FILE);
		
		//add in the .act, .sdf, and .x files
		File projectDirFile = new File(projectDir);
		String[] projectDirFilenames = projectDirFile.list();
		if(projectDirFilenames == null){
			Utility.writeToDebug("Error reading directory: " + projectDir);
		}
		int x = 0;
		while(projectDirFilenames != null && x<projectDirFilenames.length){
			if(projectDirFilenames[x].endsWith(".act") 
					|| projectDirFilenames[x].endsWith(".sdf") 
					|| projectDirFilenames[x].endsWith(".x")){
				datasetFiles.add(projectDirFilenames[x]);
			}
			x++;
		}
		
		//add the Logs files in
		File ProjectDirLogsFile = new File(projectDir + "Logs/");
		String[] projectDirLogsFilenames = ProjectDirLogsFile.list();
		x = 0;
		while(projectDirLogsFilenames != null && x<projectDirLogsFilenames.length){
			datasetFiles.add("Logs/" + projectDirLogsFilenames[x]);
			x++;
		}
		
		//add the Visualization/Structures dir
		File ProjectDirStructuresFile = new File(projectDir + "Visualization/Structures/");
		String[] ProjectDirStructuresFilenames = ProjectDirStructuresFile.list();
		x = 0;
		while(ProjectDirStructuresFilenames != null && x<ProjectDirStructuresFilenames.length){
			datasetFiles.add("Visualization/Structures/" + ProjectDirStructuresFilenames[x]);
			x++;
		}
		
		//add in the Visualization/Sketches dir
		File ProjectDirSketchesFile = new File(projectDir + "Visualization/Sketches/");
		String[] ProjectDirSketchesFilenames = ProjectDirSketchesFile.list();
		x = 0;
		while(ProjectDirSketchesFilenames != null && x<ProjectDirSketchesFilenames.length){
			datasetFiles.add("Visualization/Sketches/" + ProjectDirSketchesFilenames[x]);
			x++;
		}
		
		//if descriptorUser, add Descriptors and Descriptor Logs
		if(Utility.canDownloadDescriptors(userName)){
			File ProjectDirDescriptorsFile = new File(projectDir + "Descriptors/");
			Utility.writeToDebug("Downloading descriptors dir: " + projectDir + "Descriptors/");
			String[] ProjectDirDescriptorsFilenames = ProjectDirDescriptorsFile.list();
			Utility.writeToDebug("desc dir size: " + ProjectDirDescriptorsFilenames.length);
			x = 0;
			while(ProjectDirDescriptorsFilenames != null && x<ProjectDirDescriptorsFilenames.length){
				datasetFiles.add("Descriptors/" + ProjectDirDescriptorsFilenames[x]);
				x++;
			}
			File ProjectDirDescriptorsLogsFile = new File(projectDir + "Descriptors/Logs/");
			Utility.writeToDebug("Downloading descriptors log dir: " + projectDir + "Descriptors/Logs");
			String[] ProjectDirDescriptorsLogsFilenames = ProjectDirDescriptorsLogsFile.list();
			Utility.writeToDebug("log dir size: " + ProjectDirDescriptorsLogsFilenames.length);
			x = 0;
			while(ProjectDirDescriptorsLogsFilenames != null && x<ProjectDirDescriptorsLogsFilenames.length){
				datasetFiles.add("Descriptors/Logs/" + ProjectDirDescriptorsLogsFilenames[x]);
				x++;
			}
		}
		
		//datasetFiles now contains names of all the files we need. Package it up!
		for(String fileName : datasetFiles){
			try{
				if(! new File(projectDir + fileName).isDirectory()){
					FileInputStream in = new FileInputStream(projectDir + fileName);
					out.putNextEntry(new ZipEntry(fileName));
					int len;
		            while ((len = in.read(buf)) > 0) {
		                out.write(buf, 0, len);
		            }
		            out.closeEntry();
		            in.close();
				}
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		out.close();
	}
	
	public static void ZipKnnModelingResults(String userName, String jobName, String zipFile) throws Exception{
		Utility.writeToDebug("Creating archive of predictor: " + jobName);
	    // These are the files to include in the ZIP file
		if(userName.equals(Constants.ALL_USERS_USERNAME)){
			userName = "all-users";
		}
		String projectSubDir = userName + "/PREDICTORS/" + jobName + "/";
		if(projectSubDir.contains("..") || projectSubDir.contains("~")){
			//someone's trying to download something they shouldn't be!
			return;
		}
		String projectDir = Constants.CECCR_USER_BASE_PATH + projectSubDir;
		
		if(Utility.canDownloadDescriptors(userName)){
			//this is a special user - just give them the whole damn directory
			String workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/";
			String subDir = jobName + "/";
			ZipEntireDirectory(workingDir, subDir, zipFile);
			return;
		}
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		
		/*
			Modeling Output zipfile should contain:
			knn-output.tbl
			external_prediction_table
			mychart.jpeg
			*.act 
			*.sdf
			*.a
			Logs/*
		*/
		
		ArrayList<String> modelingFiles = new ArrayList<String>();
		//add in the modeling summary files
		modelingFiles.add("knn-output.tbl");
		modelingFiles.add("mychart.jpeg");
		modelingFiles.add("external_prediction_table");
		
		//add in the .act, .sdf, and .a files
		File projectDirFile = new File(projectDir);
		String[] projectDirFilenames = projectDirFile.list();
		if(projectDirFilenames == null){
			Utility.writeToDebug("Error reading directory: " + projectDir);
		}
		int x = 0;
		while(projectDirFilenames != null && x<projectDirFilenames.length){
			if(projectDirFilenames[x].endsWith(".act") 
					|| projectDirFilenames[x].endsWith(".sdf") 
					|| projectDirFilenames[x].endsWith(".a")){
				modelingFiles.add(projectDirFilenames[x]);
			}
			x++;
		}
		
		//add the Logs files in
		File ProjectDirLogsFile = new File(projectDir + "Logs/");
		String[] projectDirLogsFilenames = ProjectDirLogsFile.list();
		x = 0;
		while(projectDirLogsFilenames != null && x<projectDirLogsFilenames.length){
			modelingFiles.add("Logs/" + projectDirLogsFilenames[x]);
			x++;
		}
		
		//modelingFiles now contains names of all the files we need. Package it up!
		for(String fileName : modelingFiles){
			try{
				if( (new File(projectDir + fileName)).exists() ){
					FileInputStream in = new FileInputStream(projectDir + fileName);
					out.putNextEntry(new ZipEntry(fileName));
					int len;
		            while ((len = in.read(buf)) > 0) {
		                out.write(buf, 0, len);
		            }
		            out.closeEntry();
		            in.close();
				}
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		out.close();
	}
		
	public static void ZipKnnPredictionResults(String userName, String jobName, String zipFile) throws Exception{
		Utility.writeToDebug("Creating archive of prediction: " + jobName);
		if(userName.equals(Constants.ALL_USERS_USERNAME)){
			userName = "all-users";
		}
		String projectSubDir = userName + "/PREDICTIONS/" + jobName + "/";
		if(projectSubDir.contains("..") || projectSubDir.contains("~")){
			//someone's trying to download something they shouldn't be!
			return;
		}
		String projectDir = Constants.CECCR_USER_BASE_PATH + projectSubDir;
		
		if(Utility.canDownloadDescriptors(userName)){
			//this is a special user - just give them the whole damn directory
			String workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTIONS/";
			String subDir = jobName + "/";
			ZipEntireDirectory(workingDir, subDir, zipFile);
			return;
		}
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		
		/*
			Prediction Output zipfile should contain:
			*.act 
			*.sdf
			Logs/*
			subdir/cons_pred
			subdir/Logs/*
		*/
		
		ArrayList<String> predictionFiles = new ArrayList<String>();
		
		//add in the prediction dataset files
		File projectDirFile = new File(projectDir);
		String[] projectDirFilenames = projectDirFile.list();
		if(projectDirFilenames == null){
			Utility.writeToDebug("Error reading directory: " + projectDir);
		}
		int x = 0;
		while(projectDirFilenames != null && x<projectDirFilenames.length){
			if(projectDirFilenames[x].endsWith(".act") 
					|| projectDirFilenames[x].endsWith(".sdf") 
					|| projectDirFilenames[x].endsWith(".a")){
				predictionFiles.add(projectDirFilenames[x]);
			}
			x++;
		}
		
		//add in the Logs subdirectory
		File ProjectDirLogsFile = new File(projectDir + "Logs/");
		String[] projectDirLogsFilenames = ProjectDirLogsFile.list();
		x = 0;
		while(projectDirLogsFilenames != null && x<projectDirLogsFilenames.length){
			predictionFiles.add("Logs/" + projectDirLogsFilenames[x]);
			x++;
		}
		
		//scan for the predictor subdirectories
		x = 0;
		ArrayList<String> predictorSubDirs = new ArrayList<String>();
		while(projectDirFilenames != null && x<projectDirFilenames.length){
			if((new File(projectDir + projectDirFilenames[x])).isDirectory() && !projectDirFilenames[x].equals("Logs")){
				predictorSubDirs.add(projectDirFilenames[x] + "/");
			}
			x++;
		}
		
		//for each predictor, get the Logs and cons_pred output
		for(String subdir : predictorSubDirs){
			//add in the Logs subdirectory
			File predictorLogsFile = new File(projectDir + subdir + "Logs/");
			String[] predictorLogsFilenames = predictorLogsFile.list();
			x = 0;
			while(predictorLogsFilenames != null && x<predictorLogsFilenames.length){
				predictionFiles.add(subdir + "Logs/" + predictorLogsFilenames[x]);
				x++;
			}
			
			//add in cons_pred
			predictionFiles.add(subdir + "cons_pred");
		}
		
		//predictionFiles now contains names of all the files we need. Package it up!
		for(String fileName : predictionFiles){
			try{
				FileInputStream in = new FileInputStream(projectDir + fileName);
				out.putNextEntry(new ZipEntry(fileName));
				int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            out.closeEntry();
	            in.close();
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		out.close();
	}
	
}