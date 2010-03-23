package edu.unc.ceccr.action.RegisterActions;


import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.SoftwareExpiration;
import edu.unc.ceccr.utilities.Utility;

//Called whenever the Admin page loads.

public class Administration extends Action {

	
	@SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward();

		HttpSession session = request.getSession(false); 
		if (session == null ){

			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){

			forward = mapping.findForward("login");
		}
		else{

			String style="<td class='ChangePSText'>";
			
			//Check if user acceptance was changed (manual or automatic)

			//load form data
			 ArrayList<LabelValueBean> month=new ArrayList<LabelValueBean>();
			
			 ArrayList<LabelValueBean> date=new ArrayList<LabelValueBean>();

			 for(int i=1;i<13;i++)
			 {
				 LabelValueBean monthBean=new LabelValueBean(""+i,""+i);
			   
				 month.add(monthBean);
			 }
			 
			 for(int j=1;j<32;j++)
			 {
				 LabelValueBean dateBean=new LabelValueBean(""+j,""+j);
				
				 date.add(dateBean);
			 }
			session.removeAttribute("month"); 
			
			session.setAttribute("month",month);
			
			session.removeAttribute("date");
			
			session.setAttribute("date",date);

			List<SoftwareExpiration> list=new ArrayList<SoftwareExpiration>();
			
			SoftwareExpiration temp=new SoftwareExpiration();

			String block="<tr>"+style+"</td></tr>";
					
			Session s = HibernateUtil.getSession();// query
			
			Transaction tx = null;
			
			try {
				
				tx = s.beginTransaction();
				
				list=s.createCriteria(SoftwareExpiration.class).list();
				
				tx.commit();
			} catch (RuntimeException e) {
				
				if (tx != null)
					
					tx.rollback();
				
				Utility.writeToDebug(e);		
				forward = mapping.findForward("failure");
				
			} finally {
				
				s.close(); 
				}
			if(list!=null)
			{		
				Iterator it=list.iterator();
				
				//block=block+"<tr>"+style+"&nbsp<br/></td></tr>";
				
				while(it.hasNext())
				{
					temp=(SoftwareExpiration)(it.next());
					
					block=block+"<tr>"+style+"<b>"+temp.getName()+"</b> will expire in "+"<font color='red'>"
					
					+Utility.checkExpiration(temp.getYear(),temp.getMonth(),temp.getDate())+"</font>  days.&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
					
					+"<a class='small' href='getSoftwareDates.do?id="+temp.getId()+"' onclick='return confirmation2()'><u><b>remove</b></u></a></td></tr>";
				}
				session.removeAttribute("block");
				
				session.setAttribute("block",block);
			}
			
			session.removeAttribute("queuedTasks");
			
			forward = mapping.findForward("admin");
		}
		return forward;
	}
	
	
}