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

	private static ArrayList<String> act_compounds;
	private static ArrayList<String> sdf_compounds;
	
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
		Utility.writeToMSDebug("!!!");
		if(!act.canWrite()){
			Utility.writeToMSDebug("Cannot write to ACT file!!!");
			return;
		}
		
		File file = new File(sdfPath);
		Utility.writeToMSDebug("!!!"+sdfPath);
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
	
	
	public static String uploadDataset(String userName, File sdfFile, File actFile, File xFile, String datasetName, String description, String type) throws Exception{
		//will take care of the upload SDF, SDF and ACT file, in case of errors will delete the directory 
			
		String path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+datasetName+"/";
		
		Utility.writeToDebug("Creating dataset at " + path);
		
		String msg="";
		String formula = "";
					
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
			msg += saveSDFFile(userName, sdfFile, path);
			rewriteSdf(path, sdfFile.getName());
			
			if(!sdfIsValid(sdfFile.getInputStream()))
			{
					msg+=ErrorMessages.INVALID_SDF;
			}
			//Check if SDF file contains duplicates 
			int sdf_duplicate_position = findDuplicates(false);
			if(sdf_duplicate_position!=-1) msg+= ErrorMessages.SDF_CONTAINS_DUPLICATES + act_compounds.get(sdf_duplicate_position);
		}
		if(actFile != null){
			 msg += saveACTFile(actFile, path);
			 formula = rewriteACTFile(path + actFile.getName());
			 
			 if(!actIsValid(actFile.getInputStream()))
			 {
				msg=ErrorMessages.ACT_NOT_VALID;
			 }
			 else if(!actMatchProjectType(actFile.getInputStream(), knnType))
			 {
				msg=ErrorMessages.ACT_DOESNT_MATCH_PROJECT_TYPE;
			 }
			 //Check if ACT file contains duplicates 
			 int act_duplicate_position = findDuplicates(true);
			 if(act_duplicate_position!=-1) msg+= ErrorMessages.ACT_CONTAINS_DUPLICATES + act_compounds.get(act_duplicate_position);
				
		}
		if(xFile != null){
			 msg += saveXFile(actFile, path);
		}

		//generate an empty activity file (needed for... heatmaps or something...?)
		if(type.equals(Constants.PREDICTION)){
			generateEmptyActFile(path, sdfFile.getName().substring(0,sdfFile.getName().lastIndexOf(".")), path+sdfFile.getName());
		}
		if(type.equals(Constants.PREDICTIONWITHDESCRIPTORS)){
			generateEmptyActFile(path, xFile.getName().substring(0,xFile.getName().lastIndexOf(".")), path+xFile.getName());
		}
		
		//more validation: check that the information in the files lines up properly (act, sdf, x should all have the same compounds)
		if(actFile != null && sdfFile != null){
			//Check if SDF matches ACT file 
			String sdf_act_match = sdfMatchesAct(sdfFile, actFile, userName, datasetName);
			if(!sdf_act_match.equals("-1"))
			{	
				msg+=sdf_act_match;
			}
		}
		
		if (msg == ""){
			//success - passed all validations
			Utility.writeToMSDebug("File saved, formula="+formula);
			writeDatasetToDatabase(userName, datasetName, sdfFile.getName(), actFile!=null?actFile.getName():sdfFile.getName().substring(0,sdfFile.getName().lastIndexOf("."))+".act", type, description, formula);
		}
		else{
			//failed validation - completely delete directory of this dataset
			FileAndDirOperations.deleteDirContents(path);
			FileAndDirOperations.deleteDir(new File(path));
		}
		return msg;
	}
	
	public static String saveSDFFile(String userName, File sdfFile, String path) throws Exception{
		Utility.writeToMSDebug("saveSDFFile");

		if(!sdfFile.getName().toLowerCase().endsWith(".sdf")) return ErrorMessages.SDF_FILE_EXTENSION_INVALID;
		
		String destFilePath = path+sdfFile.getName();
		FileAndDirOperations.copyFile(sdfFile.getAbsolutePath(), destFilePath);
		
		return "";
	}
	
	public static void rewriteSdf(String filePath, String fileName) {
		
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
			
			infile.delete();
			outfile.renameTo(infile);
		}
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		
		//now, remove the long lines from the input file
		try{
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
		catch(Exception ex){
			Utility.writeToDebug(ex);
		}
	}
	
	public static String saveACTFile(File actFile, String path) throws IOException{
		
		boolean isXlsFile = actFile.getName().endsWith(".x") || actFile.getName().endsWith(".xl") || actFile.getName().endsWith(".xls");
		String act_file = actFile.getName();
		
		boolean isActFile = act_file.toLowerCase().endsWith(".act");		
		if(!isXlsFile && !isActFile) return ErrorMessages.ACT_FILE_EXTENSION_INVALID; 
		Utility.writeToMSDebug("saveACTFile");
		String destFilePath = path+actFile.getName();
		FileAndDirOperations.copyFile(actFile.getAbsolutePath(), destFilePath);
		if(isXlsFile){
			XLStoACT(path, actFile.getName().substring(0,actFile.getName().indexOf("."))+".act", actFile.getName());
			new File(path +actFile.getName()).delete();
		}
			
		return "";
	}
	

	public static String rewriteACTFile(String filePath)
	throws FileNotFoundException, IOException {
		//removes the \r things (stupid Windows)
		//and extracts the ACT file header so it can be stored in the DB.
		File file = new File(filePath);
		String fileType = " ";
		if (file.exists()) {
			FileReader fin = new FileReader(file);

			Scanner src = new Scanner(fin);
			StringBuilder sb = new StringBuilder();
			String header = src.nextLine();
			String[] type = header.split("\\s+");

			if (GenericValidator.isFloat(type[1])) {
				sb.append(header + "\n");
			} else {
				fileType = type[1];
			}

			String temp;
			while (src.hasNext()) {
				temp = src.nextLine();
				sb.append(temp + "\n");
			}

			FileWriter fout = new FileWriter(filePath);
			fout.write(sb.toString());
			fout.close();
			fin.close();
			return fileType;
		} else {
			return "  ";
		}

	}

	public static String saveXFile(File xFile, String path) throws IOException{
		Utility.writeToMSDebug("saveXFile");

		if(!xFile.getName().toLowerCase().endsWith("x")) return ErrorMessages.X_FILE_EXTENSION_INVALID;
		
		String destFilePath = path+xFile.getName();
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
	
	public static int numCompoundsInActFile(String fileLocation)
			throws FileNotFoundException, IOException {
		int numCompounds = 0;
		act_compounds = new ArrayList<String>();
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
						numCompounds++;
						act_compounds.add(array[0].trim());
						//Utility.writeToMSDebug(".act:::"+array[0].trim());
					}
				}
			}
		}
		return numCompounds;
	}

	// returns the position of the duplicate
	private static int findDuplicates(boolean inACT){
		ArrayList<String> a = new ArrayList<String>();
		if(inACT)
			a = act_compounds;
		else a = sdf_compounds;
		
		ArrayList<String> temp_list = new ArrayList<String>();
		for(int i= 0;i < a.size();i++){
			if(temp_list.contains(a.get(i))) return i; 
			temp_list.add(a.get(i));			
		}
		return -1;
	}
	
	private static int checkCompoundsAreSameInACTAndSDF(){
		for(int i = 0;i<act_compounds.size();i++)
			if(!act_compounds.get(i).equals(sdf_compounds.get(i))){
				return i;
			}
		return -1;
	}
	
	private static void writeDatasetToDatabase(String userName, String name, String sdfFileName, String actFileName,
			String modelType, String description, String formula) throws IOException,
			SQLException, ClassNotFoundException {
		Utility.writeToMSDebug("writeDartasetToDatabase::"+"userName::"+userName+ " name::" + name+" sdfFileName::"+sdfFileName +" actFileName::"+actFileName+
			"modelType::"+modelType+"description::"+description);

		int numCompound = act_compounds.size();
		if(modelType.equals(Constants.PREDICTION)){
			numCompound = sdf_compounds.size();//new Integer(numCompoundsFromsdfFiles(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + name, sdfFileName)).intValue();
		}
		DataSet dataSet = new DataSet();

		dataSet.setFileName(name);

		dataSet.setUserName(userName);

		dataSet.setActFile(actFileName);

		dataSet.setSdfFile(sdfFileName);

		dataSet.setModelType(modelType);

		dataSet.setNumCompound(numCompound);

		dataSet.setCreatedTime(new Date());

		dataSet.setDescription(description);
		
		dataSet.setActFormula(formula);
		
		Session session = HibernateUtil.getSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(dataSet);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

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
	
	/**
	 * This function will return number of compounds in .sdf file. In case if it returns -1 
	 * file contains some duplicated compounds id.
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static int numCompoundsFromsdfFiles(String filePath, String fileName)
			throws FileNotFoundException, IOException {
		File file = new File(filePath +"/"+ fileName);
		FileReader fin = new FileReader(file);
		int num = 0;
		String temp;
		//added compounds id uniqueness checking by msypa, Dec 02, 08
		sdf_compounds = new ArrayList<String>();
		//added compounds id uniqueness checking by msypa, Dec 02, 08
		Scanner src = new Scanner(fin);
		
		if(src.hasNext()){
			temp = src.nextLine();
			sdf_compounds.add(temp.trim());
			//Utility.writeToMSDebug(temp);
		}
		while (src.hasNext()) {
			temp = src.nextLine();
			if (temp.startsWith("$")) {
				num++;
				if(src.hasNext()){
					temp = src.nextLine();
					sdf_compounds.add(temp.trim());
				}
			}
		}
		fin.close();
		return num;
	}
	
	public static ArrayList<String> getChemicalNamesFromSdf(String sdfPath) throws Exception{
		
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
		return chemicalNames;
	}
	
	public static String sdfMatchesAct(FormFile sdfFile, FormFile actFile, String user, String datasetName)
	throws IOException {
			
		String userDir = Constants.CECCR_USER_BASE_PATH + user + "/DATASETS/"+datasetName;
			
		// Checking if number of compounds in ACT is the same as in SDF file
		int numACT = numCompoundsInActFile(userDir+"/"+ actFile.getFileName());
		int numSDF = numCompoundsFromsdfFiles(userDir, sdfFile.getFileName());
		
		Utility.writeToMSDebug("Number of compounds in ACT:::"+numACT);
		
		if(numACT!=numSDF) return ErrorMessages.ACT_DOESNT_MATCH_SDF+ "ACT contains " + numACT+ " vs. "+numSDF+" in SDF file!";
		//* Checking if number of compounds in ACT is the same as in SDF file
		
		// Check if compounds in act are the same as compounds in sdf
		int compound_position = checkCompoundsAreSameInACTAndSDF();
		if(compound_position!=-1) return ErrorMessages.ACT_DOESNT_MATCH_SDF + " Compound "+ act_compounds.get(compound_position)+" from ACT file does not match compound "+ sdf_compounds.get(compound_position)+" from SDF file!";
		//* Check if compounds in act are the same as compounds in sdf
		return "-1";
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


	public static boolean actIsValid(InputStream actFileStream)
			throws IOException {
		if (actFileStream.available() < 0) {
			return false;
		}
		Scanner s = new Scanner(actFileStream);
		int size, index = 1;
		String line;
		String[] lineArray;
		while (s.hasNextLine()) {
			Scanner ss = new Scanner(s.nextLine());
			if (ss.hasNext()) {
				line = ss.nextLine();
				lineArray = (line.trim()).split("\\s+");
				size = lineArray.length;

				if (index > 1) {
					if (size == 2 && GenericValidator.isFloat(lineArray[1])) {
					} else {
						ss.close();
						return false;
					}
				}
				index++;
			}
		}
		s.close();
		return true;
	}

	public static boolean actMatchProjectType(InputStream actFileStream,
			String projectType) throws IOException {
		//checks that the categories are integers
		//if they're not int's, the user probably meant to make a continuous
		//dataset instead.
		//(Or they forgot to discretize their input data.)
		Scanner s = new Scanner(actFileStream);
		int index = 1;
		String line;
		String[] lineArray;
		while (s.hasNextLine()) {
			Scanner ss = new Scanner(s.nextLine());
			if (ss.hasNext()) {
				line = ss.nextLine();
				lineArray = (line.trim()).split("\\s+");

				if (index > 1) {
					if (projectType.equalsIgnoreCase(Constants.CATEGORY)) {
						if (GenericValidator.isInt(lineArray[1])) {
						} else {
							ss.close();
							return false;
						}
					}
				}
				index++;
			}
		}
		s.close();

		return true;
	}

	
}