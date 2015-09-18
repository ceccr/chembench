package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.google.common.base.Functions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Predictor;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RandomForestDescriptorImportancePage extends ViewPredictorAction {
    private static Logger logger = Logger.getLogger(RandomForestDescriptorImportancePage.class.getName());
    private ImmutableSortedMap<String, Double> importance;
    private String importanceMeasure;
    private int foldNumber = 0;
    private int totalFolds = 0;

    public String execute() throws Exception {
        String result = getBasicParameters();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        Map<String, Double> rawImportance = Maps.newHashMap();
        if (selectedPredictor.getChildType().equals(Constants.NFOLD)) {
            childPredictors = selectedPredictor.getChildren();
            totalFolds = childPredictors.size();
            if (foldNumber > 0) {
                if (foldNumber > totalFolds) {
                    throw new IllegalArgumentException(String.format(
                            "Invalid fold number: requested %d but only %d " + "folds exist", foldNumber,
                            totalFolds));
                }
                rawImportance = getDescriptorImportance(childPredictors.get(foldNumber - 1), selectedPredictor);
            } else {
                Map<String, Double> averagedImportance = Maps.newHashMap();
                for (Predictor p : childPredictors) {
                    Map<String, Double> childImportance = getDescriptorImportance(p, selectedPredictor);
                    for (String key : childImportance.keySet()) {
                        Double childValue = childImportance.get(key);
                        Double totalValue = averagedImportance.get(key);
                        if (totalValue == null) {
                            totalValue = 0d;
                        }
                        averagedImportance.put(key, childValue + totalValue);
                    }
                }
                for (String key : averagedImportance.keySet()) {
                    averagedImportance.put(key, averagedImportance.get(key) / totalFolds);
                }
                rawImportance = averagedImportance;
            }
        } else {
            rawImportance = getDescriptorImportance(selectedPredictor, null);
        }

        // XXX the ...compound(Ordering.natural()) is needed to break ties when values are equal. this adds
        // secondary sorting by key alphabetically
        Ordering<String> descValueComparator = Ordering.natural().onResultOf(Functions.forMap(rawImportance)).reverse()
                .compound(Ordering.natural());
        importance = ImmutableSortedMap.copyOf(rawImportance, descValueComparator);
        return SUCCESS;
    }

    private Map<String, Double> getDescriptorImportance(Predictor p, Predictor parentPredictor) {
        Path basePath = Paths.get(Constants.CECCR_USER_BASE_PATH, p.getUserName(), "PREDICTORS");
        if (parentPredictor != null) {
            basePath = basePath.resolve(parentPredictor.getName());
        }
        basePath = basePath.resolve(p.getName());

        // XXX new models will never generate more than one RData file (named "RF_rand_sets_0_trn0.RData")
        // however, old models may have more than one RData file due to how splitting was implemented for legacy RF.
        // (in the past we allowed RF models to have more than one split.)
        // in these cases the descriptor importance table will only show the importance data for the first split.
        String[] filenames = basePath.toFile().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".rdata");
            }
        });
        Arrays.sort(filenames);

        File outFile = basePath.resolve("importance.csv").toFile();
        int exitValue = 0;
        if (outFile.length() == 0) {
            try {
                ProcessBuilder pb = new ProcessBuilder("Rscript",
                        Paths.get(Constants.CECCR_BASE_PATH, Constants.SCRIPTS_PATH, "get_importance.R").toString(),
                        basePath.resolve(filenames[0]).toString());
                pb.redirectOutput(outFile);
                exitValue = pb.start().waitFor();
            } catch (IOException e) {
                throw new RuntimeException("R descriptor importance extraction failed", e);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while waiting for descriptor importance extraction", e);
            }

            if (outFile.length() == 0) {
                throw new RuntimeException("Descriptor importance extraction produced no output");
            } else if (exitValue != 0) {
                outFile.delete();
                throw new RuntimeException("Descriptor importance extraction exited with non-zero exit code: " +
                        exitValue);
            }
        }

        Splitter splitter = Splitter.on('\t');
        Map<String, Double> data = Maps.newHashMap();
        try (BufferedReader reader = Files.newBufferedReader(outFile.toPath(), StandardCharsets.UTF_8)) {
            List<String> headerFields = splitter.splitToList(reader.readLine());
            // XXX if the RF call has importance = FALSE (the default), only IncNodePurity (continuous) or
            // MeanDecreaseGini (category) is generated. If importance = TRUE, then %IncMse (continuous)
            // or MeanDecreaseAccuracy (category) are also generated. ideally we'd report both measures, but for now,
            // report IncNodePurity / MeanDecreaseGini as that'll always be there
            int importanceMeasureIndex = -1;
            for (int i = 0; i < headerFields.size(); i++) {
                String currField = headerFields.get(i);
                if (currField.equals("IncNodePurity") || currField.equals("MeanDecreaseGini")) {
                    importanceMeasureIndex = i;
                    importanceMeasure = currField;
                    break;
                }
            }
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> fields = splitter.splitToList(line);
                data.put(fields.get(0), Double.parseDouble(fields.get(importanceMeasureIndex)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read descriptor importance output", e);
        }
        return data;
    }

    public Map<String, Double> getImportance() {
        return importance;
    }

    public String getImportanceMeasure() {
        return importanceMeasure;
    }

    public int getFoldNumber() {
        return foldNumber;
    }

    public void setFoldNumber(int foldNumber) {
        this.foldNumber = foldNumber;
    }

    public int getTotalFolds() {
        return totalFolds;
    }
}
