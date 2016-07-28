package edu.unc.ceccr.chembench.actions.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.DatasetRepository;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import org.apache.http.client.utils.URIBuilder;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Compound3DAction extends ActionSupport implements ServletResponseAware {
    private static final Logger logger = LoggerFactory.getLogger(Compound3DAction.class);
    private static final int APPLET_WIDTH = 350;
    private static final int APPLET_HEIGHT = 350;

    private final DatasetRepository datasetRepository;
    private User user = User.getCurrentUser();

    private String compoundName;
    private Long datasetId;
    private MarvinApplet applet;
    private HttpServletResponse response;

    @Autowired
    public Compound3DAction(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    public String execute() throws IOException {
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
            convert2Dto3D(sdfFilePath, molFilePath);
        }
        applet = new MarvinApplet();
        applet.height = APPLET_HEIGHT;
        applet.width = APPLET_WIDTH;
        try {
            URIBuilder uriBuilder = new URIBuilder("/fileServlet");
            uriBuilder.addParameter("compoundId", compoundName);
            uriBuilder.addParameter("jobType", Constants.DATASET);
            uriBuilder.addParameter("file", "mol");
            uriBuilder.addParameter("datasetName", dataset.getName());
            uriBuilder.addParameter("userName", dataset.getUserName());
            applet.url = uriBuilder.build().toString();
        } catch (URISyntaxException e) {
            logger.error("URL building failed", e);
            return ERROR;
        }

        // the struts2-json-plugin doesn't let us disable html escaping for the url,
        // so we write to the response manually and return NONE to skip result processing
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        response.getWriter().write(gson.toJson(applet));
        return NONE;
    }

    private void convert2Dto3D(Path inFilePath, Path outFilePath) {
        // We have a Visualization/Structures directory, filled with single-compound 2D SDFs.
        // We need 3D mol files in order to visualize them.
        // So, this function will convert a 2D SDF to a 3D mol file on demand.
        logger.debug(".mol file doesn't exist yet, creating it from sdf");
        String command = String.format("molconvert -3:S{fast} mol \"%s\" -o \"%s\"", inFilePath.toString(),
                outFilePath.toString());
        RunExternalProgram.runCommandAndLogOutput(command, outFilePath.getParent(), "molconvert_3D");
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

    public MarvinApplet getApplet() {
        return applet;
    }

    public void setApplet(MarvinApplet applet) {
        this.applet = applet;
    }

    @Override
    public void setServletResponse(HttpServletResponse httpServletResponse) {
        this.response = httpServletResponse;
    }

    /**
     * Container class for info necessary for creating a Marvin 3D compound visualization applet.
     *
     * Note: This inner class *must* be declared public or the struts2-json-plugin will not correctly serialize it.
     */
    public static class MarvinApplet {
        private int width;
        private int height;
        private String url;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
