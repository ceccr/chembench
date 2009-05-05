package edu.unc.ceccr.taskObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity()
@Table(name = "cbench_prediction_task")
public class PredictionTask {

	private Long id = null;
	
	private Long datasetId;	
	
	public PredictionTask(Long datasetId, Long id) {
		this.datasetId = datasetId;
		this.id = id;
	}

	/**
	 * @return the id
	 */
	@Column(name="id")
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the datasetId
	 */
	@Column(name="datasetID")
	public Long getDatasetId() {
		return datasetId;
	}

	/**
	 * @param datasetId the datasetId to set
	 */
	public void setDatasetId(Long datasetId) {
		this.datasetId = datasetId;
	}
	
	
}
