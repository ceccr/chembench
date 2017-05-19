<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="mybench-predictions-selection" class="checkbox-table">
    <%@ include file="/jsp/mybench/mybench-predictions.jsp" %>
</div>

<p id="mybench-predictions-list-message">
    Currently you have chosen to delete <strong><span id="selected-predictions-count"></span></strong> predictions(s):
</p>

<ul id="mybench-predictions-list"></ul>

<s:form id="delete-predictions" method="post" cssClass="form-horizontal"
        theme="simple">
    <%--<input name="selectedModelId" id="selectedDatasetId" type="hidden">--%>


    <div class="form-group">
        <div class="col-xs-offset-3 col-xs-3">
            <button type="submit" class="btn btn-primary btn-delete">Delete prediction(s)</button>
        </div>
    </div>
</s:form>
