package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
//struts2

public class AdminAction extends ActionSupport {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(AdminAction.class.getName());
    User user;
    String buildDate;
    List<User> users;
    //for sending email to all users
    String emailMessage;
    String emailSubject;
    String sendTo;
    private List<String> errorStrings = Lists.newArrayList();

    public String loadPage() throws Exception {
        String result = SUCCESS;
        //log the results
        logger.debug("Forwarding user " + user.getUserName() + " to admin page.");

        //set up any values that need to be populated onto the page (dropdowns, lists, display stuff)

        // open database connection
        Session session = HibernateUtil.getSession();

        // Latest Build Date
        buildDate = Constants.BUILD_DATE;

        // list of users
        users = PopulateDataObjects.getAllUsers(session);
        session.close();

        //go to the page
        return result;
    }

    public String loadEmailAllUsersPage() throws Exception {
        sendTo = "JUSTME";
        return SUCCESS;
    }

    public String emailSelectedUsers() throws Exception {
        //check that the user is logged in
        logger.debug("emailing SELECTED user(s)");
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
        //check that the user is logged in
        logger.debug("emailing user(s)");
        Session s = HibernateUtil.getSession();
        List<User> userList = PopulateDataObjects.getAllUsers(s);
        s.close();

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
        //get the current user and the username of the user to be altered
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();
        String userToChange = ((String[]) context.getParameters().get("userToChange"))[0];

        Session s = HibernateUtil.getSession();
        User toChange = null;
        if (userToChange.equals(user.getUserName())) {
            toChange = user;
        } else {
            toChange = PopulateDataObjects.getUserByUserName(userToChange, s);
        }

        if (toChange.getIsAdmin().equals(Constants.YES)) {
            toChange.setIsAdmin(Constants.NO);
        } else {
            toChange.setIsAdmin(Constants.YES);
        }

        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            s.saveOrUpdate(toChange);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        } finally {
            s.close();
        }


