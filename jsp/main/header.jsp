<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
 
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link> 
 
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
   <td><p><a href="home"><img src="/theme/ccbTheme/images/ccbLogo.jpg" border="0" /></a></p></td>
   <td>
   <div class='StandardTextDarkGrayParagraph'>
   
   <s:if test="#session['user']!=null">
		<s:if test="#session['user'].userName!=''">

			<table width="400" border="0" align="right" cellpadding="5" cellspacing="2">
			  <tr>
			  <td align="right" valign="middle">
					<div align="right">	
						<span class="StandardTextDarkGray4" align="right">Logged in as  
		  				<b><s:property value="#session['user'].userName" /></b>.
		  				</span>
					</div>
					<div align="right">
						<a href="/logout">log out</a> 
						<s:if test="#session['user'].userName !='guest'">
							| <a href="editProfile">edit profile</a> 
						</s:if>
						| <a href="help-overview" target="_top">help pages</a>
						<s:if test="#session['user'].isAdmin=='YES'">
							| <a href="admin">admin</a> 
						</s:if>
					</div>
			  </td>
			  </tr> 
			</table>

		</s:if>
		<s:else>
			<table width="400" border="0" align="right" cellpadding="5" cellspacing="2">
			  <tr><td align="right" valign="middle">	
			<p align="right"><span><span class="StandardTextDarkGray4">
			ERROR: Username empty. Logout or restart your browser.  <button onclick="logout()" type="button" class="LoginBoxes1" style="border-style:solid; border-color:gray;border-width:1px">logout</button>
			    &nbsp &nbsp &nbsp</span> </span></td>
			  </tr> 
			</table></p>
		</s:else>
	</s:if>
   </div>
   </td>
  </tr>
</table>