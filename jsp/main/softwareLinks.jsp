<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
<title>C-CHEMBENCH | Freely Available Software</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>
 
</head>
<body onload="setTabToHome();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />

	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Free Cheminformatics Tools</b>
			</p></td>
		</tr>
		<tr>
			<td>
			<div class="StandardTextDarkGrayParagraph"><i><!-- space for a description if needed --></i><br /></div></td>
		 </tr>	
		 
		<!-- Table of software and links -->
		<tr><td><div class="StandardTextDarkGrayParagraph">
		<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<!-- <td class="TableRowText01">Type</td> -->
				<td class="TableRowText01">Function</td>
				<td class="TableRowText01">Availability</td>
				<td class="TableRowText01">Reference</td>
				<!-- allow admins to delete bad entries.
				<s:if test="adminUser"><td class="TableRowText01">Delete</td></s:if> -->
			</tr>
			<s:iterator value="softwareLinks">
				<tr>
				<td class="TableRowText02"><a href="<s:property value="url" />"><s:property value="name" /></a></td>
				<!-- <td class="TableRowText02">type</td> -->
				<td class="TableRowText02"><s:property value="function" /></td>
				<td class="TableRowText02"><s:property value="availability" /></td>
				<td class="TableRowText02"><s:property value="reference" /></td>
				<!-- allow admins to delete bad entries.
				<s:if test="adminUser">
				<td class="TableRowText02">Delete (image or something?)
				</td></s:if> -->
				</tr> 
			</s:iterator>
			<tr><td colspan="2">&nbsp;</td></tr>
		</table>

		<!-- Add a Tool -->
		<!-- only allow logged in users to do this -->
		<s:form action="addSoftware" enctype="multipart/form-data" theme="simple">
		<table width="924" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
				<tbody>
					<tr>
						<td align="left" colspan="2">
						<div class="StandardTextDarkGrayParagraph2" align="left"><b>Add New Tool</b></div><br />
						</td>
						<td>
						</td>
				    </tr> 
					<tr>
					<td>
					<table>
					<tr>
						<td height="26">
						<div align="right" class="StandardTextDarkGray"><b>Select a Tool Type: </b></div>
						</td>
						<td align="left" valign="top">
						<!-- <s:select name="selectedDatasetId" list="userDatasets" id="selectedDataset" listKey="fileId" listValue="fileName" />
						-->
						</td>
					</tr>		
					<tr>
						<td height="26">
						<div align="right" class="StandardTextDarkGray"><b>Name: </b></div>
						</td>
						<td align="left" valign="top"><s:textfield name="name" id="name" size="4" /><span id="messageDiv2"></span></td>
					</tr>
					<tr>
						<td height="26">
						<div align="right" class="StandardTextDarkGray"><b>URL: </b></div>
						</td>
						<td align="left" valign="top"><s:textfield name="url" id="url" size="4" /><span id="messageDiv2"></span></td>
					</tr>
					<tr>
						<td height="26">
						<div align="right" class="StandardTextDarkGray"><b>Function: </b></div>
						</td>
						<td align="left" valign="top"><s:textfield name="function" id="function" size="4" /><span id="messageDiv2"></span></td>
					</tr>
					<tr>
						<td height="26">
						<div align="right" class="StandardTextDarkGray"><b>&nbsp;</b></div>
						</td>
						<td align="left" valign="top">
						<input type="button" name="userAction" id="userAction" onclick="submitForm3(this);" value="Add" />
						</td>
					</tr>
					</table></td></tr>
				</tbody>
			</table>
		</s:form>

</div></td></tr></tbody></table>

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>