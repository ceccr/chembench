package edu.unc.ceccr.utilities;

import java.security.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.math.*;
import java.util.Random;
import java.io.*;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.messages.ErrorMessages;
import edu.unc.ceccr.persistence.AdminSettings;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Queue;
import edu.unc.ceccr.taskObjects.GenerateSketchesTask;
import edu.unc.ceccr.taskObjects.QsarModelingTask;
import edu.unc.ceccr.workflows.SdfToJpgWorkflow;

import org.apache.commons.validator.GenericValidator;


import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.struts.upload.FormFile;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;



public class Utility {
	private static Integer debug_counter = 0;

	public Utility() {
	};

	public static byte[] encrypt(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		byte[] encryptedStr = md.digest();
		writeToDebug("Encoding password: " + str  + " to " + encryptedStr + "\n");
		return encryptedStr;
	}

	public boolean compareEncryption(byte[] byte1, byte[] byte2) {
		if (new BigInteger(1, byte1).toString(16).equalsIgnoreCase(
				new BigInteger(1, byte2).toString(16))) {
			return true;
		} else {
			return false;
		}
	}

	public static String randomPassword() throws Exception {
		String source = Constants.SOURCE;
		String password = "";
		Random generator = new Random();
		char[] sourceStr = source.toCharArray();
		for (int i = 0; i < 8; i++) {
			password = password + sourceStr[generator.nextInt(62)];
		}
		return password;
	}

	public static void writeToDebug(String s, String userName, String jobName) {
		//Debug output write function. Used throughout Java code.
		/*
		 * if (userName == null || jobName == null){ //for now, no debug
		 * statements from the non-user jobs like loading pages, etc. //May
		 * re-add these some other time. return; }
		 */
		try {
			// Append to current-job file. For ease of use, really.
			FileWriter fstream = new FileWriter(
					Constants.CECCR_BASE_PATH + "/workflow-users/javadebug.log", true);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write(debug_counter.toString() + " " + userName + " " + jobName
					+ " " + s + "\n");
			out.close();
		} catch (Exception e) {
		}

		try {
			// Append to file
			FileWriter fstream = new FileWriter(
					Constants.CECCR_BASE_PATH + "/workflow-users/debug/" + userName
							+ "-" + jobName + ".log", true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(debug_counter.toString() + " " + s + "\n");
			out.close();
		} catch (Exception e) {
		}
		debug_counter++;
	}
	
	public static void writeToMSDebug(String s) {
		//Debug output write function. SPECIFIC AND TEMPORARY FOR msypa USER. Used throughout Java code.
		
		try {
			// Append to file
			FileWriter fstream = new FileWriter(
					Constants.CECCR_BASE_PATH + "/workflow-users/debug/msypa-test.log", true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(new Date() + ":: " + s + "\n");
			out.close();
		} catch (Exception e) {
		}
	}
	

	public static void writeToDebug(String s) {
		//Debug output write function. Used throughout Java code.
		try {
			// Append to current-job file. For ease of use, really.
			FileWriter fstream = new FileWriter(
					Constants.CECCR_BASE_PATH + "/workflow-users/javadebug.log", true);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write(debug_counter.toString() + " " + s + "\n");
			out.close();
		} catch (Exception e) {
		}
		debug_counter++;
	}

	
	public static void writeToDebug(Exception ex) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(
					Constants.CECCR_BASE_PATH+"/workflow-users/javadebug.log", true);
			String s;
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			ex.printStackTrace(printWriter);
			s = result.toString();
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(s);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
		}
	}
	
	public static void writeProgramLogfile(String workingDir, String programName, InputStream stdout, InputStream errout){
		//Take the inputStreams from an external program execution, write them to a logfile
		    
		try{
		  File file=new File(workingDir + "/Logs/");
		  if(!file.exists()){
		  	file.mkdirs();
		  }
			
		  FileOutputStream logFileOS = new FileOutputStream(new File(workingDir + "/Logs/" + programName + ".log"));
		  StreamGobbler outputGobbler = new StreamGobbler(stdout, "stdout", logFileOS);  
		  StreamGobbler errorGobbler = new StreamGobbler(errout, "errout", logFileOS);   
		  outputGobbler.start();
		  errorGobbler.start();
		  logFileOS.close();
		}
		catch(Exception ex){
	      writeToDebug(ex);
		}
	}
	
