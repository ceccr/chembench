package edu.unc.ceccr.action;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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
import org.hibernate.Session;

import edu.unc.ceccr.action.ViewDataset.Compound;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.SoftwareLink;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class SoftwareLinksAction extends ActionSupport {

	private ArrayList<SoftwareLink> softwareLinks = new ArrayList<SoftwareLink>();
	
	public ArrayList<SoftwareLink> getSoftwareLinks() {
		return softwareLinks;
	}
	public void setSoftwareLinks(ArrayList<SoftwareLink> softwareLinks) {
		this.softwareLinks = softwareLinks;
	}

	public String loadPage() throws Exception {

		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("FreeSoftwareAction: No ActionContext available");
		}
		else{
			Session s = HibernateUtil.getSession();
			softwareLinks = (ArrayList<SoftwareLink>) PopulateDataObjects.populateSoftwareLinks(s);
		}
		
		return result;
	}
	
	public String addSoftware() throws Exception {

		String result = SUCCESS;
		
		//check that the user is logged in
		ActionContext context = ActionContext.getContext();
		
		if(context == null){
			Utility.writeToStrutsDebug("FreeSoftwareAction: No ActionContext available");
		}
		else{
			
		}
		
		return result;
	}
}