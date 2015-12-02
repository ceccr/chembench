<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<script language="javascript" src="javascript/modeling.js"></script>
<s:div>
  <!-- SVM Parameters -->
  <table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
    <tbody>
    <tr>
      <td width="100%" height="24" align="left" colspan="2"><br />

        <p class="StandardTextDarkGrayParagraph2">
          <b>Set Random Forest Parameters</b>
        </p></td>
    </tr>
    <tr>
      <td>
        <table>

          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Number of Trees per Split:</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="numTrees" name="numTrees" size="5" theme="simple" /></td>
          </tr>
          <tr>
            <td>
              <div class="StandardTextDarkGrayParagraph">
                <b>Seed to use (negative value = generate randomly):</b>
              </div>
            </td>
            <td align="left" valign="top"><s:textfield id="seed" name="seed" size="5" theme="simple" /></td>
          </tr>

        </table>
      </td>
    </tr>
    </tbody>
  </table>
</s:div>
