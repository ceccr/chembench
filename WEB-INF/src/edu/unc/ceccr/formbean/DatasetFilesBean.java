package edu.unc.ceccr.formbean;

import org.apache.struts.validator.ValidatorForm;
/**
 * Bean to handle form for additional dataset files creation. These files will be used
 * during visualization of the dataset. 
 * @author msypa
 * @date 12/05/08
 * 
 */
public class DatasetFilesBean extends ValidatorForm {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private String[] sketches;
	private String[] represent;
	private String[] similarity_measure;
	private String datasetname;
	private String sdfName; 
	
	public String getSdfName() {
		return sdfName;
	}
	public void setSdfName(String sdfName) {
		this.sdfName = sdfName;
	}
	/**
	 * @return the sketches
	 */
	/*public String[] getSketches() {
		return sketches;
	}*/
	/**
	 * @param sketches the sketches to set
	 */
	/*public void setSketches(String[] sketches) {
		this.sketches = sketches;
	}*/
	/**
	 * @return the represent
	 */
	public String[] getRepresent() {
		return represent;
	}
	/**
	 * @param represent the represent to set
	 */
	public void setRepresent(String[] represent) {
		this.represent = represent;
	}
	/**
	 * @return the similarity_measure
	 */
	public String[] getSimilarity_measure() {
		return similarity_measure;
	}
	/**
	 * @param similarity_measure the similarity_measure to set
	 */
	public void setSimilarity_measure(String[] similarity_measure) {
		this.similarity_measure = similarity_measure;
	}
	/**
	 * @return the datasetname
	 */
	public String getDatasetname() {
		return datasetname;
	}
	/**
	 * @param datasetname the datasetname to set
	 */
	public void setDatasetname(String datasetname) {
		this.datasetname = datasetname;
	}

	
	
	
}
