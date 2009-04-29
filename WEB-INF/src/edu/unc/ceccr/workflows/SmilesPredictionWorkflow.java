package edu.unc.ceccr.workflows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class SmilesPredictionWorkflow{
	public static String[] PredictSmilesSDF(String workingDir, String username, Predictor predictor, Float cutoff) throws Exception{

		String sdfile = workingDir + "smiles.sdf";
		Utility.writeToDebug("Running PredictSmilesSDF in dir " + workingDir);
		//run MolconnZ to get descriptors
		GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(sdfile, sdfile + ".S");
		
		//copy the predictor to the workingDir.
		String predictorUsername = predictor.getUserName();
		if(predictorUsername.equalsIgnoreCase("_all")){
			predictorUsername = "all-users";	
		}
		String fromDir = Constants.CECCR_USER_BASE_PATH + predictorUsername + "/PREDICTORS/" + predictor.getName() + "/";

		Utility.writeToDebug("Copying predictor files from " + fromDir);
		FileAndDirOperations.copyDirContents(fromDir, workingDir, false);

		Utility.writeToDebug("Copying complete. Normalizing descriptors to fit predictor. ");
		//normalize, prepare .x file for kNN
		MolconnZToDescriptors.MakePredictionDescriptors(sdfile + ".S", workingDir + "train_0.x", sdfile + ".x");
		
		Utility.writeToDebug("Running prediction.");
	    //Run prediction
		String preddir = workingDir;
		
			String execstr = "PredActivCont3rwknnLIN " + "knn-output.list " + sdfile + ".x " + "pred_output " + cutoff;
			Utility.writeToDebug("Running external program: " + execstr + " in dir: " + preddir);
			Process p = Runtime.getRuntime().exec(execstr, null, new File(preddir));
			Utility.writeProgramLogfile(preddir, "PredActivCont3rwknnLIN", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			
			execstr = "ConsPredContrwknnLIN " + "pred_output.comp.list " + "pred_output.list " + "cons_pred";
	    	Utility.writeToDebug("Running external program: " + execstr + " in dir: " + preddir);
	        p = Runtime.getRuntime().exec(execstr, null, new File(preddir));
	        Utility.writeProgramLogfile(preddir, "ConsPredContrwknnLIN", p.getInputStream(), p.getErrorStream());
	        p.waitFor();
	    
	    //read prediction output
	    	Utility.writeToDebug("Reading file: " + workingDir + Constants.PRED_OUTPUT_FILE);
			BufferedReader in = new BufferedReader(new FileReader(workingDir + Constants.PRED_OUTPUT_FILE));
			String inputString;
			//skip all the non-blank lines with junk in them
			while (!(inputString = in.readLine()).equals(""))
				;
			//now skip some blank lines
			while ((inputString = in.readLine()).equals(""))
				;
			//now we're at the data we need
			String[] predValues = inputString.split("\\s+");
		    Utility.writeToDebug("Finished prediction. Output: " + inputString);
		    return predValues;
	}
	
	public static void smilesToSDF(String smiles, String smilesDir) throws Exception{
		//takes in a SMILES string and produces an SDF file from it. 
		//Returns the file path as a string.
		
		Utility.writeToDebug("Running smilesToSDF with SMILES: " + smiles);
		
		//set up the directory, just in case it's not there yet.
		File dir = new File(smilesDir);
		dir.mkdirs();
		
		//make sure there's nothing in the dir already.
		FileAndDirOperations.deleteDirContents(smilesDir);
		
		//write SMILES string to file
			FileWriter fstream = new FileWriter(smilesDir + "tmp.smiles");
	        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(smiles);
		    out.close();
		
		//execute molconvert to change it to SDF
	    	String execstr = "molconvert -2:O1 sdf " + smilesDir + "tmp.smiles -o " + smilesDir + "smiles.sdf";
	    	Utility.writeToDebug("Running external program: " + execstr);
	    	Process p = Runtime.getRuntime().exec(execstr);
	    	Utility.writeProgramLogfile(smilesDir, "molconvert", p.getInputStream(), p.getErrorStream());
	    	p.waitFor();
	    	
	    Utility.writeToDebug("Finished smilesToSDF");
	}	
}