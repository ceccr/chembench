<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
  <!-- kNN Parameters -->
  <table width="100%" align="center" border="0" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set kNN Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td>
        <table>

          <!-- kNN, Basic parameters  -->
          <tr>
            <td colspan="2">
              <div class="StandardTextDarkGrayParagraph">
                <i>For information on what these parameters do, refer to the <u><a href="help-faq"
                                                                                   onclick="window.open('/help-faq'); return true;">help
                  pages</a></u>.<br /></i>
              </div>
            </td>
          </tr>
          <tr>
            <td width="33%">
              <div class="StandardTextDarkGrayParagraph">
                <b>Descriptors Per Model:</b>
              </div>
            </td>
            <td align="left" valign="top">
              <div class="StandardTextDarkGrayParagraphNoIndent">
                From:
                <s:textfield id="knnCon02" name="minNumDescriptors" size="5" theme="simple" />
                To:
                <s:textfield id="knnCon03" name="maxNumDescriptors" size="5" theme="simple" />
                Step:
                <s:textfield id="knnCon01" name="stepSize" size="5" theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Number of Runs:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon11" name="numRuns" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Applicability Domain Cutoff:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon19" name="cutoff" size="5" theme="simple" /></td>
          </tr>

          <!-- kNN, Advanced Parameters -->
          <s:if test="user.showAdvancedKnnModeling=='YES'">
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Max. Number of Nearest Neighbors:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon06" name="nearest_Neighbors" size="5"
                                                         theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Percentage of Pseudo Neighbors:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon07" name="pseudo_Neighbors" size="5"
                                                         theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Number of Permutations:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon04" name="numMutations" size="5"
                                                         theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Number of Cycles:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon05" name="numCycles" size="5" theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Log Initial Temperature:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon08" name="t1" size="5" theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Log Final Temperature:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon09" name="t2" size="5" theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Mu:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon10" name="mu" size="5" theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Stop Condition:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="knnCon20" name="stop_cond" size="5" theme="simple" /></td>
            </tr>
          </s:if>
          <!-- Everything up to this point is used by both Continuous and Category kNN. -->

          <!-- The following parameters are JUST for continuous kNN. -->
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b><u><br />Continuous kNN Parameters:</u></b>
              </div>
            </td>
            <td><br />
              <br /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum q<sup>2</sup>:
                </b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon13" name="minAccTraining" size="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum r<sup>2</sup>:
                </b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon14" name="minAccTest" size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum Slope:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon15" name="minSlopes" size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Maximum Slope:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon16" name="maxSlopes" size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Relative_diff_R_R0:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon17" name="relativeDiffRR0" size="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Diff_R01_R02:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCon18" name="diffR01R02" size="5" theme="simple" /></td>
          </tr>
          <!-- End continuous kNN parameters -->

          <!-- The parameters below are specific to Category kNN -->
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b><u><br />Category kNN Parameters:</u></b>
              </div>
            </td>
            <td><br />
              <br /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum Accuracy for Training Set:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCat13" disabled="true" name="minAccTraining"
                                                       size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum Accuracy for Test Set:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnCat14" disabled="true" name="minAccTest" size="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Optimization Method:</b>
              </div>
            </td>
            <td><s:radio name="knnCategoryOptimization" id="knnCategoryOptimization"
                         list="knnCategoryOptimizations" theme="simple" disabled="true" /></td>
          </tr>
          <!-- End Category Specific kNN Parameters  -->

        </table>
      </td>
    </tr>
    </tbody>
  </table>

</s:div>