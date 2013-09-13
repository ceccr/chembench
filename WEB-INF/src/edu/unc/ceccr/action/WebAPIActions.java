package edu.unc.ceccr.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.taskObjects.CreateDatasetTask;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.PopulateDataObjects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
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
public class WebAPIActions extends ActionSupport
{
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(
            WebAPIActions.class.getName());
    private List<String> errorStrings = new ArrayList<String>();

    private static final String WEBAPI_USER_NAME = "webapi";
    private static final int TIMEOUT = 50000; // in ms
    private static final int POLLING_INTERVAL = 5000; // in ms
    private long generatedPredictorId;

    /**
     * Generates a predictor from a list of structures and activities.
     *
     * Accepts three URL parameters with comma-separated values:
     *
     * names: The names of the compounds
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
    public String generatePredictor()
    {
        ActionContext context = ActionContext.getContext();
        Map<String, Object> params = context.getParameters();
        String activityType = "CATEGORY";

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

        // log input and determine if activities are category or continuous
        for (int i = 0; i < smiles.length; i++) {
            logger.debug(String.format(
                        "Compound %d: name=%s, SMILES=%s, activity=%s",
                        i + 1, names[i], smiles[i], activities[i]));

            int activityValue = -1;
            try {
                activityValue = Integer.parseInt(activities[i]);
            } catch (NumberFormatException e) {
                logger.debug(String.format(
                        "Compound %d's activity value is not an " +
                        "integer; setting data type to CONTINUOUS.", i + 1));
                activityType = "CONTINUOUS";
            }

            // if the activity value was an integer, make sure it is
            // non-negative
            if (activityType.equals("CATEGORY")) {
                if (activityValue < 0) {
                    logger.debug(String.format(
                        "Compound %d's activity value is negative; " +
                        "setting data type to CONTINUOUS.", i + 1));
                    activityType = "CONTINUOUS";
                }
            }
        }

        // generate sdf and act files from url parameters
        String tempSdfFilePath;
        String tempActFilePath;
        try {
            tempSdfFilePath = convertSmilesToSdf(names, smiles);
            tempActFilePath = createAct(names, activities);
        } catch (IOException e) {
            logger.error(e);
            errorStrings.add("File creation failed: " + e.getMessage());
            return ERROR;
        } catch (RuntimeException e) {
            errorStrings.add(e.getMessage());
            return ERROR;
        }

        DataSet dataset = null;
        try {
            dataset = this.generateDataset(tempSdfFilePath, tempActFilePath,
                                           activityType, names.length);
        } catch (Exception e) {
            // (error messages have already been added by generateDataset())
            return ERROR;
        }

        // create a dummy modeling form and populate it with our parameters
        ModelingFormActions form = new ModelingFormActions();
        // set only what we have to; use defaults where possible
        // what we want:
        // - RandomForest
        // - CDK descriptors
        // - no external split
        String predictorName = this.generatePredictorName();
        form.setModelingType(Constants.RANDOMFOREST);
        form.setJobName(predictorName);
        form.setSelectedDatasetId(dataset.getId());
        try {
            QsarModelingTask task = new QsarModelingTask(
                    WEBAPI_USER_NAME, form);
            CentralDogma centralDogma = CentralDogma.getInstance();
            centralDogma.addJobToIncomingList(
                    WEBAPI_USER_NAME, predictorName, task,
                    dataset.getNumCompound(),
                    1, // number of models (only 1 for RF w/ defaults)
                    "false" // don't email on completion
            );
        } catch (Exception e) {
            logger.error(e);
            errorStrings.add("Modeling job creation failed: " +
                             e.getMessage());
            return ERROR;
        }

        // retrieve the predictor that we just created and set the
        // generatedPredictorId property to the id of the new predictor;
        // then the JSP will respond with the property value
        Session session = null;
        Predictor newPredictor = null;
        try {
            session = HibernateUtil.getSession();
            newPredictor = PopulateDataObjects.getPredictorByName(
                    predictorName, WEBAPI_USER_NAME, session);
        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e);
            errorStrings.add(
                    "Failed to retrieve session or object from database.");
            return ERROR;
        } finally {
            session.close();
        }
        generatedPredictorId = newPredictor.getId();
        return SUCCESS;
    }

    /**
     * Generates a dataset from an SDF file and an ACT file.
     *
     * The object that is returned is the state of the DataSet upon execution
     * completion, i.e. when its jobCompleted attribute equals "YES".
     *
     * @param sdfFilePath
     *      The path to the SDF file to use.
     * @param actFilePath
     *      The path to the ACT file to use.
     * @param activityType
     *      The type of activity values in the dataset; either "CATEGORY" or
     *      "CONTINUOUS".
     * @param numCompounds
     *      The number of compounds in the dataset.
     * @return
     *      An instance of the created DataSet object as retrieved from the
     *      database after it has finished executing on the job queue.
     */
    private DataSet generateDataset(String sdfFilePath, String actFilePath,
            String activityType, int numCompounds) throws Exception
    {
        // generate a name for the dataset using current time in ms
        String datasetName = this.generateDatasetName();

        // copy the sdf and act files into the right folder:
        // <user-root>/<user-name>/<dataset-name>/
        Path sdfSource = new File(sdfFilePath).toPath();
        Path actSource = new File(actFilePath).toPath();

        Path destinationDir = Paths.get(Constants.CECCR_USER_BASE_PATH,
                WEBAPI_USER_NAME, "DATASETS", datasetName);
        assert Files.exists(destinationDir) == false; // we shouldn't reuse dirs
        try {
            Files.createDirectories(destinationDir);
        } catch (IOException e) {
            logger.error(e);
            errorStrings.add("Failed to create destination directory: " +
                             e.getMessage());
            throw e;
        }

        // sadly there isn't a new Path(Path p1, Path p2) method...
        Path sdfDestination = Paths.get(
                destinationDir.toString(),
                sdfSource.getFileName().toString());
        Path actDestination = Paths.get(
                destinationDir.toString(),
                actSource.getFileName().toString());

        try {
            logger.debug(String.format(
                    "Copying SDF file: source=%s, destination=%s",
                    sdfSource, sdfDestination));
            Files.copy(sdfSource, sdfDestination);
            logger.debug(String.format(
                    "Copying ACT file: source=%s, destination=%s",
                    actSource, actDestination));
            Files.copy(actSource, actDestination);
        } catch (IOException e) {
            logger.error(e);
            errorStrings.add("Couldn't copy SDF or ACT file: " +
                             e.getMessage());
            throw e;
        }

        // submit the dataset job
        CreateDatasetTask task = new CreateDatasetTask(
                WEBAPI_USER_NAME,
                Constants.MODELING, // dataset type
                sdfDestination.getFileName().toString(),
                actDestination.getFileName().toString(),
                "", // X file name for uploaded descriptors; N/A
                "", // descriptor type for uploaded descriptors; N/A
                activityType, // CATEGORY or CONTINUOUS
                "true", // standardize the SDF
                Constants.USERDEFINED, // user-defined split type
                "", // no scaling has been done on this
                "0", // number of external compounds, if RANDOM split
                "0", // number of external folds, if NFOLD
                "false", // don't use activity binning if RANDOM split
                "", // empty external compound list
                datasetName,
                "", // no paper reference
                "", // empty dataset description
                "false" // don't generate Mahalanobis
            );
        try {
            CentralDogma centralDogma = CentralDogma.getInstance();
            centralDogma.addJobToIncomingList(
                    WEBAPI_USER_NAME, datasetName, task, numCompounds,
                    0, // number of models
                    "false" // don't email on completion
            );
        } catch (Exception e) {
            logger.error(e);
            errorStrings.add("Dataset job creation failed: " + e.getMessage());
            throw e;
        }

        // poll repeatedly until the dataset task is done
        logger.debug(String.format(
                "Waiting for dataset task to complete: user=%s, name=%s",
                WEBAPI_USER_NAME, datasetName));
        int timeLeft = TIMEOUT;
        boolean jobFinished = false;
        DataSet newDataset = null;
        try {
            while (timeLeft > 0 && !jobFinished) {
                Session session = HibernateUtil.getSession();
                newDataset = PopulateDataObjects.getDataSetByName(
                        datasetName, WEBAPI_USER_NAME, session);
                logger.debug("Job completion status: " +
                             newDataset.getJobCompleted());
                if (newDataset.getJobCompleted().equals("YES")) {
                    jobFinished = true;
                } else {
                    Thread.sleep(POLLING_INTERVAL);
                    timeLeft -= POLLING_INTERVAL;
                }
                session.close();
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e);
            errorStrings.add("Failed to retrieve object from database.");
            throw e;
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }

        if (!jobFinished) {
            // we timed out; raise an exception to be echoed as an error message
            // by the caller.
            String error = "Dataset job timed out. The server may be too " +
                       "busy to handle your request. Please try again later.";
            errorStrings.add(error);
            throw new RuntimeException(error);
        }

        logger.debug(String.format(
                "Dataset job complete: name=%s, id=%d",
                datasetName, newDataset.getId()));
        return newDataset;
    }

