package edu.unc.ceccr.chembench.actions;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.calculations.PredictorEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AdminAction extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(AdminAction.class);
    private final UserRepository userRepository;
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final ExternalValidationRepository externalValidationRepository;

    private final KnnPlusModelRepository knnPlusModelRepository;
    private final SvmModelRepository svmModelRepository;
    private final RandomForestGroveRepository randomForestGroveRepository;
    private final RandomForestTreeRepository randomForestTreeRepository;

    private final RandomForestParametersRepository randomForestParametersRepository;
    private final SvmParametersRepository svmParametersRepository;
    private final KnnPlusParametersRepository knnPlusParametersRepository;

    private List<User> users;
    private String userName;
    private boolean isAdmin;
    private boolean canDownloadDescriptors;
    private long datasetId;
    private long predictorId;
    private String predictorType;

    @Autowired
    public AdminAction(UserRepository userRepository, DatasetRepository datasetRepository,
                       PredictorRepository predictorRepository, PredictionRepository predictionRepository,
                       PredictionValueRepository predictionValueRepository,
                       ExternalValidationRepository externalValidationRepository,
                       KnnPlusModelRepository knnPlusModelRepository, SvmModelRepository svmModelRepository,
                       RandomForestGroveRepository randomForestGroveRepository,
                       RandomForestTreeRepository randomForestTreeRepository,
                       RandomForestParametersRepository randomForestParametersRepository,
                       SvmParametersRepository svmParametersRepository,
                       KnnPlusParametersRepository knnPlusParametersRepository) {
        this.userRepository = userRepository;
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.externalValidationRepository = externalValidationRepository;
        this.knnPlusModelRepository = knnPlusModelRepository;
        this.svmModelRepository = svmModelRepository;
        this.randomForestGroveRepository = randomForestGroveRepository;
        this.randomForestTreeRepository = randomForestTreeRepository;
        this.randomForestParametersRepository = randomForestParametersRepository;
        this.svmParametersRepository = svmParametersRepository;
        this.knnPlusParametersRepository = knnPlusParametersRepository;
    }

    public String execute() {
        users = userRepository.findAll();
        return SUCCESS;
    }

    public String changeUserFlags() {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return "badrequest";
        }
        user.setIsAdmin(Utility.booleanToString(isAdmin));
        user.setCanDownloadDescriptors(Utility.booleanToString(canDownloadDescriptors));
        userRepository.save(user);
        return SUCCESS;
    }

    /**
     * Method responsible for converting predictor from private to public use
     *
     * @return
     */
    public String makePredictorPublic() {
        Predictor predictor = predictorRepository.findOne(predictorId);
        if (predictor == null) {
            return ERROR;
        }
        if (predictor.getParentId() != null) {
            Predictor parentPredictor = predictorRepository.findOne(predictor.getParentId());
            if (parentPredictor == null) {
                return ERROR;
            }
            predictor = parentPredictor;
        }

        // idiot proof if someone will try to make public predictor public again.
        if (predictor.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
            return SUCCESS;
        }

        //prevent duplication of names
        if (predictorRepository.findByNameAndUserName(predictor.getName(), Constants.ALL_USERS_USERNAME) != null) {
            addActionError("There is already a public predictor with the same name as this one.");
            return ERROR;
        }

        Dataset dataset = datasetRepository.findOne(predictor.getDatasetId());
        if (dataset == null) {
            return ERROR;
        } else if (!dataset.getUserName().trim().equals(Constants.ALL_USERS_USERNAME)) {
            this.datasetId = dataset.getId();
            String datasetResult = makeDatasetPublic();
            if (datasetResult.equals(ERROR)) {
                return ERROR;
            }
        }

        String allUserPredictorDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME +
                "/PREDICTORS/" + predictor.getName();
        String predictorUserName = predictor.getUserName();
        String userPredictorDir = Constants.CECCR_USER_BASE_PATH + predictorUserName + "/PREDICTORS/" + predictor.getName();

        //copy files to all users folder
        logger.debug("Start copying files from '" + userPredictorDir + "' to '" + allUserPredictorDir + "'");
        String cmd = "cp -r " + userPredictorDir + " " + allUserPredictorDir;
        RunExternalProgram.runCommand(cmd, "");

        //starting database records cloning process
        Long predictorId = predictor.getId();
        Long newPredictorId = null;
        //duplicating predictor record
        logger.debug("------DB: Duplicating predictor record for predictor: " + predictor.getName());
        predictor.setId(null);
        predictor.setUserName(Constants.ALL_USERS_USERNAME);
        predictor.setPredictorType(predictorType);
        predictor.setDatasetId(dataset.getId());
        predictorRepository.save(predictor);
        newPredictorId = predictor.getId();

        //taking care of external validation table
        logger.debug("------//taking care of external validation table");

        List<ExternalValidation> extValidation = externalValidationRepository.findByPredictorId(predictorId);
        for (ExternalValidation exVal : extValidation) {
            exVal.setExternalValId(null);
            exVal.setPredictorId(newPredictorId);
            externalValidationRepository.save(exVal);
        }

        //taking care of knnPlusModel table
        logger.debug("------//taking care of knnPlusModel table");
        List<KnnPlusModel> knnPlusModels = knnPlusModelRepository.findByPredictorId(predictorId);
        for (KnnPlusModel knnPlusModel : knnPlusModels) {
            knnPlusModel.setId(null);
            knnPlusModel.setPredictorId(newPredictorId);
            knnPlusModelRepository.save(knnPlusModel);
        }

        //taking care of SVM table
        logger.debug("------//taking care of SVM table");
        List<SvmModel> svmModels = svmModelRepository.findByPredictorId(predictorId);
        for (SvmModel svmModel : svmModels) {
            svmModel.setId(null);
            svmModel.setPredictorId(newPredictorId);
            svmModelRepository.save(svmModel);
        }

        //taking care of RandomForest table
        logger.debug("------//taking care of RandomForest table");
        List<RandomForestGrove> groves = randomForestGroveRepository.findByPredictorId(predictorId);
        for (RandomForestGrove grove : groves) {
            Long oldGroveId = grove.getId();
            grove.setId(null);
            grove.setPredictorId(newPredictorId);
            randomForestGroveRepository.save(grove);
            List<RandomForestTree> trees = randomForestTreeRepository.findByRandomForestGroveId(oldGroveId);
            for (RandomForestTree tree : trees) {
                tree.setId(null);
                tree.setRandomForestGroveId(grove.getId());
                randomForestTreeRepository.save(tree);
            }
        }

        //taking care of modeling parameters
        logger.debug("------//taking care of modeling parameters");

        Predictor oldPredictor = predictorRepository.findOne(predictorId);
        if (oldPredictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
            logger.debug("------//RANDOMFOREST");
            RandomForestParameters randomForestParameters =
                    randomForestParametersRepository.findOne(oldPredictor.getModelingParametersId());
            randomForestParameters.setId(null);
            randomForestParametersRepository.save(randomForestParameters);
            predictor.setModelingParametersId(randomForestParameters.getId());
        } else if (oldPredictor.getModelMethod().equals(Constants.KNNGA) || oldPredictor.getModelMethod()
                .equals(Constants.KNNSA)) {
            logger.debug("------//KNN+");
            KnnPlusParameters knnPlusParameters =
                    knnPlusParametersRepository.findOne(oldPredictor.getModelingParametersId());
            knnPlusParameters.setId(null);
            knnPlusParametersRepository.save(knnPlusParameters);
            predictor.setModelingParametersId(knnPlusParameters.getId());
        } else if (oldPredictor.getModelMethod().equals(Constants.SVM)) {
            logger.debug("------//SVM");
            SvmParameters svmParameters = svmParametersRepository.findOne(oldPredictor.getModelingParametersId());
            svmParameters.setId(null);
            svmParametersRepository.save(svmParameters);
            predictor.setModelingParametersId(svmParameters.getId());
        }

        logger.debug("--------Old predictor ID=" + predictorId + " -> new one = " + newPredictorId);

        //duplicating child predictors
        String[] predictorChildren = null;
        String newChildIds = null;
        if (predictor.getChildIds() != null) {
            logger.debug("--------Child predictor IDs=" + predictor.getChildIds());
            newChildIds = "";
            predictorChildren = predictor.getChildIds().split("\\s+");
            for (String id : predictorChildren) {
                logger.debug("--------Child predictor ID=" + id + " longId=" + Long.parseLong(id));
                Predictor child = predictorRepository.findOne(Long.parseLong(id));
                if (child != null) {
                    child.setId(null);
                    child.setUserName(Constants.ALL_USERS_USERNAME);
                    child.setPredictorType("Hidden");
                    child.setDatasetId(dataset.getId());
                    child.setParentId(newPredictorId);
                    predictorRepository.save(child);
                    Long newId = child.getId();
                    newChildIds += newId.toString() + " ";

                    //taking care of external validation table
                    extValidation = externalValidationRepository.findByPredictorId(Long.parseLong(id));
                    for (ExternalValidation exVal : extValidation) {
                        exVal.setExternalValId(null);
                        exVal.setPredictorId(newId);
                        externalValidationRepository.save(exVal);
                    }

                    if (child.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
                        RandomForestParameters randomForestParameters =
                                randomForestParametersRepository.findOne(child.getModelingParametersId());
                        randomForestParameters.setId(null);
                        randomForestParametersRepository.save(randomForestParameters);
                        child.setModelingParametersId(randomForestParameters.getId());
                    } else if (child.getModelMethod().equals(Constants.KNNGA) || child.getModelMethod()
                            .equals(Constants.KNNSA)) {
                        KnnPlusParameters knnPlusParameters =
                                knnPlusParametersRepository.findOne(child.getModelingParametersId());
                        knnPlusParameters.setId(null);
                        knnPlusParametersRepository.save(knnPlusParameters);
                        child.setModelingParametersId(knnPlusParameters.getId());
                    } else if (child.getModelMethod().equals(Constants.SVM)) {
                        SvmParameters svmParameters = svmParametersRepository.findOne(child.getModelingParametersId());
                        svmParameters.setId(null);
                        svmParametersRepository.save(svmParameters);
                        child.setModelingParametersId(svmParameters.getId());
                    }
                    //taking care of RandomForest table
                    logger.debug("------//taking care of RandomForest table");

                    groves = randomForestGroveRepository.findByPredictorId(Long.parseLong(id));
                    for (RandomForestGrove grove : groves) {
                        Long oldId = grove.getId();
                        grove.setId(null);
                        grove.setPredictorId(newId);
                        randomForestGroveRepository.save(grove);
                        List<RandomForestTree> trees = randomForestTreeRepository.findByRandomForestGroveId(oldId);
                        for (RandomForestTree tree : trees) {
                            tree.setId(null);
                            tree.setRandomForestGroveId(grove.getId());
                            randomForestTreeRepository.save(tree);
                        }
                    }
                    predictorRepository.save(child);
                }
            }
        }

        //updating newly created predictor with new child ids
        predictor.setChildIds(newChildIds);
        logger.debug("--------New child predictor IDs=" + newChildIds);
        predictorRepository.save(predictor);
        return SUCCESS;
    }

    public String makeDatasetPublic() {
        Dataset dataset = datasetRepository.findOne(datasetId);
        if (dataset == null) {
            return ERROR;
        }

        // idiot proof if someone will try to make public dataset public again.
        if (dataset.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
            return SUCCESS;
        }

        //prevent duplication of names
        if (datasetRepository.findByNameAndUserName(dataset.getName(), Constants.ALL_USERS_USERNAME) != null) {
            addActionError("There is already been a public dataset with the same name as this one.");
            return ERROR;
        }

        String allUserDatasetDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/DATASETS/" +
                dataset.getName();
        String userDatasetDir = Constants.CECCR_USER_BASE_PATH + dataset.getUserName() + "/DATASETS/" + dataset.getName();

        //copy files to all users folder
        logger.debug("Start copying files from '" + userDatasetDir + "' to '" + allUserDatasetDir + "'");

        String cmd = "cp -r " + userDatasetDir + " " + allUserDatasetDir;
        RunExternalProgram.runCommand(cmd, "");

        //starting database records cloning process

        //duplicating dataset record
        logger.debug("------DB: Duplicating dataset record for dataset: " + dataset.getName());
        dataset.setId(null);
        dataset.setUserName(Constants.ALL_USERS_USERNAME);
        datasetRepository.save(dataset);
        return SUCCESS;
    }

    public String regenerateCcr() throws Exception {
        logger.info("Starting regeneration of CCR for all category predictors");
        try {
            for (User user : userRepository.findAll()) {
                logger.debug("Regenerating CCR for predictors owned by " + user.getUserName());
                for (Predictor p : predictorRepository.findByUserName(user.getUserName())) {
                    if (p.getActivityType().equals(Constants.CATEGORY)) {
                        logger.debug("Regenerating predictor " + p.getName());
                        PredictorEvaluation.addRSquaredAndCCRToPredictor(p);
                        predictorRepository.save(p);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("An error occurred during regeneration, rolling back: ", e);
            return ERROR;
        }
        logger.info("CCR regeneration complete.");
        return SUCCESS;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getCanDownloadDescriptors() {
        return canDownloadDescriptors;
    }

    public void setCanDownloadDescriptors(boolean canDownloadDescriptors) {
        this.canDownloadDescriptors = canDownloadDescriptors;
    }

    public long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(long datasetId) {
        this.datasetId = datasetId;
    }

    public long getPredictorId() {
        return predictorId;
    }

    public void setPredictorId(long predictorId) {
        this.predictorId = predictorId;
    }

    public String getPredictorType() {
        return predictorType;
    }

    public void setPredictorType(String predictorType) {
        this.predictorType = predictorType;
    }
}
