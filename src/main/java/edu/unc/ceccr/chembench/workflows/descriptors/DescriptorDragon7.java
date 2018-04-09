package edu.unc.ceccr.chembench.workflows.descriptors;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Descriptors;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class DescriptorDragon7 extends DescriptorCommonDragon implements DescriptorSet {
    private static final Logger logger = LoggerFactory.getLogger(DescriptorDragon7.class);

    @Override
    public String getDescriptorSetName() { return Constants.DRAGON7;}

    @Override
    public String getFileEnding() { return ".dragon7";}

    @Override
    public String getFileErrorOut() { return "dragon7.out";}

    @Override
    public void generateDescriptors(String sdfFile, String outFile) throws DescriptorGenerationException {
        try {
            logger.debug(sdfFile);
            String scriptFilePath = Paths.get(Constants.CECCR_BASE_PATH, Constants.DRAGON7_SCRIPT_PATH).toString();
            String execstr = Utility.SPACE_JOINER.join(new String[]{"dragon7",
                    "-s", scriptFilePath,
                    "<", sdfFile
            });
            logger.debug(execstr);
            Path sdfFilePath = Paths.get(sdfFile);
            Path descriptorsDirPath = sdfFilePath.getParent().resolve("Descriptors");
            RunExternalProgram.runCommandAndLogOutput(execstr, descriptorsDirPath.toString() + "/", "dragon7");
            logger.debug(execstr);
            try {
                Files.move(descriptorsDirPath.resolve("dragon7.out"), Paths.get(outFile + getFileEnding()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new DescriptorGenerationException("Dragon 7 descriptor generation failed", e);
            }
        } catch (DescriptorGenerationException e) {
            logger.error("Dragon 7 descriptor generation failed; not adding to available descriptors", e);
        }
    }

    @Override
    public String checkDescriptors(String outputFile) throws Exception {
        File dragon7File = new File(outputFile + getFileEnding());
        String errors = "";

        if (!dragon7File.exists()) {
            errors = "Cannot find dragon7 files";
        }
        return errors;
    }
}
