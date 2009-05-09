package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ReadDescriptorsFileWorkflow{
	//Read in the output of a descriptor generation program (molconnZ, dragon, etc.)
	//Create a Descriptors object for each compound. 
	//puts results into descriptorNames and descriptorValueMatrix.

	public static void readMolconnZDescriptors(String molconnZOutputFile, ArrayList<String> descriptorNames, ArrayList<Descriptors> descriptorValueMatrix) throws Exception{

		Utility.writeToDebug("reading MolconnZ Descriptors");
		
		File file = new File(molconnZOutputFile);
		FileReader fin = new FileReader(file);

		String temp;
		Scanner src = new Scanner(fin);
		ArrayList<String> descriptorValues = new ArrayList<String>(); //values for each molecule

		boolean readingDescriptorNames = true;
		while (src.hasNext()) {
			//sometimes MolconnZ spits out nonsensical crap like ¿­¤C along with
			//a descriptor value. Filter that out.
			temp = src.next();
			if(temp.matches("not_available")){
				//molconnz will spit out a not_available if it gets a bad molecule.
				descriptorValues.clear();
			}
			if(temp.matches("[a-zA-Z0-9[-.()]]+")){

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
						//done reading values for this molecule, we're on the next one now.
						descriptorValues.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS); //contains molecule name, which isn't a descriptor
						descriptorValues.remove(0); //contains molecule ID, which isn't a descriptor
						Descriptors di = new Descriptors();
						di.setDescriptorValues(descriptorValues.toString().replaceAll("[,\\[\\]]", ""));
						descriptorValueMatrix.add(di);
						descriptorValues.clear();
					}
					descriptorValues.add(temp);
				}
			}
		}
		//add the last molecule's descriptors
		descriptorValues.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS); //contains molecule name, which isn't a descriptor
		descriptorNames.remove(Constants.MOLCONNZ_COMPOUND_NAME_POS - 1);
		descriptorValues.remove(0); //contains molecule ID, which isn't a descriptor
		descriptorNames.remove(0);
		Descriptors di = new Descriptors();
		di.setDescriptorValues(descriptorValues.toString().replaceAll("[,\\[\\]]", ""));
		descriptorValueMatrix.add(di);
		
		fin.close();
	}

	public static void readDragonDescriptors(String dragonOutputFile, ArrayList<String> descriptorNames, ArrayList<Descriptors> descriptorValueMatrix) throws Exception{
		
		Utility.writeToDebug("reading Dragon Descriptors");
		
		File file = new File(dragonOutputFile);
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
			String dname =  tok.next();
			descriptorNames.add(dname);
		}
		
		//read in the descriptor values. If one of them is the word "Error", quit this shit - means Dragon failed at descriptoring.
		while((line = br.readLine()) != null){
			tok = new Scanner(line);
			descriptorValues = new ArrayList<String>();
			descriptorValues.clear();
			while(tok.hasNext()){
				String dvalue = tok.next();
				if(dvalue.equalsIgnoreCase("Error")){
					throw new Exception("Dragon descriptors invalid!");
				}
				descriptorValues.add(dvalue);
			}
			
			Descriptors di = new Descriptors();
			di.setDescriptorValues(descriptorValues.toString().replaceAll("[,\\[\\]]", ""));
			descriptorValueMatrix.add(di);
			descriptorValues.clear();
		}
	}
	
	public static void readMaccsDescriptors(String maccsOutputFile, ArrayList<String> descriptorNames, ArrayList<Descriptors> descriptorValueMatrix) throws Exception{
		//generate with "maccs.sh infile.sdf outfile.maccs"
		
		Utility.writeToDebug("reading Maccs Descriptors");
		
		File file = new File(maccsOutputFile);
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);

		String line = br.readLine(); // first line is junk, it says "name,FP:MACCS".
		
		while((line = br.readLine()) != null){
			String descriptorString = new String("");
			Scanner tok = new Scanner(line);
			tok.useDelimiter(",");
			tok.next();
			tok = new Scanner(tok.next());
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
	}
	
	public static void readMoe2DDescriptors(String moe2DOutputFile, ArrayList<String> descriptorNames, ArrayList<Descriptors> descriptorValueMatrix) throws Exception{
		Utility.writeToDebug("reading Moe2D Descriptors");
		
		File file = new File(moe2DOutputFile);
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
	}
}