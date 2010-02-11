<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

	<br />
		<p class="StandardTextDarkGray"><u><b>Descriptor Generation Results</b></u></p><br />
		
<s:iterator value="descriptorGenerationResults">
<b><s:property value="descriptorType" />:</b> <s:property value="generationResult" /> <br />
<p class="StandardTextDarkGrey"><b>Output:</b> <s:property value="programOutput" /></p>
<p class="StandardTextDarkGrey"><b>Errors:</b> <s:property value="programErrorOutput" /></p><br /><br />
</s:iterator>

<br />