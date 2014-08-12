package edu.unc.ceccr.chembench.workflows.calculations;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.Utility;

import java.util.List;

public class ConfusionMatrix {
    List<String> uniqueObservedValues;
    List<List<Integer>> matrixValues;
    Double ccr;

    public List<String> getUniqueObservedValues() {
        return uniqueObservedValues;
    }

    public void setUniqueObservedValues(List<String> uniqueObservedValues) {
        this.uniqueObservedValues = uniqueObservedValues;
    }

    public List<List<Integer>> getMatrixValues() {
        return matrixValues;
    }

    public void setMatrixValues(List<List<Integer>> matrixValues) {
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
