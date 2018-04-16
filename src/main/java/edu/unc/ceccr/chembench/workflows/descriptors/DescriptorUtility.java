package edu.unc.ceccr.chembench.workflows.descriptors;


import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DescriptorUtility {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorUtility.class);
    private static int compoundsPerChunk = 1000;
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

    public static void convertDescriptorsToXAndScaleInChunks(String workingDir, String sdfile, String predictorXFile,
                                                             String outputXFile, AllDescriptors descriptorsSetListObj,
                                                             String scalingType) throws Exception {

        // split each descriptor file into chunks
        // sdfile are without file endings
        // descriptorsFile have file endings
        String descriptorsFile = descriptorsSetListObj.splitDescriptors(workingDir, sdfile);

        // run scaling and conversion process on each chunk, producing several
        // X files
        int filePartNumber = 0;
        File descriptorsFilePart = new File(workingDir + descriptorsFile + "_" + filePartNumber);

        List<String> allChemicalNames;
        if (descriptorsSetListObj.getDescriptors().equals(Constants.UPLOADED)) {
            allChemicalNames = DatasetFileOperations.getXCompoundNames(workingDir + sdfile);
        } else if (descriptorsSetListObj.getDescriptors().equals(Constants.CDK)) {
            allChemicalNames = DatasetFileOperations.getXCompoundNames(workingDir + descriptorsFile);
        } else {
            allChemicalNames = DatasetFileOperations.getSdfCompoundNames(workingDir + sdfile);
        }

        while (descriptorsFilePart.exists()) {
            List<String> descriptorNames = new ArrayList<>();
            List<Descriptors> descriptorValueMatrix = new ArrayList<>();
            List<String> chemicalNames = new ArrayList<>();

            for (int i = filePartNumber * compoundsPerChunk; i < (filePartNumber + 1) * compoundsPerChunk; i++) {
                if (i < allChemicalNames.size()) {
                    chemicalNames.add(allChemicalNames.get(i));
                }
            }

            descriptorsSetListObj.readDescriptorSetsChunks(workingDir + descriptorsFile + "_" + filePartNumber,
                    descriptorNames,
                    descriptorValueMatrix);

            String descriptorString = Utility.stringListToString(descriptorNames);
            WriteDescriptors.writePredictionXFile(chemicalNames, descriptorValueMatrix, descriptorString,
                    workingDir + outputXFile + "_" + filePartNumber, workingDir + predictorXFile, scalingType);
            // FileAndDirOperations.deleteFile(workingDir + descriptorsFile +
            // "_" + filePartNumber);

            filePartNumber++;
            descriptorsFilePart = new File(workingDir + descriptorsFile + "_" + filePartNumber);
        }
        // reassemble X file parts into one big X file
        mergeXFileParts(workingDir, outputXFile, scalingType, allChemicalNames.size());
    }

    public static void convertDescriptorsToXAndScale(String workingDir, String sdfile, String predictorXFile,
                                                     String outputXFile, String descriptorGenerationType,
                                                     String scalingType, int numCompounds) throws Exception {
        AllDescriptors descriptorsSetListObj = new AllDescriptors(descriptorGenerationType);

        if (numCompounds > compoundsPerChunk) {
            convertDescriptorsToXAndScaleInChunks(workingDir, sdfile, predictorXFile, outputXFile,
                    descriptorsSetListObj, scalingType);
            return;
        }

        List<String> descriptorNames = new ArrayList<>();
        List<Descriptors> descriptorValueMatrix = new ArrayList<>();
        List<String> chemicalNames;
        if (descriptorGenerationType.equals(Constants.UPLOADED)) {
            chemicalNames = DatasetFileOperations.getXCompoundNames(workingDir + sdfile);
        } else {
            logger.info("Getting compound names from SDF file: " +
                    workingDir + sdfile);
            chemicalNames = DatasetFileOperations.getSdfCompoundNames(workingDir + sdfile);
        }

        descriptorsSetListObj.readDescriptorSets(workingDir + sdfile, descriptorNames, descriptorValueMatrix);

        String descriptorString = Utility.stringListToString(descriptorNames);
        
        WriteDescriptors
                .writePredictionXFile(chemicalNames, descriptorValueMatrix, descriptorString, workingDir + outputXFile,
                        workingDir + predictorXFile, scalingType);
    }

    public static String[] readDescriptorNamesFromX(String xFile, String workingDir) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(workingDir + xFile));
        br.readLine(); // numCompounds, numDescriptors;
        String[] descs = br.readLine().split("\\s+");
        br.close();
        return descs;
    }

    // helper functions
    private static void mergeXFileParts(String workingDir, String outputXFile, String scalingType, int numCompounds)
            throws Exception {
        int filePartNumber = 0;
        File xFilePart = new File(workingDir + outputXFile + "_" + filePartNumber);

        BufferedWriter xFileOut = new BufferedWriter(new FileWriter(workingDir + outputXFile));
        List<String> linesInFilePart = null;
        int compoundIndex = 1;
        while (xFilePart.exists()) {
            // read all lines into array
            BufferedReader br = new BufferedReader(new FileReader(xFilePart));
            linesInFilePart = new ArrayList<>();
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
            xFilePart = new File(workingDir + outputXFile + "_" + filePartNumber);
        }
        logger.debug("----------Lines in file part:" + linesInFilePart.size() + " nuber of componds = " + numCompounds);
        // print footer
        if (!scalingType.equals(Constants.NOSCALING) && linesInFilePart != null) {
            xFileOut.write("\n");
            xFileOut.write(linesInFilePart.get(linesInFilePart.size() - 2));
            xFileOut.write(linesInFilePart.get(linesInFilePart.size() - 1));
        }
        xFileOut.close();
    }
}
