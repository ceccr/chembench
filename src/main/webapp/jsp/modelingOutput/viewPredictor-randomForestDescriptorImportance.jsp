<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<h3>Descriptor Importance</h3>

<table>
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
