package edu.unc.ceccr.chembench.servlet;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.workflows.download.WriteZip;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;


public class ProjectFilesServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ProjectFilesServlet.class.getName());

    //provides zipfiles containing predictors and predictions
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String BASE = Constants.CECCR_USER_BASE_PATH;
        HttpSession session = request.getSession(false);
        String userName = ((User) session.getAttribute("user")).getUserName();
        String projectUserName = request.getParameter("user");
        String projectName = request.getParameter("project");
        String projectType = request.getParameter("projectType");

        if (projectType.equalsIgnoreCase("modeling")) {
            projectType = "PREDICTORS";
        } else {
            projectType = "PREDICTIONS";
        }
        String zipFile = BASE + projectUserName + "/" + projectType + "/" + projectName + ".zip";
        File filePath = new File(zipFile);

        BufferedInputStream input = null;

        try {
            if (projectType.equalsIgnoreCase("PREDICTORS")) {
                WriteZip.ZipModelingResults(userName, projectUserName, projectName, zipFile);
            } else {
                WriteZip.ZipPredictionResults(userName, projectUserName, projectName, zipFile);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        if (filePath.exists()) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                input = new BufferedInputStream(fis);
                response.setContentType("application/zip");
                int contentLength = input.available();
                response.setContentLength(contentLength);
                response.setHeader("Content-Disposition", "inline; filename=" + projectName + ".zip");
                BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
                while (contentLength-- > 0) {
                    output.write(input.read());
                }
                output.close();
                fis.close();
                filePath.delete();

            } catch (Exception e) {
                logger.error("", e);
            }

        } else {
            PrintWriter writer = response.getWriter();

            writer.write("An ERROR occured, can not download the project file.");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}



