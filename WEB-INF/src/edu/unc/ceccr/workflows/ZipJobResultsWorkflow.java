
package edu.unc.ceccr.workflows;

import java.io.*;
import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.global.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.*;

public class ZipJobResultsWorkflow{
	
	public static void ZipKnnResults(String userName, String jobName, String zipFile, String jobType) throws Exception{
		Utility.writeToDebug("Creating archive of project: " + jobName);
	    // These are the files to include in the ZIP file
		if(userName.equals(Constants.ALL_USERS_USERNAME)){
			userName = "all-users";
		}
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
		//get subdirectories (needed for predictions now, grr)
		ArrayList<String> stringList = new ArrayList<String>();
		stringList.addAll(Arrays.asList(filenames));
		while(filenames != null && x<filenames.length){
			File subdir = new File(projectDir + filenames[x]);
			if(subdir.isDirectory()){
				String[] subdir_files = subdir.list();
				for(int i = 0; i < subdir_files.length; i++){
					stringList.add(filenames[x] + "/" + subdir_files[i]);
				}
			}
		}
		
		filenames = (String[]) stringList.toArray();
		x = 0;
		while(filenames != null && x<filenames.length){
			if(
					(//files matching this stuff can contain descriptor values
					 //if we let people download that, we get sued X_X
					filenames[x].matches(".*mz") || filenames[x].matches(".*dragon") || 
					filenames[x].matches(".*moe") || filenames[x].matches(".*maccs") ||
					filenames[x].matches(".*S") || filenames[x].matches(".*x") || 
					filenames[x].matches("RAND_sets.*[0-9]+") || filenames[x].matches(".*x_r") || 
					filenames[x].matches("Rand_sets_[a-zA-Z]+.[0-9]+") ||
					new File(projectDir + filenames[x]).isDirectory() //don't want dirs either
					)
					&& 
					( //the mod and pred files are fine -- those are regular kNN outputs and are needed
					!filenames[x].matches(".*mod") &&
					!filenames[x].matches(".*pred") 
					)
					)
			{
				//don't include these files (do nothing)
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