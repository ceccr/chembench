package edu.unc.ceccr.chembench.actions.ViewPredictor;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;

public class ParametersPage extends ViewPredictorAction {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private KnnParameters knnParameters;
    private KnnPlusParameters knnPlusParameters;
    private SvmParameters svmParameters;
    private RandomForestParameters randomForestParameters;

    public String load() throws Exception {
        String result = getBasicParameters();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        session = HibernateUtil.getSession();

        if (selectedPredictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
            randomForestParameters = PopulateDataObjects
                    .getRandomForestParametersById(selectedPredictor.getModelingParametersId(), session);
        } else if (selectedPredictor.getModelMethod().equals(Constants.KNNGA) || selectedPredictor.getModelMethod()
                .equals(Constants.KNNSA)) {
            knnPlusParameters =
                    PopulateDataObjects.getKnnPlusParametersById(selectedPredictor.getModelingParametersId(), session);
        } else if (selectedPredictor.getModelMethod().equals(Constants.KNN)) {
            knnParameters =
                    PopulateDataObjects.getKnnParametersById(selectedPredictor.getModelingParametersId(), session);
        } else if (selectedPredictor.getModelMethod().equals(Constants.SVM)) {
            svmParameters =
                    PopulateDataObjects.getSvmParametersById(selectedPredictor.getModelingParametersId(), session);
            if (svmParameters != null) {
                if (svmParameters.getSvmTypeCategory().equals("0")) {
                    svmParameters.setSvmTypeCategory("C-SVC");
                } else {
                    svmParameters.setSvmTypeCategory("nu-SVC");
                }
                if (svmParameters.getSvmTypeCategory().equals("3")) {
                    svmParameters.setSvmTypeCategory("epsilon-SVR");
                } else {
                    svmParameters.setSvmTypeCategory("nu-SVR");
                }
                if (svmParameters.getSvmKernel().equals("0")) {
                    svmParameters.setSvmKernel("linear");
                } else if (svmParameters.getSvmKernel().equals("1")) {
                    svmParameters.setSvmKernel("polynomial");
                } else if (svmParameters.getSvmKernel().equals("2")) {
                    svmParameters.setSvmKernel("radial basis function");
                } else if (svmParameters.getSvmKernel().equals("3")) {
                    svmParameters.setSvmKernel("sigmoid");
                }
                if (svmParameters.getSvmHeuristics().equals("0")) {
                    svmParameters.setSvmHeuristics("NO");
                } else {
                    svmParameters.setSvmHeuristics("YES");
                }
                if (svmParameters.getSvmProbability().equals("0")) {
                    svmParameters.setSvmProbability("NO");
                } else {
                    svmParameters.setSvmProbability("YES");
                }
            }
        }
        session.close();
        return result;
    }

    // getters and setters

    public KnnParameters getKnnParameters() {
        return knnParameters;
    }

    public void setKnnParameters(KnnParameters knnParameters) {
        this.knnParameters = knnParameters;
    }

    public KnnPlusParameters getKnnPlusParameters() {
        return knnPlusParameters;
    }

    public void setKnnPlusParameters(KnnPlusParameters knnPlusParameters) {
        this.knnPlusParameters = knnPlusParameters;
    }

    public SvmParameters getSvmParameters() {
        return svmParameters;
    }

    public void setSvmParameters(SvmParameters svmParameters) {
        this.svmParameters = svmParameters;
    }

    public RandomForestParameters getRandomForestParameters() {
        return randomForestParameters;
    }

    public void setRandomForestParameters(RandomForestParameters randomForestParameters) {
        this.randomForestParameters = randomForestParameters;
    }

    // end getters and setters

}
