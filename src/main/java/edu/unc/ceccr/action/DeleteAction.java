package edu.unc.ceccr.action;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.*;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// struts2

public class DeleteAction extends ActionSupport {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(DeleteAction.class.getName());
    private List<String> errorStrings = Lists.newArrayList();

    private void checkDatasetDependencies(Dataset ds) throws ClassNotFoundException, SQLException {
        // make sure there are no predictors, predictions, or jobs that depend
        // on this dataset
        logger.debug("checking dataset dependencies");

        Session session = HibernateUtil.getSession();
        String userName = ds.getUserName();
        ArrayList<Predictor> userPredictors =
                (ArrayList<Predictor>) PopulateDataObjects.populatePredictors(userName, true, false, session);
        ArrayList<Prediction> userPredictions =
                (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);

        // check each predictor
        for (int i = 0; i < userPredictors.size(); i++) {
            logger.debug("predictor id: " + userPredictors.get(i).getDatasetId() + " dataset id: " + ds.getId());
            if (userPredictors.get(i).getDatasetId() != null && userPredictors.get(i).getDatasetId()
                    .equals(ds.getId())) {
                errorStrings
                        .add("The predictor '" + userPredictors.get(i).getName() + "' depends on this dataset. Please" +
                                " delete it first.\n");
            }
        }

        // check each prediction
        for (int i = 0; i < userPredictions.size(); i++) {
            logger.debug("Prediction id: " + userPredictions.get(i).getDatasetId() + " dataset id: " + ds.getId());
            if (userPredictions.get(i).getDatasetId() != null && userPredictions.get(i).getDatasetId()
                    .equals(ds.getId())) {
                errorStrings.add("The prediction '" + userPredictions.get(i).getName()
                        + "' depends on this dataset. Please " +
                        "delete it first.\n");
            }
        }

        // check each job
        // Actually, we don't need to check the jobs.
        // When a modeling or prediction job runs, it creates a Predictor or
        // Prediction entry in the database
        // and that's enough to catch the dependency.

    }

    private void checkPredictorDependencies(Predictor p) throws ClassNotFoundException, SQLException {
        // make sure there are no predictions or prediction jobs that depend
        // on this predictor

        String userName = p.getUserName();
        Session session = HibernateUtil.getSession();
        ArrayList<Prediction> userPredictions =
                (ArrayList<Prediction>) PopulateDataObjects.populatePredictions(userName, false, session);
        session.close();

        // check each prediction
        for (int i = 0; i < userPredictions.size(); i++) {
            Prediction prediction = userPredictions.get(i);
            String[] predictorIds = prediction.getPredictorIds().split("\\s+");
            for (int j = 0; j < predictorIds.length; j++) {
                if (Long.parseLong(predictorIds[j]) == p.getId()) {
                    errorStrings
                            .add("The prediction '" + userPredictions.get(i).getName() + "' depends on this predictor."
                                    +
                                    " Please delete it first.\n");
                }
            }
        }

        // check running jobs
        // Actually, we don't need to check the jobs.
        // When a prediction job runs, it creates a Prediction entry in the
        // database
        // and that's enough to catch the dependency.

    }

    private boolean checkPermissions(String objectUser) {
        // make sure the user has permissions to delete this object

        ActionContext context = ActionContext.getContext();

        // check that there is a user logged in
        User user = null;
        if (context == null) {
            logger.debug("No ActionContext available");
            return false;
        }
        user = (User) context.getSession().get("user");
        if (user == null) {
            logger.debug("No user logged in.");
            return false;
        }

        // make sure the user can actually delete this object
        if (user.getUserName().equalsIgnoreCase(objectUser) || user.getIsAdmin().equals(Constants.YES)) {
            return true;
        }

        return false;
    }

