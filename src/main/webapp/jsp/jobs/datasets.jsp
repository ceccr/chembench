<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="table table-hover table-bordered datatable" data-url="<s:url action="getDatasets"
namespace="/api" />" data-object-type="dataset">
  <thead>
  <tr>
    <th data-property="name" class="name-column">Name</th>
    <th data-property="numCompound">Size</th>
    <th data-property="datasetType">Type</th>
    <th data-property="availableDescriptors">Descriptors</th>
    <th data-property="modi">MODI
      <a class="modi-help" data-container="body" tabindex="0" role="button"><span
          class="glyphicon glyphicon-question-sign"></span></a></th>
    <th data-property="createdTime" class="date-created">Date</th>
    <th data-property="userName" class="public-private">Public?</th>
  </tr>
  </thead>
  <tbody></tbody>
</table>
