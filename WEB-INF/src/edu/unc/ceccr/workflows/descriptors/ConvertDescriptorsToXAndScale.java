package edu.unc.ceccr.workflows.descriptors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.log4j.Logger;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;

public class ConvertDescriptorsToXAndScale
{

    private static Logger logger            = Logger.getLogger(ConvertDescriptorsToXAndScale.class
                                                    .getName());
    private static int    compoundsPerChunk = 1000;

    /*
     * For large prediction sets (e.g. > 10000 compounds), the descriptors
     * will not
     * be able to fit in Tomcat's memory, and this will cause errors.
     * So instead of reading in the descriptors to a Java object
     * (ReadDescriptorsFileWorkflow) and then
     * writing the Java object out as a .x (WriteDescriptorsFileWorkflow), we
     * need to do a straight file
     * conversion in this case.
     */

    public static void
            convertDescriptorsToXAndScaleInChunks(String workingDir,
                                                  String sdfile,
                                                  String predictorXFile,
                                                  String outputXFile,
                                                  String descriptorGenerationType,
                                                  String scalingType) throws Exception
    {

        // split each descriptor file into chunks
        String descriptorsFile = sdfile;
        if (descriptorGenerationType.equals(Constants.MOLCONNZ)) {
            descriptorsFile += ".molconnz";
            splitMolconnZFile(workingDir, descriptorsFile);
        }
        else if (descriptorGenerationType.equals(Constants.CDK)) {
            descriptorsFile += ".cdk";
            ReadDescriptors.convertCDKToX(workingDir + descriptorsFile,
                    workingDir);
            descriptorsFile += ".x";
            splitXFile(workingDir, descriptorsFile);
        }
        else if (descriptorGenerationType.equals(Constants.DRAGONH)) {
            descriptorsFile += ".dragonH";
            splitDragonFile(workingDir, descriptorsFile);
        }
        else if (descriptorGenerationType.equals(Constants.DRAGONNOH)) {
            descriptorsFile += ".dragonNoH";
            splitDragonFile(workingDir, descriptorsFile);
        }
        else if (descriptorGenerationType.equals(Constants.MOE2D)) {
            descriptorsFile += ".moe2D";
            splitMoe2dFile(workingDir, descriptorsFile);
        }
        else if (descriptorGenerationType.equals(Constants.MACCS)) {
            descriptorsFile += ".maccs";
            splitMaccsFile(workingDir, descriptorsFile);
        }
		else if (descriptorGenerationType.equals(Constants.ISIDA)) {
            descriptorsFile += ".ISIDA";
            splitISIDAFile(workingDir, descriptorsFile);
        }
        else if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            splitXFile(workingDir, descriptorsFile);
        }

        // run scaling and conversion process on each chunk, producing several
        // X files
        int filePartNumber = 0;
        File descriptorsFilePart = new File(workingDir + descriptorsFile
                + "_" + filePartNumber);

