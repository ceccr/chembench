<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

	<br />
		<p class="StandardTextDarkGray"><u><b>Descriptor Generation Results</b></u></p><br />
		
<s:iterator value="descriptorGenerationResults">
<p class="StandardTextDarkGray"><b><s:property value="descriptorType" />:</b> <s:property value="generationResult" /></p>
<p class="StandardTextDarkGray"><b>Output:</b> <s:property value="programOutput" /></p>
<p class="StandardTextDarkGray"><b>Errors / Warnings:</b> <s:property value="programErrorOutput" /></p><br />
</s:iterator>

<br />