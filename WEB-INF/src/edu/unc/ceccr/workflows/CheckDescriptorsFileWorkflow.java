package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CheckDescriptorsFileWorkflow{
	//Read in the output of a descriptor generation program (molconnZ, dragon, etc.)
	//Look for any errors that would make the output unusable in modeling
	//Return an HTML-formatted string with user-readable feedback

	public static String checkMolconnZDescriptors(String molconnZOutputFile) throws Exception{

		 ArrayList<String> descriptorNames = new ArrayList<String>();
		 ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
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
							errors += "Molecule " + 
								descriptorValues.get(Constants.MOLCONNZ_COMPOUND_NAME_POS) + 
								" has formula " + descriptorValues.get(Constants.MOLCONNZ_FORMULA_POS) + "<br />";
						}
						
						descriptorValues.remove(Constants.MOLCONNZ_FORMULA_POS); //contains molecule name, which isn't a descriptor
						descriptorValues.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS); //contains molecule name, which isn't a descriptor
						descriptorValues.remove(0); //contains molecule ID, which isn't a descriptor
						Descriptors di = new Descriptors();
						di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));
						descriptorValueMatrix.add(di);
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
										descriptorValues.get(Constants.MOLCONNZ_COMPOUND_NAME_POS) + "<br />\n")){
							errors += "Descriptors could not be calculated for molecule: " + 
								descriptorValues.get(Constants.MOLCONNZ_COMPOUND_NAME_POS) + "<br />\n";
						}
						temp = "-1"; //junk value. 
					}
					descriptorValues.add(temp);
				}
			}
		}
		//add the last molecule's descriptors
		descriptorValues.remove(Constants.MOLCONNZ_FORMULA_POS); //contains molecule name, which isn't a descriptor
		descriptorNames.remove(Constants.MOLCONNZ_FORMULA_POS);
		descriptorValues.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS); //contains molecule name, which isn't a descriptor
		descriptorNames.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS);
		descriptorValues.remove(0); //contains molecule ID, which isn't a descriptor
		descriptorNames.remove(0);
		Descriptors di = new Descriptors();
		di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));
		descriptorValueMatrix.add(di);
		
		/*
		Utility.writeToDebug(di.getDescriptorValues());
		for(int i = 0; i < descriptorValueMatrix.size(); i++){
			Descriptors matrixLine = descriptorValueMatrix.get(i);
			Utility.writeToDebug(matrixLine.getDescriptorValues());
		}*/
		
		fin.close();
		return errors;
	}

	public static String checkDragonDescriptors(String dragonOutputFile) throws Exception{
		
		Utility.writeToDebug("reading Dragon Descriptors");
		ArrayList<String> descriptorNames = new ArrayList<String>();
		 ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
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
		//int num_molecules = Integer.parseInt(tok.next()); 
		tok.next(); //just says "2" all the time, no idea what that means, so skip that
		//int num_descriptors = Integer.parseInt(tok.next());
		
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
					if(!errors.contains("Descriptor generation failed for molecule: " + descriptorValues.get(1) +".<br />"))
					errors += "Descriptor generation failed for molecule: " + descriptorValues.get(1) +".<br />";
				}
				descriptorValues.add(dvalue);
			}
			
			Descriptors di = new Descriptors();
			
			descriptorValues.remove(1); //contains molecule name, which isn't a descriptor
			descriptorValues.remove(0); //contains molecule number, which isn't a descriptor
			
			di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));

			descriptorValueMatrix.add(di);
			descriptorValues.clear();
		}
		return errors;
	}
	
	public static String checkMaccsDescriptors(String maccsOutputFile) throws Exception{
		//right now this doesn't check anything. The MACCS keys never seem to cause issues.

		Utility.writeToDebug("reading Maccs Descriptors");
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		String errors = "";
		
		File file = new File(maccsOutputFile);
		if(! file.exists() || file.length() == 0){
			return "Could not read descriptor file.\n";
		}
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		String line = br.readLine(); // first line is junk, it says "name,FP:MACCS".
		
		while((line = br.readLine()) != null){
			String descriptorString = new String("");
			Scanner tok = new Scanner(line);
			tok.useDelimiter(",");
			tok.next(); //skip compound identifier
			String tmp = tok.next();
			tok = new Scanner(tmp).useDelimiter(" ");
			int last = 0;
			int descriptor = 0;
			while(tok.hasNext()){
				descriptor = Integer.parseInt(tok.next());
				for(int i = last; i < descriptor; i++){
					descriptorString += 0 + " ";
				}
				descriptorString += 1 + " ";
				last = descriptor + 1;
			}
			for(int i = last; i < Constants.NUM_MACCS_KEYS; i++){
				descriptorString += 0 + " ";
			}
			Descriptors di = new Descriptors();
			di.setDescriptorValues(descriptorString);
			descriptorValueMatrix.add(di);

		}
		for(int i = 0; i < Constants.NUM_MACCS_KEYS; i++){
			descriptorNames.add((new Integer(i)).toString());
		}
		return errors;
	}
	
	public static String checkMoe2DDescriptors(String moe2DOutputFile) throws Exception{
		//right now this doesn't check anything. The MOE2D descriptors never seem to cause issues.
		Utility.writeToDebug("reading Moe2D Descriptors");
		ArrayList<String> descriptorNames = new ArrayList<String>();
		 ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
		String errors = "";
		
		File file = new File(moe2DOutputFile);
		if(! file.exists() || file.length() == 0){
			return "Could not read descriptor file.\n";
		}
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		String line = br.readLine(); // contains descriptor names
		Scanner tok = new Scanner(line).useDelimiter(",");
		tok.next(); //first descriptor says "name"; we don't need that.
		while(tok.hasNext()){
			descriptorNames.add(tok.next());
		}
		while((line = br.readLine()) != null){
			tok = new Scanner(line).useDelimiter(",");
			if(tok.hasNext()){
				tok.next(); //first descriptor value is the name of the compound
			}
			String descriptorString = new String("");
			while(tok.hasNext()){
				descriptorString += tok.next() + " ";
			}
			if(! descriptorString.equalsIgnoreCase("")){
				Descriptors di = new Descriptors();
				di.setDescriptorValues(descriptorString);
				descriptorValueMatrix.add(di);
			}
		}
		return errors;
	}
}