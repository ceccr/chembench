package edu.unc.ceccr.chembench.workflows.descriptors;

public interface DescriptorSet {

    String getDescriptorSet();
    String getFileEnding();
    String getFileErrorOut();
    void generateDescriptors(String sdfile, String outfile) throws Exception;
    String checkDescriptors(String outputFile) throws Exception;

}
