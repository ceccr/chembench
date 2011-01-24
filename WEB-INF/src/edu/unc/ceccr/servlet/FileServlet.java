package edu.unc.ceccr.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.action.ViewPredictionAction.CompoundPredictions;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
public class FileServlet extends HttpServlet {
	//used to download individual files.

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("name");
        String userName=request.getParameter("user");
        Long predId=Long.parseLong(request.getParameter("predId"));
        String predictor=request.getParameter("predictor");

        if (fileName != null) {
            // Strip "/" and "\" (avoid directory sniffing by hackers!).
            fileName = fileName.replaceAll("(\\\\|/)", "");
        } else {
            response.sendRedirect("/jsp/main/error.jsp");
            return;
        }

        String contentType = URLConnection.guessContentTypeFromName(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Prepare streams
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
        	String content= "";
			StringBuffer stringBuffer = new StringBuffer(content);
        	ByteArrayInputStream bais = new ByteArrayInputStream(stringBuffer.toString().getBytes("UTF-8"));
        	
        	input = new BufferedInputStream(bais);
            int contentLength = input.available();

            // Init servlet response.
            response.setContentLength(contentLength);
            response.setContentType(contentType);
            response.setHeader(
                "Content-disposition", "attachment; filename=\"" + fileName + "\"");
            output = new BufferedOutputStream(response.getOutputStream());

            // Write file contents to response.
            while (contentLength-- > 0) {
                output.write(input.read());
            }
            output.flush();
        } 
        catch (Exception e) {
			Utility.writeToDebug(e);
		} finally {
          
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Utility.writeToDebug(e);
                  
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Utility.writeToDebug(e);
                   
                }
            }
        }
    }
    
	public static void writePredictionValuesAsText(Long predictionId) throws Exception {
		List<PredictionValue> predictionValues=null;
		Session s = HibernateUtil.getSession();
		Prediction prediction = PopulateDataObjects.getPredictionById(predictionId, s);
		
		String outfileName = Constants.CECCR_BASE_PATH +  prediction.getUserName() + "/PREDICTIONS/" + "predictionValues.txt";
		if(new File(outfileName).exists()){
			FileAndDirOperations.deleteFile(outfileName);
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(outfileName));
		
		ArrayList<Predictor> predictors = new ArrayList<Predictor>();
		String[] predictorIdArray = prediction.getPredictorIds().split("\\s+");
		for(int i = 0; i < predictorIdArray.length; i++){
			predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(predictorIdArray[i]), s));
		}
		
		String predictorNames = "";
		for(Predictor p: predictors){
			predictorNames += p.getName() + ", ";
		}
		predictorNames = predictorNames.substring(0, predictorNames.lastIndexOf(","));
		
		out.write("\n\nChembench Prediction Output \n"
		+"=========================================\n"
		+"User Name: "+prediction.getUserName()+"\n"
		+"Prediction Name: "+prediction.getJobName()+"\n"
		+"Predictors Used: " + predictorNames + "\n"
		+"Similarity Cutoff: "+prediction.getSimilarityCutoff()+"\n"
		+"Prediction Dataset: "+prediction.getDatabase()+"\n"
		+"Predicted Date: "+prediction.getDateCreated()+"\n"
		+"Download Date: "+new Date()+"\n"
		+"Web Site: " + Constants.WEBADDRESS+"\n"
		+"========================================="+"\n"+"\n");
		
		for(Predictor p: predictors){
			PopulateDataObjects.getPredictionValuesByPredictionIdAndPredictorId(predictionId, p.getPredictorId(), s);
			
			String predictorName = p.getName();
			String body = "Prediction results from " + predictorName + ":\n"
			+"Compound Name\t"+"Standard Deviation\t"+"Predicted Value\t"+"Number of Models"+"\n";
			String end ="\n\n";
			
			Iterator<PredictionValue> it = predictionValues.iterator();
			while(it.hasNext()){
				PredictionValue pv = it.next();
				if(pv.getPredictorId().equals(p.getPredictorId())){
					body=body+pv.getCompoundName()+"\t"+pv.getStandardDeviation()+"\t"+pv.getPredictedValue()+"\t"+pv.getNumModelsUsed()+"\n";
				}
			}
		}
		s.close();
		out.close();
	}
	
	public void writePredictionValuesAsCSV(String userName, Long predId, String predictor){
		
	}
}