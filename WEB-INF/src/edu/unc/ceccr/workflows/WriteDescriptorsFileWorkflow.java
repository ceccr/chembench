package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.Constants.ScalingTypeEnumeration;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.utilities.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class WriteDescriptorsFileWorkflow{
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
		descriptorValueMinima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split(" ")));
		descriptorValueMaxima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split(" ")));
		
		//initialize the avgs and stddevs to 0
		descriptorValueAvgs.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split(" ")));
		descriptorValueStdDevs.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split(" ")));
		
		for(int j = 0; j < descriptorValueAvgs.size(); j++){
			descriptorValueAvgs.set(j, "0");
			descriptorValueStdDevs.set(j, "0");
		}

		//Get the minimum and maximum value for each column.
		//Get column totals for calculating the averages.
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
				Float totalSoFar = Float.parseFloat(descriptorValueAvgs.get(j));
				descriptorValueAvgs.set(j, "" + (Float.parseFloat(descriptorValues.get(j)) + totalSoFar));
			}
			descriptorValues.clear(); //cleanup
		}
		
		//divide to get averages
		for(int j = 0; j < descriptorValueAvgs.size(); j++){
			descriptorValueAvgs.set(j, "" + (Float.parseFloat(descriptorValueAvgs.get(j)) / descriptorMatrix.size()));
		}
		
		//now go through again to get stddev... what a pain
		//wish there was a faster way
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split(" ")));
			
			for(int j = 0; j < descriptorValues.size(); j++){
				Float mean = Float.parseFloat(descriptorValueAvgs.get(j));
				Float distFromMeanSquared = new Float(Math.pow((Double.parseDouble(descriptorValues.get(j)) - mean), 2));
				descriptorValueStdDevs.set(j, "" + (Float.parseFloat(descriptorValueStdDevs.get(j)) + distFromMeanSquared));
			}
			descriptorValues.clear(); //cleanup
		}
		//divide sum then take sqrt to get stddevs
		for(int j = 0; j < descriptorValueStdDevs.size(); j++){
			double squareDistTotal = Double.parseDouble(descriptorValueStdDevs.get(j));
			descriptorValueStdDevs.set(j, "" +  Math.sqrt( squareDistTotal / descriptorMatrix.size()));
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
				if(max - min != 0){
					descriptorValues.set(j, Float.toString(( (value - min) / (max - min) )));
				}
				//if max - min == 0, the descriptor is zero-variance and will be removed later.
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
	
	public static void autoScaleGivenAvgStdDev(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevsPlusAvgs){
		//subtract the avg from each value
		//then divide by the stddev
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			ArrayList<String> descriptorValues = new ArrayList<String>();
			descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split(" ")));
			
			for(int j = 0; j < descriptorValues.size(); j++){
				Float avg = Float.parseFloat(descriptorValueAvgs.get(j));
				Float stdDevPlusAvg = Float.parseFloat(descriptorValueStdDevsPlusAvgs.get(j));
				Float val = Float.parseFloat(descriptorValues.get(j));
				if((stdDevPlusAvg - avg) != 0){
					descriptorValues.set(j, "" + ( (val - avg) / (stdDevPlusAvg - avg) ));
				}
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


	public static void removeHighlyCorellatedDescriptors(ArrayList<Descriptors> descriptorMatrix, 
			ArrayList<String> descriptorValueMinima, 
			ArrayList<String> descriptorValueMaxima,
			ArrayList<String> descriptorValueAvgs, 
			ArrayList<String> descriptorValueStdDevs,
			ArrayList<String> descriptorNames, /* optional argument -- can be null */
			Float corellationCutoff){
		
		//lol write this later
		//will end up using the same algorithm as "remove high sequence identity" thing.
		//that was a recursive one, right...? how *did* I do that?
		//Right, here it is. Algorithm in perl:
		
		/*
		 if($#ARGV + 1 != 4 || $ARGV[2] < 0 || ($ARGV[3] ne "s" && $ARGV[3] ne "i")){
	#didn't provide 4 arguments, so print usage info
	print "\nThis program runs a maximal independent set algorithm to eliminate \nsequences that exceed the threshold similarity or identity to any other sequence.\n";
	print "It processes outputs from 'needle' (Needleman-Wunsch alignment tool), \npart of the free Emboss biology tool suite.\n\n";
	print "Usage: 'seqfilter.pl infile outfile percentage i' (for identity filter)\n";
	print "Or: 'seqfilter.pl infile outfile percentage s' (for similarity filter)\n\n";
	print "Example: 'seqfilter.pl sample_input.score sample_input_90_s.score 90.5 s'\n";
	exit;
}

$first = true;

#read input file

$input_file = "$ARGV[0]";

$running = "true";
while($running){
	open(FH, $input_file) or die "can't open input file: $input_file\n";
	%tallies = {};
	while($line = <FH>){
		if($line =~ m/\# 1:\s+(\S+)/){
			$p1 = $1;
		}
		if($line =~ m/\# 2:\s+(\S+)/){
			$p2 = $1;
		}
		if($line =~ m/\# Identity:\s+\S+\s+\(\s*(\S+)\s*%\)/){
			if(($ARGV[3] eq "i") && ($1 >= $ARGV[2]) && ($p1 ne $p2)){
				$tallies{$p1}++;
				$tallies{$p2}++;
			}
		}
		if($line =~ m/\# Similarity:\s+\S+\s+\(\s*(\S+)\s*%\)/){
			if(($ARGV[3] eq "s") && ($1 >= $ARGV[2]) && ($p1 ne $p2)){
				$tallies{$p1}++;
				$tallies{$p2}++;
			}
		}

	}
	close(FH);

	#find highest valued key (i.e. protein with the most above-threshold connections to other proteins)
	$max_value = 0;
	$max_key = "";
	while(my($key, $value) = each(%tallies)) {
		if($value > $max_value){
			$max_key = $key;
			$max_value = $value;
		}
	}
	if($max_key eq ""){
		#finished; we have eliminated all proteins above threshold
		#move temp file to output file location
		$outfile = $ARGV[1];
		if($infile eq $ARGV[0]){
			print `cp $input_file $outfile`;
		}
		else{
			print `mv $input_file $outfile`;
		}
		$running = "";
	}
	else{
		print "Removing protein $max_key with tally $max_value.\n";
		#reread input file and print it out, skipping any sections that involve the highest-value protein
		open(FH, $input_file) or die "can't open file: $input_file";
		open(TEMPOUT, ">$ARGV[1].temp.out")  or die "can't open temp file: $ARGV[1].temp.out\n";

		$section = "";
		while($line = <FH>){
			if($line =~ m/\# 1:\s+(\S+)/){
				$p1 = $1;
				$section = $line;
			}
			if($line =~ m/\# 2:\s+(\S+)/){
				$p2 = $1;
				$section .= $line;
			}
			if($line =~ m/\# Identity:/){
				$section .= $line;
				
			}
			if($line =~ m/\# Similarity:/){
				$section .= $line;
				if($p1 ne $max_key && $p2 ne $max_key){
					print TEMPOUT $section;
				}
			}
		}
		close(TEMPOUT);
		close(FH);
		print `mv $ARGV[1].temp.out $ARGV[1].temp.in`;
	}
	$input_file = "$ARGV[1].temp.in"; #read from the temp file for the next iteration

}
		 */
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
			Utility.writeToDebug("1 i: " + i);
			/*if(si < descriptorNames.size() && descriptorNames.get(si).equalsIgnoreCase(predictorDescriptorNames.get(i))){
				Utility.writeToDebug("I: " + i + " - " + predictorDescriptorNames.get(i) + " || si: " + si + " - " + descriptorNames.get(si));
			}*/
			while((si < descriptorNames.size()) && ! descriptorNames.get(si).equalsIgnoreCase(predictorDescriptorNames.get(i)) ){
				mapping.set(si, -1);
				si++;
			}
			Utility.writeToDebug("2 i: " + i);
			
			if(si < descriptorNames.size()){
				mapping.set(si, i);
			}
			si++;
		}
		while(si < descriptorNames.size()){
			Utility.writeToDebug("3 si: " + si);
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
			ArrayList<String> predictorDescriptorValueAvgs, 
			ArrayList<String> predictorDescriptorValueStdDevs,
			String predictorScaleType,
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
			String corellationCutoff) throws Exception{
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
			descriptorValueStdDevPlusAvgs.add("" + (stddev + avg));
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
		descriptorNames.addAll(Arrays.asList(descriptorNameString.split(" ")));
		
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
		if(Float.parseFloat(corellationCutoff) < 1){
			removeHighlyCorellatedDescriptors(descriptorMatrix, 
					descriptorValueMinima, descriptorValueMaxima, 
					descriptorValueAvgs, descriptorValueStdDevPlusAvgs,
					descriptorNames, Float.parseFloat(corellationCutoff));
		}
		
		//write output
		File file = new File(xFilePath);
		FileWriter xFileOut = new FileWriter(file);
		
		xFileOut.write(descriptorMatrix.size() + " " + descriptorNames.size() + "\n"); // numcompounds numdescriptors
		xFileOut.write(descriptorNames.toString().replaceAll("[,\\[\\]]", "") + "\n"); //descriptor names
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			//each line of the descriptors matrix
			xFileOut.write((i+1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
		}

		if(scalingType.equalsIgnoreCase(Constants.RANGESCALING)){
			xFileOut.write(descriptorValueMinima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //minima
			xFileOut.write(descriptorValueMaxima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //maxima
		}
		else if(scalingType.equalsIgnoreCase(Constants.AUTOSCALING)){
			xFileOut.write(descriptorValueAvgs.toString().replaceAll("[,\\[\\]]", "") + "\n"); //averages
			xFileOut.write(descriptorValueStdDevPlusAvgs.toString().replaceAll("[,\\[\\]]", "") + "\n"); //standard deviations minus averages
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
		
		int numDescriptors = predictorDescriptorNameString.split(" ").length;
		xFileOut.write(descriptorMatrix.size() + " " + numDescriptors + "\n"); // numcompounds numdescriptors
		xFileOut.write(descriptorNameString + "\n"); //descriptor names
		
		for(int i = 0; i < descriptorMatrix.size(); i++){
			//each line of the descriptors matrix
			xFileOut.write((i+1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
		}
		
		if(predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)){
			xFileOut.write(predictorDescriptorValueMinima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //minima
			xFileOut.write(predictorDescriptorValueMaxima.toString().replaceAll("[,\\[\\]]", "") + "\n"); //maxima
		}
		else if(predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)){
			xFileOut.write(predictorDescriptorValueAvgs.toString().replaceAll("[,\\[\\]]", "") + "\n"); //avgs
			xFileOut.write(predictorDescriptorValueStdDevsPlusAvgs.toString().replaceAll("[,\\[\\]]", "") + "\n"); //stddevs + avgs
		}
		xFileOut.close();
	}
	
	public static void writeSVMModelingFile(ArrayList<Descriptors> descriptorMatrix){
		//need activity values too
		
	}
	public static void writeSVMPredictionFile(ArrayList<Descriptors> descriptorMatrix){
		//need compound names..?
		
	}

}
