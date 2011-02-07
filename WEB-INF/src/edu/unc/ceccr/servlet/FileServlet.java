package edu.unc.ceccr.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import edu.unc.ceccr.workflows.ZipJobResultsWorkflow;

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
			
	    	String dirName = Constants.CECCR_USER_BASE_PATH;
	    	String fileName = "";
	    	Session s = HibernateUtil.getSession();
	    	if(jobType.equalsIgnoreCase(Constants.DATASET)){
	    		DataSet dataset = PopulateDataObjects.getDataSetById(Long.parseLong(id), s);
	    		dirName += dataset.getUserName() + "/";
	    		dirName += dataset.getFileName() + "/";
	    		
	    		//add file names here...
	    	}
	    	else if(jobType.equalsIgnoreCase(Constants.MODELING)){
	    		Predictor predictor = PopulateDataObjects.getPredictorById(Long.parseLong(id), s);
	    		dirName += predictor.getUserName() + "/";
	    		dirName += predictor.getName() + "/";
	    		
	    		//add file names here...
	    	}
	    	else if(jobType.equalsIgnoreCase(Constants.PREDICTION)){
	    		Prediction prediction = PopulateDataObjects.getPredictionById(Long.parseLong(id), s);
	    		dirName += prediction.getUserName() + "/";
	    		dirName += prediction.getJobName() + "/";
	    		
	    		if(file.equalsIgnoreCase("predictionsAsCSV")){
	    			WriteDownloadableFilesWorkflow.writePredictionValuesAsCSV(Long.parseLong(id));
	    			fileName = prediction.getJobName() + "-prediction-values.csv";
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
	        	PrintWriter writer=response.getWriter();
	        	writer.write("An ERROR occured, can not download the project file.");
	        }
	    }
	    catch(Exception ex){
	    	Utility.writeToDebug(ex);
	    }
	}
}