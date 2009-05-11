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
import edu.unc.ceccr.taskObjects.QsarModelingTask;


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
		try {
			// Append to current-job file. For ease of use, really.
			FileWriter fstream = new FileWriter(
					Constants.CECCR_BASE_PATH + "/workflow-users/javadebug.log", true);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write(debug_counter.toString() + " " + userName + " " + jobName
					+ " " + s + "\n");
			out.close();
		} catch (Exception e) {
			//ohnoes!
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
			//whatever
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
		  FileOutputStream errFileOS = new FileOutputStream(new File(workingDir + "/Logs/" + programName + ".err"));
		  StreamGobbler outputGobbler = new StreamGobbler(stdout, "stdout", logFileOS);  
		  StreamGobbler errorGobbler = new StreamGobbler(errout, "errout", errFileOS);   
		  outputGobbler.start();
		  errorGobbler.start();
		  logFileOS.close();
		  errFileOS.close();
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

	public static Long checkExpiration(int year, int month, int day) {
		Calendar cal1 = Calendar.getInstance();
		cal1.set(year, month - 1, day);
		Calendar cal2 = Calendar.getInstance();
		Date end = cal1.getTime();
		Date today = cal2.getTime();

		return (end.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
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
	
	
}