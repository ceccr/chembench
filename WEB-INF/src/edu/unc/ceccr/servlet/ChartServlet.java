
package edu.unc.ceccr.servlet;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.SQLException;

import org.apache.commons.validator.GenericValidator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.BasicStroke;
import java.awt.Stroke;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.chart.*;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.urls.CustomXYURLGenerator;
import org.jfree.chart.labels.CustomXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import edu.unc.ceccr.persistence.*;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

@SuppressWarnings("serial")
public class ChartServlet extends HttpServlet {
	//makes and displays external validation charts for nfold or regular predictors

	protected void processRequest(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException,ClassNotFoundException, SQLException {

		String project=request.getParameter("project");
		String user=request.getParameter("user");
		String currentFoldNumber = "0";
		if(request.getParameter("currentFoldNumber") != null){
			currentFoldNumber=request.getParameter("currentFoldNumber");
		}
		
		Session session = HibernateUtil.getSession();
		Predictor predictor = PopulateDataObjects.getPredictorByName(project, user, session);

		List<ExternalValidation> extValidation;
		List<ExternalValidation> highlightedExtValidation = new ArrayList<ExternalValidation>(); //used to highlight one child of an nfold
		
		ArrayList<Predictor> childPredictors = PopulateDataObjects.getChildPredictors(predictor, session);
		if(childPredictors.size() != 0){
			//get external set for each
			extValidation = new ArrayList<ExternalValidation>();
			for(int i = 0; i < childPredictors.size(); i++){
				Predictor cp = childPredictors.get(i);
				List<ExternalValidation> childExtVals = PopulateDataObjects.getExternalValidationValues(cp, session);
				if(currentFoldNumber.equals("" + (i+1))){
					highlightedExtValidation.addAll(childExtVals);
				}
				else{
					extValidation.addAll(childExtVals);
				}
			}
		}
		else{
			extValidation=PopulateDataObjects.getExternalValidationValues(predictor, session);
		}

		Utility.writeToDebug("extval size: " + extValidation.size());

		int index=0;
		float high,low;
		session.close();
		ExternalValidation extv=null;

		for(int i = 0; i < extValidation.size(); i++){
			if(extValidation.get(i).getNumModels() == 0){
				//no models predicted this point; remove it 
				//so it doesn't skew the chart.
				extValidation.remove(i);
				i--;
			}
		}
		for(int i = 0; i < highlightedExtValidation.size(); i++){
			if(highlightedExtValidation.get(i).getNumModels() == 0){
				//no models predicted this point; remove it 
				//so it doesn't skew the chart.
				highlightedExtValidation.remove(i);
				i--;
			}
		}

		XYSeries series0 = new XYSeries(0,false);
		XYSeries series1 = new XYSeries("");
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		final Stroke stroke = new BasicStroke(0.7f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {3.5f}, 0.0f);
		XYSeriesCollection ds = new XYSeriesCollection();

		HashMap<Integer, String> map = new HashMap<Integer,String>();
		
		List<String> tooltipList = new ArrayList<String>();
		List<XYSeries> pointsList = new ArrayList<XYSeries>();
		List<XYSeries> highlightedPointsList = new ArrayList<XYSeries>();

		Iterator it = extValidation.iterator();
		while(it.hasNext())
		{
			extv=(ExternalValidation)it.next();
			series0.add(extv.getActualValue() ,extv.getPredictedValue());
			map.put(index, extv.getCompoundId());
			if(extv.getNumModels()>3)
			{
				tooltipList.add("Compound ID: "+extv.getCompoundId()+"<br/>"+extv.getPredictedValue()+" &#177; " +extv.getStandDev());
				XYSeries series = new XYSeries("");
				if(GenericValidator.isFloat(extv.getStandDev()))
				{ high=extv.getPredictedValue()+Float.parseFloat(extv.getStandDev());
				low=extv.getPredictedValue()-Float.parseFloat(extv.getStandDev());}
				else{
					high=extv.getPredictedValue();low=extv.getPredictedValue();
				}
				series.add(extv.getActualValue(),high);
				series.add(extv.getActualValue(),low);
				pointsList.add(series);
			}
			else{
				tooltipList.add("Compound ID: "+extv.getCompoundId());
			}
			index++;
		}


		it = highlightedExtValidation.iterator();
		while(it.hasNext())
		{
			extv=(ExternalValidation)it.next();
			series0.add(extv.getActualValue() ,extv.getPredictedValue());
			map.put(index, extv.getCompoundId());
			if(extv.getNumModels()>3)
			{
				tooltipList.add("Compound ID: "+extv.getCompoundId()+"<br/>"+extv.getPredictedValue()+" &#177; " +extv.getStandDev());
				XYSeries series = new XYSeries("");
				if(GenericValidator.isFloat(extv.getStandDev()))
				{ high=extv.getPredictedValue()+Float.parseFloat(extv.getStandDev());
				low=extv.getPredictedValue()-Float.parseFloat(extv.getStandDev());}
				else{
					high=extv.getPredictedValue();low=extv.getPredictedValue();
				}
				series.add(extv.getActualValue(),high);
				series.add(extv.getActualValue(),low);
				highlightedPointsList.add(series);
			}
			else{
				tooltipList.add("Compound ID: "+extv.getCompoundId());
			}
			index++;
		}
		
		double min=setMin(MinRange(extValidation,0),MinRange(extValidation,1));
		double max=setMax(MaxRange(extValidation,0),MaxRange(extValidation,1));

		series1.add(min,min);
		series1.add(max, max);
		ds.addSeries(series0);
		ds.addSeries(series1);

		int i=0;
		Iterator it2=pointsList.iterator();
		while(it2.hasNext())
		{
			ds.addSeries((XYSeries)it2.next());
			renderer.setSeriesLinesVisible(i+2, true);
			renderer.setSeriesShapesVisible(i+2, true);
			if(highlightedPointsList.size() > 0){
				renderer.setSeriesPaint(i+2,Color.DARK_GRAY);
			}
			else{
				renderer.setSeriesPaint(i+2,Color.RED);
			}
			renderer.setSeriesStroke(i+2, stroke);
			renderer.setSeriesItemLabelsVisible(i+2,false);
			renderer.setSeriesShape(i+2, new Rectangle2D.Double(-3.0, -3.0, 8.0, 0.10 ));
			i++;
		}

		CustomXYToolTipGenerator ctg=new CustomXYToolTipGenerator();
		ctg.addToolTipSeries(tooltipList);

		CustomXYURLGenerator cxyg=new CustomXYURLGenerator();
		cxyg.addURLSeries( customizedURLs( ds, map,project,user));

		renderer.setSeriesLinesVisible(0, false);

		if(highlightedPointsList.size() > 0){
			renderer.setSeriesPaint(0,Color.DARK_GRAY);
		}
		else{
			renderer.setSeriesPaint(0,Color.RED);
		}
		renderer.setSeriesItemLabelsVisible(0,true);
		renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
		renderer.setSeriesToolTipGenerator(0,ctg);    

		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(1, false);
		renderer.setSeriesPaint(1,Color.LIGHT_GRAY);

		renderer.setURLGenerator(cxyg);
		renderer.setSeriesToolTipGenerator(0, ctg);

		final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());//

		JFreeChart chart = ChartFactory.createXYLineChart("External Validation Set",    
				"Observed", "Predicted", ds, PlotOrientation.VERTICAL, false,true,true);

		chart.setBackgroundPaint(new Color(0xDA, 0xEC, 0xF8));
		TextTitle tt = new TextTitle(   " http://chembench.mml.unc.edu",  new Font("Dialog", Font.PLAIN, 11));
		tt.setPosition(RectangleEdge.BOTTOM); 
		tt.setHorizontalAlignment(HorizontalAlignment.RIGHT);
		tt.setMargin(0.0, 0.0, 4.0, 4.0);
		chart.addSubtitle(tt);

		final XYPlot plot = chart.getXYPlot();

		plot.setBackgroundPaint(Color.white);
		plot.setForegroundAlpha(0.5f);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setRenderer(renderer);

		final NumberAxis Yaxis =(NumberAxis)plot.getRangeAxis();
		Yaxis.setAutoRange(false);
		Yaxis.setAutoRangeMinimumSize(0.01);
		Yaxis.setRange(min,max);

		final NumberAxis  Xaxis=(NumberAxis)plot.getDomainAxis();
		Xaxis.setAutoRange(false);
		Xaxis.setAutoRangeMinimumSize(0.01);
		Xaxis.setRange(min,max);

		Utility.writeToDebug("Writing external validation chart to file: " + Constants.CECCR_USER_BASE_PATH+user+"/PREDICTORS/"+project+"/mychart.jpeg");
		String basePath=Constants.CECCR_USER_BASE_PATH+user+"/PREDICTORS/"+project+"/";
		if(!currentFoldNumber.equals("0")){
			int numChildren = predictor.getChildIds().split("\\s+").length;
			String childPredName = project + "_fold_" + currentFoldNumber + "_of_" + numChildren;
			basePath += childPredName + "/";
		}
		FileOutputStream  fos_jpg = new FileOutputStream(basePath+"mychart.jpeg"); 
		ChartUtilities.writeChartAsJPEG(fos_jpg, 1.0f, chart, 650, 650, info); 
		fos_jpg.close();
		FileOutputStream fos_cri = new FileOutputStream(basePath+"mychart.map"); 
		PrintWriter pw=new PrintWriter(fos_cri);
		fos_cri.close();
		ChartUtilities.writeImageMap(pw, "mychart", info, true); 

		pw.flush();

		final InputStream input = new BufferedInputStream(new FileInputStream(basePath+"mychart.map"));
		int contentLength = input.available();
		PrintWriter writer=response.getWriter();
		writer.write("<HTML>");
		writer.write("<HEAD><TITLE>Observed  VS Predicted </TITLE><script src='javascript/overlib.js'></script></HEAD>");
		writer.write("<BODY> ");
		while (contentLength-- > 0) {
			writer.write(input.read());
		}

		writer.write("<IMG SRC='imageServlet?project="+project+"&projectType=modeling&user="+user+
				"&compoundId=externalValidationChart&currentFoldNumber="+currentFoldNumber+"' " +
				"WIDTH=\"650\" HEIGHT=\"650\" BORDER=\"0\"  ISMAP=\"ISMAP\" USEMAP=\"#mychart\">");
		writer.write("</center></BODY>");
		writer.write("</HTML>");
		writer.close();


	}
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException{

		try{
			processRequest(request, response);
		}
		catch(SQLException e){
			Utility.writeToDebug(e);
		}
		catch(ClassNotFoundException e){
			Utility.writeToDebug(e);
		}
	}


