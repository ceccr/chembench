package edu.unc.ceccr.workflows;


import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.io.File;

public class KnnPredictionWorkflow{
	
	//Execute external programs to generate a prediction for a given molecule set.
	
	public static void RunKnnPrediction(String userName, String jobName, String sdFile, float cutoffValue ) throws Exception{

		String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		
		String execstr1 = "PredActivCont3rwknnLIN knn-output.list " + sdFile + ".renorm.x pred_output " + cutoffValue;
		  Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingdir);
	      Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "PredActivCont3rwknnLIN", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	    try{
	      int j = 1 / 0;
	    }
	    catch(Exception ex){
	    	Utility.writeToDebug(ex);
	    	throw(ex);
	    }
	    String execstr2 = "ConsPredContrwknnLIN pred_output.comp.list pred_output.list cons_pred";
		  Utility.writeToDebug("Running external program: " + execstr2 + " in dir " + workingdir);
	      p = Runtime.getRuntime().exec(execstr2, null, new File(workingdir));
	      Utility.writeProgramLogfile(workingdir, "ConsPredContrwknnLIN", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}
	
	public static void MoveToPredictionsDir(String userName, String jobName) throws Exception{
		//When the prediction job is finished, move all the files over to the predictions dir.
		Utility.writeToDebug("Moving to PREDICTIONS dir.", userName, jobName);
		String moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";
		String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTIONS/" + jobName + "/";
		String execstr = "mv " + moveFrom + " " + moveTo;

		  Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr);
	      //Utility.writeProgramLogfile(moveTo, "mv", p.getInputStream(), p.getErrorStream());
	      p.waitFor();

	}
}