package edu.unc.ceccr.workflows.calculations;

import java.util.ArrayList;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

public class ConfusionMatrix {
    ArrayList<String> uniqueObservedValues;
    ArrayList<ArrayList<Integer>> matrixValues;
    Double ccr;

    public ArrayList<String> getUniqueObservedValues() {
        return uniqueObservedValues;
    }

    public void setUniqueObservedValues(ArrayList<String> uniqueObservedValues) {
        this.uniqueObservedValues = uniqueObservedValues;
    }

    public ArrayList<ArrayList<Integer>> getMatrixValues() {
        return matrixValues;
    }

    public void setMatrixValues(ArrayList<ArrayList<Integer>> matrixValues) {
        this.matrixValues = matrixValues;
    }

    public Double getCcr() {
        return ccr;
    }

    public void setCcr(Double ccr) {
        this.ccr = ccr;
    }

    public String getCcrAsString() {
        return Utility.roundSignificantFigures("" + ccr, Constants.REPORTED_SIGNIFICANT_FIGURES);
    }
}