package edu.unc.ceccr.workflows.calculations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;


public class DistanceMeasure {
    protected static String file_path;
    protected static Vector<Vector<String>> data;
    protected static Vector<Vector<Double>> bit_matrix;
    protected static String[] names;
    protected static Vector<Vector<Double>> distance_matrix;

    public DistanceMeasure() {
    }

    public DistanceMeasure(String userName, String datasetName, String sdfName) throws IOException {
        file_path = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + datasetName + "/Visualization/" +
                sdfName;
        // reading content of the .x file to Vector
    }

    public void readData() throws Exception {
        data = DatasetFileOperations.readFileToVector(" ", file_path + ".x");
        // removing two first rows (general file info and names)
        data.remove(0);
        data.remove(0);
        names = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            names[i] = data.get(i).get(1);
        }
        for (int i = 0; i < data.size(); i++) {
            data.get(i).remove(0);
            data.get(i).remove(0);
        }
        bit_matrix = convertStringVectorToDoubleVector(data);
    }

    private Vector<Vector<Double>> convertStringVectorToDoubleVector(Vector<Vector<String>> invector) {
        Vector<Vector<Double>> outvector = new Vector<Vector<Double>>();
        for (int i = 0; i < invector.size(); i++) {
            Vector<Double> temp = new Vector<Double>();
            for (int j = 0; j < invector.get(i).size(); j++) {
                temp.add(new Double(invector.get(i).get(j)));
            }
            outvector.add(temp);
        }
        return outvector;
    }

    public void writeMATFile(Vector<Vector<String>> res, String ext) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(file_path + "_" + ext + ".mat"));
        out.write("");
        for (int i = 0; i < names.length; i++) {
            out.write("\t" + names[i]);
        }
        out.write("\n");

        for (int i = 0; i < res.size(); i++) {
            out.write(names[i]);
            for (int j = 0; j < res.get(i).size(); j++) {
                out.write("\t" + res.get(i).get(j));
            }
            out.write("\n");
        }
        out.close();
    }

    public void performMatrixCreation() throws IOException {
    }


    public void performXMLCreation() throws IOException {

    }

    public void performPCAcreation() {
    }
}
