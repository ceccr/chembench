package edu.unc.ceccr.chembench.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PredictionValueRepositoryImpl implements PredictionValueRepository {
    @Autowired
    private BasePredictionValueRepository basePredictionValueRepository;
    @Autowired
    private PredictorRepository predictorRepository;

    public List<PredictionValue> findByPredictionId(Long predictionId) {
        List<PredictionValue> pvs = basePredictionValueRepository.findByPredictionIdOrderByPredictorIdAsc(predictionId);
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
        List<PredictionValue> pvs = basePredictionValueRepository.findByPredictionIdAndPredictorId(predictionId,
                predictorId);
        Predictor predictor = predictorRepository.findOne(predictorId);
        for (PredictionValue pv : pvs) {
            pv.setNumTotalModels(predictor.getNumTotalModels());
        }
        return pvs;
    }

    @Override
    public List<PredictionValue> findByPredictionIdOrderByPredictorIdAsc(Long predictionId) {
        return basePredictionValueRepository.findByPredictionIdOrderByPredictorIdAsc(predictionId);
    }

    @Override
    public void delete(PredictionValue deleted) {
        basePredictionValueRepository.delete(deleted);
    }

    @Override
    public List<PredictionValue> findAll() {
        return basePredictionValueRepository.findAll();
    }

    @Override
    public PredictionValue findOne(Long id) {
        return basePredictionValueRepository.findOne(id);
    }

    @Override
    public PredictionValue save(PredictionValue persisted) {
        return basePredictionValueRepository.save(persisted);
    }
}
