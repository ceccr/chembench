package edu.unc.ceccr.action;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.GenericValidator;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class VisualizationAction extends Action {

	public final static double MAXIMUM=-1000.0;
	public final static double MINIMUM=1000.0;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward forward = new ActionForward();
		
		HttpSession session = request.getSession(false);

		ActionErrors errors = new ActionErrors();

		if (session == null) {
			forward = mapping.findForward("login");
		} else if (session.getAttribute("user") == null) {
			forward = mapping.findForward("login");
		} else {
			Utility.writeToDebug("blah blah I am useless haha");
		}
		return null;
	}

	   public HistogramDataset createDataset(HashMap map) {
		   double[] values;  
		   double min,max;
		   
		   values=getValues(map);	
		   min=getMinimum(values);
		   max=getMaximum(values);
		   
		   final HistogramDataset dataset = new HistogramDataset();
		   dataset.addSeries(0,values,10,min,max);
		   
		   return dataset;
	    }

	   public double[] getValues(HashMap map)  {
		   
		   int i=0;
		   
		   Object key,value;
		   
		   double[] temp=new double[map.size()];
		   
		   Iterator it=map.keySet().iterator();
		   
		   while(it.hasNext()) {
			   key=it.next();
			   value=map.get(key);
			   if (GenericValidator.isDouble((String)value))
			   {
				   temp[i]=Double.parseDouble((String)value);
				   i++;
			   }			
		   }
		   
		   double[] values=new double[i];
		   
		   for(int m=0;m<i;m++)
		   {
			   values[m]=temp[m];
		   }
		   return values;
	   }
	   
	   public double getMinimum(double[] values)
	   {
		   double min=MINIMUM;
		  
		   for(int i=0;i<values.length;i++)
		   {
			   if(min>values[i]){
				   min=values[i];
			   }
		   }
		   return min;
	   }
	   
	   public double getMaximum(double[] values)
	   {
		   double max=MAXIMUM;
		   for(int i=0;i<values.length;i++){
			   if(max<values[i]){
				   max=values[i];
			   }
		   }
		   return max;
	   }
	   

}
