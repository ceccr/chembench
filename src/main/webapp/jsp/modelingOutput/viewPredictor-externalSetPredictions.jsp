<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sj" uri="/struts-jquery-tags" %>

<div id="externalValidationDiv">
  <s:if test="dataset.splitType=='NFOLD'">
    <p class="StandardTextDarkGray">
      View Fold:
      <!-- need to add 1 because the "All" special value, not "1", takes the [0] index -->
      <s:iterator value="foldNums" status="foldNumsStatus">
        <s:if test="#foldNumsStatus.count == (currentFoldNumber + 1)">
          <b><s:property /></b>
        </s:if>
        <s:else>
          <s:url var="foldUrl" action="viewPredictorExternalValidationSection" escapeAmp="false">
            <s:param name="id" value="selectedPredictor.id" />
            <s:param name="currentFoldNumber" value="%{#foldNumsStatus.index}" />
          </s:url>
          <sj:a href="%{foldUrl}" targets="externalValidationDiv"><s:property /></sj:a>
        </s:else>
      </s:iterator>
    </p>
  </s:if>

  <!-- External Validation Chart -->
  <s:if test="externalValValues.size!=0&&hasGoodModels=='YES'">
    <s:if test="selectedPredictor.activityType=='CONTINUOUS'">
      <p class="StandardTextDarkGray">
        <b>External Validation Chart</b> <br /> <img
          src="imageServlet?project=<s:property value='selectedPredictor.name' />&projectType=modeling&user=<s:property value='selectedPredictor.userName' />&compoundId=externalValidationChart&currentFoldNumber=<s:property value='currentFoldNumber' />"
          WIDTH="650" HEIGHT="650" BORDER="0" ISMAP="ISMAP" USEMAP="#mychart" />
      </p>
      <s:if test="dataset.splitType=='NFOLD'&&currentFoldNumber==0">
         <p class="StandardTextDarkGray">
          Overall R<sup>2</sup> for compounds in all external sets: <b><s:property
            value="selectedPredictor.externalPredictionAccuracy" /></b>
        </p>

        <p class="StandardTextDarkGray">
          Average and standard deviation of R<sup>2</sup> among sets: <b><s:property
            value="selectedPredictor.externalPredictionAccuracyAvg" /></b><br />
        </p>

        <p class="StandardTextDarkGray">
          MAE for compounds in all external sets: <b><s:property value="mae" /></b>
        </p>

        <p class="StandardTextDarkGray">
          Mean and standard deviation of MAE among sets: <b><s:property value="maeSets" /> ± <s:property
            value="stdDev" /></b>
        </p>
      </s:if>
      <s:else>
        <p class="StandardTextDarkGray">
          R<sup>2</sup> for external
          <s:if test="externalValValues.size!=0&&currentFoldNumber!=0">fold <s:property value="currentFoldNumber" />
          </s:if>
          <s:else>set</s:else>
          : <b><s:property value="rSquared" /></b>
        </p>

        <p class="StandardTextDarkGray">
          MAE for external
          <s:if test="externalValValues.size!=0&&currentFoldNumber!=0">fold <s:property value="currentFoldNumber" />
          </s:if>
          <s:else>set</s:else>
          : <b><s:property value="mae" /></b>
        </p>
      </s:else>
    </s:if>
    <s:elseif test="selectedPredictor.activityType=='CATEGORY'">
      <p class="StandardTextDarkGray">
        <b><u>Confusion Matrix</u></b>
      </p>

      <p class="StandardTextDarkGray">Predicted category is based on the consensus prediction of the compound's
        activity rounded to the nearest whole number.</p>

      <s:if test="confusionMatrix.isBinary">
        <table>
          <tr>
            <td><!-- spacer --></td>
            <td class="TableRowText01">Predicted 0</td>
            <td class="TableRowText01">Predicted 1</td>
            <td><!-- spacer --></td>
          <tr>
            <td class="TableRowText01">Observed 0</td>
            <td
                class="TableRowText02"><abbr title="True Negatives">TN</abbr>:&nbsp;
              <s:property value="confusionMatrix.trueNegatives" /></td>
            <td class="TableRowText02"><abbr title="False Positives">FP</abbr>:&nbsp;
              <s:property value="confusionMatrix.falsePositives" /></td>

            <td class="TableRowText01">Specificity:
              <b><s:property value="confusionMatrix.specificityAsString" /></b></td>
          </tr>
          <tr>
            <td class="TableRowText01">Observed 1</td>
            <td class="TableRowText02"><abbr title="False Negatives">FN</abbr>:&nbsp;
              <s:property value="confusionMatrix.falseNegatives" /></td>
            <td class="TableRowText02"><abbr title="True Positives">TP</abbr>:&nbsp;
              <s:property value="confusionMatrix.truePositives" /></td>
            <td class="TableRowText01">Sensitivity:
              <b><s:property value="confusionMatrix.sensitivityAsString" /></b></td>
          </tr>
          <tr>
            <td><!-- spacer --></td>
            <td class="TableRowText01"><abbr title="Negative Predictive Value">NPV</abbr>:&nbsp;
              <s:property value="confusionMatrix.npvAsString" /></td>
            <td class="TableRowText01"><abbr title="Positive Predictive Value">PPV</abbr>:&nbsp;
              <s:property value="confusionMatrix.ppvAsString" /></td>
            <td><!-- spacer --></td>
          </tr>
        </table>
      </s:if>
      <s:else>
        <table>
          <tr>
            <td><!-- spacer --></td>
            <s:iterator value="confusionMatrix.uniqueObservedValues">
              <td class="TableRowText01">Predicted <s:property /></td>
            </s:iterator>
          </tr>

          <s:iterator value="confusionMatrix.matrix" var="row" status="i">
            <tr>
              <td class="TableRowText01">Observed <s:property value="#i.index" /></td>
              <s:iterator value="row">
                <td class="TableRowText02"><s:property /></td>
              </s:iterator>
            </tr>
          </s:iterator>
        </table>
      </s:else>

      <p class="StandardTextDarkGray">Total number of predictions:
        <b><s:property value="confusionMatrix.totalCorrect + confusionMatrix.totalIncorrect" /></b></p>

      <p class="StandardTextDarkGray">Total number of correct predictions:
        <b><s:property value="confusionMatrix.totalCorrect" /></b></p>

      <p class="StandardTextDarkGray">Total number of incorrect predictions:
        <b><s:property value="confusionMatrix.totalIncorrect" /></b></p>

      <p class="StandardTextDarkGray">Accuracy (total correct / total predictions):
        <b><s:property value="confusionMatrix.accuracyAsString" /></b></p>

      <s:if test="dataset.splitType == 'NFOLD' && currentFoldNumber == 0">
        <p class="StandardTextDarkGray">
          Overall CCR for compounds in all external sets: <b><s:property
            value="selectedPredictor.externalPredictionAccuracy" /></b>
        </p>

        <p class="StandardTextDarkGray">
          Average and standard deviation of CCR among sets: <b><s:property
            value="selectedPredictor.externalPredictionAccuracyAvg" /></b><br />
        </p>
      </s:if>
      <s:else>
        <p class="StandardTextDarkGray">
          CCR for external set: <b><s:property value="confusionMatrix.ccrAsString" /></b>
        </p>
      </s:else>
    </s:elseif>
    <p class="StandardTextDarkGray">Note: If there are fewer than 5 compounds in your external set, the external
      validation will not be a reliable measure of predictor performance.</p>
  </s:if>
  <br />
  <!-- End External Validation Chart -->

  <!-- External Validation Compound Predictions -->
  <p class="StandardTextDarkGray">
    <b><u>Predictions for External Validation Set</u></b>
  </p>

  <s:if test="hasGoodModels=='NO'">
    <br />

    <p class="StandardTextDarkGray">No models were generated that passed your cutoffs.</p>
    <br />
    <br />
  </s:if>
  <s:elseif test="externalValValues.size==0">
    <br />

    <p class="StandardTextDarkGray">There were no compounds in the dataset's external validation set.</p>
    <br />
    <br />
  </s:elseif>
  <s:else>
  <table width="100%" align="center" class="sortable" id="externalSetPredictions">
  <!--DWLayoutTable-->
  <tr>
    <th class="TableRowText01">Compound ID</th>
    <s:if test="!dataset.sdfFile.isEmpty()">
      <th class="TableRowText01_unsortable">Structure</th>
    </s:if>
    <th class="TableRowText01">Observed Value</th>
    <th class="TableRowText01">Predicted Value</th>
    <th class="TableRowText01">Residual</th>
    <th class="TableRowText01">Predicting Models / Total Models</th>
  </tr>

  <s:iterator value="externalValValues" status="extValStatus">
    <tr>
      <td class="TableRowText02"><s:property value="compoundId" /></td>
      <s:if test="!dataset.sdfFile.isEmpty()">
        <th class="TableRowText01_unsortable">Structure</th>
      </s:if>
      <th class="TableRowText01">Observed Value</th>
      <th class="TableRowText01">Predicted Value</th>
      <th class="TableRowText01">Residual</th>
      <th class="TableRowText01">Predicting Models / Total Models</th>
    </tr>

    <s:iterator value="externalValValues" status="extValStatus">
      <tr>
        <td class="TableRowText02"><s:property value="compoundId" /></td>
        <s:if test="!dataset.sdfFile.isEmpty()">
          <td class="TableRowText02">
            <img
                src="imageServlet?projectType=modeling&user=<s:property value='dataset.userName' />&project=<s:property value='selectedPredictor.name' />&compoundId=<s:property value='compoundId' />&datasetName=<s:property value='dataset.name' />"
                border="0" height="150" />
          </td>
        </s:if>
        <td class="TableRowText02"><s:property value="actualValue" /></td>
        <td class="TableRowText02"><s:if test="numModels>=2">
          <s:property value="predictedValue" /> &#177; <s:property value="standDev" />
        </s:if> <s:elseif test="numModels==1">
          <s:property value="predictedValue" />
        </s:elseif></td>
        <td class="TableRowText02"><s:property value="residuals[#extValStatus.index]" /></td>
        <s:if test="selectedPredictor.childType=='NFOLD'">
          <td class="TableRowText02"><s:property value="numModels" /> / <s:property value="numTotalModels" /></td>
        </s:if>
        <s:else>
          <td class="TableRowText02"><s:property value="numModels" /> / <s:property
              value="selectedPredictor.numTestModels" /></td>
        </s:else>
      </tr>
    </s:iterator>
    </s:else>
  </table>
  <br />
  <!-- End External Validation Compound Predictions -->
</div>
