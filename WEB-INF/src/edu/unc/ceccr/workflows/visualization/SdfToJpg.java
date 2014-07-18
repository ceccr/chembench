package edu.unc.ceccr.workflows.visualization;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SdfToJpg {

    private static Logger logger = Logger.getLogger(SdfToJpg.class.getName());

    public static void makeSketchFiles(String filePath, String fileName, String structuresDir,
                                       String sketchesDir) throws Exception {
        //filePath = directory the SDF is in, e.g. workflow-users/theo/DATASETS/
        //fileName = name of sdfile, like anticonv_91.sdf
        //structuresDir = subdirectory for structures, e.g. Visualization/Structures/
        //sketchesDir = subdirectory for sketches, e.g. Visualization/Sketches/

        String command = Constants.CECCR_BASE_PATH + "jchem/bin/molconvert -Y -g -2 jpeg:w300," +
                "Q95 " + filePath + fileName + " -o " + filePath + sketchesDir + "i.jpg -m";
        RunExternalProgram.runCommandAndLogOutput(command, filePath, "molconvertLog");

        //remove explicit hydrogens from SDFs; they are noise as far as the JPG is concerned.
        String execstr1 = "removeExplicitH.sh " + filePath + fileName + " " + filePath + fileName + ".removedH";
        RunExternalProgram.runCommandAndLogOutput(execstr1, filePath, "removeExplicitH");

        fileName += ".removedH";

        //Split the input SDF (lots of compounds) into separate SDFs (1 compound each).
        //Put that in the Structures dir.
        structuresDir = filePath + structuresDir;
        sketchesDir = filePath + sketchesDir;
        logger.debug("Creating structures into dir: " + structuresDir);

        //make sure Structures dir exists.
        File stDir = new File(structuresDir);
        if (!stDir.exists()) {
            stDir.mkdirs();
        }

        File file;
        file = new File(filePath + fileName);
        FileReader fin = new FileReader(file);
        Scanner src = new Scanner(fin);
        List<String> compoundNames = new ArrayList<String>();

        while (src.hasNext()) {
            StringBuilder sb = new StringBuilder();
            String title = src.nextLine();
            sb.append(title);
            String temp;
            while (src.hasNext()) {
                temp = src.nextLine();
                sb.append("\n" + temp);
                if (temp.startsWith("$")) {
                    try {
                        FileWriter fout = new FileWriter(structuresDir + title.trim() + ".sdf");
                        compoundNames.add(title.trim());
                        fout.write(sb.toString());
                        fout.close();
                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                    break;
                }
            }
        }
        src.close();
        fin.close();

        logger.debug("Done creating structures. COMPOUND NAMES COUNT:" + compoundNames.size());

        //Done generating Structures files.
        //look in Structures directory.
        //for each .sdf file in Structures/, create a .jpg file in Sketches/.

        //make sure Sketches dir exists.
        File skDir = new File(sketchesDir);
        if (!skDir.exists()) {
            skDir.mkdirs();
        }

        File dir = new File(structuresDir);
        String files[] = dir.list();
        if (files == null) {
            logger.warn("Error reading Structures directory: " + structuresDir);
        }

        File molconvertErr = new File(filePath + "Logs/" + "molconvertLog.err");
        //waiting for molconvert to finish execution
        while (new File(sketchesDir).list().length < compoundNames.size()) {
            //wait for a sec
            Thread.sleep(1000);
            //check if error log file is empty
            //if not then stop loop execution
            if (molconvertErr.exists()) {
                FileInputStream fis = new FileInputStream(molconvertErr);
                int b = fis.read();
                if (b != -1) {
                    logger.warn("----Error occured while creating compound sketches!");
                    FileAndDirOperations.deleteDirContents(sketchesDir);
                    fis.close();
                    break;
                } else {
                    continue;
                }
            }
        }


        logger.debug("DIR size::" + new File(sketchesDir).list().length);
        String from;
        for (int i = 0; i < files.length; i++) {
            String jpgFilename = files[i].replace("sdf", "jpg");

            //only make the JPG if it's not already there
            if (!new File(sketchesDir + jpgFilename).exists() && new File(structuresDir + files[i]).exists()) {
                //command = "mv "+from+" "+to;
                //RunExternalProgram.runCommand(command, "");
                from = sketchesDir + "i" + (i + 1) + ".jpg";
                new File(from).renameTo(new File(sketchesDir + compoundNames.get(i) + ".jpg"));
                new File(from).delete();

            }

        }
        logger.debug("Done creating sketches. ");

    }
}