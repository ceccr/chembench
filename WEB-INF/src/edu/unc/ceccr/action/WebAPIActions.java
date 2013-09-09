package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

    /**
     * Generates a predictor from a list of structures and activities.
     *
     * Accepts three URL parameters with comma-separated values:
     *
     * name: The names of the compounds
     * smiles: The structures of the above compounds in SMILES format
     * activities: The activity values for the compounds
     *
     * Using these, this method generates an SDF and ACT file and creates
     * a new DataSet object. It then creates a new predictor based on that
     * dataset and returns the predictor's id.
     *
     * (Before returning from the method, the value of generatedPredictorId
     * should be set to the id that is to be returned to the requester.)
     *
     * @return
     *      SUCCESS if the predictor generation succeeded, or ERROR otherwise.
     */
    public String generatePredictor() {
        ActionContext context = ActionContext.getContext();
        Map<String, Object> params = context.getParameters();

        if (params.get("names") == null) {
            errorStrings.add("No compound names provided.");
            return ERROR;
        }
        if (params.get("smiles") == null) {
            errorStrings.add("No SMILES-format structures provided.");
            return ERROR;
        }
        if (params.get("activities") == null) {
            errorStrings.add("No activity values provided.");
            return ERROR;
        }

        String[] names = ((String[]) params.get("names"))[0].split(",");
        String[] smiles = ((String[]) params.get("smiles"))[0].split(",");
        String[] activities =
            ((String[]) params.get("activities"))[0].split(",");

        if (!(names.length == smiles.length &&
                    smiles.length == activities.length)) {
            errorStrings.add("The values provided do not match in length.");
            errorStrings.add("Number of compounds: " + names.length);
            errorStrings.add("Number of SMILES strings: " + smiles.length);
            errorStrings.add("Number of activity values: " + activities.length);
            return ERROR;
        }

        for (int i = 0; i < smiles.length; i++) {
            logger.debug(String.format(
                        "Compound %d: name=%s, SMILES=%s, activity=%s",
                        i, names[i], smiles[i], activities[i]));
        }

        // convert smiles strings into sdf file
        String tempSdfFile;
        try {
            tempSdfFile = convertSmilesToSdf(names, smiles);
        } catch (IOException e) {
            errorStrings.add("SMILES to SDF conversion failed: " +
                             e.getMessage());
            return ERROR;
        } catch (RuntimeException e) {
            errorStrings.add(e.getMessage());
            return ERROR;
        }

        // NYI
        errorStrings.add("This method has not been implemented yet.");
        return ERROR;
    }

    /**
     * Converts SMILES strings into a single SDF structure file.
     *
     * @param names
     *      An array of compound names that correspond to the given SMILES
     *      strings.
     * @param smiles
     *      An array of SMILES strings to convert.
     * @return
     *      A path to the generated SDF file.
     */
    private String convertSmilesToSdf(String[] names, String[] smiles)
            throws IOException {
        assert names.length == smiles.length;

        // create a temporary file and write out the names + smiles to it
        File tempFile = File.createTempFile("smiles", ".tmp");
        String tempFileLocation = tempFile.getAbsolutePath();
        FileWriter fw = new FileWriter(tempFile);
        BufferedWriter out = new BufferedWriter(fw);
        try {
            for (int i = 0; i < names.length; i++) {
                if (smiles[i].isEmpty()) {
                    // clean up and exit
                    out.close();
                    tempFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has an empty SMILES string.", i + 1));
                }
                if (names[i].isEmpty()) {
                    out.close();
                    tempFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has an empty compound name.", i + 1));
                }
                out.write(smiles[i]);
                out.write(" ");
                out.write(names[i]);

                logger.debug(String.format("Wrote line %d: \"%s %s\", file=%s",
                        i + 1, smiles[i], names[i], tempFileLocation));
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        } finally {
            out.close();
        }
        return tempFileLocation;
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

