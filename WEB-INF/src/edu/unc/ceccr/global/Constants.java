package edu.unc.ceccr.global;

import java.util.ArrayList;

public class Constants {

	public static boolean isCustomized=false;
	public static String ACCEPTANCE; //can be "automatic" or "manual".
	public static int SESSION_EXPIRATION_TIME = 1800; //How long a login will last if idle, measured in seconds. 1800 seconds = 30 minutes. 
	
	//Enums for Data Types
	public enum DescriptorEnumeration { MOLCONNZ, DRAGON };
	public enum KnnEnumeration { CONTINUOUS, CATEGORY };

	//Types of kNN
	public static final String CONTINUOUS = "CONTINUOUS";
	public static final String CATEGORY = "CATEGORY";

	public static final String MODELBUILDING="MODELING";
	public static final String PREDICTION="PREDICTION";
	public static final String MOLCONNZ = "MOLCONNZ";
	public static final String DRAGON = "DRAGON";
	
	//kNN Constants
	public static final int CONTINUOUS_NNN_LOCATION = 1;
	public static final int CONTINUOUS_Q_SQUARED_LOCATION = 2;
	public static final int CONTINUOUS_N_LOCATION = 3;
	public static final int CONTINUOUS_R_LOCATION = 8;
	public static final int CONTINUOUS_R_SQUARED_LOCATION = 9;
	public static final int CONTINUOUS_K1_LOCATION = 14;
	public static final int CONTINUOUS_K2_LOCATION = 15;
	public static final int CONTINUOUS_R01_SQUARED_LOCATION = 16;
	public static final int CONTINUOUS_R02_SQUARED_LOCATION = 17;
	
	//kNN Constants
	//nnn, Training Accuracy, Normalized Training Accuracy, Test Accuracy, and Normalized Test Accuracy.
	public static final int CATEGORY_NNN_LOCATION = 2;
	public static final int CATEGORY_TRAINING_ACC_LOCATION = 3;
	public static final int CATEGORY_NORMALIZED_TRAINING_ACC_LOCATION = 4;
	public static final int CATEGORY_TEST_ACC_LOCATION = 8;
	public static final int CATEGORY_NORMALIZED_TEST_ACC_LOCATION = 9;

	
	//External Validation Constants
	public static final int COMPOUND_ID = 0;
	public static final int STRUCTURE_FILE = 0; //This and following constants decreased by 1. Wonder what this will do... 
	public static final int ACTUAL = 1;
	public static final int PREDICTED = 2;
	public static final int NUM_MODELS = 3;
	public static final int STD_DEVIATION = 3;
	
	//Paths
	public static String CECCR_BASE_PATH ;
	public static String TOMCAT_PATH ;
	public static String BUILD_DATE_FILE_PATH;
	public static String BUILD_DATE;
	public static String CECCR_USER_BASE_PATH;
	public static String XML_FILE_PATH;
	public static String SDFILE_FILEPATH ;
	public static String DATAFILE_FILEPATH ;
	
	public static String CATEGORY_DATAFILE_FILEPATH;
	public static String CONTINUOUS_DATAFILE_FILEPATH;
	
	public static String EXECUTABLEFILE_PATH;
	public static String MOLCONNZ_DATFILE_PATH = "/usr/local/chemb/ParameterFiles/MZ405.dat";
	
	public static final String kNN_OUTPUT_FILE = "knn-outputsort.tbl";
	public static final String EXTERNAL_VALIDATION_OUTPUT_FILE = "external_prediction_table";
	public static final String PRED_OUTPUT_FILE = "cons_pred";
	public static final String KNN_DEFAULT_FILENAME = "knn.default";
	public static final String KNN_CATEGORY_DEFAULT_FILENAME = "knn_category.default";
	public static final String SE_DEFAULT_FILENAME = "param9.txt";
	
	public static final int SELECT = 0, UPLOAD = 1;
	
	//for testing
	public static final Integer MAX_FILE_SIZE = new Integer("1024");
	
	//administration
	public static  String WEBADDRESS;
	public static  String PHONRNUMBER;
	public static  String WEBSITEEMAIL;
	
	//mail
	public static  String MAILSERVER;
	public static  String MAILHOST;
	
	// web service validation
	public static  String RECAPTCHA_PUBLICKEY;
	public static  String RECAPTCHA_PRIVATEKEY;
	
	public static int MAXCOMPOUNDS=200;
	public static int MAXMODELS=10000;
	
	public static String DATABASE_URL;
    public static String DATABASE_DRIVER;
	public static String DATABASE_USERNAME;
	public static String CECCR_DATABASE_NAME;
	public static String CECCR_DATABASE_PASSWORD;
	
	public static final String ALL_USERS_USERNAME = "_all";
	
	public static String WORKBENCH;
	public static final String CCHEMBENCH="cchembench";
	public static final String CTOXBENCH="ctoxbench";
	public static final String MAINKNN="MAINKNN";
	public static final String RANDOMKNN="RANDOMKNN";
	
	public static ArrayList<String> ADMIN_LIST=new ArrayList<String>();
	public static ArrayList<String> ADMINEMAIL_LIST=new ArrayList<String>();
	
	//used by password hash function when user gets or changes their password
	public static final String SOURCE="qwertyuio123NBV456pasdfghOPASDFGHjklm7890QWERTYUInbvcxzJKLMCXZ";
	public static final String VALIDATOR_STRING="1234567890~!@#$%^&*()=+[]{}|:;'<>?"; 
	

}
