package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;

import java.io.File;

public class DescriptorMaccs implements DescriptorSet{

    @Override
    public String getDescriptorSet() {
        return Constants.MACCS;
    }

    @Override
    public String getFileEnding() {
        return ".maccs";
    }

    @Override
    public String getFileErrorOut() {
        return "maccs.out";
    }

    @Override
    public void generateDescriptors(String sdfile, String outfile) throws Exception {
        //command: "maccs.sh infile.sdf outfile.maccs"
        String execstr = "maccs.sh " + sdfile + " " + outfile + getFileEnding() + " " + Constants.CECCR_BASE_PATH +
                "mmlsoft/SVL_DIR/batch_sd_MACCSFP.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "maccs.sh");
    }

    @Override
    public String checkDescriptors(String maccsOutputFile) throws Exception {
        // right now this doesn't check anything. The MACCS keys never seem to
        // cause issues.
        String errors = "";

        File file = new File(maccsOutputFile + getFileEnding());
        if (!file.exists() || file.length() == 0) {
            errors = "Could not read descriptor file.\n";
        }
        return errors;
    }
}
