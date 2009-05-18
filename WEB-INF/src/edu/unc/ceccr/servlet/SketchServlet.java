package edu.unc.ceccr.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.Generate3DMolWorkflow;
import edu.unc.ceccr.global.Constants;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SketchServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Utility.writeToDebug("doing a get, yo.");
		//this servlet returns 3D rotatable images when a user clicks on the 2D molecule from a prediction or model.
		
		String project = request.getParameter("project");
		String id = request.getParameter("compoundId");
		String userName = request.getParameter("user");
        String datasetID = request.getParameter("datasetID");
	
        Utility.writeToDebug("called 3d servlet. project: " + project + " compound: " + id + " user: " + userName + " datasetID: " + datasetID);
        
		DataSet ds = null;
		try{ 
			ds = PopulateDataObjects.getDataSetById(Long.parseLong(datasetID));
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		userName = ds.getUserName();
		if(userName.equalsIgnoreCase("_all")){
			userName = "all-users";
		}
		Utility.writeToDebug("Running SketchServlet.", userName, project);
		String workingDir = Constants.CECCR_USER_BASE_PATH + userName+ "/DATASETS/" + ds.getFileName() + "/Visualization/Structures/";
		String sdf = id + ".sdf";
		String mol3D = id + "_3D.mol";
		
		String sdfPath = workingDir + sdf;

		String urlBaseDir = "/BASE/" + userName+ "/DATASETS/" + ds.getFileName() + "/Visualization/Structures/";
		
		Utility.writeToDebug("Generating 3D structure for file : " + workingDir + sdf);
		
		String title = "<html><title>" + id
				+ " 3D view</title><head></head><body bgcolor='black' ><div align='center'><font color='white' size='3'> Compound ID = "
				+ id + "</div></font>";

		String front = "<script LANGUAGE='JavaScript1.1' SRC='/jchem/marvin/marvin.js'></script><script LANGUAGE='JavaScript1.1'>"
				+ "mview_begin('/jchem/marvin/', 350, 350);";

		String parameter = "mview_param('mol'," + "'" + urlBaseDir + mol3D + "'" + ");";


		
		String end = "</script><applet codebase='/jchem/marvin/' archive='jmarvin.jar' code='JMView' height='350' width='350'></applet></body></html>";

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		try {
			File sdfile = new File(sdfPath);
			String warning = "ERROR, the SD file is not in correct format.\rThe structure can not be displayed.";
			if (sdfile.exists()) {
				InputStream twoDis = new FileInputStream(sdfile);
				if (DatasetFileOperations.sdfIsValid(twoDis)) {
					Utility.writeToMSDebug("VALID>>>>"+sdfile.getName());
					
					Utility.writeToDebug("Running 2d->3d convert");
					Generate3DMolWorkflow.Convert2Dto3D(userName, project, sdf, mol3D, workingDir);
					Utility.writeToDebug("2d->3d convert done");

					out.println(title);
					out.println(front);
					out.println(parameter);
					out.println(end);
					
				} else {
					out.println(warning);
				}

			} else {
				out.println(" ERROR : Can not find SD file: " + id);
			}
		} catch (Exception e) {
			Utility.writeToDebug(e);
		} finally {
			out.close();
		}
		
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Utility.writeToDebug("doing a post, yo.");
		doGet(request, response);
		Utility.writeToDebug("done wit da post, yo.");
	}

}
