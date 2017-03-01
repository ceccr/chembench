<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="mybench-model-selection" class="checkbox-table">
    <%@ include file="/jsp/mybench/mybench-models.jsp" %>
</div>

<p id="mybench-model-list-message">
    Currently you have chosen to delete <strong><span id="selected-model-count"></span></strong> model(s):
</p>

<ul id="mybench-model-list"></ul>

<s:form id="delete-model" method="post" cssClass="form-horizontal"
        theme="simple">
    <%--<input name="selectedModelId" id="selectedDatasetId" type="hidden">--%>


    <div class="form-group">
        <div class="col-xs-offset-3 col-xs-3">
            <button type="submit" class="btn btn-primary btn-delete">Delete Model(s)</button>
        </div>
    </div>
</s:form>
