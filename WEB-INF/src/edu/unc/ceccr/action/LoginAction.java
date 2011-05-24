package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.ErrorMessages;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.CreateDatasetTask;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class LoginAction extends ActionSupport{
	public String execute() throws Exception {
		String result = SUCCESS; 

		String debugText = "";
		if(Constants.doneReadingConfigFile)
		{
			debugText ="already read config file (?)";
		}
		else{
			try{
				HttpServletRequest hrequest = ServletActionContext.getRequest();
				ActionContext context = ActionContext.getContext();
				
				String path = RequestUtils.getServletPath(hrequest);

				debugText = "path: " + path;
				//=getServlet().getServletContext().getRealPath("WEB-INF/systemConfig.xml");
				Utility.setAdminConfiguration(path);
			}
			catch(Exception ex){
				debugText += ex.getMessage();
			}
		}
		FileAndDirOperations.writeStringToFile(debugText, "/usr/local/ceccr/deploy/debug-log.txt");
		
		return result;
	}
}