/*
 *  ErrorMessages - class which contains the error messages we display to user when
 *  the error situation has occurred in the code.
 *  @author Myroslav Sypa
 *  @date 12/03/08  
 */
package edu.unc.ceccr.global;

public class ErrorMessages {

	public ErrorMessages(){};
	
	public final static String ACT_NOT_VALID = 					"<br/>Error! The ACTIVITY file you try to upload is not a valid <u> ACTIVITY </u>file !<br/> It has invalid format. To obtain more information about format of the activity files please <a href='http://chembench-dev.mml.unc.edu/help.do#04'>click here</a>";
	public final static String ACT_DOESNT_MATCH_PROJECT_TYPE =  "<br/>Error! The ACTIVITY file does not match the project type you have choosen. For help <a href='http://chembench-dev.mml.unc.edu/help.do'>click here</a>";
	public final static String ACT_DOESNT_MATCH_SDF = 			"<br/>Error! The submitted SDF and ACT file do not match. ";
	public final static String ACT_FILE_EXTENSION_INVALID = 	"<br/>Error! The ACTIVITY file you try to upload has invalid extension! Please upload the files with this extensions: .x, .xls, .xl, .act! To obtain more information about format of the activity files please <a href='http://chembench-dev.mml.unc.edu/help.do#04'>click here</a>";
	public final static String SDF_FILE_EXTENSION_INVALID = 	"<br/>Error! The extension of the file you try to upload is not valid. You should use .sdf extension for <u>SDF</u> files!";
	public final static String X_FILE_EXTENSION_INVALID = 		"<br/>Error! The extension of the file you try to upload is not valid. You should use .x extension for <u>X</u> files!";
	public final static String INVALID_SDF = 					"<br/>Error! The SD file you try to upload is invalid! Please check the content of your file!";
	public final static String SDF_CONTAINS_DUPLICATES=			"<br/>Error! The SDF file you try to upload contains duplicated compound ids:";
	public final static String ACT_CONTAINS_DUPLICATES=			"<br/>Error! The ACTIVITY file you try to upload contains duplicated compound ids:";
	
}
