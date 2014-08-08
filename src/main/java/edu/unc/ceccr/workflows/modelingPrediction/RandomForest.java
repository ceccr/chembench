package edu.unc.ceccr.workflows.modelingPrediction;

import com.google.common.collect.Lists;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.*;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

public class RandomForest {
    private static Logger logger = Logger.getLogger(RandomForest.class.getName());

    // MODELING WORKFLOW FUNCTIONS
    public static void
    SetUpYRandomization(String userName, String jobName) throws Exception {
        String workingdir = Constants.CECCR_USER_BASE_PATH + userName + "/"
                + jobName + "/";

        // create yRandom dirs
        new File(workingdir + "yRandom/").mkdir();
        new File(workingdir + "yRandom/Logs/").mkdir();

        // make sure dirs are empty
        FileAndDirOperations.deleteDirContents(workingdir + "yRandom/");
        FileAndDirOperations.deleteDirContents(workingdir + "yRandom/Logs/");

        // copy files to yRandom
        String fromDir = workingdir;
        String toDir = workingdir + "yRandom/";

        String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
        String newModelingXFile = "RF_" + Constants.MODELING_SET_X_FILE;

        FileAndDirOperations.copyFile(fromDir + "RF_RAND_sets.list", toDir
                + "RF_RAND_sets.list");
        FileAndDirOperations.copyFile(fromDir + newExternalXFile, toDir
                + newExternalXFile);
        FileAndDirOperations.copyFile(fromDir + newModelingXFile, toDir
                + newModelingXFile);

        logger.debug("Copying files in RF_RAND_sets.list from "
                + workingdir + " to " + workingdir + "yRandom/");
        BufferedReader in = new BufferedReader(new FileReader(workingdir
                + "RF_RAND_sets.list"));
        String inputString;
        while ((inputString = in.readLine()) != null
                && !inputString.equals("")) {
            if (!inputString.contains("#")) {
                String[] data = inputString.split("\\s+");
                String[] files = new String[4];
                files[0] = data[0];
                files[1] = data[1];
                files[2] = data[3];
                files[3] = data[4];

                for (int i = 0; i < files.length; i++) {
                    if (new File(fromDir + files[i]).exists()) {
                        FileAndDirOperations.copyFile(fromDir + files[i],
                                toDir + files[i]);
                    }
                }
            }
        }
        in.close();

        String yRandomDir = Constants.CECCR_USER_BASE_PATH + userName + "/"
                + jobName + "/yRandom/";
        logger.debug("User: " + userName + " Job: " + jobName + " YRandomization");
        File dir = new File(yRandomDir);
        String files[] = dir.list();
        if (files == null) {
            logger.warn("Error reading directory: " + yRandomDir);
        }
        int x = 0;
        logger.debug("Randomizing each activity file (*rand_sets*.a) in dir "
                + yRandomDir);
        while (files != null && x < files.length) {
            if (files[x].matches(".*rand_sets.*a")) {
                // shuffle the values in each .a file (ACT file)
                DatasetFileOperations.randomizeActivityFile(yRandomDir
                        + files[x], yRandomDir + files[x]);
            }
            x++;
        }

    }

    public static void
    makeRandomForestXFiles(String scalingType, String workingDir) throws Exception {
        // changes the usual .x files to random forest versions.

        BufferedWriter out = new BufferedWriter(new FileWriter(workingDir
                + "RF_RAND_sets.list"));
        BufferedReader in = new BufferedReader(new FileReader(workingDir
                + "RAND_sets.list"));
        String inputString;
        while ((inputString = in.readLine()) != null
                && !inputString.equals("")) {
            if (!inputString.contains("#")) {
                String[] data = inputString.split("\\s+");
                preProcessXFile(scalingType, data[0], "RF_" + data[0],
                        workingDir);
                preProcessXFile(scalingType, data[3], "RF_" + data[3],
                        workingDir);
                out.write(inputString.replace(data[0], "RF_" + data[0])
                        .replace(data[3], "RF_" + data[3])
                        + System.getProperty("line.separator"));
            }
        }
        in.close();
        out.flush();
        out.close();

        // fix external set .x file too
        String newExternalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
        preProcessXFile(scalingType, Constants.EXTERNAL_SET_X_FILE,
                newExternalXFile, workingDir);
        String newModelingXFile = "RF_" + Constants.MODELING_SET_X_FILE;
        preProcessXFile(scalingType, Constants.MODELING_SET_X_FILE,
                newModelingXFile, workingDir);

    }

