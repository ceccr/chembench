package edu.unc.ceccr.persistence;

import javax.persistence.*;

@Entity()
@Table(name = "cbench_svmParameters")
public class SvmParameters {

    private Long id;

    private String svmDegreeFrom;
    private String svmDegreeTo;
    private String svmDegreeStep;

    private String svmGammaFrom;
    private String svmGammaTo;
    private String svmGammaStep;

    private String svmCostFrom;
    private String svmCostTo;
    private String svmCostStep;

    private String svmNuFrom;
    private String svmNuTo;
    private String svmNuStep;

    private String svmPEpsilonFrom;
    private String svmPEpsilonTo;
    private String svmPEpsilonStep;

    private String svmCrossValidation;
    private String svmEEpsilon;
    private String svmHeuristics;
    private String svmKernel;
    private String svmProbability;
    private String svmTypeCategory;
    private String svmTypeContinuous;
    private String svmWeight;
    private String svmCutoff;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "svmDegreeFrom")
    public String getSvmDegreeFrom() {
        return svmDegreeFrom;
    }

    public void setSvmDegreeFrom(String svmDegreeFrom) {
        this.svmDegreeFrom = svmDegreeFrom;
    }

    @Column(name = "svmDegreeTo")
    public String getSvmDegreeTo() {
        return svmDegreeTo;
    }

    public void setSvmDegreeTo(String svmDegreeTo) {
        this.svmDegreeTo = svmDegreeTo;
    }

    @Column(name = "svmDegreeStep")
    public String getSvmDegreeStep() {
        return svmDegreeStep;
    }

    public void setSvmDegreeStep(String svmDegreeStep) {
        this.svmDegreeStep = svmDegreeStep;
    }

    @Column(name = "svmGammaFrom")
    public String getSvmGammaFrom() {
        return svmGammaFrom;
    }

    public void setSvmGammaFrom(String svmGammaFrom) {
        this.svmGammaFrom = svmGammaFrom;
    }

    @Column(name = "svmGammaTo")
    public String getSvmGammaTo() {
        return svmGammaTo;
    }

    public void setSvmGammaTo(String svmGammaTo) {
        this.svmGammaTo = svmGammaTo;
    }

    @Column(name = "svmGammaStep")
    public String getSvmGammaStep() {
        return svmGammaStep;
    }

    public void setSvmGammaStep(String svmGammaStep) {
        this.svmGammaStep = svmGammaStep;
    }

    @Column(name = "svmCostFrom")
    public String getSvmCostFrom() {
        return svmCostFrom;
    }

    public void setSvmCostFrom(String svmCostFrom) {
        this.svmCostFrom = svmCostFrom;
    }

    @Column(name = "svmCostTo")
    public String getSvmCostTo() {
        return svmCostTo;
    }

    public void setSvmCostTo(String svmCostTo) {
        this.svmCostTo = svmCostTo;
    }

    @Column(name = "svmCostStep")
    public String getSvmCostStep() {
        return svmCostStep;
    }

    public void setSvmCostStep(String svmCostStep) {
        this.svmCostStep = svmCostStep;
    }

    @Column(name = "svmNuFrom")
    public String getSvmNuFrom() {
        return svmNuFrom;
    }

    public void setSvmNuFrom(String svmNuFrom) {
        this.svmNuFrom = svmNuFrom;
    }

    @Column(name = "svmNuTo")
    public String getSvmNuTo() {
        return svmNuTo;
    }

    public void setSvmNuTo(String svmNuTo) {
        this.svmNuTo = svmNuTo;
    }

    @Column(name = "svmNuStep")
    public String getSvmNuStep() {
        return svmNuStep;
    }

    public void setSvmNuStep(String svmNuStep) {
        this.svmNuStep = svmNuStep;
    }

    @Column(name = "svmPEpsilonFrom")
    public String getSvmPEpsilonFrom() {
        return svmPEpsilonFrom;
    }

    public void setSvmPEpsilonFrom(String svmPEpsilonFrom) {
        this.svmPEpsilonFrom = svmPEpsilonFrom;
    }

    @Column(name = "svmPEpsilonTo")
    public String getSvmPEpsilonTo() {
        return svmPEpsilonTo;
    }

    public void setSvmPEpsilonTo(String svmPEpsilonTo) {
        this.svmPEpsilonTo = svmPEpsilonTo;
    }

    @Column(name = "svmPEpsilonStep")
    public String getSvmPEpsilonStep() {
        return svmPEpsilonStep;
    }

    public void setSvmPEpsilonStep(String svmPEpsilonStep) {
        this.svmPEpsilonStep = svmPEpsilonStep;
    }

    @Column(name = "svmCrossValidation")
    public String getSvmCrossValidation() {
        return svmCrossValidation;
    }

    public void setSvmCrossValidation(String svmCrossValidation) {
        this.svmCrossValidation = svmCrossValidation;
    }

    @Column(name = "svmEEpsilon")
    public String getSvmEEpsilon() {
        return svmEEpsilon;
    }

    public void setSvmEEpsilon(String svmEEpsilon) {
        this.svmEEpsilon = svmEEpsilon;
    }

    @Column(name = "svmHeuristics")
    public String getSvmHeuristics() {
        return svmHeuristics;
    }

    public void setSvmHeuristics(String svmHeuristics) {
        this.svmHeuristics = svmHeuristics;
    }

    @Column(name = "svmKernel")
    public String getSvmKernel() {
        // '0':'linear','1':'polynomial','2':'radial basis function','3':'sigmoid'
        return svmKernel;
    }

    public void setSvmKernel(String svmKernel) {
        this.svmKernel = svmKernel;
    }

    @Column(name = "svmProbability")
    public String getSvmProbability() {
        return svmProbability;
    }

    public void setSvmProbability(String svmProbability) {
        this.svmProbability = svmProbability;
    }

    @Column(name = "svmTypeCategory")
    public String getSvmTypeCategory() {
        // '0':'C-SVC','1':'nu-SVC'
        return svmTypeCategory;
    }

    public void setSvmTypeCategory(String svmTypeCategory) {
        this.svmTypeCategory = svmTypeCategory;
    }

    @Column(name = "svmTypeContinuous")
    public String getSvmTypeContinuous() {
        // '3':'epsilon-SVR','4':'nu-SVR'
        return svmTypeContinuous;
    }

    public void setSvmTypeContinuous(String svmTypeContinuous) {
        this.svmTypeContinuous = svmTypeContinuous;
    }

    @Column(name = "svmWeight")
    public String getSvmWeight() {
        return svmWeight;
    }

    public void setSvmWeight(String svmWeight) {
        this.svmWeight = svmWeight;
    }

    @Column(name = "svmCutoff")
    public String getSvmCutoff() {
        return svmCutoff;
    }

    public void setSvmCutoff(String svmCutoff) {
        this.svmCutoff = svmCutoff;
    }

}