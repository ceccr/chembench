package edu.unc.ceccr.utilities;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.unc.ceccr.global.Constants;

//Java suuure is dumb sometimes. Needs me to write it a whole class just to 
//run a program and capture its output without bleeding file handles 
//all over the place.


public class RunExternalProgram {
	
	//these programs should not have anything appear in the log file
	//when they run. (For programs that execute many times in quick succession.)
	private static String[] runQuietly = 
		{"bjobs.sh", 
		"datasplit_train_test",
		"checkKnnSaProgress",
		"checkKnnGaProgress",
		"svm-train",
		"svm-predict"};
	
	public static void close(Closeable c) {
		if (c != null) {
		  try {
		    c.close();
		  } catch (IOException e) {
		  // ignored
	     }
	   }
	}

	public static void runCommand(String cmd, String workingDir){
		//runs an external program (no logging)
		
		try{
			Process p = null;

			if(workingDir.isEmpty()){
				workingDir = Constants.CECCR_USER_BASE_PATH;
			}
			
			boolean outputRunningMessage = true;
			for(int i = 0; i < runQuietly.length; i++){
				if(cmd.startsWith(runQuietly[i])){
					outputRunningMessage = false;
				}
			}
			
			if(workingDir.isEmpty()){
				if(outputRunningMessage){
					Utility.writeToDebug("Running external program " + cmd);
				}
				p = Runtime.getRuntime().exec(cmd);
			}
			else{
				if(outputRunningMessage){
					Utility.writeToDebug("Running external program " + cmd + " in dir: " + workingDir);
				}
				p = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
			}
		
			//capture program output in log file
			File file=new File(workingDir + "/Logs/");
			if(!file.exists()){
				file.mkdirs();
			}
			
			//wait for the program to finish running
		    p.waitFor();
		    
		    //close any file handles we might have 
		 	RunExternalProgram.close(p.getOutputStream());
	        RunExternalProgram.close(p.getInputStream());
	        RunExternalProgram.close(p.getErrorStream());
	        p.destroy();
	        
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public static void runCommandAndLogOutput(String cmd, String workingDir, String logFileName){
		//runs an external program and writes user info to logfile
		
		try{
			Process p = null;

			if(workingDir.isEmpty()){
				workingDir = Constants.CECCR_USER_BASE_PATH;
			}
			

			boolean outputRunningMessage = true;
			for(int i = 0; i < runQuietly.length; i++){
				if(cmd.startsWith(runQuietly[i]) || logFileName.startsWith(runQuietly[i])){
					outputRunningMessage = false;
				}
			}
			if(outputRunningMessage){
				Utility.writeToDebug("Running external program " + cmd + " in dir: " + workingDir);
			}
			
			p = Runtime.getRuntime().exec(cmd, null, new File(workingDir));
		
			//capture program output in log file
			File file=new File(workingDir + "/Logs/");
			if(!file.exists()){
				file.mkdirs();
			}
			
			InputStream stdout = p.getInputStream();
			InputStream errout = p.getErrorStream();
			
			FileOutputStream logFileOS = new FileOutputStream(new File(workingDir + "/Logs/" + logFileName + ".log"));
			FileOutputStream errFileOS = new FileOutputStream(new File(workingDir + "/Logs/" + logFileName + ".err"));
			StreamGobbler outputGobbler = new StreamGobbler(stdout, "stdout", logFileOS);  
			StreamGobbler errorGobbler = new StreamGobbler(errout, "errout", errFileOS);   
			outputGobbler.start();
			errorGobbler.start();
			
			//wait for the program to finish running
		    p.waitFor();
		    
		    //close any file handles we might have
		 	RunExternalProgram.close(p.getOutputStream());
	        RunExternalProgram.close(p.getInputStream());
	        RunExternalProgram.close(p.getErrorStream());
	        p.destroy();
	        
			logFileOS.close();
		 	errFileOS.close();
	        
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
}