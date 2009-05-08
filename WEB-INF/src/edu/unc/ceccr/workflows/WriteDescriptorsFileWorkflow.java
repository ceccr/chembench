package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class WriteDescriptorsFileWorkflow{
	//using a set of Descriptors objects, create output files for kNN or SVM
	//containing the descriptors for a dataset.
	//Performs operations on data matrices as well (e.g. range-scaling).
	//All such operations are in-place -- the arguments will have their values modified.
	
	public static void findMinMax(ArrayList<Descriptors> descriptorMatrix, ArrayList<String> descriptorValueMinima, ArrayList<String> descriptorValueMaxima){
		//calculates the descriptorValueMinima and descriptorValueMaxima arrays based on descriptorMatrix
		//used in range scaling and when finding zero-variance descriptors.
		
		//Initialize the min and max values to equal the first compound's descriptors
		descriptorValueMinima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split(" ")));
		descriptorValueMaxima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split(" ")));
		
		//Get the minimum and maximum value for each column.
		//Then call RangeScaleGivenMinMax to get the scaled matrix. Return that.
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split(" ")));
			
			for(int j = 0; j < descriptorValues.size(); j++){
				if(Float.parseFloat(descriptorValues.get(j)) < Float.parseFloat(descriptorValueMinima.get(j))){
					descriptorValueMinima.set(j, descriptorValues.get(j));
				}
				if(Float.parseFloat(descriptorValues.get(j)) > Float.parseFloat(descriptorValueMaxima.get(j))){
					descriptorValueMaxima.set(j, descriptorValues.get(j));
				}
			}
			descriptorValues.clear(); //cleanup
		}
	}
	
	public static void rangeScaleGivenMinMax(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima) {
		//range-scales the values in the descriptor matrix.
		//We know the min and max. Scaled value = ((value - min) / (max-min)).
		
		Utility.writeToDebug("range-scaling descriptor matrix according to given max and min");
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split(" ")));
			for(int j = 0; j < descriptorValues.size(); j++){
				float value = Float.parseFloat(descriptorValues.get(j));
				float min = Float.parseFloat(descriptorValueMinima.get(j));
				float max = Float.parseFloat(descriptorValueMaxima.get(j));
				descriptorValues.set(j, Float.toString(( (value - min) / (max - min) )));
			}
			
			//we need to make the descriptors arraylist into a space separated string
			//ArrayList.toString() gives values separated by ", "
			//so just remove the commas and we're done
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(descriptorValues.toString().replaceAll("[,\\[\\]]", ""));
			descriptorMatrix.set(i, di);
			descriptorValues.clear(); // cleanup
		}
	}
	
	public static void removeZeroVarianceDescriptors(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima,
			ArrayList<String> descriptorNames /* optional argument -- can be null */){
		
		//removes descriptors where the min and max are equal
		//used only during modeling
		Utility.writeToDebug("removing zero-variance descriptors from descriptor matrix");
		
		ArrayList<Integer> zeroVariance = new ArrayList<Integer>();
		for(int i = 0; i < descriptorValueMinima.size(); i++){
			float min = Float.parseFloat(descriptorValueMinima.get(i));
			float max = Float.parseFloat(descriptorValueMaxima.get(i));
			if(max - min < 0.0001){
				zeroVariance.add(1);
			}
			else{
				zeroVariance.add(0);
			}
		}

		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split(" ")));
				
			for(int j = zeroVariance.size() - 1; j >= 0; j--){
				if(zeroVariance.get(j) == 1){
					descriptorValues.remove(j);
				}
			}
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(descriptorValues.toString().replaceAll("[,\\[\\]]", ""));
			descriptorMatrix.set(i, di);
			descriptorValues.clear();
		}

		for(int j = zeroVariance.size() - 1; j >= 0; j--){
			if(zeroVariance.get(j) == 1){
				descriptorValueMinima.remove(j);
				descriptorValueMaxima.remove(j);
				if(descriptorNames != null){
					descriptorNames.remove(j);
				}
			}
		}
	}
	
	public static void removeDescriptorsNotInPredictor(ArrayList<Descriptors> descriptorMatrix, 
			StringBuffer descriptorNameStringBuffer,
			String predictorDescriptorNameString){
		
		//removes any descriptors that weren't in the predictor
		//used only during prediction
		Utility.writeToDebug("removing descriptors that weren't in the predictor from the prediction descriptor matrix");
		
		String descriptorNameString = descriptorNameStringBuffer.toString();
		ArrayList<String> descriptorNames = new ArrayList<String>();
		descriptorNames.addAll(Arrays.asList(descriptorNameString.split(" ")));
		ArrayList<String> predictorDescriptorNames = new ArrayList<String>();
		predictorDescriptorNames.addAll(Arrays.asList(predictorDescriptorNameString.split(" ")));

		//first, create a mapping -- each descriptorName 
		//will either point to the index of a predictorDescriptorName
		//or to nothing (-1).
		ArrayList<Integer> mapping = new ArrayList<Integer>(descriptorNames.size());
		for(int i = 0; i < descriptorNames.size(); i++){
			mapping.add(-1);
		}
		
		int si = 0;
		for(int i = 0; i < predictorDescriptorNames.size(); i++){
			if(descriptorNames.get(si).equalsIgnoreCase(predictorDescriptorNames.get(i))){
				//Utility.writeToDebug("I: " + i + " - " + xFileDescriptorNames.get(i) + " || si: " + si + " - " + sFileDescriptorNames.get(si));
			}
			while((si < descriptorNames.size()) && ! descriptorNames.get(si).equalsIgnoreCase(predictorDescriptorNames.get(i)) ){
				mapping.set(si, -1);
				si++;
			}
			
			if(si < descriptorNames.size()){
				mapping.set(si, i);
			}
			si++;
		}
		while(si < descriptorNames.size()){
			mapping.set(si, -1);
			si++;
		}
		Utility.writeToDebug("done creating mapping.");

		//use the mapping to get rid of descriptors where mapping == -1.
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split(" ")));
			for(int j = mapping.size() - 1; j >= 0; j--){
				if(mapping.get(j) == -1){
					descriptorValues.remove(j);
				}
			}
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(descriptorValues.toString().replaceAll("[,\\[\\]]", ""));
			descriptorMatrix.set(i, di);
			descriptorValues.clear(); // cleanup
		}
		if(descriptorNames != null){
			for(int j = mapping.size() - 1; j >= 0; j--){
				if(mapping.get(j) == -1){
					descriptorNames.remove(j);
				}
			}
		}
		descriptorNameString = descriptorNames.toString().replaceAll("[,\\[\\]]", "");
		descriptorNameStringBuffer.setLength(0);
		descriptorNameStringBuffer.append(descriptorNameString);

	}
	
	public static void readPredictorXFile(StringBuffer predictorDescriptorNameString, 
			ArrayList<String> predictorDescriptorValueMinima, 
			ArrayList<String> predictorDescriptorValueMaxima,
			String predictorXFile) throws Exception{
		//get the descriptor names and min / max values of each descriptor 
		//So, read in the name, min, and max of each descriptor from the modeling .x file
		Utility.writeToDebug("reading predictor .x file");
		
		File file = new File(predictorXFile);
		FileReader xFile = new FileReader(file);
		BufferedReader br = new BufferedReader(xFile);
		
		String line = br.readLine();
		Scanner src = new Scanner(line);
		int xFileNumCompounds = Integer.parseInt(src.next());
		int xFileNumDescriptors = Integer.parseInt(src.next());

		line = br.readLine();
		src = new Scanner(line);
		while(src.hasNext()){
			predictorDescriptorNameString.append(src.next() + " ");
		}

		for(int i = 0; i < xFileNumCompounds; i++){
			line = br.readLine(); //skip all the compounds, we don't care about them
		}

		//get min and max values
		line = br.readLine();
		src = new Scanner(line);
		while(src.hasNext()){
			predictorDescriptorValueMinima.add(src.next());
		}
		line = br.readLine();
		src = new Scanner(line);
		while(src.hasNext()){
			predictorDescriptorValueMaxima.add(src.next());
		}
	}
	
	public static void writeModelingXFile(ArrayList<String> compoundNames, 
			ArrayList<Descriptors> descriptorMatrix, 
			String descriptorNameString, 
			String xFilePath) throws Exception{
		//Perform range-scaling on descriptorMatrix 
		//remove zero-variance descriptors from descriptorMatrix
		//Write a new file at xFilePath containing descriptorMatrix and other data needed for .x file
		//see Developer's Guide in documentation folder for .x file format details.

		//find min/max values for each descriptor
		ArrayList<String> descriptorValueMinima = new ArrayList<String>();
		ArrayList<String> descriptorValueMaxima = new ArrayList<String>();
		findMinMax(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima);
		
		//do range scaling on descriptorMatrix
		rangeScaleGivenMinMax(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima);

		//remove descriptors that are useless to modeling (zero variance)
		ArrayList<String> descriptorNames = new ArrayList<String>();
		descriptorNames.addAll(Arrays.asList(descriptorNameString.split(" ")));
		removeZeroVarianceDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima, descriptorNames);
		
		//write output
		File file = new File(xFilePath);
		FileWriter xFileOut = new FileWriter(file);
		
		xFileOut.write(descriptorMatrix.size() + " " + descriptorNames.size() + "\n"); // numcompounds numdescriptors
		xFileOut.write(descriptorNames.toString().replaceAll("[,\\[\\]]", "") + "\n"); //descriptor names
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			//each line of the descriptors matrix
			xFileOut.write((i+1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
		}
		
		xFileOut.write(descriptorValueMinima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //minima
		xFileOut.write(descriptorValueMaxima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //maxima

		xFileOut.close();
	}
	
	public static void writePredictionXFile(ArrayList<String> compoundNames, 
			ArrayList<Descriptors> descriptorMatrix, 
			String descriptorNameString, 
			String xFilePath,
			String predictorXFilePath) throws Exception{

		//read in the xFile used to make the predictor
		StringBuffer predictorDescriptorNameStringBuffer = new StringBuffer("");
		ArrayList<String> predictorDescriptorValueMinima = new ArrayList<String>();
		ArrayList<String> predictorDescriptorValueMaxima = new ArrayList<String>();
		readPredictorXFile(predictorDescriptorNameStringBuffer, predictorDescriptorValueMinima, predictorDescriptorValueMaxima, predictorXFilePath);
		String predictorDescriptorNameString = predictorDescriptorNameStringBuffer.toString();

		//remove descriptors from prediction set that are not in the predictor
		StringBuffer descriptorNameStringBuffer = new StringBuffer(descriptorNameString);
		removeDescriptorsNotInPredictor(descriptorMatrix, descriptorNameStringBuffer, predictorDescriptorNameString);
		descriptorNameString = descriptorNameStringBuffer.toString();

		//presumably, we should have the number of descriptors in descriptorNameString == predictorDescriptorValueMinima.size()
		//check this to make sure nothing's going weird
		if(predictorDescriptorValueMinima.size() == predictorDescriptorNameString.split(" ").length){
			Utility.writeToDebug("sizes look good: " + predictorDescriptorValueMinima.size()); 
		}
		else{
			Utility.writeToDebug("aww crap. got " + predictorDescriptorValueMinima.size() + " minimum values but " + predictorDescriptorNameString.split(" ").length + " descriptors."); 
		}
		
		//do range scaling on descriptorMatrix
		rangeScaleGivenMinMax(descriptorMatrix, predictorDescriptorValueMinima, predictorDescriptorValueMaxima);
		
		//write output
		File file = new File(xFilePath);
		FileWriter xFileOut = new FileWriter(file);
		
		xFileOut.write(descriptorMatrix.size() + " " + predictorDescriptorValueMinima.size() + "\n"); // numcompounds numdescriptors
		xFileOut.write(descriptorNameString + "\n"); //descriptor names
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			//each line of the descriptors matrix
			xFileOut.write((i+1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
		}
		
		xFileOut.write(predictorDescriptorValueMinima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //minima
		xFileOut.write(predictorDescriptorValueMaxima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //maxima
		
		xFileOut.close();
	}
	
	public static void writeSVMModelingFile(ArrayList<Descriptors> descriptorMatrix){
		//need activity values too
		
	}
	public static void writeSVMPredictionFile(ArrayList<Descriptors> descriptorMatrix){
		//need compound names..?
		
	}

}
