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
import org.hibernate.criterion.Order;

import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Model;
import edu.unc.ceccr.persistence.ModelInterface;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.Utility;

/**
 * The File servlet for serving from absolute path.
 * @author balusc@xs4all.nl
 * @see http://balusc.xs4all.nl/srv/dev-jep-fil.html
 */
public class ModelServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
       String modelName = request.getParameter("modelName");
       String fileName = request.getParameter("modelName")+".txt";
        String userName=request.getParameter("user");
      //  Long predId=Long.parseLong(request.getParameter("predId"));
      //  String predictor=request.getParameter("predictor");

     
       if (fileName != null) {
            //Strip "../" and "..\" (avoid directory sniffing by hackers!).
          fileName = fileName.replaceAll("\\.+(\\\\|/)", "");
       } else {
          
          response.sendRedirect("/jsp/main/error.jsp");
         return;
       }

        String contentType = URLConnection.guessContentTypeFromName(fileName);

     
          if (contentType == null) {
          contentType = "application/octet-stream";
       }

   
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
        	String content="";
        	try {
				content= buildContent(modelName, userName);
			} catch (ClassNotFoundException e) {
				Utility.writeToDebug(e);
			} catch (SQLException e) {
				Utility.writeToDebug(e);
			}
        	
        	StringBuffer stringBuffer = new StringBuffer(content);
        	ByteArrayInputStream bais = new ByteArrayInputStream(stringBuffer.toString().getBytes("UTF-8"));
        	
        	input = new BufferedInputStream(bais);
            int contentLength = input.available();

           
            response.setContentLength(contentLength);
            response.setContentType("text/html");
            response.setHeader(
                "Content-disposition", "attachment; filename=\"" + fileName + "\"");
            output = new BufferedOutputStream(response.getOutputStream());

          
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
    
 
    
public String buildContent(String modelName,String userName)throws ClassNotFoundException, SQLException
{
	String title="",body="";

	Predictor predictor=null;
	List<ModelInterface> models=null;
	
	predictor = getPredictor(modelName);
	models=getModels(predictor);
	
   String newline=System.getProperty("line.separator");
	
	title=title+newline+newline+"C-ChemBench Models Output "+newline
	+"========================================================="+newline
	+"User Name                :  "+userName+newline
	+"Model Name              :  "+predictor.getName()+newline
	+"Modeling Type           :  "+predictor.getModelMethod()+newline
	+"Descriptor Generation  :  "+predictor.getDescriptorGeneration()+newline
	+"ACT File                    :  "+predictor.getActFileName()+newline
	+"SD File                      :  "+predictor.getSdFileName()+newline
	+"Build Date                 :  "+predictor.getDateCreated()+newline
	+"Download Date         :  "+new Date()+newline
	+"Web Site                   :  http:// ceccr.ibiblio.org"+newline
	+"========================================================="+newline+newline
	+"Model #      "+"nnn      "+"q-square      "+"n       "+"r       "+"r-square       "+"Ro1-square       "+"Ro2-square       "+"k1       "+"k2 "+newline
	+"________________________________________________________________________  "+newline;
	Iterator it=models.iterator();
	Model md=null;
	int i=1;
	while(it.hasNext())
	{
		md=(Model)it.next();
	 body=body+i+++"        "+md.getNnn()+"    "+md.getQ_squared()+"    "+md.getN()+"    "+md.getR()+"    "+md.getR_squared()+"    "+
	 md.getR01_squared()+"    "+md.getR02_squared()+"    "+md.getK1()+"    "+md.getK2()+newline;
		
	}
	return (title+body);
	
}
    
    
    
protected static Predictor getPredictor( String modelname)
throws ClassNotFoundException, SQLException {
	
Predictor predictor = null;
Session session = HibernateUtil.getSession();
Transaction tx = null;
try {
tx = session.beginTransaction();
predictor = (Predictor) session.createCriteria(Predictor.class)
		.add(Expression.eq("name", modelname))
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

@SuppressWarnings("unchecked")
protected static List<ModelInterface> getModels(Predictor pred)
throws ClassNotFoundException, SQLException {

List<ModelInterface> models = null;
Session session = HibernateUtil.getSession();
Transaction tx = null;
try {
tx = session.beginTransaction();
models = session.createCriteria(Model.class).add(Expression.eq("predictor", pred)).addOrder(Order.asc("RSquared")).list();

tx.commit();
} catch (RuntimeException e) {
if (tx != null)
	tx.rollback();
Utility.writeToDebug(e);
} finally {
session.close();
}

return models;
}


}

