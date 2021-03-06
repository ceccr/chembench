package edu.unc.ceccr.chembench.workflows.calculations;

import java.util.Set;

public class ConfusionMatrix {
    private Set<Integer> uniqueObservedValues;
    private int[][] matrix;
    private int totalCorrect;
    private int totalIncorrect;
    private double ccr;
    private double accuracy;

    private boolean isBinary;
    private double ppv;
    private double npv;
    private double sensitivity;
    private double specificity;
    private int totalPositives;
    private int totalNegatives;
    private int truePositives;
    private int falsePositives;
    private int trueNegatives;
    private int falseNegatives;

    public Set<Integer> getUniqueObservedValues() {
        return uniqueObservedValues;
    }

    public void setUniqueObservedValues(Set<Integer> uniqueObservedValues) {
        this.uniqueObservedValues = uniqueObservedValues;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public double getCcr() {
        return ccr;
    }

    public void setCcr(double ccr) {
        this.ccr = ccr;
    }

    public int getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public int getTotalIncorrect() {
        return totalIncorrect;
    }

    public void setTotalIncorrect(int totalIncorrect) {
        this.totalIncorrect = totalIncorrect;
    }

    public double getPpv() {
        return ppv;
    }

    public void setPpv(double ppv) {
        this.ppv = ppv;
    }

    public double getNpv() {
        return npv;
    }

    public void setNpv(double npv) {
        this.npv = npv;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public double getSpecificity() {
        return specificity;
    }

    public void setSpecificity(double specificity) {
        this.specificity = specificity;
    }

    public int getTotalPositives() {
        return totalPositives;
    }

    public void setTotalPositives(int totalPositives) {
        this.totalPositives = totalPositives;
    }

    public int getTotalNegatives() {
        return totalNegatives;
    }

    public void setTotalNegatives(int totalNegatives) {
        this.totalNegatives = totalNegatives;
    }

    public int getTruePositives() {
        return truePositives;
    }

    public void setTruePositives(int truePositives) {
        this.truePositives = truePositives;
    }

    public int getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(int falsePositives) {
        this.falsePositives = falsePositives;
    }

    public int getTrueNegatives() {
        return trueNegatives;
    }

    public void setTrueNegatives(int trueNegatives) {
        this.trueNegatives = trueNegatives;
    }

    public int getFalseNegatives() {
        return falseNegatives;
    }

    public void setFalseNegatives(int falseNegatives) {
        this.falseNegatives = falseNegatives;
    }

    public boolean getIsBinary() {
        return isBinary;
    }

    public void setIsBinary(boolean isBinary) {
        this.isBinary = isBinary;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }
}
