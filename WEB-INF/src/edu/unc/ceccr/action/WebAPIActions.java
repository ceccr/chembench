package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

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

    public String generatePredictor() {
        // NYI
        return ERROR;
    }

    /**
     * Converts SMILES strings into SDF structure files, then concatenates them
     * into a single SDF file.
     *
     * @param smiles - 
     *      An array of SMILES strings to convert into a single
     *      concatentated SDF file.
     * @return -
     *      A path to the generated SDF file.
     */
    private String convertSmilesToSdf(String[] smiles) {
        // NYI
        return null;
    }
}

