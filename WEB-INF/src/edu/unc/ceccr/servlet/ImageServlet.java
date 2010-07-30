
package edu.unc.ceccr.servlet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    	
    	//this servlet returns 2D images for compounds, 
    	//external validation charts, and dataset activity charts
    	//the code is really awful, my only excuse is 
    	//"it was like that when I got here and there's no time to fix it now"
    	
        //Note that some of these may be null, depending on the request type!
        String project = request.getParameter("project"); //predictor or prediction name
        String datasetName = request.getParameter("datasetName");
        String compoundId = request.getParameter("compoundId");
        String userName = request.getParameter("user");
        String projectType = request.getParameter("projectType"); //dataset, modeling, prediction
		
      
        
        //You might be thinking of adding a call to the database to get the 
        //dataset object here. Don't do it! It'll slow shit down and bleed sessions
        //because this code is called once PER IMAGE on a page.
        
		if(userName.equalsIgnoreCase("_all")){
			userName = "all-users";
		}
        String imageFileName;
        if(compoundId.startsWith("activityChart")){
    		//activity chart for dataset 
    		imageFileName=userName+"/DATASETS/"+project+"/Visualization/activityChart.png";
      	}
        else if(compoundId.startsWith("externalValidationChart")){
    		//ext validation chart for modeling
    		imageFileName=userName+"/PREDICTORS/"+project+"/mychart.jpeg";
        }
    	else if(projectType.equals("dataset")){
    		imageFileName=userName+"/DATASETS/"+project+"/Visualization/Sketches/"+compoundId+".jpg";
    	}
        else{
        	//modeling and prediction images
    		imageFileName=userName+"/DATASETS/"+datasetName+"/Visualization/Sketches/"+compoundId+".jpg";
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