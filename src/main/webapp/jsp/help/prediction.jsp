<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Help | Prediction</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Help &amp; Resources</h2>
    <hr>
    <div class="row">
      <div class="col-xs-3">
        <%@ include file="help-nav.jsp" %>
      </div>

      <section id="help-content" class="col-xs-9">
        <h3>Prediction</h3>

        <img class="interstitial" src="${pageContext.request.contextPath}/assets/images/help/prediction-workflow.png"
             alt="Prediction workflow">

        <h4>Selecting Predictors</h4>

        <p>Start by selecting one or more of the predictors. If you have built many predictors on the same modeling
          dataset, you may want to choose all of them, so that you can compare their predictions. In most cases,
          however, you will only need to use one of the predictors.
        </p>

        <p>Once you have selected one or more predictors, you can then choose between single molecule prediction and
          dataset prediction.
        </p>

        <h4>Single Molecule Prediction</h4>

        <p>A prediction can be made on a single molecule instead of an entire dataset. You can enter in a SMILES string
          directly and hit "Predict". You can also draw a molecule using the Java applet, and click "Get SMILES" to
          retrieve its SMILES string for prediction. Single molecule predictions do not require the running of a job;
          the predictions will be calculated immediately and displayed on the Predictions page.
        </p>

        <h4>Dataset Prediction</h4>

        <p>Choose a dataset and specify a similarity cutoff. Setting the cutoff higher (e.g. to 5) will result in more
          predictions, but the predictions will be of lower confidence as they are likely to be outside the models'
          applicability domain. Note that the cutoff value is measured in standard deviations, so it is nonlinear.
        </p>

        <h4>The Prediction Job</h4>

        <p>In a prediction, the descriptor type of each predictor is examined. These descriptors are then generated for
          the dataset to be predicted. The descriptors are normalized according to the ranges used in the predictor's
          training set. Then, the predictors are each applied to the scaled descriptor sets to create predictions. The
          results can be viewed by clicking on the name of the prediction in the Predictions section of the My Bench
          page.
        </p>
      </section>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/help.js"></script>
</body>
</html>
