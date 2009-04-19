<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>
<jsp:useBean class="edu.unc.ceccr.persistence.User" id="user"	scope="session"></jsp:useBean>

<jsp:setProperty name="modelsFiles" property="user" value="<%= user.getUserName() %>"></jsp:setProperty>
	<p class="StandardTextDarkGray">Previously Generated Predictors</p>
			<p class="StandardTextDarkGray">Select a predictor and press the Edit button to view the models associated with the predictor.</p>
			<table>
				<tr>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td class="TableRowText01">Name</td>
					<td class="TableRowText01">Date Created</td>
					<td class="TableRowText01">Modeling Type</td>
					<td class="TableRowText01">Descriptor Generation Method</td>
					<td class="TableRowText01">Download</td>
					
				</tr>
				<logic:iterate id="pred" name="modelsFiles">
					<tr>
						<td>
						<div class="StandardTextDarkGray"><input type="radio" name="selectedPredictorName" onclick="enableEdit()"
						value = '<bean:write name="pred" property="name" />'/></div>
						</td>
						<td class="TableRowText02"><bean:write name="pred" property="name" /></td>
						<td class="TableRowText02"><bean:write name="pred" property="dateCreated" /></td>
						<td class="TableRowText02"><bean:write name="pred" property="modelMethodDisplay" /></td>
						<td class="TableRowText02"><bean:write name="pred" property="descriptorGenerationDisplay" /></td>
						<td class="TableRowText02">
						<!--
						<a href="model?modelName=<bean:write name='pred' property='name' />&user=<bean:write name='user' property='userName' />">
						-->
						<a href="projectFilesServlet?project=<bean:write name='pred' property='name' />&user=<bean:write name='user' property='userName' />&projectType=modelbuilder">
						download</a></td>
						
					</tr>
				</logic:iterate>
				<tr>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td><input type="submit" value = "Edit" id="Edit" disabled="true"></input></td>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
					<td>
					<div class="StandardTextDarkGray"></div>
					</td>
				</tr>
			</table>
