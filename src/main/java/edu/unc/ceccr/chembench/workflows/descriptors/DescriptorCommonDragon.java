package edu.unc.ceccr.chembench.workflows.descriptors;


import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class DescriptorCommonDragon implements DescriptorSet {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorCommonDragon.class);

    @Override
    public String checkDescriptors(String dragonOutputFile) throws Exception {
        List<String> descriptorNames = new ArrayList<>();
        String dragonOutputFileWithEnding = dragonOutputFile + getFileEnding();
        String errors = "";

        logger.debug("Checking Dragon descriptors: " + dragonOutputFileWithEnding);
        File file = new File(dragonOutputFileWithEnding);
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
        logger.debug("Done checking Dragon descriptors: " + dragonOutputFileWithEnding);
        br.close();
        return errors;
    }

    @Override
    public void readDescriptors(String dragonOutputFile, List<String> descriptorNames,
                                List<Descriptors> descriptorValueMatrix) throws Exception {

        logger.debug("reading Dragon Descriptors");
        logger.debug(dragonOutputFile);
        File file = new File(dragonOutputFile);
        if (!file.exists() ) {
            throw new Exception("Could not read Dragon descriptors. (File Missing)\n");
        }else if(file.length() == 0){
            throw new Exception("Could not read Dragon descriptors. (File Length is 0)\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);
        /* values for each molecule */
        List<String> descriptorValues;

        if (getDescriptorSetName().equals(Constants.DRAGONH) || getDescriptorSetName().equals(Constants.DRAGONNOH)){
            br.readLine(); // junk line, should say "dragonX: Descriptors"
            br.readLine(); // contains some numbers
        }

        /* the descriptor names are on this line */
        String line = br.readLine();
        Scanner tok = new Scanner(line);
        while (tok.hasNext()) {
            String dname = tok.next();
            descriptorNames.add(dname);
        }
        tok.close();
        /* contains molecule name, which isn't a descriptor */
        descriptorNames.remove(1);
        descriptorNames.remove(0);

        /*
         * read in the descriptor values. If one of them is the word "Error" ,
         * quit this shit - means Dragon failed at descriptoring.
         */
        while ((line = br.readLine()) != null) {
            tok = new Scanner(line);
            descriptorValues = new ArrayList<>();
            descriptorValues.clear();
            while (tok.hasNext()) {
                String dvalue = tok.next();
                if (dvalue.equalsIgnoreCase("Error")) {
                    tok.close();
                    throw new Exception("Dragon descriptors invalid!");
                }
                descriptorValues.add(dvalue);
            }
            tok.close();

            Descriptors di = new Descriptors();
            /* contains molecule name, which isn't a descriptor */
            di.setCompoundName(descriptorValues.remove(1));
            di.setCompoundIndex(Integer.parseInt(descriptorValues.remove(0)));

            di.setDescriptorValues(Utility.stringListToDoubleList(descriptorValues));
            descriptorValueMatrix.add(di);
            descriptorValues.clear();
        }
        br.close();
    }
}
