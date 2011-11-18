package edu.unc.ceccr.action.ViewPredictor;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class SvmModelsPage extends ViewPredictorAction {

	private List<SvmModel> svmModels;
	private SvmParameters svmParameters;
	
	public String load() throws Exception{
		//get models associated with predictor
		String result = getBasicParameters();
		if(!result.equals(SUCCESS)) return result;
		
		getModelsPageParameters();
		
		// not all columns are relevant for all SVM types.  allows us to select only those needed
		session = HibernateUtil.getSession();
		svmParameters = PopulateDataObjects.getSvmParametersById(selectedPredictor.getModelingParametersId(), session);
		session.close();
		if(childPredictors.size() == 0){
			result=loadModels();
		}
		else{
			currentFoldNumber = "" + (Integer.parseInt(currentFoldNumber) + 1);
			for(int i = 0; i < childPredictors.size(); i++){
				foldNums.add("" + (i+1));
				if(currentFoldNumber.equals("" + (i+1))){
					String parentId = objectId;
					objectId = "" + childPredictors.get(i).getId();
					result=loadModels();
					objectId = parentId; 
				}
			}
		}	
		return result;
	}
	
	private String loadModels() {
		String result = SUCCESS;

		try{
			svmModels = new ArrayList<SvmModel>();
			session = HibernateUtil.getSession();
			List<SvmModel> temp = PopulateDataObjects.getSvmModelsByPredictorId(Long.parseLong(objectId), session);
			session.close();
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
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
			errorStrings.add(ex.getMessage());
			return ERROR;
		}
		return result;
	}
	
	private String loadModelSets() {
		String result = SUCCESS;
		for(Predictor childPredictor : childPredictors){
			objectId = "" + childPredictor.getId();
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

	public SvmParameters getSvmParameters() {
		return svmParameters;
	}
	public void setSvmParameters(SvmParameters svmParameters) {
		this.svmParameters = svmParameters;
	}
}