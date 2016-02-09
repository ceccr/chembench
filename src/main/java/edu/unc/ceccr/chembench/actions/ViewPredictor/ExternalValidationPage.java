package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.ExternalValidation;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.calculations.ConfusionMatrix;
import edu.unc.ceccr.chembench.workflows.calculations.RSquaredAndCCR;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.util.ArrayList;
import java.util.List;

// struts2

public class ExternalValidationPage extends ViewPredictorAction {
    /**
     *
     */

    // used in creation of confusion matrix (category modeling only)
    ConfusionMatrix confusionMatrix;
    String rSquared = "";
    String rSquaredAverageAndStddev = "";
    String ccrAverageAndStddev = "";
    String mae = "";
    String maeSets = "";
    String stdDev = "";
    private List<ExternalValidation> externalValValues;
    private String hasGoodModels = Constants.YES;
    private List<String> residuals;

    public String load() throws Exception {
        String result = getBasicParameters();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        session = HibernateUtil.getSession();
        // get external validation compounds of predictor
        if (childPredictors.size() > 0) {

            foldNums.add("All");
            // get external set for each
            externalValValues = Lists.newArrayList();
            // SummaryStatistics childAccuracies = new SummaryStatistics();
            // //contains the ccr or r^2 of each child

            SummaryStatistics maeStat = new SummaryStatistics();
            for (int i = 0; i < childPredictors.size(); i++) {
                foldNums.add("" + (i + 1));
                Predictor cp = childPredictors.get(i);
                List<ExternalValidation> childExtVals = (ArrayList<ExternalValidation>) PopulateDataObjects
                        .getExternalValidationValues(cp.getId(), session);

                int numTotalModels = cp.getNumTotalModels();
                if (cp.getModelMethod().equals(Constants.KNNGA)) {
                    numTotalModels = PopulateDataObjects.getKnnPlusModelsByPredictorId(cp.getId(), session).size();
                }
                for (ExternalValidation ev : childExtVals) {
                    ev.setNumTotalModels(numTotalModels);
                }

                if (currentFoldNumber == 0 || currentFoldNumber == (i + 1)) {
                    externalValValues.addAll(childExtVals);
                }
                // calculate mean absolute error for this child
                List<Double> residualsForChild = RSquaredAndCCR.calculateResiduals(childExtVals);
                Double childSum = 0d;
                if (residualsForChild.size() > 0) {
                    for (Double residual : residualsForChild) {
                        if (!residual.isNaN()) {
                            // if at least one residual exists, there must
                            // have been a good model
                            childSum += Math.abs(residual);
                        }
                    }
                    maeStat.addValue(childSum / residualsForChild.size());
                }
            }

            maeSets = Utility.roundSignificantFigures("" + maeStat.getMean(), Constants.REPORTED_SIGNIFICANT_FIGURES);
            stdDev = Utility.roundSignificantFigures("" + maeStat.getStandardDeviation(),
                    Constants.REPORTED_SIGNIFICANT_FIGURES);
        } else {
            externalValValues = (ArrayList<ExternalValidation>) PopulateDataObjects
                    .getExternalValidationValues(selectedPredictor.getId(), session);
        }

        if (externalValValues == null || externalValValues.isEmpty()) {
            String modelMethod = selectedPredictor.getModelMethod();
            if ((modelMethod.equals(Constants.KNNGA) || modelMethod.equals(Constants.KNNSA))
                    && PopulateDataObjects.getKnnPlusModelsByPredictorId(selectedPredictor.getId(), session).size()
                    == 0) {
                hasGoodModels = Constants.NO;
            } else if (modelMethod.equals(Constants.SVM)
                    && PopulateDataObjects.getSvmModelsByPredictorId(selectedPredictor.getId(), session).size() == 0) {
                hasGoodModels = Constants.NO;
            }
            externalValValues = Lists.newArrayList();
            return result;
        }

        dataset = PopulateDataObjects.getDataSetById(selectedPredictor.getDatasetId(), session);

        // calculate residuals and fix significant figures on output data
        List<Double> residualsAsDouble = RSquaredAndCCR.calculateResiduals(externalValValues);

        hasGoodModels = Constants.NO;
        residuals = Lists.newArrayList();
        Double maeDouble = 0d;
        if (residualsAsDouble.size() > 0) {
            for (Double residual : residualsAsDouble) {
                if (residual.isNaN()) {
                    residuals.add("");
                } else {
                    // if at least one residual exists, there must have been a
                    // good model
                    hasGoodModels = Constants.YES;
                    residuals.add(Utility
                            .roundSignificantFigures("" + residual, Constants.REPORTED_SIGNIFICANT_FIGURES));
                    maeDouble += Math.abs(residual);
                }
            }
            mae = Utility.roundSignificantFigures("" + maeDouble / residualsAsDouble.size(),
                    Constants.REPORTED_SIGNIFICANT_FIGURES);
        } else {
            return result;
        }

        if (selectedPredictor.getActivityType().equals(Constants.CATEGORY)) {
            // if category model, create confusion matrix.
            // round off the predicted values to nearest integer.
            confusionMatrix = RSquaredAndCCR.calculateConfusionMatrix(externalValValues);
        } else if (selectedPredictor.getActivityType().equals(Constants.CONTINUOUS) && externalValValues.size() > 1) {
            // if continuous, calculate overall r^2 and... r0^2? or something?
            // just r^2 for now, more later.
            Double rSquaredDouble = RSquaredAndCCR.calculateRSquared(externalValValues, residualsAsDouble);
            rSquared = Utility.roundSignificantFigures("" + rSquaredDouble, Constants.REPORTED_SIGNIFICANT_FIGURES);
        }
        session.close();
        return result;
    }

    // getters and setters

    public List<ExternalValidation> getExternalValValues() {
        return externalValValues;
    }

    public void setExternalValValues(List<ExternalValidation> externalValValues) {
        this.externalValValues = externalValValues;
    }

    public String getHasGoodModels() {
        return hasGoodModels;
    }

    public void setHasGoodModels(String hasGoodModels) {
        this.hasGoodModels = hasGoodModels;
    }

    public List<String> getResiduals() {
        return residuals;
    }

    public void setResiduals(List<String> residuals) {
        this.residuals = residuals;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getRSquared() {
        return rSquared;
    }

    public void setRSquared(String rSquared) {
        this.rSquared = rSquared;
    }

    public String getRSquaredAverageAndStddev() {
        return rSquaredAverageAndStddev;
    }

    public void setRSquaredAverageAndStddev(String rSquaredAverageAndStddev) {
        this.rSquaredAverageAndStddev = rSquaredAverageAndStddev;
    }

    public ConfusionMatrix getConfusionMatrix() {
        return confusionMatrix;
    }

    public void setConfusionMatrix(ConfusionMatrix confusionMatrix) {
        this.confusionMatrix = confusionMatrix;
    }

    public String getCcrAverageAndStddev() {
        return ccrAverageAndStddev;
    }

    public void setCcrAverageAndStddev(String ccrAverageAndStddev) {
        this.ccrAverageAndStddev = ccrAverageAndStddev;
    }

    public String getMaeSets() {
        return maeSets;
    }

    public void setMaeSets(String maeSets) {
        this.maeSets = maeSets;
    }

    public String getStdDev() {
        return stdDev;
    }

    public void setStdDev(String stdDev) {
        this.stdDev = stdDev;
    }

    // end getters and setters
}
