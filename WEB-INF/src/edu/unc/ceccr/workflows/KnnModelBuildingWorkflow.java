package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.validator.GenericValidator;

public class KnnModelBuildingWorkflow{
	
	public static void writeKnnContinuousDefaultFile(String fullFileLocation, KnnParameters knnParameters) throws Exception {

		FileOutputStream fout;	
		PrintStream out;
		try
		{
		    fout = new FileOutputStream (fullFileLocation);
		    out = new PrintStream(fout);
		
		    out.println("Min_Number_Of_Descriptors: " + knnParameters.getMinNumDescriptors());
			out.println("Step: " + knnParameters.getStepSize());
			out.println("Number_Of_Steps: "
					+ ((new Integer(knnParameters.getMaxNumDescriptors()).intValue() - new Integer(
							knnParameters.getMinNumDescriptors()).intValue()) / new Integer(
								knnParameters.getStepSize()).intValue()));
		    out.println("Number_Of_Cycles: " + knnParameters.getNumCycles());
		    out.println("Number_Of_Neares_Neighbors: " + knnParameters.getNearestNeighbors());
		    out.println("Number_Of_Pseudo_Neighbors: " + knnParameters.getPseudoNeighbors());
		    out.println("Number_Of_Mutations: " + knnParameters.getNumMutations());
			out.println("Runs_For_Each_Set_Of_Parameters: " + knnParameters.getNumRuns());
		    out.println("T1: " + knnParameters.getT1());
		    out.println("T2: " + knnParameters.getT2());
			out.println("Mu: " + knnParameters.getMu());
		    out.println("TcOverTb: " + knnParameters.getTcOverTb());
		    out.println("CutOff: " + knnParameters.getCutoff());
		    out.println("Minimum_acc_train: " + knnParameters.getMinAccTraining());
			out.println("Minimum_acc_test: " + knnParameters.getMinAccTest());	
		    out.println("Minimum_and_maximum_slopes: " + knnParameters.getMinSlopes() + " " + knnParameters.getMaxSlopes());
		    out.println("Relative_diff_R_R0: " + knnParameters.getRelativeDiffRR0());
		    out.println("Diff_R01_R02: " + knnParameters.getDiffR01R02());
		    out.println("Stop: " + knnParameters.getStopCond());
		
		    out.close();
		    fout.close();	
	    } catch (IOException e) {
	    	Utility.writeToDebug(e);
	    }
	}

	public static void writeKnnCategoryDefaultFile(String fullFileLocation, KnnParameters knnParameters) throws Exception {
	
		FileOutputStream fout;
		PrintStream out;
		try {
			fout = new FileOutputStream(fullFileLocation);
			out = new PrintStream(fout);
			out.println("Min_Number_Of_Descriptors: " + knnParameters.getMinNumDescriptors());
			out.println("Step: " + knnParameters.getStepSize());
			out.println("Number_Of_Steps: "
							+ ((new Integer(knnParameters.getMaxNumDescriptors()).intValue() - new Integer(
									knnParameters.getMinNumDescriptors()).intValue()) / new Integer(
										knnParameters.getStepSize()).intValue()));
		    out.println("Number_Of_Cycles: " + knnParameters.getNumCycles());
		    out.println("Number_Of_Neares_Neighbors: " + knnParameters.getNearestNeighbors());
		    out.println("Number_Of_Pseudo_Neighbors: " + knnParameters.getPseudoNeighbors());
			out.println("Number_Of_Mutations: " + knnParameters.getNumMutations());
			out.println("Runs_For_Each_Set_Of_Parameters: " + knnParameters.getNumRuns());
		    out.println("T1: " + knnParameters.getT1());
		    out.println("T2: " + knnParameters.getT2());
			out.println("Mu: " + knnParameters.getMu());
		    out.println("TcOverTb: " + knnParameters.getTcOverTb());
			out.println("Minimum_acc_train: " + knnParameters.getMinAccTraining());
			out.println("Minimum_acc_test: " + knnParameters.getMinAccTest());			
			out.println("CutOff: " + knnParameters.getCutoff());
			out.println("Stop: " + knnParameters.getStopCond());
		
			out.close();
			fout.close();
		} catch (IOException e) {
			Utility.writeToDebug(e);
		}
	}
	
