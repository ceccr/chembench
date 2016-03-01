package edu.unc.ceccr.chembench.persistence;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.chembench.workflows.descriptors.ReadDescriptors;
import org.apache.log4j.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.neighboursearch.KDTree;

import javax.persistence.*;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Entity
@Table(name = "cbench_dataset")
public class Dataset implements java.io.Serializable {
    private static final Logger logger = Logger.getLogger(Dataset.class.getName());

    private Long id;
    private String name;
    private String userName;
    private String actFile;
    private String sdfFile;
    private String xFile;
    private String modelType; // continuous or category
    private String datasetType; // prediction, modeling, predictionwithdescriptors, modelingwithdescriptors
    private String uploadedDescriptorType; // used for modelingwithdescriptors
    private int numCompound;
    private Date createdTime;
    private String description;
    private String actFormula;
    private String showByDefault;
    private String paperReference;
    private String hasBeenViewed;
    private String availableDescriptors;
    private String jobCompleted; // Initially NO; YES on completion.

    private String standardize;
    private String splitType;
    private String hasBeenScaled;
    private String numExternalCompounds;
    private String useActivityBinning;
    private String externalCompoundList;
    private String numExternalFolds;
    private int hasVisualization;

    private double modi;
    private boolean modiGenerated = false;

    public boolean canGenerateModi() {
        return actFile != null && !actFile.isEmpty() && (availableDescriptors.contains(Constants.DRAGONH)
                || availableDescriptors.contains(Constants.CDK));
    }

    public void generateModi(DatasetRepository datasetRepository) throws Exception {
        if (modiGenerated) {
            return;
        }

        if (canGenerateModi()) {
            Path baseDir = getDirectoryPath();
            Path descriptorDir = baseDir.resolve("Descriptors");
            List<String> descriptorNames = Lists.newArrayList();
            List<Descriptors> descriptorValueMatrix = Lists.newArrayList();

            // read in descriptors, preferring Dragon when available (as it has the most descriptors)
            // but use CDK as a fallback if it's not available
            if (availableDescriptors.contains(Constants.DRAGONH)) {
                Path dragonDescriptorFile = descriptorDir.resolve(sdfFile + ".dragonH");
                ReadDescriptors
                        .readDragonDescriptors(dragonDescriptorFile.toString(), descriptorNames, descriptorValueMatrix);
            } else if (availableDescriptors.contains(Constants.CDK)) {
                Path cdkDescriptorFile = descriptorDir.resolve(sdfFile + ".cdk.x");
                ReadDescriptors.readXDescriptors(cdkDescriptorFile.toString(), descriptorNames, descriptorValueMatrix);
            }

            // read in activities so we can append them to the input file for Weka
            Map<String, String> activityMap =
                    DatasetFileOperations.getActFileIdsAndValues(baseDir.resolve(actFile).toString());

            // create a csv input file for Weka with activities included
            Path wekaInputFile = Files.createTempFile(getDirectoryPath(), "weka", ".csv");
            wekaInputFile.toFile().deleteOnExit();
            BufferedWriter writer = Files.newBufferedWriter(wekaInputFile, StandardCharsets.UTF_8);
            List<String> header = Lists.newArrayList(descriptorNames);
            header.add(0, "Activity");
            Joiner joiner = Joiner.on("\t");
            writer.write(joiner.join(header));
            writer.newLine();
            Splitter splitter = Splitter.on(' ');
            for (Descriptors d : descriptorValueMatrix) {
                List<String> values = Lists.newArrayList(splitter.omitEmptyStrings()
                        .splitToList(d.getDescriptorValues()));
                values.add(0, activityMap.get(d.getCompoundName()));
                writer.write(joiner.join(values));
                writer.newLine();
            }
            writer.close();

            // now convert the csv into a Weka dataset
            CSVLoader loader = new CSVLoader();
            loader.setFieldSeparator("\t");
            loader.setNoHeaderRowPresent(false);
            loader.setSource(wekaInputFile.toFile());
            if (this.isCategory()) {
                loader.setNominalAttributes("1");
            }
            Instances dataset = loader.getDataSet();
            Attribute activity = dataset.attribute("Activity");
            if (this.isCategory()) {
                assert activity.isNominal();
            } else if (this.isContinuous()) {
                assert activity.isNumeric();
            }
            dataset.setClass(activity);

            KDTree kdt = new KDTree();
            EuclideanDistance df = new EuclideanDistance();
            df.setDontNormalize(false);
            kdt.setDistanceFunction(df);
            IBk classifier = new IBk(5);
            classifier.setNearestNeighbourSearchAlgorithm(kdt);
            classifier.buildClassifier(dataset);
            Evaluation evaluation = new Evaluation(dataset);
            evaluation.crossValidateModel(classifier, dataset, 5, new Random(0));
            if (this.isCategory()) {
                this.modi = 1 - evaluation.errorRate();
            } else if (this.isContinuous()) {
                this.modi = evaluation.correlationCoefficient();
            }
            this.modiGenerated = true;
            datasetRepository.save(this);
        } else {
            throw new IllegalStateException("MODI cannot be generated for this dataset");
        }
    }

