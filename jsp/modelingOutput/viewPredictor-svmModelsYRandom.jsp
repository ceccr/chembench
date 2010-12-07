<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>
	
<!-- SVM Models from y-Randomization -->	

	<br />
	<p class="StandardTextDarkGray"><b><u>Models</u></b></p>
	
	<p class="StandardTextDarkGray">
	<s:if test="svmModels.size==0">
		<s:if test="selectedPredictor.activityType=='CONTINUOUS'">
			No models that passed your r<sup>2</sup> cutoff were generated.<br/>
		</s:if>
		<s:else>
			No models that passed your CCR cutoff were generated.<br/>
		</s:else>
	</s:if>
	<s:elseif test="selectedPredictor.userName=='_all'">
		<br/>Model information is not available for public predictors.<br/>
	</s:elseif>
	<s:else>
		You gots random models!
	</s:else>