<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="edu.unc.ceccr.chembench.global.Constants"%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="/jsp/main/head.jsp"%>
<title>Chembench | My Bench</title>
</head>
<body>
  <div id="main" class="container">
    <%@ include file="/jsp/main/header.jsp"%>

    <section id="content">
      <h2>My Bench</h2>
      <p>Every dataset, predictor, and prediction you have created on Chembench is available on this page. You can
        track progress of all the running jobs using the job queue.</p>
      <p>
        Publicly available datasets and predictors are also displayed. If you wish to share datasets or predictors you
        have developed with the Chembench community, please contact us at <a href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a>.
      </p>
      <p>All data is sorted by the creation date in descending order (newest on top).</p>

      <ul class="nav nav-tabs">
        <li class="active"><a href="#jobs" data-toggle="tab">Job Queue</a></li>
        <li><a href="#datasets" data-toggle="tab">Datasets</a></li>
        <li><a href="#predictors" data-toggle="tab">Predictors</a></li>
        <li><a href="#predictions" data-toggle="tab">Predictions</a></li>
      </ul>

      <div class="tab-content">
        <div id="jobs" class="tab-pane active">
          <h3>
            Job Queue
            <button id="jobs-queue-refresh" class="btn btn-primary">
              <span class="glyphicon glyphicon-refresh"></span> Refresh
            </button>
          </h3>
          <p class="tab-description">
            Running jobs from all Chembench users are displayed below. Use the <b>Refresh</b> button to update the list.
            Other users can see your jobs while they are running, but only you can access your completed datasets,
            predictors, and predictions.
          </p>

          <s:if test="!incomingJobs.isEmpty()">
            <div class="panel panel-default">
              <div class="panel-heading">
                <h4>Unassigned Jobs</h4>
              </div>
              <div class="panel-body">
                <table class="table table-hover tablesorter">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th><th class="date-created">Date Created</th>
                      <th>Status</th>
                      <th class="sorter-false">Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="incomingJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td class="name">
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td class="date-created">
                            <s:date name="timeCreated" format="yyyy-MM-dd HH:mm" />
                          </td>
                          <td>
                            <s:property value="message" />
                          </td>
                          <td>
                            <s:if test="adminUser || user.userName.equals(userName)">
                              <a class="delete-link" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if>
                          </td>
                        </tr>
                      </s:if>
                    </s:iterator>
                  </tbody>
                </table>
              </div>
            </div>
          </s:if>

          <div class="panel panel-default">
            <div class="panel-heading">
              <h4>Jobs on Local Queue</h4>
            </div>
            <div class="panel-body">
              <s:if test="!localJobs.isEmpty()">
                <table class="table table-hover tablesorter">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th class="date-created">Date Created</th>
                      <th>Status</th>
                      <th class="sorter-false">Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="localJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td class="name">
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td class="date-created">
                            <s:date name="timeCreated" format="yyyy-MM-dd HH:mm" />
                          </td>
                          <td>
                            <s:property value="message" />
                          </td>
                          <td class="delete">
                            <s:if test="adminUser || user.userName.equals(userName)">
                              <a href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if>
                          </td>
                        </tr>
                      </s:if>
                    </s:iterator>
                  </tbody>
                </table>
              </s:if>
              <s:else>
                <span class="text-muted">(No jobs are running on the local queue.)</span>
              </s:else>
            </div>
          </div>

          <div class="panel panel-default">
            <div class="panel-heading">
              <h4>Jobs on LSF Queue</h4>
            </div>
            <div class="panel-body">
              <s:if test="!lsfJobs.isEmpty()">
                <table class="table table-hover tablesorter">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th class="date-created">Date Created</th>
                      <th>Status</th>
                      <th class="sorter-false">Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="lsfJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td class="name">
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td class="date-created">
                            <s:date name="timeCreated" format="yyyy-MM-dd HH:mm" />
                          </td>
                          <td>
                            <s:property value="message" />
                          </td>
                          <td class="delete">
                            <s:if test="adminUser || user.userName.equals(userName)">
                              <a href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if>
                          </td>
                        </tr>
                      </s:if>
                    </s:iterator>
                  </tbody>
                </table>
              </s:if>
              <s:else>
                <span class="text-muted">(No jobs are running on the LSF queue.)</span>
              </s:else>
            </div>
          </div>

          <s:if test="!errorJobs.isEmpty()">
            <div class="panel panel-danger">
              <div class="panel-heading">
                <h4>Jobs with Errors</h4>
              </div>
              <div class="panel-body">
                <table class="table table-hover tablesorter">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th class="date-created">Date Created</th>
                      <th class="sorter-false">Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="errorJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td class="name">
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td class="date-created">
                            <s:date name="timeCreated" format="yyyy-MM-dd HH:mm" />
                          </td>
                          <td class="delete">
                            <s:if test="adminUser">
                              <a href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if>
                          </td>
                        </tr>
                      </s:if>
                    </s:iterator>
                  </tbody>
                </table>
              </div>
            </div>
          </s:if>
        </div>

        <div id="datasets" class="tab-pane">
          <h3>Datasets</h3>
          <table class="table table-hover table-bordered tablesorter">
            <thead>
              <tr>
                <th>Name</th>
                <th>Size</th>
                <th>Dataset Type</th>
                <th>Descriptors</th>
                <th><abbr title="Modelability Index">MODI</abbr></th>
                <th class="date-created">Date Created</th>
                <th class="public-private">Public?</th>
              </tr>
            </thead>

            <tbody>
              <s:iterator value="userDatasets">
                <tr>
                  <td class="name">
                    <s:url var="datasetId" action="viewDataset">
                      <s:param name="id" value="%{id}" />
                    </s:url>
                    <s:a href="%{datasetId}">
                      <s:property value="name" />
                    </s:a>
                    <br>
                    <div class="button-group">
                      <div class="download">
                        <s:url var="datasetDownload" value="datasetFilesServlet">
                          <s:param name="datasetName" value="%{name}" />
                          <s:param name="user" value="%{userName}" />
                        </s:url>
                        <span class="glyphicon glyphicon-save"></span>
                        <s:a href="%{datasetDownload}">Download</s:a>
                      </div>
                      <s:if test="!userName.equals(@edu.unc.ceccr.chembench.global.Constants@ALL_USERS_USERNAME)">
                        <div class="delete">
                          <span class="glyphicon glyphicon-remove"></span>
                          <s:url var="datasetDelete" action="deleteDataset">
                            <s:param name="id" value="%{id}" />
                          </s:url>
                          <s:a href="%{datasetDelete}">Delete</s:a>
                        </div>
                      </s:if>
                    </div>
                  </td>
                  <td>
                    <s:property value="numCompound" />
                  </td>
                  <td class="dataset-type activity-type">
                    <s:property value="datasetType" />
                    <s:if test="hasActivities()">
                      (<s:property value="modelType" />)
                    </s:if>
                  </td>
                  <td class="available-descriptors">
                    <s:property value="availableDescriptors" />
                    <s:if test="!uploadedDescriptorType.isEmpty()">
                      ("<s:property value="uploadedDescriptorType" />")
                    </s:if>
                  </td>
                  <td>
                    <s:if test="!canGenerateModi()">
                      <span class="text-muted">Not available</span>
                    </s:if>
                    <s:else>
                      <s:if test="modiGenerated">
                        <s:if test="modi >= @edu.unc.ceccr.chembench.global.Constants@MODI_MODELABLE">
                          <span class="modi-value text-success" title="Modelable"> <s:property
                              value="getText('{0, number, #, ##0.00}', {modi})" /></span>
                        </s:if>
                        <s:else>
                          <span class="modi-value text-danger" title="Not modelable"> <s:property
                              value="getText('{0, number, #, ##0.00}', {modi})" /></span>
                        </s:else>
                      </s:if>
                      <s:else>
                        <input type="hidden" name="dataset-id" value="<s:property value="id" />">
                        <span class="text-warning">Not generated</span>
                        <button class="btn btn-primary btn-xs generate-modi">Generate MODI</button>
                      </s:else>
                    </s:else>
                  </td>
                  <td class="date-created">
                    <s:date name="createdTime" format="yyyy-MM-dd HH:mm" />
                  </td>
                  <td class="public-private">
                    <s:if test="userName.equals('all-users')">
                      <span class="text-primary"><span class="glyphicon glyphicon-eye-open" title="Public"></span>
                        Yes</span>
                    </s:if>
                    <s:else>
                      <span class="text-muted"><span class="glyphicon glyphicon-eye-close" title="Private"></span>
                        No</span>
                    </s:else>
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>

        <div id="predictors" class="tab-pane">
          <h3>Predictors</h3>
          <table class="table table-hover table-bordered tablesorter">
            <thead>
              <tr>
                <th>Name</th>
                <th>Modeling Dataset</th>
                <th>R<sup>2</sup> or <abbr title="Correct Classification Rate" class="initialism">CCR</abbr></th>
                <th>Model Type</th>
                <th>Descriptor Type</th>
                <th class="date-created">Date Created</th>
                <th class="public-private">Public?</th>
              </tr>
            </thead>
            <tbody>
              <s:iterator value="userPredictors">
                <tr>
                  <td class="name">
                    <s:url var="viewPredictor" action="viewPredictor">
                      <s:param name="id" value="%{id}" />
                    </s:url>
                    <s:a href="%{viewPredictor}">
                      <s:property value="name" />
                    </s:a>
                  </td>
                  <td>
                    <s:url var="viewModelingDataset" action="viewDataset">
                      <s:param name="id" value="%{datasetId}" />
                    </s:url>
                    <s:a href="%{viewModelingDataset}">
                      <s:property value="datasetDisplay" />
                    </s:a>
                  </td>
                  <td>
                    <s:if test="childType.equals('NFOLD')">
                      <s:if test="!externalPredictionAccuracyAvg.equals('0.0 ± 0.0')">
                        <s:property value="externalPredictionAccuracyAvg" />
                      </s:if>
                      <s:else>
                        NA
                      </s:else>
                    </s:if>
                    <s:else>
                      <s:if test="!externalPredictionAccuracy.equals('0.0')">
                        <s:property value="externalPredictionAccuracy" />
                      </s:if>
                      <s:else>
                        NA
                      </s:else>
                    </s:else>
                  </td>
                  <td class="modeling-method">
                    <s:property value="modelMethod" />
                  </td>
                  <td class="available-descriptors">
                    <s:if test="descriptorGeneration.equals('UPLOADED')">
                      * <s:property value="uploadedDescriptorType" />
                    </s:if>
                    <s:else>
                      <s:property value="descriptorGeneration" />
                    </s:else>
                  </td>
                  <td class="date-created">
                    <s:date name="dateCreated" format="yyyy-MM-dd HH:mm" />
                  </td>
                  <td class="public-private">
                    <s:if test="userName.equals('all-users')">
                      <span class="text-primary"><span class="glyphicon glyphicon-eye-open" title="Public"></span>
                        Yes</span>
                    </s:if>
                    <s:else>
                      <span class="text-muted"><span class="glyphicon glyphicon-eye-close" title="Private"></span>
                        No</span>
                    </s:else>
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>

        <div id="predictions" class="tab-pane">
          <h3>Predictions</h3>
          <table class="table table-hover table-bordered tablesorter">
            <thead>
              <tr>
                <th>Name</th>
                <th>Prediction Dataset</th>
                <th>Predictor(s) Used</th>
                <th class="date-created">Date Created</th>
              </tr>
            </thead>
            <tbody>
              <s:iterator value="userPredictions">
                <tr>
                  <td class="name">
                    <s:url var="viewPrediction" action="viewPrediction">
                      <s:param name="id" value="%{id}" />
                    </s:url>
                    <s:a href="%{viewPrediction}">
                      <s:property value="name" />
                    </s:a>
                  </td>
                  <td>
                    <s:url var="viewPredictionDataset" action="viewDataset">
                      <s:param name="id" value="datasetId" />
                    </s:url>
                    <s:a href="%{viewPredictionDataset}">
                      <s:property value="datasetDisplay" />
                    </s:a>
                  </td>
                  <td>
                    <s:property value="predictorNames" />
                  </td>
                  <td class="date-created">
                    <s:date name="dateCreated" format="yyyy-MM-dd HH:mm" />
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>
      </div>
    </section>

    <%@include file="/jsp/main/footer.jsp"%>
  </div>

  <%@ include file="/jsp/main/tail.jsp"%>
  <script src="assets/js/jobs.js"></script>
</body>
</html>
