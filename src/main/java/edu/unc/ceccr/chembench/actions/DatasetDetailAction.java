package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Compound;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.DatasetRepository;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.visualization.ActivityHistogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DatasetDetailAction extends DetailAction {

    private static final Logger logger = LoggerFactory.getLogger(DatasetDetailAction.class);
    private static final String DRAGON7_ERROR_HEADER = "\\*\\*\\* Errors encountered during execution of Dragon:";
    private final DatasetRepository datasetRepository;

    private Dataset dataset;
    private Path datasetPath;
    private boolean editable = false;
    private List<Compound> datasetCompounds;
    private List<DescriptorGenerationResult> descriptorGenerationResults;

    private List<Compound> externalCompounds; // for random split or chosen compounds
    private String externalCountDisplay;
    private List<Integer> foldNumbers;
    private int foldNumber;
    private List<Compound> foldCompounds;

    private String description;
    private String paperReference;

    @Autowired
    public DatasetDetailAction(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public String execute() throws Exception {
        dataset = datasetRepository.findOne(id);
        String result = validateObject(dataset);
        if (!result.equals(SUCCESS)) {
            return result;
        }
        datasetPath = dataset.getDirectoryPath();
        editable = dataset.isEditableBy(user);

        if (request.getMethod().equals("POST")) {
            return updateDataset();
        }

        // the dataset has now been viewed. Update DB accordingly.
        if (!dataset.getHasBeenViewed().equals(Constants.YES)) {
            dataset.setHasBeenViewed(Constants.YES);
            datasetRepository.save(dataset);
        }

        if (dataset.getDatasetType().startsWith(Constants.MODELING)) {
            if (dataset.getSplitType().equals(Constants.NFOLD)) {
                foldNumbers = new ArrayList<>();
                int smallestFoldSize = 0;
                int largestFoldSize = 0;
                int numFolds = Integer.parseInt(dataset.getNumExternalFolds());
                for (int i = 1; i <= numFolds; i++) {
                    foldNumbers.add(i);
                    Path foldFilePath = datasetPath.resolve(dataset.getActFile() + ".fold" + i);
                    Map<String, Double> actIdsAndValues = DatasetFileOperations.getActFileIdsAndValues(foldFilePath);
                    int numExternalInThisFold = actIdsAndValues.size();
                    if (largestFoldSize == 0 || largestFoldSize < numExternalInThisFold) {
                        largestFoldSize = numExternalInThisFold;
                    }
                    if (smallestFoldSize == 0 || smallestFoldSize > numExternalInThisFold) {
                        smallestFoldSize = numExternalInThisFold;
                    }
                }
                if (smallestFoldSize == largestFoldSize) {
                    externalCountDisplay = smallestFoldSize + " per fold";
                } else {
                    externalCountDisplay = smallestFoldSize + " to " + largestFoldSize + " per fold";
                }
            } else {
                // load external compounds from file
                externalCompounds = new ArrayList<>();
                Map<String, Double> actIdsAndValues = DatasetFileOperations
                        .getActFileIdsAndValues(datasetPath.resolve(Constants.EXTERNAL_SET_A_FILE));
                List<String> compoundIds = Lists.newArrayList(actIdsAndValues.keySet());
                for (String compoundId : compoundIds) {
                    Compound c = new Compound();
                    c.setCompoundId(compoundId);
                    c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
                    externalCompounds.add(c);
                }
                externalCountDisplay = Integer.toString(externalCompounds.size());
            }
            ActivityHistogram.createChart(id);
        }

        // load compounds
        datasetCompounds = new ArrayList<>();
        List<String> compoundIds = null;
        if (dataset.getXFile() != null && !dataset.getXFile().isEmpty()) {
            compoundIds = DatasetFileOperations.getXCompoundNames(datasetPath.resolve(dataset.getXFile()));
        } else {
            compoundIds = DatasetFileOperations.getSdfCompoundNames(datasetPath.resolve(dataset.getSdfFile()));
        }

        for (String cid : compoundIds) {
            Compound c = new Compound();
            c.setCompoundId(cid);
            datasetCompounds.add(c);
        }

        // get activity values (if applicable)
        if (dataset.getDatasetType().startsWith(Constants.MODELING)) {
            Map<String, Double> actIdsAndValues =
                    DatasetFileOperations.getActFileIdsAndValues(datasetPath.resolve(dataset.getActFile()));

            for (Compound c : datasetCompounds) {
                c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
            }
        }

        descriptorGenerationResults = readDescriptorGenerationResults();
        return SUCCESS;
    }

    private List<DescriptorGenerationResult> readDescriptorGenerationResults() {
        descriptorGenerationResults = new ArrayList<>();
        Path descriptorLogPath = datasetPath.resolve("Descriptors").resolve("Logs");
        // read descriptor program outputs
        //CDK is special because it is available even if there are errors.
        DescriptorGenerationResult cdkResult = new DescriptorGenerationResult();
        cdkResult.setDescriptorType("CDK");
        //.out exist only if there are errors
        if (Files.exists(descriptorLogPath.resolve("cdk.out"))) {
            cdkResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("cdk.out")));
            cdkResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        else {
            cdkResult.setGenerationResult("Successful");
        }
        descriptorGenerationResults.add(cdkResult);

        DescriptorGenerationResult isidaResult = new DescriptorGenerationResult();
        try {
            isidaResult.setDescriptorType("ISIDA");
            if (dataset.getAvailableDescriptors().contains(Constants.ISIDA)) {
                isidaResult.setGenerationResult("Successful");
            } else {
                if (Files.exists(descriptorLogPath.resolve("ISIDA.out"))) {
                    isidaResult
                            .setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("ISIDA.out")));
                }
                isidaResult.setGenerationResult("Descriptor generation failed. See error summary for details.");
            }
            descriptorGenerationResults.add(isidaResult);
        } catch (Exception e) {
            logger.error("Isida ", e);
        }
       

        DescriptorGenerationResult dragonHResult = new DescriptorGenerationResult();
        dragonHResult.setDescriptorType("Dragon (with hydrogens)");
        if (dataset.getAvailableDescriptors().contains(Constants.DRAGONH)) {
            dragonHResult.setGenerationResult("Successful");
        } else {
            if (Files.exists(descriptorLogPath.resolve("dragonH.out"))) {
                dragonHResult.setProgramOutput(
                        FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragonH.out")));
            }
            dragonHResult.setGenerationResult("Descriptor generation failed. See error summary for details.");
        }
        descriptorGenerationResults.add(dragonHResult);

        DescriptorGenerationResult dragonNoHResult = new DescriptorGenerationResult();
        dragonNoHResult.setDescriptorType("Dragon (no hydrogens)");
        if (dataset.getAvailableDescriptors().contains(Constants.DRAGONNOH)) {
            dragonNoHResult.setGenerationResult("Successful");
        } else {
            if (Files.exists(descriptorLogPath.resolve("dragonNoH.out"))) {
                dragonNoHResult.setProgramOutput(
                        FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragonNoH.out")));
            }
            dragonNoHResult.setGenerationResult("Descriptor generation failed. See error summary for details.");
        }
        descriptorGenerationResults.add(dragonNoHResult);

        DescriptorGenerationResult moe2DResult = new DescriptorGenerationResult();
        moe2DResult.setDescriptorType(Constants.MOE2D);
        if (dataset.getAvailableDescriptors().contains(Constants.MOE2D)) {
            moe2DResult.setGenerationResult("Successful");
        } else {
            if (Files.exists(descriptorLogPath.resolve("moe2d.out"))) {
                moe2DResult
                        .setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("moe2d.out")));
            }
            moe2DResult.setGenerationResult("Descriptor generation failed. See error summary for details.");
        }
        descriptorGenerationResults.add(moe2DResult);

        DescriptorGenerationResult maccsResult = new DescriptorGenerationResult();
        maccsResult.setDescriptorType(Constants.MACCS);
        if (dataset.getAvailableDescriptors().contains(Constants.MOE2D)) {
            maccsResult.setGenerationResult("Successful");
        } else {
            if (Files.exists(descriptorLogPath.resolve("maccs.out"))) {
                maccsResult
                        .setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("maccs.out")));
            }
            maccsResult.setGenerationResult("Descriptor generation failed. See error summary for details.");
        }
        descriptorGenerationResults.add(maccsResult);

        DescriptorGenerationResult dragon7Result = new DescriptorGenerationResult();
        dragon7Result.setDescriptorType("Dragon 7");
        if (!dataset.getAvailableDescriptors().contains(Constants.DRAGON7)) {
            dragon7Result.setProgramOutput(""); // dragon 7 doesn't have a separate .log file (it'll always be empty)
            if (Files.exists(descriptorLogPath.resolve("dragon7.err"))) {
                dragon7Result.setGenerationResult("Descriptor generation failed. See error summary for details.");
                String[] rawLog = FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragon7.err"))
                        .split(DRAGON7_ERROR_HEADER);
                if (rawLog.length > 1) {
                    String errorSummary = rawLog[1].trim();
                    if (errorSummary.contains("not correctly licensed")) {
                        dragon7Result.setProgramOutput("Invalid license.");
                    } else if (errorSummary.contains("script file")) {
                        dragon7Result.setProgramOutput("Invalid or missing script file.");
                    } else {
                        dragon7Result.setProgramOutput("Unknown error.");
                    }
                }
            } else {
                dragon7Result.setGenerationResult("Descriptor generation failed."); // no extra info available
            }
        } else {
            dragon7Result.setGenerationResult("Successful");
        }
        descriptorGenerationResults.add(dragon7Result);

        return descriptorGenerationResults;
    }

    private String updateDataset() {
        if (editable && dataset != null && description != null && paperReference != null) {
            dataset.setDescription(description);
            dataset.setPaperReference(paperReference);
            datasetRepository.save(dataset);
            return SUCCESS;
        }
        return ERROR;
    }

    public String getFold() {
        dataset = datasetRepository.findOne(id);
        if (dataset == null) {
            return "notfound";
        }

        if (dataset.getModelType().equals(Constants.PREDICTION) || !dataset.getSplitType().equals(Constants.NFOLD)
                || foldNumber < 1 || foldNumber > Integer.parseInt(dataset.getNumExternalFolds())) {
            return "badrequest";
        }

        Path foldFilePath =
                Paths.get(Constants.CECCR_USER_BASE_PATH, dataset.getUserName(), "DATASETS", dataset.getName(),
                        dataset.getActFile() + ".fold" + foldNumber);
        Map<String, Double> foldCompoundsAndActivities = DatasetFileOperations.getActFileIdsAndValues(foldFilePath);
        foldCompounds = new ArrayList<>();
        for (String name : foldCompoundsAndActivities.keySet()) {
            Compound c = new Compound();
            c.setCompoundId(name);
            c.setActivityValue(foldCompoundsAndActivities.get(name));
            foldCompounds.add(c);
        }

        return SUCCESS;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public List<Compound> getDatasetCompounds() {
        return datasetCompounds;
    }

    public void setDatasetCompounds(List<Compound> datasetCompounds) {
        this.datasetCompounds = datasetCompounds;
    }

    public List<Compound> getExternalCompounds() {
        return externalCompounds;
    }

    public void setExternalCompounds(List<Compound> externalCompounds) {
        this.externalCompounds = externalCompounds;
    }

    public List<DescriptorGenerationResult> getDescriptorGenerationResults() {
        return descriptorGenerationResults;
    }

    public void setDescriptorGenerationResults(List<DescriptorGenerationResult> descriptorGenerationResults) {
        this.descriptorGenerationResults = descriptorGenerationResults;
    }

    public String getExternalCountDisplay() {
        return externalCountDisplay;
    }

    public void setExternalCountDisplay(String externalCompoundsCount) {
        this.externalCountDisplay = externalCompoundsCount;
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaperReference() {
        return paperReference;
    }

    public void setPaperReference(String paperReference) {
        this.paperReference = paperReference;
    }

    public int getFoldNumber() {
        return foldNumber;
    }

    public void setFoldNumber(int foldNumber) {
        this.foldNumber = foldNumber;
    }

    public List<Compound> getFoldCompounds() {
        return foldCompounds;
    }

    public void setFoldCompounds(List<Compound> foldCompounds) {
        this.foldCompounds = foldCompounds;
    }

    public List<Integer> getFoldNumbers() {
        return foldNumbers;
    }

    public void setFoldNumbers(List<Integer> foldNumbers) {
        this.foldNumbers = foldNumbers;
    }

    public class DescriptorGenerationResult {
        private String descriptorType;
        private String generationResult;
        private String programOutput;

        public String getDescriptorType() {
            return descriptorType;
        }

        public void setDescriptorType(String descriptorType) {
            this.descriptorType = descriptorType;
        }

        public String getGenerationResult() {
            return generationResult;
        }

        public void setGenerationResult(String generationResult) {
            this.generationResult = generationResult;
        }

        public String getProgramOutput() {
            return programOutput;
        }

        public void setProgramOutput(String programOutput) {
            this.programOutput = programOutput;
        }
    }

}
