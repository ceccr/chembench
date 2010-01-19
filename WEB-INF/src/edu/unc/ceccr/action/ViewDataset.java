package edu.unc.ceccr.action;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import edu.unc.ceccr.persistence.PredictionValue;
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

public class ViewDataset extends ActionSupport {
	
	private User user;
	private DataSet dataset; 

	public class Compound{
		//using a class instead of two arraylists for sortability.
		String compound;
		String activityValue;
	}
	private ArrayList<Compound> datasetCompounds; 
	
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
			String datasetId = ((String[]) context.getParameters().get("id"))[0];
			String orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
			String limit = ((String[]) context.getParameters().get("limit"))[0]; //how many to get
			String offset = ((String[]) context.getParameters().get("offset"))[0]; //how many to skip (pagination)
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			if(datasetId == null){
				Utility.writeToStrutsDebug("No dataset ID supplied.");
			}
			else{
				user.getViewDatasetCompoundsPerPage();
				user.getViewPredictionCompoundsPerPage();
				
				//get compounds
				String datasetDir = Constants.CECCR_USER_BASE_PATH + "DATASETS/" + dataset.getFileName() + "/";
				ArrayList<String> compounds = DatasetFileOperations.getSDFCompoundList(datasetDir + dataset.getSdfFile());
				
				//get activity values (if applicable)
				if(dataset.getDatasetType().equals(Constants.))
				DatasetFileOperations.getACTCompoundList(datasetDir + dataset.getActFile());
				DatasetFileOperations.getActFileValues(datasetDir + dataset.getActFile());
				
				//sort the compound array
				if(orderBy == null || orderBy.equals("") || orderBy.equals("compoundId")){
					//sort by compoundId
					
				}
			
				//pick out the ones to be displayed on the page based on orderBy, offset, and limit
				Utility.writeToStrutsDebug("dataset id: " + datasetId);
				dataset = PopulateDataObjects.getDataSetById(datasetId, session);
				if(datasetId == null){
					Utility.writeToStrutsDebug("Invalid prediction ID supplied.");
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

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public DataSet getDataset() {
		return dataset;
	}
	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
	
	public ArrayList<Compound> getDatasetCompounds() {
		return datasetCompounds;
	}
	public void setDatasetCompounds(ArrayList<Compound> datasetCompounds) {
		this.datasetCompounds = datasetCompounds;
	}
	
}