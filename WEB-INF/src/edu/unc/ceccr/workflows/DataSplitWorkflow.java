package edu.unc.ceccr.workflows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.Constants.TrainTestSplitTypeEnumeration;

import java.util.ArrayList;
import java.util.Scanner;

public class DataSplitWorkflow{
	
	public static void SplitModelingExternal(
			String userName, 
			String jobName, 
			String sdFile, 
			String actFile, 
			String xFile, 
			String numCompoundsExternalSet) throws Exception {
		//splits the input dataset into modeling and external validation set
		
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//copy the act file to a ".a" file because datasplit will expect it that way
		FileAndDirOperations.copyFile(workingdir + actFile, workingdir + sdFile + ".a");
		
		//split dataset into [modeling set | external test set]
		String execstr1 = "datasplit "+ sdFile + ".x" + " -4EXT" + " -N=1 -M=R -OUT=mdlext.list -S=" + numCompoundsExternalSet;
		Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
	    Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
	    Utility.writeProgramLogfile(workingdir, "datasplit", p.getInputStream(), p.getErrorStream());
	    p.waitFor();
	      
	    //put the split files in the right spots
		FileAndDirOperations.copyFile(workingdir + "mdlext_mdl0.a", workingdir + "train_0.a");
		FileAndDirOperations.copyFile(workingdir + "mdlext_mdl0.x", workingdir + "train_0.x");
		FileAndDirOperations.copyFile(workingdir + "mdlext_ext0.a", workingdir + "ext_0.a");
		FileAndDirOperations.copyFile(workingdir + "mdlext_ext0.x", workingdir + "ext_0.x");
	}

	public static void SplitTrainTestRandom(String userName,
			String jobName, 
			String numSplitsStr, 
			String randomSplitMinTestSizeStr, 
			String randomSplitMaxTestSizeStr) throws Exception {
		
		//splits the modeling set into several training and test sets randomly
		Utility.writeToDebug("Splitting train/test data randomly", userName, jobName);
		
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		int numSplits = Integer.parseInt(numSplitsStr);
		double randomSplitMinTestSize = Double.parseDouble(randomSplitMinTestSizeStr);
		double randomSplitMaxTestSize = Double.parseDouble(randomSplitMaxTestSizeStr);
		double testSizeRange = randomSplitMaxTestSize - randomSplitMinTestSize;
		
		//We will want to combine each of the "RAND_sets_i.list" files to form RAND_sets.list.
		String listFileContents = "";
		
		for(int i = 0; i < numSplits; i++){
			double testSize = Math.random()*testSizeRange + randomSplitMinTestSize;
			testSize = testSize / 100; //it's a percent
			
			String listFileName = "rand_sets_" + i + ".list";
			String execstr1 = "datasplit train_0.x -N=1 -M=R -OUT=" + listFileName + " -F=" + testSize;
			Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
			Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
			Utility.writeProgramLogfile(workingdir, "datasplit", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			
			//Read in the listfile that was just created.
			String fileLocation = workingdir + listFileName;
			BufferedReader in = new BufferedReader(new FileReader(fileLocation));
			String line = in.readLine(); //first line is a comment
			line = in.readLine(); //second line has the list info
			if(line != null){
				listFileContents += line + "\n";
			}
			
		}
		
		//Now print out a list file that kNN will like.
		FileWriter fstream = new FileWriter(workingdir + "RAND_sets.list");
        BufferedWriter out = new BufferedWriter(fstream);
	    out.write(listFileContents);
	    out.close();
		
	}

	public static void SplitTrainTestSphereExclusion(String userName, 
			String jobName, 
			String numSplitsStr, 
			String splitIncludesMinStr, 
			String splitIncludesMaxStr, 
			String sphereSplitMinTestSizeStr, 
			String selectionNextTrainPt) throws Exception{
		//splits the modeling set into several training and test sets using sphere exclusion
		Utility.writeToDebug("Splitting train/test data by sphere exclusion", userName, jobName);
		
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		int numSplits = Integer.parseInt(numSplitsStr);
		double minTestSize = Double.parseDouble(sphereSplitMinTestSizeStr);
		minTestSize = minTestSize / 100; //it's a percent
		
		boolean splitIncludesMin, splitIncludesMax;
		if(splitIncludesMinStr.equalsIgnoreCase("1"))
			splitIncludesMin = true;
		else
			splitIncludesMin = false;
		if(splitIncludesMaxStr.equalsIgnoreCase("1"))
			splitIncludesMax = true;
		else
			splitIncludesMax = false;
		
		String forcedCompounds = "";
		if(splitIncludesMin && splitIncludesMax)
			forcedCompounds += "LO,HI";
		else if(splitIncludesMin)
			forcedCompounds += "LO";
		else if(splitIncludesMax)
			forcedCompounds += "HI";
		
		String nextTrainPt = "";
		if(selectionNextTrainPt.equalsIgnoreCase("0"))
			nextTrainPt = "R";
		else if(selectionNextTrainPt.equalsIgnoreCase("1"))
			nextTrainPt = "SL1"; //SUM-MIN, expands, tumor-like
		else if(selectionNextTrainPt.equalsIgnoreCase("2"))
			nextTrainPt = "LH1"; //MIN_MAX, even coverage, lattice-like
		else if(selectionNextTrainPt.equalsIgnoreCase("3"))
			nextTrainPt = "SH1"; //SUM-MAX, corners and edges first working inwards
		
		String execstr1 = "datasplit train_0.x -N=" + numSplits + " -M=S -OUT=RAND_sets.list -+=" + forcedCompounds + " -D=" + nextTrainPt + " -F=" + minTestSize;
		Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
		Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
		Utility.writeProgramLogfile(workingdir, "datasplit", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		
		//datasplit will change all its filenames to lowercase. We need RAND_sets.list, not rand_sets.list!
		FileAndDirOperations.copyFile(workingdir + "rand_sets.list", workingdir + "RAND_sets.list");
	}
	
	
	public static void SplitData(String userName, String jobName, String sdFile, String actFile, String randomSeed, String numCompoundsExternalSet) throws Exception {
		//Do the data set division things.
		
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//copy the act file to a ".a" file because datasplit will expect it
		FileAndDirOperations.copyFile(workingdir + actFile, workingdir + sdFile + ".a");
		
		//split dataset into [modeling set | external test set]
		  String execstr1 = "datasplit "+ sdFile + ".x" + " -4EXT -SRND=" + randomSeed + " -N=1 -M=R -OUT=mdlext.list -S=" + numCompoundsExternalSet;
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
	}
	    	
}