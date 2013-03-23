<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page language="java" import="java.util.*"%>

<html>
<head>
    <sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | My Bench</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link href="theme/ccbStyle.css" rel="stylesheet" type="text/css">
    <link href="theme/ccbStyleNavBar.css" rel="stylesheet" type="text/css">
    <link rel="stylesheet" href="theme/screen.css" type="text/css"
          media="screen, projection">
    <link rel="stylesheet" href="theme/print.css" type="text/css"
          media="print">
    <link href="theme/standard.css" rel="stylesheet" type="text/css">
    <link href="theme/links.css" rel="stylesheet" type="text/css">
    <link href="theme/dynamicTab.css" rel="stylesheet" type="text/css">
    <link rel="icon" href="/theme/img/mml.ico" type="image/ico">
    <link rel="SHORTCUT ICON" href="/theme/img/mml.ico">
    <link href="theme/customStylesheet.css" rel="stylesheet"
          type="text/css">
    <script src="javascript/script.js"></script>
    <script src="javascript/dataset.js"></script>
    <script language="javascript"
            src="javascript/jquery-1.6.4.min.js"></script>
    <script language="JavaScript" src="javascript/sortableTable.js"></script>
</head>
<body onload="setTabToMyBench();">

<div class="outer">
<div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
<div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>
<div class="StandardTextDarkGrayParagraph benchBackground benchAlign">
    <div class="homeLeft">
        <br /> <br />
        <p style="margin-left:20px">
            <b>My Bench</b> <br /> <br /> Every dataset, predictor, and
            prediction you have created on Chembench is available on this page.
            You can track progress of all the running jobs using the job queue.
            <br /> <br /> Publicly available datasets and predictors are also
            displayed. If you wish to share datasets or predictors you have
            developed with the Chembench community, please contact us at <a
                href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a>. <br />
            All data is sorted by the creation date in descending order (newest
            on top).
        </p>
    </div>
</div>
<!-- Queued, Local, and LSF Jobs -->
<a name="jobs"></a>
<div class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
<p class="StandardTextDarkGrayParagraph2">
    <b>Job Queue</b>
</p>
<i>Running jobs from all Chembench users are displayed below.
    Use the REFRESH STATUS button to update the list. Other users can
    see your jobs while they are running, but only you can access your
    completed datasets, predictors, and predictions.</i><br /> <br />
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
                    <td class="TableRowText02"><s:property
                            value="numCompounds" /></td>
                    <td class="TableRowText02"><s:if
                            test="jobTypeString!='dataset'">
                        <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                    <td class="TableRowText02"><s:date name="timeCreated"
                                                       format="yyyy-MM-dd HH:mm" /></td>
                    <td class="TableRowText02"><b><s:property
                            value="message" /><b></td>
                    <td class="TableRowText02"><s:if test="adminUser">
                        <a onclick="return confirmDelete('job')"
                           href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:if> <s:elseif
                            test="user.userName==userName && userName.conatains('guest')">
                        <a onclick="return confirmDelete('job')"
                           href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
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
                    <td class="TableRowText02"><s:property
                            value="numCompounds" /></td>
                    <td class="TableRowText02"><s:if
                            test="jobTypeString!='dataset'">
                        <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                    <td class="TableRowText02"><s:date name="timeCreated"
                                                       format="yyyy-MM-dd HH:mm" /></td>
                    <td class="TableRowText02"><b><s:property
                            value="message" /><b></td>
                    <td class="TableRowText02"><s:if test="adminUser">
                        <a onclick="return confirmDelete('job')"
                           href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:if> <s:elseif test="user.userName==userName && userName!='guest'">
                        <a onclick="return confirmDelete('job')"
                           href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
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
                    <td class="TableRowText02"><s:property
                            value="numCompounds" /></td>
                    <td class="TableRowText02"><s:if
                            test="jobTypeString!='dataset'">
                        <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                    <td class="TableRowText02"><s:date name="timeCreated"
                                                       format="yyyy-MM-dd HH:mm" /></td>
                    <td class="TableRowText02"><b><s:property
                            value="message" /><b></td>
                    <td class="TableRowText02"><s:if test="adminUser">
                        <a onclick="return confirmDelete('job')"
                           href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:if> <s:elseif test="user.userName==userName && userName!='guest'">
                        <a onclick="return confirmDelete('job')"
                           href="deleteJob?id=<s:property value="id" />#jobs">cancel</a>
                    </s:elseif></td>
                </tr>
            </s:if>
        </s:iterator>
    </table>
</s:if>
<s:else>
    (The LSF queue is empty.)
