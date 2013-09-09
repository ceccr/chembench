package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;

import edu.unc.ceccr.global.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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

        // generate sdf and act files from url parameters
        String sdfFile;
        String actFile;
        try {
            sdfFile = convertSmilesToSdf(names, smiles);
            actFile = createAct(names, activities);
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
     *      An array of compound names.
     * @param smiles
     *      An array of SMILES strings corresponding to the above compounds'
     *      structures.
     * @return
     *      A path to the generated SDF file.
     */
    private String convertSmilesToSdf(String[] names, String[] smiles)
            throws IOException
    {
        assert names.length == smiles.length;

        // create a temporary file and write out the names + smiles to it
        File tempDir = Files.createTempDirectory("chembench").toFile();
        logger.debug("Created temporary directory: " + tempDir);
        File inputFile = File.createTempFile("smiles", ".tmp", tempDir);
        FileWriter fw = new FileWriter(inputFile);
        BufferedWriter out = new BufferedWriter(fw);
        logger.debug("Creating input file for molconvert: " +
                     inputFile.getAbsolutePath());
        try {
            for (int i = 0; i < names.length; i++) {
                if (smiles[i].isEmpty()) {
                    // clean up and exit
                    out.close();
                    inputFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has no SMILES string.", i + 1));
                }
                if (names[i].isEmpty()) {
                    out.close();
                    inputFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has no name.", i + 1));
                }
                out.write(smiles[i]);
                out.write(" ");
                out.write(names[i]);
                out.newLine();

                logger.debug(String.format("Wrote line %d: \"%s %s\"",
                        i + 1, smiles[i], names[i], inputFile.getAbsolutePath()));
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        } finally {
            out.close();
        }

        // convert the input file to SDF using JChem molconvert
        File outputFile = File.createTempFile("out", ".sdf", tempDir);
        ProcessBuilder pb = new ProcessBuilder("molconvert",
                "sdf", // output format
                inputFile.getName(),
                "-o", outputFile.getName());
        pb.directory(tempDir);
        logger.debug(String.format(
                    "Converting SMILES to SDF, command: %s, directory: %s",
                    Arrays.toString(pb.command().toArray()), tempDir));
        Process p = pb.start();
        int returnCode = -1;
        try {
            returnCode = p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (returnCode != 0) {
            throw new RuntimeException("SMILES to SDF conversion failed.");
        }
        logger.debug("Generated raw SDF, location: " +
                outputFile.getAbsolutePath());

        // standardize the SDF
        String standardizedFileName = outputFile.getName() + ".standard";
        pb = new ProcessBuilder("standardize",
                outputFile.getName(), // input file
                "-c", // configuration xml to use
                new File(Constants.CECCR_BASE_PATH,
                    "config/standardizer.xml").getAbsolutePath(),
                "-f", "sdf", // output format
                "-o", standardizedFileName);
        pb.directory(tempDir);
        logger.debug(String.format(
                    "Standardizing structures, command: %s, directory: %s",
                    Arrays.toString(pb.command().toArray()), tempDir));
        p = pb.start();
        returnCode = -1;
        try {
            returnCode = p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (returnCode != 0) {
            throw new RuntimeException("SDF standardization failed.");
        }
        logger.debug("Generated standardized SDF, location: " +
                new File(tempDir, standardizedFileName).getAbsolutePath());

        return standardizedFileName;
    }

    /**
     * Creates an ACT (activity) file from arrays of names and activities.
     *
     * @param names
     *      An array of compound names.
     * @param activities
     *      An array of activity values corresponding to the above compounds'
     *      activity values in the experiment.
     * @return
     *      A path to the generated ACT file.
     */
    private String createAct(String[] names, String[] activities)
        throws IOException
    {
        assert names.length == activities.length;

        File actFile = File.createTempFile("activities", ".tmp");
        FileWriter fw = new FileWriter(actFile);
        BufferedWriter out = new BufferedWriter(fw);
        logger.debug("Creating activity file: " + actFile.getAbsolutePath());
        try {
            for (int i = 0; i < names.length; i++) {
                if (names[i].isEmpty()) {
                    out.close();
                    actFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has no name.", i + 1));
                }
                if (activities[i].isEmpty()) {
                    out.close();
                    actFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has no associated activity value.",
                            i + 1));
                }
                try {
                    Double.parseDouble(activities[i]);
                } catch (NumberFormatException e) {
                    out.close();
                    actFile.delete();
                    throw new RuntimeException(String.format(
                            "Compound %d has a non-number activity value.", i + 1));
                }
                out.write(names[i]);
                out.write(" ");
                out.write(activities[i]);
                out.newLine();

                logger.debug(String.format("Wrote line %d: \"%s %s\"",
                        i + 1, names[i], activities[i],
                        actFile.getAbsolutePath()));
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        } finally {
            out.close();
        }

        return actFile.getAbsolutePath();
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

