<%@ taglib prefix="s" uri="/struts-tags" %>

<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | Model Creation</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>Model Creation</h2>

    <p>
      Here you can develop Quantitative Structure-Activity Relationship (QSAR) models using your uploaded modeling
      datasets.<br>You can also build models using publicly available modeling datasets.
    </p>

    <p>
      For more information about creating models and selecting the right parameters, see the <a href="/help-modeling">Modeling
      help page</a>.
    </p>

    <p>
      The full modeling workflow as described in our <a href="/help-workflows">Workflow help page</a> is detailed in
      the following publication:

        <span class="citation"><a href="http://onlinelibrary.wiley.com/doi/10.1002/minf.201000061/full">
          Tropsha, A. (2010). Best Practices for QSAR Model Development, Validation, and Exploitation.
          Molecular Informatics, 29(6-7), 476-488.
        </a></span>
    </p>

    <hr>
    <s:form action="createModelingJob" enctype="multipart/form-data" cssClass="form-horizontal" theme="simple">
      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select a Modeling Dataset</h3>
        </div>
        <div class="panel-body">
          <p>Select a modeling dataset to create a model from. (You can create more datasets using the
            <b><s:a action="dataset">Dataset Creation</s:a></b> page.)</p>

          <div class="row">
            <div class="col-xs-6">
              <div class="col-xs-12">
                <div class="form-group">
                  <s:select id="dataset-selection" cssClass="form-control" list="userDatasets" listKey="id"
                            listValue="name" theme="simple" value="(select a dataset)"/>
                </div>
                <div class="form-group">
                  <a id="view-dataset-detail" href="#" class="btn btn-primary disabled">View Selected Dataset</a>
                  <span class="text-muted">Opens in a new window.</span>
                </div>
              </div>
            </div>

            <div id="dataset-info-wrapper" class="col-xs-6">
              <div id="dataset-info">
              </div>

              <div id="dataset-info-help">
                <h4>Select a dataset to continue</h4>

                <div class="text-muted">
                  <p>
                    Once you select a dataset, basic information about the dataset will be displayed here.
                  </p>

                  <p>
                    You can also click <b>View Selected Dataset</b> to view more detailed information about the selected
                    dataset.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Choose Model Descriptors</h3>
        </div>
        <div class="panel-body">
          Chicken turkey alcatra venison. Landjaeger hamburger sirloin jerky drumstick pastrami strip steak tri-tip.
          Shankle rump fatback ball tip, beef filet mignon turducken landjaeger sausage hamburger pig brisket
          frankfurter. Prosciutto short ribs kevin, sausage beef rump cupim. Strip steak hamburger chuck pork loin ham
          hock chicken kielbasa.
        </div>
      </div>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Select Model Type and Parameters</h3>
        </div>
        <div class="panel-body">
          Tongue chicken pork belly, capicola landjaeger beef ribs cupim sirloin tail ham kielbasa strip steak biltong
          meatloaf. Short ribs flank pastrami, spare ribs beef ribs bacon ground round capicola prosciutto brisket filet
          mignon bresaola salami ham hock. Corned beef biltong bresaola alcatra picanha short ribs. Tenderloin
          frankfurter leberkas kielbasa swine. Sausage corned beef fatback venison pork belly kevin ribeye turducken.
        </div>
      </div>

      <div id="internal-split-type-selection" class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Choose Internal Data Split Method</h3>
        </div>
        <div class="panel-body">
          Prosciutto brisket pastrami, bacon fatback tenderloin shankle leberkas shoulder chicken pork belly. Strip
          steak ham bacon hamburger, picanha pork belly andouille flank drumstick. Turducken andouille bacon, short ribs
          meatball sirloin fatback hamburger rump. Picanha bresaola meatloaf jowl, t-bone tri-tip turkey alcatra
          frankfurter. Landjaeger pork chop prosciutto ground round kevin jerky.
        </div>
      </div>

      <div class="panel panel-primary">
        <div class="panel-heading">
          <h3 class="panel-title">Add Model Metadata</h3>
        </div>
        <div class="panel-body">
          Shoulder hamburger cupim brisket tenderloin ball tip, tongue turkey porchetta venison boudin tail shankle
          bresaola. Boudin swine leberkas tenderloin rump brisket ham hock picanha meatloaf chicken kevin short loin.
          Turkey pancetta prosciutto, ball tip meatloaf cupim sirloin beef ribs pork. Short loin ground round biltong,
          leberkas kevin meatloaf fatback. Shankle shank meatloaf tenderloin ribeye ball tip pork loin corned beef chuck
          pork belly brisket hamburger kevin turkey.
        </div>
      </div>
    </s:form>
  </section>

  <%@include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="assets/js/modeling.js"></script>
</body>
</html>
