package edu.unc.ceccr.workflows;

import java.io.*;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Scanner;

/* Take the output of the MolconnZ program, and 
 * generate descriptors for use with kNN.
 * This code is accessed once for model building, and once for predictions.
 * The descriptors must be normalized according to the predictor for predictions but
 * according to the data in model building, so 2 different functions are used depending on
 * whether it's a prediction job or a modeling job.
 * 
 * This should work with any version of MolconnZ, but has only been tested on v4.09.
 */

public class MolconnZToDescriptors{
	
	public static void MakeModelingDescriptors(String MolconnZOutputFile, String KnnInputFile){
		//reads in a MolconnZ output file (.S) and makes a kNN input file (.x)
		Utility.writeToDebug("Normalizing Modeling Descriptors");
		try{
		File file = new File(MolconnZOutputFile);
		FileReader fin = new FileReader(file);
		File file2 = new File(KnnInputFile);
		FileWriter fout = new FileWriter(file2);

		String temp;
		Scanner src = new Scanner(fin);
		ArrayList<String> descriptorNames = new ArrayList<String>();
		ArrayList< ArrayList<String> > descriptorValueMatrix = new ArrayList< ArrayList<String> >();
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
						descriptorValueMatrix.add(descriptorValues);
						descriptorValues = new ArrayList<String>();
						descriptorValues.clear();
					}
					descriptorValues.add(temp);
				}
			}
		}
		descriptorValueMatrix.add(descriptorValues);
		fin.close();

		int num_molecules = descriptorValueMatrix.size();
		int num_descriptors = descriptorNames.size();

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
			if(descriptorNames.get(i).toString().equalsIgnoreCase("molname")){
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


	}catch(Exception ex){
		Utility.writeToDebug(ex);
	}
	
	}



	public static void MakePredictionDescriptors(String MolconnZOutputFile, String NormalizedModelingDescriptors, String KnnInputFile){
		
		//reads in a MolconnZ output file (.S) and the .x file used to make the model, and makes a kNN input file (.x)
		Utility.writeToDebug("Normalizing Prediction Descriptors");
		/*Utility.writeToDebug("MolconnZOutputFile: " + MolconnZOutputFile);
		Utility.writeToDebug("NormalizedModelingDescriptors: " + NormalizedModelingDescriptors);
		Utility.writeToDebug("KnnInputFile: " + KnnInputFile);*/
try{
		File file = new File(MolconnZOutputFile);
		FileReader sfile = new FileReader(file);
		File file2 = new File(NormalizedModelingDescriptors);
		FileReader xfile = new FileReader(file2);
		File file3 = new File(KnnInputFile);
		FileWriter outfile = new FileWriter(file3);

		//read in the .S file
		String temp;
		Scanner src = new Scanner(sfile);
		ArrayList<String> sFileDescriptorNames = new ArrayList<String>();
		ArrayList< ArrayList<String> > descriptorValueMatrix = new ArrayList< ArrayList<String> >();
		ArrayList<String> descriptorValues = new ArrayList<String>(); //values for each molecule
		int sFileNumMolecules = 0;

		boolean readingDescriptorNames = true;
		Utility.writeToDebug("MolconnZToDescriptors: Reading .S file. If this function stops working, heap size is too small.");
		int count = 0;
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
					sFileDescriptorNames.add(temp);
				}
				else{
					if(descriptorValues.size() == sFileDescriptorNames.size()){
						//done reading values for this molecule, we're on the next one now.
						count++;
						descriptorValueMatrix.add(descriptorValues);
						sFileNumMolecules++;
						descriptorValues = new ArrayList<String>();
						descriptorValues.clear();
					}
					descriptorValues.add(temp);
				}
			}
		}
		descriptorValueMatrix.add(descriptorValues);
		sFileNumMolecules++;
		sfile.close();

		Utility.writeToDebug("MolconnZToDescriptors: Done reading .S file.");
		//read in the name, min, and max of each descriptor from the modeling .x file
		src = new Scanner(xfile);
		
		int xFileNumCompounds = Integer.parseInt(src.next());
		int xFileNumDescriptors = Integer.parseInt(src.next());

		
		ArrayList<String> xFileDescriptorNames = new ArrayList<String>();
		float[] xFileDescriptorMinimum = new float[xFileNumDescriptors];
		float[] xFileDescriptorMaximum = new float[xFileNumDescriptors];
		

		readingDescriptorNames = true;
		while (src.hasNext()) {
			//reading in descriptor names 
			temp = src.next();


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
			 
			//System.out.print(sFileDescriptorNames.get(si) + " ");
			}

		}

		Utility.writeToDebug("MolconnZToDescriptors: Done reading .x file.");
		
		//Make sure we'll only be using a descriptor from the .S file if it was also in the
		//modeling .x file. Each .S descriptor must map to one of the .x file descriptors.

		int[] sFileDescriptorMap = new int[sFileDescriptorNames.size()];

		int si = 0;
		for(int i = 0; i < xFileDescriptorNames.size(); i++){
			if(sFileDescriptorNames.get(si).equalsIgnoreCase(xFileDescriptorNames.get(i))){
				//Utility.writeToDebug("I: " + i + " - " + xFileDescriptorNames.get(i) + " || si: " + si + " - " + sFileDescriptorNames.get(si));
			}
			while((si < sFileDescriptorNames.size()) && ! sFileDescriptorNames.get(si).equalsIgnoreCase(xFileDescriptorNames.get(i)) ){
				sFileDescriptorMap[si] = -1;
				si++;
			}
			
			if(si < sFileDescriptorNames.size()){
				sFileDescriptorMap[si] = i;
			}
			si++;
		}
		while(si < sFileDescriptorNames.size()){
			sFileDescriptorMap[si] = -1;
			si++;
		}
		Utility.writeToDebug("MolconnZToDescriptors: Done creating mapping.");		
/*
		//DEBUG: All the mappings are right if this output is true.
		for(int i = 0; i < sFileDescriptorNames.size(); i++){
			if(sFileDescriptorMap[i] != -1){
				System.out.println(sFileDescriptorNames.get(i) + " == " + xFileDescriptorNames.get(sFileDescriptorMap[i]) );
			}	
		}
*/


		//Now we know
		// - Which descriptors should be in the output
		// - What min and max they should be normalized to
		// - And their values for each molecule in the prediction set.
		//So, it's time to normalize the values and output them.

		outfile.write(sFileNumMolecules + " " + xFileDescriptorNames.size() + "\n");
		
		int molname_index = 0;
		for(int i = 0; i < sFileDescriptorNames.size(); i++){
			if(sFileDescriptorMap[i] != -1){
				outfile.write(sFileDescriptorNames.get(i) + " ");
			}
			if(sFileDescriptorNames.get(i).equalsIgnoreCase("molname")){
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
					if(sFileDescriptorMap[i] != -1){
						ival = (ival - xFileDescriptorMinimum[sFileDescriptorMap[i]])/(xFileDescriptorMaximum[sFileDescriptorMap[i]] - xFileDescriptorMinimum[sFileDescriptorMap[i]]);
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

		Utility.writeToDebug("MolconnZToDescriptors: Finished.");
		
	}catch(Exception ex){
		Utility.writeToDebug(ex);
	}
	
	}
}