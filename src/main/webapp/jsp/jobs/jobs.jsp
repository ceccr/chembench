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

<!--
<div id="datasets" class="border StandardTextDarkGrayParagraph benchAlign bottomMarginDataset">
  <p class="StandardTextDarkGrayParagraph2">
    <b>Datasets</b>
  </p>
  <p class="StandardTextDarkGrayParagraph">
    * Descriptors for the dataset were created outside of Chembench and uploaded by the user. <br /> <br /> <i>Click
      on the name of dataset to visualize it.</i><br />
    <s:if test="user.userName!='guest'&&user.showPublicDatasets=='SOME'">
      <i>Additional public datasets are available. You can choose to show these from the <a href="editProfile">edit
          profile</a> page.
      </i>
      <br />
    </s:if>
    <s:if test="user.userName!='guest'&&user.showPublicDatasets=='ALL'">
      <i>You are currently viewing all available public datasets. You can choose to hide these from the <a
        href="editProfile">edit profile</a> page.
      </i>
      <br />
    </s:if>
    <s:if test="user.userName!='guest'&&user.showPublicDatasets=='NONE'">
      <i>Public datasets are currently hidden. You can choose to show these from the <a href="editProfile">edit
          profile</a> page.
      </i>
      <br />
    </s:if>
  </p>
  <table class="sortable" id="datasets">
    <tr>
      <th class="TableRowText01">Name</th>
      <th class="TableRowText01">Number of Compounds</th>
      <th class="TableRowText01">Type</th>
      <th class="TableRowText01">Structure Images Available</th>
      <th class="TableRowText01">Descriptor Type name</th>
      <th class="TableRowText01">Date Created</th>
      <th class="TableRowText01">Public/Private</th>
    </tr>
    <s:iterator value="userDatasets">
      <s:if test="hasBeenViewed=='YES'">
        <tr class="TableRowText02">
      </s:if>
      <s:else>
        <tr class="TableRowText03">
      </s:else>

      <td align="center"><a href="viewDataset?id=<s:property value="id" />"> <s:property value="name" />
      </a><br /> <a href="datasetFilesServlet?datasetName=<s:property value='name' />&user=<s:property value='userName'/>"><img
          alt="download" width="18" height="18" src="theme/img/download.png" /></a> <s:if test="userName=='all-users'"></s:if>
        <s:else>
          <a onclick="return confirmDelete('predictor')" href="deleteDataset?id=<s:property value="id" />#datasets"><img
            alt="delete" width="15" height="15" src="theme/img/delete.png" /></a>
        </s:else></td>
      <td><s:property value="numCompound" /></td>
      <td><s:property value="modelType" /></td>
      <s:if test="!sdfFile.isEmpty()">
        <td>YES</td>
      </s:if>
      <s:else>
        <td>NO</td>
      </s:else>
      <s:if test="uploadedDescriptorType!=''">
        <td>*<s:property value="uploadedDescriptorType" /></td>
      </s:if>
      <s:else>
        <td><s:property value="availableDescriptors" /></td>
      </s:else>
      <td><s:date name="createdTime" format="yyyy-MM-dd HH:mm" /></td>
      <s:if test="userName=='all-users'">
        <td>Public</td>
      </s:if>
      <s:else>
        <td>Private</td>
      </s:else>
      </tr>
    </s:iterator>
  </table>
</div>

