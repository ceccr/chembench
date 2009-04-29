package edu.unc.ceccr.workflows;

import java.io.IOException;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.Utility;

public class CSV_X_Workflow {
	
	private String file_path;
	private String viz_path;
	
	public CSV_X_Workflow(String userName, String datasetName, String sdfName) throws IOException{
		this.viz_path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/" +datasetName+"/Visualization/"+sdfName;
		this.file_path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/" +datasetName+"/"+sdfName;
		
	}
	
	public void performMACCSCreation(){
		try{
			Process p= Runtime.getRuntime().exec("moebatch_shell_script.sh "+file_path+".sdf "+viz_path+".maccs");
			Utility.writeToMSDebug("Shell script: "+"moebatch_shell_script.sh "+file_path+".sdf "+viz_path+".maccs");
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
			String tanimoto =  "/usr/local/ceccr/installs/R-2.8.1/bin/R --slave --vanilla  --args \""+
			viz_path+".x\" \"" +viz_path+"_tan.mat\" \""+viz_path+"_tan.xml\" \"e\""+ 
			"< /usr/local/ceccr/mmlsoft/perl/heatmap_script.R /usr/local/ceccr/mmlsoft/perl/out.txt";
			String mahalanobis =  "/usr/local/ceccr/installs/R-2.8.1/bin/R --slave --vanilla  --args \""+
			viz_path+".x\" \"" +viz_path+"_mah.mat\" \""+viz_path+"_mah.xml\" \"m\""+ 
			"< /usr/local/ceccr/mmlsoft/perl/heatmap_script.R /usr/local/ceccr/mmlsoft/perl/out.txt";
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
			Utility.writeToDebug(ex);
			Utility.writeToMSDebug("performCSVCreation::"+ex.getMessage());
		}
	}
	
	public void performPCAcreation(){
		try{
			//Process p = Runtime.getRuntime().exec("convert_x_to_csv.pl "+viz_path+".x "+viz_path+".csv");
			//Utility.writeToMSDebug("CVS script: "+"convert_x_to_csv.pl "+viz_path+".x "+viz_path+".csv");
			//Utility.writeProgramLogfile(viz_path, "convert_x_to_csv",  p.getInputStream(), p.getErrorStream());
			//p.waitFor();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
			Utility.writeToMSDebug("performCSVCreation::"+ex.getMessage());
		}
	}
}
