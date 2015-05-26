package edu.unc.ceccr.chembench.workflows.download;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WriteCsv {
    //In most cases, the files generated during the running of a job are
    //of no use to humans. These functions generate downloadable results
    //files that give job results in a more readable form.

    private static Logger logger = Logger.getLogger(WriteCsv.class.getName());

    public static void writeExternalPredictionsAsCSV(Long predictorId) throws Exception {
        Session s = HibernateUtil.getSession();
        Predictor predictor = PopulateDataObjects.getPredictorById(predictorId, s);
        List<ExternalValidation> externalValidationValues = Lists.newArrayList();

        String outfileName = Constants.CECCR_USER_BASE_PATH + predictor.getUserName() + "/PREDICTORS/" +
                predictor.getName() + "/" + predictor.getName() + "-external-set-predictions.csv";
        BufferedWriter out = new BufferedWriter(new FileWriter(outfileName));

        List<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(predictor, s);
        if (childPredictors.isEmpty()) {
            externalValidationValues = (ArrayList<ExternalValidation>) PopulateDataObjects
                    .getExternalValidationValues(predictor.getId(), s);
            for (ExternalValidation ev : externalValidationValues) {
                ev.setNumTotalModels(predictor.getNumTestModels());
            }
        } else {
            for (Predictor cp : childPredictors) {
                List<ExternalValidation> childExtVals =
                        (ArrayList<ExternalValidation>) PopulateDataObjects.getExternalValidationValues(cp.getId(), s);
                for (ExternalValidation ev : childExtVals) {
                    ev.setNumTotalModels(cp.getNumTestModels());
                    externalValidationValues.add(ev);
                }
            }
        }

        out.write("Chembench Predictor External Validation\n" + "User Name," + predictor.getUserName() + "\n"
                + "Predictor Name," + predictor.getName() + "\n" + "Dataset," + predictor.getDatasetDisplay() + "\n");
        if (predictor.getChildType() != null && predictor.getChildType().equals(Constants.NFOLD)) {
            out.write("External Set Accuracy," + predictor.getExternalPredictionAccuracyAvg() + "\n");
        } else {
            out.write("External Set Accuracy," + predictor.getExternalPredictionAccuracy() + "\n");
        }
        out.write("Modeling Method," + predictor.getModelMethod() + "\n" + "Descriptor Type," + predictor
                .getDescriptorGeneration() + "\n" + "Created Date," + Utility.formatDate(predictor.getDateCreated())
                + "\n" + "Download Date," + new Date() + "\n" + "Web Site," + Constants.WEBADDRESS + "\n\n");
        out.write("Compound ID," +
                "Observed Value," +
                "Predicted Value," +
                "Standard Deviation," +
                "Number of Predicting Models," +
                "Total Number of Models\n");

        for (ExternalValidation ev : externalValidationValues) {
            String observedValueStr =
                    Utility.roundSignificantFigures("" + ev.getActualValue(), Constants.REPORTED_SIGNIFICANT_FIGURES);
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

    public static void writePredictionValuesAsCSV(Long predictionId) {
        logger.debug(String.format("Writing out prediction %d as CSV", predictionId));
        Session s = null;
        try {
            s = HibernateUtil.getSession();
            Prediction prediction = PopulateDataObjects.getPredictionById(predictionId, s);

            String outfileName = Paths.get(Constants.CECCR_USER_BASE_PATH, prediction.getUserName(), "PREDICTIONS",
                    prediction.getName(), prediction.getName() + "-prediction-values.csv").toString();
            if (new File(outfileName).exists()) {
                FileAndDirOperations.deleteFile(outfileName);
            }

            List<Predictor> predictors = Lists.newArrayList();
            String[] predictorIdArray = prediction.getPredictorIds().split("\\s+");
            for (String aPredictorIdArray : predictorIdArray) {
                predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(aPredictorIdArray), s));
            }

            String predictorNames =
                    Joiner.on(" ").join(Iterables.transform(predictors, new Function<Predictor, String>() {
                        public String apply(Predictor p) {
                            return p.getName();
                        }
                    }));

            try (BufferedWriter out = new BufferedWriter(new FileWriter(outfileName))) {
                String[][] header = {{"Chembench Prediction Output"}, {"User Name", prediction.getUserName()},
                        {"Prediction Name", prediction.getName()}, {"Predictors Used", predictorNames},
                        {"Similarity Cutoff", prediction.getSimilarityCutoff().toString()},
                        {"Prediction Dataset", prediction.getDatasetDisplay()},
                        {"Predicted Date", Utility.formatDate(prediction.getDateCreated())},
                        {"Download Date", new Date().toString()}, {"Website", Constants.WEBADDRESS}};
                for (String[] line : header) {
                    out.write(Joiner.on(",").join(line));
                    out.newLine();
                }

                // Option 1: Show everything in a big horizontal table
                List<String> predictionHeader = Lists.newArrayList("Compound ID");
                for (Predictor p : predictors) {
                    List<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(p, s);
                    if (childPredictors.isEmpty()) {
                        predictionHeader.addAll(Lists
                                .newArrayList(p.getName() + " Predicted Value", p.getName() + " Standard Deviation",
                                        p.getName() + " Predicting Models", p.getName() + " Total Models"));
                    } else {
                        predictionHeader.addAll(Lists.newArrayList(p.getName() + " Predicted Value",
                                p.getName() + " " + "Standard Deviation", p.getName() + " Predicting Folds",
                                p.getName() + " Total Folds"));
                    }
                    predictionHeader.add(p.getName() + " σ");
                }
                out.write(Joiner.on(",").join(predictionHeader));
                out.newLine();

                List<CompoundPredictions> compoundPredictionValues = PopulateDataObjects
                        .populateCompoundPredictionValues(prediction.getDatasetId(), predictionId, s);
                for (CompoundPredictions cp : compoundPredictionValues) {
                    out.write(cp.getCompound().replaceAll(",", "_") + ",");

                    List<Object> predictionValues = Lists.newArrayList();
                    for (PredictionValue pv : cp.getPredictionValues()) {
                        if (pv.getZScore() > prediction.getSimilarityCutoff()) {
                            predictionValues.add("Above cutoff");
                        } else {
                            predictionValues.add(pv.getPredictedValue());
                        }
                        predictionValues.add(pv.getStandardDeviation());
                        predictionValues.add(pv.getNumModelsUsed());
                        predictionValues.add(pv.getNumTotalModels());
                        predictionValues.add(pv.getZScore());
                    }
                    out.write(Joiner.on(",").join(predictionValues));
                    out.newLine();
                }
            } catch (IOException e) {
                logger.error("Failed to write prediction to CSV", e);
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to fetch prediction object or related object", e);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}
