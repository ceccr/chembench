<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<script language="javascript">
function validatePasswordsMatch(){
	var p1 = document.getElementById("").value;
	var p2 = document.getElementById("").value;
	if(p1 == p2){
		return true;
	}
	else{
		alert("Passwords do not match! Try again.");
		return false;
	}
}
</script>

<!-- CHANGE PASSWORD -->

<div id="changePassword">
<s:form action="changePassword" enctype="multipart/form-data" theme="simple">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
   <tr>
   <td height="25" colspan="17" valign="top">
   <table width="100%" border="0" cellpadding="0" cellspacing="0" align="left">
     
<tr>
	<td height="24" align="left" colspan="2">
	<p class="StandardTextDarkGrayParagraph2">
	<br /><b>Change Password</b>
	</p></td>
</tr>

<tr>
	<td width="240">
	<div class="StandardTextDarkGrayParagraph"><b>Current Password: </b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:password name="oldPassword" size="27" /></div>
	</td>
</tr>

<tr>
	<td width="240">
	<div class="StandardTextDarkGrayParagraph"><b>New Password: </b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:password name="newPassword" id="newPassword" size="27" /></div>
	</td>
</tr>

<tr>
	<td width="240">
	<div class="StandardTextDarkGrayParagraph"><b>Confirm New Password: </b></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><s:password name="confirmNewPassword" id="confirmNewPassword" size="27" /></div>
	</td>
</tr>

<tr>
	<td width="240">
	<div class="StandardTextDarkGrayParagraph"></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><!-- spacer --></div>
	</td>
</tr>

<tr>
	<td width="240">
	<div class="StandardTextDarkGrayParagraph"></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><input type="submit" value="Submit" onclick="if(document.getElementById('newPassword').value == (document.getElementById('confirmNewPassword').value)) this.form.submit(); else alert('Passwords do not match. Please retype your new password.');"/></div>
	</td>
</tr>

<tr>
	<td width="240">
	<div class="StandardTextDarkGrayParagraph"></div></td>
	<td align="left" valign="top">
	<div class="StandardTextDarkGrayParagraphNoIndent"><!-- spacer --></div>
	</td>
</tr>

</td></tr></table>
</s:form>
</div>

<!-- END CHANGE PASSWORD -->