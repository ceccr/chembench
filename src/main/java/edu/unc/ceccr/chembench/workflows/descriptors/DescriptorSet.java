package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.persistence.Descriptors;
import java.util.List;

public interface DescriptorSet {

    String getDescriptorSetName();
    String getFileEnding();
    String getFileErrorOut();
    void generateDescriptors(String sdfile, String outfile) throws Exception;
    String checkDescriptors(String outputFile) throws Exception;
    void readDescriptors(String outputFile, List<String> descriptorNames, List<Descriptors> descriptorValueMatrix) throws Exception;
}
