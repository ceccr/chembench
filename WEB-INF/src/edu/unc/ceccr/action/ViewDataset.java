package edu.unc.ceccr.action;

import java.io.File;
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
import edu.unc.ceccr.persistence.Compound;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.workflows.visualization.ActivityHistogram;

public class ViewDataset extends ActionSupport {
	
	private User user;
	private DataSet dataset; 
	private ArrayList<Compound> datasetCompounds; 
	private ArrayList<Compound> externalCompounds; 
	private ArrayList<Compound> externalFold; 
	private ArrayList<String> pageNums;
	private String currentPageNumber;
	private ArrayList<String> foldNums;
	private String currentFoldNumber;
	private String orderBy;
	private String editable;
	private String sortDirection;
	private String datasetId; 
	private String externalCompoundsCount;
	private String datasetReference = "";
	private String datasetDescription = "";
	private String datasetTypeDisplay = "";
	private String webAddress = Constants.WEBADDRESS;
	private ArrayList<DescriptorGenerationResult> descriptorGenerationResults;

	public class DescriptorGenerationResult{
		private String descriptorType;
		private String generationResult;
		private String programOutput;
		private String programErrorOutput;
		
		public String getDescriptorType() {
			return descriptorType;
		}
		public void setDescriptorType(String descriptorType) {
			this.descriptorType = descriptorType;
		}
		public String getGenerationResult() {
			return generationResult;
		}
		public void setGenerationResult(String generationResult) {
			this.generationResult = generationResult;
		}
		public String getProgramOutput() {
			return programOutput;
		}
		public void setProgramOutput(String programOutput) {
			this.programOutput = programOutput;
		}
		public String getProgramErrorOutput() {
			return programErrorOutput;
		}
		public void setProgramErrorOutput(String programErrorOutput) {
			this.programErrorOutput = programErrorOutput;
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
			if(context.getParameters().get("currentPageNumber") != null){
				currentPageNumber = ((String[]) context.getParameters().get("currentPageNumber"))[0]; 	
			}
			else{
				currentPageNumber = "1";
			}
			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			if(context.getParameters().get("orderBy") != null){
				 orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
			}
			else{
				orderBy = "compoundId";
			}
			if(context.getParameters().get("sortDirection") != null){
				sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0]; 	
			}
			else{
				sortDirection = "asc";
			}
				
			//get dataset
			Utility.writeToStrutsDebug("dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			if(dataset == null){
				Utility.writeToDebug("Error: Could not load dataset with id: " + datasetId);
			}
			
			//define which compounds will appear on page
			int pagenum, limit, offset;
			if(user.getViewDatasetCompoundsPerPage().equals(Constants.ALL)){
				pagenum = 0;
				limit = 99999999;
				offset = pagenum * limit;
			}
			else{
				pagenum = Integer.parseInt(currentPageNumber) - 1;
				limit = Integer.parseInt(user.getViewDatasetCompoundsPerPage()); //compounds per page to display
				offset = pagenum * limit; //which compoundid to start on
			}
			
			//get compounds
			datasetCompounds = new ArrayList<Compound>();
			String datasetUser = dataset.getUserName();
			
			String datasetDir = Constants.CECCR_USER_BASE_PATH + datasetUser + "/";
			datasetDir += "DATASETS/" + dataset.getName() + "/";
			
