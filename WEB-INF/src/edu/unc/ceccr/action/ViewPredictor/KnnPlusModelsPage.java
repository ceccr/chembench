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

public class KnnPlusModelsPage extends ViewPredictorAction {

	private List<KnnPlusModel> knnPlusModels;
	
	public String loadPage() throws Exception {
		
		Utility.writeToDebug("begin loading knn+ models page");
		getBasicParameters();
		getModelsPageParameters();
		String result = SUCCESS;

		if(childPredictors.size() == 0){
			loadModels();
		}
		else{
			currentFoldNumber = "" + (Integer.parseInt(currentFoldNumber) + 1);
			for(int i = 0; i < childPredictors.size(); i++){
				foldNums.add("" + (i));
				if(currentFoldNumber.equals("" + (i))){
					String parentId = predictorId;
					predictorId = "" + childPredictors.get(i).getId();
					loadModels();
					predictorId = parentId; 
				}
			}
		}
		
		//get descriptor freqs from models
		HashMap<String, Integer> descriptorFreqMap = new HashMap<String, Integer>();
		if(knnPlusModels != null){
			for(KnnPlusModel m : knnPlusModels){
				if(m.getDimsNames() != null && ! m.getDimsNames().equals("")){
					String[] descriptorArray = m.getDimsNames().split("\\s+");
					for(int i = 0; i < descriptorArray.length; i++){
						if(descriptorFreqMap.get(descriptorArray[i]) == null){
							descriptorFreqMap.put(descriptorArray[i], 1);
						}
						else{
							//increment
							descriptorFreqMap.put(descriptorArray[i], descriptorFreqMap.get(descriptorArray[i]) + 1);
						}
					}
				}
			}
		}
		
		Utility.writeToDebug("getting descriptor frequencies");
		
		ArrayList<descriptorFrequency> descriptorFrequencies = new ArrayList<descriptorFrequency>();
		ArrayList<String> mapKeys = new ArrayList(descriptorFreqMap.keySet());
		for(String k: mapKeys){
			descriptorFrequency df = new descriptorFrequency();
			df.setDescriptor(k);
			df.setNumOccs(descriptorFreqMap.get(k));
			descriptorFrequencies.add(df);
		}

		Utility.writeToDebug("getting top 5 descriptors");
		
		Collections.sort(descriptorFrequencies, new Comparator<descriptorFrequency>() {
		    public int compare(descriptorFrequency df1, descriptorFrequency df2) {
		    	return (df1.getNumOccs() > df2.getNumOccs()? -1 : 1);
		    }});
		if(descriptorFrequencies.size() >= 5){
			//if there weren't at least 5 descriptors, don't even bother - no summary needed
			mostFrequentDescriptors = "The 5 most frequent descriptors used in your models were: ";
			for(int i = 0; i < 5; i++){
				mostFrequentDescriptors += descriptorFrequencies.get(i).getDescriptor() + " (" + 
					descriptorFrequencies.get(i).getNumOccs() + " models)";
				if(i < 4){
					mostFrequentDescriptors += ", ";
				}
			}
			mostFrequentDescriptors += ".";
		}
		

		Utility.writeToDebug("end loading knn+ models page");
		return result;
	}
	

	private String loadModels() {
		String result = SUCCESS;
		try{
			knnPlusModels = new ArrayList<KnnPlusModel>();
			List<KnnPlusModel> temp = PopulateDataObjects.getKnnPlusModelsByPredictorId(Long.parseLong(predictorId), session);
			
			if(temp != null){
				Iterator<KnnPlusModel> it = temp.iterator();
				while(it.hasNext()){
					KnnPlusModel m = it.next();
					if(m.getIsYRandomModel().equals(Constants.NO) && isYRandomPage.equals(Constants.NO)){
						knnPlusModels.add(m);
					}
					else if(m.getIsYRandomModel().equals(Constants.YES) && isYRandomPage.equals(Constants.YES)){
						knnPlusModels.add(m);
					}
				}
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
			return ERROR;
		}
		return result;
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
	
	public List<KnnPlusModel> getKnnPlusModels() {
		return knnPlusModels;
	}
	public void setKnnPlusModels(List<KnnPlusModel> knnPlusModels) {
		this.knnPlusModels = knnPlusModels;
	}

}