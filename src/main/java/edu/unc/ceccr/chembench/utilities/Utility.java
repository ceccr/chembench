package edu.unc.ceccr.chembench.utilities;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.io.IOUtils.closeQuietly;

//The Utility class is for cross-cutting concerns (logging, authentication / user stuff).

public class Utility {

    public static final Function<Object, String> NAME_TRANSFORM = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            if (o instanceof Dataset) {
                return ((Dataset) o).getName();
            } else if (o instanceof Predictor) {
                return ((Predictor) o).getName();
            } else if (o instanceof Prediction) {
                return ((Prediction) o).getName();
            } else if (o instanceof Job) {
                return ((Job) o).getJobName();
            } else {
                throw new RuntimeException("Unrecognized object type: " + o);
            }
        }
    };
    public static final Joiner SPACE_JOINER = Joiner.on(' ');
    public static final Joiner TAB_JOINER = Joiner.on('\t');
    public static final Joiner COMMA_JOINER = Joiner.on(',');
    public static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings();
    private static final Logger logger = LoggerFactory.getLogger(Utility.class);
    private static final Function<String, Double> PARSE_DOUBLE_TRANSFORM = new Function<String, Double>() {
        @Override
        public Double apply(String s) {
            return Double.parseDouble(s);
        }
    };
    private static final Function<String, Long> PARSE_LONG_TRANSFORM = new Function<String, Long>() {
        @Override
        public Long apply(String s) {
            return Long.parseLong(s);
        }
    };
    private static Integer debug_counter = 0;

    /**
     * Converts a list of Strings to a list of Doubles.
     *
     * @param strings the Strings to convert into Doubles
     * @return the converted Doubles
     * @throws IllegalArgumentException if one of the Strings is not a valid Double
     */
    public static List<Double> stringListToDoubleList(List<String> strings) {
        // XXX must return a new list, or the returned list cannot be added to
        try {
            return Lists.newArrayList(Lists.transform(strings, PARSE_DOUBLE_TRANSFORM));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("String to double conversion failed", e);
        }
    }

    /**
     * Converts a list of Strings to a list of Longs.
     *
     * @param strings the Strings to convert into Longs
     * @return the converted Longs
     * @throws IllegalArgumentException if one of the Strings is not a valid Long
     */
    public static List<Long> stringListToLongList(List<String> strings) {
        try {
            return Lists.newArrayList(Lists.transform(strings, PARSE_LONG_TRANSFORM));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("String to long conversion failed");
        }
    }

    public static String encrypt(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(str.getBytes());
        byte[] encryptedStr = md.digest();
        //convert each byte to a readable ascii character for easy database access.
        //Values need to be inside the range [40..126].
        for (int i = 0; i < encryptedStr.length; i++) {
            //writeToDebug("before: " + (Math.abs(new Integer(encryptedStr[i]))) + " after: " + (Math.abs(new Integer
            // (encryptedStr[i]) % 87) + 40) + " afterchar: " + (char)(Math.abs(new Integer(encryptedStr[i]) % 87) +
            // 40)  );
            encryptedStr[i] = (byte) (Math.abs(new Integer(encryptedStr[i]) % 87) + 40);
        }
        return new String(encryptedStr);
    }

    public static String randomPassword() throws Exception {
        String source = Constants.SOURCE;
        String password = "";
        Random generator = new Random();
        char[] sourceStr = source.toCharArray();
        for (int i = 0; i < 8; i++) {
            password = password + sourceStr[generator.nextInt(62)];
        }
        return password;
    }

    public static String getDate() {
        Date dateNow = new Date();

        SimpleDateFormat df = new SimpleDateFormat("dd-HH:mm:ss");
        StringBuilder s = new StringBuilder(df.format(dateNow));

        return s.toString();
    }

    public static String doubleToString(Double num) {
        java.text.NumberFormat f = java.text.NumberFormat.getInstance();
        f.setGroupingUsed(false);
        return f.format(num);
    }

    public static String floatToString(Float num) {
        java.text.NumberFormat f = java.text.NumberFormat.getInstance();
        f.setGroupingUsed(false);
        return f.format(num);
    }

    public static Long checkExpiration(int year, int month, int day) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(year, month - 1, day);
        Calendar cal2 = Calendar.getInstance();
        Date end = cal1.getTime();
        Date today = cal2.getTime();

        return (end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
    }

    public static int getSignificantFigures(String number, boolean removeTrailingZeros) {

        int sigfigs = 0;

        //Remove negative sign.
        if (number.charAt(0) == '-') {
            number = number.substring(1);
        }

        //remove any leading zeros from the number
        while (number.charAt(0) == '0') {
            number = number.substring(1);
        }
        //remove any trailing zeros from the number
        if (removeTrailingZeros) {
            while (number.charAt(number.length() - 1) == '0') {
                number = number.substring(0, number.length() - 1);
            }
        }
        //find decimal place in number
        int decPointPos = number.lastIndexOf(".");
        System.out.println("dec point found at: " + decPointPos);

        for (int i = 0; i < decPointPos; i++) {
            number = number.substring(1);
            sigfigs++;
        }
        if (number.charAt(0) == '.') {
            number = number.substring(1);
        }
        while (number.length() > 0) {
            number = number.substring(1);
            sigfigs++;
        }
        return sigfigs;
    }

    public static String stringListToString(List<String> stringArrayList) {
        return SPACE_JOINER.join(stringArrayList);
    }

    public static String roundSignificantFigures(double number, int numFigs) {
        return roundSignificantFigures("" + number, numFigs);
    }

    public static String roundSignificantFigures(String number, int numFigs) {
        //outputs a numerical string
        //e.g., 12345 to 2 significant figures is 12000, not 1.2*10^4
        //although the latter is more correct, the former is more intuitive.
        boolean debug = false;

        if (debug) {
            logger.debug("Rounding " + number + " to " + numFigs + " significant figures.");
        }

        if (number.length() < numFigs) {
            return number;
        }

        //check if number is negative. Remove and remember.
        boolean isNegative = false;
        if (number.charAt(0) == '-') {
            isNegative = true;
            number = number.substring(1);
        }
        //remove any leading zeros from the number
        while (number.charAt(0) == '0' && number.length() > 0) {
            number = number.substring(1);
        }

        if (debug) {
            System.out.println("number is " + number);
        }

        int order = (int) Math.floor(Math.log10(Double.parseDouble(number)));

        if (debug) {
            System.out.println("Number is order " + order);
        }
        //find decimal place in number
        int decPointPos = number.lastIndexOf(".");
        //we want to remove the decimal point, to make things easier
        if (decPointPos == 0) {
            number = number.replaceFirst("\\.", "0");
        } else {
            number = number.replaceFirst("\\.", "");
        }

        if (numFigs > number.length()) {
            numFigs = number.length();
        }

        //next we want to round off the insignificant digits
        String significant = number.substring(0, numFigs);
        String insignificant = number.substring(numFigs);
        String forRounding = significant + "." + insignificant;
        int roundedSignificant = (int) Math.round(Double.parseDouble(forRounding));

        if (debug) {
            System.out.println("chopped number down to " + roundedSignificant);
        }
        String roundedSignificantStr = "" + roundedSignificant;
        String outputStr = "";
        //restore number to its original order
        int currentOrder = (int) Math.floor(Math.log10(roundedSignificant));
        if (debug) {
            System.out.println("order was " + order + " and is now " + currentOrder);
        }

        if (currentOrder > order) {
            //we need to make this a decimal.
            //number was sth like 1.20 and now it's 12
            if (order >= 0) {
                for (int i = 0; i <= order; i++) {
                    outputStr += roundedSignificantStr.charAt(0);
                    roundedSignificantStr = roundedSignificantStr.substring(1);
                }
                outputStr += ".";
                while (!roundedSignificantStr.equals("")) {
                    outputStr += roundedSignificantStr.charAt(0);
                    roundedSignificantStr = roundedSignificantStr.substring(1);
                }
            } else {
                outputStr = "0.";
                for (int i = 0; i < (Math.abs(order) - 1); i++) {
                    outputStr += "0";
                }
                while (!roundedSignificantStr.equals("")) {
                    outputStr += roundedSignificantStr.charAt(0);
                    roundedSignificantStr = roundedSignificantStr.substring(1);
                }
            }
        } else {
            //number was sth like 123456 and now it's 12
            for (int i = 0; i < roundedSignificantStr.length(); i++) {
                outputStr += roundedSignificantStr.charAt(i);
            }
            while (outputStr.length() <= order) {
                outputStr += "0";
            }
        }

        if (debug) {
            System.out.println("restored number to " + outputStr);
        }

        if (isNegative) {
            outputStr = "-" + outputStr;
        }

        return outputStr;

    }

    public static int naturalSortCompare(Object o1, Object o2) {
        String firstString = o1.toString();
        String secondString = o2.toString();

        if (secondString == null || firstString == null) {
            return 0;
        }

        int lengthFirstStr = firstString.length();
        int lengthSecondStr = secondString.length();

        int index1 = 0;
        int index2 = 0;

        while (index1 < lengthFirstStr && index2 < lengthSecondStr) {
            char ch1 = firstString.charAt(index1);
            char ch2 = secondString.charAt(index2);

            char[] space1 = new char[lengthFirstStr];
            char[] space2 = new char[lengthSecondStr];

            int loc1 = 0;
            int loc2 = 0;

            do {
                space1[loc1++] = ch1;
                index1++;

                if (index1 < lengthFirstStr) {
                    ch1 = firstString.charAt(index1);
                } else {
                    break;
                }
            } while (Character.isDigit(ch1) == Character.isDigit(space1[0]));

            do {
                space2[loc2++] = ch2;
                index2++;

                if (index2 < lengthSecondStr) {
                    ch2 = secondString.charAt(index2);
                } else {
                    break;
                }
            } while (Character.isDigit(ch2) == Character.isDigit(space2[0]));

            String str1 = new String(space1);
            String str2 = new String(space2);

            int result;

            if (Character.isDigit(space1[0]) && Character.isDigit(space2[0])) {
                Integer firstNumberToCompare = new Integer(Integer.parseInt(str1.trim()));
                Integer secondNumberToCompare = new Integer(Integer.parseInt(str2.trim()));
                result = firstNumberToCompare.compareTo(secondNumberToCompare);
            } else {
                result = str1.compareTo(str2);
            }

            if (result != 0) {
                return result;
            }
        }
        return lengthFirstStr - lengthSecondStr;
    }

    public static String truncateString(String s, int numChars) {
        if (s != null && s.length() > numChars) {
            s = s.substring(0, numChars);
        }
        return s;
    }

    public static boolean stringContainsInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getDuration(Date start, Date end) {
        long time = end.getTime() - start.getTime(); //haha, a "long time"
        float seconds = new Float(time) / 1000;
        int minutes = Math.round(seconds) / 60;
        int hours = minutes / 60;
        return "" + hours + "h " + minutes + "m " + seconds + "s";
    }

    public static String formatDate(Date d) {
        //do this when you have internets dammit

        return "";
    }

    public static void writeToLSFLog(String message) {
        try {
            // Create file
            FileWriter fstream = new FileWriter(Constants.CECCR_USER_BASE_PATH + "LSF.log", true);
            String s;
            final Writer result = new StringWriter();
            s = result.toString();
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(s + " [" + getDate() + "]");
            // Close the output stream
            out.close();
        } catch (Exception e) {// Catch exception if any
        }

    }

    public static String booleanToString(boolean bool) {
        return bool ? Constants.YES : Constants.NO;
    }

    public static boolean stringToBoolean(String str) {
        if (str == null || !(str.equals(Constants.YES) || str.equals(Constants.NO))) {
            throw new IllegalArgumentException("Invalid string representation of boolean: " + str);
        }
        return str.equals(Constants.YES);
    }

    public static float safeStringToFloat(String standDev) {
        if (standDev != null) {
            try {
                return Float.parseFloat(standDev);
            } catch (NumberFormatException e) {
                ; // expected exception
            }
        }
        return 0.0f;
    }

    public static <T extends Serializable> T clone(T entity) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(entity);
            oos.flush();

            byte[] bytes = baos.toByteArray();
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            Object result = ois.readObject();
            return (T) result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Object can't be copied", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(
                    "Unable to reconstruct serialized object due to invalid class definition", e);
        } finally {
            closeQuietly(oos);
            closeQuietly(baos);
            closeQuietly(bis);
            closeQuietly(ois);
        }
    }

    public static void hybrid(int descriptorSetListSize, String descriptorType, List<String> descriptorNames,
                              List<Descriptors> descriptorValueMatrix, List<String> descriptorNamesCombined,
                              List<Descriptors> descriptorValueMatrixCombined) {
        if (descriptorSetListSize > 1) {
            for (String name : descriptorNames) {
                descriptorNamesCombined.add(descriptorType + "_" + name);
            }
        } else {
            descriptorNamesCombined.addAll(descriptorNames);
        }
        combine(descriptorValueMatrix, descriptorValueMatrixCombined);
    }

    public static void combine(List<Descriptors> descriptorValueMatrix,
                               List<Descriptors> descriptorValueMatrixCombined) {
        logger.info("Combining descriptors of size " + descriptorValueMatrix.size() + " and "
                + descriptorValueMatrixCombined.size());
        if (!descriptorValueMatrixCombined.isEmpty()) {
            int j = 0;
            int k = 0;
            int size = (descriptorValueMatrixCombined.size() < descriptorValueMatrix.size()) ?
                    descriptorValueMatrixCombined.size() : descriptorValueMatrix.size();
            List<Descriptors> descriptorValueMatrixCombinedTemp = new ArrayList<>();
            descriptorValueMatrixCombinedTemp.addAll(descriptorValueMatrixCombined);
            descriptorValueMatrixCombined.clear();
            for (int i = 0; i < size; i++) {
                List<Double> descriptorValues = new ArrayList<>();
                Descriptors descriptors = new Descriptors();
                //sets the descriptors in the combined matrix based on whichever matrix is smaller
                if (descriptorValueMatrixCombinedTemp.size() < descriptorValueMatrix.size()) {
                    k = i;
                } else {
                    j = i;
                }
                if (descriptorValueMatrix.get(j).getCompoundName()
                        .equals(descriptorValueMatrixCombinedTemp.get(k).getCompoundName())) {
                    descriptorValues.addAll(descriptorValueMatrixCombinedTemp.get(k).getDescriptorValues());
                    descriptorValues.addAll(descriptorValueMatrix.get(j).getDescriptorValues());
                    descriptors.setCompoundName(descriptorValueMatrix.get(j).getCompoundName());
                    descriptors.setDescriptorValues(descriptorValues);
                    descriptorValueMatrixCombined.add(i, descriptors);
                } else {
                    if (descriptorValueMatrixCombinedTemp.size() < descriptorValueMatrix.size()) {
                        j++;
                    } else {
                        k++;
                    }
                    i--;
                }
            }
        } else {
            descriptorValueMatrixCombined.addAll(descriptorValueMatrix);
        }
    }
}
