package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.*;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
// struts2

public class JobsActions extends ActionSupport {

    /**
     *
     */
    private static final long serialVersionUID = 42L;

    private static Logger logger
            = Logger.getLogger(JobsActions.class.getName());
    // ====== variables used for displaying the JSP =====//
    private User user;
    private boolean adminUser;
    // ====== variables used to hold the queue and finished jobs information
    // =====//
    private List<Dataset> userDatasets;
    private List<Predictor> userPredictors;
    private List<Prediction> userPredictions;
    private List<Job> incomingJobs;
    private List<Job> lsfJobs;
    private List<Job> localJobs;
    private List<Job> errorJobs;

    public String loadPage() throws Exception {

        String result = SUCCESS;

        // check that the user is logged in
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.warn("No ActionContext available");
        } else {
            user = (User) context.getSession().get("user");

            if (user == null) {
                logger.warn("No user is logged in.");
                result = LOGIN;
                return result;
            }
        }

        if (Utility.isAdmin(user.getUserName())) {
            adminUser = true;
        } else {
            adminUser = false;
        }

        Thread.sleep(200);

        // set up any values that need to be populated onto the page
        // (dropdowns, lists, display stuff)
        Session session = HibernateUtil.getSession();

        // get datasets
        if (user.getShowPublicDatasets().equals(Constants.ALL)) {
            // get the user's datasets and all public ones
            userDatasets = PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.CONTINUOUS, true, session);
            userDatasets.addAll(PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.CATEGORY, true, session));
            userDatasets.addAll(PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.PREDICTION, true, session));
        } else if (user.getShowPublicDatasets().equals(Constants.NONE)) {
            // just get the user's datasets
            userDatasets = PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.CONTINUOUS, false, session);
            userDatasets.addAll(PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.CATEGORY, false, session));
            userDatasets.addAll(PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.PREDICTION, false, session));
        } else if (user.getShowPublicDatasets().equals(Constants.SOME)) {
            // get all the datasets and filter out all the public ones that
            // aren't "show by default"
            userDatasets = PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.CONTINUOUS, true, session);
            userDatasets.addAll(PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.CATEGORY, true, session));
            userDatasets.addAll(PopulateDataObjects.populateDataset(user
                    .getUserName(), Constants.PREDICTION, true, session));

            for (int i = 0; i < userDatasets.size(); i++) {
                String s = userDatasets.get(i).getShowByDefault();
                if (s != null && s.equals(Constants.NO)) {
                    userDatasets.remove(i);
                    i--;
                }
            }
        }
        if (userDatasets != null) {
            // Collections.sort(userDatasets, new Comparator<Dataset>() {
            // public int compare(Dataset d1, Dataset d2) {
            // return
            // d1.getName().toLowerCase().compareTo(d2.getName().toLowerCase());
            // }});

            Collections.sort(userDatasets, new Comparator<Dataset>() {
                public int compare(Dataset d1, Dataset d2) {
                    return d2.getCreatedTime().compareTo(d1.getCreatedTime());
                }
            });

            for (int i = 0; i < userDatasets.size(); i++) {
                if (userDatasets.get(i).getJobCompleted() == null
                        || userDatasets.get(i).getJobCompleted().equals(
                        Constants.NO)) {
                    userDatasets.remove(i);
                    i--;
                }
            }
        }
        // get predictors
        if (user.getShowPublicPredictors().equals(Constants.ALL)) {
            // get the user's predictors and all public ones
            userPredictors = PopulateDataObjects.populatePredictors(user
                    .getUserName(), true, true, session);
        } else {
            // just get the user's predictors
            userPredictors = PopulateDataObjects.populatePredictors(user
                    .getUserName(), false, true, session);
        }
        if (userPredictors != null) {
            // Collections.sort(userPredictors, new Comparator<Predictor>() {
            // public int compare(Predictor p1, Predictor p2) {
            // return
            // p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
            // }});
            Collections.sort(userPredictors, new Comparator<Predictor>() {
                public int compare(Predictor p1, Predictor p2) {
                    return p2.getDateCreated().compareTo(p1.getDateCreated());
                }
            });
        }

        // get predictions
        userPredictions = PopulateDataObjects.populatePredictions(user
                .getUserName(), false, session);
        if (userPredictions != null) {
            // Collections.sort(userPredictions, new Comparator<Prediction>()
            // {
            // public int compare(Prediction p1, Prediction p2) {
            // return
            // p1.getName().toLowerCase().compareTo(p2.getName().toLowerCase());
            // }});

            Collections.sort(userPredictions, new Comparator<Prediction>() {
                public int compare(Prediction p1, Prediction p2) {
                    return p2.getDateCreated().compareTo(p1.getDateCreated());
                }
            });

            for (int i = 0; i < userPredictions.size(); i++) {
                if (userPredictions.get(i).getJobCompleted() == null
                        || userPredictions.get(i).getJobCompleted().equals(
                        Constants.NO)) {
                    userPredictions.remove(i);
                    i--;
                }
            }
        }

        // get local jobs
        localJobs = CentralDogma.getInstance().localJobs.getReadOnlyCopy();

        for (int i = 0; i < localJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!localJobs.get(i).getUserName().equals(user.getUserName())
                    && !user.getIsAdmin().equals(Constants.YES)) {
                localJobs.remove(i);
                i--;
                continue;
            }

            if (localJobs.get(i).workflowTask != null) {
                localJobs.get(i).setMessage(
                        localJobs.get(i).workflowTask.getProgress(user
                                .getUserName())
                );
            }
        }

        // get lsf jobs
        lsfJobs = CentralDogma.getInstance().lsfJobs.getReadOnlyCopy();

        for (int i = 0; i < lsfJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!lsfJobs.get(i).getUserName().equals(user.getUserName())
                    && !user.getIsAdmin().equals(Constants.YES)) {
                lsfJobs.remove(i);
                i--;
                continue;
            }

            if (lsfJobs.get(i).workflowTask != null) {
                lsfJobs.get(i).setMessage(
                        lsfJobs.get(i).workflowTask.getProgress(user
                                .getUserName())
                );
            }
        }

        // get incoming jobs
        incomingJobs = CentralDogma.getInstance().incomingJobs
                .getReadOnlyCopy();

        for (int i = 0; i < incomingJobs.size(); i++) {
            // hide job if job is from a different user and logged in user is
            // not admin
            if (!incomingJobs.get(i).getUserName().equals(user.getUserName())
                    && !user.getIsAdmin().equals(Constants.YES)) {
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
            if (!errorJobs.get(i).getUserName().equals(user.getUserName())
                    && !user.getIsAdmin().equals(Constants.YES)) {
                errorJobs.remove(i);
                i--;
            }
        }

        session.close();

        logger.debug("Forwarding user " + user.getUserName()
                + " to jobs page.");

        return result;
    }

    public String execute() throws Exception {
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
