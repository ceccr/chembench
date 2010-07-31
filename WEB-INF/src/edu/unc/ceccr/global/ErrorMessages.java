/*
 *  ErrorMessages - class which contains the error messages we display to user when
 *  the error situation has occurred in the code.
 *  @author Myroslav Sypa
 *  @date 12/03/08  
 */
package edu.unc.ceccr.global;

public class ErrorMessages {

	public ErrorMessages(){};
	
	public final static String ACT_NOT_VALID = 					"Error! The ACT file you tried to upload is not valid!";
	public final static String ACT_DOESNT_MATCH_PROJECT_TYPE =  "Error! The ACT file contains non-integer categories. Please use integers to specify your categories.";
	public final static String COMPOUND_IDS_ACT_DONT_MATCH_SDF = "Error! The following compound IDs in your ACT file could not be found in your SDF: ";
	public final static String COMPOUND_IDS_SDF_DONT_MATCH_ACT = "Error! The following compound IDs in your SDF file could not be found in your ACT: ";
	public final static String INVALID_SDF = 					"Error! The SDF file you tried to upload is not valid!";
	public final static String SDF_CONTAINS_DUPLICATES=			"Error! The SDF file you tried to upload contains duplicated compound ids:";
	public final static String ACT_CONTAINS_DUPLICATES=			"Error! The ACT file you tried to upload contains duplicated compound ids:";
	public final static String X_CONTAINS_DUPLICATES=			"Error! The X file you tried to upload contains duplicated compound ids:";	
}
