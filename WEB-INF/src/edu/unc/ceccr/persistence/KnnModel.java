package edu.unc.ceccr.persistence;

import javax.persistence.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

// default package
// Generated Jun 20, 2006 1:22:16 PM by Hibernate Tools 3.1.0.beta5

@Entity
@Table(name = "cbench_model")
public class KnnModel implements java.io.Serializable {

	// Fields
	private Long id;
	private Long predictorId;
	private Integer nnn; //number of nearest neighbors
	private Integer n; //number of compounds used in... internal test set I think?
	private Float QSquared;
	private Float RSquared;
	private Float b01;
	private Float b02;
	private Float b11;
	private Float b12;
	private Float r;
	private Float slSquared;
	private Float f1;
	private Float s2Squared;
	private Float f2;
	private Float k1;
	private Float k2;
	private Float r01Squared;
	private Float r02Squared;
	private Float s01Squared;
	private Float s02Squared;
	private Float f01;
	private Float f02;
	private Float r451Squared;
	private Float r452Squared;
	private Float st45;
	
	private String descriptorsUsed;

	//added values for knn-category
	private Float trainingAcc;
	private Float normalizedTestAcc;
	private Float testAcc;
	private Float normalizedTrainingAcc;
	private String knnType;
	private String file;
	private String flowType;

	// Constructors

	/** default constructor */
	public KnnModel() {
	}

