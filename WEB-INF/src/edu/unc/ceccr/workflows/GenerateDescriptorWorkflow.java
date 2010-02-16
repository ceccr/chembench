package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

public class GenerateDescriptorWorkflow{
	
	public static void GenerateMolconnZDescriptors(String sdfile, String outfile) throws Exception{
		//Given an SD file, run MolconnZ to get the chemical descriptors for each compound.
		String datFile;
		
		datFile = Constants.MOLCONNZ_PREDICTION_DATFILE_PATH;
		
		String execstr = "molconnz " + Constants.CECCR_BASE_PATH + datFile + " " + sdfile + " " + outfile;
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir + "/Descriptors/", "molconnz", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	
	}

	public static void GenerateHExplicitDragonDescriptors(String sdfile, String outfile) throws Exception{
		String workingDir = outfile.replaceAll("/[^/]+$", "") + "/";
		writeHExplicitDragonScriptFiles(sdfile, workingDir, outfile);
		  
		String execstr = "/usr/local/ceccr/dragon/dragonX -s " + workingDir + "dragon-scriptH.txt";
			
	    Utility.writeToDebug("Running external program: " + execstr);
	    Process p = Runtime.getRuntime().exec(execstr, null, new File(workingDir));
	    Utility.writeProgramLogfile(workingDir, "dragonH", p.getInputStream(), p.getErrorStream());
	    p.waitFor();
	}	
	
	public static void GenerateHDepletedDragonDescriptors(String sdfile, String outfile) throws Exception{
		String workingDir = outfile.replaceAll("/[^/]+$", "") + "/";
		writeHDepletedDragonScriptFiles(sdfile, workingDir, outfile);
		  
		String execstr = "/usr/local/ceccr/dragon/dragonX -s " + workingDir + "dragon-scriptNoH.txt";
			
	    Utility.writeToDebug("Running external program: " + execstr);
	    Process p = Runtime.getRuntime().exec(execstr, null, new File(workingDir));
	    Utility.writeProgramLogfile(workingDir, "dragonNoH", p.getInputStream(), p.getErrorStream());
	    p.waitFor();
	}
	
	private static void writeHExplicitDragonScriptFiles(String sdFile, String workingDir, String outfile) throws IOException {
		//also used for descriptor generation for prediction sets.
		
		Utility.writeToDebug("Writing Dragon scripts for " + sdFile + " into " + workingDir);
		
		FileOutputStream fout;
		PrintStream out;
		try {
			fout = new FileOutputStream(workingDir + "dragon-scriptH.txt");
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
			out.println("/d GetB11 None /PCno"); //blocks 11-16 and 19 are 3D descriptors; we only use 2D on Chembench.
			out.println("/d GetB12 None /PCno");
			out.println("/d GetB13 None /PCno");
			out.println("/d GetB14 None /PCno"); 
			out.println("/d GetB15 None /PCno"); 
			out.println("/d GetB16 None /PCno");
			out.println("/d GetB17 All /PCno");
			out.println("/d GetB18 All /PCno");
			out.println("/d GetB19 None /PCno");
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
			fout = new FileOutputStream(workingDir + "dragon-scriptNoH.txt");
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
		String execstr = "moe2D.sh " + " " + sdfile + " " + outfile + " " + Constants.CECCR_BASE_PATH + "mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir + "/Descriptors/", "moe2d.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	}

	public static void GenerateMaccsDescriptors(String sdfile, String outfile) throws Exception{
		//command: "maccs.sh infile.sdf outfile.maccs"
		String execstr = "maccs.sh " + sdfile + " " + outfile + " " + Constants.CECCR_BASE_PATH + "mmlsoft/SVL_DIR/batch_sd_MACCSFP.svl";
		String workingDir = sdfile.replaceAll("/[^/]+$", "");
		Utility.writeToDebug("Running external program: " + execstr);
		//Process p= Runtime.getRuntime().exec("moebatch_shell_script.sh "+file_path +" "+viz_path+".maccs");
		Process p = Runtime.getRuntime().exec(execstr);
		Utility.writeProgramLogfile(workingDir + "/Descriptors/", "maccs.sh", p.getInputStream(), p.getErrorStream());
		p.waitFor();
	}
	
}