    public static void
    buildRandomForestModels(RandomForestParameters randomForestParameters,
                            String actFileDataType,
                            String scalingType,
                            String categoryWeights,
                            String workingDir,
                            String jobName) throws Exception {

        String command = "";
        logger.debug("Running Random Forest Modeling...");

        String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
        String buildModelScript = scriptDir
                + Constants.RF_BUILD_MODEL_RSCRIPT;

        // build model script parameter
        String type = actFileDataType.equals(Constants.CATEGORY) ? "classification"
                : "regression";
        String ntree = randomForestParameters.getNumTrees().trim();

        /*
         * FIXME mtry & nodesize currently disabled because defaults should be
         * used in most cases instead. Eventually, add these to an "advanced
         * options" section in the RF modeling form and then conditionally set
         * them to form values, if those values are not null.
         *
         * XXX that change needs to be made in randomForestBuildModel.R as well!
         *
        //String nodesize = randomForestParameters.getMinTerminalNodeSize();
        String mtry = randomForestParameters.getDescriptorsPerTree();
        if (mtry != null) {
            mtry = mtry.trim();
        }
        */
        //String nodesize = randomForestParameters.getMinTerminalNodeSize();

        // String classwt = categoryWeights;
        String classwt = "NULL";

        //String nodesize = randomForestParameters.getMinTerminalNodeSize();
        String maxnodes = randomForestParameters.getMaxNumTerminalNodes();

        String externalXFile = "RF_" + Constants.EXTERNAL_SET_X_FILE;
        if (DatasetFileOperations.getXCompoundNames(
                workingDir + "RF_" + Constants.EXTERNAL_SET_X_FILE).size() == 0) {
            // Random Forest will not run without a non-empty x file.
            // (facepalm)
            // workaround: use the training set X file in this case. The
            // external
            // prediction results will be ignored.
            externalXFile = "RF_" + Constants.MODELING_SET_X_FILE;
        }

        if (maxnodes.equals("0")) {
            maxnodes = "NULL";
        }
        command = "Rscript --vanilla " + buildModelScript + " --scriptsDir "
                + scriptDir + " --workDir " + workingDir
                + " --externalXFile " + externalXFile
                + " --dataSplitsListFile " + "RF_RAND_sets.list" + " --type "
                + type + " --ntree " + ntree
                + " --classwt " + classwt
                + " --maxnodes " + maxnodes;
        /*
         * FIXME disabled until default-if-not-provided is implemented
        if (mtry != null) {
            command += " --mtry" + mtry;
        }
        if (nodesize != null) {
            command += " --nodesize " + nodesize;
        }
        */

        RunExternalProgram.runCommandAndLogOutput(command, workingDir,
                "randomForestBuildModel");
    }

