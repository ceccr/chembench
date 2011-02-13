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

public class PredictorPage extends ViewPredictorAction {

	private String predictorDescription = "";
	private String predictorReference = "";
	private String editable = "";
	
	public String load() throws Exception {
		if(context == null){
			Utility.writeToDebug("Context in PredictorPage is null");
		}
		else{
			Utility.writeToDebug("Context in PredictorPage is not null");
		}
		if(context.getParameters().get("editable") != null){
			if(user.getIsAdmin().equals(Constants.YES)|| user.getUserName().equals(dataset.getUserName())){
				editable = "YES";
			}
		}
		else{
			editable = "NO";
		}
		predictorDescription = selectedPredictor.getDescription();
		predictorReference = selectedPredictor.getPaperReference();
		
		//the predictor has now been viewed. Update DB accordingly.
		if(! selectedPredictor.getHasBeenViewed().equals(Constants.YES)){
			selectedPredictor.setHasBeenViewed(Constants.YES);
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				session.saveOrUpdate(selectedPredictor);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx != null)
					tx.rollback();
				Utility.writeToDebug(e);
			}
		}
		
		//go to the page
		return SUCCESS;
	}

	public String updatePredictor() throws Exception {
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();

		if(context != null){
			//get predictorId id
			predictorId = ((String[]) context.getParameters().get("predictorId"))[0];
			String[] predictorIdAsStringArray = new String[1];
			predictorIdAsStringArray[0] = predictorId;
			context.getParameters().put("id", predictorIdAsStringArray);
			predictorDescription = ((String[]) context.getParameters().get("predictorDescription"))[0];
			predictorReference = ((String[]) context.getParameters().get("predictorReference"))[0];
			
			selectedPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);
			selectedPredictor.setDescription(predictorDescription);
			selectedPredictor.setPaperReference(predictorReference);
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				session.saveOrUpdate(selectedPredictor);
				tx.commit();
			}
			catch (Exception ex) {
				Utility.writeToDebug(ex); 
			} 
		}
		return load();
	}

	
	//getters and setters
	
	public String getPredictorDescription() {
		return predictorDescription;
	}
	public void setPredictorDescription(String predictorDescription) {
		this.predictorDescription = predictorDescription;
	}

	public String getPredictorReference() {
		return predictorReference;
	}
	public void setPredictorReference(String predictorReference) {
		this.predictorReference = predictorReference;
	}

	public String getEditable() {
		return editable;
	}
	public void setEditable(String editable) {
		this.editable = editable;
	}
	
	//end getters and setters
	
}