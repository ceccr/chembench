package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class SvmWorkflow{

	public static void convertXtoSvm(String xFileName, String aFileName, String workingDir) throws Exception{
		//generates an SVM-compatible input descriptor file
		
		Utility.writeToDebug("Generating an SVM-compatible file: " + xFileName + " + " + aFileName + " => " + xFileName.replace(".x", ".svm"));
		ArrayList<String> activityValues = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(workingDir + aFileName));
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			//for each model
			String[] data = inputString.split("\\s+"); // [0] is the compound id, [1] is the activity value
			activityValues.add(data[1]);
		}
		in.close();
		
		in = new BufferedReader(new FileReader(workingDir + xFileName));
		BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + xFileName.replace(".x", ".svm")));
		StringBuilder sb = new StringBuilder();
		
		in.readLine(); // header
		in.readLine(); // header
		
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
	
	public static void preProcessXFile(String scalingType, String xFile, String newXFile, String workingDir) throws Exception
	{
		
		
	}
	
	public static void buildSvmModels(SvmParameters svmParameters, String actFileDataType, String workingDir) throws Exception{
		//
		
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
		
		Utility.writeToDebug("Running SVM Modeling...");
		convertXtoSvm(Constants.MODELING_SET_X_FILE, Constants.MODELING_SET_A_FILE, workingDir);
		convertXtoSvm(Constants.EXTERNAL_SET_X_FILE, Constants.EXTERNAL_SET_A_FILE, workingDir);
		
		BufferedReader in = new BufferedReader(new FileReader(workingDir + "RAND_sets.list"));
		String inputString;
		while ((inputString = in.readLine()) != null && ! inputString.equals(""))
		{
			String[] data = inputString.split("\\s+");
			convertXtoSvm(data[0], data[1], workingDir);
			convertXtoSvm(data[3], data[4], workingDir);
		}
		in.close();
	}
	
	public static ArrayList<SvmModel> readSvmModels(){
		return null;
	}
	
	public static void runSvmPrediction() throws Exception{
		
	}
	
	public static ArrayList<PredictionValue> readPredictionOutput(String workingDir, Long predictorId) throws Exception{
		return null;
	}
}