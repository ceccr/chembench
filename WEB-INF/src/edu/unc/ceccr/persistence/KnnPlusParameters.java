package edu.unc.ceccr.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "cbench_knnPlusParameters")
public class KnnPlusParameters {

    private Long id;

    private String knnMinNumDescriptors;
    private String knnMaxNumDescriptors;
    private String knnDescriptorStepSize;
    private String knnMinNearestNeighbors;
    private String knnMaxNearestNeighbors;

    private String saNumRuns;
    private String saMutationProbabilityPerDescriptor;
    private String saNumBestModels;
    private String saTempDecreaseCoefficient;
    private String saLogInitialTemp;
    private String saFinalTemp;
    private String saTempConvergence;

    private String gaPopulationSize;
    private String gaMaxNumGenerations;
    private String gaNumStableGenerations;
    private String gaTournamentGroupSize;
    private String gaMinFitnessDifference;

    private String knnApplicabilityDomain;
    private String knnMinTraining;
    private String knnMinTest;
    private String knnSaErrorBasedFit;
    private String knnGaErrorBasedFit;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "knnMinNumDescriptors")
    public String getKnnMinNumDescriptors() {
        return knnMinNumDescriptors;
    }

    public void setKnnMinNumDescriptors(String knnMinNumDescriptors) {
        this.knnMinNumDescriptors = knnMinNumDescriptors;
    }

    @Column(name = "knnMaxNumDescriptors")
    public String getKnnMaxNumDescriptors() {
        return knnMaxNumDescriptors;
    }

    public void setKnnMaxNumDescriptors(String knnMaxNumDescriptors) {
        this.knnMaxNumDescriptors = knnMaxNumDescriptors;
    }

    @Column(name = "knnDescriptorStepSize")
    public String getKnnDescriptorStepSize() {
        return knnDescriptorStepSize;
    }

    public void setKnnDescriptorStepSize(String knnDescriptorStepSize) {
        this.knnDescriptorStepSize = knnDescriptorStepSize;
    }

    @Column(name = "knnMinNearestNeighbors")
    public String getKnnMinNearestNeighbors() {
        return knnMinNearestNeighbors;
    }

    public void setKnnMinNearestNeighbors(String knnMinNearestNeighbors) {
        this.knnMinNearestNeighbors = knnMinNearestNeighbors;
    }

    @Column(name = "knnMaxNearestNeighbors")
    public String getKnnMaxNearestNeighbors() {
        return knnMaxNearestNeighbors;
    }

    public void setKnnMaxNearestNeighbors(String knnMaxNearestNeighbors) {
        this.knnMaxNearestNeighbors = knnMaxNearestNeighbors;
    }

    @Column(name = "saNumRuns")
    public String getSaNumRuns() {
        return saNumRuns;
    }

    public void setSaNumRuns(String saNumRuns) {
        this.saNumRuns = saNumRuns;
    }

    @Column(name = "saMutationProbabilityPerDescriptor")
    public String getSaMutationProbabilityPerDescriptor() {
        return saMutationProbabilityPerDescriptor;
    }

    public void setSaMutationProbabilityPerDescriptor(
            String saMutationProbabilityPerDescriptor) {
        this.saMutationProbabilityPerDescriptor = saMutationProbabilityPerDescriptor;
    }

    @Column(name = "saNumBestModels")
    public String getSaNumBestModels() {
        return saNumBestModels;
    }

    public void setSaNumBestModels(String saNumBestModels) {
        this.saNumBestModels = saNumBestModels;
    }

    @Column(name = "saTempDecreaseCoefficient")
    public String getSaTempDecreaseCoefficient() {
        return saTempDecreaseCoefficient;
    }

    public void setSaTempDecreaseCoefficient(String saTempDecreaseCoefficient) {
        this.saTempDecreaseCoefficient = saTempDecreaseCoefficient;
    }

    @Column(name = "saLogInitialTemp")
    public String getSaLogInitialTemp() {
        return saLogInitialTemp;
    }

    public void setSaLogInitialTemp(String saLogInitialTemp) {
        this.saLogInitialTemp = saLogInitialTemp;
    }

    @Column(name = "saFinalTemp")
    public String getSaFinalTemp() {
        return saFinalTemp;
    }

    public void setSaFinalTemp(String saFinalTemp) {
        this.saFinalTemp = saFinalTemp;
    }

    @Column(name = "saTempConvergence")
    public String getSaTempConvergence() {
        return saTempConvergence;
    }

    public void setSaTempConvergence(String saTempConvergence) {
        this.saTempConvergence = saTempConvergence;
    }

    @Column(name = "gaPopulationSize")
    public String getGaPopulationSize() {
        return gaPopulationSize;
    }

    public void setGaPopulationSize(String gaPopulationSize) {
        this.gaPopulationSize = gaPopulationSize;
    }

    @Column(name = "gaMaxNumGenerations")
    public String getGaMaxNumGenerations() {
        return gaMaxNumGenerations;
    }

    public void setGaMaxNumGenerations(String gaMaxNumGenerations) {
        this.gaMaxNumGenerations = gaMaxNumGenerations;
    }

    @Column(name = "gaNumStableGenerations")
    public String getGaNumStableGenerations() {
        return gaNumStableGenerations;
    }

    public void setGaNumStableGenerations(String gaNumStableGenerations) {
        this.gaNumStableGenerations = gaNumStableGenerations;
    }

    @Column(name = "gaTournamentGroupSize")
    public String getGaTournamentGroupSize() {
        return gaTournamentGroupSize;
    }

    public void setGaTournamentGroupSize(String gaTournamentGroupSize) {
        this.gaTournamentGroupSize = gaTournamentGroupSize;
    }

    @Column(name = "gaMinFitnessDifference")
    public String getGaMinFitnessDifference() {
        return gaMinFitnessDifference;
    }

    public void setGaMinFitnessDifference(String gaMinFitnessDifference) {
        this.gaMinFitnessDifference = gaMinFitnessDifference;
    }

    @Column(name = "knnApplicabilityDomain")
    public String getKnnApplicabilityDomain() {
        return knnApplicabilityDomain;
    }

    public void setKnnApplicabilityDomain(String knnApplicabilityDomain) {
        this.knnApplicabilityDomain = knnApplicabilityDomain;
    }

    @Column(name = "knnMinTraining")
    public String getKnnMinTraining() {
        return knnMinTraining;
    }

    public void setKnnMinTraining(String knnMinTraining) {
        this.knnMinTraining = knnMinTraining;
    }

    @Column(name = "knnMinTest")
    public String getKnnMinTest() {
        return knnMinTest;
    }

    public void setKnnMinTest(String knnMinTest) {
        this.knnMinTest = knnMinTest;
    }

    @Column(name = "knnSaErrorBasedFit")
    public String getKnnSaErrorBasedFit() {
        return knnSaErrorBasedFit;
    }

    public void setKnnSaErrorBasedFit(String knnSaErrorBasedFit) {
        this.knnSaErrorBasedFit = knnSaErrorBasedFit;
    }

    @Column(name = "knnGaErrorBasedFit")
    public String getKnnGaErrorBasedFit() {
        return knnGaErrorBasedFit;
    }

    public void setKnnGaErrorBasedFit(String knnGaErrorBasedFit) {
        this.knnGaErrorBasedFit = knnGaErrorBasedFit;
    }


}