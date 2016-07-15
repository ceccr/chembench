<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Help | Modeling</title>
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
        <h3>Modeling</h3>

        <img class="interstitial" src="${pageContext.request.contextPath}/assets/images/help/modeling-workflow.png"
             alt="Modeling workflow">

        <p>The steps in creating a chembench predictor are detailed below.</p>

        <h4>Select a Modeling Dataset</h4>

        <p>There are two types of activity files you can set in your Dataset; these are used with different modeling
          methods, so they are separated. "Continuous" activity values vary over a range, while "Category" datasets are
          discrete numbers (typically 1, 2, 3...). Continuous data is used with regression data mining methods, while
          Category data is used with classification methods. So, you will see different options enabled on the Modeling
          page if you pick a Continuous dataset versus a Category one.
        </p>

        <h4>Define Model Descriptors</h4>

        <p>The descriptor type option defines what descriptors will be used to represent your compounds in the modeling
          process. If you want to use other descriptor types besides what Chembench can generate, you can create a
          dataset with your own descriptors in it from the <s:a action="datasets" namespace="/"
                                                                target="_blank">Datasets page</s:a>. Descriptor
          generation and scaling are skipped if you supply your own descriptors.
        </p>

        <p>The generated descriptor values can be scaled by range scaling or auto scaling, or they can be left unscaled.
          Range scaling changes the range of each descriptor: it finds the max and min value of the descriptor,
          subtracts the minimum from each of the descriptor values, then divides each by (max-min) to produce values
          between 0 and 1. In Auto scaling, the mean and standard deviation is found for each descriptor. The mean is
          subtracted from the descriptor's values, and the result is divided by the standard deviation. Auto scaling may
          perform better than range scaling in cases where outliers are expected in the descriptor values. There is some
          debate over whether auto scaling or range scaling should be used in QSAR.
        </p>

        <h4>Select Predictor Type and Parameters</h4>

        <p>At present, there are four model types available:</p>

        <ul>
          <li>Random Forest (as implemented by <a
              href="http://scikit-learn.org/stable/modules/ensemble.html#forests-of-randomized-trees" target="_blank">scikit-learn</a>)
          </li>
          <li>Support Vector Machines (as implemented by <a href="https://www.csie.ntu.edu.tw/~cjlin/libsvm"
                                                            target="_blank">libsvm</a>)
          </li>
          <li><var>k</var>-Nearest Neighbors with Genetic Algorithm descriptor selection ("GA-kNN")</li>
          <li><var>k</var>-Nearest Neighbors with Simulated Annealing descriptor selection ("SA-kNN")</li>
        </ul>

        <h4>Choose Internal Data Splitting Method</h4>

        <p>(<b>Note:</b> for Random Forest predictors, no further internal data splitting is performed, so if Random
          Forest has been selected as the predictor type this step will not be displayed.)
        </p>

        <p>The dataset's external validation set has already been defined. The compounds not in the external set,
          referred to as the "modeling set", will be divided into a training set and a test set for the creation of each
          model. For each such internal split, a model will be built on the training set and applied to the test set.
          (At the end of the modeling process, the good models are collected together into a predictor, and the
          predictor is applied to the external validation set.)
        </p>

        <p>The internal train/test splits can be made randomly or by sphere exclusion. Sphere exclusion is a process
          that chooses training set compounds which are close to the test set compounds in the descriptor space. This
          can help modeling by ensuring that each model will be presented with test cases that the model can reasonably
          predict.
        </p>

        <p>We recommend using sphere exclusion for small datasets (under 300 compounds), and random selection for larger
          datasets (300 or more compounds).
        </p>

        <h4>The Modeling Job</h4>

        <p>Modeling goes through six discrete steps:</p>

        <ul>
          <li>First, descriptors for the selected dataset are scaled.</li>
          <li>Second, the training and test sets are created.</li>
          <li>Third, a <var>y</var>-randomized version of each train-test set is created, where the activity values are
            scrambled; this is set aside for later.
          </li>
          <li>Fourth, the modeling procedure is performed on the train-test sets, generating models.</li>
          <li>Fifth, the modeling procedure is run again, this time on the <var>y</var>-randomized train-test sets; this
            creates the <var>y</var>-randomized models.
          </li>
          <li>Sixth, the models are bundled into a predictor and applied to the external validation set.</li>
        </ul>
        <p>When the job is finished, it can be viewed by clicking on its name in the Predictors section of the My Bench
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