	public static boolean isAdmin(String userName){
		boolean user_is_admin = false;
		Iterator it=Constants.ADMIN_LIST.iterator();
		while(it.hasNext())
		{
			if(userName.equals((String)it.next())){
				user_is_admin = true;
			}
		}
		
		return user_is_admin;
	}

	public int getCounter() throws FileNotFoundException, IOException {
		return readCounter();
	}

	public synchronized void increaseCounter() throws IOException {
		int counter = readCounter();
		writeCounter(counter + 1);
	}

	public void writeCounter(int counter) throws IOException {
		File counterFile = new File(Constants.CECCR_USER_BASE_PATH
				+ "counter.txt");
		if (!counterFile.exists()) {
			counterFile.createNewFile();
		}
		DataOutputStream out = new DataOutputStream(new FileOutputStream(
				counterFile));
		out.writeInt(counter);
		out.close();
	}

	public int readCounter() throws FileNotFoundException, IOException {
		int counter = 0;
		File counterFile = new File(Constants.CECCR_USER_BASE_PATH
				+ "counter.txt");
		if (counterFile.exists()) {
			FileInputStream fin = new FileInputStream(counterFile);
			DataInputStream in = new DataInputStream(fin);
			counter = in.readInt();
			return counter;
		} else {
			return counter;
		}

	}

