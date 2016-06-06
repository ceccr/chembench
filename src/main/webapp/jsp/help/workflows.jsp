<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Help | Workflows</title>
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
        <h3>Workflows</h3>

        <img class="interstitial" src="${pageContext.request.contextPath}/assets/images/help/overall-workflow.png"
             alt="Overall workflow">

        <p>This flowchart, as presented in <span class="citation"><a
            href="http://onlinelibrary.wiley.com/doi/10.1002/minf.201000061/full" target="_blank">
          Tropsha, A. (2010). Best Practices for QSAR Model Development, Validation, and Exploitation.
          Molecular Informatics, 29(6-7), 476-488.
        </a></span>, forms the basis of the Chembench workflow. Chembench implements the best practices detailed in that
          review.
        </p>

        <p>Chembench breaks that entire process down into three major steps: Dataset creation, Modeling, and Prediction.
          Each of the steps may be used independently. For example, a medicinal chemist or toxicologist could use the
          predictors we have made available in the Prediction section to help determine activity of a specific compound.
        </p>

        <h4>Dataset Creation</h4>

        <img class="interstitial" src="${pageContext.request.contextPath}/assets/images/help/dataset-workflow.png"
             alt="Dataset workflow">
        <p>The dataset workflow handles preprocessing of the compounds, including structure standardization using the <a
            href="https://www.chemaxon.com/products/instant-jchem-suite/instant-jchem" target="_blank">JChem
          standardizer</a>. An external set is specified, and descriptors and visualizations are generated. When the
          dataset creation is finished, the dataset, the selected external set, and visualizations can be viewed and
          downloaded. See the <s:a action="datasets" namespace="/help">Dataset help section</s:a> for more details.
        </p>

        <h4>Modeling</h4>

        <img class="interstitial" src="${pageContext.request.contextPath}/assets/images/help/modeling-workflow.png"
             alt="Modeling workflow">
        <p>The Modeling workflow generates a predictor composed of an ensemble of models. After descriptors have been
          selected, the dataset's modeling set is split into several training and test sets, and one or more models is
          created for each train-test split. Once the predictor has been created, it is used to predict the activity of
          the dataset's external set, so that the predictor's accuracy can be evaluated. In addition, a second predictor
          is created for validation purposes by <var>y</var>-Randomization modeling. These techniques are thoroughly
          described in the publication linked above. See the <s:a action="modeling"
                                                                  namespace="/help">Modeling help section</s:a> for more
          details on modeling in Chembench.
        </p>

        <h4>Prediction</h4>

        <img class="interstitial" src="${pageContext.request.contextPath}/assets/images/help/prediction-workflow.png"
             alt="Prediction workflow">
        <p>To make a prediction, a user selects one or more predictors and a dataset. See the <s:a action="prediction"
                                                                                                   namespace="/help">Prediction help section</s:a>
          for more details.
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