	public static void buildKnnCategoryModel(String userName, String jobName, String optimizationValue, String workingDir) throws Exception{
			String command = "AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue;
			RunExternalProgram.runCommandAndLogOutput(command, workingDir, "AllKnn_category_nl");
			Utility.writeToDebug("Category kNN finished.", userName, jobName);
	}
	
	public static void buildKnnContinuousModel(String userName, String jobName, String workingDir) throws Exception{
			String command = "AllKnn2LIN_nl 1 RAND_sets.list knn-output";
			RunExternalProgram.runCommandAndLogOutput(command, workingDir, "AllKnn2LIN_nl");
			Utility.writeToDebug("Continuous kNN finished.", userName, jobName);
	}
	
	public static void SetUpYRandomization(String userName, String jobName) throws Exception{
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//create yRandom dirs
		new File(workingdir + "yRandom/").mkdir();
		new File(workingdir + "yRandom/Logs/").mkdir();

		//make sure dirs are empty
		FileAndDirOperations.deleteDirContents(workingdir + "yRandom/");
		FileAndDirOperations.deleteDirContents(workingdir + "yRandom/Logs/");
		
		//copy *.default and RAND_sets* to yRandom
		File file;
		String fromDir = workingdir;
		String toDir = workingdir + "yRandom/";
		Utility.writeToDebug("Copying *.default and RAND_sets* from " + fromDir + " to " + toDir);
			file = new File(fromDir);
			String files[] = file.list();
			if(files == null){
				Utility.writeToDebug("Error reading directory: " + fromDir);
			}
			int x = 0;
			while(files != null && x<files.length){
				if(files[x].matches(".*default.*") || files[x].matches(".*RAND_sets.*") || files[x].matches(".*rand_sets.*")){
					FileChannel ic = new FileInputStream(fromDir + files[x]).getChannel();
					FileChannel oc = new FileOutputStream(toDir + files[x]).getChannel();
					ic.transferTo(0, ic.size(), oc);
					ic.close();
					oc.close(); 
				}
				x++;
			}
	}

	public static void YRandomization(String userName, String jobName) throws Exception{
		//Do y-randomization shuffling
		
		String yRandomDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/yRandom/";
		Utility.writeToDebug("YRandomization", userName, jobName);
		File dir = new File(yRandomDir);
		String files[] = dir.list();
		if(files == null){
			Utility.writeToDebug("Error reading directory: " + yRandomDir);
		}
		int x = 0;
		Utility.writeToDebug("Randomizing each activity file (*rand_sets*.a) in dir " + yRandomDir);
		while(files != null && x<files.length){
			if(files[x].matches(".*rand_sets.*a")){
				//shuffle the values in each .a file (ACT file)
				DatasetFileOperations.randomizeActivityFile(yRandomDir + files[x], yRandomDir + files[x]);
			}
			x++;
		}
	}
	
	public static void RunExternalSet(String userName, String jobName, String sdFile, String actFile) throws Exception{

		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName;
		
		String execstr1 = "PredActivCont3rwknnLIN knn-output.list " + Constants.EXTERNAL_SET_X_FILE + " pred_output 1.0";
		RunExternalProgram.runCommandAndLogOutput(execstr1, workingdir, "PredActivCont3rwknnLIN");  
		
	    String execstr2 = "ConsPredContrwknnLIN pred_output.comp.list pred_output.list cons_pred";
	    RunExternalProgram.runCommandAndLogOutput(execstr2, workingdir, "PredActivCont3rwknnLIN");  
		
	    String execstr3 = "parse_structgen_merge.pl cons_pred " + Constants.EXTERNAL_SET_A_FILE + " fake_argument external_prediction_table";
	    RunExternalProgram.runCommandAndLogOutput(execstr3, workingdir, "PredActivCont3rwknnLIN");  
	}
	
	public static void MoveToPredictorsDir(String userName, String jobName, String parentPredictorName) throws Exception{
		//When the job is finished, move all the files over to the PREDICTORS dir.
		String moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName;
		String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/";
		if(parentPredictorName.equals("")){
			(new File(moveTo)).mkdirs();
			moveTo += jobName;
		}
		else{
			moveTo += jobName + "/" + parentPredictorName;
			(new File(moveTo)).mkdirs();
		}
		String execstr = "mv " + moveFrom + " " + moveTo;
		RunExternalProgram.runCommand(execstr, "");  
	}
}