package edu.unc.ceccr.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cbench_svmModel")
public class SvmModel implements java.io.Serializable{
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
	private Long predictorId;
	private String isYRandomModel;
	
	//parameters that generated this model
	private String gamma;
	private String cost;
	private String nu;
	private String loss;
	private String degree;
	
	//modeling results
	private String rSquaredTest;
	private String mseTest;
	private String ccrTest;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "predictorId")
	public Long getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(Long predictorId) {
		this.predictorId = predictorId;
	}

	@Column(name = "isYRandomModel")
	public String getIsYRandomModel() {
		return isYRandomModel;
	}
	public void setIsYRandomModel(String isYRandomModel) {
		this.isYRandomModel = isYRandomModel;
	}

	@Column(name = "gamma")
	public String getGamma() {
		return gamma;
	}
	public void setGamma(String gamma) {
		this.gamma = gamma;
	}

	@Column(name = "cost")
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}

	@Column(name = "nu")
	public String getNu() {
		return nu;
	}
	public void setNu(String nu) {
		this.nu = nu;
	}

	@Column(name = "loss")
	public String getLoss() {
		return loss;
	}
	public void setLoss(String loss) {
		this.loss = loss;
	}

	@Column(name = "degree")
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}

	@Column(name = "rSquaredTest")
	public String getrSquaredTest() {
		return rSquaredTest;
	}
	public void setrSquaredTest(String rSquaredTest) {
		this.rSquaredTest = rSquaredTest;
	}

	@Column(name = "mseTest")
	public String getMseTest() {
		return mseTest;
	}
	public void setMseTest(String mseTest) {
		this.mseTest = mseTest;
	}

	@Column(name = "ccrTest")
	public String getCcrTest() {
		return ccrTest;
	}
	public void setCcrTest(String ccrTest) {
		this.ccrTest = ccrTest;
	}
}