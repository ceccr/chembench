package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

public class KnnModelingLsfWorkflow{
	
	public static void checkJobStatus() throws Exception{
		//called periodically during running job
		//gets information from bjobs about whether the running job has completed
		//bjobs -a: gives all jobs, even ones that are completed (within the past hour, anyway).
		
	}
	
	public static void retrieveCompletedPredictor(String userName, String jobName) throws Exception{
		//open the directory in /smallfs/ceccr/ where the job was run
		
		//copy directory contents back
		
		//remove /smallfs/ceccr/ directory
		
	}
	
	public static void makeModelingDirectory(String userName, String jobName) throws Exception{
		//create a dir out in /smallfs/ceccr/ to run the calculation of the job
		
		//copy all files from current modeling dir out there
		
		//copy kNN executables to the temp directory
		
	}

	public static void buildKnnCategoryModel(String userName, String jobName, String optimizationValue, String workingDir) throws Exception{
			String command = "AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue;
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "AllKnn_category_nl", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Category kNN finished.", userName, jobName);
	}
	

	public static void buildKnnContinuousModel(String userName, String jobName, String workingDir) throws Exception{
			String command = "AllKnn2LIN_nl 1 RAND_sets.list knn-output";
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "AllKnn2LIN_nl", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Continuous kNN finished.", userName, jobName);
	}
	
	
	
}