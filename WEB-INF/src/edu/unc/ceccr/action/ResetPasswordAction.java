package edu.unc.ceccr.action;

//struts2

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.SendEmails;
import edu.unc.ceccr.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ResetPasswordAction extends ActionSupport {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(ResetPasswordAction.class.getName());
    String userName;
    String email;
    String errorMessage;

    public String execute() throws Exception {

        // set up session to check user name and email
        Session s = HibernateUtil.getSession();
        User user = PopulateDataObjects.getUserByUserName(userName, s);
        if (user == null || !user.getEmail().equals(email)) {
            errorMessage = "Invalid username or email!";
            return ERROR;
        }

        //email matches
        String randomPassword = Utility.randomPassword();
        user.setPassword(Utility.encrypt(randomPassword));

        // Commit changes
        Transaction tx = null;

        try {
            tx = s.beginTransaction();
            s.saveOrUpdate(user);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null)
                tx.rollback();
            logger.error(e);
        } finally {
            s.close();
        }

        // message to user

        //email
        String message = user.getFirstName() + ", your Chembench password has been reset." + "<br/>" + "Your " +
                "username: " + user.getUserName()
                + "<br/> Your new password is: " + randomPassword + "<br/><br/><br/>"
                + "You may login from " + Constants.WEBADDRESS + ".<br/> <br/><br/>"
                + "Once you are logged in, you may change your password from the 'edit profile' page.";

        SendEmails.sendEmail(email, "", "", "Chembench Password Reset", message);

        return SUCCESS;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}