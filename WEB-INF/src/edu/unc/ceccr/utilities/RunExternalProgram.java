package edu.unc.ceccr.utilities;

import edu.unc.ceccr.global.Constants;
import org.apache.log4j.Logger;

import java.io.*;

// Java suuure is dumb sometimes. Needs me to write it a whole class just to
// run a program and capture its output without bleeding file handles
// all over the place.

public class RunExternalProgram {

    // these programs should not have anything appear in the log file
    // when they run. (For programs that execute many times in quick
    // succession.)

    private static Logger logger = Logger.getLogger(RunExternalProgram.class
            .getName());
    private static String[] runQuietly = {"bjobs.sh",
            "datasplit_train_test", "checkKnnSaProgress",
            "checkKnnGaProgress", "svm-train", "svm-predict", "molconvert"};

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                logger.warn("Couldn't close file handle", e);
            }
        }
    }

    public static void runCommand(String cmd, String workingDir) {
        // runs an external program (no logging)

        try {
            Process p = null;

            if (workingDir.isEmpty()) {
                workingDir = Constants.CECCR_USER_BASE_PATH;
            }

            boolean outputRunningMessage = true;
            for (int i = 0; i < runQuietly.length; i++) {
                if (cmd.startsWith(runQuietly[i])) {
                    outputRunningMessage = false;
                }
            }

            if (workingDir.isEmpty()) {
                if (outputRunningMessage) {
                    logger.info(String.format(
                            "Running external program: CMD=%s", cmd));
                }
                p = Runtime.getRuntime().exec(cmd);
            } else {
                if (outputRunningMessage) {
                    logger.info(String.format(
                            "Running external program: CMD=%s, WORKINGDIR=%s",
                            cmd, workingDir));
                }
                p = Runtime.getRuntime()
                        .exec(cmd, null, new File(workingDir));
            }

            // capture program output in log file
            File file = new File(workingDir + "/Logs/");
            if (!file.exists()) {
                file.mkdirs();
            }

            // wait for the program to finish running
            p.waitFor();

            // close any file handles we might have
            RunExternalProgram.close(p.getOutputStream());
            RunExternalProgram.close(p.getInputStream());
            RunExternalProgram.close(p.getErrorStream());
            p.destroy();

        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static void runCommandAndLogOutput(String cmd,
                                              String workingDir,
                                              String logFileName) {
        // runs an external program and writes user info to logfile

        try {
            Process p = null;

            if (workingDir.isEmpty()) {
                workingDir = Constants.CECCR_USER_BASE_PATH;
            }

            boolean outputRunningMessage = true;
            for (int i = 0; i < runQuietly.length; i++) {
                if (cmd.startsWith(runQuietly[i])
                        || logFileName.startsWith(runQuietly[i])) {
                    outputRunningMessage = false;
                }
            }

            // capture program output in log file
            File file = new File(workingDir + "Logs/");
            if (!file.exists()) {
                file.mkdirs();
            }
            String logsPath = workingDir + "Logs/";

            cmd = cmd + " > " + logsPath + logFileName + ".log" + " 2> "
                    + logsPath + logFileName + ".err";

            File scriptFile = new File(workingDir + "temp-script.sh");
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(scriptFile));
            out.write(cmd + "\n");
            out.close();
            scriptFile.setExecutable(true);
            FileAndDirOperations.makeDirContentsExecutable(workingDir);

            if (outputRunningMessage) {
                logger.info(String.format(
                        "Running external program: CMD=%s, WORKINGDIR=%s",
                        cmd, workingDir));
            }

            p = Runtime.getRuntime().exec(workingDir + "temp-script.sh",
                    null, new File(workingDir));
            p.waitFor();

        } catch (Exception e) {
            logger.error("Error executing external program", e);
        }
    }

}
