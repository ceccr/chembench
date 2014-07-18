package edu.unc.ceccr.workflows.datasets;

import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

public class StandardizeMolecules {

    private static Logger logger = Logger.getLogger(StandardizeMolecules.class.getName());

    public static void standardizeSdf(String sdfIn,
                                      String sdfOut,
                                      String workingDir) throws Exception {
        // Standardizes the molecules in this sdfile. Necessary to do this
        // before running DRAGON
        // descriptor generation. Also important to do this to any molecules
        // that could go into our database.

        logger.debug("standardizeSdf: getting sdf compounds");
        List<String> compoundNames = DatasetFileOperations
                .getSDFCompoundNames(workingDir + sdfIn);
        logger.debug("standardizeSdf: done getting sdf compounds");

        if (compoundNames.size() < 600) {
            String execstr1 = "standardize.sh " + sdfIn + " " + sdfOut;
            RunExternalProgram.runCommandAndLogOutput(execstr1, workingDir,
                    "standardize");
        } else {
            // The JChem software won't let you do more than 666 molecules in
            // this process at a time
            // so we split the SDF into chunks of size 600, do the process on
            // each file, then
            // reassemble the outputs.

            // split the SDF
            logger.debug("Splitting and standardizing " + sdfIn
                    + " in dir " + workingDir);

            File infile = new File(workingDir + sdfIn);
            FileReader fin = new FileReader(infile);
            BufferedReader br = new BufferedReader(fin);

            // read molecules from original SDF
            int compoundsInCurrentFile = 0;
            int currentFileNumber = 0;

            String sdfFilePart = sdfIn + "_" + currentFileNumber + ".sdf";
            BufferedWriter partOut = new BufferedWriter(new FileWriter(
                    workingDir + sdfFilePart));

            String line;
            while ((line = br.readLine()) != null) {
                partOut.write(line + "\n");
                if (line.startsWith("$$$$")) {
                    // done reading a compound
                    compoundsInCurrentFile++;
                    if (compoundsInCurrentFile == 600) {
                        // close current file part and apply standardization
                        // to it
                        partOut.close();
                        String standardizedFilePart = sdfFilePart
                                + ".standardize";
                        String execstr1 = "standardize.sh " + sdfFilePart
                                + " " + standardizedFilePart;

                        RunExternalProgram.runCommandAndLogOutput(execstr1,
                                workingDir, "standardize_"
                                        + currentFileNumber
                        );

                        // start a new file
                        compoundsInCurrentFile = 0;
                        currentFileNumber++;
                        sdfFilePart = sdfIn + "_" + currentFileNumber
                                + ".sdf";
                        partOut = new BufferedWriter(new FileWriter(
                                workingDir + sdfFilePart));
                    }
                }
            }

            // close and standardize the final file part
            br.close();
            partOut.close();
            String standardizedFilePart = sdfFilePart + ".standardize";
            String execstr1 = "standardize.sh " + sdfFilePart + " "
                    + standardizedFilePart;
            RunExternalProgram.runCommandAndLogOutput(execstr1, workingDir,
                    "standardize_" + currentFileNumber);

            logger.debug("Merging standardized SDFs");
            // merge the output files back together

            BufferedWriter out = new BufferedWriter(new FileWriter(workingDir
                    + sdfOut));

            for (int i = 0; i <= currentFileNumber; i++) {
                String filePartName = sdfIn + "_" + i + ".sdf.standardize";
                standardizedFilePart = FileAndDirOperations
                        .readFileIntoString(workingDir + filePartName);
                out.write(standardizedFilePart);

                // delete the standardized file-part from disk, it's no longer
                // needed
                FileAndDirOperations.deleteFile(workingDir + filePartName);
                String oldFile = sdfIn + "_" + i + ".sdf";
                FileAndDirOperations.deleteFile(workingDir + oldFile);
            }
            out.close();
        }
    }
}