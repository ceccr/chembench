package edu.unc.ceccr.workflows.descriptors;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadDescriptors {
    // Read in the output of a descriptor generation program
    // (molconnZ, dragon, etc.)
    // Create a Descriptors object for each compound.
    // puts results into descriptorNames and descriptorValueMatrix.

    private static Logger logger = Logger.getLogger(ReadDescriptors.class
            .getName());

    public static String[]
    readDescriptorNamesFromX(String xFile, String workingDir) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(workingDir
                + xFile));
        br.readLine(); // numCompounds, numDescriptors;
        String[] descs = br.readLine().split("\\s+");
        br.close();
        return descs;
    }

    public static void convertMzToX(String molconnZOutputFile,
                                    String workingDir) throws Exception {
        String cmd = "python " + Constants.CECCR_BASE_PATH
                + Constants.SCRIPTS_PATH + "mzToX.py " + molconnZOutputFile
                + " " + molconnZOutputFile + ".x";
        RunExternalProgram
                .runCommandAndLogOutput(cmd, workingDir, "mzToX.py");

        // Any errors from MolconnZ processing will be in the log files. Read
        // 'em.
    }

    public static void
    convertCDKToX(String cdkOutputFile, String workingDir) throws Exception {
        String cmd = "python " + Constants.CECCR_BASE_PATH
                + Constants.SCRIPTS_PATH + "cdkToX.py " + cdkOutputFile + " "
                + cdkOutputFile + ".x";
        RunExternalProgram.runCommandAndLogOutput(cmd, workingDir,
                "cdkToX.py");

        // Any errors from MolconnZ processing will be in the log files. Read
        // 'em.
    }

    public static void
    readMolconnZDescriptors(String molconnZOutputFile,
                            ArrayList<String> descriptorNames,
                            ArrayList<Descriptors> descriptorValueMatrix) throws Exception {

        logger.debug("reading MolconnZ Descriptors");

        File file = new File(molconnZOutputFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception(
                    "Could not read MolconnZ descriptors from file:" + " "
                            + molconnZOutputFile + "\n"
            );
        }
        FileReader fin = new FileReader(file);

        String temp;
        Scanner src = new Scanner(fin);
        // values for each molecule
        ArrayList<String> descriptorValues = new ArrayList<String>();

        boolean readingDescriptorNames = true;
        while (src.hasNext()) {
            // sometimes MolconnZ spits out nonsensical crap like ���C
            // along with
            // a descriptor value. Filter that out.
            temp = src.next();
            if (temp.matches("not_available")) {
                // molconnz will spit out a not_available if it gets a bad
                // molecule.
                descriptorValues.clear();
            }
            if (temp.matches("[\\p{Graph}]+")) {

                if (temp.matches("[0-9&&[^a-zA-Z]]+")
                        && readingDescriptorNames) {
                    // The first occurrence of a number indicates we're no
                    // longer reading descriptor names.
                    // "1" will indicate the first molecule, no matter what
                    // the SDF
                    // had as molecule numbers.
                    readingDescriptorNames = false;
                }

                if (readingDescriptorNames) {
                    descriptorNames.add(temp);
                } else {
                    if (descriptorValues.size() == descriptorNames.size()) {
                        // done reading values for this molecule.

                        String formula = descriptorValues
                                .get(Constants.MOLCONNZ_FORMULA_POS);
                        // formula should look something like C(12)H(22)O(11)
                        if (!formula.contains("(")) {
                            // the formula for the molecule isn't a formula
                            // usually indicates missing descriptors
                            // on the previous molecule
                            throw new Exception(
                                    "MolconnZ error: Molecule "
                                            + descriptorValues
                                            .get(Constants.MOLCONNZ_COMPOUND_NAME_POS)
                                            + " has formula "
                                            + descriptorValues
                                            .get(Constants.MOLCONNZ_FORMULA_POS)
                            );
                        }
                        /* contains molecule name, which isn't a descriptor */
                        descriptorValues
                                .remove(Constants.MOLCONNZ_FORMULA_POS);
                        /* contains molecule name, which isn't a descriptor */
                        descriptorValues
                                .remove(Constants.MOLCONNZ_COMPOUND_NAME_POS);
                        /* contains molecule ID, which isn't a descriptor */
                        descriptorValues.remove(0);
                        Descriptors di = new Descriptors();
                        di.setDescriptorValues(Utility
                                .StringArrayListToString(descriptorValues));
                        descriptorValueMatrix.add(di);
                        descriptorValues.clear();
                    }

                    /*
                     * a couple more special cases for when MolconnZ decides
                     * to go crazy
                     */
                    if (temp.equals("inf")) {
                        temp = "9999";
                    } else if (temp.equals("-inf")) {
                        temp = "-9999";
                    } else if (temp.equals("not_available")) {
                        /*
                         * quit this shit - means MolconnZ failed at
                         * descriptoring and all values past this point will
                         * be offset.
                         */
                        throw new Exception("MolconnZ descriptors invalid!");
                    }
                    descriptorValues.add(temp);
                }
            }
        }
        /* add the last molecule's descriptors */
        /* contains molecule name, which isn't a descriptor */
        descriptorValues.remove(Constants.MOLCONNZ_FORMULA_POS);
        descriptorNames.remove(Constants.MOLCONNZ_FORMULA_POS);
        /* contains molecule name, which isn't a descriptor */
        descriptorValues.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS);
        descriptorNames.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS);
        /* contains molecule ID, which isn't a descriptor */
        descriptorValues.remove(0);
        descriptorNames.remove(0);
        Descriptors di = new Descriptors();
        di.setDescriptorValues(Utility
                .StringArrayListToString(descriptorValues));
        descriptorValueMatrix.add(di);

        src.close();
        fin.close();
    }

    public static void
    readDragonDescriptors(String dragonOutputFile,
                          ArrayList<String> descriptorNames,
                          ArrayList<Descriptors> descriptorValueMatrix) throws Exception {

        logger.debug("reading Dragon Descriptors");

        File file = new File(dragonOutputFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read Dragon descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);
        /* values for each molecule */
        ArrayList<String> descriptorValues;
        /* junk line, should say "dragonX: Descriptors" */
        String line = br.readLine();

        /* contains some numbers */
        line = br.readLine();
        Scanner tok = new Scanner(line);
        // int num_molecules = Integer.parseInt(tok.next());

        /* just says "2" all the time, no idea what that means, so skip that */
        tok.next();
        // int num_descriptors = Integer.parseInt(tok.next());

        /* the descriptor names are on this line */
        line = br.readLine();
        tok.close();
        tok = new Scanner(line);
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
            descriptorValues = new ArrayList<String>();
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
            descriptorValues.remove(1);
            descriptorValues.remove(0);

            di.setDescriptorValues(Utility
                    .StringArrayListToString(descriptorValues));
            descriptorValueMatrix.add(di);
            descriptorValues.clear();
        }
        br.close();
    }

    public static void
    readMaccsDescriptors(String maccsOutputFile,
                         ArrayList<String> descriptorNames,
                         ArrayList<Descriptors> descriptorValueMatrix) throws Exception {
        // generate with "maccs.sh infile.sdf outfile.maccs"

        logger.debug("reading Maccs Descriptors");

        File file = new File(maccsOutputFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read MACCS keys.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);
        /* first line is junk, it says "name,FP:MACCS." */
        String line = br.readLine();

        while ((line = br.readLine()) != null) {
            String descriptorString = new String("");
            Scanner tok = new Scanner(line);
            tok.useDelimiter(",");
            tok.next(); // skip compound identifier
            String tmp = tok.next();
            tok.close();
            tok = new Scanner(tmp);
            tok.useDelimiter(" ");
            int last = 0;
            int descriptor = 0;
            while (tok.hasNext()) {
                descriptor = Integer.parseInt(tok.next());
                for (int i = last; i < descriptor; i++) {
                    descriptorString += "0 ";
                }
                descriptorString += "1 ";
                last = descriptor + 1;
            }
            tok.close();
            for (int i = last; i < Constants.NUM_MACCS_KEYS; i++) {
                descriptorString += "0 ";
            }
            Descriptors di = new Descriptors();
            di.setDescriptorValues(descriptorString);
            descriptorValueMatrix.add(di);

        }
        br.close();
        for (int i = 0; i < Constants.NUM_MACCS_KEYS; i++) {
            descriptorNames.add((new Integer(i)).toString());
        }
    }

    public static void
    readMoe2DDescriptors(String moe2DOutputFile,
                         ArrayList<String> descriptorNames,
                         ArrayList<Descriptors> descriptorValueMatrix) throws Exception {
        logger.debug("reading Moe2D Descriptors");

        File file = new File(moe2DOutputFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read MOE2D descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);
        /* contains descriptor names */
        String line = br.readLine();
        Scanner tok = new Scanner(line);
        tok.useDelimiter(",");
        /* first descriptor says "name"; we don't need that. */
        tok.next();
        while (tok.hasNext()) {
            descriptorNames.add(tok.next());
        }
        while ((line = br.readLine()) != null) {
            tok = new Scanner(line);
            tok.useDelimiter(",");
            if (tok.hasNext()) {
                /* first descriptor value is the name of the compound */
                tok.next();
            }
            String descriptorString = new String("");
            while (tok.hasNext()) {
                String val = tok.next();
                if (val.contains("NaN") || val.contains("e")) {
                    /*
                     * there's a divide-by-zero error for MOE2D sometimes.
                     * Results in NaN or "e+23" type numbers. only happens on
                     * a few descriptors, so it should be OK to just call it a
                     * 0 and move on.
                     */
                    val = "0";
                }
                descriptorString += val + " ";
            }
            if (!descriptorString.equalsIgnoreCase("")) {
                Descriptors di = new Descriptors();
                di.setDescriptorValues(descriptorString);
                descriptorValueMatrix.add(di);
            }
            tok.close();
        }
        br.close();
    }

    public static void
    readISIDADescriptors(String ISIDAOutputFile,
                         ArrayList<String> descriptorNames,
                         ArrayList<Descriptors> descriptorValueMatrix) throws Exception {
        logger.debug("reading ISIDA Descriptors");

        File file = new File(ISIDAOutputFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read ISIDA descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);
        /* contains descriptor names */
        String line = br.readLine();
        Scanner tok = new Scanner(line);
        tok.useDelimiter(" ");
        /* first descriptor says "title"; we don't need that. */
        tok.next();
        while (tok.hasNext()) {
            descriptorNames.add(tok.next());
        }
        while ((line = br.readLine()) != null) {
            tok = new Scanner(line);
            tok.useDelimiter(" ");
            if (tok.hasNext()) {
                /* first descriptor value is the name of the compound */
                tok.next();
            }
            String descriptorString = new String("");
            while (tok.hasNext()) {
                String val = tok.next();
                descriptorString += val + " ";
            }
            if (!descriptorString.equalsIgnoreCase("")) {
                Descriptors di = new Descriptors();
                di.setDescriptorValues(descriptorString);
                descriptorValueMatrix.add(di);
            }
            tok.close();
        }
        br.close();
    }

    public static void
    readXDescriptors(String xFile,
                     ArrayList<String> descriptorNames,
                     ArrayList<Descriptors> descriptorValueMatrix) throws Exception {
        logger.debug("Trying to read uploaded descriptors");
        File file = new File(xFile);
        if (!file.exists() || file.length() == 0) {
            logger.error(xFile + ": xFile not found");
            throw new Exception("Could not read X file descriptors: " + xFile
                    + "\n");
        }

        try {
            FileReader fin = new FileReader(file);
            BufferedReader br = new BufferedReader(fin);
            String line = br.readLine(); // header. ignored.
            line = br.readLine(); // contains descriptor names
            Scanner tok = new Scanner(line);
            tok.useDelimiter("\\s+");
            while (tok.hasNext()) {
                descriptorNames.add(tok.next());
            }
            tok.close();

            while ((line = br.readLine()) != null) {
                tok = new Scanner(line);
                tok.useDelimiter("\\s+");
                if (tok.hasNext()) {
                    tok.next(); // first value is the index of the compound
                }
                if (tok.hasNext()) {
                    tok.next(); // second value is the name of the compound
                }
                String descriptorString = new String("");
                while (tok.hasNext()) {
                    descriptorString += tok.next() + " ";
                }
                if (!descriptorString.equalsIgnoreCase("")) {
                    Descriptors di = new Descriptors();
                    di.setDescriptorValues(descriptorString);
                    descriptorValueMatrix.add(di);
                }
                tok.close();
            }
            br.close();
        } catch (FileNotFoundException e) {
            logger.error(file + ": File not found");
        }
    }
}