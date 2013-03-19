package edu.unc.ceccr.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.workflows.download.WriteCsv;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class FileServlet extends HttpServlet {
	//used to download individual files, e.g., a job result summary.

    private static Logger logger = Logger.getLogger(FileServlet.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response) {	
		try{
			HttpSession session=request.getSession(false);
			
			String jobType = request.getParameter("jobType"); //DATASET, MODELING, PREDICTION
	    	String id = request.getParameter("id"); //id of the dataset, predictor, or prediction
	    	String file = request.getParameter("file"); //Type of file requested, e.g. "predictionAsCsv". 
	    	String userName = ((User) session.getAttribute("user")).getUserName();
			
	    	String dirName = Constants.CECCR_USER_BASE_PATH;
	    	String fileName = "";
	    	Session s = HibernateUtil.getSession();
	    	if(jobType.equalsIgnoreCase(Constants.DATASET)){
	    		DataSet dataset = PopulateDataObjects.getDataSetById(Long.parseLong(id), s);
	    		dirName += dataset.getUserName() + "/DATASETS/";
	    		dirName += dataset.getName() + "/";
	    		
	    		//add file names here...
	    	}
	    	else if(jobType.equalsIgnoreCase(Constants.MODELING)){
	    		Predictor predictor = PopulateDataObjects.getPredictorById(Long.parseLong(id), s);
	    		dirName += predictor.getUserName() + "/PREDICTORS/";
	    		dirName += predictor.getName() + "/";
	    		
	    		//add file names here...
	    		if(file.equalsIgnoreCase("externalPredictionsAsCSV")){
	    			WriteCsv.writeExternalPredictionsAsCSV(Long.parseLong(id));
	    			fileName = predictor.getName() + "-external-set-predictions.csv";
	    		}
	    	}
	    	else if(jobType.equalsIgnoreCase(Constants.PREDICTION)){
	    		Prediction prediction = PopulateDataObjects.getPredictionById(Long.parseLong(id), s);
	    		dirName += prediction.getUserName() + "/PREDICTIONS/";
	    		dirName += prediction.getName() + "/";
	    		
	    		if(file.equalsIgnoreCase("predictionAsCSV")){
	    			WriteCsv.writePredictionValuesAsCSV(Long.parseLong(id));
	    			fileName = prediction.getName() + "-prediction-values.csv";
	    		}
	    	}
	    	s.close();
	    	
	    	//Now we know what file to send the user. Send it!
	    	
	        // Prepare streams
	    	File filePath=new File(dirName+fileName);
	        
			BufferedInputStream input=null;
	        if(filePath.exists()){
	        	FileInputStream fis=new FileInputStream(filePath);
	        	input=new BufferedInputStream(fis);
	        	response.setContentType("application/zip");
	            int contentLength=input.available();
	            response.setContentLength(contentLength);
	            response.setHeader("Content-Disposition", "inline; filename="+fileName);
	            BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
	            while (contentLength-- > 0) {
	                output.write(input.read());
	            }
	            output.close();
	            fis.close();
				filePath.delete();
	        }else{
	        	logger.warn("Bad filepath: " + dirName + fileName);
	        	PrintWriter writer=response.getWriter();
	        	writer.write("An error occured, can not download the project file.");
	        }
	    }
	    catch(Exception ex){
	    	logger.error(ex);
	    }
	}
}