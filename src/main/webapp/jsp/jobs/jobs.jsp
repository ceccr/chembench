<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" import="java.util.*"%>

<html>
<head>
<title>Chembench | My Bench</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
<link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="theme/screen.css" type="text/css" media="screen, projection">
<link rel="stylesheet" href="theme/print.css" type="text/css" media="print">
<link href="theme/standard.css" rel="stylesheet" type="text/css">
<link href="theme/links.css" rel="stylesheet" type="text/css">
<link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
<link rel="icon" href="/theme/img/mml.ico" type="image/ico">
<link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
<link href="theme/customStylesheet.css" rel="stylesheet" type="text/css">
<script src="javascript/chembench.js"></script>
<script src="javascript/dataset.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css"
  href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.min.css"></link>
<script language="JavaScript" src="javascript/sortableTable.js"></script>
<script>
        $(function() {
            $("#tabs").tabs();
        });
    </script>
</head>
<body onload="setTabToMyBench();">

  <div class="outer">
    <div class="includesHeader"><%@include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp"%></div>
    <div class="StandardTextDarkGrayParagraph benchBackground benchAlign">
      <div class="homeLeft">
        <br />
        <p style="margin-left: 20px">
          <b>My Bench</b> <br /> <br /> Every dataset, predictor, and prediction you have created on Chembench is
          available on this page. You can track progress of all the running jobs using the job queue. <br /> <br />
          Publicly available datasets and predictors are also displayed. If you wish to share datasets or predictors you
          have developed with the Chembench community, please contact us at <a href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a>.
          <br /> All data is sorted by the creation date in descending order (newest on top).
        </p>
      </div>
    </div>

    <div id="tabs">
      <ul>
        <li><a href="#jobQueue">Job Queue</a></li>
        <li><a href="#datasets">Datasets</a></li>
        <li><a href="#predictors">Predictors</a></li>
        <li><a href="#predictions">Predictions</a></li>
      </ul>

      <!-- Queued, Local, and LSF Jobs -->
      <div id="jobQueue" class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
        <p class="StandardTextDarkGrayParagraph2">
          <b>Job Queue</b>
        </p>
        <i>Running jobs from all Chembench users are displayed below. Use the REFRESH STATUS button to update the
          list. Other users can see your jobs while they are running, but only you can access your completed datasets,
          predictors, and predictions.</i><br /> <br />
        <form action="jobs">
          <div class="StandardTextDarkGrayParagraph">
            <button type="submit">REFRESH STATUS</button>
          </div>
        </form>
        <br />
        <!-- Queued (incomingJobs) -->
        <b>Unassigned Jobs: </b> <br />
        <s:if test="! incomingJobs.isEmpty()">
          <table class="sortable" id="incomingJobs">
            <tr>
              <th class="TableRowText01">Name</th>
              <th class="TableRowText01">Owner</th>
              <th class="TableRowText01">Job Type</th>
              <th class="TableRowText01">Number of Compounds</th>
              <th class="TableRowText01">Number of Models</th>
              <th class="TableRowText01">Time Created</th>
              <th class="TableRowText01">Status</th>
              <th class="TableRowText01_unsortable">Cancel</th>
            </tr>
            <s:iterator value="incomingJobs">
              <s:if test="adminUser || userName==user.userName">
                <tr>
                  <td class="TableRowText02"><s:property value="jobName" /></td>
                  <td class="TableRowText02"><s:property value="userName" /></td>
                  <td class="TableRowText02"><s:property value="jobType" /></td>
                  <td class="TableRowText02"><s:property value="numCompounds" /></td>
                  <td class="TableRowText02"><s:if test="jobTypeString!='dataset'">
                      <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                  <td class="TableRowText02"><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                  <td class="TableRowText02"><b><s:property value="message" /><b></td>
                  <td class="TableRowText02"><s:if test="adminUser">
                      <a onclick="return confirmDelete('job')" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:if> <s:elseif test="user.userName==userName && userName.conatains('guest')">
                      <a onclick="return confirmDelete('job')" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:elseif></td>
                </tr>
              </s:if>
            </s:iterator>
          </table>
        </s:if>
        <s:else>
    (No jobs are waiting to be assigned.)
