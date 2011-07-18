package edu.unc.ceccr.workflows.modelingPrediction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Compound;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;

public class DataSplit{
	
	public static void SplitModelingExternal(
			String workingdir,
			String actFile, 
			String xFile, 
			String numCompoundsExternalSet,
			String useActivityBinning) throws Exception {
		//splits the input dataset into modeling and external validation set
		
		Utility.writeToDebug(numCompoundsExternalSet);
		if((new Double(Double.parseDouble(numCompoundsExternalSet))).equals(new Double(0))){
			//datasplit will do something weird if you specify 0 as the size. 
			//Gotta do this manually.
			splitModelingExternalGivenList(workingdir, actFile, xFile, "");
			return;
		}
		
		//copy the act file to a ".a" file because datasplit will expect it that way
		String xFileBase = xFile.substring(0, xFile.lastIndexOf("."));
		FileAndDirOperations.copyFile(workingdir + actFile, workingdir + xFileBase + ".a");

		//split dataset into [modeling set | external test set]
		String execstr1;
		if(Double.parseDouble(numCompoundsExternalSet) < 1){ 
			execstr1 = "datasplit "+ xFile + " -4EXT" + " -N=1 -M=R -OUT=mdlext.list -F=" + numCompoundsExternalSet;
		}
		else{
			execstr1 = "datasplit "+ xFile + " -4EXT" + " -N=1 -M=R -OUT=mdlext.list -S=" + numCompoundsExternalSet;
		}
		if(useActivityBinning.equalsIgnoreCase("true")){
			execstr1 += " -A=" + numCompoundsExternalSet;
		}
		RunExternalProgram.runCommandAndLogOutput(execstr1, workingdir, "datasplit_train_ext");
		
	    //put the split files in the right spots
		FileAndDirOperations.copyFile(workingdir + "mdlext_mdl0.a", workingdir + Constants.MODELING_SET_A_FILE);
		FileAndDirOperations.copyFile(workingdir + "mdlext_mdl0.x", workingdir + Constants.MODELING_SET_X_FILE);
		FileAndDirOperations.copyFile(workingdir + "mdlext_ext0.a", workingdir + Constants.EXTERNAL_SET_A_FILE);
		FileAndDirOperations.copyFile(workingdir + "mdlext_ext0.x", workingdir + Constants.EXTERNAL_SET_X_FILE);
	}
	
