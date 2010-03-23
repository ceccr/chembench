package edu.unc.ceccr.action.RegisterActions;


import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.AdminSettings;

//Called whenever the Admin page loads.

public class UpdateAdminSettingsAction extends Action {

	
	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward();
		forward = mapping.findForward("admin");
		
		HttpSession session = request.getSession(false); 
		if (session != null ){
			
			//Changing user acceptance type
			String setting=(String)request.getParameter("userAcceptance");
			if(setting!=null){
				Utility.writeToDebug("Changing user acceptance setting to " + setting);
				Constants.ACCEPTANCE=setting;
				
				
				//get existing objects
				List<AdminSettings> ls = new LinkedList<AdminSettings>();
				
				Session s = HibernateUtil.getSession();
				try{
					Transaction tx = s.beginTransaction();
					ls.addAll(s.createCriteria(AdminSettings.class).add(Expression.eq("type", "userAcceptanceMode")).list());
					tx.commit();
				}catch(Exception ex){
					Utility.writeToDebug(ex);
					forward = mapping.findForward("failure");
				}
				
				if(ls.size() != 0){
					//already exists, so update it
					
					try{
						Iterator iter  = ls.iterator();
						AdminSettings userAcceptanceSetting = (AdminSettings) iter.next();
						userAcceptanceSetting.setValue(setting);
						
						Transaction tx = s.beginTransaction();
						s.saveOrUpdate(userAcceptanceSetting);
						tx.commit();
					}catch(Exception ex){
						Utility.writeToDebug(ex);
						forward = mapping.findForward("failure");
					}
				}
				else{
					//doesn't exist yet, so create it
					AdminSettings userAcceptanceSetting = new AdminSettings();
					userAcceptanceSetting.setType("userAcceptanceMode");
					userAcceptanceSetting.setValue(setting);
					
					try{
						Transaction tx = s.beginTransaction();
						s.saveOrUpdate(userAcceptanceSetting);
						tx.commit();
					}catch(Exception ex){
						Utility.writeToDebug(ex);
						forward = mapping.findForward("failure");
					}
				}
				
			}
			
			//Changing modeling limits
			setting=(String)request.getParameter("numCompounds");
			if(setting!=null){
				Utility.writeToDebug("Changing modeling limits to " + request.getParameter("numCompounds") + " " + request.getParameter("numModels"));
				Constants.MAXMODELS=Integer.parseInt(request.getParameter("numModels"));
				Constants.MAXCOMPOUNDS=Integer.parseInt(request.getParameter("numCompounds"));

				//get existing objects
				List<AdminSettings> ls = new LinkedList<AdminSettings>();
				
				Session s = HibernateUtil.getSession();
				try{
					Transaction tx = s.beginTransaction();
					ls.addAll(s.createCriteria(AdminSettings.class).add(Expression.eq("type", "maxCompounds")).list());
					ls.addAll(s.createCriteria(AdminSettings.class).add(Expression.eq("type", "maxModels")).list());
					tx.commit();
				}catch(Exception ex){
					Utility.writeToDebug(ex);
					forward = mapping.findForward("failure");
				}
				
				//check if we already have a numModels or numCompounds object
				if(ls.size() > 0){
					//we got maxCompounds and maxModels from the DB, so use those
					
					try{
						Iterator iter  = ls.iterator();
						AdminSettings maxCompoundsSetting = (AdminSettings) iter.next();
						AdminSettings maxModelsSetting = (AdminSettings) iter.next();
						
						maxCompoundsSetting.setValue(request.getParameter("numCompounds"));
						maxModelsSetting.setValue(request.getParameter("numModels"));
						
						Transaction tx = s.beginTransaction();
						s.saveOrUpdate(maxCompoundsSetting);
						s.saveOrUpdate(maxModelsSetting);
						tx.commit();
						
					}catch(Exception ex){
						Utility.writeToDebug(ex);
						forward = mapping.findForward("failure");
					}
					
				}
				else{
					//create them
					
					AdminSettings maxCompoundsSetting = new AdminSettings();
					maxCompoundsSetting.setType("maxCompounds");
					maxCompoundsSetting.setValue(request.getParameter("numCompounds"));
					
					AdminSettings maxModelsSetting = new AdminSettings();
					maxModelsSetting.setType("maxModels");
					maxModelsSetting.setValue(request.getParameter("numModels"));
					
					try{
						Transaction tx = s.beginTransaction();
						s.saveOrUpdate(maxCompoundsSetting);
						s.saveOrUpdate(maxModelsSetting);
						tx.commit();
					}catch(Exception ex){
						Utility.writeToDebug(ex);
						forward = mapping.findForward("failure");
					}
				}
				
				
				
			}
		}
		else{
			forward = mapping.findForward("failure");
		}
		
		return forward;
	}
}
			