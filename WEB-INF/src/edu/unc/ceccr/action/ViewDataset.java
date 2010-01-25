package edu.unc.ceccr.action;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
		private String compoundId;
		private String activityValue;
		
		public String getCompoundId() {
			return compoundId;
		}
		public void setCompoundId(String compoundId) {
			this.compoundId = compoundId;
		}
		public String getActivityValue() {
			return activityValue;
		}
		public void setActivityValue(String activityValue) {
			this.activityValue = activityValue;
		}
	}
	private ArrayList<Compound> datasetCompounds; 
	
	public String loadPage() throws Exception {
		String result = SUCCESS;
		Utility.writeToDebug("a");
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		Session session = HibernateUtil.getSession();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			Utility.writeToDebug("b");
			user = (User) context.getSession().get("user");
			String datasetId = ((String[]) context.getParameters().get("id"))[0];
			
			String orderBy = null;
			if(context.getParameters().get("orderBy") != null){
				 orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
			}
			String pagenumstr = null;
			if(context.getParameters().get("pagenum") != null){
				pagenumstr = ((String[]) context.getParameters().get("pagenum"))[0]; //how many to skip (pagination)
			}
			
			int pagenum = 0;
			if(pagenumstr != null){
				pagenum = Integer.parseInt(pagenumstr);
			}

			Utility.writeToDebug("c");
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			if(datasetId == null){
				Utility.writeToStrutsDebug("No dataset ID supplied.");
			}
			else{
				Utility.writeToDebug("d");
				//get dataset
				Utility.writeToStrutsDebug("dataset id: " + datasetId);
				dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
				if(datasetId == null){
					Utility.writeToStrutsDebug("Invalid prediction ID supplied.");
				}
				
				int limit = Integer.parseInt(user.getViewDatasetCompoundsPerPage()); //compounds per page to display
				int offset = pagenum * limit; //which compoundid to start on

				Utility.writeToDebug("e");
				
				//get compounds
				String datasetUser = dataset.getUserName();
				if(datasetUser.equals("_all")){
					datasetUser = "all-users";
				}
				
				String datasetDir = Constants.CECCR_USER_BASE_PATH + datasetUser + "/";
				datasetDir += "DATASETS/" + dataset.getFileName() + "/";
				Utility.writeToDebug("opening file: " + datasetDir + dataset.getSdfFile());
				ArrayList<String> compoundIDs = DatasetFileOperations.getSDFCompoundList(datasetDir + dataset.getSdfFile());

				if(compoundIDs == null){
					Utility.writeToDebug("why would this be null?");
				}
				
				Utility.writeToDebug("f0");
				for(String cid: compoundIDs){
					Utility.writeToDebug("string: " + cid);
					Compound c = new Compound();
					Utility.writeToDebug("yay");
					c.compoundId = cid;
					Utility.writeToDebug("ckltert");
					datasetCompounds.add(c);
					Utility.writeToDebug("blarg");
				}
				
				Utility.writeToDebug("f");
				//get activity values (if applicable)
				if(! dataset.getDatasetType().equals(Constants.PREDICTION)){
					HashMap<String, String> actIdsAndValues = DatasetFileOperations.getActFileIdsAndValues(datasetDir + dataset.getActFile());
					
					for(Compound c: datasetCompounds){
						c.activityValue = actIdsAndValues.get(c.compoundId);
					}
				}

				Utility.writeToDebug("g");
				//sort the compound array
				if(orderBy == null || orderBy.equals("") || orderBy.equals("compoundId")){
					//sort by compoundId
					Collections.sort(datasetCompounds, new Comparator<Compound>() {
					    public int compare(Compound o1, Compound o2) {
				    		return o2.compoundId.compareTo(o1.compoundId);
					    }});
				}
				else if(orderBy == "activityValue" && ! dataset.getDatasetType().equals(Constants.PREDICTION)){
					Collections.sort(datasetCompounds, new Comparator<Compound>() {
					    public int compare(Compound o1, Compound o2) {
					    	float f1 = Float.parseFloat(o1.activityValue);
					    	float f2 = Float.parseFloat(o2.activityValue);
					    	return (f2 > f1? 1:-1);
					    }});
				}

				Utility.writeToDebug("h");
				//pick out the ones to be displayed on the page based on offset and limit
				if(offset != -1 && limit != -1){
					for(int i = 0; i < datasetCompounds.size(); i++){
						if(i < offset || i > offset + limit){
							//don't display this compound
							datasetCompounds.remove(i);
							i--;
						}				
						else{
							//leave it in the array
						}
					}
				}
				Utility.writeToDebug("i");
				
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