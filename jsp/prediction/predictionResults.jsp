<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head></head>
<body><font color="red"><b>Input: </b><br />
Predictor: <%=session.getAttribute("SmilesPredictPredictor")%><br />
SMILES string: <%=session.getAttribute("SmilesPredictSmiles")%><br />
Cutoff: <%=session.getAttribute("SmilesCutoff")%><br />
<br />
<b>Results:</b><br />
Predicted value: <%=session.getAttribute("SmilesPredictedValue")%><br />
Predicting Models / Total Models: <%=session.getAttribute("SmilesUsedModels")%> / <%=session.getAttribute("SmilesTotalModels")%><br />
Standard deviation: <%=session.getAttribute("SmilesStdDev")%><br />
</font>

<br /><br />
<s:property value="smilesString" /><br />
<s:property value="smilesCutoff" /><br />
<s:iterator value="smilesPredictions">
	<s:property value="predictorName" /><br />
	<s:property value="predictedValue" /><br />
	<s:property value="stdDeviation" /><br />
	<s:property value="predictingModels" /> / <s:property value="totalModels" /><br />
</s:iterator>
</body>
</html>