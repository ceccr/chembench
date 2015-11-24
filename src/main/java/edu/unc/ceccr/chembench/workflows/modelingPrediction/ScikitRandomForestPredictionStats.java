package edu.unc.ceccr.chembench.workflows.modelingPrediction;

public class ScikitRandomForestPredictionStats {
    private double rSquared;
    private double mse;
    private double rmse;
    private double mae;
    private double ccr;

    public double getrSquared() {
        return rSquared;
    }

    public double getMse() {
        return mse;
    }

    public double getRmse() {
        return rmse;
    }

    public double getMae() {
        return mae;
    }

    public double getCcr() {
        return ccr;
    }
}
