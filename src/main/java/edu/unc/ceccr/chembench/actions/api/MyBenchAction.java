package edu.unc.ceccr.chembench.actions.api;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public class MyBenchAction extends ActionSupport {
    private User user = User.getCurrentUser();
    private List<?> data = Lists.newArrayList();

    public String getLocalJobs() {
        if (user == null) {
            return SUCCESS;
        }

        List<Job> jobList = CentralDogma.getInstance().localJobs.getReadOnlyCopy();
        for (Job j : jobList) {
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                jobList.remove(j);
            }
        }
        data = jobList;
        return SUCCESS;
    }

    public String getLsfJobs() {
        if (user == null) {
            return SUCCESS;
        }

        List<Job> jobList = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();
        for (Job j : jobList) {
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                jobList.remove(j);
            }
        }
        data = jobList;
        return SUCCESS;
    }

    public String getErrorJobs() {
        if (user == null) {
            return SUCCESS;
        }

        List<Job> jobList = CentralDogma.getInstance().errorJobs.getReadOnlyCopy();
        for (Job j : jobList) {
            if (!(user.getIsAdmin().equals("YES") || user.getUserName().equals(j.getUserName()))) {
                jobList.remove(j);
            }
        }
        data = jobList;
        return SUCCESS;
    }

    public String getDatasets() throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        List<Dataset> datasets = Lists.newArrayList();
        if (user != null) {
            // return user's datasets and public datasets
            datasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CONTINUOUS, true, session));
            datasets.addAll(PopulateDataObjects.populateDataset(user.getUserName(), Constants.CATEGORY, true, session));
        } else {
            // return public datasets only
            datasets.addAll(PopulateDataObjects.populateDataset("", Constants.CONTINUOUS, true, session));
            datasets.addAll(PopulateDataObjects.populateDataset("", Constants.CATEGORY, true, session));
        }
        this.data = datasets;
        if (session != null) {
            session.close();
        }
        return SUCCESS;
    }

    public String getModels() throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        List<Predictor> predictors = Lists.newArrayList();
        if (user != null) {
            // return user's models and public models
            predictors.addAll(PopulateDataObjects.populatePredictors(user.getUserName(), true, true, session));
        } else {
            // return public models only
            predictors.addAll(PopulateDataObjects.populatePredictors("", true, true, session));
        }
        this.data = predictors;
        if (session != null) {
            session.close();
        }
        return SUCCESS;
    }

    public String getPredictions() throws SQLException, ClassNotFoundException {
        if (user == null) {
            return SUCCESS;
        }

        Session session = HibernateUtil.getSession();
        List<Prediction> predictions = Lists.newArrayList();
        predictions.addAll(PopulateDataObjects.populatePredictions(user.getUserName(), false, session));
        data = predictions;
        return SUCCESS;
    }

    public List<?> getData() {
        return data;
    }
}