    @Transient
    public Path getDirectoryPath() {
        Path basePath = Paths.get(Constants.CECCR_USER_BASE_PATH, userName);
        if (jobCompleted.equals(Constants.YES)) {
            return basePath.resolve("DATASETS").resolve(name);
        } else {
            return basePath.resolve(name);
        }
    }

    @Transient
    public boolean hasActivities() {
        return actFile != null && !actFile.isEmpty();
    }

    @Transient
    public boolean hasStructures() {
        return sdfFile != null && !sdfFile.isEmpty();
    }

    @Transient
    public boolean isCategory() {
        return modelType.equalsIgnoreCase(Constants.CATEGORY);
    }

    @Transient
    public boolean isContinuous() {
        return modelType.equalsIgnoreCase(Constants.CONTINUOUS);
    }

    @Transient
    public boolean isPublic() {
        return userName.equals(Constants.ALL_USERS_USERNAME);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "username")
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "actFile")
    public String getActFile() {
        return this.actFile;
    }

    public void setActFile(String actFile) {
        this.actFile = actFile;
    }

    @Column(name = "sdfFile")
    public String getSdfFile() {
        return this.sdfFile;
    }

    public void setSdfFile(String sdfFile) {
        this.sdfFile = sdfFile;
    }

    @Column(name = "xFile")
    public String getXFile() {
        return xFile;
    }

    public void setXFile(String xFile) {
        this.xFile = xFile;
    }

    @Column(name = "datasetType")
    public String getDatasetType() {
        return datasetType;
    }

    public void setDatasetType(String datasetType) {
        this.datasetType = datasetType;
    }

    @Column(name = "modelType")
    public String getModelType() {
        return this.modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    @Column(name = "uploadedDescriptorType")
    public String getUploadedDescriptorType() {
        return uploadedDescriptorType;
    }

    public void setUploadedDescriptorType(String uploadedDescriptorType) {
        this.uploadedDescriptorType = uploadedDescriptorType;
    }

    @Column(name = "numCompound")
    public int getNumCompound() {
        return this.numCompound;
    }

    public void setNumCompound(int num) {
        this.numCompound = num;
    }

    @Column(name = "createdTime")
    public Date getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Date date) {
        this.createdTime = date;
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
    }

    @Column(name = "actFormula")
    public String getActFormula() {
        return actFormula;
    }

    public void setActFormula(String actFormula) {
        this.actFormula = actFormula;
    }

    @Column(name = "showByDefault")
    public String getShowByDefault() {
        return showByDefault;
    }

    public void setShowByDefault(String showByDefault) {
        this.showByDefault = showByDefault;
    }

    @Column(name = "paperReference")
    public String getPaperReference() {
        return paperReference;
    }

    public void setPaperReference(String paperReference) {
        this.paperReference = paperReference;
    }

    @Column(name = "hasBeenViewed")
    public String getHasBeenViewed() {
        return hasBeenViewed;
    }

    public void setHasBeenViewed(String hasBeenViewed) {
        this.hasBeenViewed = hasBeenViewed;
    }

    @Column(name = "availableDescriptors")
    public String getAvailableDescriptors() {
        return availableDescriptors;
    }

    public void setAvailableDescriptors(String availableDescriptors) {
        this.availableDescriptors = availableDescriptors;
    }

    @Column(name = "jobCompleted")
    public String getJobCompleted() {
        return jobCompleted;
    }

    public void setJobCompleted(String jobCompleted) {
        this.jobCompleted = jobCompleted;
    }

    @Column(name = "standardize")
    public String getStandardize() {
        return standardize;
    }

    public void setStandardize(String standardize) {
        this.standardize = standardize;
    }

    @Column(name = "splitType")
    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    @Column(name = "hasBeenScaled")
    public String getHasBeenScaled() {
        return hasBeenScaled;
    }

    public void setHasBeenScaled(String hasBeenScaled) {
        this.hasBeenScaled = hasBeenScaled;
    }

    @Column(name = "numExternalCompounds")
    public String getNumExternalCompounds() {
        return numExternalCompounds;
    }

    public void setNumExternalCompounds(String numExternalCompounds) {
        this.numExternalCompounds = numExternalCompounds;
    }

    @Column(name = "useActivityBinning")
    public String getUseActivityBinning() {
        return useActivityBinning;
    }

    public void setUseActivityBinning(String useActivityBinning) {
        this.useActivityBinning = useActivityBinning;
    }

    @Column(name = "externalCompoundList")
    public String getExternalCompoundList() {
        return externalCompoundList;
    }

    public void setExternalCompoundList(String externalCompoundList) {
        this.externalCompoundList = externalCompoundList;
    }

    @Column(name = "numExternalFolds")
    public String getNumExternalFolds() {
        return numExternalFolds;
    }

    public void setNumExternalFolds(String numExternalFolds) {
        this.numExternalFolds = numExternalFolds;
    }

    @Column(name = "hasVisualization")
    public int getHasVisualization() {
        return hasVisualization;
    }

    public void setHasVisualization(int hasVisualization) {
        this.hasVisualization = hasVisualization;
    }

    public double getModi() {
        return modi;
    }

    public void setModi(double modi) {
        this.modi = modi;
    }

    @Column(name = "modiGenerated")
    public boolean isModiGenerated() {
        return modiGenerated;
    }

    public void setModiGenerated(boolean modiGenerated) {
        this.modiGenerated = modiGenerated;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
