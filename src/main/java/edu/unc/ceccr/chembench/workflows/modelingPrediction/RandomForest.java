package edu.unc.ceccr.chembench.workflows.modelingPrediction;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.RandomForestParameters;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.workflows.datasets.DatasetFileOperations;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class RandomForest {
    public static final Logger logger = Logger.getLogger(RandomForest.class);

    public static final String MODEL_METADATA = "forest.json";
    public static final String EXTERNAL_SET_PREDICTION_OUTPUT = "external_set_predictions.json";

    private static final String RF_X_FILE_PREFIX = "RF_";
    private static final String BUILD_SCRIPT = "rf_build_model.py";
    private static final String PREDICT_SCRIPT = "rf_predict.py";
    private static final String MODEL_PICKLE_RAW = "forest.pkl";
    private static final String MODEL_PICKLE = "forest.pkl.gz";
    private static final String PREDICTION_OUTPUT = "predictions.json";
    private static final String Y_RANDOM_DIRECTORY = "yRandom";

    public static void preprocessXFiles(Path predictorDir, Constants.ScalingType scalingType) {
        String[] filenames = {Constants.EXTERNAL_SET_X_FILE, Constants.MODELING_SET_X_FILE};
        if (scalingType == Constants.ScalingType.NOSCALING) {
            // just rename the files to contain the RF prefix
            try {
                for (String filename : filenames) {
                    Files.move(predictorDir.resolve(filename), predictorDir.resolve(RF_X_FILE_PREFIX + filename));
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to rename X file", e);
            }
        } else {
            // if scaling was applied, the last 2 lines of a .x file will contain the scaling ranges.
            // remove these before sending the .x files to the build script.
            for (String filename : filenames) {
                preprocessXFile(predictorDir, filename);
            }
        }
    }

    private static void preprocessXFile(Path predictorDir, String filename) {
        try (BufferedReader in = Files.newBufferedReader(predictorDir.resolve(filename), Charset.defaultCharset());
             BufferedWriter out = Files
                     .newBufferedWriter(predictorDir.resolve(RF_X_FILE_PREFIX + filename), Charset.defaultCharset())) {
            List<String> lines = Lists.newArrayList();
            String line;
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }

            for (int i = 0; i < lines.size(); i++) {
                if (i < lines.size() - 2) {
                    out.write(lines.get(i));
                    out.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("X file preprocessing failed", e);
        }
    }

    public static Path setUpYRandomization(Path predictorDir) {
        Path yRandomDir;
        try {
            yRandomDir = Files.createDirectories(predictorDir.resolve(Y_RANDOM_DIRECTORY));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create y-random directory", e);
        }

        String[] filesToCopy = new String[]{RF_X_FILE_PREFIX + Constants.MODELING_SET_X_FILE,
                RF_X_FILE_PREFIX + Constants.EXTERNAL_SET_X_FILE, Constants.MODELING_SET_A_FILE,
                Constants.EXTERNAL_SET_A_FILE};
        try {
            for (String filename : filesToCopy) {
                Files.copy(predictorDir.resolve(filename), yRandomDir.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't copy original modeling .x/.act files to y-random directory", e);
        }

        Path yRandomActFile = yRandomDir.resolve(Constants.MODELING_SET_A_FILE);
        try {
            DatasetFileOperations.randomizeActivityFile(yRandomActFile, yRandomActFile);
        } catch (IOException e) {
            throw new RuntimeException("Activity randomization failed", e);
        }
        return yRandomDir;
    }

    public static void growForest(Path predictorDir, Constants.ActivityType activityType,
                                  RandomForestParameters params) {
        String command = String.format("%s %s %s %s --output %s --num-trees %s --seed %d", BUILD_SCRIPT,
                predictorDir.resolve(RF_X_FILE_PREFIX + Constants.MODELING_SET_X_FILE),
                predictorDir.resolve(Constants.MODELING_SET_A_FILE), activityType.toString().toLowerCase(),
                predictorDir.resolve(MODEL_PICKLE_RAW),
                params.getNumTrees(),
                params.getSeed());
        int exitcode = RunExternalProgram.runCommandAndLogOutput(command, predictorDir, BUILD_SCRIPT);
        if (exitcode != 0) {
            String baseMessage = "Model generation failed, exit code " + exitcode;
            Path logFilePath = predictorDir.resolve("Logs").resolve(BUILD_SCRIPT + ".err");
            throw new RuntimeException(getExceptionMessage(baseMessage, logFilePath));
        }

        command = String.format("%s %s %s --activity %s %s --output %s", PREDICT_SCRIPT,
                predictorDir.resolve(MODEL_PICKLE),
                predictorDir.resolve(RF_X_FILE_PREFIX + Constants.EXTERNAL_SET_X_FILE),
                predictorDir.resolve(Constants.EXTERNAL_SET_A_FILE), activityType.toString().toLowerCase(),
                predictorDir.resolve(EXTERNAL_SET_PREDICTION_OUTPUT));
        exitcode = RunExternalProgram.runCommandAndLogOutput(command, predictorDir, PREDICT_SCRIPT);
        if (exitcode != 0) {
            String baseMessage = "External set prediction failed, exit code " + exitcode;
            Path logFilePath = predictorDir.resolve("Logs").resolve(PREDICT_SCRIPT + ".err");
            throw new RuntimeException(getExceptionMessage(baseMessage, logFilePath));
        }
    }

    public static ScikitRandomForestPrediction readPrediction(Path predictorDir) {
        Path externalSetPredictionsPath = predictorDir.resolve(EXTERNAL_SET_PREDICTION_OUTPUT);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        ScikitRandomForestPrediction pred;
        try (BufferedReader reader = Files.newBufferedReader(externalSetPredictionsPath, StandardCharsets.UTF_8)) {
            pred = gson.fromJson(reader, ScikitRandomForestPrediction.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read external set predictions", e);
        }
        return pred;
    }

    public static Map<String, Double> getDescriptorImportance(Path predictorDir) {
        Path metadataPath = predictorDir.resolve(MODEL_METADATA);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        ScikitRandomForest rf;
        try (BufferedReader reader = Files.newBufferedReader(metadataPath, StandardCharsets.UTF_8)) {
            rf = gson.fromJson(reader, ScikitRandomForest.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read model metadata", e);
        }
        return rf.getDescriptorImportance();
    }

    public static ScikitRandomForestPrediction predict(Path predictorDir, Path predictionDir, String sdfFile) {
        // caution, hic sunt dracones: don't confuse the predictorDir and predictionDir paths! note the names!

        preprocessXFile(predictionDir, sdfFile);
        Path targetSdf = predictionDir.resolve(RF_X_FILE_PREFIX + sdfFile);

        String command = String.format("%s %s %s --output %s", PREDICT_SCRIPT, predictorDir.resolve(MODEL_PICKLE),
                predictionDir.resolve(targetSdf), predictionDir.resolve(PREDICTION_OUTPUT));
        int exitcode = RunExternalProgram.runCommandAndLogOutput(command, predictionDir, PREDICT_SCRIPT);
        if (exitcode != 0) {
            String baseMessage = "Prediction failed, exit code " + exitcode;
            Path logFilePath = predictionDir.resolve("Logs").resolve(PREDICT_SCRIPT + ".err");
            throw new RuntimeException(getExceptionMessage(baseMessage, logFilePath));
        }

        ScikitRandomForestPrediction pred;
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        try (BufferedReader reader = Files
                .newBufferedReader(predictionDir.resolve(PREDICTION_OUTPUT), StandardCharsets.UTF_8)) {
            pred = gson.fromJson(reader, ScikitRandomForestPrediction.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read prediction output", e);
        }
        return pred;
    }

    public static void cleanUp(Path predictorDir) {
        // once we have prediction stats for y-random models, we don't need any of the files anymore, so delete them
        try {
            Files.walkFileTree(predictorDir.resolve(Y_RANDOM_DIRECTORY), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            // warn, but don't worry about it
            logger.warn("Failed to delete y-random file or directory", e);
        }

        Path externalPredictions = predictorDir.resolve(EXTERNAL_SET_PREDICTION_OUTPUT);
        Path gzippedExternalPredictions = predictorDir.resolve(EXTERNAL_SET_PREDICTION_OUTPUT + ".gz");
        try (InputStream in = Files.newInputStream(externalPredictions);
             GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(gzippedExternalPredictions))) {
            byte[] buf = new byte[1024 * 4];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            Files.delete(externalPredictions);
        } catch (IOException e) {
            // compression failed, but this is non-essential
            logger.warn("gzipping of external set predictions failed", e);
            try {
                Files.deleteIfExists(gzippedExternalPredictions);
            } catch (IOException e1) {
                // give up
                logger.warn("Failed to delete residual .gz", e1);
            }
        }
    }

    public static String getExceptionMessage(String baseMessage, Path logFilePath) {
        List<String> lines = Lists.newArrayList();
        lines.add(0, baseMessage);
        try (BufferedReader br = Files.newBufferedReader(logFilePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // pass
        }
        return Joiner.on("\n").join(lines);
    }

    public static double getProgress(Path predictorDir) {
        Path[] dirs = new Path[]{predictorDir, predictorDir.resolve(Y_RANDOM_DIRECTORY)};
        int dirsCompleted = 0;
        for (Path dir : dirs) {
            if (Files.exists(dir.resolve(EXTERNAL_SET_PREDICTION_OUTPUT))) {
                dirsCompleted++;
            }
        }
        return (double) dirsCompleted / dirs.length;
    }
}
