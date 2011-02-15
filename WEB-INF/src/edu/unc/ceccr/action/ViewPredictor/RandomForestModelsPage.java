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
	
	public String loadGroves() throws Exception {
		getBasicParameters();
		getModelsPageParameters();
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
		return result;
	}
	

	public String loadTrees() throws Exception {
		getBasicParameters();
		getModelsPageParameters();
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
			splitNumber = splitNumber.split("_")[3];
			rfTree.setTreeFileName(splitNumber);
		}
		session.close();
		return result;
	}

	public List<RandomForestGrove> getRandomForestGroves() {
		return randomForestGroves;
	}
	public void setRandomForestGroves(List<RandomForestGrove> randomForestGroves) {
		this.randomForestGroves = randomForestGroves;
	}

	public List<RandomForestTree> getRandomForestTrees() {
		return randomForestTrees;
	}
	public void setRandomForestTrees(List<RandomForestTree> randomForestTrees) {
		this.randomForestTrees = randomForestTrees;
	}
	
}