</s:else>
        <br /> <br />
        <!-- Local Jobs -->
        <b>Jobs on Local Queue: </b> <br />
        <s:if test="! localJobs.isEmpty()">
          <table class="sortable" id="localJobs">
            <tr>
              <th class="TableRowText01">Name</th>
              <th class="TableRowText01">Owner</th>
              <th class="TableRowText01">Job Type</th>
              <th class="TableRowText01">Number of Compounds</th>
              <th class="TableRowText01">Number of Models</th>
              <th class="TableRowText01">Time Created</th>
              <th class="TableRowText01">Status</th>
              <th class="TableRowText01_unsortable">Cancel</th>
            </tr>
            <s:iterator value="localJobs">
              <s:if test="adminUser || userName==user.userName">
                <tr>
                  <td class="TableRowText02"><s:property value="jobName" /></td>
                  <td class="TableRowText02"><s:property value="userName" /></td>
                  <td class="TableRowText02"><s:property value="jobType" /></td>
                  <td class="TableRowText02"><s:property value="numCompounds" /></td>
                  <td class="TableRowText02"><s:if test="jobTypeString!='dataset'">
                      <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                  <td class="TableRowText02"><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                  <td class="TableRowText02"><b><s:property value="message" /><b></td>
                  <td class="TableRowText02"><s:if test="adminUser">
                      <a onclick="return confirmDelete('job')" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:if> <s:elseif test="user.userName==userName && userName!='guest'">
                      <a onclick="return confirmDelete('job')" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:elseif></td>
                  </td>
                </tr>
              </s:if>
            </s:iterator>
          </table>
        </s:if>
        <s:else>
    (The local processing queue is empty.)
</s:else>
        <br /> <br />
        <!-- LSF Jobs -->
        <b>Jobs on LSF Queue: </b> <br />
        <s:if test="! lsfJobs.isEmpty()">
          <table class="sortable" id="lsfJobs">
            <tr>
              <th class="TableRowText01">Name</th>
              <th class="TableRowText01">Owner</th>
              <th class="TableRowText01">Job Type</th>
              <th class="TableRowText01">Number of Compounds</th>
              <th class="TableRowText01">Number of Models</th>
              <th class="TableRowText01">Time Created</th>
              <th class="TableRowText01">Status</th>
              <th class="TableRowText01_unsortable">Cancel</th>
            </tr>
            <s:iterator value="lsfJobs">
              <s:if test="adminUser || userName==user.userName">
                <tr>
                  <td class="TableRowText02"><s:property value="jobName" /></td>
                  <td class="TableRowText02"><s:property value="userName" /></td>
                  <td class="TableRowText02"><s:property value="jobType" /></td>
                  <td class="TableRowText02"><s:property value="numCompounds" /></td>
                  <td class="TableRowText02"><s:if test="jobTypeString!='dataset'">
                      <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                  <td class="TableRowText02"><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                  <td class="TableRowText02"><b><s:property value="message" /><b></td>
                  <td class="TableRowText02"><s:if test="adminUser">
                      <a onclick="return confirmDelete('job')" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:if> <s:elseif test="user.userName==userName && userName!='guest'">
                      <a onclick="return confirmDelete('job')" href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:elseif></td>
                </tr>
              </s:if>
            </s:iterator>
          </table>
        </s:if>
        <s:else>
    (The LSF queue is empty.)
