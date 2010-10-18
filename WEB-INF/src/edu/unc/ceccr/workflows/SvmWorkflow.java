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
import java.util.Scanner;

import org.hibernate.Session;

public class SvmWorkflow{

	public static void convertXtoSvm(String xFileName, String aFileName, String workingDir) throws Exception{
		//generates an SVM-compatible input descriptor file
		
		//Utility.writeToDebug("Generating an SVM-compatible file: " + xFileName + " + " + aFileName + " => " + xFileName.replace(".x", ".svm"));

		ArrayList<String> activityValues = new ArrayList<String>();
		if(aFileName != null && ! aFileName.isEmpty()){
			//read in the activity file
			
			BufferedReader in = new BufferedReader(new FileReader(workingDir + aFileName));
			String inputString;
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				//for each model
				String[] data = inputString.split("\\s+"); // [0] is the compound id, [1] is the activity value
				activityValues.add(data[1]);
			}
			in.close();
		}
		else{
			//if no activity file is supplied, just use zeros for activities
			int numCompounds = DatasetFileOperations.getXCompoundNames(workingDir + xFileName).size();
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
	
	public static void buildSvmModels(SvmParameters svmParameters, String actFileDataType, String workingDir) throws Exception{
		double cutoff = 0.4;
		
		/*
		Usage: svm-train [options] training_set_file [model_file]
		options:
		-s svm_type : set type of SVM (default 0)
		        0 -- C-SVC
		        1 -- nu-SVC
		        2 -- one-class SVM
		        3 -- epsilon-SVR
		        4 -- nu-SVR
		-t kernel_type : set type of kernel function (default 2)
		        0 -- linear: u'*v
		        1 -- polynomial: (gamma*u'*v + coef0)^degree
		        2 -- radial basis function: exp(-gamma*|u-v|^2)
		        3 -- sigmoid: tanh(gamma*u'*v + coef0)
		        4 -- precomputed kernel (kernel values in training_set_file)
		-d degree : set degree in kernel function (default 3)
		-g gamma : set gamma in kernel function (default 1/num_features)
		-r coef0 : set coef0 in kernel function (default 0)
		-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)
		-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
		-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)
		-m cachesize : set cache memory size in MB (default 100)
		-e epsilon : set tolerance of termination criterion (default 0.001)
		-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)
		-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)
		-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)
		-v n: n-fold cross validation mode
		*/
		
		Utility.writeToDebug("Running SVM Modeling in dir: " + workingDir);
		if(! workingDir.endsWith("yRandom/")){
			convertXtoSvm(Constants.MODELING_SET_X_FILE, Constants.MODELING_SET_A_FILE, workingDir);
			convertXtoSvm(Constants.EXTERNAL_SET_X_FILE, Constants.EXTERNAL_SET_A_FILE, workingDir);
		}
		
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
			
			//generate SVM models for this train-test split
			
			for(Float cost = Float.parseFloat(svmParameters.getSvmCostFrom()); 
				cost <= Float.parseFloat(svmParameters.getSvmCostTo()); 
				cost += Float.parseFloat(svmParameters.getSvmCostStep())){
				
				//remove annoying floating-point errors, if any
				String costStr = "" + cost;
				if(costStr.contains("0000")){
					costStr = costStr.substring(0, costStr.indexOf("0000"));
				}
				
				for(Float degree = Float.parseFloat(svmParameters.getSvmDegreeFrom());
					degree <= Float.parseFloat(svmParameters.getSvmDegreeTo());
					degree += Float.parseFloat(svmParameters.getSvmDegreeStep())){
					
					String degreeStr = "" + degree;
					if(degreeStr.contains("0000")){
						degreeStr = degreeStr.substring(0, degreeStr.indexOf("0000"));
					}
					
					for(Float gamma = Float.parseFloat(svmParameters.getSvmGammaFrom());
					gamma <= Float.parseFloat(svmParameters.getSvmGammaTo());
					gamma += Float.parseFloat(svmParameters.getSvmGammaStep())){
					
						String gammaStr = "" + gamma;
						if(gammaStr.contains("0000")){
							gammaStr = gammaStr.substring(0, gammaStr.indexOf("0000"));
						}
						
						for(Float nu = Float.parseFloat(svmParameters.getSvmNuFrom());
						nu <= Float.parseFloat(svmParameters.getSvmNuTo());
						nu += Float.parseFloat(svmParameters.getSvmNuStep())){
						

							String nuStr = "" + nu;
							if(nuStr.contains("0000")){
								nuStr = nuStr.substring(0, nuStr.indexOf("0000"));
							}
							
							for(Float pEpsilon = Float.parseFloat(svmParameters.getSvmPEpsilonFrom());
							pEpsilon <= Float.parseFloat(svmParameters.getSvmPEpsilonTo());
							pEpsilon += Float.parseFloat(svmParameters.getSvmPEpsilonStep())){

								String pEpsilonStr = "" + pEpsilon;
								if(pEpsilonStr.contains("0000")){
									pEpsilonStr = pEpsilonStr.substring(0, pEpsilonStr.indexOf("0000"));
								}
								
								String command = "svm-train ";
								
								//svm type
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
								command += "-s " + svmType + " ";
								
								//kernel type
								command += "-t " + svmParameters.getSvmKernel() + " ";
									
								//parameters from for loop
								command += "-d " + degreeStr + " ";
								command += "-g " + gammaStr + " ";
								command += "-c " + costStr + " ";
								command += "-n " + nuStr + " ";
								command += "-p " + pEpsilonStr + " ";
								
								//tolerance
								command += "-e " + svmParameters.getSvmEEpsilon() + " ";
								
								//shrinking and probability booleans
								command += "-h " + svmParameters.getSvmHeuristics() + " ";
								command += "-b " + svmParameters.getSvmProbability() + " ";
								
								//class weight parameter
								command += "-wi " + svmParameters.getSvmWeight() + " ";
								
								//cross validation
								if(Integer.parseInt(svmParameters.getSvmCrossValidation()) != 0){
									command += "-v " + svmParameters.getSvmCrossValidation() + " ";
								}

								//input file name
								String inputFile = data[0].replace(".x", ".svm");
								command += " " + inputFile + " ";
								
								//output file name
								String modelFileName = inputFile.replace(".svm", "") + "_d" + degreeStr + "_g" + gammaStr + "_c" + costStr + "_n" + nuStr + "_p" + pEpsilonStr + ".mod";
								command += modelFileName;
								
								RunExternalProgram.runCommandAndLogOutput(command, workingDir, "svm-train" + modelFileName);
								
								//run prediction on test set
								String testFileName = data[3].replace(".x", ".svm");
								String predictionOutputFileName = modelFileName + ".pred-test";
								
								String command2 = "svm-predict " + testFileName + " " + modelFileName + " " + predictionOutputFileName;
								
								RunExternalProgram.runCommandAndLogOutput(command2, workingDir, "svm-predict" + modelFileName);
								
								//eliminate (delete) model if it doesn't pass its CCR or r^2 cutoff
								
								//get predicted and actual (test set) values from files
								//read test .a file
								String testActivityFileName = testFileName.replace(".svm", ".a");
								
								BufferedReader br = new BufferedReader(new FileReader(workingDir + testActivityFileName));
								String line = "";
								ArrayList<Double> testValues = new ArrayList<Double>();
								while((line = br.readLine()) != null){
									if(!line.isEmpty()){
										String[] parts = line.split("\\s+");
										testValues.add(Double.parseDouble(parts[1]));
									}
								}
								br.close();
								
								//read predicted .a file
								br = new BufferedReader(new FileReader(workingDir + predictionOutputFileName));
								ArrayList<Double> predictedValues = new ArrayList<Double>();
								while((line = br.readLine()) != null){
									if(!line.isEmpty()){
										predictedValues.add(Double.parseDouble(line));
									}
								}
								br.close();
								
								if(testValues.size() != predictedValues.size()){
									Utility.writeToDebug("Warning: test set act file has " + testValues.size() + 
											" entries, but predicted file has " + 
											predictedValues.size() + " entries for file: " + predictionOutputFileName);
								}
								
								boolean modelIsGood = true;
								
								if(actFileDataType.equals(Constants.CONTINUOUS)){
									//calculate r^2 for test set prediction
									
									Double avg = 0.0;
									for(Double testValue : testValues){
										avg += testValue;
									}
									avg /= testValues.size();
									Double ssErr = 0.0;
									for(int i = 0; i < testValues.size(); i++){
										Double residual = testValues.get(i) - predictedValues.get(i);
										ssErr += residual * residual;
									}
									Double ssTot = 0.0;
									for(Double testValue : testValues){
										ssTot += (testValue - avg) * (testValue - avg);
									}
									Double rSquared = 0.0;
									if(ssTot != 0){
										rSquared = Double.parseDouble(Utility.roundSignificantFigures("" + (1 - (ssErr / ssTot)), 4));
									}
									if(rSquared < cutoff){
										modelIsGood = false;
									}
								}
								else if(actFileDataType.equals(Constants.CATEGORY)){
									//calculate CCR for test set prediction
									int numCorrect = 0;
									int numIncorrect = 0;
									for(int i = 0; i < testValues.size(); i++){
										if(Math.round(testValues.get(i)) == Math.round(predictedValues.get(i))){
											numCorrect++;
										}
										else{
											numIncorrect++;
										}
									}
									if((numCorrect / (numCorrect + numIncorrect)) < cutoff){
										Utility.writeToDebug("bad model: ccr = " + (numCorrect / (numCorrect + numIncorrect)));
										modelIsGood = false;
									}
								}
								
								if(! modelIsGood){
									//delete it
									FileAndDirOperations.deleteFile(workingDir + modelFileName);
									FileAndDirOperations.deleteFile(workingDir + predictionOutputFileName);
								}
								
								//read MSE and correlation coeff. for prediction
								//String s = FileAndDirOperations.readFileIntoString(workingDir + "Logs/" + "svm-predict" + modelFileName + ".log");
								//Utility.writeToDebug(s);
								
							}
						}
					}
				}
			}
			
			//delete all the models on the list of bad models
			
			
		}
		in.close();
		
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
			predictionValues.add(pv);
		}
		
