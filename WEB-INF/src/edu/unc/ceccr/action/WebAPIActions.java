package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

/**
 * Actions for handling Web API requests.
 *
 * @author Ian Kim <iansjk@gmail.com>
 * @since 2013-09-08
 */
public class WebAPIActions extends ActionSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(
            WebAPIActions.class.getName());
    private List<String> errorStrings = new ArrayList<String>();
    private int generatedPredictorId;

    public String generatePredictor() {
        // NYI
        errorStrings.add("This method has not been implemented yet.");
        return ERROR;
    }

    /**
     * Converts SMILES strings into a single SDF structure file.
     *
     * @param smiles
     *      An array of SMILES strings to convert into a single
     *      concatentated SDF file.
     * @return
     *      A path to the generated SDF file.
     */
    private String convertSmilesToSdf(String[] smiles) {
        // NYI
        return null;
    }

    // --- begin getters / setters ---
    public List<String> getErrorStrings() {
        return errorStrings;
    }
    public void setErrorStrings(List<String> errorStrings) {
        this.errorStrings = errorStrings;
    }
    public int getGeneratedPredictorId() {
        return generatedPredictorId;
    }
    public void setGeneratedPredictorId() {
        this.generatedPredictorId = generatedPredictorId;
    }
}

