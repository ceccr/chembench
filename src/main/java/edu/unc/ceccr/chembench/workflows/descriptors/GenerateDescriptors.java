package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Scanner;

public class GenerateDescriptors {

    private static Logger logger = Logger.getLogger(GenerateDescriptors.class.getName());

    public static void GenerateMolconnZDescriptors(String sdfile, String outfile) throws Exception {
        //Given an SD file, run MolconnZ to get the chemical descriptors for each compound.
        String datFile;

        //datFile = Constants.MOLCONNZ_CSV_DATFILE_PATH;
        datFile = Constants.MOLCONNZ_PREDICTION_DATFILE_PATH;

        String execstr = "molconnz " + Constants.CECCR_BASE_PATH + datFile + " " + sdfile + " " + outfile;
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "molconnz");
    }

    public static void GenerateCDKDescriptors(String sdfile, String outfile) throws Exception {
        //Given an SD file, run CDK to get the chemical descriptors for each compound.
        String xmlFile = Constants.CECCR_BASE_PATH + Constants.CDK_XMLFILE_PATH;

        String execstr = "java -jar " + Constants.EXECUTABLEFILE_PATH + "CDKGui-1.30.jar -b -o " + outfile + " -s " +
                xmlFile + " " + sdfile;

        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "cdk");

        //Temporary thing; makes life easier for some users for bioactivity use case
        outfile = outfile.substring(outfile.lastIndexOf("/") + 1);
        ReadDescriptors.convertCDKToX(outfile, workingDir + "/Descriptors/");
    }

    public static void GenerateISIDADescriptors(String sdfile, String outfile) throws Exception {
        //Given an SD file, run ISIDA to get the chemical descriptors for each compound
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        String execstr = "Fragmentor" + " -i " + sdfile + " -o " + outfile + " -t 0" +
                " -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "ISIDA");

        //Transform sdf.ISIDA.svm to sdf.ISIDA which is in the format of CDK, DrangonH, etc.
        try {
            //Add a name tag for each compound to ISIDA
            String hdr = outfile + ".hdr";
            String svm = outfile + ".svm";

            FileWriter fout = new FileWriter(outfile);

            //Add fragments
            File hdrFile = new File(hdr);
            FileReader fin = new FileReader(hdrFile);
            Scanner src = new Scanner(fin);
            int num = 0;

            fout.write("Title ");

            while (src.hasNext()) {
                StringBuilder sb = new StringBuilder();
                num++;
                String frg = src.nextLine();
                String title = "";
                int i = 0;
                while (i < frg.length()) {
                    if (frg.charAt(i) != '.') {
                        i++;
                    } else {
                        break;
                    }
                }
                title = num + "|";
                title += frg.substring(i + 1).trim();
                title.trim();
                sb.append(title);
                sb.append(" ");
                fout.write(sb.toString());
            }

            //Add compound matrix
            File svmFile = new File(svm);
            FileReader finSVM = new FileReader(svmFile);
            Scanner srcSVM = new Scanner(finSVM);

            while (srcSVM.hasNext()) {
                StringBuilder matrix = new StringBuilder();
                String compoundLine = srcSVM.nextLine();
                int counter = 0;
                String[] parts = compoundLine.split(" ");

                //Add compound name
                matrix.append(parts[counter]);
                matrix.append(" ");
                counter++;

                //Initialize fragment values
                String[] frgValues = new String[num];
                for (int i = 0; i < num; i++) {
                    frgValues[i] = "0";
                }

                //Replace with values
                while (counter < parts.length) {
                    String[] numberAndTimes = parts[counter].split(":", 2);
                    String number = numberAndTimes[0];
                    int numberInt = Integer.parseInt(number);
                    String times = numberAndTimes[1];
                    frgValues[numberInt - 1] = times;
                    counter++;
                }

                for (int j = 0; j < num; j++) {
                    matrix.append(frgValues[j]);
                    matrix.append(" ");
                }

                fout.write("\n");
                fout.write(matrix.toString());
            }

            fout.close();
            src.close();
            fin.close();
            srcSVM.close();
            finSVM.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public static void GenerateISIDADescriptorsWithHeader(String sdfile, String outfile, String headerFile)
            throws Exception {
        //Given an SD file, run ISIDA to get the chemical descriptors for each compound with the .hdr from predictor
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        String execstr = "Fragmentor" + " -i " + sdfile + " -o " + outfile + " -t 0" +
                " -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name " + "-h " + headerFile + " --StrictFrg";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "ISIDA");

        //Transform sdf.ISIDA.svm to sdf.ISIDA which is in the format of CDK, DrangonH, etc.
        try {
            //Add a name tag for each compound to ISIDA
            String hdr = outfile + ".hdr";
            String svm = outfile + ".svm";

            FileWriter fout = new FileWriter(outfile);

            //Add fragments
            File hdrFile = new File(hdr);
            FileReader fin = new FileReader(hdrFile);
            Scanner src = new Scanner(fin);
            int num = 0;

            fout.write("Title ");

            while (src.hasNext()) {
                StringBuilder sb = new StringBuilder();
                num++;
                String frg = src.nextLine();
                String title = "";
                int i = 0;
                while (i < frg.length()) {
                    if (frg.charAt(i) != '.') {
                        i++;
                    } else {
                        break;
                    }
                }
                title = num + "|";
                title += frg.substring(i + 1).trim();
                title.trim();
                sb.append(title);
                sb.append(" ");
                fout.write(sb.toString());
            }

            //Add compound matrix
            File svmFile = new File(svm);
            FileReader finSVM = new FileReader(svmFile);
            Scanner srcSVM = new Scanner(finSVM);

            while (srcSVM.hasNext()) {
                StringBuilder matrix = new StringBuilder();
                String compoundLine = srcSVM.nextLine();
                int counter = 0;
                String[] parts = compoundLine.split(" ");

                //Add compound name
                matrix.append(parts[counter]);
                matrix.append(" ");
                counter++;

                //Initialize fragment values
                String[] frgValues = new String[num];
                for (int i = 0; i < num; i++) {
                    frgValues[i] = "0";
                }

                //Replace with values
                while (counter < parts.length) {
                    String[] numberAndTimes = parts[counter].split(":", 2);
                    String number = numberAndTimes[0];
                    int numberInt = Integer.parseInt(number);
                    String times = numberAndTimes[1];
                    frgValues[numberInt - 1] = times;
                    counter++;
                }

                for (int j = 0; j < num; j++) {
                    matrix.append(frgValues[j]);
                    matrix.append(" ");
                }

                fout.write("\n");
                fout.write(matrix.toString());
            }

            fout.close();
            src.close();
            fin.close();
            srcSVM.close();
            finSVM.close();

        } catch (Exception e) {//Catch exception if any
            logger.error(e);
        }
    }

    public static void GenerateHExplicitDragonDescriptors(String sdfile, String outfile) throws Exception {
        String workingDir = outfile.replaceAll("/[^/]+$", "") + "/";
        writeHExplicitDragonScriptFiles(sdfile, workingDir, outfile);

        String execstr =
                // "/usr/local/ceccr/dragon/dragonX -s "
                "dragonX -s " + workingDir + "dragon-scriptH.txt";
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir, "dragonH");
    }

    public static void GenerateHDepletedDragonDescriptors(String sdfile, String outfile) throws Exception {
        String workingDir = outfile.replaceAll("/[^/]+$", "") + "/";
        writeHDepletedDragonScriptFiles(sdfile, workingDir, outfile);

        String execstr =
                // "/usr/local/ceccr/dragon/dragonX -s "
                "dragonX -s " + workingDir + "dragon-scriptNoH.txt";
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
            logger.error(e);
        }
        try {
            fout = new FileOutputStream(workingDir + "molfile");
            out = new PrintStream(fout);
            out.println(sdFile);
            out.println("");
            out.close();
            fout.close();

        } catch (IOException e) {
            logger.error(e);
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
            logger.error(e);
        }
        try {
            fout = new FileOutputStream(workingDir + "molfile");
            out = new PrintStream(fout);
            out.println(sdFile);
            out.println("");
            out.close();
            fout.close();

        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void GenerateMoe2DDescriptors(String sdfile, String outfile) throws Exception {
        //command: "moe2D.sh infile.sdf outfile.moe2D"
        String execstr = "moe2D.sh " + " " + sdfile + " " + outfile + " " + Constants.CECCR_BASE_PATH +
                "mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "moe2d.sh");
    }

    public static void GenerateMaccsDescriptors(String sdfile, String outfile) throws Exception {
        //command: "maccs.sh infile.sdf outfile.maccs"
        String execstr = "maccs.sh " + sdfile + " " + outfile + " " + Constants.CECCR_BASE_PATH +
                "mmlsoft/SVL_DIR/batch_sd_MACCSFP.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "maccs.sh");
    }

}
