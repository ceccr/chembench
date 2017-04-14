package edu.unc.ceccr.chembench.workflows.descriptors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class WriteDescriptors {
    private static final Logger logger = LoggerFactory.getLogger(WriteDescriptors.class);
    // using a set of Descriptors objects, create output files for kNN or SVM
    // containing the descriptors for a dataset.
    // Performs operations on data matrices as well (e.g. range-scaling).
    // All such operations are in-place -- the arguments will have their
    // values modified.

    private static void findMinMaxAvgStdDev(List<Descriptors> descriptorMatrix, List<Double> descriptorValueMinima,
                                            List<Double> descriptorValueMaxima, List<Double> descriptorValueAvgs,
                                            List<Double> descriptorValueStdDevs) {
        String logString = "findMinMaxAvgStdDev: descriptorMatrix ";
        if (descriptorMatrix == null) {
            logString += "is null";
            logger.warn(logString);
        } else if (descriptorMatrix.size() == 0) {
            logString += "has no elements";
            logger.warn(logString);
        } else {
            logString += String.format("has %d elements", descriptorMatrix.size());
            logger.info(logString);
        }

        // calculates the descriptorValueMinima and descriptorValueMaxima
        // arrays based on descriptorMatrix
        // used in scaling and when finding zero-variance descriptors.

        // Initialize the min and max values to equal the first compound's descriptors
        List<Double> firstCompoundDescriptorValues = descriptorMatrix.get(0).getDescriptorValues();
        for (Double value : firstCompoundDescriptorValues) {
            descriptorValueMinima.add(value);
            descriptorValueMaxima.add(value);
        }

        // initialize the avgs and stddevs to 0
        for (int i = 0; i < firstCompoundDescriptorValues.size(); i++) {
            descriptorValueAvgs.add(0d);
            descriptorValueStdDevs.add(0d);
        }

        // Get the minimum and maximum value for each column.
        // Get column totals for calculating the averages.
        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<Double> descriptorValues = new ArrayList<>();
            descriptorValues.addAll(descriptorMatrix.get(i).getDescriptorValues());

            for (int j = 0; j < descriptorValues.size(); j++) {
                if (descriptorValues.get(j) < descriptorValueMinima.get(j)) {
                    descriptorValueMinima.set(j, descriptorValues.get(j));
                }
                if (descriptorValues.get(j) > descriptorValueMaxima.get(j)) {
                    descriptorValueMaxima.set(j, descriptorValues.get(j));
                }
                Double totalSoFar = descriptorValueAvgs.get(j);

                descriptorValueAvgs.set(j, descriptorValues.get(j) + totalSoFar);
            }
        }

        // divide to get averages
        for (int j = 0; j < descriptorValueAvgs.size(); j++) {
            descriptorValueAvgs.set(j, descriptorValueAvgs.get(j) / descriptorMatrix.size());
        }

        // now go through again to get stddev... what a pain
        // wish there was a faster way
        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<Double> descriptorValues = new ArrayList<>();
            descriptorValues.addAll(descriptorMatrix.get(i).getDescriptorValues());

            for (int j = 0; j < descriptorValues.size(); j++) {
                Double mean = descriptorValueAvgs.get(j);
                Double distFromMeanSquared = Math.pow(descriptorValues.get(j) - mean, 2);
                descriptorValueStdDevs.set(j, descriptorValueStdDevs.get(j) + distFromMeanSquared);
            }
        }
        // divide sum then take sqrt to get stddevs
        for (int j = 0; j < descriptorValueStdDevs.size(); j++) {
            double squareDistTotal = descriptorValueStdDevs.get(j);
            descriptorValueStdDevs.set(j, Math.sqrt(squareDistTotal / descriptorMatrix.size()));
        }

    }

    private static void rangeScaleGivenMinMax(List<Descriptors> descriptorMatrix, List<Double> descriptorValueMinima,
                                              List<Double> descriptorValueMaxima) {
        // range-scales the values in the descriptor matrix.
        // We know the min and max. Scaled value = ((value - min) /
        // (max-min)).

        logger.debug("range-scaling descriptor matrix according " + "to given max and min");

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<Double> descriptorValues = descriptorMatrix.get(i).getDescriptorValues();
            for (int j = 0; j < descriptorValues.size(); j++) {
                double value = descriptorValues.get(j);
                double min = descriptorValueMinima.get(j);
                double max = descriptorValueMaxima.get(j);
                if (max - min != 0) {
                    descriptorValues.set(j, (value - min) / (max - min));
                }
                // if max - min == 0, the descriptor is zero-variance and will
                // be removed later.
            }

            // we need to make the descriptors arraylist into a space
            // separated string
            // ArrayList.toString() gives values separated by ", "
            // so just remove the commas and we're done
            Descriptors di = descriptorMatrix.get(i);
            di.setDescriptorValues(descriptorValues);
            descriptorMatrix.set(i, di);
        }
    }

    private static void autoScaleGivenAvgStdDev(List<Descriptors> descriptorMatrix, List<Double> descriptorValueAvgs,
                                                List<Double> descriptorValueStdDevsPlusAvgs) {
        // subtract the avg from each value
        // then divide by the stddev

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<Double> descriptorValues = descriptorMatrix.get(i).getDescriptorValues();
            for (int j = 0; j < descriptorValues.size(); j++) {
                double avg = descriptorValueAvgs.get(j);
                double stdDevPlusAvg = descriptorValueStdDevsPlusAvgs.get(j);
                double val = descriptorValues.get(j);
                if ((stdDevPlusAvg - avg) != 0) {
                    descriptorValues.set(j, (val - avg) / (stdDevPlusAvg - avg));
                }
            }

            // we need to make the descriptors arraylist into a space
            // separated string
            // ArrayList.toString() gives values separated by ", "
            // so just remove the commas and we're done
            Descriptors di = descriptorMatrix.get(i);
            di.setDescriptorValues(descriptorValues);
            descriptorMatrix.set(i, di);
        }
    }

    private static double findCorrelation(List<Double> d1, List<Double> d2) {
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = d1.get(0);
        double mean_y = d2.get(0);
        for (int i = 2; i < d1.size() + 1; i++) {
            double sweep = (i - 1) / i;
            double delta_x = d1.get(i - 1) - mean_x;
            double delta_y = d2.get(i - 1) - mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        double pop_sd_x = Math.sqrt(sum_sq_x / d1.size());
        double pop_sd_y = Math.sqrt(sum_sq_y / d1.size());
        double cov_x_y = sum_coproduct / d1.size();
        result = cov_x_y / (pop_sd_x * pop_sd_y);
        return result;
    }

    private static void removeHighlyCorellatedDescriptors(List<Descriptors> descriptorMatrix,
                                                          List<Double> descriptorValueMinima,
                                                          List<Double> descriptorValueMaxima,
                                                          List<Double> descriptorValueAvgs,
                                                          List<Double> descriptorValueStdDevs,
                                                          List<String> descriptorNames, String correlationCutoff) {

        List<List<Double>> descriptorValues = new ArrayList<>();
        for (Descriptors d : descriptorMatrix) {
            descriptorValues.add(d.getDescriptorValues());
        }
        // first thing: we need to transpose the descriptor matrix.
        // By default, it's organized by compound - we need it organized by descriptor.
        List<List<Double>> transpose = transpose(descriptorValues);

        List<Integer> removedDescriptorIndexes = new ArrayList<>();
        boolean done = false;
        while (!done) {
            // find the one descriptor with the most high correlations to
            // others and remove it.

            int[] counts = new int[transpose.size()];
            for (int i = 0; i < counts.length; i++) {
                counts[i] = 0;
            }

            for (int i = 0; i < transpose.size(); i++) {
                for (int j = i + 1; j < transpose.size(); j++) {
                    double correlation = findCorrelation(transpose.get(i), transpose.get(j));
                    if (correlation > Double.parseDouble(correlationCutoff)) {
                        counts[j]++;
                        counts[i]++;
                    }
                }
            }

            int max_count = 0;
            int max_index = -1;
            for (int i = 0; i < counts.length; i++) {
                if (counts[i] > max_count) {
                    max_index = i;
                    max_count = counts[i];
                }
            }
            if (max_index == -1) {
                done = true;
            } else {
                // remove descriptor with largest number of correlations
                removedDescriptorIndexes.add(max_index);
                transpose.remove(max_index);

                if (descriptorValueMinima != null) {
                    descriptorValueMinima.remove(max_index);
                }
                if (descriptorValueMaxima != null) {
                    descriptorValueMaxima.remove(max_index);
                }
                if (descriptorNames != null) {
                    descriptorNames.remove(max_index);
                }
                if (descriptorValueAvgs != null) {
                    descriptorValueAvgs.remove(max_index);
                }
                if (descriptorValueStdDevs != null) {
                    descriptorValueStdDevs.remove(max_index);
                }
            }
        }
        Collections.sort(removedDescriptorIndexes);

        // now, transpose the descriptor matrix back
        List<List<Double>> transposedBack = transpose(transpose);
        // and put it back into the original descriptor matrix
        for (int i = 0; i < descriptorMatrix.size(); i++) {
            descriptorMatrix.get(i).setDescriptorValues(transposedBack.get(i));
        }
    }

    private static List<List<Double>> transpose(List<List<Double>> original) {
        List<List<Double>> transpose = new ArrayList<>();
        for (List<Double> column : original) {
            for (int r = 0; r < column.size(); r++) {
                List<Double> row = transpose.get(r);
                if (row == null) {
                    row = new ArrayList<>();
                }
                row.add(column.get(r));
            }
        }
        return transpose;
    }

    private static void removeLowStdDevDescriptors(List<Descriptors> descriptorMatrix,
                                                   List<Double> descriptorValueMinima,
                                                   List<Double> descriptorValueMaxima, List<Double> descriptorValueAvgs,
                                                   List<Double> descriptorValueStdDevs, List<String> descriptorNames,
                                                   String stdDevCutoff) {

        // lol write this later
        // should be easy, just like removeZeroVariance but with a stddev
        // cutoff instead
        // eventually merge into removeZeroVariance cause they do pretty much
        // the same thing
    }

    private static void removeZeroVarianceDescriptors(List<Descriptors> descriptorMatrix,
                                                      List<Double> descriptorValueMinima,
                                                      List<Double> descriptorValueMaxima,
                                                      List<Double> descriptorValueAvgs,
                                                      List<Double> descriptorValueStdDevs,
                                                      List<String> descriptorNames) {

        // removes descriptors where the min and max are equal
        // used only during modeling
        logger.debug("removing zero-variance descriptors " + "from descriptor matrix");

        List<Integer> zeroVariance = new ArrayList<>();
        for (int i = 0; i < descriptorValueMinima.size(); i++) {
            double min = descriptorValueMinima.get(i);
            double max = descriptorValueMaxima.get(i);
            if (max - min < 0.0001) {
                zeroVariance.add(1);
            } else {
                zeroVariance.add(0);
            }
        }

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<Double> descriptorValues = descriptorMatrix.get(i).getDescriptorValues();

            for (int j = zeroVariance.size() - 1; j >= 0; j--) {
                if (zeroVariance.get(j) == 1) {
                    descriptorValues.remove(j);
                }
            }
            Descriptors di = descriptorMatrix.get(i);
            di.setDescriptorValues(descriptorValues);
            descriptorMatrix.set(i, di);
        }

        for (int j = zeroVariance.size() - 1; j >= 0; j--) {
            if (zeroVariance.get(j) == 1) {
                descriptorValueMinima.remove(j);
                descriptorValueMaxima.remove(j);
                descriptorValueAvgs.remove(j);
                descriptorValueStdDevs.remove(j);
                if (descriptorNames != null) {
                    descriptorNames.remove(j);
                }
            }
        }
    }

    private static void removeDescriptorsNotInPredictor(List<Descriptors> descriptorMatrix,
                                                        StringBuffer descriptorNameStringBuffer,
                                                        String predictorDescriptorNameString) {
        logger.debug("removing descriptors from dataset not contained in predictor");
        // determine intersection of the two sets of descriptor names:
        // one set is the dataset being trimmed, the other is the predictor used for comparison
        String[] datasetDescriptorNames = descriptorNameStringBuffer.toString().split("\\s+");
        Set<String> datasetDescriptors = ImmutableSet.copyOf(datasetDescriptorNames);
        Set<String> predictorDescriptors = ImmutableSet.copyOf(predictorDescriptorNameString.split("\\s+"));
        SetView<String> commonDescriptors = Sets.intersection(datasetDescriptors, predictorDescriptors);
        logger.debug(String.format("%d descriptors in dataset, %d descriptors in predictor; size of intersection: %d",
                datasetDescriptors.size(), predictorDescriptors.size(), commonDescriptors.size()));

        // build a set of column indexes corresponding to the common descriptors
        Set<Integer> commonColumnIndexes = Sets.newHashSet();
        for (int i = 0; i < datasetDescriptorNames.length; i++) {
            if (commonDescriptors.contains(datasetDescriptorNames[i])) {
                commonColumnIndexes.add(i);
            }
        }

        // trim dataset's descriptor value strings so that only values corresponding to
        // descriptors in both datasets are kept
        for (Descriptors descriptors : descriptorMatrix) {
            List<Double> oldValues = descriptors.getDescriptorValues();
            List<Double> newValues = new ArrayList<>();
            for (int i = 0; i < oldValues.size(); i++) {
                if (commonColumnIndexes.contains(i)) {
                    newValues.add(oldValues.get(i));
                }
            }
            descriptors.setDescriptorValues(newValues);
        }

        // update the descriptor names to reflect only the intersection
        descriptorNameStringBuffer.setLength(0);
        descriptorNameStringBuffer.append(Utility.SPACE_JOINER.join(commonDescriptors));
        logger.debug("done trimming dataset descriptors");
    }

    private static void readPredictorXFile(StringBuffer predictorDescriptorNameString,
                                           List<Double> predictorDescriptorValueMinima,
                                           List<Double> predictorDescriptorValueMaxima,
                                           List<Double> predictorDescriptorValueAvgs,
                                           List<Double> predictorDescriptorValueStdDevs, String predictorScaleType,
                                           String predictorXFile) throws Exception {
        // get the descriptor names and min / max values of each descriptor
        // So, read in the name, min, and max of each descriptor from the
        // modeling .x file
        logger.debug("reading predictor .x file");
        logger.debug("predictorXFile " + predictorXFile);
        File file = new File(predictorXFile);
        FileReader xFile = new FileReader(file);
        BufferedReader br = new BufferedReader(xFile);

        String line = br.readLine();
        Scanner src = new Scanner(line);
        int xFileNumCompounds = Integer.parseInt(src.next());
        line = br.readLine();
        src.close();
        src = new Scanner(line);
        while (src.hasNext()) {
            predictorDescriptorNameString.append(src.next() + " ");
        }

        for (int i = 0; i < xFileNumCompounds; i++) {
            line = br.readLine(); // skip all the compounds, we don't care
            // about them
        }

        if (predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)) {
            // get min and max values
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueMinima.add(src.nextDouble());
            }
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueMaxima.add(src.nextDouble());
            }
        } else if (predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            // get avg and stdDev values
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueAvgs.add(src.nextDouble());
            }
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueStdDevs.add(src.nextDouble());
            }
        }
        src.close();
        br.close();
    }

    public static void writeModelingXFile(List<String> compoundNames, List<Descriptors> descriptorMatrix,
                                          List<String> descriptorNames, String xFilePath, String scalingType,
                                          String stdDevCutoff, String correlationCutoff) throws Exception {
        // Perform scaling on descriptorMatrix
        // remove zero-variance descriptors from descriptorMatrix
        // Write a new file at xFilePath containing descriptorMatrix and other
        // data needed for .x file
        // see Developer's Guide in documentation folder for .x file format
        // details.

        // find min/max values for each descriptor
        List<Double> descriptorValueMinima = new ArrayList<>();
        List<Double> descriptorValueMaxima = new ArrayList<>();
        List<Double> descriptorValueAvgs = new ArrayList<>();
        List<Double> descriptorValueStdDevs = new ArrayList<>();

        findMinMaxAvgStdDev(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima, descriptorValueAvgs,
                descriptorValueStdDevs);

        // To maximize compatibility, what we actually store is the
        // value of (StdDev + average) on the last line.
        // This makes sense (I swear!)

        // Consider the process of range scaling: To scale
        // prediction descriptors so that they're in the same range as the
        // predictor's descriptors,
        // for each value you do:
        // scaled_value = (descriptor_value - min) / (max - min), right?
        // Well, we anticipate that other software is going to be using our
        // modeling .x files.
        // Since range scaling is a long-held tradition in our lab, that
        // software might end up
        // doing the same scaling process as range scaling:
        // scaled_value = (descriptor_value - number_on_first_line) /
        // (number_on_second_line - number_on_first_line)
        // And we can make that work with the autoscaling as well!

        // To autoscale, we do:
        // scaled_value = descriptor_value - average) / (standard_deviation)
        // So if we make the second line (standard_deviation + average), then
        // we can still do
        // scaled_value = (descriptor_value - number_on_first_line) /
        // (number_on_second_line - number_on_first_line)
        // but now it's
        // scaled_value = descriptor_value - average) / ((standard_deviation +
        // average) - average)
        // and that happily gives us a scaled result.
        // The process for restoring descriptors to their unscaled state will
        // also be
        // identical in both rangescale and autoscale with this standard.
        // (Figuring out why this is true is left as an exercise to the
        // reader.)

        List<Double> descriptorValueStdDevPlusAvgs = new ArrayList<>();
        for (int i = 0; i < descriptorValueStdDevs.size(); i++) {
            Double stddev = descriptorValueStdDevs.get(i);
            Double avg = descriptorValueStdDevs.get(i);
            descriptorValueStdDevPlusAvgs.add(stddev + avg);
        }

        // do scaling on descriptorMatrix
        if (scalingType.equalsIgnoreCase(Constants.RANGESCALING)) {
            rangeScaleGivenMinMax(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima);
        } else if (scalingType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            autoScaleGivenAvgStdDev(descriptorMatrix, descriptorValueAvgs, descriptorValueStdDevPlusAvgs);
        } else if (scalingType.equalsIgnoreCase(Constants.NOSCALING)) {
            // don't do anything!
        }

        // remove descriptors that are useless to modeling (zero variance)
        removeZeroVarianceDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima,
                descriptorValueAvgs, descriptorValueStdDevPlusAvgs, descriptorNames);

        if (Float.parseFloat(stdDevCutoff) > 0) {
            removeLowStdDevDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima,
                    descriptorValueAvgs, descriptorValueStdDevPlusAvgs, descriptorNames, stdDevCutoff);
        }
        if (Float.parseFloat(correlationCutoff) < 1) {
            removeHighlyCorellatedDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima,
                    descriptorValueAvgs, descriptorValueStdDevPlusAvgs, descriptorNames, correlationCutoff);
        }

        // write output
        Joiner joiner = Utility.SPACE_JOINER;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(xFilePath), StandardCharsets.UTF_8)) {
            writer.write(joiner.join(descriptorMatrix.size(), descriptorNames.size()));
            writer.newLine();
            writer.write(joiner.join(descriptorNames));
            writer.newLine();

            for (int i = 0; i < descriptorMatrix.size(); i++) {
                // each line of the descriptors matrix
                List<Double> descriptorValues = descriptorMatrix.get(i).getDescriptorValues();
                if (descriptorValues.contains(Double.NaN) || descriptorValues.contains(Double.NEGATIVE_INFINITY) ||
                        descriptorValues.contains(Double.POSITIVE_INFINITY)) {
                    logger.warn("Compound " + compoundNames.get(i) + " has NaN/Inf descriptor value");
                }
                writer.write(joiner.join(i + 1, compoundNames.get(i), joiner.join(descriptorValues)));
                writer.newLine();
            }

            if (scalingType.equalsIgnoreCase(Constants.RANGESCALING)) {
                writer.write(joiner.join(descriptorValueMinima));
                writer.newLine();
                writer.write(joiner.join(descriptorValueMaxima));
                writer.newLine();
            } else if (scalingType.equalsIgnoreCase(Constants.AUTOSCALING)) {
                writer.write(joiner.join(descriptorValueAvgs));
                writer.newLine();
                writer.write(joiner.join(descriptorValueStdDevPlusAvgs));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write modeling X file", e);
        }
    }

    public static void addZeroToOutput(List<Descriptors> descriptorMatrix,
                                       String descriptorNameString,
                                       String predictorDescriptorNameString){
        logger.info("Adding 0's to the dataset descriptors");
        String[] datasetDescriptorNames = descriptorNameString.split("\\s+");
        String[] predictorDescriptorNames = predictorDescriptorNameString.split("\\s+");
        List<Descriptors> descriptorMatrixTemp = new ArrayList<>();

        //j keeps track of the dataset while i keeps track of the predictor
        int j = 0;
        for (Descriptors descriptors: descriptorMatrix) {
            for (int i = 0; i < predictorDescriptorNames.length; i++) {
                if (!datasetDescriptorNames[j].equals(predictorDescriptorNames[i])) {
                    descriptors.getDescriptorValues().add(j, 0.0);
                } else {
                    j++;
                }
            }
        }
    }

    public static void writePredictionXFile(List<String> compoundNames, List<Descriptors> descriptorMatrix,
                                            String descriptorNameString, String xFilePath, String predictorXFilePath,
                                            String predictorScaleType) throws Exception {

        // read in the xFile used to make the predictor
        StringBuffer predictorDescriptorNameStringBuffer = new StringBuffer("");
        List<Double> predictorDescriptorValueMinima = new ArrayList<>();
        List<Double> predictorDescriptorValueMaxima = new ArrayList<>();
        List<Double> predictorDescriptorValueAvgs = new ArrayList<>();
        List<Double> predictorDescriptorValueStdDevsPlusAvgs = new ArrayList<>();

        readPredictorXFile(predictorDescriptorNameStringBuffer, predictorDescriptorValueMinima,
                predictorDescriptorValueMaxima, predictorDescriptorValueAvgs, predictorDescriptorValueStdDevsPlusAvgs,
                predictorScaleType, predictorXFilePath);
        String predictorDescriptorNameString = predictorDescriptorNameStringBuffer.toString();

        // remove descriptors from prediction set that are not in the
        // predictor
        StringBuffer descriptorNameStringBuffer = new StringBuffer(descriptorNameString);
        removeDescriptorsNotInPredictor(descriptorMatrix, descriptorNameStringBuffer, predictorDescriptorNameString);
        descriptorNameString = descriptorNameStringBuffer.toString();

        // do range scaling on descriptorMatrix
        if (predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)) {
            rangeScaleGivenMinMax(descriptorMatrix, predictorDescriptorValueMinima, predictorDescriptorValueMaxima);
        } else if (predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            autoScaleGivenAvgStdDev(descriptorMatrix, predictorDescriptorValueAvgs,
                    predictorDescriptorValueStdDevsPlusAvgs);
        } else if (predictorScaleType.equalsIgnoreCase(Constants.NOSCALING)) {
            // don't do anything
        }

        int numberOfPredictorDescriptors = predictorDescriptorNameString.split("\\s+").length;
        int numberOfOutputDescripors = descriptorNameString.split("\\s+").length;
        if (numberOfPredictorDescriptors!= numberOfOutputDescripors) {
            logger.warn("WARNING: predictor had " + numberOfPredictorDescriptors
                    + " descriptors and output has " + numberOfOutputDescripors);
            //add 0's to the dataset descriptor to make them have the same number of descriptors
            if (numberOfPredictorDescriptors > numberOfOutputDescripors){
                addZeroToOutput(descriptorMatrix, descriptorNameString, predictorDescriptorNameString);
            }
        }

        // write output
        logger.debug("Writing X file to " + xFilePath);
        File file = new File(xFilePath);
        BufferedWriter xFileOut = new BufferedWriter(new FileWriter(file));

        xFileOut.write(descriptorMatrix.size() + " " + descriptorNameString.split("\\s+").length + "\n");
        xFileOut.write(descriptorNameString + "\n"); // descriptor names
        Joiner joiner = Utility.SPACE_JOINER;
        try {
            for (int i = 0; i < descriptorMatrix.size(); i++) {
                // each line of the descriptors matrix
                xFileOut.write(joiner.join((i + 1), compoundNames.get(i), joiner.join(descriptorMatrix.get(i)
                        .getDescriptorValues())));
                xFileOut.newLine();
            }
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Mismatch between size of descriptor " +
                            "matrix and list of compounds: " +
                            "descriptorMatrix.size() was %d, " +
                            "compoundNames.size() was %d", descriptorMatrix.size(), compoundNames.size()),
                    ex); // log the exception stacktrace
        }

        if (predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)) {
            xFileOut.write(joiner.join(predictorDescriptorValueMinima));
            xFileOut.newLine();
            xFileOut.write(joiner.join(predictorDescriptorValueMaxima));
            xFileOut.newLine();
        } else if (predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            xFileOut.write(joiner.join(predictorDescriptorValueAvgs));
            xFileOut.newLine();
            xFileOut.write(joiner.join(predictorDescriptorValueStdDevsPlusAvgs));
            xFileOut.newLine();
        }
        xFileOut.close();
    }

}
