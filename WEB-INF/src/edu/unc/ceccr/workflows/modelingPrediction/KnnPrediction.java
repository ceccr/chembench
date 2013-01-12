package edu.unc.ceccr.workflows.modelingPrediction;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;

public class KnnPrediction{
	
	//Execute external programs to generate a prediction for a given molecule set.
	// Used for legacy models that were created using Sasha's kNN code.
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId, String sdFile) throws Exception{
		//NOTE: THIS IS THE VERSION USED FOR KNN ONLY. For knn+, go to knnPlusWorkflow.java.
		
        //read prediction output for a kNN job.
		//sample output filename: cons_pred_vs_anticonvulsants_91.sdf.renorm.preds
		String outputFile = Constants.PRED_OUTPUT_FILE + "_vs_" + sdFile.toLowerCase()  + ".renorm.preds"; //the ".preds" is added automatically by knn+
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
		//(which is really the transpose of the matrix we're looking for... *sigh*)
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
		in.close();
	    return predictionValues;
	}


	public static void runKnnPlusPredictionForKnnPredictors(String userName, String jobName, String workingDir, String sdfile, float cutoffValue) throws Exception{
		// Used for legacy models that were created using Sasha's kNN code.
		
		//write a dummy .a file because knn+ needs it or it fails bizarrely... X_X
		String actfile = workingDir + sdfile + ".renorm.a";
		BufferedWriter aout = new BufferedWriter(new FileWriter(actfile));
		ArrayList<String> compoundNames = DatasetFileOperations.getSDFCompoundNames(workingDir + sdfile);
		for(String compoundName : compoundNames){
			aout.write(compoundName + " 0\n");
		}
		aout.close();
		
	    //Run prediction
		//String preddir = workingDir;
		
		String xfile = sdfile + ".renorm.x";
		String execstr = "knn+ knn-output.list -4PRED=" + xfile + " -AD=" + cutoffValue + "_avd -OUT=" + Constants.PRED_OUTPUT_FILE;
		RunExternalProgram.runCommandAndLogOutput(execstr, workingDir, "knnPlusPrediction");
	}
}