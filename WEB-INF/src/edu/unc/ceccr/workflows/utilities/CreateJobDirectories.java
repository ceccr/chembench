package edu.unc.ceccr.workflows.utilities;

import java.io.File;

import edu.unc.ceccr.global.Constants;

public class CreateJobDirectories {
    public static void createDirs(String userName, String jobName) throws Exception {
        String basedir = Constants.CECCR_USER_BASE_PATH;
        String jobdir = userName + "/" + jobName + "/";
        String dirs[] = {userName, userName + "/DATASETS/", userName + "/PREDICTORS/", userName + "/PREDICTIONS/",
                jobdir};

        for (int i = 0; i < dirs.length; i++) {
            File fp = new File(basedir + dirs[i]);
            fp.mkdir();
        }
    }
}