    public static List<ExternalValidation>
    readExternalSetPredictionOutput(String workingDir,
                                    Predictor predictor) throws Exception {
        // note that in Random Forest, making external predictions is done
        // automatically
        // as part of the modeling process.

        List<ExternalValidation> allExternalValues = Lists.newArrayList();
        BufferedReader in = new BufferedReader(new FileReader(workingDir
                + Constants.EXTERNAL_SET_A_FILE));
        String inputString;

        while ((inputString = in.readLine()) != null
                && !inputString.equals("")) {
            String data[] = inputString.split("\\s+"); // Note: [0] is the
            // compound name and
            // [1] is the activity
            // value.
            ExternalValidation externalValidationValue = new ExternalValidation();
            externalValidationValue.setPredictorId(predictor.getId());
            externalValidationValue.setCompoundId(data[0]);
            externalValidationValue.setActualValue(new Float(data[1])
                    .floatValue());
            allExternalValues.add(externalValidationValue);
        }
        in.close();

        in = new BufferedReader(new FileReader(workingDir + "RF_"
                + Constants.EXTERNAL_SET_X_FILE.replace(".x", ".pred")));
        inputString = in.readLine(); // header
        for (int i = 0; i < allExternalValues.size(); i++) {
            ExternalValidation externalValidationValue = allExternalValues
                    .get(i);
            inputString = in.readLine();
            if (inputString != null && !inputString.trim().isEmpty()) {

                String[] data = inputString.split("\\s+"); // Note: [0] is the
                // compound name
                // and the
                // following are
                // the predicted
                // values.

                Float[] compoundPredictedValues = new Float[data.length - 1];

                externalValidationValue
                        .setNumModels(compoundPredictedValues.length);

                float sum = 0;
                for (int j = 0; j < compoundPredictedValues.length; j++) {
                    compoundPredictedValues[j] = new Float(data[j + 1]);
                    sum += compoundPredictedValues[j].floatValue();
                }

                float mean = sum / compoundPredictedValues.length;
                externalValidationValue.setPredictedValue((new Float(mean)));

                double sumDistFromMeanSquared = 0.0;
                for (int j = 0; j < compoundPredictedValues.length; j++) {
                    double distFromMean = compoundPredictedValues[j]
                            .doubleValue()
                            - (double) mean;
                    sumDistFromMeanSquared += Math.pow(distFromMean,
                            (double) 2);
                }
                double stdDev = Math.sqrt(sumDistFromMeanSquared
                        / (double) compoundPredictedValues.length);
                externalValidationValue.setStandDev(Utility
                        .roundSignificantFigures(Double.toString(stdDev), 4));
            }
        }

        return allExternalValues;
    }

    public static List<RandomForestGrove>
    readRandomForestGroves(String workingDir,
                           Predictor predictor,
                           String isYRandomModel) throws Exception {
        List<RandomForestGrove> randomForestModels = Lists.newArrayList();

        // read the models list
        BufferedReader in = new BufferedReader(new FileReader(workingDir
                + Constants.RF_DESCRIPTORS_USED_FILE));
        String inputString;
        while ((inputString = in.readLine()) != null
                && !inputString.equals("")) {
            // for each model
            String[] data = inputString.split("\t"); // [0] is the grove name,
            // [1] is the list of
            // descriptors used in
            // this grove
            RandomForestGrove m = new RandomForestGrove();
            m.setPredictorId(predictor.getId());
            m.setName(data[0]);
            if (data.length > 1) { // sometimes R code doesn't print
                // descriptors right. Not a big deal, just
                // move along.
                m.setDescriptorsUsed(data[1]);
            }
            m.setIsYRandomModel(isYRandomModel);
            randomForestModels.add(m);
        }
        in.close();
        return randomForestModels;
    }

