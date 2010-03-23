package edu.unc.ceccr.workflows;

import java.io.File;
import java.io.IOException;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class CSV_X_Workflow {
	
	public static void performXCreation(String viz_path){
		
		try{
			Process p = Runtime.getRuntime().exec("convert_maccs_to_X2.pl "+viz_path+".maccs "+viz_path+".x");
			Utility.writeProgramLogfile(viz_path, "convert_maccs_to_X2",  p.getInputStream(), p.getErrorStream());
			p.waitFor();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public static void performCSVCreation(String viz_path){
		try{
			Process p = Runtime.getRuntime().exec("convert_x_to_csv.pl "+viz_path+".x "+viz_path+".csv");
			Utility.writeProgramLogfile(viz_path, "convert_x_to_csv",  p.getInputStream(), p.getErrorStream());
			p.waitFor();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}

	public static void performHeatMapAndTreeCreation(String viz_path, String method){
		//assumes that a .x file generated from MACCS descriptors exists in the directory already
		//method = "tanimoto" or "mahalanobis"
		try{
			String tanimoto = "run_heatmap_tree.sh "+ viz_path+".x " +viz_path+"_tan.mat "+ viz_path+"_tan.xml " +"e"; 
			String mahalanobis = "run_heatmap_tree.sh "+ viz_path+".x " +viz_path+"_mah.mat "+ viz_path+"_mah.xml " +"m";
			Process p;
			if(method.equals("tanimoto")){
				p = Runtime.getRuntime().exec(tanimoto);
				Utility.writeToDebug("Heatmap script: "+tanimoto);
				Utility.writeProgramLogfile(viz_path, "run_heatmap_tree.sh", p.getInputStream(), p.getErrorStream());
				p.waitFor();
			}
			else if(method.equals("mahalanobis")){
				p = Runtime.getRuntime().exec(mahalanobis);
				Utility.writeToDebug("Heatmap script: "+mahalanobis);
				Utility.writeProgramLogfile(viz_path, "run_heatmap_tree.sh", p.getInputStream(), p.getErrorStream());
				p.waitFor();
			}
			else return;
			
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public static void performPCAcreation(String viz_path, String act_path){
		try{
			if(act_path!=null && !act_path.isEmpty()){
				Process p = Runtime.getRuntime().exec("run_PCA_ScatterPlot.sh /usr/local/ceccr/installs/MCR/v78 "+ viz_path+".x "+ act_path);
				Utility.writeToDebug("run_PCA_ScatterPlot.sh ::act =" + "run_PCA_ScatterPlot.sh /usr/local/ceccr/installs/MCR/v78 "+ viz_path+".x "+ act_path);
				Utility.writeProgramLogfile(viz_path, "PCA",  p.getInputStream(), p.getErrorStream());
				p.waitFor();
				File old = new File(viz_path+".png");
				File new_ =  new File(viz_path+".jpg");
			}
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
}