</s:else>
        <!-- Error Jobs -->
        <s:if test="! errorJobs.isEmpty()">
          <br />
          <br />
          <b>Jobs with errors: </b>
          <br />
          <div class="StandardTextDarkGray">One or more of your jobs has encountered an error and cannot be
            completed. The Chembench administrators have been contacted and will resolve the issue as soon as possible.
            We will let you know when the error is fixed.</div>
          <table class="sortable">
            <tr>
              <th class="TableRowText01">Name</th>
              <th class="TableRowText01">Owner</th>
              <th class="TableRowText01">Job Type</th>
              <th class="TableRowText01">Number of Compounds</th>
              <th class="TableRowText01">Number of Models</th>
              <th class="TableRowText01">Time Created</th>
              <s:if test="adminUser">
                <th class="TableRowText01_unsortable">Remove Job (admin only)</th>
              </s:if>
            </tr>
            <s:iterator value="errorJobs">
              <s:if test="adminUser || userName==user.userName">
                <tr>

                  <td class="TableRowText02"><s:if test="adminUser&&jobTypeString=='modeling'">
                      <s:url id="predictorLink" value="/viewPredictor" includeParams="none">
                        <s:param name="predictorId" value='predictorId' />
                      </s:url>
                      <s:a href="%{predictorLink}">
                        <s:property value="jobName" />
                      </s:a>
                    </s:if> <s:else>
                      <s:property value="jobName" />
                    </s:else></td>

                  <td class="TableRowText02"><s:property value="userName" /></td>
                  <td class="TableRowText02"><s:property value="jobType" /></td>
                  <td class="TableRowText02"><s:property value="numCompounds" /></td>
                  <td class="TableRowText02"><s:if test="jobTypeString!='dataset'">
                      <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                  <td class="TableRowText02"><s:date name="timeCreated" format="yyyy-MM-dd HH:mm" /></td>
                  <s:if test="adminUser">
                    <td class="TableRowText02"><a onclick="return confirmDelete('job')"
                      href="deleteJob?id=<s:property value="id" />#jobs">remove</a></td>
                  </s:if>
                </tr>
              </s:if>
            </s:iterator>
          </table>
        </s:if>
      </div>
      <!-- Finished Dataset Jobs -->
      <div id="datasets" class="border StandardTextDarkGrayParagraph benchAlign bottomMarginDataset">
        <p class="StandardTextDarkGrayParagraph2">
          <b>Datasets</b>
        </p>
        <p class="StandardTextDarkGrayParagraph">
          * Descriptors for the dataset were created outside of Chembench and uploaded by the user. <br />
          <br /> <i>Click on the name of dataset to visualize it.</i><br />
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
            </a><br />
            <a href="datasetFilesServlet?datasetName=<s:property value='name' />&user=<s:property value='userName'/>"><img
                alt="download" width="18" height="18" src="theme/img/download.png" /></a> <s:if test="userName=='all-users'"></s:if>
              <s:else>
                <a onclick="return confirmDelete('predictor')"
                  href="deleteDataset?id=<s:property value="id" />#datasets"><img alt="delete" width="15"
                  height="15" src="theme/img/delete.png" /></a>
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

      <!-- Finished Modeling Jobs -->
      <div id="predictors" class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
        <p class="StandardTextDarkGrayParagraph2">
          <b>Predictors</b><br />
        </p>
        <p class="StandardTextDarkGrayParagraph">* Predictor was built on a dataset with descriptors that were
          created outside of Chembench and uploaded by the user.</p>
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
                </s:a> <br /> <!--<a href="selectPredictor" name="predictorCheckBoxes" value="%{id}">Predict</a>--> <a
                href="projectFilesServlet?project=<s:property value='name' />&user=<s:property value="userName" />&projectType=modeling"><img
                  alt="download" width="18" height="18" src="theme/img/download.png" /></a> <s:if
                  test="userName=='all-users'"></s:if> <s:else>
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
      <!-- Finished Prediction Jobs -->
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
                </s:a><br />
              <a href="fileServlet?id=<s:property value="id" />&jobType=PREDICTION&file=predictionAsCsv"><img
                  alt="download" width="18" height="18" src="theme/img/download.png" /></a>
              <s:if test="userName=='all-users'"></s:if> <s:else>
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
