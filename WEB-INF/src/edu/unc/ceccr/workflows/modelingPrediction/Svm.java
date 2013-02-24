package edu.unc.ceccr.workflows.modelingPrediction;


import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.LsfOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.global.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Svm
{
    private static Logger logger 
                = Logger.getLogger(Svm.class.getName());
    
    public static void convertXtoSvm(String xFileName,
                                     String aFileName,
                                     String workingDir) throws Exception
    {
        // generates an SVM-compatible input descriptor file
        // logger.debug("Generating an SVM-compatible file: " +
        // xFileName + " + " + aFileName + " => " + xFileName.replace(".x",
        // ".svm"));

        ArrayList<String> activityValues = new ArrayList<String>();
        if (aFileName != null && !aFileName.isEmpty()) {
            // read in the activity file

            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + aFileName));
            String inputString;
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                // [0] is the compound id, [1] is the activity value
                String[] data = inputString.split("\\s+"); 
                activityValues.add(data[1]);
            }
            in.close();
        }
        else {
            // if no activity file is supplied, just use zeros for activities
            int numCompounds = DatasetFileOperations.getXCompoundNames(
                    workingDir + xFileName).size();
            if (xFileName.contains("ext_0")) {
                logger.debug("found " + numCompounds
                        + " compounds in ext_0.x");
            }
            for (int i = 0; i < numCompounds; i++) {
                activityValues.add("0");
            }
        }

        // read in x file and translate to svm file, adding activity values
        // along the way
        BufferedReader in = new BufferedReader(new FileReader(workingDir
                + xFileName));
        BufferedWriter out = new BufferedWriter(new FileWriter(workingDir
                + xFileName.replace(".x", ".svm")));
        StringBuilder sb = new StringBuilder();

        in.readLine(); // header
        in.readLine(); // header

        String inputString;
        for (int i = 0; i < activityValues.size(); i++) {
            sb.append(activityValues.get(i));
            inputString = in.readLine();
            String[] data = inputString.split("\\s+"); // [0] and [1] are id
            for (int j = 2; j < data.length; j++) {
                sb.append(" " + (j - 1) + ":" + data[j]);
            }
            sb.append(System.getProperty("line.separator"));
            out.write(sb.toString());
            sb.delete(0, sb.length());
        }
        in.close();
        out.flush();
        out.close();
    }

    public static void writeSvmModelingParamsFile(
                                       SvmParameters svmParameters,
                                       String actFileDataType,
                                       String paramFilePath,
                                       String workingDir) throws Exception
    {
        // fix any "step" parameters; they must be >0 or python will blow up
        if (Double.parseDouble(svmParameters.getSvmCostStep()) <= 0) {
            svmParameters.setSvmCostStep("1");
        }
        if (Double.parseDouble(svmParameters.getSvmDegreeStep()) <= 0) {
            svmParameters.setSvmDegreeStep("1");
        }
        if (Double.parseDouble(svmParameters.getSvmGammaStep()) <= 0) {
            svmParameters.setSvmGammaStep("1");
        }
        if (Double.parseDouble(svmParameters.getSvmPEpsilonStep()) <= 0) {
            svmParameters.setSvmPEpsilonStep("1");
        }
        if (Double.parseDouble(svmParameters.getSvmNuStep()) <= 0) {
            svmParameters.setSvmNuStep("1");
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(paramFilePath));

        String svmType = "";
        if (actFileDataType.equals(Constants.CATEGORY)) {
            svmType = svmParameters.getSvmTypeCategory();
        }
        else {
            svmType = svmParameters.getSvmTypeContinuous();
        }

        out.write("list-file: " + "RAND_sets.list" + "\n");
        out.write("activity-type: " + actFileDataType + "\n");
        out.write("modeling-dir: " + workingDir + "\n");
        out.write("y-random-dir: " + workingDir + "yRandom/" + "\n");

        // basic parameters
        out.write("svm-type: " + svmType + "\n");
        out.write("kernel-type: " + svmParameters.getSvmKernel() + "\n");

        out.write("shrinking-heuristics: " + svmParameters.getSvmHeuristics()
                + "\n");
        out.write("use-probability-heuristics: "
                + svmParameters.getSvmProbability() + "\n");
        out.write("c-svc-weight: " + svmParameters.getSvmWeight() + "\n");
        out.write("num-cross-validation-folds: "
                + svmParameters.getSvmCrossValidation() + "\n");
        out.write("tolerance-for-termination: "
                + svmParameters.getSvmEEpsilon() + "\n");

        // loop parameters
        out.write("cost-from: " + svmParameters.getSvmCostFrom() + "\n");
        out.write("cost-to: " + svmParameters.getSvmCostTo() + "\n");
        out.write("cost-step: " + svmParameters.getSvmCostStep() + "\n");

        out.write("gamma-from: " + svmParameters.getSvmGammaFrom() + "\n");
        out.write("gamma-to: " + svmParameters.getSvmGammaTo() + "\n");
        out.write("gamma-step: " + svmParameters.getSvmGammaStep() + "\n");

        out.write("degree-from: " + svmParameters.getSvmDegreeFrom() + "\n");
        out.write("degree-to: " + svmParameters.getSvmDegreeTo() + "\n");
        out.write("degree-step: " + svmParameters.getSvmDegreeStep() + "\n");

        out.write("nu-from: " + svmParameters.getSvmNuFrom() + "\n");
        out.write("nu-to: " + svmParameters.getSvmNuTo() + "\n");
        out.write("nu-step: " + svmParameters.getSvmNuStep() + "\n");

        out.write("loss-epsilon-from: " + svmParameters.getSvmPEpsilonFrom()
                + "\n");
        out.write("loss-epsilon-to: " + svmParameters.getSvmPEpsilonTo()
                + "\n");
        out.write("loss-epsilon-step: " + svmParameters.getSvmPEpsilonStep()
                + "\n");

        // model acceptance parameters
        out.write("model-acceptance-cutoff: " + svmParameters.getSvmCutoff()
                + "\n");

        out.close();
    }

    public static void svmPreProcess(SvmParameters svmParameters,
                                     String actFileDataType,
                                     String workingDir) throws Exception
    {

        if (!workingDir.endsWith("/yRandom/")) {
            convertXtoSvm(Constants.MODELING_SET_X_FILE,
                    Constants.MODELING_SET_A_FILE, workingDir);
            convertXtoSvm(Constants.EXTERNAL_SET_X_FILE,
                    Constants.EXTERNAL_SET_A_FILE, workingDir);
            FileAndDirOperations.copyFile(Constants.CECCR_BASE_PATH
                    + Constants.SCRIPTS_PATH + "svm.py", workingDir
                    + "svm.py");
        }

        // log file containing each model generated and its test set r^2 or
        // CCR
        // used for debugging and checking progress

        BufferedReader in = new BufferedReader(new FileReader(workingDir
                + "RAND_sets.list"));
        String inputString;
        while ((inputString = in.readLine()) != null
                && !inputString.equals("")) {
            String[] data = inputString.split("\\s+");

            if (!inputString.contains("#")) {
                convertXtoSvm(data[0], data[1], workingDir);
                convertXtoSvm(data[3], data[4], workingDir);
            }
        }
        in.close();

    }

    public static void buildSvmModels(String workingDir)
    {
        // run modeling (exec python script)
        String cmd = "python svm.py";
        RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "svm.py");
    }

    public static String buildSvmModelsLsf(String workingDir,
                                           String userName,
                                           String jobName) throws Exception
    {
        // run modeling (bsub the python script)

        String cmd = "";
        if (LsfOperations.patronsQueueHasRoom()) {
            cmd += "bsub -q patrons ";
        }
        else {
            cmd += "bsub -q idle ";
        }

        cmd += "-J cbench_" + userName + "_" + jobName
                + " -o bsubOutput.txt python svm.py";
        RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "svm.py");

        String logFilePath = workingDir + "Logs/svm.py.log";
        return LsfUtilities.getLsfJobId(logFilePath);
    }

    public static ArrayList<SvmModel>
            readSvmModels(String workingDir, String cutoff) throws Exception
    {
        ArrayList<SvmModel> svmModels = new ArrayList<SvmModel>();

        BufferedReader br = new BufferedReader(new FileReader(workingDir
                + "svm-results.txt"));
        String line;
        br.readLine(); // skip header
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                String[] tokens = line.split("\t");
                // Header: "rSquared\t" + "ccr\t" + "MSE\t" + "degree\t" +
                // "gamma\t" + "cost\t" + "nu\t" + "loss (epsilon)"

                boolean isGoodModel = false;
                if (tokens[0] != null && !tokens[0].trim().equals("NA")) {
                    // check cutoff against rSquared
                    if (Double.parseDouble(tokens[0]) >= Double
                            .parseDouble(cutoff)) {
                        isGoodModel = true;
                    }
                }
                else if (tokens[1] != null && !tokens[1].trim().equals("NA")) {
                    // check cutoff against CCR
                    if (Double.parseDouble(tokens[1]) >= Double
                            .parseDouble(cutoff)) {
                        isGoodModel = true;
                    }
                }

                if (isGoodModel) {
                    SvmModel svmModel = new SvmModel();
                    svmModel.setrSquaredTest(tokens[0]);
                    svmModel.setCcrTest(tokens[1]);
                    svmModel.setMseTest(tokens[2]);
                    svmModel.setDegree(tokens[3]);
                    svmModel.setGamma(tokens[4]);
                    svmModel.setCost(tokens[5]);
                    svmModel.setNu(tokens[6]);
                    svmModel.setLoss(tokens[7]);
                    if (workingDir.endsWith("/yRandom/")) {
                        svmModel.setIsYRandomModel(Constants.YES);
                    }
                    else {
                        svmModel.setIsYRandomModel(Constants.NO);
                    }

                    svmModels.add(svmModel);
                }
                else {
                    // logger.debug("Bad model. rSq: " + tokens[0] +
                    // " ccr: " + tokens[1]);
                }
            }
        }
        br.close();

        return svmModels;
    }

    public static void cleanExcessFilesFromDir(String workingDir)
    {

        try {
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + "RAND_sets.list"));
            String inputString;
            while ((inputString = in.readLine()) != null
                    && !inputString.equals("")) {
                if (!inputString.contains("#")) {
                    String[] data = inputString.split("\\s+");

                    FileAndDirOperations.deleteFile(workingDir + data[0]);
                    FileAndDirOperations.deleteFile(workingDir + data[1]);
                    FileAndDirOperations.deleteFile(workingDir + data[3]);
                    FileAndDirOperations.deleteFile(workingDir + data[4]);

                    FileAndDirOperations.deleteFile(workingDir
                            + data[0].replace(".x", ".svm"));
                    FileAndDirOperations.deleteFile(workingDir
                            + data[3].replace(".x", ".svm"));
                }
            }
            in.close();
        }
        catch (Exception ex) {
            logger.error(ex);
        }
    }

    public static void
    runSvmPrediction(String workingDir, String predictionXFileName) 
                                                   throws Exception
    {
        // find all models files in working dir
        // run svm-predict on the prediction file using each model
        // average the results

        convertXtoSvm(predictionXFileName, "", workingDir);

        String predictionFileName = predictionXFileName.replace(".x", ".svm");

        File dir = new File(workingDir);
        String[] files = dir.list(new FilenameFilter()
        {
            public boolean accept(File arg0, String arg1)
            {
                return arg1.endsWith(".mod");
            }
        });
        for (int i = 0; i < files.length; i++) {
            String command = "svm-predict " + predictionFileName + " "
                    + files[i] + " " + files[i] + ".pred";
            RunExternalProgram.runCommandAndLogOutput(command, workingDir,
                    "svm-predict-" + files[i]);
        }
    }

    public static ArrayList<PredictionValue>
    readPredictionOutput(String workingDir,
                                 String predictionXFileName,
                                 Long predictorId) throws Exception
    {
        ArrayList<PredictionValue> predictionValues 
                                        = new ArrayList<PredictionValue>();

        ArrayList<String> compoundNames = DatasetFileOperations
                .getXCompoundNames(workingDir + predictionXFileName);

        for (int i = 0; i < compoundNames.size(); i++) {
            PredictionValue pv = new PredictionValue();
            pv.setCompoundName(compoundNames.get(i));
            pv.setPredictedValue(new Float(0.0));
            pv.setPredictorId(predictorId);
            predictionValues.add(pv);
        }

        File dir = new File(workingDir);
        String[] files = dir.list(new FilenameFilter()
        {
            public boolean accept(File arg0, String arg1)
            {
                return arg1.endsWith(".pred");
            }
        });
        for (int i = 0; i < files.length; i++) {
            // open the prediction file and get the results for each compound.
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + files[i]));
            String line;
            int j = 0;
            while ((line = in.readLine()) != null) {
                if (!line.isEmpty()) {
                    predictionValues.get(j).setPredictedValue(
                            Float.parseFloat(line.trim())
                                    + predictionValues.get(j)
                                            .getPredictedValue());
                    j++;
                }
            }
            in.close();
        }
        // Each predictionValue contains the sum of all predicted values.
        // We need the average, so divide each value by numModels.
        for (PredictionValue pv : predictionValues) {
            if (files.length > 0) {
                pv.setPredictedValue(pv.getPredictedValue() / files.length);
            }
            pv.setNumModelsUsed(files.length);
            pv.setNumTotalModels(files.length);
            pv.setStandardDeviation(new Float(0.1)); // calculate this later
                                                     // once other stuff works
        }

        return predictionValues;
    }

    public static ArrayList<ExternalValidation>
    readExternalPredictionOutput(String workingDir, Long predictorId) 
                                                            throws Exception
    {
        ArrayList<ExternalValidation> externalPredictions 
                                         = new ArrayList<ExternalValidation>();

        // set compound names
        String line;
        BufferedReader br = new BufferedReader(new FileReader(workingDir
                + "ext_0.a"));
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                String[] tokens = line.split("\\s+");
                ExternalValidation ev = new ExternalValidation();
                ev.setCompoundId(tokens[0]);
                ev.setActualValue(Float.parseFloat(tokens[1]));
                externalPredictions.add(ev);
            }
        }
        br.close();
        File dir = new File(workingDir);
        String[] files = dir.list(new FilenameFilter()
        {
            public boolean accept(File arg0, String arg1)
            {
                return arg1.endsWith(".pred");
            }
        });
        for (int i = 0; i < files.length; i++) {
            // open the prediction file and get the results for each compound.
            BufferedReader in = new BufferedReader(new FileReader(workingDir
                    + files[i]));
            int j = 0;
            while ((line = in.readLine()) != null) {
                if (!line.isEmpty()) {
                    externalPredictions.get(j).setPredictedValue(
                            Float.parseFloat(line.trim())
                                    + externalPredictions.get(j)
                                            .getPredictedValue());
                    j++;
                }
            }
            // This is the last time we'll need the external prediction
            // output. Delete it.
            FileAndDirOperations.deleteFile(workingDir + files[i]);
            in.close();
        }

        // Each predictionValue contains the sum of all predicted values.
        // We need the average, so divide each value by numModels.
        // set the predictor ID at the same time
        for (ExternalValidation pv : externalPredictions) {
            if (files.length > 0) {
                pv.setPredictedValue(pv.getPredictedValue() / files.length);
            }
            pv.setNumModels(files.length);
            pv.setStandDev("0.1");
            pv.setPredictorId(predictorId);
        }

        return externalPredictions;
    }
}