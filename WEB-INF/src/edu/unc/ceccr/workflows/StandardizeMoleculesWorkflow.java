package edu.unc.ceccr.workflows;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class StandardizeMoleculesWorkflow {
	
	public static void standardizeSdf(String sdfIn, String sdfOut, String workingDir) throws Exception{
		//Standardizes the molecules in this sdfile. Necessary to do this before running DRAGON
		//descriptor generation. Also important to do this to any molecules that could go into our database.
		
		Utility.writeToDebug("standardizeSdf: getting sdf compounds");		
		ArrayList<String> compoundNames = DatasetFileOperations.getSDFCompoundNames(workingDir + sdfIn);
		Utility.writeToDebug("standardizeSdf: done getting sdf compounds");
		
		if(compoundNames.size() < 600){

			String execstr1 = "standardize.sh " + sdfIn + " " + sdfOut;
			Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + workingDir);
			Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "standardize", p.getInputStream(), p.getErrorStream());
			p.waitFor();
			
		}
		else{
			//The JChem software won't let you do more than 666 molecules in this process at a time
			//so we split the SDF into chunks of size 600, do the process on each file, then
			//reassemble the outputs.
			
			//split the SDF
			Utility.writeToDebug("Splitting and standardizing " + sdfIn + " in dir " + workingDir);
			
			File infile = new File(workingDir + sdfIn);
			FileReader fin = new FileReader(infile);
			BufferedReader br = new BufferedReader(fin);
			
			
			//read molecules from original SDF
			int compoundsInCurrentFile = 0;
			int currentFileNumber = 0;
			

			String sdfFilePart = sdfIn + "_" + currentFileNumber + ".sdf";
			BufferedWriter partOut = new BufferedWriter(new FileWriter(workingDir + sdfFilePart));
			
			String line;
			while((line = br.readLine()) != null){
				
				partOut.write(line + "\n");
				
				if(line.startsWith("$$$$")){
					//done reading a compound
					compoundsInCurrentFile++;
					if(compoundsInCurrentFile == 600){
						//close current file part and apply standardization to it
						partOut.close();
						String standardizedFilePart = sdfFilePart + ".standardize";
						String execstr1 = "standardize.sh " + sdfFilePart + " " + standardizedFilePart;
						Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingDir));
						Utility.writeProgramLogfile(workingDir, "standardize" + currentFileNumber, p.getInputStream(), p.getErrorStream());
						p.waitFor();
						
						//start a new file
						currentFileNumber++;
						sdfFilePart = sdfIn + "_" + currentFileNumber + ".sdf";
						partOut = new BufferedWriter(new FileWriter(workingDir + sdfFilePart));
						
					}
				}
			}
			
			//close and standardize the final file part
			partOut.close();
			String standardizedFilePart = sdfFilePart + ".standardize";
			String execstr1 = "standardize.sh " + sdfFilePart + " " + standardizedFilePart;
			Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingDir));
			Utility.writeProgramLogfile(workingDir, "standardize" + currentFileNumber, p.getInputStream(), p.getErrorStream());
			p.waitFor();
			
			
			Utility.writeToDebug("Merging standardized SDFs");
			//merge the output files back together
			
			BufferedWriter out = new BufferedWriter(new FileWriter(workingDir + sdfOut));
		        
			for(int i = 0; i <= currentFileNumber; i++){
				String filePartName = sdfIn + "_" + i + ".sdf.standardize";
				standardizedFilePart = FileAndDirOperations.readFileIntoString(workingDir + filePartName);
				out.write(standardizedFilePart);
				
				//delete the standardized file-part from disk, it's no longer needed
				FileAndDirOperations.deleteFile(workingDir + filePartName);
				String oldFile = sdfIn + "_" + i + ".sdf";
				FileAndDirOperations.deleteFile(workingDir + oldFile);
			}
			out.close();
		}
  	}
}