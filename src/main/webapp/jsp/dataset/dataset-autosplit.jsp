<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>


<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
  <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set Automatic Splitting Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td colspan="2">
        <table>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Use activity binning:</b>
              </div>
            </td>
            <td><s:checkbox name="useActivityBinning" id="useActivityBinning" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>External Set Size:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield name="numExternalCompounds" id="numExternalCompounds"
                                                       size="5" theme="simple" /> <s:select
                name="externalCompoundsCountOrPercent"
                list="#{'Percent':'Percent','Compounds':'Compounds'}" theme="simple" /></td>
          </tr>
        </table>
      </td>
    </tr>
    </tbody>
  </table>
</s:div>