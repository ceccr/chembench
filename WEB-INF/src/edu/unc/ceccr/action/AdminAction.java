package edu.unc.ceccr.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;

public class AdminAction extends ActionSupport{

	User user;
	String buildDate;
	ArrayList<User> users;
	
	//for sending email to all users
	String emailMessage;
	String emailSubject;
	String sendTo;
	
	public String loadPage() throws Exception {

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
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}

		//log the results
		Utility.writeToStrutsDebug("Forwarding user " + user.getUserName() + " to admin page.");
		
		//set up any values that need to be populated onto the page (dropdowns, lists, display stuff)

		// open database connection
		Session session = HibernateUtil.getSession();
	
		// Latest Build Date
		buildDate = Constants.BUILD_DATE;
	
		// list of users
		users = PopulateDataObjects.getAllUsers(session);	
		session.close();

		//go to the page
		return result;
	}
	
	public String loadEmailAllUsersPage() throws Exception {
		sendTo = "JUSTME";
		return SUCCESS;
	}
	
	public String emailSelectedUsers() throws Exception {
		//check that the user is logged in
		Utility.writeToDebug("emailing SELECTED user(s)");
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				return LOGIN;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				Utility.writeToDebug("user " + user.getUserName() + " isn't an admin");
				return ERROR;
			}
		}
		if(!sendTo.trim().isEmpty() && !emailMessage.trim().isEmpty() && !emailSubject.trim().isEmpty()){
			List<String> emails = Arrays.asList(sendTo.split(";"));
			Iterator<String> it=emails.iterator();
			while(it.hasNext()){
				String email = it.next();
				if(!email.trim().isEmpty()) SendEmails.sendEmail(email, "", "", emailSubject, emailMessage);
			}
		}
		return SUCCESS;
	}
	
	public String emailAllUsers() throws Exception {
		//check that the user is logged in
		Utility.writeToDebug("emailing user(s)");
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("No ActionContext available");
		}
		else{
			user = (User) context.getSession().get("user");
			
			if(user == null){
				Utility.writeToStrutsDebug("No user is logged in.");
				return LOGIN;
			}
			else if(! user.getIsAdmin().equals(Constants.YES)){
				Utility.writeToDebug("user " + user.getUserName() + " isn't an admin");
				return ERROR;
			}
		}
		
		Session s = HibernateUtil.getSession();
		List<User> userList= PopulateDataObjects.getAllUsers(s);
		s.close();
			
		if(sendTo.equals("ALLUSERS") && ! emailMessage.trim().isEmpty() && ! emailSubject.trim().isEmpty()){
			Iterator<User> it=userList.iterator();
			while(it.hasNext()){
				User userInfo = it.next();
				SendEmails.sendEmail(userInfo.getEmail(), "", "", emailSubject, emailMessage);
			}
		}
		else if(sendTo.equals("JUSTME") && ! emailMessage.trim().isEmpty() && ! emailSubject.trim().isEmpty()){
			SendEmails.sendEmail(user.getEmail(), "", "", emailSubject, emailMessage);
		}
		return SUCCESS;
	}
	
	public String changeUserAdminStatus() throws Exception{
		//get the current user and the username of the user to be altered
		String result = SUCCESS;
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
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}
		String userToChange = ((String[]) context.getParameters().get("userToChange"))[0];

		Session s = HibernateUtil.getSession();
		User toChange = null;
		if(userToChange.equals(user.getUserName())){
			toChange = user;
		}
		else{
			toChange = PopulateDataObjects.getUserByUserName(userToChange, s);
		}
		
		if(toChange.getIsAdmin().equals(Constants.YES)){
			toChange.setIsAdmin(Constants.NO);
		}
		else{
			toChange.setIsAdmin(Constants.YES);
		}
		
		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(toChange);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		
		return result;
	}
	
	public String changeUserDescriptorDownloadStatus() throws Exception{
		//get the current user and the username of the user to be altered
		String result = SUCCESS;
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
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}

		String userToChange = ((String[]) context.getParameters().get("userToChange"))[0];

		Session s = HibernateUtil.getSession();
		User toChange = null;
		if(userToChange.equals(user.getUserName())){
			toChange = user;
		}
		else{
			toChange = PopulateDataObjects.getUserByUserName(userToChange, s);
		}
		
		if(toChange.getCanDownloadDescriptors().equals(Constants.YES)){
			toChange.setCanDownloadDescriptors(Constants.NO);
		}
		else{
			toChange.setCanDownloadDescriptors(Constants.YES);
		}

		Transaction tx = null;
		try {
			tx = s.beginTransaction();
			s.saveOrUpdate(toChange);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {s.close();}
		
		return result;
	}
	
	/**
	 * Method responsible for converting predictor from private to public use
	 * @return
	 */
	public String makePredictorPublic(){
		String result = SUCCESS;
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
			else if(! user.getIsAdmin().equals(Constants.YES)){
				result = ERROR;
				return result;
			}
		}

		try{
			String predictorName = ((String[]) context.getParameters().get("predictorName"))[0];
			String userName = ((String[]) context.getParameters().get("userName"))[0];
			String predictorType = ((String[]) context.getParameters().get("predictorType"))[0];
			
			if(predictorName.isEmpty() || userName.isEmpty() || predictorType.isEmpty()) return SUCCESS;
			
			Utility.writeToDebug("++++++++++++++++++Predictor name:"+predictorName+" User name="+userName);
					
			Session session = HibernateUtil.getSession();
			Predictor predictor = PopulateDataObjects.getPredictorByName(predictorName, userName, session);
			if(predictor==null) return ERROR;
			
			// idiot proof if someone will try to make public predictor public again.  
			if(predictor.getUserName().equals(Constants.ALL_USERS_USERNAME)) return SUCCESS;
			
			//prevent duplication of names 
			if(PopulateDataObjects.getPredictorByName(predictorName, Constants.ALL_USERS_USERNAME, session)!=null) return SUCCESS;
			
			DataSet dataset = PopulateDataObjects.getDataSetById(predictor.getDatasetId(),session);
			if(dataset==null) return ERROR;
			session.close();
			
			//check if predictor is based on the public dataset
			boolean isDatasetPublic = false;
			if(dataset.getUserName().equals(Constants.ALL_USERS_USERNAME)) isDatasetPublic = true;
			
			String allUserDatasetDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/DATASETS/"+dataset.getName();
			String allUserPredictorDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/PREDICTORS/"+predictor.getName();
			
			String userDatasetDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/"+dataset.getName();
			String userPredictorDir = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/"+predictor.getName();
			
			//copy files to all users folder
			Utility.writeToDebug("Start copying files from '"+userDatasetDir+"' to '"+allUserDatasetDir+"'");
			if(!isDatasetPublic){
				String cmd = "cp -r " + userDatasetDir+" "+allUserDatasetDir;
				RunExternalProgram.runCommand(cmd, "");
			}
			Utility.writeToDebug("Start copying files from '"+userPredictorDir+"' to '"+allUserPredictorDir+"'");
			String cmd = "cp -r " + userPredictorDir+" "+allUserPredictorDir;
			RunExternalProgram.runCommand(cmd, "");
			
			//starting database records cloning process
			
			if(!isDatasetPublic){
				//duplicating dataset record
				Utility.writeToDebug("------DB: Duplicating dataset record for dataset: "+dataset.getName());
				session = HibernateUtil.getSession();
				session.evict(dataset);
				dataset.setId(null);
				dataset.setUserName(Constants.ALL_USERS_USERNAME);
				session.save(dataset);
				session.flush();
				session.close();
			}
			
			
			Long predictorId = predictor.getId();
			Long newPredictorId = null;
			//duplicating predictor record
			Utility.writeToDebug("------DB: Duplicating predictor record for predictor: "+predictor.getName());
			session = HibernateUtil.getSession();
			session.evict(predictor);
			predictor.setId(null);
			predictor.setUserName(Constants.ALL_USERS_USERNAME);
			predictor.setPredictorType(predictorType);
			predictor.setDatasetId(dataset.getId());
			session.save(predictor);
			session.flush();
			newPredictorId = predictor.getId();
			session.close();
			
			//taking care of external validation table 
			Utility.writeToDebug("------//taking care of external validation table");
			session = HibernateUtil.getSession();
			List<ExternalValidation> extValidation = PopulateDataObjects.getExternalValidationValues(predictorId, session);
			session.close();
			for(ExternalValidation exVal:extValidation){
				session = HibernateUtil.getSession();
				session.evict(exVal);
				exVal.setExternalValId(-1);
				exVal.setPredictorId(newPredictorId);
				session.save(exVal);
				session.close();
			}
			
			/*
			//taking care of knnModel table
			Utility.writeToDebug("------//taking care of knnModel table");
			session = HibernateUtil.getSession();
			List<KnnModel> knnModels = PopulateDataObjects.getModelsByPredictorId(predictorId, session);
			session.close();
			for(KnnModel knnModel:knnModels){
				session = HibernateUtil.getSession();
				session.evict(knnModel);
				knnModel.setId(null);
				knnModel.setPredictorId(newPredictorId);
				session.save(knnModel);
				session.close();
			}
			*/
			
			//taking care of knnPlusModel table 
			Utility.writeToDebug("------//taking care of knnPlusModel table");
			session = HibernateUtil.getSession();
			List<KnnPlusModel> knnPlusModels = PopulateDataObjects.getKnnPlusModelsByPredictorId(predictorId, session);
			session.close();
			for(KnnPlusModel knnPlusModel:knnPlusModels){
				session = HibernateUtil.getSession();
				session.evict(knnPlusModel);
				knnPlusModel.setId(null);
				knnPlusModel.setPredictorId(newPredictorId);
				session.save(knnPlusModel);
				session.close();
			}
			
			//taking care of SVM table 
			Utility.writeToDebug("------//taking care of SVM table");
			session = HibernateUtil.getSession();
			List<SvmModel> svmModels = PopulateDataObjects.getSvmModelsByPredictorId(predictorId, session);
			session.close();
			for(SvmModel svmModel:svmModels){
				session = HibernateUtil.getSession();
				session.evict(svmModel);
				svmModel.setId(null);
				svmModel.setPredictorId(newPredictorId);
				session.save(svmModel);
				session.close();
			}
			
			//taking care of RandomForest table 
			Utility.writeToDebug("------//taking care of RandomForest table");
			session = HibernateUtil.getSession();
			List<RandomForestGrove> randomForests = PopulateDataObjects.getRandomForestGrovesByPredictorId(predictorId, session);
			session.close();
			for(RandomForestGrove randomForest:randomForests){
				session = HibernateUtil.getSession();
				Long oldId = randomForest.getId();
				session.evict(randomForest);
				randomForest.setId(null);
				randomForest.setPredictorId(newPredictorId);
				session.save(randomForest);
				session.flush();
				List<RandomForestTree> trees = PopulateDataObjects.getRandomForestTreesByGroveId(oldId,session);
				for(RandomForestTree tree:trees){
					session.evict(tree);
					tree.setId(null);
					tree.setRandomForestGroveId(randomForest.getId());
					session.save(tree);
				}
				session.close();
			}
			
			//taking care of modeling parameters
			Utility.writeToDebug("------//taking care of modeling parameters");
			session = HibernateUtil.getSession();
			Predictor oldPredictor = PopulateDataObjects.getPredictorById(predictorId, session);
			if(oldPredictor.getModelMethod().equals(Constants.RANDOMFOREST)){
				Utility.writeToDebug("------//RANDOMFOREST");
				RandomForestParameters randomForestParameters = PopulateDataObjects.getRandomForestParametersById(oldPredictor.getModelingParametersId(), session);
				session.evict(randomForestParameters);
				randomForestParameters.setId(null);
				session.save(randomForestParameters);
				session.flush();
				predictor.setModelingParametersId(randomForestParameters.getId());
			}
			else if(oldPredictor.getModelMethod().equals(Constants.KNNGA) || 
					oldPredictor.getModelMethod().equals(Constants.KNNSA)){
				Utility.writeToDebug("------//KNN+");
				KnnPlusParameters knnPlusParameters = PopulateDataObjects.getKnnPlusParametersById(oldPredictor.getModelingParametersId(), session);
				session.evict(knnPlusParameters);
				knnPlusParameters.setId(null);
				session.save(knnPlusParameters);
				session.flush();
				predictor.setModelingParametersId(knnPlusParameters.getId());
			}
			/*else if(oldPredictor.getModelMethod().equals(Constants.KNN)){
				Utility.writeToDebug("------//KNN");
				KnnParameters params = PopulateDataObjects.getKnnParametersById(oldPredictor.getModelingParametersId(),session);
				session.evict(params);
				params.setId(null);
				session.save(params);
				session.flush();
				predictor.setModelingParametersId(params.getId());
			}*/
			else if(oldPredictor.getModelMethod().equals(Constants.SVM)){
				Utility.writeToDebug("------//SVM");
				SvmParameters svmParameters = PopulateDataObjects.getSvmParametersById(oldPredictor.getModelingParametersId(), session);
				session.evict(svmParameters);
				svmParameters.setId(null);
				session.save(svmParameters);
				session.flush();
				predictor.setModelingParametersId(svmParameters.getId());
			}
			
			Utility.writeToDebug("--------Old predictor ID="+predictorId+" -> new one = "+newPredictorId);
			
			//duplicating child predictors
			String[] predictorChildren = null;
			String newChildIds =null;
			if(predictor.getChildIds()!=null){
				newChildIds = new String();
				predictorChildren = predictor.getChildIds().split(" ");
				for(String id:predictorChildren){
					Long newId = null;
					session = HibernateUtil.getSession();
					Predictor child = PopulateDataObjects.getPredictorById(Long.parseLong(id), session);
					session.evict(child);
					child.setId(null);
					child.setUserName(Constants.ALL_USERS_USERNAME);
					child.setPredictorType(predictorType);
					child.setParentId(newPredictorId);
					session.save(child);
					session.flush();
					newId = child.getId();
					newChildIds+=newId.toString();
					session.close();
					
					//taking care of external validation table 
					session = HibernateUtil.getSession();
					extValidation = PopulateDataObjects.getExternalValidationValues(newId, session);
					session.close();
					for(ExternalValidation exVal:extValidation){
						session = HibernateUtil.getSession();
						session.evict(exVal);
						exVal.setExternalValId(-1);
						exVal.setPredictorId(newId);
						session.save(exVal);
						session.close();
					}
					
					session = HibernateUtil.getSession();
						if(child.getModelMethod().equals(Constants.RANDOMFOREST)){
							RandomForestParameters randomForestParameters = PopulateDataObjects.getRandomForestParametersById(child.getModelingParametersId(), session);
							session.evict(randomForestParameters);
							randomForestParameters.setId(null);
							session.save(randomForestParameters);
							session.flush();
							child.setModelingParametersId(randomForestParameters.getId());
						}
						else if(child.getModelMethod().equals(Constants.KNNGA) || 
								child.getModelMethod().equals(Constants.KNNSA)){
							KnnPlusParameters knnPlusParameters = PopulateDataObjects.getKnnPlusParametersById(child.getModelingParametersId(), session);
							session.evict(knnPlusParameters);
							knnPlusParameters.setId(null);
							session.save(knnPlusParameters);
							session.flush();
							child.setModelingParametersId(knnPlusParameters.getId());
						}
						/*else if(child.getModelMethod().equals(Constants.KNN)){
							KnnParameters params = PopulateDataObjects.getKnnParametersById(child.getModelingParametersId(),session);
							session.evict(params);
							params.setId(null);
							session.save(params);
							session.flush();
							child.setModelingParametersId(params.getId());
						}*/
						else if(child.getModelMethod().equals(Constants.SVM)){
							SvmParameters svmParameters = PopulateDataObjects.getSvmParametersById(child.getModelingParametersId(), session);
							session.evict(svmParameters);
							svmParameters.setId(null);
							session.save(svmParameters);
							session.flush();
							child.setModelingParametersId(svmParameters.getId());
						}
				}
				
			}
			
			//updating newly created predictor with new child ids
			session = HibernateUtil.getSession();
			Predictor p = PopulateDataObjects.getPredictorById(newPredictorId, session);
			p.setChildIds(newChildIds);
			session.update(p);
			session.close();
			
		}
		catch(Exception ex){
			result = ERROR;
			Utility.writeToDebug(ex);
		}
		
		return result;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	public String getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getSendTo() {
		return sendTo;
	}
	
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

}