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
<title>C-CHEMBENCH | Home </title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="/theme/ccbTheme/css/ccbStyle.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" src="javascript/script.js"> </script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<br />
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="407" height="665" align="left" valign="top"><p class="ccbHomeHeadings">
      ACCELERATING CHEMICAL GENOMICS RESEARCH BY CHEMINFORMATICS<br />
        <br />
        <img src="/theme/ccbTheme/images/ccbHorizontalRule.jpg" width="407" height="6" /></p>
      <p align="justify" class="ccbHomeStandard">The Carolina Cheminformatics Workbench (C-ChemBench) is an integrated toolkit developed by the <a href="http://www.ceccr.unc.edu" target="_blank">Carolina Exploratory Center for Cheminformatics Research (CECCR)</a> with the support of the <a href="http://www.nih.gov" target="_blank">National Institutes of Health</a>.</p>
      <p class="ccbHomeStandard"><img src="/theme/ccbTheme/images/ccbHomeMolecule3d.jpg" height="97" /></p>
      <p align="justify" class="ccbHomeStandard">It provides cheminformatics research support to molecular modelers, experimental chemists in the Chemical Synthesis Centers and quantitative biologists in the <a href="http://nihroadmap.nih.gov/molecularlibraries/" target="_blank">Molecular Libraries Screening Centers Network (MLSCN)</a> by integrating robust model builders, property and activity predictors, virtual library of available chemicals with predicted biological and drug-like properties, and special tools for chemical library design.</p>
      <p align="justify" class="ccbHomeStandard">The Workbench is intended in part as a data analytical extension of the <a href="http://pubchem.ncbi.nlm.nih.gov/" target="_blank">PubChem</a>. C-ChemBench enables researchers to mine available chemical and biological data to rationally design or select new compounds or compound libraries with significantly enhanced hit rates in screening experiments.</p>
      <p align="justify" class="ccbHomeStandard"><!-- C-ChemBench Stats Notification Area starts here. -->
		C-ChemBench web site has been visited <%=u.getCounter()%> times and currently there are <%=au.getActiveSessions()%> online users. Thank you for your visit.
		<!-- C-ChemBench Stats end. --></p>
      <p class="ccbHomeHeadings">&nbsp;</p>
      <p>&nbsp;</p>
    <p>&nbsp;</p></td>
    <td valign="top">

    <!-- This table contains the right column of the page -->
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
  
      <tr>
        <td width="41" valign="top">&nbsp;</td> <!-- spacer -->
        <td width="300" rowspan="2" align="left" valign="top">
		
			<!-- LOGIN BOX STARTS HERE -->        
	        <table width="300" align="right" border="1" cellpadding="10" cellspacing="0" bordercolor="#CCCCCC">
	        <tr><td width="284" valign="top" class="ccbLoginBoxHeading">Please login            
			
			<!-- Other menus inside login box -->
	             <p>New Users<br />
	               <span class="ccbHomeStandard">Please <a href="gotoregister.do">register here</a></span></p>
	              <p>FAQ &amp; Help <br />
	               <span class="ccbHomeStandard"><a href="http://ceccr.ibiblio.org/c-chembench/theme/cchembench_userguide.pdf" target="_blank">                CCB User Guide</a><br />
	               <a href="http://ceccr.ibiblio.org/c-chembench/theme/kNNQSAR.pdf" target="_blank">kNN-QSAR User Guide</a><br />
	               <a href="http://chembench-dev.metalab.unc.edu/help.do" target="_blank">Frequently Asked Questions</a></span></p>
	             <p>System Settings<br />
	               <span class="ccbHomeStandard"><a href="admin.do"> Please click here</a></span><br />
	               </td>
	         </tr>
	         <!-- end login box -->
	         </table>
          
          <!-- Still in the table defining the right side column... -->
          </td>
       </tr>
       <tr>
         <td width="41" valign="top">&nbsp;</td> <!-- spacer -->
         <td width="300" rowspan="2" align="left" valign="top">
          <table width="300" border="1" cellpadding="10" cellspacing="0" bordercolor="#CCCCCC">
	          <tr><td>
	          <p><span class="ccbHomeHeadings">COLLABORATORS<br />
	          </span></p>
	          <a href="http://www.chemcomp.com/"><img src="/theme/img/logos/CCG.jpg" /></a> <a href="http://www.talete.mi.it/"><img src="/theme/img/logos/Talete.jpg" /></a> <a href="http://www.chemaxon.com/"><img src="/theme/img/logos/ChemAxon.jpg" /></a> <a href="http://www.edusoft-lc.com"><img src="/theme/img/logos/edusoft.jpg" /></a>
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
