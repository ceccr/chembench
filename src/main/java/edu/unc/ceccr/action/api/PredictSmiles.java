package edu.unc.ceccr.action.api;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import edu.unc.ceccr.persistence.User;

public class PredictSmiles implements Action {
    private static final String BAD_REQUEST = "badRequest";
    private static final String UNAUTHORIZED = "unauthorized";
    private String error;

    @Override
    public String execute() throws Exception {
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
