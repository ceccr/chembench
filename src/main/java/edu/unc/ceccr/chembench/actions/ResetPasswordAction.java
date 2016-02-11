package edu.unc.ceccr.chembench.actions;

//struts2

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.persistence.UserRepository;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ResetPasswordAction extends ActionSupport {

    private static final Logger logger = Logger.getLogger(ResetPasswordAction.class.getName());
    private String userName;
    private String email;
    private String errorMessage;
    private UserRepository userRepository;

    @Autowired
    public ResetPasswordAction(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String execute() throws Exception {
        // set up session to check user name and email
        User user = userRepository.findByUserName(userName);
        if (user == null || !user.getEmail().equals(email)) {
            errorMessage = "Invalid username or email!";
            return ERROR;
        }

        //email matches
        String randomPassword = Utility.randomPassword();
        user.setPassword(Utility.encrypt(randomPassword));
        userRepository.save(user);
        // message to user

        //email
        String message = user.getFirstName() + ", your Chembench password has been reset." + "<br/>" + "Your " +
                "username: " + user.getUserName() + "<br/> Your new password is: " + randomPassword + "<br/><br/><br/>"
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
