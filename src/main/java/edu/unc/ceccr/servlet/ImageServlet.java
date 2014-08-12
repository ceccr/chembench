package edu.unc.ceccr.servlet;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.workflows.visualization.ExternalValidationChart;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(ImageServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        //this servlet returns 2D images for compounds,
        //external validation charts, and dataset activity charts

        //Note that some of these may be null, depending on the request type!
        String project = request.getParameter("project"); //predictor or prediction name
        String datasetName = request.getParameter("datasetName");
        String compoundId = request.getParameter("compoundId");
        String userName = request.getParameter("user");
        String projectType = request.getParameter("projectType"); //dataset, modeling, prediction
        String currentFoldNumber = request.getParameter("currentFoldNumber"); //ext chart only

        //You might be thinking of adding a call to the database to get the 
        //dataset object here. Don't do it! It'll slow shit down and bleed sessions
        //because this code is called once PER IMAGE on a page.

        String imageFileName = "";
        if (compoundId.startsWith("activityChart")) {
            //activity chart for dataset
            imageFileName = userName + "/DATASETS/" + project + "/Visualization/activityChart.png";
        } else if (compoundId.startsWith("externalValidationChart")) {
            //displays ext validation chart for modeling

            try {
                Session s = HibernateUtil.getSession();
                Predictor predictor = PopulateDataObjects.getPredictorByName(project, userName, s);

                if (!currentFoldNumber.equals("0")) {
                    int numChildren = predictor.getChildIds().split("\\s+").length;
                    String childPredName = project + "_fold_" + currentFoldNumber + "_of_" + numChildren;
                    project += "/" + childPredName;
                }
                imageFileName = userName + "/PREDICTORS/" + project + "/mychart.jpeg";

                if (!new File(Constants.CECCR_USER_BASE_PATH + imageFileName).exists()) {
                    //if there's no ext validation chart, make one
                    ExternalValidationChart.createChart(predictor, currentFoldNumber);
                }

            } catch (Exception ex) {
                logger.error(ex);
            }
        } else if (projectType.equals("dataset")) {
            imageFileName = userName + "/DATASETS/" + datasetName + "/Visualization/Sketches/" + compoundId + ".jpg";
        } else {
            //modeling and prediction images
            imageFileName = userName + "/DATASETS/" + datasetName + "/Visualization/Sketches/" + compoundId + ".jpg";
        }

        File imageFile = new File(Constants.CECCR_USER_BASE_PATH + imageFileName);

        if (!imageFile.exists()) {

            imageFileName = Constants.CECCR_BASE_PATH + Constants.IMAGE_FILEPATH + "no_image.jpg";
            imageFile = new File(imageFileName);

            if (!imageFile.exists()) {
                logger.warn("Could not find default image file.");
                response.setContentLength(0);
                response.setContentType("image/jpeg");
                return;
            }
        }

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(imageFile));
            int contentLength = input.available();

            response.reset();
            response.setContentLength(contentLength);
            response.setContentType("image/jpeg");

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