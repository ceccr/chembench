package edu.unc.ceccr.chembench.actions.api;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.DatasetRepository;
import edu.unc.ceccr.chembench.persistence.User;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static edu.unc.ceccr.chembench.workflows.visualization.SdfToJpg.convert2Dto3D;

/**
 * Handles requests for 3D .mol files for use in 3D compound visualization.
 */
public class Compound3DAction extends ActionSupport implements ServletResponseAware {
    private static final Logger logger = LoggerFactory.getLogger(Compound3DAction.class);

    private final DatasetRepository datasetRepository;
    private User user = User.getCurrentUser();

    private String compoundName;
    private Long datasetId;
    private HttpServletResponse response;

    @Autowired
    public Compound3DAction(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    /**
     * Return a plaintext response containing the 3D .mol file of the requested compound.
     *
     * @return result string
     */
    public String execute() {
        if (compoundName == null || datasetId == null) {
            return "badrequest";
        }
        Dataset dataset = datasetRepository.findOne(datasetId);
        if (dataset == null) {
            return "notfound";
        } else if (!dataset.canBeViewedBy(user)) {
            return "forbidden";
        }

        Path datasetStructuresDirPath = dataset.getDirectoryPath().resolve("Visualization").resolve("Structures");
        Path sdfFilePath = datasetStructuresDirPath.resolve(compoundName + ".sdf");
        if (!Files.exists(sdfFilePath)) {
            return "notfound";
        }
        Path molFilePath = datasetStructuresDirPath.resolve(compoundName + "_3D.mol");
        logger.debug("Getting molfile: " + molFilePath);
        if (!Files.exists(molFilePath)) {
            logger.debug(".mol file doesn't exist yet, creating it from sdf");
            convert2Dto3D(sdfFilePath, molFilePath);
        }
        try (BufferedReader reader = Files.newBufferedReader(molFilePath, StandardCharsets.UTF_8)) {
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.write('\n');
            }
        } catch (IOException e) {
            logger.error("Couldn't write to response", e);
            return ERROR;
        }
        // we've already written directly to the response, so return NONE to skip result processing
        return NONE;
    }

    public String getCompoundName() {
        return compoundName;
    }

    public void setCompoundName(String compoundName) {
        this.compoundName = compoundName;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
    }

    @Override
    public void setServletResponse(HttpServletResponse httpServletResponse) {
        this.response = httpServletResponse;
    }
}
