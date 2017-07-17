package edu.unc.ceccr.chembench.utilities;


public class StringUtility {

    public static String jobNameRegExCleanUp(String jobName){
        if (jobName != null) {
            jobName = jobName.replaceAll("\\s+", "_");
            jobName = jobName.replaceAll("\\(", "_");
            jobName = jobName.replaceAll("\\)", "_");
            jobName = jobName.replaceAll("\\[", "_");
            jobName = jobName.replaceAll("\\]", "_");
            jobName = jobName.replaceAll("/", "_");
            jobName = jobName.replaceAll("&", "_");
        }

        return jobName;
    }

    public static String fileNameRegExCleanUp(String filename){
        filename = filename.replaceAll(" ", "_");
        filename = filename.replaceAll("\\(", "_");
        filename = filename.replaceAll("\\)", "_");

        return filename;
    }

}
