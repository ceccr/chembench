package edu.unc.ceccr.action;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;
//struts2

public class AdminAction extends ActionSupport{

	User user;
	String buildDate;
	ArrayList<User> users;
	
	//for sending email to all users
	String emailMessage;
	String emailSubject;
	String sendTo;
	private ArrayList<String> errorStrings = new ArrayList<String>();
	
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
	 * method responsible for deletion of the public predictor
	 * @return
	 */
	public String deletePublicPredictor(){
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
			String predictorID = ((String[]) context.getParameters().get("predictorName"))[0];
			String userName = ((String[]) context.getParameters().get("userName"))[0];
			
			if(predictorID.isEmpty() || userName.isEmpty()){
				errorStrings.add("Predictor ID and user name shouldn't be empty!");
				return ERROR;
			}
			
			if(!userName.trim().equals(Constants.ALL_USERS_USERNAME)){
				errorStrings.add("You can only delete public predictors here!");
				return ERROR;
			}
			Session session = HibernateUtil.getSession();
			Predictor predictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorID), session);
			if(predictor==null){
				errorStrings.add("No predictor with ID "+predictorID+" was found in the database!");
				return ERROR;
			}
			
			new DeleteAction().deletePredictor(predictor, session);
			session.close();
			
		}
		catch(Exception ex){
			errorStrings.add(ex.getMessage());
			return ERROR;
		}
		if(!errorStrings.isEmpty()) return ERROR;
		return SUCCESS;
	}
	
	/**
	 * method responsible for deletion of the public prediction
	 * @return
	 */
	public String deletePublicPrediction(){
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
			String predictionID = ((String[]) context.getParameters().get("predictionName"))[0];
			String userName = ((String[]) context.getParameters().get("userName"))[0];
			
			if(predictionID.isEmpty() || userName.isEmpty()){
				errorStrings.add("Prediction ID and user name shouldn't be empty!");
				return ERROR;
			}
			
			if(!userName.trim().equals(Constants.ALL_USERS_USERNAME)){
				errorStrings.add("You can only delete public prediction here!");
				return ERROR;
			}
			Session session = HibernateUtil.getSession();
			Prediction prediction = PopulateDataObjects.getPredictionById(Long.parseLong(predictionID), session);
			if(prediction==null){
				errorStrings.add("No prediction with ID "+predictionID+" was found in the database!");
				return ERROR;
			}
			
			//delete the files associated with this prediction
			String dir = Constants.CECCR_USER_BASE_PATH+Constants.ALL_USERS_USERNAME+"/PREDICTIONS/"+prediction.getName();
			if(! FileAndDirOperations.deleteDir(new File(dir))){
				errorStrings.add("Error deleting dir");
				Utility.writeToStrutsDebug("error deleting dir: " + dir);
			}
			
			//delete the prediction values associated with the prediction
			ArrayList<PredictionValue> pvs = (ArrayList<PredictionValue>) PopulateDataObjects.getPredictionValuesByPredictionId(prediction.getId(), session);
			
			if(pvs != null){
				for(PredictionValue pv : pvs){
					Transaction tx = null;
					try{
						tx = session.beginTransaction();
					    session.delete(pv);
						tx.commit();
					}
					catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);
					}
				}
			}
			
			//delete the database entry for the prediction
			Transaction tx = null;
			try{
				tx = session.beginTransaction();
			    session.delete(prediction);
				tx.commit();
			}catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			}
			
			session.close();
			
		}
		catch(Exception ex){
			errorStrings.add(ex.getMessage());
			return ERROR;
		}
		if(!errorStrings.isEmpty()) return ERROR;
		return SUCCESS;
	}
	
	/**
	 * method responsible for deletion of the public dataset
	 * @return
	 */
	public String deletePublicDataset(){
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
			String datasetID = ((String[]) context.getParameters().get("datasetName"))[0];
			String userName = ((String[]) context.getParameters().get("userName"))[0];
			
			if(datasetID.isEmpty() || userName.isEmpty()){
				errorStrings.add("Dataset ID and user name shouldn't be empty!");
				return ERROR;
			}
			
			if(!userName.trim().equals(Constants.ALL_USERS_USERNAME)){
				errorStrings.add("You can only delete public datasets here!");
				return ERROR;
			}
			Session session = HibernateUtil.getSession();
			DataSet dataset = PopulateDataObjects.getDataSetById(Long.parseLong(datasetID), session);
			if(dataset==null){
				errorStrings.add("No dataset with ID "+datasetID+" was found in the database!");
				return ERROR;
			}	
			
			//make sure nothing else depends on this dataset existing
			List<String> depend = checkPublicPredictorDependencies(dataset);
			if(!depend.isEmpty()){
				errorStrings.addAll(depend);
				return ERROR;
			}

			//delete the files associated with this dataset
			String dir = Constants.CECCR_USER_BASE_PATH+Constants.ALL_USERS_USERNAME+"/DATASETS/"+dataset.getName();
			
			if((new File(dir)).exists()){
				if(! FileAndDirOperations.deleteDir(new File(dir))){
					Utility.writeToStrutsDebug("error deleting dir: " + dir);
					errorStrings.add("Cannot delete directory!");
				}
			}
			
			//delete the database entry for the dataset
			Transaction tx = null;
			try{
				tx = session.beginTransaction();
			    session.delete(dataset);
				tx.commit();
			}catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
				return ERROR;
			}
			session.close();
			
		}
		catch(Exception ex){
			errorStrings.add(ex.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
	
	
	private ArrayList<String> checkPublicPredictorDependencies(DataSet ds) throws HibernateException, ClassNotFoundException, SQLException{
		Utility.writeToDebug("checking dataset dependencies");
	
		ArrayList<String> dependencies = new ArrayList<String>();
		Session session = HibernateUtil.getSession();
		ArrayList<Predictor> userPredictors = (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(Constants.ALL_USERS_USERNAME, true, false, session);
		ArrayList<Prediction> userPredictions = (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(Constants.ALL_USERS_USERNAME, false, session);
	
		//check each predictor
		for(int i = 0; i < userPredictors.size();i++){
			Utility.writeToDebug("predictor id: " + userPredictors.get(i).getDatasetId() + " dataset id: " + ds.getId());
			if(userPredictors.get(i).getDatasetId() != null && userPredictors.get(i).getDatasetId().equals(ds.getId())){
				dependencies.add("The predictor '" + userPredictors.get(i).getName() + "' depends on this dataset. Please delete it first.\n");
			}
		}
	
		//check each prediction
		for(int i = 0; i < userPredictions.size();i++){
			Utility.writeToDebug("Prediction id: " + userPredictions.get(i).getDatasetId() + " dataset id: " + ds.getId());
			if(userPredictions.get(i).getDatasetId() != null && userPredictions.get(i).getDatasetId().equals(ds.getId())){
				dependencies.add("The prediction '" + userPredictions.get(i).getName() + "' depends on this dataset. Please delete it first.\n");
			}
		}
		return dependencies;
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
			
			if(predictorName.isEmpty() || userName.isEmpty() || predictorType.isEmpty()){
				
				return ERROR;
			}
			
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
			if(dataset.getUserName().trim().equals(Constants.ALL_USERS_USERNAME)){
				Utility.writeToDebug("**************DATASET IS ALREADY PUBLIC!");
				isDatasetPublic = true;
			}
			
			//check if any other dataset with the same name is already public
			session = HibernateUtil.getSession();
			DataSet checkPublicDataset = PopulateDataObjects.getDataSetByName(dataset.getName(), Constants.ALL_USERS_USERNAME, session);
			session.close();
			if(checkPublicDataset!=null){
				isDatasetPublic = true;
				dataset=checkPublicDataset;
			}
			
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
				Utility.writeToDebug("--------Child predictor IDs="+predictor.getChildIds());
				newChildIds = new String();
				predictorChildren = predictor.getChildIds().split("\\s+");
				for(String id:predictorChildren){
					Utility.writeToDebug("--------Child predictor ID="+id+" longId="+Long.parseLong(id));
					session = HibernateUtil.getSession();
					Predictor child = PopulateDataObjects.getPredictorById(Long.parseLong(id), session);
					if(child!=null){
						session.evict(child);
						child.setId(null);
						child.setUserName(Constants.ALL_USERS_USERNAME);
						child.setPredictorType("Hidden");
						child.setDatasetId(dataset.getId());
						child.setParentId(newPredictorId);
						session.save(child);
						session.flush();
						Long newId = child.getId();
						newChildIds+=newId.toString()+" ";
						session.close();
					
						//taking care of external validation table 
						session = HibernateUtil.getSession();
						extValidation = PopulateDataObjects.getExternalValidationValues(Long.parseLong(id), session);
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
							
							session.close();	
							//taking care of RandomForest table 
							Utility.writeToDebug("------//taking care of RandomForest table");
							session = HibernateUtil.getSession();
							randomForests = PopulateDataObjects.getRandomForestGrovesByPredictorId(Long.parseLong(id), session);
							session.close();
							for(RandomForestGrove randomForest:randomForests){
								session = HibernateUtil.getSession();
								Long oldId = randomForest.getId();
								session.evict(randomForest);
								randomForest.setId(null);
								randomForest.setPredictorId(newId);
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
							session = HibernateUtil.getSession();
							Transaction tx = null;
							try {
								tx = session.beginTransaction();
								session.saveOrUpdate(child);
								tx.commit();
							} catch (RuntimeException e) {
								if (tx != null)
									tx.rollback();
								Utility.writeToDebug(e);
							} finally {session.close();}
					}
				}
				
			}
			
			//updating newly created predictor with new child ids
			predictor.setChildIds(newChildIds);
			Utility.writeToDebug("--------New child predictor IDs="+newChildIds);
			session = HibernateUtil.getSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				session.saveOrUpdate(predictor);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			} finally {session.close();}
			
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
	public ArrayList<String> getErrorStrings() {
		return errorStrings;
	}

	public void setErrorStrings(ArrayList<String> errorStrings) {
		this.errorStrings = errorStrings;
	}

}