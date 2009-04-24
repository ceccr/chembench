<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<%@ page import="edu.unc.ceccr.global.Constants" %>
<jsp:useBean id="user" class="edu.unc.ceccr.persistence.User" 	scope="session" />

<%@ page import="edu.unc.ceccr.utilities.ActiveUser" %>
<jsp:useBean id="au" class="edu.unc.ceccr.utilities.ActiveUser" scope="request" />
<%@ page import="edu.unc.ceccr.utilities.Utility" %>
<% Utility u=new Utility();%>

<html:html>
<head>
<title>C-CHEMBENCH | My Jobs</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/standard.css" rel="stylesheet" 	type="text/css" />
<link href="theme/miscellaneous.css" rel="stylesheet" type="text/css"/>
<link href="theme/links.css" rel="stylesheet" type="text/css" />
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css" />
<link rel="icon"  href="theme/img/mml.ico" type="image/ico"></link>
<link rel="SHORTCUT ICON" href="theme/img/mml.ico" ></link>

<script src="javascript/yahoo/yahoo.js"></script>
<script src="javascript/connection/connection.js"></script>
<script src="javascript/script.js"></script>
<script src='javascript/overlib.js'>
<script src="javascript/miscellaneous.js"></script>

</head>
<body>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/header.jsp" %></td></tr></table>
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0"><tr><td><%@include file="/jsp/main/centralNavigationBar.jsp" %></td></tr></table>

<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td height="557" colspan="5" valign="top"
			background="theme/img/backgrindex.jpg">
		<p class="StandardTextDarkGrayParagraph"><br><br><b>My Active Jobs</b> <br><br>
		Use following refresh button to check the status of your job. <br>Please do not use your web browser's refresh button.<br>
		<table width="100%" border="0" cellspacing="0" cellpadding="20">
  <tr>
    <td><form action="viewTaskList.do">
			<button type="submit">REFRESH STATUS</button>
		</form></td>
  </tr>
</table>
<b><p class="StandardTextDarkGrayParagraph">Finished Jobs </p></b><br>
<table width="100%" border="0" cellspacing="0" cellpadding="18">
  <tr>
    <td>		
		
		<table >
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Submitted</td>
				<td class="TableRowText01">Status</td>
				<td class="TableRowText01">Started</td>
				<td class="TableRowText01">Finished</td>
			</tr>
			
			<logic:iterate id="task" name="myTasks"
				type="edu.unc.ceccr.persistence.Queue.QueueTask">
				<tr>
					<!-- If the task is finished, provide a link to its output -->
					<td class="TableRowText02">
					<logic:equal name="task" property="state" value="finished">
						<html:link action="/viewmb" paramName="task" paramProperty="id" paramId="id">
						<bean:write name="task" property="jobName" />
						</html:link>
						<br />
					</logic:equal>
					 <logic:notEqual name="task" property="state" value="finished">
						<bean:write name="task" property="jobName" />
						</logic:notEqual>
					</td>
					<td class="TableRowText02"><bean:write name="task" property="submit" /></td>
					<td class="TableRowText02"><bean:write name="task" property="state" />
					 <logic:notEqual name="task" property="state" value="finished">
					 		: <bean:write name="task" property="message" />
					 		<br />
							<html:link action="/cancelJob" paramName="task" paramProperty="id" paramId="id">
							Cancel Job
							</html:link>
							</logic:notEqual>
							</td>
					<td class="TableRowText02"><%=task.getStart() != null ? task.getStart()
							: (char) 0%>
							</td>
							<!--<td></td>  Space for progress indicator. -->
					<td class="TableRowText02"><%=task.getFinish() != null ? task.getFinish()
							: (char) 0%></td>
				</tr>
			
			</logic:iterate>
		</table><br>

			
			
<b><p class="StandardTextDarkGrayNoIndent">Jobs in Queue </p></b><br>
		<table>
			<tr>
				<td class="TableRowText01">Name</td>
				<td class="TableRowText01">Owner</td>
				<td class="TableRowText01">Submitted</td>
				<td class="TableRowText01">Status</td>
			</tr>
			<!-- name="tasks" refers to a function in unc.ceccr.action.ViewTaskListAction.java, which in turn calls the queue. -->
			<logic:iterate id="task" name="tasks"
				type="edu.unc.ceccr.persistence.Queue.QueueTask">
				<tr>
					<%edu.unc.ceccr.task.WorkflowTask wt = (edu.unc.ceccr.task.WorkflowTask) task.task;%>
					<td class="TableRowText02">
					
					<logic:equal name="task" property="state" value="deleted">
					<span onMouseOver="return overlib('Click to delete this denied task.');" onMouseOut="return nd();">
					<% if(task.getUserName().equals(user.getUserName())){%>
					<a href="deleteRecord.do?jobName=<%=task.getJobName()%>"><%=task.getJobName()%></a></span>
					<%}else{%><%=task.getJobName()%><%}%>
					</logic:equal>
					
					<logic:notEqual name="task" property="state" value="deleted">
					<%=task.getJobName()%>
					</logic:notEqual>
					
					</td>
					

					<td class="TableRowText02">
					<%
					String username_output;
					if(task.getUserName().equals(user.getUserName())){
						username_output = task.getUserName();
					}
					else if(u.isAdmin(user.getUserName())){
						username_output = task.getUserName();
					}
					else{
						username_output = "other user";
					}
					
					%>
					<%=
						username_output
					%></td>
									
						
					<td class="TableRowText02"><%=task.getSubmit()%></td>
					<td class="TableRowText02">
					
					<logic:equal name="task" property="state" value="PermissionRequired">
					<span onMouseOver="return overlib('The files you submitted have more than 200 compounds or 10,000 models, you need the special permission to start. please contact the administrator for permission.');" onMouseOut="return nd();">
					<a href="mailto:admin@ceccr.ibiblio.org?subject=Permission Request"><font color="red" size="1">
					PermissionRequired
					</font></a></span></logic:equal>
					
					<logic:equal name="task" property="state" value="started">
					<b><bean:write name="task" property="message" /></b>
					</logic:equal>
					
					<logic:equal name="task" property="state" value="ready">
					<b><bean:write name="task" property="state" /></b>
					</logic:equal>
					
					<br />
					<html:link action="/cancelJob" paramName="task" paramProperty="id" paramId="id">
					<logic:notEqual name="task" property="state" value="started">
					<%=
					username_output.equals("other user") ? "" : "Cancel Job"
					%>
					</logic:notEqual>
					</html:link>
					
					</td>
				</tr>
			</logic:iterate>
		</table>
</td>
  </tr>
</table>
		</td>
	</tr>
	<tr>
	<%@include file ="/jsp/main/footer.jsp" %>
</body>
</html:html>
