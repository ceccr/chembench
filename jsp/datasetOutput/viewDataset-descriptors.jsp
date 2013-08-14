<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

	<br />
		<p class="StandardTextDarkGray"><u><b>Descriptor Program Results</b></u></p><br />
		
<s:iterator value="descriptorGenerationResults">
    <p class="StandardTextDarkGray"><b><s:property value="descriptorType" />:</b> <s:property value="generationResult" /></p>
    <p class="StandardTextDarkGray"><b>Error Summary:</b> <s:property value="programOutput" /></p>
    <s:if test="generationResult!='Successful'">
        <p class="StandardTextDarkGray"><b>Program Output:</b> <s:property value="programErrorOutput" /></p><br />
    </s:if>
    <s:else>
        <br />
    </s:else>
</s:iterator>

<br />
