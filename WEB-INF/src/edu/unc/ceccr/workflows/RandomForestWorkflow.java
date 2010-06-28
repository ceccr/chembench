package edu.unc.ceccr.workflows;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class RandomForestWorkflow{

	public static void buildRandomForestModels(RandomForestParameters randomForestParameters, String actFileDataType, String scalingType, String workingDir) throws Exception{
		Utility.writeToDebug("Running Random Forest Modeling...");
		if(scalingType.equals(Constants.NOSCALING)){
			//the last two lines of the .x file do not need to be removed
			
		}
		else{
			//the last two lines of the .x file need to be removed
			
		}
	}

	public static void runRandomForestPrediction() throws Exception{
		
	}
}