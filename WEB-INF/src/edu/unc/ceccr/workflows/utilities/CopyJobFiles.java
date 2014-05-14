package edu.unc.ceccr.workflows.utilities;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopyJobFiles {

    private static Logger logger = Logger.getLogger(CopyJobFiles.class.getName());

    public static void getDatasetFiles(String userName,
                                       DataSet dataset,
                                       String jobType,
                                       String toDir) throws Exception {
        // gathers the dataset files needed for a modeling or prediction run

        String allUserDir = Constants.CECCR_USER_BASE_PATH + "all-users"
                + "/DATASETS/" + dataset.getName() + "/";
        String userFilesDir = Constants.CECCR_USER_BASE_PATH + userName
                + "/DATASETS/" + dataset.getName() + "/";

        String fromDir = "";
        if (dataset.getUserName().equals(userName)) {
            // get it from the user's DATASET directory
            fromDir = userFilesDir;
        } else {
            fromDir = allUserDir;
        }

        String sdFile = dataset.getSdfFile();
        String actFile = dataset.getActFile();
        String xFile = dataset.getXFile();
        String externalSplitXFile = "";

        // make sure toDir exists
        new File(toDir).mkdirs();

        if (dataset.getDatasetType().equals(Constants.MODELING)
                || dataset.getDatasetType().equals(
                Constants.MODELINGWITHDESCRIPTORS)) {
            if (dataset.getSplitType().equals(Constants.NFOLD)
                    && jobType.equals(Constants.MODELING)) {
                // use the right external set for this fold
                Pattern p = Pattern.compile("fold_(\\d+)_of_(\\d+)");
                Matcher matcher = p.matcher(toDir);
                int foldNum = 0;
                if (matcher.find()) {
                    foldNum = Integer.parseInt(matcher.group(1));
                } else {
                    throw new Exception(
                            "Could not find fold number in path: " + toDir);
                }

                String datasetDir = Constants.CECCR_USER_BASE_PATH
                        + dataset.getUserName() + "/DATASETS/"
                        + dataset.getName() + "/";
                String foldPath = datasetDir + dataset.getActFile() + ".fold"
                        + (foldNum);
                String extPath = toDir + "ext_0.a";
                FileAndDirOperations.copyFile(foldPath, extPath);
                DatasetFileOperations.makeXFromACT(toDir, "ext_0.a");
            } else {
                // for nfold, external split file is produced from fold info.
                // All other cases will copy the dataset's ext_0.x.
                externalSplitXFile = Constants.EXTERNAL_SET_X_FILE;
            }
        }

        logger.debug("User: " + userName + " " + "Fetching dataset files from " + userFilesDir);
        if (!sdFile.equals("")) {
            FileAndDirOperations.copyFile(fromDir + sdFile, toDir + sdFile);
        }
        if (!actFile.equals("")) {
            FileAndDirOperations.copyFile(fromDir + actFile, toDir + actFile);
        }
        if (!xFile.equals("")) {
            FileAndDirOperations.copyFile(fromDir + xFile, toDir + xFile);
        }
        if (!externalSplitXFile.equals("")
                && new File(fromDir + externalSplitXFile).exists()) {
            FileAndDirOperations.copyFile(fromDir + externalSplitXFile, toDir
                    + externalSplitXFile);
        }
        String descriptorDir = "Descriptors/";
        if (new File(fromDir + descriptorDir).exists()) {
            FileAndDirOperations.copyDirContents(fromDir + descriptorDir,
                    toDir, false);
        }
    }

    public static void getPredictorFiles(String userName,
                                         Predictor predictor,
                                         String toDir) throws Exception {
        // gathers the predictor files needed for a prediction run
        String fromDir;
        String predictorName = predictor.getName();

        if (predictor.getUserName().equals(userName)) {
            fromDir = new File(new File(
                    Constants.CECCR_USER_BASE_PATH,
                    userName),
                    "/PREDICTORS"
            ).getAbsolutePath();
        } else {
            fromDir = new File(
                    Constants.CECCR_USER_BASE_PATH,
                    "all-users/PREDICTORS/").getAbsolutePath();
        }

        if (predictor.getParentId() != null) {
            Session s = HibernateUtil.getSession();
            Predictor parentPredictor = PopulateDataObjects.getPredictorById(
                    predictor.getParentId(), s);
            String parentPredictorName = parentPredictor.getName();
            fromDir = new File(new File(
                    fromDir,
                    parentPredictorName),
                    predictorName
            ).getAbsolutePath();
        } else {
            fromDir = new File(fromDir, predictorName).getAbsolutePath();
        }

        logger.info(String.format(
                "Copying predictor: USER=%s, SOURCE=%s, DESTINATION=%s",
                userName, fromDir, toDir));

        String[] dirListing = new File(fromDir).list();
        int symlinkedFileCount = 0;
        int deepCopiedFileCount = 0;
        for (String filename : dirListing) {
            if (!(new File(fromDir, filename).isDirectory())) {
                Path source = new File(fromDir, filename).toPath();
                Path destination = new File(toDir, filename).toPath();

                // for symlinks; "link" denotes the actual symlink file-object,
                // and "target" is where the link points to (hence the reversal)
                Path link = destination;
                Path target = source;

                try {
                    if (filename.endsWith(".x")) {
                        // deep-copy descriptor matrices
                        Files.copy(source, destination);
                        deepCopiedFileCount++;
                    } else {
                        // don't copy other predictor files, just symlink them
                        Files.createSymbolicLink(link, target);
                        symlinkedFileCount++;
                    }
                } catch (FileAlreadyExistsException e) {
                    logger.error(String.format(
                            "Couldn't copy %s -> %s; file already exists",
                            source, destination));
                }
            }
        }

        logger.info(String.format(
                "Deep-copied %d files, symlinked %d files.",
                deepCopiedFileCount, symlinkedFileCount));
    }
}
