
package edu.unc.ceccr.workflows;

import java.io.*;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.zip.*;

public class ZipDatasetsWorkflow{
	
	public static void ZipDatasets(String userName, String datasetName, String zipFile) throws Exception{
		Utility.writeToDebug("Creating archive of dataset: " + datasetName);
	    // These are the files to include in the ZIP file
		String projectDir = Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + datasetName + "/";
		//ArrayList<String> filesForZip = new ArrayList<String>();
		File file = new File(projectDir);
		File[] files = file.listFiles();
		if(files == null){
			Utility.writeToDebug("Error reading directory: " + projectDir);
		}
		if(!new File(zipFile).exists()){
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
			byte[] buf = new byte[1024];
				
			int x = 0;
				while(files != null && x<files.length){
					
						//add the file into the zip
						try{
							Utility.writeToMSDebug(files[x].getName());
							FileInputStream in = new FileInputStream(projectDir + files[x].getName());
							out.putNextEntry(new ZipEntry(files[x].getName()));
							int len;
				            while ((len = in.read(buf)) > 0) {
				                out.write(buf, 0, len);
				            }
				            out.closeEntry();
				            in.close();
						}
						catch(Exception ex){
							Utility.writeToDebug(ex);
						}
				
					x++;
				}
				out.close();
			}
		}
	
}