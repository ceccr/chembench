package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.persistence.User;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.ArrayList;

public abstract class ViewAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(ViewAction.class.getName());
    protected ArrayList<String> errorStrings = new ArrayList<String>();
    protected Session session;
    protected ActionContext context;
    protected User user;
    protected String objectId;

    public String checkBasicParams() {

        try {

            context = ActionContext.getContext();
            if (context == null) {
                logger.debug("No ActionContext available");
                return ERROR;
            }


            if (context.getSession().get("user") == null) {
                logger.debug("No user is logged in.");
                return LOGIN;
            } else {
                user = (User) context.getSession().get("user");
            }


            if (context.getParameters().get("id") != null) {
                objectId = ((String[]) context.getParameters().get("id"))[0];
            } else {
                logger.debug("No ID supplied.");
                errorStrings.add("No ID supplied.");
                return ERROR;
            }
            if (objectId.trim().isEmpty() || !objectId.matches("^\\d*$")) {
                logger.debug("No ID supplied.");
                errorStrings.add("No ID supplied.");
                return ERROR;
            }
        } catch (Exception e) {
            logger.error(e);
            errorStrings.add(e.getMessage());
            return ERROR;
        }
        return SUCCESS;

    }

    public ArrayList<String> getErrorStrings() {
        return errorStrings;
    }

    public void setErrorStrings(ArrayList<String> errorStrings) {
        this.errorStrings = errorStrings;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObject(String objectId) {
        this.objectId = objectId;
    }

}
