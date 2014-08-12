package edu.unc.ceccr.chembench.persistence;

import java.util.List;

public class CompoundPredictions {

    //not yet saved to DB, but someday it will be

    String compound;
    List<PredictionValue> predictionValues;
    int sortByIndex = 0; //only used when sorting by prediction value

    public String getCompound() {
        return compound;
    }

    public void setCompound(String compound) {
        this.compound = compound;
    }

    public List<PredictionValue> getPredictionValues() {
        return predictionValues;
    }

    public void setPredictionValues(List<PredictionValue> predictionValues) {
        this.predictionValues = predictionValues;
    }

    public int getSortByIndex() {
        return sortByIndex;
    }

    public void setSortByIndex(int sortByIndex) {
        this.sortByIndex = sortByIndex;
    }
}
