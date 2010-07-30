package edu.unc.ceccr.workflows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class StandardizeMoleculesWorkflow {
	
	public static void standardizeSdf(String sdfIn, String sdfOut, String workingDir) throws Exception{
		//Standardizes the molecules in this sdfile. Necessary to do this before running DRAGON
		//descriptor generation. Also important to do this to any molecules that could go into our database.
		
		
		ArrayList<String> compounds = DatasetFileOperations.getCompoundsFromSdf(workingDir + sdfIn);
		
		if(compounds.size() < 600){

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
			
			String fileContents = "";
			int fileIndex = 0;
			for(int i = 0; i < compounds.size(); i++){
				fileContents += compounds.get(i);
				if((i+1) % 600 == 0 || i == compounds.size() - 1){
					String sdfFilePart = sdfIn + "_" + fileIndex + ".sdf";
					FileAndDirOperations.writeStringToFile(fileContents, workingDir + sdfFilePart);
					
					//apply standardization to that file
					String standardizedFilePart = sdfFilePart + ".standardize";
					String execstr1 = "standardize.sh " + sdfFilePart + " " + standardizedFilePart;
					Process p = Runtime.getRuntime().exec(execstr1, null, new File(workingDir));
					Utility.writeProgramLogfile(workingDir, "standardize" + fileIndex, p.getInputStream(), p.getErrorStream());
					p.waitFor();
					
					fileIndex++;
					fileContents = "";
				}
			}
			
			Utility.writeToDebug("Merging standardized SDFs");
			//merge the output files back together
			String standardizedFile = "";
			for(int i = 0; i < fileIndex; i++){
				String filePartName = sdfIn + "_" + i + ".sdf.standardize";
				standardizedFile += FileAndDirOperations.readFileIntoString(workingDir + filePartName);
				
				//clean up all the file parts, they're no longer needed
				FileAndDirOperations.deleteFile(workingDir + filePartName);
				String oldFile = sdfIn + "_" + i + ".sdf";
				FileAndDirOperations.deleteFile(workingDir + oldFile);
			}
			String mergedFileName = "";
			FileAndDirOperations.writeStringToFile(standardizedFile, sdfOut);
		}
  	}
}