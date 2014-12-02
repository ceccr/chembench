package edu.unc.ceccr.chembench.actions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Compound;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.visualization.ActivityHistogram;
import edu.unc.ceccr.chembench.workflows.visualization.HeatmapAndPCA;

@SuppressWarnings("serial")
public class ViewDataset extends ActionSupport {

    private static Logger logger = Logger.getLogger(ViewDataset.class.getName());

    private ActionContext context;
    private User user;

    private long id;
    private Dataset dataset;
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

    public String load() throws Exception {
        context = ActionContext.getContext();
        user = (User) context.getSession().get("user");
        if (user == null) {
            return LOGIN;
        }

        this.dataset = Dataset.get(id);
        if (dataset == null || (!dataset.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user.getUserName()
                .equals(dataset.getUserName()))) {
            super.addActionError("Invalid dataset ID.");
            return ERROR;
        }

        if (user.getIsAdmin().equals(Constants.YES) || user.getUserName().equals(dataset.getUserName())) {
            editable = true;
        }

        // the dataset has now been viewed. Update DB accordingly.
        if (!dataset.getHasBeenViewed().equals(Constants.YES)) {
            dataset.setHasBeenViewed(Constants.YES);
            dataset.save();
        }

        Path datasetDirPath = Paths.get(Constants.CECCR_USER_BASE_PATH, dataset.getUserName(), "DATASETS",
                dataset.getName());

        if (dataset.getDatasetType().startsWith(Constants.MODELING)) {
            if (dataset.getSplitType().equals(Constants.NFOLD)) {
                foldNumbers = Lists.newArrayList();
                int smallestFoldSize = 0;
                int largestFoldSize = 0;
                int numFolds = Integer.parseInt(dataset.getNumExternalFolds());
                for (int i = 1; i <= numFolds; i++) {
                    foldNumbers.add(i);
                    Path foldFilePath = datasetDirPath.resolve(dataset.getActFile() + ".fold" + i);
                    HashMap<String, String> actIdsAndValues = DatasetFileOperations
                            .getActFileIdsAndValues(foldFilePath.toString());
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
                HashMap<String, String> actIdsAndValues =
                        DatasetFileOperations.getActFileIdsAndValues(datasetDirPath.resolve(
                                Constants.EXTERNAL_SET_A_FILE)
                                .toString());
                List<String> compoundIds = Lists.newArrayList(actIdsAndValues.keySet());
                for (String compoundId : compoundIds) {
                    Compound c = new Compound();
                    c.setCompoundId(compoundId);
                    c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
                    externalCompounds.add(c);
                }

                externalCountDisplay = Integer.toString(externalCompounds.size());
            }

            // create activity chart
            ActivityHistogram.createChart(id);
        }

        // load compounds
        datasetCompounds = Lists.newArrayList();
        List<String> compoundIDs = null;
        if (dataset.getXFile() != null && !dataset.getXFile().isEmpty()) {
            compoundIDs = DatasetFileOperations
                    .getXCompoundNames(datasetDirPath.resolve(dataset.getXFile()).toString());
        } else {
            compoundIDs = DatasetFileOperations.getSDFCompoundNames(datasetDirPath.resolve(dataset.getSdfFile())
                    .toString());
        }

        for (String cid : compoundIDs) {
            Compound c = new Compound();
            c.setCompoundId(cid);
            datasetCompounds.add(c);
        }

        // get activity values (if applicable)
        if (dataset.getDatasetType().startsWith(Constants.MODELING)) {
            HashMap<String, String> actIdsAndValues =
                    DatasetFileOperations.getActFileIdsAndValues(datasetDirPath
                            .resolve(dataset.getActFile()).toString());

            for (Compound c : datasetCompounds) {
                c.setActivityValue(actIdsAndValues.get(c.getCompoundId()));
            }
        }

        return SUCCESS;
    }

    public String loadDescriptorsSection() throws Exception {
        descriptorGenerationResults = Lists.newArrayList();
        String descriptorsDir = Constants.CECCR_USER_BASE_PATH;
        descriptorsDir += dataset.getUserName() + "/";
        descriptorsDir += "DATASETS/" + dataset.getName() + "/Descriptors/Logs/";

        // read descriptor program outputs
        DescriptorGenerationResult cdkResult = new DescriptorGenerationResult();
        cdkResult.setDescriptorType("CDK");
        if ((new File(descriptorsDir + "cdk.out")).exists()) {
            cdkResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "cdk.out"));
        }
        if ((new File(descriptorsDir + "cdk.err")).exists()) {
            cdkResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "cdk.err"));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.CDK)) {
            cdkResult.setGenerationResult("Successful");
        } else {
            cdkResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(cdkResult);

        DescriptorGenerationResult ISIDAResult = new DescriptorGenerationResult();
        ISIDAResult.setDescriptorType("ISIDA");
        if ((new File(descriptorsDir + "ISIDA.out")).exists()) {
            ISIDAResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "ISIDA.out"));
        }
        if ((new File(descriptorsDir + "ISIDA.err")).exists()) {
            ISIDAResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "ISIDA.err"));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.ISIDA)) {
            ISIDAResult.setGenerationResult("Successful");
        } else {
            ISIDAResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(ISIDAResult);

        DescriptorGenerationResult dragonHResult = new DescriptorGenerationResult();
        dragonHResult.setDescriptorType("Dragon (with hydrogens)");
        if ((new File(descriptorsDir + "dragonH.out")).exists()) {
            dragonHResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonH.out"));
        }
        if ((new File(descriptorsDir + "dragonH.err")).exists()) {
            String dragonErrStr = FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonH.err");
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
        dragonNoHResult.setDescriptorType("Dragon (without hydrogens)");
        if ((new File(descriptorsDir + "dragonNoH.out")).exists()) {
            dragonNoHResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonNoH.out"));
        }
        if ((new File(descriptorsDir + "dragonNoH.err")).exists()) {
            String dragonErrStr = FileAndDirOperations.readFileIntoString(descriptorsDir + "dragonNoH.err");
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
        if ((new File(descriptorsDir + "moe2d.out")).exists()) {
            moe2DResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "moe2d.out"));
        }
        if ((new File(descriptorsDir + "moe2d.sh.err")).exists()) {
            moe2DResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "moe2d.sh.err"));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.MOE2D)) {
            moe2DResult.setGenerationResult("Successful");
        } else {
            moe2DResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(moe2DResult);

        DescriptorGenerationResult maccsResult = new DescriptorGenerationResult();
        maccsResult.setDescriptorType(Constants.MACCS);
        if ((new File(descriptorsDir + "maccs.out")).exists()) {
            maccsResult.setProgramOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "maccs.out"));
        }
        if ((new File(descriptorsDir + "maccs.sh.err")).exists()) {
            maccsResult.setProgramErrorOutput(FileAndDirOperations.readFileIntoString(descriptorsDir + "maccs.sh.err"));
        }
        if (dataset.getAvailableDescriptors().contains(Constants.MOE2D)) {
            maccsResult.setGenerationResult("Successful");
        } else {
            maccsResult.setGenerationResult("Descriptor generation failed. See program output for details.");
        }
        descriptorGenerationResults.add(maccsResult);

        return SUCCESS;
    }

    public String updateDataset() throws Exception {
        dataset = Dataset.get(id);
        if (dataset != null) {
            dataset.setDescription(datasetDescription);
            dataset.setPaperReference(datasetReference);
            dataset.save();
            return SUCCESS;
        }
        return ERROR;
    }

    public String generateMahalanobis() throws Exception {
        Path visualizationDirPath = Paths.get(Constants.CECCR_USER_BASE_PATH, user.getUserName(), "DATASETS",
                dataset.getName(), "Visualization");
        HeatmapAndPCA.performHeatMapAndTreeCreation(visualizationDirPath.toString(), dataset.getSdfFile(),
                "mahalanobis");
        dataset.setHasVisualization(1);
        dataset.save();

        return load();
    }

    public String getFold() {
        dataset = Dataset.get(id);
        if (dataset == null) {
            return "badrequest";
        }

        if (dataset.getDatasetType().equals(Constants.PREDICTION) || !dataset.getSplitType().equals(Constants.NFOLD)
                || foldNumber < 1 || foldNumber > Integer.parseInt(dataset.getNumExternalFolds())) {
            return "badrequest";
        }

        Path foldFilePath = Paths.get(Constants.CECCR_USER_BASE_PATH, dataset.getUserName(), "DATASETS",
                dataset.getName(), dataset.getActFile() + ".fold" + foldNumber);
        Map<String, String> foldCompoundsAndActivities = DatasetFileOperations.getActFileIdsAndValues(foldFilePath
                .toString());
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
