package edu.unc.ceccr.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.io.ByteArrayInputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
public class FileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        String fileName = request.getParameter("name");
        String userName=request.getParameter("user");
        Long predId=Long.parseLong(request.getParameter("predId"));
        String predictor=request.getParameter("predictor");

       if (fileName != null) {
            // Strip "../" and "..\" (avoid directory sniffing by hackers!).
            fileName = fileName.replaceAll("\\.+(\\\\|/)", "");
        } else {
          
            response.sendRedirect("/jsp/main/error.jsp");
            return;
        }

      
        String contentType = URLConnection.guessContentTypeFromName(fileName);

      
            if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
        	String content="";
        	try {
				content= buildMainContent(userName,predId,predictor);
			} catch (ClassNotFoundException e) {
				Utility.writeToDebug(e);
			} catch (SQLException e) {
				Utility.writeToDebug(e);
			}
        	
        	StringBuffer stringBuffer = new StringBuffer(content);
        	ByteArrayInputStream bais = new ByteArrayInputStream(stringBuffer.toString().getBytes("UTF-8"));
        	
        	input = new BufferedInputStream(bais);
            int contentLength = input.available();

            // Init servlet response.
            response.setContentLength(contentLength);
            response.setContentType(contentType);
            response.setHeader(
                "Content-disposition", "attachment; filename=\"" + fileName + "\"");
            output = new BufferedOutputStream(response.getOutputStream());

            // Write file contents to response.
            while (contentLength-- > 0) {
                output.write(input.read());
            }

            output.flush();
        } catch (IOException e) {
          
            Utility.writeToDebug(e);
        } finally {
          
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Utility.writeToDebug(e);
                  
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Utility.writeToDebug(e);
                   
                }
            }
        }
    }
    
 
    
public String buildMainContent(String userName, Long predId,String predictor)throws ClassNotFoundException, SQLException
{
	String title="",body="";

	Prediction predictionJob=null;
	List<PredictionValue> predictionValues=null;
	
	predictionJob = getPrediction(predId);
	predictionValues=getMainPredictionValues(predId);
	
   String newline=System.getProperty("line.separator");
	
	title=title+newline+newline+"Chembench Prediction Output "+newline
	+"========================================="+newline
	+"User Name                :  "+userName+newline
	+"Prediction Name       :  "+predictionJob.getJobName()+newline
	+"Predictor   Used        :  "+predictor+newline
	+"Similarity  Cutoff        :  "+predictionJob.getSimilarityCutoff()+newline
	+"Prediction Database :  "+predictionJob.getDatabase()+newline
	+"Predicted Date          :  "+predictionJob.getDateCreated()+newline
	+"Download Date          :  "+new Date()+newline
	+"Web Site                     :  http:// ceccr.ibiblio.org"+newline
	+"========================================="+newline+newline
	+"Compound Name     "+"Standard Deviation     "+"Predicted Value     "+"Number of Models"+newline
	+"______________________________________________________________"+newline;
	Iterator it=predictionValues.iterator();
	PredictionValue pv=null;
	while(it.hasNext())
	{
		pv=(PredictionValue)it.next();
		body=body+pv.getCompoundName()+"                "+pv.getStandardDeviation()+"                 "+pv.getPredictedValue()+"                "+
		pv.getNumModelsUsed()+newline;
		
	}
	return (title+body);
	
}
    
    

    
protected static Prediction getPrediction(Long selectedPredictionId) 
throws ClassNotFoundException, SQLException {

Prediction predictionJob = null;
		
Session session = HibernateUtil.getSession();
Transaction tx = null;
try {
	tx = session.beginTransaction();
	
	predictionJob = (Prediction) session
			.createCriteria(Prediction.class).add(Expression.eq("predictionJobId",selectedPredictionId))
					.uniqueResult();
	
	predictionJob.getPredictedValues().size(); //initialize the 'predictionJob'
	tx.commit();
	
} catch (RuntimeException e) {
	if (tx != null)
		tx.rollback();
	Utility.writeToDebug(e);
} finally {
	session.close();
}

return predictionJob;
}

	@SuppressWarnings("unchecked")
	private List<PredictionValue> getMainPredictionValues(Long selectedPredictionId)
	throws ClassNotFoundException, SQLException {

		List predictionValues = null;

		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			Prediction predictionJob = (Prediction) session.createCriteria(
					Prediction.class).add(Expression.eq("predictionJobId", selectedPredictionId))
					.uniqueResult();
           
			predictionValues = session.createFilter( predictionJob.getPredictedValues(), " order by this.compoundName ASC" )
			                      .list();
		
			tx.commit();

		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		return predictionValues;
	}	
	
	protected static Predictor getPredictor(Long predictorIdUsed)throws ClassNotFoundException, SQLException 
	{

Predictor predictor = null;
Session session = HibernateUtil.getSession();
Transaction tx = null;
try {
	tx = session.beginTransaction();
	predictor = (Predictor) session.createCriteria(
			Predictor.class).add(
			Expression.eq("predictorId", predictorIdUsed))
			.uniqueResult();

	tx.commit();
} catch (RuntimeException e) {
	if (tx != null)
		tx.rollback();
	Utility.writeToDebug(e);
} finally {
	session.close();
}

return predictor;
}

	
}

