<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-nested" prefix="nested"%>

<html>
<logic:present name="ACTDataSet" scope="session">
<body bgcolor="#ffffff"  onload="displayChart()">
</logic:present>
<logic:notPresent name="ACTDataSet" scope="session">
<body bgcolor="#ff4400">
</logic:notPresent>
</html>