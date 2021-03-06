package edu.unc.ceccr.chembench.workflows.modelingPrediction;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class ModelingUtilities {

    private static final Logger logger = LoggerFactory.getLogger(ModelingUtilities.class);

    public static void SetUpYRandomization(String userName, String jobName) throws Exception {
        String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/";

        //create yRandom dirs
        new File(workingdir + "yRandom/").mkdir();
        new File(workingdir + "yRandom/Logs/").mkdir();

        //make sure dirs are empty
        FileAndDirOperations.deleteDirContents(workingdir + "yRandom/");
        FileAndDirOperations.deleteDirContents(workingdir + "yRandom/Logs/");

        //copy *.default and RAND_sets* to yRandom
        File file;
        String fromDir = workingdir;
        String toDir = workingdir + "yRandom/";
        logger.debug("Copying *.default and RAND_sets* from " + fromDir + " to " + toDir);
        file = new File(fromDir);
        String files[] = file.list();
        if (files == null) {
            logger.warn("Error reading directory: " + fromDir);
        }
        int x = 0;
        while (files != null && x < files.length) {
            if (files[x].matches(".*default.*") || files[x].matches(".*RAND_sets.*") || files[x]
                    .matches(".*rand_sets" + ".*")) {
                FileInputStream fis = new FileInputStream(fromDir + files[x]);
                FileOutputStream fos = new FileOutputStream(toDir + files[x]);
                FileChannel ic = fis.getChannel();
                FileChannel oc = fos.getChannel();
                ic.transferTo(0, ic.size(), oc);
                fis.close();
                fos.close();
            }
            x++;
        }
    }

    public static void YRandomization(String userName, String jobName) throws Exception {
        //Do y-randomization shuffling

        String yRandomDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName + "/yRandom/";
        logger.debug("User: " + userName + " Job: " + jobName + " YRandomization");
        File dir = new File(yRandomDir);
        String files[] = dir.list();
        if (files == null) {
            logger.warn("Error reading directory: " + yRandomDir);
        }
        int x = 0;
        logger.debug("Randomizing each activity file (*rand_sets*.a) in dir " + yRandomDir);
        while (files != null && x < files.length) {
            if (files[x].matches(".*rand_sets.*a")) {
                //shuffle the values in each .a file (ACT file)
                DatasetFileOperations.randomizeActivityFile(yRandomDir + files[x], yRandomDir + files[x]);
            }
            x++;
        }
    }

    public static void MoveToPredictorsDir(String userName, String jobName, String parentPredictorName)
            throws Exception {
        //When the job is finished, move all the files over to the PREDICTORS dir.
        String moveFrom = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobName;
        String moveTo = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/";
        if (parentPredictorName.equals("")) {
            (new File(moveTo)).mkdirs();
            moveTo += jobName;
        } else {
            moveTo += parentPredictorName + "/";
            (new File(moveTo)).mkdirs();
            moveTo += jobName;
        }
        String execstr = "mv " + moveFrom + " " + moveTo;
        RunExternalProgram.runCommand(execstr, "");
    }
}
