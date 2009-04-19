<html>
<head></head>
<body><b>Input: </b><br />
Predictor: <%=session.getAttribute("SmilesPredictPredictor")%><br />
SMILES string: <%=session.getAttribute("SmilesPredictSmiles")%><br />
Cutoff: <%=session.getAttribute("SmilesCutoff")%><br />
<br />
<b>Results:</b><br />
Predicted value: <%=session.getAttribute("SmilesPredictedValue")%><br />
Predicting Models / Total Models: <%=session.getAttribute("SmilesUsedModels")%> / <%=session.getAttribute("SmilesTotalModels")%><br />
Standard deviation: <%=session.getAttribute("SmilesStdDev")%><br />
</body>
</html>