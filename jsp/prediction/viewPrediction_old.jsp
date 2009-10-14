<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" scope="session" />
<bean:define id="predValueList" type="java.util.List" name="predictionValues" scope="session"></bean:define>
<html:html>
<head>
<title>C-CHEMBENCH | predictors</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />

<script src="javascript/script.js"></script>
<script language="javascript">
function change()
{var item="<%=session.getAttribute("sortItem")%>";
 var direction="<%=session.getAttribute("direction")%>";

         if(item=="compoundID")
           { if(direction=="ASC")
               {document.getElementById("d1").innerHTML="Compound ID &nbsp;<font color=white> &#8711</font>";}
               else{document.getElementById("d1").innerHTML="Compound ID &nbsp;<font color=white> &#916</font>";}
           }
           if(item=="value")
           { if(direction=="ASC")
               {document.getElementById("d2").innerHTML="Predicted Value <font color=white> &#8711</font>";}
               else{document.getElementById("d2").innerHTML="Predicted Value <font color=white> &#916</font>";}
           }

        if(item=="numModel")
           { if(direction=="ASC")
               {document.getElementById("d3").innerHTML="Number of Models <font color=white> &#916</font>";}
               else{document.getElementById("d3").innerHTML="Number of Models <font color=white> &#8711</font>";}
           }

}

function order(item)
{  var direction="<%=session.getAttribute("direction")%>";
  
  if(direction=="ASC")
   { window.location.href="viewPredOutput.do?sortBy="+item+"&id=<%=session.getAttribute("predictionId")%>&order=DESC&flow=<%=session.getAttribute("flow")%>";}
    else{window.location.href="viewPredOutput.do?sortBy="+item+"&id=<%=session.getAttribute("predictionId")%>&order=ASC&flow=<%=session.getAttribute("flow")%>";}
  }

</script>

</head>
<body onload=change()>
<table width="749" border="0" align="center" cellpadding="0"	cellspacing="0">
	<%@include file="/jsp/main/header.jsp" %>
