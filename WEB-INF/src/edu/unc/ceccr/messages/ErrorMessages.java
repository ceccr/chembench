/*
 *  ErrorMessages - class which contains the error messages we display to user when
 *  the error situation has occurred in the code.
 *  @author Myroslav Sypa
 *  @date 12/03/08  
 */
package edu.unc.ceccr.messages;

public class ErrorMessages {

	public ErrorMessages(){};
	
	public final static String ACT_NOT_VALID = 					"<br/>The .act file you try to upload is not a valid <u> ACT </u>file !<br/> It may be empty or contain some invalid values.";
	public final static String ACT_DOESNT_MATCH_PROJECT_TYPE =  "<br/>The activity file does not match the project type you have choosen.";
	public final static String ACT_DOESNT_MATCH_SDF = 			"<br/>The submitted SDF and ACT file do not match.";
	public final static String SDF_NOT_VALID = 					"<br/>The extension of the file you try to upload is not valid. You should use .sdf extension for <u>SDF</u> files!";
	public final static String INVALID_SDF = 					"<br/>The SD file you try to upload is invalid! Please check the content of your file!";
	public final static String SDF_CONTAINS_DUPLICATES=			"<br/>The .sdf file you try to upload contains duplicated compound ids:";
	public final static String ACT_CONTAINS_DUPLICATES=			"<br/>The .act file you try to upload contains duplicated compound ids:";
	public final static String DATABASE_CONTAINS_DATASET=		"<br/>The database already contains the dataset with the same name as the name of the file you try to upload!";
	public final static String DATABASE_CONTAIN_PREDICTOR=		"<br/>The databse already contains the predictor with the same name as the name of the file you try to upload!";
	
}
