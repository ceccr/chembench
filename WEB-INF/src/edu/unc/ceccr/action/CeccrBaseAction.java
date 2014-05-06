package edu.unc.ceccr.action;


//struts2

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.persistence.User;
import org.apache.log4j.Logger;

public class CeccrBaseAction extends ActionSupport {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CeccrBaseAction.class.getName());
    //====== variables used for displaying the JSP =====//
    private User user;

    public String loadPage() throws Exception {

        String result = SUCCESS;

        //check that the user is logged in
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.debug("No ActionContext available");
        } else {
            user = (User) context.getSession().get("user");

            if (user == null) {
                logger.debug("No user is logged in.");
                result = LOGIN;
                return result;
            }
        }

        return result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}