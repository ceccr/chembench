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
	
	public static void retrieveCompletedPredictor(String filePath, String lsfPath) throws Exception{
		//open the directory in /smallfs/ceccr/ where the job was run
		//copy directory contents back
		FileAndDirOperations.copyDirContents(lsfPath, filePath, true);
		
		//remove /smallfs/ceccr/ subdirectory
		FileAndDirOperations.deleteDir(new File(lsfPath));
	}
	
	public static void makeLsfModelingDirectory(String filePath, String lsfPath) throws Exception{
		//create a dir out in /smallfs/ceccr/ to run the calculation of the job
		File dir = new File(lsfPath);
		dir.mkdirs();
		
		//copy all files from current modeling dir out there
		FileAndDirOperations.copyDirContents(filePath, lsfPath, true);
		
		//copy kNN executables to the temp directory and to the yRandom subdirectory
		//also, make them executable
		FileAndDirOperations.copyDirContents(Constants.CECCR_BASE_PATH + "mmlsoft/bin/", lsfPath, false);
		FileAndDirOperations.makeDirContentsExecutable(lsfPath);
		FileAndDirOperations.copyDirContents(Constants.CECCR_BASE_PATH + "mmlsoft/bin/", lsfPath + "yRandom/", false);
		FileAndDirOperations.makeDirContentsExecutable(lsfPath + "yRandom/");
		
	}

	public static void buildKnnCategoryModel(String userName, String jobName, String optimizationValue, String workingDir) throws Exception{
			//write shell script containing LSF submission (both yRandom and regular kNN run in one exec)
			
			FileOutputStream fout;
			PrintStream out;
			fout = new FileOutputStream(workingDir + "bsubKnn.sh");
			out = new PrintStream(fout);
			
			out.println("bsub -q week AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue);
			out.println("cd yRandom");
			out.println("bsub -q week AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue);
			
			
			out.close();
			fout.close();
			
			//give exec permissions to script file
			File f = new File(workingDir + "bsubKnn.sh");
			f.setExecutable(true);
			
			//exec shell script
			String command = "bsubKnn.sh";
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "bsubKnn", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Category kNN submitted.", userName, jobName);
	}
	

	public static void buildKnnContinuousModel(String userName, String jobName, String workingDir) throws Exception{

		FileOutputStream fout;
		PrintStream out;
		fout = new FileOutputStream(workingDir + "bsubKnn.sh");
		out = new PrintStream(fout);
		
		out.println("AllKnn2LIN_nl 1 RAND_sets.list knn-output");
		out.println("cd yRandom/");
		out.println("AllKnn2LIN_nl 1 RAND_sets.list knn-output");
		out.close();
		fout.close();
		
		//give exec permissions to script file
		File f = new File(workingDir + "bsubKnn.sh");
		f.setExecutable(true);
		
		//exec shell script
		String command = "bsubKnn.sh";
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "bsubKnn", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Continuous kNN submitted.", userName, jobName);	
		
	}
	
	
	
}