package edu.unc.ceccr.chembench.taskObjects;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.DatasetRepository;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.StandardizeSdfFormat;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.datasets.StandardizeMolecules;
import edu.unc.ceccr.chembench.workflows.descriptors.CheckDescriptors;
import edu.unc.ceccr.chembench.workflows.descriptors.DescriptorGenerationException;
import edu.unc.ceccr.chembench.workflows.descriptors.GenerateDescriptors;
import edu.unc.ceccr.chembench.workflows.modelingPrediction.DataSplit;
import edu.unc.ceccr.chembench.workflows.visualization.HeatmapAndPCA;
import edu.unc.ceccr.chembench.workflows.visualization.SdfToJpg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Configurable(autowire = Autowire.BY_TYPE)
public class CreateDatasetTask extends WorkflowTask {
    private static final Logger logger = LoggerFactory.getLogger(CreateDatasetTask.class);
    private String userName = null;
    private String datasetType;
    private String sdfFileName;
    private String actFileName;
    private String xFileName;
    private String descriptorType;
    private String actFileDataType;
    private String standardize;
    private String splitType;
    private String hasBeenScaled;
    private String numExternalCompounds;
    private String numExternalFolds;
    private String useActivityBinning;
    private String externalCompoundList;
    private String jobName = null;
    private String paperReference;
    private String dataSetDescription;
    private String actFileHeader;
    private String availableDescriptors = "";
    private String generateMahalanobis;
    private int numCompounds;
    private Dataset dataset; // contains pretty much all the member variables. This is dumb but hopefully temporary.
    private String step = Constants.SETUP; // stores what step we're on

    @Autowired
    private DatasetRepository datasetRepository;

    public CreateDatasetTask(Dataset dataset) {
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

        String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";
        try {
            if (!sdfFileName.equals("")) {
                this.numCompounds = DatasetFileOperations.getSdfCompoundNames(path + sdfFileName).size();
            } else if (!xFileName.equals("")) {
                this.numCompounds = DatasetFileOperations.getXCompoundNames(path + xFileName).size();
            }
        } catch (Exception ex) {
            logger.error("User: " + userName + "Job: " + jobName + " " + ex);
        }
    }

    public CreateDatasetTask(String userName, String datasetType, String sdfFileName, String actFileName,
                             String xFileName, String descriptorType, String actFileDataType, String standardize,
                             String splitType, String hasBeenScaled, String numExternalCompounds,
                             String numExternalFolds, String useActivityBinning, String externalCompoundList,
                             String datasetName, String paperReference, String dataSetDescription,
                             String generateImages) {
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

        this.dataset = new Dataset();

        String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";
        try {
            if (!sdfFileName.equals("")) {
                this.numCompounds =
                        DatasetFileOperations.getSdfCompoundNames(path + sdfFileName.replaceAll(" ", "_")).size();
            } else if (!xFileName.equals("")) {
                this.numCompounds =
                        DatasetFileOperations.getXCompoundNames(path + xFileName.replaceAll(" ", "_")).size();
            }
        } catch (Exception ex) {
            logger.error("User: " + userName + "Job: " + jobName + " " + ex);
        }
    }

    public String getProgress(String userName) {
        return step;
    }

    public Long setUp() throws Exception {
        // create Dataset object in DB to allow for recovery of this job if it fails.
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
        dataset.setHasVisualization((generateMahalanobis.equals("true")) ? 1 : 0);
        datasetRepository.save(dataset);
        lookupId = dataset.getId();
        jobType = Constants.DATASET;
        return lookupId;
    }

    public void preProcess() throws Exception {
        String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";
        logger.debug("User: " + userName + "Job: " + jobName + " executing task.");

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
            logger.debug("User: " + userName + "Job: " + jobName + " Standardizing SDF: " + sdfFileName);
            StandardizeMolecules.standardizeSdf(sdfFileName, sdfFileName + ".standardize", path);
            File standardized = new File(path + sdfFileName + ".standardize");
            if (standardized.exists()) {
                // replace old SDF with new standardized SDF
                FileAndDirOperations.copyFile(path + sdfFileName + ".standardize", path + sdfFileName);
                FileAndDirOperations.deleteFile(path + sdfFileName + ".standardize");
            }
        }

        if (!sdfFileName.equals("")) {
            //add a name tag <Chembench_Name> for each compound to ISIDA
            StandardizeSdfFormat.addNameTag(userName, jobName, path + sdfFileName, path + sdfFileName + ".addNameTag");
        }

        if (!sdfFileName.equals("")) {
            // generate descriptors
            this.numCompounds = DatasetFileOperations.getSdfCompoundNames(path + sdfFileName).size();

            String descriptorDir = "Descriptors/";
            if (!new File(path + descriptorDir).exists()) {
                new File(path + descriptorDir).mkdirs();
            }

            step = Constants.DESCRIPTORS;
            logger.debug("User: " + userName + "Job: " + jobName + " Generating Descriptors");

            // the dataset included an SDF so we need to generate descriptors from it
            logger.debug("User: " + userName + "Job: " + jobName + " Generating CDK Descriptors");
            GenerateDescriptors.generateCdkDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".cdk");

