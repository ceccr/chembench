<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="#urlOverride != null">
  <s:set name="dataUrl" value="#urlOverride" />
</s:if>
<s:else>
  <s:set name="dataUrl"><s:url action="getDatasets" namespace="/api" /></s:set>
</s:else>
<table class="table table-hover table-bordered datatable" data-object-type="dataset"
     data-url="<s:property value="#dataUrl" />"
    <s:if test="#showAll != true">
      data-paging="data-paging"
    </s:if>
>
  <thead>
  <tr>
    <th data-property="name">Name</th>
    <th data-property="numCompound">Size</th>
    <th data-property="datasetType">Type</th>
    <th data-property="availableDescriptors">Descriptors</th>
    <th data-property="modi">MODI
      <a class="modi-help" tabindex="0" role="button"><span class="glyphicon glyphicon-question-sign"></span></a></th>
    <th data-property="createdTime" data-sort-direction="desc" class="date-created">Date</th>
    <th data-property="public-private" data-transient="data-transient">Public?</th>
  </tr>
  </thead>
  <tbody></tbody>
</table>
