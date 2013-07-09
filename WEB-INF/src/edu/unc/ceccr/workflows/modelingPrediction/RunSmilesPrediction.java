package edu.unc.ceccr.workflows.modelingPrediction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.Descriptors;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.workflows.datasets.StandardizeMolecules;
import edu.unc.ceccr.workflows.descriptors.GenerateDescriptors;
import edu.unc.ceccr.workflows.descriptors.ReadDescriptors;
import edu.unc.ceccr.workflows.descriptors.WriteDescriptors;
import edu.unc.ceccr.workflows.utilities.CopyJobFiles;

public class RunSmilesPrediction
{
    private static Logger logger = Logger.getLogger(RunSmilesPrediction.class
                                         .getName());

    public static String[] PredictSmilesSDF(String workingDir,
                                            String username,
                                            Predictor predictor,
                                            Float cutoff) throws Exception
    {

        String sdfile = workingDir + "smiles.sdf";
        logger.debug("Running PredictSmilesSDF in dir " + workingDir);

        /* copy the predictor to the workingDir. */
        String predictorUsername = predictor.getUserName();
        predictorUsername = "all-users";
        String fromDir = Constants.CECCR_USER_BASE_PATH + predictorUsername
                + "/PREDICTORS/" + predictor.getName() + "/";

        /* get train_0.x file from the predictor dir. */
        logger.debug("Copying predictor files from " + fromDir);
        CopyJobFiles.getPredictorFiles(username, predictor, workingDir);

        logger.debug("Copying complete. Generating descriptors. ");

        /* create the descriptors for the chemical and read them in */
        ArrayList<String> descriptorNames = new ArrayList<String>();
        ArrayList<Descriptors> descriptorValueMatrix = new ArrayList<Descriptors>();
        ArrayList<String> chemicalNames = DatasetFileOperations
                .getSDFCompoundNames(sdfile);

        if (predictor.getDescriptorGeneration().equals(Constants.MOLCONNZ)) {
            GenerateDescriptors.GenerateMolconnZDescriptors(sdfile, sdfile
                    + ".molconnz");
            ReadDescriptors.readMolconnZDescriptors(sdfile + ".molconnz",
                    descriptorNames, descriptorValueMatrix);
        }
        else if (predictor.getDescriptorGeneration().equals(Constants.CDK)) {
            GenerateDescriptors.GenerateCDKDescriptors(sdfile, sdfile
                    + ".cdk");

            ReadDescriptors.convertCDKToX(sdfile + ".cdk", workingDir);
            ReadDescriptors.readXDescriptors(sdfile + ".cdk.x",
                    descriptorNames, descriptorValueMatrix);
        }
        else if (predictor.getDescriptorGeneration()
                .equals(Constants.DRAGONH)) {
            GenerateDescriptors.GenerateHExplicitDragonDescriptors(sdfile,
                    sdfile + ".dragonH");
            ReadDescriptors.readDragonDescriptors(sdfile + ".dragonH",
                    descriptorNames, descriptorValueMatrix);
        }
        else if (predictor.getDescriptorGeneration().equals(
                Constants.DRAGONNOH)) {
            GenerateDescriptors.GenerateHExplicitDragonDescriptors(sdfile,
                    sdfile + ".dragonNoH");
            ReadDescriptors.readDragonDescriptors(sdfile + ".dragonNoH",
                    descriptorNames, descriptorValueMatrix);
        }
        else if (predictor.getDescriptorGeneration().equals(Constants.MOE2D)) {
            GenerateDescriptors.GenerateMoe2DDescriptors(sdfile, sdfile
                    + ".moe2D");
            ReadDescriptors.readMoe2DDescriptors(sdfile + ".moe2D",
                    descriptorNames, descriptorValueMatrix);
        }
        else if (predictor.getDescriptorGeneration().equals(Constants.MACCS)) {
            GenerateDescriptors.GenerateMaccsDescriptors(sdfile, sdfile
                    + ".maccs");
            ReadDescriptors.readMaccsDescriptors(sdfile + ".maccs",
                    descriptorNames, descriptorValueMatrix);
        }

        logger.debug("Normalizing descriptors to fit predictor.");

        String descriptorString = Utility
                .StringArrayListToString(descriptorNames);
        WriteDescriptors.writePredictionXFile(chemicalNames,
                descriptorValueMatrix, descriptorString,
                sdfile + ".renorm.x", workingDir + "train_0.x", predictor
                        .getScalingType());

        /* read prediction output */
        ArrayList<String> predValueArray = new ArrayList<String>();
        if (predictor.getModelMethod().equals(Constants.KNNGA)
                || predictor.getModelMethod().equals(Constants.KNNSA)
                || predictor.getModelMethod().equals(Constants.KNN)) {

            /*
             * write a dummy .a file because knn+ needs it or it fails
             * bizarrely... X_X
             */
            String actfile = sdfile + ".renorm.a";

            BufferedWriter aout = new BufferedWriter(new FileWriter(actfile));
            aout.write("1 0");
            aout.close();

            // Run prediction
            logger.debug("Running prediction.");
            String preddir = workingDir;

            String execstr = "";
            if (predictor.getModelMethod().equals(Constants.KNN)) {
                execstr = "knn+ knn-output.list -4PRED="
                        + "smiles.sdf.renorm.x" + " -AD=" + cutoff
                        + "_avd -OUT=" + Constants.PRED_OUTPUT_FILE;
            }
            else if (predictor.getModelMethod().equals(Constants.KNNGA)
                    || predictor.getModelMethod().equals(Constants.KNNSA)) {
                execstr = "knn+ models.tbl -4PRED=" + "smiles.sdf.renorm.x"
                        + " -AD=" + cutoff + "_avd -OUT="
                        + Constants.PRED_OUTPUT_FILE;
            }

            RunExternalProgram.runCommandAndLogOutput(execstr, preddir,
                    "runSmilesPrediction");

            String outputFile = Constants.PRED_OUTPUT_FILE
                    + "_vs_smiles.sdf.renorm.preds";
            logger.debug("Reading file: " + workingDir + outputFile);
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + outputFile));
            String inputString;

