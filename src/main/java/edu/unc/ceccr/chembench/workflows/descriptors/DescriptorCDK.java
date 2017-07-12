package edu.unc.ceccr.chembench.workflows.descriptors;


import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    @Override
    public void readDescriptors(String xFile, List<String> descriptorNames,
                                        List<Descriptors> descriptorValueMatrix) throws Exception {
        xFile += getFileEnding();
        String cdkXFile = xFile + ".x";

        //generate x file for cdk if it doesn't exist already
        if (!new File(cdkXFile).isFile()) {
            File cdkFile = new File(xFile);
            if (!cdkFile.exists() || cdkFile.length() == 0) {
                logger.error(xFile + ": cdk file not found");
                throw new Exception("Could not read cdk file descriptors for cdk to x file operation: " + xFile + "\n");
            }
            convertCdkToX(xFile, cdkFile.getParent());
        }

        readDescriptorFile(cdkXFile, descriptorNames, descriptorValueMatrix);
    }

    @Override
    public void readDescriptorsChunks(String xFile, List<String> descriptorNames,
                                      List<Descriptors> descriptorValueMatrix) throws Exception {

        readDescriptorFile(xFile, descriptorNames, descriptorValueMatrix);
    }

    @Override
    public String splitFile(String workingDir, String descriptorsFile) throws Exception {
        descriptorsFile += getFileEnding();
        convertCdkToX(workingDir + descriptorsFile, workingDir);
        descriptorsFile += ".x";

        File file = new File(workingDir + descriptorsFile);
        if (!file.exists() || file.length() == 0) {
            throw new Exception("Could not read UPLOADED descriptors.\n");
        }
        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        int currentFile = 0;
        int moleculesInCurrentFile = 0;
        BufferedWriter outFilePart =
                new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));

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
                outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
                outFilePart.write(descriptorNames);
            }
        }
        br.close();
        outFilePart.write("\n");
        outFilePart.close();

        return descriptorsFile;
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

    private void convertCdkToX(String cdkOutputFile, String workingDir) throws Exception {
        String cmd = "python " + Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH + "cdkToX.py " + cdkOutputFile +
                " " + cdkOutputFile + ".x";
        RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "cdkToX.py");

        // Any errors from MolconnZ processing will be in the log files. Read
        // 'em.
    }

    private void readDescriptorFile(String xFile, List<String> descriptorNames,
                                    List<Descriptors> descriptorValueMatrix) throws Exception{
        //read generated x file
        File file = new File(xFile);
        logger.debug("Trying to read uploaded descriptors");
        if (!file.exists() || file.length() == 0) {
            logger.error(xFile + ": xFile not found");
            throw new Exception("Could not read X file descriptors: " + xFile + "\n");
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
}
