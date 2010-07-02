package edu.unc.ceccr.workflows;


import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class KnnPredictionWorkflow{
	
	//Execute external programs to generate a prediction for a given molecule set.
	
	public static void RunKnnPrediction(String userName, String jobName, String workingdir, String sdFile, float cutoffValue ) throws Exception{
		
		String execstr1 = "PredActivCont3rwknnLIN knn-output.list " + sdFile + ".renorm.x pred_output " + cutoffValue;
		  Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
	      Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "PredActivCont3rwknnLIN", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	   
	    String execstr2 = "ConsPredContrwknnLIN pred_output.comp.list pred_output.list cons_pred";
		  Utility.writeToDebug("Running external program: " + execstr2 + " in dir " + workingdir);
	      p = Runtime.getRuntime().exec(execstr2, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "ConsPredContrwknnLIN", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}
	
	public static void MoveToPredictionsDir(String userName, String jobName) throws Exception{
		//When the prediction job is finished, move all the files over to the predictions dir.
		Utility.writeToDebug("Moving to PREDICTIONS dir.", userName, jobName);
		String moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTIONS/" + jobName + "/";
		String execstr = "mv " + moveFrom + " " + moveTo;

		  Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr);
	      //Utility.writeProgramLogfile(moveTo, "mv", p.getInputStream(), p.getErrorStream());
	      p.waitFor();

	}
	
	
	public static void RunKnnPlusPrediction(String userName, String jobName, String workingDir, String sdfile, float cutoffValue) throws Exception{

		//write a dummy .a file because knn+ needs it or it fails bizarrely... X_X
		String actfile = workingDir + sdfile + ".renorm.a";
		BufferedWriter aout = new BufferedWriter(new FileWriter(actfile));
		ArrayList<String> compoundNames = DatasetFileOperations.getSDFCompoundList(workingDir + sdfile);
		for(String compoundName : compoundNames){
			aout.write(compoundName + " 0\n");
		}
		aout.close();
		
	    //Run prediction
		String preddir = workingDir;
		
		String xfile = sdfile + ".renorm.x";
		String execstr = "knn+ knn-output.list -4PRED=" + xfile + " -AD=" + cutoffValue + "_avd -OUT=" + Constants.PRED_OUTPUT_FILE;
		Utility.writeToDebug("Running external program: " + execstr + " in dir: " + preddir);
		Process p = Runtime.getRuntime().exec(execstr, null, new File(preddir));
		Utility.writeProgramLogfile(preddir, "knn+_prediction", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		
	}
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		
        //read prediction output
		String outputFile = Constants.PRED_OUTPUT_FILE + ".preds"; //the ".preds" is added automatically by knn+
    	Utility.writeToDebug("Reading file: " + workingDir + outputFile);
		BufferedReader in = new BufferedReader(new FileReader(workingDir + outputFile));
		String inputString;
		
		//The first four lines are all header data
		in.readLine(); //junk
		inputString = in.readLine(); //compound names are here; we'll need them
		String[] compoundNames = inputString.split("\\s+");
		
		in.readLine(); //junk
		in.readLine(); //junk
		
		ArrayList<ArrayList<String>> predictionMatrix = new ArrayList<ArrayList<String>>(); //read output file into this
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>(); //holds objects to be returned

		//each line of output represents a model
		//(which is really the transform of the matrix we're looking for... *sigh*
		while ((inputString = in.readLine()) != null && ! inputString.equals("")){

			ArrayList<String> modelValues = new ArrayList<String>();
			
			//get output for each compound in model
			String[] predValues = inputString.split("\\s+"); //Note: [0] and [1] in this array will be junk.
			
			//predValues(0) will be model_id, which is just an index.
			//predValues(1) will be AD_distance, which we may want to capture someday.
			//String adDistanceValue = predValues[1];
			for(int i = 2; i < predValues.length; i++){
				String predictValue = predValues[i];
				modelValues.add(predictValue);
			}
			predictionMatrix.add(modelValues);
		}
		
		//Utility.writeToDebug("calculating nummodels, avg, and stddev for each compound");
		
		//for each compound, calculate nummodels, avg, and stddev
		int numCompounds = predictionMatrix.get(0).size();
		for(int i = 0; i < numCompounds; i++){

			try{
			//calculate stddev and avg for each compound
			Float sum = new Float(0);
			Float mean = new Float(0);
			int numPredictingModels = predictionMatrix.size();
			//Utility.writeToDebug("doing sum for compound " + i);
			
			for(int j = 0; j < predictionMatrix.size(); j++){
				String predValue = predictionMatrix.get(j).get(i);
				if(predValue.equalsIgnoreCase("NA")){
					numPredictingModels--;
				}
				else{
					sum += Float.parseFloat(predValue);
				}
			}
			if(numPredictingModels > 0){
				mean = sum / numPredictingModels;
			}
			else{
				mean = null;
			}

			//Utility.writeToDebug("doing stddev for compound " + i);

			Float stddev = new Float(0);
			if(numPredictingModels > 0){
				for(int j = 0; j < predictionMatrix.size(); j++){
					String predValue = predictionMatrix.get(j).get(i);
					if(!predValue.equalsIgnoreCase("NA")){
						float distFromMeanSquared = (float) Math.pow((Double.parseDouble(predValue) - mean), 2);
						stddev += distFromMeanSquared;
					}
				}
				//divide sum then take sqrt to get stddev
				stddev = (float) Math.sqrt( stddev / numPredictingModels);
			}
			else{
				stddev = null;
			}
			
			//Utility.writeToDebug("making predvalue object for compound " + i);
			
			//create prediction value object
			PredictionValue p = new PredictionValue();
			p.setNumModelsUsed(numPredictingModels);
			p.setNumTotalModels(predictionMatrix.size());
			p.setPredictedValue(mean);
			p.setStandardDeviation(stddev);
			p.setCompoundName(compoundNames[i+2]);
			p.setPredictorId(predictorId);
			
			predictionValues.add(p);
	
			}catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}

	    return predictionValues;
	}
	
	
}