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
import edu.unc.ceccr.messages.ErrorMessages;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;

/*
Functions relating to the processing of incoming dataset files go in here.
We might move this to the Workflows package later, but it's OK here for now.
*/

public class DatasetFileOperations {

	private static ArrayList<String> act_compounds;
	private static ArrayList<String> sdf_compounds;
	private static String formula="";
	
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
	
	/**
	 * will take care of the upload SDF, SDF and ACT file, in case of the error will delete the directory 
	 * @param userName
	 * @param sdFile
	 * @param actFile
	 * @param datasetName
	 * @param description
	 * @param type
	 * @return message in case of the error 
	 * @throws Exception
	 */
	public static String uploadDataset(String userName, FormFile sdFile, FormFile actFile, String datasetName, String description, String type) throws Exception{
		
		String path = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+datasetName+"/";
		String dir = Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS/"+datasetName;

		Utility.writeToDebug("Creating dataset at " + path);
		
		File f = new File(dir);
		String msg="";
		
		if(f.exists()) msg =  ErrorMessages.FILESYSTEM_CONTAINS_DATASET;
		
		if(msg==""){	
			msg = saveSDFFile(userName, sdFile, path);
			Utility.writeToDebug("rewriting sdf into a standard 2D format: " + path + sdFile.getFileName());
			rewriteSdf(path, sdFile.getFileName());
			Utility.writeToDebug("Done rewriting SDF.");
			Utility.writeToMSDebug("Message::"+msg);
		}
		
		if(msg=="" && !type.equals(Constants.PREDICTION)){
			 msg = saveACTFile(actFile, path);
			 Utility.writeToMSDebug(">>>>>>>>>>>>>>>>checkingUploadedFiles2<<<<<<<<<");
		}
		
		else if(type.equals(Constants.PREDICTION)){
			    generateEmptyActFile(path, sdFile.getFileName().substring(0,sdFile.getFileName().lastIndexOf(".")), path+sdFile.getFileName());
		}
		msg =  checkUploadedFiles(sdFile, actFile, type, userName, datasetName);
		if (msg == ""){
			Utility.writeToMSDebug("File saved, formula="+formula);
			writeDatasetToDatabase(userName, datasetName, sdFile.getFileName(), actFile!=null?actFile.getFileName():sdFile.getFileName().substring(0,sdFile.getFileName().lastIndexOf("."))+".act", type, description, formula);
		}
		return msg;
	}
	
