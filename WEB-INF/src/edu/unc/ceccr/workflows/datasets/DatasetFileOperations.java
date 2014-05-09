package edu.unc.ceccr.workflows.datasets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.validator.GenericValidator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.ErrorMessages;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.utilities.BigFile;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;

import org.apache.log4j.Logger;

/*
 * Functions relating to the processing of incoming dataset files go in here.
 * We might move this to the Workflows package later, but it's OK here for
 * now.
 */

public class DatasetFileOperations {
    private static Logger logger
            = Logger.getLogger(DatasetFileOperations.class.getName());

    public static HashMap<String, String>
    getActFileIdsAndValues(String filePath) {
        HashMap<String, String> idsAndValues = new HashMap<String, String>();

        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte[] bytes = new byte[length];
            fis.read(bytes);
            String byteStr = (new String(bytes)).trim();

            String[] array = byteStr.split("\\s+");

            for (int i = 0; i < array.length; i += 2) {
                idsAndValues.put(array[i], array[i + 1]);
            }
            fis.close();
        } catch (Exception ex) {
            logger.error(ex);
        }

        return idsAndValues;
    }

    public static ArrayList<String>
    getActFileValues(DataSet dataset) throws Exception {
        ArrayList<String> actFileValues = new ArrayList<String>();

        // find activity file
        String datasetUserName = dataset.getUserName();
        String dir = Constants.CECCR_USER_BASE_PATH + datasetUserName
                + "/DATASETS/" + dataset.getName() + "/";
        String fileName = dir + dataset.getActFile();

        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        int length = fis.available();
        byte[] bytes = new byte[length];
        fis.read(bytes);
        String byteStr = new String(bytes);

        String[] array = byteStr.split("\\s+");

        for (int i = 0; i < array.length; i += 2) {
            actFileValues.add(array[i + 1]);
        }
        fis.close();
        return actFileValues;
    }

    public static void
    generateEmptyActFile(String path, String name, String sdfPath)
            throws IOException {
        File act = new File(path + name + ".act");
        act.createNewFile();
        FileOutputStream to = new FileOutputStream(act);
        if (!act.canWrite()) {
            to.close();
            return;
        }

        File file = new File(sdfPath);
        if (file.exists()) {
            FileReader fin = new FileReader(file);
            Scanner src = new Scanner(fin);
            String line;

            if (src.hasNext()) {
                line = src.nextLine();
                to.write(new String(line.trim() + " " + 0 + "\n").getBytes());
            }
            while (src.hasNext()) {
                line = src.nextLine();
                if (line.startsWith("$")) {
                    if (src.hasNext()) {
                        line = src.nextLine();
                        to.write(new String(line.trim() + " " + 0 + "\n")
                                .getBytes());
                    }
                }
            }
            src.close();
        }
        to.close();
    }

    public static String
    makeXFromACT(String path, String actFileName) throws Exception {
        // creates an X file with no descriptors using the compound list from
        // the ACT file.
        // Needed in order to use datasplit on modeling sets that are
        // specified by ACT and
        // SDF files (no X file provided). This is because datasplit only
        // works on X files.

        String msg = "";
        String xFileName = actFileName.substring(0, actFileName
                .lastIndexOf("."))
                + ".x";
        File actFile = new File(path + actFileName);
        File xFile = new File(path + xFileName);

        BufferedReader fin = new BufferedReader(new FileReader(actFile));
        FileWriter fout = new FileWriter(xFile);

        int numActCompounds = getACTCompoundNames(path + actFileName).size();
        int numDescriptors = 2; // datasplit will refuse any inputs without at
        // least 2 descriptors
        fout.write("" + numActCompounds + " " + numDescriptors + "\n");
        String descriptorsLine = "junk1 junk2\n";
        fout.write(descriptorsLine);

        String line;
        int index = 1;
        while ((line = fin.readLine()) != null) {
            String[] array = line.split("\\s+");
            if (array.length == 2) {
                // fake descriptor values of "1" and "2" are added in.
                fout.write("" + index + " " + array[0] + " 1 2\n");
            }
            index++;
        }
        fin.close();
        fout.close();
        return msg;
    }

    public static ArrayList<String> uploadDataset(
            String userName,
            File sdfFile,
            String sdfFileName,
            File actFile,
            String actFileName,
            File xFile,
            String xFileName,
            String datasetName,
            String actFileType,
            String datasetType,
            String externalCompoundList) throws Exception {
        // will take care of the upload SDF, X, and ACT file
        // in case of errors will delete the directory

        String path = Constants.CECCR_USER_BASE_PATH + userName
                + "/DATASETS/" + datasetName + "/";

        logger.debug("Copying dataset files to " + path);

        ArrayList<String> msgs = new ArrayList<String>(); // holds any error
        // messages from
        // validations

        ArrayList<String> act_compounds = null;
        ArrayList<String> sdf_compounds = null;
        ArrayList<String> x_compounds = null;

        // create dir
        if (!new File(Constants.CECCR_USER_BASE_PATH + userName).exists()) {
            new File(Constants.CECCR_USER_BASE_PATH + userName).mkdirs();
        }
        if (!new File(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS")
                .exists()) {
            new File(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS")
                    .mkdirs();
        }
        File datasetDir = new File(path);
        datasetDir.mkdirs();

        // copy files from temp location into datasets dir
        // run validations on each file after the copy
        if (sdfFile != null) {
            sdfFileName = sdfFileName.replaceAll(" ", "_").replaceAll("\\(",
                    "_").replaceAll("\\)", "_");
            logger.debug("checking SDF");
            saveSDFFile(sdfFile, path, sdfFileName);
            sdfFile = new File(path + sdfFileName);

            sdf_compounds = getSDFCompoundNames(sdfFile.getAbsolutePath());

            rewriteSdf(path, sdfFileName, sdf_compounds);

            if (!sdfIsValid(sdfFile).equals("")) {
                msgs.add(sdfIsValid(sdfFile));
            }
            // Check if SDF file contains duplicates
            String duplicates = findDuplicates(sdf_compounds);
            if (!duplicates.isEmpty()) {
                msgs.add(ErrorMessages.SDF_CONTAINS_DUPLICATES + duplicates);
            }
            logger.debug("done checking SDF");
        }

        if (actFile != null) {
            actFileName = actFileName.replaceAll(" ", "_").replaceAll("\\(",
                    "_").replaceAll("\\)", "_");
            logger.debug("checking ACT");
            String msg = saveACTFile(actFile, path, actFileName);
            if (!msg.isEmpty()) {
                msgs.add(msg);
            }
            actFile = new File(path + actFileName);

            act_compounds = getACTCompoundNames(actFile.getAbsolutePath());

            msg = rewriteACTFile(path + actFileName);
            if (!msg.isEmpty()) {
                msgs.add(msg);
            }

            actFile = new File(path + actFileName);
            msg = actIsValid(actFile, actFileType);
            if (!msg.isEmpty()) {
                msgs.add(msg);
            }

            // Check if ACT file contains duplicates
            String duplicates = findDuplicates(act_compounds);
            if (!duplicates.isEmpty()) {
                msgs.add(ErrorMessages.ACT_CONTAINS_DUPLICATES + duplicates);
            }
            logger.debug("done checking ACT");
        }

        if (xFile != null) {
            xFileName = xFileName.replaceAll(" ", "_").replaceAll("\\(", "_")
                    .replaceAll("\\)", "_");
            logger.debug("checking X");
            String msg = saveXFile(xFile, path, xFileName);
            xFile = new File(path + xFileName);
            x_compounds = getXCompoundNames(path + xFileName);

            msg += rewriteXFileAndValidate(xFile);
            if (!msg.isEmpty()) {
                msgs.add(msg);
            }
            logger.debug("done checking X");

        }

        // generate an empty activity file (needed for... heatmaps or
        // something...?)
        if (actFileType.equals(Constants.PREDICTION)) {
            logger.debug("Generating empty ACT");
            generateEmptyActFile(path, sdfFileName.substring(0, sdfFileName
                    .lastIndexOf(".")), path + sdfFileName);
        }
        if (actFileType.equals(Constants.PREDICTIONWITHDESCRIPTORS)) {
            logger.debug("Generating empty ACT");
            generateEmptyActFile(path, xFileName.substring(0, xFileName
                    .lastIndexOf(".")), path + xFileName);
        }

        logger.debug("doing compound list validations");
        // more validation: check that the information in the files lines up
        // properly (act, sdf, x should all have the same compounds)
        if (actFile != null && sdfFile != null) {
            // Check if SDF matches ACT file
            int numACT = act_compounds.size();
            int numSDF = sdf_compounds.size();

            if (numACT != numSDF) {
                msgs.add("Error: The ACT file contains " + numACT
                        + " compounds; the SDF contains " + numSDF
                        + " compounds.");
            }

            // Check if compounds in act are the same as compounds in sdf
            String mismatches = "";
            for (int i = 0; i < act_compounds.size(); i++) {
                if (i >= sdf_compounds.size()
                        || !act_compounds.get(i).equals(sdf_compounds.get(i))) {
                    mismatches += act_compounds.get(i) + " ";
                }
            }

            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.COMPOUND_IDS_ACT_DONT_MATCH_SDF
                        + mismatches);
            }

            // check that compounds in the sdf are matched by compounds in the
            // act, too
            mismatches = "";
            for (int i = 0; i < sdf_compounds.size(); i++) {
                if (i >= act_compounds.size()
                        || !sdf_compounds.get(i).equals(act_compounds.get(i))) {
                    mismatches += sdf_compounds.get(i) + " ";
                }
            }

            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.COMPOUND_IDS_SDF_DONT_MATCH_ACT
                        + mismatches);
            }
            sdf_compounds = getSDFCompoundNames(path + sdfFileName);
            act_compounds = getACTCompoundNames(path + actFileName);
        }

        if (actFile != null && xFile != null) {
            // Check if X file matches ACT file
            int numACT = act_compounds.size();
            int numX = x_compounds.size();

            if (numACT != numX) {
                msgs.add("Error: The ACT file contains " + numACT
                        + " compounds; the X file contains " + numX
                        + " compounds.");
            }

            String mismatches = "";
            for (int i = 0; i < act_compounds.size(); i++) {
                if (i >= x_compounds.size()
                        || !act_compounds.get(i).equals(x_compounds.get(i))) {
                    mismatches += act_compounds.get(i) + " ";
                }
            }

            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.COMPOUND_IDS_ACT_DONT_MATCH_X
                        + mismatches);
            }

            // check that compounds in the x file are matched by compounds in
            // the act, too
            mismatches = "";
            for (int i = 0; i < x_compounds.size(); i++) {
                if (i >= act_compounds.size()
                        || !x_compounds.get(i).equals(act_compounds.get(i))) {
                    mismatches += x_compounds.get(i) + " ";
                }
            }

            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.COMPOUND_IDS_X_DONT_MATCH_ACT
                        + mismatches);
            }
            x_compounds = getXCompoundNames(path + xFileName);
            act_compounds = getACTCompoundNames(path + actFileName);
        }

        if (sdfFile != null && xFile != null) {
            // Check if SDF matches X file
            int numX = x_compounds.size();
            int numSDF = sdf_compounds.size();

            if (numX != numSDF) {
                msgs.add("Error: The X file contains " + numX
                        + " compounds; the SDF contains " + numSDF
                        + " compounds.");
            }

            String mismatches = "";
            for (int i = 0; i < x_compounds.size(); i++) {
                if (i >= sdf_compounds.size()
                        || !x_compounds.get(i).equals(sdf_compounds.get(i))) {
                    mismatches += x_compounds.get(i) + " ";
                }
            }

            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.COMPOUND_IDS_X_DONT_MATCH_SDF
                        + mismatches);
            }

            // check that compounds in the sdf are matched by compounds in the
            // x, too
            mismatches = "";
            for (int i = 0; i < sdf_compounds.size(); i++) {
                if (i >= x_compounds.size()
                        || !sdf_compounds.get(i).equals(x_compounds.get(i))) {
                    mismatches += sdf_compounds.get(i) + " ";
                }
            }

            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.COMPOUND_IDS_SDF_DONT_MATCH_X
                        + mismatches);
            }
            x_compounds = getXCompoundNames(path + xFileName);
            sdf_compounds = getSDFCompoundNames(path + sdfFileName);
        }

        if (externalCompoundList != null && !externalCompoundList.isEmpty()) {
            // check that the dataset actually contains all the compounds the
            // user gave in the external compound list
            ArrayList<String> datasetCompounds = new ArrayList<String>();
            if (sdfFile != null) {
                datasetCompounds.addAll(sdf_compounds);
            } else {
                datasetCompounds.addAll(x_compounds);
            }
            Collections.sort(datasetCompounds);
            String[] extCompoundsArray = externalCompoundList.replaceAll(",",
                    " ").replaceAll("\\\n", " ").split("\\s+");

            String mismatches = "";
            for (int i = 0; i < extCompoundsArray.length; i++) {
                if (Collections.binarySearch(datasetCompounds,
                        extCompoundsArray[i]) < 0) {
                    // compound was not found in dataset. Output an error.
                    mismatches += " " + extCompoundsArray[i];
                }
            }
            if (!mismatches.isEmpty()) {
                msgs.add(ErrorMessages.EXTERNAL_COMPOUNDS_NOT_IN_DATASET
                        + mismatches);
            }
        }

        if (msgs.isEmpty()) {
            logger.debug("Dataset file validation successful!");
            // success - passed all validations
        } else {
            logger.debug("Validations failed - deleting");
            // failed validation - completely delete directory of this dataset

            /*
             * FileAndDirOperations.deleteDirContents(path); if(!
             * FileAndDirOperations.deleteDir(new File(path))){
             * logger.debug("Directory delete failed"); } if((new
             * File(path)).exists()){
             * logger.debug("Directory still exists"); }
             */
        }

        return msgs;
    }

    public static String saveSDFFile(File sdfFile,
                                     String path,
                                     String sdfFileName) throws Exception {

        String destFilePath = path + sdfFileName;
        FileAndDirOperations
                .copyFile(sdfFile.getAbsolutePath(), destFilePath);
        return "";
    }

    public static void
    rewriteSdf(String filePath,
               String fileName,
               ArrayList<String> sdfCompoundNames) throws Exception {

        // SDFs with lines longer than 1023 characters will not work properly
        // with MolconnZ.
        // This function gets rid of all such lines.
        // Does not change the chemical information in the SD file; long lines
        // in SDFs are always comments.

        // This function will also remove the silly /r characters Windows
        // likes
        // to add to newlines.

        // It also corrects any compound names that may have spaces in them,
        // by
        // changing the spaces to underscores.

        // In addition, there is a comment line under the compound ID.
        // Sometimes
        // this can be blank -- which breaks the JChem tools. This function
        // will
        // add the comment line for each compound.

        File infile = new File(filePath + fileName);
        File outfile = new File(filePath + fileName + ".temp");
        logger.debug("PATH: " + filePath);

        // now, remove the long lines from the input file
        FileReader fin = new FileReader(infile);
        String temp;
        String lastLine = "$$$$";
        Scanner src = new Scanner(fin);
        FileWriter fout = new FileWriter(outfile);

        /*
         * The JChem tools are VERY specific about what the beginning of each
         * compound in an SDF should look like, and will fail if there's any
         * deviation. The first line must be a compound ID, and the fourth
         * line must begin the compound information. Traditionally, the second
         * line is a comment and the third is blank, but they can contain
         * anything, so we'll just make them both blank.
         */

        int sdfCompoundNamesIndex = 0;
        while (src.hasNextLine()) {
            temp = src.nextLine();

            // replace any spaces in compound name with underscores
            if (sdfCompoundNamesIndex < sdfCompoundNames.size()
                    && temp.trim().equals(
                    sdfCompoundNames.get(sdfCompoundNamesIndex))
                    && lastLine.startsWith("$$$$")) {
                temp = temp.trim();

                // remove quotes around the compound name
                if ((temp.startsWith("\"") && temp.endsWith("\""))
                        || (temp.startsWith("\"") && temp.endsWith("\""))) {
                    temp = temp.substring(1, temp.length() - 1);
                }

                // we will make temp contain the compound ID line, two blank
                // lines, then the
                // start of the compound information.

                temp = temp.replaceAll("\\s+", "_").replaceAll("#", "_H_")
                        .replaceAll("~", "_T_").replaceAll("!", "_EM_")
                        .replaceAll("%", "_P_").replaceAll("@", "_A_")
                        .replaceAll("&", "_AND_").replaceAll("\\*", "_S_")
                        .replaceAll("\\\\", "_BS_").replaceAll("/", "_FS_")
                        .replaceAll("\\(", "_OB_").replaceAll("\\)", "_CB_")
                        .replaceAll("\\[", "_SOB_")
                        .replaceAll("\\]", "_SCB_").replaceAll("\\+", "_PL_")
                        .replaceAll("\\=", "_E_");

                temp += "\n\n\n";
                sdfCompoundNamesIndex++;

                while (src.hasNextLine()) {
                    String line = src.nextLine();
                    String[] array = line.trim().split("\\s+");
                    if (array.length > 5
                            && Utility.stringContainsInt(array[0])
                            && Utility.stringContainsInt(array[1])
                            && Utility.stringContainsInt(array[2])
                            && Utility.stringContainsInt(array[3])
                            && Utility.stringContainsInt(array[4])) {
                        // it's a pretty safe bet this is the start of the
                        // compound information
                        temp += line;
                        break;
                    }
                }
            }

            // remove Windows-format \r "newline" characters
            temp = temp.replace('\r', ' ');
            if (temp.length() < 1000) {

                fout.write(temp + "\n");
            }
            lastLine = temp;
        }
        src.close();
        fin.close();
        fout.close();
        // infile.delete();
        infile.renameTo(new File(filePath + fileName + ".old"));
        outfile.renameTo(infile);
    }

    public static String saveACTFile(File actFile,
                                     String path,
                                     String actFileName) throws IOException {

        boolean isXlsFile = actFile.getName().endsWith(".x")
                || actFile.getName().endsWith(".xl")
                || actFile.getName().endsWith(".xls");

        String destFilePath = path + actFileName;
        FileAndDirOperations
                .copyFile(actFile.getAbsolutePath(), destFilePath);

        if (isXlsFile) {
            XLStoACT(path, actFile.getName().substring(0,
                    actFile.getName().indexOf("."))
                    + ".act", actFile.getName());
            new File(path + actFile.getName()).delete();
        }

        return "";
    }

    public static String
    rewriteACTFile(String filePath) throws FileNotFoundException,
            IOException {
        // removes the \r things (stupid Windows)
        // removes the header, if any
        // replaces spaces in compound names with underscores

        File file = new File(filePath);
        if (file.exists()) {

            FileReader fin = new FileReader(file);

            Scanner src = new Scanner(fin);
            StringBuilder sb = new StringBuilder();

            String temp = "";
            // do first line
            boolean firstLineContainsHeader = false;
            if (src.hasNext()) {
                temp = src.nextLine();
                if (temp.split("\\s+").length == 2) {
                    try {
                        Float.parseFloat(temp.split("\\s+")[1].trim());
                    } catch (Exception ex) {
                        // second thing isn't a number -- line was a header!
                        logger.debug(
                                "Activity file contains a header: "
                                        + temp
                                        + " {"
                                        + temp.split("\\s+")[1].trim()
                                        + "}"
                        );
                        logger.error(ex);
                        firstLineContainsHeader = true;
                    }
                } else {
                    // contains more than 2 things -- it was a header!
                    logger.debug("Activity file header: " + temp);
                    firstLineContainsHeader = true;
                }
            }

            if (!firstLineContainsHeader) {
                logger.debug(
                        "Activity file has no header. First line: "
                                + temp
                );

                String[] tempTokens = temp.trim().split("\\s+");
                String compoundId = tempTokens[0];
                String activity = tempTokens[1];

                // remove quotes around compound name
                if ((compoundId.startsWith("\"") && compoundId.endsWith("\""))
                        || (compoundId.startsWith("\"") && compoundId
                        .endsWith("\""))) {
                    compoundId = compoundId.substring(1,
                            compoundId.length() - 1);
                }

                compoundId = compoundId.replaceAll("\\s+", "_").replaceAll(
                        "#", "_H_").replaceAll("~", "_T_").replaceAll("!",
                        "_EM_").replaceAll("%", "_P_").replaceAll("@", "_A_")
                        .replaceAll("&", "_AND_").replaceAll("\\*", "_S_")
                        .replaceAll("\\\\", "_BS_").replaceAll("/", "_FS_")
                        .replaceAll("\\(", "_OB_").replaceAll("\\)", "_CB_")
                        .replaceAll("\\[", "_SOB_")
                        .replaceAll("\\]", "_SCB_").replaceAll("\\+", "_PL_")
                        .replaceAll("\\=", "_E_");

                sb.append(compoundId + " " + activity + "\n");
            }

            while (src.hasNext()) {
                temp = src.nextLine();
                String[] tempTokens = temp.split("\\s+");
                String compoundId = "";
                String activity = "";
                for (int i = 0; i < tempTokens.length; i++) {
                    if (i == tempTokens.length - 1) {
                        activity = tempTokens[i];
                    } else {
                        compoundId += tempTokens[i];
                        if (i < tempTokens.length - 2) {
                            compoundId += "_";
                        }
                    }
                }
                // remove quotes around compound names
                if ((compoundId.startsWith("\"") && compoundId.endsWith("\""))
                        || (compoundId.startsWith("\"") && compoundId
                        .endsWith("\""))) {
                    compoundId = compoundId.substring(1,
                            compoundId.length() - 1);
                }

                compoundId = compoundId.replaceAll("\\s+", "_").replaceAll(
                        "#", "_H_").replaceAll("~", "_T_").replaceAll("!",
                        "_EM_").replaceAll("%", "_P_").replaceAll("@", "_A_")
                        .replaceAll("&", "_AND_").replaceAll("\\*", "_S_")
                        .replaceAll("\\\\", "_BS_").replaceAll("/", "_FS_")
                        .replaceAll("\\(", "_OB_").replaceAll("\\)", "_CB_")
                        .replaceAll("\\[", "_SOB_")
                        .replaceAll("\\]", "_SCB_").replaceAll("\\+", "_PL_")
                        .replaceAll("\\=", "_E_");

                sb.append(compoundId + " " + activity + "\n");
            }
            src.close();
            fin.close();

            FileWriter fout = new FileWriter(filePath);
            fout.write(sb.toString());
            fout.close();

            return "";
        } else {
            return "File does not exist: " + filePath;
        }

    }

    public static String
    saveXFile(File xFile, String path, String xFileName) throws IOException {

        String destFilePath = path + xFileName;
        FileAndDirOperations.copyFile(xFile.getAbsolutePath(), destFilePath);

        return "";
    }

    public static Vector<Vector<String>>
    readFileToVector(String delimiter, String path) throws Exception {

        Vector<Vector<String>> result = new Vector<Vector<String>>();

        try {
            BigFile bf = new BigFile(path);
            for (Iterator<String> i = bf.iterator(); i.hasNext(); ) {
                Vector<String> temp = new Vector<String>();
                String[] s = i.next().split(delimiter);
                for (int j = 0; j < s.length; j++) {
                    temp.add(s[j]);
                }
                result.add(temp);
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return result;

    }

    public static ArrayList<String>
    getXCompoundNames(String fileLocation) throws Exception {
        ArrayList<String> x_compounds = new ArrayList<String>();
        File file = new File(fileLocation);
        logger.debug("Getting X file compounds from " + fileLocation);
        if (file.exists()) {
            FileReader fin = new FileReader(file);
            Scanner src = new Scanner(fin);
            String line = src.nextLine();
            String[] header = line.split("\\s+");
            int numCompounds = Integer.parseInt(header[0]);
            src.nextLine(); // skip descriptors
            int i = 0;
            while (src.hasNextLine()) {
                line = src.nextLine();
                String[] array = line.trim().split("\\s+");
                if (array.length > 1 && i < numCompounds) { // this will skip
                    // any blank lines
                    // in an X file, first value is an index, second is
                    // compoundID
                    x_compounds.add(array[1].trim());
                    i++;
                }
            }
            src.close();
            fin.close();
        }
        return x_compounds;
    }

    public static ArrayList<String>
    getACTCompoundNames(String fileLocation) throws FileNotFoundException,
            IOException {
        ArrayList<String> act_compounds = new ArrayList<String>();
        File file = new File(fileLocation);

        if (file.exists()) {
            FileReader fin = new FileReader(file);
            Scanner src = new Scanner(fin);
            String line;
            while (src.hasNext()) {
                line = src.nextLine();
                String[] array = line.split("\\s+");
                if (array.length == 2) {
                    if (GenericValidator.isDouble(array[1])) {
                        act_compounds.add(array[0].trim());
                    }
                }
            }
            src.close();
            fin.close();
        }
        return act_compounds;
    }

    public static ArrayList<String>
    getSDFCompoundNames(String sdfPath) throws Exception {
        // returns JUST THE NAMES of the compounds in an SDF, no structure or
        // anything.

        File infile = new File(sdfPath);
        FileReader fin = new FileReader(infile);
        BufferedReader br = new BufferedReader(fin);
        ArrayList<String> chemicalNames = new ArrayList<String>();

        String line;
        // skip any whitespace lines before the first molecule
        while ((line = br.readLine()) != null && line.trim().isEmpty()) {
        }
        // read first molecule
        if (line != null) {
            chemicalNames.add(line.trim());
        }
        // read subsequent molecules
        while ((line = br.readLine()) != null) {
            if (line.startsWith("$$$$")) {
                // skip any whitespace lines before the next molecule
                while ((line = br.readLine()) != null
                        && line.trim().isEmpty()) {
                }
                // read next molecule
                if (line != null && !line.trim().isEmpty()) {
                    chemicalNames.add(line.trim());
                }
            }
        }

        // Do not sort this output. It is used to generate the identifiers in
        // .X files.
        // Sorting this would screw with the indexing; these should be left
        // alone.
        // Collections.sort(chemicalNames);
        br.close();
        return chemicalNames;
    }

    public static ArrayList<String>
    getCompoundsFromSdf(String sdfPath) throws Exception {
        // opens an SDF, and returns each full compound (the name,
        // coordinates, comments, etc)
        // as a string just as it appears in the file. Useful for splitting or
        // combining
        // of SDFs.
        // warning: don't open too large of files with this, as you will run
        // out of memory.
        ArrayList<String> compounds = new ArrayList<String>();

        File infile = new File(sdfPath);
        FileReader fin = new FileReader(infile);
        BufferedReader br = new BufferedReader(fin);

        String compound = new String();
        String line;
        // read molecules
        while ((line = br.readLine()) != null) {
            compound += line + "\n";
            if (line.startsWith("$$$$")) {
                // done reading compound, add it to the list
                compounds.add(compound);
                compound = new String();
            }
        }
        br.close();
        return compounds;
    }

    // returns the compound names of the duplicates
    private static String findDuplicates(ArrayList<String> compoundList) {
        String duplicates = "";

        ArrayList<String> temp_list = new ArrayList<String>();
        for (int i = 0; i < compoundList.size(); i++) {
            if (temp_list.contains(compoundList.get(i))) {
                duplicates += compoundList.get(i) + " ";
            } else {
                temp_list.add(compoundList.get(i));
            }
        }
        return duplicates;
    }

    public static HashMap<String, String>
    parseActFile(String fileName) throws FileNotFoundException,
            IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        int length = fis.available();
        byte[] bytes = new byte[length];
        fis.read(bytes);
        String byteStr = new String(bytes);

        String[] array = byteStr.split("\\s+");
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < array.length; i++) {
            map.put(array[i], array[i + 1]);
            i++;
        }
        fis.close();
        return map;
    }

    public static void XLStoACT(String filePath,
                                String actFileName,
                                String xlsFileName) {
        String fullPath = filePath + xlsFileName;
        File file = new File(fullPath);
        StringBuilder sb = new StringBuilder();

        try {
            POIFSFileSystem fs = new POIFSFileSystem(
                    new FileInputStream(file));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell1, cell2;

            int rows = sheet.getPhysicalNumberOfRows();
            for (int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {
                    cell1 = row.getCell((short) 0);
                    cell2 = row.getCell((short) 1);
                    if (cell1.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        sb.append((int) cell1.getNumericCellValue());
                    } else {
                        sb.append(cell1.getRichStringCellValue());
                    }

                    if (cell2.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        sb.append(" " + cell2.getNumericCellValue() + "\n");
                    } else {
                        sb.append(" " + cell2.getRichStringCellValue() + "\n");
                    }
                }
            }

            FileWriter fout = new FileWriter(filePath + actFileName);
            fout.write(sb.toString());
            fout.close();

        } catch (Exception ioe) {
            logger.error(ioe);
        }
    }

    public static String sdfIsValid(File sdfFile) throws Exception {
        if (!sdfFile.exists()) {
            return ErrorMessages.INVALID_SDF;
        } else if (getSDFCompoundNames(sdfFile.getAbsolutePath()).size() == 0) {
            return ErrorMessages.SDF_IS_EMPTY;
        }

        return "";
    }

    public static String rewriteXFileAndValidate(File xFile) throws Exception {
        // checks the X file and removes any extra lines

        if (!xFile.exists()) {
            return ErrorMessages.INVALID_X;
        } else if (getXCompoundNames(xFile.getAbsolutePath()).size() == 0) {
            return ErrorMessages.X_IS_EMPTY;
        }

        BufferedReader br = new BufferedReader(new FileReader(xFile));

        BufferedWriter out = new BufferedWriter(new FileWriter(xFile
                .getAbsolutePath() + ".temp"));

        String line = br.readLine();
        String[] header = line.trim().split("\\s+");

        int numCompounds = 0;
        int numDescriptors = 0;
        try {
            numCompounds = Integer.parseInt(header[0]);
            numDescriptors = Integer.parseInt(header[1]);
        } catch (Exception ex) {
            logger.error("Invalid X File header on line 1: \"" + line
                    + "\". \n" + ex);
            br.close();
            out.close();
            return "Invalid X File header on line 1: \"" + line + "\". ";
        }

        out.write("" + numCompounds + " " + numDescriptors + "\n");

        // descriptor names line
        line = br.readLine();
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length != numDescriptors) {
            String err = "Error in X file line " + 2 + ": line contains "
                    + tokens.length + " elements but " + numDescriptors
                    + " were expected. Line was: \"" + line + "\"";
            logger.debug(err);
            br.close();
            out.close();
            return err;
        }
        for (int i = 0; i < tokens.length; i++) {
            out.write(tokens[i] + " ");
        }
        out.write("\n");

        // read through the X file line by line; ensure the matrix is of the
        // correct size throughout.
        for (int i = 0; i < numCompounds; i++) {
            line = br.readLine();
            if (line != null) {
                tokens = line.trim().split("\\s+");
                int expectedTokens = numDescriptors + 2;
                if (tokens.length != expectedTokens) {
                    br.close();
                    out.close();
                    return "Error in X file line " + (i + 3)
                            + ": line contains " + tokens.length
                            + " elements but " + expectedTokens
                            + " were expected. Line was: \"" + line + "\"";
                }
                out.write(tokens[0] + " "); // line number

                // remove quotes around the compound id
                if ((tokens[1].startsWith("\"") && tokens[1].endsWith("\""))
                        || (tokens[1].startsWith("\"") && tokens[1]
                        .endsWith("\""))) {
                    tokens[1] = tokens[1]
                            .substring(1, tokens[1].length() - 1);
                }

                tokens[1] = tokens[1].replaceAll("\\s+", "_").replaceAll("#",
                        "_H_").replaceAll("~", "_T_").replaceAll("!", "_EM_")
                        .replaceAll("%", "_P_").replaceAll("@", "_A_")
                        .replaceAll("&", "_AND_").replaceAll("\\*", "_S_")
                        .replaceAll("\\\\", "_BS_").replaceAll("/", "_FS_")
                        .replaceAll("\\(", "_OB_").replaceAll("\\)", "_CB_")
                        .replaceAll("\\[", "_SOB_")
                        .replaceAll("\\]", "_SCB_").replaceAll("\\+", "_PL_")
                        .replaceAll("\\=", "_E_");

                out.write(tokens[1] + " "); // write compound id
                for (int j = 2; j < tokens.length; j++) { // for each
                    // descriptor value
                    try {
                        // convert from scientific notation to a regular
                        // decimal form
                        // done for compatibility with datasplit and other
                        // programs
                        if (tokens[j].contains("E")
                                || tokens[j].contains("e")) {
                            tokens[j] = Utility.floatToString(Float
                                    .parseFloat(tokens[j]));
                        } else {

                            // check that descriptor value is numeric (not a
                            // string or something else crazy)
                            Float.parseFloat(tokens[j]);
                        }
                    } catch (Exception ex) {
                        String err = "Error in X file at compound " + (i + 1)
                                + " at descriptor " + (j - 1) + ": '"
                                + tokens[j] + "' is not a number.";
                        logger.error(err + "\n" + ex);
                        br.close();
                        out.close();
                        return err;
                    }
                    out.write(tokens[j] + " ");
                }
                out.write("\n");

            } else {
                br.close();
                out.close();
                return "Error in X file: expected " + numCompounds
                        + " compounds but only " + i + " were present.";
            }
        }

        br.close();
        out.close();

        FileAndDirOperations.deleteFile(xFile.getAbsolutePath());
        FileAndDirOperations.copyFile(xFile.getAbsolutePath() + ".temp",
                xFile.getAbsolutePath());
        FileAndDirOperations.deleteFile(xFile.getAbsolutePath() + ".temp");

        return "";
    }

    public static void dos2unix(String filePath) {
        String execstr = "dos2unix " + filePath;
        RunExternalProgram.runCommand(execstr, "");
    }

    public static String
    actIsValid(File actFile, String actFileType) throws IOException {
        // really not much of a validator, but better than nothing.
        // checks that
        if (!actFile.exists()) {
            return ErrorMessages.ACT_NOT_VALID;
        }
        BufferedReader br = new BufferedReader(new FileReader(actFile));
        String line;
        String[] lineArray;
        int size;
        while ((line = br.readLine()) != null) {
            lineArray = (line.trim()).split("\\s+");
            size = lineArray.length;
            // skip blank lines
            // make sure each line has 2 things on it
            if (size == 2 && GenericValidator.isFloat(lineArray[1])
                    || size == 0) {
                // good so far
            } else {
                // bad line found
                br.close();
                return ErrorMessages.ACT_NOT_VALID;
            }

            if (actFileType.equalsIgnoreCase(Constants.CATEGORY)) {
                if (GenericValidator.isInt(lineArray[1])) {
                } else {
                    br.close();
                    return ErrorMessages.ACT_DOESNT_MATCH_PROJECT_TYPE;
                }
            }
        }
        br.close();
        return "";
    }

    public static void
    randomizeActivityFile(String filePath, String outFilePath) throws Exception {
        ArrayList<String> actFileCompounds = getACTCompoundNames(filePath);
        HashMap<String, String> actFileIdsAndValues
                = getActFileIdsAndValues(filePath);
        ArrayList<String> actFileValues = new ArrayList<String>(
                actFileIdsAndValues.values());
        Collections.shuffle(actFileValues);

        if (actFileValues.size() != actFileCompounds.size()) {
            throw new Exception("Error: act file value array is size "
                    + actFileValues.size()
                    + " and compound names array is size "
                    + actFileCompounds.size());
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(outFilePath));
        for (int i = 0; i < actFileCompounds.size(); i++) {
            out.write(actFileCompounds.get(i) + " " + actFileValues.get(i)
                    + "\n");
        }

        out.close();

    }

    public static void
    removeSkippedCompoundsFromExternalSetList(String fullXFile,
                                              String workingDir,
                                              String extSetXFile)
            throws Exception {
        // if some of the external compounds had bad descriptors, they need to
        // be removed from the
        // set of external compounds specified in ext_0.x.
        // Since ext_0.x just stores the compound IDs of the external set
        // compounds, we can write
        // an act file containing the updated external set and then convert it
        // to x (no information is lost this way)

        BufferedReader xFileIn = new BufferedReader(new FileReader(workingDir
                + extSetXFile));

        String actFileName = extSetXFile.substring(0, extSetXFile
                .lastIndexOf("."))
                + ".a";
        BufferedWriter actFileOut = new BufferedWriter(new FileWriter(
                workingDir + actFileName));
        int numCompounds = Integer
                .parseInt((xFileIn.readLine().split("\\s+"))[0]);
        xFileIn.readLine(); // descriptors; skip

        ArrayList<String> allCompounds = getXCompoundNames(workingDir
                + fullXFile);

        String line = "";
        for (int lineNum = 0; lineNum < numCompounds; lineNum++) {
            line = xFileIn.readLine();
            String[] tokens = line.split("\\s+");
            String xCompoundName = tokens[1];
            for (int i = 0; i < allCompounds.size(); i++) {
                if (xCompoundName.equals(allCompounds.get(i))) {
                    actFileOut.write(xCompoundName + " 0.0\n");
                }
            }
        }

        xFileIn.close();
        actFileOut.close();
        makeXFromACT(workingDir, actFileName);
    }

    public static void
    removeSkippedCompoundsFromActFile(String fullXFile,
                                      String workingDir,
                                      String actFile) throws Exception {
        // if some of the external compounds had bad descriptors, they need to
        // be removed from the
        // set of activities (ACT file).

        BufferedReader actFileIn = new BufferedReader(new FileReader(
                workingDir + actFile));

        String actFileOutName = actFile + ".temp";
        BufferedWriter actFileOut = new BufferedWriter(new FileWriter(
                workingDir + actFileOutName));

        ArrayList<String> allCompounds = getXCompoundNames(workingDir
                + fullXFile);

        String line = "";
        while ((line = actFileIn.readLine()) != null) {
            String[] tokens = line.split("\\s+");
            String actCompoundName = tokens[0];
            for (int i = 0; i < allCompounds.size(); i++) {
                if (actCompoundName.equals(allCompounds.get(i))) {
                    actFileOut
                            .write(actCompoundName + " " + tokens[1] + "\n");
                }
            }
        }

        actFileIn.close();
        actFileOut.close();
        FileAndDirOperations.copyFile(workingDir + actFileOutName, workingDir
                + actFile);
        FileAndDirOperations.deleteFile(workingDir + actFileOutName);
    }
}
