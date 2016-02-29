package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.KnnModel;
import edu.unc.ceccr.chembench.persistence.KnnModelRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

public class KnnModelsPage extends DetailPredictorAction {
    private final KnnModelRepository knnModelRepository;
    private List<KnnModel> models;

    @Autowired
    public KnnModelsPage(KnnModelRepository knnModelRepository) {
        this.knnModelRepository = knnModelRepository;
    }

    public String load() throws Exception {
        // get models associated with predictor
        String results = getBasicParameters();
        if (!results.equals(SUCCESS)) {
            return results;
        }
        getModelsPageParameters();

        models = Lists.newArrayList();
        List<KnnModel> allModels = Lists.newArrayList();
        List<KnnModel> temp = knnModelRepository.findByPredictorId(Long.parseLong(objectId));
        if (temp != null) {
            allModels.addAll(temp);

            Iterator<KnnModel> it = allModels.iterator();
            while (it.hasNext()) {
                KnnModel m = it.next();
                if (m.getFlowType().equalsIgnoreCase(Constants.MAINKNN) && isYRandomPage.equals(Constants.NO)) {
                    models.add(m);
                } else if (m.getFlowType().equalsIgnoreCase(Constants.RANDOMKNN) && isYRandomPage
                        .equals(Constants.NO)) {
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
