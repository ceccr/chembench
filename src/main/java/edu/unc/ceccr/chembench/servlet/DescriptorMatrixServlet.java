package edu.unc.ceccr.chembench.servlet;

import edu.unc.ceccr.chembench.global.Constants;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@SuppressWarnings("serial")
public class DescriptorMatrixServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(DescriptorMatrixServlet.class.getName());

    //serves up files for use with the dataset visualization Flash app

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        String project = request.getParameter("project");
        String name = request.getParameter("name");
        String userName = request.getParameter("user");

        File matFile = new File(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + project + "/" + name);

        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        if (matFile.exists() && matFile.isFile()) {
            logger.debug("MAT FILE EXISTS? " + matFile.exists());
            try {
                input = new BufferedInputStream(new FileInputStream(matFile));
                int contentLength = input.available();

                response.reset();
                response.setContentLength(contentLength);

                output = new BufferedOutputStream(response.getOutputStream());

                // Write file contents to response.
                while (contentLength-- > 0) {
                    output.write(input.read());
                }

                output.flush();
            } catch (IOException e) {
                logger.error(e);
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
        }
    }
}
