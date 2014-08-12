/*
 *  ErrorMessages - class which contains the error messages we display to user when
 *  the error situation has occurred in the code.
 *  @author Myroslav Sypa
 *  @date 12/03/08
 */
package edu.unc.ceccr.global;

public class ErrorMessages {

    public final static String ACT_NOT_VALID = "The ACT file you tried to upload is not valid!";

    ;
    public final static String ACT_DOESNT_MATCH_PROJECT_TYPE =
            "The ACT file contains non-integer categories. Please " + "use integers to specify your categories.";
    public final static String COMPOUND_IDS_ACT_DONT_MATCH_SDF =
            "The following compound IDs in your ACT file could " + "not be found in your SDF: ";
    public final static String COMPOUND_IDS_SDF_DONT_MATCH_ACT =
            "The following compound IDs in your SDF file could " + "not be found in your ACT: ";
    public final static String COMPOUND_IDS_ACT_DONT_MATCH_X =
            "The following compound IDs in your ACT file could not" + " be found in your X file: ";
    public final static String COMPOUND_IDS_X_DONT_MATCH_ACT =
            "The following compound IDs in your X file could not " + "be found in your ACT: ";
    public final static String COMPOUND_IDS_X_DONT_MATCH_SDF =
            "The following compound IDs in your X file could not " + "be found in your SDF: ";
    public final static String COMPOUND_IDS_SDF_DONT_MATCH_X =
            "The following compound IDs in your SDF file could not" + " be found in your X file: ";
    public final static String INVALID_SDF = "The SDF file you tried to upload is not valid!";
    public final static String SDF_IS_EMPTY = "The SDF file you tried to upload contains no compounds!";
    public final static String INVALID_X = "The X file you tried to upload is not valid!";
    public final static String X_IS_EMPTY = "The X file you tried to upload contains no compounds!";
    public final static String SDF_CONTAINS_DUPLICATES =
            "The SDF file you tried to upload contains duplicated " + "compound ids:";
    public final static String ACT_CONTAINS_DUPLICATES =
            "The ACT file you tried to upload contains duplicated " + "compound ids:";
    public final static String X_CONTAINS_DUPLICATES =
            "The X file you tried to upload contains duplicated compound " + "ids:";
    public final static String EXTERNAL_COMPOUNDS_NOT_IN_DATASET =
            "The following external compounds you selected are missing from your dataset:";

    public ErrorMessages() {
    }
}