	protected ArrayList customizedURLs( XYDataset ds, HashMap map,String predictorName, String user) 
	{
		ArrayList<String> list=new ArrayList<String>();
		String url;

		for(int i=0;i<map.size();i++)
		{
			url="javascript: void(window.open('sketch?project="+predictorName+"&projectType=modelbuilder&user="+user+"&compoundId="+map.get(i)+"', 'window"+new java.util.Date().getTime()+"','width=380, height=400'));  ";
			try{
				Thread.sleep(10);
			}catch(InterruptedException e){
				Utility.writeToDebug(e);
			}
			list.add(url);
		}

		return list;
	}

	@SuppressWarnings("unchecked")


	protected double setMax(double max1,double max2)
	{
		if(max1>max2){return max1;}else{return max2;}
	}
	protected double setMin(double min1,double min2)
	{
		if(min1>min2){return min2;}else{return min1;}
	}
	protected double MinRange(List<ExternalValidation> extValidation, int option)
	{
		double min=100.00;
		double extvalue;
		ExternalValidation extv=null;
		Iterator it= extValidation.iterator();

		while(it.hasNext()) {
			extv=( ExternalValidation)it.next();


			if(option==0){
				extvalue=extv.getPredictedValue();
			}
			else{
				extvalue=extv.getActualValue();
			}

			if(min>extvalue) {
				min=extvalue;
			}
		}

		return min-0.5;
	}

	protected double MaxRange(List<ExternalValidation> extValidation, int option)
	{
		double max=-100.00;
		double extvalue;
		ExternalValidation extv=null;
		Iterator it= extValidation.iterator();

		while(it.hasNext())
		{
			extv=( ExternalValidation)it.next();

			if(option==0)
			{
				extvalue=extv.getPredictedValue();
			}else{extvalue=extv.getActualValue();}

			if(max<extvalue) 
			{
				max=extvalue;
			}
		}

		return max+0.5;

	}

}