<%@include file="/jsp/main/centralNavigationBar.jsp" %>
		</td>
		</span>
	</tr>
	
	
	<tr>
		<span id="maincontent">
		<td  colspan="5" valign="top"
			background="theme/img/backgrpredictors.jpg">
			
		
		<br />
		<table width="924" align="center">
			<tr>
			<td><form action="predictors.do"><input type="submit" value="Save Prediction" /></form></td>
			<td>		<form action="delete.do"><input type="submit" value="Delete Prediction" /></form></td>
 <td><a href="file?name=<bean:write name='predictionJob' property='jobName'/>+.txt&user=<bean:write name='user' property='userName' />&predId=<%=session.getAttribute("predictionId")%>&predictor=<bean:write name='predictionJob' property='predictorName' />"><u>Download Prediction</u></a></td>
           <!-- <td> <a href="viewObservedValue.do?id=<%=session.getAttribute("predictionId")%>" target="_blank"> <u>Observed VS Predicted</u><a></td>-->

			</tr>
			<% int startNum=(Integer)session.getAttribute("start"); int endNum=(Integer)session.getAttribute("end"); int totalNum=(Integer)session.getAttribute("total"); %>
			  
           <% int backPoint=(Integer)session.getAttribute("startRange")-20;%>
           <% Long predictionId=(Long)session.getAttribute("predictionId");
			     String sortBy=(String)session.getAttribute("sortItem");
			     Integer BACK=(Integer)session.getAttribute("back");
			     Integer FORWARD=(Integer)session.getAttribute("forth");
			     String order=(String)session.getAttribute("direction");
			%>     
			     
			<tr><td colspan="5"><div align="right" style="margin-right:30px;color:#5F7692;font-family: Arial, Helvetica, sans-serif;font-size: 12px;">
         
           <%=startNum%>&nbsp;-&nbsp;-&nbsp;<%=endNum%>&nbsp;of <%=totalNum%>&nbsp;&nbsp;
          <logic:greaterThan name="back" value="0">
			<a href="viewPredOutput.do?page=<%=backPoint%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=back&BACK=<%=BACK%>&FORWARD=<%=FORWARD%>&order=<%=order%>"><u><font color="#FF6600">&#171;&#171;</font></u></a>
			</logic:greaterThan>
			
			<% for(int j=(Integer)session.getAttribute("startRange");j<=(Integer)session.getAttribute("page");j++) {%>
			
			<a href="viewPredOutput.do?page=<%=j-1%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=stay&startRange=<%=session.getAttribute("startRange")%>&endRange=<%=session.getAttribute("endRange")%>&FORWARD=<%=FORWARD%>&BACK=<%=BACK%>&order=<%=order%>"><u><font color="#FF6600"><%=j%></font></u></a>
			<%}%>
			
		<u><b>	<%=(Integer)session.getAttribute("page")+1%></b></u>
			<% for(int k=(Integer)session.getAttribute("page")+2;k<=(Integer)session.getAttribute("endRange");k++) {%>
			
			<a href="viewPredOutput.do?page=<%=k-1%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=stay&startRange=<%=session.getAttribute("startRange")%>&endRange=<%=session.getAttribute("endRange")%>&FORWARD=<%=FORWARD%>&BACK=<%=BACK%>&order=<%=order%>"><u><font color="#FF6600"><%=k%></font></u></a>
			<%}%>
			
			<% int forwardPoint=(Integer)session.getAttribute("endRange")+1;%>
			<logic:greaterThan name="forth" value="0">
			<a href="viewPredOutput.do?page=<%=forwardPoint%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=forth&FORWARD=<%=FORWARD%>&BACK=<%=BACK%>&order=<%=order%>"><u><font color="#FF6600">&#187;&#187;</font></u></a>
			</logic:greaterThan>
			</div>
			</td></tr>

     
			<tr>
				<td class="TableRowText01">Prediction Name</td>
				<td class="TableRowText01">Date Created</td>
				<td class="TableRowText01">Predictor Used</td>
				<td class="TableRowText01">Database Predicted</td>
				<td class="TableRowText01">Similarity Cutoff</td>
			</tr>
			<tr>
				<td class="TableRowText02"><bean:write name="predictionJob" property="jobName" /></td>
				<td class="TableRowText02"><bean:write name="predictionJob" property="dateCreated" /></td>
				<td class="TableRowText02"><bean:write name="predictionJob" property="predictorName" /></td>
				<td class="TableRowText02"><bean:write name="predictionJob" property="database" /></td>
				<td class="TableRowText02"><bean:write name="predictionJob" property="similarityCutoff" /></td>
			</tr>
			
			
			<tr>
				<td width="70" height="31" align="center" valign="middle" bgcolor="#0D439D" class="TableRowText01">
					<a href="#" onclick=order("compoundID") ><div id="d1" >Comp_ID</div></a>
				</td>
			   	<td width="93" align="center" valign="middle" bgcolor="#0D439D" class="TableRowText01">Structure</td>
                <td width="60" align="center" valign="middle" bgcolor="#0D439D" class="TableRowText01">Standard Deviation </td>
				<td width="95" align="center" valign="middle" bgcolor="#0D439D"
					class="TableRowText01"><a href="#" onclick=order("value")><div id="d2">Predicted Value</div></a></td>
				<td width="94" align="center" valign="middle" bgcolor="#0D439D"	class="TableRowText01">
				<a href="#" onclick=order("numModel")><div id="d3">Number of Models</div></a></td>
			</tr>
			                                                                  
			
			<logic:iterate id="predictionOutput" name="predValueList" type="edu.unc.ceccr.persistence.PredictionValue">
			
				<tr>
					<td height="23" align="center" valign="middle" bgcolor="#0B3C8C"
						class="TableRowText02"><bean:write name="predictionOutput" property="compoundName" /></td>
						
					<td align="center" valign="middle" bgcolor="#0B3C8C"
						class="TableRowText02">
						<a href="#" onclick="window.open('compound3D?compoundId=<bean:write name='predictionOutput' property='compoundName' />&projectType=predictor&user=<bean:write name='user' property='userName' />&project=<bean:write name='predictionJob' property='jobName' />&datasetID=<bean:write name='predictionJob' property='datasetId' />', '<% new java.util.Date().getTime(); %>','width=350, height=350');">
						<img src="/imageServlet?user=<bean:write name='user' property='userName' />&projectType=predictor&compoundId=<bean:write name='predictionOutput' property='compoundName' />&project=<bean:write name='predictionJob' property='jobName' />&datasetID=<bean:write name='predictionJob' property='datasetId' />" border="0"/></a></td>
					</td>
					
					<td align="center" valign="middle" bgcolor="#0B3C8C"
						class="TableRowText02"><bean:write name="predictionOutput"
						property="standardDeviation" /></td>
		
					<td align="center" valign="middle" bgcolor="#0B3C8C"
						class="TableRowText02"><bean:write name="predictionOutput"
						property="predictedValue" /></td>
					<td align="center" valign="middle" bgcolor="#0B3C8C"
						class="TableRowText02"><bean:write name="predictionOutput"
						property="numModelsUsed" /></td>
				</tr>
				
			</logic:iterate>

			<tr><td colspan="5"><div align="right" style="margin-right:30px;color:#5F7692;font-family: Arial, Helvetica, sans-serif;font-size: 12px;">
           <%=startNum%>&nbsp;-&nbsp;-&nbsp;<%=endNum%>&nbsp;of <%=totalNum%>&nbsp;&nbsp;
                      
          <logic:greaterThan name="back" value="0">
			<a href="viewPredOutput.do?page=<%=backPoint%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=back&BACK=<%=BACK%>&FORWARD=<%=FORWARD%>&order=<%=order%>"><u><font color="#FF6600">&#171;&#171;</font></u></a>
			</logic:greaterThan>
			
			<% for(int j=(Integer)session.getAttribute("startRange");j<=(Integer)session.getAttribute("page");j++) {%>
			
			<a href="viewPredOutput.do?page=<%=j-1%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=stay&startRange=<%=session.getAttribute("startRange")%>&endRange=<%=session.getAttribute("endRange")%>&FORWARD=<%=FORWARD%>&BACK=<%=BACK%>&order=<%=order%>"><u><font color="#FF6600"><%=j%></font></u></a>
			<%}%>
			
		<u><b>	<%=(Integer)session.getAttribute("page")+1%></b></u>
			<% for(int k=(Integer)session.getAttribute("page")+2;k<=(Integer)session.getAttribute("endRange");k++) {%>
			
			<a href="viewPredOutput.do?page=<%=k-1%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=stay&startRange=<%=session.getAttribute("startRange")%>&endRange=<%=session.getAttribute("endRange")%>&FORWARD=<%=FORWARD%>&BACK=<%=BACK%>&order=<%=order%>"><u><font color="#FF6600"><%=k%></font></u></a>
			<%}%>
			
			<logic:greaterThan name="forth" value="0">
			<a href="viewPredOutput.do?page=<%=forwardPoint%>&id=<%=predictionId%>&sortBy=<%=sortBy%>&going=forth&FORWARD=<%=FORWARD%>&BACK=<%=BACK%>&order=<%=order%>"><u><font color="#FF6600">&#187;&#187;</font></u></a>
			</logic:greaterThan>
			</div>
			</td></tr>



		</table>
		<!-- <form action="predictors.do"><input type="submit" value="Back to Predictors" /></form>-->
		<!-- <form action="delete.do"><input type="submit" value="Delete this Prediction" /></form>-->
		</td>
		</span>
	</tr>
	<tr>
<%@include file ="/jsp/main/footer.jsp" %>
</body>

</html:html>