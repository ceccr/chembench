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
        <li class="active"><a href="#jobqueue" data-toggle="tab">Job Queue</a></li>
        <li><a href="#datasets" data-toggle="tab">Datasets</a></li>
        <li><a href="#predictors" data-toggle="tab">Predictors</a></li>
        <li><a href="#predictions" data-toggle="tab">Predictions</a></li>
      </ul>

      <div class="tab-content">
        <div id="jobqueue" class="tab-pane active">
          <h3>
            Job Queue
            <button class="jobs-queue-refresh btn btn-primary">
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
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th>&#8470; Compounds</th>
                      <th>&#8470; Models</th>
                      <th>Time Created</th>
                      <th>Status</th>
                      <th>Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="incomingJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td>
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td>
                            <s:property value="numCompounds" />
                          </td>
                          <td>
                            <s:if test="!jobType.equals(Constants.DATASET)">
                              <s:property value="numModels" />
                            </s:if>
                            <s:else>N/A</s:else>
                          </td>
                          <td>
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
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th>&#8470; Compounds</th>
                      <th>&#8470; Models</th>
                      <th>Time Created</th>
                      <th>Status</th>
                      <th>Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="localJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td>
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td>
                            <s:property value="numCompounds" />
                          </td>
                          <td>
                            <s:if test="!jobType.equals(Constants.DATASET))">
                              <s:property value="numModels" />
                            </s:if>
                            <s:else>N/A</s:else>
                          </td>
                          <td>
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
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th>&#8470; Compounds</th>
                      <th>&#8470; Models</th>
                      <th>Time Created</th>
                      <th>Status</th>
                      <th>Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="lsfJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td>
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td>
                            <s:property value="numCompounds" />
                          </td>
                          <td>
                            <s:if test="!jobType.equals(Constants.DATASET)">
                              <s:property value="numModels" />
                            </s:if>
                            <s:else>N/A</s:else>
                          </td>
                          <td>
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
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Owner</th>
                      <th>Job Type</th>
                      <th>&#8470; Compounds</th>
                      <th>&#8470; Models</th>
                      <th>Time Created</th>
                      <th>Cancel</th>
                    </tr>
                  </thead>
                  <tbody>
                    <s:iterator value="errorJobs">
                      <s:if test="adminUser || userName==user.userName">
                        <tr>
                          <td>
                            <s:property value="jobName" />
                          </td>
                          <td>
                            <s:property value="userName" />
                          </td>
                          <td class="job-type">
                            <s:property value="jobType" />
                          </td>
                          <td>
                            <s:property value="numCompounds" />
                          </td>
                          <td>
                            <s:if test="!jobType.equals(Constants.DATASET)">
                              <s:property value="numModels" />
                            </s:if>
                            <s:else>N/A</s:else>
                          </td>
                          <td>
                            <s:date name="timeCreated" format="yyyy-MM-dd HH:mm" />
                          </td>
                          <td>
                            <s:if test="adminUser">
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
        </div>

        <div id="datasets" class="tab-pane">
          <h3>Datasets</h3>
          <p class="tab-description">Descriptors marked with an asterisk (*) indicate descriptors that were created
            outside of Chembench and uploaded by the user.</p>

          <table class="table table-hover">
            <thead>
              <tr>
                <th>Name</th>
                <th>&#8470; Compounds</th>
                <th>Activity Type</th>
                <th><abbr title="Modelability Index">MODI</abbr></th>
                <th>Available Descriptors</th>
                <th>Date Created</th>
                <th>Visibility</th>
              </tr>
            </thead>

            <tbody>
              <s:iterator value="userDatasets">
                <tr>
                  <td>
                    <s:url var="datasetId" action="viewDataset">
                      <s:param name="id" value="%{id}" />
                    </s:url>
                    <s:a href="%{datasetId}">
                      <s:property value="name" />
                    </s:a>
                  </td>
                  <td>
                    <s:property value="numCompound" />
                  </td>
                  <td class="activity-type">
                    <s:property value="modelType" />
                  </td>
                  <td>
                    <s:if test="!canGenerateModi()">
                      <span class="text-muted">Not available</span>
                    </s:if>
                    <s:else>
                      <s:if test="modiGenerated">
                        <s:if test="modi >= @edu.unc.ceccr.chembench.global.Constants@MODI_MODELABLE">
                          <span class="text-success" title="Modelable"> <s:property
                              value="getText('{0, number, #, ##0.00}', {modi})" /></span>
                        </s:if>
                        <s:else>
                          <span class="text-danger" title="Not modelable"> <s:property
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
                  <td class="descriptor-types">
                    <s:property value="availableDescriptors" />
                    <s:if test="uploadedDescriptorType != null && !uploadedDescriptorType.isEmpty()">
                      *<s:property value="uploadedDescriptorType" />
                    </s:if>
                  </td>
                  <td>
                    <s:date name="createdTime" format="yyyy-MM-dd HH:mm" />
                  </td>
                  <td>
                    <s:if test="userName.equals('all-users')">
                    Public
                  </s:if>
                    <s:else>
                    Private
                  </s:else>
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>

        <div id="predictors" class="tab-pane">
          <h3>Predictors</h3>
          <p class="tab-description">Predictors marked with an asterisk (*) indicate predictors built using
            descriptors that were created outside of Chembench and uploaded by the user.</p>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>Name</th>
                <th>Modeling Dataset</th>
                <th>n-Fold</th>
                <th>Activity Type</th>
                <th>External Set R<sup>2</sup> or <abbr title="Correct Classification Rate" class="initialism">CCR</abbr></th>
                <th>Modeling Method</th>
                <th>Descriptor Type</th>
                <th>Date Created</th>
                <th>Visibility</th>
              </tr>
            </thead>
            <tbody>
              <s:iterator value="userPredictors">
                <tr>
                  <td>
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
                    <s:if test="childType.equals('NFOLD')">Yes</s:if>
                    <s:else>No</s:else>
                  </td>
                  <td class="activity-type">
                    <s:property value="activityType" />
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
                  <td class="descriptor-types">
                    <s:if test="descriptorGeneration.equals('UPLOADED')">
                      * <s:property value="uploadedDescriptorType" />
                    </s:if>
                    <s:else>
                      <s:property value="descriptorGeneration" />
                    </s:else>
                  </td>
                  <td>
                    <s:date name="dateCreated" format="yyyy-MM-dd HH:mm" />
                  </td>
                  <td>
                    <s:if test="username.equals('all-users')">
                      Public
                    </s:if>
                    <s:else>
                      Private
                    </s:else>
                  </td>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>

        <div id="predictions" class="tab-pane">
          <h3>Predictions</h3>
          <table class="table table-hover">
            <thead>
              <tr>
                <th>Name</th>
                <th>Prediction Dataset</th>
                <th>Predictor(s) Used</th>
                <th>Date Created</th>
              </tr>
            </thead>
            <tbody>
              <s:iterator value="userPredictions">
                <tr>
                  <td>
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
                  <td>
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
