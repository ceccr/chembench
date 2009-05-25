package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

public class GenerateDescriptorWorkflow{
	
	//Given an SD file, run MolconnZ to get the chemical descriptors for each compound.
	public static void GenerateMolconnZDescriptors(String sdfile, String outfile) throws Exception{
		  String execstr = "molconnz " + Constants.CECCR_BASE_PATH + Constants.MOLCONNZ_DATFILE_PATH + " " + sdfile + " " + sdfile + ".S";
		  String workingDir = sdfile.replaceAll("/[^/]+$", "");
	      Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr);
	      Utility.writeProgramLogfile(workingDir, "molconnz", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}
	
	//Given an SD file, run Dragon to get the chemical descriptors for each compound.
	public static void GenerateDragonDescriptors(String sdfile, String outfile) throws Exception{
		  //dragonX -s data/script_w_H.txt

		  String workingDir = sdfile.replaceAll("/[^/]+$", "") + "/";
		  
		  StandardizeMoleculesWorkflow.standardizeSdf(sdfile, sdfile + ".standardized", workingDir);
		  
		  writeDragonScriptFiles(sdfile + ".standardized", workingDir, outfile);
		  String execstr = "/usr/local/ceccr/dragon/dragonX -s " + workingDir + "dragon-script.txt";
			
	      Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr, null, new File(workingDir));
	      Utility.writeProgramLogfile(workingDir, "dragonX", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}	
	
	private static void writeDragonScriptFiles(String sdFile, String workingDir, String outfile) throws IOException {
		
		Utility.writeToDebug("Writing Dragon scripts for " + sdFile + " into " + workingDir);
		
		FileOutputStream fout;
		PrintStream out;
		try {
			fout = new FileOutputStream(workingDir + "dragon-script.txt");
			out = new PrintStream(fout);
			
			out.println("DRAGON script Ver 2");
			out.println("/d GetB1 All /PCno");
			out.println("/d GetB2 All /PCno");
			out.println("/d GetB3 All /PCno");
			out.println("/d GetB4 All /PCno");
			out.println("/d GetB5 All /PCno");
			out.println("/d GetB6 All /PCno");
			out.println("/d GetB7 All /PCno");
			out.println("/d GetB8 All /PCno");
			out.println("/d GetB9 All /PCno");
			out.println("/d GetB10 All /PCno");
			out.println("/d GetB11 None /PCno");
			out.println("/d GetB12 None /PCno");
			out.println("/d GetB13 None /PCno");
			out.println("/d GetB14 None /PCno");
			out.println("/d GetB15 None /PCno");
			out.println("/d GetB16 None /PCno");
			out.println("/d GetB17 All /PCno");
			out.println("/d GetB18 All /PCno");
			out.println("/d GetB19 None /PCno");
			out.println("/d GetB20 None /PCno");
			out.println("/d GetB21 None /PCno");
			out.println("/d GetB22 None /PCno");
			out.println("/fm molfile -f4 -i2 -Hy -2D");
			out.println("/fy None");
			out.println("/fo " + outfile + " -f1 -k -m -999");
			out.close();
			fout.close();
			
		} catch (IOException e) {
			Utility.writeToDebug(e);
		}	
		try {
			fout = new FileOutputStream(workingDir + "molfile");
			out = new PrintStream(fout);
			out.println(sdFile);
			out.println("");
			out.close();
			fout.close();
			
		} catch (IOException e) {
			Utility.writeToDebug(e);
		}	
	}

	public static void GenerateMoe2DDescriptors(String sdfile, String outfile) throws Exception{
		//command: "moe2D.sh infile.sdf outfile.moe2D"
		String execstr = "moe2D.sh " + " " + sdfile + " " + sdfile + ".moe2D";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir, "moe2d.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	}
	
	public static void GenerateMaccsDescriptors(String sdfile, String outfile) throws Exception{
		//command: "maccs.sh infile.sdf outfile.maccs"
		String execstr = "maccs.sh " + sdfile + " " + sdfile + ".maccs";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir, "maccs.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	}
	
}