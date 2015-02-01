<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@page language="java" %>

<!-- Predictions -->
<br />

<p class="StandardTextDarkGray"><b><u>Prediction Results</u></b></p>

<div style="overflow:auto;">
  <table width="924" align="center">
    <tr>
      <td>
        <p class="StandardTextDarkGray" width="550">Go To Page:
          <s:iterator value="pageNums" status="pageNumsStatus">
            <s:if test="pageNums[#pageNumsStatus.index]==currentPageNumber"><s:property /></s:if>
            <s:else><a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                value='prediction.id' />&currentPageNumber=<s:property />&orderBy=<s:property
                value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><s:property /></a></s:else>
          </s:iterator>
        </p>
      </td>
      <td style="text-align:right;">
        <s:if test="prediction.computeZscore=='YES'">
          <p class="StandardTextDarkGray">
            Change similarity cutoff to
            <s:if test="cutoff==0.0">
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><input type="
                 button"
              value="0&sigma;"
              style="background-color:yellow" /></a>
            </s:if>
            <s:else>
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><input type="
                 button"
              value="0&sigma;" /></a>
            </s:else>
            <s:if test="cutoff==1.0">
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=1")><input
                  type="button" value="1&sigma;" style="background-color:yellow" /></a>
            </s:if>
            <s:else>
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=1")><input
                  type="button" value="1&sigma;" /></a>
            </s:else>
            <s:if test="cutoff==2.0">
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=2")><input
                  type="button" value="2&sigma;" style="background-color:yellow" /></a>
            </s:if>
            <s:else>
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=2")><input
                  type="button" value="2&sigma;" /></a>
            </s:else>
            <s:if test="cutoff==3.0">
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=3")><input
                  type="button" value="3&sigma;" style="background-color:yellow" /></a>
            </s:if>
            <s:else>
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=3")><input
                  type="button" value="3&sigma;" /></a>
            </s:else>
            <s:if test="cutoff==99999.0">
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=99999")><input
                  type="button" value="N/A" style="background-color:yellow" /></a>
            </s:if>
            <s:else>
              <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                  value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=
                 <s:property value='orderBy' />&sortDirection=<s:property value='sortDirection' />&cutoff=99999")><input
                  type="button" value="N/A" /></a>
            </s:else>
          </p>

          <p class="StandardTextDarkGray">
            To see additional predictions, increase similarity cutoff above the listed &sigma;.
          </p>
        </s:if>
        <s:else>
          <font color="red">
            Applicability Domain is not applicable.
          </font>
        </s:else>
      </td>
    </tr>
  </table>

  <div class="double-scroll" width="924">
    <table>
      <!-- header line for predictor names -->
      <tr>
        <td class="TableRowText01" colspan="2"></td>
        <!-- compound ID, sketch -->
        <s:iterator value="predictors" status="predictorsStatus">
          <td class="TableRowText01" colspan="3">   <!-- predictor name -->
            <s:property value="name" />
          </td>
        </s:iterator>
      </tr>
      <!-- header for table -->
      <tr>
        <td class="TableRowText01">Compound ID<br />
          <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
              value='prediction.id' />&currentPageNumber=<s:property
              value='currentPageNumber' />&orderBy=compoundId&sortDirection=asc")><img
              src="theme/img/sortArrowUp.png" /></a>
          <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
              value='prediction.id' />&currentPageNumber=<s:property
              value='currentPageNumber' />&orderBy=compoundId&sortDirection=desc")><img
              src="theme/img/sortArrowDown.png" /></a>
        </td>
        <s:if test="!dataset.sdfFile.isEmpty()">
          <td class="TableRowText01">Structure</td>
        </s:if>
        <s:iterator value="predictors" status="predictorsStatus">
          <td class="TableRowText01">Prediction<br />
            <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=<s:property
                value="name" />&sortDirection=asc")><img src="theme/img/sortArrowUp.png" /></a>
            <a href="#tabs" onclick=loadPredictionValuesTab("viewPredictionPredictionsSection?id=<s:property
                value='prediction.id' />&currentPageNumber=<s:property value='currentPageNumber' />&orderBy=<s:property
                value="name" />&sortDirection=desc")><img src="theme/img/sortArrowDown.png" /></a>
          </td>
          <td class="TableRowText01"><s:if
              test="childType=='NFOLD'">Predicting Folds</s:if><s:else>Predicting Models</s:else></td>
          <td class="TableRowText01">&sigma;<br /></td>
        </s:iterator>
      </tr>
      <!-- body for table -->
      <s:iterator value="compoundPredictionValues" status="compoundPredictionValuesStatus">
        <tr>
          <td class="TableRowText02"><s:property value="compound" /></td>
          <s:if test="!dataset.sdfFile.isEmpty()">
            <td class="TableRowText02">
              <a class="compound_img_a" href="#"
                 onclick="window.open('compound3D?compoundId=<s:property value="compound" />&project=<s:property
                     value="prediction.name" />&projectType=predictor&user=<s:property
                     value="user.userName" />&datasetName=<s:property
                     value="dataset.name" />', '<% new java.util.Date().getTime(); %>','width=400, height=400'); return false;">
                <img
                    src="/imageServlet?user=<s:property value="dataset.userName" />&projectType=predictor&compoundId=<s:property value='compound' />&project=<s:property value="prediction.name" />&datasetName=<s:property value="dataset.name" />"
                    border="0" height="150" /></a>
            </td>
          </s:if>
          <s:iterator value="predictionValues" status="predictionValuesStatus">
            <td class="TableRowText02">
              <s:if test="%{prediction.computeZscore=='YES' && zScore != null}">
                <s:if test="%{cutoff>zScore || cutoff==99999.0}">
                  <s:if test="predictedValue!=null">
                    <s:property value="predictedValue" />
                  </s:if>
                  <s:else>Not Predicted</s:else>
                  <s:if test="standardDeviation!=null"> &plusmn; </s:if> <s:property value="standardDeviation" />
                </s:if>
                <s:else>
                  <font color="red">Compound similarity is above cutoff</font>
                </s:else>
              </s:if>
              <s:else>
                <s:if test="predictedValue!=null">
                  <s:property value="predictedValue" />
                </s:if>
                <s:else>Not Predicted</s:else>
                <s:if test="standardDeviation!=null"> &plusmn; </s:if> <s:property value="standardDeviation" />
              </s:else>
            </td>
            <td class="TableRowText02">
              <s:if test="PredictedValue!=null"><s:property value="numModelsUsed" /> / <s:property
                  value="numTotalModels" /></s:if>
            </td>
            <td class="TableRowText02">
              <s:if test="%{prediction.computeZscore=='YES' && zScore != null}">
                <s:property value="zScore" />&sigma;
              </s:if>
              <s:else>
                N/A
              </s:else>
            </td>
          </s:iterator>
        </tr>
      </s:iterator>
    </table>
  </div>
  <!-- End Predictions -->
