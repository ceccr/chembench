<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<%@ page import="edu.unc.ceccr.global.Constants" %>
<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<% Utility u=new Utility();%>

<jsp:useBean id="au" class="edu.unc.ceccr.utilities.ActiveUser" scope="request" />
<html:html>
<head>
<title>CHEMBENCH | welcome</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />

<script src="javascript/script.js"></script>
<script src="javascript/miscellaneous.js"></script>
<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container_core.js"></script>
<script type="text/javascript">
function confirmation()
{
     confirm("Are you sure to delete this user?");
     }

function EmailWindow()
{
 var options = "toolbar=no,scrollbars=no,resizable=no,width=400,height=500;"
window.open("toEmail.do","_blank",options);
}
</script>
</head>

<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr >
		<span id="maincontent">
		<td height="557" colspan="5" valign="top" background="theme/img/backgrindex.jpg">
              <br/> <br/> <br/>
             <p class="StandardTextDarkGrayParagraph"> <a href="admin.do" ><font color="blue"><u>Back to Administration</u></font></a></p> <br/>
             <p class="StandardTextDarkGrayParagraph">Total approved users : <font color="green"> <bean:write name="totalUser" /></font></p>
              <br/> 
             			<table>
				<tr>
					
					<td class="TableRowText01">First Name</td>
					<td class="TableRowText01">Last Name</td>
					<td class="TableRowText01">Orgnization</td>
					<td class="TableRowText01">Org Type</td>
					<td class="TableRowText01">User Name</td>
					<td class="TableRowText01">Country</td>
					<td class="TableRowText01">Email</td>
					<td class="TableRowText01">Delete</td>
				</tr>
				<logic:iterate id="userInfo" name="viewUsers">
					<tr>
						<td class="TableRowText02"><bean:write name="userInfo" property="firstName" /></td>
						<td class="TableRowText02"><bean:write name="userInfo" property="lastName" /></td>
						<td class="TableRowText02"><bean:write name="userInfo" property="orgName" /></td>
						<td class="TableRowText02"><bean:write name="userInfo" property="orgType" /></td>
						<td class="TableRowText02"><bean:write name="userInfo" property="userName" /></td>
						<td class="TableRowText02"><bean:write name="userInfo" property="country" /></td>
						<td class="TableRowText02"><a class="small" href="mailto:<bean:write name="userInfo" property="email" />?subject=From CECCR"><bean:write name="userInfo" property="email" /></a></td>
						<td class="TableRowText02"><a class="small" href="deleteUser.do?userName=<bean:write name='userInfo' property='userName' />" onclick="return confirm('Are you sure to delete this user?')">delete</a></td>
					</tr>
				</logic:iterate>
				<tr height="50">
					<td align="right" colspan="7">
					<div class="StandardTextDarkGray"><button onclick="EmailWindow()">Email All</button></div>
					</td>
					
				</tr>
			</table>
			
		</td>
		
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
