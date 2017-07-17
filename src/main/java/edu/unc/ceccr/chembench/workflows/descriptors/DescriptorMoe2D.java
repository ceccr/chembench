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

public class DescriptorMoe2D implements DescriptorSet {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorMoe2D.class);

    @Override
    public String getDescriptorSetName() {
        return Constants.MOE2D;
    }

    @Override
    public String getFileEnding() {
        return ".moe2D";
    }

    @Override
    public String getFileErrorOut() {
        return "moe2d.out";
    }

    @Override
    public void generateDescriptors(String sdfile, String outfile) throws Exception {
        //command: "moe2D.sh infile.sdf outfile.moe2D"
        String execstr = "moe2D.sh " + " " + sdfile + " " + outfile + getFileEnding() + " " + Constants
                .CECCR_BASE_PATH + "mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "moe2d.sh");
    }

    @Override
    public void readDescriptors(String moe2DOutputFile, List<String> descriptorNames,
                                            List<Descriptors> descriptorValueMatrix) throws Exception {
        moe2DOutputFile +=getFileEnding();
        readDescriptorFile (moe2DOutputFile, descriptorNames, descriptorValueMatrix);
    }

    @Override
    public void readDescriptorsChunks(String outputFile, List<String> descriptorNames,
                                      List<Descriptors> descriptorValueMatrix) throws Exception {
        readDescriptorFile (outputFile, descriptorNames, descriptorValueMatrix);
    }

    @Override
    public String splitFile(String workingDir, String descriptorsFile) throws Exception {
        descriptorsFile += getFileEnding();

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
        BufferedWriter outFilePart =
                new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));

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
                outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
                outFilePart.write(header);
            }
        }
        br.close();
        outFilePart.write("\n");
        outFilePart.close();

        return descriptorsFile;
    }

    @Override
    public String checkDescriptors(String moe2DOutputFile) throws Exception {
        // right now this doesn't check anything. The MOE2D descriptors never
        // seem to cause issues.
        String errors = "";
        moe2DOutputFile += getFileEnding();
        File file = new File(moe2DOutputFile + getFileEnding());
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

    private void readDescriptorFile (String outputFile, List<String> descriptorNames, List<Descriptors>
            descriptorValueMatrix) throws Exception{
        logger.debug("reading Moe2D Descriptors");
        File file = new File(outputFile);
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
            List<Double> descriptorValues = new ArrayList<>();
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
                descriptorValues.add(Double.parseDouble(val));
            }
            if (!descriptorValues.isEmpty()) {
                Descriptors di = new Descriptors();
                di.setDescriptorValues(descriptorValues);
                descriptorValueMatrix.add(di);
            }
            tok.close();
        }
        br.close();
    }
}
