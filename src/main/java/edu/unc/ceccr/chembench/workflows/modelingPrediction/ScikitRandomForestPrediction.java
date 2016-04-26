package edu.unc.ceccr.chembench.workflows.modelingPrediction;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.ExternalValidation;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.persistence.RandomForestGrove;
import edu.unc.ceccr.chembench.persistence.RandomForestTree;
import edu.unc.ceccr.chembench.utilities.Utility;

import java.util.List;
import java.util.Map;

/**
 * This is a transport class to shuttle forest/tree metadata from Python (scikit) to Java (Chembench).
 * It shouldn't be necessary to manually instantiate and modify this class in Java; instead, read the
 * scikit-generated json metadata (using Gson in our case) into this Java class.
 */
public class ScikitRandomForestPrediction {
    private List<ScikitRandomForestPrediction> trees;
    private Map<String, Double> predictions;
    private ScikitRandomForestPredictionStats stats;
    private List<String> descriptorsUsed;

    public RandomForestGrove getGrove(Predictor predictor, boolean isYRandom) {
        RandomForestGrove grove = new RandomForestGrove();
        grove.setPredictorId(predictor.getId());
        grove.setName(predictor.getName());
        grove.setIsYRandomModel((isYRandom) ? Constants.YES : Constants.NO);
        grove.setR2(Utility.roundSignificantFigures(this.stats.getrSquared(), 4));
        grove.setMse(Utility.roundSignificantFigures(this.stats.getMse(), 4));
        grove.setCcr(Utility.roundSignificantFigures(this.stats.getCcr(), 4));
        return grove;
    }

    public List<RandomForestTree> getTrees(RandomForestGrove grove) {
        List<RandomForestTree> trees = Lists.newArrayList();
        for (ScikitRandomForestPrediction treePrediction : this.trees) {
            RandomForestTree tree = new RandomForestTree();
            tree.setRandomForestGroveId(grove.getId());
            ScikitRandomForestPredictionStats stats = treePrediction.getStats();
            tree.setR2(Utility.roundSignificantFigures(stats.getrSquared(), 4));
            tree.setMse(Utility.roundSignificantFigures(stats.getMse(), 4));
            tree.setCcr(Utility.roundSignificantFigures(stats.getCcr(), 4));
            tree.setDescriptorsUsed(Utility.SPACE_JOINER.join(treePrediction.getDescriptorsUsed()));
            trees.add(tree);
        }
        return trees;
    }

    public List<ExternalValidation> getExternalSetPredictions(Map<String, Double> groundTruth, long predictorId) {
        List<ExternalValidation> evs = Lists.newArrayList();
        for (String key : predictions.keySet()) {
            ExternalValidation ev = new ExternalValidation();
            ev.setPredictorId(predictorId);
            ev.setCompoundId(key);
            ev.setPredictedValue((float) ((double) predictions.get(key)));
            ev.setActualValue((float) ((double) groundTruth.get(key)));

            ev.setNumModels(1);
            ev.setNumTotalModels(1);
            ev.setStandDev("0.0");
            evs.add(ev);
        }
        return evs;
    }

    public List<ScikitRandomForestPrediction> getTrees() {
        return trees;
    }

    public void setTrees(List<ScikitRandomForestPrediction> trees) {
        this.trees = trees;
    }

    public Map<String, Double> getPredictions() {
        return predictions;
    }

    public void setPredictions(Map<String, Double> predictions) {
        this.predictions = predictions;
    }

    public ScikitRandomForestPredictionStats getStats() {
        return stats;
    }

    public void setStats(ScikitRandomForestPredictionStats stats) {
        this.stats = stats;
    }

    public List<String> getDescriptorsUsed() {
        return descriptorsUsed;
    }

    public void setDescriptorsUsed(List<String> descriptorsUsed) {
        this.descriptorsUsed = descriptorsUsed;
    }
}
