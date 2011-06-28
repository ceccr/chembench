package edu.unc.ceccr.workflows.descriptors;

import java.io.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.utilities.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class WriteDescriptors{
	//using a set of Descriptors objects, create output files for kNN or SVM
	//containing the descriptors for a dataset.
	//Performs operations on data matrices as well (e.g. range-scaling).
	//All such operations are in-place -- the arguments will have their values modified.
	
	public static void findMinMaxAvgStdDev(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima, 
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevs){
		//calculates the descriptorValueMinima and descriptorValueMaxima arrays based on descriptorMatrix
		//used in scaling and when finding zero-variance descriptors.
		
		//Initialize the min and max values to equal the first compound's descriptors
		descriptorValueMinima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));
		descriptorValueMaxima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));
		
		//initialize the avgs and stddevs to 0
		descriptorValueAvgs.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));
		descriptorValueStdDevs.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));
		
		for(int j = 0; j < descriptorValueAvgs.size(); j++){
			descriptorValueAvgs.set(j, "0");
			descriptorValueStdDevs.set(j, "0");
		}

		//Get the minimum and maximum value for each column.
		//Get column totals for calculating the averages.
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
			
			for(int j = 0; j < descriptorValues.size(); j++){
				if(Float.parseFloat(descriptorValues.get(j)) < Float.parseFloat(descriptorValueMinima.get(j))){
					descriptorValueMinima.set(j, descriptorValues.get(j));
				}
				if(Float.parseFloat(descriptorValues.get(j)) > Float.parseFloat(descriptorValueMaxima.get(j))){
					descriptorValueMaxima.set(j, descriptorValues.get(j));
				}
				Float totalSoFar = Float.parseFloat(descriptorValueAvgs.get(j));
				
				descriptorValueAvgs.set(j, Utility.floatToString(Float.parseFloat(descriptorValues.get(j)) + totalSoFar));
			}
			descriptorValues.clear(); //cleanup
		}
		
		//divide to get averages
		for(int j = 0; j < descriptorValueAvgs.size(); j++){
			descriptorValueAvgs.set(j, Utility.floatToString(Float.parseFloat(descriptorValueAvgs.get(j)) / descriptorMatrix.size()));
		}
		
		//now go through again to get stddev... what a pain
		//wish there was a faster way
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
			
			for(int j = 0; j < descriptorValues.size(); j++){
				Float mean = Float.parseFloat(descriptorValueAvgs.get(j));
				Float distFromMeanSquared = new Float(Math.pow((Double.parseDouble(descriptorValues.get(j)) - mean), 2));
				descriptorValueStdDevs.set(j, Utility.floatToString(Float.parseFloat(descriptorValueStdDevs.get(j)) + distFromMeanSquared));
			}
			descriptorValues.clear(); //cleanup
		}
		//divide sum then take sqrt to get stddevs
		for(int j = 0; j < descriptorValueStdDevs.size(); j++){
			double squareDistTotal = Double.parseDouble(descriptorValueStdDevs.get(j));
			descriptorValueStdDevs.set(j, Utility.doubleToString(Math.sqrt( squareDistTotal / descriptorMatrix.size())));
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
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
			for(int j = 0; j < descriptorValues.size(); j++){
				float value = Float.parseFloat(descriptorValues.get(j));
				float min = Float.parseFloat(descriptorValueMinima.get(j));
				float max = Float.parseFloat(descriptorValueMaxima.get(j));
				if(max - min != 0){
					descriptorValues.set(j, Utility.floatToString((value - min) / (max - min)));
				}
				//if max - min == 0, the descriptor is zero-variance and will be removed later.
			}
			
			//we need to make the descriptors arraylist into a space separated string
			//ArrayList.toString() gives values separated by ", "
			//so just remove the commas and we're done
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));
			descriptorMatrix.set(i, di);
			descriptorValues.clear(); // cleanup
		}
	}
	
	public static void autoScaleGivenAvgStdDev(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevsPlusAvgs){
		//subtract the avg from each value
		//then divide by the stddev
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
			
			for(int j = 0; j < descriptorValues.size(); j++){
				Float avg = Float.parseFloat(descriptorValueAvgs.get(j));
				Float stdDevPlusAvg = Float.parseFloat(descriptorValueStdDevsPlusAvgs.get(j));
				Float val = Float.parseFloat(descriptorValues.get(j));
				if((stdDevPlusAvg - avg) != 0){
					descriptorValues.set(j, Utility.floatToString((val - avg) / (stdDevPlusAvg - avg)));
				}
			}
			
			//we need to make the descriptors arraylist into a space separated string
			//ArrayList.toString() gives values separated by ", "
			//so just remove the commas and we're done
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));
			descriptorMatrix.set(i, di);
			descriptorValues.clear(); // cleanup
		}
	}

	private static double findCorrelation(ArrayList<Double> d1, ArrayList<Double> d2){
	    double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = d1.get(0);
        double mean_y = d2.get(0);
        for(int i=2; i < d1.size() + 1; i++){
            double sweep = Double.valueOf(i-1) / i;
            double delta_x = d1.get(i-1) - mean_x;
            double delta_y = d2.get(i-1) - mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x/d1.size());
        double pop_sd_y = (double) Math.sqrt(sum_sq_y/d1.size());
        double cov_x_y = sum_coproduct / d1.size();
        result = cov_x_y / (pop_sd_x*pop_sd_y);
        return result;
	}

	public static void removeHighlyCorellatedDescriptors(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima,
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevs,
			ArrayList<String> descriptorNames, /* optional argument -- can be null */
			Float correlationCutoff){

		//first thing: we need to transpose the descriptor matrix.
		//By default, it's organized by compound - we need it organized by descriptor.
		ArrayList< ArrayList<Double> > descriptorMatrixT = new ArrayList< ArrayList<Double> >();
		
		//populate the first values of each row in descriptorMatrix
		String[] sa = descriptorMatrix.get(0).getDescriptorValues().split("\\s+");
		for(int i = 0; i < sa.length; i++){
			ArrayList<Double> doubleArray = new ArrayList<Double>();
			doubleArray.add(Double.parseDouble(sa[i]));
			descriptorMatrixT.add(doubleArray);
		}
		
		//now go through the rest of the descriptorMatrix and add in each value
		for(int i = 1; i < descriptorMatrix.size(); i++){
			sa = descriptorMatrix.get(i).getDescriptorValues().split("\\s+");
			for(int j = 0; j < sa.length; j++){
				descriptorMatrixT.get(j).add(Double.parseDouble(sa[j]));
			}
		}

		ArrayList<Integer> removedDescriptorIndexes = new ArrayList<Integer>();
		boolean done = false;
		while(!done){
			//find the one descriptor with the most high correlations to others and remove it. 
			
			int[] counts = new int[descriptorMatrixT.size()];
			for(int i = 0; i < counts.length; i++){
				counts[i] = 0;
			}
			
			for(int i = 0; i < descriptorMatrixT.size(); i++){
				for(int j = i + 1; j < descriptorMatrixT.size(); j++){
					double correlation = findCorrelation(descriptorMatrixT.get(i), descriptorMatrixT.get(j));
					if(correlation > correlationCutoff){
						counts[j]++;
						counts[i]++;
					}
				}
			}
			
			int max_count = 0; 
			int max_index = -1;
			for(int i = 0; i < counts.length; i++){
				if(counts[i] > max_count){
					max_index = i;
					max_count = counts[i];
				}
			}
			if(max_index == -1){
				done = true;
			}
			else{
				//remove descriptor with largest number of correlations
				removedDescriptorIndexes.add(max_index);
				descriptorMatrixT.remove(max_index);
				
				if(descriptorValueMinima != null){
					descriptorValueMinima.remove(max_index);
				}
				if(descriptorValueMaxima != null){
					descriptorValueMaxima.remove(max_index);
				}
				if(descriptorNames != null){
					descriptorNames.remove(max_index);
				}
				if(descriptorValueAvgs != null){
					descriptorValueAvgs.remove(max_index);
				}
				if(descriptorValueStdDevs != null){
					descriptorValueStdDevs.remove(max_index);
				}
			}
		}
		Collections.sort(removedDescriptorIndexes);
		
		//now, transpose the descriptor matrix back
		ArrayList<String> descriptorMatrixTT = new ArrayList<String>();
		
		for(int i = 0; i < descriptorMatrixT.get(0).size(); i++){
			String as = Utility.doubleToString(descriptorMatrixT.get(0).get(i));
			descriptorMatrixTT.add(as);
		}
		
		for(int i = 1; i < descriptorMatrixT.size(); i++){
			for(int j = 0; j < descriptorMatrixT.get(i).size(); j++){
				descriptorMatrixTT.set(j, (descriptorMatrixTT.get(j) + " " + descriptorMatrixT.get(i).get(j)));
			}
		}
		
		//and put it back into the original descriptor matrix
		for(int i = 0; i < descriptorMatrix.size(); i++){
			descriptorMatrix.get(i).setDescriptorValues(descriptorMatrixTT.get(i));
		}
		
	}
	

	public static void removeLowStdDevDescriptors(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima,
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevs,
			ArrayList<String> descriptorNames, /* optional argument -- can be null */
			Float stdDevCutoff){
		
		//lol write this later
		//should be easy, just like removeZeroVariance but with a stddev cutoff instead
		//eventually merge into removeZeroVariance cause they do pretty much the same thing
	}
	
	public static void removeZeroVarianceDescriptors(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima,
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevs,
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
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
			
			for(int j = zeroVariance.size() - 1; j >= 0; j--){
				if(zeroVariance.get(j) == 1){
					descriptorValues.remove(j);
				}
			}
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));
			descriptorMatrix.set(i, di);
			descriptorValues.clear();
		}

		for(int j = zeroVariance.size() - 1; j >= 0; j--){
			if(zeroVariance.get(j) == 1){
				descriptorValueMinima.remove(j);
				descriptorValueMaxima.remove(j);
				descriptorValueAvgs.remove(j);
				descriptorValueStdDevs.remove(j);
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
		descriptorNames.addAll(Arrays.asList(descriptorNameString.split("\\s+")));
		ArrayList<String> predictorDescriptorNames = new ArrayList<String>();
		predictorDescriptorNames.addAll(Arrays.asList(predictorDescriptorNameString.split("\\s+")));

		//first, create a mapping -- each descriptorName 
		//will either point to the index of a predictorDescriptorName
		//or to nothing (-1).
		ArrayList<Integer> mapping = new ArrayList<Integer>(descriptorNames.size());
		for(int i = 0; i < descriptorNames.size(); i++){
			mapping.add(-1);
		}
		
		int si = 0;
		for(int i = 0; i < predictorDescriptorNames.size(); i++){
			
			//skip all non-matching ones
			while((si < descriptorNames.size()) && ! descriptorNames.get(si).equalsIgnoreCase(predictorDescriptorNames.get(i)) ){
				//Utility.writeToDebug("No match; skipping predictor descriptor: " + i + " - " + predictorDescriptorNames.get(i) + " || Input descriptor: " + si + " - " + descriptorNames.get(si));
				mapping.set(si, -1);
				si++;
			}
			
			//find a match
			if(si < descriptorNames.size()){
				//Utility.writeToDebug("Match found! Predictor descriptor: " + i + " - " + predictorDescriptorNames.get(i) + " || Input descriptor: " + si + " - " + descriptorNames.get(si));
				mapping.set(si, i);
				si++;
			}
		}
		
		while(si < descriptorNames.size()){
			mapping.set(si, -1);
			si++;
		}
		Utility.writeToDebug("done creating mapping.");

		//use the mapping to get rid of descriptors where mapping == -1.
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
			for(int j = mapping.size() - 1; j >= 0; j--){
				if(mapping.get(j) == -1 && j < descriptorValues.size()){
					//Utility.writeToDebug("removing descriptor " + j + " array size: " + descriptorValues.size());
					descriptorValues.remove(j);
				}
			}
			Descriptors di = descriptorMatrix.get(i);
			di.setDescriptorValues(Utility.StringArrayListToString(descriptorValues));
			descriptorMatrix.set(i, di);
			descriptorValues.clear(); // cleanup
		}
		if(descriptorNames != null){
			for(int j = mapping.size() - 1; j >= 0; j--){
				if(mapping.get(j) == -1){
					//Utility.writeToDebug("removing descriptor index " + j + " array size: " + descriptorNames.size());
					descriptorNames.remove(j);
				}
			}
		}
		descriptorNameString = Utility.StringArrayListToString(descriptorNames);
		descriptorNameStringBuffer.setLength(0);
		descriptorNameStringBuffer.append(descriptorNameString);

	}
	
	public static void readPredictorXFile(StringBuffer predictorDescriptorNameString, 
			ArrayList<String> predictorDescriptorValueMinima, 
			ArrayList<String> predictorDescriptorValueMaxima,
			ArrayList<String> predictorDescriptorValueAvgs, 
			ArrayList<String> predictorDescriptorValueStdDevs,
			String predictorScaleType,
			String predictorXFile) throws Exception{
		//get the descriptor names and min / max values of each descriptor 
		//So, read in the name, min, and max of each descriptor from the modeling .x file
		Utility.writeToDebug("reading predictor .x file");
		Utility.writeToDebug("predictorXFile " + predictorXFile);
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

		if(predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)){
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
		else if(predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)){
			//get avg and stdDev values
			line = br.readLine();
			src = new Scanner(line);
			while(src.hasNext()){
				predictorDescriptorValueAvgs.add(src.next());
			}
			line = br.readLine();
			src = new Scanner(line);
			while(src.hasNext()){
				predictorDescriptorValueStdDevs.add(src.next());
			}
		}
	}
	
	public static void writeModelingXFile(ArrayList<String> compoundNames, 
			ArrayList<Descriptors> descriptorMatrix, 
			String descriptorNameString, 
			String xFilePath,
			String scalingType,
			String stdDevCutoff, 
			String correlationCutoff) throws Exception{
		//Perform scaling on descriptorMatrix 
		//remove zero-variance descriptors from descriptorMatrix
		//Write a new file at xFilePath containing descriptorMatrix and other data needed for .x file
		//see Developer's Guide in documentation folder for .x file format details.

		//find min/max values for each descriptor
		ArrayList<String> descriptorValueMinima = new ArrayList<String>();
		ArrayList<String> descriptorValueMaxima = new ArrayList<String>();
		ArrayList<String> descriptorValueAvgs = new ArrayList<String>();
		ArrayList<String> descriptorValueStdDevs = new ArrayList<String>();
		
		findMinMaxAvgStdDev(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima, descriptorValueAvgs, descriptorValueStdDevs);
		
		//To maximize compatibility, what we actually store is the
		//value of (StdDev + average) on the last line.
		//This makes sense (I swear!)
		
		// Consider the process of range scaling: To scale 
		// prediction descriptors so that they're in the same range as the predictor's descriptors,
		// for each value you do:
		// scaled_value = (descriptor_value - min) / (max - min), right?
		// Well, we anticipate that other software is going to be using our modeling .x files.
		// Since range scaling is a long-held tradition in our lab, that software might end up
		// doing the same scaling process as range scaling:
		// scaled_value = (descriptor_value - number_on_first_line) / (number_on_second_line - number_on_first_line)
		// And we can make that work with the autoscaling as well!
		
		// To autoscale, we do:
		// scaled_value = descriptor_value - average) / (standard_deviation)
		// So if we make the second line (standard_deviation + average), then we can still do
		// scaled_value = (descriptor_value - number_on_first_line) / (number_on_second_line - number_on_first_line)
		// but now it's
		// scaled_value = descriptor_value - average) / ((standard_deviation + average) - average)
		// and that happily gives us a scaled result.
		// The process for restoring descriptors to their unscaled state will also be 
		// identical in both rangescale and autoscale with this standard. 
		//(Figuring out why this is true is left as an exercise to the reader.)
		
		ArrayList<String> descriptorValueStdDevPlusAvgs = new ArrayList<String>();
		for(int i = 0; i < descriptorValueStdDevs.size(); i++){
			Float stddev = Float.parseFloat(descriptorValueStdDevs.get(i));
			Float avg = Float.parseFloat(descriptorValueStdDevs.get(i));
			descriptorValueStdDevPlusAvgs.add(Utility.floatToString((stddev + avg)));
		}
		
		//do scaling on descriptorMatrix
		if(scalingType.equalsIgnoreCase(Constants.RANGESCALING)){
			rangeScaleGivenMinMax(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima);
		}
		else if(scalingType.equalsIgnoreCase(Constants.AUTOSCALING)){
			autoScaleGivenAvgStdDev(descriptorMatrix, descriptorValueAvgs, descriptorValueStdDevPlusAvgs);
		}
		else if(scalingType.equalsIgnoreCase(Constants.NOSCALING)){
			//don't do anything!
		}
		
		//remove descriptors that are useless to modeling (zero variance)
		ArrayList<String> descriptorNames = new ArrayList<String>();
		descriptorNames.addAll(Arrays.asList(descriptorNameString.split("\\s+")));
		
		removeZeroVarianceDescriptors(descriptorMatrix, 
				descriptorValueMinima, descriptorValueMaxima, 
				descriptorValueAvgs, descriptorValueStdDevPlusAvgs,
				descriptorNames);
		
		if(Float.parseFloat(stdDevCutoff) > 0){
			removeLowStdDevDescriptors(descriptorMatrix, 
					descriptorValueMinima, descriptorValueMaxima, 
					descriptorValueAvgs, descriptorValueStdDevPlusAvgs,
					descriptorNames, Float.parseFloat(stdDevCutoff));
		}
		if(Float.parseFloat(correlationCutoff) < 1){
			removeHighlyCorellatedDescriptors(descriptorMatrix, 
					descriptorValueMinima, descriptorValueMaxima, 
					descriptorValueAvgs, descriptorValueStdDevPlusAvgs,
					descriptorNames, Float.parseFloat(correlationCutoff));
		}
		
		//write output
		File file = new File(xFilePath);
		FileWriter xFileOut = new FileWriter(file);
		
		xFileOut.write(descriptorMatrix.size() + " " + descriptorNames.size() + "\n"); // numcompounds 
		xFileOut.write(Utility.StringArrayListToString(descriptorNames) + "\n");
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			//each line of the descriptors matrix
			xFileOut.write((i+1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
		}

		if(scalingType.equalsIgnoreCase(Constants.RANGESCALING)){
			xFileOut.write(Utility.StringArrayListToString(descriptorValueMinima) + "\n");
			xFileOut.write(Utility.StringArrayListToString(descriptorValueMaxima) + "\n");
		}
		else if(scalingType.equalsIgnoreCase(Constants.AUTOSCALING)){
			xFileOut.write(Utility.StringArrayListToString(descriptorValueAvgs) + "\n");
			xFileOut.write(Utility.StringArrayListToString(descriptorValueStdDevPlusAvgs) + "\n");
		}
		
		xFileOut.close();
	}
	
	public static void writePredictionXFile(ArrayList<String> compoundNames, 
			ArrayList<Descriptors> descriptorMatrix, 
			String descriptorNameString, 
			String xFilePath,
			String predictorXFilePath,
			String predictorScaleType) throws Exception{

		//read in the xFile used to make the predictor
		StringBuffer predictorDescriptorNameStringBuffer = new StringBuffer("");
		ArrayList<String> predictorDescriptorValueMinima = new ArrayList<String>();
		ArrayList<String> predictorDescriptorValueMaxima = new ArrayList<String>();
		ArrayList<String> predictorDescriptorValueAvgs = new ArrayList<String>();
		ArrayList<String> predictorDescriptorValueStdDevsPlusAvgs = new ArrayList<String>();
		
		readPredictorXFile(predictorDescriptorNameStringBuffer, predictorDescriptorValueMinima, predictorDescriptorValueMaxima, predictorDescriptorValueAvgs, predictorDescriptorValueStdDevsPlusAvgs, predictorScaleType, predictorXFilePath);
		String predictorDescriptorNameString = predictorDescriptorNameStringBuffer.toString();
		
		//remove descriptors from prediction set that are not in the predictor
		StringBuffer descriptorNameStringBuffer = new StringBuffer(descriptorNameString);
		removeDescriptorsNotInPredictor(descriptorMatrix, descriptorNameStringBuffer, predictorDescriptorNameString);
		descriptorNameString = descriptorNameStringBuffer.toString();
	
		if(predictorDescriptorNameString.split("\\s+").length != descriptorNameString.split("\\s+").length){
			Utility.writeToDebug("WARNING: predictor had " + predictorDescriptorNameString.split("\\s+").length + " descriptors and output has " +  descriptorNameString.split("\\s+").length);
		}
		
		//do range scaling on descriptorMatrix
		if(predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)){
			rangeScaleGivenMinMax(descriptorMatrix, predictorDescriptorValueMinima, predictorDescriptorValueMaxima);
		}
		else if(predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)){
			autoScaleGivenAvgStdDev(descriptorMatrix, predictorDescriptorValueAvgs, predictorDescriptorValueStdDevsPlusAvgs);
		}
		else if(predictorScaleType.equalsIgnoreCase(Constants.NOSCALING)){
			//don't do anything
		}
			
		//write output
		File file = new File(xFilePath);
		FileWriter xFileOut = new FileWriter(file);
		
		xFileOut.write(descriptorMatrix.size() + " " + descriptorNameString.split("\\s+").length + "\n"); // numcompounds numdescriptors
		xFileOut.write(descriptorNameString + "\n"); //descriptor names
		
		try{
			for(int i = 0; i < descriptorMatrix.size(); i++){
				//each line of the descriptors matrix
				xFileOut.write((i+1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
			}
		}
		catch(Exception ex){
			Utility.writeToDebug("descriptorMatrix.size: " + descriptorMatrix.size() + " compoundNames.size: " + compoundNames.size());
			Utility.writeToDebug(ex);
		}
		
		if(predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)){
			xFileOut.write(Utility.StringArrayListToString(predictorDescriptorValueMinima) + "\n");
			xFileOut.write(Utility.StringArrayListToString(predictorDescriptorValueMaxima) + "\n");
		}
		else if(predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)){
			xFileOut.write(Utility.StringArrayListToString(predictorDescriptorValueAvgs) + "\n");
			xFileOut.write(Utility.StringArrayListToString(predictorDescriptorValueStdDevsPlusAvgs) + "\n");
		}
		xFileOut.close();
	}


}