package edu.unc.ceccr.action;

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

import edu.unc.ceccr.action.ViewPredictionAction.CompoundPredictions;
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

	private User user;
	private Predictor selectedPredictor;
	private DataSet dataset;
	private String predictorId;
	private List<KnnModel> models;
	private List<KnnModel> randomModels;
	private List<KnnPlusModel> knnPlusModels;
	private List<KnnPlusModel> knnPlusRandomModels;
	private List<SvmModel> svmModels;
	private List<SvmModel> svmRandomModels;
	private List<RandomForestGrove> randomForestGroves;
	private List<RandomForestGrove> randomForestYRandomGroves;
	private List<RandomForestTree> randomForestTrees;
	private List<RandomForestTree> randomForestYRandomTrees;
	private List<ExternalValidation> externalValValues;
	private List<String> residuals;
	private String dataType;
	private String orderBy;
	private String sortDirection;
	private String mostFrequentDescriptors = "";
	private RandomForestParameters randomForestParameters;
	private KnnParameters knnParameters;
	private KnnPlusParameters knnPlusParameters;
	private SvmParameters svmParameters;
	private String hasGoodModels = Constants.YES;
	
	//used in creation of confusion matrix (category modeling only)
	public class ConfusionMatrixRow{
		ArrayList<Integer> values;

		public ArrayList<Integer> getValues() {
			return values;
		}
		public void setValues(ArrayList<Integer> values) {
			this.values = values;
		}
	}
	ArrayList<ConfusionMatrixRow> confusionMatrix;
	String ccr = "";
	ArrayList<String> uniqueObservedValues;
	String rSquared = "";
	
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
			
			if(externalValValues == null || externalValValues.isEmpty()){
				externalValValues = new ArrayList<ExternalValidation>();
				return result;
			}
			
			dataset = PopulateDataObjects.getDataSetById(selectedPredictor.getDatasetId(), session);
			
			//calculate residuals and fix significant figures on output data
			residuals = new ArrayList<String>();
			Iterator<ExternalValidation> eit = externalValValues.iterator();
			int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
			int numExtValuesWithNoModels = 0;
			while(eit.hasNext()){
				ExternalValidation e = eit.next();
				if(e.getNumModels() != 0){
					String residual = DecimalFormat.getInstance().format(e.getActualValue() - e.getPredictedValue()).replaceAll(",", "");
					residuals.add(Utility.roundSignificantFigures(residual, sigfigs));
				}
				else{
					numExtValuesWithNoModels++;
					residuals.add("");
				}
				String predictedValue = DecimalFormat.getInstance().format(e.getPredictedValue()).replaceAll(",", "");
				e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));  
				if(! e.getStandDev().equalsIgnoreCase("No value")){
					e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
				}
			}
			if(numExtValuesWithNoModels == externalValValues.size()){
				//all external predictions were empty, meaning there were no good models.
				//can't calculate any summary data.
				hasGoodModels = Constants.NO;
				return result;
			}
			
			if(selectedPredictor.getActivityType().equals(Constants.CATEGORY)){
				//if category model, create confusion matrix.
				//round off the predicted values to nearest integer.
				try{
				
				//scan through to find the unique observed values
				uniqueObservedValues = new ArrayList<String>();
				for(ExternalValidation ev : externalValValues){
					int observedValue = Math.round(ev.getActualValue());
					int predictedValue = Math.round(ev.getPredictedValue());
					if(! uniqueObservedValues.contains("" + observedValue)){
						uniqueObservedValues.add("" + observedValue);
					}
					//if a value is predicted but not observed, we still need
					//a spot in the matrix for that, so make a spot for those too.
					if(! uniqueObservedValues.contains("" + predictedValue)){
						uniqueObservedValues.add("" + predictedValue);
					}
				}
				
				
				//set up a confusion matrix to store counts of each (observed, predicted) possibility
				confusionMatrix = new ArrayList<ConfusionMatrixRow>();
				
				//make a matrix of zeros
				for(int i = 0; i < uniqueObservedValues.size(); i++){
					ConfusionMatrixRow row = new ConfusionMatrixRow();
					row.values = new ArrayList<Integer>();
					for(int j = 0; j < uniqueObservedValues.size(); j++){
						row.values.add(0);
					}
					confusionMatrix.add(row);
				}
				
				double CCR = 0.0;	
				HashMap<Integer, Integer> correctPredictionCounts = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> observedValueCounts = new HashMap<Integer, Integer>();
				
				//populate the confusion matrix and count values needed to calculate CCR
				for(ExternalValidation ev : externalValValues){
					//for each observed-predicted pair, update
					//the confusion matrix accordingly
					int observedValue = Math.round(ev.getActualValue());
					int predictedValue = Math.round(ev.getPredictedValue());
					int observedValueIndex = uniqueObservedValues.indexOf("" + observedValue);
					int predictedValueIndex = uniqueObservedValues.indexOf("" + predictedValue);
					int previousCount = confusionMatrix.get(observedValueIndex).values.get(predictedValueIndex);
					confusionMatrix.get(observedValueIndex).values.set(predictedValueIndex, previousCount+1);
					
					if(observedValueCounts.containsKey(observedValue)){
						observedValueCounts.put(observedValue, observedValueCounts.get(observedValue) + 1);
					}
					else{
						observedValueCounts.put(observedValue, 1);
					}
					
					if(predictedValue == observedValue){
						if(correctPredictionCounts.containsKey(observedValue)){
							correctPredictionCounts.put(observedValue, correctPredictionCounts.get(observedValue) + 1);
						}
						else{
							correctPredictionCounts.put(observedValue, 1);
						}
					}
					
				}
				
				//calculate the CCR
				//formula: 1/n(correct 1 / actual 1 + correct 2 / actual 2 ...correct n /predicted n)
				Double ccrDouble = 0.0;
				for(Integer d: correctPredictionCounts.keySet()){
					ccrDouble += new Double(correctPredictionCounts.get(d)) / new Double(observedValueCounts.get(d));
				}
				ccrDouble = ccrDouble / new Double(observedValueCounts.keySet().size());
				ccr = Utility.roundSignificantFigures(Utility.doubleToString(ccrDouble), 4);
				
				}catch(Exception ex){
					Utility.writeToDebug(ex);
				}
			}
			else if(selectedPredictor.getActivityType().equals(Constants.CONTINUOUS) && externalValValues.size() > 1){
				//if continuous, calculate overall r^2 and... r0^2? or something? 
				//just r^2 for now, more later.
				Double avg = 0.0;
				for(ExternalValidation ev : externalValValues){
					avg += ev.getActualValue();
				}
				avg /= externalValValues.size();
				Double ssErr = 0.0;
				for(String residual : residuals){
					if(! residual.isEmpty()){
						ssErr += Double.parseDouble(residual) * Double.parseDouble(residual);
					}
				}
				Double ssTot = 0.0;
				for(ExternalValidation ev : externalValValues){
					ssTot += (ev.getActualValue() - avg) * (ev.getActualValue() - avg);
				}
				if(ssTot != 0){
					rSquared = Utility.roundSignificantFigures("" + (1 - (ssErr / ssTot)), 4);
				}
			}
		}
		
		getModels(session);
		
		return result;
	}
	
	
	public String loadParametersSection() throws Exception {
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
			
			//get predictor object
			predictorId = ((String[]) context.getParameters().get("id"))[0];
			if(predictorId == null){
				Utility.writeToStrutsDebug("No predictor ID supplied.");
			}
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			
			if(selectedPredictor == null){
				Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
			}
			
			if(selectedPredictor.getModelMethod().equals(Constants.RANDOMFOREST)){
				randomForestParameters = PopulateDataObjects.getRandomForestParametersById(selectedPredictor.getModelingParametersId(), session);
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.KNNGA) || 
					selectedPredictor.getModelMethod().equals(Constants.KNNSA)){
				knnPlusParameters = PopulateDataObjects.getKnnPlusParametersById(selectedPredictor.getModelingParametersId(), session);
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.KNN)){
				knnParameters = PopulateDataObjects.getKnnParametersById(selectedPredictor.getModelingParametersId(), session);
			}
			else if(selectedPredictor.getModelMethod().equals(Constants.SVM)){
				svmParameters = PopulateDataObjects.getSvmParametersById(selectedPredictor.getModelingParametersId(), session);
				if(svmParameters.getSvmTypeCategory().equals("0")){
					svmParameters.setSvmTypeCategory("C-SVC");
				}
				else{
					svmParameters.setSvmTypeCategory("nu-SVC");
				}
				if(svmParameters.getSvmTypeCategory().equals("3")){
					svmParameters.setSvmTypeCategory("epsilon-SVR");
				}
				else{
					svmParameters.setSvmTypeCategory("nu-SVR");
				}
				if(svmParameters.getSvmKernel().equals("0")){
					svmParameters.setSvmKernel("linear");
				}
				else if(svmParameters.getSvmKernel().equals("1")){
					svmParameters.setSvmKernel("polynomial");
				}
				else if(svmParameters.getSvmKernel().equals("2")){
					svmParameters.setSvmKernel("radial basis function");
				}
				else if(svmParameters.getSvmKernel().equals("3")){
					svmParameters.setSvmKernel("sigmoid");
				}
				if(svmParameters.getSvmHeuristics().equals("0")){
					svmParameters.setSvmHeuristics("NO");
				}
				else{
					svmParameters.setSvmHeuristics("YES");
				}
				if(svmParameters.getSvmProbability().equals("0")){
					svmParameters.setSvmProbability("NO");
				}
				else{
					svmParameters.setSvmProbability("YES");
				}
			}
		}
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
			
			if(selectedPredictor.getModelMethod().equals(Constants.SVM)){
				//no descriptor selection in SVM.
				return result;
			}
			
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
			else{
				predictorId = ((String[]) context.getParameters().get("id"))[0];
				if(predictorId == null){
					Utility.writeToStrutsDebug("No predictor ID supplied.");
				}
				selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
				
				if(selectedPredictor == null){
					Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
				}
				dataType = selectedPredictor.getActivityType();

				List<RandomForestGrove> rfGroves = PopulateDataObjects.getRandomForestGrovesByPredictorId(Long.parseLong(predictorId), session);
				randomForestGroves = new ArrayList<RandomForestGrove>();
				randomForestYRandomGroves = new ArrayList<RandomForestGrove>();
				
				if(rfGroves != null){
					for(RandomForestGrove rfg : rfGroves){
						if(rfg.getIsYRandomModel().equals(Constants.YES)){
							randomForestYRandomGroves.add(rfg);
						}
						else{
							randomForestGroves.add(rfg);
						}
					}
				}
				else{
					Utility.writeToDebug("rfgroves null!");
				}
			}
		}
		return result;
	}
	

	public String loadTreesSection() throws Exception {
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
			else{
				predictorId = ((String[]) context.getParameters().get("id"))[0];
				if(predictorId == null){
					Utility.writeToStrutsDebug("No predictor ID supplied.");
				}
				selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
				
				if(selectedPredictor == null){
					Utility.writeToStrutsDebug("Invalid predictor ID supplied.");
				}
				dataType = selectedPredictor.getActivityType();

				List<RandomForestGrove> rfGroves = PopulateDataObjects.getRandomForestGrovesByPredictorId(Long.parseLong(predictorId), session);
				

				randomForestTrees = new ArrayList<RandomForestTree>();
				randomForestYRandomTrees = new ArrayList<RandomForestTree>();
				if(rfGroves != null){
					for(RandomForestGrove rfg : rfGroves){
						ArrayList<RandomForestTree> rfTrees = (ArrayList<RandomForestTree>) PopulateDataObjects.getRandomForestTreesByGroveId(rfg.getId(), session);
						
						if(rfg.getIsYRandomModel().equals(Constants.YES)){
							if(rfTrees != null){
								randomForestYRandomTrees.addAll(rfTrees);
							}
						}
						else{
							randomForestTrees.addAll(rfTrees);
						}
					}
				}
				for(RandomForestTree rfTree: randomForestTrees){
					String splitNumber = rfTree.getTreeFileName();
					splitNumber = splitNumber.split("_")[3];
					rfTree.setTreeFileName(splitNumber);
				}
				for(RandomForestTree rfTree: randomForestYRandomTrees){
					String splitNumber = rfTree.getTreeFileName();
					splitNumber = splitNumber.split("_")[3];
					rfTree.setTreeFileName(splitNumber);
				}
			}
		}
		session.close();
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
		else if(selectedPredictor.getModelMethod().equals(Constants.SVM)){
			svmModels = new ArrayList<SvmModel>();
			svmRandomModels = new ArrayList<SvmModel>();
			ArrayList<SvmModel> allModels = new ArrayList<SvmModel>();
			List temp = PopulateDataObjects.getSvmModelsByPredictorId(Long.parseLong(predictorId), session);
			if(temp != null){
				allModels.addAll(temp);
	
				Iterator<SvmModel> it = allModels.iterator();
				while(it.hasNext()){
					SvmModel m = it.next();
					if(m.getIsYRandomModel().equals(Constants.NO)){
						svmModels.add(m);
					}
					else{
						svmRandomModels.add(m);
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

	public List<RandomForestGrove> getRandomForestGroves() {
		return randomForestGroves;
	}
	public void setRandomForestGroves(List<RandomForestGrove> randomForestGroves) {
		this.randomForestGroves = randomForestGroves;
	}

	public List<RandomForestGrove> getRandomForestYRandomGroves() {
		return randomForestYRandomGroves;
	}
	public void setRandomForestYRandomGroves(
			List<RandomForestGrove> randomForestYRandomGroves) {
		this.randomForestYRandomGroves = randomForestYRandomGroves;
	}

	public List<RandomForestTree> getRandomForestTrees() {
		return randomForestTrees;
	}
	public void setRandomForestTrees(List<RandomForestTree> randomForestTrees) {
		this.randomForestTrees = randomForestTrees;
	}

	public List<RandomForestTree> getRandomForestYRandomTrees() {
		return randomForestYRandomTrees;
	}
	public void setRandomForestYRandomTrees(
			List<RandomForestTree> randomForestYRandomTrees) {
		this.randomForestYRandomTrees = randomForestYRandomTrees;
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

	public RandomForestParameters getRandomForestParameters() {
		return randomForestParameters;
	}
	public void setRandomForestParameters(
			RandomForestParameters randomForestParameters) {
		this.randomForestParameters = randomForestParameters;
	}

	public KnnParameters getKnnParameters() {
		return knnParameters;
	}
	public void setKnnParameters(KnnParameters knnParameters) {
		this.knnParameters = knnParameters;
	}

	public KnnPlusParameters getKnnPlusParameters() {
		return knnPlusParameters;
	}
	public void setKnnPlusParameters(KnnPlusParameters knnPlusParameters) {
		this.knnPlusParameters = knnPlusParameters;
	}

	public SvmParameters getSvmParameters() {
		return svmParameters;
	}
	public void setSvmParameters(SvmParameters svmParameters) {
		this.svmParameters = svmParameters;
	}

	public ArrayList<ConfusionMatrixRow> getConfusionMatrix() {
		return confusionMatrix;
	}
	public void setConfusionMatrix(ArrayList<ConfusionMatrixRow> confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}

	public String getCcr() {
		return ccr;
	}
	public void setCcr(String ccr) {
		this.ccr = ccr;
	}

	public ArrayList<String> getUniqueObservedValues() {
		return uniqueObservedValues;
	}
	public void setUniqueObservedValues(ArrayList<String> uniqueObservedValues) {
		this.uniqueObservedValues = uniqueObservedValues;
	}

	public String getrSquared() {
		return rSquared;
	}
	public void setrSquared(String rSquared) {
		this.rSquared = rSquared;
	}

	public String getHasGoodModels() {
		return hasGoodModels;
	}
	public void setHasGoodModels(String hasGoodModels) {
		this.hasGoodModels = hasGoodModels;
	}

}