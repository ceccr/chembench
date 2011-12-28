<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
<title>CHEMBENCH | Cheminformatics Tools</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>

</head>
<body onload="setTabToHome();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr><td height="180" colspan="5" valign="top"
			background="theme/img/backgrindex.jpg" STYLE="background-repeat: no-repeat;">
			<br />
		<p class="StandardTextDarkGrayParagraph"><b>Not Logged In</b></p>
		<br />
		<table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr><td>
        <p class="StandardTextDarkGrayParagraph">
		You are seeing this page because you are currently not logged in, or your session has expired.
		<br /><br />
		If you need an account, you can make one from the <a href="home">Home</a> page. Creating an account is quick and free.
		<br /><br />
		If you already have an account, you can log in from the below form as well.
		<br /><br />
		Thanks for using Chembench! If you encounter any problems, please contact us at ceccr@email.unc.edu. 
		</p>
		</td></tr>
		</table>
	</td></tr>
</table>

<table width="924" border="0" align="center" cellpadding="10" cellspacing="0">
	        <tr><td width="284" valign="top" class="ccbLoginBoxHeading">Please login            
			
			<!-- LOGIN INPUT FIELD STARTS HERE. -->
			<s:if test="user==null">
					<br />
					<s:form action="login" enctype="multipart/form-data" method="post" theme="simple">
					  <table width="250" border="0" >	
			           <tr>
				           <td width="90" class="ccbHomeStandard">Username: </td>
				           <td width="110"><s:textfield name="username" id="username" size="8" onfocus="if(this.value=='username'){value=''}" theme="simple" /></td>
				           <td width="50"></td>
			           </tr>
					   <tr>
					   	   <td  width="90" class="ccbHomeStandard">Password: </td>
						   <td width="110"><s:password name="password" id="password" size="8" onfocus="if(this.value=='password'){value=''}" theme="simple" /></td>
					       <td width="50"><label><input name="Submit" type="submit" class="StandardTextDarkGray4" value="login" style="border-style:solid; border-color:blue;border-width:1px;text-align:center;font-size:14px;" /></label></td>
					   </tr>
			          </table>     
			        </s:form>
			        
			        <table width="250" border="0" ><tr><td>
			        <span class="ccbHomeStandard">
			        <% String ipAddress  = request.getHeader("X-FORWARDED-FOR");  
				        if(ipAddress == null)  
				        {  
				          ipAddress = request.getRemoteAddr();  
				        }  
				        String ip = ipAddress.replaceAll("\\.", "");
				   	%>
			        Or, <a href="/login?username=guest&ip=<%=ip %>"
			        onclick="alert('The guest account allows a user to explore the function of Chembench with publicly available datasets, predictions based on a molecule, and modeling using random forest. All guest data is deleted when you leave the site or are inactive for 90 minutes. For additional function, please register.')">
			        login as a guest</a></span>
			        </td></tr></table>
			        
			</s:if>
			
	        <s:if test="user==null">
				<br />
		        <table width="250" border="0" ><tr><td>
		        <span class="ccbHomeStandard">
		        Forget your password? <a href="/forgotPassword">click here</a></span>
		        </td></tr></table> 
			</s:if>
	        
			
			
			<!-- LOGIN INPUT FIELD ENDS HERE-->
			</td>
	         </tr>
	         <!-- end login box -->
	         </table>

<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html>
