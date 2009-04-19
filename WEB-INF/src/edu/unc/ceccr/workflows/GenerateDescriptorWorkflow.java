package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;


public class GenerateDescriptorWorkflow{
	
	//Given an SD file, run MolconnZ to get the chemical descriptors for each compound.
	public static void GenerateMolconnZDescriptors(String sdfile, String outfile){

		try {
			//make /usr/local/chemb/ParameterFiles a global, please
		  String execstr = "molconnz " + Constants.MOLCONNZ_DATFILE_PATH + " " + sdfile + " " + sdfile + ".S";
		  String workingDir = sdfile.replaceAll("/[^/]+$", "");
	      Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr);
	      Utility.writeProgramLogfile(workingDir, "molconnz", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	      
	    }
	    catch (Exception ex) {
	      Utility.writeToDebug(ex);
	    }
	}
	
}