package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import edu.unc.ceccr.chembench.workflows.calculations.RSquaredAndCCR;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AdminAction extends ActionSupport {

    private static final Logger logger = Logger.getLogger(AdminAction.class.getName());
    private final UserRepository userRepository;
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final PredictionRepository predictionRepository;

    private final PredictionValueRepository predictionValueRepository;
    private final ExternalValidationRepository externalValidationRepository;

    private final KnnPlusModelRepository knnPlusModelRepository;
    private final SvmModelRepository svmModelRepository;
    private final RandomForestGroveRepository randomForestGroveRepository;
    private final RandomForestTreeRepository randomForestTreeRepository;

    private final RandomForestParametersRepository randomForestParametersRepository;
    private final SvmParametersRepository svmParametersRepository;
    private final KnnPlusParametersRepository knnPlusParametersRepository;

    User user = User.getCurrentUser();
    String buildDate;
    List<User> users;
    //for sending email to all users
    String emailMessage;
    String emailSubject;
    String sendTo;
    private List<String> errorStrings = Lists.newArrayList();

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
        this.predictionRepository = predictionRepository;
        this.predictionValueRepository = predictionValueRepository;
        this.externalValidationRepository = externalValidationRepository;
        this.knnPlusModelRepository = knnPlusModelRepository;
        this.svmModelRepository = svmModelRepository;
        this.randomForestGroveRepository = randomForestGroveRepository;
        this.randomForestTreeRepository = randomForestTreeRepository;
        this.randomForestParametersRepository = randomForestParametersRepository;
        this.svmParametersRepository = svmParametersRepository;
        this.knnPlusParametersRepository = knnPlusParametersRepository;
    }

    public String loadPage() throws Exception {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        buildDate = Constants.BUILD_DATE;
        users = userRepository.findAll();
        return SUCCESS;
    }

    public String loadEmailAllUsersPage() throws Exception {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        sendTo = "JUSTME";
        return SUCCESS;
    }

    public String emailSelectedUsers() throws Exception {
        logger.debug("emailing SELECTED user(s)");
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        if (!sendTo.trim().isEmpty() && !emailMessage.trim().isEmpty() && !emailSubject.trim().isEmpty()) {
            List<String> emails = Arrays.asList(sendTo.split(";"));
            Iterator<String> it = emails.iterator();
            while (it.hasNext()) {
                String email = it.next();
                if (!email.trim().isEmpty()) {
                    SendEmails.sendEmail(email, "", "", emailSubject, emailMessage);
                }
            }
        }
        return SUCCESS;
    }

    public String emailAllUsers() throws Exception {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        List<User> userList = userRepository.findAll();

        if (sendTo.equals("ALLUSERS") && !emailMessage.trim().isEmpty() && !emailSubject.trim().isEmpty()) {
            Iterator<User> it = userList.iterator();
            while (it.hasNext()) {
                User userInfo = it.next();
                SendEmails.sendEmail(userInfo.getEmail(), "", "", emailSubject, emailMessage);
            }
        } else if (sendTo.equals("JUSTME") && !emailMessage.trim().isEmpty() && !emailSubject.trim().isEmpty()) {
            SendEmails.sendEmail(user.getEmail(), "", "", emailSubject, emailMessage);
        }
        return SUCCESS;
    }

    public String changeUserAdminStatus() throws Exception {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        //get the current user and the username of the user to be altered
        ActionContext context = ActionContext.getContext();
        String userNameToChange = ((String[]) context.getParameters().get("userToChange"))[0];

        User userToChange = null;
        if (userNameToChange.equals(user.getUserName())) {
            userToChange = user;
        } else {
            userToChange = userRepository.findByUserName(userNameToChange);
        }

        if (userToChange.getIsAdmin().equals(Constants.YES)) {
            userToChange.setIsAdmin(Constants.NO);
        } else {
            userToChange.setIsAdmin(Constants.YES);
        }
        userRepository.save(userToChange);
        return SUCCESS;
    }

    public String changeUserDescriptorDownloadStatus() throws Exception {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        //get the current user and the username of the user to be altered
        ActionContext context = ActionContext.getContext();
        String userNameToChange = ((String[]) context.getParameters().get("userToChange"))[0];
        User userToChange = null;
        if (userNameToChange.equals(user.getUserName())) {
            userToChange = user;
        } else {
            userToChange = userRepository.findByUserName(userNameToChange);
        }

        if (userToChange.getCanDownloadDescriptors().equals(Constants.YES)) {
            userToChange.setCanDownloadDescriptors(Constants.NO);
        } else {
            userToChange.setCanDownloadDescriptors(Constants.YES);
        }
        userRepository.save(userToChange);
        return SUCCESS;
    }

    public String deletePredictor() {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        ActionContext context = ActionContext.getContext();
        try {
            Map<String, Object> params = context.getParameters();
            String predictorName = ((String[]) params.get("predictorName"))[0];
            String userName = ((String[]) params.get("userName"))[0];
            if (userName.isEmpty()) {
                // assume that no username means public
                userName = Constants.ALL_USERS_USERNAME;
            }

            if (predictorName.isEmpty()) {
                errorStrings.add("Please enter a predictor name.");
                return ERROR;
            }

            Predictor predictor = predictorRepository.findByNameAndUserName(predictorName, userName);
            if (predictor == null) {
                String error;
                if (userName.equals(Constants.ALL_USERS_USERNAME)) {
                    error = "No public predictor with name " + predictorName +
                            " was found in the database.";
                } else {
                    error = String.format(
                            "No predictor belonging to user %s with name %s " + "was found in the database.", userName,
                            predictorName);
                }
                errorStrings.add(error);
                return ERROR;
            }

            (new DeleteAction()).deletePredictor(predictor);
        } catch (Exception ex) {
            errorStrings.add(ex.getMessage());
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * method responsible for deletion of the public prediction
     *
     * @return
     */
    public String deletePublicPrediction() {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        ActionContext context = ActionContext.getContext();
        try {
            String predictionID = ((String[]) context.getParameters().get("predictionName"))[0];
            String userName = ((String[]) context.getParameters().get("userName"))[0];

            if (predictionID.isEmpty() || userName.isEmpty()) {
                errorStrings.add("Prediction ID and user name shouldn't be empty!");
                return ERROR;
            }

            if (!userName.trim().equals(Constants.ALL_USERS_USERNAME)) {
                errorStrings.add("You can only delete public prediction here!");
                return ERROR;
            }

            Prediction prediction = predictionRepository.findOne(Long.parseLong(predictionID));
            if (prediction == null) {
                errorStrings.add("No prediction with ID " + predictionID + " was found in the database!");
                return ERROR;
            }

            //delete the files associated with this prediction
            String dir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/PREDICTIONS/" + prediction
                    .getName();
            if (!FileAndDirOperations.deleteDir(new File(dir))) {
                errorStrings.add("Error deleting dir");
                logger.error("error deleting dir: " + dir);
            }

            //delete the prediction values associated with the prediction
            List<PredictionValue> pvs = predictionValueRepository.findByPredictionId(prediction.getId());
            if (pvs != null) {
                for (PredictionValue pv : pvs) {
                    predictionValueRepository.delete(pv);
                }
            }

            //delete the database entry for the prediction
            predictionRepository.delete(prediction);
        } catch (Exception ex) {
            errorStrings.add(ex.getMessage());
            return ERROR;
        }
        if (!errorStrings.isEmpty()) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String deleteDataset() {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        ActionContext context = ActionContext.getContext();
        try {
            Map<String, Object> params = context.getParameters();
            String datasetName = ((String[]) params.get("datasetName"))[0];
            String userName = ((String[]) params.get("userName"))[0];
            if (userName.isEmpty()) {
                // assume that no username means public
                userName = Constants.ALL_USERS_USERNAME;
            }

            if (datasetName.isEmpty()) {
                errorStrings.add("Please enter a dataset name.");
                return ERROR;
            }

            Dataset dataset = datasetRepository.findByNameAndUserName(datasetName, userName);
            if (dataset == null) {
                String error;
                if (userName.equals(Constants.ALL_USERS_USERNAME)) {
                    error = "No public dataset with name " + datasetName +
                            " was found in the database.";
                } else {
                    error = String.format(
                            "No dataset belonging to user %s with name %s " + "was found in the database.", userName,
                            datasetName);
                }
                errorStrings.add(error);
                return ERROR;
            }

            // check for predictors depending on this dataset
            List<String> dependencies = checkDatasetDependencies(dataset, userName);
            if (!dependencies.isEmpty()) {
                errorStrings.addAll(dependencies);
                return ERROR;
            }

            //delete the files associated with this dataset
            File dir = Paths.get(Constants.CECCR_USER_BASE_PATH, userName, "DATASETS", dataset.getName()).toFile();

            if (dir.exists()) {
                if (!FileAndDirOperations.deleteDir(dir)) {
                    String error = "Failed to delete directory: " + dir.getAbsolutePath();
                    logger.error(error);
                    errorStrings.add(error);
                }
            }

            //delete the database entry for the dataset
            datasetRepository.delete(dataset);

        } catch (Exception ex) {
            errorStrings.add(ex.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }


    private List<String> checkDatasetDependencies(Dataset ds, String userName)
            throws HibernateException, ClassNotFoundException, SQLException {
        logger.debug("checking dataset dependencies");

        List<String> dependencies = Lists.newArrayList();

        List<Predictor> userPredictors = predictorRepository.findByUserName(userName);
        userPredictors.addAll(predictorRepository.findPublicPredictors());
        List<Prediction> userPredictions = predictionRepository.findByUserName(userName);

        //check each predictor
        for (int i = 0; i < userPredictors.size(); i++) {
            logger.debug("predictor id: " + userPredictors.get(i).getDatasetId() + " dataset id: " + ds.getId());
            if (userPredictors.get(i).getDatasetId() != null && userPredictors.get(i).getDatasetId()
                    .equals(ds.getId())) {
                dependencies.add("The predictor '" + userPredictors.get(i).getName() + "' depends on this dataset. " +
                        "Please delete it first.\n");
            }
        }

        //check each prediction
        for (int i = 0; i < userPredictions.size(); i++) {
            logger.debug("Prediction id: " + userPredictions.get(i).getDatasetId() + " dataset id: " + ds.getId());
            if (userPredictions.get(i).getDatasetId() != null && userPredictions.get(i).getDatasetId()
                    .equals(ds.getId())) {
                dependencies.add("The prediction '" + userPredictions.get(i).getName() + "' depends on this dataset. " +
                        "Please delete it first.\n");
            }
        }
        return dependencies;
    }

    /**
     * Method responsible for converting predictor from private to public use
     *
     * @return
     */
    public String makePredictorPublic() {
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        ActionContext context = ActionContext.getContext();
        try {
            String predictorName = ((String[]) context.getParameters().get("predictorName"))[0];
            String userName = ((String[]) context.getParameters().get("userName"))[0];
            String predictorType = ((String[]) context.getParameters().get("predictorType"))[0];

            if (predictorName.isEmpty() || userName.isEmpty() || predictorType.isEmpty()) {
                errorStrings.add("Predictor name, user name and predictor type shouldn't be empty!");
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
            //if(PopulateDataObjects.getPredictorByName(predictorName, Constants.ALL_USERS_USERNAME,
            // session)!=null) return SUCCESS;
            if (predictorRepository.findByNameAndUserName(predictorName, Constants.ALL_USERS_USERNAME) != null) {
                errorStrings.add("There has already been a public predictor with" + predictorName);
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
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
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
                errorStrings.add("User " + userName + " does not have a dataset with Name " + datasetName);
                return ERROR;
            }

            // idiot proof if someone will try to make public dataset public again.
            if (dataset.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
                return SUCCESS;
            }

            //prevent duplication of names
            //if(PopulateDataObjects.getDataSetByName(datasetName, Constants.ALL_USERS_USERNAME,
            // session)!=null) return SUCCESS;
            if (datasetRepository.findByNameAndUserName(datasetName, Constants.ALL_USERS_USERNAME) != null) {
                errorStrings.add("There has already been a public Dataset with the same name" + datasetName);
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
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }

        logger.info("Starting regeneration of CCR for all category predictors");
        try {
            for (User user : userRepository.findAll()) {
                logger.debug("Regenerating CCR for predictors owned by " + user.getUserName());
                for (Predictor p : predictorRepository.findByUserName(user.getUserName())) {
                    if (p.getActivityType().equals(Constants.CATEGORY)) {
                        logger.debug("Regenerating predictor " + p.getName());
                        RSquaredAndCCR.addRSquaredAndCCRToPredictor(p);
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public List<String> getErrorStrings() {
        return errorStrings;
    }

    public void setErrorStrings(List<String> errorStrings) {
        this.errorStrings = errorStrings;
    }
}
