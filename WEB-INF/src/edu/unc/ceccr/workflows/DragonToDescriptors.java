package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

public class DragonToDescriptors{
	public static void MakeModelingDescriptors(String DragonOutputFile, String KnnInputFile) throws Exception{
		//read in the Dragon descriptors and make a range-scaled kNN input file (.x) as output
		
		Utility.writeToDebug("Normalizing Modeling Descriptors");
		
		File file = new File(DragonOutputFile);
		FileReader fin = new FileReader(file);
		BufferedReader br = new BufferedReader(fin);
		File file2 = new File(KnnInputFile);
		FileWriter fout = new FileWriter(file2);

		//the data structures we'll be filling up
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList< ArrayList<String> > descriptorValueMatrix = new ArrayList< ArrayList<String> >();
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
			descriptorValueMatrix.add(descriptorValues);
		}
		
		int num_molecules = descriptorValueMatrix.size();
		int num_descriptors = descriptorNames.size();

		//DONE READING .dragon FILE
		
		//Find the min and max values for each descriptor
		float[] descriptorMinimum = new float[num_descriptors];
		float[] descriptorMaximum = new float[num_descriptors];
		//descriptors are only useful if their values aren't the same across the whole set.
		boolean[] usefulDescriptor = new boolean[num_descriptors];
		usefulDescriptor[0] = true; //keep the moleculeID "descriptor"
		
		//the molecule's name is somewhere in the descriptors (and obviously shouldn't be.)
		//Take it out and note where it is, so we can use it later.
		int molname_index = 0;
		for(int i = 1; i < descriptorNames.size(); i++){
			if(descriptorNames.get(i).toString().equalsIgnoreCase("MOL_ID")){
				usefulDescriptor[i] = false;
				molname_index = i;
			}
		}

		//collect information for use in normalization
		boolean first = true;
		for(ArrayList<String> al: descriptorValueMatrix){
			for(int i = 1; i < al.size(); i++){
				//Starts at 1 so we don't normalize the molecule id
				float ival;
				try{
					ival = Float.parseFloat(al.get(i));
				}
				catch(NumberFormatException ex){
					ival = 0;
				}
				if(first){
					descriptorMinimum[i] = ival;
					descriptorMaximum[i] = ival;
				}
				else{
					if(descriptorMinimum[i] > ival){
						descriptorMinimum[i] = ival;
					}
					if(descriptorMaximum[i] < ival){
						descriptorMaximum[i] = ival;
					}
				}
			}
			first = false;
		}

		//Normalize the values of each descriptor
		for(ArrayList<String> al: descriptorValueMatrix){
			for(int i = 1; i < al.size(); i++){
				//Starts at 1 so we don't normalize the molecule id
				if(i == molname_index){
					//skip molname index. We don't want to normalize that.
					i++;
				}
				float ival;
				try{
					ival = Float.parseFloat(al.get(i));
				}
				catch(NumberFormatException ex){
					ival = 0;
				}
				if(Math.abs(descriptorMinimum[i] - descriptorMaximum[i]) < 0.0001){
					usefulDescriptor[i] = false;
				}
				else{
					al.set(i, ((Float)((ival - descriptorMinimum[i])/(descriptorMaximum[i] - descriptorMinimum[i]))).toString());
					usefulDescriptor[i] = true;
				}
			}
		}
		
		//get output ready, then write it
		int num_useful_descriptors = 0;
		for(int i = 1; i < num_descriptors; i++){
			if(usefulDescriptor[i]){
				num_useful_descriptors++;
			}
		}
		fout.write(num_molecules + " ");
		fout.write(num_useful_descriptors + "\n");
		
		for(int i = 1; i < descriptorNames.size(); i++){
			if(usefulDescriptor[i]){
				fout.write(descriptorNames.get(i) + " ");
			}
		}
		fout.write("\n");
		for(ArrayList<String> al: descriptorValueMatrix){
			for(int i = 0; i < al.size(); i++){
				if(usefulDescriptor[i]){
					fout.write(al.get(i) + " ");
				}
				if(i == 0){
					//also output the molecule name at the beginning. Cause that's just how kNN rolls.
					fout.write(al.get(molname_index).toString() + " ");
				}
				else{
				}
			}
			fout.write("\n");
		}
		for(int i = 1; i < descriptorNames.size(); i++){
			if(usefulDescriptor[i]){
				fout.write(descriptorMinimum[i] + " ");
			}
		}
		fout.write("\n");
		for(int i = 1; i < descriptorNames.size(); i++){
			if(usefulDescriptor[i]){
				fout.write(descriptorMaximum[i] + " ");
			}
		}
		fout.write("\n");

