package edu.unc.ceccr.servlet;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.workflows.visualization.Molecule3D;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@SuppressWarnings("serial")
public class Compound3DServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(Compound3DServlet.class.getName());

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        //this servlet returns 3D rotatable images when a user clicks on the 2D molecule from a prediction or model.

        String id = request.getParameter("compoundId");
        String userName = request.getParameter("user");
        String datasetName = request.getParameter("datasetName");

        logger.debug("User: " + userName + " Dataset: " + datasetName + " Running SketchServlet.");
        String workingDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + datasetName +
                "/Visualization/Structures/";

        String sdf = id + ".sdf";
        String mol3D = id + "_3D.mol";

        String sdfPath = workingDir + sdf;
        ///////////////////
        //String urlBaseDir = "/BASE/"  + userName +  "/DATASETS/" + datasetName + "/Visualization/Structures/";
        String urlBaseDir = Constants.WEBADDRESS + "/fileServlet?" + "jobType=DATASET" + "&" + "file=mol" + "&" +
                "datasetName=" + datasetName + "&"
                + "userName=" + userName;

        //Check if file exist if no then we assuming that servlet was called from Prediction tab for the predictor
        // which was based on public dataset
        if (!new File(sdfPath).exists()) {
            workingDir = Constants.CECCR_USER_BASE_PATH + "all-users/DATASETS/" + datasetName +
                    "/Visualization/Structures/";
            //////////////////
            //urlBaseDir = "/BASE/"  + "all-users/DATASETS/" + datasetName + "/Visualization/Structures/";
            urlBaseDir = Constants.WEBADDRESS + "/fileServlet?" + "jobType=DATASET" + "&" + "file=mol" + "&" +
                    "datasetName=" + datasetName + "&"
                    + "userName=all-users";
            sdfPath = workingDir + sdf;
        }

        String title = "<html><title>" + id
                + " 3D view</title><head></head><body bgcolor='black' ><div align='center'><font color='white' " +
                "size='3'> Compound ID = "
                + id + "</div></font>";

        String front = "<script LANGUAGE='JavaScript1.1' SRC='jchem/marvin/marvin.js'></script><script " +
                "LANGUAGE='JavaScript1.1'>"
                + "mview_begin('/jchem/marvin/', 350, 350);";

        String mol3D_URL_friendly = id.replaceAll("%", "%25") + "_3D.mol";
        //String parameter  = "mview_param('mol'," + "'" + urlBaseDir + mol3D_URL_friendly + "'" + ");";
        String parameter = "mview_param('mol'," + "'" + urlBaseDir + "&" + "compoundId=" + id.replaceAll("%",
                "%25") + "'" + ");";

        String end = "mview_end();</script></body></html>";

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        logger.debug("Getting 3D structure for file : " + workingDir + sdf);

        try {
            File sdfile = new File(sdfPath);

			/*String warning = "ERROR, the SD file is not in correct format.\rThe " +
                    "structure can not be displayed.";
			*/
            if (sdfile.exists()) {
                InputStream twoDis = new FileInputStream(sdfile);

                File mol3DFile = new File(workingDir + mol3D);
                if (!mol3DFile.exists()) {
                    Molecule3D.Convert2Dto3D(userName, datasetName, sdf, mol3D, workingDir);
                } else {
                    logger.debug("3D structure already calculated. Returning it.");
                }

                out.println(title);
                out.println(front);
                out.println(parameter);
                out.println(end);

                twoDis.close();
            } else {
                out.println(" ERROR : Can not find SD file: " + id);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            out.close();
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Far as I can tell, this is never actually called anywhere.
        logger.debug("doing a post, yo.");
        doGet(request, response);
        logger.debug("done wit da post, yo.");
    }

}
