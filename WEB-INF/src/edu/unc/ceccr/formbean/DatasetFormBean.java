/**
 * Bean to handle form for dataset.  
 * @author msypa
 * @date 02/11/08
 * 
 */
package edu.unc.ceccr.formbean;

import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;


public class DatasetFormBean extends ValidatorForm {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String knnType;
	
	private String upload;

	private String datasetName;

	private FormFile sdFileModeling = null;

	private FormFile actFile = null;
	
	private FormFile sdFilePrediction= null;

	private String dataSetDescription;
	
	
	public String getDataSetDescription() {
		return dataSetDescription;
	}
	public void setDataSetDescription(String dataSetDescription) {
		this.dataSetDescription = dataSetDescription;
	}
	
	public FormFile getActFile() {
		return actFile;
	}

	private String message;

	public void setActFile(FormFile actFile) {
		this.actFile = actFile;
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUpload() {
		return upload;
	}
	public void setUpload(String upload) {
		this.upload = upload;
	}
	public FormFile getSdFileModeling() {
		return sdFileModeling;
	}
	public void setSdFileModeling(FormFile sdFileModeling) {
		this.sdFileModeling = sdFileModeling;
	}
	public FormFile getSdFilePrediction() {
		return sdFilePrediction;
	}
	public void setSdFilePrediction(FormFile sdFilePrediction) {
		this.sdFilePrediction = sdFilePrediction;
	}
	public String getKnnType() {
		return knnType;
	}
	
	public void setKnnType(String knnType) {
		this.knnType = knnType;
	}
	public String getDatasetname() {
		return datasetName;
	}
	public void setDatasetname(String datasetName) {
		this.datasetName = datasetName;
	}
	
}