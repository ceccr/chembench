package edu.unc.ceccr.action.ViewPredictor;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.utilities.PopulateDataObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KnnModelsPage extends ViewPredictorAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<KnnModel> models;

    public String load() throws Exception {
        // get models associated with predictor
        String results = getBasicParameters();
        if (!results.equals(SUCCESS)) {
            return results;
        }
        getModelsPageParameters();

        models = new ArrayList<KnnModel>();
        List<KnnModel> allModels = new ArrayList<KnnModel>();
        session = HibernateUtil.getSession();
        List<KnnModel> temp = PopulateDataObjects.getModelsByPredictorId(Long
                .parseLong(objectId), session);
        session.close();
        if (temp != null) {
            allModels.addAll(temp);

            Iterator<KnnModel> it = allModels.iterator();
            while (it.hasNext()) {
                KnnModel m = it.next();
                if (m.getFlowType().equalsIgnoreCase(Constants.MAINKNN)
                        && isYRandomPage.equals(Constants.NO)) {
                    models.add(m);
                } else if (m.getFlowType()
                        .equalsIgnoreCase(Constants.RANDOMKNN)
                        && isYRandomPage.equals(Constants.NO)) {
                    models.add(m);
                }
            }
        }

        return results;
    }

    // getters and setters

    public List<KnnModel> getModels() {
        return models;
    }

    public void setModels(List<KnnModel> models) {
        this.models = models;
    }
    // end getters and setters
}