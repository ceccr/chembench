package edu.unc.ceccr.chembench.actions.api;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import edu.unc.ceccr.chembench.persistence.HibernateUtil;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import org.hibernate.Session;

public class PredictSmiles implements Action {
    private static final String BAD_REQUEST = "badRequest";
    private static final String UNAUTHORIZED = "unauthorized";
    private String error;

    private String smiles = null;
    private long predictorId = -1;
    private Double cutoff = null; // where "null" indicates "do not use"

    @Override
    public String execute() throws Exception {
        User user = (User) ActionContext.getContext().getSession().get("user");
        if (user == null) {
            return UNAUTHORIZED;
        }

        if (smiles == null || smiles.isEmpty()) {
            return BAD_REQUEST;
        }

        // check for valid predictorId, returning Bad Request otherwise
        Session s = HibernateUtil.getSession();
        Predictor predictor = PopulateDataObjects.getPredictorById(predictorId, s);
        s.close();
        if (predictor == null) {
            return BAD_REQUEST;
        }

        // TODO make the prediction here

        return SUCCESS;
    }

    public String getError() {
        return error;
    }

    public Double getCutoff() {
        return cutoff;
    }

    public void setCutoff(Double cutoff) {
        this.cutoff = cutoff;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public long getPredictorId() {
        return predictorId;
    }

    public void setPredictorId(long predictorId) {
        this.predictorId = predictorId;
    }
}
