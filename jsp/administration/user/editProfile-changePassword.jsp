	
<!-- CHANGE PASSWORD -->

<div id="changePassword">
<s:form action="changePassword" enctype="multipart/form-data" theme="simple">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
   <tr>
   <td height="25" colspan="17" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
     <tr><td height="80"></td></tr>

	<tr><td width="200" height="40" valign="middle" align="right" class="ChangePSText" >Password</td>
    <td width="218" align="left" ><s:textfield name="oldPassword" size="27" /></td><td width="300" ></td></tr>

	<tr><td width="200" height="40" valign="middle" align="right" class="ChangePSText" >New Password</td>
    <td width="218" align="left" ><s:textfield name="newPassword" id="confirmNewPassword" size="27" /></td><td width="300" ></td></tr>

	<tr><td width="200" height="40" valign="middle" align="right"  class="ChangePSText">Confirm New Password</td>
    <td width="218" align="left" ><s:textfield name="confirmNewPassword" id="confirmNewPassword" size="27" /></td><td width="300" align="left"><div id="passwordMatchin"></div></td></tr>

<tr><td width="200" height="40" valign="middle" align="right" ></td>
<td width="218" align="right" ><input type="submit" value="Submit" onclick="return validateMatchingPasswords()"/></td><td width="300" ></td>
</tr>

</td></tr></table>
</s:form>
</div>

<!-- END CHANGE PASSWORD -->