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
import org.hibernate.Transaction;

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
import edu.unc.ceccr.utilities.NaturalSortComparator;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.CreateActivityChartWorkflow;
import edu.unc.ceccr.persistence.Queue.QueueTask.jobTypes;

public class ViewDataset extends ActionSupport {
	
	private User user;
	private DataSet dataset; 
	private ArrayList<Compound> datasetCompounds; 
	private ArrayList<Compound> externalCompounds; 
	private ArrayList<String> pageNums;
	private String currentPageNumber;
	private String orderBy;
	private String sortDirection;
	private String datasetId; 
	private String webAddress = Constants.WEBADDRESS;

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

	public String loadCompoundsSection() throws Exception {
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
			if(context.getParameters().get("currentPageNumber") != null){
				currentPageNumber = ((String[]) context.getParameters().get("currentPageNumber"))[0]; 	
			}
			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			if(context.getParameters().get("sortDirection") != null){
				sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0]; 	
			}
				
			//get dataset
			Utility.writeToStrutsDebug("dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			
			//define which compounds will appear on page
			int pagenum = Integer.parseInt(currentPageNumber) - 1;
			int limit = Integer.parseInt(user.getViewDatasetCompoundsPerPage()); //compounds per page to display
			int offset = pagenum * limit; //which compoundid to start on
         	
			//get compounds
			datasetCompounds = new ArrayList<Compound>();
			String datasetUser = dataset.getUserName();
			if(datasetUser.equals("_all")){
				datasetUser = "all-users";
			}
			
			String datasetDir = Constants.CECCR_USER_BASE_PATH + datasetUser + "/";
			datasetDir += "DATASETS/" + dataset.getFileName() + "/";
			ArrayList<String> compoundIDs = DatasetFileOperations.getSDFCompoundList(datasetDir + dataset.getSdfFile());
			
			for(String cid: compoundIDs){
				Compound c = new Compound();
				c.setCompoundId(cid);
				datasetCompounds.add(c);
			}
			
			//get activity values (if applicable)
			if(! dataset.getDatasetType().equals(Constants.PREDICTION)){
				HashMap<String, String> actIdsAndValues = DatasetFileOperations.getActFileIdsAndValues(datasetDir + dataset.getActFile());
				
				for(Compound c: datasetCompounds){
					c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
				}
			}

			//sort the compound array
			if(orderBy == null || orderBy.equals("") || orderBy.equals("compoundId")){
				//sort by compoundId
				
				Collections.sort(datasetCompounds, new Comparator<Compound>() {
				    public int compare(Compound o1, Compound o2) {
			    		return Utility.naturalSortCompare(o1.getCompoundId(), o2.getCompoundId());
				    }});
			}
			else if(orderBy.equals("activityValue")){
				Collections.sort(datasetCompounds, new Comparator<Compound>() {
				    public int compare(Compound o1, Compound o2) {
				    	float f1 = Float.parseFloat(o1.getActivityValue());
				    	float f2 = Float.parseFloat(o2.getActivityValue());
				    	return (f2 < f1? 1:-1);
				    }});
			}
			if(sortDirection != null && sortDirection.equals("desc")){
				Collections.reverse(datasetCompounds);
			}
			
			//pick out the ones to be displayed on the page based on offset and limit
			int compoundNum = 0;
			for(int i = 0; i < datasetCompounds.size(); i++){
				if(compoundNum < offset || compoundNum >= (offset + limit)){
					//don't display this compound
					datasetCompounds.remove(i);
					i--;
				}				
				else{
					//leave it in the array
				}
				compoundNum++;
			}

			pageNums = new ArrayList<String>(); //displays the page numbers at the top
			int j = 1;
			for(int i = 0; i < compoundIDs.size(); i += limit){
				String page = Integer.toString(j);
				pageNums.add(page);
				j++;
			}
		}
		return result;
	}
	
	public String loadExternalCompoundsSection() throws Exception {
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

			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			if(context.getParameters().get("orderBy") != null){
				orderBy = ((String[]) context.getParameters().get("orderBy"))[0]; 	
			}
			if(context.getParameters().get("sortDirection") != null){
				sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0]; 	
			}
			//Right now there's no pagination on external compounds. This is because
			//typical datasets have <50 external compounds in them. If a real need arises to add
			//pagination, it's easy enough to add.
			
			//get dataset
			Utility.writeToStrutsDebug("[ext_compounds] dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			
			//load external compounds from file
			externalCompounds = new ArrayList<Compound>();
			String datasetUser = dataset.getUserName();
			if(datasetUser.equals("_all")){
				datasetUser = "all-users";
			}
			
			String datasetDir = Constants.CECCR_USER_BASE_PATH + datasetUser + "/";
			datasetDir += "DATASETS/" + dataset.getFileName() + "/";
			
			HashMap<String, String> actIdsAndValues = DatasetFileOperations.getActFileIdsAndValues(datasetDir + Constants.EXTERNAL_SET_A_FILE);
			
			ArrayList<String> compoundIds = new ArrayList<String>(actIdsAndValues.keySet());
			for(String compoundId : compoundIds){
				Compound c = new Compound();
				c.setCompoundId(compoundId);
				c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
				externalCompounds.add(c);
			}
			
			//sort by activity by default, that seems good
			if(orderBy != null && orderBy.equals("activityValue")){
				Collections.sort(externalCompounds, new Comparator<Compound>() {
				    public int compare(Compound o1, Compound o2) {
				    	float f1 = Float.parseFloat(o1.getActivityValue());
				    	float f2 = Float.parseFloat(o2.getActivityValue());
				    	return (f2 < f1? 1:-1);
				    }});
			}
			else{
				Collections.sort(externalCompounds, new Comparator<Compound>() {
					  public int compare(Compound o1, Compound o2) {
				    		return Utility.naturalSortCompare(o1.getCompoundId(), o2.getCompoundId());
					    }});
			}
			if(sortDirection != null && sortDirection.equals("desc")){
				Collections.reverse(externalCompounds);
			}
			
		}
		return result;
	}

	public String loadVisualizationSection() throws Exception {
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
			
			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			//get dataset
			Utility.writeToStrutsDebug("[ext_compounds] dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			
		}
			
		return result;
	}
	

	public String loadActivityChartSection() throws Exception {
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
			
			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			//get dataset
			Utility.writeToStrutsDebug("[ext_compounds] dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			
			//create activity chart
			CreateActivityChartWorkflow.createChart(datasetId);
			
		}
			
		return result;
	}

	public String loadDescriptorsSection() throws Exception {
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
			
			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			//get dataset
			Utility.writeToStrutsDebug("[ext_compounds] dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
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
			datasetId = ((String[]) context.getParameters().get("id"))[0];
			
			if(context.getParameters().get("orderBy") != null){
				 orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
			}
			String pagenumstr = null;
			if(context.getParameters().get("pagenum") != null){
				pagenumstr = ((String[]) context.getParameters().get("pagenum"))[0]; //how many to skip (pagination)
			}
			
			currentPageNumber = "1";
			if(pagenumstr != null){
				currentPageNumber = pagenumstr;
			}

			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				result = LOGIN;
				return result;
			}
			if(datasetId == null){
				Utility.writeToStrutsDebug("No dataset ID supplied.");
			}
			else{
				dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
				
				//the dataset has now been viewed. Update DB accordingly.
				if(! dataset.getHasBeenViewed().equals(Constants.YES)){
					dataset.setHasBeenViewed(Constants.YES);
					Transaction tx = null;
					try {
						tx = session.beginTransaction();
						session.saveOrUpdate(dataset);
						tx.commit();
					} catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);
					}
				}
				
			}
		}

		session.close();
		
		//log the results
		if(result.equals(SUCCESS)){
			Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to viewDataset page.");
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

	public ArrayList<String> getPageNums() {
		return pageNums;
	}
	public void setPageNums(ArrayList<String> pageNums) {
		this.pageNums = pageNums;
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
	
	public String getCurrentPageNumber() {
		return currentPageNumber;
	}
	public void setCurrentPageNumber(String currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
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

	public String getDatasetId() {
		return datasetId;
	}
	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public ArrayList<Compound> getExternalCompounds() {
		return externalCompounds;
	}
	public void setExternalCompounds(ArrayList<Compound> externalCompounds) {
		this.externalCompounds = externalCompounds;
	}
	
	public String getWebAddress() {
		return webAddress;
	}
	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}
}