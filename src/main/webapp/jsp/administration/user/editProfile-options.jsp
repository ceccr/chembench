<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" import="java.util.*"%>



<!-- USER OPTIONS -->

<s:form action="updateUserOptions" enctype="multipart/form-data" theme="simple">
  <table border="0" align="left" width="680">

    <tr>
      <td height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph2">
          <br />
          <b>Chembench Options</b>
        </p>
      </td>
    </tr>

    <tr>
      <td height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph">
          <br />
          <b>Public Datasets and Predictors</b>
        </p>
      </td>
    </tr>

    <tr>
      <td width="100%" height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph">
          <i>Chembench provides sample datasets and predictors for you to experiment with. <br />If you choose to
            hide them, they will no longer appear on the My Bench, Modeling, and Prediction pages.
          </i>
        </p>
      </td>
    </tr>

    <tr>
      <td width="240">
        <div class="StandardTextDarkGrayParagraph">
          <b>Show Public Datasets:</b>
        </div>
      </td>
      <td align="left" valign="top">
        <div class="StandardTextDarkGrayParagraphNoIndent">
          <s:radio name="showPublicDatasets" value="showPublicDatasets"
            list="#{'NONE':'None','SOME':'Some','ALL':'All'}" />
        </div>
      </td>
    </tr>

    <tr>
      <td width="240">
        <div class="StandardTextDarkGrayParagraph">
          <b>Show Public Predictors:</b>
        </div>
      </td>
      <td align="left" valign="top">
        <div class="StandardTextDarkGrayParagraphNoIndent">
          <s:radio name="showPublicPredictors" value="showPublicPredictors" list="#{'NONE':'None','ALL':'All'}" />
        </div>
      </td>
    </tr>

    <tr>
      <td height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph">
          <br />
          <b>View Options</b>
        </p>
      </td>
    </tr>

    <tr>
      <td width="240">
        <div class="StandardTextDarkGrayParagraph">
          <b>Compounds Per Page on View Dataset: </b>
        </div>
      </td>
      <td align="left" valign="top">
        <div class="StandardTextDarkGrayParagraphNoIndent">
          <s:radio name="viewDatasetCompoundsPerPage" value="viewDatasetCompoundsPerPage"
            list="#{'10':'10','25':'25','50':'50','100':'100','ALL':'All'}" />
        </div>
      </td>
    </tr>

    <tr>
      <td width="240">
        <div class="StandardTextDarkGrayParagraph">
          <b>Compounds Per Page on View Predictions: </b>
        </div>
      </td>
      <td align="left" valign="top">
        <div class="StandardTextDarkGrayParagraphNoIndent">
          <s:radio name="viewPredictionCompoundsPerPage" value="viewPredictionCompoundsPerPage"
            list="#{'10':'10','25':'25','50':'50','100':'100','ALL':'All'}" />
        </div>
      </td>
    </tr>

    <tr>
      <td height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph">
          <br />
          <b>Modeling Options</b>
        </p>
      </td>
    </tr>

    <tr>
      <td width="100%" height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph">
          <i>Under the Modeling tab, there are parameters that would only be of interest to advanced users. <br />
            Before turning this option on, you should familiarize yourself with the fine details of the kNN modeling
            procedure.
          </i>
        </p>
      </td>
    </tr>

    <tr>
      <td width="240">
        <div class="StandardTextDarkGrayParagraph">
          <b>Show Advanced kNN Modeling Controls:</b>
        </div>
      </td>
      <td align="left" valign="top">
        <div class="StandardTextDarkGrayParagraphNoIndent">
          <s:radio name="showAdvancedKnnModeling" value="showAdvancedKnnModeling" list="#{'YES':'Yes','NO':'No'}" />
        </div>
      </td>
    </tr>

    <tr>
      <td width="100%" height="24" align="left" colspan="2">
        <p class="StandardTextDarkGrayParagraph">
          <!-- spacer -->
        </p>
      </td>
    </tr>
    <tr>
      <td></td>
      <td class="" valign="top"><input type="button" name="userAction" id="userAction" onclick="this.form.submit()"
        value="Submit" /> <span id="textarea"></span> <br /></td>
    </tr>

  </table>
</s:form>
</div>
<!-- END USER OPTIONS -->

