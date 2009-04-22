<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
</head>

<body>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" 	scope="session" />

<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<jsp:useBean id="au" class="edu.unc.ceccr.utilities.ActiveUser" scope="request" />
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<%@ page import="edu.unc.ceccr.persistence.User"%>
<% Utility u=new Utility();%>

<head>
<title>C-CHEMBENCH | Administration Panel</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />

<script src="javascript/script.js"></script>
<script src="javascript/miscellaneous.js"></script>
<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/dom/dom.js"></script>
<script src="javascript/event/event.js"></script>
<script src="javascript/container/container_core.js"></script>
<script src="javascript/admin.js"></script>
<script type="text/javascript">
function showPanel()
{

  if(document.getElementById("updateDiv").style.display=='inline')
{
document.getElementById("updateDiv").style.display='none';}
else{document.getElementById("updateDiv").style.display='inline';}

}
function showPanel2()
{

  if(document.getElementById("updateDiv2").style.display=='inline')
{
document.getElementById("updateDiv2").style.display='none';}
else{document.getElementById("updateDiv2").style.display='inline';}

}
// a new function has been defined for the change password div area.
function showPanel3()
{

  if(document.getElementById("updateDiv3").style.display=='inline')
{
document.getElementById("updateDiv3").style.display='none';}
else{document.getElementById("updateDiv3").style.display='inline';}

}
function showPanelDocs()
{

  if(document.getElementById("docsDiv").style.display=='inline')
{
document.getElementById("docsDiv").style.display='none';}
else{document.getElementById("docsDiv").style.display='inline';}

}

// a new function has been defined for the manage users area.
function showPanel4()
{

  if(document.getElementById("updateDiv4").style.display=='inline')
{
document.getElementById("updateDiv4").style.display='none';}
else{document.getElementById("updateDiv4").style.display='inline';}

}

function confirmation2()
{
if(
window.confirm("Are you sure to delete this notification?"))
{return true;}else{return false;}
}

function checkName()
{
  if(document.getElementById("name").value=="")
{ window.alert("The software name is needed!");
  return false;}
else{return true;}
}
function valid()
{
  var error=document.getElementById("error1");
  error.innerHTML="";
  var p1=document.getElementById("newPs");
  var p2=document.getElementById("rePs");
  if(p1.value!=p2.value)
   {error.innerHTML+="<font size=2 color=red face=arial>The passwords do not match.</font>"
			 	p1.focus(); p1.value="";p2.value="";
			 	return false; }
   else{return true;}
  }
</script>

<%User usr = (User) session.getAttribute("user");%>
</head>

<body  onload="setAcceptance('<%=Constants.ACCEPTANCE%>')">

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br/>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="220" colspan="5" valign="top" background="theme/img/backgrindex.jpg">
		<br/><br/><br/>
<!-- ACCOUNT SETTINGS: CHANGE PASSWORD FORM STARTS HERE -->
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <img src="theme/img/ccb_settings01.gif" /> <br/><br/>
              &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<a href="#" onClick="showPanel3()"><img src="theme/img/ccb_settings02.gif" border=0 /></a>
<div id="updateDiv3" style="display:none">
<form action="changePassword.do">
            &nbsp; &nbsp; &nbsp; <table width="367" height="200" border="0" cellpadding="0" cellspacing="0">
                  <tr>
            <td height="19" align="left" valign="top" ><input type="hidden" name="userName" size="27"  value="<bean:write name="user" property="userName" />              "></td>
          </tr>
<tr>
       <td height="38" align="right" valign="middle" class="ChangePSText" >Current Password: &nbsp;</td>
       <td align="left" valign="middle" ><input type="password" name="oldPs" size="27"></td>
       <td >&nbsp;</td>
</tr>

<tr>
       <td height="38" align="right" valign="middle" class="ChangePSText" >New Password: &nbsp;</td>
       <td align="left" valign="middle" ><input type="password" name="newPs" id="newPs" size="27"></td>
       <td >&nbsp;</td>
</tr>

<tr>
       <td height="38" align="right" valign="middle"  class="ChangePSText">Confirm Password: &nbsp;</td>
       <td align="left" valign="middle" ><input type="password" id="rePs" size="27"></td>
       <td >&nbsp;</td>
