package edu.unc.ceccr.persistence;

import javax.persistence.*;

@Entity()
@Table(name = "cbench_knnParameters")
public class KnnParameters {

    private Long id;

    private String minNumDescriptors;
    private String stepSize;
    private String maxNumDescriptors;
    private String knnCategoryOptimization;
    private String numCycles;
    private String nearestNeighbors;
    private String pseudoNeighbors;
    private String numRuns;
    private String numMutations;
    private String T1;
    private String T2;
    private String mu;
    private String TcOverTb;
    private String cutoff;
    private String minAccTraining;
    private String minAccTest;
    private String minSlopes;
    private String maxSlopes;
    private String relativeDiffRR0;
    private String diffR01R02;
    private String stopCond;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "minNumDescriptors")
    public String getMinNumDescriptors() {
        return minNumDescriptors;
    }

    public void setMinNumDescriptors(String minNumDescriptors) {
        this.minNumDescriptors = minNumDescriptors;
    }

    @Column(name = "stepSize")
    public String getStepSize() {
        return stepSize;
    }

    public void setStepSize(String stepSize) {
        this.stepSize = stepSize;
    }

    @Column(name = "maxNumDescriptors")
    public String getMaxNumDescriptors() {
        return maxNumDescriptors;
    }

    public void setMaxNumDescriptors(String maxNumDescriptors) {
        this.maxNumDescriptors = maxNumDescriptors;
    }

    @Column(name = "knnCategoryOptimization")
    public String getKnnCategoryOptimization() {
        return knnCategoryOptimization;
    }

    public void setKnnCategoryOptimization(String knnCategoryOptimization) {
        this.knnCategoryOptimization = knnCategoryOptimization;
    }

    @Column(name = "numCycles")
    public String getNumCycles() {
        return numCycles;
    }

    public void setNumCycles(String numCycles) {
        this.numCycles = numCycles;
    }

    @Column(name = "nearestNeighbors")
    public String getNearestNeighbors() {
        return nearestNeighbors;
    }

    public void setNearestNeighbors(String nearestNeighbors) {
        this.nearestNeighbors = nearestNeighbors;
    }

    @Column(name = "pseudoNeighbors")
    public String getPseudoNeighbors() {
        return pseudoNeighbors;
    }

    public void setPseudoNeighbors(String pseudoNeighbors) {
        this.pseudoNeighbors = pseudoNeighbors;
    }

    @Column(name = "numRuns")
    public String getNumRuns() {
        return numRuns;
    }

    public void setNumRuns(String numRuns) {
        this.numRuns = numRuns;
    }

    @Column(name = "numMutations")
    public String getNumMutations() {
        return numMutations;
    }

    public void setNumMutations(String numMutations) {
        this.numMutations = numMutations;
    }

    @Column(name = "T1")
    public String getT1() {
        return T1;
    }

    public void setT1(String t1) {
        T1 = t1;
    }

    @Column(name = "T2")
    public String getT2() {
        return T2;
    }

    public void setT2(String t2) {
        T2 = t2;
    }

    @Column(name = "mu")
    public String getMu() {
        return mu;
    }

    public void setMu(String mu) {
        this.mu = mu;
    }

    @Column(name = "TcOverTb")
    public String getTcOverTb() {
        return TcOverTb;
    }

    public void setTcOverTb(String tcOverTb) {
        TcOverTb = tcOverTb;
    }

    @Column(name = "cutoff")
    public String getCutoff() {
        return cutoff;
    }

    public void setCutoff(String cutoff) {
        this.cutoff = cutoff;
    }

    @Column(name = "minAccTraining")
    public String getMinAccTraining() {
        return minAccTraining;
    }

    public void setMinAccTraining(String minAccTraining) {
        this.minAccTraining = minAccTraining;
    }

    @Column(name = "minAccTest")
    public String getMinAccTest() {
        return minAccTest;
    }

    public void setMinAccTest(String minAccTest) {
        this.minAccTest = minAccTest;
    }

    @Column(name = "minSlopes")
    public String getMinSlopes() {
        return minSlopes;
    }

    public void setMinSlopes(String minSlopes) {
        this.minSlopes = minSlopes;
    }

    @Column(name = "maxSlopes")
    public String getMaxSlopes() {
        return maxSlopes;
    }

    public void setMaxSlopes(String maxSlopes) {
        this.maxSlopes = maxSlopes;
    }

    @Column(name = "relativeDiffRR0")
    public String getRelativeDiffRR0() {
        return relativeDiffRR0;
    }

    public void setRelativeDiffRR0(String relativeDiffRR0) {
        this.relativeDiffRR0 = relativeDiffRR0;
    }

    @Column(name = "diffR01R02")
    public String getDiffR01R02() {
        return diffR01R02;
    }

    public void setDiffR01R02(String diffR01R02) {
        this.diffR01R02 = diffR01R02;
    }

    @Column(name = "stopCond")
    public String getStopCond() {
        return stopCond;
    }

    public void setStopCond(String stopCond) {
        this.stopCond = stopCond;
    }
}