	public static void splitModelingExternalGivenList(
			String workingdir,
			String actFileName, 
			String xFileName, 
			String compoundIdString) throws Exception {

		String[] compoundIDs = compoundIdString.trim().split("\\s+");
		Utility.writeToDebug("called splitModelingExternalGivenList in dir: " + workingdir + " actfile: " + actFileName + " xfile: " + xFileName);
		
		File inX = new File(workingdir + xFileName);
		BufferedReader inXReader = new BufferedReader(new FileReader(inX));
		File inAct = new File(workingdir + actFileName);
		BufferedReader inActReader = new BufferedReader(new FileReader(inAct));
		
		File outActModeling = new File(workingdir + Constants.MODELING_SET_A_FILE);
		FileWriter outActModelingWriter = new FileWriter(outActModeling);
		File outActExternal = new File(workingdir + Constants.EXTERNAL_SET_A_FILE);
		FileWriter outActExternalWriter = new FileWriter(outActExternal);
		File outXModeling = new File(workingdir + Constants.MODELING_SET_X_FILE);
		FileWriter outXModelingWriter = new FileWriter(outXModeling);
		File outXExternal = new File(workingdir + Constants.EXTERNAL_SET_X_FILE);
		FileWriter outXExternalWriter = new FileWriter(outXExternal);
		
		//split the X file
		//header line first
		String line;
		line = inXReader.readLine(); //line contains: numCompounds numDescriptors
		String[] array = line.split("\\s+");
		int numCompounds = Integer.parseInt(array[0]);
		int numDescriptors = Integer.parseInt(array[1]);
		int numExternalCompounds = compoundIDs.length;
		
		Utility.writeToDebug("num compounds in external set list: " + compoundIDs.length);
		
		if(compoundIDs.length == 1 && compoundIDs[0].equals("") || compoundIDs.length == 0){
			numExternalCompounds = 0;
		}
		int numCompoundsModelingSet = numCompounds - numExternalCompounds;
		outXModelingWriter.write("" + numCompoundsModelingSet + " " + numDescriptors + "\n");
		outXExternalWriter.write("" + numExternalCompounds + " " + numDescriptors + "\n");
		
		//next do the descriptors line
		line = inXReader.readLine();
		outXModelingWriter.write(line + "\n");
		outXExternalWriter.write(line + "\n");
		
		//then all the rest
		int lineIndex = 0;
		while((line = inXReader.readLine()) != null){
			if(lineIndex < numCompounds){
				//this line contains a compound and descriptor values
				array = line.split("\\s+");
				boolean lineIsExternal = false;
				for(int i = 0; i < numExternalCompounds; i++){
					//in an X file, first value is an index, second is compoundID
					if(array[1].equals(compoundIDs[i]) && ! array[1].trim().equals("")){
						outXExternalWriter.write(line + "\n");
						lineIsExternal = true;
						//Utility.writeToDebug("Compound [" + array[1] + "] -> EXTERNAL");
					}
				}
				if(!lineIsExternal){
					//compound belongs in modeling set
					outXModelingWriter.write(line + "\n");
					//Utility.writeToDebug("Compound [" + array[1] + "] -> MODELING");
				}
			}
			else{
				//we've read all the compounds
				//we're at the bottom lines that give min/max or stddev/offset
				//those go in both modeling and external.
				outXExternalWriter.write(line + "\n");
				outXModelingWriter.write(line + "\n");
			}
			lineIndex++;
		}
		outXModelingWriter.close();
		outXExternalWriter.close();
		//done splitting the X file

		//split the ACT file
		Utility.writeToDebug("reading ACT file from: " + workingdir + actFileName);
		while((line = inActReader.readLine()) != null){
			array = line.split("\\s+");
			//Utility.writeToDebug(line);
			boolean lineIsExternal = false;
			for(int i = 0; i < numExternalCompounds; i++){
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
		outActModelingWriter.close();
		outActExternalWriter.close();
	}
	
	public static void SplitModelingExternalNFold(String workingDir, String actFile, String numFolds, String useActivityBinning) throws Exception{
		//read in the act file
		ArrayList<Compound> compounds = new ArrayList<Compound>();
		File act = new File(workingDir + actFile);
		BufferedReader br = new BufferedReader(new FileReader(act));
		String line;
		while((line = br.readLine()) != null){
			String[] parts = line.split("\\s+");
			Compound ca = new Compound();
			ca.setCompoundId(parts[0]);
			ca.setActivityValue(parts[1]);
			compounds.add(ca);
		}
		if(useActivityBinning.equalsIgnoreCase("true")){
			Collections.sort(compounds, new Comparator<Compound>() {
			    public int compare(Compound ca1, Compound ca2) {
		    		return (new Double(Double.parseDouble(ca1.getActivityValue())).
		    				compareTo(new Double(Double.parseDouble(ca2.getActivityValue()))));
			    }});
		}
		else{
			Collections.shuffle(compounds);
		}
		
		//go down the sorted list in chunks of size nFolds
		int folds = Integer.parseInt(numFolds);
		ArrayList<ArrayList<Compound>> externalFolds = new ArrayList<ArrayList<Compound>>();
		for(int i = 0; i < folds; i++){
			ArrayList<Compound> fold = new ArrayList<Compound>();
			externalFolds.add(fold);
		}
		
		for(int i = 0; i < compounds.size(); i++){
			externalFolds.get(i % folds).add(compounds.get(i));
		}
		
		//write the compounds lists to .fold1, .fold2, etc
		for (int i = 0; i < externalFolds.size(); i++){
			BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + actFile + ".fold" + (i+1)));
			for(int j = 0; j < externalFolds.get(i).size(); j++){
				out.write(externalFolds.get(i).get(j).getCompoundId() + " " + externalFolds.get(i).get(j).getActivityValue() + "\n");
			}
			out.close();
		}
	}

	public static void SplitTrainTestRandom(String userName,
			String jobName, 
			String numSplitsStr, 
			String randomSplitMinTestSizeStr, 
			String randomSplitMaxTestSizeStr,
			String randomSplitSampleWithReplacement) throws Exception {
		
		//need to make the sample with replace option actually do something
		
		//splits the modeling set into several training and test sets randomly
		Utility.writeToDebug("Splitting train/test data randomly", userName, jobName);
		
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		int numSplits = Integer.parseInt(numSplitsStr);
		double randomSplitMinTestSize = Double.parseDouble(randomSplitMinTestSizeStr);
		double randomSplitMaxTestSize = Double.parseDouble(randomSplitMaxTestSizeStr);
		double testSizeRange = randomSplitMaxTestSize - randomSplitMinTestSize;
		
		//We will want to combine each of the "RAND_sets_i.list" files to form RAND_sets.list.
		String listFileContents = "";
		
		Utility.writeToDebug("Running train-test splitting in dir " + workingdir);
		for(int i = 0; i < numSplits; i++){
			double testSize = Math.random()*testSizeRange + randomSplitMinTestSize;
			testSize = testSize / 100; //it's a percent
			
			String listFileName = "rand_sets_" + i + ".list";
			String execstr1 = "datasplit train_0.x -N=1 -M=R -OUT=" + listFileName + " -F=" + testSize;
			
			RunExternalProgram.runCommandAndLogOutput(execstr1, workingdir, "datasplit_train_test");
			
			//Read in the listfile that was just created.
			String fileLocation = workingdir + listFileName;
			BufferedReader in = new BufferedReader(new FileReader(fileLocation));
			String line = in.readLine(); //first line is a comment
			line = in.readLine(); //second line has the list info
			if(line != null && !line.contains("#")){
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

		RunExternalProgram.runCommandAndLogOutput(execstr1, workingdir, "datasplit_train_test");
		
		//datasplit will change all its filenames to lowercase. We need RAND_sets.list, not rand_sets.list!
		FileAndDirOperations.copyFile(workingdir + "rand_sets.list", workingdir + "RAND_sets.list");
	}
	
	    	
}