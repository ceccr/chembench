package edu.unc.ceccr.action.RegisterActions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.SoftwareExpiration;
import edu.unc.ceccr.formbean.SoftwareExpirationForm;
import org.apache.commons.validator.GenericValidator;
import edu.unc.ceccr.utilities.Utility;

public class UpdateSoftwareAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward(); 
		HttpSession session = request.getSession(false);
		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {
			String id=request.getParameter("id");
			
			if(id!=null)
			{
				SoftwareExpiration se=new SoftwareExpiration();
				Session s = HibernateUtil.getSession();// query
				try{
					se.setId(Integer.parseInt(id));
					
					Transaction tx=null;
					tx=s.beginTransaction();
					s.delete(se);
					tx.commit();
					
				}catch(Exception e)
				{
					forward=mapping.findForward("failure");
				}
				finally{
					s.close();
				}
				forward=mapping.findForward("success");
			}
			else{

				SoftwareExpirationForm  beanForm=(SoftwareExpirationForm)form;
				SoftwareExpiration se=new SoftwareExpiration();
				se.setDate(beanForm.getDate());
				se.setMonth(beanForm.getMonth());
				se.setName(beanForm.getName());
				se.setYear(beanForm.getYear());
	             
				try {
					Session s = HibernateUtil.getSession();// query
					
					
					Transaction tx = null;
					try {
						tx = s.beginTransaction();
						s.save(se);
						tx.commit();
					} catch (RuntimeException e) {
						if (tx != null)
							tx.rollback();
						Utility.writeToDebug(e);				forward = mapping.findForward("failure");
					} finally {
						s.close(); }
					forward=mapping.findForward("success");
				} catch (Exception e) {
					forward = mapping.findForward("failure");
					Utility.writeToDebug(e);
				}
			}
			
		}
		
		
		return forward;
	}
	
	
}
