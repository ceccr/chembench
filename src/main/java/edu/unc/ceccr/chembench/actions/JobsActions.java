package edu.unc.ceccr.chembench.actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.Job;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.List;

public class JobsActions extends ActionSupport {
    private static Logger logger = Logger.getLogger(JobsActions.class.getName());

    // ====== variables used for displaying the JSP =====//
    private User user;
    private boolean adminUser;
    // ====== variables used to hold the queue and finished jobs information
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

        session.close();

        logger.debug("Forwarding user " + user.getUserName() + " to jobs page.");

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
