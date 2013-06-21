<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
<title>CHEMBENCH | Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
<link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="theme/screen.css" type="text/css" media="screen, projection">
<link rel="stylesheet" href="theme/print.css" type="text/css" media="print">
<link href="theme/standard.css" rel="stylesheet" type="text/css">
<link href="theme/links.css" rel="stylesheet" type="text/css">
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
<link rel="icon" href="/theme/img/mml.ico" type="image/ico">
<link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
<link href="theme/customStylesheet.css" rel="stylesheet" type="text/css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script language="JavaScript" src="javascript/script.js"> </script>

<!-- focus the username input box -->
<script>
$(document).ready(function() {
    $('input[name="username"]').focus();
});
</script>
 
</head>
<body onload="setTabToHome();">
<div class="outer">

    <div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>

    <div class="homeLeft topMarginBench">
        <h2 class="ccbHomeHeadings">ACCELERATING CHEMICAL GENOMICS RESEARCH BY CHEMINFORMATICS</h2>
        <hr {color="" background-color="" #fff="" border="" 1px="" #ff0000="" border-style="" none="" dotted="" }="">
        <div class="ccbHomeStandard">
            <p>Chembench is a free portal that enables researchers to mine available chemical and biological data. Chembench can help researchers
                rationally design or select new compounds or compound libraries with significantly enhanced hit rates in screening experiments.</p>
            <img style="width: 100%; height: 101px;" alt="molecular_image" src="/theme/ccbTheme/images/ccbHomeMolecule3d.jpg">
            <br>
            <br>
            <p>It provides cheminformatics research support to molecular modelers, medicinal chemists and quantitative biologists by integrating
                robust model builders, property and activity predictors, virtual libraries of available chemicals with predicted biological and
                drug-like properties, and special tools for chemical library design. Chembench was initially developed to support researchers in the <a href="http://mli.nih.gov/mli/">Molecular Libraries Probe
                    Production Centers Network (MLPCN)</a> and the Chemical Synthesis
                Centers.</p>
            <p>Please cite this website using the following URL: <a href="http://chembench.mml.unc.edu">http://chembench.mml.unc.edu</a></p>
            <hr {color="" background-color="" #fff="" border="" 1px="" #ff0000="" border-style="" none="" dotted="" }="">
            <p>The Carolina Cheminformatics Workbench (Chembench) is developed by the Carolina Exploratory Center for Cheminformatics Research (CECCR) with the support of the
                <a href="http://www.nih.gov" target="_blank">National Institutes of Health</a> (grants
                <a href="http://projectreporter.nih.gov/project_info_details.cfm?aid=7472715&icde=4746318">P20HG003898</a> and
                <a href="http://projectreporter.nih.gov/project_info_description.cfm?aid=7818406&icde=4746305">R01GM066940</a>
                ) and the Environmental Protection Agency (RD83382501 and RD832720). This website has been developed using grants from the EPA and NIH. Therefore Chembench adheres to their required terms of use.</p>
        </div>
    </div>
    <div class="homeRight topMarginBench bottomMarginDataset">
        <div class="ccbHomeStandard border">
            <span class="ccbLoginBoxHeading">Please login</span>
            <s:if test="user==null">
                <s:form action="login" enctype="multipart/form-data" method="post" theme="simple">
                    <table>
                        <tbody>
                        <tr>
                            <td>Username:</td>
                            <td><s:textfield name="username" id="username" size="12" onfocus="if (this.value=='username'){value=''}" theme="simple"></s:textfield></td>
                        </tr>
                        <tr>
                            <td>Password:</td>
                            <td><s:password name="password" id="password" size="12" onfocus="if (this.value=='password'){value=''}" theme="simple"></s:password></td>
                        </tr>
                        <tr>
                            <td><label><input name="Submit" class="StandardTextDarkGray4" value="login" style="border: 1px solid blue; text-align: center; font-size: 14px;" type="submit"></label></td>
                            <td></td>
                        </tr>
                        </tbody>
                    </table>
                </s:form>
                <% String ipAddress  = request.getHeader("X-FORWARDED-FOR");
                    if(ipAddress == null)
                    {
                        ipAddress = request.getRemoteAddr();
                    }
                    String ip = ipAddress.replaceAll("\\.", "");
                %>
                Or, <a href="/login?username=guest&ip=<%=ip %>"onclick="alert('The guest account allows a user to explore the function of Chembench with publicly available datasets, predictions based on a molecule, and modeling using random forest. All guest data is deleted when you leave the site or are inactive for 90 minutes. For additional function, please register.')">
                login as a guest</a>
                <br>
            </s:if>
            <br>
            <s:if test="loginFailed=='YES'">
                <br>
                <font color="red">Username or password incorrect. </font>
                <br>
            </s:if>
            <s:if test="user==null">
                Forget your password?
                <a href="/forgotPassword">click here</a>
                <br>
            </s:if>
            <s:if test="user!=null">
            <s:if test="user.userName!=''">
                <s:if test="user.userName.contains('guest')">
                    Welcome, guest &nbsp;
                </s:if>
                <s:else>
                    Welcome, <s:property value="user.userName" /> &nbsp;
                </s:else>
                <button onclick="logout()" type="button" class="StandardTextDarkGray4" style="border-style:solid; border-color:gray;border-width:1px">logout</button>
            </s:if>
            <s:else>
            <p align="right"><span><span class="StandardTextDarkGray4">
						ERROR: Username empty. Logout or restart your browser.  <button onclick="logout()" type="button" class="StandardTextDarkGray4" style="border-style:solid; border-color:gray;border-width:1px;text-align:center;font-size:14px;">logout</button>
						&nbsp &nbsp &nbsp</span></span>
                </s:else>
                </s:if>
                <br>
                <span class="ccbLoginBoxHeading">New Users</span>
                <br>
                Please <a href="loadRegistrationPage">register here</a>
                <br>
                <br>
                <span class="ccbLoginBoxHeading">Help &amp; Links</span>
                <br>
                <a href="help-overview" target="_blank">Chembench Overview</a>
                <br>
                <a href="help-workflows" target="_blank">Chembench Workflows &amp; Methodology</a>
                <br>
                <a href="softwareList" target="_blank">Links to More Cheminformatics Tools</a>
                <br>
                <br>
                <s:if test="showStatistics!=null || showStatistics=='NO'">
            <div class="ccbLoginBoxHeading">Statistics</div>
            <s:property value="visitors" />
            <br>
            <s:property value="userStats" />
            <br>
            <s:property value="jobStats" />
            <br>
            <s:property value="cpuStats" />
            <br>
            <s:property value="activeUsers" />
            <br>
            <s:property value="runningJobs" />
            </s:if>
        </div>
        <br>
        <div class="ccbHomeStandard border">
            <span class="ccbLoginBoxHeading">We thank the following commercial vendors:</span>
            <table>
                <tbody>
                <tr>
                    <td><a href="http://www.chemcomp.com/"><img src="/theme/img/logos/CCG.jpg"></a></td>
                    <td><a href="http://www.talete.mi.it/"><img src="/theme/img/logos/Talete.jpg"></a></td>
                </tr>
                <tr>
                    <td><a href="http://www.chemaxon.com/"><img src="/theme/img/logos/ChemAxon.jpg"></a></td>
                    <td><a href="http://www.edusoft-lc.com"><img src="/theme/img/logos/edusoft.jpg"></a></td>
                </tr>
                <tr>
                    <td><a href="http://www.sunsetmolecular.com/"><img src="/theme/img/logos/sunsetMolecularLogo.png"></a></td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div>
        <br />
    </div>

    <div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>

</div>

</body>
</html>
