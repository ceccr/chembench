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
import java.nio.channels.FileChannel;
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
		
		if(f.exists()) msg =  ErrorMessages.DATABASE_CONTAINS_DATASET;
		if(msg==""){	
			msg =  saveSDFFile(userName, sdFile, datasetName, path);
			Utility.writeToMSDebug("Message::"+msg);
		}
		if(msg=="" && !type.equals(Constants.PREDICTION)){
			 msg = saveACTFile(userName, actFile, datasetName, path);
			 msg =  checkUploadedFiles(sdFile, actFile, type, userName, datasetName);
			 Utility.writeToMSDebug(">>>>>>>>>>>>>>>>checkingUploadedFiles2<<<<<<<<<");
		}
		else if(type.equals(Constants.PREDICTION)){
			try{
				generateEmptyActFile(path, sdFile.getFileName().substring(0,sdFile.getFileName().lastIndexOf(".")), path+sdFile.getFileName());
			}
			catch(Exception e){
			Utility.writeToMSDebug("ERROR:::"+e.getMessage());
			}
		}
		if (msg != "") {
			Utility.writeToMSDebug(">>>>>>>>>>>>>>>>deleteDir<<<<<<<<<"+dir);
			if(f.exists() && !msg.contains(ErrorMessages.DATABASE_CONTAINS_DATASET)) FileAndDirOperations.deleteDir(f);
		}
		else{
			Utility.writeToMSDebug("File saved");
			writeDatasetToDatabase(userName, datasetName, sdFile.getFileName(), actFile!=null?actFile.getFileName():sdFile.getFileName().substring(0,sdFile.getFileName().lastIndexOf(".")), type, description);
		}
		return msg;
	}
	

	public static String saveSDFFile(String userName, FormFile sdFile, String datasetName,String path) throws Exception{
		Utility.writeToMSDebug("saveSDFFile");
		if(!sdFile.getFileName().toLowerCase().endsWith(".sdf")) return ErrorMessages.SDF_NOT_VALID;
		if(!new File(Constants.CECCR_USER_BASE_PATH+userName).exists())
			new File(Constants.CECCR_USER_BASE_PATH+userName).mkdirs();
		if(!new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS").exists())
			new File(Constants.CECCR_USER_BASE_PATH+userName+"/DATASETS").mkdirs();
				
		String dir = path;
		
		// checking if directory with the same name already exists in our file system
		File datasetDir = new File(dir);
		
		if(datasetDir.exists()) return ErrorMessages.DATABASE_CONTAINS_DATASET;
		else{
			
			datasetDir.mkdirs();
			
			String filePath = dir+sdFile.getFileName();
			new File(filePath).createNewFile();
			FileAndDirOperations.writeFiles(sdFile.getInputStream(),filePath);
			rewriteSdf(dir, sdFile.getFileName());
		}
		return "";
	}
	
	public static String saveACTFile(String userName, FormFile actFile, String datasetName, String path) throws IOException{
		boolean isXlsFile = actFile.getFileName().endsWith(".x")
		|| actFile.getFileName().endsWith(".xl") || actFile.getFileName().endsWith(".xls");
		String act_file = actFile.getFileName();
		act_file = act_file.toLowerCase();
		boolean isActFile = act_file.endsWith(".act");		
		if(!isXlsFile && !isActFile) return ErrorMessages.ACT_NOT_VALID; 
		Utility.writeToMSDebug("saveACTFile");
		String dir = path;
		String filePath = dir+actFile.getFileName();
		new File(filePath).createNewFile();
		FileAndDirOperations.writeFiles(actFile.getInputStream(),filePath);
		if(isXlsFile){
			XLStoACT(dir, actFile.getFileName().substring(0,actFile.getFileName().indexOf("."))+".act",	actFile.getFileName());
			new File(dir +actFile.getFileName()).delete();
		}
			
		rewriteACTFile(filePath);
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
	
	public static String numCompounds(String fileLocation)
			throws FileNotFoundException, IOException {
		int numCompounds = 0;
		//added compounds id uniqueness checking by msypa, Dec 03, 08
		act_compounds = new ArrayList<String>();
		File file = new File(fileLocation);
		//added compounds id uniqueness checking by msypa, Dec 03, 08
		
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
						//added compounds id uniqueness checking by msypa, Dec 03, 08
						if(act_compounds.contains(array[0].trim())) return ErrorMessages.ACT_CONTAINS_DUPLICATES+array[0].trim();
						else{
							act_compounds.add(array[0].trim());
							//Utility.writeToMSDebug(".act:::"+array[0].trim());
						}
					}
				}
			}
		}
		return new Integer(numCompounds).toString();
	}

	
	private static void writeDatasetToDatabase(String userName, String name, String sdfFileName , String actFileName,
			String modelType, String description) throws IOException,
			SQLException, ClassNotFoundException {
		Utility.writeToMSDebug("writeDartasetToDatabase::"+"userName::"+userName+ " name::" + name+" sdfFileName::"+sdfFileName +" actFileName::"+actFileName+
			"modelType::"+modelType+"description::"+description);

		int numCompound = -1;
		if(actFileName!=null){
			String temp = numCompounds(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + name + "/" + actFileName);
			if(!temp.contains(ErrorMessages.ACT_CONTAINS_DUPLICATES)) numCompound = new Integer(temp).intValue();
		}
		if(modelType.equals(Constants.PREDICTION)){
			numCompound = new Integer(numCompoundsFromSDFiles(Constants.CECCR_USER_BASE_PATH + userName + "/DATASETS/" + name, sdfFileName)).intValue();
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

	public static String checkUploadedFiles(FormFile sdFile, FormFile actFile, String knnType, String user, String datasetname) throws FileNotFoundException, IOException{
		String msg="";
		
		if(!actIsValid(actFile.getInputStream()))
		{
			
				msg=ErrorMessages.ACT_NOT_VALID;
			
		}
		else
		{
			if(!actMatchProjectType(actFile.getInputStream(), knnType))
			{
				
				msg=ErrorMessages.ACT_DOESNT_MATCH_PROJECT_TYPE;
				
			}
		}
			String sdf_act_match = sdfMatchesAct(sdFile , actFile, user, datasetname);
			if(sdf_act_match.equals("Error"))
			{
				msg+=ErrorMessages.ACT_DOESNT_MATCH_SDF;
			}
			if(sdf_act_match.contains("Error") && !sdf_act_match.equals("Error"))
			{
				msg+=sdf_act_match;
			}
			if(sdf_act_match.contains(ErrorMessages.ACT_CONTAINS_DUPLICATES) || sdf_act_match.contains(ErrorMessages.SDF_CONTAINS_DUPLICATES))
			{
				msg+=sdf_act_match;
			}
			
			
		if(!sdfIsValid(sdFile.getInputStream()))
		{
				msg+=ErrorMessages.SDF_NOT_VALID;
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
	public static String numCompoundsFromSDFiles(String filePath, String fileName)
			throws FileNotFoundException, IOException {
		File file = new File(filePath +"/"+ fileName);
		FileReader fin = new FileReader(file);
		int num = 0;
		String temp;
		//added compounds id uniqueness checking by msypa, Dec 02, 08
		sdf_compounds = new ArrayList<String>();
		
		Scanner src = new Scanner(fin);
		//added compounds id uniqueness checking by msypa, Dec 02, 08
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
					if(sdf_compounds.contains(temp.trim())) return ErrorMessages.SDF_CONTAINS_DUPLICATES+temp.trim();
					else{
						sdf_compounds.add(temp.trim());
						//Utility.writeToMSDebug(temp);
					}
				}
			}
		}
		fin.close();
		return new Integer(num).toString();
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
		Utility.writeToDebug(chemicalNames.toString());
		return chemicalNames;
	}
	
	public static String sdfMatchesAct(FormFile sdFile, FormFile actFile, String user, String datasetname)
	throws IOException {
		String fl = "Error";
		
		try{
			String userDir = Constants.CECCR_USER_BASE_PATH + user + "/DATASETS/"+datasetname;
			
		int numCompound=-1;
		int numCompoundSD=-1;
		
		String temp = numCompounds(userDir+"/"+ actFile.getFileName());
		Utility.writeToMSDebug("Number of compounds in ACT:::"+temp);
		
		if(!temp.contains(ErrorMessages.ACT_CONTAINS_DUPLICATES)) numCompound = new Integer(temp).intValue();
		else fl = temp;
		
		String temp2 = numCompoundsFromSDFiles(userDir, sdFile.getFileName());
		Utility.writeToMSDebug("Number of compounds in SDF:::"+temp2);
		
		if(!temp2.contains(ErrorMessages.SDF_CONTAINS_DUPLICATES)) numCompoundSD = new Integer(temp2).intValue();
		else fl += temp2;
		
		if(numCompound==numCompoundSD && numCompound!=-1)	fl= "true";
		
		// check if compounds in act are the same as compounds in sdf
		
		if(fl.equals("true")){
			for(int i = 0;i<act_compounds.size();i++)
				if(!act_compounds.get(i).equals(sdf_compounds.get(i))){
					fl = "Error " + ErrorMessages.ACT_DOESNT_MATCH_SDF+"Compound <b>"+act_compounds.get(i)+"</b> from ACT file doesnt match compound <b>"+sdf_compounds.get(i)+"</b> from SDF file!";
					Utility.writeToMSDebug(fl);
				}
			}
		}
		catch (Exception e) {
			Utility.writeToDebug(e);
		}
		
		return fl;
	}
	
	public static void rewriteSdf(String filePath, String fileName)
	throws FileNotFoundException, IOException {
		
		//SDFs with lines longer than 1023 characters will
		//not work properly with MolconnZ.
		//This function gets rid of all such lines.
		//Does not change the information in the SD file;
		//long lines in SDFs are always comments.
		//Google for "C Programmer's Disease" for more info.
		
		//This function will also remove the silly /r characters Windows likes
		//to add to newlines.
		Utility.writeToMSDebug("=========="+filePath + fileName+"======Rewrite_Start");
		File infile = new File(filePath + fileName.toLowerCase());
		File outfile = new File(filePath + fileName + ".temp");
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
		Utility.writeToMSDebug("=========="+filePath + fileName+"======Rewrite_End");
	}

	public static boolean sdfIsValid(InputStream sdFileStream)
		throws IOException {
		if (sdFileStream.available() < 0) {
			return false;
		}
		
		Scanner s = new Scanner(sdFileStream);
		int size, index = 1;
		String line;
		String[] lineArray;
		while (s.hasNextLine()) {
			Scanner ss = new Scanner(s.nextLine());
			if (ss.hasNext()) {
				line = ss.nextLine();
				lineArray = (line.trim()).split("\\s+");
				size = lineArray.length;
		
				if (index > 3) {
					if (!lineArray[0].startsWith("M")) {
						if (size != 7 && size != 16) {
							ss.close();
							return false;
						}
					} else {
						ss.close();
						return true;
					}
				}
				index++;
			}
		}
		s.close();
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