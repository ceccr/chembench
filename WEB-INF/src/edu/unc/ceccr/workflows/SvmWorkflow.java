package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.hibernate.Session;

public class SvmWorkflow{

	public static void convertXtoSvm(String xFileName, String aFileName, String workingDir) throws Exception{
		//generates an SVM-compatible input descriptor file
		
		//Utility.writeToDebug("Generating an SVM-compatible file: " + xFileName + " + " + aFileName + " => " + xFileName.replace(".x", ".svm"));

		ArrayList<String> activityValues = new ArrayList<String>();
		if(aFileName != null && !aFileName.isEmpty()){
			//read in the activity file
			
			BufferedReader in = new BufferedReader(new FileReader(workingDir + aFileName));
			String inputString;
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				String[] data = inputString.split("\\s+"); // [0] is the compound id, [1] is the activity value
				activityValues.add(data[1]);
			}
			in.close();
		}
		else{
			//if no activity file is supplied, just use zeros for activities
			int numCompounds = DatasetFileOperations.getXCompoundNames(workingDir + xFileName).size();
			if(xFileName.contains("ext_0")){
				Utility.writeToDebug("found " + numCompounds + " compounds in ext_0.x");
			}
			for(int i = 0; i < numCompounds; i++){
				activityValues.add("0");
			}
		}	
		
