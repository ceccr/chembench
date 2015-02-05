<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
  <!-- kNN Parameters -->
  <table width="100%" align="center" border="0" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set kNN+ Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td>
        <table>

          <!-- knn+ parameters  -->

          <!-- overall parameters -->
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
                <s:textfield id="knnMinNumDescriptors" name="knnMinNumDescriptors" size="5" theme="simple" />
                To:
                <s:textfield id="knnMaxNumDescriptors" name="knnMaxNumDescriptors" size="5" theme="simple" />
                Step:
                <s:textfield id="knnDescriptorStepSize" name="knnDescriptorStepSize" size="5" theme="simple" />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Applicability Domain Cutoff:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnApplicabilityDomain" name="knnApplicabilityDomain"
                                                       size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Min. Nearest Neighbors:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnMinNearestNeighbors" name="knnMinNearestNeighbors"
                                                       size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Max. Nearest Neighbors:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="knnMaxNearestNeighbors" name="knnMaxNearestNeighbors"
                                                       size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Descriptor Selection Method:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:radio name="knnVarSelectionMethod" value="knnVarSelectionMethod"
                                                   list="#{'SIMULATEDANNEALING':'Simulated Annealing','GENETICALGORITHM':'Genetic Algorithm'}"
                                                   theme="simple" /></td>
          </tr>
          <!-- end overall parameters -->

          <!-- Descriptor Selection Parameters -->
          <!-- Simulated Annealing Parameters (basic) -->
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b><u><br />Simulated Annealing Parameters:</u></b>
              </div>
            </td>
            <td><br />
              <br /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Number of Runs:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="saNumRuns" name="saNumRuns" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Mutation Probability Per Descriptor:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="saNumMutationsPerDescriptor"
                                                       name="saNumMutationsPerDescriptor" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Number of Best Models To Store:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="saNumBestModels" name="saNumBestModels" size="5"
                                                       value="5" theme="simple" /></td>
          </tr>
          <!-- End Simulated Annealing Parameters (basic) -->

          <!-- Simulated Annealing Parameters (advanced) -->
          <s:if test="user.showAdvancedKnnModeling=='YES'">
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Log Initial Temperature:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="saLogInitialTemp" name="saLogInitialTemp" size="5"
                                                         value="5" theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Final Temperature:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="saFinalTemp" name="saFinalTemp" size="5" value="5"
                                                         theme="simple" /></td>
            </tr>
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Temperature Convergence Range:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield id="saTempConvergence" name="saTempConvergence" size="5"
                                                         value="5" theme="simple" /></td>
            </tr>
          </s:if>
          <tr>
            <td colspan="2">
              <div class="StandardTextDarkGrayParagraph">
                <i>Moving the Temperature Decrease Coefficient closer to 0 will make descriptor selection faster
                  but less optimal.<br />
                </i>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Temperature Decrease Coefficient:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="saTempDecreaseCoefficient" value="0.9"
                                                       name="saTempDecreaseCoefficient" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <!-- end Simulated Annealing Parameters (advanced) -->

          <!-- Genetic Algorithm Parameters -->
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b><u><br />Genetic Algorithm Parameters:</u></b>
              </div>
            </td>
            <td><br />
              <br /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Population Size:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield disabled="true" id="gaPopulationSize"
                                                       name="gaPopulationSize" size="5" value="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Maximum Number of Generations:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield disabled="true" id="gaMaxNumGenerations"
                                                       name="gaMaxNumGenerations" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Stop if Stable For This Many Generations:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield disabled="true" id="gaMaxNumGenerations"
                                                       name="gaNumStableGenerations" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <s:if test="user.showAdvancedKnnModeling=='YES'">
            <tr>
              <td>
                <div class="StandardTextDarkGrayParagraph">
                  <b>Minimum Fitness Difference To Proceed:</b>
                </div>
              </td>
              <td align="left" valign="top"><s:textfield disabled="true" id="gaMinFitnessDifference"
                                                         name="gaMinFitnessDifference" size="5" value="5"
                                                         theme="simple" /></td>
            </tr>
          </s:if>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Group Size for Tournament Selection:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield disabled="true" id="gaTournamentGroupSize"
                                                       name="gaTournamentGroupSize" size="5" value="5"
                                                       theme="simple" /></td>
          </tr>
          <!-- end Genetic Algorithm Parameters -->
          <!-- end Descriptor Selection Parameters -->

          <!-- Model Acceptance Parameters -->
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b><u><br />Model Acceptance Parameters:</u></b>
              </div>
            </td>
            <td><br />
              <br /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum
                  <div id="trainingSetMinimumDescription">
                    q<sup>2</sup>
                  </div>
                  for Training Set :
                </b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="minAccTraining" name="knnMinAccTraining" size="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum
                  <div id="testSetMinimumDescription">
                    r<sup>2</sup>
                  </div>
                  for Test Set:
                </b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="minAccTest" name="knnMinAccTest" size="5"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Use Error Based Fit Index:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:checkbox id="knnErrorBasedFit" name="knnErrorBasedFit"
                                                      theme="simple" /></td>
          </tr>
          <!-- end Model Acceptance Parameters -->

          <!-- end knn+ parameters -->

        </table>
      </td>
    </tr>
    </tbody>
  </table>
</s:div>
