package edu.unc.ceccr.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/*
There is a lot of code strewn around the system for doing things like
copying files, copying directories, deleting directories, and so on. 
We need to put all of this in one place. Here's good.
*/

public class FileAndDirOperations {
	
	public static void copyDirContents(String fromDir, String toDir, boolean recurse){
		try{
			File dir = new File(fromDir);
			String files[] = dir.list();
			if(files == null){
				Utility.writeToDebug("Error reading directory: " + fromDir);
			}
			int x = 0;
			while(files != null && x<files.length){
				File xfile = new File(fromDir + "/" + files[x]);
				if(! xfile.isDirectory()){
					FileChannel ic = new FileInputStream(fromDir + "/" + files[x]).getChannel();
					FileChannel oc = new FileOutputStream(toDir + "/" + files[x]).getChannel();
					ic.transferTo(0, ic.size(), oc);
					ic.close();
					oc.close(); 
				}
				else{
					//we hit a subdirectory. Recurse down into it if needed, otherwise ignore it.
					if(recurse){
						File newDir = new File(toDir + "/" + files[x]);
						newDir.mkdir();
						copyDirContents(fromDir + "/" + files[x], toDir + "/" + files[x], true);
					}
				}
				x++;
			}
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	public static void copyFile(String fromPath, String toPath){
		try{
			FileChannel ic = new FileInputStream(fromPath).getChannel();
			FileChannel oc = new FileOutputStream(toPath).getChannel();
			ic.transferTo(0, ic.size(), oc);
			ic.close();
			oc.close();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	public static void moveFile(String fromPath, String toPath){
		
	}
	public static void deleteDirContents(String dirToErase){
		//Removes all files in a directory.
		//For safety reasons, this function is not recursive. 
		//(Don't want anyone to delete the whole filesystem by accident.)
		File dir = new File(dirToErase);
		try{
			String files[] = dir.list();
			if(files != null){
				Utility.writeToDebug("Deleting " + files.length + " files from dir: " + dirToErase);
			}
			else{
				Utility.writeToDebug("Could not open dir: " + dirToErase);
			}
			int x = 0;
			while(files != null && x<files.length){
				if(! (new File(dir + files[x])).isDirectory()){
					(new File(dir + files[x])).delete();
				}
				x++;
			}
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
}