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
public class DescriptorMatrixServlet extends HttpServlet{
	//serves up files for use with the dataset visualization Flash app
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
	    	
	        String project = request.getParameter("project");
	        String name=request.getParameter("name");
	        String userName=request.getParameter("user");
	         
	        File matFile = new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+project+"/"+name);

	        BufferedInputStream input = null;
	        BufferedOutputStream output = null;

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
