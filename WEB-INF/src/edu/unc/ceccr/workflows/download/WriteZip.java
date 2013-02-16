
package edu.unc.ceccr.workflows.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.visualization.ExternalValidationChart;

public class WriteZip{

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
	
	public static void ZipDatasets(String userName, String datasetUserName, String datasetName, String zipFile) throws Exception{
		Utility.writeToDebug("Creating archive of dataset: " + datasetName + " into file: " + zipFile);
	    // These are the files to include in the ZIP file
		String projectSubDir = datasetUserName + "/DATASETS/" + datasetName + "/";
		if(projectSubDir.contains("..") || projectSubDir.contains("~")){
			//someone's trying to download something they shouldn't be!
			Utility.writeToDebug("Access attempt on directory: " + projectSubDir);
			return;
		}
		String projectDir = Constants.CECCR_USER_BASE_PATH + projectSubDir;
		
		if(Utility.canDownloadDescriptors(userName)){
			//this is a special user - just give them the whole damn directory
			String workingDir = Constants.CECCR_USER_BASE_PATH + datasetUserName + "/DATASETS/";
			String subDir = datasetName + "/";
			ZipEntireDirectory(workingDir, subDir, zipFile);
			return;
		}
		
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
		
		//add in the .act, .sdf, .cdk, and .x files
		File projectDirFile = new File(projectDir);
		String[] projectDirFilenames = projectDirFile.list();
		if(projectDirFilenames == null){
			Utility.writeToDebug("Error reading directory: " + projectDir);
		}
		int x = 0;
		while(projectDirFilenames != null && x<projectDirFilenames.length){
			if(projectDirFilenames[x].endsWith(".act") 
					|| projectDirFilenames[x].endsWith(".sdf") 
					|| projectDirFilenames[x].endsWith(".x")
					|| projectDirFilenames[x].replaceAll("[0-9]", "").endsWith("fold")){
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
		
		//add the Descriptor files in (just CDK)
		File ProjectDirDescriptorsFile = new File(projectDir + "Descriptors/");
		String[] projectDirDescriptorsFilenames = ProjectDirDescriptorsFile.list();
		x = 0;
		while(projectDirDescriptorsFilenames != null && x<projectDirDescriptorsFilenames.length){
			if(projectDirDescriptorsFilenames[x].endsWith(".cdk") || projectDirDescriptorsFilenames[x].endsWith(".x")){
				datasetFiles.add("Descriptors/" + projectDirDescriptorsFilenames[x]);
			}
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
	
	public static void ZipModelingResults(String userName, String predictorUserName, String jobName, String zipFile) throws Exception{
		/*
		So, there are two contradicting goals this function has to achieve.
		(1) It should provide the user with a usable predictor.
		(2) It cannot have any descriptor information.
		
		This is ridiculous; in order to be able to make use of a predictor, a user
		would need to know the min and max of each descriptor value for scaling purposes, 
		but giving them that info violates (2).
		Not only that -- they'd need a descriptor generation program. And all the 
		format conversion tools in Chembench. And a copy of the modeling tool. 
		
		The only reason the 'download' option is even there is so that users in the lab
		(i.e. those with descriptor download privileges) can get their stuff out. 
		
		So all we give to non-descriptor users is:
		- the external set charts 
 		- a summary of the models
 		- Detailed (model-by-model) external set prediction data, if applicable.
		 */
		
		Utility.writeToDebug("Creating archive of predictor: " + jobName);
	    // These are the files to include in the ZIP file
		String projectSubDir = predictorUserName + "/PREDICTORS/" + jobName + "/";
		if(projectSubDir.contains("..") || projectSubDir.contains("~")){
			//someone's trying to download something they shouldn't be!
			return;
		}
		String projectDir = Constants.CECCR_USER_BASE_PATH + projectSubDir;
		

		Session session = HibernateUtil.getSession();
		Predictor predictor = PopulateDataObjects.getPredictorByName(jobName, predictorUserName, session);
		ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(predictor, session);
		session.close();
		
		//get external predictions
		WriteCsv.writeExternalPredictionsAsCSV(predictor.getId());
		
		if(Utility.canDownloadDescriptors(userName)){
			//this is a special user - just give them the whole damn directory
			String workingDir = Constants.CECCR_USER_BASE_PATH + predictorUserName + "/PREDICTORS/";
			String subDir = jobName + "/";
			ZipEntireDirectory(workingDir, subDir, zipFile);
			return;
		}
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];

		ArrayList<String> modelingFiles = new ArrayList<String>(); //list of files that will be in the downloaded zip

		modelingFiles.add(predictor.getName() + "-external-set-predictions.csv");
		
		//add in the .act, .sdf, and .a files
		if(childPredictors != null && !childPredictors.isEmpty()){
			for(Predictor cp: childPredictors){
				File projectDirFile = new File(projectDir + cp.getName() + "/");
				String[] projectDirFilenames = projectDirFile.list();
				
				int x = 0;
				while(projectDirFilenames != null && x<projectDirFilenames.length){
					if(projectDirFilenames[x].endsWith(".act") 
							|| projectDirFilenames[x].endsWith(".sdf") 
							|| projectDirFilenames[x].endsWith(".a")){
						modelingFiles.add(cp.getName() + "/" + projectDirFilenames[x]);
					}
					else if(projectDirFilenames[x].endsWith(".x") && predictor.getDescriptorGeneration().equals(Constants.UPLOADED)){
						modelingFiles.add(projectDirFilenames[x]);
					}
					x++;
				}
			}
		}
		else{
			File projectDirFile = new File(projectDir);
			String[] projectDirFilenames = projectDirFile.list();
			
			int x = 0;
			while(projectDirFilenames != null && x<projectDirFilenames.length){
				if(projectDirFilenames[x].endsWith(".act") 
						|| projectDirFilenames[x].endsWith(".sdf") 
						|| projectDirFilenames[x].endsWith(".a")){
					modelingFiles.add(projectDirFilenames[x]);
				}
				else if(projectDirFilenames[x].endsWith(".x") && predictor.getDescriptorGeneration().equals(Constants.UPLOADED)){
					modelingFiles.add(projectDirFilenames[x]);
				}
				x++;
			}
		}
		
		//add the Logs files in
		if(childPredictors != null && !childPredictors.isEmpty()){
			for(Predictor cp: childPredictors){
				File ProjectDirLogsFile = new File(projectDir + cp.getName() + "/Logs/");
				String[] projectDirLogsFilenames = ProjectDirLogsFile.list();
				int x = 0;
				while(projectDirLogsFilenames != null && x<projectDirLogsFilenames.length){
					modelingFiles.add(cp.getName() + "/Logs/" + projectDirLogsFilenames[x]);
					x++;
				}
			}
		}
		else{
			File ProjectDirLogsFile = new File(projectDir + "Logs/");
			String[] projectDirLogsFilenames = ProjectDirLogsFile.list();
			int x = 0;
			while(projectDirLogsFilenames != null && x<projectDirLogsFilenames.length){
				modelingFiles.add("Logs/" + projectDirLogsFilenames[x]);
				x++;
			}
		}
		
		//get external prediction summary information
		if(predictor.getActivityType().equals(Constants.CONTINUOUS)){
			//build ext validation chart(s)
			if(! new File(projectDir+"mychart.jpeg").exists()){
				ExternalValidationChart.createChart(predictor, "0");
			}
			modelingFiles.add("mychart.jpeg");
			if(childPredictors != null && !childPredictors.isEmpty()){
				for(Predictor cp: childPredictors){
					if(! new File(projectDir+cp.getName()+"/mychart.jpeg").exists()){
						Pattern p = Pattern.compile("fold_(\\d+)_of_(\\d+)");
						Matcher matcher = p.matcher(cp.getName());
						if(matcher.find()){
							int foldNum = Integer.parseInt(matcher.group(1));
							if(! new File(projectDir + cp.getName() + "/mychart.jpeg").exists()){
								ExternalValidationChart.createChart(predictor, "" + foldNum);
							}
						}
					}
					modelingFiles.add(cp.getName() + "/mychart.jpeg");
				}
			}
		}
		else{
			//getting the confusion matrix as text could be nice.
			//maybe later.
		}
		
		
		//add files specific to the modeling method (per-model outputs, parameters files)
		if(predictor.getModelMethod().equals(Constants.SVM)){
			modelingFiles.add("svm-params.txt");
			modelingFiles.add("svm-results.txt");
			modelingFiles.add("yRandom/svm-params.txt");
			modelingFiles.add("yRandom/svm-results.txt");
			if(childPredictors != null && !childPredictors.isEmpty()){
				for(Predictor cp: childPredictors){
					modelingFiles.add(cp.getName() + "/svm-params.txt");
					modelingFiles.add(cp.getName() + "/svm-results.txt");
					modelingFiles.add(cp.getName() + "/yRandom/svm-params.txt");
					modelingFiles.add(cp.getName() + "/yRandom/svm-results.txt");
				}
			}
		}
		else if(predictor.getModelMethod().equals(Constants.RANDOMFOREST)){
			modelingFiles.add("RF_ext_0.pred");
			if(childPredictors != null && !childPredictors.isEmpty()){
				for(Predictor cp: childPredictors){
					modelingFiles.add(cp.getName() + "/RF_ext_0.pred");
				}
			}
		}
		else if(predictor.getModelMethod().equals(Constants.KNNGA) || predictor.getModelMethod().equals(Constants.KNNSA)){
			modelingFiles.add("cons_pred_vs_ext_0.preds");
			if(childPredictors != null && !childPredictors.isEmpty()){
				for(Predictor cp: childPredictors){
					modelingFiles.add(cp.getName() + "/cons_pred_vs_ext_0.preds");
				}
			}
		}
		else{
			//old-style KNN. No real need to support this.
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
				else{
					//don't worry about missing files
				}
			}
			catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		out.close();
	}
		
	public static void ZipPredictionResults(String userName, String predictionUserName, String jobName, String zipFile) throws Exception{
		Utility.writeToDebug("Creating archive of prediction: " + jobName);
		String projectSubDir = predictionUserName + "/PREDICTIONS/" + jobName + "/";
		if(projectSubDir.contains("..") || projectSubDir.contains("~")){
			//someone's trying to download something they shouldn't be!
			return;
		}
		String projectDir = Constants.CECCR_USER_BASE_PATH + projectSubDir;

		Session session = HibernateUtil.getSession();
		Prediction prediction = PopulateDataObjects.getPredictionByName(jobName, predictionUserName, session);
		session.close();
		if(! new File(prediction.getName() + "-prediction-values.csv").exists()){
			WriteCsv.writePredictionValuesAsCSV(prediction.getId());
		}
		
		if(Utility.canDownloadDescriptors(userName)){
			//this is a special user - just give them the whole damn directory
			String workingDir = Constants.CECCR_USER_BASE_PATH + predictionUserName + "/PREDICTIONS/";
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
		predictionFiles.add(prediction.getName() + "-prediction-values.csv");
		
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