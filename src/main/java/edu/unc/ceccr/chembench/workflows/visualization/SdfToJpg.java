package edu.unc.ceccr.chembench.workflows.visualization;

import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SdfToJpg {

    private static final Logger logger = LoggerFactory.getLogger(SdfToJpg.class);

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
        List<String> compoundNames = new ArrayList<>();

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
                        logger.error("", ex);
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
        logger.info("Converting 2d structures to 3d in " + structuresDir);
        Path dirPath = dir.toPath();
        for (int i = 0; i < files.length; i++) {
            File source = new File(sketchesDir, "i" + (i + 1) + ".jpg");
            File destination = new File(sketchesDir, compoundNames.get(i) + ".jpg");
            source.renameTo(destination);

            // convert 2d structures to 3d structures for the 3d compound viewer
            Path compound2d = dirPath.resolve(files[i]);
            Path compound3d = dirPath.resolve(files[i].replaceAll("\\.sdf$", "_3D.mol"));
            convert2Dto3D(compound2d, compound3d);
        }
    }

    public static void convert2Dto3D(Path inFilePath, Path outFilePath) {
        // We have a Visualization/Structures directory, filled with single-compound 2D SDFs.
        // We need 3D mol files in order to visualize them.
        // So, this function will convert a 2D SDF to a 3D mol file on demand.
        String command = String.format("molconvert -3:S{fast} mol \"%s\" -o \"%s\"", inFilePath.toString(),
                outFilePath.toString());
        RunExternalProgram.runCommandAndLogOutput(command, outFilePath.getParent(), "molconvert_3D");
    }
}
