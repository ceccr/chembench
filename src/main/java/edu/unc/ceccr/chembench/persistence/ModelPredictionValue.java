package edu.unc.ceccr.chembench.persistence;

import javax.persistence.*;

@Entity
@Table(name = "cbench_modelPredictionValue")
public class ModelPredictionValue implements java.io.Serializable {
    /*
        create table cbench_modelPredictionValues (
		id INT(12) UNSIGNED auto_increment PRIMARY KEY,
		modelId VARCHAR(255),
		modelType INT(12) UNSIGNED,
		datasetId INT(12) UNSIGNED,
		compoundId VARCHAR(255),
		predictedValue VARCHAR(255),
		appDomainCutoff VARCHAR(255),
		FOREIGN KEY (datasetId) REFERENCES cbench_dataset(id) ON DELETE CASCADE ON UPDATE CASCADE
		);
	*/


    private Long id;

    //reference a model
    private Long modelId;
    private String modelType;

    //reference a compound
    private Long datasetId;
    private String compoundId;

    //value and associated info
    private String predictedValue;
    private String appDomainCutoff;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "modelId")
    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Column(name = "modelType")
    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    @Column(name = "datasetId")
    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    @Column(name = "compoundId")
    public String getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(String compoundId) {
        this.compoundId = compoundId;
    }

    @Column(name = "predictedValue")
    public String getPredictedValue() {
        return predictedValue;
    }

    public void setPredictedValue(String predictedValue) {
        this.predictedValue = predictedValue;
    }

    @Column(name = "appDomainCutoff")
    public String getAppDomainCutoff() {
        return appDomainCutoff;
    }

    public void setAppDomainCutoff(String appDomainCutoff) {
        this.appDomainCutoff = appDomainCutoff;
    }

}
