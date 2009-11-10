package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 
import org.apache.struts2.interceptor.SessionAware;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.taskObjects.QsarPredictionTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.SmilesPredictionWorkflow;


public class PredictionFormActions extends ActionSupport{

	public String loadSelectPredictorPage() throws Exception{
		
		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
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

		Session session = HibernateUtil.getSession();
		userPredictors = PopulateDataObjects.populatePredictors(user.getUserName(), true, false, session);
		session.close();
		
		return result;
	}
	
	public String makeSmilesPrediction() throws Exception{
		String result = SUCCESS;
		
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		//use the same session for all data requests
		Session session = HibernateUtil.getSession();

		Map k = context.getParameters();
		Utility.writeToDebug("starting params");
		for(Object key : k.keySet()){
			Utility.writeToDebug(key.toString() + " : " + ((String[]) k.get(key))[0]);
		}
		Utility.writeToDebug("ending params");
			
		String smiles = ((String[]) context.getParameters().get("smiles"))[0];
		String cutoff = ((String[]) context.getParameters().get("cutoff"))[0];
		String predictorIds = ((String[]) context.getParameters().get("predictorIds"))[0];

		Utility.writeToDebug(" 1: " + smiles + " 2: " + cutoff + " 3: " + predictorIds);
	
		Utility.writeToDebug(user.getUserName());
		Utility.writeToDebug("SMILES predids: " + predictorIds);

		int numCompounds = 1;
		String[] selectedPredictorIdArray = predictorIds.split("\\s+");
		int numModels = 0;
		
		/*
		for(int i = 0; i < selectedPredictorIdArray.length; i++){
			numModels += PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorIdArray[i]), session).getNumTestModels();
		}*/
		
		Predictor: <%=session.getAttribute("SmilesPredictPredictor")%><br />
		Predicted value: <%=session.getAttribute("SmilesPredictedValue")%><br />
		Predicting Models / Total Models: <%=session.getAttribute("SmilesUsedModels")%> / <%=session.getAttribute("SmilesTotalModels")%><br />
		Standard deviation: <%=session.getAttribute("SmilesStdDev")%><br />
		
		for(int i = 0; i < selectedPredictorIdArray.length; i++){
			Predictor selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(selectedPredictorIdArray[i]), s);
			
			selectedPredictorNames.add(selectedPredictor.getName());
			
			//We're keeping a count of how many times each predictor was used.
	        //So, increment number of times used on each and save each predictor object.
	        selectedPredictor.setNumPredictions(selectedPredictor.getNumPredictions() + 1);
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				s.saveOrUpdate(selectedPredictor);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			}

