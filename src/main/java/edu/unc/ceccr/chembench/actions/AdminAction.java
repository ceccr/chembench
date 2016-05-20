package edu.unc.ceccr.chembench.actions;

import com.opensymphony.xwork2.ActionContext;
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
        ActionContext context = ActionContext.getContext();
        try {
            String predictorName = ((String[]) context.getParameters().get("predictorName"))[0];
            String userName = ((String[]) context.getParameters().get("userName"))[0];
            String predictorType = ((String[]) context.getParameters().get("predictorType"))[0];

            if (predictorName.isEmpty() || userName.isEmpty() || predictorType.isEmpty()) {
                addActionError("Predictor name, user name and predictor type shouldn't be empty!");
                return ERROR;
            }

            logger.debug("++++++++++++++++++Predictor name:" + predictorName + " User name=" + userName);


            Predictor predictor = predictorRepository.findByNameAndUserName(predictorName, userName);
            if (predictor == null) {
                return ERROR;
            }

            // idiot proof if someone will try to make public predictor public again.
            if (predictor.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
                return SUCCESS;
            }

            //prevent duplication of names
            if (predictorRepository.findByNameAndUserName(predictorName, Constants.ALL_USERS_USERNAME) != null) {
                addActionError("There has already been a public predictor with" + predictorName);
                return ERROR;
            }

            Dataset dataset = datasetRepository.findOne(predictor.getDatasetId());
            if (dataset == null) {
                return ERROR;
            }

            //check if predictor is based on the public dataset
            boolean isDatasetPublic = false;
            if (dataset.getUserName().trim().equals(Constants.ALL_USERS_USERNAME)) {
                logger.debug("**************DATASET IS ALREADY PUBLIC!");
                isDatasetPublic = true;
            }

            //check if any other dataset with the same name is already public

            Dataset checkPublicDataset =
                    datasetRepository.findByNameAndUserName(dataset.getName(), Constants.ALL_USERS_USERNAME);
            if (checkPublicDataset != null) {
                isDatasetPublic = true;
                dataset = checkPublicDataset;
            }

            String allUserDatasetDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/DATASETS/" +
                    dataset.getName();
            String allUserPredictorDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME +
                    "/PREDICTORS/" + predictor.getName();

            String userDatasetDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + dataset.getName();
            String userPredictorDir = Constants.CECCR_USER_BASE_PATH + userName + "/PREDICTORS/" + predictor.getName();

            //copy files to all users folder
            logger.debug("Start copying files from '" + userDatasetDir + "' to '" + allUserDatasetDir + "'");
            if (!isDatasetPublic) {
                String cmd = "cp -r " + userDatasetDir + " " + allUserDatasetDir;
                RunExternalProgram.runCommand(cmd, "");
            }
            logger.debug("Start copying files from '" + userPredictorDir + "' to '" + allUserPredictorDir + "'");
            String cmd = "cp -r " + userPredictorDir + " " + allUserPredictorDir;
            RunExternalProgram.runCommand(cmd, "");

            //starting database records cloning process

            if (!isDatasetPublic) {
                //duplicating dataset record
                logger.debug("------DB: Duplicating dataset record for dataset: " + dataset.getName());
                dataset.setId(null);
                dataset.setUserName(Constants.ALL_USERS_USERNAME);
                datasetRepository.save(dataset);
            }


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
                exVal.setPredictorId(newPredictorId);
                externalValidationRepository.save(exVal);
            }

            //taking care of knnPlusModel table
            logger.debug("------//taking care of knnPlusModel table");
            List<KnnPlusModel> knnPlusModels = knnPlusModelRepository.findByPredictorId(predictorId);
            for (KnnPlusModel knnPlusModel : knnPlusModels) {
                knnPlusModel.setPredictorId(newPredictorId);
                knnPlusModelRepository.save(knnPlusModel);
            }

            //taking care of SVM table
            logger.debug("------//taking care of SVM table");
            List<SvmModel> svmModels = svmModelRepository.findByPredictorId(predictorId);
            for (SvmModel svmModel : svmModels) {
                svmModel.setPredictorId(newPredictorId);
                svmModelRepository.save(svmModel);
            }

            //taking care of RandomForest table
            logger.debug("------//taking care of RandomForest table");
            List<RandomForestGrove> groves = randomForestGroveRepository.findByPredictorId(predictorId);
            for (RandomForestGrove grove : groves) {
                Long oldGroveId = grove.getId();
                grove.setPredictorId(newPredictorId);
                randomForestGroveRepository.save(grove);
                List<RandomForestTree> trees = randomForestTreeRepository.findByRandomForestGroveId(oldGroveId);
                for (RandomForestTree tree : trees) {
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
                randomForestParametersRepository.save(randomForestParameters);
                predictor.setModelingParametersId(randomForestParameters.getId());
            } else if (oldPredictor.getModelMethod().equals(Constants.KNNGA) || oldPredictor.getModelMethod()
                    .equals(Constants.KNNSA)) {
                logger.debug("------//KNN+");
                KnnPlusParameters knnPlusParameters =
                        knnPlusParametersRepository.findOne(oldPredictor.getModelingParametersId());
                knnPlusParametersRepository.save(knnPlusParameters);
                predictor.setModelingParametersId(knnPlusParameters.getId());
            } else if (oldPredictor.getModelMethod().equals(Constants.SVM)) {
                logger.debug("------//SVM");
                SvmParameters svmParameters = svmParametersRepository.findOne(oldPredictor.getModelingParametersId());
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
                            exVal.setPredictorId(newId);
                            externalValidationRepository.save(exVal);
                        }


                        if (child.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
                            RandomForestParameters randomForestParameters =
                                    randomForestParametersRepository.findOne(child.getModelingParametersId());
                            randomForestParametersRepository.save(randomForestParameters);
                            child.setModelingParametersId(randomForestParameters.getId());
                        } else if (child.getModelMethod().equals(Constants.KNNGA) || child.getModelMethod()
                                .equals(Constants.KNNSA)) {
                            KnnPlusParameters knnPlusParameters =
                                    knnPlusParametersRepository.findOne(child.getModelingParametersId());
                            knnPlusParametersRepository.save(knnPlusParameters);
                            child.setModelingParametersId(knnPlusParameters.getId());
                        } else if (child.getModelMethod().equals(Constants.SVM)) {
                            SvmParameters svmParameters =
                                    svmParametersRepository.findOne(child.getModelingParametersId());
                            svmParametersRepository.save(svmParameters);
                            child.setModelingParametersId(svmParameters.getId());
                        }
                        //taking care of RandomForest table
                        logger.debug("------//taking care of RandomForest table");

                        groves = randomForestGroveRepository.findByPredictorId(Long.parseLong(id));
                        for (RandomForestGrove grove : groves) {
                            Long oldId = grove.getId();
                            grove.setPredictorId(newId);
                            randomForestGroveRepository.save(grove);
                            List<RandomForestTree> trees = randomForestTreeRepository.findByRandomForestGroveId(oldId);
                            for (RandomForestTree tree : trees) {
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
        } catch (Exception ex) {
            logger.error("", ex);
            return ERROR;
        }
        return SUCCESS;
    }

    public String makeDatasetPublic() {
        ActionContext context = ActionContext.getContext();
        try {
            String datasetName = ((String[]) context.getParameters().get("datasetName"))[0];
            String userName = ((String[]) context.getParameters().get("userName"))[0];

            if (datasetName.isEmpty() || userName.isEmpty()) {
                return ERROR;
            }

            logger.debug("++++++++++++++++++Dataset name:" + datasetName + " User name=" + userName);

            Dataset dataset = datasetRepository.findByNameAndUserName(datasetName, userName);
            if (dataset == null) {
                addActionError("User " + userName + " does not have a dataset with Name " + datasetName);
                return ERROR;
            }

            // idiot proof if someone will try to make public dataset public again.
            if (dataset.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
                return SUCCESS;
            }

            //prevent duplication of names
            if (datasetRepository.findByNameAndUserName(datasetName, Constants.ALL_USERS_USERNAME) != null) {
                addActionError("There has already been a public Dataset with the same name" + datasetName);
                return ERROR;
            }

            String allUserDatasetDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/DATASETS/" +
                    dataset.getName();
            String userDatasetDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + dataset.getName();

            //copy files to all users folder
            logger.debug("Start copying files from '" + userDatasetDir + "' to '" + allUserDatasetDir + "'");

            String cmd = "cp -r " + userDatasetDir + " " + allUserDatasetDir;
            RunExternalProgram.runCommand(cmd, "");

            //starting database records cloning process

            //duplicating dataset record
            logger.debug("------DB: Duplicating dataset record for dataset: " + dataset.getName());
            dataset.setUserName(Constants.ALL_USERS_USERNAME);
            datasetRepository.save(dataset);
        } catch (Exception ex) {
            logger.error("", ex);
            return ERROR;
        }

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
}
