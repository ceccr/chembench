package edu.unc.ceccr.workflows;

import java.io.File;
import java.io.IOException;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.Utility;

public class CSV_X_Workflow {
	
	private String file_path;
	private String viz_path;
	private String act_path;
	
	public CSV_X_Workflow(String userName, String datasetName, String sdfName, String actPath) throws IOException{
		this.viz_path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/" +datasetName+"/Visualization/"+sdfName.substring(0,sdfName.lastIndexOf("."));
		this.file_path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/" +datasetName+"/"+sdfName;
		this.act_path  = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/" +datasetName+"/"+actPath;
		
	}
	
	public void performMACCSCreation(){
		try{
			Process p= Runtime.getRuntime().exec("moebatch_shell_script.sh "+file_path +" "+viz_path+".maccs");
			Utility.writeToMSDebug("Shell script: "+"moebatch_shell_script.sh "+file_path+" "+viz_path+".maccs");
			Utility.writeProgramLogfile(file_path, "moebatch_shell_script",  p.getInputStream(), p.getErrorStream());
			p.waitFor();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
			Utility.writeToMSDebug("performMACCSCreation::"+ex.getMessage());
		}
	}
	
	public void performXCreation(){
		
		try{
			Process p = Runtime.getRuntime().exec("convert_maccs_to_X2.pl "+viz_path+".maccs "+viz_path+".x");
			Utility.writeToMSDebug("X2 script: "+"convert_maccs_to_X2.pl "+viz_path+".maccs "+viz_path+".x");
			Utility.writeProgramLogfile(viz_path, "convert_maccs_to_X2",  p.getInputStream(), p.getErrorStream());
			p.waitFor();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
			Utility.writeToMSDebug("performXCreation::"+ex.getMessage());
		}
	}
	
	public void performCSVCreation(){
		try{
			Process p = Runtime.getRuntime().exec("convert_x_to_csv.pl "+viz_path+".x "+viz_path+".csv");
			Utility.writeToMSDebug("CVS script: "+"convert_x_to_csv.pl "+viz_path+".x "+viz_path+".csv");
			Utility.writeProgramLogfile(viz_path, "convert_x_to_csv",  p.getInputStream(), p.getErrorStream());
			p.waitFor();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
			Utility.writeToMSDebug("performCSVCreation::"+ex.getMessage());
		}
	}
	/**
	 * 
	 * @param method - could be "tanimoto" or "mahalanobis" for now
	 */
	public void performHeatMapAndTreeCreation(String method){
		try{
			String tanimoto =  "run_heatmap_tree.sh "+ viz_path+".x " +viz_path+"_tan.mat "+ viz_path+"_tan.xml " +"e"; 
			String mahalanobis =  "run_heatmap_tree.sh "+ viz_path+".x " +viz_path+"_mah.mat "+ viz_path+"_mah.xml " +"m";
			Process p;
			if(method.equals("tanimoto")){
				p = Runtime.getRuntime().exec(tanimoto);
				Utility.writeToMSDebug("Heatmap script: "+tanimoto);
				Utility.writeProgramLogfile(viz_path, "convert_x_to_csv",  p.getInputStream(), p.getErrorStream());
				p.waitFor();
			}
			else if(method.equals("mahalanobis")){
				p = Runtime.getRuntime().exec(mahalanobis);
				Utility.writeToMSDebug("Heatmap script: "+mahalanobis);
				Utility.writeProgramLogfile(viz_path, "convert_x_to_csv",  p.getInputStream(), p.getErrorStream());
				p.waitFor();
			}
			else return;
			
		}catch(Exception ex){
			//Utility.writeToDebug(ex); screw it, this has been getting a "file not found" error for months
			Utility.writeToMSDebug("performCSVCreation::"+ex.getMessage());
		}
	}
	
	public void performPCAcreation(){
		try{
			if(act_path!=null && !act_path.isEmpty()){
				Process p = Runtime.getRuntime().exec("run_PCA_ScatterPlot.sh /usr/local/ceccr/installs/MCR/v78 "+ viz_path+".x "+ act_path);
				Utility.writeToMSDebug("run_PCA_ScatterPlot.sh ::act =" + "run_PCA_ScatterPlot.sh /usr/local/ceccr/installs/MCR/v78 "+ viz_path+".x "+ act_path);
				Utility.writeProgramLogfile(viz_path, "PCA",  p.getInputStream(), p.getErrorStream());
				p.waitFor();
				File old = new File(viz_path+".png");
				File new_ =  new File(viz_path+".jpg");
				Utility.writeToMSDebug("Rename::"+old.renameTo(new_));
			}
		}catch(Exception ex){
			Utility.writeToDebug(ex);
			Utility.writeToMSDebug("PCACreation::"+ex.getMessage());
		}
	}
}