			ArrayList<String> compoundIDs = null;
			if(dataset.getXdataFile() != null && ! dataset.getXdataFile().isEmpty()){
				compoundIDs = DatasetFileOperations.getXCompoundNames(datasetDir + dataset.getXdataFile());
			}
			else{
				compoundIDs = DatasetFileOperations.getSDFCompoundNames(datasetDir + dataset.getSdfFile());
			}
			
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
			else{
				orderBy = "compoundId";
			}
			if(context.getParameters().get("sortDirection") != null){
				sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0]; 	
			}
			else{
				sortDirection = "asc";
			}
			//get dataset
			Utility.writeToStrutsDebug("[ext_compounds] dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			
			//load external compounds from file
			externalCompounds = new ArrayList<Compound>();
			String datasetUser = dataset.getUserName();
			
			String datasetDir = Constants.CECCR_USER_BASE_PATH + datasetUser + "/";
			datasetDir += "DATASETS/" + dataset.getName() + "/";
			
			HashMap<String, String> actIdsAndValues = DatasetFileOperations.getActFileIdsAndValues(datasetDir + Constants.EXTERNAL_SET_A_FILE);
			
			if(actIdsAndValues.isEmpty()){
				return result;
			}
			
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
	

	public String loadExternalCompoundsNFoldSection() throws Exception {
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

			if(context.getParameters().get("currentFoldNumber") != null){
				currentFoldNumber = ((String[]) context.getParameters().get("currentFoldNumber"))[0]; 	
			}
			else{
				currentFoldNumber = "1";
			}
			if(context.getParameters().get("datasetId") != null){
				datasetId = ((String[]) context.getParameters().get("datasetId"))[0]; 	
			}
			if(context.getParameters().get("orderBy") != null){
				 orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
			}
			else{
				orderBy = "compoundId";
			}
			if(context.getParameters().get("sortDirection") != null){
				sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0]; 	
			}
			else{
				sortDirection = "asc";
			}
			//get dataset
			Utility.writeToStrutsDebug("[ext_compounds] dataset id: " + datasetId);
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
			if(datasetId == null){
				Utility.writeToStrutsDebug("Invalid dataset ID supplied.");
			}
			String datasetUser = dataset.getUserName();
			String datasetDir = Constants.CECCR_USER_BASE_PATH + datasetUser + "/";
			datasetDir += "DATASETS/" + dataset.getName() + "/";

			foldNums = new ArrayList<String>(); //displays the fold numbers at the top
			int j = 1;
			for(int i = 0; i < Integer.parseInt(dataset.getNumExternalFolds()); i += 1){
				String fold = Integer.toString(j);
				foldNums.add(fold);
				j++;
			}
			
			//load external fold from file
			externalFold = new ArrayList<Compound>();
			int foldNum = Integer.parseInt(currentFoldNumber);
			HashMap<String, String> actIdsAndValues = 
				DatasetFileOperations.getActFileIdsAndValues(datasetDir + dataset.getActFile() + ".fold" + (foldNum));
			
			if(! actIdsAndValues.isEmpty()){
				ArrayList<String> compoundIds = new ArrayList<String>(actIdsAndValues.keySet());
				for(String compoundId : compoundIds){
					Compound c = new Compound();
					c.setCompoundId(compoundId);
					c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
					externalFold.add(c);
				}
			}
					
			if(orderBy != null && orderBy.equals("activityValue")){
				Collections.sort(externalFold, new Comparator<Compound>() {
				    public int compare(Compound o1, Compound o2) {
				    	float f1 = Float.parseFloat(o1.getActivityValue());
				    	float f2 = Float.parseFloat(o2.getActivityValue());
				    	return (f2 < f1? 1:-1);
				    }});
			}
			else{
				Collections.sort(externalFold, new Comparator<Compound>() {
					  public int compare(Compound o1, Compound o2) {
				    		return Utility.naturalSortCompare(o1.getCompoundId(), o2.getCompoundId());
					    }});
			}
			if(sortDirection != null && sortDirection.equals("desc")){
				Collections.reverse(externalFold);
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
			ActivityHistogram.createChart(datasetId);
			
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
		
		descriptorGenerationResults = new ArrayList<DescriptorGenerationResult>();
		String descriptorsDir = Constants.CECCR_USER_BASE_PATH;
		descriptorsDir += dataset.getUserName() + "/";
		descriptorsDir += "DATASETS/" + dataset.getName() + "/Descriptors/Logs/";
		
		//read descriptor program outputs
		DescriptorGenerationResult molconnZResult = new DescriptorGenerationResult();
		molconnZResult.setDescriptorType("MolconnZ");
		if((new File(descriptorsDir + "molconnz.out")).exists()){
			molconnZResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "molconnz.out"));
		}
		if((new File(descriptorsDir + "molconnz.err")).exists()){
			molconnZResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "molconnz.err"));
		}
		if(dataset.getAvailableDescriptors().contains(Constants.MOLCONNZ)){
			molconnZResult.setGenerationResult("Successful");
		}
		else{
			molconnZResult.setGenerationResult("Descriptor generation failed. See program output for details.");
		}
		descriptorGenerationResults.add(molconnZResult);

		
		DescriptorGenerationResult cdkResult = new DescriptorGenerationResult();
		cdkResult.setDescriptorType("CDK");
		if((new File(descriptorsDir + "cdk.out")).exists()){
			cdkResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "cdk.out"));
		}
		if((new File(descriptorsDir + "cdk.err")).exists()){
			cdkResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "cdk.err"));
		}
		if(dataset.getAvailableDescriptors().contains(Constants.CDK)){
			cdkResult.setGenerationResult("Successful");
		}
		else{
			cdkResult.setGenerationResult("Descriptor generation failed. See program output for details.");
		}
		descriptorGenerationResults.add(cdkResult);

		
		
		
		DescriptorGenerationResult dragonHResult = new DescriptorGenerationResult();
		dragonHResult.setDescriptorType("Dragon (with hydrogens)");
		if((new File(descriptorsDir + "dragonH.out")).exists()){
			dragonHResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonH.out"));
		}
		if((new File(descriptorsDir + "dragonH.err")).exists()){
			String dragonErrStr = FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonH.err");
			if(dragonErrStr.contains("error: license not valid on the computer in use")){
				dragonErrStr = "Dragon license invalid or expired.";
			}
			if(dragonErrStr.contains("Access violation")){
				Utility.writeToDebug("DragonX crashed; please contact the system administrator at " + Constants.WEBSITEEMAIL + " to fix this problem.");
			}
			//The Dragon output contains lots of extra info (MAC address of server, that sorta thing)
			//that should not be displayed. Remove it.
			//Sample of stuff we don't want to show:
			/*
			 * dragonX version 1.4 - Command line version for Linux - v.1.4.2 - built on: 2007-12-04 
			 * License file (/usr/local/ceccr/dragon/2010-12-31_drgx_license_UNC.txt) is a valid license file 
			 * User: ceccr (). Date: 2010/02/17 - 00:56:10 Licensed to: UNC-Chapel Hill - License type: Academic 
			 * (Single Workstation) - Expiration Date: 2010/12/31 - MAC address: 00:14:5E:3D:75:24 
			 * Decimal Separator set to: '.' - Thousands Separator set to: ','
			 */
			if(dragonErrStr.contains("Thousands")){
				dragonErrStr = dragonErrStr.substring(dragonErrStr.indexOf("Thousands"), dragonErrStr.length());
				dragonErrStr = dragonErrStr.replace("Thousands Separator set to: ','", "");
				dragonErrStr = dragonErrStr.replaceAll(Constants.CECCR_USER_BASE_PATH, "");
			}
			dragonHResult.setProgramErrorOutput(dragonErrStr);
		}
		if(dataset.getAvailableDescriptors().contains(Constants.DRAGONH)){
			dragonHResult.setGenerationResult("Successful");
		}
		else{
			dragonHResult.setGenerationResult("Descriptor generation failed. See program output for details.");
		}
		descriptorGenerationResults.add(dragonHResult);
		
		DescriptorGenerationResult dragonNoHResult = new DescriptorGenerationResult();
		dragonNoHResult.setDescriptorType("Dragon (without hydrogens)");
		if((new File(descriptorsDir + "dragonNoH.out")).exists()){
			dragonNoHResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonNoH.out"));
		}
		if((new File(descriptorsDir + "dragonNoH.err")).exists()){
			String dragonErrStr = FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonNoH.err");
			if(dragonErrStr.contains("error: license not valid on the computer in use")){
				dragonErrStr = "Dragon license invalid or expired.";
			}
			if(dragonErrStr.contains("Access violation")){
				Utility.writeToDebug("DragonX crashed; please contact the system administrator at " + Constants.WEBSITEEMAIL + " to fix this problem.");
			}
			if(dragonErrStr.contains("Thousands")){
				dragonErrStr = dragonErrStr.substring(dragonErrStr.indexOf("Thousands"), dragonErrStr.length());
				dragonErrStr = dragonErrStr.replace("Thousands Separator set to: ','", "");
				dragonErrStr = dragonErrStr.replaceAll(Constants.CECCR_USER_BASE_PATH, "");
			}
			dragonNoHResult.setProgramErrorOutput(dragonErrStr);
		}
		if(dataset.getAvailableDescriptors().contains(Constants.DRAGONNOH)){
			dragonNoHResult.setGenerationResult("Successful");
		}
		else{
			dragonNoHResult.setGenerationResult("Descriptor generation failed. See program output for details.");
		}
		descriptorGenerationResults.add(dragonNoHResult);
		
		DescriptorGenerationResult moe2DResult = new DescriptorGenerationResult();
		moe2DResult.setDescriptorType(Constants.MOE2D);
		if((new File(descriptorsDir + "moe2d.out")).exists()){
			moe2DResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "moe2d.out"));
		}
		if((new File(descriptorsDir + "moe2d.sh.err")).exists()){
			moe2DResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "moe2d.sh.err"));
		}
		if(dataset.getAvailableDescriptors().contains(Constants.MOE2D)){
			moe2DResult.setGenerationResult("Successful");
		}
		else{
			moe2DResult.setGenerationResult("Descriptor generation failed. See program output for details.");
		}
		descriptorGenerationResults.add(moe2DResult);
		
		DescriptorGenerationResult maccsResult = new DescriptorGenerationResult();
		maccsResult.setDescriptorType(Constants.MACCS);
		if((new File(descriptorsDir + "maccs.out")).exists()){
			maccsResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "maccs.out"));
		}
		if((new File(descriptorsDir + "maccs.sh.err")).exists()){
			maccsResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "maccs.sh.err"));
		}
		if(dataset.getAvailableDescriptors().contains(Constants.MOE2D)){
			maccsResult.setGenerationResult("Successful");
		}
		else{
			maccsResult.setGenerationResult("Descriptor generation failed. See program output for details.");
		}
		descriptorGenerationResults.add(maccsResult);
		
		return result;
	}
	
	public String updateDataset() throws Exception {
		String result = SUCCESS;
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		if(context != null){
			//get dataset id
			datasetId = ((String[]) context.getParameters().get("datasetId"))[0];
			String[] datasetIdAsStringArray = new String[1];
			datasetIdAsStringArray[0] = datasetId;
			context.getParameters().put("id", datasetIdAsStringArray);
			datasetDescription = ((String[]) context.getParameters().get("datasetDescription"))[0];
			datasetReference = ((String[]) context.getParameters().get("datasetReference"))[0];
			
			Session s = HibernateUtil.getSession();
			dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), s);
			dataset.setDescription(datasetDescription);
			dataset.setPaperReference(datasetReference);
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				s.saveOrUpdate(dataset);
				tx.commit();
			}
			catch (Exception ex) {
				Utility.writeToDebug(ex); 
			} 
			finally {
				s.close();
			}
		}
		return loadPage();
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
			
			if(context.getParameters().get("editable") != null && datasetId != null){
				dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);
				if(user.getIsAdmin().equals(Constants.YES)|| user.getUserName().equals(dataset.getUserName())){
					editable = "YES";
				}
			}
			else{
				editable = "NO";
			}
			
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
				if(dataset.getDatasetType().equals(Constants.MODELING) || 
						dataset.getDatasetType().equals(Constants.MODELINGWITHDESCRIPTORS)){
					if(dataset.getSplitType().equals(Constants.NFOLD)){
						externalCompoundsCount = "";
						int smallestFoldSize = 0;
						int largestFoldSize = 0;
						String datasetDir = Constants.CECCR_USER_BASE_PATH + dataset.getUserName() + "/";
						datasetDir += "DATASETS/" + dataset.getName() + "/";
						int numFolds = Integer.parseInt(dataset.getNumExternalFolds());
						for(int i = 0; i < numFolds; i++){
							HashMap<String, String> actIdsAndValues = 
								DatasetFileOperations.getActFileIdsAndValues(datasetDir + dataset.getActFile() + ".fold" + (i+1));
							int numExternalInThisFold = actIdsAndValues.size();
							if(largestFoldSize == 0 || largestFoldSize < numExternalInThisFold){
								largestFoldSize = numExternalInThisFold;
							}
							if(smallestFoldSize == 0 || smallestFoldSize > numExternalInThisFold){
								smallestFoldSize = numExternalInThisFold;
							}
						}
						externalCompoundsCount += smallestFoldSize + " to " + largestFoldSize + " per fold";
					}
					
					else{
						int numCompounds = dataset.getNumCompound();
						float compoundsExternal = Float.parseFloat(dataset.getNumExternalCompounds());
						if(compoundsExternal < 1){
							//dataset.numExternalCompounds is a multiplier
							numCompounds *= compoundsExternal;
						}
						else{
							//dataset.numExternalCompounds is actually the number of compounds
							numCompounds = Integer.parseInt(dataset.getNumExternalCompounds());
						}
						externalCompoundsCount = "" + numCompounds;
					}
				}
				
				//make dataset type more readable
				if(dataset.getDatasetType().equals(Constants.MODELING)){
					datasetTypeDisplay = "Modeling";
				}
				if(dataset.getDatasetType().equals(Constants.MODELINGWITHDESCRIPTORS)){
					datasetTypeDisplay = "Modeling, with uploaded descriptors";
				}
				if(dataset.getDatasetType().equals(Constants.PREDICTION)){
					datasetTypeDisplay = "Prediction";
					dataset.setDatasetType("");
				}
				if(dataset.getDatasetType().equals(Constants.PREDICTIONWITHDESCRIPTORS)){
					datasetTypeDisplay = "Prediction, with uploaded descriptors";
				}
				//make textfield access for paper reference and datasetDescription
				datasetReference = dataset.getPaperReference();
				datasetDescription = dataset.getDescription();
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

	public ArrayList<DescriptorGenerationResult> getDescriptorGenerationResults() {
		return descriptorGenerationResults;
	}
	public void setDescriptorGenerationResults(
			ArrayList<DescriptorGenerationResult> descriptorGenerationResults) {
		this.descriptorGenerationResults = descriptorGenerationResults;
	}

	public ArrayList<Compound> getExternalFold() {
		return externalFold;
	}
	public void setExternalFold(ArrayList<Compound> externalFold) {
		this.externalFold = externalFold;
	}

	public String getExternalCompoundsCount() {
		return externalCompoundsCount;
	}
	public void setExternalCompoundsCount(String externalCompoundsCount) {
		this.externalCompoundsCount = externalCompoundsCount;
	}

	public ArrayList<String> getFoldNums() {
		return foldNums;
	}
	public void setFoldNums(ArrayList<String> foldNums) {
		this.foldNums = foldNums;
	}

	public String getCurrentFoldNumber() {
		return currentFoldNumber;
	}
	public void setCurrentFoldNumber(String currentFoldNumber) {
		this.currentFoldNumber = currentFoldNumber;
	}
	
	public String getDatasetReference() {
		return datasetReference;
	}
	public void setDatasetReference(String datasetReference) {
		this.datasetReference = datasetReference;
	}

	public String getDatasetDescription() {
		return datasetDescription;
	}
	public void setDatasetDescription(String datasetDescription) {
		this.datasetDescription = datasetDescription;
	}

	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}

	public String getDatasetTypeDisplay() {
		return datasetTypeDisplay;
	}
	public void setDatasetTypeDisplay(String datasetTypeDisplay) {
		this.datasetTypeDisplay = datasetTypeDisplay;
	}

}