
package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

import java.util.ArrayList;
import java.util.zip.*;

public class ZipJobResultsWorkflow{

	public static void ZipEntireDirectory(String projectDir, String zipFile) throws Exception{
		//will be used for MML members - they can access all files on every project type

		String baseDir = Constants.CECCR_USER_BASE_PATH;
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		
		ArrayList<String> fileNames = new ArrayList<String>();
		ArrayList<String> dirNames = new ArrayList<String>();
		dirNames.add(projectDir);
		
		for(int i = 0; i < dirNames.size(); i++){
			//read through the directory's files.
			//Add each subdirectory to dirNames.
			//Add each file to fileNames.
			
			File dirFile = new File(dirNames.get(i));
			String[] dirFilenames = dirFile.list();
			
			int x = 0;
			while(dirFilenames != null && x<dirFilenames.length){
				if((new File(baseDir + dirNames.get(i) + dirFilenames[x])).isDirectory()){
					dirNames.add(dirNames.get(i) + dirFilenames[x] + "/");
				}
				else{
					fileNames.add(dirNames.get(i) + dirFilenames[x]);
				}
			}
		}
		
		for(String fileName : fileNames){
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
		datasetFiles.add("train_0.a");
		datasetFiles.add("ext_0.a");
		
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
		
		//datasetFiles now contains names of all the files we need. Package it up!
		for(String fileName : datasetFiles){
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
	}
	
	public static void ZipKnnModelingResults(String userName, String jobName, String zipFile, String jobType) throws Exception{
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
		}
		
		//add in the Logs subdirectory
		File ProjectDirLogsFile = new File(projectDir + "Logs/");
		String[] projectDirLogsFilenames = ProjectDirLogsFile.list();
		x = 0;
		while(projectDirLogsFilenames != null && x<projectDirLogsFilenames.length){
			predictionFiles.add("Logs/" + projectDirLogsFilenames[x]);
		}
		
		//scan for the predictor subdirectories
		x = 0;
		ArrayList<String> predictorSubDirs = new ArrayList<String>();
		while(projectDirFilenames != null && x<projectDirFilenames.length){
			if((new File(projectDirFilenames[x])).isDirectory() && ! projectDirFilenames[x].equals("Logs")){
				predictorSubDirs.add(projectDirFilenames[x] + "/");
			}
		}
		
		//for each predictor, get the Logs and cons_pred output
		for(String subdir : predictorSubDirs){
			//add in the Logs subdirectory
			File predictorLogsFile = new File(projectDir + subdir + "Logs/");
			String[] predictorLogsFilenames = predictorLogsFile.list();
			x = 0;
			while(predictorLogsFilenames != null && x<predictorLogsFilenames.length){
				predictionFiles.add(subdir + "Logs/" + predictorLogsFilenames[x]);
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
	}
	
}