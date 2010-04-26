package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class KnnModelingLsfWorkflow{
	
	public static void checkJobStatus() throws Exception{
		//called periodically during running job
		//gets information from bjobs about whether the running job has completed
		//bjobs -a: gives all jobs, even ones that are completed (within the past hour, anyway).
		
	}
	
	public static void retrieveCompletedPredictor(String filePath, String lsfPath) throws Exception{
		//open the directory in /largefs/ceccr/ where the job was run
		
		String execstr = "mv.sh " + lsfPath + "* " + filePath;
		  Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr);
	      //Utility.writeProgramLogfile(filePath, "mv", p.getInputStream(), p.getErrorStream());
	      p.waitFor();

		execstr = "mv.sh " + lsfPath + "yRandom/* " + filePath + "yRandom/ ";
		  Utility.writeToDebug("Running external program: " + execstr);
	      p = Runtime.getRuntime().exec(execstr);
	      //Utility.writeProgramLogfile(filePath, "mv2", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	      
		//remove the empty /largefs/ceccr/userName/jobName/ subdirectory
		//FileAndDirOperations.deleteDir(new File(lsfPath));
		
	}
	
	public static void makeLsfModelingDirectory(String filePath, String lsfPath) throws Exception{
		//create a dir out in /largefs/ceccr/ to run the calculation of the job
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

	public static String buildKnnCategoryModel(String userName, String jobName, String optimizationValue, String workingDir) throws Exception{
			//write shell script containing LSF submission (both yRandom and regular kNN run in one exec)
			
			FileOutputStream fout;
			PrintStream out;
			fout = new FileOutputStream(workingDir + "bsubKnn.sh");
			out = new PrintStream(fout);

			out.println("cd " + workingDir);
			out.println("AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue);
			out.println("cd yRandom");
			out.println("AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue);
			
			out.close();
			fout.close();
			
			//give exec permissions to script file
			File f = new File(workingDir + "bsubKnn.sh");
			f.setExecutable(true);
			
			//exec shell script
			String command = "bsub -q week -J cbench_" + userName + "_" + jobName + " -o bsubOutput.txt " + workingDir + "bsubKnn.sh";
			Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "bsubKnn", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Category kNN submitted.", userName, jobName);

			String logFilePath = workingDir + "Logs/bsubKnn.log";
			return getLsfJobId(logFilePath);
	}
	

	public static String buildKnnContinuousModel(String userName, String jobName, String workingDir) throws Exception{

		FileOutputStream fout;
		PrintStream out;
		fout = new FileOutputStream(workingDir + "bsubKnn.sh");
		out = new PrintStream(fout);

		out.println("cd " + workingDir);
		out.println("AllKnn2LIN_nl 1 RAND_sets.list knn-output");
		out.println("cd yRandom/");
		out.println("AllKnn2LIN_nl 1 RAND_sets.list knn-output");
		out.close();
		fout.close();
		
		//give exec permissions to script file
		File f = new File(workingDir + "bsubKnn.sh");
		f.setExecutable(true);
		
		//exec shell script
		String command = "bsub -q week -J cbench_" + userName + "_" + jobName + " -o bsubOutput.txt " + workingDir + "bsubKnn.sh";
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "bsubKnn", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Continuous kNN submitted.", userName, jobName);	

		String logFilePath = workingDir + "Logs/bsubKnn.log";
		return getLsfJobId(logFilePath);
	}
	
	public static String getLsfJobId(String logFilePath) throws Exception{
		Thread.sleep(200); //give the file time to close properly? I guess?
		BufferedReader in = new BufferedReader(new FileReader(logFilePath));
		String line = in.readLine(); //junk
		Scanner sc = new Scanner(line);
		String jobId = "";
		if(sc.hasNext()){
			sc.next();
		}
		if(sc.hasNext()){
			jobId = sc.next();
		}
		Utility.writeToDebug(jobId.substring(1, jobId.length() - 1));
		return jobId.substring(1, jobId.length() - 1);
	}
	
}