    public static List<RandomForestTree>
    readRandomForestTrees(String workingDir,
                          Predictor predictor,
                          RandomForestGrove grove,
                          String actFileDataType) throws Exception {
        List<RandomForestTree> randomForestTrees = Lists.newArrayList();

        if (actFileDataType.equals(Constants.CONTINUOUS)) {
            List<String> treeFileName = Lists.newArrayList();
            List<String> treeR2 = Lists.newArrayList();
            List<String> treeMse = Lists.newArrayList();
            List<String> treeDescriptorsUsed = Lists.newArrayList();

            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + grove.getName() + "_trees.list"));
            String inputString;
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                treeFileName.add(inputString);
            }
            in.close();

            in = new BufferedReader(new FileReader(workingDir
                    + grove.getName() + ".rsq"));
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                treeR2.add(inputString);
            }
            in.close();

            in = new BufferedReader(new FileReader(workingDir
                    + grove.getName() + ".mse"));
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                treeMse.add(inputString);
            }
            in.close();

            in = new BufferedReader(new FileReader(workingDir
                    + grove.getName() + "_desc_used_in_trees.txt"));
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                treeDescriptorsUsed.add(inputString);
            }
            in.close();

            // for each tree
            for (int i = 0; i < treeFileName.size(); i++) {
                RandomForestTree t = new RandomForestTree();
                t.setRandomForestGroveId(grove.getId());
                t.setTreeFileName(treeFileName.get(i));
                t.setR2(Utility.roundSignificantFigures(treeR2.get(i), 4));
                t.setMse(Utility.roundSignificantFigures(treeMse.get(i), 4));
                t.setDescriptorsUsed(treeDescriptorsUsed.get(i));
                randomForestTrees.add(t);
            }
        } else {
            List<String> treeFileName = Lists.newArrayList();
            List<String> treeDescriptorsUsed = Lists.newArrayList();
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + grove.getName() + "_trees.list"));
            String inputString;
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                treeFileName.add(inputString);
            }
            in.close();

            in = new BufferedReader(new FileReader(workingDir
                    + grove.getName() + "_desc_used_in_trees.txt"));
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                treeDescriptorsUsed.add(inputString);
            }
            in.close();

            // for each tree
            for (int i = 0; i < treeFileName.size(); i++) {
                RandomForestTree t = new RandomForestTree();
                t.setRandomForestGroveId(grove.getId());
                t.setTreeFileName(treeFileName.get(i));
                if (i < treeDescriptorsUsed.size()) { // if no descriptors,
                    // not a big deal
                    t.setDescriptorsUsed(treeDescriptorsUsed.get(i));
                }
                randomForestTrees.add(t);
            }
        }
        return randomForestTrees;
    }

    public static void cleanUpExcessFiles(String workingDir) {
        // remove the training and test set .x files; they are no longer
        // needed and take up lots of space

        try {
            // open RF_RAND_sets.list and remove the .x files listed in it
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + "RF_RAND_sets.list"));

            // sample line:
            // RF_rand_sets_39_trn0.x rand_sets_39_trn0.a 37
            // RF_rand_sets_39_tst0.x rand_sets_39_tst0.a 13

            String inputString;
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                if (!inputString.contains("#")) {
                    String[] files = inputString.split("\\s+");
                    for (int i = 0; i < files.length; i++) {
                        // remove RF_rand_sets.*.x
                        if (files[i].endsWith("x")
                                && new File(workingDir + files[i]).exists()) {
                            FileAndDirOperations.deleteFile(workingDir
                                    + files[i]);
                        }
                        // remove rand_sets.*.x
                        if (files[i].length() > 3) {
                            files[i] = files[i].substring(3);
                            if (files[i].endsWith("x")
                                    && new File(workingDir + files[i])
                                    .exists()) {
                                FileAndDirOperations.deleteFile(workingDir
                                        + files[i]);
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    // END MODELING WORKFLOW FUNCTIONS

    // PREDICTION WORKFLOW FUNCTIONS
    public static void
    runRandomForestPrediction(String workingDir,
                              String jobName,
                              String sdfile,
                              Predictor predictor) throws Exception {
        String xFile = sdfile + ".renorm.x";
        String newXFile = "RF_" + xFile;
        preProcessXFile(predictor.getScalingType(), xFile, newXFile,
                workingDir);
        FileAndDirOperations.deleteFile(workingDir + xFile);

        String scriptDir = Constants.CECCR_BASE_PATH + Constants.SCRIPTS_PATH;
        String predictScript = scriptDir + Constants.RF_PREDICT_RSCRIPT;
        String modelsListFile = "models.list";
        String command = "Rscript --vanilla " + predictScript
                + " --scriptsDir " + scriptDir + " --workDir " + workingDir
                + " --modelsListFile " + modelsListFile + " --xFile "
                + newXFile;

        RunExternalProgram.runCommandAndLogOutput(command, workingDir,
                "randomForestPredict");
    }

    public static List<PredictionValue>
    readPredictionOutput(String workingDir, Long predictorId) throws Exception {
        List<PredictionValue> predictionValues = Lists.newArrayList(); // holds
        // objects
        // to
        // be
        // returned

        // Get the predicted values of the forest
        String outputFile = Constants.PRED_OUTPUT_FILE + ".preds";
        logger.debug("Reading consensus prediction file: "
                + workingDir + outputFile);
        BufferedReader in = new BufferedReader(new FileReader(workingDir
                + outputFile));
        String inputString;

        in.readLine(); // first line is the header with the model name
        while ((inputString = in.readLine()) != null
                && !inputString.equals("")) {
            String[] data = inputString.split("\\s+"); // Note: [0] is the
            // compound name and
            // the following are
            // the predicted
            // values.

            PredictionValue p = new PredictionValue();
            p.setPredictorId(predictorId);
            p.setCompoundName(data[0]);

            Float[] compoundPredictedValues = new Float[data.length - 1];
            p.setNumTotalModels(compoundPredictedValues.length);
            p.setNumModelsUsed(compoundPredictedValues.length);
            float sum = 0;
            for (int i = 0; i < compoundPredictedValues.length; i++) {
                compoundPredictedValues[i] = new Float(data[i + 1]);
                sum += compoundPredictedValues[i].floatValue();
            }
            float mean = sum / compoundPredictedValues.length;
            p.setPredictedValue((new Float(mean)));

            double sumDistFromMeanSquared = 0.0;
            for (int i = 0; i < compoundPredictedValues.length; i++) {
                double distFromMean = compoundPredictedValues[i]
                        .doubleValue()
                        - (double) mean;
                sumDistFromMeanSquared += Math.pow(distFromMean, (double) 2);
            }
            double stdDev = Math.sqrt(sumDistFromMeanSquared
                    / (double) compoundPredictedValues.length);
            p.setStandardDeviation(new Float(stdDev));

            predictionValues.add(p);
        }
        in.close();

        return predictionValues;
    }

    // END PREDICTION WORKFLOW FUNCTIONS

    // HELPER FUNCTIONS
    public static void preProcessXFile(String scalingType,
                                       String xFile,
                                       String newXFile,
                                       String workingDir) throws Exception {
        String logString = String.format(
                "Preprocessing X file: SCALING=%s, OLD=%s, NEW=%s, DIR=%s",
                scalingType, xFile, newXFile, workingDir);
        logger.debug(logString);

        // if scaling was applied, the last 2 lines of a .x file will contain
        // the scaling ranges.
        // Random Forest can't deal with these last 2 lines, so they must be
        // removed.
        // Also, descriptor names containing "#" character will break Random
        // Forest, so these
        // are changed to "=_" instead.

        if (!new File(workingDir + xFile).exists()) {
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(workingDir
                + xFile));
        List<String> lines = Lists.newArrayList();

        // replace "#" with "=_"
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line.replaceAll("#", "=_"));
        }
        br.close();

        BufferedWriter out = new BufferedWriter(new FileWriter(workingDir
                + newXFile));
        for (int i = 0; i < lines.size(); i++) {
            // write out all but the last two lines UNLESS no scaling occurred
            if (i < lines.size() - 2 || scalingType.equals("NOSCALING")) {
                out.write(lines.get(i));
                out.newLine();
            }
        }
        out.close();
    }

    // END HELPER FUNCTIONS

}
