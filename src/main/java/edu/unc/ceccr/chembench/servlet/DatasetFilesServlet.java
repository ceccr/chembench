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

@SuppressWarnings("serial")
public class DatasetFilesServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(DatasetFilesServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String BASE = Constants.CECCR_USER_BASE_PATH;
        String datasetUserName = request.getParameter("user");
        String datasetName = request.getParameter("datasetName");
        String zipFile = BASE + datasetUserName + "/DATASETS/" + datasetName + ".zip";
        File filePath = new File(zipFile);
        BufferedInputStream input = null;
        HttpSession session = request.getSession(false);
        String userName = ((User) session.getAttribute("user")).getUserName();
        try {
            WriteZip.ZipDatasets(userName, datasetUserName, datasetName, zipFile);
            if (filePath.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                input = new BufferedInputStream(fis);
                response.setContentType("application/zip");
                int contentLength = input.available();
                response.setContentLength(contentLength);
                response.setHeader("Content-Disposition", "inline; filename=" + datasetName + ".zip");
                BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
                while (contentLength-- > 0) {
                    output.write(input.read());
                }
                output.close();
                fis.close();
                filePath.delete();
            } else {
                PrintWriter writer = response.getWriter();
                writer.write("An ERROR occured, can not download the dataset file.");
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
