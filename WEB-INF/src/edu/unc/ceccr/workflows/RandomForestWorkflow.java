package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.print.attribute.standard.JobName;

public class RandomForestWorkflow{

	//MODELING WORKFLOW FUNCTIONS
	public static void SetUpYRandomization(String userName, String jobName) throws Exception{
		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		//create yRandom dirs
		new File(workingdir + "yRandom/").mkdir();
		new File(workingdir + "yRandom/Logs/").mkdir();

		//make sure dirs are empty
		FileAndDirOperations.deleteDirContents(workingdir + "yRandom/");
		FileAndDirOperations.deleteDirContents(workingdir + "yRandom/Logs/");
		
		//copy file to yRandom
		String fromDir = workingdir;
		String toDir = workingdir + "yRandom/";
		BufferedReader in = new BufferedReader(new FileReader(workingdir + "RF_RAND_sets.list"));
		Utility.writeToDebug("Copying RF_RAND_sets.list from " + fromDir + " to " + toDir);
		FileChannel ic = new FileInputStream(fromDir + "RF_RAND_sets.list").getChannel();
		FileChannel oc = new FileOutputStream(toDir + "RF_RAND_sets.list").getChannel();
		ic.transferTo(0, ic.size(), oc);
		ic.close();
		oc.close(); 
		
		String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
		
		Utility.writeToDebug("Copying " + newExternalXFile + " from " + fromDir + " to " + toDir);
		ic = new FileInputStream(fromDir + newExternalXFile).getChannel();
		oc = new FileOutputStream(toDir + newExternalXFile).getChannel();
		ic.transferTo(0, ic.size(), oc);
		ic.close();
		oc.close();
		
		Utility.writeToDebug("Copying files in RF_RAND_sets.list from " + fromDir + " to " + toDir);
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String[] data = inputString.split("\\s+");
			String[] files = new String[4];
			files[0] = data[0];
			files[1] = data[1];
			files[2] = data[3];
			files[3] = data[4];
			
			for(int i = 0; i<files.length; i++)
			{
				if(new File(fromDir + files[i]).exists()){
					ic = new FileInputStream(fromDir + files[i]).getChannel();
					oc = new FileOutputStream(toDir + files[i]).getChannel();
					ic.transferTo(0, ic.size(), oc);
					ic.close();
					oc.close();	
				}
			}
		}
		
