package edu.unc.ceccr.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cbench_ext_pred")
public class ExternalValidation implements java.io.Serializable {
	
	private int externalValId;
	
	private Predictor predictor;
	
	private String compoundId;
	
	private float predictedValue;
	
	private float actualValue;
	
	private int numModels;
	
	private String standDev;
	
	//private String structureFile;
	private byte[] structureFile;

	public ExternalValidation(){};
	
	public ExternalValidation(int externalValId, Predictor predictor, String compoundId, float predictedValue, float actualValue, int numModels,String standDev) {
		super();
		this.externalValId = externalValId;
		this.predictor = predictor;
		this.compoundId = compoundId;
		this.predictedValue = predictedValue;
		this.actualValue = actualValue;
		this.numModels = numModels;
		this.standDev=standDev;
	}

	@Column(name="std_deviation")
	public String getStandDev()
	{
		return standDev;
	}
	public void setStandDev(String standDev)
	{
		this.standDev=standDev;
	}
	
	@Column(name = "act_value")
	public float getActualValue() {
		return actualValue;
	}

	public void setActualValue(float actualValue) {
		this.actualValue = actualValue;
	}

	@Column(name = "compound_id")
	public String getCompoundId() {
		return compoundId;
	}

	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ext_pred_id")
	public int getExternalValId() {
		return externalValId;
	}

	public void setExternalValId(int externalValId) {
		this.externalValId = externalValId;
	}

	@Column(name = "num_models")
	public int getNumModels() {
		return numModels;
	}

	public void setNumModels(int numModels) {
		this.numModels = numModels;
	}
	
	@Column(name = "pred_value")
	public float getPredictedValue() {
		return predictedValue;
	}

	public void setPredictedValue(float predictedValue) {
		this.predictedValue = predictedValue;
	}

	@ManyToOne
	@JoinColumn(name = "predictor_id")
	public Predictor getPredictor() {
		return predictor;
	}

	public void setPredictor(Predictor predictor) {
		this.predictor = predictor;
	}
	
	@Column(name = "structure_file")
	/*public String getStructureFile() {
		return structureFile;
	}

	public void setStructureFile(String structureFile) {
		this.structureFile = structureFile;
	}*/
	public byte[] getStructureFile() {
		return structureFile;
	}

	public void setStructureFile(byte[] structureFile) {
		this.structureFile = structureFile;
	}
}