	/** full constructor */
	public KnnModel(Long id, Long predictorId, Integer nnn, Float QSquared,
			Float RSquared, Integer n, Float b01, Float b02, Float b11,
			Float b12, Float r, Float slSquared, Float f1, Float s2Squared,
			Float f2, Float k1, Float k2, Float r01Squared, Float r02Squared,
			Float s01Squared, Float s02Squared, Float f01, Float f02,
			Float r451Squared, Float r452Squared, Float st45, String file, String flowType) {
		this.id = id;
		this.predictorId = predictorId;
		this.nnn = nnn;
		this.QSquared = QSquared;
		this.RSquared = RSquared;
		this.n = n;
		this.b01 = b01;
		this.b02 = b02;
		this.b11 = b11;
		this.b12 = b12;
		this.r = r;
		this.slSquared = slSquared;
		this.f1 = f1;
		this.s2Squared = s2Squared;
		this.f2 = f2;
		this.k1 = k1;
		this.k2 = k2;
		this.r01Squared = r01Squared;
		this.r02Squared = r02Squared;
		this.s01Squared = s01Squared;
		this.s02Squared = s02Squared;
		this.f01 = f01;
		this.f02 = f02;
		this.r451Squared = r451Squared;
		this.r452Squared = r452Squared;
		this.st45 = st45;
		this.file = file;
		this.flowType=flowType;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getId()
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "model_id")
	// Property accessors
	public Long getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getPredictor()
	 */
	@Column(name = "predictor_id")
	public Long getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(Long predictorId) {
		this.predictorId = predictorId;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getNnn()
	 */
	@Column(name = "nnn")
	public Integer getNnn() {
		return this.nnn;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setNnn(Float)
	 */
	public void setNnn(Integer nnn) {
		this.nnn = nnn;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getQSquared()
	 */
	@Column(name = "q_squared")
	public Float getQSquared() {
		return this.QSquared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setQSquared(Float)
	 */
	public void setQSquared(Float QSquared) {
		this.QSquared = QSquared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getRSquared()
	 */
	@Column(name = "r_squared")
	public Float getRSquared() {
		return this.RSquared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setRSquared(Float)
	 */
	public void setRSquared(Float RSquared) {
		this.RSquared = RSquared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getN()
	 */
	public Integer getN() {
		return this.n;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setN(Float)
	 */
	public void setN(Integer n) {
		this.n = n;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getB01()
	 */
	public Float getB01() {
		return this.b01;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setB01(Float)
	 */
	public void setB01(Float b01) {
		this.b01 = b01;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getB02()
	 */
	public Float getB02() {
		return this.b02;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setB02(Float)
	 */
	public void setB02(Float b02) {
		this.b02 = b02;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getB11()
	 */
	public Float getB11() {
		return this.b11;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setB11(Float)
	 */
	public void setB11(Float b11) {
		this.b11 = b11;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getB12()
	 */
	public Float getB12() {
		return this.b12;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setB12(Float)
	 */
	public void setB12(Float b12) {
		this.b12 = b12;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getR()
	 */
	public Float getR() {
		return this.r;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setR(Float)
	 */
	public void setR(Float r) {
		this.r = r;
	}

	// public Float getSlSquared() {
	// return this.slSquared;
	// }
	//
	// public void setSlSquared(Float slSquared) {
	// this.slSquared = slSquared;
	// }

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getF1()
	 */
	public Float getF1() {
		return this.f1;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setF1(Float)
	 */
	public void setF1(Float f1) {
		this.f1 = f1;
	}

	// public Float getS2Squared() {
	// return this.s2Squared;
	// }
	//
	// public void setS2Squared(Float s2Squared) {
	// this.s2Squared = s2Squared;
	// }

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getF2()
	 */
	public Float getF2() {
		return this.f2;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setF2(Float)
	 */
	public void setF2(Float f2) {
		this.f2 = f2;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getK1()
	 */
	public Float getK1() {
		return this.k1;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setK1(Float)
	 */
	public void setK1(Float k1) {
		this.k1 = k1;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getK2()
	 */
	public Float getK2() {
		return this.k2;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setK2(Float)
	 */
	public void setK2(Float k2) {
		this.k2 = k2;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getR01Squared()
	 */
	@Column(name = "r01_squared")
	public Float getR01Squared() {
		return this.r01Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setR01Squared(Float)
	 */
	public void setR01Squared(Float r01Squared) {
		this.r01Squared = r01Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getR02Squared()
	 */
	@Column(name = "r02_squared")
	public Float getR02Squared() {
		return this.r02Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setR02Squared(Float)
	 */
	public void setR02Squared(Float r02Squared) {
		this.r02Squared = r02Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getS01Squared()
	 */
	@Column(name = "s01_squared")
	public Float getS01Squared() {
		return this.s01Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setS01Squared(Float)
	 */
	public void setS01Squared(Float s01Squared) {
		this.s01Squared = s01Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getS02Squared()
	 */
	@Column(name = "s02_squared")
	public Float getS02Squared() {
		return this.s02Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setS02Squared(Float)
	 */
	public void setS02Squared(Float s02Squared) {
		this.s02Squared = s02Squared;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getF01()
	 */
	public Float getF01() {
		return this.f01;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setF01(Float)
	 */
	public void setF01(Float f01) {
		this.f01 = f01;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getF02()
	 */
	public Float getF02() {
		return this.f02;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setF02(Float)
	 */
	public void setF02(Float f02) {
		this.f02 = f02;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getSt45()
	 */
	public Float getSt45() {
		return this.st45;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setSt45(Float)
	 */
	public void setSt45(Float st45) {
		this.st45 = st45;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#getFile()
	 */
	public String getFile() {
		return this.file;
	}

	/* (non-Javadoc)
	 * @see edu.unc.ceccr.session.IModel#setFile(java.lang.String)
	 */
	public void setFile(String file) {
		this.file = file;
	}
	@Column(name="type")
	public String getFlowType()
	{
		return this.flowType;
	}
	public void setFlowType(String flowType)
	{
		this.flowType=flowType;
	}

	@Transient
	//@Column(name = "knnType")
	public String getKnnType() {
		return knnType;
	}

	public void setKnnType(String knnType) {
		this.knnType = knnType;
	}

	@Column(name = "normTestAcc")
	public Float getNormalizedTestAcc() {
		return normalizedTestAcc;
	}

	public void setNormalizedTestAcc(Float normalizedTestAcc) {
		this.normalizedTestAcc = normalizedTestAcc;
	}

	@Column(name = "normTrainingAcc")
	public Float getNormalizedTrainingAcc() {
		return normalizedTrainingAcc;
	}

	public void setNormalizedTrainingAcc(Float normalizedTrainingAcc) {
		this.normalizedTrainingAcc = normalizedTrainingAcc;
	}

	@Column(name = "testAcc")
	public Float getTestAcc() {
		return testAcc;
	}

	public void setTestAcc(Float testAcc) {
		this.testAcc = testAcc;
	}

	@Column(name = "trainingAcc")
	public Float getTrainingAcc() {
		return trainingAcc;
	}

	public void setTrainingAcc(Float trainingAcc) {
		this.trainingAcc = trainingAcc;
	}

	@Column(name = "descriptorsUsed")
	public String getDescriptorsUsed() {
		return descriptorsUsed;
	}
	public void setDescriptorsUsed(String descriptorsUsed) {
		if(descriptorsUsed != null && descriptorsUsed.length() > 4000){
			//truncate to 4000 and log an error
			Utility.writeToDebug("Warning: Descriptors truncated for model " + id + " in predictor with id: " + predictorId);
			descriptorsUsed = descriptorsUsed.substring(0, 3999);
		}
		this.descriptorsUsed = descriptorsUsed;
	}

}