	public static String saveSDFFile(String userName, FormFile sdFile, String path) throws Exception{
		Utility.writeToMSDebug("saveSDFFile");

		String dir = path;
		File datasetDir = new File(dir);
		if(!sdFile.getFileName().toLowerCase().endsWith(".sdf")) return ErrorMessages.SDF_FILE_EXTENSION_INVALID;
		if(!new File(Constants.CECCR_USER_BASE_PATH+userName).exists())
			new File(Constants.CECCR_USER_BASE_PATH+userName).mkdirs();
		if(!new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS").exists())
			new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS").mkdirs();
		
		datasetDir.mkdirs();
		String filePath = dir+sdFile.getFileName();
		new File(filePath).createNewFile();
		FileAndDirOperations.writeFiles(sdFile.getInputStream(),filePath);
		
		return "";
	}
	
	public static String saveACTFile(FormFile actFile, String path) throws IOException{
		boolean isXlsFile = actFile.getFileName().endsWith(".x")
		|| actFile.getFileName().endsWith(".xl") || actFile.getFileName().endsWith(".xls");
		String act_file = actFile.getFileName();
		boolean isActFile = act_file.toLowerCase().endsWith(".act");		
		if(!isXlsFile && !isActFile) return ErrorMessages.ACT_FILE_EXTENSION_INVALID; 
		Utility.writeToMSDebug("saveACTFile");
		String dir = path;
		String filePath = dir+actFile.getFileName();
		new File(filePath).createNewFile();
		FileAndDirOperations.writeFiles(actFile.getInputStream(),filePath);
		if(isXlsFile){
			XLStoACT(dir, actFile.getFileName().substring(0,actFile.getFileName().indexOf("."))+".act",	actFile.getFileName());
			new File(dir +actFile.getFileName()).delete();
		}
			
		 formula = rewriteACTFile(filePath);
		 return "";
	}
	

	
	@SuppressWarnings("unchecked")
	public static Vector<Vector<String>> readFileToVector(String delimiter, String path) throws Exception {
    	
    	Vector<Vector<String>> result = new Vector<Vector<String>>();
        
    	//Utility.writeToMSDebug("-----readFileToVector-----Starting read");
    	try{
    	BigFile bf = new BigFile(path);
    	for(Iterator i = bf.iterator();i.hasNext();){
    		Vector<String> temp = new Vector<String>();
    		String[] s = ((String)i.next()).split(delimiter);
    		//Utility.writeToMSDebug("::::::::::::::::"+s.length);
            for (int j = 0; j < s.length; j++) {
                  temp.add(s[j]);
              }  
            //Utility.writeToMSDebug("::::"+temp.toString());
            result.add(temp);
    	}
    	}
    	catch(Exception e){
    			Utility.writeToMSDebug(e.getMessage());
    	}
    	
		/*BufferedReader buffered_data = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
		Utility.writeToMSDebug("-----readFileToVector-----Buffer read  "+buffered_data.ready());
    	String line;    
    	try{
       while ((line = buffered_data.readLine()) != null) {
          Vector<String> temp = new Vector<String>();
          String[] s = line.split(delimiter);
           for (int j = 0; j < s.length; j++) {
                 temp.add(s[j]);
             }
          Utility.writeToMSDebug("::::"+temp.toString());
        result.add(temp);
       }

       buffered_data.close();
    	}catch(Exception e){
    		Utility.writeToMSDebug("Exception:::"+ e.getMessage());
    	}*/
    	
       Utility.writeToMSDebug("-----readFileToVector-----Data readed");
       return result;         

    }
	

	public static boolean createFiles(File filePath, FormFile sdFile, FormFile actFile) throws IOException{
		boolean result = true; 
		if(!filePath.exists()) result = filePath.mkdir();
		if(result && !new File(filePath.getAbsolutePath()+"/"+sdFile.getFileName()).exists())
		   result = new File(filePath.getAbsolutePath()+"/"+sdFile.getFileName()).createNewFile();
		if(result && !new File(filePath.getAbsolutePath()+"/"+actFile.getFileName()).exists())
		    result = new File(filePath.getAbsolutePath()+"/"+actFile.getFileName()).createNewFile();
		return result;
	}
	
	public static int numCompounds(String fileLocation)
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
	
	
	private static void writeDatasetToDatabase(String userName, String name, String sdfFileName , String actFileName,
			String modelType, String description, String formula) throws IOException,
			SQLException, ClassNotFoundException {
		Utility.writeToMSDebug("writeDartasetToDatabase::"+"userName::"+userName+ " name::" + name+" sdfFileName::"+sdfFileName +" actFileName::"+actFileName+
			"modelType::"+modelType+"description::"+description);

		int numCompound = act_compounds.size();
		if(modelType.equals(Constants.PREDICTION)){
			numCompound = sdf_compounds.size();//new Integer(numCompoundsFromSDFiles(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + name, sdfFileName)).intValue();
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

	public static String checkUploadedFiles(FormFile sdFile, FormFile actFile, String knnType, String user, String datasetName) throws FileNotFoundException, IOException{
		String msg="";
		
		if(!actIsValid(actFile.getInputStream()))
		{
				msg=ErrorMessages.ACT_NOT_VALID;
		}
		
		else if(!sdfIsValid(sdFile.getInputStream()))
		{
				msg+=ErrorMessages.INVALID_SDF;
		}
		
		else if(!actMatchProjectType(actFile.getInputStream(), knnType))
		{
			msg=ErrorMessages.ACT_DOESNT_MATCH_PROJECT_TYPE;
		}
		else{
			//Check if ADF matches ACT file 
			String sdf_act_match = sdfMatchesAct(sdFile , actFile, user, datasetName);
			if(!sdf_act_match.equals("-1"))
			{	
				msg+=sdf_act_match;
			}
			//* Check if ADF matches ACT file
				
			//Check if ACT file contains duplicates 
			int act_duplicate_position = findDuplicates(true);
			if(act_duplicate_position!=-1) msg+= ErrorMessages.ACT_CONTAINS_DUPLICATES + act_compounds.get(act_duplicate_position);
			//* Check if ACT file contains duplicates
				
			//Check if SDF file contains duplicates 
			int sdf_duplicate_position = findDuplicates(false);
			if(sdf_duplicate_position!=-1) msg+= ErrorMessages.SDF_CONTAINS_DUPLICATES + act_compounds.get(sdf_duplicate_position);
			//* Check if SDF file contains duplicates
		}
		return msg;
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
	public static int numCompoundsFromSDFiles(String filePath, String fileName)
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
	
	public static String rewriteACTFile(String filePath)
	throws FileNotFoundException, IOException {
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
	
	public static String sdfMatchesAct(FormFile sdFile, FormFile actFile, String user, String datasetName)
	throws IOException {
			
		String userDir = Constants.CECCR_USER_BASE_PATH + user + "/DATASETS/"+datasetName;
			
		// Checking if number of compounds in ACT is the same as in SDF file
		int numACT = numCompounds(userDir+"/"+ actFile.getFileName());
		int numSDF = numCompoundsFromSDFiles(userDir, sdFile.getFileName());
		
		Utility.writeToMSDebug("Number of compounds in ACT:::"+numACT);
		
		if(numACT!=numSDF) return ErrorMessages.ACT_DOESNT_MATCH_SDF+ "ACT contains " + numACT+ " vs. "+numSDF+" in SDF file!";
		//* Checking if number of compounds in ACT is the same as in SDF file
		
		// Check if compounds in act are the same as compounds in sdf
		int compound_position = checkCompoundsAreSameInACTAndSDF();
		if(compound_position!=-1) return ErrorMessages.ACT_DOESNT_MATCH_SDF + " Compound "+ act_compounds.get(compound_position)+" from ACT file does not match compound "+ sdf_compounds.get(compound_position)+" from SDF file!";
		//* Check if compounds in act are the same as compounds in sdf
		return "-1";
	}
	
	public static void rewriteSdf(String filePath, String fileName) {
		
		//SDFs with lines longer than 1023 characters will
		//not work properly with MolconnZ.
		//This function gets rid of all such lines.
		//Does not change the information in the SD file;
		//long lines in SDFs are always comments.
		//Google for "C Programmer's Disease" for more info.
		
		//This function will also remove the silly /r characters Windows likes
		//to add to newlines.

		File infile = new File(filePath + fileName);
		File outfile = new File(filePath + fileName + ".temp");
		
		//First, run the file through jchem to eliminate anything totally bizarre
		try{
			Utility.writeToMSDebug("=========="+filePath + fileName+"======Rewrite_Start");
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
		
		//File infile_lowercase = new File(filePath + fileName.toLowerCase());
		//outfile.renameTo(infile_lowercase);
		
		Utility.writeToMSDebug("=========="+filePath + fileName+"======Rewrite_End");
	}

	public static boolean sdfIsValid(InputStream sdFileStream)
		throws IOException {
		if (sdFileStream.available() < 0) {
			return false;
		}
		/*
		Scanner s = new Scanner(sdFileStream);
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
		//yessir, that's a great SDF ya got there!
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
						if (GenericValidator.isInt(lineArray[1])
								&& GenericValidator.isInRange(Integer
										.parseInt(lineArray[1]), -1, 20)) {
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