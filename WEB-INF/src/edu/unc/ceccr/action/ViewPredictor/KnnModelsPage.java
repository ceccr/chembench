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
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class KnnModelsPage extends ViewPredictorAction {

	private List<KnnModel> models;
	private List<KnnModel> randomModels;
	
	public String load() throws Exception{
		//get models associated with predictor
		getBasicParameters();
		getModelsPageParameters();
		
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
		return SUCCESS;
	}
	
	//getters and setters
	
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
	//end getters and setters
}