        return result;
    }

    public String changeUserDescriptorDownloadStatus() throws Exception {
        //get the current user and the username of the user to be altered
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();
        String userToChange = ((String[]) context.getParameters().get("userToChange"))[0];

        Session s = HibernateUtil.getSession();
        User toChange = null;
        if (userToChange.equals(user.getUserName())) {
            toChange = user;
        } else {
            toChange = PopulateDataObjects.getUserByUserName(userToChange, s);
        }

        if (toChange.getCanDownloadDescriptors().equals(Constants.YES)) {
            toChange.setCanDownloadDescriptors(Constants.NO);
        } else {
            toChange.setCanDownloadDescriptors(Constants.YES);
        }

        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            s.saveOrUpdate(toChange);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        } finally {
            s.close();
        }

        return result;
    }

    public String deletePredictor() {
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.warn("Attempted to access ActionContext but returned null");
        } else {
            user = User.getCurrentUser();
            if (!user.getIsAdmin().equals(Constants.YES)) {
                logger.warn(String.format("Non-admin user %s attempted to delete predictor", user.getUserName()));
                return ERROR;
            }
        }
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

            Session session = HibernateUtil.getSession();
            Predictor predictor = PopulateDataObjects.getPredictorByName(predictorName, userName, session);
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

            (new DeleteAction()).deletePredictor(predictor, session);
            session.close();
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
        String result = SUCCESS;
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
            Session session = HibernateUtil.getSession();
            Prediction prediction = PopulateDataObjects.getPredictionById(Long.parseLong(predictionID), session);
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
            List<PredictionValue> pvs = (ArrayList<PredictionValue>) PopulateDataObjects
                    .getPredictionValuesByPredictionId(prediction.getId(), session);

            if (pvs != null) {
                for (PredictionValue pv : pvs) {
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        session.delete(pv);
                        tx.commit();
                    } catch (RuntimeException e) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        logger.error(e);
                    }
                }
            }

            //delete the database entry for the prediction
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.delete(prediction);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e);
            }

            session.close();

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
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.warn("Attempted to access ActionContext but returned null");
        } else {
            user = User.getCurrentUser();
            if (!user.getIsAdmin().equals(Constants.YES)) {
                logger.warn(String.format("Non-admin user %s attempted to delete dataset", user.getUserName()));
                return ERROR;
            }
        }
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

            Session session = HibernateUtil.getSession();
            Dataset dataset = PopulateDataObjects.getDataSetByName(datasetName, userName, session);
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
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.delete(dataset);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e);
                return ERROR;
            }
            session.close();

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
        Session session = HibernateUtil.getSession();
        List<Predictor> userPredictors =
                (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(userName, true, false, session);
        List<Prediction> userPredictions =
                (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);

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
        String result = SUCCESS;
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

            Session session = HibernateUtil.getSession();
            Predictor predictor = PopulateDataObjects.getPredictorByName(predictorName, userName, session);
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
            if (PopulateDataObjects.getPredictorByName(predictorName, Constants.ALL_USERS_USERNAME, session) != null) {
                errorStrings.add("There has already been a public predictor with" + predictorName);
                return ERROR;
            }

            Dataset dataset = PopulateDataObjects.getDataSetById(predictor.getDatasetId(), session);
            if (dataset == null) {
                return ERROR;
            }
            session.close();

            //check if predictor is based on the public dataset
            boolean isDatasetPublic = false;
            if (dataset.getUserName().trim().equals(Constants.ALL_USERS_USERNAME)) {
                logger.debug("**************DATASET IS ALREADY PUBLIC!");
                isDatasetPublic = true;
            }

            //check if any other dataset with the same name is already public
            session = HibernateUtil.getSession();
            Dataset checkPublicDataset =
                    PopulateDataObjects.getDataSetByName(dataset.getName(), Constants.ALL_USERS_USERNAME, session);
            session.close();
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
                session = HibernateUtil.getSession();
                session.evict(dataset);
                dataset.setId(null);
                dataset.setUserName(Constants.ALL_USERS_USERNAME);
                session.save(dataset);
                session.flush();
                session.close();
            }


            Long predictorId = predictor.getId();
            Long newPredictorId = null;
            //duplicating predictor record
            logger.debug("------DB: Duplicating predictor record for predictor: " + predictor.getName());
            session = HibernateUtil.getSession();
            session.evict(predictor);
            predictor.setId(null);
            predictor.setUserName(Constants.ALL_USERS_USERNAME);
            predictor.setPredictorType(predictorType);
            predictor.setDatasetId(dataset.getId());
            session.save(predictor);
            session.flush();
            newPredictorId = predictor.getId();
            session.close();

            //taking care of external validation table
            logger.debug("------//taking care of external validation table");
            session = HibernateUtil.getSession();
            List<ExternalValidation> extValidation =
                    PopulateDataObjects.getExternalValidationValues(predictorId, session);
            session.close();
            for (ExternalValidation exVal : extValidation) {
                session = HibernateUtil.getSession();
                session.evict(exVal);
                exVal.setExternalValId(-1);
                exVal.setPredictorId(newPredictorId);
                session.save(exVal);
                session.close();
            }

            /*
            //taking care of knnModel table
            logger.debug("------//taking care of knnModel table");
            session = HibernateUtil.getSession();
            List<KnnModel> knnModels = PopulateDataObjects.getModelsByPredictorId(predictorId, session);
            session.close();
            for(KnnModel knnModel:knnModels){
                session = HibernateUtil.getSession();
                session.evict(knnModel);
                knnModel.setId(null);
                knnModel.setPredictorId(newPredictorId);
                session.save(knnModel);
                session.close();
            }
            */

            //taking care of knnPlusModel table
            logger.debug("------//taking care of knnPlusModel table");
            session = HibernateUtil.getSession();
            List<KnnPlusModel> knnPlusModels = PopulateDataObjects.getKnnPlusModelsByPredictorId(predictorId, session);
            session.close();
            for (KnnPlusModel knnPlusModel : knnPlusModels) {
                session = HibernateUtil.getSession();
                session.evict(knnPlusModel);
                knnPlusModel.setId(null);
                knnPlusModel.setPredictorId(newPredictorId);
                session.save(knnPlusModel);
                session.close();
            }

            //taking care of SVM table
            logger.debug("------//taking care of SVM table");
            session = HibernateUtil.getSession();
            List<SvmModel> svmModels = PopulateDataObjects.getSvmModelsByPredictorId(predictorId, session);
            session.close();
            for (SvmModel svmModel : svmModels) {
                session = HibernateUtil.getSession();
                session.evict(svmModel);
                svmModel.setId(null);
                svmModel.setPredictorId(newPredictorId);
                session.save(svmModel);
                session.close();
            }

            //taking care of RandomForest table
            logger.debug("------//taking care of RandomForest table");
            session = HibernateUtil.getSession();
            List<RandomForestGrove> randomForests =
                    PopulateDataObjects.getRandomForestGrovesByPredictorId(predictorId, session);
            session.close();
            for (RandomForestGrove randomForest : randomForests) {
                session = HibernateUtil.getSession();
                Long oldId = randomForest.getId();
                session.evict(randomForest);
                randomForest.setId(null);
                randomForest.setPredictorId(newPredictorId);
                session.save(randomForest);
                session.flush();
                List<RandomForestTree> trees = PopulateDataObjects.getRandomForestTreesByGroveId(oldId, session);
                for (RandomForestTree tree : trees) {
                    session.evict(tree);
                    tree.setId(null);
                    tree.setRandomForestGroveId(randomForest.getId());
                    session.save(tree);
                }
                session.close();
            }

            //taking care of modeling parameters
            logger.debug("------//taking care of modeling parameters");
            session = HibernateUtil.getSession();
            Predictor oldPredictor = PopulateDataObjects.getPredictorById(predictorId, session);

            if (oldPredictor.getModelMethod().equals(Constants.RANDOMFOREST)) {
                logger.debug("------//RANDOMFOREST");
                RandomForestParameters randomForestParameters = PopulateDataObjects
                        .getRandomForestParametersById(oldPredictor.getModelingParametersId(), session);
                session.evict(randomForestParameters);
                randomForestParameters.setId(null);
                session.save(randomForestParameters);
                session.flush();
                predictor.setModelingParametersId(randomForestParameters.getId());
            } else if (oldPredictor.getModelMethod().equals(Constants.KNNGA) || oldPredictor.getModelMethod()
                    .equals(Constants.KNNSA)) {
                logger.debug("------//KNN+");
                KnnPlusParameters knnPlusParameters =
                        PopulateDataObjects.getKnnPlusParametersById(oldPredictor.getModelingParametersId(), session);
                session.evict(knnPlusParameters);
                knnPlusParameters.setId(null);
                session.save(knnPlusParameters);
                session.flush();
                predictor.setModelingParametersId(knnPlusParameters.getId());
            }
            /*else if(oldPredictor.getModelMethod().equals(Constants.KNN)){
                logger.debug("------//KNN");
                KnnParameters params = PopulateDataObjects.getKnnParametersById(oldPredictor.getModelingParametersId
                (),session);
                session.evict(params);
                params.setId(null);
                session.save(params);
                session.flush();
                predictor.setModelingParametersId(params.getId());
            }*/
            else if (oldPredictor.getModelMethod().equals(Constants.SVM)) {
                logger.debug("------//SVM");
                SvmParameters svmParameters =
                        PopulateDataObjects.getSvmParametersById(oldPredictor.getModelingParametersId(), session);
                session.evict(svmParameters);
                svmParameters.setId(null);
                session.save(svmParameters);
                session.flush();
                predictor.setModelingParametersId(svmParameters.getId());
            }

            logger.debug("--------Old predictor ID=" + predictorId + " -> new one = " + newPredictorId);

            //duplicating child predictors
            String[] predictorChildren = null;
            String newChildIds = null;
            if (predictor.getChildIds() != null) {
                logger.debug("--------Child predictor IDs=" + predictor.getChildIds());
                newChildIds = new String();
                predictorChildren = predictor.getChildIds().split("\\s+");
                for (String id : predictorChildren) {
                    logger.debug("--------Child predictor ID=" + id + " longId=" + Long.parseLong(id));
                    session = HibernateUtil.getSession();
                    Predictor child = PopulateDataObjects.getPredictorById(Long.parseLong(id), session);
                    if (child != null) {
                        session.evict(child);
                        child.setId(null);
                        child.setUserName(Constants.ALL_USERS_USERNAME);
                        child.setPredictorType("Hidden");
                        child.setDatasetId(dataset.getId());
                        child.setParentId(newPredictorId);
                        session.save(child);
                        session.flush();
                        Long newId = child.getId();
                        newChildIds += newId.toString() + " ";
                        session.close();

                        //taking care of external validation table
                        session = HibernateUtil.getSession();
                        extValidation = PopulateDataObjects.getExternalValidationValues(Long.parseLong(id), session);
                        session.close();
                        for (ExternalValidation exVal : extValidation) {
                            session = HibernateUtil.getSession();
                            session.evict(exVal);
                            exVal.setExternalValId(-1);
                            exVal.setPredictorId(newId);
                            session.save(exVal);
                            session.close();
                        }

                        session = HibernateUtil.getSession();
                        if (child.getModelMethod().equals(Constants.RANDOMFOREST)) {
                            RandomForestParameters randomForestParameters = PopulateDataObjects
                                    .getRandomForestParametersById(child.getModelingParametersId(), session);
                            session.evict(randomForestParameters);
                            randomForestParameters.setId(null);
                            session.save(randomForestParameters);
                            session.flush();
                            child.setModelingParametersId(randomForestParameters.getId());
                        } else if (child.getModelMethod().equals(Constants.KNNGA) || child.getModelMethod()
                                .equals(Constants.KNNSA)) {
                            KnnPlusParameters knnPlusParameters = PopulateDataObjects
                                    .getKnnPlusParametersById(child.getModelingParametersId(), session);
                            session.evict(knnPlusParameters);
                            knnPlusParameters.setId(null);
                            session.save(knnPlusParameters);
                            session.flush();
                            child.setModelingParametersId(knnPlusParameters.getId());
                        }
                            /*else if(child.getModelMethod().equals(Constants.KNN)){
                                KnnParameters params = PopulateDataObjects.getKnnParametersById(child
                                .getModelingParametersId(),session);
                                session.evict(params);
                                params.setId(null);
                                session.save(params);
                                session.flush();
                                child.setModelingParametersId(params.getId());
                            }*/
                        else if (child.getModelMethod().equals(Constants.SVM)) {
                            SvmParameters svmParameters =
                                    PopulateDataObjects.getSvmParametersById(child.getModelingParametersId(), session);
                            session.evict(svmParameters);
                            svmParameters.setId(null);
                            session.save(svmParameters);
                            session.flush();
                            child.setModelingParametersId(svmParameters.getId());
                        }

                        session.close();
                        //taking care of RandomForest table
                        logger.debug("------//taking care of RandomForest table");
                        session = HibernateUtil.getSession();
                        randomForests =
                                PopulateDataObjects.getRandomForestGrovesByPredictorId(Long.parseLong(id), session);
                        session.close();
                        for (RandomForestGrove randomForest : randomForests) {
                            session = HibernateUtil.getSession();
                            Long oldId = randomForest.getId();
                            session.evict(randomForest);
                            randomForest.setId(null);
                            randomForest.setPredictorId(newId);
                            session.save(randomForest);
                            session.flush();
                            List<RandomForestTree> trees =
                                    PopulateDataObjects.getRandomForestTreesByGroveId(oldId, session);
                            for (RandomForestTree tree : trees) {
                                session.evict(tree);
                                tree.setId(null);
                                tree.setRandomForestGroveId(randomForest.getId());
                                session.save(tree);
                            }
                            session.close();
                        }
                        session = HibernateUtil.getSession();
                        Transaction tx = null;
                        try {
                            tx = session.beginTransaction();
                            session.saveOrUpdate(child);
                            tx.commit();
                        } catch (RuntimeException e) {
                            if (tx != null) {
                                tx.rollback();
                            }
                            logger.error(e);
                        } finally {
                            session.close();
                        }
                    }
                }

            }

            //updating newly created predictor with new child ids
            predictor.setChildIds(newChildIds);
            logger.debug("--------New child predictor IDs=" + newChildIds);
            session = HibernateUtil.getSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.saveOrUpdate(predictor);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e);
            } finally {
                session.close();
            }

        } catch (Exception ex) {
            result = ERROR;
            logger.error(ex);
        }

        return result;
    }

    public String makeDatasetPublic() {
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();
        try {
            String datasetName = ((String[]) context.getParameters().get("datasetName"))[0];
            String userName = ((String[]) context.getParameters().get("userName"))[0];

            if (datasetName.isEmpty() || userName.isEmpty()) {
                return ERROR;
            }

            logger.debug("++++++++++++++++++Dataset name:" + datasetName + " User name=" + userName);

            Session session = HibernateUtil.getSession();
            Dataset dataset = PopulateDataObjects.getDataSetByName(datasetName, userName, session);
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
            if (PopulateDataObjects.getDataSetByName(datasetName, Constants.ALL_USERS_USERNAME, session) != null) {
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
            session = HibernateUtil.getSession();
            session.evict(dataset);
            dataset.setId(null);
            dataset.setUserName(Constants.ALL_USERS_USERNAME);
            session.save(dataset);
            session.flush();
            session.close();

            session = HibernateUtil.getSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.saveOrUpdate(dataset);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e);
            } finally {
                session.close();
            }

        } catch (Exception ex) {
            result = ERROR;
            logger.error(ex);
        }

        return result;
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