            /* Skip the first four lines (header data) */
            in.readLine();
            in.readLine();
            in.readLine();
            in.readLine();

            /* get output for each model */
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                String[] predValues = inputString.split("\\s+");
                if (predValues != null && predValues.length > 2
                        && !predValues[2].equals("NA")) {
                    // logger.debug(predValues[1] + " " + predValues[2]);
                    predValueArray.add(predValues[2]);
                }
            }
            in.close();
            logger.debug("numModels: " + predValueArray.size());
        }
        else if (predictor.getModelMethod().equals(Constants.RANDOMFOREST)) {
            // run prediction
            String xFile = "smiles.sdf.renorm.x";
            String newXFile = "RF_" + xFile;
            RandomForest.preProcessXFile(predictor.getScalingType(), xFile,
                    newXFile, workingDir);

            String scriptDir = Constants.CECCR_BASE_PATH
                    + Constants.SCRIPTS_PATH;
            String predictScript = scriptDir + Constants.RF_PREDICT_RSCRIPT;
            String modelsListFile = "models.list";
            String command = "Rscript --vanilla " + predictScript
                    + " --scriptsDir " + scriptDir + " --workDir "
                    + workingDir + " --modelsListFile " + modelsListFile
                    + " --xFile " + newXFile;

            RunExternalProgram.runCommandAndLogOutput(command, workingDir,
                    "randomForestPredict");

            // get output
            String outputFile = Constants.PRED_OUTPUT_FILE + ".preds";
            logger.debug("Reading consensus prediction file: " + workingDir
                    + outputFile);
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + outputFile));
            String inputString;
            /* first line is the header with the model names */
            in.readLine();
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                /*
                 * Note: [0] is the compound name and the following are the
                 * predicted values.
                 */
                String[] data = inputString.split("\\s+");

                for (int i = 1; i < data.length; i++) {
                    predValueArray.add(data[i]);
                }
            }
            in.close();
        }
        else if (predictor.getModelMethod().equals(Constants.SVM)) {
            // TODO "smiles.sdf" + ".renorm.x" filepath is a magic string --
            // codify or parameterize this somewhere
            File xFile = new File(workingDir, "smiles.sdf.renorm.x");
            logger.info(String.format(
                    "running SMILES prediction: FILE=%s, METHOD=%s", xFile
                            .getAbsolutePath(), Constants.SVM));
            if (!xFile.exists()) {
                logger.warn(String.format(
                        "X file doesn't exist: LOCATION=%s", xFile
                                .getAbsolutePath()));
            }
            else {
                Svm.runSvmPrediction(workingDir, xFile.getName());
                logger.info(String.format(
                        "reading predicted values: FILE=%s, METHOD=%s", xFile
                                .getAbsolutePath(), Constants.SVM));
                // since we have common mean/stddev calculation code in
                // RunSmilesPrediction, we'll parse the files ourselves
                // TODO prediction output file extension ".pred" should be a
                // const
                File dir = new File(workingDir);
                String[] predictionFiles = dir.list(new FilenameFilter()
                {
                    public boolean accept(File arg0, String arg1)
                    {
                        return arg1.endsWith(".pred");
                    }
                });
                for (String filename : predictionFiles) {
                    String filepath = new File(workingDir, filename)
                            .getAbsolutePath();
                    try {
                        BufferedReader br = new BufferedReader(
                                new FileReader(filepath));
                        // because this is a SMILES prediction,
                        // there should only be one line of prediction output
                        String predLine = br.readLine();
                        if (predLine == null || predLine.isEmpty()) {
                            logger.warn(String
                                    .format("prediction line was null or empty, FILE=%s",
                                            filepath));
                        }
                        else {
                            predLine.trim();
                            logger.info(String.format(
                                    "adding prediction: VALUE=%s, FILE=%s",
                                    predLine, filepath));
                            predValueArray.add(predLine);

                            // assert that there's nothing more in the file
                            String secondLine = br.readLine();
                            if (secondLine != null && !secondLine.isEmpty()) {
                                logger.warn(String
                                        .format("unexpected prediction: VALUE=%s, FILE=%s",
                                                secondLine.trim(), filepath));
                            }
                        }
                        br.close();
                    }
                    catch (IOException e) {
                        logger.warn(String.format(
                                "couldn't read prediction file: FILE=%s",
                                filepath), e);
                    }
                }
            }
        }
        else {
            // unsupported modeling type
            String logString = String.format(
                    "Model method not recognized: PREDICTOR=%s, METHOD=%s",
                    predictor.getName(), predictor.getModelMethod());
            logger.warn(logString);
        }

        /* calculate stddev */
        double sum = 0;
        double mean = 0;
        if (predValueArray.size() > 0) {
            for (String predValue : predValueArray) {
                sum += Float.parseFloat(predValue);
            }
            mean = sum / predValueArray.size();
        }

        double stddev = 0;
        if (predValueArray.size() > 1) {
            for (String predValue : predValueArray) {
                double distFromMeanSquared = Math.pow((Double
                        .parseDouble(predValue) - mean), 2);
                stddev += distFromMeanSquared;
            }
            /* divide sum then take sqrt to get stddev */
            stddev = Math.sqrt(stddev / predValueArray.size());
        }

        logger.debug("prediction: " + mean);
        logger.debug("stddev: " + stddev);

        /* format numbers nicely and return them */
        String[] prediction = new String[3];
        prediction[0] = "" + predValueArray.size();
        if (predValueArray.size() > 0) {
            String predictedValue = DecimalFormat.getInstance().format(mean)
                    .replaceAll(",", "");
            logger.debug("String-formatted prediction: " + predictedValue);
            predictedValue = (Utility.roundSignificantFigures(predictedValue,
                    Constants.REPORTED_SIGNIFICANT_FIGURES));
            prediction[1] = predictedValue;
        }
        else {
            prediction[1] = "N/A";
            if (predictor.getModelMethod().equals(Constants.KNNGA)
                    || predictor.getModelMethod().equals(Constants.KNNSA)
                    || predictor.getModelMethod().equals(Constants.KNN)) {
                prediction[1] += "- Cutoff Too Low";
            }
        }
        if (predValueArray.size() > 1) {
            String stdDevStr = DecimalFormat.getInstance().format(stddev)
                    .replaceAll(",", "");
            logger.debug("String-formatted stddev: " + stdDevStr);
            stdDevStr = (Utility.roundSignificantFigures(stdDevStr,
                    Constants.REPORTED_SIGNIFICANT_FIGURES));
            prediction[2] = stdDevStr;
        }
        else {
            prediction[2] = "N/A";
        }

        return prediction;
    }

    public static void
            smilesToSDF(String smiles, String smilesDir) throws Exception
    {
        /*
         * takes in a SMILES string and produces an SDF file from it. Returns
         * the file path as a string.
         */

        logger.debug("Running smilesToSDF with SMILES: " + smiles);

        /* set up the directory, just in case it's not there yet. */
        File dir = new File(smilesDir);
        dir.mkdirs();

        /* write SMILES string to file */
        FileWriter fstream = new FileWriter(smilesDir + "tmp.smiles");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(smiles + " 1");
        out.close();

        String sdfFileName = "smiles.sdf";

        /* execute molconvert to change it to SDF */
        String execstr = "molconvert sdf " + smilesDir + "tmp.smiles -o "
                + smilesDir + sdfFileName;

        RunExternalProgram.runCommandAndLogOutput(execstr, smilesDir,
                "molconvert");

        // standardize the SDF
        StandardizeMolecules.standardizeSdf(sdfFileName, sdfFileName
                + ".standardize", smilesDir);
        File standardized = new File(smilesDir + sdfFileName + ".standardize");
        if (standardized.exists()) {
            /* replace old SDF with new standardized SDF */
            FileAndDirOperations.copyFile(smilesDir + sdfFileName
                    + ".standardize", smilesDir + sdfFileName);
            FileAndDirOperations.deleteFile(smilesDir + sdfFileName
                    + ".standardize");
        }

        logger.debug("Finished smilesToSDF");
    }
}
