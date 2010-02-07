

package edu.unc.ceccr.servlet;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.Color;
import java.awt.Font;

import org.apache.commons.validator.GenericValidator;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.title.TextTitle;

public class ActivityChartServlet extends HttpServlet 
{
	 
	private static final long serialVersionUID = 1L;

	public final static double MAXIMUM=-1000.0;
	
	public final static double MINIMUM=1000.0;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	 {
		 HttpSession session = request.getSession(false);
		 OutputStream out = response.getOutputStream();
		 response.setContentType("image/gif");
		 
		 if(session==null){
			 PrintWriter writer=response.getWriter();
			writer.write("<font color='red'> Sorry, session expired!</font>");
			writer.close();
		 }
		 
		 HashMap dataMap=(HashMap)session.getAttribute("ACTDataSet");
		 session.removeAttribute("ACTDataSet");
		 IntervalXYDataset dataset =new HistogramDataset();
		 dataset=createDataset(dataMap);
		 
		 final JFreeChart chart = ChartFactory.createHistogram("Activity Histogram", "Range","Frequency", dataset, PlotOrientation.VERTICAL,false, false, false);
		
		 chart.setBackgroundPaint(Color.gray);
		 chart.getTitle().setPaint(Color.black); 
		 TextTitle tt = new TextTitle("C-Chembench", new Font("Dialog", Font.PLAIN, 11));
		   tt.setPosition(RectangleEdge.BOTTOM); 
		   tt.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		   tt.setMargin(0.0, 0.0, 4.0, 4.0);
		   chart.addSubtitle(tt);
		 
		   XYPlot plot = (XYPlot) chart.getPlot();
	     		 
		   final NumberAxis Yaxis =(NumberAxis)plot.getRangeAxis();
		   Yaxis.setAutoRange(true);
		   Yaxis.setAutoRangeMinimumSize(3);
		   
		   final NumberAxis domainAxis = new NumberAxis("Range");
		   
		   DecimalFormat format = new DecimalFormat("0.00");
		   
		   domainAxis.setAutoRange(false);
		   domainAxis.setAutoRangeIncludesZero(false);
		   domainAxis.setNumberFormatOverride(format);
		   domainAxis.setAutoRangeMinimumSize(0.1);
		   domainAxis.setRange(getMinimum(getValues(dataMap)), getMaximum(getValues(dataMap)));
		   plot.setDomainAxis(domainAxis);
		 
		 try{ 
		 ChartUtilities.writeChartAsPNG(out, chart, 550, 500);
		 }
		 catch (Exception e) {
		 System.err.println(e.toString());
		 }
		 finally {
		 out.close();
		 }
		
	 }
	
	
	 public void doPost(HttpServletRequest request,  HttpServletResponse response) throws ServletException, IOException 
     { doGet(request, response);}

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

