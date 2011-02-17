package edu.unc.ceccr.action.ViewPredictor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class ExternalValidationPage extends ViewPredictorAction {
	private List<ExternalValidation> externalValValues;
	private String hasGoodModels = Constants.YES;
	private List<String> residuals;
	
	//used in creation of confusion matrix (category modeling only)
	public class ConfusionMatrixRow{
		ArrayList<Integer> values;

		public ArrayList<Integer> getValues() {
			return values;
		}
		public void setValues(ArrayList<Integer> values) {
			this.values = values;
		}
	}
	ArrayList<ConfusionMatrixRow> confusionMatrix;
	String ccr = "";
	ArrayList<String> uniqueObservedValues;
	String rSquared = "";
	
	public String load() throws Exception {
		getBasicParameters();
		String result = SUCCESS;
		//check that the user is logged in
		
		//get external validation compounds of predictor
		ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(selectedPredictor, session);
		if(childPredictors.size() != 0){
			//get external set for each
			externalValValues = new ArrayList<ExternalValidation>();
			for(Predictor cp: childPredictors){
				List<ExternalValidation> childExtVals = PopulateDataObjects.getExternalValidationValues(cp, session);
				if(childExtVals != null){
					externalValValues.addAll(childExtVals);
				}
			}
		}
		else{
			externalValValues=PopulateDataObjects.getExternalValidationValues(selectedPredictor, session);
		}
		
		if(externalValValues == null || externalValValues.isEmpty()){
			Utility.writeToDebug("ext validation set empty!");
			externalValValues = new ArrayList<ExternalValidation>();
			return result;
		}
		
		dataset = PopulateDataObjects.getDataSetById(selectedPredictor.getDatasetId(), session);
		
		//calculate residuals and fix significant figures on output data
		residuals = new ArrayList<String>();
		Iterator<ExternalValidation> eit = externalValValues.iterator();
		int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
		int numExtValuesWithNoModels = 0;
		while(eit.hasNext()){
			ExternalValidation e = eit.next();
			if(e.getNumModels() != 0){
				String residual = DecimalFormat.getInstance().format(e.getActualValue() - e.getPredictedValue()).replaceAll(",", "");
				residuals.add(Utility.roundSignificantFigures(residual, sigfigs));
			}
			else{
				numExtValuesWithNoModels++;
				residuals.add("");
			}
			String predictedValue = DecimalFormat.getInstance().format(e.getPredictedValue()).replaceAll(",", "");
			e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));  
			if(! e.getStandDev().equalsIgnoreCase("No value")){
				e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
			}
		}
		if(numExtValuesWithNoModels == externalValValues.size()){
			//all external predictions were empty, meaning there were no good models.
			//can't calculate any summary data.
			hasGoodModels = Constants.NO;
			return result;
		}
		
		if(selectedPredictor.getActivityType().equals(Constants.CATEGORY)){
			//if category model, create confusion matrix.
			//round off the predicted values to nearest integer.
			try{
			
			//scan through to find the unique observed values
			uniqueObservedValues = new ArrayList<String>();
			for(ExternalValidation ev : externalValValues){
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
			confusionMatrix = new ArrayList<ConfusionMatrixRow>();
			
			//make a matrix of zeros
			for(int i = 0; i < uniqueObservedValues.size(); i++){
				ConfusionMatrixRow row = new ConfusionMatrixRow();
				row.values = new ArrayList<Integer>();
				for(int j = 0; j < uniqueObservedValues.size(); j++){
					row.values.add(0);
				}
				confusionMatrix.add(row);
			}
			
			double CCR = 0.0;	
			HashMap<Integer, Integer> correctPredictionCounts = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> observedValueCounts = new HashMap<Integer, Integer>();
			
			//populate the confusion matrix and count values needed to calculate CCR
			for(ExternalValidation ev : externalValValues){
				//for each observed-predicted pair, update
				//the confusion matrix accordingly
				int observedValue = Math.round(ev.getActualValue());
				int predictedValue = Math.round(ev.getPredictedValue());
				int observedValueIndex = uniqueObservedValues.indexOf("" + observedValue);
				int predictedValueIndex = uniqueObservedValues.indexOf("" + predictedValue);
				int previousCount = confusionMatrix.get(observedValueIndex).values.get(predictedValueIndex);
				confusionMatrix.get(observedValueIndex).values.set(predictedValueIndex, previousCount+1);
				
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
			
			//calculate the CCR
			//formula: 1/n(correct 1 / actual 1 + correct 2 / actual 2 ...correct n /predicted n)
			Double ccrDouble = 0.0;
			for(Integer d: correctPredictionCounts.keySet()){
				ccrDouble += new Double(correctPredictionCounts.get(d)) / new Double(observedValueCounts.get(d));
			}
			ccrDouble = ccrDouble / new Double(observedValueCounts.keySet().size());
			ccr = Utility.roundSignificantFigures(Utility.doubleToString(ccrDouble), 4);
			
			}catch(Exception ex){
				Utility.writeToDebug(ex);
			}
		}
		else if(selectedPredictor.getActivityType().equals(Constants.CONTINUOUS) && externalValValues.size() > 1){
			//if continuous, calculate overall r^2 and... r0^2? or something? 
			//just r^2 for now, more later.
			Double avg = 0.0;
			for(ExternalValidation ev : externalValValues){
				avg += ev.getActualValue();
			}
			avg /= externalValValues.size();
			Double ssErr = 0.0;
			for(String residual : residuals){
				if(! residual.isEmpty()){
					ssErr += Double.parseDouble(residual) * Double.parseDouble(residual);
				}
			}
			Double ssTot = 0.0;
			for(ExternalValidation ev : externalValValues){
				ssTot += (ev.getActualValue() - avg) * (ev.getActualValue() - avg);
			}
			if(ssTot != 0){
				rSquared = Utility.roundSignificantFigures("" + (1 - (ssErr / ssTot)), 4);
			}
		}
		
		return result;
	}

	
	//getters and setters

	public List<ExternalValidation> getExternalValValues() {
		return externalValValues;
	}
	public void setExternalValValues(List<ExternalValidation> externalValValues) {
		this.externalValValues = externalValValues;
	}

	public String getHasGoodModels() {
		return hasGoodModels;
	}
	public void setHasGoodModels(String hasGoodModels) {
		this.hasGoodModels = hasGoodModels;
	}

	public List<String> getResiduals() {
		return residuals;
	}
	public void setResiduals(List<String> residuals) {
		this.residuals = residuals;
	}

	public ArrayList<ConfusionMatrixRow> getConfusionMatrix() {
		return confusionMatrix;
	}
	public void setConfusionMatrix(ArrayList<ConfusionMatrixRow> confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}

	public String getCcr() {
		return ccr;
	}
	public void setCcr(String ccr) {
		this.ccr = ccr;
	}

	public ArrayList<String> getUniqueObservedValues() {
		return uniqueObservedValues;
	}
	public void setUniqueObservedValues(ArrayList<String> uniqueObservedValues) {
		this.uniqueObservedValues = uniqueObservedValues;
	}

	public String getrSquared() {
		return rSquared;
	}
	public void setrSquared(String rSquared) {
		this.rSquared = rSquared;
	}
	//end getters and setters
}