		//read in x file and translate to svm file, adding activity values along the way 
		BufferedReader in = new BufferedReader(new FileReader(workingDir + xFileName));
		BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + xFileName.replace(".x", ".svm")));
		StringBuilder sb = new StringBuilder();
		
		in.readLine(); // header
		in.readLine(); // header
		
		String inputString;
		for(int i = 0; i<activityValues.size(); i++)
		{
			sb.append(activityValues.get(i));
			inputString = in.readLine();
			String[] data = inputString.split("\\s+"); // [0] and [1] are id
			for(int j=2; j<data.length; j++)
			{
				sb.append(" " + (j-1) + ":" + data[j]);
			}
			sb.append(System.getProperty("line.separator"));
			out.write(sb.toString());
			sb.delete(0, sb.length());
		}
		in.close();
		out.flush();
		out.close();
	}
	
	public static void writeSvmModelingParamsFile(SvmParameters svmParameters, String actFileDataType, String workingDir) throws Exception{
		BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + "svm-params.txt")); 

		String svmType = "";
		if(actFileDataType.equals(Constants.CATEGORY)){
			svmType = svmParameters.getSvmTypeCategory();
		}
		else{
			svmType = svmParameters.getSvmTypeContinuous();
			if(svmType.equals("0")){
				svmType = "3";
			}
			else{
				svmType = "4";
			}
		}

		out.write("list-file: " + "RAND_sets.list" + "\n");
		out.write("activity-type: " + actFileDataType + "\n");
		out.write("modeling-dir: " + workingDir + "\n");
		out.write("y-random-dir: " + workingDir + "yRandom/" + "\n");
		
		//basic parameters
		out.write("svm-type: " + svmType + "\n");
		out.write("kernel-type: " + svmParameters.getSvmKernel() + "\n");
		
		out.write("shrinking-heuristics: " + svmParameters.getSvmHeuristics() + "\n");
		out.write("use-probability-heuristics: " + svmParameters.getSvmProbability() + "\n");
		out.write("c-svc-weight: " + svmParameters.getSvmWeight() + "\n");
		out.write("num-cross-validation-folds: " + svmParameters.getSvmCrossValidation() + "\n");
		out.write("tolerance-for-termination: " + svmParameters.getSvmEEpsilon() + "\n");
		
		//loop parameters
		out.write("cost-from: " + svmParameters.getSvmCostFrom() + "\n");
		out.write("cost-to: " + svmParameters.getSvmCostTo() + "\n");
		out.write("cost-step: " + svmParameters.getSvmCostStep() + "\n");
		
		out.write("gamma-from: " + svmParameters.getSvmGammaFrom() + "\n");
		out.write("gamma-to: " + svmParameters.getSvmGammaTo() + "\n");
		out.write("gamma-step: " + svmParameters.getSvmGammaStep() + "\n");

		out.write("degree-from: " + svmParameters.getSvmDegreeFrom() + "\n");
		out.write("degree-to: " + svmParameters.getSvmDegreeTo() + "\n");
		out.write("degree-step: " + svmParameters.getSvmDegreeStep() + "\n");

		out.write("nu-from: " + svmParameters.getSvmNuFrom() + "\n");
		out.write("nu-to: " + svmParameters.getSvmNuTo() + "\n");
		out.write("nu-step: " + svmParameters.getSvmNuStep() + "\n");
		
		out.write("loss-epsilon-from: " + svmParameters.getSvmPEpsilonFrom() + "\n");
		out.write("loss-epsilon-to: " + svmParameters.getSvmPEpsilonTo() + "\n");
		out.write("loss-epsilon-step: " + svmParameters.getSvmPEpsilonStep() + "\n");

		//model acceptance parameters
		out.write("model-acceptance-cutoff: " + svmParameters.getSvmCutoff() + "\n");
		
		out.close();
	}
	
	public static void svmPreProcess(SvmParameters svmParameters, String actFileDataType, String workingDir) throws Exception{
		
		if(! workingDir.endsWith("/yRandom/")){
			convertXtoSvm(Constants.MODELING_SET_X_FILE, Constants.MODELING_SET_A_FILE, workingDir);
			convertXtoSvm(Constants.EXTERNAL_SET_X_FILE, Constants.EXTERNAL_SET_A_FILE, workingDir);
		}
		
		//log file containing each model generated and its test set r^2 or CCR
		//used for debugging and checking progress
		BufferedWriter log = new BufferedWriter(new FileWriter(workingDir + "svm-modeling.log"));
		
		BufferedReader in = new BufferedReader(new FileReader(workingDir + "RAND_sets.list"));
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String[] data = inputString.split("\\s+");
			
			if(actFileDataType.equals(Constants.CONTINUOUS)){
				convertXtoSvm(data[0], data[1], workingDir);
				convertXtoSvm(data[3], data[4], workingDir);
			}
			else{
				//rand_sets_0_trn0.x rand_sets_0_trn0.a 4284 rand_sets_0_tst0.x rand_sets_0_tst0.a 1133
				convertXtoSvm(data[0], data[1], workingDir);
				convertXtoSvm(data[3], data[4], workingDir);
			}
		}
		log.close();			
		in.close();

		FileAndDirOperations.copyFile(Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH + "svm.py", workingDir + "svm.py")
	}

	public static void buildSvmModels(String workingDir){
		//run modeling (exec python script)
		String cmd = "python svm.py";
		RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "svm.py");
	}
	
	public static void buildSvmModelsLsf(String workingDir, String userName, String jobName){
		//run modeling (bsub the python script)
		 
		String cmd = "bsub -q idle -J cbench_" + userName + "_" + jobName + " -o bsubOutput.txt python svm.py";
		RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "svm.py");
	}
	
	public static ArrayList<SvmModel> readSvmModels(String workingDir){
		File dir = new File(workingDir);
		String[] files = dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}});
		
		return null;
	}
	
	public static void runSvmPrediction(String workingDir, String predictionXFileName) throws Exception{
		//find all models files in working dir
		//run svm-predict on the prediction file using each model
		//average the results
		
		convertXtoSvm(predictionXFileName, "", workingDir);
		
		String predictionFileName = predictionXFileName.replace(".x", ".svm");
		
		File dir = new File(workingDir);
		String[] files = dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".mod");}});
		for(int i = 0; i < files.length; i++){
			String command = "svm-predict " + predictionFileName + " " + files[i] + " " + files[i] + ".pred";
			RunExternalProgram.runCommandAndLogOutput(command, workingDir, "svm-predict-" + files[i]);
		}
	}
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, String predictionXFileName, Long predictorId) throws Exception{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
		
		ArrayList<String> compoundNames = DatasetFileOperations.getXCompoundNames(workingDir + predictionXFileName);
		
		for(int i = 0; i < compoundNames.size(); i++){
			PredictionValue pv = new PredictionValue();
			pv.setCompoundName(compoundNames.get(i));
			pv.setPredictedValue(new Float(0.0));
			pv.setPredictorId(predictorId);
			predictionValues.add(pv);
		}
		
		File dir = new File(workingDir);
		String[] files = dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}});
		for(int i = 0; i < files.length; i++){
			//open the prediction file and get the results for each compound.
			BufferedReader in = new BufferedReader(new FileReader(workingDir + files[i]));
			String line;
			int j = 0;
			while((line = in.readLine()) != null){
				if(! line.isEmpty()){
					predictionValues.get(j).setPredictedValue(Float.parseFloat(line.trim()) + 
							predictionValues.get(j).getPredictedValue());
					j++;
				}
			}
		}
		//Each predictionValue contains the sum of all predicted values. 
		//We need the average, so divide each value by numModels.
		for(PredictionValue pv : predictionValues){
			if(files.length > 0){
				pv.setPredictedValue( pv.getPredictedValue() / files.length);
			}
			pv.setNumModelsUsed(files.length);
			pv.setNumTotalModels(files.length);
			pv.setStandardDeviation(new Float(0.1)); //calculate this later once other stuff works
		}
		
		return predictionValues;
	}
	
	public static ArrayList<ExternalValidation> readExternalPredictionOutput(String workingDir, Long predictorId) throws Exception{
		ArrayList<ExternalValidation> externalPredictions = new ArrayList<ExternalValidation>();

		//set compound names
		String line;
		BufferedReader br = new BufferedReader(new FileReader(workingDir + "ext_0.a"));
		while((line = br.readLine()) != null){
			if(! line.isEmpty()){
				String[] tokens = line.split("\\s+");
				ExternalValidation ev = new ExternalValidation();
				ev.setCompoundId(tokens[0]);
				ev.setActualValue(Float.parseFloat(tokens[1]));
				externalPredictions.add(ev);
			}
		}
		
		File dir = new File(workingDir);
		String[] files = dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}});
		for(int i = 0; i < files.length; i++){
			//open the prediction file and get the results for each compound.
			BufferedReader in = new BufferedReader(new FileReader(workingDir + files[i]));
			int j = 0;
			while((line = in.readLine()) != null){
				if(! line.isEmpty()){
					externalPredictions.get(j).setPredictedValue(Float.parseFloat(line.trim()) + 
							externalPredictions.get(j).getPredictedValue());
					j++;
				}
			}
			//This is the last time we'll need the external prediction output. Delete it.
			FileAndDirOperations.deleteFile(workingDir + files[i]);
		}
		
		//Each predictionValue contains the sum of all predicted values. 
		//We need the average, so divide each value by numModels.
		//set the predictor ID at the same time
		Session session = HibernateUtil.getSession();
		for(ExternalValidation pv : externalPredictions){
			if(files.length > 0){
				pv.setPredictedValue(pv.getPredictedValue() / files.length);
			}
			pv.setNumModels(files.length);
			pv.setStandDev("0.1");
			pv.setPredictor(PopulateDataObjects.getPredictorById(predictorId, session));
		}
		session.close();
		
		return externalPredictions;
	}
}