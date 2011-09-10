package edu.unc.ceccr.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;
import edu.unc.ceccr.workflows.visualization.Molecule3D;
import edu.unc.ceccr.global.Constants;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

@SuppressWarnings("serial")
public class Compound3DServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		//this servlet returns 3D rotatable images when a user clicks on the 2D molecule from a prediction or model.
		
		String id = request.getParameter("compoundId");
		String userName = request.getParameter("user");
        String datasetName = request.getParameter("datasetName");
	
		Utility.writeToDebug("Running SketchServlet.", userName, datasetName);
		String workingDir = Constants.CECCR_USER_BASE_PATH + userName+ "/DATASETS/" + datasetName + "/Visualization/Structures/";
		String sdf = id + ".sdf";
		String mol3D = id + "_3D.mol";
		
		String sdfPath = workingDir + sdf;

		String urlBaseDir = "/BASE/" + userName+ "/DATASETS/" + datasetName + "/Visualization/Structures/";
		
		String title = "<html><title>" + id
				+ " 3D view</title><head></head><body bgcolor='black' ><div align='center'><font color='white' size='3'> Compound ID = "
				+ id + "</div></font>";

		String front = "<script LANGUAGE='JavaScript1.1' SRC='jchem/marvin/marvin.js'></script><script LANGUAGE='JavaScript1.1'>"
				+ "mview_begin('/jchem/marvin/', 350, 350);";

		String parameter = "mview_param('mol'," + "'" + urlBaseDir + mol3D + "'" + ");";

		String end = "</script><applet codebase='/jchem/marvin/' archive='jmarvin.jar' code='JMView' height='350' width='350'></applet></body></html>";

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		Utility.writeToDebug("Getting 3D structure for file : " + workingDir + sdf);
		
		try {
			File sdfile = new File(sdfPath);
			String warning = "ERROR, the SD file is not in correct format.\rThe structure can not be displayed.";
			if (sdfile.exists()) {
				InputStream twoDis = new FileInputStream(sdfile);
				
				File mol3DFile = new File(workingDir + mol3D);
				if(! mol3DFile.exists()){
					Molecule3D.Convert2Dto3D(userName, datasetName, sdf, mol3D, workingDir);
				}
				else{
					Utility.writeToDebug("3D structure already calculated. Returning it.");
				}

				out.println(title);
				out.println(front);
				out.println(parameter);
				out.println(end);
					
				
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
		//Far as I can tell, this is never actually called anywhere.
		Utility.writeToDebug("doing a post, yo.");
		doGet(request, response);
		Utility.writeToDebug("done wit da post, yo.");
	}

}
