package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CheckDescriptors {
    private static final Logger logger = LoggerFactory.getLogger(CheckDescriptors.class);
    // Read in the output of a descriptor generation program (molconnZ,
    // dragon, etc.)
    // Look for any errors that would make the output unusable in modeling
    // Return an HTML-formatted string with user-readable feedback

    public static String checkSpecificDescriptors(String outputFileDirectory, String sdfFileName, String
            sdfFileEnding, String descriptor) throws Exception{
        Path descriptorDirPath = Paths.get(outputFileDirectory);
        String logsOutputFileDirectory = outputFileDirectory + "Logs/";
        String checkDescriptorsDir = outputFileDirectory + sdfFileName + sdfFileEnding;
        String errors = "";

        File errorSummaryFile = null;
        if (descriptor.equals(Constants.CDK)){
            errors = checkCdkDescriptors(checkDescriptorsDir);
        }else if(descriptor.equals(Constants.DRAGONH)){
            errors = CheckDescriptors.checkDragonXDescriptors(checkDescriptorsDir);
        }else if(descriptor.equals(Constants.DRAGONNOH)){
            errors = CheckDescriptors.checkDragonXDescriptors(checkDescriptorsDir);
        }else if(descriptor.equals(Constants.MOE2D)){
            errors = checkMoe2DDescriptors(checkDescriptorsDir);
        }else if(descriptor.equals(Constants.MACCS)) {
            errors = CheckDescriptors.checkMaccsDescriptors(checkDescriptorsDir);
        }else if(descriptor.equals(Constants.ISIDA)){
            if (!(Files.exists(descriptorDirPath.resolve(sdfFileName + ".ISIDA.hdr")) && Files.exists
                    (descriptorDirPath.resolve(sdfFileName + ".ISIDA.svm")))) {
                errors = "Cannot find ISIDA files";
            }
        }else if(descriptor.equals(Constants.DRAGON7)){
            if (!(Files.exists(descriptorDirPath.resolve(sdfFileName + ".dragon7")))) {
                errors = "Cannot find dragon7 files";
            }
        }

        //dragon 7 hasn't been fully implemented, no method to get errors from dragon 7 yet
        if (!errors.isEmpty() && !descriptor.equals(Constants.DRAGON7)) {
            //descriptorToFileEnding returns an array, at index 1 is the descriptor error file name
            errorSummaryFile = new File(logsOutputFileDirectory +
                    Constants.descriptorToFileEnding.get(descriptor)[1]);
            BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
            errorSummary.write(errors);
            errorSummary.close();
        }

        return errors;
    }

    public static String checkDragonXDescriptors(String dragonOutputFile) throws Exception {
        logger.debug("Checking Dragon descriptors: " + dragonOutputFile);
        List<String> descriptorNames = new ArrayList<>();
        String errors = "";

        File file = new File(dragonOutputFile);
        if (!file.exists() || file.length() == 0) {
            return "Could not read descriptor file.\n";
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        ArrayList<String> descriptorValues; // values for each molecule

        String line = br.readLine(); // junk line, should say
        // "dragonX: Descriptors"

        // contains some numbers
        line = br.readLine();
        Scanner tok = new Scanner(line);
        tok.next(); // just says "2" all the time, no idea what that means, so
        // skip that

        // the descriptor names are on this line
        line = br.readLine();
        tok.close();
        tok = new Scanner(line);
        while (tok.hasNext()) {
            String dname = tok.next();
            descriptorNames.add(dname);
        }
        tok.close();

        descriptorNames.remove(1); // contains molecule name, which isn't a
        // descriptor
        descriptorNames.remove(0); // contains molecule number, which isn't a
        // descriptor

        // read in the descriptor values. If one of them is the word "Error",
        // quit this shit - means Dragon failed at descriptoring.
        while ((line = br.readLine()) != null) {
            tok = new Scanner(line);
            descriptorValues = new ArrayList<>();
            descriptorValues.clear();
            while (tok.hasNext()) {
                String dvalue = tok.next();
                if (dvalue.equalsIgnoreCase("Error")) {
                    if (!errors.contains(
                            "Descriptor generation failed for molecule: " + descriptorValues.get(1) + ".\n")) {
                        errors += "Descriptor generation failed for molecule: " + descriptorValues.get(1) + ".\n";
                    }
                }
                descriptorValues.add(dvalue);
            }

            /*
             * not important - we don't do any checks that require the whole
             * descriptor matrix Descriptors di = new Descriptors();
             * descriptorValues.remove(1); //contains molecule name, which
             * isn't a descriptor descriptorValues.remove(0); //contains
             * molecule number, which isn't a descriptor
             * di.setDescriptorValues
             * (Utility.stringListToString(descriptorValues));
             */
            descriptorValues.clear();
        }
        logger.debug("Done checking Dragon descriptors: " + dragonOutputFile);
        br.close();
        return errors;
    }

    public static String checkCdkDescriptors(String cdkOutputFile) throws Exception {
        // check if CDK file exists, then look for NA's.
        String errors = "";

        File file = new File(cdkOutputFile);
        if (!file.exists() || file.length() == 0) {
            errors = "Could not read descriptor file.\n";
        } else {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("NA")) {
                    String compoundName = line.split("\\s+")[0];
                    errors += ("Error: compound named '" + compoundName + "' has some invalid descriptors. \n");
                }
            }
            br.close();
        }
        return errors;
    }

    public static String checkMaccsDescriptors(String maccsOutputFile) throws Exception {
        // right now this doesn't check anything. The MACCS keys never seem to
        // cause issues.
        String errors = "";

        File file = new File(maccsOutputFile);
        if (!file.exists() || file.length() == 0) {
            errors = "Could not read descriptor file.\n";
        }
        return errors;
    }

    public static String checkMoe2DDescriptors(String moe2DOutputFile) throws Exception {
        // right now this doesn't check anything. The MOE2D descriptors never
        // seem to cause issues.
        String errors = "";

        File file = new File(moe2DOutputFile);
        if (!file.exists() || file.length() == 0) {
            errors = "Could not read descriptor file.\n";
            return errors;
        }

        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        ArrayList<String> descriptorValues; // values for each molecule

        String line = br.readLine(); // descriptor names

        while ((line = br.readLine()) != null) {
            Scanner tok = new Scanner(line);

            if (tok.hasNext()) {
                tok.next(); // first value is compound name
            }

            while (tok.hasNext()) {
                String t = tok.next();
                try {
                    // check if it's a number
                    Float.parseFloat(t);
                } catch (Exception ex) {
                    errors += "Error reading Moe2D descriptor value: " + t + "\n";
                }
            }
            tok.close();
        }
        br.close();
        return errors;
    }
}
