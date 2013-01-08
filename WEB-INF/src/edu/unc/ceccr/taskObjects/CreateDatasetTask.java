package edu.unc.ceccr.taskObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.workflows.datasets.StandardizeMolecules;
import edu.unc.ceccr.workflows.descriptors.CheckDescriptors;
import edu.unc.ceccr.workflows.descriptors.GenerateDescriptors;
import edu.unc.ceccr.workflows.modelingPrediction.DataSplit;
import edu.unc.ceccr.workflows.visualization.HeatmapAndPCA;
import edu.unc.ceccr.workflows.visualization.SdfToJpg;

public class CreateDatasetTask extends WorkflowTask
{

    private String  userName             = null;
    private String  datasetType;
    private String  sdfFileName;
    private String  actFileName;
    private String  xFileName;
    private String  descriptorType;
    private String  actFileDataType;
    private String  standardize;
    private String  splitType;
    private String  hasBeenScaled;
    private String  numExternalCompounds;
    private String  numExternalFolds;
    private String  useActivityBinning;
    private String  externalCompoundList;
    private String  jobName              = null;
    private String  paperReference;
    private String  dataSetDescription;
    private String  actFileHeader;
    private String  availableDescriptors = "";
    private String  generateMahalanobis;
    private int     numCompounds;
    private DataSet dataset;                               // contains pretty
                                                            // much all the
                                                            // member
                                                            // variables. This
                                                            // is dumb but
                                                            // hopefully
                                                            // temporary.

    private String  step                 = Constants.SETUP; // stores what
                                                            // step we're on

    public String getProgress(String userName)
    {
        String percent = "";

        if (step.equals(Constants.SKETCHES)) {
            // count the number of *.jpg files in the working directory

            // Since we're generating images using milconvert script we don't
            // need to display that progress as it's really quick
            /*
             * String workingDir = ""; if(jobList.equals(Constants.LSF)){ }
             * else{ workingDir = Constants.CECCR_USER_BASE_PATH + userName +
             * "/DATASETS/" + jobName + "/Visualization/Sketches/"; } float p
             * =
             * FileAndDirOperations.countFilesInDirMatchingPattern(workingDir,
             * ".*jpg"); //divide by the number of compounds in the dataset p
             * /= numCompounds; p *= 100; //it's a percent percent = " (" +
             * Math.round(p) + "%)";
             */
        }

        return step + percent;
    }

    public CreateDatasetTask(DataSet dataset)
    {
        this.dataset = dataset;

        userName = dataset.getUserName();
        jobName = dataset.getName();
        datasetType = dataset.getDatasetType();
        sdfFileName = dataset.getSdfFile();
        actFileName = dataset.getActFile();
        xFileName = dataset.getXFile();
        descriptorType = dataset.getUploadedDescriptorType();
        actFileDataType = dataset.getModelType();
        paperReference = dataset.getPaperReference();
        dataSetDescription = dataset.getDescription();

        standardize = dataset.getStandardize();
        splitType = dataset.getSplitType();
        hasBeenScaled = dataset.getHasBeenScaled();
        numExternalCompounds = dataset.getNumExternalCompounds();
        numExternalFolds = dataset.getNumExternalFolds();
        useActivityBinning = dataset.getUseActivityBinning();
        externalCompoundList = dataset.getExternalCompoundList();

        String path = Constants.CECCR_USER_BASE_PATH + userName
                + "/DATASETS/" + jobName + "/";
        try {
            if (!sdfFileName.equals("")) {
                this.numCompounds = DatasetFileOperations
                        .getSDFCompoundNames(path + sdfFileName).size();
            }
            else if (!xFileName.equals("")) {
                this.numCompounds = DatasetFileOperations.getXCompoundNames(
                        path + xFileName).size();
            }
        }
        catch (Exception ex) {
            Utility.writeToDebug(ex, userName, jobName);
        }
    }