		File dir = new File(workingDir);
		String[] files = dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}});
		for(int i = 0; i < files.length; i++){
			//open the prediction file and get the results for each compound.
			BufferedReader in = new BufferedReader(new FileReader(workingDir + files[i] + ".pred"));
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
			pv.setPredictedValue( pv.getPredictedValue() / files.length);
			pv.setNumModelsUsed(files.length);
			pv.setNumTotalModels(files.length);
			pv.setStandardDeviation(new Float(0.1)); //calculate this later once other stuff works
		}
		
		return predictionValues;
	}
	
	public static ArrayList<ExternalValidation> readExternalPredictionOutput(String workingDir, Long predictorId) throws Exception{
		ArrayList<ExternalValidation> externalPredictions = new ArrayList<ExternalValidation>();
		File dir = new File(workingDir);
		String[] files = dir.list(new FilenameFilter() {public boolean accept(File arg0, String arg1) {return arg1.endsWith(".pred");}});
		for(int i = 0; i < files.length; i++){
			//open the prediction file and get the results for each compound.
			BufferedReader in = new BufferedReader(new FileReader(workingDir + files[i]));
			String line;
			int j = 0;
			while((line = in.readLine()) != null){
				if(! line.isEmpty()){
					externalPredictions.get(j).setPredictedValue(Float.parseFloat(line.trim()) + 
							externalPredictions.get(j).getPredictedValue());
					j++;
				}
			}
		}
		
		//set compound names
		ArrayList<String> extCompoundNames = DatasetFileOperations.getACTCompoundNames(workingDir + "ext_0.a");
		for(int i = 0; i < extCompoundNames.size(); i++){
			externalPredictions.get(i).setCompoundId(extCompoundNames.get(i));
		}
		
		//Each predictionValue contains the sum of all predicted values. 
		//We need the average, so divide each value by numModels.
		//set the predictor ID at the same time
		Session session = HibernateUtil.getSession();
		for(ExternalValidation pv : externalPredictions){
			pv.setPredictedValue(pv.getPredictedValue() / files.length);
			pv.setNumModels(files.length);
			pv.setStandDev("0.1");
			pv.setPredictor(PopulateDataObjects.getPredictorById(predictorId, session));
		}
		session.close();
		
		return externalPredictions;
	}
}