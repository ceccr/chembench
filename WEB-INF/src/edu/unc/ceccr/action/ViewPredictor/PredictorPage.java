package edu.unc.ceccr.action.ViewPredictor;

// struts2

import com.opensymphony.xwork2.ActionContext;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;

public class PredictorPage extends ViewPredictorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(PredictorPage.class.getName());

    private String predictorDescription = "";
    private String predictorReference = "";
    private String editable = "";

    public String load() throws Exception {
        String result = getBasicParameters();
        if (!result.equals(SUCCESS))
            return result;

        if (context.getParameters().get("editable") != null) {
            if (user.getIsAdmin().equals(Constants.YES)
                    || user.getUserName().equals(
                    selectedPredictor.getUserName())) {
                editable = "YES";
            }
        } else {
            editable = "NO";
        }
        predictorDescription = selectedPredictor.getDescription();
        predictorReference = selectedPredictor.getPaperReference();

        // the predictor has now been viewed. Update DB accordingly.
        if (!selectedPredictor.getHasBeenViewed().equals(Constants.YES)) {
            selectedPredictor.setHasBeenViewed(Constants.YES);
            Transaction tx = null;
            try {
                session = HibernateUtil.getSession();
                tx = session.beginTransaction();
                session.saveOrUpdate(selectedPredictor);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                logger.error(e);
            } finally {
                session.close();
            }
        }

        // go to the page
        return result;
    }

    public String update() throws Exception {
        // check that the user is logged in
        context = ActionContext.getContext();

        if (context != null
                && context.getParameters().get("objectId") != null) {
            // get predictorId id
            objectId = ((String[]) context.getParameters().get("objectId"))[0];
            String[] predictorIdAsStringArray = new String[1];
            predictorIdAsStringArray[0] = objectId;
            context.getParameters().put("id", predictorIdAsStringArray);
            predictorDescription = ((String[]) context.getParameters().get(
                    "predictorDescription"))[0];
            predictorReference = ((String[]) context.getParameters().get(
                    "predictorReference"))[0];
            session = HibernateUtil.getSession();
            selectedPredictor = PopulateDataObjects.getPredictorById(Long
                    .parseLong(objectId), session);
            session.close();
            selectedPredictor.setDescription(predictorDescription);
            selectedPredictor.setPaperReference(predictorReference);
            Transaction tx = null;
            try {
                session = HibernateUtil.getSession();
                tx = session.beginTransaction();
                session.saveOrUpdate(selectedPredictor);
                tx.commit();
            } catch (Exception ex) {
                logger.error(ex);
            } finally {
                session.close();
            }
        }
        return load();
    }

    // getters and setters

    public String getPredictorDescription() {
        return predictorDescription;
    }

    public void setPredictorDescription(String predictorDescription) {
        this.predictorDescription = predictorDescription;
    }

    public String getPredictorReference() {
        return predictorReference;
    }

    public void setPredictorReference(String predictorReference) {
        this.predictorReference = predictorReference;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    // end getters and setters

}