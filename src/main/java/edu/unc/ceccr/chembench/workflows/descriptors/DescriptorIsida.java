package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class DescriptorIsida implements DescriptorSet {

    @Override
    public String getDescriptorSet() { return Constants.ISIDA;}

    @Override
    public String getFileEnding() { return ".ISIDA";}

    public String getFileHdrEnding() { return ".ISIDA.hdr";}

    public String getFileSvmEnding() { return ".ISIDA.svm";}

    public String getFileRenormEnding() { return ".renorm.ISIDA";}

    @Override
    public String getFileErrorOut() { return "ISIDA.out";}

    @Override
    public void generateDescriptors(String sdfile, String outfile) {
        //Given an SDF file, run ISIDA to get the chemical descriptors for each compound
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        Path workingDir = Paths.get(sdfile.replaceAll("/[^/]+$", "")).resolve("Descriptors");
        Path inFilePath = workingDir.relativize(Paths.get(sdfile));
        Path outFilePath = workingDir.relativize(Paths.get(outfile + getFileEnding()));
        String execstr = String.format("Fragmentor -i %s -o %s -t 0 -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name",
                inFilePath.toString(), outFilePath.toString());
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir.toString(), "ISIDA");
    }

    public void generateIsidaDescriptorsWithHeader(String sdfile, String outfile, String headerFile) {
        //Given an SDF file, run ISIDA to get the chemical descriptors for each compound with the .hdr from predictor
        //Generate sdf.ISIDA.hdr and sdf.ISIDA.svm
        Path workingDir = Paths.get(sdfile.replaceAll("/[^/]+$", "")).resolve("Descriptors");
        Path inFilePath = workingDir.relativize(Paths.get(sdfile));
        Path outFilePath = workingDir.relativize(Paths.get(outfile));
        Path headerFilePath = workingDir.relativize(Paths.get(headerFile));
        String execstr = String.format("Fragmentor -i %s -o %s -t 0 -t 3 -l 2 -u 4 -t 10 -l 2 -u 4 -s Chembench_Name "
                + "-h %s --StrictFrg", inFilePath.toString(), outFilePath.toString(), headerFilePath.toString());
        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir.toString(), "ISIDA");
    }

    @Override
    public String checkDescriptors(String outputFile) throws Exception {
        File hdrFile = new File(outputFile + getFileHdrEnding());
        File svmFile = new File (outputFile + getFileSvmEnding());
        String errors = "";

        if (!(hdrFile.exists() && svmFile.exists())) {
            errors = "Cannot find ISIDA files";
        }
        return errors;
    }

}