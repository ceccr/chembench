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
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.persistence.CompoundPredictions;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.WriteDownloadableFilesWorkflow;

@SuppressWarnings("serial")
public class FileServlet extends HttpServlet {
	//used to download individual files, e.g., a job result summary.

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {	
		try{
			HttpSession session=request.getSession(false);
			
			String jobType = request.getParameter("jobType"); //DATASET, MODELING, PREDICTION
	    	String id = request.getParameter("id"); //id of the dataset, predictor, or prediction
	    	String file = request.getParameter("file"); //Type of file requested, e.g. "predictionAsCsv". 
	    	String userName = ((User) session.getAttribute("user")).getUserName();
			
	    	String fileName = Constants.CECCR_USER_BASE_PATH;
	    	Session s = HibernateUtil.getSession();
	    	if(jobType.equalsIgnoreCase(Constants.DATASET)){
	    		DataSet dataset = PopulateDataObjects.getDataSetById(Long.parseLong(id), s);
	    		fileName += dataset.getUserName() + "/";
	    		fileName += dataset.getFileName() + "/";
	    		
	    		//add file names here...
	    	}
	    	else if(jobType.equalsIgnoreCase(Constants.MODELING)){
	    		Predictor predictor = PopulateDataObjects.getPredictorById(Long.parseLong(id), s);
	    		fileName += predictor.getUserName() + "/";
	    		fileName += predictor.getName() + "/";
	    		
	    		//add file names here...
	    	}
	    	else if(jobType.equalsIgnoreCase(Constants.PREDICTION)){
	    		Prediction prediction = PopulateDataObjects.getPredictionById(Long.parseLong(id), s);
	    		fileName += prediction.getUserName() + "/";
	    		fileName += prediction.getJobName() + "/";
	    		
	    		if(file.equalsIgnoreCase("predictionsAsCSV")){
	    			WriteDownloadableFilesWorkflow.writePredictionValuesAsCSV(Long.parseLong(id));
	    			fileName += "predictionValues.csv";
	    		}
	    	}
	    	s.close();
	    	
	    	//Now we know what file to send the user. Send it!
	    	
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
	            String contentType = URLConnection.guessContentTypeFromName(fileName);
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
	    catch(Exception ex){
	    	Utility.writeToDebug(ex);
	    }
	}
}