    public CreateDatasetTask(String userName, String datasetType,
            String sdfFileName, String actFileName, String xFileName,
            String descriptorType, String actFileDataType,
            String standardize, String splitType, String hasBeenScaled,
            String numExternalCompounds, String numExternalFolds,
            String useActivityBinning, String externalCompoundList,
            String datasetName, String paperReference,
            String dataSetDescription, String generateImages)
    {
        // for modeling sets without included descriptors

        this.userName = userName;
        this.datasetType = datasetType;
        this.sdfFileName = sdfFileName.replaceAll(" ", "_");
        this.actFileName = actFileName.replaceAll(" ", "_");
        this.xFileName = xFileName.replaceAll(" ", "_");
        this.descriptorType = descriptorType;
        this.actFileDataType = actFileDataType;
        this.standardize = standardize;
        this.splitType = splitType;
        this.hasBeenScaled = hasBeenScaled;
        this.numExternalCompounds = numExternalCompounds;
        this.numExternalFolds = numExternalFolds;
        this.useActivityBinning = useActivityBinning;
        this.externalCompoundList = externalCompoundList;
        this.jobName = datasetName;
        this.paperReference = paperReference;
        this.dataSetDescription = dataSetDescription;
        this.generateMahalanobis = generateImages;

        this.dataset = new DataSet();

        String path = Constants.CECCR_USER_BASE_PATH + userName
                + "/DATASETS/" + jobName + "/";
        try {
            if (!sdfFileName.equals("")) {
                this.numCompounds = DatasetFileOperations
                        .getSDFCompoundNames(
                                path + sdfFileName.replaceAll(" ", "_"))
                        .size();
            }
            else if (!xFileName.equals("")) {
                this.numCompounds = DatasetFileOperations.getXCompoundNames(
                        path + xFileName.replaceAll(" ", "_")).size();
            }
        }
        catch (Exception ex) {
            Utility.writeToDebug(ex, userName, jobName);
        }
    }

    public Long setUp() throws Exception
    {
        // create DataSet object in DB to allow for recovery of this job if it
        // fails.

        dataset.setName(jobName);
        dataset.setUserName(userName);
        dataset.setDatasetType(datasetType);
        dataset.setActFile(actFileName);
        dataset.setSdfFile(sdfFileName);
        dataset.setXFile(xFileName);
        dataset.setModelType(actFileDataType);
        dataset.setNumCompound(numCompounds);
        dataset.setCreatedTime(new Date());
        dataset.setDescription(dataSetDescription);
        dataset.setPaperReference(paperReference);
        dataset.setActFormula(actFileHeader);
        dataset.setUploadedDescriptorType(descriptorType);
        dataset.setHasBeenViewed(Constants.NO);
        dataset.setJobCompleted(Constants.NO);

        dataset.setStandardize(standardize);
        dataset.setSplitType(splitType);
        dataset.setHasBeenScaled(hasBeenScaled);
        dataset.setNumExternalCompounds(numExternalCompounds);
        dataset.setNumExternalFolds(numExternalFolds);
        dataset.setUseActivityBinning(useActivityBinning);
        dataset.setExternalCompoundList(externalCompoundList);
        dataset.setHasVisualization((generateMahalanobis.equals("true")) ? 1
                : 0);

        Session session = HibernateUtil.getSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(dataset);
            tx.commit();
        }
        catch (RuntimeException e) {
            if (tx != null)
                tx.rollback();
            Utility.writeToDebug(e, userName, jobName);
        }
        finally {
            session.close();
        }

        lookupId = dataset.getId();
        jobType = Constants.DATASET;

