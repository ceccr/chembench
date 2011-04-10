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

public class SvmModelsPage extends ViewPredictorAction {

	private List<SvmModel> svmModels;
	private List<List<SvmModel>> svmModelSets = new ArrayList<List<SvmModel>>();

	
	public String loadPage() throws Exception{
		//get models associated with predictor
		getBasicParameters();
		getModelsPageParameters();
		
		if(childPredictors.size() == 0){
			loadModels();
		}
		else{
			for(int i = 0; i < childPredictors.size(); i++){
				foldNums.add("" + (i+1));
				if(currentFoldNumber.equals("" + (i+1))){
					loadCurrentFoldModels();
				}
			}
		}	

		return SUCCESS;
	}
	
	private String loadModels() {
		String result = SUCCESS;

		try{
			svmModels = new ArrayList<SvmModel>();
			List<SvmModel> temp = PopulateDataObjects.getSvmModelsByPredictorId(Long.parseLong(predictorId), session);
			
			if(temp != null){
				Iterator<SvmModel> it = temp.iterator();
				while(it.hasNext()){
					SvmModel m = it.next();
					if(m.getIsYRandomModel().equals(Constants.NO) && isYRandomPage.equals(Constants.NO)){
						svmModels.add(m);
					}
					else if(m.getIsYRandomModel().equals(Constants.YES) && isYRandomPage.equals(Constants.YES)){
						svmModels.add(m);
					}
				}
				if(svmModels.size() > 0){ 
					//potential bug: what if Fold 3 is size 0? 
					//it will mistakenly print Fold 1, 2, 3, and 4 and have fold 5 empty.
					svmModelSets.add(svmModels);
				}
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
			return ERROR;
		}
		return result;
	}

	private String loadCurrentFoldModels(){
		return "";
	}
	
	private String loadModelSets() {
		String result = SUCCESS;
		for(Predictor childPredictor : childPredictors){
			predictorId = "" + childPredictor.getId();
			result = loadModels();
			if(!result.equals(SUCCESS)){
				return result;
			}
		}
		return result;
	}

	public List<SvmModel> getSvmModels() {
		return svmModels;
	}
	public void setSvmModels(List<SvmModel> svmModels) {
		this.svmModels = svmModels;
	}
	
	public List<List<SvmModel>> getSvmModelSets() {
		return svmModelSets;
	}
	public void setSvmModelSets(List<List<SvmModel>> svmModelSets) {
		this.svmModelSets = svmModelSets;
	}
}