<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@page language="java" import="java.util.*" %>
<html>
<head></head>
<body><font color="red"><b>Input: </b><br />
SMILES string: <s:property value="smilesString" /><br />
<s:property value="smilesCutoff" /><br />
<br />
<s:iterator value="smilesPredictions">
<b>Results from <s:property value="predictorName" />:</b><br />
Predicted value: <s:property value="predictedValue" /><br />
Predicting Models / Total Models: <s:property value="predictingModels" /> / <s:property value="totalModels" /><br />
Standard deviation: <s:property value="stdDeviation" /><br />
<br />
</s:iterator>
</font>
</body>
</html>