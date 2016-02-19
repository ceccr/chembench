package edu.unc.ceccr.chembench.servlet;

import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.workflows.download.WriteCsv;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

public class FileServlet extends HttpServlet {
    //used to download individual files, e.g., a job result summary.

    private static final Logger logger = Logger.getLogger(FileServlet.class.getName());
    @Autowired
    private PredictorRepository predictorRepository;
    @Autowired
    private PredictionRepository predictionRepository;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, servletConfig.getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);

            String jobType = request.getParameter("jobType"); //DATASET, MODELING, PREDICTION
            String id = request.getParameter("id"); //id of the dataset, predictor, or prediction
            String datasetName = request.getParameter("datasetName");
            String file = request.getParameter("file"); //Type of file requested, e.g. "predictionAsCsv".
            String userName = ((User) session.getAttribute("user")).getUserName();
            String user_mol = request.getParameter("userName");
            String compoundId = request.getParameter("compoundId");

            String dirName = Constants.CECCR_USER_BASE_PATH;
            String fileName = "";
            if (jobType.equalsIgnoreCase(Constants.DATASET)) {
                //Dataset dataset = PopulateDataObjects.getDataSetById(Long.parseLong(id), s);
                dirName += user_mol + "/DATASETS/";
                dirName += datasetName + "/";

                //add file names here...
                if (file.equalsIgnoreCase("mol")) {
                    //String compoundID = request.getParameter("compoundId");
                    dirName += "Visualization/Structures" + "/";
                    fileName = compoundId + "_3D.mol";
                }

            } else if (jobType.equalsIgnoreCase(Constants.MODELING)) {
                Predictor predictor = predictorRepository.findOne(Long.parseLong(id));
                dirName += predictor.getUserName() + "/PREDICTORS/";
                dirName += predictor.getName() + "/";

                //add file names here...
                if (file.equalsIgnoreCase("externalPredictionsAsCSV")) {
                    WriteCsv.writeExternalPredictionsAsCSV(Long.parseLong(id));
                    fileName = predictor.getName() + "-external-set-predictions.csv";
                }
            } else if (jobType.equalsIgnoreCase(Constants.PREDICTION)) {
                Prediction prediction = predictionRepository.findOne(Long.parseLong(id));
                dirName += prediction.getUserName() + "/PREDICTIONS/";
                dirName += prediction.getName() + "/";

                if (file.equalsIgnoreCase("predictionAsCSV")) {
                    fileName = prediction.getName() + "-prediction-values.csv";
                    if (!(new File(dirName, fileName).exists())) {
                        WriteCsv.writePredictionValuesAsCSV(Long.parseLong(id));
                    }
                }
            }

            //Now we know what file to send the user. Send it!
            // Prepare streams
            File filePath = new File(dirName + fileName);

            BufferedInputStream input = null;
            if (filePath.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                input = new BufferedInputStream(fis);
                response.setContentType("application/zip");
                int contentLength = input.available();
                response.setContentLength(contentLength);
                response.setHeader("Content-Disposition", "inline; filename=" + fileName);
                BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
                while (contentLength-- > 0) {
                    output.write(input.read());
                }
                output.close();
                fis.close();
                filePath.delete();
            } else {
                logger.warn("Bad filepath: " + dirName + fileName);
                PrintWriter writer = response.getWriter();
                writer.write("An error occured, can not download the project file.");
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }
}
