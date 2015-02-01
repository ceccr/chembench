<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
  <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set n-Fold Splitting Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td colspan="2">
        <table>

          <tr>
            <td colspan="2">
              <div class='StandardTextDarkGrayParagraph'>
                <i>An <b>n</b>-fold split will generate <b>n</b> different external test sets. When you use the
                  dataset in modeling, <b>n</b> predictors will be created, one for each external test set. Each
                  external set will contain 1/<b>n</b> of the total dataset, and the external sets will not overlap.
                </i>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Number of splits (<i>n</i>):
                </b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield name="numExternalFolds" id="numExternalFolds"
                                                       theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Use activity binning:</b>
              </div>
            </td>
            <td><s:checkbox name="useActivityBinningNFold" id="useActivityBinningNFold" theme="simple" /></td>
          </tr>
        </table>
      </td>
    </tr>
    </tbody>
  </table>
</s:div>