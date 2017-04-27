package edu.unc.ceccr.chembench.workflows.download;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.actions.McraAction;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class WriteCsv {
    //In most cases, the files generated during the running of a job are
    //of no use to humans. These functions generate downloadable results
    //files that give job results in a more readable form.

    private static final Logger logger = LoggerFactory.getLogger(WriteCsv.class);
    private static PredictorRepository predictorRepository;
    private static PredictionRepository predictionRepository;
    private static ExternalValidationRepository externalValidationRepository;
    private static CompoundPredictionsRepository compoundPredictionsRepository;

    public static void writeExternalPredictionsAsCSV(Long predictorId) throws Exception {
        Predictor predictor = predictorRepository.findOne(predictorId);
        List<ExternalValidation> externalValidationValues = new ArrayList<>();

        String outfileName = Constants.CECCR_USER_BASE_PATH + predictor.getUserName() + "/PREDICTORS/" +
                predictor.getName() + "/" + predictor.getName() + "-external-set-predictions.csv";
        BufferedWriter out = new BufferedWriter(new FileWriter(outfileName));

        List<Predictor> childPredictors = predictorRepository.findByParentId(predictor.getId());
        if (childPredictors.isEmpty()) {
            externalValidationValues = externalValidationRepository.findByPredictorId(predictor.getId());
            for (ExternalValidation ev : externalValidationValues) {
                ev.setNumTotalModels(predictor.getNumTestModels());
            }
        } else {
            for (Predictor cp : childPredictors) {
                List<ExternalValidation> childExtVals = externalValidationRepository.findByPredictorId(cp.getId());
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
        Prediction prediction = predictionRepository.findOne(predictionId);

        String outfileName =
                Paths.get(Constants.CECCR_USER_BASE_PATH, prediction.getUserName(), "PREDICTIONS", prediction.getName(),
                        prediction.getName() + "-prediction-values.csv").toString();
        if (new File(outfileName).exists()) {
            FileAndDirOperations.deleteFile(outfileName);
        }

        List<Predictor> predictors = new ArrayList<>();
        String[] predictorIdArray = prediction.getPredictorIds().split("\\s+");
        for (String predictorId : predictorIdArray) {
            predictors.add(predictorRepository.findOne(Long.parseLong(predictorId)));
        }

        String predictorNames =
                Utility.SPACE_JOINER.join(Iterables.transform(predictors, new Function<Predictor, String>() {
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
                out.write(Utility.COMMA_JOINER.join(line));
                out.newLine();
            }

            // Option 1: Show everything in a big horizontal table
            List<String> predictionHeader = Lists.newArrayList("Compound ID");
            for (Predictor p : predictors) {
                List<Predictor> childPredictors = predictorRepository.findByParentId(p.getId());
                if (childPredictors.isEmpty()) {
                    predictionHeader.addAll(Lists
                            .newArrayList(p.getName() + " Predicted Value", p.getName() + " Standard Deviation",
                                    p.getName() + " Predicting Models", p.getName() + " Total Models"));
                } else {
                    predictionHeader.addAll(Lists
                            .newArrayList(p.getName() + " Predicted Value", p.getName() + " " + "Standard Deviation",
                                    p.getName() + " Predicting Folds", p.getName() + " Total Folds"));
                }
                predictionHeader.add(p.getName() + " σ");
                predictionHeader.add("In cutoff?");
            }
            out.write(Utility.COMMA_JOINER.join(predictionHeader));
            out.newLine();

            List<CompoundPredictions> compoundPredictionValues =
                    compoundPredictionsRepository.findByPredictionId(predictionId);
            for (CompoundPredictions cp : compoundPredictionValues) {
                out.write(cp.getCompound().replaceAll(",", "_") + ",");

                if (cp.getPredictionValues() != null) {
                    List<Object> predictionValues = new ArrayList<>();
                    for (PredictionValue pv : cp.getPredictionValues()) {
                        predictionValues.add(pv.getPredictedValue());
                        predictionValues.add(pv.getStandardDeviation());
                        predictionValues.add(pv.getNumModelsUsed());
                        predictionValues.add(pv.getNumTotalModels());
                        if (pv.getZScore() != null) {
                            predictionValues.add(pv.getZScore());
                            predictionValues.add((pv.getZScore() < prediction.getSimilarityCutoff()) ? "Yes" : "No");
                        } else {
                            predictionValues.add("N/A"); // app. domain column
                            predictionValues.add("N/A"); // "in cutoff?" column
                        }
                    }
                    out.write(Utility.COMMA_JOINER.join(predictionValues));
                }
                out.newLine();
            }
        } catch (IOException e) {
            logger.error("Failed to write prediction to CSV", e);
        }
    }

    /* Only to be used for when a dataset, not a single compound, is predicted. Return path to output file */
    public static String writePredictionValuesAsCSV(List<McraAction.McraPrediction> mcraPredictions, String username, String modelingDatasetName,
                                                  Dataset predictingDataset, String fileName) {
        String outfileName = Paths.get(Constants.CECCR_USER_BASE_PATH, predictingDataset.getUserName(),
                        "DATASETS", predictingDataset.getName(), fileName + ".csv").toString();
        if (new File(outfileName).exists()) {
            FileAndDirOperations.deleteFile(outfileName);
        }

        DecimalFormat format = new DecimalFormat(".##");

        try (BufferedWriter out = new BufferedWriter(new FileWriter(outfileName))) {

            // header metadata
            String[][] header = {{"Chembench Prediction Output"},
                    {"User Name", StringEscapeUtils.escapeCsv(username)},
                    {"Predicting Dataset", StringEscapeUtils.escapeCsv(predictingDataset.getName())},
                    {"Modeling Dataset", StringEscapeUtils.escapeCsv(modelingDatasetName)},
                    {"Download Date", new Date().toString()},
                    {"Website", Constants.WEBADDRESS}};
            for (String[] line : header) {
                out.write(Utility.COMMA_JOINER.join(line));
                out.newLine();
            }

            // column labels
            List<String> predictionHeader = Lists.newArrayList("Compound", "Prediction");
            boolean binary = mcraPredictions.get(0).getRoundedPredictedActivity() != -1;
            if (binary) {
                predictionHeader.add("Rounded Prediction");
            }
            predictionHeader.add("# Neighbors");
            for (McraAction.DescriptorResult descriptorResult : mcraPredictions.get(0).getDescriptors()) {
                String descriptorName = descriptorResult.getName();
                predictionHeader.addAll(Lists.newArrayList(descriptorName + " Activity",
                        descriptorName + " Similarity",
                        descriptorName + " Neighbors"));
            }
            out.write(Utility.COMMA_JOINER.join(predictionHeader));
            out.newLine();

            //rows
            for (McraAction.McraPrediction pred : mcraPredictions) {
                out.write(pred.getName().replaceAll(",", "_") + ",");   //replace commas in compound name with _
                out.write(format.format(pred.getPredictedActivity()) + ",");
                if (binary) out.write(pred.getRoundedPredictedActivity() + ",");
                out.write(pred.getNumNearestNeighbors() + ",");
                List<McraAction.DescriptorResult> descriptorResults = pred.getDescriptors();
                for (int i=0; i<descriptorResults.size(); i++) {
                    McraAction.DescriptorResult dr = descriptorResults.get(i);
                    out.write(format.format(dr.getAverageActivity()) + ",");
                    out.write(format.format(dr.getAverageSimilarity()) + ",");
                    if (i == descriptorResults.size()-1){
                        //out.write(StringEscapeUtils.escapeCsv(dr.getNeighborIds()));
                        out.write(dr.getNeighborIds().replaceAll(",", "_") + ",");   //replace commas in compound name with _
                    } else {
                        //out.write(StringEscapeUtils.escapeCsv(dr.getNeighborIds())+",");
                        out.write(dr.getNeighborIds().replaceAll(",", "_") + ",");   //replace commas in compound name with _
                    }
                }
                out.newLine();
            }
        } catch (IOException e) {
            logger.error("Failed to write prediction to CSV", e);
        }
        return outfileName;
    }


    @Autowired
    public void setPredictionRepository(PredictionRepository predictionRepository) {
        WriteCsv.predictionRepository = predictionRepository;
    }

    @Autowired
    public void setPredictorRepository(PredictorRepository predictorRepository) {
        WriteCsv.predictorRepository = predictorRepository;
    }

    @Autowired
    public void setExternalValidationRepository(ExternalValidationRepository externalValidationRepository) {
        WriteCsv.externalValidationRepository = externalValidationRepository;
    }

    @Autowired
    public void setCompoundPredictionsRepository(CompoundPredictionsRepository compoundPredictionsRepository) {
        WriteCsv.compoundPredictionsRepository = compoundPredictionsRepository;
    }
}
