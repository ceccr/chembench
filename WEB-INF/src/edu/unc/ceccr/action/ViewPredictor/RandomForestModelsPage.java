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

public class RandomForestModelsPage extends ViewPredictorAction {

	private List<RandomForestGrove> randomForestGroves;
	private List<RandomForestTree> randomForestTrees;

	private List<List<RandomForestTree>> randomForestTreeSets = new ArrayList<List<RandomForestTree>>(); //for nfold
	private List<List<RandomForestGrove>> randomForestGroveSets =  new ArrayList<List<RandomForestGrove>>(); //for nfold

	public String loadTreesPage() throws Exception{
		String result = SUCCESS;
		getBasicParameters();
		getModelsPageParameters();
		
		if(childPredictors.size() == 0){
			loadTrees();
		}
		else{
			loadTreeSets();
		}
		session.close();
		return result;
	}
	
	public String loadGrovesPage() throws Exception{
		String result = SUCCESS;
		getBasicParameters();
		getModelsPageParameters();
		
		if(childPredictors.size() == 0){
			loadGroves();
		}
		else{
			loadGroveSets();
		}	
		session.close();
		return result;
	}
	
	private String loadGroves() throws Exception {
		String result = SUCCESS;
		
		List<RandomForestGrove> rfGroves = PopulateDataObjects.getRandomForestGrovesByPredictorId(Long.parseLong(predictorId), session);
		randomForestGroves = new ArrayList<RandomForestGrove>();
		
		if(rfGroves != null){
			for(RandomForestGrove rfg : rfGroves){
				if(isYRandomPage.equals(Constants.YES) && rfg.getIsYRandomModel().equals(Constants.YES)){
					randomForestGroves.add(rfg);
				}
				else if(isYRandomPage.equals(Constants.NO) && rfg.getIsYRandomModel().equals(Constants.NO)){
					randomForestGroves.add(rfg);
				}
			}
		}
		randomForestGroveSets.add(randomForestGroves);
		return result;
	}
	
	private String loadTrees() throws Exception {
		String result = SUCCESS;
		List<RandomForestGrove> rfGroves = PopulateDataObjects.getRandomForestGrovesByPredictorId(Long.parseLong(predictorId), session);
		
		randomForestTrees = new ArrayList<RandomForestTree>();
		if(rfGroves != null){
			for(RandomForestGrove rfg : rfGroves){
				ArrayList<RandomForestTree> rfTrees = (ArrayList<RandomForestTree>) PopulateDataObjects.getRandomForestTreesByGroveId(rfg.getId(), session);				
				if(isYRandomPage.equals(Constants.YES) && 
						rfg.getIsYRandomModel().equals(Constants.YES) &&
						rfTrees != null){
					randomForestTrees.addAll(rfTrees);
				}
				else if(isYRandomPage.equals(Constants.NO) && 
						rfg.getIsYRandomModel().equals(Constants.NO) &&
						rfTrees != null){
					randomForestTrees.addAll(rfTrees);
				}
			}
		}
		for(RandomForestTree rfTree: randomForestTrees){
			String splitNumber = rfTree.getTreeFileName();
			if(splitNumber.split("_").length > 2){
				splitNumber = splitNumber.split("_")[3];
			}
			rfTree.setTreeFileName(splitNumber);
		}
		randomForestTreeSets.add(randomForestTrees);
		return result;
	}
	
	private String loadTreeSets() throws Exception{
		String result = SUCCESS;
		String parentPredictorId = predictorId;
		
		for(Predictor childPredictor : childPredictors){
			predictorId = "" + childPredictor.getPredictorId();
			result = loadTrees();
			if(!result.equals(SUCCESS)){
				return result;
			}
		}
		predictorId = parentPredictorId;
		return result;
	}
	
	private String loadGroveSets() throws Exception{
		String result = SUCCESS;
		String parentPredictorId = predictorId;
		
		for(Predictor childPredictor : childPredictors){
			predictorId = "" + childPredictor.getPredictorId();
			result = loadGroves();
			if(!result.equals(SUCCESS)){
				return result;
			}
		}
		predictorId = parentPredictorId;
		return result;
	}
	
	public List<List<RandomForestTree>> getRandomForestTreeSets() {
		return randomForestTreeSets;
	}
	public void setRandomForestTreeSets(
			List<List<RandomForestTree>> randomForestTreeSets) {
		this.randomForestTreeSets = randomForestTreeSets;
	}

	public List<List<RandomForestGrove>> getRandomForestGroveSets() {
		return randomForestGroveSets;
	}
	public void setRandomForestGroveSets(
			List<List<RandomForestGrove>> randomForestGroveSets) {
		this.randomForestGroveSets = randomForestGroveSets;
	}
	
	
	
}