<div id="predictors" class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
  <p class="StandardTextDarkGrayParagraph2">
    <b>Predictors</b><br />
  </p>
  <p class="StandardTextDarkGrayParagraph">* Predictor was built on a dataset with descriptors that were created
    outside of Chembench and uploaded by the user.</p>
  <div class="StandardTextDarkGrayParagraph">
    <i>Click on the name of a predictor to analyze the modeling results.</i><br />
  </div>
  <table class="sortable predictorTableWidth" id="predictors">
    <tbody style="width: 100%;">
      <tr>
        <th class="TableRowText01 predictorNameWidth">Name</th>
        <th class="TableRowText01 predictorDatasetWidth">Dataset</th>
        <th class="TableRowText01 predictorNfoldWidth">Nfold</th>
        <th class="TableRowText01 predictorActtypeWidth">Act type</th>
        <th class="TableRowText01 predictorExternalWidth">External Set R<sup>2</sup> or CCR
        </th>
        <th class="TableRowText01 predictorModelingWidth">Modeling Method</th>
        <th class="TableRowText01 predictorDescriptorWidth">Descriptor Type</th>
        <th class="TableRowText01 predictorPublicWidth">Public/Private</th>
        <th class="TableRowText01 predictorDateWidth">Date Created</th>
      </tr>
      <s:iterator value="userPredictors">
        <s:if test="hasBeenViewed=='YES'">
          <tr class="TableRowText02">
        </s:if>
        <s:else>
          <tr class="TableRowText03">
        </s:else>

        <s:url id="predictorLink" value="/viewPredictor" includeParams="none">
          <s:param name="id" value='id' />
        </s:url>
        <td><s:a href="%{predictorLink}">
            <s:property value="name" />
          </s:a> <br /><a
          href="projectFilesServlet?project=<s:property value='name' />&user=<s:property value="userName" />&projectType=modeling"><img
            alt="download" width="18" height="18" src="theme/img/download.png" /></a> <s:if test="userName=='all-users'"></s:if>
          <s:else>
            <a onclick="return confirmDelete('predictor')"
              href="deletePredictor?id=<s:property value="id" />#predictors"><img alt="delete" width="15"
              height="15" src="theme/img/delete.png" /></a>
          </s:else></td>
        <td><a href="viewDataset?id=<s:property value="datasetId" />"> <s:property value="datasetDisplay" />
        </a></td>
        <td><s:if test="childType=='NFOLD'">YES</s:if> <s:else>NO</s:else></td>
        <td><s:property value="activityType" /></td>
        <s:if test="childType=='NFOLD'">
          <td><s:if test='externalPredictionAccuracyAvg!="0.0 ± 0.0"'>
              <s:property value="externalPredictionAccuracyAvg" />
            </s:if> <s:else>
                    NA
                </s:else></td>
        </s:if>
        <s:else>
          <td><s:if test='externalPredictionAccuracy!="0.0"'>
              <s:property value="externalPredictionAccuracy" />
            </s:if> <s:else>
                    NA
                </s:else></td>
        </s:else>
        <td><s:if test="modelMethod=='RANDOMFOREST'">RF</s:if> <s:else>
            <s:property value="modelMethod" />
          </s:else></td>
        <s:if test="descriptorGeneration=='UPLOADED'">
          <td>*<s:property value="uploadedDescriptorType" /></td>
        </s:if>
        <s:else>
          <td><s:property value="descriptorGeneration" /></td>
        </s:else>
        <td><s:if test="userName=='all-users'">Public</s:if> <s:else>Private</s:else></td>
        <td><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
        </tr>
      </s:iterator>
    </tbody>
  </table>
</div>
<div id="predictions" class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
  <p class="StandardTextDarkGrayParagraph2">
    <b>Predictions</b>
  </p>
  <div class="StandardTextDarkGrayParagraph">
    <i>Click on the name of a prediction to see the results.</i>
  </div>
  <s:if test="! userPredictions.isEmpty()">
    <table class="sortable" id="predictions">
      <tr>
        <th class="TableRowText01">Name</th>
        <th class="TableRowText01">Dataset</th>
        <th class="TableRowText01">Predictor</th>
        <th class="TableRowText01">Date Created</th>
      </tr>
      <s:iterator value="userPredictions">
        <s:if test="hasBeenViewed=='YES'">
          <tr class="TableRowText02">
        </s:if>
        <s:else>
          <tr class="TableRowText03">
        </s:else>

        <s:url id="predictionLink" value="/viewPrediction" includeParams="none">
          <s:param name="id" value='id' />
        </s:url>
        <td><s:a href="%{predictionLink}">
            <s:property value="name" />
          </s:a><br /> <a href="fileServlet?id=<s:property value="id" />&jobType=PREDICTION&file=predictionAsCsv"><img
            alt="download" width="18" height="18" src="theme/img/download.png" /></a> <s:if test="userName=='all-users'"></s:if>
          <s:else>
            <a onclick="return confirmDelete('predictor')"
              href="deletePrediction?id=<s:property value="id" />#predictions"><img alt="delete" width="15"
              height="15" src="theme/img/delete.png" /></a>
          </s:else></td>
        <td><s:property value="datasetDisplay" /></td>
        <td><s:property value="predictorNames" /></td>
        <td><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
        </tr>
      </s:iterator>
    </table>
  </s:if>
</div>
</div>
<div class="includes"><%@include file="/jsp/main/footer.jsp"%></div>
</div>
</body>
</html>
-->
