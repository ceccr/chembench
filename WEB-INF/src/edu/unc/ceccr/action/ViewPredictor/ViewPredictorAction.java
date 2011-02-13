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

	protected String dataType;
	protected ActionContext context;
	
	protected Session session;
	//End basic parameters
	
	//Params used by all the models pages
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
	
	//Subclass instances for each sub-page of the View Predictor page.
	
	private PredictorPage predictorPage;
	private ExternalValidationPage externalValidationPage;

	private RandomForestModelsPage randomForestTreesPage;
	private RandomForestModelsPage randomForestYRandomTreesPage;
	
	//planned development:
	//private RandomForestModelsPage randomForestGrovesPage;
	//private RandomForestModelsPage randomForestYRandomGrovesPage;
	
	private SvmModelsPage svmModelsPage;
	private SvmModelsPage svmYRandomModelsPage;
	
	private KnnPlusModelsPage knnPlusModelsPage;
	private KnnPlusModelsPage knnPlusYRandomModelsPage;

	private KnnModelsPage knnModelsPage;
	private KnnModelsPage knnYRandomModelsPage;
	
	private ParametersPage parametersPage;
	
	//End subclass instances
	
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
		
		predictorId = ((String[]) context.getParameters().get("id"))[0];
		if(predictorId == null){
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
		
		return SUCCESS;
	}
	
	// Functions to load each page. Delegated to subclasses to keep things lookin' pretty.

	public String loadPredictorPage() throws Exception{
		getBasicParameters();
		predictorPage = new PredictorPage();

		if(context == null){
			Utility.writeToDebug("Context in VPA is null");
		}
		else{
			Utility.writeToDebug("Context in VPA is not null");
		}
		
		String result = predictorPage.load();
		session.close();
		return result;
	}

	public String updatePredictor() throws Exception{
		getBasicParameters();
		predictorPage = new PredictorPage();
		String result = predictorPage.updatePredictor();
		session.close();
		return result;
	}

	public String loadParametersPage() throws Exception{
		getBasicParameters();
		parametersPage = new ParametersPage();
		String result = parametersPage.load();
		session.close();
		return result;
	}
	
	public String loadExternalValidationPage() throws Exception{
		getBasicParameters();
		externalValidationPage = new ExternalValidationPage();
		String result = externalValidationPage.load();
		session.close();
		return result;
	}
	
	public String loadKnnModelsPage() throws Exception{
		getBasicParameters();
		knnModelsPage = new KnnModelsPage();
		String result = knnModelsPage.load();
		session.close();
		return result;
	}

	public String loadKnnYRandomModelsPage() throws Exception{
		getBasicParameters();
		knnYRandomModelsPage = new KnnModelsPage();
		String result = knnYRandomModelsPage.load();
		session.close();
		return result;
	}

	public String loadKnnPlusModelsPage() throws Exception{
		getBasicParameters();
		knnPlusModelsPage = new KnnPlusModelsPage();
		String result = knnPlusModelsPage.load();
		session.close();
		return result;
	}

	public String loadKnnPlusYRandomModelsPage() throws Exception{
		getBasicParameters();
		knnPlusYRandomModelsPage = new KnnPlusModelsPage();
		String result = knnPlusYRandomModelsPage.load();
		session.close();
		return result;
	}

	public String loadRandomForestTreesPage() throws Exception{
		getBasicParameters();
		randomForestTreesPage = new RandomForestModelsPage();
		String result = randomForestTreesPage.loadTrees();
		session.close();
		return result;
	}

	public String loadRandomForestYRandomTreesPage() throws Exception{
		getBasicParameters();
		randomForestYRandomTreesPage = new RandomForestModelsPage();
		String result = randomForestYRandomTreesPage.loadTrees();
		session.close();
		return result;
	}

	public String loadSvmModelsPage() throws Exception{
		getBasicParameters();
		svmModelsPage = new SvmModelsPage();
		String result = svmModelsPage.load();
		session.close();
		return result;
	}
	
	public String loadSvmYRandomModelsPage() throws Exception{
		getBasicParameters();
		svmYRandomModelsPage = new SvmModelsPage();
		String result = svmYRandomModelsPage.load();
		session.close();
		return result;
	}

	/*
	private RandomForestModelsPage randomForestTreesPage;
	private RandomForestModelsPage randomForestYRandomTreesPage;
	private RandomForestModelsPage randomForestGrovesPage;
	private RandomForestModelsPage randomForestYRandomGrovesPage;
	
	private ParametersPage parametersPage;*/
		
	// End functions to load each page.
	
	//A big ugly pile of getters and setters

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

	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getMostFrequentDescriptors() {
		return mostFrequentDescriptors;
	}
	public void setMostFrequentDescriptors(String mostFrequentDescriptors) {
		this.mostFrequentDescriptors = mostFrequentDescriptors;
	}

	public PredictorPage getPredictorPage() {
		return predictorPage;
	}
	public void setPredictorPage(PredictorPage predictorPage) {
		this.predictorPage = predictorPage;
	}

	public ExternalValidationPage getExternalValidationPage() {
		return externalValidationPage;
	}
	public void setExternalValidationPage(
			ExternalValidationPage externalValidationPage) {
		this.externalValidationPage = externalValidationPage;
	}

	public RandomForestModelsPage getRandomForestTreesPage() {
		return randomForestTreesPage;
	}
	public void setRandomForestTreesPage(
			RandomForestModelsPage randomForestTreesPage) {
		this.randomForestTreesPage = randomForestTreesPage;
	}

	public RandomForestModelsPage getRandomForestYRandomTreesPage() {
		return randomForestYRandomTreesPage;
	}
	public void setRandomForestYRandomTreesPage(
			RandomForestModelsPage randomForestYRandomTreesPage) {
		this.randomForestYRandomTreesPage = randomForestYRandomTreesPage;
	}

	public SvmModelsPage getSvmModelsPage() {
		return svmModelsPage;
	}
	public void setSvmModelsPage(SvmModelsPage svmModelsPage) {
		this.svmModelsPage = svmModelsPage;
	}

	public SvmModelsPage getSvmYRandomModelsPage() {
		return svmYRandomModelsPage;
	}
	public void setSvmYRandomModelsPage(SvmModelsPage svmYRandomModelsPage) {
		this.svmYRandomModelsPage = svmYRandomModelsPage;
	}

	public KnnPlusModelsPage getKnnPlusModelsPage() {
		return knnPlusModelsPage;
	}
	public void setKnnPlusModelsPage(KnnPlusModelsPage knnPlusModelsPage) {
		this.knnPlusModelsPage = knnPlusModelsPage;
	}

	public KnnPlusModelsPage getKnnPlusYRandomModelsPage() {
		return knnPlusYRandomModelsPage;
	}
	public void setKnnPlusYRandomModelsPage(
			KnnPlusModelsPage knnPlusYRandomModelsPage) {
		this.knnPlusYRandomModelsPage = knnPlusYRandomModelsPage;
	}

	public KnnModelsPage getKnnModelsPage() {
		return knnModelsPage;
	}
	public void setKnnModelsPage(KnnModelsPage knnModelsPage) {
		this.knnModelsPage = knnModelsPage;
	}

	public KnnModelsPage getKnnYRandomModelsPage() {
		return knnYRandomModelsPage;
	}
	public void setKnnYRandomModelsPage(KnnModelsPage knnYRandomModelsPage) {
		this.knnYRandomModelsPage = knnYRandomModelsPage;
	}

	public ParametersPage getParametersPage() {
		return parametersPage;
	}
	public void setParametersPage(ParametersPage parametersPage) {
		this.parametersPage = parametersPage;
	}
	
	//End big ugly getter-setter pile
}