<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<h3>Descriptor Importance</h3>

<s:if test="selectedPredictor.childType == 'NFOLD'">
  <div class="fold-selection-section">
    View fold(s):
    <ul class="fold-selection">
    <s:iterator begin="0" end="totalFolds" status="foldStatus">
      <s:url action="viewPredictorRandomForestDescriptorImportanceSection" var="foldUrl">
        <s:param name="id"><s:property value="selectedPredictor.id" /></s:param>
        <s:param name="foldNumber"><s:property value="#foldStatus.index" /></s:param>
      </s:url>

      <s:if test="#foldStatus.index == 0">
        <s:set name="foldDisplay" value="'All'" />
      </s:if>
      <s:else>
        <s:set name="foldDisplay" value="#foldStatus.index" />
      </s:else>

      <s:if test="#foldStatus.index == foldNumber">
        <li class="active"><s:property value="#foldDisplay" /></li>
      </s:if>
      <s:else>
        <li><s:a href="%{foldUrl}"><s:property value="#foldDisplay" /></s:a></li>
      </s:else>
    </s:iterator>
    </ul>
  </div>
</s:if>

<table class="descriptor-importance">
  <thead>
    <tr>
      <th>Descriptor</th>
      <th><s:property value="importanceMeasure" /></th>
    </tr>
  </thead>

  <tbody>
    <s:iterator value="importance">
    <tr>
      <td><s:property value="key" /></td>
      <td><s:property value="value" /></td>
    </tr>
    </s:iterator>
  </tbody>
</table>
