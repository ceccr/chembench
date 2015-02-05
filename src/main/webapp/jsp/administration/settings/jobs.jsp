<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" %>

<html>
<head>
  <title>Chembench | Administration</title>
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
  <script src="javascript/admin.js"></script>
  <script language="JavaScript" src="javascript/sortableTable.js"></script>
  <script language="javascript" src="javascript/jquery-1.6.4.min.js"></script>

  <script type="text/javascript">
    function checkContent() {
      if (document.getElementById("content").value == "") {
        return (window.confirm("Send emails without content?"));
      } else {
        return true;
      }
    }
  </script>
</head>
<body onload="setTabToHome();">

<!-- headers -->
<div id="bodyDIV"></div>
<!-- used for the "Please Wait..." box. Do not remove. -->
<!--  page content -->
<!-- Queued, Local, and LSF Jobs -->
<p class="StandardTextDarkGrayParagraph2">
  <b>Job Queue</b>
</p>
<i>Running jobs from all Chembench users are displayed below. Use the REFRESH STATUS button to update the list.
  Other users can see your jobs while they are running, but only you can access your completed datasets, predictors,
  and predictions.</i>
<br />
<br />

<form action="adminJobs">
  <div class="StandardTextDarkGrayParagraph">
    <button type="submit">REFRESH STATUS</button>
  </div>
</form>
<br />
<!-- Queued (incomingJobs) -->
<b>Unassigned Jobs: </b>
<br />
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
<br />
<br />
<!-- Local Jobs -->
<b>Jobs on Local Queue: </b>
<br />
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
<br />
<br />
<!-- LSF Jobs -->
<b>Jobs on LSF Queue: </b>
<br />
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
<br />
<br />
<!-- Error Jobs -->
<s:if test="! errorJobs.isEmpty()">
  <b>Jobs with errors: </b>
  <br />

  <div class="StandardTextDarkGray">One or more of your jobs has encountered an error and cannot be completed.
    The Chembench administrators have been contacted and will resolve the issue as soon as possible. We will let you
    know when the error is fixed.
  </div>
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

</body>
</html>