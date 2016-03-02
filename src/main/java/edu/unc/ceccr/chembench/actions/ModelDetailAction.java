package edu.unc.ceccr.chembench.actions;

import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.DatasetRepository;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.persistence.PredictorRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ModelDetailAction extends DetailAction {
    private static final Logger logger = Logger.getLogger(ModelDetailAction.class);
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private Predictor predictor;
    private Dataset modelingDataset;
    private boolean editable;

    @Autowired
    public ModelDetailAction(DatasetRepository datasetRepository, PredictorRepository predictorRepository) {
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
    }

    public String execute() {
        predictor = predictorRepository.findOne(id);
        String result = validateObject(predictor);
        if (!result.equals(SUCCESS)) {
            return result;
        }
        editable = predictor.isEditableBy(user);
        modelingDataset = datasetRepository.findOne(predictor.getDatasetId());
        predictor.setDatasetDisplay(modelingDataset.getName());
        return SUCCESS;
    }

    public Predictor getPredictor() {
        return predictor;
    }

    public void setPredictor(Predictor predictor) {
        this.predictor = predictor;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
