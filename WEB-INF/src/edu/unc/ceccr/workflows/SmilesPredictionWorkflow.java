package edu.unc.ceccr.workflows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.Constants.DescriptorEnumeration;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class SmilesPredictionWorkflow{
	public static String[] PredictSmilesSDF(String workingDir, String username, Predictor predictor, Float cutoff) throws Exception{

		String sdfile = workingDir + "smiles.sdf";
		Utility.writeToDebug("Running PredictSmilesSDF in dir " + workingDir);
		
		//copy the predictor to the workingDir.
		String predictorUsername = predictor.getUserName();
		if(predictorUsername.equalsIgnoreCase("_all")){
			predictorUsername = "all-users";	
		}
		String fromDir = Constants.CECCR_USER_BASE_PATH + predictorUsername + "/PREDICTORS/" + predictor.getName() + "/";

		Utility.writeToDebug("Copying predictor files from " + fromDir);
		FileAndDirOperations.copyDirContents(fromDir, workingDir, false);

		Utility.writeToDebug("Copying complete. Generating descriptors. ");
		
		//create the descriptors for the chemical and read them in
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		ArrayList<String> chemicalNames = DatasetFileOperations.getChemicalNamesFromSdf(sdfile);

		if(predictor.getDescriptorGeneration().equals(DescriptorEnumeration.MOLCONNZ)){
			GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(sdfile, sdfile + ".S");
			ReadDescriptorsFileWorkflow.readMolconnZDescriptors(sdfile + ".S", descriptorNames, descriptorValueMatrix);
		}
		else if(predictor.getDescriptorGeneration().equals(DescriptorEnumeration.DRAGON)){
			GenerateDescriptorWorkflow.GenerateDragonDescriptors(sdfile, sdfile + ".dragon");
			ReadDescriptorsFileWorkflow.readDragonDescriptors(sdfile + ".dragon", descriptorNames, descriptorValueMatrix);
		}
		else if(predictor.getDescriptorGeneration().equals(DescriptorEnumeration.MOE2D)){
			GenerateDescriptorWorkflow.GenerateMoe2DDescriptors(sdfile, sdfile + ".moe2D");
			ReadDescriptorsFileWorkflow.readMoe2DDescriptors(sdfile + ".moe2D", descriptorNames, descriptorValueMatrix);
		}
		else if(predictor.getDescriptorGeneration().equals(DescriptorEnumeration.MACCS)){
			GenerateDescriptorWorkflow.GenerateMaccsDescriptors(sdfile, sdfile + ".maccs");
			ReadDescriptorsFileWorkflow.readMaccsDescriptors(sdfile + ".maccs", descriptorNames, descriptorValueMatrix);
		}

		Utility.writeToDebug("Normalizing descriptors to fit predictor.");

		String descriptorString = descriptorNames.toString().replaceAll("[,\\[\\]]", "");
		WriteDescriptorsFileWorkflow.writePredictionXFile(chemicalNames, descriptorValueMatrix, descriptorString, sdfile + ".renorm.x", workingDir + "train_0.x");
	
		Utility.writeToDebug("Running prediction.");
	    //Run prediction
		String preddir = workingDir;
		
			String execstr = "PredActivCont3rwknnLIN " + "knn-output.list " + sdfile + ".renorm.x " + "pred_output " + cutoff;
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
		
		//write SMILES string to file
			FileWriter fstream = new FileWriter(smilesDir + "tmp.smiles");
	        BufferedWriter out = new BufferedWriter(fstream);
		    out.write(smiles + " 1");
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