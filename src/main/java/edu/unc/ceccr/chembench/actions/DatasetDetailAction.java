package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Compound;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.DatasetRepository;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.visualization.ActivityHistogram;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public class DatasetDetailAction extends DetailAction {

    private static final Logger logger = Logger.getLogger(DatasetDetailAction.class);
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

    private String datasetDescription;
    private String datasetReference;

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
                foldNumbers = Lists.newArrayList();
                int smallestFoldSize = 0;
                int largestFoldSize = 0;
                int numFolds = Integer.parseInt(dataset.getNumExternalFolds());
                for (int i = 1; i <= numFolds; i++) {
                    foldNumbers.add(i);
                    Path foldFilePath = datasetPath.resolve(dataset.getActFile() + ".fold" + i);
                    Map<String, String> actIdsAndValues = DatasetFileOperations.getActFileIdsAndValues(foldFilePath);
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
                externalCompounds = Lists.newArrayList();
                Map<String, String> actIdsAndValues = DatasetFileOperations
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
        datasetCompounds = Lists.newArrayList();
        List<String> compoundIds = null;
        if (dataset.getXFile() != null && !dataset.getXFile().isEmpty()) {
            compoundIds = DatasetFileOperations.getXCompoundNames(datasetPath.resolve(dataset.getXFile()));
        } else {
            compoundIds = DatasetFileOperations.getSDFCompoundNames(datasetPath.resolve(dataset.getSdfFile()));
        }

        for (String cid : compoundIds) {
            Compound c = new Compound();
            c.setCompoundId(cid);
            datasetCompounds.add(c);
        }

        // get activity values (if applicable)
        if (dataset.getDatasetType().startsWith(Constants.MODELING)) {
            Map<String, String> actIdsAndValues =
                    DatasetFileOperations.getActFileIdsAndValues(datasetPath.resolve(dataset.getActFile()));

            for (Compound c : datasetCompounds) {
                c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
            }
        }

        descriptorGenerationResults = readDescriptorGenerationResults();
        return SUCCESS;
    }

    private List<DescriptorGenerationResult> readDescriptorGenerationResults() {
        descriptorGenerationResults = Lists.newArrayList();
        Path descriptorLogPath = datasetPath.resolve("Descriptors").resolve("Logs");
        // read descriptor program outputs
        DescriptorGenerationResult cdkResult = new DescriptorGenerationResult();
        cdkResult.setDescriptorType("CDK");
        if (Files.exists(descriptorLogPath.resolve("cdk.out"))) {
            cdkResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("cdk.out")));
        }
        if (Files.exists(descriptorLogPath.resolve("cdk.err"))) {
            cdkResult.setProgramErrorOutput(
                    FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("cdk" + ".err")));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.CDK)) {
            cdkResult.setGenerationResult("Successful");
        } else {
            cdkResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(cdkResult);

        DescriptorGenerationResult ISIDAResult = new DescriptorGenerationResult();
        ISIDAResult.setDescriptorType("ISIDA");
        if (Files.exists(descriptorLogPath.resolve("ISIDA.out"))) {
            ISIDAResult
                    .setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("ISIDA.out")));
        }
        if (Files.exists(descriptorLogPath.resolve("ISIDA.err"))) {
            ISIDAResult.setProgramErrorOutput(
                    FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("ISIDA.err")));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.ISIDA)) {
            ISIDAResult.setGenerationResult("Successful");
        } else {
            ISIDAResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(ISIDAResult);

        DescriptorGenerationResult dragonHResult = new DescriptorGenerationResult();
        dragonHResult.setDescriptorType("Dragon (with hydrogens)");
        if (Files.exists(descriptorLogPath.resolve("dragonH.out"))) {
            dragonHResult.setProgramOutput(
                    FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragonH.out")));
        }
        if (Files.exists(descriptorLogPath.resolve("dragonH.err"))) {
            String dragonErrStr = FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragonH.err"));
            if (dragonErrStr.contains("error: license not valid on the computer in use")) {
                dragonErrStr = "Dragon license invalid or expired.";
            }
            if (dragonErrStr.contains("Access violation")) {
                logger.debug("DragonX crashed; please contact the system administrator at " + Constants.WEBSITEEMAIL
                        + " to fix this problem.");
            }
            // The Dragon output contains lots of extra info (MAC address of server, that sorta thing)
            // that should not be displayed. Remove it.
            // Sample of stuff we don't want to show:
            /*
             * dragonX version 1.4 - Command line version for Linux - v.1.4.2 - built on: 2007-12-04
             * License file (/usr/local/ceccr/dragon/2010-12-31_drgx_license_UNC.txt) is a valid license file
             * User: ceccr (). Date: 2010/02/17 - 00:56:10 Licensed to: UNC-Chapel Hill - License type: Academic
             * (Single Workstation) - Expiration Date: 2010/12/31 - MAC address: 00:14:5E:3D:75:24
             * Decimal Separator set to: '.' - Thousands Separator set to: ','
             */
            if (dragonErrStr.contains("Thousands")) {
                dragonErrStr = dragonErrStr.substring(dragonErrStr.indexOf("Thousands"), dragonErrStr.length());
                dragonErrStr = dragonErrStr.replace("Thousands Separator set to: ','", "");
                dragonErrStr = dragonErrStr.replaceAll(Constants.CECCR_USER_BASE_PATH, "");
            }
            dragonHResult.setProgramErrorOutput(dragonErrStr);
        }
        if (dataset.getAvailableDescriptors().contains(Constants.DRAGONH)) {
            dragonHResult.setGenerationResult("Successful");
        } else {
            dragonHResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(dragonHResult);

        DescriptorGenerationResult dragonNoHResult = new DescriptorGenerationResult();
        dragonNoHResult.setDescriptorType("Dragon (no hydrogens)");
        if (Files.exists(descriptorLogPath.resolve("dragonNoH.out"))) {
            dragonNoHResult.setProgramOutput(
                    FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragonNoH.out")));
        }
        if (Files.exists(descriptorLogPath.resolve("dragonNoH.err"))) {
            String dragonErrStr = FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("dragonNoH.err"));
            if (dragonErrStr.contains("error: license not valid on the computer in use")) {
                dragonErrStr = "Dragon license invalid or expired.";
            }
            if (dragonErrStr.contains("Access violation")) {
                logger.debug("DragonX crashed; please contact the system administrator at " + Constants.WEBSITEEMAIL
                        + " to fix this problem.");
            }
            if (dragonErrStr.contains("Thousands")) {
                dragonErrStr = dragonErrStr.substring(dragonErrStr.indexOf("Thousands"), dragonErrStr.length());
                dragonErrStr = dragonErrStr.replace("Thousands Separator set to: ','", "");
                dragonErrStr = dragonErrStr.replaceAll(Constants.CECCR_USER_BASE_PATH, "");
            }
            dragonNoHResult.setProgramErrorOutput(dragonErrStr);
        }
        if (dataset.getAvailableDescriptors().contains(Constants.DRAGONNOH)) {
            dragonNoHResult.setGenerationResult("Successful");
        } else {
            dragonNoHResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(dragonNoHResult);

        DescriptorGenerationResult moe2DResult = new DescriptorGenerationResult();
        moe2DResult.setDescriptorType(Constants.MOE2D);
        if (Files.exists(descriptorLogPath.resolve("moe2d.out"))) {
            moe2DResult
                    .setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("moe2d.out")));
        }
        if (Files.exists(descriptorLogPath.resolve("moe2d.sh.err"))) {
            moe2DResult.setProgramErrorOutput(
                    FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("moe2d.sh.err")));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.MOE2D)) {
            moe2DResult.setGenerationResult("Successful");
        } else {
            moe2DResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(moe2DResult);

        DescriptorGenerationResult maccsResult = new DescriptorGenerationResult();
        maccsResult.setDescriptorType(Constants.MACCS);
        if (Files.exists(descriptorLogPath.resolve("maccs.out"))) {
            maccsResult
                    .setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("maccs.out")));
        }
        if (Files.exists(descriptorLogPath.resolve("maccs.sh.err"))) {
            maccsResult.setProgramErrorOutput(
                    FileAndDirOperations.readFileIntoString(descriptorLogPath.resolve("maccs.sh.err")));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.MOE2D)) {
            maccsResult.setGenerationResult("Successful");
        } else {
            maccsResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(maccsResult);
        return descriptorGenerationResults;
    }

    private String updateDataset() {
        if (dataset != null && datasetDescription != null && datasetReference != null) {
            dataset.setDescription(datasetDescription);
            dataset.setPaperReference(datasetReference);
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
        Map<String, String> foldCompoundsAndActivities = DatasetFileOperations.getActFileIdsAndValues(foldFilePath);
        foldCompounds = Lists.newArrayList();
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

    public String getDatasetDescription() {
        return datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public String getDatasetReference() {
        return datasetReference;
    }

    public void setDatasetReference(String datasetReference) {
        this.datasetReference = datasetReference;
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
        private String programErrorOutput;

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

        public String getProgramErrorOutput() {
            return programErrorOutput;
        }

        public void setProgramErrorOutput(String programErrorOutput) {
            this.programErrorOutput = programErrorOutput;
        }
    }

}
