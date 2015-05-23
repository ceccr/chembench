package edu.unc.ceccr.chembench.workflows.visualization;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class SdfToJpg {

    private static Logger logger = Logger.getLogger(SdfToJpg.class.getName());

    public static void makeSketchFiles(String filePath, String fileName, String structuresDir, String sketchesDir)
            throws Exception {
        String command =
                String.format("molconvert -Y -g -m jpeg:w300,Q95 %s -o %s", Paths.get(filePath, fileName).toString(),
                        Paths.get(filePath, sketchesDir, "i.jpg").toString());
        RunExternalProgram.runCommandAndLogOutput(command, filePath, "molconvertLog");

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
        List<String> compoundNames = Lists.newArrayList();

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

        for (int i = 0; i < files.length; i++) {
            File source = new File(sketchesDir, "i" + (i + 1) + ".jpg");
            File destination = new File(sketchesDir, compoundNames.get(i) + ".jpg");
            source.renameTo(destination);
        }
    }
}