</s:else>
<br /> <br />
<!-- Error Jobs -->
<s:if test="! errorJobs.isEmpty()">
    <b>Jobs with errors: </b>
    <br />
    <div class="StandardTextDarkGray">One or more of your jobs
        has encountered an error and cannot be completed. The Chembench
        administrators have been contacted and will resolve the issue as
        soon as possible. We will let you know when the error is fixed.</div>
    <table class="sortable">
        <tr>
            <th class="TableRowText01">Name</th>
            <th class="TableRowText01">Owner</th>
            <th class="TableRowText01">Job Type</th>
            <th class="TableRowText01">Number of Compounds</th>
            <th class="TableRowText01">Number of Models</th>
            <th class="TableRowText01">Time Created</th>
            <s:if test="adminUser">
                <th class="TableRowText01_unsortable">Remove Job (admin
                    only)</th>
            </s:if>
        </tr>
        <s:iterator value="errorJobs">
            <s:if test="adminUser || userName==user.userName">
                <tr>

                    <td class="TableRowText02"><s:if
                            test="adminUser&&jobTypeString=='modeling'">
                        <s:url id="predictorLink" value="/viewPredictor"
                               includeParams="none">
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
                    <td class="TableRowText02"><s:property
                            value="numCompounds" /></td>
                    <td class="TableRowText02"><s:if
                            test="jobTypeString!='dataset'">
                        <s:property value="numModels" />
                    </s:if> <s:else>N/A</s:else></td>
                    <td class="TableRowText02"><s:date name="timeCreated"
                                                       format="yyyy-MM-dd HH:mm" /></td>
                    <s:if test="adminUser">
                        <td class="TableRowText02"><a
                                onclick="return confirmDelete('job')"
                                href="deleteJob?id=<s:property value="id" />#jobs">remove</a>
                        </td>
                    </s:if>
                </tr>
            </s:if>
        </s:iterator>
    </table>
</s:if>
</div>
<!-- Finished Dataset Jobs -->
<a name="datasets"></a>
<div class="border StandardTextDarkGrayParagraph benchAlign bottomMarginDataset">
    <p class="StandardTextDarkGrayParagraph2">
        <b>Datasets</b>
    </p>
    <p class="StandardTextDarkGrayParagraph">
        * Descriptors for the dataset were created outside of Chembench and
        uploaded by the user. <br /> <i>Click on the name of dataset
        to visualize it.</i><br />
        <s:if
                test="user.userName!='guest'&&user.showPublicDatasets=='SOME'">
            <i>Additional public datasets are available. You can choose to
                show these from the <a href="editProfile">edit profile</a> page.
            </i>
            <br />
        </s:if>
        <s:if test="user.userName!='guest'&&user.showPublicDatasets=='ALL'">
            <i>You are currently viewing all available public datasets.
                You can choose to hide these from the <a href="editProfile">edit
                    profile</a> page.
            </i>
            <br />
        </s:if>
        <s:if
                test="user.userName!='guest'&&user.showPublicDatasets=='NONE'">
            <i>Public datasets are currently hidden. You can choose to
                show these from the <a href="editProfile">edit profile</a> page.
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
            <th class="TableRowText01_unsortable">Download</th>
            <th class="TableRowText01_unsortable">Delete</th>
        </tr>
        <s:iterator value="userDatasets">
            <s:if test="hasBeenViewed=='YES'">
                <tr class="TableRowText02">
            </s:if>
            <s:else>
                <tr class="TableRowText03">
            </s:else>

            <td align="center"><a
                    href="viewDataset?id=<s:property value="id" />"> <s:property
                    value="name" />
            </a></td>
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
                <td><a
                        href="datasetFilesServlet?datasetName=<s:property value="name" />&user=all-users">download</a></td>
                <td>
                    <!-- dataset is public, so no delete option -->
                </td>
            </s:if>
            <s:else>
                <td>Private</td>
                <td><a
                        href="datasetFilesServlet?datasetName=<s:property value="name" />&user=<s:property value="user.userName" />">download</a></td>
                <td><a onclick="return confirmDelete('dataset')"
                       href="deleteDataset?id=<s:property value="id" />#datasets">delete</a></td>
            </s:else>

            </tr>
        </s:iterator>
        <br />
        <br />
    </table>
</div>

<!-- Finished Modeling Jobs -->
<a name="predictors"></a> <br />

