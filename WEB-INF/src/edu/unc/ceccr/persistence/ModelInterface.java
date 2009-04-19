package edu.unc.ceccr.persistence;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


public interface ModelInterface {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "model_id")
	// Property accessors
	public abstract Long getId();

	public abstract void setId(Long id);

	@ManyToOne
	@JoinColumn(name = "predictor_id")
	public abstract Predictor getPredictor();

	public abstract void setPredictor(Predictor predictor);

	public abstract Float getNnn();

	public abstract void setNnn(Float nnn);

	@Column(name = "q_squared")
	public abstract Float getQSquared();

	public abstract void setQSquared(Float QSquared);

	@Column(name = "r_squared")
	public abstract Float getRSquared();

	public abstract void setRSquared(Float RSquared);

	public abstract Float getN();

	public abstract void setN(Float n);

	public abstract Float getB01();

	public abstract void setB01(Float b01);

	public abstract Float getB02();

	public abstract void setB02(Float b02);

	public abstract Float getB11();

	public abstract void setB11(Float b11);

	public abstract Float getB12();

	public abstract void setB12(Float b12);

	public abstract Float getR();

	public abstract void setR(Float r);

	public abstract Float getF1();

	public abstract void setF1(Float f1);

	public abstract Float getF2();

	public abstract void setF2(Float f2);

	public abstract Float getK1();

	public abstract void setK1(Float k1);

	public abstract Float getK2();

	public abstract void setK2(Float k2);

	@Column(name = "r01_squared")
	public abstract Float getR01Squared();

	public abstract void setR01Squared(Float r01Squared);

	@Column(name = "r02_squared")
	public abstract Float getR02Squared();

	public abstract void setR02Squared(Float r02Squared);

	@Column(name = "s01_squared")
	public abstract Float getS01Squared();

	public abstract void setS01Squared(Float s01Squared);

	@Column(name = "s02_squared")
	public abstract Float getS02Squared();

	public abstract void setS02Squared(Float s02Squared);

	public abstract Float getF01();

	public abstract void setF01(Float f01);

	public abstract Float getF02();

	public abstract void setF02(Float f02);

	public abstract Float getSt45();

	public abstract void setSt45(Float st45);

	public abstract String getFile();

	public abstract void setFile(String file);
	
	public abstract String getFlowType();
	
	public abstract void setFlowType(String flowType);
	
	public abstract Float getNormalizedTestAcc();
	public abstract void setNormalizedTestAcc(Float normalizedTestAcc);

}