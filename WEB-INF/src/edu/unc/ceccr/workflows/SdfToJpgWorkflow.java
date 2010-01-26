package edu.unc.ceccr.workflows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

import edu.unc.ceccr.utilities.Utility;

public class SdfToJpgWorkflow {
	
	public static void makeSketchFiles(String filePath, String fileName, String structuresDir, String sketchesDir) throws Exception
	{
		//filePath = directory the SDF is in, e.g. /public/projects/ceccr/workflow-users/theo/DATASETS/
		//fileName = name of sdfile, like anticonv_91.sdf
		//structuresDir = subdirectory for structures, e.g. Visualization/Structures/
		//sketchesDir = subdirectory for sketches, e.g. Visualization/Sketches/
		
		//remove explicit hydrogens from SDFs; they are noise as far as the JPG is concerned.
		String execstr1 = "removeExplicitH.sh " + filePath + fileName + " " + filePath + fileName + ".removedH";
		Utility.writeToDebug("Running external program: " + execstr1 + " in dir " + filePath);
		Process p = Runtime.getRuntime().exec(execstr1, null, new File(filePath));
		Utility.writeProgramLogfile(filePath, "removeExplicitH", p.getInputStream(), p.getErrorStream());
		p.waitFor();
		
		fileName += ".removedH";
		
		//Split the input SDF (lots of compounds) into separate SDFs (1 compound each).
		//Put that in the Structures dir.
		structuresDir = filePath + structuresDir;
		sketchesDir = filePath + sketchesDir;
		Utility.writeToDebug("Creating structures into dir: " + structuresDir);

		//make sure Structures dir exists. 
		File stDir = new File(structuresDir);
		if(!stDir.exists()){
			stDir.mkdirs();
		}
		
		File file;
		file= new File(filePath + fileName);
		FileReader fin = new FileReader(file);
		Scanner src = new Scanner(fin);
		
		while (src.hasNext()) {
			StringBuilder sb = new StringBuilder();
			String title = src.nextLine();
			sb.append(title);
			String temp;
			while (src.hasNext()) {
				temp = src.nextLine();
				sb.append("\n" + temp);
				if (temp.startsWith("$")) {
						FileWriter fout = new FileWriter(structuresDir + title.trim() + ".sdf");
						fout.write(sb.toString());
						fout.close();
						
					break;
				}
			}
		}
		fin.close();
	
		Utility.writeToDebug("Done creating structures. ");
		Utility.writeToDebug("Creating sketches into dir: " + sketchesDir);

		//Done generating Structures files.
		//look in Structures directory.
		//for each .sdf file in Structures/, create a .jpg file in Sketches/.

		//make sure Sketches dir exists.
		File skDir = new File(sketchesDir);
		if(!skDir.exists()){
			skDir.mkdirs();
		}
		
		File dir = new File(structuresDir);
		String files[] = dir.list();
		if(files == null){
			Utility.writeToDebug("Error reading Structures directory: " + structuresDir);
		}
		int x = 0;
		while(files != null && x<files.length){
			String jpgFilename = files[x].replace("sdf", "jpg");
			
			//only make the JPG if it's not already there - saves a lot of time!
			if(! new File(sketchesDir + jpgFilename).exists()){ 
				
				String command = "molconvert -2 jpeg:w300,Q95 "+ structuresDir + files[x]+ " -o "+ sketchesDir + jpgFilename;
				Utility.writeToMSDebug("Creating image::"+command);
				
				// run molconvert
				Process process = Runtime.getRuntime().exec(command);
				Utility.writeProgramLogfile(sketchesDir, "molconvert" + x, process.getInputStream(), process.getErrorStream());
				
				process.waitFor();
				//Utility.writeToMSDebug("exit value = " + process.exitValue());
			
			}
			x++;
		}
		Utility.writeToMSDebug("END Creating .jpg files into Sketches dir: " + sketchesDir);
		Utility.writeToDebug("Done creating sketches. ");
		
	}
}