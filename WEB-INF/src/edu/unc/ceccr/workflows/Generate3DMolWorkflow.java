package edu.unc.ceccr.workflows;

import edu.unc.ceccr.utilities.Utility;

public class Generate3DMolWorkflow {

	public static void Convert2Dto3D(String userName, String jobName, String fileName, String outFileName, String workingDir)throws Exception
	{
		//We have a Visualization/Structures directory, filled with single-compound 2D SDFs.
		//We need 3D mol files in order to visualize them.
		//So, this function will convert a 2D SDF to a 3D mol file on demand.
		String command = "molconvert -3:S{fast} mol " + workingDir + fileName + " -o " + workingDir + outFileName;
		Utility.writeToDebug("Running External Program: " + command, userName, jobName);
		Process p = Runtime.getRuntime().exec(command);
		Utility.writeProgramLogfile(workingDir, "molconvert_3D", p.getInputStream(), p.getErrorStream());
		p.waitFor();
        Utility.close(p.getOutputStream());
        Utility.close(p.getInputStream());
        Utility.close(p.getErrorStream());
        p.destroy();
				
	}
}