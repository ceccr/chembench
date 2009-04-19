package edu.unc.ceccr.servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
public class ApplicationServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
       
       String appName = request.getParameter("exe");
       String fileDir=Constants.EXECUTABLEFILE_PATH+"chemb/bin/";
       if (appName != null) {
          appName = appName.replaceAll("\\.+(\\\\|/)", "");
       } else {
          response.sendRedirect("/jap/error.jsp");
         return;
       }

       BufferedInputStream input=null;

        try {
        	
        	File filePath=new File(fileDir+appName);
        	FileInputStream fis=new FileInputStream(filePath);

        	input=new BufferedInputStream(fis);
            int contentLength=input.available();
            response.setContentLength(contentLength);
            response.setContentType("application/x-msword");
            response.setHeader(
                "Content-disposition", "attachment; filename=\"" + appName + "\"");
            BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());

            // Write file contents to response.
            while (contentLength-- > 0) {
                output.write(input.read());
            }

            output.flush();
        } catch (IOException e) {
        	Utility.writeToDebug(e);
        } finally { 
        	input.close(); }
        }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException
    {
    	doGet(request,response);
    }
}
    
    
 
