<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Help | Overview</title>
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
        <h3>Overview</h3>

        <p>Chembench is a web-based tool for QSAR (Quantitative Structure-Activity Relationship) modeling and
          prediction. Chembench doesn't require any programming or scripting knowledge to use. It's an interface that
          lets you skip past the hassles of file management and translating between programs, so you can focus on the
          science of making and applying predictive models.
        </p>

        <p>The first step in using Chembench is to <s:a action="loadRegistrationPage" namespace="/"
                                                        target="_blank">create an account</s:a>. When you submit the
          form, you'll be sent a password by email. If you prefer not to make an account, you can explore the website as
          a guest user; however, your work will not be saved.
        </p>

        <p>When you're logged in, you can get to the other tabs&mdash;My Bench, Datasets, and so on. Once logged in, you
          can also change your password from the <s:a action="editProfile" namespace="/"
                                                      target="_blank">edit profile</s:a> page.
        </p>

        <h4>Chembench Objects</h4>
        <p>
          In Chembench, you can create <b>Datasets</b>, <b>Predictors</b>, and <b>Predictions</b>.
        </p>

        <p>
          A <b>dataset</b> is created from files that you upload. Typically, you will use a set of compounds (an <s:a
            action="file-formats" namespace="/help" anchor="SDF">SDF file</s:a>), along with experimental values for
          those compounds (an <s:a action="file-formats" namespace="/help" anchor="ACT">ACT file</s:a>) to make a
          dataset. Datasets can be created using the Datasets tab. There are also many public datasets available on
          Chembench for you to experiment with.
        </p>

        <p>
          A <b>predictor</b> is a set of models that can be used to make a prediction. You need a modeling dataset (a
          dataset that contains activity values) to make a predictor. You create predictors from the Modeling tab.
        </p>

        <p>
          A <b>prediction</b> is made when a predictor is applied to a dataset. Predictions are made from the Prediction
          tab. You can make several predictions at once on a dataset by selecting more than one predictor.
        </p>

        <p>Whenever you submit a dataset, predictor, or prediction to be created, you will be taken to the My Bench
          page. From there, you can see the progress of the job currently running, the queue of waiting jobs, and all of
          the datasets, predictors, and predictions available. When a dataset job finishes, you can see the results in
          the dataset section. Finished modeling My Bench appear in the predictors section, and finished prediction My
          Bench go to the prediction section of the My Bench page.
        </p>

        <p>
          That should be enough information to get you started&mdash;the other help sections give more detail on each
          part of Chembench, explaining the modeling workflows and the different parameters you can set. You can refer
          back to them as needed, and you can contact us at <a href="mailto:pozefsky@cs.unc.edu">pozefsky@cs.unc.edu</a>
          with any questions.
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
