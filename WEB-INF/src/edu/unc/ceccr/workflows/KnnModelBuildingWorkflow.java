package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

public class KnnModelBuildingWorkflow{
	
	public static void SplitData(String userName, String jobName, String sdFile, String actFile, String randomSeed, String numCompoundsExternalSet) throws Exception {
		//Do the data set division things.
		//Copy files over for y-randomization workflow.
		
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//copy the act file to a ".a" file because datasplit will expect it
		FileAndDirOperations.copyFile(workingdir + actFile, workingdir + sdFile + ".a");
		
		//split dataset into [modeling set | external test set]
		  String execstr1 = "datasplit activator_protein_43.sdf.x -4EXT -SRND=" + randomSeed + " -N=1 -M=R -OUT=mdlext.list -S=" + numCompoundsExternalSet;
		  //Sasha's datasplit (deprecated) was:
		  //String execstr1 = "RandomDivSlow3 " + sdFile + ".x " + actFile + " train ext 1 list " + numCompoundsExternalSet + " n";
		  Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
	      Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "datasplit", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	      
	    //put the split files in the right spots
			FileAndDirOperations.copyFile(workingdir + "mdlext_mdl0.a", workingdir + "train_0.a");
			FileAndDirOperations.copyFile(workingdir + "mdlext_mdl0.x", workingdir + "train_0.x");
			FileAndDirOperations.copyFile(workingdir + "mdlext_ext0.a", workingdir + "ext_0.a");
			FileAndDirOperations.copyFile(workingdir + "mdlext_ext0.x", workingdir + "ext_0.x");

	    //split modeling set, making several [ training | internal test ] sets
		String execstr2 = "se9v1_nl train_0.x train_0.a RAND_sets";
		  Utility.writeToDebug("Running external program: " + execstr2 + " in dir " + workingdir);
	      p = Runtime.getRuntime().exec(execstr2, null,  new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "se9v1", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	    
		new File(workingdir + "yRandom/").mkdir();
		new File(workingdir + "yRandom/Logs/").mkdir();
		
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
				if(files[x].matches(".*default.*") || files[x].matches(".*RAND_sets.*")){
					FileChannel ic = new FileInputStream(fromDir + files[x]).getChannel();
					FileChannel oc = new FileOutputStream(toDir + files[x]).getChannel();
					ic.transferTo(0, ic.size(), oc);
					ic.close();
					oc.close(); 
				}
				x++;
			}
	}
	
	public static void buildKnnCategoryModel(String userName, String jobName, String optimizationValue, String workingDir) throws Exception{
			String command = "AllKnn_category_nl 1 RAND_sets.list knn-output " + optimizationValue;
			Utility.writeToDebug("Running Category kNN in dir " + workingDir, userName, jobName);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "AllKnn_category_nl", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Category kNN finished.", userName, jobName);
	}
	

	public static void buildKnnContinuousModel(String userName, String jobName, String workingDir) throws Exception{
			String command = "AllKnn2LIN_nl 1 RAND_sets.list knn-output";
			Utility.writeToDebug("Running Continuous kNN in dir " + workingDir, userName, jobName);
			Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "AllKnn2LIN_nl", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			Utility.writeToDebug("Continuous kNN finished.", userName, jobName);
	}
	
	public static void YRandomization(String userName, String jobName) throws Exception{
		//Run y-randomization test on kNN model.
		
		String yRandomDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/yRandom/";
		File dir = new File(yRandomDir);
		String files[] = dir.list();
		if(files == null){
			Utility.writeToDebug("Error reading directory: " + yRandomDir);
		}
		int x = 0;
		while(files != null && x<files.length){
			if(files[x].matches(".*RAND_sets_a1.*")){
				//generate model building results for each randomized file
				String execstr = "RandomizationSlowLIN " + files[x] + " tempfile";
				  Utility.writeToDebug("Running external program: " + execstr + " in dir " + yRandomDir);
			      Process p = Runtime.getRuntime().exec(execstr, null, new File(yRandomDir));
			      Utility.writeProgramLogfile(yRandomDir, "RandomizationSlowLIN", p.getInputStream(), p.getErrorStream());
			      p.waitFor();
			    
			}
			x++;
		}
	}
	
	public static void RunExternalSet(String userName, String jobName, String sdFile, String actFile) throws Exception{

		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName;
		
		String execstr1 = "PredActivCont3rwknnLIN knn-output.list ext_0.x pred_output 1.0";
		  Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
	      Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "PredActivCont3rwknnLIN", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	    
	    String execstr2 = "ConsPredContrwknnLIN pred_output.comp.list pred_output.list cons_pred";
		  Utility.writeToDebug("Running external program: " + execstr2 + " in dir " + workingdir);
	      p = Runtime.getRuntime().exec(execstr2, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "ConsPredContrwknnLIN", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	    
	    String execstr3 = "parse_structgen_merge.pl cons_pred ext_0.a fake_argument external_prediction_table";
		  Utility.writeToDebug("Running external program: " + execstr3 + " in dir " + workingdir);
	      p = Runtime.getRuntime().exec(execstr3, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "parse_structgen_merge.pl", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}
	
	public static void MoveToPredictorsDir(String userName, String jobName) throws Exception{
		//When the kNN job is finished, move all the files over to the PREDICTORS dir.
		String moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + jobName + "/";
		String execstr = "mv " + moveFrom + " " + moveTo;
		  System.out.println("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr);
	      //Utility.writeProgramLogfile(moveTo, "mv", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}
}