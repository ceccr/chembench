<%@include file="/jsp/help/helpheader.jsp" %>

<div class="outer">
  <div class="ccbHomeStandard">

    <p align="justify" class="ccbHomeStandard">
      <b>Overview</b>

    <p>Chembench is a web-based tool for QSAR modeling and prediction. Chembench doesn't require any programming or
      scripting knowledge to use. It's an interface that lets you skip past the hassles of file management and
      translating between programs, so you can focus on the science of making and applying predictive models.</p>

    <p>The first step in using Chembench is to create an account. Point a browser at http://chembench.mml.unc.edu
      and hit "register here" under the login box. When you submit the form, you'll be sent a password by email. If you
      prefer not to make an account, you can explore the website as a guest user; however, your work will not be
      saved.</p>

    <p>When you're logged in, you can get to the other tabs - MY BENCH, DATASET, and so on. Once logged in, you can
      also change your password from "edit profile" at the top right of the screen.</p>

    <p>
      In Chembench, you can create <b>Datasets</b>, <b>Predictors</b>, and <b>Predictions</b>.
    </p>

    <p>
      A <b>dataset</b> is created from files that you upload. Typically, you will use a set of compounds (an <a
        href="help-fileformats#SDF">SDF</a>), along with experimental values for those compounds (an <a
        href="help-fileformats#ACT">ACT file</a>) to make a dataset. Datasets can be created using the DATASET tab.
      There are also many benchmark datasets available on Chembench for you to experiment with.
    </p>

    <p>
      A <b>predictor</b> is a set of models that can be used to make a prediction. You need a modeling dataset (a
      dataset that contains activity values) to make a predictor. You create predictors from the MODELING tab.
    </p>

    <p>
      A <b>prediction</b> is made when a predictor is applied to a dataset. Predictions are made from the PREDICTION
      tab. You can make several predictions at once on a dataset by selecting more than one predictor.
    </p>

    <p>Whenever you submit a dataset, predictor, or prediction to be created, you will be taken to the MY BENCH
      page. From there, you can see the progress of the job currently running, the queue of waiting jobs, and all of the
      datasets, predictors, and predictions available. When a dataset job finishes, you can see the results in the
      dataset section. Finished modeling MY BENCH appear in the predictors section, and finished prediction MY BENCH go
      to the prediction section of the MY BENCH page.</p>

    <p>
      That should be enough information to get you started -- the other help sections give more detail on each part of
      Chembench, explaining the modeling workflows and the different parameters you can set. You can refer back to them
      as needed, and you can contact us at <a href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a> with any
      questions.
    </p>
    </p>

    <%@include file="/jsp/help/helpcontents.jsp" %>

  </div>
  <div class="includes">
    <%@include file="/jsp/main/footer.jsp" %>
  </div>
</div>
</body>
</html>
