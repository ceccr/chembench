<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/jsp/main/head.jsp" %>
    <title>Chembench | MCRA</title>
</head>
<body>
<div id="main" class="container">
    <%@ include file="/jsp/main/header.jsp" %>

    <div id="content">
        <%--<ul class="nav nav-tabs">--%>
            <%--<li class="active"><a href="#new-predictions" data-toggle="tab"><h5>Create a New Prediction</h5></a></li>--%>
            <%--<li><a href="#existing-predictions" data-toggle="tab"><h5>Existing Predictions</h5></a></li>--%>
        <%--</ul>--%>

        <div class="tab-content">
            <div id="new-predictions" class="tab-pane active">
                <section>
                    <h2>Create a New Prediction</h2>

                    <hr>
                        <div id="modeling-dataset-selection" class="panel panel-primary">
                            <div class="panel-heading">
                                <h3 class="panel-title">Select a Modeling Dataset</h3>
                            </div>
                            <div class="panel-body">
                                <p>Select a modeling dataset to create a model from. (You can create more datasets using the
                                    <b><s:a action="datasets">Dataset Creation</s:a></b> page.)
                                </p>

                                <s:set name="urlOverride"><s:url action="getMcraModelingDatasets" namespace="/api" /></s:set>
                                <div class="radio-table">
                                    <%@ include file="/jsp/mybench/mybench-datasets.jsp" %>
                                </div>
                            </div>
                        </div>

                        <div id="make-prediction" class="panel panel-primary" style="display: block;">
                            <div class="panel-heading">
                                <h3 class="panel-title">Make a Prediction</h3>
                            </div>

                            <div class="panel-body">
                                <p>Now you can make a prediction using the modeling dataset you have selected above. You can either predict a single
                                    compound at a time, or predict an entire dataset at once. Use the tabs to change the selected prediction
                                    mode.
                                </p>

                                <ul class="nav nav-tabs">
                                    <li class="active"><a href="#single-compound" data-toggle="tab">Predict a Single Compound</a></li>
                                    <li><a href="#dataset" data-toggle="tab">Predict a Dataset</a></li>
                                </ul>

                                <div class="tab-content">
                                    <div id="single-compound" class="tab-pane active">
                                        <h3>Predict a Single Compound</h3>

                                        <div class="row">
                                            <div id="jsme-container" class="col-xs-5">
                                                <div id="jsme"></div>

                                                <div class="button-group">
                                                    <button id="jsme-clear" class="btn">Clear</button>
                                                    <button id="jsme-smiles-predict" class="btn btn-primary">Get SMILES and Predict</button>
                                                </div>
                                            </div>

                                            <div class="col-xs-7">
                                                <p>Enter a molecule in SMILES format, e.g. <kbd>C1=CC=C(C=C1)CC(C(=O)O)N</kbd> (phenylalanine).
                                                    Or, use the applet on the left to draw a molecule, then click "Get SMILES and Predict".
                                                </p>

                                                <s:form id="predict-compound" action="makeSmilesPredictionMcra" method="get" cssClass="form-horizontal"
                                                        theme="simple">
                                                    <input name="selectedModelingDatasetId" type="hidden">

                                                    <div class="form-group">
                                                        <label for="smiles" class="col-xs-6 control-label">SMILES:</label>
                                                        <div class="col-xs-6">
                                                            <input id="smiles" name="smiles" class="form-control">
                                                        </div>
                                                    </div>

                                                    <div class="form-group">
                                                        <div class="col-xs-offset-6 col-xs-6">
                                                            <button type="submit" class="btn btn-primary">Predict</button>
                                                        </div>
                                                    </div>
                                                </s:form>

                                                <hr>
                                                <div id="prediction-results">
                                                    <p class="help-block">Your prediction results will appear here.</p>
                                                </div>
                                            </div>
                                            <hr>
                                        </div>
                                    </div>

                                    <div id="dataset" class="tab-pane">
                                        <h3>Predict a Dataset</h3>

                                        <p>Select a dataset to predict.</p>

                                        <s:set name="urlOverride"><s:url action="getMcraDatasets" namespace="/api" /></s:set>
                                        <div id="prediction-dataset-selection" class="radio-table">
                                            <%@ include file="/jsp/mybench/mybench-datasets.jsp" %>
                                        </div>

                                        <s:form id="predict-dataset" action="makeDatasetPredictionMcra" method="post" cssClass="form-horizontal"
                                                theme="simple" style="display: block">
                                            <input name="selectedPredictingDatasetId" id="selectedPredictingDatasetId" type="hidden">
                                            <input name="selectedModelingDatasetId" id="selectedModelingDatasetId" type="hidden">

                                            <div class="form-group">
                                                <label for="jobName" class="col-xs-3 control-label">Prediction name:</label>
                                                <div class="col-xs-6">
                                                    <input name="jobName" id="jobName" class="form-control" required="required">
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <div class="col-xs-offset-3 col-xs-3">
                                                    <button type="submit" class="btn btn-primary" >Predict Dataset</button>
                                                </div>
                                            </div>
                                        </s:form>

                                        <hr>
                                        <div id="dataset-prediction-results">
                                            <p class="help-block">Your prediction results will appear here.</p>
                                        </div>

                                    </div>
                                </div>
                            </div>
                        </div>

                </section>
            </div>

            <div id="existing-predictions" class="tab-pane">
                <section>
                    <h2>Existing Predictions</h2>
                    <%--<%@ include file="/jsp/mybench/mybench-models.jsp" %>--%>
                </section>
            </div>
        </div>
    </div>

    <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/jsme/jsme.nocache.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/mcra.js"></script>
</body>
</html>
