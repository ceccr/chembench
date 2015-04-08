<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Prediction</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <section>
      <h2>Existing Predictions</h2>

      <%@ include file="/jsp/jobs/predictions.jsp" %>
    </section>

    <hr>
    <section>
      <h2>Create a New Prediction</h2>

      <p>&nbsp;<!-- TODO description here --></p>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select Models</h3>
        </div>
        <div class="panel-body">
          <p>Select the model(s) you want to predict with. To select a model, simply click on its row in the table.
            To deselect it, click its row again.
          </p>

          <p>Currently you have chosen <strong><span id="selected-model-count">0</span></strong> model(s). You
            must choose at least one model.
          </p>

          <hr>
          <div id="prediction-model-selection">
            <%@ include file="/jsp/jobs/models.jsp" %>
          </div>
        </div>
      </div>
    </section>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="/assets/js/prediction.js"></script>
</body>
</html>
