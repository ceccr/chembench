package edu.unc.ceccr.chembench.workflows.descriptors;

import com.google.common.base.Splitter;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class AllDescriptors {
    private List<DescriptorSet> descriptorSets;

    public AllDescriptors(String descriptors) {
        descriptorSets = new ArrayList<>();
        if (!descriptors.isEmpty()) {
            List<String> descriptorsSplit = Splitter.on(", ").splitToList(descriptors);

            for (String descriptorString : descriptorsSplit) {
                if (descriptorString.equals(Constants.CDK) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorCDK());
                }
                if (descriptorString.equals(Constants.DRAGONH) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorDragonH());
                }
                if (descriptorString.equals(Constants.DRAGONNOH) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorDragonNoH());
                }
                if (descriptorString.equals(Constants.MOE2D) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorMoe2D());
                }
                if (descriptorString.equals(Constants.MACCS) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorMaccs());
                }
                if (descriptorString.equals(Constants.ISIDA) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorIsida());
                }
                if (descriptorString.equals(Constants.DRAGON7) || descriptorString.equals(Constants.ALL)) {
                    descriptorSets.add(new DescriptorDragon7());
                }
                if (descriptorString.equals(Constants.UPLOADED)) {
                    descriptorSets.add(new DescriptorUploaded());
                }
            }
        }
    }

    public List<DescriptorSet> getDescriptorSets (){
        return descriptorSets;
    }

    public void generateDescriptorSets(String sdfFile, String outFile, String notDescriptorSet) throws
            Exception{
        //this function is for smiles prediction
         if (!descriptorSets.isEmpty()){
             for (DescriptorSet descriptorSet: descriptorSets){
                 if (!descriptorSet.getDescriptorSetName().equals(notDescriptorSet) ||
                         !descriptorSet.getDescriptorSetName().equals(Constants.UPLOADED)) {
                     descriptorSet.generateDescriptors(sdfFile, outFile);
                 }
             }
         }
    }

    public void readDescriptorSets(String sdfFile, List<String> descriptorNames,
                                   List<Descriptors> descriptorValueMatrix) throws Exception{
        if (!descriptorSets.isEmpty()){
            for (DescriptorSet descriptorSet: descriptorSets){
                descriptorSet.readDescriptors(sdfFile, descriptorNames, descriptorValueMatrix);
            }
        }
    }

    public void readDescriptorSetsWithFileEnding(String sdfFile, List<String> descriptorNames,
                                   List<Descriptors> descriptorValueMatrix) throws Exception{
        if (!descriptorSets.isEmpty()){
            for (DescriptorSet descriptorSet: descriptorSets){
                sdfFile += descriptorSet.getFileEnding();
                descriptorSet.readDescriptors(sdfFile, descriptorNames, descriptorValueMatrix);
            }
        }
    }

    public String checkDescriptorsAndReturnAvailableDescriptors(String descriptorDir, String descriptorFile)
            throws Exception{
        String availableDescriptors = "";

        for (DescriptorSet descriptorSet : descriptorSets) {
            String errors = descriptorSet.checkDescriptors(descriptorFile);
            if (!errors.isEmpty()) {
                File errorSummaryFile = new File(descriptorDir + "Logs/" + descriptorSet.getFileErrorOut());
                BufferedWriter errorSummary = new BufferedWriter(new FileWriter(errorSummaryFile));
                errorSummary.write(errors);
                errorSummary.close();
            }

            //CDK is available regardless of errors
            if (descriptorSet.getDescriptorSetName().equals(Constants.CDK)) {
                availableDescriptors += Constants.CDK + " ";
            } else if (errors.isEmpty()) {
                availableDescriptors += descriptorSet.getDescriptorSetName() + " ";
            }
        }
        return availableDescriptors;
    }
}
