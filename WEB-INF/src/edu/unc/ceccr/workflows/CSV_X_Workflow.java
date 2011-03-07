package edu.unc.ceccr.workflows;

import java.io.File;
import java.io.IOException;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.RunExternalProgram;
import edu.unc.ceccr.utilities.Utility;

public class CSV_X_Workflow {
	
	public static void performXCreation(String maccsFileName, String outputXFileName, String workingDir){
		outputXFileName = outputXFileName.substring(0, outputXFileName.lastIndexOf(".sdf"));
		
		String cmd = "convert_maccs_to_X2.pl " + workingDir + maccsFileName + " " + workingDir + outputXFileName;
		RunExternalProgram.runCommandAndLogOutput(cmd, workingDir, "convert_maccs_to_X2");
	}

	public static void performHeatMapAndTreeCreation(String viz_path, String method){
		//assumes that a .x file generated from MACCS descriptors exists in the directory already
		//method = "tanimoto" or "mahalanobis"
		try{
			String tanimoto = "run_heatmap_tree.sh "+ viz_path+".x " +viz_path+"_tan.mat "+ viz_path+"_tan.xml " +"e"; 
			String mahalanobis = "run_heatmap_tree.sh "+ viz_path+".x " +viz_path+"_mah.mat "+ viz_path+"_mah.xml " +"m";
			Process p;
			if(method.equals("tanimoto")){
				RunExternalProgram.runCommandAndLogOutput(tanimoto, "", "tanimoto");
			}
			else if(method.equals("mahalanobis")){
				RunExternalProgram.runCommandAndLogOutput(mahalanobis, "", "mahalanobis");
			}
			else return;
			
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public static void performPCAcreation(String viz_path, String act_path){
		try{
			if(act_path!=null && !act_path.isEmpty()){
				String cmd = "run_PCA_ScatterPlot.sh /usr/local/ceccr/installs/MCR/v78 "+ viz_path+".x "+ act_path;
				RunExternalProgram.runCommandAndLogOutput(cmd, "", "run_PCA_ScatterPlot.sh");
			}
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
}