    /**
     * Generates a dataset name to be used for new CreateDatasetTasks.
     *
     * @return
     *      A suitable dataset name.
     */
    private String generateDatasetName() {
        return "bard-dataset" + System.currentTimeMillis();
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
                        i + 1, smiles[i], names[i]));
            }
        } catch (IOException e) {
            logger.error(e);
            throw e;
        } finally {
            out.close();
        }

        // convert the input file to SDF using JChem molconvert
        File sdfFile = File.createTempFile("out", ".sdf", tempDir);
        ProcessBuilder pb = new ProcessBuilder("molconvert",
                "sdf", // output format
                inputFile.getName(),
                "-o", sdfFile.getName());
        pb.directory(tempDir);
        logger.debug(String.format(
                    "Converting SMILES to SDF, command: %s, directory: %s",
                    Arrays.toString(pb.command().toArray()), tempDir));
        Process p = pb.start();
        int returnCode = -1;
        try {
            returnCode = p.waitFor();
        } catch (InterruptedException e) {
            logger.error(e);
            Thread.currentThread().interrupt();
        }

        if (returnCode != 0) {
            throw new RuntimeException("SMILES to SDF conversion failed.");
        }

        String sdfFilePath = sdfFile.getAbsolutePath();
        logger.debug("Generated SDF, location: " + sdfFilePath);
        return sdfFilePath;
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

        File actFile = File.createTempFile("activities", ".act");
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
                        i + 1, names[i], activities[i]));
            }
        } catch (IOException e) {
            throw e;
        } finally {
            out.close();
        }

        return actFile.getAbsolutePath();
    }

    // --- begin getters / setters ---
    public List<String> getErrorStrings()
    {
        return errorStrings;
    }
    public void setErrorStrings(List<String> errorStrings)
    {
        this.errorStrings = errorStrings;
    }
    public long getGeneratedPredictorId()
    {
        return generatedPredictorId;
    }
    public void setGeneratedPredictorId()
    {
        this.generatedPredictorId = generatedPredictorId;
    }
}

