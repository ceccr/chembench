package edu.unc.ceccr.chembench.actions.api;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.persistence.User;

public class UserAction extends ActionSupport {
    private User user = new User();

    public String getCurrentUser() {
        User user = User.getCurrentUser();
        if (user != null) {
            this.user = user;
        }
        return SUCCESS;
    }

    public User getUser() {
        return user;
    }
}
