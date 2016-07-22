package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GenerateDescriptors {

    private static final Logger logger = LoggerFactory.getLogger(GenerateDescriptors.class);

    public static void generateCdkDescriptors(String sdfile, String outfile) throws Exception {
        //Given an SD file, run CDK to get the chemical descriptors for each compound.
        String xmlFile = Constants.CECCR_BASE_PATH + Constants.CDK_XMLFILE_PATH;

        String execstr = "java -jar " + Constants.EXECUTABLEFILE_PATH + "CDKGui-1.30.jar -b -o " + outfile + " -s " +
                xmlFile + " " + sdfile;

        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "cdk");

        //Temporary thing; makes life easier for some users for bioactivity use case
        outfile = outfile.substring(outfile.lastIndexOf("/") + 1);
        ReadDescriptors.convertCdkToX(outfile, workingDir + "/Descriptors/");
    }

    public static void generateIsidaDescriptors(String sdfile, String outfile) {
        //Given an SDF file, run ISIDA to get the chemical descriptors for each compound
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        String execstr = "Fragmentor" + " -i " + sdfile + " -o " + outfile + " -t 0" +
                " -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "ISIDA");
    }

    public static void generateIsidaDescriptorsWithHeader(String sdfile, String outfile, String headerFile) {
        //Given an SDF file, run ISIDA to get the chemical descriptors for each compound with the .hdr from predictor
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        String execstr = "Fragmentor" + " -i " + sdfile + " -o " + outfile + " -t 0" +
                " -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name " + "-h " + headerFile + " --StrictFrg";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "ISIDA");
    }

    public static void generateHExplicitDragonDescriptors(String sdfile, String outfile) throws Exception {
        String workingDir = outfile.replaceAll("/[^/]+$", "") + "/";
        writeHExplicitDragonScriptFiles(sdfile, workingDir, outfile);
        String execstr = "dragonX -s " + workingDir + "dragon-scriptH.txt";
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir, "dragonH");
    }

    public static void generateHDepletedDragonDescriptors(String sdfile, String outfile) throws Exception {
        String workingDir = outfile.replaceAll("/[^/]+$", "") + "/";
        writeHDepletedDragonScriptFiles(sdfile, workingDir, outfile);
        String execstr = "dragonX -s " + workingDir + "dragon-scriptNoH.txt";
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir, "dragonNoH");
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

    private static void writeHDepletedDragonScriptFiles(String sdFile, String workingDir, String outfile)
            throws IOException {

        logger.debug("Writing Dragon scripts for " + sdFile + " into " + workingDir);

        FileOutputStream fout;
        PrintStream out;
        try {
            fout = new FileOutputStream(workingDir + "dragon-scriptNoH.txt");
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
            out.println("/d GetB11 None /PCno");
            out.println("/d GetB12 None /PCno");
            out.println("/d GetB13 None /PCno");
            out.println("/d GetB14 None /PCno");
            out.println("/d GetB15 None /PCno");
            out.println("/d GetB16 None /PCno");
            out.println("/d GetB17 All /PCno");
            out.println("/d GetB18 All /PCno");
            out.println("/d GetB19 None /PCno");
            out.println("/d GetB20 None /PCno");
            out.println("/d GetB21 None /PCno");
            out.println("/d GetB22 None /PCno");
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

    public static void generateMoe2DDescriptors(String sdfile, String outfile) throws Exception {
        //command: "moe2D.sh infile.sdf outfile.moe2D"
        String execstr = "moe2D.sh " + " " + sdfile + " " + outfile + " " + Constants.CECCR_BASE_PATH +
                "mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "moe2d.sh");
    }

    public static void generateMaccsDescriptors(String sdfile, String outfile) throws Exception {
        //command: "maccs.sh infile.sdf outfile.maccs"
        String execstr = "maccs.sh " + sdfile + " " + outfile + " " + Constants.CECCR_BASE_PATH +
                "mmlsoft/SVL_DIR/batch_sd_MACCSFP.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "maccs.sh");
    }

    public static void generateDragon7Descriptors(String sdfFile, String outFile) throws DescriptorGenerationException {
        String scriptFilePath = Paths.get(Constants.CECCR_BASE_PATH, Constants.DRAGON7_SCRIPT_PATH).toString();
        String execstr = Utility.SPACE_JOINER.join(new String[]{"dragon7",
                "-s", scriptFilePath,
                "<", sdfFile
        });
        Path sdfFilePath = Paths.get(sdfFile);
        Path descriptorsDirPath = sdfFilePath.getParent().resolve("Descriptors");
        RunExternalProgram.runCommandAndLogOutput(execstr, descriptorsDirPath.toString() + "/", "dragon7");

        // dragon7 will write out descriptor output to dragon7.log and its log to dragon7.err;
        // rename dragon7.log to the descriptor file (<sdf_name>.dragon7) and dragon7.err to dragon7.log
        // (in that order, or you'll overwrite the file! need to use REPLACE_EXISTING if execution failed before)
        try {
            Files.move(descriptorsDirPath.resolve("Logs").resolve("dragon7.log"), Paths.get(outFile), REPLACE_EXISTING);
            Files.move(descriptorsDirPath.resolve("Logs").resolve("dragon7.err"),
                    descriptorsDirPath.resolve("Logs").resolve("dragon7.log"), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DescriptorGenerationException("Dragon 7 descriptor generation failed", e);
        }
    }
}
