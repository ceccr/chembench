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
}