</tr>
<tr>
       <td height="62" valign="middle" align="left" ></td>
       <td align="left" valign="middle" ><div id="error1"></div>
         <br />
             <input type="reset" onclick="return confirm('Are you sure to reset all the fields?')"/>
             <input type="submit" value="Submit" onclick="return valid();" /></td></tr></table></form></div>
<!-- THE ADMINISTRATION PANEL STARTS HERE --><% if(Utility.isAdmin(usr.getUserName())){ %>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<!--  "USER MANAGEMENT" AREA STARTS HERE-->
	<tr><table width="100%" height="179" border="0" >
	  <tr><td width="1606" height="85" valign="top"><br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <img src="theme/img/ccb_settings03.gif" /><br><br/>
	    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href="#" onClick="showPanel4()"><img src="theme/img/ccb_settings04.gif" border="0"/></a></td>
	  </tr>
	  <tr>
	    <td height="21" valign="top" class="ChangePSText"><div id="updateDiv4" style="display:none"> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; <b><img src="theme/img/ccbOrangeArrow.gif">&nbsp; New User Approvals: &nbsp; &nbsp;</b><span onmouseover="autoInfoShow()" onmouseout="autoInfoOut()">Automatic
	      <input type="radio" id="automatic" name="automatic" value="automatic" onclick="switchSetting(this)"/></span><span onmouseover="manualInfoShow()" onmouseout="manualInfoOut()"> Manual
	        <input type="radio" id="manual" name="manual" value="manual"  onclick="switchSetting(this)"/></span><br /><br /> <span onmouseover="newuserShow()" onmouseout="newuserOut()">
	         &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<a href="checkNewUser.do" class="ChangePSText"><img src="theme/img/ccbOrangeArrow.gif" border="0"> Pending New User Approvals</a></span> <br />  <br /> 
	        <span onmouseover="viewuserShow()" onmouseout="viewuserOut()">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp;<img src="theme/img/ccbOrangeArrow.gif"><span class="ChangePSText"><a href="viewUsers.do" class="ChangePSText"><b> List of Users</b></a></span> </span></div></td>
      </tr>
      
      <!--  DOCS AREA STARTS HERE-->            
<br />
            <tr><td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href="#" onClick="showPanelDocs()"><img src="theme/img/ccb_settingsDocs.gif" border="0" /></a><br /></td>
</tr>
<tr><td><div id="docsDiv" style="display:none"><blockquote><blockquote>
<!--  "BUILD DATE" AREA STARTS HERE-->              
&nbsp; <img src="theme/img/ccbOrangeArrow.gif">&nbsp;<font class="AdminTableRowText">Build Date: <%=Constants.BUILD_DATE%></font><br /><br />

</div>
 </td></tr>
 <tr><td>&nbsp;</td></tr>
 <br /><br /><br /><br /><br /><br /><br /><br />
<!-- DOCS AREA ENDS HERE -->
      
<!--  "SYSTEM OPTIONS" AREA STARTS HERE-->            
                <tr><td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href="#" onClick="showPanel()"><img src="theme/img/ccb_settings05.gif" border="0" /></a><br /></td>
                </tr>
                <tr><td><div id="updateDiv" style="display:none"><blockquote><blockquote>
<!--  "BUILD DATE" AREA STARTS HERE-->              
                &nbsp; <img src="theme/img/ccbOrangeArrow.gif">&nbsp;<font class="AdminTableRowText">Build Date: <%=Constants.BUILD_DATE%></font><br /><br />
                
<!--  "CHANGE MODELING LIMITS" AREA STARTS HERE-->               
                &nbsp; <img src="theme/img/ccbOrangeArrow.gif">&nbsp;<font class="AdminTableRowText">Change Modeling Limits</font><br /><br />
              
                <form action="updateAdminSettings.do">
                <table  bgcolor="white"  style="width:200px;margin-left:10px">
               <tr><td class="ChangePSText">Max Compounds: <input type="text" value="<%=Constants.MAXCOMPOUNDS%>" name="numCompounds" size="5"></td></tr>
                <tr><td class="ChangePSText">Max models: <input type="text" value="<%=Constants.MAXMODELS%>" name="numModels" size="5"></td></tr>
                 <tr><td>&nbsp; &nbsp; &nbsp;<input type="submit" value="Update"> </td></tr>
                 </table>
                 </form> 
         
 <!--  "SET A NEW LICENSE EXPIRATION KEY" AREA STARTS HERE-->               
              <br /> <img src="theme/img/ccbOrangeArrow.gif">&nbsp; <font class="AdminTableRowText">Set a New Software License Expiration Date</font><br/><br/>
                  
                  <html:form action="updateSoftwareDate.do">
                 
                  <table  bgcolor="white"  style="width:200px;margin-left:20px">
                  <tr><td>
                  <font class="ChangePSText">Software</font></td><td><html:text property="name" size="15"></html:text><br/></td></tr>
                  <tr><td><font class="ChangePSText">Year</font></td><td><html:select property="year">
                  <html:option value="2008">2008</html:option>
                  <html:option value="2009">2009</html:option>

                  <html:option value="2010">2010</html:option>

                  <html:option value="2011">2011</html:option>

                  <html:option value="2012">2012</html:option>
                  <html:option value="2013">2013</html:option>
                  <html:option value="2014">2014</html:option>
                  <html:option value="2015">2015</html:option>
                  <html:option value="2016">2016</html:option>
                    

                  </html:select></td></tr>
                  <tr><td><font class="ChangePSText">Month</font></td><td> <html:select property="month">
            <html:options collection="month" property="value" labelProperty="label" />
        </html:select></td></tr>

                    <tr><td><font class="ChangePSText">Date</font></td><td> <html:select property="date">
            <html:options collection="date" property="value" labelProperty="label" />
        </html:select></td></tr>
                 <tr><td></td><td ><html:submit onclick=" return checkName()">Submit</html:submit>
                 <br>
                 </td></tr>
                 <table width="400"><tr><td><br><%=session.getAttribute("block")%></td></tr></table>
              </table>
        
                  </html:form>
                  
                  </blockquote></blockquote>
                  </div>                 
<!--  SYSTEM OPTIONS AREA ENDS HERE-->  

<!-- JOB MANAGEMENT AREA STARTS HERE -->
	<tr >
		<span id="maincontent">
		<td height="557" colspan="5" valign="top" bgcolor="white"><br/> &nbsp; &nbsp; &nbsp;&nbsp; &nbsp; &nbsp;<a href="#" onClick="showPanel2()"><img src="theme/img/ccb_settings06.gif" border="0" /></a> <br/>
            <div id="updateDiv2" style="display:none"><table width="82%" height="200" align="center" border="0">   
          <tr> <td width="1346" height="81" ><table border="0" width="100%" ><br/>
               <tr><td  width="100%" height="20" valign="top"><br/></td>
               <tr>
             
             <tr><td valign="top" height="52">
             <table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Owner</td>
				<td class="TableRowText01">Submitted</td>
				<td class="TableRowText01"># Compounds</td>
                <td class="TableRowText01"># Models</td>
                <td class="TableRowText01">Permit</td>
                <td class="TableRowText01">Deny</td>
                </tr>
                
                <logic:iterate id="task" name="queuedTasks"	type="edu.unc.ceccr.persistence.Queue.QueueTask">
               	<tr>
					<!--<%edu.unc.ceccr.task.WorkflowTask wt = (edu.unc.ceccr.task.WorkflowTask) task.task;%>-->
					<td class="TableRowText02"><%=task.getJobName()%></td>
					<td class="TableRowText02"><%=task.getUserName()%></td>
					<td class="TableRowText02"><%=task.getSubmit()%></td>
					<td class="TableRowText02"><%=task.getNumCompounds()%></td>
					<td class="TableRowText02"><%=task.getNumModels()%></td>
					<logic:equal name="task" property="state" value="started">
					 <td class="TableRowText02"><font color="red" face="Arial"> Running </font></td>
                    <td class="TableRowText02">&nbsp</td>  
					</logic:equal>
					<logic:notEqual name="task" property="state" value="started">
					
				   <td class="TableRowText02"><a href="permission.do?jobId=<%=task.getId()%>&decision=AGREE">Permit</a></td>
                    <td class="TableRowText02"><a href="permission.do?jobId=<%=task.getId()%>&decision=DENY">Delete</a></td>  
                    </logic:notEqual>              
                </tr>
                </logic:iterate>
                </table>
                

                </div> 
             
             </td></tr>
       
       

       
       
            
             </table>   </tr>   
      </table></blockquote> 
  
        
		</td>
		</span>
	</tr>
	<tr>

<% } %>


	<%@include file ="/jsp/main/footer.jsp" %>

</body>
</html>
