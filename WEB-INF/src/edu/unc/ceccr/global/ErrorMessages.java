/*
 *  ErrorMessages - class which contains the error messages we display to user when
 *  the error situation has occurred in the code.
 *  @author Myroslav Sypa
 *  @date 12/03/08  
 */
package edu.unc.ceccr.global;

public class ErrorMessages {

	public ErrorMessages(){};
	
	public final static String ACT_NOT_VALID = 					"<br/>Error! The ACT file you tried to upload is not valid!";
	public final static String ACT_DOESNT_MATCH_PROJECT_TYPE =  "<br/>Error! The ACT file contains non-integer categories. Please use integers to specify your categories.";
	public final static String COMPOUND_IDS_DONT_MATCH = 		"<br/>Error! The compound IDs in the submitted files do not match.";
	public final static String INVALID_SDF = 					"<br/>Error! The SDF file you tried to upload is not valid!";
	public final static String SDF_CONTAINS_DUPLICATES=			"<br/>Error! The SDF file you tried to upload contains duplicated compound ids:";
	public final static String ACT_CONTAINS_DUPLICATES=			"<br/>Error! The ACT file you tried to upload contains duplicated compound ids:";
	public final static String X_CONTAINS_DUPLICATES=			"<br/>Error! The X file you tried to upload contains duplicated compound ids:";	
}
