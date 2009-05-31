package edu.unc.ceccr.formbean;

import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorForm;

import edu.unc.ceccr.global.Constants;

public class PredictorFormBean extends ValidatorForm {
	
	private String userName;
	
	private String jobName;

	private String predictorName;
	
	private Long selectedPredictorId;
	
	private Long selectedDatasetID;

	private FormFile sdFile = null;

	private String cutOff;

	private int uploadOrSelect = Constants.SELECT;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getPredictorName() {
		return predictorName;
	}

	public void setPredictorName(String predictorName) {
		this.predictorName = predictorName;
	}

	public FormFile getSdFile() {
		return sdFile;
	}

	public void setSdFile(FormFile sdFile) {
		this.sdFile = sdFile;
	}

	public String getCutOff() {
		return cutOff;
	}

	public void setCutOff(String cutOff) {
		this.cutOff = cutOff;
	}


	public Long getSelectedPredictorId() {
		return selectedPredictorId;
	}

	public void setSelectedPredictorId(Long selectedPredictorId) {
		this.selectedPredictorId = selectedPredictorId;
	}

	public int getUploadOrSelect() {
		return uploadOrSelect;
	}

	public void setUploadOrSelect(int uploadOrSelect) {
		this.uploadOrSelect = uploadOrSelect;
	}

	public Long getSelectedDatasetID() {
		return selectedDatasetID;
	}
	
	public void setSelectedDatasetID(Long selectedDatasetID) {
		this.selectedDatasetID = selectedDatasetID;
	}

}