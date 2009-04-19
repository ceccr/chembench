<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<html:html>
<head>
<title>C-CHEMBENCH | Registration </title>
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/ss.css" rel="stylesheet" type="text/css"></link>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/yahoo_ui/menu.css" rel="stylesheet" type="text/css"></link>
<link href="theme/yahoo_ui/text.css" rel="stylesheet" type="text/css"></link>
<link href="theme/yahoo_ui/container/container.css" rel="stylesheet"	type="text/css"></link>
<link href="theme/standard.css" rel="stylesheet"	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>
<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container_core.js"></script>
<script src="javascript/registerFormValidation.js"></script>

<script>
  function login() {
	window.alert("You need to create an account in order to view this page.");
}
</script>
</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<span id="maincontent">
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrindex.jpg">

		<br/>

<table width="465" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
		<p align="justify" class="StandardTextDarkGrayParagraph"><br />
		
		Welcome to Carolina Cheminformatics Workbench (C-ChemBench)- an integrated toolkit developed by the Carolina Exploratory Center for Cheminformatics
		Research (CECCR) with the support of the National Institutes of Health. <br/><br/><br/>Please register your information here in order to start using C-ChemBench. Thank you. <br/><br/><br/>
        
		</td>
          </tr>
        </table>        
        
        <html:form action="/register.do" focus="firstName">
        <table border="0" align="center" width="680">
        <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">First Name</td>
            <td width="250"><html:text property="firstName" size="30"/></td>
            <td width="250" align="left"><span id="messageDiv1"></span></td></tr>
            <tr height="20">
            <td align="right" width="180" align="left" class="StandardTextDarkGray">Last Name</td>
            <td width="250"><html:text property="lastName" size="30"/></td><td width="250" align="left"><span id="messageDiv2"></span></td></tr>

            <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Type of Organization</td>
            <td width="250"><html:select property="organization">

           <html:option value="Academia">Academia</html:option>
           
            <html:option value="Goverment">Government</html:option>
             <html:option value="Industry">Industry</html:option>

            <html:option value="Other">Other</html:option>

            </html:select>
            </td><td width="250" align="left"></td></tr>

           <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Name of Organization</td><td width="250">
           <html:text property="nameOfOrg" size="30"/></td><td width="250" align="left"><span id="messageDiv4"></span></td></tr>

          <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Position in Organization</td>
          <td width="250"><html:text property="position" size="30"/></td><td width="250" align="left"><span id="messageDiv5"></span></td></tr>

         <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Address</td>
         <td width="250"><html:text property="address" size="30"/></td><td width="250" align="left"><span id="messageDiv6"></span></td></tr>
        <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">City</td>
        <td width="250"><html:text property="city" size="30"/></td><td width="250" align="left"><span id="messageDiv7"></span></td></tr>

        <tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">State/Province</td>
       <td width="250"><html:text property="state" size="30"/></td><td width="250" align="left"><span id="messageDiv8"></span></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Country</td>
<td width="250"><html:select property="country">
<html:option value="USA">United States</html:option>
<html:option value="Russia">Russia</html:option>

<html:option value="China">China</html:option>

<html:option value="India">India</html:option>

<html:option value="France">France</html:option>

<html:option value="Other">Other</html:option>


</html:select>

</td>
<td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Zip Code</td>
<td width="250"><html:text property="zipCode" size="30"/></td><td width="250" align="left"><span id="messageDiv9"></span></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Phone Number</td>
<td width="250"><html:text property="phone" size="30"/></td><td width="250" align="left"><span id="messageDiv11"></td></tr>
<tr height="6"><td align="right" width="180" align="left"></td><td width="250"><font size="1"><u>Please use your orgnization email account</u></font></td>
<td width="250" align="left"><span id="messageDiv11"></span></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Email</td>
<td width="250"><html:text property="email" size="30"/></td><td width="250" align="left"><span id="messageDiv12"></span></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">Work Bench</td>

<td width="250"><html:select property="workbench">
<html:option value="cchem">C-CHEM</html:option>
<html:option value="ctox">C-TOX</html:option>
</html:select>

<tr height="6"><td align="right" width="180" align="left"></td><td width="250"><font size="1"><u>The user name must be at least 4 characters.</u></font></td>
<td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray">User Name</td>
<td width="250"><html:text property="userName" size="30"  /></td><td width="250" align="left"><span id="messageDiv13"></span></td></tr>
<!--
<%! String image()
{
  java.util.Random generator=new java.util.Random();
  String imgNum=Integer.toString(generator.nextInt(4));
return imgNum;
}
%>
-->
<tr height="50"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>
<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray"><u>Verification</u></td><td width="250"></td><td width="250" align="left"></td></tr>
<tr height="20"><td  colspan="2" align="right">

<script>
var RecaptchaOptions = {
   theme : 'white',
   tabindex : 2
};
</script>
<script type="text/javascript"
   src="http://api.recaptcha.net/challenge?k=<%=Constants.RECAPTCHA_PUBLICKEY%>">
</script>

<noscript>
   <iframe src="http://api.recaptcha.net/noscript?k=<%=Constants.RECAPTCHA_PUBLICKEY%>"
       height="40" width="200" frameborder="0"></iframe><br>
   <textarea name="recaptcha_challenge_field" rows="1" cols="35">
   </textarea>
   <input type="hidden" name="recaptcha_response_field"   value="manual_challenge">
</noscript>

</td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" align="left" class="StandardTextDarkGray"></td>
<td width="250">

<logic:equal name="notValid" value="true">
<font size="1" color="red" face="arial">Incorrect input, Please try again!</font>
</logic:equal>

</td><td width="250" align="left"></td></tr>

<tr height="40"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>
<tr height="20"><td align="right" width="180" align="left"></td>
<td width="250"><html:reset onclick="return confirm('Are you sure to reset all the fields?')" value="Reset"></html:reset>&nbsp&nbsp&nbsp&nbsp
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<html:submit property="userAction" onclick="return submitForm1(this,document.getElementById('textarea'));"
								value="Submit" /> </td>
<td width="250" align="left"><span id="textarea"></span></td></tr>
<tr height="20"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>

<tr height="20"><td align="right" width="180" align="left"></td><td width="250"></td><td width="250" align="left"></td></tr>

				
		</table></p></html:form>
		</td>
		</span>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
