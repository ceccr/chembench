package edu.unc.ceccr.chembench.workflows.descriptors;


import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class DescriptorCDK implements DescriptorSet{

    @Override
    public String getDescriptorSet() {
        return Constants.CDK;
    }

    @Override
    public String getFileEnding() {
        return ".cdk";
    }

    @Override
    public String getFileErrorOut() {
        return "cdk.out";
    }

    @Override
    public void generateDescriptors(String sdfile, String outfile) throws Exception {
        //Given an SD file, run CDK to get the chemical descriptors for each compound.
        String xmlFile = Constants.CECCR_BASE_PATH + Constants.CDK_XMLFILE_PATH;
        String outfileWithEnding = outfile + getFileEnding();

        String execstr = "java -jar " + Constants.EXECUTABLEFILE_PATH + "CDKGui-1.30.jar -b -o " + outfileWithEnding
                + " -s" + " " + xmlFile + " " + sdfile;

        String workingDir = sdfile.replaceAll("/[^/]+$", "");

        RunExternalProgram.runCommandAndLogOutput(execstr, workingDir + "/Descriptors/", "cdk");

        //Temporary thing; makes life easier for some users for bioactivity use case
        outfileWithEnding = outfileWithEnding.substring(outfileWithEnding.lastIndexOf("/") + 1);
        ReadDescriptors.convertCdkToX(outfileWithEnding, workingDir + "/Descriptors/");
    }

    @Override
    public String checkDescriptors(String cdkOutputFile) throws Exception {
        // check if CDK file exists, then look for NA's.
        String errors = "";

        File file = new File(cdkOutputFile + getFileEnding());
        if (!file.exists() || file.length() == 0) {
            errors = "Could not read descriptor file.\n";
        } else {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains("NA")) {
                    String compoundName = line.split("\\s+")[0];
                    errors += ("Error: compound named '" + compoundName + "' has some invalid descriptors. \n");
                }
            }
            br.close();
        }
        return errors;
    }
}
