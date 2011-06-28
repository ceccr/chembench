package edu.unc.ceccr.workflows.descriptors;

import java.io.*;

import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CheckDescriptors{
	//Read in the output of a descriptor generation program (molconnZ, dragon, etc.)
	//Look for any errors that would make the output unusable in modeling
	//Return an HTML-formatted string with user-readable feedback

	public static String checkMolconnZDescriptors(String molconnZOutputFile) throws Exception{
		
		Utility.writeToDebug("Checking MolconnZ descriptors: " + molconnZOutputFile);
		
		 ArrayList<String> descriptorNames = new ArrayList<String>();
		String errors = "";
		 
		File file = new File(molconnZOutputFile);
		if(! file.exists() || file.length() == 0){
			return "Could not read descriptor file.\n";
		}
		
		FileReader fin = new FileReader(file);

		String temp;
		Scanner src = new Scanner(fin);
		ArrayList<String> descriptorValues = new ArrayList<String>(); //values for each molecule

		boolean readingDescriptorNames = true;
		while (src.hasNext()) {
			//sometimes MolconnZ spits out nonsensical crap like ���C along with
			//a descriptor value. Filter that out.
			temp = src.next();
			if(temp.matches("not_available")){
				//molconnz will spit out a not_available if it gets a bad molecule.
				descriptorValues.clear();
			}
			if(temp.matches("[\\p{Graph}]+")){ 

				if(temp.equals("1") && readingDescriptorNames){
					//The first occurrence of the number "1" indicates we're no
					//longer reading descriptor names.
					//"1" will indicate the first molecule, no matter what the SDF
					//had as molecule numbers.
					readingDescriptorNames = false;
				}

				if(readingDescriptorNames){
					descriptorNames.add(temp);
				}
				else{
					if(descriptorValues.size() == descriptorNames.size()){
						//done reading values for this molecule.
						String formula = descriptorValues.get(Constants.MOLCONNZ_FORMULA_POS);
						//formula should look something like C(12)H(22)O(11)
						if(! formula.contains("(")){
							//the formula for the molecule isn't a formula
							//usually indicates missing descriptors on the previous molecule
							errors += "Error reading descriptors file at line: ";
							for(int i = 0; i < 15; i++){
								if(i < descriptorValues.size()){
									errors += descriptorValues.get(i) + " ";
								}
							}
							errors += "\n";
						}
						descriptorValues.clear();
					}
					
					//a couple more special cases for when MolconnZ decides to go crazy
					if(temp.equals("inf")){
						temp = "9999";
					}
					else if(temp.equals("-inf")){
						temp = "-9999";
					}
					else if(temp.equals("not_available")){
						//quit this shit - means MolconnZ failed at descriptoring and all values past this point will be offset.
						if(descriptorValues.size() > Constants.MOLCONNZ_COMPOUND_NAME_POS &&
								! errors.contains("Descriptor generation failed for molecule: " + 
										descriptorValues.get(Constants.MOLCONNZ_COMPOUND_NAME_POS) + "\n")){
							errors += "Descriptors could not be calculated for molecule: " + 
								descriptorValues.get(Constants.MOLCONNZ_COMPOUND_NAME_POS) + "\n";
						}
						temp = "-1"; //junk value. 
					}
					descriptorValues.add(temp);
				}
			}
		}
		
		fin.close();
		Utility.writeToDebug("Done checking MolconnZ descriptors: " + molconnZOutputFile);
		return errors;
	}

	public static String checkDragonDescriptors(String dragonOutputFile) throws Exception{
		Utility.writeToDebug("Checking Dragon descriptors: " + dragonOutputFile);
		ArrayList<String> descriptorNames = new ArrayList<String>();
		String errors = "";
		
		File file = new File(dragonOutputFile);
		if(! file.exists() || file.length() == 0){
			return "Could not read descriptor file.\n";
		}
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		ArrayList<String> descriptorValues; //values for each molecule
		
		String line = br.readLine();  //junk line, should say "dragonX: Descriptors"

		//contains some numbers
		line = br.readLine();
		Scanner tok = new Scanner(line);
		tok.next(); //just says "2" all the time, no idea what that means, so skip that
		
		//the descriptor names are on this line
		line = br.readLine();
		tok = new Scanner(line);
		while(tok.hasNext()){
			String dname = tok.next();
			descriptorNames.add(dname);
		}

		descriptorNames.remove(1); //contains molecule name, which isn't a descriptor
		descriptorNames.remove(0); //contains molecule number, which isn't a descriptor
		
		//read in the descriptor values. If one of them is the word "Error", quit this shit - means Dragon failed at descriptoring.
		while((line = br.readLine()) != null){
			tok = new Scanner(line);
			descriptorValues = new ArrayList<String>();
			descriptorValues.clear();
			while(tok.hasNext()){
				String dvalue = tok.next();
				if(dvalue.equalsIgnoreCase("Error")){
					if(!errors.contains("Descriptor generation failed for molecule: " + descriptorValues.get(1) +".\n"))
					errors += "Descriptor generation failed for molecule: " + descriptorValues.get(1) +".\n";
				}
				descriptorValues.add(dvalue);
			}
			
			/* not important - we don't do any checks that require the whole descriptor matrix
			Descriptors di = new Descriptors();
			
			descriptorValues.remove(1); //contains molecule name, which isn't a descriptor
			descriptorValues.remove(0); //contains molecule number, which isn't a descriptor
			
			di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues)); */
			descriptorValues.clear();
		}
		Utility.writeToDebug("Done checking Dragon descriptors: " + dragonOutputFile);
		return errors;
	}

	public static String checkCDKDescriptors(String cdkOutputFile) throws Exception{
		//check if CDK file exists, then look for NA's.
		String errors = "";
		
		File file = new File(cdkOutputFile);
		if(! file.exists() || file.length() == 0){
			errors = "Could not read descriptor file.\n";
		}
		else{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = br.readLine()) != null ){
				if(line.contains("NA")){
					String compoundName = line.split("\\s+")[0];
					errors += ("Error: compound named '" + compoundName + "' has some invalid descriptors. \n");
				}
			}
		}
		return errors;
	}
	
	
	public static String checkMaccsDescriptors(String maccsOutputFile) throws Exception{
		//right now this doesn't check anything. The MACCS keys never seem to cause issues.
		String errors = "";
		
		File file = new File(maccsOutputFile);
		if(! file.exists() || file.length() == 0){
			errors = "Could not read descriptor file.\n";
		}
		return errors;
	}
	
	public static String checkMoe2DDescriptors(String moe2DOutputFile) throws Exception{
		//right now this doesn't check anything. The MOE2D descriptors never seem to cause issues.
		String errors = "";
		
		File file = new File(moe2DOutputFile);
		if(! file.exists() || file.length() == 0){
			errors =  "Could not read descriptor file.\n";
			return errors;
		}
		
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		ArrayList<String> descriptorValues; //values for each molecule
		
		String line = br.readLine();  //descriptor names
		
		while((line = br.readLine()) != null){
			Scanner tok = new Scanner(line);
			
			if(tok.hasNext()){
				tok.next(); //first value is compound name
			}
			
			while(tok.hasNext()){
				String t = tok.next();
				try{
					//check if it's a number
					Float.parseFloat(t);
				}
				catch(Exception ex){
					errors += "Error reading Moe2D descriptor value: " + t + "\n";
				}
			}
		}
		
		return errors;
	}
}