    public String deleteDataset() throws Exception {

        ActionContext context = ActionContext.getContext();

        String datasetId;
        Dataset ds = null;

        datasetId = ((String[]) context.getParameters().get("id"))[0];
        logger.debug("Deleting dataset with id: " + datasetId);

        if (datasetId == null) {
            errorStrings.add("No dataset ID supplied.");
            return ERROR;
        }

        Session session = HibernateUtil.getSession();
        ds = PopulateDataObjects.getDataSetById(Long.parseLong(datasetId), session);

        if (ds == null) {
            errorStrings.add("Invalid dataset ID supplied.");
            return ERROR;
        }

        if (!checkPermissions(ds.getUserName())) {
            errorStrings.add("Error: You do not have the permissions " + "needed to delete this dataset.");
            return ERROR;
        }

        // make sure nothing else depends on this dataset existing
        checkDatasetDependencies(ds);
        if (!errorStrings.isEmpty()) {
            return ERROR;
        }

        // delete the files associated with this dataset
        String dir = Constants.CECCR_USER_BASE_PATH + ds.getUserName() + "/DATASETS/" + ds.getName();
        if ((new File(dir)).exists()) {
            if (!FileAndDirOperations.deleteDir(new File(dir))) {
                logger.warn("error deleting dir: " + dir);
            }
        }

        // delete the database entry for the dataset
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(ds);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
            return ERROR;
        }

