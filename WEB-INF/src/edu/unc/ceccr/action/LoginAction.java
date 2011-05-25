package edu.unc.ceccr.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//struts2
import com.opensymphony.xwork2.ActionSupport; 
import com.opensymphony.xwork2.ActionContext; 

import org.apache.struts.upload.FormFile;
import org.apache.struts2.interceptor.ServletResponseAware;
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

public class LoginAction extends ActionSupport implements ServletResponseAware {

	protected HttpServletResponse servletResponse;
	@Override
	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}
	
	// define username and password
	String username;
	String password;

	public String execute() throws Exception {
		String result = SUCCESS; 

		String debugText = "";
		if(Constants.doneReadingConfigFile)
		{
			debugText ="already read config file (?)";
		}
		else{
			try{
				//STATIC PATH OH NOES
				String path = "/usr/local/ceccr/tomcat6/webapps/ROOT/WEB-INF/systemConfig.xml";
				Utility.setAdminConfiguration(path);
			}
			catch(Exception ex){
				debugText += ex.getMessage();
			}
		}
		FileAndDirOperations.writeStringToFile(debugText, "/usr/local/ceccr/deploy/debug-log.txt");
		
		//start up the queues, if they're not running yet
		CentralDogma.getInstance();
		

		//check username and password
		ActionContext context = ActionContext.getContext();
		
		Session s = HibernateUtil.getSession();
		User user = PopulateDataObjects.getUserByUserName(username, s);
		s.close();
		
		if(user!= null){
			String realPasswordHash =user.getPassword();
			
			if (Utility.encrypt(password).equals(realPasswordHash)){
				context.getSession().put("user", user);
				Cookie ckie=new Cookie("login","true");
				servletResponse.addCookie(ckie);
				
				Utility.writeToUsageLog("Logged in", user.getUserName());
			}
			else if(user.getUserName().equals("guest")){
				Utility.writeToUsageLog("Logged in", user.getUserName());
				context.getSession().put("user", user);
				Cookie ckie=new Cookie("login","true");
				servletResponse.addCookie(ckie);
				
				Utility.writeToUsageLog("Logged in", user.getUserName());
			}
			else{
				result=LOGIN;
			}
		}
		
		return result;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}