<%@ taglib prefix="s" uri="/struts-tags"%>

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

          <div class="panel panel-default">
            <div class="panel-heading">
              <h4>Unassigned Jobs</h4>
            </div>
            <div class="panel-body">
              <s:if test="!incomingJobs.isEmpty()">
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
                          <td><s:property value="jobName" /></td>
                          <td><s:property value="userName" /></td>
                          <td><s:property value="jobType" /></td>
                          <td><s:property value="numCompounds" /></td>
                          <td><s:if test="jobTypeString!='dataset'">
                              <s:property value="numModels" />
                            </s:if> <s:else>N/A</s:else></td>
                          <td><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                          <td><s:property value="message" /></td>
                          <td><s:if test="adminUser || user.userName.equals(userName)">
                              <a class="delete-link" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if></td>
                        </tr>
                      </s:if>
                    </s:iterator>
                  </tbody>
                </table>
              </s:if>
              <s:else>
                <span class="text-muted">(No jobs are waiting to be assigned.)</span>
              </s:else>
            </div>
          </div>

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
                          <td><s:property value="jobName" /></td>
                          <td><s:property value="userName" /></td>
                          <td><s:property value="jobType" /></td>
                          <td><s:property value="numCompounds" /></td>
                          <td><s:if test="jobTypeString!='dataset'">
                              <s:property value="numModels" />
                            </s:if> <s:else>N/A</s:else></td>
                          <td><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                          <td><s:property value="message" /></td>
                          <td><s:if test="adminUser || user.userName.equals(userName)">
                              <a class="delete-link" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if></td>
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
                          <td><s:property value="jobName" /></td>
                          <td><s:property value="userName" /></td>
                          <td><s:property value="jobType" /></td>
                          <td><s:property value="numCompounds" /></td>
                          <td><s:if test="jobTypeString!='dataset'">
                              <s:property value="numModels" />
                            </s:if> <s:else>N/A</s:else></td>
                          <td><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                          <td><s:property value="message" /></td>
                          <td><s:if test="adminUser || user.userName.equals(userName)">
                              <a class="delete-link" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                            </s:if></td>
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
                <th>Structure Images</th>
                <th>Visibility</th>
              </tr>
            </thead>

            <tbody>
              <s:iterator value="userDatasets">
                <tr>
                  <td><s:url var="datasetId" action="viewDataset">
                      <s:param name="id" value="%{id}" />
                    </s:url> <s:a href="%{datasetId}">
                      <s:property value="name" />
                    </s:a></td>
                  <td><s:property value="numCompound" /></td>
                  <td class="activity-type"><s:property value="modelType" /></td>
                  <td><s:if test="!canGenerateModi()">
                      <span class="text-danger">Not available</span>
                    </s:if> <s:else>
                      <s:if test="modiGenerated">
                        <s:property value="getText('{0, number, #, ##0.00}', {modi})" />
                      </s:if>
                      <s:else>
                        <input type="hidden" name="dataset-id" value="<s:property value="id" />">
                        <span class="text-warning">Not generated</span>
                        <button class="btn btn-primary btn-xs generate-modi">Generate MODI</button>
                      </s:else>
                    </s:else></td>
                  <td class="available-descriptors"><s:property value="availableDescriptors" /> <s:if
                      test="uploadedDescriptorType != null && !uploadedDescriptorType.isEmpty()">
                  *<s:property value="uploadedDescriptorType" />
                    </s:if></td>
                  <td><s:date name="createdTime" format="yyyy-MM-dd HH:mm" /></td>
                  <td><s:if test="sdfFile == null || sdfFile.isEmpty()">
                    No
                  </s:if> <s:else>
                    Yes
                  </s:else></td>
                  <td><s:if test="userName.equals('all-users')">
                    Public
                  </s:if> <s:else>
                    Private
                  </s:else>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>

        <div id="predictors" class="tab-pane">
          <h3>Predictors</h3>
        </div>

        <div id="predictions" class="tab-pane">
          <h3>Predictions</h3>
        </div>
      </div>
    </section>

    <%@include file="/jsp/main/footer.jsp"%>
  </div>

  <%@ include file="/jsp/main/tail.jsp"%>
  <script src="assets/js/jobs.js"></script>
</body>
</html>
