package edu.unc.ceccr.workflows.visualization;

import edu.unc.ceccr.utilities.RunExternalProgram;

public class Molecule3D {

    public static void Convert2Dto3D(String userName, String jobName, String fileName, String outFileName,
                                     String workingDir) throws Exception {
        //We have a Visualization/Structures directory, filled with single-compound 2D SDFs.
        //We need 3D mol files in order to visualize them.
        //So, this function will convert a 2D SDF to a 3D mol file on demand.
        String command = "molconvert -3:S{fast} mol \"" + workingDir + fileName + "\" -o \"" + workingDir +
                outFileName + "\"";
        RunExternalProgram.runCommandAndLogOutput(command, workingDir, "molconvert_3D");
    }
}