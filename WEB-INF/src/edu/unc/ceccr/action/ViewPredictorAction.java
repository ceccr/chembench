package edu.unc.ceccr.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

import edu.unc.ceccr.action.ViewPredictionAction.CompoundPredictions;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class ViewPredictorAction extends ActionSupport {

	private User user;
	private Predictor selectedPredictor;
	private String predictorId;
	private List<KnnModel> models;
	private List<KnnModel> randomModels;
	private List<KnnPlusModel> knnPlusModels;
	private List<KnnPlusModel> knnPlusRandomModels;
	private List<ExternalValidation> externalValValues;
	private List<String> residuals;
	private String dataType;
	private String orderBy;
	private String sortDirection;
	private String mostFrequentDescriptors = "";
	
	public String loadExternalValidationSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
			//get external validation compounds of predictor
			externalValValues = PopulateDataObjects.getExternalValidationValues(selectedPredictor, session);
			
			//calculate residuals and fix significant figures on output data
			residuals = new ArrayList<String>();
			Iterator<ExternalValidation> eit = externalValValues.iterator();
			int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
			while(eit.hasNext()){
				ExternalValidation e = eit.next();
				if(e.getNumModels() != 0){
					String residual = DecimalFormat.getInstance().format(e.getActualValue() - e.getPredictedValue()).replaceAll(",", "");
					residuals.add(Utility.roundSignificantFigures(residual, sigfigs));
				}
				else{
					residuals.add("");
				}
				String predictedValue = DecimalFormat.getInstance().format(e.getPredictedValue()).replaceAll(",", "");
				e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));  
				if(! e.getStandDev().equalsIgnoreCase("No value")){
					e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
				}
			}
		}
		
		getModels(session);
		
		return result;
	}
	
	
	public String loadModelsSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}

			if(context.getParameters().get("orderBy") != null){
				 orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
			}
			if(context.getParameters().get("sortDirection") != null){
				sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0];
			}
			
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
			if(selectedPredictor == null){
				Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
			}

			getModels(session);
			
			//get descriptor freqs from models
			HashMap<String, Integer> descriptorFreqMap  = new HashMap<String, Integer>();
			if(models != null){
				for(KnnModel m : models){
					if(m.getDescriptorsUsed() != null && ! m.getDescriptorsUsed().equals("")){
						String[] descriptorArray = m.getDescriptorsUsed().split("\\s+");
						for(int i = 0; i < descriptorArray.length; i++){
							if(descriptorFreqMap.get(descriptorArray[i]) == null){
								descriptorFreqMap.put(descriptorArray[i], 1);
							}
							else{
								//increment
								descriptorFreqMap.put(descriptorArray[i], descriptorFreqMap.get(descriptorArray[i]) + 1);
							}
						}
					}
				}
			}
			if(knnPlusModels != null){
				for(KnnPlusModel m : knnPlusModels){
					if(m.getDimsNames() != null && ! m.getDimsNames().equals("")){
						String[] descriptorArray = m.getDimsNames().split("\\s+");
						for(int i = 0; i < descriptorArray.length; i++){
							if(descriptorFreqMap.get(descriptorArray[i]) == null){
								descriptorFreqMap.put(descriptorArray[i], 1);
							}
							else{
								//increment
								descriptorFreqMap.put(descriptorArray[i], descriptorFreqMap.get(descriptorArray[i]) + 1);
							}
						}
					}
				}
			}
			
			ArrayList<descriptorFrequency> descriptorFrequencies = new ArrayList<descriptorFrequency>();
			ArrayList<String> mapKeys = new ArrayList(descriptorFreqMap.keySet());
			for(String k: mapKeys){
				descriptorFrequency df = new descriptorFrequency();
				df.setDescriptor(k);
				df.setNumOccs(descriptorFreqMap.get(k));
				descriptorFrequencies.add(df);
			}
			
			Collections.sort(descriptorFrequencies, new Comparator<descriptorFrequency>() {
			    public int compare(descriptorFrequency df1, descriptorFrequency df2) {
			    	return (df1.getNumOccs() > df2.getNumOccs()? -1 : 1);
			    }});
			if(descriptorFrequencies.size() >= 5){
				//if there weren't at least 5 descriptors, don't even bother - no summary needed
				mostFrequentDescriptors = "The 5 most frequent descriptors used in your models were: ";
				for(int i = 0; i < 5; i++){
					mostFrequentDescriptors += descriptorFrequencies.get(i).getDescriptor() + " (" + 
						descriptorFrequencies.get(i).getNumOccs() + " models)";
					if(i < 4){
						mostFrequentDescriptors += ", ";
					}
				}
				mostFrequentDescriptors += ".";
			}
			
		}
		
		return result;
	}
	
	public String loadGrovesSection() throws Exception {
		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
		}
		return result;
	}
	
	
	public String loadYRandomSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
			getModels(session);
		}
		
		return result;
	}

	public String loadWarningsSection() throws Exception {

		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
		}
		return result;
	}
	
	
	public String loadPage() throws Exception {

		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			else{
				Utility.writeToStrutsDebug("predictor id: " + predictorId);
				selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
				
				if(selectedPredictor == null){
					Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
				}
			}
			
			
			//the predictor has now been viewed. Update DB accordingly.
			if(! selectedPredictor.getHasBeenViewed().equals(Constants.YES)){
				selectedPredictor.setHasBeenViewed(Constants.YES);
				Transaction tx = null;
				try {
					tx = session.beginTransaction();
					session.saveOrUpdate(selectedPredictor);
					tx.commit();
				} catch (RuntimeException e) {
					if (tx != null)
						tx.rollback();
					Utility.writeToDebug(e);
				}
			}
		}

		session.close();
		
		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to viewPredictor page.");
		}
		else{
			Utility.writeToStrutsDebug("Cannot load page.");
		}
		
		//go to the page
		return result;
	}
	public String execute() throws Exception {
		return SUCCESS;
	}
	

	private void getModels(Session session) throws Exception{
		
		//get models associated with predictor
		if(selectedPredictor.getDatasetId() != null){
			datasetUserName = PopulateDataObjects.getDataSetById(selectedPredictor.getDatasetId(), session).getUserName();
		}
		dataType = selectedPredictor.getActivityType();
		
		if(selectedPredictor.getModelMethod().equals(Constants.KNN)){
			models = new ArrayList<KnnModel>();
			randomModels = new ArrayList<KnnModel>();
			ArrayList<KnnModel> allModels = new ArrayList<KnnModel>();
			List temp = PopulateDataObjects.getModelsByPredictorId(Long.parseLong(predictorId), session);
			if(temp != null){
				allModels.addAll(temp);
	
				Iterator<KnnModel> it = allModels.iterator();
				while(it.hasNext()){
					KnnModel m = it.next();
					if(m.getFlowType().equalsIgnoreCase(Constants.MAINKNN)){
						models.add(m);
					}
					else{
						randomModels.add(m);
					}
				}
			}
		}
		else if(selectedPredictor.getModelMethod().equals(Constants.KNNSA) ||
				selectedPredictor.getModelMethod().equals(Constants.KNNGA)){
			knnPlusModels = new ArrayList<KnnPlusModel>();
			knnPlusRandomModels = new ArrayList<KnnPlusModel>();
			ArrayList<KnnPlusModel> allModels = new ArrayList<KnnPlusModel>();
			List temp = PopulateDataObjects.getKnnPlusModelsByPredictorId(Long.parseLong(predictorId), session);
			if(temp != null){
				allModels.addAll(temp);
	
				Iterator<KnnPlusModel> it = allModels.iterator();
				while(it.hasNext()){
					KnnPlusModel m = it.next();
					if(m.getIsYRandomModel().equals(Constants.NO)){
						knnPlusModels.add(m);
					}
					else{
						knnPlusRandomModels.add(m);
					}
				}
			}
		}
	}
	
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
	
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	String datasetUserName;
	public String getDatasetUserName() {
		return datasetUserName;
	}
	public void setDatasetUserName(String datasetUserName) {
		this.datasetUserName = datasetUserName;
	}
	
	public Predictor getSelectedPredictor() {
		return selectedPredictor;
	}
	public void setSelectedPredictor(Predictor selectedPredictor) {
		this.selectedPredictor = selectedPredictor;
	}
	
	public String getPredictorId() {
		return predictorId;
	}
	public void setPredictorId(String predictorId) {
		this.predictorId = predictorId;
	}
	
	public List<KnnModel> getModels() {
		return models;
	}
	public void setModels(List<KnnModel> models) {
		this.models = models;
	}
	
	public List<KnnModel> getRandomModels() {
		return randomModels;
	}
	public void setRandomModels(List<KnnModel> randomModels) {
		this.randomModels = randomModels;
	}
	
	public List<KnnPlusModel> getKnnPlusModels() {
		return knnPlusModels;
	}
	public void setKnnPlusModels(List<KnnPlusModel> knnPlusModels) {
		this.knnPlusModels = knnPlusModels;
	}
	
	public List<KnnPlusModel> getKnnPlusRandomModels() {
		return knnPlusRandomModels;
	}
	public void setKnnPlusRandomModels(List<KnnPlusModel> knnPlusRandomModels) {
		this.knnPlusRandomModels = knnPlusRandomModels;
	}

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public List<ExternalValidation> getExternalValValues() {
		return externalValValues;
	}
	public void setExternalValValues(List<ExternalValidation> externalValValues) {
		this.externalValValues = externalValValues;
	}
	public List<String> getResiduals() {
		return residuals;
	}
	public void setResiduals(List<String> residuals) {
		this.residuals = residuals;
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
	
	public String getMostFrequentDescriptors() {
		return mostFrequentDescriptors;
	}
	public void setMostFrequentDescriptors(String mostFrequentDescriptors) {
		this.mostFrequentDescriptors = mostFrequentDescriptors;
	}

}