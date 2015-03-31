package edu.unc.ceccr.chembench.actions.api;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;

public class MyBenchAction extends ActionSupport {
    private List<?> data;

    public String getDatasets() throws SQLException, ClassNotFoundException {
        User user = User.getCurrentUser();
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
        session.close();
        return SUCCESS;
    }

    public String getModels() throws SQLException, ClassNotFoundException {
        User user = User.getCurrentUser();
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
        session.close();
        return SUCCESS;
    }

    public String getPredictions() throws SQLException, ClassNotFoundException {
        User user = User.getCurrentUser();
        Session session = HibernateUtil.getSession();
        List<Prediction> predictions = Lists.newArrayList();
        if (user != null) {
            predictions.addAll(PopulateDataObjects.populatePredictions(user.getUserName(), false, session));
        } else {
            // no public predictions to show, so return an empty list
        }
        this.data = predictions;
        return SUCCESS;
    }

    public List<?> getData() {
        return data;
    }
}
