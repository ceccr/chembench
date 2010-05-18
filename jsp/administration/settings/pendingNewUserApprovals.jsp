<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<% Utility u=new Utility();%>

<jsp:useBean id="newUserInfo" class="edu.unc.ceccr.persistence.User" scope="session" /> 
<jsp:useBean id="au" class="edu.unc.ceccr.utilities.ActiveUser" scope="request" />
<html:html>
<head>
<title>CHEMBENCH | Welcome</title>
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
</head>

<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br/>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
		<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top" background="theme/img/backgrindex.jpg"><br/><br/>
             <p class="StandardTextDarkGrayParagraph"> <a href="admin.do" ><font color="blue"><u>Back to Administration</u></font></a></p>
              <logic:equal name="number" value="0">
       <p class="StandardTextDarkGrayParagraph">No more newly registered users.</p>

       </logic:equal>
       <logic:notEqual name="number" value="0">
	 <p class="StandardTextDarkGray4">There are <font size="4" color="red"><%=session.getAttribute("number")%></font> new users are waiting in the database.</p>
       <br/><br/>
       

        <table border="0" align="center" width="680">
        	<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">First Name</td>
            <td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="firstName"/></td> <td width="250" align="left"></td></tr>
            
            <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Last Name</td>
            <td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="lastName"/></td><td width="250" align="left"></td></tr>

			<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">User Name</td>
            <td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="userName"/></td><td width="250" align="left"></td></tr>

            <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Type of Organization</td>
            <td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="orgType"/></td><td width="250" align="left"></td></tr>

          	<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Name of Organization</td>
          	<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="orgName"/> </td><td width="250" align="left"></td></tr>

          	<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Position in Organization</td>
          	<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="orgPosition"/></td><td width="250" align="left"></td></tr>

         	<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Address</td>
         	<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="address"/></td><td width="250" align="left"></td></tr>
        	
        	<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">City</td>
        	<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="city"/></td><td width="250" align="left"></td></tr>

	        <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">State</td>
    	    <td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="state"/></td><td width="250" align="left"></td></tr>

			<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Country</td>
			<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="country"/></td><td width="250" align="left"></td></tr>
			
			<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Zip Code</td>
			<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="zipCode"/></td><td width="250" align="left"></td></tr>
			
			<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Phone Number</td>
			<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="phone"/></td><td width="250" align="left"></td></tr>
			
			<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray2">Email</td>
			<td width="250" class="StandardTextDarkGray3"><bean:write name="newUserInfo" property="email"/></td><td width="250" align="left"></td></tr>


<tr height="20"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>
<tr height="20"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>
<tr height="20"><td align="right" width="180" align="left"></td>
<td width="250"><input type="button" value="DENY" onclick="confirmation()"/>&nbsp&nbsp&nbsp&nbsp
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<input type="button" value="AGREE" onclick="agree()" /> </td>
<td width="250" align="left"><span id="textarea"></span></td></tr>
<tr height="20"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>

				
		</table></p>
</logic:notEqual>
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
