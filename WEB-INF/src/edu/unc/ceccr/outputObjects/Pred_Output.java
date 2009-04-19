package edu.unc.ceccr.outputObjects;

public class Pred_Output {
	private String numOfModels;
	private String compoundID;
	private String prediction;
	private String standardDeviation;
	
	public String getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(String standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public String getNumOfModels() {
		return this.numOfModels;
	}
	
	public void setNumOfModels(String numOfModels) {
		this.numOfModels = numOfModels;
	}
	public String getCompoundID() {
		return this.compoundID;
	}
	
	public void setCompoundID(String numOfModels) {
		this.compoundID = numOfModels;
	}
	public String getPredictedValue() {
		return this.prediction;
	}
	
	public void setPredictedValue(String numOfModels) {
		this.prediction = numOfModels;
	}
}
