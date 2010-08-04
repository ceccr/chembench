<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<% ActiveUser au= new ActiveUser();%>
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<%@ page import="edu.unc.ceccr.persistence.User"%>
<% Utility u=new Utility();%>

<html:html>
<head>
<title>CHEMBENCH | Home </title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>
 
</head>
<body onload="setTabToHome();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="407" height="665" align="left" valign="top"><p class="ccbHomeHeadings">
      ACCELERATING CHEMICAL GENOMICS RESEARCH BY CHEMINFORMATICS<br />
        <br />
        <img src="/theme/ccbTheme/images/ccbHorizontalRule.jpg" width="407" height="6" /></p>
      <p align="justify" class="ccbHomeStandard">Chembench is a free portal that enables researchers 
      to mine available chemical and biological data. Chembench can help researchers rationally design or 
      select new compounds or compound libraries with significantly enhanced hit rates in screening experiments.</p>
      <p class="ccbHomeStandard"><img src="/theme/ccbTheme/images/ccbHomeMolecule3d.jpg" height="97" /></p>
      <p align="justify" class="ccbHomeStandard">It provides cheminformatics research support to molecular 
      modelers, experimental chemists in the Chemical Synthesis Centers and quantitative biologists in the 
      <a href="http://mli.nih.gov/mli/">Molecular Libraries Probe Production Centers Network (MLPCN)</a> 
      by integrating robust model builders, property and activity predictors, virtual library of available 
      chemicals with predicted biological and drug-like properties, and special tools for chemical library 
      design.</p>
      
      <!--
      It provides cheminformatics research support to molecular modelers, medicinal chemists and quantitative 
      biologists by integrating robust model builders, property and activity predictors, virtual library of 
      available chemicals with predicted biological and drug-like properties, and special tools for chemical 
      library design.

		Chembench was initially developed to support researchers in the Molecular Libraries Probe Production Centers 
		Network (MLPCN) and the Chemical Synthesis Centers.
		<br />
		Please cite this website using the following URL:
