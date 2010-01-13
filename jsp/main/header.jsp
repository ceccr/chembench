<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
 
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link> 
 
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
   <td><p><a href="home.do"><img src="/theme/ccbTheme/images/ccbLogo.jpg" border="0" /></a></p></td>
   <td>
   <div class='StandardTextDarkGrayParagraph'>
   <logic:present name="user">
	   <logic:notEqual name="user" property="userName" value="">    
			<table width="400" border="0" align="right" cellpadding="5" cellspacing="2">
			  <tr><td align="right" valign="middle">	
			<p align="right"><span><span class="StandardTextDarkGray4">Logged in as  
			  </span></span><span><span	class="StandardTextDarkGray4">
		  <b><bean:write name="user" property="userName" /></b>.
		  &nbsp &nbsp &nbsp &nbsp</span> </span>        
			<p align="right"><a href="#" onclick="logout()">log out</a> | <a href="editProfile">edit profile</a> | <a href="help-overview" target="_top">help pages</a>&nbsp &nbsp &nbsp</p></td>
			  </tr> 
			</table>
	   </logic:notEqual>
		
	   <logic:equal name="user" property="userName" value="">    
			<table width="400" border="0" align="right" cellpadding="5" cellspacing="2">
			  <tr><td align="right" valign="middle">	
			<p align="right"><span><span class="StandardTextDarkGray4">
			ERROR: Username empty. Logout or restart your browser.  <button onclick="logout()" type="button" class="LoginBoxes1" style="border-style:solid; border-color:gray;border-width:1px">logout</button>
			    &nbsp &nbsp &nbsp</span> </span></td>
			  </tr> 
			</table></p>
		</logic:equal>
	</logic:present>
   </div>
   </td>
  </tr>
</table>