package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
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
			command += " -O=GA";

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
			command += " -O=SA";
			
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
			command += "@B=" + knnPlusParameters.getSaNumBestModels().trim();
			
			//'..@K=' - T decreasing coeff.; 
			command += "@K=" + knnPlusParameters.getSaTempDecreaseCoefficient().trim();
			
		}

		//'-KR=1@9' (to try from 1 to 9 neighbors)
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
		if(knnPlusParameters.getKnnErrorBasedFit().equalsIgnoreCase("true")){
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

	public static void runKnnPlusPrediction() throws Exception{
		
	}
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		return null;
	}
}