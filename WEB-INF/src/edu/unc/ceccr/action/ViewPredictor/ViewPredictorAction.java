package edu.unc.ceccr.action.ViewPredictor;

import java.util.ArrayList;

import edu.unc.ceccr.action.ViewAction;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
//struts2


public class ViewPredictorAction extends ViewAction {

	//Basic parameters. Inherited by all subclasses.
	
	protected Predictor selectedPredictor;
	protected DataSet dataset;
	
	ArrayList<Predictor> childPredictors;
	//End basic parameters

	//used by ext validation and models pages. 
	ArrayList<String> foldNums = new ArrayList<String>(); //usually contains 1 to n; ext validation also has "All". 
	protected String currentFoldNumber = "0";
	
	//Params used by all the models pages
	protected String isYRandomPage;
	protected String orderBy;
	protected String sortDirection;
	protected String mostFrequentDescriptors = "";
	
	
	public class descriptorFrequency{
		private String descriptor;
		private int numOccs;
		
		public String getDescriptor() {
			return descriptor;
		}
		public void setDescriptor(String descriptor) {
			this.descriptor = descriptor;
		}
		public int getNumOccs() {
			return numOccs;
		}
		public void setNumOccs(int numOccs) {
			this.numOccs = numOccs;
		}
	}
	//End params used by all models pages
	
	public String getBasicParameters() throws Exception {
		//this function gets params that all subclasses will need.
		String basic = checkBasicParams();
		if(!basic.equals(SUCCESS)) return basic; 
		session = HibernateUtil.getSession();
		selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(objectId), session);
		if(selectedPredictor == null  || (!selectedPredictor.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user.getUserName().equals(selectedPredictor.getUserName()))){
			Utility.writeToStrutsDebug("Invalid predictor ID supplied. ");
			errorStrings.add("Invalid predictor ID supplied.");
			session.close();
			return ERROR;
		}
		
		Long datasetId = selectedPredictor.getDatasetId();
		dataset = PopulateDataObjects.getDataSetById(datasetId, session);
		
		childPredictors = PopulateDataObjects.getChildPredictors(selectedPredictor, session);
		if(context.getParameters().get("currentFoldNumber") != null){
			currentFoldNumber = ((String[]) context.getParameters().get("currentFoldNumber"))[0];
		}
		session.close();
		return SUCCESS;
	}
	
	public String getModelsPageParameters() throws Exception{
		//gets parameters used by each modeling page
		//assumes getBasicParameters has already been called
		
		isYRandomPage = ((String[]) context.getParameters().get("isYRandomPage"))[0];

		if(context.getParameters().get("sortDirection") != null){
			sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0];
		}
		if(context.getParameters().get("orderBy") != null){
			orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
		}
		
		return SUCCESS;
	}
	
	//getters and setters

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public Predictor getSelectedPredictor() {
		return selectedPredictor;
	}
	public void setSelectedPredictor(Predictor selectedPredictor) {
		this.selectedPredictor = selectedPredictor;
	}

	public DataSet getDataset() {
		return dataset;
	}
	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}

	public String getMostFrequentDescriptors() {
		return mostFrequentDescriptors;
	}
	public void setMostFrequentDescriptors(String mostFrequentDescriptors) {
		this.mostFrequentDescriptors = mostFrequentDescriptors;
	}

	public String getIsYRandomPage() {
		return isYRandomPage;
	}
	public void setIsYRandomPage(String isYRandomPage) {
		this.isYRandomPage = isYRandomPage;
	}

	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public Integer getCurrentFoldNumber() {
		return Integer.parseInt(currentFoldNumber);
	}
	public void setCurrentFoldNumber(String currentFoldNumber) {
		this.currentFoldNumber = currentFoldNumber;
	}
	
	public ArrayList<String> getFoldNums() {
		return foldNums;
	}
	public void setFoldNums(ArrayList<String> foldNums) {
		this.foldNums = foldNums;
	}
	
	//End getters and setters

}