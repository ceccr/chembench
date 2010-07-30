
package edu.unc.ceccr.servlet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    	
    	//this servlet returns 2D images for compounds, external validation charts, and activity charts
    	//the code is really awful, my only excuse is 
    	//"it was like that when I got here and there's no time to fix it now"
    	
        //Note that some of these may be null, depending on the request type!
        String project = request.getParameter("project");
        String compoundId = request.getParameter("compoundId");
        String userName = request.getParameter("user");
        String projectType = request.getParameter("projectType");
        String datasetID = request.getParameter("datasetID");
		
        DataSet ds = null;
        if( !compoundId.startsWith("mychart") && !projectType.equals("dataset")){
	        try{
				Session session = HibernateUtil.getSession();
				ds = PopulateDataObjects.getDataSetById(Long.parseLong(datasetID), session);
				session.close();
	        }
	        catch(Exception ex){
	        	Utility.writeToDebug(ex);
	        }
        }
        
        if(ds != null && ds.getUserName().equalsIgnoreCase("_all")){
        	userName = "all-users";
        }
		if(userName.equalsIgnoreCase("_all")){
			userName = "all-users";
		}
        
        String imageFileName;
        if(compoundId.startsWith("mychart"))
        {
        	if(compoundId.startsWith("mychartActivity")){
        		//activity chart for dataset 
        		imageFileName=userName+"/DATASETS/"+project+"/Visualization/activityChart.png";
        	}
        	else{
        		//ext validation chart for modeling
        		imageFileName=userName+"/PREDICTORS/"+project+"/mychart.jpeg";
        	}
      	}
    	else if(projectType.equalsIgnoreCase("PCA")){
        	imageFileName=userName+"/DATASETS/"+project+"/Visualization/"+compoundId+".jpg";
        }
    	else if(projectType.equals("dataSet")){
    		imageFileName=userName+"/DATASETS/"+project+"/Visualization/Sketches/"+compoundId+".jpg";
    	}
        else{
        	//modeling and prediction images
    		imageFileName=userName+"/DATASETS/"+ds.getFileName()+"/Visualization/Sketches/"+compoundId+".jpg";
    	}
        
        File imageFile = new File(Constants.CECCR_USER_BASE_PATH + imageFileName);

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

}