<div class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
    <p class="StandardTextDarkGrayParagraph2">
        <br /> <b>Predictors</b><br />
    </p>
    <p class="StandardTextDarkGrayParagraph">* Predictor was built
        on a dataset with descriptors that were created outside of
        Chembench and uploaded by the user.</p>
    <div class="StandardTextDarkGrayParagraph">
        <i>Click on the name of a predictor to analyze the modeling
            results.</i><br />
    </div>
    <table class="sortable" id="predictors">
        <tr>
            <th class="TableRowText01">Name</th>
            <th class="TableRowText01" style="width: 30px;">Dataset</th>
            <s:if test="adminUser">
                <th class="TableRowText01">Nfold</th>
                <th class="TableRowText01">Act type</th>
            </s:if>
            <th class="TableRowText01">External Set R<sup>2</sup> or CCR
            </th>
            <th class="TableRowText01">Modeling Method</th>
            <th class="TableRowText01">Descriptor Type</th>
            <th class="TableRowText01">Public/Private</th>
            <th class="TableRowText01">Date Created</th>
            <th class="TableRowText01_unsortable">Download</th>
            <th class="TableRowText01_unsortable">Delete</th>
        </tr>
        <s:iterator value="userPredictors">
            <s:if test="hasBeenViewed=='YES'">
                <tr class="TableRowText02">
            </s:if>
            <s:else>
                <tr class="TableRowText03">
            </s:else>

            <s:url id="predictorLink" value="/viewPredictor"
                   includeParams="none">
                <s:param name="id" value='id' />
            </s:url>
            <td><s:a href="%{predictorLink}">
                <s:property value="name" />
            </s:a></td>
            <td><a href="viewDataset?id=<s:property value="datasetId" />">
                <s:property value="datasetDisplay" />
            </a></td>
            <s:if test="adminUser">
                <td><s:if test="childType=='NFOLD'">YES</s:if> <s:else>NO</s:else>
                </td>
                <td><s:property value="activityType" /></td>
            </s:if>
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
            <td><s:property value="modelMethod" /></td>
            <s:if test="descriptorGeneration=='UPLOADED'">
                <td>*<s:property value="uploadedDescriptorType" /></td>
            </s:if>
            <s:else>
                <td><s:property value="descriptorGeneration" /></td>
            </s:else>
            <td><s:if test="userName=='all-users'">Public</s:if> <s:else>Private</s:else></td>
            <td><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
            <td><a
                    href="projectFilesServlet?project=<s:property value='name' />&user=<s:property value="userName" />&projectType=modeling">download</a></td>
            <td><s:if test="userName=='all-users'"></s:if> <s:else>
                <a onclick="return confirmDelete('predictor')"
                   href="deletePredictor?id=<s:property value="id" />#predictors">delete</a>
            </s:else></td>
            </tr>
        </s:iterator>
        <br />
        <br />
    </table>
</div>
<!-- Finished Prediction Jobs -->
<div class="border StandardTextDarkGrayParagraph benchAlign bottomMargin">
    <p class="StandardTextDarkGrayParagraph2">
        <br /> <b>Predictions</b>
    </p>
    <div class="StandardTextDarkGrayParagraph">
        <i>Click on the name of a prediction to see the results.</i><br />
    </div>
        <s:if test="! userPredictions.isEmpty()">
            <table class="sortable" id="predictions">
                <tr>
                    <th class="TableRowText01">Name</th>
                    <th class="TableRowText01">Dataset</th>
                    <th class="TableRowText01">Predictor</th>
                    <th class="TableRowText01">Date Created</th>
                    <th class="TableRowText01_unsortable">Download</th>
                    <th class="TableRowText01_unsortable">Delete</th>
                </tr>
                <s:iterator value="userPredictions">
                    <s:if test="hasBeenViewed=='YES'">
                        <tr class="TableRowText02">
                    </s:if>
                    <s:else>
                        <tr class="TableRowText03">
                    </s:else>

                    <s:url id="predictionLink" value="/viewPrediction"
                           includeParams="none">
                        <s:param name="id" value='id' />
                    </s:url>
                    <td><s:a href="%{predictionLink}">
                        <s:property value="name" />
                    </s:a></td>
                    <td><s:property value="datasetDisplay" /></td>
                    <td><s:property value="predictorNames" /></td>
                    <td><s:date name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                    <td><a
                            href="projectFilesServlet?project=<s:property value='name' />&user=<s:property value='userName' />&projectType=prediction">download</a></td>
                    <td><a onclick="return confirmDelete('prediction')"
                           href="deletePrediction?id=<s:property value="id" />#predictions">delete</a></td>
                    </tr>
                </s:iterator>
                <br />
                <br />
            </table>
        </s:if>
</div>
<div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>
</body>
</html>