			selectedPredictors.add(selectedPredictor);
		}
		
		/*
		 getPredictorFiles(userName, predictor, String toDir);
		 Utility.writeToDebug("Called SMILES predict action. Predictor ID: " + predictor+" SMILES: "+smiles);
			Utility.writeToMSDebug("user::"+userName+" predictor::"+predictor+" smiles::"+smiles);
			
			String smilesDir = Constants.CECCR_USER_BASE_PATH + userName + "/SMILES/";
			
			//make sure there's nothing in the dir already.
			FileAndDirOperations.deleteDirContents(smilesDir);
			
			//generate an SDF from this SMILES string
			SmilesPredictionWorkflow.smilesToSDF(smiles, smilesDir);
			
			//create descriptors for the SDF, normalize them, and make a prediction
			String[] predValues = SmilesPredictionWorkflow.PredictSmilesSDF(smilesDir, userName, predictor, Float.parseFloat(cutoff));
			
			session.removeAttribute("SmilesPredictPredictor");
			session.removeAttribute("SmilesPredictSmiles");
			session.removeAttribute("SmilesCutoff");
			session.removeAttribute("SmilesUsedModels");
			session.removeAttribute("SmilesTotalModels");
			session.removeAttribute("SmilesStdDev");
			session.removeAttribute("SmilesPredictedValue");
			
			session.setAttribute("SmilesPredictPredictor", predictor.getName());
			session.setAttribute("SmilesPredictSmiles", smiles);
			session.setAttribute("SmilesCutoff", cutoff);
			
			if(predValues[2].equalsIgnoreCase("no")){
				//no prediction.
				session.setAttribute("SmilesUsedModels", "0");
				session.setAttribute("SmilesPredictedValue", "Molecule is outside the domain of this predictor. Use a higher cutoff value to force a low-confidence prediction.");
				session.setAttribute("SmilesStdDev", "N/A");
				session.setAttribute("SmilesTotalModels", "?");
			}
			else{
				session.setAttribute("SmilesUsedModels", predValues[1]);
				session.setAttribute("SmilesPredictedValue", predValues[2]);
				if(predValues.length > 3){
					//Standard deviation will only be calculated if there's more than one pred value
					session.setAttribute("SmilesStdDev", predValues[3]);
				}
				else{
					session.setAttribute("SmilesStdDev", "N/A");
				}
				session.setAttribute("SmilesTotalModels", predictor.getNumTotalModels());
			}
		 */
		
		
		
		
		
		//we need to populate a set of vars for the page to read from
		/*
	Input:
	SMILES string: <%=session.getAttribute("SmilesPredictSmiles")%><br />
	Cutoff: <%=session.getAttribute("SmilesCutoff")%><br />
	
	Results:
	<s:iterator>
	Predictor: <%=session.getAttribute("SmilesPredictPredictor")%><br />
	Predicted value: <%=session.getAttribute("SmilesPredictedValue")%><br />
	Predicting Models / Total Models: <%=session.getAttribute("SmilesUsedModels")%> / <%=session.getAttribute("SmilesTotalModels")%><br />
	Standard deviation: <%=session.getAttribute("SmilesStdDev")%><br />
	<br />
	</s:iterator>
	
</font></body>
*/
		
		//give back the session at the end
		session.close();
		
		return result;
	}

	public String loadMakePredictionsPage() throws Exception{
		
		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
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
		
		
		//use the same session for all data requests
		Session session = HibernateUtil.getSession();

		//get list of predictor IDs from the checked checkboxes
		selectedPredictorIds = predictorCheckBoxes.replaceAll(",", " ");
		String[] predictorIds = selectedPredictorIds.split("\\s+");
		if(predictorIds.length == 0){
			Utility.writeToStrutsDebug("no predictor chosen!");
			result = ERROR;
		}
		for(int i = 0; i < predictorIds.length; i++){
			Predictor p = PopulateDataObjects.getPredictorById(Long.parseLong(predictorIds[i]), session);
			selectedPredictors.add(p);
		}
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)
		userDatasetNames = PopulateDataObjects.populateDatasetNames(user.getUserName(), true, session);
		userPredictorNames = PopulateDataObjects.populatePredictorNames(user.getUserName(), true, session);
		userPredictionNames = PopulateDataObjects.populatePredictionNames(user.getUserName(), true, session);
		userTaskNames = PopulateDataObjects.populateTaskNames(user.getUserName(), false, session);
		userDatasets = PopulateDataObjects.populateDatasetsForPrediction(user.getUserName(), true, session);
		
		//give back the session at the end
		session.close();
		return result;
	}

	public String makeDatasetPrediction() throws Exception {
		//prediction form submitted, so create a new prediction task and run it
	
		ActionContext context = ActionContext.getContext();
		user = (User) context.getSession().get("user");
		
		//use the same session for all data requests
		Session session = HibernateUtil.getSession();
		
		DataSet predictionDataset = PopulateDataObjects.getDataSetById(selectedDatasetId, session);
		String sdf = predictionDataset.getSdfFile();
		
		Utility.writeToDebug(user.getUserName());
		Utility.writeToDebug("predids: " + selectedPredictorIds);

		QsarPredictionTask predTask = new QsarPredictionTask(user.getUserName(), jobName, sdf, 
				cutOff, selectedPredictorIds, predictionDataset);

		predTask.setUp();
		int numCompounds = predictionDataset.getNumCompound();
		String[] ids = selectedPredictorIds.split("\\s+");
		int numModels = 0;
		for(int i = 0; i < ids.length; i++){
			numModels += PopulateDataObjects.getPredictorById(Long.parseLong(ids[i]), session).getNumTestModels();
		}
		Queue.getInstance().addJob(predTask,user.getUserName(), jobName, numCompounds, numModels);

		//give back the session at the end
		session.close();
		return SUCCESS;
	}	
	
	public String execute() throws Exception {
		return SUCCESS;
	}	
	
	//variables used for JSP display
	private User user;
	private List<Predictor> userPredictors;
	private List<String> userDatasetNames;
	private List<String> userPredictorNames;
	private List<String> userPredictionNames;
	private List<String> userTaskNames;
	private List<DataSet> userDatasets;
	private String predictorCheckBoxes;
	private List<Predictor> selectedPredictors = new ArrayList<Predictor>();
	
	public User getUser(){
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public List<Predictor> getUserPredictors(){
		return userPredictors;
	}
	public void setUserPredictors(List<Predictor> userPredictors) {
		this.userPredictors = userPredictors;
	}
	
	public List<String> getUserDatasetNames(){
		return userDatasetNames;
	}
	public void setUserDatasetNames(List<String> userDatasetNames) {
		this.userDatasetNames = userDatasetNames;
	}
	
	public List<String> getUserPredictorNames(){
		return userPredictorNames;
	}
	public void setUserPredictorNames(List<String> userPredictorNames) {
		this.userPredictorNames = userPredictorNames;
	}
	
	public List<String> getUserPredictionNames(){
		return userPredictionNames;
	}
	public void setUserPredictionNames(List<String> userPredictionNames) {
		this.userPredictionNames = userPredictionNames;
	}
	
	public List<String> getUserTaskNames(){
		return userTaskNames;
	}
	public void setUserTaskNames(List<String> userTaskNames) {
		this.userTaskNames = userTaskNames;
	}
	
	public List<DataSet> getUserDatasets(){
		return userDatasets;
	}
	public void setUserDatasets(List<DataSet> userDatasets) {
		this.userDatasets = userDatasets;
	}
	
	public List<Predictor> getSelectedPredictors() {
		return selectedPredictors;
	}
	public void setSelectedPredictors(List<Predictor> selectedPredictors) {
		this.selectedPredictors = selectedPredictors;
	}
	
	//populated by the JSP form
	private Long selectedDatasetId;
	private String cutOff = "0.5";
	private String jobName;
	private String selectedPredictorIds;
	
	public Long getSelectedDatasetId() {
		return selectedDatasetId;
	}
	public void setSelectedDatasetId(Long selectedDatasetId) {
		this.selectedDatasetId = selectedDatasetId;
	}
	
	public String getCutOff() {
		return cutOff;
	}
	public void setCutOff(String cutOff) {
		this.cutOff = cutOff;
	}

	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getPredictorCheckBoxes() {
		return predictorCheckBoxes;
	}
	public void setPredictorCheckBoxes(String predictorCheckBoxes) {
		this.predictorCheckBoxes = predictorCheckBoxes;
	}

	public String getSelectedPredictorIds() {
		return selectedPredictorIds;
	}
	public void setSelectedPredictorIds(String selectedPredictorIds) {
		this.selectedPredictorIds = selectedPredictorIds;
	}

}