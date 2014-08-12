package edu.unc.ceccr.chembench.action;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.SoftwareLink;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//struts2

public class SoftwareLinksAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(SoftwareLinksAction.class.getName());

    private List<SoftwareLink> softwareLinks = Lists.newArrayList();

    private boolean userIsAdmin = false;
    private String userName = "";
    private String name;
    private String type;
    private String newType;
    private String availability;
    private String function;
    private String reference;
    private String url;

    //this has to be a hashmap for struts2 to be able to make an <s:select> dropdown.
    private HashMap<String, String> softwareTypes = new HashMap<String, String>();

    public String loadPage() throws Exception {

        String result = SUCCESS;

        //check that the user is logged in
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.debug("FreeSoftwareAction: No ActionContext available");
        } else {
            Session s = HibernateUtil.getSession();
            softwareLinks = (ArrayList<SoftwareLink>) PopulateDataObjects.populateSoftwareLinks(s);

            for (SoftwareLink sl : softwareLinks) {
                softwareTypes.put(sl.getType(), sl.getType());
            }

            //get the username if the user is logged in
            User user = (User) context.getSession().get("user");
            if (user != null) {
                userName = user.getUserName();
                if (Utility.isAdmin(userName)) {
                    userIsAdmin = true;
                }
            }
        }

        return result;
    }

    public String addSoftware() throws Exception {

        String result = SUCCESS;

        //check that the user is logged in
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.debug("FreeSoftwareAction: No ActionContext available");
        } else {
            //verify the user is logged in
            //get the username if the user is logged in
            User user = (User) context.getSession().get("user");
            if (user == null) {
                return ERROR;
            } else {
                userName = user.getUserName();

                SoftwareLink sl = new SoftwareLink();
                sl.setAvailability(availability);
                sl.setFunction(function);
                sl.setName(name);
                sl.setUserName(userName);
                sl.setReference(reference);
                if (newType != null && !newType.isEmpty()) {
                    sl.setType(newType);
                } else {
                    sl.setType(type);
                }
                if (url.startsWith("www")) {
                    //fix the most common error people make when typing urls...
                    url = "http://" + url;
                }
                sl.setUrl(url);

                Session s = HibernateUtil.getSession();
                Transaction tx = null;

                try {
                    tx = s.beginTransaction();
                    s.saveOrUpdate(sl);
                    tx.commit();
                } catch (RuntimeException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    logger.error(e);
                } finally {
                    s.close();
                }
            }
        }

        return result;
    }

    public String deleteSoftwareLink() throws Exception {

        String result = SUCCESS;

        //check that the user is logged in
        ActionContext context = ActionContext.getContext();

        if (context == null) {
            logger.debug("FreeSoftwareAction: No ActionContext available");
        } else {
            //verify that the user is logged in
            User user = (User) context.getSession().get("user");

            //get the software link to be deleted
            Long idToDelete = Long.parseLong(((String[]) context.getParameters().get("id"))[0]);

            Session s = HibernateUtil.getSession();
            SoftwareLink sl = PopulateDataObjects.getSoftwareLinkById(idToDelete, s);

            if (user != null && (Utility.isAdmin(user.getUserName()) || user.getUserName().equals(sl.getUserName()))) {


                //remove it from the database
                Transaction tx = null;
                try {
                    tx = s.beginTransaction();
                    s.delete(sl);
                    tx.commit();
                } catch (RuntimeException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    logger.error(e);
                    return ERROR;
                } finally {
                    s.close();
                }

            } else {
                return ERROR;
            }
        }

        return result;
    }

    public List<SoftwareLink> getSoftwareLinks() {
        return softwareLinks;
    }

    public void setSoftwareLinks(List<SoftwareLink> softwareLinks) {
        this.softwareLinks = softwareLinks;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isUserIsAdmin() {
        return userIsAdmin;
    }

    public void setUserIsAdmin(boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public HashMap<String, String> getSoftwareTypes() {
        return softwareTypes;
    }

    public void setSoftwareTypes(HashMap<String, String> softwareTypes) {
        this.softwareTypes = softwareTypes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
