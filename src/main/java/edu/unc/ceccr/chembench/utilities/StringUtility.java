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
        if (!filename.isEmpty()) {
            filename = filename.replaceAll(" ", "_");
            filename = filename.replaceAll("\\(", "_");
            filename = filename.replaceAll("\\)", "_");
        }
        return filename;
    }

    public static String checkAndCorrectXFileName(String xFileName){
        if (!xFileName.isEmpty()){
            if (!xFileName.endsWith(".x")) {
                xFileName += ".x";
            }
        }
        return xFileName;
    }

    public static String checkAndCorrectACTFileName(String actFileName){
        if (!actFileName.isEmpty()) {
            if (actFileName.endsWith(".a")) {
                actFileName = actFileName.substring(0, actFileName.length() - 2) + ".act";
            } else if (!actFileName.endsWith(".act")) {
                actFileName += ".act";
            }
        }
        return actFileName;
    }
}
