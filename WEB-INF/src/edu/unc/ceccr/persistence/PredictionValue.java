package edu.unc.ceccr.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
@Entity
//@IdClass(PredictionValuePK.class)
@Table(name = "cbench_prediction_val")
public class PredictionValue implements java.io.Serializable{

	@SuppressWarnings("unused")
	private Long predictorId;
	private String compoundName;
	private int numModelsUsed;
	private Float predictedValue;
	private Float standardDeviation;
	private Long predictionId;
	private Long id;
	private float observedValue;

	private int numTotalModels;
	
	@Column(name="predictor_id")
	public Long getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(Long predictorId) {
		this.predictorId = predictorId;
	}
	
	@Column(name="observed_value")
	public Float getObservedValue()
	{
		return this.observedValue;
	}
	public void setObservedValue(Float value)
	{
		this.observedValue=value;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "pred_val_id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "compound_name")
	public String getCompoundName() {
		return compoundName;
	}

	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}

	@Column(name = "num_models")
	public int getNumModelsUsed() {
		return numModelsUsed;
	}

	public void setNumModelsUsed(int numModelsUsed) {
		this.numModelsUsed = numModelsUsed;
	}
	
	@Column(name = "predicted_value")
	public Float getPredictedValue() {
		return predictedValue;
	}

	public void setPredictedValue(Float predictedValue) {
		this.predictedValue = predictedValue;
	}

	@Column(name = "stdev")
	public Float getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(Float standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	@Column(name = "prediction_id")
	public Long getPredictionId() {
		return predictionId;
	}
	public void setPredictionId(Long predictionId) {
		this.predictionId = predictionId;
	}

	@Transient
	public int getNumTotalModels() {
		return numTotalModels;
	}
	public void setNumTotalModels(int numTotalModels) {
		this.numTotalModels = numTotalModels;
	}
}
