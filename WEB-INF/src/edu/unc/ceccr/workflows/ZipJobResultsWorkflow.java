
package edu.unc.ceccr.workflows;

import java.io.*;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;
import java.util.zip.*;

public class ZipJobResultsWorkflow{
	
	public static void ZipKnnResults(String userName, String jobName, String zipFile, String jobType) throws Exception{
		Utility.writeToDebug("Creating archive of project: " + jobName);
	    // These are the files to include in the ZIP file
		String projectDir = Constants.CECCR_USER_BASE_PATH + userName + "/" + jobType + "/" + jobName + "/";
		//ArrayList<String> filesForZip = new ArrayList<String>();
		File file = new File(projectDir);
		String[] filenames = file.list();
		if(filenames == null){
			Utility.writeToDebug("Error reading directory: " + projectDir);
		}
		
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		byte[] buf = new byte[1024];
		
		int x = 0;
		while(filenames != null && x<filenames.length){
			
			if(filenames[x].matches(".*S") || filenames[x].matches(".*x") || 
					filenames[x].matches("RAND_sets.*[0-9]+") || filenames[x].matches(".*x_r") || 
					filenames[x].matches(".*mod") || filenames[x].matches("Rand_sets_[a-zA-Z]+.[0-9]+")){
				
				if( filenames[x].matches("Rand_sets_[a-zA-Z]+.[0-9]+")){
					Utility.writeToDebug("I am awesome: " + filenames[x]);
				}
				//these files contain descriptors! 
				//We can't let people download them, or MolconnZ's lawyers will
				//come get us.
			}
			else{
				//add the file into the zip
				try{
					FileInputStream in = new FileInputStream(projectDir + filenames[x]);
					out.putNextEntry(new ZipEntry(filenames[x]));
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
			}
			x++;
		}
		out.close();
	}
	
}