        session.close();
        return SUCCESS;
    }

    public String deletePredictor() throws Exception {

        ActionContext context = ActionContext.getContext();

        String predictorId;
        Predictor p = null;

        predictorId = ((String[]) context.getParameters().get("id"))[0];
        logger.debug("Deleting predictor with id: " + predictorId);

        if (predictorId == null) {
            logger.debug("No predictor ID supplied.");
            return ERROR;
        }

        Session session = HibernateUtil.getSession();
        p = PopulateDataObjects.getPredictorById(Long.parseLong(predictorId), session);

        if (p == null) {
            errorStrings.add("Invalid predictor ID supplied.");
            return ERROR;
        }

        if (!checkPermissions(p.getUserName())) {
            errorStrings.add("You do not have the permissions " + "needed to delete this predictor.");
            return ERROR;
        }

        // make sure nothing else depends on this predictor existing
        checkPredictorDependencies(p);
        if (!errorStrings.isEmpty()) {
            return ERROR;
        }

        deletePredictor(p, session);
        session.close();

        return SUCCESS;
    }

    public void deletePredictor(Predictor p, Session session) throws Exception {
        ArrayList<ExternalValidation> extVals = Lists.newArrayList();
        // delete the files associated with this predictor
        String dir = Constants.CECCR_USER_BASE_PATH + p.getUserName() + "/PREDICTORS/" + p.getName() + "/";
        if (!FileAndDirOperations.deleteDir(new File(dir))) {
            logger.warn("error deleting dir: " + dir);
        }

        // delete the database entry for the predictor
        // delete any child predictors too. (Their files will already be gone
        // since deleteDir recurses into subdirs.)
        List<Predictor> childPredictors = Lists.newArrayList();
        if (p.getChildIds() != null && !p.getChildIds().trim().equals("")) {
            String[] childIdArray = p.getChildIds().split("\\s+");
            for (String childId : childIdArray) {
                if (childId.equals("null")) {
                    logger.warn("Attempted to delete a nonexistant child " +
                            "predictor belonging to predictor id " + p.getId());
                } else {
                    Predictor childPredictor = PopulateDataObjects.getPredictorById(Long.parseLong(childId), session);
                    if (childPredictor == null) {
                        logger.warn(String.format("Child predictor with id %s not found", childId));
                    } else {
                        childPredictors.add(childPredictor);
                        extVals.addAll(
                                PopulateDataObjects.getExternalValidationValues(childPredictor.getId(), session));
                    }
                }
            }
        }
        extVals.addAll(PopulateDataObjects.getExternalValidationValues(p.getId(), session));
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(p);
            for (Predictor childPredictor : childPredictors) {
                session.delete(childPredictor);
            }
            for (ExternalValidation ev : extVals) {
                session.delete(ev);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        }
    }

    public String deletePrediction() throws Exception {

        ActionContext context = ActionContext.getContext();

        String predictionId;
        Prediction p = null;

        predictionId = ((String[]) context.getParameters().get("id"))[0];
        logger.debug("Deleting prediction with id: " + predictionId);

        if (predictionId == null) {
            errorStrings.add("No prediction ID supplied.");
            return ERROR;
        }

        Session session = HibernateUtil.getSession();
        p = PopulateDataObjects.getPredictionById(Long.parseLong(predictionId), session);
        if (p == null) {
            errorStrings.add("Invalid prediction ID.");
            return ERROR;
        }

        if (!checkPermissions(p.getUserName())) {
            errorStrings.add("You do not have the permissions " + "needed to delete this prediction.");
            return ERROR;
        }

        // delete the files associated with this prediction
        String dir = Constants.CECCR_USER_BASE_PATH + p.getUserName() + "/PREDICTIONS/" + p.getName();
        if (!FileAndDirOperations.deleteDir(new File(dir))) {
            logger.warn("error deleting dir: " + dir);
        }

        // delete the prediction values associated with the prediction
        ArrayList<PredictionValue> pvs =
                (ArrayList<PredictionValue>) PopulateDataObjects.getPredictionValuesByPredictionId(p.getId(), session);

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

        // delete the database entry for the prediction
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.delete(p);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) {
                tx.rollback();
            }
            logger.error(e);
        }

        session.close();

        return SUCCESS;
    }

    public String deleteJob() throws Exception {
        // stops the job and removes all associated files

        ActionContext context = ActionContext.getContext();

        String taskId;

        taskId = ((String[]) context.getParameters().get("id"))[0];
        logger.debug("Deleting job with id: " + taskId);

        try {
            Session s = HibernateUtil.getSession();
            Job j = PopulateDataObjects.getJobById(Long.parseLong(taskId), s);
            if (j != null && j.getJobType().equals(Constants.MODELING)) {
                if (j.getLookupId() != null) {
                    logger.debug("getting predictor with id: " + j.getLookupId());
                    Predictor p = PopulateDataObjects.getPredictorById(j.getLookupId(), s);

                    String parentPredictorName = "";
                    if (p.getName().matches(".*_fold_(\\d+)_of_(\\d+)")) {
                        // this is a child predictor in an nfold run
                        int pos = p.getName().lastIndexOf("_fold");
                        parentPredictorName = p.getName().substring(0, pos);
                    }
                    Predictor parentPredictor =
                            PopulateDataObjects.getPredictorByName(parentPredictorName, p.getUserName(), s);
                    if (!parentPredictorName.isEmpty() && parentPredictor != null) {
                        logger.debug("Parent predictor is not null, deleting sibling jobs.");
                        String[] childPredictorIds = parentPredictor.getChildIds().split("\\s+");

                        // get siblings
                        ArrayList<Predictor> siblingPredictors = Lists.newArrayList();
                        for (String childPredictorId : childPredictorIds) {
                            if (!childPredictorId.equals("" + p.getId())) {
                                Predictor sibling =
                                        PopulateDataObjects.getPredictorById(Long.parseLong(childPredictorId), s);
                                siblingPredictors.add(sibling);
                            }
                        }

                        // find sibling jobs and cancel those
                        for (Predictor sp : siblingPredictors) {
                            Job sibJob = PopulateDataObjects.getJobByNameAndUsername(sp.getName(), sp.getUserName(), s);
                            try {
                                CentralDogma.getInstance().cancelJob(sibJob.getId());
                            } catch (Exception ex) {
                                // if some siblings are missing, don't
                                // crash, just keep deleting things
                                logger.error(ex);
                            }
                        }
                        // cancel this job
                        CentralDogma.getInstance().cancelJob(Long.parseLong(taskId));

                        // delete the parent predictor
                        deletePredictor(parentPredictor, s);
                    } else {
                        CentralDogma.getInstance().cancelJob(Long.parseLong(taskId));
                    }
                }
            } else {
                CentralDogma.getInstance().cancelJob(Long.parseLong(taskId));
            }
            s.close();
        } catch (Exception ex) {
            // if it failed, no big deal - just write out the exception.
            logger.error(ex);
        }
        return SUCCESS;
    }

    public String deleteUser() throws Exception {
        // check that the person deleting the user is an admin, just to be
        // safe
        ActionContext context = ActionContext.getContext();
        User u = (User) context.getSession().get("user");

        String userToDelete = ((String[]) context.getParameters().get("userToDelete"))[0];
        logger.debug("Deleting user: " + userToDelete);

        if (u == null || !u.getIsAdmin().equals(Constants.YES)) {
            // this isn't an admin! Kick 'em out.
            return ERROR;
        }

        if (userToDelete.isEmpty() || userToDelete.contains("..") || userToDelete.contains("~") || userToDelete
                .contains("/")) {
            // just being a little safer, since there's a recursive delete in
            // this function
            return ERROR;
        }

        Session s = HibernateUtil.getSession();

        List<Prediction> predictions = Lists.newArrayList();
        Iterator<?> predictionItr = PopulateDataObjects.getUserData(userToDelete, Prediction.class, s).iterator();
        while (predictionItr.hasNext()) {
            predictions.add((Prediction) predictionItr.next());

        }

        List<Predictor> predictors = Lists.newArrayList();

        Iterator<?> predictorIter = PopulateDataObjects.getUserData(userToDelete, Predictor.class, s).iterator();
        while (predictorIter.hasNext()) {
            predictors.add((Predictor) predictorIter.next());

        }

        List<Dataset> datasets = Lists.newArrayList();

        Iterator<?> dataSetIter = PopulateDataObjects.getUserData(userToDelete, Dataset.class, s).iterator();
        while (dataSetIter.hasNext()) {
            datasets.add((Dataset) dataSetIter.next());

        }

        List<Job> jobs = Lists.newArrayList();

        Iterator<?> jobsIter = PopulateDataObjects.getUserData(userToDelete, Job.class, s).iterator();
        while (jobsIter.hasNext()) {
            jobs.add((Job) jobsIter.next());

        }
        s.close();

        for (Prediction p : predictions) {
            String[] idAsArray = new String[1];
            idAsArray[0] = "" + p.getId();
            context.getParameters().put("id", idAsArray);
            deletePrediction();
        }

        for (Predictor p : predictors) {
            String[] idAsArray = new String[1];
            idAsArray[0] = "" + p.getId();
            context.getParameters().put("id", idAsArray);
            deletePredictor();
        }

        for (Dataset d : datasets) {
            String[] idAsArray = new String[1];
            idAsArray[0] = "" + d.getId();
            context.getParameters().put("id", idAsArray);
            deleteDataset();
        }

        for (Job j : jobs) {
            String[] idAsArray = new String[1];
            idAsArray[0] = "" + j.getId();
            context.getParameters().put("id", idAsArray);
            deleteJob();
        }

        try {
            Session session = HibernateUtil.getSession();
            User deleteMe = PopulateDataObjects.getUserByUserName(userToDelete, session);
            Transaction tx = null;
            tx = session.beginTransaction();
            session.delete(deleteMe);
            tx.commit();

        } catch (Exception ex) {
            logger.error(ex);
        }

        // last, delete all the files that user has
        // recurses
        File dir = new File(Constants.CECCR_USER_BASE_PATH + userToDelete);
        FileAndDirOperations.deleteDir(dir);

        return SUCCESS;
    }

    protected void deleteDatabaseData(List<?> list) throws ClassNotFoundException, SQLException {
        if (list.size() != 0) {
            Session session = HibernateUtil.getSession();
            Iterator<?> it = list.iterator();
            while (it.hasNext()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.delete(it.next());
                    tx.commit();
                } catch (RuntimeException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    logger.error(e);
                }
            }
            session.close();
        }
    }

    public List<String> getErrorStrings() {
        return errorStrings;
    }

    public void setErrorStrings(List<String> errorStrings) {
        this.errorStrings = errorStrings;
    }

}
