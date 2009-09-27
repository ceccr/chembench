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
			String workingdir,
			String actFile, 
			String xFile, 
			String numCompoundsExternalSet,
			String useActivityBinning) throws Exception {
		//splits the input dataset into modeling and external validation set
		
		//copy the act file to a ".a" file because datasplit will expect it that way
		String actFileBase = actFile.substring(0, actFile.lastIndexOf("."));
		FileAndDirOperations.copyFile(workingdir + actFile, workingdir + actFileBase + ".a");
		
		//split dataset into [modeling set | external test set]
		String execstr1 = "datasplit "+ xFile + " -4EXT" + " -N=1 -M=R -OUT=mdlext.list -S=" + numCompoundsExternalSet;
		if(useActivityBinning.equalsIgnoreCase("true")){
			execstr1 += " -A=" + numCompoundsExternalSet;
		}
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
	
	public static void splitModelingExternalGivenList(
			String workingdir,
			String actFileName, 
			String xFileName, 
			String compoundIdString) throws Exception {

		String[] compoundIDs = compoundIdString.split("\\s+");
		
		File inX = new File(workingdir + xFileName);
		BufferedReader inXReader = new BufferedReader(new FileReader(inX));
		File inAct = new File(workingdir + actFileName);
		BufferedReader inActReader = new BufferedReader(new FileReader(inAct));
		
		File outActModeling = new File(workingdir + "train_0.a");
		FileWriter outActModelingWriter = new FileWriter(outActModeling);
		File outActExternal = new File(workingdir + "ext_0.a");
		FileWriter outActExternalWriter = new FileWriter(outActExternal);
		File outXModeling = new File(workingdir + "train_0.x");
		FileWriter outXModelingWriter = new FileWriter(outXModeling);
		File outXExternal = new File(workingdir + "ext_0.x");
		FileWriter outXExternalWriter = new FileWriter(outXExternal);
		
		//split the X file
		//header line first
		String line;
		line = inXReader.readLine(); //line contains: numCompounds numDescriptors
		String[] array = line.split("\\s+");
		int numCompounds = Integer.parseInt(array[0]);
		int numDescriptors = Integer.parseInt(array[1]);

		outXModelingWriter.write("" + (numCompounds - compoundIDs.length) + " " + numDescriptors);
		outXExternalWriter.write("" + (compoundIDs.length) + " " + numDescriptors);
		
		while((line = inXReader.readLine()) != null){
			array = line.split("\\s+");
			boolean lineIsExternal = false;
			for(int i = 0; i < compoundIDs.length; i++){
				if(array[0].equals(compoundIDs[i])){
					outXExternalWriter.write(line + "\n");
					lineIsExternal = true;
				}
			}
			if(!lineIsExternal){
				//compound belongs in modeling set
				outXModelingWriter.write(line + "\n");
			}
		}
		//done splitting the x file

		//split the ACT file
		//header line first
		line = inActReader.readLine(); //line contains: numCompounds numDescriptors
		outActModelingWriter.write(line + "\n");
		outActExternalWriter.write(line + "\n");
		
		while((line = inActReader.readLine()) != null){
			array = line.split("\\s+");
			boolean lineIsExternal = false;
			for(int i = 0; i < compoundIDs.length; i++){
				if(array[0].equals(compoundIDs[i])){
					outActExternalWriter.write(line + "\n");
					lineIsExternal = true;
				}
			}
			if(!lineIsExternal){
				//compound belongs in modeling set
				outActModelingWriter.write(line + "\n");
			}
		}
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
	
	    	
}