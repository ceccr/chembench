package edu.unc.ceccr.chembench.workflows.descriptors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;


public class WriteDescriptors {
    private static final Logger logger = Logger.getLogger(WriteDescriptors.class.getName());
    // using a set of Descriptors objects, create output files for kNN or SVM
    // containing the descriptors for a dataset.
    // Performs operations on data matrices as well (e.g. range-scaling).
    // All such operations are in-place -- the arguments will have their
    // values modified.

    public static void findMinMaxAvgStdDev(List<Descriptors> descriptorMatrix, List<String> descriptorValueMinima,
                                           List<String> descriptorValueMaxima, List<String> descriptorValueAvgs,
                                           List<String> descriptorValueStdDevs) {
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

        // Initialize the min and max values to equal the first compound's
        // descriptors
        descriptorValueMinima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));
        descriptorValueMaxima.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));

        // initialize the avgs and stddevs to 0
        descriptorValueAvgs.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));
        descriptorValueStdDevs.addAll(Arrays.asList(descriptorMatrix.get(0).getDescriptorValues().split("\\s+")));

        for (int j = 0; j < descriptorValueAvgs.size(); j++) {
            descriptorValueAvgs.set(j, "0");
            descriptorValueStdDevs.set(j, "0");
        }

        // Get the minimum and maximum value for each column.
        // Get column totals for calculating the averages.
        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<String> descriptorValues = Lists.newArrayList();
            descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));

            for (int j = 0; j < descriptorValues.size(); j++) {
                if (Float.parseFloat(descriptorValues.get(j)) < Float.parseFloat(descriptorValueMinima.get(j))) {
                    descriptorValueMinima.set(j, descriptorValues.get(j));
                }
                if (Float.parseFloat(descriptorValues.get(j)) > Float.parseFloat(descriptorValueMaxima.get(j))) {
                    descriptorValueMaxima.set(j, descriptorValues.get(j));
                }
                Float totalSoFar = Float.parseFloat(descriptorValueAvgs.get(j));

                descriptorValueAvgs
                        .set(j, Utility.floatToString(Float.parseFloat(descriptorValues.get(j)) + totalSoFar));
            }
            descriptorValues.clear(); // cleanup
        }

        // divide to get averages
        for (int j = 0; j < descriptorValueAvgs.size(); j++) {
            descriptorValueAvgs.set(j,
                    Utility.floatToString(Float.parseFloat(descriptorValueAvgs.get(j)) / descriptorMatrix.size()));
        }

        // now go through again to get stddev... what a pain
        // wish there was a faster way
        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<String> descriptorValues = Lists.newArrayList();
            descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));

            for (int j = 0; j < descriptorValues.size(); j++) {
                Float mean = Float.parseFloat(descriptorValueAvgs.get(j));
                Float distFromMeanSquared =
                        new Float(Math.pow((Double.parseDouble(descriptorValues.get(j)) - mean), 2));
                descriptorValueStdDevs.set(j,
                        Utility.floatToString(Float.parseFloat(descriptorValueStdDevs.get(j)) + distFromMeanSquared));
            }
            descriptorValues.clear(); // cleanup
        }
        // divide sum then take sqrt to get stddevs
        for (int j = 0; j < descriptorValueStdDevs.size(); j++) {
            double squareDistTotal = Double.parseDouble(descriptorValueStdDevs.get(j));
            descriptorValueStdDevs.set(j, Utility.doubleToString(Math.sqrt(squareDistTotal / descriptorMatrix.size())));
        }

    }

    public static void rangeScaleGivenMinMax(List<Descriptors> descriptorMatrix, List<String> descriptorValueMinima,
                                             List<String> descriptorValueMaxima) {
        // range-scales the values in the descriptor matrix.
        // We know the min and max. Scaled value = ((value - min) /
        // (max-min)).

        logger.debug("range-scaling descriptor matrix according " + "to given max and min");

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<String> descriptorValues = Lists.newArrayList();
            descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));
            for (int j = 0; j < descriptorValues.size(); j++) {
                float value = Float.parseFloat(descriptorValues.get(j));
                float min = Float.parseFloat(descriptorValueMinima.get(j));
                float max = Float.parseFloat(descriptorValueMaxima.get(j));
                if (max - min != 0) {
                    descriptorValues.set(j, Utility.floatToString((value - min) / (max - min)));
                }
                // if max - min == 0, the descriptor is zero-variance and will
                // be removed later.
            }

            // we need to make the descriptors arraylist into a space
            // separated string
            // ArrayList.toString() gives values separated by ", "
            // so just remove the commas and we're done
            Descriptors di = descriptorMatrix.get(i);
            di.setDescriptorValues(Utility.StringListToString(descriptorValues));
            descriptorMatrix.set(i, di);
            descriptorValues.clear(); // cleanup
        }
    }

    public static void autoScaleGivenAvgStdDev(List<Descriptors> descriptorMatrix, List<String> descriptorValueAvgs,
                                               List<String> descriptorValueStdDevsPlusAvgs) {
        // subtract the avg from each value
        // then divide by the stddev

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<String> descriptorValues = Lists.newArrayList();
            descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));

            for (int j = 0; j < descriptorValues.size(); j++) {
                Float avg = Float.parseFloat(descriptorValueAvgs.get(j));
                Float stdDevPlusAvg = Float.parseFloat(descriptorValueStdDevsPlusAvgs.get(j));
                Float val = Float.parseFloat(descriptorValues.get(j));
                if ((stdDevPlusAvg - avg) != 0) {
                    descriptorValues.set(j, Utility.floatToString((val - avg) / (stdDevPlusAvg - avg)));
                }
            }

            // we need to make the descriptors arraylist into a space
            // separated string
            // ArrayList.toString() gives values separated by ", "
            // so just remove the commas and we're done
            Descriptors di = descriptorMatrix.get(i);
            di.setDescriptorValues(Utility.StringListToString(descriptorValues));
            descriptorMatrix.set(i, di);
            descriptorValues.clear(); // cleanup
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
            double sweep = Double.valueOf(i - 1) / i;
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

    public static void removeHighlyCorellatedDescriptors(List<Descriptors> descriptorMatrix,
                                                         List<String> descriptorValueMinima,
                                                         List<String> descriptorValueMaxima,
                                                         List<String> descriptorValueAvgs,
                                                         List<String> descriptorValueStdDevs,
                                                         List<String> descriptorNames, Float correlationCutoff) {

        // first thing: we need to transpose the descriptor matrix.
        // By default, it's organized by compound - we need it organized by
        // descriptor.
        List<List<Double>> descriptorMatrixT = Lists.newArrayList();

        // populate the first values of each row in descriptorMatrix
        String[] sa = descriptorMatrix.get(0).getDescriptorValues().split("\\s+");
        for (int i = 0; i < sa.length; i++) {
            List<Double> doubleArray = Lists.newArrayList();
            doubleArray.add(Double.parseDouble(sa[i]));
            descriptorMatrixT.add(doubleArray);
        }

        // now go through the rest of the descriptorMatrix and add in each
        // value
        for (int i = 1; i < descriptorMatrix.size(); i++) {
            sa = descriptorMatrix.get(i).getDescriptorValues().split("\\s+");
            for (int j = 0; j < sa.length; j++) {
                descriptorMatrixT.get(j).add(Double.parseDouble(sa[j]));
            }
        }

        List<Integer> removedDescriptorIndexes = Lists.newArrayList();
        boolean done = false;
        while (!done) {
            // find the one descriptor with the most high correlations to
            // others and remove it.

            int[] counts = new int[descriptorMatrixT.size()];
            for (int i = 0; i < counts.length; i++) {
                counts[i] = 0;
            }

            for (int i = 0; i < descriptorMatrixT.size(); i++) {
                for (int j = i + 1; j < descriptorMatrixT.size(); j++) {
                    double correlation = findCorrelation(descriptorMatrixT.get(i), descriptorMatrixT.get(j));
                    if (correlation > correlationCutoff) {
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
                descriptorMatrixT.remove(max_index);

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
        List<String> descriptorMatrixTT = Lists.newArrayList();

        for (int i = 0; i < descriptorMatrixT.get(0).size(); i++) {
            String as = Utility.doubleToString(descriptorMatrixT.get(0).get(i));
            descriptorMatrixTT.add(as);
        }

        for (int i = 1; i < descriptorMatrixT.size(); i++) {
            for (int j = 0; j < descriptorMatrixT.get(i).size(); j++) {
                descriptorMatrixTT.set(j, (descriptorMatrixTT.get(j) + " " + descriptorMatrixT.get(i).get(j)));
            }
        }

        // and put it back into the original descriptor matrix
        for (int i = 0; i < descriptorMatrix.size(); i++) {
            descriptorMatrix.get(i).setDescriptorValues(descriptorMatrixTT.get(i));
        }

    }

    public static void removeLowStdDevDescriptors(List<Descriptors> descriptorMatrix,
                                                  List<String> descriptorValueMinima,
                                                  List<String> descriptorValueMaxima, List<String> descriptorValueAvgs,
                                                  List<String> descriptorValueStdDevs, List<String> descriptorNames,
                                                  Float stdDevCutoff) {

        // lol write this later
        // should be easy, just like removeZeroVariance but with a stddev
        // cutoff instead
        // eventually merge into removeZeroVariance cause they do pretty much
        // the same thing
    }

    public static void removeZeroVarianceDescriptors(List<Descriptors> descriptorMatrix,
                                                     List<String> descriptorValueMinima,
                                                     List<String> descriptorValueMaxima,
                                                     List<String> descriptorValueAvgs,
                                                     List<String> descriptorValueStdDevs,
                                                     List<String> descriptorNames) {

        // removes descriptors where the min and max are equal
        // used only during modeling
        logger.debug("removing zero-variance descriptors " + "from descriptor matrix");

        List<Integer> zeroVariance = Lists.newArrayList();
        for (int i = 0; i < descriptorValueMinima.size(); i++) {
            float min = Float.parseFloat(descriptorValueMinima.get(i));
            float max = Float.parseFloat(descriptorValueMaxima.get(i));
            if (max - min < 0.0001) {
                zeroVariance.add(1);
            } else {
                zeroVariance.add(0);
            }
        }

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            List<String> descriptorValues = Lists.newArrayList();
            descriptorValues.addAll(Arrays.asList(descriptorMatrix.get(i).getDescriptorValues().split("\\s+")));

            for (int j = zeroVariance.size() - 1; j >= 0; j--) {
                if (zeroVariance.get(j) == 1) {
                    descriptorValues.remove(j);
                }
            }
            Descriptors di = descriptorMatrix.get(i);
            di.setDescriptorValues(Utility.StringListToString(descriptorValues));
            descriptorMatrix.set(i, di);
            descriptorValues.clear();
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

    public static void removeDescriptorsNotInPredictor(List<Descriptors> descriptorMatrix,
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
            String[] oldValues = descriptors.getDescriptorValues().split("\\s+");
            List<String> newValues = Lists.newArrayList();
            for (int i = 0; i < oldValues.length; i++) {
                if (commonColumnIndexes.contains(i)) {
                    newValues.add(oldValues[i]);
                }
            }
            descriptors.setDescriptorValues(Joiner.on(" ").join(newValues));
        }

        // update the descriptor names to reflect only the intersection
        descriptorNameStringBuffer.setLength(0);
        descriptorNameStringBuffer.append(Joiner.on(" ").join(commonDescriptors));
        logger.debug("done trimming dataset descriptors");
    }

    public static void readPredictorXFile(StringBuffer predictorDescriptorNameString,
                                          List<String> predictorDescriptorValueMinima,
                                          List<String> predictorDescriptorValueMaxima,
                                          List<String> predictorDescriptorValueAvgs,
                                          List<String> predictorDescriptorValueStdDevs, String predictorScaleType,
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
                predictorDescriptorValueMinima.add(src.next());
            }
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueMaxima.add(src.next());
            }
        } else if (predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            // get avg and stdDev values
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueAvgs.add(src.next());
            }
            line = br.readLine();
            src.close();
            src = new Scanner(line);
            while (src.hasNext()) {
                predictorDescriptorValueStdDevs.add(src.next());
            }
        }
        src.close();
        br.close();
    }

    public static void writeModelingXFile(List<String> compoundNames, List<Descriptors> descriptorMatrix,
                                          String descriptorNameString, String xFilePath, String scalingType,
                                          String stdDevCutoff, String correlationCutoff) throws Exception {
        // Perform scaling on descriptorMatrix
        // remove zero-variance descriptors from descriptorMatrix
        // Write a new file at xFilePath containing descriptorMatrix and other
        // data needed for .x file
        // see Developer's Guide in documentation folder for .x file format
        // details.

        // find min/max values for each descriptor
        List<String> descriptorValueMinima = Lists.newArrayList();
        List<String> descriptorValueMaxima = Lists.newArrayList();
        List<String> descriptorValueAvgs = Lists.newArrayList();
        List<String> descriptorValueStdDevs = Lists.newArrayList();

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

        List<String> descriptorValueStdDevPlusAvgs = Lists.newArrayList();
        for (int i = 0; i < descriptorValueStdDevs.size(); i++) {
            Float stddev = Float.parseFloat(descriptorValueStdDevs.get(i));
            Float avg = Float.parseFloat(descriptorValueStdDevs.get(i));
            descriptorValueStdDevPlusAvgs.add(Utility.floatToString((stddev + avg)));
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
        List<String> descriptorNames = Lists.newArrayList();
        descriptorNames.addAll(Arrays.asList(descriptorNameString.split("\\s+")));

        removeZeroVarianceDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima,
                descriptorValueAvgs, descriptorValueStdDevPlusAvgs, descriptorNames);

        if (Float.parseFloat(stdDevCutoff) > 0) {
            removeLowStdDevDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima,
                    descriptorValueAvgs, descriptorValueStdDevPlusAvgs, descriptorNames,
                    Float.parseFloat(stdDevCutoff));
        }
        if (Float.parseFloat(correlationCutoff) < 1) {
            removeHighlyCorellatedDescriptors(descriptorMatrix, descriptorValueMinima, descriptorValueMaxima,
                    descriptorValueAvgs, descriptorValueStdDevPlusAvgs, descriptorNames,
                    Float.parseFloat(correlationCutoff));
        }

        // write output
        File file = new File(xFilePath);
        FileWriter xFileOut = new FileWriter(file);

        xFileOut.write(descriptorMatrix.size() + " " + descriptorNames.size() + "\n"); // numcompounds
        xFileOut.write(Utility.StringListToString(descriptorNames) + "\n");

        for (int i = 0; i < descriptorMatrix.size(); i++) {
            // each line of the descriptors matrix
            xFileOut.write(
                    (i + 1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues() + "\n");
        }

        if (scalingType.equalsIgnoreCase(Constants.RANGESCALING)) {
            xFileOut.write(Utility.StringListToString(descriptorValueMinima) + "\n");
            xFileOut.write(Utility.StringListToString(descriptorValueMaxima) + "\n");
        } else if (scalingType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            xFileOut.write(Utility.StringListToString(descriptorValueAvgs) + "\n");
            xFileOut.write(Utility.StringListToString(descriptorValueStdDevPlusAvgs) + "\n");
        }

        xFileOut.close();
    }

    public static void writePredictionXFile(List<String> compoundNames, List<Descriptors> descriptorMatrix,
                                            String descriptorNameString, String xFilePath, String predictorXFilePath,
                                            String predictorScaleType) throws Exception {

        // read in the xFile used to make the predictor
        StringBuffer predictorDescriptorNameStringBuffer = new StringBuffer("");
        List<String> predictorDescriptorValueMinima = Lists.newArrayList();
        List<String> predictorDescriptorValueMaxima = Lists.newArrayList();
        List<String> predictorDescriptorValueAvgs = Lists.newArrayList();
        List<String> predictorDescriptorValueStdDevsPlusAvgs = Lists.newArrayList();

        readPredictorXFile(predictorDescriptorNameStringBuffer, predictorDescriptorValueMinima,
                predictorDescriptorValueMaxima, predictorDescriptorValueAvgs, predictorDescriptorValueStdDevsPlusAvgs,
                predictorScaleType, predictorXFilePath);
        String predictorDescriptorNameString = predictorDescriptorNameStringBuffer.toString();

        // remove descriptors from prediction set that are not in the
        // predictor
        StringBuffer descriptorNameStringBuffer = new StringBuffer(descriptorNameString);
        removeDescriptorsNotInPredictor(descriptorMatrix, descriptorNameStringBuffer, predictorDescriptorNameString);
        descriptorNameString = descriptorNameStringBuffer.toString();

        if (predictorDescriptorNameString.split("\\s+").length != descriptorNameString.split("\\s+").length) {
            logger.warn("WARNING: predictor had " + predictorDescriptorNameString.split("\\s+").length
                    + " descriptors and output has " + descriptorNameString.split("\\s+").length);
        }

        // do range scaling on descriptorMatrix
        if (predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)) {
            rangeScaleGivenMinMax(descriptorMatrix, predictorDescriptorValueMinima, predictorDescriptorValueMaxima);
        } else if (predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            autoScaleGivenAvgStdDev(descriptorMatrix, predictorDescriptorValueAvgs,
                    predictorDescriptorValueStdDevsPlusAvgs);
        } else if (predictorScaleType.equalsIgnoreCase(Constants.NOSCALING)) {
            // don't do anything
        }

        // write output
        logger.debug("Writing X file to " + xFilePath);
        File file = new File(xFilePath);
        FileWriter xFileOut = new FileWriter(file);

        xFileOut.write(descriptorMatrix.size() + " " + descriptorNameString.split("\\s+").length + "\n");
        xFileOut.write(descriptorNameString + "\n"); // descriptor names

        try {
            for (int i = 0; i < descriptorMatrix.size(); i++) {
                // each line of the descriptors matrix
                xFileOut.write(
                        (i + 1) + " " + compoundNames.get(i) + " " + descriptorMatrix.get(i).getDescriptorValues()
                                + "\n");
            }
        } catch (IndexOutOfBoundsException ex) {
            logger.error(String.format("Mismatch between size of descriptor " +
                            "matrix and list of compounds: " +
                            "descriptorMatrix.size() was %d, " +
                            "compoundNames.size() was %d", descriptorMatrix.size(), compoundNames.size()),
                    ex); // log the exception stacktrace
        }

        if (predictorScaleType.equalsIgnoreCase(Constants.RANGESCALING)) {
            xFileOut.write(Utility.StringListToString(predictorDescriptorValueMinima) + "\n");
            xFileOut.write(Utility.StringListToString(predictorDescriptorValueMaxima) + "\n");
        } else if (predictorScaleType.equalsIgnoreCase(Constants.AUTOSCALING)) {
            xFileOut.write(Utility.StringListToString(predictorDescriptorValueAvgs) + "\n");
            xFileOut.write(Utility.StringListToString(predictorDescriptorValueStdDevsPlusAvgs) + "\n");
        }
        xFileOut.close();
    }

}
