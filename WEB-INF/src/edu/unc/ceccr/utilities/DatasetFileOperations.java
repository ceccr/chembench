package edu.unc.ceccr.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.validator.GenericValidator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts.upload.FormFile;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.global.ErrorMessages;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;

/*
Functions relating to the processing of incoming dataset files go in here.
We might move this to the Workflows package later, but it's OK here for now.
*/

public class DatasetFileOperations {
	
	public static ArrayList<String> getActFileValues(DataSet dataset) throws Exception {
		ArrayList<String> actFileValues = new ArrayList<String>();

		//find activity file
		String datasetUserName = dataset.getUserName();
		if(datasetUserName.equals("_all")){
			datasetUserName = "all-users";
		}
		String dir = Constants.CECCR_USER_BASE_PATH + datasetUserName + "/DATASETS/"+ dataset.getFileName() + "/";
		String fileName = dir + dataset.getActFile(); 
		
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte[] bytes = new byte[length];
		fis.read(bytes);
		String byteStr = new String(bytes);

		String[] array = byteStr.split("\\s+");

		for (int i = 0; i < array.length; i+=2) {
			actFileValues.add(array[i + 1]);
		}
		
		return actFileValues;
	}

	public static void generateEmptyActFile(String path, String name, String sdfPath) throws IOException {
		File act = new File(path+name+".act");
		act.createNewFile();
		FileOutputStream to = new FileOutputStream(act);
		if(!act.canWrite()){
			Utility.writeToMSDebug("Cannot write to ACT file!!!");
			return;
		}
		
		File file = new File(sdfPath);
		if (file.exists()) {
			FileReader fin = new FileReader(file);
			Scanner src = new Scanner(fin);
			String line;
							
			if(src.hasNext()){
				line = src.nextLine();
				to.write(new String(line.trim()+" "+0+"\n").getBytes());
			}
			while (src.hasNext()) {
				line = src.nextLine();
				if (line.startsWith("$")) {
					if(src.hasNext()){
						line = src.nextLine();
						to.write(new String(line.trim()+" "+0+"\n").getBytes());
					}
				}
			}
		
		}
	}
	
	public static String makeXFromACT(String path, String actFileName) throws Exception{
		//creates an X file with no descriptors using the compound list from the ACT file.
		//Needed in order to use datasplit on modeling sets that are specified by ACT and 
		//SDF files (no X file provided). This is because datasplit only works on X files.
		
		String msg = "";
		String xFileName = actFileName.substring(0,actFileName.lastIndexOf(".")) + ".x";
		File actFile = new File(path + actFileName);
		File xFile = new File(path + xFileName);
		
		BufferedReader fin = new BufferedReader(new FileReader(actFile));
		FileWriter fout = new FileWriter(xFile);
		
		int numActCompounds = getACTCompoundList(path + actFileName).size();
		int numDescriptors = 0;
		fout.write("" + numActCompounds + " " + numDescriptors + "\n");
		String descriptorsLine = "\n";
		fout.write(descriptorsLine);
		
		String line;
		while((line = fin.readLine()) != null){
			String[] array = line.split("\\s+");
			if(array.length == 2){
				fout.write(array[0] + "\n");
			}
		}
		
		return msg;
	}
	
