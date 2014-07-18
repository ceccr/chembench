package edu.unc.ceccr.workflows.download;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.*;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import org.hibernate.Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class WriteCsv {
    //In most cases, the files generated during the running of a job are
    //of no use to humans. These functions generate downloadable results
    //files that give job results in a more readable form.

    public static void writeExternalPredictionsAsCSV(Long predictorId) throws Exception {
        Session s = HibernateUtil.getSession();
        Predictor predictor = PopulateDataObjects.getPredictorById(predictorId, s);
        ArrayList<ExternalValidation> externalValidationValues = new ArrayList<ExternalValidation>();

        String outfileName = Constants.CECCR_USER_BASE_PATH + predictor.getUserName() + "/PREDICTORS/" +
                predictor.getName() + "/" + predictor.getName() + "-external-set-predictions.csv";
        BufferedWriter out = new BufferedWriter(new FileWriter(outfileName));

        ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(predictor, s);
        if (childPredictors.isEmpty()) {
            externalValidationValues = (ArrayList<ExternalValidation>) PopulateDataObjects
                    .getExternalValidationValues(predictor.getId(), s);
            for (ExternalValidation ev : externalValidationValues) {
                ev.setNumTotalModels(predictor.getNumTestModels());
            }
        } else {
            for (Predictor cp : childPredictors) {
                ArrayList<ExternalValidation> childExtVals = (ArrayList<ExternalValidation>) PopulateDataObjects
                        .getExternalValidationValues(cp.getId(), s);
                for (ExternalValidation ev : childExtVals) {
                    ev.setNumTotalModels(cp.getNumTestModels());
                    externalValidationValues.add(ev);
                }
            }
        }

        out.write("Chembench Predictor External Validation\n"
                + "User Name," + predictor.getUserName() + "\n"
                + "Predictor Name," + predictor.getName() + "\n"
                + "Dataset," + predictor.getDatasetDisplay() + "\n");
        if (predictor.getChildType() != null && predictor.getChildType().equals(Constants.NFOLD)) {
            out.write("External Set Accuracy," + predictor.getExternalPredictionAccuracyAvg() + "\n");
        } else {
            out.write("External Set Accuracy," + predictor.getExternalPredictionAccuracy() + "\n");
        }
        out.write("Modeling Method," + predictor.getModelMethod() + "\n"
                + "Descriptor Type," + predictor.getDescriptorGeneration() + "\n"
                + "Created Date," + Utility.formatDate(predictor.getDateCreated()) + "\n"
                + "Download Date," + new Date() + "\n"
                + "Web Site," + Constants.WEBADDRESS + "\n\n");
        out.write("Compound ID," +
                "Observed Value," +
                "Predicted Value," +
                "Standard Deviation," +
                "Number of Predicting Models," +
                "Total Number of Models\n");

        for (ExternalValidation ev : externalValidationValues) {
            String observedValueStr = Utility.roundSignificantFigures("" + ev.getActualValue(),
                    Constants.REPORTED_SIGNIFICANT_FIGURES);
            String predictedValueStr = Utility.roundSignificantFigures("" + ev.getPredictedValue(),
                    Constants.REPORTED_SIGNIFICANT_FIGURES);
            out.write(ev.getCompoundId() + "," +
                    observedValueStr + "," +
                    predictedValueStr + "," +
                    ev.getStandDev() + "," +
                    ev.getNumModels() + "," +
                    ev.getNumTotalModels() + "\n");
        }

        out.close();
    }

    public static void writePredictionValuesAsCSV(Long predictionId) throws Exception {
        Session s = HibernateUtil.getSession();
        Prediction prediction = PopulateDataObjects.getPredictionById(predictionId, s);

        String outfileName = Constants.CECCR_USER_BASE_PATH + prediction.getUserName() + "/PREDICTIONS/" +
                prediction.getName() + "/" + prediction.getName() + "-prediction-values.csv";
        if (new File(outfileName).exists()) {
            FileAndDirOperations.deleteFile(outfileName);
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(outfileName));

        ArrayList<Predictor> predictors = new ArrayList<Predictor>();
        String[] predictorIdArray = prediction.getPredictorIds().split("\\s+");
        for (int i = 0; i < predictorIdArray.length; i++) {
            predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(predictorIdArray[i]), s));
        }

        String predictorNames = "";
        for (Predictor p : predictors) {
            predictorNames += p.getName() + " ";
        }

        out.write("Chembench Prediction Output\n"
                + "User Name," + prediction.getUserName() + "\n"
                + "Prediction Name," + prediction.getName() + "\n"
                + "Predictors Used," + predictorNames + "\n"
                + "Similarity Cutoff," + prediction.getSimilarityCutoff() + "\n"
                + "Prediction Dataset," + prediction.getDatasetDisplay() + "\n"
                + "Predicted Date," + Utility.formatDate(prediction.getDateCreated()) + "\n"
                + "Download Date," + new Date() + "\n"
                + "Web Site," + Constants.WEBADDRESS + "\n");

		/*
		//Option 1: Show everything in a big horizontal table
		String predictionHeader = "";
		for(Predictor p: predictors){
			ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(p, s);
			if(childPredictors.isEmpty()){
				predictionHeader += 
					p.getName() + " Predicted Value," + 
					p.getName() + " Standard Deviation," + 
					p.getName() + " Predicting Models," + 
					p.getName() + " Total Models,";
			}
			else{
				predictionHeader += 
					p.getName() + " Predicted Value," + 
					p.getName() + " Standard Deviation," + 
					p.getName() + " Predicting Folds," + 
					p.getName() + " Total Folds,";
			}
		}
		predictionHeader = predictionHeader.substring(0, predictionHeader.lastIndexOf(","));
		out.write("Compound ID," + predictionHeader + "\n");

		ArrayList<CompoundPredictions> compoundPredictionValues = PopulateDataObjects.populateCompoundPredictionValues
		(prediction.getDatasetId(), predictionId, s);
		for(CompoundPredictions cp: compoundPredictionValues){
			out.write(cp.getCompound().replaceAll(",", "_") + ",");
			String predictionValues = "";
			for(PredictionValue pv : cp.getPredictionValues()){
				predictionValues += pv.getPredictedValue() + "," + pv.getStandardDeviation() + ",
				" + pv.getNumModelsUsed() + "," + pv.getNumTotalModels() + ",";
			}
			predictionValues = predictionValues.substring(0, predictionValues.lastIndexOf(","));
			out.write(predictionValues + "\n");
		}
		*/

        //Option 2: Show predictor-by-predictor output, stacked vertically
        for (Predictor p : predictors) {
            List<PredictionValue> predictionValues =
                    PopulateDataObjects.getPredictionValuesByPredictionIdAndPredictorId(predictionId, p.getId(), s);

            String predictorName = p.getName();
            out.write("\nPredictor," + predictorName + "\n"
                    + "Compound Name," + "Predicted Value," + "Standard Deviation," + "Models Used," + "Models In Predictor" + "\n");

            Iterator<PredictionValue> it = predictionValues.iterator();
            while (it.hasNext()) {
                PredictionValue pv = it.next();
                if (pv.getPredictorId().equals(p.getId())) {
                    out.write(pv.getCompoundName().replaceAll(",", "_") + "," + pv.getPredictedValue() + ",");
                    out.write(pv.getStandardDeviation() + "," + pv.getNumModelsUsed() + "," + pv.getNumTotalModels() + "\n");
                }
            }
        }
        s.close();
        out.close();
    }
}