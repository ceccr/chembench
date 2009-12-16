package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

public class GenerateDescriptorWorkflow{
	
	//Given an SD file, run MolconnZ to get the chemical descriptors for each compound.
	public static void GenerateMolconnZDescriptors(String sdfile, String outfile, String taskType) throws Exception{
		String datFile;
		if(taskType.equalsIgnoreCase(Constants.PREDICTION)){		
			datFile = Constants.MOLCONNZ_PREDICTION_DATFILE_PATH;
		}
		else{
			datFile = Constants.MOLCONNZ_MODELING_DATFILE_PATH;
		}
		String execstr = "molconnz " + Constants.CECCR_BASE_PATH + datFile + " " + sdfile + " " + sdfile + ".mz";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir, "molconnz", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	
	}
	
	//Given an SD file, run Dragon to get the chemical descriptors for each compound.
	public static void GenerateDragonDescriptors(String sdfile, String outfile, String taskType) throws Exception{
		  //dragonX -s data/script_w_H.txt

		  String workingDir = sdfile.replaceAll("/[^/]+$", "") + "/";
		  
		  if(taskType.equalsIgnoreCase(Constants.MODELING)){
			  writeChrisModelingDragonScriptFiles(sdfile, workingDir, outfile);
		  }
		  else{
			  writeExplicitHDragonScriptFiles(sdfile, workingDir, outfile);
		  }
		  
		  String execstr = "/usr/local/ceccr/dragon/dragonX -s " + workingDir + "dragon-script.txt";
			
	      Utility.writeToDebug("Running external program: " + execstr);
	      Process p = Runtime.getRuntime().exec(execstr, null, new File(workingDir));
	      Utility.writeProgramLogfile(workingDir, "dragonX", p.getInputStream(), p.getErrorStream());
	      p.waitFor();
	}	
	
	private static void writeExplicitHDragonScriptFiles(String sdFile, String workingDir, String outfile) throws IOException {
		//also used for descriptor generation for prediction sets.
		
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
			out.println("/d GetB11 All /PCno");
			out.println("/d GetB12 All /PCno");
			out.println("/d GetB13 All /PCno");
			out.println("/d GetB14 None /PCno"); //these are 3D descriptors; we only use 2D on Chembench.
			out.println("/d GetB15 All /PCno");
			out.println("/d GetB16 All /PCno");
			out.println("/d GetB17 All /PCno");
			out.println("/d GetB18 All /PCno");
			out.println("/d GetB19 All /PCno");
			out.println("/d GetB20 All /PCno");
			out.println("/d GetB21 All /PCno");
			out.println("/d GetB22 All /PCno");
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

	private static void writeHDepletedDragonScriptFiles(String sdFile, String workingDir, String outfile) throws IOException {

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
			out.println("/fm molfile -f4 -i2 -Hn -2D");
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
	
	private static void writeChrisModelingDragonScriptFiles(String sdFile, String workingDir, String outfile) throws IOException {
		
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
		String execstr = "moe2D.sh " + " " + sdfile + " " + sdfile + ".moe2D" + " " + Constants.CECCR_BASE_PATH + "mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir, "moe2d.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	}
	
	public static void GenerateMaccsDescriptors(String sdfile, String outfile) throws Exception{
		//command: "maccs.sh infile.sdf outfile.maccs"
		String execstr = "maccs.sh " + sdfile + " " + sdfile + ".maccs" + " " + Constants.CECCR_BASE_PATH + "mmlsoft/SVL_DIR/batch_sd_MACCSFP.svl";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir, "maccs.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	}
	
}