<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>

<html>
<head>
	<sx:head debug="false" cache="false" compressed="true" />
<title>C-CHEMBENCH | My Jobs</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" type="text/css" />
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/script.js"></script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrindex.jpg">
		<p class="StandardTextDarkGrayParagraph"><br><br><b>My Jobs</b> <br><br>
		<table width="465" border="0" cellspacing="0" cellpadding="0">
        <tr><td>
        <p class="StandardTextDarkGrayParagraph">
		Every completed job you have run on C-Chembench is available on this page. 
		You can also track progress of running jobs using the job queue.
		</p>
		</td></tr>
		</table>
		<table width="100%" border="0" cellspacing="0" cellpadding="20">
  <tr>
    <td></td>
  </tr>
</table>

	<!-- Finished Dataset Jobs -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Finished Dataset Jobs</b>
			</p></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>Click on the name of dataset to visualize it.</i><br />
			
			<table>
				<tr>
					<td class="TableRowText01">Name</td>
					<td class="TableRowText01">Date Created</td>
					<td class="TableRowText01">Number of Compounds</td>
					<td class="TableRowText01">Type</td>
					<td class="TableRowText01">Public/Private</td>
					<td class="TableRowText01">Download</td>
					<td class="TableRowText01">Delete</td>
				</tr>
				<s:iterator value="userDatasets">
					<tr>
					<td class="TableRowText02">
						<a href="viewDatasetFlash.do?fileName=<s:property value="fileName" />&isPublic=<s:if test="userName=='_all'">true</s:if><s:else>false</s:else>">
						<s:property value="fileName" />
						</a>
					</td>
					<td class="TableRowText02"><s:date name="createdTime" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="numCompound" /></td>
					<td class="TableRowText02"><s:property value="modelType" /></td>
					<td class="TableRowText02"><s:if test="userName=='_all'">Public</s:if><s:else>Private</s:else></td>
					<td class="TableRowText02"><a href="">download</a></td>
					<td class="TableRowText02"><s:if test="userName=='_all'"></s:if><s:else><a href="">delete</a></s:else></td>
					</tr> 
				</s:iterator>
				<br />
			</table>
			</div>
			</td>
		 </tr>	
	</tbody>
	</table>
	<br />

	<!-- Finished Modeling Jobs -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Finished Modeling Jobs</b>
			</p></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>Click on the name of a predictor to analyze the modeling results.</i><br />
			
			<table>
				<tr>
					<td class="TableRowText01">Name</td>
					<td class="TableRowText01">Date Created</td>
					<td class="TableRowText01">Modeling Method</td>
					<td class="TableRowText01">Descriptor Type</td>
					<td class="TableRowText01">Dataset</td>
					<td class="TableRowText01">Public/Private</td>
					<td class="TableRowText01">Download</td>
					<td class="TableRowText01">Delete</td>
				</tr>
				<s:iterator value="userPredictors">
					<tr>
					
					<s:url id="predictorLink" value="/viewPredictor" includeParams="none">
						<s:param name="id" value='predictorId' />
					</s:url>
					<td class="TableRowText02"><s:a href="%{predictorLink}"><s:property value="name" /></s:a></td>
					<td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="modelMethodDisplay" /></td>
					<td class="TableRowText02"><s:property value="descriptorGenerationDisplay" /></td>
					<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
					<td class="TableRowText02"><s:if test="userName=='_all'">Public</s:if><s:else>Private</s:else></td>
					<td class="TableRowText02"><a href="">download</a></td>
					<td class="TableRowText02"><s:if test="userName=='_all'"></s:if><s:else><a href="">delete</a></s:else></td>
					</tr> 
				</s:iterator>
				<br />
			</table>
			
			</div>
			</td>
		 </tr>	
	</tbody>
	</table>
	<br />
	
	<!-- Finished Prediction Jobs -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Finished Prediction Jobs</b>
			</p></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>Click on the name of a prediction to see the results.</i><br />
			<table>
				<tr>
					<td class="TableRowText01">Name</td>
					<td class="TableRowText01">Date Created</td>
					<td class="TableRowText01">Dataset</td>
					<td class="TableRowText01">Predictor</td>
					<td class="TableRowText01">Download</td>
					<td class="TableRowText01">Delete</td>
				</tr>
				<s:iterator value="userPredictions">
					<tr>
					
					<s:url id="predictionLink" value="/viewPrediction.do" includeParams="none">
						<s:param name="id" value='predictionId' />
					</s:url>
					<td class="TableRowText02"><s:a href="%{predictionLink}"><s:property value="jobName" /></s:a></td>
					<td class="TableRowText02"><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
					<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
					<td class="TableRowText02"><s:property value="predictorName" /></td>
					<td class="TableRowText02"><a href="">download</a></td>
					<td class="TableRowText02"><a href="">delete</a></td>
					</tr> 
				</s:iterator>
				<br />
			</table>
			</div>
			</td>
		 </tr>	
	</tbody>
	</table>
	<br />
	
	<!-- Running Jobs -->
	<table width="94%" frame="border" rules="none" align="center" cellpadding="0" cellspacing="4" colspan="2">
		<tbody>			
		<tr>
			<td height="24" align="left" colspan="2">
			<p class="StandardTextDarkGrayParagraph2">
			<br /><b>Job Queue</b>
			</p></td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="StandardTextDarkGrayParagraph"><i>Running jobs are displayed below. Use the REFRESH STATUS button to update the list.</i><br /></div></td>
		 </tr>	
		 <tr><td></td><td>
		 <form action="jobs">
			<button type="submit">REFRESH STATUS</button>
		</form>
		</td></tr>
		<tr><td colspan="2"><div class="StandardTextDarkGrayParagraph">
		<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Owner</td>
				<td class="TableRowText01">Job Type</td>
				<td class="TableRowText01">Dataset</td>
				<td class="TableRowText01">Number of Compounds</td>
				<td class="TableRowText01">Number of Models</td>
				<td class="TableRowText01">Time Submitted</td>
				<td class="TableRowText01">Status</td>
				<td class="TableRowText01">Cancel</td>
			</tr>
			<s:iterator value="userQueueTasks">
				<tr>
				<td class="TableRowText02"><a href=""><s:property value="jobName" /></a></td>
				<td class="TableRowText02"><a href=""><s:property value="userName" /></a></td>
				<td class="TableRowText02"><a href=""><s:property value="jobTypeString" /></a></td>
				<td class="TableRowText02"><s:property value="datasetDisplay" /></td>
				<td class="TableRowText02"><a href=""><s:property value="numCompounds" /></a></td>
				<td class="TableRowText02"><a href=""><s:property value="numModels" /></a></td>
				<td class="TableRowText02"><s:date name="start" format="yyyy-MM-dd HH:mm" /></td>
				<td class="TableRowText02"><s:property value="state" /></td>
				<td class="TableRowText02"><a href="">cancel</a></td>
				</tr> 
			</s:iterator>
			<br />
		</table>
	</table>
	</div>
	<br>

	</td></tr>
</table>
		</td>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>