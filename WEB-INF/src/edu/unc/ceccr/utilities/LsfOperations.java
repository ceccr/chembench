package edu.unc.ceccr.utilities;

import edu.unc.ceccr.global.Constants;

import org.apache.log4j.Logger;

//Holds functions needed for managing LSF jobs.
//Moving files back and forth to largefs, checking status of LSF-submitted jobs, etc.

public class LsfOperations{

    private static Logger logger = Logger.getLogger(LsfOperations.class.getName());

    public static boolean patronsQueueHasRoom(){
        try{
            RunExternalProgram.runCommand("numPatronsJobs.sh", Constants.CECCR_USER_BASE_PATH);
            String numJobsStr = FileAndDirOperations.readFileIntoString(Constants.CECCR_USER_BASE_PATH + "numPatronsJobs.txt");
            numJobsStr = numJobsStr.split("\\\n")[0];
            if(Integer.parseInt(numJobsStr) < Constants.PATRONSQUEUESLOTS){
                return true;
            }
        }
        catch(Exception ex){
            logger.error(ex);
            return false;
        }
        return false;
    }
}