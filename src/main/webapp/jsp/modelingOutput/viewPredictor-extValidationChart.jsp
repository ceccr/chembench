<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" import="java.util.*"%>
<html>
<head>
<title>Chembench | View Predictor</title>

<link href="theme/ss.css" rel="stylesheet" type="text/css" />
<link href="theme/ajaxtabs.css" rel="stylesheet" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon" href="theme/img/mml.ico" type="image/ico" />
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" />

<script language="JavaScript" src="/javascript/chembench.js"></script>
<script src="/javascript/jquery-1.4.2-development.js" type="text/javascript"></script>
<script src="/javascript/jtip.js" type="text/javascript"></script>

<link type="text/css" rel="stylesheet" media="all" href="theme/jtip.css" />

</head>

<body onload="setTabToMyBench();">
  <table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
    <tr>
      <td>
        <table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
          <%@include file="/jsp/main/header.jsp"%>
          <%@include file="/jsp/main/centralNavigationBar.jsp"%>
          </td>
          </span>
          </tr>

        </table>
      </td>
    </tr>
  </table>

  <!-- BEGIN image and image map -->
  <p>
    <map id="example_map" name="example_map">
      <area id="cp2" class="jTip" shape="rect" coords="390,250,420,280"
        href="compoundTooltip?compoundId=2&observedValue=1.0&predictedValue=0.989&width=400" alt="Compound 2" />
      <area id="cp1" class="jTip" shape="rect" coords="260,450,270,480"
        href="compoundTooltip?compoundId=1&observedValue=7.0&predictedValue=0.666&width=400" alt="Compound 1" />
    </map>
    <img id="mapImage" usemap="#example_map" src="theme/img/tooltip/activityChart.jpg" border="0" />
  </p>
  <!-- END image and image map -->


</body>
</html>
