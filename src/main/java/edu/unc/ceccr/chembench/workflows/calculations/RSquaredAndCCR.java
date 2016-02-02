package edu.unc.ceccr.chembench.workflows.calculations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.ExternalValidation;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RSquaredAndCCR {

    private static Logger logger = Logger.getLogger(RSquaredAndCCR.class.getName());

    public static List<Double> calculateResiduals(List<ExternalValidation> externalValidationList) {
        List<Double> residuals = Lists.newArrayList();

        Iterator<ExternalValidation> eit = externalValidationList.iterator();
        int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
        int numExtValuesWithNoModels = 0;
        while (eit.hasNext()) {
            ExternalValidation e = eit.next();
            if (e.getNumModels() != 0) {
                Double residual = new Double(e.getActualValue() - e.getPredictedValue());
                residuals.add(residual);
            } else {
                numExtValuesWithNoModels++;
                residuals.add(Double.NaN);
            }
            String predictedValue = DecimalFormat.getInstance().format(e.getPredictedValue()).replaceAll(",", "");
            e.setPredictedValue(Float.parseFloat(Utility.roundSignificantFigures(predictedValue, sigfigs)));
            if (!e.getStandDev().equalsIgnoreCase("No value")) {
                e.setStandDev(Utility.roundSignificantFigures(e.getStandDev(), sigfigs));
            }
        }
        if (numExtValuesWithNoModels == externalValidationList.size()) {
            //all external predictions were empty, meaning there were no good models.
            return residuals;
        }

        return residuals;
    }

    public static Double calculateRSquared(List<ExternalValidation> externalValidationList, List<Double> residuals) {

        Double avg = 0.0;
        for (ExternalValidation ev : externalValidationList) {
            avg += ev.getActualValue();
        }
        avg /= externalValidationList.size();
        Double ssErr = 0.0;
        boolean hasNoModels = true;
        for (Double residual : residuals) {
            if (!residual.isNaN()) {
                ssErr += residual * residual;
                hasNoModels = false;
            }
        }
        Double ssTot = 0.0;
        for (ExternalValidation ev : externalValidationList) {
            ssTot += (ev.getActualValue() - avg) * (ev.getActualValue() - avg);
        }
        Double rSquared = 0.0;
        if (ssTot != 0) {
            rSquared = (1.0 - (ssErr / ssTot));
        }
        if (hasNoModels) {
            rSquared = 0.0;
        }
        return rSquared;
    }

    public static ConfusionMatrix calculateConfusionMatrix(List<ExternalValidation> externalValidationList) {

        //scan through to find the unique observed values
        Set<Integer> classes = Sets.newTreeSet();
        for (ExternalValidation ev : externalValidationList) {
            int observedValue = Math.round(ev.getActualValue());
            int predictedValue = Math.round(ev.getPredictedValue());
            classes.add(observedValue);
            //if a value is predicted but not observed, we still need
            //a spot in the matrix for that, so make a spot for those too.
            classes.add(predictedValue);
        }

        //set up a confusion matrix to store counts of each (observed, predicted) possibility
        int numClasses = classes.size();
        int[][] matrix = new int[numClasses][numClasses]; // ...[observed][predicted] indexing
        for (ExternalValidation ev : externalValidationList) {
            int observedValue = Math.round(ev.getActualValue());
            int predictedValue = Math.round(ev.getPredictedValue());
            matrix[observedValue][predictedValue]++;
        }

        int total = externalValidationList.size();
        int totalCorrect = 0;
        for (int i = 0; i < numClasses; i++) {
            totalCorrect += matrix[i][i];
        }
        int totalIncorrect = total - totalCorrect;
        double ccr = totalCorrect / (double) total;

        ConfusionMatrix cm = new ConfusionMatrix();
        cm.setMatrix(matrix);
        cm.setUniqueObservedValues(classes);
        cm.setTotalCorrect(totalCorrect);
        cm.setTotalIncorrect(totalIncorrect);
        cm.setCcr(ccr);

        // for binary datasets with active (1) and inactive (0) categories, calculate additional stats
        if (classes.size() == 2 && classes.contains(0) && classes.contains(1)) {
            cm.setIsBinary(true);
            int trueNegatives = matrix[0][0];
            int truePositives = matrix[1][1];
            int falseNegatives = matrix[1][0];
            int falsePositives = matrix[0][1];
            int totalPositives = falsePositives + truePositives;
            int totalNegatives = falseNegatives + trueNegatives;

            double ppv = truePositives / (double) totalPositives;
            double npv = trueNegatives / (double) totalNegatives;
            double sensitivity = truePositives / (double) (truePositives + falseNegatives);
            double specificity = trueNegatives / (double) (trueNegatives + falsePositives);

            cm.setTrueNegatives(trueNegatives);
            cm.setTruePositives(truePositives);
            cm.setFalseNegatives(falseNegatives);
            cm.setFalsePositives(falsePositives);
            cm.setPpv(ppv);
            cm.setNpv(npv);
            cm.setSensitivity(sensitivity);
            cm.setSpecificity(specificity);
        }

        return cm;
    }

    public static void addRSquaredAndCCRToPredictor(Predictor selectedPredictor, Session session) {
        try {
            ConfusionMatrix confusionMatrix;
            String rSquared = "";
            String rSquaredAverageAndStddev = "";
            String ccrAverageAndStddev = "";
            List<ExternalValidation> externalValValues = null;
            List<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(selectedPredictor, session);

            //get external validation compounds of predictor
            if (childPredictors.size() != 0) {

                //get external set for each
                externalValValues = Lists.newArrayList();
                SummaryStatistics childAccuracies = new SummaryStatistics(); //contains the ccr or r^2 of each child

                for (int i = 0; i < childPredictors.size(); i++) {
                    Predictor cp = childPredictors.get(i);
                    List<ExternalValidation> childExtVals = (ArrayList<ExternalValidation>) PopulateDataObjects
                            .getExternalValidationValues(cp.getId(), session);

                    //calculate r^2 / ccr for this child
                    if (childExtVals.size() > 0) {
                        if (selectedPredictor.getActivityType().equals(Constants.CATEGORY)) {
                            Double childCcr = (RSquaredAndCCR.calculateConfusionMatrix(childExtVals)).getCcr();
                            childAccuracies.addValue(childCcr);
                        } else if (selectedPredictor.getActivityType().equals(Constants.CONTINUOUS)) {
                            List<Double> childResiduals = RSquaredAndCCR.calculateResiduals(childExtVals);
                            Double childRSquared = RSquaredAndCCR.calculateRSquared(childExtVals, childResiduals);
                            childAccuracies.addValue(childRSquared);
                            //CreateExtValidationChartWorkflow.createChart(selectedPredictor, ""+(i+1));
                        }
                        externalValValues.addAll(childExtVals);
                    }
                }

                Double mean = childAccuracies.getMean();
                Double stddev = childAccuracies.getStandardDeviation();

                if (selectedPredictor.getActivityType().equals(Constants.CONTINUOUS)) {
                    rSquaredAverageAndStddev =
                            Utility.roundSignificantFigures("" + mean, Constants.REPORTED_SIGNIFICANT_FIGURES);
                    rSquaredAverageAndStddev += " \u00B1 ";
                    rSquaredAverageAndStddev +=
                            Utility.roundSignificantFigures("" + stddev, Constants.REPORTED_SIGNIFICANT_FIGURES);
                    logger.debug("rsquared avg and stddev: " + rSquaredAverageAndStddev);
                    selectedPredictor.setExternalPredictionAccuracyAvg(rSquaredAverageAndStddev);
                    //make main ext validation chart
                    //CreateExtValidationChartWorkflow.createChart(selectedPredictor, "0");
                } else if (selectedPredictor.getActivityType().equals(Constants.CATEGORY)) {
                    ccrAverageAndStddev =
                            Utility.roundSignificantFigures("" + mean, Constants.REPORTED_SIGNIFICANT_FIGURES);
                    ccrAverageAndStddev += " \u00B1 ";
                    ccrAverageAndStddev +=
                            Utility.roundSignificantFigures("" + stddev, Constants.REPORTED_SIGNIFICANT_FIGURES);
                    logger.debug("ccr avg and stddev: " + ccrAverageAndStddev);
                    selectedPredictor.setExternalPredictionAccuracyAvg(ccrAverageAndStddev);
                }
            } else {
                externalValValues = (ArrayList<ExternalValidation>) PopulateDataObjects
                        .getExternalValidationValues(selectedPredictor.getId(), session);
            }

            if (externalValValues == null || externalValValues.isEmpty()) {
                logger.debug("ext validation set empty!");
                externalValValues = Lists.newArrayList();
                return;
            }

            //calculate residuals and fix significant figures on output data
            List<Double> residualsAsDouble = RSquaredAndCCR.calculateResiduals(externalValValues);
            List<String> residuals = Lists.newArrayList();
            if (residualsAsDouble.size() > 0) {
                for (Double residual : residualsAsDouble) {
                    if (residual.isNaN()) {
                        residuals.add("");
                    } else {
                        //if at least one residual exists, there must have been a good model
                        residuals.add(Utility
                                .roundSignificantFigures("" + residual, Constants.REPORTED_SIGNIFICANT_FIGURES));
                    }
                }
            } else {
                return;
            }

            if (selectedPredictor.getActivityType().equals(Constants.CATEGORY)) {
                //if category model, create confusion matrix.
                //round off the predicted values to nearest integer.
                confusionMatrix = RSquaredAndCCR.calculateConfusionMatrix(externalValValues);
                selectedPredictor.setExternalPredictionAccuracy(confusionMatrix.getCcrAsString());
            } else if (selectedPredictor.getActivityType().equals(Constants.CONTINUOUS)
                    && externalValValues.size() > 1) {
                //if continuous, calculate overall r^2 and... r0^2? or something?
                //just r^2 for now, more later.
                Double rSquaredDouble = RSquaredAndCCR.calculateRSquared(externalValValues, residualsAsDouble);
                rSquared = Utility.roundSignificantFigures("" + rSquaredDouble, Constants.REPORTED_SIGNIFICANT_FIGURES);
                selectedPredictor.setExternalPredictionAccuracy(rSquared);
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return;
    }
}
