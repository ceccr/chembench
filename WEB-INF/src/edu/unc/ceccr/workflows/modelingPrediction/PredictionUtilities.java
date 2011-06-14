package edu.unc.ceccr.workflows.modelingPrediction;

import java.io.*;
import java.nio.channels.FileChannel;

import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.global.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.validator.GenericValidator;

public class PredictionUtilities{

	//Execute external programs to generate a prediction for a given molecule set.
	// Used for legacy models that were created using Sasha's kNN code.
	
	public static void MoveToPredictionsDir(String userName, String jobName) throws Exception{
		//When the prediction job is finished, move all the files over to the predictions dir.
		Utility.writeToDebug("Moving to PREDICTIONS dir.", userName, jobName);
		String moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTIONS/" + jobName + "/";
		String execstr = "mv " + moveFrom + " " + moveTo;
		RunExternalProgram.runCommand(execstr, "");
	}
	
}