	public static String uploadDataset(String userName, File sdfFile, String sdfFileName, File actFile, 
			String actFileName, File xFile, String xFileName, String datasetName, 
			String actFileType, String datasetType) throws Exception{
		//will take care of the upload SDF, SDF and ACT file, in case of errors will delete the directory 
			
		String path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+datasetName+"/";
		
		Utility.writeToDebug("Creating dataset at " + path);
		
		String msg=""; //holds any error messages from validations
		String formula = "";
		int numCompounds = 0;

		ArrayList<String> act_compounds = null;
		ArrayList<String> sdf_compounds = null;
		ArrayList<String> x_compounds = null;
					
		//create dir
		if(!new File(Constants.CECCR_USER_BASE_PATH+userName).exists())
			new File(Constants.CECCR_USER_BASE_PATH+userName).mkdirs();
		if(!new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS").exists())
			new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS").mkdirs();
		File datasetDir = new File(path);
		datasetDir.mkdirs();
		
		//copy files from temp location into datasets dir
		//run validations on each file after the copy
		if(sdfFile != null){
			Utility.writeToDebug("checking SDF");
			msg += saveSDFFile(userName, sdfFile, path, sdfFileName);
			sdfFile = new File(path + sdfFileName);
			
			sdf_compounds = getSDFCompoundList(sdfFile.getAbsolutePath());
			numCompounds = sdf_compounds.size();
			rewriteSdf(path, sdfFileName);
			
			if(!sdfIsValid(sdfFile))
			{
					msg+=ErrorMessages.INVALID_SDF;
			}
			//Check if SDF file contains duplicates 
			int sdf_duplicate_position = findDuplicates(sdf_compounds);
			if(sdf_duplicate_position!=-1){
				msg += ErrorMessages.SDF_CONTAINS_DUPLICATES + sdf_compounds.get(sdf_duplicate_position);
			}
			Utility.writeToDebug("done checking SDF");
		}
		if(actFile != null){
			Utility.writeToDebug("checking ACT");
			 msg += saveACTFile(actFile, path, actFileName);
			 actFile = new File(path + actFileName);

			 act_compounds = getACTCompoundList(actFile.getAbsolutePath());
			 numCompounds = act_compounds.size();
			 actFile = new File(path + actFileName);
			 formula = rewriteACTFile(path + actFileName);
			 msg += actIsValid(actFile, actFileType);
			 
			 //Check if ACT file contains duplicates 
			 int act_duplicate_position = findDuplicates(act_compounds);
			 if(act_duplicate_position!=-1) msg+= ErrorMessages.ACT_CONTAINS_DUPLICATES + act_compounds.get(act_duplicate_position);
			 Utility.writeToDebug("done checking ACT");
		}
		if(xFile != null){
			 Utility.writeToDebug("checking X");
			 msg += saveXFile(xFile, path, xFileName);
			 xFile = new File(path + xFileName);
			 Utility.writeToDebug("done checking X");
		}

		//generate an empty activity file (needed for... heatmaps or something...?)
		if(actFileType.equals(Constants.PREDICTION)){
			Utility.writeToDebug("Generating empty ACT");
			generateEmptyActFile(path, sdfFileName.substring(0,sdfFileName.lastIndexOf(".")), path+sdfFileName);
		}
		if(actFileType.equals(Constants.PREDICTIONWITHDESCRIPTORS)){
			Utility.writeToDebug("Generating empty ACT");
			generateEmptyActFile(path, xFileName.substring(0,xFileName.lastIndexOf(".")), path+xFileName);
		}

		Utility.writeToDebug("doing compound list validations");
		//more validation: check that the information in the files lines up properly (act, sdf, x should all have the same compounds)
		if(actFile != null && sdfFile != null){
			//Check if SDF matches ACT file 
			int numACT = act_compounds.size();
			int numSDF = sdf_compounds.size();
			
			Utility.writeToMSDebug("Number of compounds in ACT:::"+numACT);
			
			if(numACT!=numSDF) return ErrorMessages.COMPOUND_IDS_DONT_MATCH + " The ACT file contains " + numACT + " compounds; the SDF contains "+numSDF+" compounds!";
			
			// Check if compounds in act are the same as compounds in sdf
			int mismatchIndex = -1;
			for(int i = 0;i<act_compounds.size();i++){
				if(!act_compounds.get(i).equals(sdf_compounds.get(i))){
					mismatchIndex = i;
				}
			}
			
			if(mismatchIndex!=-1) return ErrorMessages.COMPOUND_IDS_DONT_MATCH + " Compound "+ act_compounds.get(mismatchIndex)+" from ACT file does not match compound "+ sdf_compounds.get(mismatchIndex)+" from SDF file!";
			return "";
		}
		
		if (msg.equals("")){
			//success - passed all validations
		}
		else{
			Utility.writeToDebug("Validations failed - deleting");
			//failed validation - completely delete directory of this dataset
			FileAndDirOperations.deleteDirContents(path);
			if(! FileAndDirOperations.deleteDir(new File(path))){
				Utility.writeToDebug("Directory delete failed");
			}
			if((new File(path)).exists()){
				Utility.writeToDebug("Directory still exists");
			}
		}
		return msg;
	}
	
	public static String saveSDFFile(String userName, File sdfFile, String path, String sdfFileName) throws Exception{
		
		String destFilePath = path + sdfFileName;
		FileAndDirOperations.copyFile(sdfFile.getAbsolutePath(), destFilePath);
		return "";
	}
	
	public static void rewriteSdf(String filePath, String fileName) throws Exception {
		
		//SDFs with lines longer than 1023 characters will not work properly with MolconnZ.
		//This function gets rid of all such lines.
		//Does not change the chemical information in the SD file; long lines in SDFs are always comments.
		//MolconnZ is dumb. Creator has C Programmer's Disease.
		
		//This function will also remove the silly /r characters Windows likes
		//to add to newlines.

		File infile = new File(filePath + fileName);
		File outfile = new File(filePath + fileName + ".temp");
		
		//First, run the file through jchem to eliminate anything totally bizarre
		try{
			String execstr = "molconvert sdf " + filePath + fileName + " -o " + filePath + fileName + ".temp";
			Process process = Runtime.getRuntime().exec(execstr);
			Utility.writeProgramLogfile(filePath, "molconvert", process.getInputStream(), process.getErrorStream());
			
			process.waitFor();
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		if(outfile.exists()){
			infile.delete();
			outfile.renameTo(infile);
		}
		
		//now, remove the long lines from the input file
		FileReader fin = new FileReader(infile);
		String temp;
		Scanner src = new Scanner(fin);
		FileWriter fout = new FileWriter(outfile);
		while (src.hasNextLine()) {
			temp = src.nextLine();

			//remove Windows-format \r "newline" characters
			temp = temp.replace('\r', ' ');
			if(temp.length() < 1000){
				fout.write(temp + "\n");
			}
		}
		fin.close();
		fout.close();
		infile.delete();
		outfile.renameTo(infile);
	}
	
	public static String saveACTFile(File actFile, String path, String actFileName) throws IOException{
		
		boolean isXlsFile = actFile.getName().endsWith(".x") || actFile.getName().endsWith(".xl") || actFile.getName().endsWith(".xls");
		String act_file = actFile.getName();
		
		String destFilePath = path + actFileName;
		FileAndDirOperations.copyFile(actFile.getAbsolutePath(), destFilePath);
		if(isXlsFile){
			XLStoACT(path, actFile.getName().substring(0,actFile.getName().indexOf("."))+".act", actFile.getName());
			new File(path +actFile.getName()).delete();
		}
			
		return "";
	}
	
	public static String getActFileHeader(String filePath) throws Exception{
		File file = new File(filePath);
		FileReader fin = new FileReader(file);

		Scanner src = new Scanner(fin);
		String header = src.nextLine();

		fin.close();
		return header;
	}
	
	public static String rewriteACTFile(String filePath)
	throws FileNotFoundException, IOException {
		//removes the \r things (stupid Windows)
		File file = new File(filePath);
		if (file.exists()) {
			FileReader fin = new FileReader(file);

			Scanner src = new Scanner(fin);
			StringBuilder sb = new StringBuilder();
			String header = src.nextLine();
			sb.append(header + "\n");
			
			String temp;
			while (src.hasNext()) {
				temp = src.nextLine();
				sb.append(temp + "\n");
			}
			fin.close();

			FileWriter fout = new FileWriter(filePath);
			fout.write(sb.toString());
			fout.close();
			return "";
		}
		else {
			return "File does not exist: " + filePath;
		}

	}

	public static String saveXFile(File xFile, String path, String xFileName) throws IOException{
		
		String destFilePath = path + xFileName;
		FileAndDirOperations.copyFile(xFile.getAbsolutePath(), destFilePath);
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> readFileToVector(String delimiter, String path) throws Exception {
    	
    	Vector<Vector<String>> result = new Vector<Vector<String>>();
        
    	try{
	    	BigFile bf = new BigFile(path);
	    	for(Iterator i = bf.iterator();i.hasNext();){
	    		Vector<String> temp = new Vector<String>();
	    		String[] s = ((String)i.next()).split(delimiter);
	            for (int j = 0; j < s.length; j++) {
	                  temp.add(s[j]);
	              }  
	            result.add(temp);
	    	}
    	}
    	catch(Exception e){
    			Utility.writeToMSDebug(e.getMessage());
    	}
    	
       return result;         

    }
	
	public static ArrayList<String> getXCompoundList(String fileLocation) throws Exception{
		ArrayList<String> x_compounds = new ArrayList<String>();
		File file = new File(fileLocation);
		
		if (file.exists()) {
			FileReader fin = new FileReader(file);
			Scanner src = new Scanner(fin);
			String line;
			while (src.hasNext()) {
				line = src.nextLine();
				String[] array = line.split("\\s+");
				if (array.length != 2 && array.length != 0) { //this will skip the first line and any blank lines
					x_compounds.add(array[0].trim());
				}
			}
		}
		Collections.sort(x_compounds);
		return x_compounds;
	}
	

	public static ArrayList<String> getSDFCompoundList(String sdfPath) throws Exception{
		
		File infile = new File(sdfPath);
		FileReader fin = new FileReader(infile);
		BufferedReader br = new BufferedReader(fin);
		ArrayList<String> chemicalNames = new ArrayList<String>();
		
		String line;
		//skip any whitespace lines before the first molecule
		while((line = br.readLine()) != null && line.trim().isEmpty()){ }
		//read first molecule
		if(line != null){
			chemicalNames.add(line.trim().replace(" ", "_"));
		}
		//read subsequent molecules
		while((line = br.readLine()) != null){
			if(line.startsWith("$$$$")){
				//skip any whitespace lines before the next molecule
				while((line = br.readLine()) != null && line.trim().isEmpty()){ }
				//read next molecule
				if(line != null && !line.trim().isEmpty()){
					chemicalNames.add(line.trim().replace(" ", "_"));
				}
			}
		}
		Collections.sort(chemicalNames);
		return chemicalNames;
	}
	
	
	public static ArrayList<String> getACTCompoundList(String fileLocation)
			throws FileNotFoundException, IOException {
		ArrayList<String> act_compounds = new ArrayList<String>();
		File file = new File(fileLocation);
		
		if (file.exists()) {
			FileReader fin = new FileReader(file);
			Scanner src = new Scanner(fin);
			String line;
			while (src.hasNext()) {
				line = src.nextLine();
				String[] array = line.split("\\s+");
				if (array.length == 2) {
					if (GenericValidator.isDouble(array[1])) {
						act_compounds.add(array[0].trim());
					}
				}
			}
		}
		Collections.sort(act_compounds);
		return act_compounds;
	}

	// returns the position of the duplicate
	private static int findDuplicates(ArrayList<String> a){
		
		ArrayList<String> temp_list = new ArrayList<String>();
		for(int i= 0;i < a.size();i++){
			if(temp_list.contains(a.get(i))) return i; 
			temp_list.add(a.get(i));			
		}
		return -1;
	}
	
	public static HashMap parseActFile(String fileName)
			throws FileNotFoundException, IOException {
		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte[] bytes = new byte[length];
		fis.read(bytes);
		String byteStr = new String(bytes);

		String[] array = byteStr.split("\\s+");
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < array.length; i++) {
			map.put(array[i], array[i + 1]);
			i++;
		}
		return map;
	}
	
	public static void XLStoACT(String filePath, String actFileName,
			String xlsFileName) {
		String fullPath = filePath + xlsFileName;
		File file = new File(fullPath);
		StringBuilder sb = new StringBuilder();

		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row;
			HSSFCell cell1, cell2;

			int rows = sheet.getPhysicalNumberOfRows();
			for (int r = 0; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					cell1 = row.getCell((short) 0);
					cell2 = row.getCell((short) 1);
					if (cell1.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						sb.append((int) cell1.getNumericCellValue());
					} else {
						sb.append(cell1.getRichStringCellValue());
					}

					if (cell2.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						sb.append(" " + cell2.getNumericCellValue() + "\n");
					} else {
						sb.append(" " + cell2.getRichStringCellValue() + "\n");
					}
				}
			}

			FileWriter fout = new FileWriter(filePath + actFileName);
			fout.write(sb.toString());
			fout.close();

		} catch (Exception ioe) {
			Utility.writeToDebug(ioe);
		}
	}
	
	
	public static boolean sdfIsValid(File sdfFile)
		throws IOException {
		if(! sdfFile.exists()){
			return false;
		}
		/*
		Scanner s = new Scanner(sdfFileStream);
		int size, index = 1;
		String line;
		String[] lineArray;
		//for each line...
		while (s.hasNextLine()) {
			Scanner ss = new Scanner(s.nextLine());
			if (ss.hasNext()) {
				line = ss.nextLine(); //pull the line into a string
				lineArray = (line.trim()).split("\\s+"); //tokenize the string... why did he do it this way?
				size = lineArray.length; //size = number of tokens in it
		
				if (index > 3) { //the first three lines of the sdf can be anything, so skip till we're on line 4
					if (!lineArray[0].startsWith("M")) {
						//"M END" signifies the end of the molecule
						if (size != 7 && size != 16) { //Anything specifying a molecule will have 7 or 16 tokens in a line
							ss.close();
							return false;
						}
					} else { //this is just stupid... it only reads the first molecule?! 
						//fuck it, I'm commenting this whole function out, this thing is worthless. Rewrite it later.
						ss.close();
						return true;
					}
				}
				index++;
			}
		}
		s.close();
		*/
		return true;
	}


	public static String actIsValid(File actFile, String actFileType)
			throws IOException {
		//really not much of a validator, but better than nothing.
		//checks that 
		if (! actFile.exists()) {
			return ErrorMessages.ACT_NOT_VALID;
		}
		BufferedReader br = new BufferedReader(new FileReader(actFile));
		String line;
		String[] lineArray;
		int size, index = 1;
		while((line = br.readLine()) != null){
			lineArray = (line.trim()).split("\\s+");
			size = lineArray.length;
			//first line will have header info on it
			if (index > 1) {
				//skip blank lines
				//make sure each line has 2 things on it
				if (size == 2 && GenericValidator.isFloat(lineArray[1]) || size == 0) {
					//good so far
				} 
				else {
					//bad line found
					return ErrorMessages.ACT_NOT_VALID;
				}

				if (actFileType.equalsIgnoreCase(Constants.CATEGORY)) {
					if (GenericValidator.isInt(lineArray[1])) {
					} else {
						return ErrorMessages.ACT_DOESNT_MATCH_PROJECT_TYPE;
					}
				}
			}
			index++;
		}
		
		return "";
	}
	
}