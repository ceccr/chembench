package edu.unc.ceccr.chembench.workflows.descriptors;


import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class DescriptorDragonH extends DescriptorCommonDragon implements DescriptorSet {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorDragonH.class);

    public DescriptorDragonH(){

    }

    @Override
    public String getDescriptorSet() {
        return Constants.DRAGONH;
    }

    @Override
    public String getFileEnding() {
        return ".dragonH";
    }

    @Override
    public String getFileErrorOut() {
        return "dragonH.out";
    }

    @Override
    public void generateDescriptors(String sdfile, String outfile) throws Exception {
        String outfileWithEnding = outfile + getFileEnding();
        String workingDir = outfileWithEnding.replaceAll("/[^/]+$", "") + "/";
        writeHExplicitDragonScriptFiles(sdfile, workingDir, outfileWithEnding);
        String execstr = "dragonX -s " + workingDir + "dragon-scriptH.txt";
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir, "dragonH");
    }

    private static void writeHExplicitDragonScriptFiles(String sdFile, String workingDir, String outfile)
            throws IOException {
        //also used for descriptor generation for prediction sets.

        logger.debug("Writing Dragon scripts for " + sdFile + " into " + workingDir);

        FileOutputStream fout;
        PrintStream out;
        try {
            fout = new FileOutputStream(workingDir + "dragon-scriptH.txt");
            out = new PrintStream(fout);

            out.println("DRAGON script Ver 2");
            out.println("/d GetB1 All /PCno");
            out.println("/d GetB2 All /PCno");
            out.println("/d GetB3 All /PCno");
            out.println("/d GetB4 All /PCno");
            out.println("/d GetB5 All /PCno");
            out.println("/d GetB6 All /PCno");
            out.println("/d GetB7 All /PCno");
            out.println("/d GetB8 All /PCno");
            out.println("/d GetB9 All /PCno");
            out.println("/d GetB10 All /PCno");
            out.println("/d GetB11 None /PCno"); //blocks 11-16 and 19 are 3D descriptors; we only use 2D on Chembench.
            out.println("/d GetB12 None /PCno");
            out.println("/d GetB13 None /PCno");
            out.println("/d GetB14 None /PCno");
            out.println("/d GetB15 None /PCno");
            out.println("/d GetB16 None /PCno");
            out.println("/d GetB17 All /PCno");
            out.println("/d GetB18 All /PCno");
            out.println("/d GetB19 None /PCno");
            out.println("/d GetB20 All /PCno");
            out.println("/d GetB21 All /PCno");
            out.println("/d GetB22 All /PCno");
            out.println("/fm molfile -f4 -i2 -Hy -2D");
            out.println("/fy None");
            out.println("/fo " + outfile + " -f1 -k -m -999");
            out.close();
            fout.close();

        } catch (IOException e) {
            logger.error("", e);
        }
        try {
            fout = new FileOutputStream(workingDir + "molfile");
            out = new PrintStream(fout);
            out.println(sdFile);
            out.println("");
            out.close();
            fout.close();

        } catch (IOException e) {
            logger.error("", e);
        }
    }
}
