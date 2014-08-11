package edu.unc.ceccr.utilities;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class StandardizeSdfFormat {
    private static Logger logger = Logger.getLogger(StandardizeSdfFormat.class.getName());

    public static void addNameTag(String userName, String jobName, String sdfFile, String outputSdfFile) {

        try {
            //add a name tag for each compound to ISIDA
            logger.debug("User: " + userName + "Job: " + jobName + " Add name tags to SDF: " + sdfFile);

            File file = new File(sdfFile);
            FileReader fin = new FileReader(file);
            File ouputFile = new File(outputSdfFile);
            FileWriter fout = new FileWriter(outputSdfFile);
            Scanner src = new Scanner(fin);

            while (src.hasNext()) {
                StringBuilder sb = new StringBuilder();
                String title = src.nextLine();
                sb.append(title);
                String temp;
                while (src.hasNext()) {
                    temp = src.nextLine();
                    if (!temp.startsWith("$")) {
                        sb.append("\n" + temp);
                    } else {
                        try {
                            sb.append("\n" + "> <Chembench_Name>");
                            sb.append("\n" + title);
                            sb.append("\n");
                            sb.append("\n" + temp + "\n");
                            fout.write(sb.toString());
                        } catch (Exception ex) {
                        }
                        break;
                    }
                }
            }
            fout.close();
            src.close();
            fin.close();

            // replace old SDF with new SDF
            FileAndDirOperations.copyFile(outputSdfFile, sdfFile);
            FileAndDirOperations.deleteFile(outputSdfFile);

        } catch (Exception e) {//Catch exception if any
            logger.error("User: " + userName + "Job: " + jobName + e);
        }
    }
}