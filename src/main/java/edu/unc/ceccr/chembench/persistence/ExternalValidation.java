package edu.unc.ceccr.chembench.persistence;

import javax.persistence.*;

@Entity
@Table(name = "cbench_externalValidation")
public class ExternalValidation implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    int numTotalModels = 0;
    private int externalValId;
    private Long predictorId;
    private String compoundId;
    private float predictedValue;
    private float actualValue;
    private int numModels;
    private String standDev;

    public ExternalValidation() {
    }

    ;

    public ExternalValidation(int externalValId, Long predictorId, String compoundId, float predictedValue,
                              float actualValue, int numModels, String standDev) {
        super();
        this.externalValId = externalValId;
        this.predictorId = predictorId;
        this.compoundId = compoundId;
        this.predictedValue = predictedValue;
        this.actualValue = actualValue;
        this.numModels = numModels;
        this.standDev = standDev;
    }

    @Column(name = "std_deviation")
    public String getStandDev() {
        return standDev;
    }

    public void setStandDev(String standDev) {
        this.standDev = standDev;
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
        if (compoundId.length() > 255) {
            compoundId = compoundId.substring(0, 255);
        }
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

    @Column(name = "predictor_id")
    public Long getPredictorId() {
        return predictorId;
    }

    public void setPredictorId(Long predictorId) {
        this.predictorId = predictorId;
    }

    @Transient
    public Integer getNumTotalModels() {
        return numTotalModels;
    }

    public void setNumTotalModels(int i) {
        this.numTotalModels = i;
    }


}
