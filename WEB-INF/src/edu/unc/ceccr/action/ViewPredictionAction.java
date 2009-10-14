package edu.unc.ceccr.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.task.Task;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;

public class ViewPredictionAction extends ActionSupport {
	
	private User user;
	private String predictionId;
	private Prediction prediction;
	private List<Predictor> predictors;
	private List<String> compounds; //list of compoundIds for display & image requests

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
			predictionId = ((String[]) context.getParameters().get("id"))[0];
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
			}
			if(predictionId == null){
				Utility.writeToStrutsDebug("No prediction ID supplied.");
			}
			else{
				Utility.writeToStrutsDebug("prediction id: " + predictionId);
				prediction = PopulateDataObjects.getPredictionById(Long.parseLong(predictionId), session);
				prediction.setDatasetDisplay(PopulateDataObjects.getDataSetById(prediction.getDatasetId(), session).getFileName());
				if(predictionId == null){
					Utility.writeToStrutsDebug("Invalid prediction ID supplied.");
				}
				predictors = new ArrayList<Predictor>();
				String[] predictorIds = prediction.getPredictorIds().split("\\s+");
				for(int i = 0; i < predictorIds.length; i++){
					predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(predictorIds[i]), session));
				}
			}
		}

		session.close();
		
		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to viewPrediction page.");
		}
		else{
			Utility.writeToStrutsDebug("Cannot load page.");
		}
		
		return result;
	}

	
	public String getPredictionId() {
		return predictionId;
	}
	public void setPredictionId(String predictionId) {
		this.predictionId = predictionId;
	}

	public Prediction getPrediction() {
		return prediction;
	}
	public void setPrediction(Prediction prediction) {
		this.prediction = prediction;
	}

	public List<Predictor> getPredictors() {
		return predictors;
	}
	public void setPredictors(List<Predictor> predictors) {
		this.predictors = predictors;
	}
	
	public List<String> getCompounds() {
		return compounds;
	}
	public void setCompounds(List<String> compounds) {
		this.compounds = compounds;
	}
}