package edu.unc.ceccr.workflows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.commons.validator.GenericValidator;

import edu.unc.ceccr.action.ViewPredictorAction.descriptorFrequency;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

//performs all functions relating to reading the output from a KNN run.
//There's a ton of them. O.o

public class KnnOutputWorkflow{

	String[] externalValues = null;
	ArrayList<ExternalValidation> allExternalValues = null;
	
	
	public static ArrayList<KnnModel> parseContinuouskNNOutput(String fileLocation, String flowType) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation + Constants.kNN_OUTPUT_FILE));
		String inputString;
		String[] kNNValues = null;
		ArrayList<KnnModel> knnModels;
		knnModels = new ArrayList<KnnModel>();
	
		while ((inputString = in.readLine()) != null) {
			// 5 types of lines found in the knn-outputsort.tbl file:
			// | STATISTICS OF REGRESSION LINES Y = b11*X + b01 AND X =
			// b12*numTrainModels + b02 ...
			// nnn q^2 n ...
			// ----------------------------------- ...
			// blank lines
			// data lines (keep only the data)
			if (inputString.trim().startsWith("|")
					|| inputString.trim().startsWith("-")
					|| inputString.trim().equals("")
					|| inputString.trim().startsWith("nnn")) {
				// skip all rows that don't have data
			} else {
				kNNValues = inputString.split("\\s+");
				KnnModel knnOutput = createContinuousKnnOutputObject(fileLocation, kNNValues, flowType);
				knnModels.add(knnOutput);
			}
		}
		in.close();
		return knnModels;
	}
	
	public static ArrayList<KnnModel> parseCategorykNNOutput(String fileLocation, String flowType) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation + Constants.kNN_OUTPUT_FILE));
		String inputString;
		String[] kNNValues = null;
		ArrayList<KnnModel> knnModels = new ArrayList<KnnModel>();

		while ((inputString = in.readLine()) != null) 
		{
			// data lines (keep only the data)
			if (inputString.trim().startsWith("|")
					|| inputString.trim().startsWith("-")
					|| inputString.trim().equals("")
					|| inputString.trim().startsWith("STATISTICS")
					|| inputString.trim().startsWith("NCompTrain")) 
			{
				// skip all rows that don't have data
			} else {
				kNNValues = inputString.split("\\s+");
				KnnModel knnOutput = createCategoryKnnOutputObject(fileLocation, kNNValues, flowType);
				knnModels.add(knnOutput);
			}
		}
		in.close();
		return knnModels;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<ExternalValidation> parseExternalValidationOutput(String fileLocation,
			String file_path) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(fileLocation));
		String inputString;

		ArrayList<ExternalValidation> externalSetPredictions = new ArrayList<ExternalValidation>();
		while ((inputString = in.readLine()) != null) {
			String[] externalValues = inputString.split("\\s+");
			ExternalValidation extPrediction = createExternalValidationObject(file_path, externalValues);
			externalSetPredictions.add(extPrediction);
		}
		
		
		
		return externalSetPredictions;
	}
	
	private static ExternalValidation createExternalValidationObject(
			String file_path, String[] extValues) throws Exception {
		if (extValues == null) {
			return null;
		}
		ExternalValidation extValOutput = new ExternalValidation();
		extValOutput.setCompoundId(extValues[Constants.COMPOUND_ID]);
	
		extValOutput.setActualValue(Float.parseFloat(extValues[Constants.ACTUAL]));
		
		if (GenericValidator.isFloat(extValues[Constants.PREDICTED]))
			extValOutput.setPredictedValue(Float.parseFloat(extValues[Constants.PREDICTED]));
		
		if (GenericValidator.isFloat(extValues[Constants.PREDICTED]))
			extValOutput.setNumModels(Integer.parseInt(extValues[Constants.NUM_MODELS]));
		
		parseConpredStdDev(file_path);
		
		return extValOutput;
	}
	
	
	public static KnnModel createContinuousKnnOutputObject(String filePath, String[] kNNValues, String flowType) {
		// The values array starts at 1 - not 0!
		if (kNNValues == null) {
			return null;
		}
		if (kNNValues.length <= 1) {
			return null;
		}
		KnnModel knnOutput = new KnnModel();
		knnOutput.setKnnType(Constants.CONTINUOUS);
		knnOutput.setNnn(Integer.parseInt(kNNValues[Constants.CONTINUOUS_NNN_LOCATION]));
		knnOutput.setQSquared(Float.parseFloat(kNNValues[Constants.CONTINUOUS_Q_SQUARED_LOCATION]));
		knnOutput.setN(Integer.parseInt(kNNValues[Constants.CONTINUOUS_N_LOCATION]));
		knnOutput.setR(Float.parseFloat(kNNValues[Constants.CONTINUOUS_R_LOCATION]));
		knnOutput.setRSquared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R_SQUARED_LOCATION]));
		knnOutput.setR01Squared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R01_SQUARED_LOCATION]));
		knnOutput.setR02Squared(Float
				.parseFloat(kNNValues[Constants.CONTINUOUS_R02_SQUARED_LOCATION]));
		knnOutput.setK1(Float.parseFloat(kNNValues[Constants.CONTINUOUS_K1_LOCATION]));
		knnOutput.setK2(Float.parseFloat(kNNValues[Constants.CONTINUOUS_K2_LOCATION]));
		knnOutput.setFlowType(flowType);
		
		String fileName = kNNValues[25];
		knnOutput.setFile(fileName);
		
		//get descriptors from model file
		//noncritical; if this fails, just write out the error, don't fail the job
		try{
			//fileName contains the .pred file, but we want the .mod file
			fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".mod";
			
			File modelFile = new File(filePath + fileName);
			BufferedReader br = new BufferedReader(new FileReader(modelFile));
			br.readLine(); 
			br.readLine();
			//descriptor names are the third line of the model file
			knnOutput.setDescriptorsUsed(br.readLine());
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		return knnOutput;
	}

	public static KnnModel createCategoryKnnOutputObject(String filePath, String[] kNNValues, String flowType) {
		// The values array starts at 1 - not 0!
		if (kNNValues == null) {
			return null;
		}
		if (kNNValues.length <= 1) {
			return null;
		}
		KnnModel knnOutput = new KnnModel();
		knnOutput.setKnnType(Constants.CATEGORY);
		knnOutput.setNnn(Integer.parseInt(kNNValues[Constants.CATEGORY_NNN_LOCATION]));
		knnOutput.setTrainingAcc(Float
				.parseFloat(kNNValues[Constants.CATEGORY_TRAINING_ACC_LOCATION]));
		knnOutput.setNormalizedTrainingAcc(Float.parseFloat(kNNValues[Constants.CATEGORY_NORMALIZED_TRAINING_ACC_LOCATION]));
		
		knnOutput.setTestAcc(Float.parseFloat(kNNValues[Constants.CATEGORY_TEST_ACC_LOCATION]));
		knnOutput.setNormalizedTestAcc(Float
				.parseFloat(kNNValues[Constants.CATEGORY_NORMALIZED_TEST_ACC_LOCATION]));

		knnOutput.setFlowType(flowType);
		
		String fileName = kNNValues[12];
		knnOutput.setFile(fileName);
		
		//get descriptors from model file
		//noncritical; if this fails, just write out the error, don't fail the job
		try{
			//fileName contains the .pred file, but we want the .mod file
			fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".mod";
			File modelFile = new File(filePath + fileName);
			BufferedReader br = new BufferedReader(new FileReader(modelFile));
			br.readLine(); 
			br.readLine();
			//descriptor names are the third line of the model file
			knnOutput.setDescriptorsUsed(br.readLine());
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		return knnOutput;
	}

	public static void addStdDeviation(ArrayList<ExternalValidation> externalSetPredictions, String consPredFile)
	{
		ArrayList<String> stdDevList = parseConpredStdDev(consPredFile);
		
		Iterator it1=externalSetPredictions.iterator();
		Iterator it2=stdDevList.iterator();
		while(it1.hasNext()&&it2.hasNext())
		{
			((ExternalValidation)it1.next()).setStandDev((String)it2.next());
		}
	}

	public static ArrayList parseConpredStdDev(String path)throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(path));
		String inputString;
		ArrayList<String> stdDevValues = new ArrayList<String>();
		while ((inputString = in.readLine()) != null) {
			String[] externalValues = inputString.split("\\s+");
			
			if(externalValues.length==4)
			{
				if(GenericValidator.isFloat(externalValues[Constants.STD_DEVIATION])){
					stdDevValues.add(externalValues[Constants.STD_DEVIATION]);}
				else{
					stdDevValues.add("No value");
				}
			}
			else{
				if(externalValues.length==3){
					stdDevValues.add("No value");
				}
			}
		}
		return stdDevValues;
	}
}