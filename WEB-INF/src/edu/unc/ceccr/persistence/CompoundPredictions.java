package edu.unc.ceccr.persistence;

import java.util.ArrayList;

public class CompoundPredictions {

    //not yet saved to DB, but someday it will be

    String compound;
    ArrayList<PredictionValue> predictionValues;
    int sortByIndex = 0; //only used when sorting by prediction value

    public String getCompound() {
        return compound;
    }

    public void setCompound(String compound) {
        this.compound = compound;
    }

    public ArrayList<PredictionValue> getPredictionValues() {
        return predictionValues;
    }

    public void setPredictionValues(ArrayList<PredictionValue> predictionValues) {
        this.predictionValues = predictionValues;
    }

    public int getSortByIndex() {
        return sortByIndex;
    }

    public void setSortByIndex(int sortByIndex) {
        this.sortByIndex = sortByIndex;
    }
}