            logger.debug("User: " + userName + "Job: " + jobName + " Generating DragonH Descriptors");
            GenerateDescriptors.generateHExplicitDragonDescriptors(path + sdfFileName,
                    path + descriptorDir + sdfFileName + ".dragonH");

            logger.debug("User: " + userName + "Job: " + jobName + " Generating DragonNoH Descriptors");
            GenerateDescriptors.generateHDepletedDragonDescriptors(path + sdfFileName,
                    path + descriptorDir + sdfFileName + ".dragonNoH");

            logger.debug("User: " + userName + "Job: " + jobName + " Generating Moe2D Descriptors");
            GenerateDescriptors
                    .generateMoe2DDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".moe2D");

            logger.debug("User: " + userName + "Job: " + jobName + " Generating MACCS Descriptors");
            GenerateDescriptors
                    .generateMaccsDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".maccs");

            logger.debug("User: " + userName + "Job: " + jobName + " Generating ISIDA Descriptors");
            GenerateDescriptors
                    .generateIsidaDescriptors(path + sdfFileName, path + descriptorDir + sdfFileName + ".ISIDA");

            logger.debug("User: " + userName + "Job: " + jobName + " Generating Dragon 7 Descriptors");
            try {
                GenerateDescriptors.generateDragon7Descriptors(path + sdfFileName,
                        path + descriptorDir + sdfFileName + ".dragon7");
                availableDescriptors += Constants.DRAGON7 + " ";
            } catch (DescriptorGenerationException e) {
                logger.error("Dragon 7 descriptor generation failed; not adding to available descriptors", e);
            }

            step = Constants.CHECKDESCRIPTORS;

            // CDK
            String errors = CheckDescriptors.checkCdkDescriptors(path + descriptorDir + sdfFileName + ".cdk");
            if (errors.equals("")) {
                availableDescriptors += Constants.CDK + " ";
            } else {
                availableDescriptors += Constants.CDK + " "; // CDK is available even when there are errors
                File errorSummaryFile = new File(path + descriptorDir + "Logs/cdk.out");
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // DragonH
            errors = CheckDescriptors.checkDragonXDescriptors(path + descriptorDir + sdfFileName + ".dragonH");
            if (errors.equals("")) {
                availableDescriptors += Constants.DRAGONH + " ";
            } else {
                File errorSummaryFile = new File(path + descriptorDir + "Logs/dragonH.out");
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // DragonNoH
            errors = CheckDescriptors.checkDragonXDescriptors(path + descriptorDir + sdfFileName + ".dragonNoH");
            if (errors.equals("")) {
                availableDescriptors += Constants.DRAGONNOH + " ";
            } else {
                File errorSummaryFile = new File(path + descriptorDir + "Logs/dragonNoH.out");
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // MOE2D
            errors = CheckDescriptors.checkMoe2DDescriptors(path + descriptorDir + sdfFileName + ".moe2D");
            if (errors.equals("")) {
                availableDescriptors += Constants.MOE2D + " ";
            } else {
                File errorSummaryFile = new File(path + descriptorDir + "Logs/moe2d.out");
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            // MACCS
            errors = CheckDescriptors.checkMaccsDescriptors(path + descriptorDir + sdfFileName + ".maccs");
            if (errors.equals("")) {
                availableDescriptors += Constants.MACCS + " ";
            } else {
                File errorSummaryFile = new File(path + descriptorDir + "Logs/maccs.out");
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }
            //ISIDA
            Path descriptorDirPath = Paths.get(path + descriptorDir);
            if (Files.exists(descriptorDirPath.resolve(sdfFileName + ".ISIDA.hdr")) && Files.exists
                    (descriptorDirPath.resolve(sdfFileName + ".ISIDA.svm"))) {
                availableDescriptors += Constants.ISIDA + " ";
            } else {
                File errorSummaryFile = new File(path + descriptorDir + "Logs/ISIDA.out");
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write("The ISIDA .hdr / .svm file(s) are missing.");
                errorSummary.close();
            }
        }

        // add uploaded descriptors to list (if any)
        if (datasetType.equals(Constants.MODELINGWITHDESCRIPTORS) || datasetType
                .equals(Constants.PREDICTIONWITHDESCRIPTORS)) {
            availableDescriptors += Constants.UPLOADED + " ";
        }

        if (datasetType.equals(Constants.MODELING) || datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)) {
            // split dataset to get external set and modeling set

            step = Constants.SPLITDATA;

            logger.debug(
                    "User: " + userName + "Job: " + jobName + " Creating " + splitType + " External Validation Set");

            if (splitType.equals(Constants.RANDOM)) {

                logger.debug("User: " + userName + "Job: " + jobName + " Making random external split");
                if (datasetType.equals(Constants.MODELING)) {
                    // we will need to make a .x file from the .act file
                    DatasetFileOperations.makeXFromACT(path, actFileName);
                    String tempXFileName = actFileName.substring(0, actFileName.lastIndexOf('.')) + ".x";

                    // now run datasplit on the resulting .x file to get a
                    // list of compounds
                    DataSplit.SplitModelingExternal(path, actFileName, tempXFileName, numExternalCompounds,
                            useActivityBinning);

                    // delete the temporary .x file
                    FileAndDirOperations.deleteFile(path + tempXFileName);
                } else if (datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)) {
                    // already got a .x file, so just split that
                    DataSplit.SplitModelingExternal(path, actFileName, xFileName, numExternalCompounds,
                            useActivityBinning);
                }

            } else if (splitType.equals(Constants.USERDEFINED)) {
                logger.debug("User: " + userName + "Job: " + jobName + " Making user-defined external split");
                // get the list of compound IDs
                externalCompoundList = externalCompoundList.replaceAll(",", " ");
                externalCompoundList = externalCompoundList.replaceAll("\\\n", " ");

                if (datasetType.equals(Constants.MODELING)) {

                    // we will need to make a .x file from the .act file
                    DatasetFileOperations.makeXFromACT(path, actFileName);
                    String tempXFileName = actFileName.substring(0, actFileName.lastIndexOf('.')) + ".x";

                    // now split the resulting .x file
                    DataSplit.splitModelingExternalGivenList(path, actFileName, tempXFileName, externalCompoundList);

                    // delete the temporary .x file
                    FileAndDirOperations.deleteFile(path + tempXFileName);
                } else if (datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)) {
                    // already got a .x file, so just split that
                    DataSplit.splitModelingExternalGivenList(path, actFileName, xFileName, externalCompoundList);
                }
            } else if (splitType.equals(Constants.NFOLD)) {
                if (datasetType.equals(Constants.MODELING) || datasetType.equals(Constants.MODELINGWITHDESCRIPTORS)) {
                    // generate the lists of compounds for each split
                    DataSplit.SplitModelingExternalNFold(path, actFileName, numExternalFolds, useActivityBinning);
                }
            }
        }

        if (jobList.equals(Constants.LSF)) {
            // copy needed files out to LSF
        }
    }

    public String executeLSF() throws Exception {
        // this should do the same thing as executeLocal functionally
        // it will create a job on LSF and return immediately.
        return "";
    }

    public void executeLocal() throws Exception {

        String path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/";
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

            logger.debug("User: " + userName + "Job: " + jobName + " Generating JPGs");

			if(numCompounds<200){
				SdfToJpg.makeSketchFiles(path, sdfFileName, structDir, sketchDir);
			} // Planning on taking this out at a later time and Just Generating all Images through Chemdoodle or something else when clicked upon. tt
            logger.debug("User: " + userName + "Job: " + jobName + " Generating JPGs END");

            if (numCompounds < 500 && !sdfFileName.equals("") && new File(path + descriptorDir + sdfFileName + ".maccs")
                    .exists()) {
                // totally not worth doing visualizations on huge datasets,
                // the heatmap is
                // just nonsense at that point and it wastes a ton of compute
                // time.
                step = Constants.VISUALIZATION;
                logger.debug("User: " + userName + "Job: " + jobName + " Generating Visualizations");
                String vis_path =
                        Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + jobName + "/Visualization/";
                HeatmapAndPCA
                        .performXCreation(path + descriptorDir + sdfFileName + ".maccs", sdfFileName + ".x", vis_path);
                if (generateMahalanobis != null && generateMahalanobis.equals("true")) {
                    HeatmapAndPCA.performHeatMapAndTreeCreation(vis_path, sdfFileName, "mahalanobis");
                }
                HeatmapAndPCA.performHeatMapAndTreeCreation(vis_path, sdfFileName, "tanimoto");

                if (!actFileName.equals("")) {
                    // generate ACT-file related visualizations
                    this.numCompounds = DatasetFileOperations.getACTCompoundNames(path + actFileName).size();
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

                logger.debug("User: " + userName + "Job: " + jobName + " Generating Visualizations END");
            } else {
                logger.debug("User: " + userName + "Job: " + jobName + " Skipping generation of heatmap data");
            }

        }

        if (!xFileName.equals("")) {
            this.numCompounds = DatasetFileOperations.getXCompoundNames(path + xFileName).size();
        }
    }

    public void postProcess() throws Exception {

        logger.debug("User: " + userName + "Job: " + jobName + " Saving dataset to database");

        if (jobList.equals(Constants.LSF)) {
            // copy needed back from LSF
        }

        dataset.setHasBeenViewed(Constants.NO);
        dataset.setJobCompleted(Constants.YES);
        dataset.setAvailableDescriptors(availableDescriptors);
        if (dataset.canGenerateModi()) {
            step = Constants.MODI;
            dataset.generateModi();
        }
        // add dataset to DB
        datasetRepository.save(dataset);
    }

    public void delete() throws Exception {

    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getStatus() {
        return step;
    }
}
