package edu.unc.ceccr.formbean;

import java.util.ArrayList;
import java.util.List;
import edu.unc.ceccr.persistence.PredictionValue;

import org.apache.struts.validator.ValidatorForm;

public class ViewOutputFormBean extends ValidatorForm {

	//String file;
	
	Long predictionId;

	List<String> propList = new ArrayList<String>();

	List<String> opList = new ArrayList<String>();

	List<String> valList = new ArrayList<String>();

	/*
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}*/

	public List<String> getCriteria_field() {
		return propList;
	}

	public String getCriteria_field(int i) {
		return this.propList.get(i);
	}

	public void setCriteria_field(List propList) {
		this.propList = propList;
	}

	public List<String> getCriteriaOperator() {
		return this.opList;
	}

	public String getCriteriaOperator(int i) {
		return this.opList.get(i);
	}

	public void setCriteriaOperator(List operatorList) {
		this.opList = operatorList;
	}

	public List<String> getCriteriaValue() {
		return this.valList;
	}

	public String getCriteriaValue(int i) {
		return this.valList.get(i);
	}

	public void setCriteriaValue(List valList) {
		this.valList = valList;
	}

	public Long getPredictionId() {
		return predictionId;
	}

	public void setPredictionId(Long predictionId) {
		this.predictionId = predictionId;
	}

}