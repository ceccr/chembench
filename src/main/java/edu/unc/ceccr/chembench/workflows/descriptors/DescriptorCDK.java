package edu.unc.ceccr.chembench.workflows.descriptors;


import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DescriptorCDK implements DescriptorSet{
    private static final Logger logger = LoggerFactory.getLogger(DescriptorCDK.class);

    @Override
    public String getDescriptorSetName() {
        return Constants.CDK;
    }

    @Override
    public String getFileEnding() {
        return ".cdk";
    }

    @Override
    public String getFileErrorOut() {
        return "cdk.out";
    }

    @Override
    public void generateDescriptors(String sdfile, String outfile) throws Exception {
        //Given an SD file, run CDK to get the chemical descriptors for each compound.
        String xmlFile = Constants.CECCR_BASE_PATH + Constants.CDK_XMLFILE_PATH;
        String outfileWithEnding = outfile + getFileEnding();

        String execstr = "java -jar " + Constants.EXECUTABLEFILE_PATH + "CDKGui-1.30.jar -b -o " + outfileWithEnding
                + " -s" + " " + xmlFile + " " + sdfile;

        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "cdk");

        //Temporary thing; makes life easier for some users for bioactivity use case
        outfileWithEnding = outfileWithEnding.substring(outfileWithEnding.lastIndexOf("/") + 1);
        convertCdkToX(outfileWithEnding, workingDir + "/Descriptors/");
    }

    public void convertCdkToX(String cdkOutputFile, String workingDir) throws Exception {
        String cmd = "python " + Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH + "cdkToX.py " + cdkOutputFile +
                " " + cdkOutputFile + ".x";
        RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "cdkToX.py");

        // Any errors from MolconnZ processing will be in the log files. Read
        // 'em.
    }

    @Override
    public void readDescriptors(String xFile, List<String> descriptorNames,
                                        List<Descriptors> descriptorValueMatrix) throws Exception {

        String cdkXFile = xFile + ".x";
        File file = new File(cdkXFile);

        //generate x file for cdk if it doesn't exist already
        if (!file.exists()) {
            File sdfFile = new File(xFile);
            if (!sdfFile.exists() || sdfFile.length() == 0) {
                logger.error(xFile + ": sdf file not found");
                throw new Exception("Could not read sdf file descriptors for cdk to x file operation: " + xFile + "\n");
            }
             convertCdkToX(xFile, sdfFile.getParent());
            file = new File(cdkXFile);
        }

        logger.debug("Trying to read uploaded descriptors");
        if (!file.exists() || file.length() == 0) {
            logger.error(cdkXFile + ": cdkXFile not found");
            throw new Exception("Could not read X file descriptors: " + cdkXFile + "\n");
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
                Descriptors di = new Descriptors();
                if (tok.hasNext()) {
                    di.setCompoundIndex(Integer.parseInt(tok.next())); // first value is the index of the compound
                }
                if (tok.hasNext()) {
                    di.setCompoundName(tok.next()); // second value is the name of the compound
                }
                List<Double> descriptorValues = new ArrayList<>();
                while (tok.hasNextDouble()) {
                    descriptorValues.add(tok.nextDouble());
                }
                if (!descriptorValues.isEmpty()) {
                    di.setDescriptorValues(descriptorValues);
                    descriptorValueMatrix.add(di);
                }
                tok.close();
            }
            br.close();
        } catch (FileNotFoundException e) {
            logger.error(file + ": File not found");
        }
    }

    @Override
    public String checkDescriptors(String cdkOutputFile) throws Exception {
        // check if CDK file exists, then look for NA's.
        String errors = "";

        File file = new File(cdkOutputFile + getFileEnding());
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
}
