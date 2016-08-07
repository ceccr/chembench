package edu.unc.ceccr.chembench.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class PredictionValueRepositoryImpl implements PredictionValueRepositoryCustom {
    private PredictionValueRepository predictionValueRepository;
    private PredictorRepository predictorRepository;

    public List<PredictionValue> findByPredictionId(Long predictionId) {
        List<PredictionValue> pvs = predictionValueRepository.findByPredictionIdOrderByPredictorIdAsc(predictionId);
        Predictor predictor = predictorRepository.findOne(pvs.get(0).getPredictorId());
        for (PredictionValue pv : pvs) {
            if (!(pv.getPredictorId().equals(predictor.getId()))) {
                predictor = predictorRepository.findOne(pv.getPredictorId());
            }
            pv.setNumTotalModels(predictor.getNumTotalModels());
        }
        return pvs;
    }

    public List<PredictionValue> findByPredictionIdAndPredictorId(Long predictionId, Long predictorId) {
        List<PredictionValue> pvs = predictionValueRepository.findByPredictionIdAndPredictorId(predictionId,
                predictorId);
        Predictor predictor = predictorRepository.findOne(predictorId);
        for (PredictionValue pv : pvs) {
            pv.setNumTotalModels(predictor.getNumTotalModels());
        }
        return pvs;
    }

    @Autowired
    public void setPredictionValueRepository(PredictionValueRepository predictionValueRepository) {
        this.predictionValueRepository = predictionValueRepository;
    }

    @Autowired
    public void setPredictorRepository(PredictorRepository predictorRepository) {
        this.predictorRepository = predictorRepository;
    }
}
