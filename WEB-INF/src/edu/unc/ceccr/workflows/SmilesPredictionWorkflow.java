package edu.unc.ceccr.workflows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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

		
		//get train_0.x file from the predictor dir.
		Utility.writeToDebug("Copying predictor files from " + fromDir);
		GetJobFilesWorkflow.getPredictorFiles(username, predictor, workingDir);
		
		Utility.writeToDebug("Copying complete. Generating descriptors. ");
		
		//create the descriptors for the chemical and read them in
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		ArrayList<String> chemicalNames = DatasetFileOperations.getSDFCompoundList(sdfile);

		if(predictor.getDescriptorGeneration().equals(DescriptorEnumeration.MOLCONNZ)){
			GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(sdfile, sdfile + ".mz");
			ReadDescriptorsFileWorkflow.readMolconnZDescriptors(sdfile + ".mz", descriptorNames, descriptorValueMatrix);
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
		WriteDescriptorsFileWorkflow.writePredictionXFile(chemicalNames, descriptorValueMatrix, descriptorString, sdfile + ".renorm.x", workingDir + "train_0.x", predictor.getScalingType());
	
		Utility.writeToDebug("Running prediction.");
	    //Run prediction
		String preddir = workingDir;
		
		String xfile = sdfile + ".renorm"; //the ".x" will be added on by knn+
		String execstr = "/knn+ knn-output.list -4PRED=" + xfile + " -AD=" + cutoff + "_avd -OUT=" + Constants.PRED_OUTPUT_FILE;
		Utility.writeToDebug("Running external program: " + execstr + " in dir: " + preddir);
		Process p = Runtime.getRuntime().exec(execstr, null, new File(preddir));
		Utility.writeProgramLogfile(preddir, "PredActivCont3rwknnLIN", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		
	        //read prediction output
			String outputFile = Constants.PRED_OUTPUT_FILE + ".preds"; //the .preds is added automatically by knn+
	    	Utility.writeToDebug("Reading file: " + workingDir + outputFile);
			BufferedReader in = new BufferedReader(new FileReader(workingDir + outputFile));
			String inputString;
			
			//Skip the first four lines (header data)
			in.readLine();
			in.readLine();
			in.readLine();
			in.readLine();
			
			//get output for each model
			
			ArrayList<String> predValueArray = new ArrayList<String>();
			while (!(inputString = in.readLine()).equals("")){
				String[] predValues = inputString.split("\\s+");
				Utility.writeToDebug(predValues[1] + " " + predValues[2]);
				if(! predValues[2].equals("NA")){
					predValueArray.add(predValues[2]);
				}
			}

			Utility.writeToDebug("numModels: " + predValueArray.size());
			
			double sum = 0;
			double mean = 0;
			if(predValueArray.size() > 0){
				for(String predValue : predValueArray){
					sum += Float.parseFloat(predValue);
				}
				mean = sum / predValueArray.size();
			}

			double stddev = 0;
			if(predValueArray.size() > 1){
				for(String predValue : predValueArray){
					double distFromMeanSquared = Math.pow((Double.parseDouble(predValue) - mean), 2);
					stddev += distFromMeanSquared;
				}
				//divide sum then take sqrt to get stddev
				stddev = Math.sqrt( stddev / predValueArray.size());
			}
				
			Utility.writeToDebug("prediction: " + mean);
			Utility.writeToDebug("stddev: " + stddev);

			//format numbers nicely and return them
			int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
			String predictedValue = DecimalFormat.getInstance().format("" + mean).replaceAll(",", "");
			predictedValue = (Utility.roundSignificantFigures(predictedValue, sigfigs));
			
			String stdDevStr = DecimalFormat.getInstance().format("" + stddev).replaceAll(",", "");
			stdDevStr = (Utility.roundSignificantFigures(stdDevStr, sigfigs));
			
			String[] prediction = new String[3];
			prediction[0] = "" + predValueArray.size();
			if(predValueArray.size() > 0){
				prediction[1] = predictedValue;
			}
			else{
				prediction[1] = "N/A";
			}
			if(predValueArray.size() > 1){
				prediction[2] = stdDevStr;
			}
			else{
				prediction[2] = "N/A";
			}
			
		    return prediction;
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