        ArrayList<String> allChemicalNames = null;
        if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            allChemicalNames = DatasetFileOperations
                    .getXCompoundNames(workingDir + sdfile);
        }
        else if (descriptorGenerationType.equals(Constants.CDK)) {
            allChemicalNames = DatasetFileOperations
                    .getXCompoundNames(workingDir + descriptorsFile);
        }
        else {
            allChemicalNames = DatasetFileOperations
                    .getSDFCompoundNames(workingDir + sdfile);
        }

        while (descriptorsFilePart.exists()) {

            ArrayList<String> descriptorNames = new ArrayList<String>();
            ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();

            ArrayList<String> chemicalNames = new ArrayList<String>();

            for (int i = filePartNumber * compoundsPerChunk; i < (filePartNumber + 1)
                    * compoundsPerChunk; i++) {
                if (i < allChemicalNames.size()) {
                    chemicalNames.add(allChemicalNames.get(i));
                }
            }

            if (descriptorGenerationType.equals(Constants.MOLCONNZ)) {
                // ReadDescriptorsFileWorkflow.convertMzToX(workingDir +
                // descriptorsFile + "_" + filePartNumber, workingDir);
                // ReadDescriptorsFileWorkflow.readXDescriptors(workingDir +
                // descriptorsFile + "_" + ".mz.x", descriptorNames,
                // descriptorValueMatrix);
                ReadDescriptors.readMolconnZDescriptors(workingDir
                        + descriptorsFile, descriptorNames,
                        descriptorValueMatrix);
            }
            else if (descriptorGenerationType.equals(Constants.CDK)) {
                // descriptorsFile += ".cdk";
                // ReadDescriptors.convertCDKToX(workingDir +
                // descriptorsFile+".x_" + filePartNumber, workingDir);
                ReadDescriptors.readXDescriptors(workingDir + descriptorsFile
                        + "_" + filePartNumber, descriptorNames,
                        descriptorValueMatrix);
            }
            else if (descriptorGenerationType.equals(Constants.DRAGONH)) {
                ReadDescriptors.readDragonDescriptors(workingDir
                        + descriptorsFile + "_" + filePartNumber,
                        descriptorNames, descriptorValueMatrix);
            }
            else if (descriptorGenerationType.equals(Constants.DRAGONNOH)) {
                ReadDescriptors.readDragonDescriptors(workingDir
                        + descriptorsFile + "_" + filePartNumber,
                        descriptorNames, descriptorValueMatrix);
            }
            else if (descriptorGenerationType.equals(Constants.MOE2D)) {
                ReadDescriptors.readMoe2DDescriptors(workingDir
                        + descriptorsFile + "_" + filePartNumber,
                        descriptorNames, descriptorValueMatrix);
            }
            else if (descriptorGenerationType.equals(Constants.MACCS)) {
                ReadDescriptors.readMaccsDescriptors(workingDir
                        + descriptorsFile + "_" + filePartNumber,
                        descriptorNames, descriptorValueMatrix);
            }
			else if (descriptorGenerationType.equals(Constants.ISIDA)) {
                ReadDescriptors.readISIDADescriptors(workingDir
                        + descriptorsFile + "_" + filePartNumber,
                        descriptorNames, descriptorValueMatrix);
            }
            else if (descriptorGenerationType.equals(Constants.UPLOADED)) {
                ReadDescriptors.readXDescriptors(workingDir + descriptorsFile
                        + "_" + filePartNumber, descriptorNames,
                        descriptorValueMatrix);
            }

            String descriptorString = Utility
                    .StringArrayListToString(descriptorNames);
            WriteDescriptors.writePredictionXFile(chemicalNames,
                    descriptorValueMatrix, descriptorString, workingDir
                            + outputXFile + "_" + filePartNumber, workingDir
                            + predictorXFile, scalingType);
            // FileAndDirOperations.deleteFile(workingDir + descriptorsFile +
            // "_" + filePartNumber);

            filePartNumber++;
            descriptorsFilePart = new File(workingDir + descriptorsFile + "_"
                    + filePartNumber);
        }
        // reassemble X file parts into one big X file
        mergeXFileParts(workingDir, outputXFile, scalingType,
                allChemicalNames.size());
    }

    public static void
            convertDescriptorsToXAndScale(String workingDir,
                                          String sdfile,
                                          String predictorXFile,
                                          String outputXFile,
                                          String descriptorGenerationType,
                                          String scalingType,
                                          int numCompounds) throws Exception
    {

        if (numCompounds > compoundsPerChunk) {
            convertDescriptorsToXAndScaleInChunks(workingDir, sdfile,
                    predictorXFile, outputXFile, descriptorGenerationType,
                    scalingType);
            return;
        }

        ArrayList<String> descriptorNames = new ArrayList<String>();
        ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
        ArrayList<String> chemicalNames = null;
        if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            chemicalNames = DatasetFileOperations
                    .getXCompoundNames(workingDir + sdfile);
        }
        else {
            logger.info("Getting compound names from SDF file: " +
                        workingDir + sdfile);
            chemicalNames = DatasetFileOperations
                    .getSDFCompoundNames(workingDir + sdfile);
        }
        String descriptorsFile = sdfile;
        if (descriptorGenerationType.equals(Constants.MOLCONNZ)) {
            // descriptorsFile += ".mz";
            // ReadDescriptorsFileWorkflow.convertMzToX(workingDir +
            // descriptorsFile, workingDir);
            // ReadDescriptorsFileWorkflow.readXDescriptors(workingDir +
            // descriptorsFile + ".x", descriptorNames,
            // descriptorValueMatrix);
            descriptorsFile += ".molconnz";
            ReadDescriptors
                    .readMolconnZDescriptors(workingDir + descriptorsFile,
                            descriptorNames, descriptorValueMatrix);
        }
        else if (descriptorGenerationType.equals(Constants.CDK)) {
            descriptorsFile += ".cdk";
            ReadDescriptors.convertCDKToX(workingDir + descriptorsFile,
                    workingDir);
            ReadDescriptors.readXDescriptors(workingDir + descriptorsFile
                    + ".x", descriptorNames, descriptorValueMatrix);
        }
        else if (descriptorGenerationType.equals(Constants.DRAGONH)) {
            descriptorsFile += ".dragonH";
            ReadDescriptors
                    .readDragonDescriptors(workingDir + descriptorsFile,
                            descriptorNames, descriptorValueMatrix);
        }
        else if (descriptorGenerationType.equals(Constants.DRAGONNOH)) {
            descriptorsFile += ".dragonNoH";
            ReadDescriptors
                    .readDragonDescriptors(workingDir + descriptorsFile,
                            descriptorNames, descriptorValueMatrix);
        }
        else if (descriptorGenerationType.equals(Constants.MOE2D)) {
            descriptorsFile += ".moe2D";
            ReadDescriptors.readMoe2DDescriptors(
                    workingDir + descriptorsFile, descriptorNames,
                    descriptorValueMatrix);
        }
        else if (descriptorGenerationType.equals(Constants.MACCS)) {
            descriptorsFile += ".maccs";
            ReadDescriptors.readMaccsDescriptors(
                    workingDir + descriptorsFile, descriptorNames,
                    descriptorValueMatrix);
        }
		else if (descriptorGenerationType.equals(Constants.ISIDA)) {
            descriptorsFile += ".ISIDA";
            ReadDescriptors.readISIDADescriptors(
                    workingDir + descriptorsFile, descriptorNames,
                    descriptorValueMatrix);
        }
        else if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            ReadDescriptors.readXDescriptors(workingDir + descriptorsFile,
                    descriptorNames, descriptorValueMatrix);
        }

        String descriptorString = Utility
                .StringArrayListToString(descriptorNames);
        WriteDescriptors.writePredictionXFile(chemicalNames,
                descriptorValueMatrix, descriptorString, workingDir
                        + outputXFile, workingDir + predictorXFile,
                scalingType);
    }

    // helper functions

    private static void
            splitMolconnZFile(String workingDir, String descriptorsFile) throws Exception
    {
        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read MolconnZ descriptors.\n");
        }
        FileReader fin = new FileReader(file);

        String temp;
        Scanner src = new Scanner(fin);
        ArrayList<String> descriptorNames = new ArrayList<String>(); // names
                                                                     // for
                                                                     // each
                                                                     // molecule;
                                                                     // used
                                                                     // in
                                                                     // counting
        ArrayList<String> descriptorValues = new ArrayList<String>(); // values
                                                                      // for
                                                                      // each
                                                                      // molecule;
                                                                      // used
                                                                      // in
                                                                      // counting

        String header = ""; // stores everything up to where descriptors
                            // begin.
        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart = new BufferedWriter(new FileWriter(
                workingDir + descriptorsFile + "_" + currentFile));

        boolean readingDescriptorNames = true;
        while (src.hasNext()) {
            temp = src.next();
            if (temp.matches("[\\p{Graph}]+")) {
                if (temp.matches("[0-9&&[^a-zA-Z]]+")
                        && readingDescriptorNames) {
                    // The first occurrence of a number indicates we're no
                    // longer reading descriptor names.
                    readingDescriptorNames = false;
                }

                outFilePart.write(temp + " ");
                if (readingDescriptorNames) {
                    descriptorNames.add(temp);
                    header += temp + " ";
                }
                else {
                    descriptorValues.add(temp);
                    if (descriptorValues.size() == descriptorNames.size()) {
                        // At end of this molecule.
                        descriptorValues.clear();
                        outFilePart.write("\n");
                        moleculesInCurrentFile++;
                        if (moleculesInCurrentFile == compoundsPerChunk) {
                            outFilePart.close();
                            moleculesInCurrentFile = 0;
                            currentFile++;
                            outFilePart = new BufferedWriter(new FileWriter(
                                    workingDir + descriptorsFile + "_"
                                            + currentFile));
                            outFilePart.write(header + "\n");
                        }
                    }
                }
            }
        }

        src.close();
        // close final file
        outFilePart.write("\n");
        outFilePart.close();
    }

    private static void
            splitDragonFile(String workingDir, String descriptorsFile) throws Exception
    {

        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read Dragon descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart = new BufferedWriter(new FileWriter(
                workingDir + descriptorsFile + "_" + currentFile));

        String header = br.readLine() + "\n"; // stores everything up to where
                                              // descriptors begin.
        header += br.readLine() + "\n";
        header += br.readLine() + "\n";

        outFilePart.write(header);

        String line;
        // Now we're at the descriptor values for each compound
        while ((line = br.readLine()) != null) {
            outFilePart.write(line + "\n");

            moleculesInCurrentFile++;
            if (moleculesInCurrentFile == compoundsPerChunk) {
                outFilePart.close();
                moleculesInCurrentFile = 0;
                currentFile++;
                outFilePart = new BufferedWriter(new FileWriter(workingDir
                        + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
            }
        }

        // close final file
        br.close();
        outFilePart.close();
    }

    private static void
            splitMaccsFile(String workingDir, String descriptorsFile) throws Exception
    {
        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read Maccs descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        String header = ""; // stores everything up to where descriptors
                            // begin.
        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart = new BufferedWriter(new FileWriter(
                workingDir + descriptorsFile + "_" + currentFile));

        header = br.readLine() + "\n";
        outFilePart.write(header);

        String line;
        while ((line = br.readLine()) != null) {
            outFilePart.write(line + "\n");

            moleculesInCurrentFile++;
            if (moleculesInCurrentFile == compoundsPerChunk) {
                outFilePart.close();
                moleculesInCurrentFile = 0;
                currentFile++;
                outFilePart = new BufferedWriter(new FileWriter(workingDir
                        + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
            }
        }
        br.close();
        outFilePart.close();
    }

    private static void
            splitMoe2dFile(String workingDir, String descriptorsFile) throws Exception
    {
        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read MOE2D descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        String header = ""; // stores everything up to where descriptors
                            // begin.
        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart = new BufferedWriter(new FileWriter(
                workingDir + descriptorsFile + "_" + currentFile));

        header = br.readLine() + "\n";
        outFilePart.write(header);

        String line;
        while ((line = br.readLine()) != null) {
            outFilePart.write(line + "\n");

            moleculesInCurrentFile++;
            if (moleculesInCurrentFile == compoundsPerChunk) {
                outFilePart.close();
                moleculesInCurrentFile = 0;
                currentFile++;
                outFilePart = new BufferedWriter(new FileWriter(workingDir
                        + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
            }
        }
        br.close();
        outFilePart.write("\n");
        outFilePart.close();
    }
	
	private static void
            splitISIDAFile(String workingDir, String descriptorsFile) throws Exception
    {
        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read ISIDA descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        String header = ""; // stores everything up to where descriptors
                            // begin.
        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart = new BufferedWriter(new FileWriter(
                workingDir + descriptorsFile + "_" + currentFile));

        header = br.readLine() + "\n";
        outFilePart.write(header);

        String line;
        while ((line = br.readLine()) != null) {
            outFilePart.write(line + "\n");

            moleculesInCurrentFile++;
            if (moleculesInCurrentFile == compoundsPerChunk) {
                outFilePart.close();
                moleculesInCurrentFile = 0;
                currentFile++;
                outFilePart = new BufferedWriter(new FileWriter(workingDir
                        + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
            }
        }
        br.close();
        outFilePart.write("\n");
        outFilePart.close();
    }

    private static void
            splitXFile(String workingDir, String descriptorsFile) throws Exception
    {
        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read UPLOADED descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart = new BufferedWriter(new FileWriter(
                workingDir + descriptorsFile + "_" + currentFile));

        // don't bother changing the numbers to reflect #compounds in file
        // part, it doesn't matter
        String header = br.readLine() + "\n";
        String descriptorNames = br.readLine() + "\n";
        outFilePart.write(header);
        outFilePart.write(descriptorNames);

        String line;
        while ((line = br.readLine()) != null) {
            outFilePart.write(line + "\n");

            moleculesInCurrentFile++;
            if (moleculesInCurrentFile == compoundsPerChunk) {
                outFilePart.close();
                moleculesInCurrentFile = 0;
                currentFile++;
                outFilePart = new BufferedWriter(new FileWriter(workingDir
                        + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
                outFilePart.write(descriptorNames);
            }
        }
        br.close();
        outFilePart.write("\n");
        outFilePart.close();
    }

    private static void mergeXFileParts(String workingDir,
                                        String outputXFile,
                                        String scalingType,
                                        int numCompounds) throws Exception
    {
        int filePartNumber = 0;
        File xFilePart = new File(workingDir + outputXFile + "_"
                + filePartNumber);

        BufferedWriter xFileOut = new BufferedWriter(new FileWriter(
                workingDir + outputXFile));
        ArrayList<String> linesInFilePart = null;
        int compoundIndex = 1;
        while (xFilePart.exists()) {
            // read all lines into array
            BufferedReader br = new BufferedReader(new FileReader(xFilePart));
            linesInFilePart = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    line += "\n";
                    linesInFilePart.add(line);
                }
            }

            // if this is the first filepart, print header
            if (filePartNumber == 0) {
                // header needs the correct number of compounds for entire
                // dataset
                String firstLine = linesInFilePart.get(0);
                String[] firstLineSplit = firstLine.split("\\s+");
                xFileOut.write(numCompounds + " " + firstLineSplit[1] + "\n");
                xFileOut.write(linesInFilePart.get(1));
            }

            // for each filepart, print all but the header and footer
            int numFooterLines = 2;
            if (scalingType.equals(Constants.NOSCALING)) {
                numFooterLines = 0;
            }
            for (int i = 2; i < linesInFilePart.size() - numFooterLines; i++) {
                // need to fix compound index on each line (has to go 1, 2,
                // 3...)
                String currentLine = linesInFilePart.get(i);
                currentLine = currentLine.substring(currentLine.indexOf(" "));
                currentLine = compoundIndex + currentLine;
                xFileOut.write(currentLine);
                compoundIndex++;
            }
            br.close();
            // FileAndDirOperations.deleteFile(workingDir + outputXFile + "_"
            // + filePartNumber);
            filePartNumber++;
            xFilePart = new File(workingDir + outputXFile + "_"
                    + filePartNumber);
        }
        logger.debug("----------Lines in file part:" + linesInFilePart.size()
                + " nuber of componds = " + numCompounds);
        // print footer
        if (!scalingType.equals(Constants.NOSCALING)
                && linesInFilePart != null) {
            xFileOut.write("\n");
            xFileOut.write(linesInFilePart.get(linesInFilePart.size() - 2));
            xFileOut.write(linesInFilePart.get(linesInFilePart.size() - 1));
        }
        xFileOut.close();
    }
}
