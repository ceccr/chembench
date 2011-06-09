

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
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;

public class TooltipActions extends ActionSupport{
	/*
	 Tooltips are JSPs which may contain calculated values.
	 This class populates any variables that will be read by a tooltip JSP.
	 */
	
	public String loadExternalValidationChartTooltip() throws Exception {
		//get the observed and predicted values of the compound
		ActionContext context = ActionContext.getContext();
		
		compoundId = ((String[]) context.getParameters().get("compoundId"))[0];
		compoundPredictedValue = ((String[]) context.getParameters().get("predictedValue"))[0];
		compoundObservedValue = ((String[]) context.getParameters().get("observedValue"))[0];
		
		return SUCCESS;
	}
	
	//member variables, with getters and setters
	public String compoundId = "Ur Butt";
	public String compoundPredictedValue = "3";
	public String compoundObservedValue = "2";
	
	public String getCompoundId() {
		return compoundId;
	}
	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}
	
	public String getCompoundPredictedValue() {
		return compoundPredictedValue;
	}
	public void setCompoundPredictedValue(String compoundPredictedValue) {
		this.compoundPredictedValue = compoundPredictedValue;
	}
	
	public String getCompoundObservedValue() {
		return compoundObservedValue;
	}
	public void setCompoundObservedValue(String compoundObservedValue) {
		this.compoundObservedValue = compoundObservedValue;
	}	
}