	public static boolean deleteDir(File dir) {
		//writeToMSDebug("deleteDir::"+dir);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
        return dir.delete();
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
		File infile = new File(filePath + fileName);
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

	private static String sdfMatchesAct(FormFile sdFile, FormFile actFile, String user, String datasetname)
	throws IOException {
		String fl = "Error";
		boolean fl2 = false;
		try{
			String userDir = Constants.CECCR_USER_BASE_PATH + user + "/DATASETS/"+datasetname;
			
			if(!isFilesExist(new File(userDir), sdFile, actFile)){
				fl2 = true;
				Utility.writeFiles(sdFile.getInputStream(),userDir+sdFile.getFileName());
				//Utility.rewriteSdf(userDir, sdFile.getFileName());
				Utility.writeToMSDebug("SDF completed:::"+userDir+actFile.getFileName());
				Utility.writeFiles(actFile.getInputStream(),userDir+actFile.getFileName());
				Utility.writeToMSDebug("ACT Completed:::");
			}
		
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
		
		Utility.writeToMSDebug(fl);
			if(!fl.equals("true") && fl2==true){
				Utility.writeToMSDebug("sdfMatchesActDelete::");
				new File(userDir+"/"+sdFile.getFileName()).delete();
				new File(userDir+"/"+actFile.getFileName()).delete();
			
			}
		}
		catch (Exception e) {
			writeToDebug(e);
		}
		
		return fl;
	}
	
	public static String[] strArray(InputStream is) throws IOException {
		byte b[] = new byte[200];
		DataInputStream in = new DataInputStream(is);
		in.read(b);
		String content = new String(b);
		String[] array = content.split("\\s+");

		return array;
	}

	public static Long checkExpiration(int year, int month, int day) {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(year, month - 1, day);
		Calendar cal2 = Calendar.getInstance();
		Date end = cal1.getTime();
		Date today = cal2.getTime();

		return (end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
	}

	public static boolean isValidEmail(String email) {
		return (email.indexOf("@") > 0) && (email.indexOf(".") > 2);
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
		ArrayList<String> compounds = new ArrayList<String>();
		
		Scanner src = new Scanner(fin);
		//added compounds id uniqueness checking by msypa, Dec 02, 08
		if(src.hasNext()){
			temp = src.nextLine();
			compounds.add(temp.trim());
			//Utility.writeToMSDebug(temp);
		}
		while (src.hasNext()) {
			temp = src.nextLine();
			if (temp.startsWith("$")) {
				num++;
				if(src.hasNext()){
					temp = src.nextLine();
					if(compounds.contains(temp.trim())) return ErrorMessages.SDF_CONTAINS_DUPLICATES+temp.trim();
					else{
						compounds.add(temp.trim());
						//Utility.writeToMSDebug(temp);
					}
				}
			}
		}
		fin.close();
		return new Integer(num).toString();
	}
	

	public static Long findExpire(String filePath)
			throws FileNotFoundException, IOException {
		File file = new File(filePath);
		FileReader fin = new FileReader(file);
		Long days = null;

		Scanner src = new Scanner(fin);
		
		while (src.hasNext()) {
			String line = src.nextLine();

			if (line.startsWith("#EXP_DATE:")) {
				String[] array = line.split("\\s");
				days = checkExpiration(Integer.parseInt(array[1]), Integer
						.parseInt(array[2]), Integer.parseInt(array[3]));
				break;
			}
		}
		src.close();

		return days;
	}

	public static String numCompounds(String fileLocation)
			throws FileNotFoundException, IOException {
		int numCompounds = 0;
		//added compounds id uniqueness checking by msypa, Dec 03, 08
		ArrayList<String> compounds = new ArrayList<String>();
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
						if(compounds.contains(array[0].trim())) return ErrorMessages.ACT_CONTAINS_DUPLICATES+array[0].trim();
						else{
							compounds.add(array[0].trim());
							//Utility.writeToMSDebug(".act:::"+array[0].trim());
						}
					}
				}
			}
		}
		return new Integer(numCompounds).toString();
	}

	public static int numModels(QsarModelingTask task)
			throws FileNotFoundException, IOException {
		int steps;

		int maxDescriptor = Integer.parseInt(task.getMaxNumDescriptors());
		int minDescriptor = Integer.parseInt(task.getMinNumDescriptors());
		int stepSize = Integer.parseInt(task.getStepSize());
		int runs = Integer.parseInt(task.getNumRuns());
		int radii = Integer.parseInt(task.getNumSphereRadii());

		steps = (maxDescriptor - minDescriptor) / stepSize + 1;

		return steps * runs * radii;
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

	public static void writeFiles(InputStream is, String fullFileLocation)
			throws IOException {
		Utility.writeToMSDebug("----------"+fullFileLocation+"------------Start");
		OutputStream bos = new FileOutputStream(fullFileLocation);

		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}
		bos.close();
		is.close();
		Utility.writeToMSDebug("----------"+fullFileLocation+"---------End");
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

	public static boolean checkIfUserZfile(String userName, String fileName,
			String type) throws IOException, SQLException,
			ClassNotFoundException {
		DataSet dataSet = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			dataSet = (DataSet) session.createCriteria(DataSet.class).add(
					Expression.eq("userName", userName)).add(
					Expression.eq("fileName", fileName)).add(
					Expression.eq("modelType", type)).uniqueResult();
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}

		if (dataSet != null) {
			return true;
		} else {

			return false;
		}

	}

	public static void saveFileToDatabase(String userName, String sdfFileName,
			String modelType, String fileFrom) throws IOException,
			SQLException, ClassNotFoundException {
		/*String BASE = Constants.CECCR_CS_UNC_EDU_FILEPATH;

		String displayName = sdfFileName.substring(0, sdfFileName.indexOf("."));

		String actPath = userName + "/DATASETS/" + modelType +"/"+ displayName + ".act";

		String sdfPath = userName + "/DATASETS/" + modelType +"/"+ sdfFileName;

		String temp = Utility.numCompounds(BASE + actPath);
		int numCompound = -1;
		if(!temp.contains(ErrorMessages.ACT_CONTAINS_DUPLICATES)) numCompound = new Integer(temp).intValue();

		PredictionDatabase predDb = new PredictionDatabase();

		Set<PredictionDatabaseFile> pdFiles = new HashSet<PredictionDatabaseFile>();

		PredictionDatabaseFile pdf = new PredictionDatabaseFile();
		//
		predDb.setDatabaseName(displayName);

		predDb.setNumberOfCompounds(numCompound);

		predDb.setUserName(userName);

		predDb.setFileComeFrom(fileFrom);

		predDb.setDateCreated(new Date());
		//
		pdf.setDatabaseName(displayName);

		pdf.setFileLocation(sdfPath);

		pdf.setPredictionDatabase(predDb);
		//
		pdFiles.add(pdf);

		predDb.setPredictionDBSFiles(new HashSet<PredictionDatabaseFile>(
				pdFiles));

		DataSet dataSet = new DataSet();

		dataSet.setFileName(displayName);

		dataSet.setUserName(userName);

		dataSet.setActFile(actPath);

		dataSet.setSdfFile(sdfPath);

		dataSet.setModelType(modelType);

		dataSet.setNumCompound(numCompound);

		dataSet.setCreatedTime(new Date());

		Session session = HibernateUtil.getSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			session.save(dataSet);
			session.save(predDb);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
*/
	}

	public static String wrapFileName(String fileName) {
		String tempFile = " ";
		int step, fixedLength = 35;
		int length = fileName.length();

		if (length % fixedLength != 0) {
			if (length / fixedLength > 0) {
				step = length / fixedLength + 1;
			} else {
				step = 0;
			}

		} else {
			step = length / fixedLength;
		}

		if (step == 0) {
			return fileName;
		} else {
			for (int m = 0; m < step; m++) {
				if (m * fixedLength + fixedLength > length) {
					tempFile = tempFile
							+ fileName.substring(m * fixedLength, length) + " ";
				} else {
					tempFile = tempFile
							+ fileName.substring(m * fixedLength, m
									* fixedLength + fixedLength) + " ";
				}
			}
			return tempFile;
		}
	}

	public static void setAdminConfiguration(String path) throws IOException {
		ParseConfigurationXML.initializeConstants(path);
		
		try{
			BufferedReader dis = new BufferedReader(new FileReader(new File(Constants.BUILD_DATE_FILE_PATH))); 
			Constants.BUILD_DATE = dis.readLine().replace("#", ""); 
		}catch(Exception ex){
			writeToDebug(ex);
		}
		
		//get max models, max compounds, and user acceptance mode from database
		//if data doesn't exist in DB yet, set to some arbitrary defaults
		//these numbers will be overwritten if there's anything in the DB.
		Constants.ACCEPTANCE = "automatic";
		Constants.MAXCOMPOUNDS = 100;
		Constants.MAXMODELS = 10000;
		
		List<AdminSettings> ls = new LinkedList<AdminSettings>();
		try{
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();
			ls.addAll(s.createCriteria(AdminSettings.class).add(Expression.eq("type", "userAcceptanceMode")).list());
			ls.addAll(s.createCriteria(AdminSettings.class).add(Expression.eq("type", "maxCompounds")).list());
			ls.addAll(s.createCriteria(AdminSettings.class).add(Expression.eq("type", "maxModels")).list());
			tx.commit();
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}
		Iterator<AdminSettings> iter = ls.iterator();
		AdminSettings as = iter.next();
		while(as != null){
			if(as.getType().equalsIgnoreCase("userAcceptanceMode")){
				Constants.ACCEPTANCE = as.getValue();
			}
			else if(as.getType().equalsIgnoreCase("maxCompounds")){
				Constants.MAXCOMPOUNDS = Integer.parseInt(as.getValue());
			}
			else if(as.getType().equalsIgnoreCase("maxModels")){
				Constants.MAXMODELS = Integer.parseInt(as.getValue());
			}
			as = iter.next();			
		}
		
	}
	
	public static String checkUploadedFiles(FormFile sdFile, FormFile actFile, String knnType, String user, String datasetname) throws FileNotFoundException, IOException{
		String msg="";
		
		if(!Utility.actIsValid(actFile.getInputStream()))
		{
			
				msg=ErrorMessages.ACT_NOT_VALID;
			
		}
		else
		{
			if(!Utility.actMatchProjectType(actFile.getInputStream(), knnType))
			{
				
				msg=ErrorMessages.ACT_DOESNT_MATCH_PROJECT_TYPE;
				
			}
		}
			String sdf_act_match = Utility.sdfMatchesAct(sdFile , actFile, user, datasetname);
			if(sdf_act_match.equals("Error"))
			{
				msg+=ErrorMessages.ACT_DOESNT_MATCH_SDF;
			}
			if(sdf_act_match.contains(ErrorMessages.ACT_CONTAINS_DUPLICATES) || sdf_act_match.contains(ErrorMessages.SDF_CONTAINS_DUPLICATES))
			{
				msg+=sdf_act_match;
			}
			
			
		if(!Utility.sdfIsValid(sdFile.getInputStream()))
		{
			
				msg+=ErrorMessages.SDF_NOT_VALID;
						
		}
		return msg;
		
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
	
	
	
	
	public static boolean isFilesExist(File filePath, FormFile sdFile, FormFile actFile) throws IOException{
		boolean result = true; 
		if(!filePath.exists())
		{
			boolean success=filePath.mkdirs();
			if(success){result=false;}
			}
		else
			{
				if(!new File(filePath.getAbsolutePath()+"/"+sdFile.getFileName()).exists())
		    	{
					result=false;
		    		new File(filePath.getAbsolutePath()+"/"+sdFile.getFileName()).createNewFile();
		    		new File(filePath.getAbsolutePath()+"/"+actFile.getFileName()).createNewFile();
		    	}
		    }
		return result;
	}
	
	
	public static DataSet getDataSetByFileAndUserName(String fileName, String userName)throws ClassNotFoundException, SQLException
	{
		DataSet dataset=null;
		Session  session=HibernateUtil.getSession();
		Transaction tx=null;
		
		try{
			tx=session.beginTransaction();
			dataset = (DataSet)session.createCriteria(DataSet.class).add(Expression.eq("fileName",fileName)).add(Expression.eq("userName",userName)).uniqueResult();
		    tx.commit();
		}catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			Utility.writeToDebug(e);
		} finally {
			session.close();
		}
	   return dataset;
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
	
	
		public static String saveSDFFile(String userName, FormFile sdFile, String datasetName,String path) throws Exception{
			Utility.writeToMSDebug("saveSDFFile");
			if(!sdFile.getFileName().endsWith(".sdf")) return ErrorMessages.SDF_NOT_VALID;
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
				Utility.writeToDebug("Creating file: " + filePath);
				new File(filePath).createNewFile();
				Utility.writeFiles(sdFile.getInputStream(),filePath);
				Utility.writeToDebug("7");
				Utility.rewriteSdf(dir, sdFile.getFileName());
			}
			return "";
		}
		
		public static String saveACTFile(String userName, FormFile actFile, String datasetName, String path) throws IOException{
			boolean isXlsFile = actFile.getFileName().endsWith(".x")
			|| actFile.getFileName().endsWith(".xl") || actFile.getFileName().endsWith(".xls");
			if(!isXlsFile&& !actFile.getFileName().endsWith(".act")) return ErrorMessages.ACT_NOT_VALID; 
			Utility.writeToMSDebug("saveACTFile");
			String dir = path;
			String filePath = dir+actFile.getFileName();
			new File(filePath).createNewFile();
			Utility.writeFiles(actFile.getInputStream(),filePath);
			if(isXlsFile){
				Utility.XLStoACT(dir, actFile.getFileName().substring(0,actFile.getFileName().indexOf("."))+".act",	actFile.getFileName());
				new File(dir +actFile.getFileName()).delete();
			}
				
			Utility.rewriteACTFile(filePath);
			return "";
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
				msg =  Utility.saveSDFFile(userName, sdFile, datasetName, path);
				Utility.writeToMSDebug("Message::"+msg);
			}
			if(msg=="" && !type.equals(Constants.PREDICTION)){
				Utility.saveACTFile(userName, actFile, datasetName, path);
				 msg =  Utility.checkUploadedFiles(sdFile, actFile, type, userName, datasetName);
				 Utility.writeToMSDebug(">>>>>>>>>>>>>>>>checkingUploadedFiles2<<<<<<<<<");
				 /*if(msg==""){
					 new File(path + "Visualization/Structures").delete();
					 new File(path + "Visualization/Structures").mkdirs();
					 new File(path + "Visualization/Sketches").delete();
					 new File(path + "Visualization/Sketches").mkdirs();
					 
					 Utility.writeToMSDebug("Directories created:"+path + "Visualization/Sketches");
					 //SdfToJpgWorkflow.makeSketchFiles(path, sdFile.getFileName(), "Visualization/Structures/", "Visualization/Sketches/");
					
				 }*/
			}
			else if(type.equals(Constants.PREDICTION)){
				try{
					Utility.generateEmptyActFile(path, sdFile.getFileName().replace(".sdf", ""), path+sdFile.getFileName());
				}
				catch(Exception e){
				Utility.writeToMSDebug("ERROR:::"+e.getMessage());
				}
			}
			if (msg != "") {
				Utility.writeToMSDebug(">>>>>>>>>>>>>>>>deleteDir<<<<<<<<<"+dir);
				if(f.exists() && !msg.contains(ErrorMessages.DATABASE_CONTAINS_DATASET)) Utility.deleteDir(f);
			}
			else{
				Utility.writeToMSDebug("File saved");
				Utility.writeDatasetToDatabase(userName, datasetName, sdFile.getFileName(), actFile!=null?actFile.getFileName():sdFile.getFileName().replace(".sdf", ".act"), type, description);
			}
			return msg;
		}
		
	
		private static void generateEmptyActFile(String path, String name, String sdfPath) throws IOException {
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
		 * Should be used when modeling or prediction job was started with uploaded files
		 * but after uploadDataset
		 * @param from
		 * @param to
		 * @throws IOException
		 */
		public static void copyFile(String from, String to) throws IOException{
			  File fromFile = new File(from);
			    File toFile = new File(to);

			    if (!fromFile.exists())
			      throw new IOException("FileCopy: " + "no such source file: "
			          + from);
			    if (!fromFile.isFile())
			      throw new IOException("FileCopy: " + "can't copy directory: "
			          + from);
			    if (!fromFile.canRead())
			      throw new IOException("FileCopy: " + "source file is unreadable: "
			          + from);

			    if (toFile.isDirectory())
			      toFile = new File(toFile, fromFile.getName());

			    if (toFile.exists()) {
			      if (!toFile.canWrite())
			        throw new IOException("FileCopy: "
			            + "destination file is unwriteable: " + to);
			      
			      String parent = toFile.getParent();
			      File dir = new File(parent);
			      if (!dir.exists())
			        throw new IOException("FileCopy: "
			            + "destination directory doesn't exist: " + parent);
			      if (dir.isFile())
			        throw new IOException("FileCopy: "
			            + "destination is not a directory: " + parent);
			      if (!dir.canWrite())
			        throw new IOException("FileCopy: "
			            + "destination directory is unwriteable: " + parent);
			    }
			    else toFile.createNewFile();

			    FileInputStream from_ = null;
			    FileOutputStream to_ = null;
			    try {
			      from_ = new FileInputStream(fromFile);
			      to_ = new FileOutputStream(toFile);
			      byte[] buffer = new byte[4096];
			      int bytesRead;

			      while ((bytesRead = from_.read(buffer)) != -1)
			        to_.write(buffer, 0, bytesRead); // write
			    } finally {
			      if (from_ != null)
			        from_.close();
			      if (to_ != null)
			          to_.close();
			     }
			 }
}