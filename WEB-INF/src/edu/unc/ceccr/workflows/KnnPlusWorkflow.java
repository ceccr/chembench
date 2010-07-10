package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class KnnPlusWorkflow{

	public static void buildKnnPlusModels(KnnPlusParameters knnPlusParameters, String actFileDataType, String modelType, String workingDir) throws Exception{
		//this converts the parameters entered on the web page into command-line
		//arguments formatted to work with knn+.
		//The comments in this function are excerpts from the knn+ help file.
		
		//knn+ will automatically convert all input filenames to lowercase.
		//so, our list file has to be lowercase.
		FileAndDirOperations.copyFile(workingDir + "RAND_sets.list", workingDir + "rand_sets.list");
		
		String command = "knn+ rand_sets.list";

		//'-OUT=...' - output file
		command += " -OUT=" + Constants.KNNPLUSMODELSFILENAME;
		
		if(actFileDataType.equals(Constants.CONTINUOUS)){
			//'-M=...' - model type: 'CNT' continuous <def.>,'CTG' - category,'CLS' - classes
			command += " -M=CNT";
		}
		else if(actFileDataType.equals(Constants.CATEGORY)){
			//'-M=...' - model type: 'CNT' continuous <def.>,'CTG' - category,'CLS' - classes
			command += " -M=CTG";
		}
		
		if(modelType.equals(Constants.KNNGA)){
			//Number of dimensions, min-max. There is no step for genetic algorithm.
			//Example: '-D=5@50'
			command += " -D=" + knnPlusParameters.getKnnMinNumDescriptors().trim() + "@" + 
				knnPlusParameters.getKnnMaxNumDescriptors().trim();

			//'-GA@...' - Genetic Algorithm settings: e.g. -GA@N=500@D=1000@S=20@V=-4@G=7
			command += " -O=GA GA";

			//'..@N=' - population size; 
			command += "@N=" + knnPlusParameters.getGaPopulationSize().trim();
			
			//'..@D=' - max.#generations; 
			command += "@N=" + knnPlusParameters.getGaMaxNumGenerations().trim();
			
			//'..@S=' - #stable generations to stop
			command += "@S=" + knnPlusParameters.getGaNumStableGenerations().trim();
			
			//'..@V=' - minimum fitness difference to proceed
			command += "@V=" + knnPlusParameters.getGaMinFitnessDifference().trim();
			
			//'..@G=' - group size for tournament ('TOUR') selection of parents
			command += "@G=" + knnPlusParameters.getGaTournamentGroupSize().trim();
		}
		else if(modelType.equals(Constants.KNNSA)){
			//Number of dimensions, min-max-step. Step can't be used in genetic alg. 
			//Example: '-D=5@50@3'
			command += " -D=" + knnPlusParameters.getKnnMinNumDescriptors().trim() + "@" + 
				knnPlusParameters.getKnnMaxNumDescriptors().trim() + "@" +
				knnPlusParameters.getKnnDescriptorStepSize().trim();

			//'-SA@...' - Simulated Annealing settings: e.g. -SA@B=3@TE=-2@K=0.6@DT=-3@ET=-5
			command += " -O=SA -SA";
			
			//'..@N=' - #SA runs to repeat; '..@D=' - #mutations at each T
			command += "@N=" + knnPlusParameters.getSaNumRuns().trim();
			
			//'..@T0=x' - start T (10^x);
			command += "@T0=" + knnPlusParameters.getSaLogInitialTemp().trim();
			
			//'..@TE=' - final T; 
			command += "@TE=" + knnPlusParameters.getSaFinalTemp().trim();
			
			//'..@DT=' - convergence range of T
			command += "@DT=" + knnPlusParameters.getSaTempConvergence().trim();
			
			//'..@M=' - mutation probability per dimension
			command += "@M=" + knnPlusParameters.getSaMutationProbabilityPerDescriptor().trim();
			
			//'..@B=' - #best models to store
			//Constraint: Number of best models must be less than or equal to the number
			//of runs. If #best > #runs, it will set #runs = #best.
			command += "@B=" + knnPlusParameters.getSaNumBestModels().trim();
			
			//'..@K=' - T decreasing coeff.; 
			command += "@K=" + knnPlusParameters.getSaTempDecreaseCoefficient().trim();
			
		}

		//'-KR=1@9' (to try from 1 to 9 neighbors)
		//Constraint: Max is strictly greater (>) than min. If this is not true,
		//it will automatically user '9' as the maximum, making the modeling take
		//a REALLY FUCKIN' LONG TIME.
		command += " -KR=" + knnPlusParameters.getKnnMinNearestNeighbors().trim() + "@" + 
			knnPlusParameters.getKnnMaxNearestNeighbors().trim();
		
		//'-AD=' - applicability domain: e.g. -AD=0.5, -AD=0.5d1_mxk
		//'0.5' is z-cutoff <def.>; d1 - direct-distance based AD <def. is dist^2>
		//Additional options of AD-checking before making prediction:
		//'_avd' - av.dist to k neighbors should be within AD (traditional)
		//'_mxk' - all k neighbors should be within AD
		//'_avk' - k/2 neighbors within AD, '_mnk' - at least 1 within AD <def.>
		command += " -AD=" + knnPlusParameters.getKnnApplicabilityDomain().trim();
		
		//'-EVL=...' - model's quality controls; e.g. -EVL=A0.5@0.6
		//For continuous kNN it means q2 >0.5 and R2>0.6
		//A - alternative control-indices; E - error-based
		//V - aver.error based (only for discrete-act.); S - simple post-evaluation
		if((modelType.equals(Constants.KNNSA) && knnPlusParameters.getKnnSaErrorBasedFit().equalsIgnoreCase("true"))
				|| (modelType.equals(Constants.KNNGA) && knnPlusParameters.getKnnGaErrorBasedFit().equalsIgnoreCase("true"))){
			command += " -EVL=E" + knnPlusParameters.getKnnMinTraining().trim() + "@" +
			knnPlusParameters.getKnnMinTest().trim();
		}
		else{
			command += " -EVL=" + knnPlusParameters.getKnnMinTraining().trim() + "@" +
			knnPlusParameters.getKnnMinTest().trim();
		}
		
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "knnPlus", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		//Utility.writeToDebug("Category kNN finished.", userName, jobName);
	}
	
	public static void predictExternalSet(String userName, String jobName, String workingDir, String cutoffValue) throws Exception{
		//Run prediction
		
		String xfile = "ext_0.x";
		//knn+ models -4PRED=ext_0.x -AD=0.5_avd -OUT=cons_pred;
		String execstr = "knn+ models.tbl -4PRED=" + xfile + " -AD=" + cutoffValue + "_avd -OUT=" + Constants.PRED_OUTPUT_FILE;
		Utility.writeToDebug("Running external program: " + execstr + " in dir: " + workingDir);
		Process p = Runtime.getRuntime().exec(execstr, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "knn+_prediction", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		
	}

	public static ArrayList<ExternalValidation> readExternalPredictionOutput(String workingDir, Predictor predictor) throws Exception{
		 
        //read prediction output
		String outputFile = "cons_pred_vs_ext_0.preds"; //the ".preds" is added automatically by knn+
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
		ArrayList<ExternalValidation> predictionValues = new ArrayList<ExternalValidation>(); //to be returned

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
		
		//get the actual (observed) values for each compound
		HashMap<String,String> observedValues = DatasetFileOperations.getActFileIdsAndValues(workingDir + "ext_0.a");
		
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
			ExternalValidation ev = new ExternalValidation();
			ev.setNumModels(numPredictingModels);
			ev.setPredictedValue(mean);
			ev.setStandDev("" + stddev);
			ev.setCompoundId(compoundNames[i+2]);
			ev.setPredictor(predictor);
			ev.setActualValue(Float.parseFloat(observedValues.get(compoundNames[i+2])));
			
			predictionValues.add(ev);
	
			}catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		
		return predictionValues;
	}
	
	
	public static int getModelingProgress(String workingDir){

		try{
			String execstr = "checkKnnPlusProgress.sh";
			Utility.writeToDebug("Running external program: " + execstr + " in dir: " + workingDir);
			Process p = Runtime.getRuntime().exec(execstr, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "checkKnnPlusProgress", p.getInputStream(), p.getErrorStream());
			p.waitFor();
	
			String file = FileAndDirOperations.readFileIntoString(workingDir + "knnPlusProgress").trim();
			String[] tokens = file.split(" ");
			return Integer.parseInt(tokens[0]);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		return -1;
	}
	
	
	public static void runKnnPlusPrediction() throws Exception{
		
	}

	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		return null;
	}
}