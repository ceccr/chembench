package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.persistence.User;

public class APIAction extends ActionSupport {
    private static final String BAD_REQUEST = "badRequest";
    private static final String UNAUTHORIZED = "unauthorized";
    private String error;

    public String predictSmiles() {
        User user = (User) ActionContext.getContext().getSession().get("user");
        if (user == null) {
            return UNAUTHORIZED;
        }

        return SUCCESS;
    }

    public String getError() {
        return error;
    }
}
