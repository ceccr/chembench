package edu.unc.ceccr.workflows;

import java.io.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.CompoundPredictions;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;

public class WriteDownloadableFilesWorkflow{
	//In most cases, the files generated during the running of a job are
	//of no use to humans. These functions generate downloadable results
	//files that give job results in a more readable form.
	
	public static void writeExternalPredictionsAsCSV(Long predictorId) throws Exception{
		Session s = HibernateUtil.getSession();
		Predictor predictor = PopulateDataObjects.getPredictorById(predictorId, s);
		ArrayList<ExternalValidation> externalValidationValues;
		
		String outfileName = Constants.CECCR_USER_BASE_PATH + predictor.getUserName() + "/PREDICTORS/" + 
			predictor.getName() + "/" + predictor.getName() + "-external-set-predictions.csv";
		ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(predictor, s);
		if(childPredictors.isEmpty()){
			//
			externalValidationValues = (ArrayList<ExternalValidation>) PopulateDataObjects.getExternalValidationValues(predictor, s);	
		}
		else{
			
		}
	}
	
	public static void writePredictionValuesAsCSV(Long predictionId) throws Exception{
		Session s = HibernateUtil.getSession();
		Prediction prediction = PopulateDataObjects.getPredictionById(predictionId, s);
		
		String outfileName = Constants.CECCR_USER_BASE_PATH + prediction.getUserName() + "/PREDICTIONS/" + 
			prediction.getJobName() + "/" + prediction.getJobName() + "-prediction-values.csv";
		if(new File(outfileName).exists()){
			FileAndDirOperations.deleteFile(outfileName);
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(outfileName));
		
		ArrayList<Predictor> predictors = new ArrayList<Predictor>();
		String[] predictorIdArray = prediction.getPredictorIds().split("\\s+");
		for(int i = 0; i < predictorIdArray.length; i++){
			predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(predictorIdArray[i]), s));
		}
		
		String predictorNames = "";
		for(Predictor p: predictors){
			predictorNames += p.getName() + " ";
		}
		
		out.write("Chembench Prediction Output\n"
		+"User Name,"+prediction.getUserName()+"\n"
		+"Prediction Name,"+prediction.getJobName()+"\n"
		+"Predictors Used," + predictorNames + "\n"
		+"Similarity Cutoff,"+prediction.getSimilarityCutoff()+"\n"
		+"Prediction Dataset,"+prediction.getDatasetDisplay()+"\n"
		+"Predicted Date,"+Utility.formatDate(prediction.getDateCreated())+"\n"
		+"Download Date,"+new Date()+"\n"
		+"Web Site," + Constants.WEBADDRESS+"\n\n");
		
		String predictionHeader = "";
		for(Predictor p: predictors){
			predictionHeader += 
				p.getName() + " Predicted Value," + 
				p.getName() + " Standard Deviation," + 
				p.getName() + " Predicting Models," + 
				p.getName() + " Total Models,";
		}
		predictionHeader = predictionHeader.substring(0, predictionHeader.lastIndexOf(","));
		out.write("Compound ID," + predictionHeader + "\n");

		ArrayList<CompoundPredictions> compoundPredictionValues = PopulateDataObjects.populateCompoundPredictionValues(prediction.getDatasetId(), predictionId, s);
		for(CompoundPredictions cp: compoundPredictionValues){
			out.write(cp.getCompound() + ",");
			String predictionValues = "";
			for(PredictionValue pv : cp.getPredictionValues()){
				predictionValues += pv.getPredictedValue() + "," + pv.getStandardDeviation() + "," + pv.getNumModelsUsed() + "," + pv.getNumTotalModels() + ",";
			}
			predictionValues = predictionValues.substring(0, predictionValues.lastIndexOf(","));
			out.write(predictionValues + "\n");
		}
		
		
		for(Predictor p: predictors){
			List<PredictionValue> predictionValues = 
				PopulateDataObjects.getPredictionValuesByPredictionIdAndPredictorId(predictionId, p.getPredictorId(), s);
			
			String predictorName = p.getName();
			out.write("Predictor," + predictorName + "\n"
			+"Compound Name,"+"Predicted Value,"+"Standard Deviation,"+"Models Used,"+"Models In Predictor"+"\n");
			
			Iterator<PredictionValue> it = predictionValues.iterator();
			while(it.hasNext()){
				PredictionValue pv = it.next();
				if(pv.getPredictorId().equals(p.getPredictorId())){
					out.write(pv.getCompoundName()+","+pv.getPredictedValue()+",");
					out.write(pv.getStandardDeviation()+","+pv.getNumModelsUsed()+","+pv.getNumTotalModels()+"\n");
				}
			}
			out.write("\n");
		}
		s.close();
		out.close();
	}
}