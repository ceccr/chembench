package edu.unc.ceccr.chembench.persistence;

import javax.persistence.*;

@Entity
@Table(name = "cbench_knnPlusModel")
public class KnnPlusModel implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long predictorId;
    private String isYRandomModel;

    //Outputs for both category and continuous models
    private String nDims;
    private String dimsIDs;
    private String dimsNames;
    private String kOrR;
    private String qualityLimitTraining;
    private String nDatapointsTraining;
    private String qualityLimitTest;
    private String nDatapointsTest;
    //End Outputs for both category and continuous models

    //Outputs for just continuous models
    private String stdevActTraining;
    private String stdevActCalcTraining;
    private String b01Training;
    private String b11Training;
    private String b02Training;
    private String b12Training;
    private String RTraining;
    private String R2Training;
    private String MSE1Training;
    private String MSE2Training;
    private String F1Training;
    private String F2Training;
    private String k1Training;
    private String k2Training;
    private String R02Training;
    private String R012Training;
    private String MSE01Training;
    private String MSE02Training;
    private String F01Training;
    private String F02Training;
    private String q2Training;
    private String qPrime2Training;
    private String MAEqTraining;
    private String MAEqPrimeTraining;
    private String MSETraining;
    private String MAETraining;

    private String stdevActTest;
    private String stdevActCalcTest;
    private String b01Test;
    private String b11Test;
    private String b02Test;
    private String b12Test;
    private String RTest;
    private String R2Test;
    private String MSE1Test;
    private String MSE2Test;
    private String F1Test;
    private String F2Test;
    private String k1Test;
    private String k2Test;
    private String R02Test;
    private String R012Test;
    private String MSE01Test;
    private String MSE02Test;
    private String F01Test;
    private String F02Test;
    private String q2Test;
    private String qPrime2Test;
    private String MAEqTest;
    private String MAEqPrimeTest;
    private String MSETest;
    private String MAETest;
    //End Outputs for just continuous models

    //Outputs for just category models

    private String AccuracyTraining;
    private String CCRNormalizedAccuracyTraining;
    private String AccuracyWithGroupWeightsTraining;
    private String CCRWithGroupWeightsTraining;
    private String AccuracyMaxErrBasedTraining;
    private String CCRMaxErrBasedTraining;
    private String AccuracyAvErrBasedTraining;
    private String CCRAvErrBasedTraining;

    private String AccuracyTest;
    private String CCRNormalizedAccuracyTest;
    private String AccuracyWithGroupWeightsTest;
    private String CCRWithGroupWeightsTest;
    private String AccuracyMaxErrBasedTest;
    private String CCRMaxErrBasedTest;
    private String AccuracyAvErrBasedTest;
    private String CCRAvErrBasedTest;

		/*
        We might need to worry about individual group accuracies for category models eventually...
		but for now, we're not gonna capture them cause it's annoying to code.
		Can always calculate them from a confusion matrix anyway.

		train_Ndatapoints_Group1
		train_Accuracy_Group1
		train_Ndatapoints_Group2
		train_Accuracy_Group2
		test_Ndatapoints_Group1
		test_Accuracy_Group1
		test_Ndatapoints_Group2
		test_Accuracy_Group2
		*/

    //End Outputs for just category models


    //GETTERS AND SETTERS
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "predictor_id")
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

    @Column(name = "nDims")
    public String getNDims() {
        return nDims;
    }

    public void setNDims(String nDims) {
        this.nDims = nDims;
    }

    @Column(name = "dimsIDs")
    public String getDimsIDs() {
        return dimsIDs;
    }

    public void setDimsIDs(String dimsIDs) {
        this.dimsIDs = dimsIDs;
    }

    @Column(name = "dimsNames")
    public String getDimsNames() {
        return dimsNames;
    }

    public void setDimsNames(String dimsNames) {
        this.dimsNames = dimsNames;
    }

    @Column(name = "kOrR")
    public String getKOrR() {
        return kOrR;
    }

    public void setKOrR(String kOrR) {
        this.kOrR = kOrR;
    }

    @Column(name = "qualityLimitTraining")
    public String getQualityLimitTraining() {
        return qualityLimitTraining;
    }

    public void setQualityLimitTraining(String qualityLimitTraining) {
        this.qualityLimitTraining = qualityLimitTraining;
    }

    @Column(name = "nDatapointsTraining")
    public String getNDatapointsTraining() {
        return nDatapointsTraining;
    }

    public void setNDatapointsTraining(String nDatapointsTraining) {
        this.nDatapointsTraining = nDatapointsTraining;
    }

    @Column(name = "qualityLimitTest")
    public String getQualityLimitTest() {
        return qualityLimitTest;
    }

    public void setQualityLimitTest(String qualityLimitTest) {
        this.qualityLimitTest = qualityLimitTest;
    }

    @Column(name = "nDatapointsTest")
    public String getNDatapointsTest() {
        return nDatapointsTest;
    }

    public void setNDatapointsTest(String nDatapointsTest) {
        this.nDatapointsTest = nDatapointsTest;
    }

    @Column(name = "stdevActTraining")
    public String getStdevActTraining() {
        return stdevActTraining;
    }

    public void setStdevActTraining(String stdevActTraining) {
        this.stdevActTraining = stdevActTraining;
    }

    @Column(name = "stdevActCalcTraining")
    public String getStdevActCalcTraining() {
        return stdevActCalcTraining;
    }

    public void setStdevActCalcTraining(String stdevActCalcTraining) {
        this.stdevActCalcTraining = stdevActCalcTraining;
    }

    @Column(name = "b01Training")
    public String getB01Training() {
        return b01Training;
    }

    public void setB01Training(String b01Training) {
        this.b01Training = b01Training;
    }

    @Column(name = "b11Training")
    public String getB11Training() {
        return b11Training;
    }

    public void setB11Training(String b11Training) {
        this.b11Training = b11Training;
    }

    @Column(name = "b02Training")
    public String getB02Training() {
        return b02Training;
    }

    public void setB02Training(String b02Training) {
        this.b02Training = b02Training;
    }

    @Column(name = "b12Training")
    public String getB12Training() {
        return b12Training;
    }

    public void setB12Training(String b12Training) {
        this.b12Training = b12Training;
    }

    @Column(name = "RTraining")
    public String getRTraining() {
        return RTraining;
    }

    public void setRTraining(String rTraining) {
        RTraining = rTraining;
    }

    @Column(name = "R2Training")
    public String getR2Training() {
        return R2Training;
    }

    public void setR2Training(String r2Training) {
        R2Training = r2Training;
    }

    @Column(name = "MSE1Training")
    public String getMSE1Training() {
        return MSE1Training;
    }

    public void setMSE1Training(String mSE1Training) {
        MSE1Training = mSE1Training;
    }

    @Column(name = "MSE2Training")
    public String getMSE2Training() {
        return MSE2Training;
    }

    public void setMSE2Training(String mSE2Training) {
        MSE2Training = mSE2Training;
    }

    @Column(name = "F1Training")
    public String getF1Training() {
        return F1Training;
    }

    public void setF1Training(String f1Training) {
        F1Training = f1Training;
    }

    @Column(name = "F2Training")
    public String getF2Training() {
        return F2Training;
    }

    public void setF2Training(String f2Training) {
        F2Training = f2Training;
    }

    @Column(name = "k1Training")
    public String getK1Training() {
        return k1Training;
    }

    public void setK1Training(String k1Training) {
        this.k1Training = k1Training;
    }

    @Column(name = "k2Training")
    public String getK2Training() {
        return k2Training;
    }

    public void setK2Training(String k2Training) {
        this.k2Training = k2Training;
    }

    @Column(name = "R02Training")
    public String getR02Training() {
        return R02Training;
    }

    public void setR02Training(String r02Training) {
        R02Training = r02Training;
    }

    @Column(name = "R012Training")
    public String getR012Training() {
        return R012Training;
    }

    public void setR012Training(String r012Training) {
        R012Training = r012Training;
    }

    @Column(name = "MSE01Training")
    public String getMSE01Training() {
        return MSE01Training;
    }

    public void setMSE01Training(String mSE01Training) {
        MSE01Training = mSE01Training;
    }

    @Column(name = "MSE02Training")
    public String getMSE02Training() {
        return MSE02Training;
    }

    public void setMSE02Training(String mSE02Training) {
        MSE02Training = mSE02Training;
    }

    @Column(name = "F01Training")
    public String getF01Training() {
        return F01Training;
    }

    public void setF01Training(String f01Training) {
        F01Training = f01Training;
    }

    @Column(name = "F02Training")
    public String getF02Training() {
        return F02Training;
    }

    public void setF02Training(String f02Training) {
        F02Training = f02Training;
    }

    @Column(name = "q2Training")
    public String getQ2Training() {
        return q2Training;
    }

    public void setQ2Training(String q2Training) {
        this.q2Training = q2Training;
    }

    @Column(name = "qPrime2Training")
    public String getQPrime2Training() {
        return qPrime2Training;
    }

    public void setQPrime2Training(String qPrime2Training) {
        this.qPrime2Training = qPrime2Training;
    }

    @Column(name = "MAEqTraining")
    public String getMAEqTraining() {
        return MAEqTraining;
    }

    public void setMAEqTraining(String mAEqTraining) {
        MAEqTraining = mAEqTraining;
    }

    @Column(name = "MAEqPrimeTraining")
    public String getMAEqPrimeTraining() {
        return MAEqPrimeTraining;
    }

    public void setMAEqPrimeTraining(String mAEqPrimeTraining) {
        MAEqPrimeTraining = mAEqPrimeTraining;
    }

    @Column(name = "MSETraining")
    public String getMSETraining() {
        return MSETraining;
    }

    public void setMSETraining(String mSETraining) {
        MSETraining = mSETraining;
    }

    @Column(name = "MAETraining")
    public String getMAETraining() {
        return MAETraining;
    }

    public void setMAETraining(String mAETraining) {
        MAETraining = mAETraining;
    }

    @Column(name = "stdevActTest")
    public String getStdevActTest() {
        return stdevActTest;
    }

    public void setStdevActTest(String stdevActTest) {
        this.stdevActTest = stdevActTest;
    }

    @Column(name = "stdevActCalcTest")
    public String getStdevActCalcTest() {
        return stdevActCalcTest;
    }

    public void setStdevActCalcTest(String stdevActCalcTest) {
        this.stdevActCalcTest = stdevActCalcTest;
    }

    @Column(name = "b01Test")
    public String getB01Test() {
        return b01Test;
    }

    public void setB01Test(String b01Test) {
        this.b01Test = b01Test;
    }

    @Column(name = "b11Test")
    public String getB11Test() {
        return b11Test;
    }

    public void setB11Test(String b11Test) {
        this.b11Test = b11Test;
    }

    @Column(name = "b02Test")
    public String getB02Test() {
        return b02Test;
    }

    public void setB02Test(String b02Test) {
        this.b02Test = b02Test;
    }

    @Column(name = "b12Test")
    public String getB12Test() {
        return b12Test;
    }

    public void setB12Test(String b12Test) {
        this.b12Test = b12Test;
    }

    @Column(name = "RTest")
    public String getRTest() {
        return RTest;
    }

    public void setRTest(String rTest) {
        RTest = rTest;
    }

    @Column(name = "R2Test")
    public String getR2Test() {
        return R2Test;
    }

    public void setR2Test(String r2Test) {
        R2Test = r2Test;
    }

    @Column(name = "MSE1Test")
    public String getMSE1Test() {
        return MSE1Test;
    }

    public void setMSE1Test(String mSE1Test) {
        MSE1Test = mSE1Test;
    }

    @Column(name = "MSE2Test")
    public String getMSE2Test() {
        return MSE2Test;
    }

    public void setMSE2Test(String mSE2Test) {
        MSE2Test = mSE2Test;
    }

    @Column(name = "F1Test")
    public String getF1Test() {
        return F1Test;
    }

    public void setF1Test(String f1Test) {
        F1Test = f1Test;
    }

    @Column(name = "F2Test")
    public String getF2Test() {
        return F2Test;
    }

    public void setF2Test(String f2Test) {
        F2Test = f2Test;
    }

    @Column(name = "k1Test")
    public String getK1Test() {
        return k1Test;
    }

    public void setK1Test(String k1Test) {
        this.k1Test = k1Test;
    }

    @Column(name = "k2Test")
    public String getK2Test() {
        return k2Test;
    }

    public void setK2Test(String k2Test) {
        this.k2Test = k2Test;
    }

    @Column(name = "R02Test")
    public String getR02Test() {
        return R02Test;
    }

    public void setR02Test(String r02Test) {
        R02Test = r02Test;
    }

    @Column(name = "R012Test")
    public String getR012Test() {
        return R012Test;
    }

    public void setR012Test(String r012Test) {
        R012Test = r012Test;
    }

    @Column(name = "MSE01Test")
    public String getMSE01Test() {
        return MSE01Test;
    }

    public void setMSE01Test(String mSE01Test) {
        MSE01Test = mSE01Test;
    }

    @Column(name = "MSE02Test")
    public String getMSE02Test() {
        return MSE02Test;
    }

    public void setMSE02Test(String mSE02Test) {
        MSE02Test = mSE02Test;
    }

    @Column(name = "F01Test")
    public String getF01Test() {
        return F01Test;
    }

    public void setF01Test(String f01Test) {
        F01Test = f01Test;
    }

    @Column(name = "F02Test")
    public String getF02Test() {
        return F02Test;
    }

    public void setF02Test(String f02Test) {
        F02Test = f02Test;
    }

    @Column(name = "q2Test")
    public String getQ2Test() {
        return q2Test;
    }

    public void setQ2Test(String q2Test) {
        this.q2Test = q2Test;
    }

    @Column(name = "qPrime2Test")
    public String getQPrime2Test() {
        return qPrime2Test;
    }

    public void setQPrime2Test(String qPrime2Test) {
        this.qPrime2Test = qPrime2Test;
    }

    @Column(name = "MAEqTest")
    public String getMAEqTest() {
        return MAEqTest;
    }

    public void setMAEqTest(String mAEqTest) {
        MAEqTest = mAEqTest;
    }

    @Column(name = "MAEqPrimeTest")
    public String getMAEqPrimeTest() {
        return MAEqPrimeTest;
    }

    public void setMAEqPrimeTest(String mAEqPrimeTest) {
        MAEqPrimeTest = mAEqPrimeTest;
    }

    @Column(name = "MSETest")
    public String getMSETest() {
        return MSETest;
    }

    public void setMSETest(String mSETest) {
        MSETest = mSETest;
    }

    @Column(name = "MAETest")
    public String getMAETest() {
        return MAETest;
    }

    public void setMAETest(String mAETest) {
        MAETest = mAETest;
    }

    @Column(name = "AccuracyTraining")
    public String getAccuracyTraining() {
        return AccuracyTraining;
    }

    public void setAccuracyTraining(String accuracyTraining) {
        AccuracyTraining = accuracyTraining;
    }

    @Column(name = "CCRNormalizedAccuracyTraining")
    public String getCCRNormalizedAccuracyTraining() {
        return CCRNormalizedAccuracyTraining;
    }

    public void setCCRNormalizedAccuracyTraining(String cCRNormalizedAccuracyTraining) {
        CCRNormalizedAccuracyTraining = cCRNormalizedAccuracyTraining;
    }

    @Column(name = "AccuracyWithGroupWeightsTraining")
    public String getAccuracyWithGroupWeightsTraining() {
        return AccuracyWithGroupWeightsTraining;
    }

    public void setAccuracyWithGroupWeightsTraining(String accuracyWithGroupWeightsTraining) {
        AccuracyWithGroupWeightsTraining = accuracyWithGroupWeightsTraining;
    }

    @Column(name = "CCRWithGroupWeightsTraining")
    public String getCCRWithGroupWeightsTraining() {
        return CCRWithGroupWeightsTraining;
    }

    public void setCCRWithGroupWeightsTraining(String cCRWithGroupWeightsTraining) {
        CCRWithGroupWeightsTraining = cCRWithGroupWeightsTraining;
    }

    @Column(name = "AccuracyMaxErrBasedTraining")
    public String getAccuracyMaxErrBasedTraining() {
        return AccuracyMaxErrBasedTraining;
    }

    public void setAccuracyMaxErrBasedTraining(String accuracyMaxErrBasedTraining) {
        AccuracyMaxErrBasedTraining = accuracyMaxErrBasedTraining;
    }

    @Column(name = "CCRMaxErrBasedTraining")
    public String getCCRMaxErrBasedTraining() {
        return CCRMaxErrBasedTraining;
    }

    public void setCCRMaxErrBasedTraining(String cCRMaxErrBasedTraining) {
        CCRMaxErrBasedTraining = cCRMaxErrBasedTraining;
    }

    @Column(name = "AccuracyAvErrBasedTraining")
    public String getAccuracyAvErrBasedTraining() {
        return AccuracyAvErrBasedTraining;
    }

    public void setAccuracyAvErrBasedTraining(String accuracyAvErrBasedTraining) {
        AccuracyAvErrBasedTraining = accuracyAvErrBasedTraining;
    }

    @Column(name = "CCRAvErrBasedTraining")
    public String getCCRAvErrBasedTraining() {
        return CCRAvErrBasedTraining;
    }

    public void setCCRAvErrBasedTraining(String cCRAvErrBasedTraining) {
        CCRAvErrBasedTraining = cCRAvErrBasedTraining;
    }

    @Column(name = "AccuracyTest")
    public String getAccuracyTest() {
        return AccuracyTest;
    }

    public void setAccuracyTest(String accuracyTest) {
        AccuracyTest = accuracyTest;
    }

    @Column(name = "CCRNormalizedAccuracyTest")
    public String getCCRNormalizedAccuracyTest() {
        return CCRNormalizedAccuracyTest;
    }

    public void setCCRNormalizedAccuracyTest(String cCRNormalizedAccuracyTest) {
        CCRNormalizedAccuracyTest = cCRNormalizedAccuracyTest;
    }

    @Column(name = "AccuracyWithGroupWeightsTest")
    public String getAccuracyWithGroupWeightsTest() {
        return AccuracyWithGroupWeightsTest;
    }

    public void setAccuracyWithGroupWeightsTest(String accuracyWithGroupWeightsTest) {
        AccuracyWithGroupWeightsTest = accuracyWithGroupWeightsTest;
    }

    @Column(name = "CCRWithGroupWeightsTest")
    public String getCCRWithGroupWeightsTest() {
        return CCRWithGroupWeightsTest;
    }

    public void setCCRWithGroupWeightsTest(String cCRWithGroupWeightsTest) {
        CCRWithGroupWeightsTest = cCRWithGroupWeightsTest;
    }

    @Column(name = "AccuracyMaxErrBasedTest")
    public String getAccuracyMaxErrBasedTest() {
        return AccuracyMaxErrBasedTest;
    }

    public void setAccuracyMaxErrBasedTest(String accuracyMaxErrBasedTest) {
        AccuracyMaxErrBasedTest = accuracyMaxErrBasedTest;
    }

    @Column(name = "CCRMaxErrBasedTest")
    public String getCCRMaxErrBasedTest() {
        return CCRMaxErrBasedTest;
    }

    public void setCCRMaxErrBasedTest(String cCRMaxErrBasedTest) {
        CCRMaxErrBasedTest = cCRMaxErrBasedTest;
    }

    @Column(name = "AccuracyAvErrBasedTest")
    public String getAccuracyAvErrBasedTest() {
        return AccuracyAvErrBasedTest;
    }

    public void setAccuracyAvErrBasedTest(String accuracyAvErrBasedTest) {
        AccuracyAvErrBasedTest = accuracyAvErrBasedTest;
    }

    @Column(name = "CCRAvErrBasedTest")
    public String getCCRAvErrBasedTest() {
        return CCRAvErrBasedTest;
    }

    public void setCCRAvErrBasedTest(String cCRAvErrBasedTest) {
        CCRAvErrBasedTest = cCRAvErrBasedTest;
    }

    //END GETTERS AND SETTERS

}