		/*
		//Print the transposed descriptor matrix (easier to read and debug.)
				for(int i = 0; i < num_descriptors; i++){
					fout.write(descriptorNames.get(i) + " ");
					fout.write("Min: " + descriptorMinimum[i] + " ");
					fout.write("Max: " + descriptorMaximum[i] + " ");
					if(! usefulDescriptor[i]){
						fout.write("USELESS ");
					}
					for(ArrayList<String> al: descriptorValueMatrix){
							
						fout.write(al.get(i) + " ");
					}
					fout.write("\n");
				}
		*/

		fout.close();
	}
	
	public static void MakePredictionDescriptors(String DragonOutputFile, String NormalizedModelingDescriptors, String KnnInputFile) throws Exception{

		///THIS NEEDS SOME FIXING YO
		
		//reads in a Dragon output file (.dragon) and the .x file used to make the model, and makes a kNN input file (.x)
		Utility.writeToDebug("Normalizing Dragon Prediction Descriptors");
		
		File file = new File(DragonOutputFile);
		FileReader dfile = new FileReader(file);
		BufferedReader br = new BufferedReader(dfile);
		File file2 = new File(NormalizedModelingDescriptors);
		FileReader xfile = new FileReader(file2);
		File file3 = new File(KnnInputFile);
		FileWriter outfile = new FileWriter(file3);

		//read in the .dragon file
		ArrayList<String> dfileDescriptorNames = new ArrayList<String>();
		ArrayList< ArrayList<String> > descriptorValueMatrix = new ArrayList< ArrayList<String> >();
		ArrayList<String> descriptorValues = new ArrayList<String>(); //values for each molecule
		
		String line = br.readLine();  //junk line, should say "dragonX: Descriptors"

		//contains some numbers
		line = br.readLine();
		Scanner tok = new Scanner(line);
		int dfileNumMolecules = Integer.parseInt(tok.next()); 
		tok.next(); //just says "2" all the time, no idea what that means, so skip that
		//int num_descriptors = Integer.parseInt(tok.next());
		
		//the descriptor names are on this line
		line = br.readLine();
		tok = new Scanner(line);
		while(tok.hasNext()){
			String dname =  tok.next();
			dfileDescriptorNames.add(dname);
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
			descriptorValueMatrix.add(descriptorValues);
		}
		
		int num_molecules = descriptorValueMatrix.size();
		int num_descriptors = dfileDescriptorNames.size();
		
		dfile.close();

		Utility.writeToDebug("DragonToDescriptors: Done reading .dragon file.");
		
		//read in the name, min, and max of each descriptor from the modeling .x file
		Scanner src = new Scanner(xfile);
		
		int xFileNumCompounds = Integer.parseInt(src.next());
		int xFileNumDescriptors = Integer.parseInt(src.next());

		
		ArrayList<String> xFileDescriptorNames = new ArrayList<String>();
		float[] xFileDescriptorMinimum = new float[xFileNumDescriptors];
		float[] xFileDescriptorMaximum = new float[xFileNumDescriptors];
		

		boolean readingDescriptorNames = true;
		while (src.hasNext()) {
			//reading in descriptor names 
			String temp = src.next();


			if(temp.matches("[0-9]+") && readingDescriptorNames){
				readingDescriptorNames = false;
				//done reading descriptors. Skip past the lines with the compounds in them
				//right down to where the min/max values are.
				for(int i = 0; i < xFileNumCompounds; i++){
					temp = src.nextLine();
				}
				
				//Now at the line containing the min value of each descriptor.
				for(int i = 0; i < xFileNumDescriptors; i++){
					temp = src.next();
					xFileDescriptorMinimum[i] = Float.parseFloat(temp);
				}
				//Now the max
				for(int i = 0; i < xFileNumDescriptors; i++){
					temp = src.next();
					xFileDescriptorMaximum[i] = Float.parseFloat(temp);
				}
			}
			
			if(readingDescriptorNames){
				//still reading descriptors. 			
				xFileDescriptorNames.add(temp);
			 
			//System.out.print(dfileDescriptorNames.get(si) + " ");
			}

		}

		Utility.writeToDebug("DragonToDescriptors: Done reading .x file.");
		
		//Make sure we'll only be using a descriptor from the .dragon file if it was also in the
		//modeling .x file. Each dragon descriptor must map to one of the .x file descriptors.

		int[] dfileDescriptorMap = new int[dfileDescriptorNames.size()];

		int si = 0;
		for(int i = 0; i < xFileDescriptorNames.size(); i++){
			if(dfileDescriptorNames.get(si).equalsIgnoreCase(xFileDescriptorNames.get(i))){
				//Utility.writeToDebug("I: " + i + " - " + xFileDescriptorNames.get(i) + " || si: " + si + " - " + dfileDescriptorNames.get(si));
			}
			while((si < dfileDescriptorNames.size()) && ! dfileDescriptorNames.get(si).equalsIgnoreCase(xFileDescriptorNames.get(i)) ){
				dfileDescriptorMap[si] = -1;
				si++;
			}
			
			if(si < dfileDescriptorNames.size()){
				dfileDescriptorMap[si] = i;
			}
			si++;
		}
		while(si < dfileDescriptorNames.size()){
			dfileDescriptorMap[si] = -1;
			si++;
		}
		Utility.writeToDebug("DragonToDescriptors: Done creating mapping.");		
/*
		//DEBUG: All the mappings are right if this output is true.
		for(int i = 0; i < dfileDescriptorNames.size(); i++){
			if(dfileDescriptorMap[i] != -1){
				System.out.println(dfileDescriptorNames.get(i) + " == " + xFileDescriptorNames.get(dfileDescriptorMap[i]) );
			}	
		}
*/


		//Now we know
		// - Which descriptors should be in the output
		// - What min and max they should be normalized to
		// - And their values for each molecule in the prediction set.
		//So, it's time to normalize the values and output them.

		outfile.write(dfileNumMolecules + " " + xFileDescriptorNames.size() + "\n");
		
		int molname_index = 0;
		for(int i = 0; i < dfileDescriptorNames.size(); i++){
			if(dfileDescriptorMap[i] != -1){
				outfile.write(dfileDescriptorNames.get(i) + " ");
			}
			if(dfileDescriptorNames.get(i).equalsIgnoreCase("molname")){
				molname_index = i;
			}
		}
		outfile.write("\n");

		for(ArrayList<String> al: descriptorValueMatrix){
			for(int i = 0; i < al.size(); i++){
				
				if(i == 0){
					//just output molecule ID numbers, no normalization.
					outfile.write(al.get(i) + " ");
					//molname also needs to be at the beginning
					outfile.write(al.get(molname_index) + " ");
				}
				else{
					float ival;
					try{
						ival = Float.parseFloat(al.get(i));
					}
					catch(NumberFormatException ex){
						ival = 0;
					}
					//normalize to correct descriptor range
					if(dfileDescriptorMap[i] != -1){
						ival = (ival - xFileDescriptorMinimum[dfileDescriptorMap[i]])/(xFileDescriptorMaximum[dfileDescriptorMap[i]] - xFileDescriptorMinimum[dfileDescriptorMap[i]]);
						outfile.write(ival + " ");
					}
				}
			}
			outfile.write("\n");
		}

		//write min/max range normalization information
		//(the same as it was in the model .x file)
		for(int i = 0; i < xFileDescriptorNames.size(); i++){
			outfile.write(xFileDescriptorMinimum[i] + " ");
		}
		outfile.write("\n");
		for(int i = 0; i < xFileDescriptorNames.size(); i++){
			outfile.write(xFileDescriptorMaximum[i] + " ");
		}
		outfile.write("\n");

		outfile.close();
	
	}
}