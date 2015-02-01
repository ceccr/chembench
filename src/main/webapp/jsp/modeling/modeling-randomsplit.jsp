<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" %>


<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
  <!-- Datasplit, Random Parameters -->
  <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set Random Splitting Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td>
        <table>

          <tr>
            <td colspan="2">
              <div class="StandardTextDarkGrayParagraph">
                <i>The modeling set will be divided into training and test sets randomly.</i><br />
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Number of Data Splits:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield name="numSplitsInternalRandom"
                                                       id="numSplitsInternalRandom" size="5" theme="simple"
                                                       onchange='calculateRuntimeEstimate()' /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Minimum Test Set Size (percent):</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield name="randomSplitMinTestSize" id="randomSplitMinTestSize"
                                                       size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Maximum Test Set Size (percent):</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield name="randomSplitMaxTestSize" id="randomSplitMaxTestSize"
                                                       size="5" theme="simple" /></td>
          </tr>
          <!--
				<tr>
					<td>
					<div class="StandardTextDarkGrayParagraph"><b>Sample With Replacement (Random Forest Only):</b></div>
					</td>
					<td align="left" valign="top"><s:checkbox name="randomSplitSampleWithReplacement" id="randomSplitSampleWithReplacement" theme="simple"/></td>
				</tr>
				-->

        </table>
      </td>
    </tr>
    </tbody>
  </table>
</s:div>