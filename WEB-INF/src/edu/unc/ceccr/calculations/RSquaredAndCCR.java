package edu.unc.ceccr.calculations;

import java.io.*;

import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class RSquaredAndCCR{
	
	public static ArrayList<Double> calculateResiduals(ArrayList<ExternalValidation> externalValidationList) {
		ArrayList<Double> residuals = new ArrayList<Double>();
		
		Iterator<ExternalValidation> eit = externalValidationList.iterator();
		int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
		int numExtValuesWithNoModels = 0;
		while(eit.hasNext()){
			ExternalValidation e = eit.next();
			if(e.getNumModels() != 0){
				Double residual = new Double(e.getActualValue() - e.getPredictedValue());
				residuals.add(residual);
			}
			else{
				numExtValuesWithNoModels++;
				residuals.add(Double.NaN);
			}
			String predictedValue = DecimalFormat.getInstance().format(e.getPredictedValue()).replaceAll(",", "");
			e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));  
			if(! e.getStandDev().equalsIgnoreCase("No value")){
				e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
			}
		}
		if(numExtValuesWithNoModels == externalValidationList.size()){
			//all external predictions were empty, meaning there were no good models.
			return null;
		}
		
		return residuals;
	}
	
	public static Double calculateRSquared(ArrayList<ExternalValidation> externalValidationList, ArrayList<Double> residuals){
		
		Double avg = 0.0;
		for(ExternalValidation ev : externalValidationList){
			avg += ev.getActualValue();
		}
		avg /= externalValidationList.size();
		Double ssErr = 0.0;
		for(Double residual : residuals){
			if(! residual.isNaN()){
				ssErr += residual * residual;
			}
		}
		Double ssTot = 0.0;
		for(ExternalValidation ev : externalValidationList){
			ssTot += (ev.getActualValue() - avg) * (ev.getActualValue() - avg);
		}
		Double rSquared = 0.0;
		if(ssTot != 0){
			rSquared = (1 - (ssErr / ssTot));
		}
		
		return rSquared;
	}

	public static ConfusionMatrix calculateConfusionMatrix(ArrayList<ExternalValidation> externalValidationList){
		
		//scan through to find the unique observed values
		ArrayList<String> uniqueObservedValues = new ArrayList<String>();
		for(ExternalValidation ev : externalValidationList){
			int observedValue = Math.round(ev.getActualValue());
			int predictedValue = Math.round(ev.getPredictedValue());
			if(! uniqueObservedValues.contains("" + observedValue)){
				uniqueObservedValues.add("" + observedValue);
			}
			//if a value is predicted but not observed, we still need
			//a spot in the matrix for that, so make a spot for those too.
			if(! uniqueObservedValues.contains("" + predictedValue)){
				uniqueObservedValues.add("" + predictedValue);
			}
		}
		
		//set up a confusion matrix to store counts of each (observed, predicted) possibility
		ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
		
		//make a matrix of zeros
		for(int i = 0; i < uniqueObservedValues.size(); i++){
			ArrayList<Integer> row = new ArrayList<Integer>();
			for(int j = 0; j < uniqueObservedValues.size(); j++){
				row.add(0);
			}
			matrix.add(row);
		}
		
		double CCR = 0.0;	
		HashMap<Integer, Integer> correctPredictionCounts = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> observedValueCounts = new HashMap<Integer, Integer>();
		
		//populate the confusion matrix and count values needed to calculate CCR
		for(ExternalValidation ev : externalValidationList){
			//for each observed-predicted pair, update
			//the confusion matrix accordingly
			int observedValue = Math.round(ev.getActualValue());
			int predictedValue = Math.round(ev.getPredictedValue());
			int observedValueIndex = uniqueObservedValues.indexOf("" + observedValue);
			int predictedValueIndex = uniqueObservedValues.indexOf("" + predictedValue);
			int previousCount = matrix.get(observedValueIndex).get(predictedValueIndex);
			matrix.get(observedValueIndex).set(predictedValueIndex, previousCount+1);
			
			if(observedValueCounts.containsKey(observedValue)){
				observedValueCounts.put(observedValue, observedValueCounts.get(observedValue) + 1);
			}
			else{
				observedValueCounts.put(observedValue, 1);
			}
			
			if(predictedValue == observedValue){
				if(correctPredictionCounts.containsKey(observedValue)){
					correctPredictionCounts.put(observedValue, correctPredictionCounts.get(observedValue) + 1);
				}
				else{
					correctPredictionCounts.put(observedValue, 1);
				}
			}
			
		}
		
		Double ccrDouble = 0.0;
		for(Integer d: correctPredictionCounts.keySet()){
			ccrDouble += new Double(correctPredictionCounts.get(d)) / new Double(observedValueCounts.get(d));
		}
		ccrDouble = ccrDouble / new Double(observedValueCounts.keySet().size());
		
		ConfusionMatrix cm = new ConfusionMatrix();
		cm.setCcr(ccrDouble);
		cm.setUniqueObservedValues(uniqueObservedValues);
		cm.setMatrixValues(matrix);
		
		return cm;
	}
	
}