		String yRandomDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/yRandom/";
		Utility.writeToDebug("YRandomization", userName, jobName);
		File dir = new File(yRandomDir);
		String files[] = dir.list();
		if(files == null){
			Utility.writeToDebug("Error reading directory: " + yRandomDir);
		}
		int x = 0;
		Utility.writeToDebug("Randomizing each activity file (*rand_sets*.a) in dir " + yRandomDir);
		while(files != null && x<files.length){
			if(files[x].matches(".*rand_sets.*a")){
				//shuffle the values in each .a file (ACT file)
				DatasetFileOperations.randomizeActivityFile(yRandomDir + files[x], yRandomDir + files[x]);
			}
			x++;
		}
		
	}
	
	public static void makeRandomForestXFiles(String scalingType, String workingDir) throws Exception {
		//changes the usual .x files to random forest versions.

		BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + "RF_RAND_sets.list"));
		BufferedReader in = new BufferedReader(new FileReader(workingDir + "RAND_sets.list"));
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			if(! inputString.contains("#")){
				String[] data = inputString.split("\\s+");
				preProcessXFile(scalingType, data[0], "RF_" + data[0], workingDir);
				preProcessXFile(scalingType, data[3], "RF_" + data[3], workingDir);
				out.write(inputString.replace(data[0], "RF_" + data[0]).replace(data[3], "RF_" + data[3]) + System.getProperty("line.separator"));
			}
		}
		in.close();
		out.flush();
		out.close();
		
		//fix external set .x file too
		String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
		preProcessXFile(scalingType, Constants.EXTERNAL_SET_X_FILE, newExternalXFile, workingDir);
		
	}
	
	public static void buildRandomForestModels(RandomForestParameters randomForestParameters, String actFileDataType, String scalingType, String categoryWeights, String workingDir, String jobName) throws Exception{
		
		String command = "";
		Utility.writeToDebug("Running Random Forest Modeling...");
		
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
		String buildModelScript = scriptDir + Constants.RF_BUILD_MODEL_RSCRIPT;
		
		// build model script parameter
		String type = actFileDataType.equals(Constants.CATEGORY) ? "classification" : "regression";
		String ntree = randomForestParameters.getNumTrees().trim();
		String mtry = randomForestParameters.getDescriptorsPerTree().trim();
//		String classwt = categoryWeights;
		String classwt = "NULL";
		String nodesize = randomForestParameters.getMinTerminalNodeSize();
		String maxnodes = randomForestParameters.getMaxNumTerminalNodes();
		
		String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
		if(maxnodes.equals("0")) maxnodes = "NULL";
		command = "Rscript --vanilla " + buildModelScript
					   + " --scriptsDir " + scriptDir
					   + " --workDir " + workingDir
					   + " --externalXFile " + newExternalXFile
					   + " --dataSplitsListFile " + "RF_RAND_sets.list"
					   + " --type " + type
					   + " --ntree " + ntree
					   + " --mtry " + mtry
					   + " --classwt " + classwt
					   + " --nodesize " + nodesize
					   + " --maxnodes " + maxnodes;
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "randomForestBuildModel", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
	}
	

	public static ArrayList<ExternalValidation> readExternalSetPredictionOutput(String workingDir, Predictor predictor) throws Exception
	{
		//note that in Random Forest, making external predictions is done automatically
		//as part of the modeling process.
		
		ArrayList<ExternalValidation> allExternalValues = new ArrayList<ExternalValidation>();
		BufferedReader in = new BufferedReader(new FileReader(workingDir + Constants.EXTERNAL_SET_A_FILE));
		String inputString;
		
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String data[] = inputString.split("\\s+"); //Note: [0] is the compound name and [1] is the activity value.
			ExternalValidation externalValidationValue = new ExternalValidation();
			externalValidationValue.setPredictor(predictor);
			externalValidationValue.setCompoundId(data[0]);
			externalValidationValue.setActualValue(new Float(data[1]).floatValue());
			allExternalValues.add(externalValidationValue);
		}
		in.close();
		
		in = new BufferedReader(new FileReader(workingDir + "RF_" + Constants.EXTERNAL_SET_X_FILE.replace(".x", ".pred")));
		inputString = in.readLine(); // header
		for(int i=0; i<allExternalValues.size(); i++)
		{
			ExternalValidation externalValidationValue = allExternalValues.get(i);
			inputString = in.readLine();
			if(inputString != null && ! inputString.trim().isEmpty()){
			
				String[] data = inputString.split("\\s+"); //Note: [0] is the compound name and the following are the predicted values.
				
				Float[] compoundPredictedValues = new Float[data.length -1];
				
				externalValidationValue.setNumModels(compoundPredictedValues.length);
				
				float sum=0;
				for(int j=0; j<compoundPredictedValues.length; j++)
				{
					compoundPredictedValues[j] = new Float(data[j+1]);
					sum += compoundPredictedValues[j].floatValue();
				}
				
				float mean = sum / compoundPredictedValues.length;
				externalValidationValue.setPredictedValue((new Float(mean)));
				
				double sumDistFromMeanSquared = 0.0;
				for(int j=0; j<compoundPredictedValues.length; j++)
				{
					double distFromMean = compoundPredictedValues[j].doubleValue() - (double)mean;
					sumDistFromMeanSquared += Math.pow(distFromMean, (double)2);
				}
				double stdDev = Math.sqrt(sumDistFromMeanSquared/(double)compoundPredictedValues.length);
				externalValidationValue.setStandDev(Utility.roundSignificantFigures(Double.toString(stdDev), 4));
			}
		}
		
		return allExternalValues;
	}
		
	public static ArrayList<RandomForestGrove> readRandomForestGroves(String workingDir, Predictor predictor, String isYRandomModel)  throws Exception{
		ArrayList<RandomForestGrove> randomForestModels = new ArrayList<RandomForestGrove>();
		
		// read the models list
		BufferedReader in = new BufferedReader(new FileReader(workingDir + Constants.RF_DESCRIPTORS_USED_FILE));
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			//for each model
			String[] data = inputString.split("\t"); // [0] is the grove name, [1] is the list of descriptors used in this grove
			RandomForestGrove m = new RandomForestGrove();
			m.setPredictor_id(predictor.getPredictorId());
			m.setName(data[0]);
			m.setDescriptorsUsed(data[1]);
			m.setIsYRandomModel(isYRandomModel);
			randomForestModels.add(m);
		}
		in.close();
		return randomForestModels;
	}
	
	public static ArrayList<RandomForestTree> readRandomForestTrees(String workingDir, Predictor predictor, RandomForestGrove grove, String actFileDataType) throws Exception{
		ArrayList<RandomForestTree> randomForestTrees = new ArrayList<RandomForestTree>();
		
		if(actFileDataType.equals(Constants.CONTINUOUS))
		{
			ArrayList<String> treeFileName = new ArrayList<String>();
			ArrayList<String> treeR2 = new ArrayList<String>();
			ArrayList<String> treeMse = new ArrayList<String>();
			ArrayList<String> treeDescriptorsUsed = new ArrayList<String>();
			
			BufferedReader in = new BufferedReader(new FileReader(workingDir + grove.getName() + "_trees.list"));
			String inputString;
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				treeFileName.add(inputString);
			}
			in.close();
			
			in = new BufferedReader(new FileReader(workingDir + grove.getName() + ".rsq"));
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				treeR2.add(inputString);
			}
			in.close();
			
			in = new BufferedReader(new FileReader(workingDir + grove.getName() + ".mse"));
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				treeMse.add(inputString);
			}
			in.close();
			
			in = new BufferedReader(new FileReader(workingDir + grove.getName() + "_desc_used_in_trees.txt"));
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				treeDescriptorsUsed.add(inputString);
			}
			in.close();
			
			//for each tree
			for(int i=0; i<treeFileName.size(); i++)
			{
				RandomForestTree t = new RandomForestTree();
				t.setRandomForestGroveId(grove.getId());
				t.setTreeFileName(treeFileName.get(i));
				t.setR2(Utility.roundSignificantFigures(treeR2.get(i), 4));
				t.setMse(Utility.roundSignificantFigures(treeMse.get(i), 4));
				t.setDescriptorsUsed(treeDescriptorsUsed.get(i));
				randomForestTrees.add(t);
			}
		}
		else
		{
			ArrayList<String> treeFileName = new ArrayList<String>();
			ArrayList<String> treeDescriptorsUsed = new ArrayList<String>();
			BufferedReader in = new BufferedReader(new FileReader(workingDir + grove.getName() + "_trees.list"));
			String inputString;
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				treeFileName.add(inputString);
			}
			in.close();
			
			in = new BufferedReader(new FileReader(workingDir + grove.getName() + "_desc_used_in_trees.txt"));
			while ((inputString = in.readLine()) != null && ! inputString.equals(""))
			{
				treeDescriptorsUsed.add(inputString);
			}
			in.close();
			
			//for each tree
			for(int i=0; i<treeFileName.size(); i++)
			{
				RandomForestTree t = new RandomForestTree();
				t.setRandomForestGroveId(grove.getId());
				t.setTreeFileName(treeFileName.get(i));
				t.setDescriptorsUsed(treeDescriptorsUsed.get(i));
				randomForestTrees.add(t);
			}
		}
		return randomForestTrees;
	}
	
	
	//END MODELING WORKFLOW FUNCTIONS
	
	//PREDICTION WORKFLOW FUNCTIONS
	public static void runRandomForestPrediction(String workingDir, String jobName, String sdfile, Predictor predictor) throws Exception{
		String xFile = sdfile + ".renorm.x";
		String newXFile = "RF_" + xFile;
		preProcessXFile(predictor.getScalingType(), xFile, newXFile, workingDir);
		
		String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
		String predictScript = scriptDir + Constants.RF_PREDICT_RSCRIPT;
		String modelsListFile = "models.list";
		String command = "Rscript --vanilla " + predictScript
							  + " --scriptsDir " + scriptDir
							  + " --workDir " + workingDir
							  + " --modelsListFile " + modelsListFile
							  + " --xFile " + newXFile;
		
		Utility.writeToDebug("Running external program: " + command + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(command, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "randomForestPredict", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		Utility.writeToDebug("Exit value: " + p.exitValue());
		if(p.exitValue() != 0)
		{
			Utility.writeToDebug("	See error log");
		}
	}
	
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>(); //holds objects to be returned
		
		// Get the predicted values of the forest
		String outputFile = Constants.PRED_OUTPUT_FILE + ".preds";
		Utility.writeToDebug("Reading consensus prediction file: " + workingDir + outputFile);
		BufferedReader in = new BufferedReader(new FileReader(workingDir + outputFile));
		String inputString;
		
		in.readLine(); // first line is the header with the model name
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String[] data = inputString.split("\\s+"); //Note: [0] is the compound name and the following are the predicted values.
			
			PredictionValue p = new  PredictionValue();
			p.setPredictorId(predictorId);
			p.setCompoundName(data[0]);
			
			Float[] compoundPredictedValues = new Float[data.length -1];
			p.setNumTotalModels(compoundPredictedValues.length);
			p.setNumModelsUsed(compoundPredictedValues.length);
			float sum=0;
			for(int i=0; i<compoundPredictedValues.length; i++)
			{
				compoundPredictedValues[i] = new Float(data[i+1]);
				sum += compoundPredictedValues[i].floatValue();
			}
			float mean = sum / compoundPredictedValues.length;
			p.setPredictedValue((new Float(mean)));
			
			double sumDistFromMeanSquared = 0.0;
			for(int i=0; i<compoundPredictedValues.length; i++)
			{
				double distFromMean = compoundPredictedValues[i].doubleValue() - (double)mean;
				sumDistFromMeanSquared += Math.pow(distFromMean, (double)2);
			}
			double stdDev = Math.sqrt(sumDistFromMeanSquared/(double)compoundPredictedValues.length);
			p.setStandardDeviation(new Float(stdDev));
			
			predictionValues.add(p);
		}
		in.close();
		
		return predictionValues;
	}
	//END PREDICTION WORKFLOW FUNCTIONS
	
	//HELPER FUNCTIONS
	public static void preProcessXFile(String scalingType, String xFile, String newXFile, String workingDir) throws Exception
	{
		//if scaling was applied, the last 2 lines of a .x file will contain the scaling ranges.
		//Random Forest can't deal with these last 2 lines, so they must be removed.
		//Also, descriptor names containing "#" character will break Random Forest, so these
		//are changed to "=_" instead.
				
		
		if(! new File(workingDir + xFile).exists()){
			return;
		}
		
		BufferedReader br = new BufferedReader(new FileReader(workingDir + xFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + newXFile));
		
		//header
		String line = br.readLine();
		String[] headerInfo = line.split("\\s+");
		int numCompounds = Integer.parseInt(headerInfo[0]);
		out.write(line + "\n");
		
		//descriptor names
		line = br.readLine().replaceAll("#", "=_");
		out.write(line + "\n");
		
		//descriptor values
		int lineCount = 0;
		while((line = br.readLine()) != null){
			if(lineCount < numCompounds){
				out.write(line + "\n");
				lineCount++;
			}
		}
		out.close();
		br.close();
	}

	//END HELPER FUNCTIONS

}