package edu.unc.ceccr.chembench.actions;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JobsActions extends ActionSupport {
    private static final Function<Object, Comparable> SORT_FUNCTION = new Function<Object, Comparable>() {
        @Override
        public Comparable apply(Object obj) {
            if (obj instanceof Dataset) {
                return ((Dataset) obj).getCreatedTime();
            } else if (obj instanceof Predictor) {
                return ((Predictor) obj).getDateCreated();
            } else if (obj instanceof Prediction) {
                return ((Prediction) obj).getDateCreated();
            } else {
                throw new RuntimeException("Attempted to sort non-supported type");
            }
        }
    };
    private static final Logger logger = Logger.getLogger(JobsActions.class.getName());
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final PredictionRepository predictionRepository;
    private User user = User.getCurrentUser();
    private boolean adminUser;
    private List<Dataset> userDatasets;
    private List<Predictor> userPredictors;
    private List<Prediction> userPredictions;
    private List<Job> incomingJobs;
    private List<Job> lsfJobs;
    private List<Job> localJobs;
    private List<Job> errorJobs;

    @Autowired
    public JobsActions(DatasetRepository datasetRepository, PredictorRepository predictorRepository,
                       PredictionRepository predictionRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.predictionRepository = predictionRepository;
    }

    public String execute() throws Exception {
        adminUser = user.getIsAdmin().equals(Constants.YES);

        // get datasets
        userDatasets = datasetRepository.findByUserName(user.getUserName());
        if (user.getShowPublicDatasets().equals(Constants.ALL)) {
            // get the user's datasets and all public ones
            userDatasets.addAll(datasetRepository.findAllPublicDatasets());
        } else if (user.getShowPublicDatasets().equals(Constants.SOME)) {
            // get the user's datasets and all public ones marked 'show by default'
            userDatasets.addAll(datasetRepository.findSomePublicDatasets());
        }

        // get predictors
        userPredictors = predictorRepository.findByUserName(user.getUserName());
        if (user.getShowPublicPredictors().equals(Constants.ALL)) {
            // get the user's predictors and all public ones
            userPredictors.addAll(predictorRepository.findPublicPredictors());
        }
        for (Predictor p : userPredictors) {
            Dataset d = datasetRepository.findOne(p.getDatasetId());
            p.setDatasetDisplay(d.getName());
        }

        // get predictions
        userPredictions = predictionRepository.findByUserName(user.getUserName());
        for (Prediction prediction : userPredictions) {
            Dataset d = datasetRepository.findOne(prediction.getDatasetId());
            prediction.setDatasetDisplay(d.getName());
            List<String> descriptorNames = Lists.newArrayList();
            for (String rawId : Splitter.on(CharMatcher.WHITESPACE).split(prediction.getPredictorIds())) {
                Predictor p = predictorRepository.findOne(Long.parseLong(rawId));
                descriptorNames.add(p.getName());
            }
            prediction.setPredictorNames(Joiner.on(' ').join(descriptorNames));
        }

        // sort objects by SORT_FUNCTION
        Ordering<Object> ordering = Ordering.natural().reverse().onResultOf(SORT_FUNCTION);
        userDatasets = ordering.sortedCopy(userDatasets);
        userPredictors = ordering.sortedCopy(userPredictors);
        userPredictions = ordering.sortedCopy(userPredictions);

        // get local jobs
        localJobs = CentralDogma.getInstance().localJobs.getReadOnlyCopy();

        for (int i = 0; i < localJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!localJobs.get(i).getUserName().equals(user.getUserName()) && !user.getIsAdmin()
                    .equals(Constants.YES)) {
                localJobs.remove(i);
                i--;
                continue;
            }

            if (localJobs.get(i).workflowTask != null) {
                localJobs.get(i).setMessage(localJobs.get(i).workflowTask.getProgress(user.getUserName()));
            }
        }

        // get lsf jobs
        lsfJobs = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();

        for (int i = 0; i < lsfJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!lsfJobs.get(i).getUserName().equals(user.getUserName()) && !user.getIsAdmin().equals(Constants.YES)) {
                lsfJobs.remove(i);
                i--;
                continue;
            }

            if (lsfJobs.get(i).workflowTask != null) {
                lsfJobs.get(i).setMessage(lsfJobs.get(i).workflowTask.getProgress(user.getUserName()));
            }
        }

        // get incoming jobs
        incomingJobs = CentralDogma.getInstance().incomingJobs.getReadOnlyCopy();

        for (int i = 0; i < incomingJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!incomingJobs.get(i).getUserName().equals(user.getUserName()) && !user.getIsAdmin()
                    .equals(Constants.YES)) {
                incomingJobs.remove(i);
                i--;
                continue;
            }

            if (incomingJobs.get(i).workflowTask != null) {
                incomingJobs.get(i).setMessage("Waiting in queue");
            }
        }

        // get error jobs
        errorJobs = CentralDogma.getInstance().errorJobs.getReadOnlyCopy();

        for (int i = 0; i < errorJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!errorJobs.get(i).getUserName().equals(user.getUserName()) && !user.getIsAdmin()
                    .equals(Constants.YES)) {
                errorJobs.remove(i);
                i--;
            }
        }
        return SUCCESS;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isAdminUser() {
        return adminUser;
    }

    public void setAdminUser(boolean adminUser) {
        this.adminUser = adminUser;
    }

    public List<Dataset> getUserDatasets() {
        return userDatasets;
    }

    public void setUserDatasets(List<Dataset> userDatasets) {
        this.userDatasets = userDatasets;
    }

    public List<Predictor> getUserPredictors() {
        return userPredictors;
    }

    public void setUserPredictors(List<Predictor> userPredictors) {
        this.userPredictors = userPredictors;
    }

    public List<Prediction> getUserPredictions() {
        return userPredictions;
    }

    public void setUserPredictions(List<Prediction> userPredictions) {
        this.userPredictions = userPredictions;
    }

    public List<Job> getIncomingJobs() {
        return incomingJobs;
    }

    public void setIncomingJobs(List<Job> incomingJobs) {
        this.incomingJobs = incomingJobs;
    }

    public List<Job> getLsfJobs() {
        return lsfJobs;
    }

    public void setLsfJobs(List<Job> lsfJobs) {
        this.lsfJobs = lsfJobs;
    }

    public List<Job> getLocalJobs() {
        return localJobs;
    }

    public void setLocalJobs(List<Job> localJobs) {
        this.localJobs = localJobs;
    }

    public List<Job> getErrorJobs() {
        return errorJobs;
    }

    public void setErrorJobs(List<Job> errorJobs) {
        this.errorJobs = errorJobs;
    }
}
