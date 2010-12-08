package edu.unc.ceccr.workflows;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.utilities.DatasetFileOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;


public class CreateActivityChartWorkflow {
	
	public final static double MAXIMUM=-1000.0;
	public final static double MINIMUM=1000.0;
	
	public static void createChart(String datasetId) throws Exception {
		//given a datasetId, get the dataset's actFile and make a chart out of it
		//(assumes the dataset is a modeling dataset and that it has an actfile.)
		
		Long datasetID = Long.parseLong(datasetId);
		
		Session s = HibernateUtil.getSession();
		DataSet selectedDataSet = PopulateDataObjects.getDataSetById(datasetID, s);
		s.close();
		
		String fullPath = Constants.CECCR_USER_BASE_PATH;
		
		String userDir;
		userDir = selectedDataSet.getUserName();
		fullPath += userDir + "/DATASETS/" + selectedDataSet.getFileName() + "/" + selectedDataSet.getActFile();
		
		//Utility.writeToDebug("Generating Activity Histogram for Dataset: " + datasetID + " from ACT file: " + fullPath);
		
		HashMap dataMap  = DatasetFileOperations.parseActFile(fullPath);
		
		 IntervalXYDataset dataset =new HistogramDataset();
		 
		 dataset=createDataset(dataMap);
		 
		 final JFreeChart chart = ChartFactory.createHistogram("Activity Histogram", "Range","Frequency", dataset, PlotOrientation.VERTICAL,false, false, false);
		
		 chart.setBackgroundPaint(Color.gray);
		 chart.getTitle().setPaint(Color.black); 
		 TextTitle tt = new TextTitle("Chembench", new Font("Dialog", Font.PLAIN, 11));
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
		 
		String outputFileStr = Constants.CECCR_USER_BASE_PATH + userDir + "/DATASETS/" + selectedDataSet.getFileName() + "/Visualization/activityChart.png";
		 ChartUtilities.saveChartAsPNG(new File(outputFileStr), chart, 550, 550);
		
		
	}

	public static HistogramDataset createDataset(HashMap map) {
		   double[] values;  
		   double min,max;
		   
		   values=getValues(map);	
		   min=getMinimum(values);
		   max=getMaximum(values);
		   
		   final HistogramDataset dataset = new HistogramDataset();
		   dataset.addSeries(0,values,10,min,max);
		   
		   return dataset;
	 }

	public static double[] getValues(HashMap map)  {
		   
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
	
	public static double getMinimum(double[] values)
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
	
	public static double getMaximum(double[] values)
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

