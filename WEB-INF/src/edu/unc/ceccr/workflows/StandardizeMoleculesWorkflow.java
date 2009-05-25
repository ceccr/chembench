package edu.unc.ceccr.workflows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

public class StandardizeMoleculesWorkflow {
	
	public static void standardizeSdf(String sdfIn, String sdfOut, String workingDir) throws Exception{
		//Standardizes the molecules in this sdfile. Necessary to do this before running DRAGON
		//descriptor generation. Also important to do this to any molecules that could go into our database.
		
		String execstr1 = "standardize -c \"dearomatize..aromatize..tautomerize..addExplicitH..clean:full\" -f sdf:-a -o " + sdfOut + " " + sdfIn;
		Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingDir);
		Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingDir));
		Utility.writeProgramLogfile(workingDir, "standardize", p.getInputStream(), p.getErrorStream());
		p.waitFor();
  	}
}