package edu.unc.ceccr.action;

import java.sql.SQLException;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.persistence.Queue.QueueTask;
import edu.unc.ceccr.persistence.Queue.QueueTask.Component;
import edu.unc.ceccr.utilities.Utility;

public class DeleteUserFile extends Action {

	ActionMapping mapping;

	private ActionForward forward;

	public ActionForward execute(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response) {

		forward = mapping.findForward("success");
		
		HttpSession session = request.getSession(false);
		
		if (session == null) {
			forward = mapping.findForward("login");
		}else if (session.getAttribute("user") == null){
			forward = mapping.findForward("login");
		}else{
			try {
				String userName=request.getParameter("userName");
				
				String fileName=request.getParameter("fileName");
				
				deleteDataset(userName, fileName);
				
				
			} catch (Exception e) {
				forward = mapping.findForward("failure");
				Utility.writeToDebug(e);
			}
		}
		return (forward);

	}
	
	
	
	public void deleteFromDB(String userName, String fileName)throws ClassNotFoundException, SQLException
	{
		Utility.writeToDebug("deleteFromDB: Function not implemented!!!! ", userName, "test");
/*		PredictionDatabase pd=new PredictionDatabase();
		
		String filePath="";
		
		Session session = HibernateUtil.getSession();
		
		Transaction tx1=null, tx2 = null;

		try {
			tx1 = session.beginTransaction();
			pd=(PredictionDatabase)session.createCriteria(PredictionDatabase.class).add(Expression.eq("userName",userName))
			      .add(Expression.eq("databaseName", fileName)).add(Expression.eq("fileComeFrom", Constants.PREDICTION)).uniqueResult();
			
			pd.getPredictionDBSFiles().size();
			
			tx1.commit();
		} catch (RuntimeException e) {
			if (tx1 != null)
				tx1.rollback();
			Utility.writeToDebug(e);
			Utility.writeToDebug("deleteFromDB___435__EXC____ "+e.getMessage(), userName, "test");
		}
	     Iterator it=(pd.getPredictionDBSFiles()).iterator();
	     
	     while(it.hasNext())
	     {
	    	 filePath=((PredictionDatabaseFile)it.next()).getFileLocation();
	    	 Utility.writeToDebug("deleteFromDB____ "+filePath, userName, "test");
	    	 new File(filePath+fileName+".sdf").delete();
	     }
		
		
		try {
			tx2 = session.beginTransaction();
			session.delete(pd);
			tx2.commit();
		} catch (RuntimeException e) {
			if (tx2 != null)
				tx2.rollback();
			Utility.writeToDebug(e);
			Utility.writeToDebug("deleteFromDB_____EXC____ "+e.getMessage(), userName, "test");
		}finally {
						
			session.close();
		}
*/		
	}
	

	public void deleteDataset(String userName, String fileName)throws ClassNotFoundException, SQLException
	{
		DataSet dataSet=new DataSet();
				
		Session session = HibernateUtil.getSession();
		
		Transaction tx=null,tx2=null;
		
		try{
			tx=session.beginTransaction();
			dataSet=(DataSet)session.createCriteria(DataSet.class).add(Expression.eq("userName", userName)).add(Expression.eq("fileName",fileName))
			               .uniqueResult();
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		}
		
		String dir = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+fileName;
		Utility.writeToMSDebug("DEL::"+dir);
				
		//deleting dataset from job
		
		List<QueueTask> tasks = (List<QueueTask>) Queue.getInstance().getUserTasks(userName);
		for(Iterator<QueueTask> i=tasks.iterator();i.hasNext();){
			QueueTask temp = i.next();
			if(temp.component.equals(Component.visualisation) && temp.getJobName().equals(fileName)){
				Queue.getInstance().deleteTask(temp);
			}
			if(temp.component.equals(Component.sketches) && temp.getJobName().equals(fileName+"_sketches_generation")){
				Queue.getInstance().deleteTask(temp);
			}
		}
		///
		Utility.writeToMSDebug("DeleteUserFile::"+dir);
		if(Utility.deleteDir(new File(dir)))
		{
			try{
				tx2=session.beginTransaction();
			    session.delete(dataSet);
			  // session.delete(predDatabase);
			    tx2.commit();
			}catch (RuntimeException e) {
				if (tx2 != null)
					tx2.rollback();
				Utility.writeToDebug(e);
			}
		}
		session.close();
		
	}

}
