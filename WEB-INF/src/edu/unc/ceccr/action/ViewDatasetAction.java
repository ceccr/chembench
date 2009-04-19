package edu.unc.ceccr.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.Utility;


public class ViewDatasetAction extends Action {

	ActionForward forward;
	

	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");

		
		HttpSession session = request.getSession(false);
		
		try {

		
				String user = ((User) session.getAttribute("user"))
						.getUserName();
				String fileName = null; 
				if(request==null && (String)session.getAttribute("fileName")=="")
					return mapping.findForward("failure");
				
				else if(request.getParameter("fileName")==null){
					Utility.writeToMSDebug("..................."+(String) session.getAttribute("fileName"));
					fileName = (String) session.getAttribute("fileName");
				}
				else{
					Utility.writeToMSDebug("...............**...."+request.getParameter("fileName"));
					fileName = request.getParameter("fileName");
				}
				DataSet ds = Utility.getDataSetByFileAndUserName(fileName, user);
							
			session.setAttribute("ds", ds);
			Utility.writeToMSDebug("ViewDatasetAction:::"+"descriptorMatrixServlet?user="+ds.getUserName()+"&project="+ds.getFileName()+"&name="+ds.getActFile());
			session.setAttribute("actFile", "descriptorMatrixServlet?user="+ds.getUserName()+"&project="+ds.getFileName()+"&name="+ds.getActFile());
			session.setAttribute("viz_path", "descriptorMatrixServlet?user="+ds.getUserName()+"&project="+ds.getFileName()+"/Visualization&name="+ds.getSdfFile().replace(".sdf", ""));
			
			

		} catch (Exception e) {
			Utility.writeToDebug(e);
		}

		return forward;

	}

}
