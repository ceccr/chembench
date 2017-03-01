<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

    <div id="mybench-dataset-selection" class="checkbox-table">
        <%@ include file="/jsp/mybench/mybench-datasets.jsp" %>
    </div>

    <p id="mybench-dataset-list-message">
        Currently you have chosen to delete <strong><span id="selected-dataset-count"></span></strong> dataset(s):
    </p>

    <ul id="mybench-dataset-list"></ul>

    <s:form id="delete-dataset" method="post" cssClass="form-horizontal"
            theme="simple">
        <%--<input name="selectedDatasetId" id="selectedDatasetId" type="hidden">--%>


        <div class="form-group">
            <div class="col-xs-offset-3 col-xs-3">
                <button type="submit" class="btn btn-primary btn-delete">Delete Dataset(s)</button>
            </div>
        </div>
    </s:form>
