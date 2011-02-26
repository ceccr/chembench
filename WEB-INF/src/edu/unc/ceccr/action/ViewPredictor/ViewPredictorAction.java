package edu.unc.ceccr.action.ViewPredictor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.action.ViewPredictorAction.ConfusionMatrixRow;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class ViewPredictorAction extends ActionSupport {

	//Basic parameters. Inherited by all subclasses.
	protected User user;
	protected Predictor selectedPredictor;
	protected DataSet dataset;
	protected String predictorId;

	protected ActionContext context;
	
	protected Session session;
	ArrayList<Predictor> childPredictors;
	//End basic parameters
	
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
		session = HibernateUtil.getSession();
		
		context = ActionContext.getContext();
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
			return ERROR;
		}

		user = (User) context.getSession().get("user");
		if(user == null){
			Utility.writeToStrutsDebug("No user is logged in.");
			return LOGIN;
		}
		
		if(context.getParameters().get("predictorId") != null){
			predictorId = ((String[]) context.getParameters().get("predictorId"))[0];
		}
		else{
			Utility.writeToStrutsDebug("No predictor ID supplied.");
			return ERROR;
		}
		
		Utility.writeToStrutsDebug("predictor id: " + predictorId);
		
		selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
		if(selectedPredictor == null){
			Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
			return ERROR;
		}
		
		Long datasetId = selectedPredictor.getDatasetId();
		dataset = PopulateDataObjects.getDataSetById(datasetId, session);
		
		childPredictors = PopulateDataObjects.getChildPredictors(selectedPredictor, session);
		
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

	public String getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(String predictorId) {
		this.predictorId = predictorId;
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

	//End getters and setters
}