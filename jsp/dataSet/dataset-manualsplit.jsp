<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>


<script language="javascript" src="javascript/datasetscripts.js"></script>
<s:div>
	<table width="100%" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>	
		<tr>
			<td width="100%" height="24" align="left" colspan="2">
			<br />
			<p class="StandardTextDarkGrayParagraph2">
			<b>Choose Compounds for External Set</b>
			</p></td>
		</tr>	
		<tr><td colspan="2"><table>
	  <tr><td colspan="2">
	  <div class='StandardTextDarkGrayParagraph'><i>List the compound names for the external set in the box below.</i></div>
	  </td></tr>
	  <tr><td>
	  <s:textarea name="externalCompoundList" id="externalCompoundList" align="left" style="height: 50px; width: 100%" />
	  </td></tr></table>
	  </td></tr>
	  </tbody>
	  </table>
</s:div>