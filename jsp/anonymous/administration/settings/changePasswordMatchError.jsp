<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | Settings</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<script src="javascript/script.js"></script>
<script language="javascript">
function login() {
	alert("Please login");
}

function valid()
{
  var error=document.getElementById("error1");
  error.innerHTML="";
  var p1=document.getElementById("newPs");
  var p2=document.getElementById("rePs");
  if(p1.value!=p2.value)
   {error.innerHTML+="<font size=2 color=red face=arial>The passwords are not identical.</font>"
			 	p1.focus(); p1.value="";p2.value="";
			 	return false; }
   else{return true;}
  
}
</script>
</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br/>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top" background="theme/img/backgrindex.jpg">

      <form action="changePassword.do"><table width="100%" border="0" cellpadding="0" cellspacing="0">
     <tr><td height="120"></td></tr>
     <tr width="100%">
       <td width="100%"   align="center" class="ChangePSText"><%=(String)session.getAttribute("MSG")%></td>
    </tr>
     <tr><td height="40"></td></tr>

   </table>  
 </td>
  </form></tr>



		</td>
			</tr>



	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>

