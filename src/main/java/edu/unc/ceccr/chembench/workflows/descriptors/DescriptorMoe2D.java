package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class DescriptorMoe2D implements DescriptorSet {

    @Override
    public String getDescriptorSet() {
        return Constants.MOE2D;
    }

    @Override
    public String getFileEnding() {
        return ".moe2D";
    }

    @Override
    public String getFileErrorOut() {
        return "moe2d.out";
    }

    @Override
    public void generateDescriptors(String sdfile, String outfile) throws Exception {
        //command: "moe2D.sh infile.sdf outfile.moe2D"
        String execstr = "moe2D.sh " + " " + sdfile + " " + outfile + getFileEnding() + " " + Constants
                .CECCR_BASE_PATH + "mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl";
        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "moe2d.sh");
    }

    @Override
    public String checkDescriptors(String moe2DOutputFile) throws Exception {
        // right now this doesn't check anything. The MOE2D descriptors never
        // seem to cause issues.
        String errors = "";

        File file = new File(moe2DOutputFile + getFileEnding());
        if (!file.exists() || file.length() == 0) {
            errors = "Could not read descriptor file.\n";
            return errors;
        }

        FileReader fin = new FileReader(file);
        BufferedReader br = new BufferedReader(fin);

        ArrayList<String> descriptorValues; // values for each molecule

        String line = br.readLine(); // descriptor names

        while ((line = br.readLine()) != null) {
            Scanner tok = new Scanner(line);

            if (tok.hasNext()) {
                tok.next(); // first value is compound name
            }

            while (tok.hasNext()) {
                String t = tok.next();
                try {
                    // check if it's a number
                    Float.parseFloat(t);
                } catch (Exception ex) {
                    errors += "Error reading Moe2D descriptor value: " + t + "\n";
                }
            }
            tok.close();
        }
        br.close();
        return errors;
    }
}
