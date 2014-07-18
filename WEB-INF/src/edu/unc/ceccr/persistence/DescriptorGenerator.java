package edu.unc.ceccr.persistence;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "cbench_descriptors")
public class DescriptorGenerator implements java.io.Serializable {
    //For a program of a specific version (e.g. molconnZ 4.12), store the descriptor names it outputs.

    private Long descriptorGeneratorId;
    private String descriptorNames;
    private String descriptorProgram; // e.g "molconnZ"
    private String descriptorProgramVersion; // e.g. "4.12"

    public DescriptorGenerator() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "descriptorGeneratorId")
    public Long getDescriptorGeneratorId() {
        return this.descriptorGeneratorId;
    }

    public void setDescriptorGeneratorId(Long descriptorGeneratorId) {
        this.descriptorGeneratorId = descriptorGeneratorId;
    }

    @Column(name = "descriptorNames")
    public String getDescriptorNames() {
        return this.descriptorNames;
    }

    public void setDescriptorNames(String descriptorNames) {
        this.descriptorNames = descriptorNames;
    }

    @Column(name = "descriptorProgram")
    public String getdescriptorProgram() {
        return this.descriptorProgram;
    }

    public void setdescriptorProgram(String descriptorProgram) {
        this.descriptorProgram = descriptorProgram;
    }

    @Column(name = "descriptorProgramVersion")
    public String getDescriptorProgramVersion() {
        return this.descriptorProgramVersion;
    }

    public void setDescriptorProgramVersion(String descriptorProgramVersion) {
        this.descriptorProgramVersion = descriptorProgramVersion;
    }

}
