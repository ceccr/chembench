package edu.unc.ceccr.workflows.modelingPrediction;


import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.global.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class LsfUtilities
{
    private static Logger logger
              = Logger.getLogger(LsfUtilities.class.getName());

    public static void
    retrieveCompletedPredictor(String filePath, String lsfPath)
                                                             throws Exception
    {
        // open the directory in /largefs/ceccr/ where the job was run

        String execstr = "mv.sh " + lsfPath + "* " + filePath;
        RunExternalProgram.runCommand(execstr, "");

        execstr = "mv.sh " + lsfPath + "yRandom/* " + filePath + "yRandom/ ";
        RunExternalProgram.runCommand(execstr, "");
    }

    public static void
    makeLsfModelingDirectory(String filePath, String lsfPath) throws Exception
    {
        // create a dir out in /largefs/ceccr/ to run the calculation of the
        // job
        File dir = new File(lsfPath);
        dir.mkdirs();
        FileAndDirOperations.deleteDirContents(lsfPath);
        logger.debug("Created fresh directories @ " + lsfPath);

        if (new File(lsfPath + "yRandom/").exists()) {
            FileAndDirOperations.deleteDirContents(lsfPath + "yRandom/");
        }

        // copy all files from current modeling dir out there
        FileAndDirOperations.copyDirContents(filePath, lsfPath, true);
        logger.debug("Copied all files from "+filePath+" to " + lsfPath);
        // copy kNN executables to the temp directory and to the yRandom
        // subdirectory also, make them executable
        FileAndDirOperations.copyDirContents(Constants.CECCR_BASE_PATH
                + "mmlsoft/bin/", lsfPath, false);
        FileAndDirOperations.makeDirContentsExecutable(lsfPath);
        FileAndDirOperations.copyDirContents(Constants.CECCR_BASE_PATH
                + "mmlsoft/bin/", lsfPath + "yRandom/", false);
        FileAndDirOperations.makeDirContentsExecutable(lsfPath + "yRandom/");
        logger.debug("Copied mmlsoft/bin to lsfPath");

    }

    public static String getLsfJobId(String logFilePath) throws Exception
    {
        Thread.sleep(200); // give the file time to close properly? I guess?
        BufferedReader in = new BufferedReader(new FileReader(logFilePath));
        String line = in.readLine(); // junk
        Scanner sc = new Scanner(line);
        String jobId = "";
        if (sc.hasNext()) {
            sc.next();
        }
        if (sc.hasNext()) {
            jobId = sc.next();
        }
        logger.debug(jobId.substring(1, jobId.length() - 1));
        in.close();
        sc.close();
        return jobId.substring(1, jobId.length() - 1);
    }

}
