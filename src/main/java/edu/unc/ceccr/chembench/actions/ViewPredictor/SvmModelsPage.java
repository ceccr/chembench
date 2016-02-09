package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

public class SvmModelsPage extends ViewPredictorAction {

    /**
     *
     */

    private static final Logger logger = Logger.getLogger(SvmModelsPage.class.getName());
    private final SvmParametersRepository svmParametersRepository;
    private List<SvmModel> svmModels;
    private SvmParameters svmParameters;

    @Autowired
    public SvmModelsPage(SvmParametersRepository svmParametersRepository) {
        this.svmParametersRepository = svmParametersRepository;
    }

    public String load() throws Exception {
        // get models associated with predictor
        String result = getBasicParameters();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        getModelsPageParameters();

        // not all columns are relevant for all SVM types. allows us to select
        // only those needed
        session = HibernateUtil.getSession();
        svmParameters = svmParametersRepository.findOne(selectedPredictor.getModelingParametersId());
        session.close();
        if (childPredictors.size() == 0) {
            result = loadModels();
        } else {
            currentFoldNumber = currentFoldNumber + 1;
            for (int i = 0; i < childPredictors.size(); i++) {
                foldNums.add("" + (i + 1));
                if (currentFoldNumber == (i + 1)) {
                    String parentId = objectId;
                    objectId = "" + childPredictors.get(i).getId();
                    result = loadModels();
                    objectId = parentId;
                }
            }
        }
        return result;
    }

    private String loadModels() {
        String result = SUCCESS;

        try {
            svmModels = Lists.newArrayList();
            session = HibernateUtil.getSession();
            List<SvmModel> temp = PopulateDataObjects.getSvmModelsByPredictorId(Long.parseLong(objectId), session);
            session.close();
            if (temp != null) {
                Iterator<SvmModel> it = temp.iterator();
                while (it.hasNext()) {
                    SvmModel m = it.next();
                    if (m.getIsYRandomModel().equals(Constants.NO) && isYRandomPage.equals(Constants.NO)) {
                        svmModels.add(m);
                    } else if (m.getIsYRandomModel().equals(Constants.YES) && isYRandomPage.equals(Constants.YES)) {
                        svmModels.add(m);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("", ex);
            errorStrings.add(ex.getMessage());
            return ERROR;
        }
        return result;
    }

    @SuppressWarnings("unused")
    private String loadModelSets() {
        String result = SUCCESS;
        for (Predictor childPredictor : childPredictors) {
            objectId = "" + childPredictor.getId();
            result = loadModels();
            if (!result.equals(SUCCESS)) {
                return result;
            }
        }
        return result;
    }

    public List<SvmModel> getSvmModels() {
        return svmModels;
    }

    public void setSvmModels(List<SvmModel> svmModels) {
        this.svmModels = svmModels;
    }

    public SvmParameters getSvmParameters() {
        return svmParameters;
    }

    public void setSvmParameters(SvmParameters svmParameters) {
        this.svmParameters = svmParameters;
    }

}
