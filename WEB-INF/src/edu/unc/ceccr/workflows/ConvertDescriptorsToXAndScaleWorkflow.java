

package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class ConvertDescriptorsToXAndScaleWorkflow{
	private static int compoundsPerChunk = 100;
	/*
	For large prediction sets (e.g. > 10000 compounds), the descriptors will not
	be able to fit in Tomcat's memory, and this will cause errors.

	So instead of reading in the descriptors to a Java object (ReadDescriptorsFileWorkflow and then
	writing the Java object out as a .x (WriteDescriptorsFileWorkflow, we need to do a straight file 
	conversion in this case.
	*/

	public static void convertDescriptorsToXAndScaleInChunks(String workingDir, 
			String sdfile, String predictorXFile, String outputXFile, 
			String descriptorGenerationType, String scalingType) throws Exception{
		
		//split each descriptor file into chunks
		String descriptorsFile = sdfile;
		if(descriptorGenerationType.equals(Constants.MOLCONNZ)){
			descriptorsFile += ".molconnz";
			splitMolconnZFile(workingDir, descriptorsFile);
		}
		else if(descriptorGenerationType.equals(Constants.DRAGONH)){
			descriptorsFile += ".dragonH";
			splitDragonFile(workingDir, descriptorsFile);
		}
		else if(descriptorGenerationType.equals(Constants.DRAGONNOH)){
			descriptorsFile += ".dragonNoH";
			splitDragonFile(workingDir, descriptorsFile);
		}
		else if(descriptorGenerationType.equals(Constants.MOE2D)){
			descriptorsFile += ".moe2D";
			splitMoe2dFile(workingDir, descriptorsFile);
		}
		else if(descriptorGenerationType.equals(Constants.MACCS)){
			descriptorsFile += ".maccs";
			splitMaccsFile(workingDir, descriptorsFile);
		}
		
		//run scaling and conversion process on each chunk, producing several X files
		int filePartNumber = 0;
		File descriptorsFilePart = new File(workingDir + descriptorsFile + "_" + filePartNumber);
		while(descriptorsFilePart.exists()){

			ArrayList<String> descriptorNames = new ArrayList<String>();
			ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
			
			ArrayList<String> allChemicalNames = DatasetFileOperations.getSDFCompoundNames(workingDir + sdfile);
			ArrayList<String> chemicalNames = new ArrayList<String>();
			
			for(int i = filePartNumber * compoundsPerChunk; 
					i < (filePartNumber + 1) * compoundsPerChunk; i++){
				if(i < allChemicalNames.size()){
					chemicalNames.add(allChemicalNames.get(i));
				}
			}
			
			if(descriptorGenerationType.equals(Constants.MOLCONNZ)){
				ReadDescriptorsFileWorkflow.readMolconnZDescriptors(workingDir + descriptorsFile + "_" + filePartNumber, 
						descriptorNames, descriptorValueMatrix);
			}
			else if(descriptorGenerationType.equals(Constants.DRAGONH)){
				ReadDescriptorsFileWorkflow.readDragonDescriptors(workingDir + descriptorsFile + "_" + filePartNumber, 
						descriptorNames, descriptorValueMatrix);
			}
			else if(descriptorGenerationType.equals(Constants.DRAGONNOH)){
				ReadDescriptorsFileWorkflow.readDragonDescriptors(workingDir + descriptorsFile + "_" + filePartNumber, 
						descriptorNames, descriptorValueMatrix);
			}
			else if(descriptorGenerationType.equals(Constants.MOE2D)){
				ReadDescriptorsFileWorkflow.readMoe2DDescriptors(workingDir + descriptorsFile + "_" + filePartNumber, 
						descriptorNames, descriptorValueMatrix);
			}
			else if(descriptorGenerationType.equals(Constants.MACCS)){
				ReadDescriptorsFileWorkflow.readMaccsDescriptors(workingDir + descriptorsFile + "_" + filePartNumber, 
						descriptorNames, descriptorValueMatrix);
			}
			

			String descriptorString = Utility.StringArrayListToString(descriptorNames);
			WriteDescriptorsFileWorkflow.writePredictionXFile(
					chemicalNames, 
					descriptorValueMatrix, 
					descriptorString, 
					workingDir + outputXFile, 
					workingDir + predictorXFile, 
					scalingType);
			
			
			filePartNumber++;
			descriptorsFilePart = new File(workingDir + descriptorsFile + "_" + filePartNumber);
		}
		
		//reassemble X file parts into one big X file
	}	
	
	public static void convertDescriptorsToXAndScale(String workingDir, 
			String sdfile, String predictorXFile, String outputXFile, 
			String descriptorGenerationType, String scalingType, int numCompounds) throws Exception{
		
		if(numCompounds > compoundsPerChunk){
			convertDescriptorsToXAndScaleInChunks(workingDir, sdfile, 
					predictorXFile, outputXFile, descriptorGenerationType, scalingType);
			return;
		}
		
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		ArrayList<String> chemicalNames = DatasetFileOperations.getSDFCompoundNames(workingDir + sdfile);

		String descriptorsFile = sdfile;
		if(descriptorGenerationType.equals(Constants.MOLCONNZ)){
			descriptorsFile += ".molconnz";
			ReadDescriptorsFileWorkflow.readMolconnZDescriptors(workingDir + descriptorsFile, descriptorNames, descriptorValueMatrix);
		}
		else if(descriptorGenerationType.equals(Constants.DRAGONH)){
			descriptorsFile += ".dragonH";
			ReadDescriptorsFileWorkflow.readDragonDescriptors(workingDir + descriptorsFile, descriptorNames, descriptorValueMatrix);
		}
		else if(descriptorGenerationType.equals(Constants.DRAGONNOH)){
			descriptorsFile += ".dragonNoH";
			ReadDescriptorsFileWorkflow.readDragonDescriptors(workingDir + descriptorsFile, descriptorNames, descriptorValueMatrix);
		}
		else if(descriptorGenerationType.equals(Constants.MOE2D)){
			descriptorsFile += ".moe2D";
			ReadDescriptorsFileWorkflow.readMoe2DDescriptors(workingDir + descriptorsFile, descriptorNames, descriptorValueMatrix);
		}
		else if(descriptorGenerationType.equals(Constants.MACCS)){
			descriptorsFile += ".maccs";
			ReadDescriptorsFileWorkflow.readMaccsDescriptors(workingDir + descriptorsFile, descriptorNames, descriptorValueMatrix);
		}
		
		String descriptorString = Utility.StringArrayListToString(descriptorNames);
		WriteDescriptorsFileWorkflow.writePredictionXFile(
				chemicalNames, 
				descriptorValueMatrix, 
				descriptorString, 
				workingDir + outputXFile, 
				workingDir + predictorXFile, 
				scalingType);
	}
	
	//helper functions

	private static void splitMolconnZFile(String workingDir, String descriptorsFile) throws Exception{
		File file = new File(workingDir + descriptorsFile);
		if(!file.exists() || file.length() == 0){
			throw new Exception("Could not read MolconnZ descriptors.\n");
		}
		FileReader fin = new FileReader(file);

		String temp;
		Scanner src = new Scanner(fin);
		ArrayList<String> descriptorNames = new ArrayList<String>();  //names for each molecule; used in counting
		ArrayList<String> descriptorValues = new ArrayList<String>(); //values for each molecule; used in counting
		
		String header = ""; //stores everything up to where descriptors begin.
		int currentFile = 0;
		int moleculesInCurrentFile = 0;
		BufferedWriter outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
		
		boolean readingDescriptorNames = true;
		while (src.hasNext()) {
			temp = src.next();
			if(temp.matches("[\\p{Graph}]+")){ 
				if(temp.equals("1") && readingDescriptorNames){
					//The first occurrence of the number "1" indicates we're no
					//longer reading descriptor names.
					//"1" will indicate the first molecule, no matter what the SDF
					//had as molecule numbers.
					readingDescriptorNames = false;
				}
				
				outFilePart.write(temp + " ");
				if(readingDescriptorNames){
					descriptorNames.add(temp);
					header += temp + " ";
				}
				else{
					descriptorValues.add(temp);
					if(descriptorValues.size() == descriptorNames.size()){
						//At end of this molecule.
						descriptorValues.clear();
						outFilePart.write("\n");
						moleculesInCurrentFile++;
						if(moleculesInCurrentFile > compoundsPerChunk){
							outFilePart.close();
							moleculesInCurrentFile = 0;
							currentFile++;
							outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
							outFilePart.write(header + "\n");
						}
					}
				}
			}
		}
		
		//close final file
		outFilePart.write("\n");
		outFilePart.close();
	}
	
	private static void splitDragonFile(String workingDir, String descriptorsFile) throws Exception{
		
		File file = new File(workingDir + descriptorsFile);
		if(!file.exists() || file.length() == 0){
			throw new Exception("Could not read Dragon descriptors.\n");
		}
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		int currentFile = 0;
		int moleculesInCurrentFile = 0;
		BufferedWriter outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
		
		String header = br.readLine() + "\n"; //stores everything up to where descriptors begin.
		header += br.readLine() + "\n";
		header += br.readLine() + "\n";
		
		outFilePart.write(header);
		
		String line;
		//Now we're at the descriptor values for each compound
		while((line = br.readLine()) != null){
			outFilePart.write(line + "\n");
			
			moleculesInCurrentFile++;
			if(moleculesInCurrentFile > compoundsPerChunk){
				outFilePart.close();
				moleculesInCurrentFile = 0;
				currentFile++;
				outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
				outFilePart.write(header);
			}
		}

		//close final file
		outFilePart.write("\n");
		outFilePart.close();		
	}

	private static void splitMaccsFile(String workingDir, String descriptorsFile) throws Exception{
		File file = new File(workingDir + descriptorsFile);
		if(!file.exists() || file.length() == 0){
			throw new Exception("Could not read MOE2D descriptors.\n");
		}
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		String header = ""; //stores everything up to where descriptors begin.
		int currentFile = 0;
		int moleculesInCurrentFile = 0;
		BufferedWriter outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
	
		header = br.readLine() + "\n"; 
		outFilePart.write(header);
		
		String line;
		while((line = br.readLine()) != null){
			outFilePart.write(line + "\n");
			
			moleculesInCurrentFile++;
			if(moleculesInCurrentFile > compoundsPerChunk){
				outFilePart.close();
				moleculesInCurrentFile = 0;
				currentFile++;
				outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
				outFilePart.write(header);
			}
		}
	}
	
	private static void splitMoe2dFile(String workingDir, String descriptorsFile) throws Exception{
		File file = new File(workingDir + descriptorsFile);
		if(!file.exists() || file.length() == 0){
			throw new Exception("Could not read MOE2D descriptors.\n");
		}
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		String header = ""; //stores everything up to where descriptors begin.
		int currentFile = 0;
		int moleculesInCurrentFile = 0;
		BufferedWriter outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
	
		header = br.readLine() + "\n";
		outFilePart.write(header);
		
		String line;
		while((line = br.readLine()) != null){
			outFilePart.write(line + "\n");
			
			moleculesInCurrentFile++;
			if(moleculesInCurrentFile > compoundsPerChunk){
				outFilePart.close();
				moleculesInCurrentFile = 0;
				currentFile++;
				outFilePart = new BufferedWriter(new FileWriter(workingDir + descriptorsFile + "_" + currentFile));
				outFilePart.write(header);
			}
		}
	}
	
	private static void mergeXFileParts(String workingDir, String outputXFile, String scalingType) throws Exception{
		int filePartNumber = 0;
		File xFilePart = new File(workingDir + outputXFile + "_" + filePartNumber);
		BufferedReader br = new BufferedReader(new FileReader(xFilePart));
		
		BufferedWriter xFileOut = new BufferedWriter(new FileWriter(workingDir + outputXFile));
		ArrayList<String> linesInFilePart = null;
		while(xFilePart.exists()){
			//read all lines into array
			linesInFilePart = new ArrayList<String>();
			String line;
			while((line = br.readLine()) != null){
				line += "\n";
				linesInFilePart.add(line);
			}
			
			//if this is the first filepart, print header
			if(filePartNumber == 0){
				xFileOut.write(linesInFilePart.get(0));
				xFileOut.write(linesInFilePart.get(1));
			}
			
			//print all but the header and footer
			int numFooterLines = 2;
			if(scalingType.equals(Constants.NOSCALING)){
				numFooterLines = 0;
			}
			for(int i = 2; i < linesInFilePart.size() - numFooterLines; i++){
				xFileOut.write(linesInFilePart.get(i));
			}	
			
			filePartNumber++;
			xFilePart = new File(workingDir + outputXFile + "_" + filePartNumber);
			br = new BufferedReader(new FileReader(xFilePart));
		}
		
		//print footer
		if(! scalingType.equals(Constants.NOSCALING)){
			xFileOut.write(linesInFilePart.get(linesInFilePart.size() - 2));
			xFileOut.write(linesInFilePart.get(linesInFilePart.size() - 1));
		}
		
	}
}
