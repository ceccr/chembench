<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
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
      <p align="justify" class="ccbHomeStandard">It provides cheminformatics research support to molecular modelers, medicinal chemists and 
      quantitative biologists by integrating robust model builders, property and activity predictors, 
      virtual libraries of available chemicals with predicted biological and drug-like properties, 
      and special tools for chemical library design. Chembench was initially developed to support 
      researchers in the 
      <a href="http://mli.nih.gov/mli/">Molecular Libraries Probe Production Centers Network (MLPCN)</a> 
      and the Chemical Synthesis Centers.</p>
	  <p class="ccbHomeStandard">Please cite this website using the following URL:
	  <a href="http://chembench.mml.unc.edu">http://chembench.mml.unc.edu</a></p>
		
      <noscript><p>&nbsp;<font color="red">Warning: JavaScript is disabled on your computer. Some parts of Chembench may not work properly. Please enable JavaScript.</font></p></noscript>
 
      <img src="/theme/ccbTheme/images/ccbHorizontalRule.jpg" width="407" height="6" /></p>
      <p align="justify" class="ccbHomeStandard">The Carolina Cheminformatics Workbench (Chembench) is 
      developed by the Carolina Exploratory Center for Cheminformatics Research (CECCR) with the support of the 
      <a href="http://www.nih.gov" target="_blank">National Institutes of Health</a> (grants  
      <a href="http://projectreporter.nih.gov/project_info_details.cfm?aid=7472715&icde=4746318">P20HG003898</a> and 
      <a href="http://projectreporter.nih.gov/project_info_description.cfm?aid=7818406&icde=4746305">R01GM066940</a>) 
      and the Environmental Protection Agency (RD83382501 and RD832720). This website has been developed using 
      grants from the EPA and NIH. Therefore Chembench adheres to their required terms of use.</p>
      </td>
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
					       <td width="50"><label><input name="Submit" type="submit" class="StandardTextDarkGray4" value="login" style="border-style:solid; border-color:blue;border-width:1px;text-align:center;font-size:14px;" /></label>
					   </tr>
				       <tr>
				       <td></td>
			          </table>     
			        </s:form>
			        
			        <table width="250" border="0" ><tr><td>
			        <span class="ccbHomeStandard">
			        Or, <a href="/login?loginName=guest&loginPassword=guest" 
			        onclick="alert('The guest account has all the same capabilities as a full Chembench account. However, all guest data is periodically deleted, and other guests can see the datasets, models, and predictions you create.')">
			        login as a guest</a></span>
			        </td></tr></table>
			        
			</s:if>
			
	        <s:if test="loginFailed=='YES'">
	  			<table width="250" border="0" ><tr><td>
	        	<span class="ccbHomeStandard" >
	        	<font color="red">Username or password incorrect. </font></span>
	        	</td></tr></table> 
	        </s:if>
	        
			<s:if test="user==null">
				<br />
		        <table width="250" border="0" ><tr><td>
		        <span class="ccbHomeStandard">
		        Forget your password? <a href="/forgotPassword">click here</a></span>
		        </td></tr></table> 
			</s:if>
	        
			<s:if test="user!=null">
				<s:if test="user.userName!=''">	    
					<table border="0">
					<tr><td>	
						<span class="ccbHomeStandard">
							Welcome, <s:property value="user.userName" /> &nbsp;
						  	<button onclick="logout()" type="button" class="StandardTextDarkGray4" style="border-style:solid; border-color:gray;border-width:1px">logout</button>
						</span>         
					</td></tr> 
					</table>
				</s:if>
				<s:else>
					<table width="250" border="0" align="right" cellpadding="5" cellspacing="2">
							  <tr><td valign="middle">	
							<p align="right"><span><span class="StandardTextDarkGray4">
							ERROR: Username empty. Logout or restart your browser.  <button onclick="logout()" type="button" class="StandardTextDarkGray4" style="border-style:solid; border-color:gray;border-width:1px;text-align:center;font-size:14px;">logout</button>
							    &nbsp &nbsp &nbsp</span> </span></td></tr> 
					</table>
				</s:else>
			</s:if>
			<!-- LOGIN INPUT FIELD ENDS HERE-->
			
			<!-- Other menus inside login box -->
	             <br />New Users<br />
	               <span class="ccbHomeStandard">Please <a href="loadRegistrationPage">register here</a><br/>
	               </span></p>
	              <p>Help & Links<br />
	               <span class="ccbHomeStandard">
	               <a href="help-overview" target="_blank">Chembench Overview</a><br />
	               <a href="help-workflows" target="_blank">Chembench Workflows & Methodology</a><br />
	               <a href="softwareList" target="_blank">Links to More Cheminformatics Tools</a>
	             </span></p>
      <!-- ChemBench Stats Notification Area starts here. -->
	              <s:if test="showStatistics!=null || showStatistics=='NO'">
		              <p>Statistics<br />
		              <span class="ccbHomeStandard">
		              		<s:property value="visitors" /><br />
		              		<s:property value="userStats" /><br />
		              		<s:property value="jobStats" /><br />
		              		<s:property value="cpuStats" /><br />
		              		<s:property value="activeUsers" /><br />
		              		<s:property value="runningJobs" />
		             </span></p>
	             </s:if>
	  <!-- ChemBench Stats end. -->
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
	          <tr><td valign="top" class="ccbLoginBoxHeading">
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
	          <td><a href="http://www.sunsetmolecular.com/"><img src="/theme/img/logos/sunsetMolecularLogo.png" /></a></td>
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
</html>