        return lookupId;
    }

    public void preProcess() throws Exception
    {
        String path = Constants.CECCR_USER_BASE_PATH + userName
                + "/DATASETS/" + jobName + "/";

        Utility.writeToDebug("executing task", userName, jobName);

        // first run dos2unix on all input files, just to be sure
        if (!sdfFileName.equals("")) {
            DatasetFileOperations.dos2unix(path + sdfFileName);
        }
        if (!xFileName.equals("")) {
            DatasetFileOperations.dos2unix(path + xFileName);
        }
        if (!actFileName.equals("")) {
            DatasetFileOperations.dos2unix(path + actFileName);
        }

        if (!sdfFileName.equals("") && standardize.equals("true")) {
            // standardize the SDF
            step = Constants.STANDARDIZING;
            Utility.writeToDebug("Standardizing SDF: " + sdfFileName,
                    userName, jobName);
            StandardizeMolecules.standardizeSdf(sdfFileName, sdfFileName
                    + ".standardize", path);
            File standardized = new File(path + sdfFileName + ".standardize");
            if (standardized.exists()) {
                // replace old SDF with new standardized SDF
                FileAndDirOperations.copyFile(path + sdfFileName
                        + ".standardize", path + sdfFileName);
                FileAndDirOperations.deleteFile(path + sdfFileName
                        + ".standardize");
            }
        }

        if (!sdfFileName.equals("")) {
            // generate descriptors
            this.numCompounds = DatasetFileOperations.getSDFCompoundNames(
                    path + sdfFileName).size();

            String descriptorDir = "Descriptors/";
            if (!new File(path + descriptorDir).exists()) {
                new File(path + descriptorDir).mkdirs();
            }

            step = Constants.DESCRIPTORS;
            Utility.writeToDebug("Generating Descriptors", userName, jobName);

            // the dataset included an SDF so we need to generate descriptors
            // from it
            Utility.writeToDebug("Generating MolconnZ Descriptors", userName,
                    jobName);
            // GenerateDescriptorWorkflow.GenerateMolconnZDescriptors(path +
            // sdfFileName, path + descriptorDir + sdfFileName + ".mz");
            GenerateDescriptors.GenerateMolconnZDescriptors(path
                    + sdfFileName, path + descriptorDir + sdfFileName
                    + ".molconnz");

            Utility.writeToDebug("Generating CDK Descriptors", userName,
                    jobName);
            GenerateDescriptors.GenerateCDKDescriptors(path + sdfFileName,
                    path + descriptorDir + sdfFileName + ".cdk");

            Utility.writeToDebug("Generating DragonH Descriptors", userName,
                    jobName);
            GenerateDescriptors.GenerateHExplicitDragonDescriptors(path
                    + sdfFileName, path + descriptorDir + sdfFileName
                    + ".dragonH");

            Utility.writeToDebug("Generating DragonNoH Descriptors",
                    userName, jobName);
            GenerateDescriptors.GenerateHDepletedDragonDescriptors(path
                    + sdfFileName, path + descriptorDir + sdfFileName
                    + ".dragonNoH");

            Utility.writeToDebug("Generating MOE2D Descriptors", userName,
                    jobName);
            GenerateDescriptors.GenerateMoe2DDescriptors(path + sdfFileName,
                    path + descriptorDir + sdfFileName + ".moe2D");

            Utility.writeToDebug("Generating MACCS Descriptors", userName,
                    jobName);
            GenerateDescriptors.GenerateMaccsDescriptors(path + sdfFileName,
                    path + descriptorDir + sdfFileName + ".maccs");

            step = Constants.CHECKDESCRIPTORS;
            // MolconnZ
            String errors = CheckDescriptors.checkMolconnZDescriptors(path
                    + descriptorDir + sdfFileName + ".molconnz");
            if (errors.equals("")) {
                availableDescriptors += Constants.MOLCONNZ + " ";
            }
            else {
                File errorSummaryFile = new File(path + descriptorDir
                        + "Logs/molconnz.out");
                BufferedWriter errorSummary = new BufferedWriter(
                        new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // CDK
            errors = CheckDescriptors.checkCDKDescriptors(path
                    + descriptorDir + sdfFileName + ".cdk");
            if (errors.equals("")) {
                availableDescriptors += Constants.CDK + " ";
            }
            else {
                availableDescriptors += Constants.CDK + " "; // CDK is
                                                             // available even
                                                             // when there are
                                                             // errors
                File errorSummaryFile = new File(path + descriptorDir
                        + "Logs/cdk.out");
                BufferedWriter errorSummary = new BufferedWriter(
                        new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // DragonH
            errors = CheckDescriptors.checkDragonDescriptors(path
                    + descriptorDir + sdfFileName + ".dragonH");
            if (errors.equals("")) {
                availableDescriptors += Constants.DRAGONH + " ";
            }
            else {
                File errorSummaryFile = new File(path + descriptorDir
                        + "Logs/dragonH.out");
                BufferedWriter errorSummary = new BufferedWriter(
                        new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // DragonNoH
            errors = CheckDescriptors.checkDragonDescriptors(path
                    + descriptorDir + sdfFileName + ".dragonNoH");
            if (errors.equals("")) {
                availableDescriptors += Constants.DRAGONNOH + " ";
            }
            else {
                File errorSummaryFile = new File(path + descriptorDir
                        + "Logs/dragonNoH.out");
                BufferedWriter errorSummary = new BufferedWriter(
                        new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // MOE2D
            errors = CheckDescriptors.checkMoe2DDescriptors(path
                    + descriptorDir + sdfFileName + ".moe2D");
            if (errors.equals("")) {
                availableDescriptors += Constants.MOE2D + " ";
            }
            else {
                File errorSummaryFile = new File(path + descriptorDir
                        + "Logs/moe2d.out");
                BufferedWriter errorSummary = new BufferedWriter(
                        new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // MACCS
            errors = CheckDescriptors.checkMaccsDescriptors(path
                    + descriptorDir + sdfFileName + ".maccs");
            if (errors.equals("")) {
                availableDescriptors += Constants.MACCS + " ";
            }
            else {
                File errorSummaryFile = new File(path + descriptorDir
                        + "Logs/maccs.out");
                BufferedWriter errorSummary = new BufferedWriter(
                        new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
        }

        // add uploaded descriptors to list (if any)
        if (datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)
                || datasetType.equals(Constants.PREDICTIONWITHDESCRIPTORS)) {
            availableDescriptors += Constants.UPLOADED + " ";
        }

        if (datasetType.equals(Constants.MODELING)
                || datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)) {
            // split dataset to get external set and modeling set

            step = Constants.SPLITDATA;

            Utility.writeToDebug("Creating " + splitType
                    + " External Validation Set", userName, jobName);

            if (splitType.equals(Constants.RANDOM)) {
                Utility.writeToDebug("Making random external split",
                        userName, jobName);
                if (datasetType.equals(Constants.MODELING)) {
                    // we will need to make a .x file from the .act file
                    DatasetFileOperations.makeXFromACT(path, actFileName);
                    String tempXFileName = actFileName.substring(0,
                            actFileName.lastIndexOf('.'))
                            + ".x";

                    // now run datasplit on the resulting .x file to get a
                    // list of compounds
                    DataSplit.SplitModelingExternal(path, actFileName,
                            tempXFileName, numExternalCompounds,
                            useActivityBinning);

                    // delete the temporary .x file
                    FileAndDirOperations.deleteFile(path + tempXFileName);
                }
                else if (datasetType
                        .equals(Constants.MODELINGWITHDESCRIPTORS)) {
                    // already got a .x file, so just split that
                    DataSplit.SplitModelingExternal(path, actFileName,
                            xFileName, numExternalCompounds,
                            useActivityBinning);
                }

            }
            else if (splitType.equals(Constants.USERDEFINED)) {
                Utility.writeToDebug("Making user-defined external split",
                        userName, jobName);

                // get the list of compound IDs
                externalCompoundList = externalCompoundList.replaceAll(",",
                        " ");
                externalCompoundList = externalCompoundList.replaceAll(
                        "\\\n", " ");

                if (datasetType.equals(Constants.MODELING)) {

                    // we will need to make a .x file from the .act file
                    DatasetFileOperations.makeXFromACT(path, actFileName);
                    String tempXFileName = actFileName.substring(0,
                            actFileName.lastIndexOf('.'))
                            + ".x";

                    // now split the resulting .x file
                    DataSplit.splitModelingExternalGivenList(path,
                            actFileName, tempXFileName, externalCompoundList);

                    // delete the temporary .x file
                    FileAndDirOperations.deleteFile(path + tempXFileName);
                }
                else if (datasetType
                        .equals(Constants.MODELINGWITHDESCRIPTORS)) {
                    // already got a .x file, so just split that
                    DataSplit.splitModelingExternalGivenList(path,
                            actFileName, xFileName, externalCompoundList);
                }
            }
            else if (splitType.equals(Constants.NFOLD)) {
                if (datasetType.equals(Constants.MODELING)
                        || datasetType
                                .equals(Constants.MODELINGWITHDESCRIPTORS)) {
                    // generate the lists of compounds for each split
                    DataSplit.SplitModelingExternalNFold(path, actFileName,
                            numExternalFolds, useActivityBinning);
                }
            }
        }

        if (jobList.equals(Constants.LSF)) {
            // copy needed files out to LSF
        }
    }

    public String executeLSF() throws Exception
    {
        // this should do the same thing as executeLocal functionally
        // it will create a job on LSF and return immediately.
        return "";
    }

    public void executeLocal() throws Exception
    {

        String path = Constants.CECCR_USER_BASE_PATH + userName
                + "/DATASETS/" + jobName + "/";
        if (!sdfFileName.equals("")) {
            // generate compound sketches and visualization files

            String descriptorDir = "Descriptors/";
            String vizFilePath = "Visualization/";

            if (!new File(path + vizFilePath).exists()) {
                new File(path + vizFilePath).mkdirs();
            }

            String structDir = "Visualization/Structures/";
            String sketchDir = "Visualization/Sketches/";

            if (!new File(path + structDir).exists()) {
                new File(path + structDir).mkdirs();
            }
            if (!new File(path + sketchDir).exists()) {
                new File(path + sketchDir).mkdirs();
            }

            step = Constants.SKETCHES;
            Utility.writeToDebug("Generating JPGs", userName, jobName);
            SdfToJpg.makeSketchFiles(path, sdfFileName, structDir, sketchDir);
            Utility.writeToDebug("Generating JPGs END", userName, jobName);
            step = Constants.SKETCHES + " finished!";

            if (numCompounds < 500
                    && !sdfFileName.equals("")
                    && new File(path + descriptorDir + sdfFileName + ".maccs")
                            .exists()) {
                // totally not worth doing visualizations on huge datasets,
                // the heatmap is
                // just nonsense at that point and it wastes a ton of compute
                // time.
                step = Constants.VISUALIZATION;
                Utility.writeToDebug("Generating Visualizations", userName,
                        jobName);

                String vis_path = Constants.CECCR_USER_BASE_PATH + userName
                        + "/DATASETS/" + jobName + "/Visualization/";
                HeatmapAndPCA.performXCreation(path + descriptorDir
                        + sdfFileName + ".maccs", sdfFileName + ".x",
                        vis_path);
                if (generateMahalanobis != null
                        && generateMahalanobis.equals("true"))
                    HeatmapAndPCA.performHeatMapAndTreeCreation(vis_path,
                            sdfFileName, "mahalanobis");
                HeatmapAndPCA.performHeatMapAndTreeCreation(vis_path,
                        sdfFileName, "tanimoto");

                if (!actFileName.equals("")) {
                    // generate ACT-file related visualizations
                    this.numCompounds = DatasetFileOperations
                            .getACTCompoundNames(path + actFileName).size();
                    /*
                    String act_path = Constants.CECCR_USER_BASE_PATH
                            + userName + "/DATASETS/" + jobName + "/"
                            + actFileName;
                    */

                    // PCA plot creation works
                    // however, there is no way to visualize the result right
                    // now.
                    // also, it's broken for some reason, so fuck that - just
                    // fix it later.
                    // CSV_X_Workflow.performPCAcreation(viz_path, act_path);

                }
                Utility.writeToDebug("Generating Visualizations END",
                        userName, jobName);
            }
            else {
                Utility.writeToDebug("Skipping generation of heatmap data",
                        userName, jobName);
            }

        }

        if (!xFileName.equals("")) {
            this.numCompounds = DatasetFileOperations.getXCompoundNames(
                    path + xFileName).size();
        }

    }

    public void postProcess() throws Exception
    {
        Utility.writeToDebug("Saving dataset to database", userName, jobName);

        if (jobList.equals(Constants.LSF)) {
            // copy needed back from LSF
        }

        // add dataset to DB
        dataset.setHasBeenViewed(Constants.NO);
        dataset.setJobCompleted(Constants.YES);
        dataset.setAvailableDescriptors(availableDescriptors);

        Session session = HibernateUtil.getSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(dataset);
            tx.commit();
        }
        catch (RuntimeException e) {
            if (tx != null)
                tx.rollback();
            Utility.writeToDebug(e, userName, jobName);
        }
        finally {
            session.close();
        }
    }

    public void delete() throws Exception
    {

    }

    public void setStep(String step)
    {
        this.step = step;
    }

    public String getStatus()
    {
        return step;
    }
}
