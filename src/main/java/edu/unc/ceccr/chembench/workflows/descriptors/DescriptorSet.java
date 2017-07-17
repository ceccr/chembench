package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.persistence.Descriptors;
import java.util.List;

public interface DescriptorSet {
    int compoundsPerChunk = 1000;
    //notes:
    //readDescriptors and splitFile function are the same for cdk and uploaded

    //this is the Constants name of each descrpitorSet
    String getDescriptorSetName();

    //file ending that denotes the type of descriptorSet file
    String getFileEnding();

    //file ending to indicate the error file of each descriptorSet file
    String getFileErrorOut();

    //generate descriptors for descriptorSet
    void generateDescriptors(String sdfile, String outfile) throws Exception;

    //check descriptor for each descriptorSet
    String checkDescriptors(String outputFile) throws Exception;

    //read descriptor into list for each descriptorSet
    void readDescriptors(String outputFile, List<String> descriptorNames, List<Descriptors> descriptorValueMatrix) throws Exception;

    void readDescriptorsChunks(String outputFile, List<String> descriptorNames, List<Descriptors> descriptorValueMatrix) throws Exception;

    //split file into chunks if there are too many descriptorSet in a file
    //and return name of the descriptorFile
    String splitFile(String workingDir, String descriptorsFile) throws Exception;
}