<a href="http://chembench.mml.unc.edu">http://chembench.mml.unc.edu</a>
		
      -->
      
        <img src="/theme/ccbTheme/images/ccbHorizontalRule.jpg" width="407" height="6" /></p>
      <p align="justify" class="ccbHomeStandard">The Carolina Cheminformatics Workbench (Chembench) is 
      developed by the Carolina Exploratory Center for Cheminformatics Research (CECCR) with the support of the 
      <a href="http://www.nih.gov" target="_blank">National Institutes of Health</a> (grants  
      <a href="http://projectreporter.nih.gov/project_info_details.cfm?aid=7472715&icde=4746318">P20HG003898</a> and 
      <a href="http://projectreporter.nih.gov/project_info_description.cfm?aid=7818406&icde=4746305">R01GM066940</a>) 
      and the Environmental Protection Agency (RD83382501 and RD832720). This website has been developed using 
      grants from the EPA and NIH. Therefore Chembench adheres to their required terms of use.</p>
      <!-- ChemBench Stats Notification Area starts here. -->
      <p align="justify" class="ccbHomeStandard">
		Chembench has been visited <%=u.readCounter()%> times and currently there are <%=au.getActiveSessions()%> users logged in. <%=u.getJobStats()%> Thank you for your visit.
	  </p>
	  <!-- ChemBench Stats end. -->
      <p class="ccbHomeHeadings">&nbsp;</p>
      <p>&nbsp;<noscript><font color="red">Warning: JavaScript is disabled on your computer. Some parts of Chembench may not work properly. Please enable JavaScript.</font></noscript></p>
    <p>&nbsp;</p></td>
    <td valign="top">

    <!-- This table contains the right column of the page -->
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
  
      <tr>
        <td width="41" valign="top">&nbsp;</td> <!-- spacer -->
        <td width="300" align="left" valign="top">
		
			<!-- LOGIN BOX STARTS HERE -->        
	        <table width="300" align="left" border="1" cellpadding="10" cellspacing="0" bordercolor="#CCCCCC">
	        <tr><td width="284" valign="top" class="ccbLoginBoxHeading">Please login            
			
			
			<!-- LOGIN INPUT FIELD STARTS HERE. -->
			<logic:notPresent name="user">
					<form id="form1" name="form1" method="post" action="submitLogin.do">
					  <table width="250" border="0" >		          
			           <tr>
			           <td><label><input name="loginName" type="text" class="LoginBoxes1" size="12" value="username" onfocus="if(this.value=='username'){value=''}"/></label></td>
				       <td><label><input name="loginPassword" type="password" class="LoginBoxes1" size="12" value="password" onfocus="if(this.value=='password'){value=''}"/></label></td>
				       </tr>
				       <tr>
						<td><label> 
						  <input name="Submit" type="submit" class="StandardTextDarkGray4" value="login"
						style="border-style:solid; border-color:blue;border-width:1px" /></label></td></tr>        
			          </table>     
			        </form>
			        <!-- <table width="250" border="0" ><tr><td>
			        <span class="ccbHomeStandard">
			        Forget your password? <a href="/getPassword.do">click here</a></span>
			        </td></tr></table> -->
			</logic:notPresent>
		
			<logic:present name="user">
				<logic:notEqual name="user" property="userName" value="">    
					<table border="0"><tr><td>	
					<span><span	class="ccbHomeStandard">Welcome, 
					  </span></span><span><span	class="ccbHomeStandard">
					  <bean:write name="user" property="userName" />
					  <button onclick="logout()" type="button" class="StandardTextDarkGray4" style="border-style:solid; border-color:gray;border-width:1px">logout</button>
					    </span> </span>        
					</td></tr> 
					</table>
				</logic:notEqual>
	
				<logic:equal name="user" property="userName" value="">    
				<table width="250" border="0" align="right" cellpadding="5" cellspacing="2">
						  <tr><td valign="middle">	
						<p align="right"><span><span class="StandardTextDarkGray4">
						ERROR: Username empty. Logout or restart your browser.  <button onclick="logout()" type="button" class="StandardTextDarkGray4" style="border-style:solid; border-color:gray;border-width:1px">logout</button>
						    &nbsp &nbsp &nbsp</span> </span></td></tr> 
				</table>
				</logic:equal>
			</logic:present>
			<!-- LOGIN INPUT FIELD ENDS HERE-->
			
			<!-- Other menus inside login box -->
	             <br />New Users<br />
	               <span class="ccbHomeStandard">Please <a href="loadRegistrationPage">register here</a><br/>
	             <!-- or, <a href="anonPredict.do">make simple predictions</a> (no login required) --> 
	               </span></p>
	              <p>FAQ &amp; Help <br />
	               <span class="ccbHomeStandard">
	               <a href="help-overview" target="_blank">Chembench Overview</a><br />
	               <a href="help-faq" target="_blank">Frequently Asked Questions</a><br />
	               <a href="softwareList" target="_blank">Links to More Cheminformatics Tools</a>
	             </span></p>
	         </tr>
	         <!-- end login box -->
	         </table>
          
          <!-- Still in the table defining the right side column... -->
          </td>
       </tr>
       <tr>
         <td width="41" valign="top">&nbsp;</td> <!-- spacer -->
         <td width="300" align="left" valign="top">
         <br />
          <table width="300" border="1" cellpadding="5" cellspacing="0" bordercolor="#CCCCCC">
	          <tr><td>
	          We thank the following commercial vendors:
	          <br />
	          <br />
	          <table><tr>
	          <td><a href="http://www.chemcomp.com/"><img src="/theme/img/logos/CCG.jpg" /></a></td>
	          <td><a href="http://www.talete.mi.it/"><img src="/theme/img/logos/Talete.jpg" /></a></td>
	          </tr><tr>
	          <td><a href="http://www.chemaxon.com/"><img src="/theme/img/logos/ChemAxon.jpg" /></a></td>
	          <td><a href="http://www.edusoft-lc.com"><img src="/theme/img/logos/edusoft.jpg" /></a></td>
	          </tr><tr>
	          <td><a href="http://www.sunsetmolecular.com/"><img src="/theme/img/logos/sunset-molecular-logo.png" /></a></td>
	          <td></td>
	          </tr></table>
	          </td></tr>
	 	  </table>
   		 </td>
  	  </tr>
</table>

	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
