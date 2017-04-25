<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
<head>
  <%@ include file="/jsp/main/head.jsp" %>
  <title>Chembench | My Bench</title>
</head>
<body>
<div id="main" class="container">
  <%@ include file="/jsp/main/header.jsp" %>

  <section id="content">
    <h2>My Bench</h2>

    <p>Every dataset, predictor, and prediction you have created on Chembench is available on this page. You can
      track progress of all the running jobs using the job queue.
    </p>

    <p>
      Publicly available datasets and models are also displayed. If you wish to share datasets or models you
      have developed with the Chembench community, please contact us at <a href="mailto:thethorn@cs.unc.edu">thethorn@cs.unc.edu</a>.
    </p>

    <p>All data is sorted by the creation date in descending order (newest on top).</p>

    <ul class="nav nav-tabs">
      <li class="active"><a href="#jobs" data-toggle="tab">Job Queue</a></li>
      <li><a href="#datasets" data-toggle="tab">Datasets</a></li>
      <li><a href="#models" data-toggle="tab">Models</a></li>
      <li><a href="#predictions" data-toggle="tab">Predictions</a></li>
    </ul>

    <div class="tab-content">
      <div id="jobs" class="tab-pane active">
        <h3>Job Queue

          <div class="header-extras">
            <button id="jobs-queue-refresh" class="btn btn-sm btn-primary has-spinner">
              <span class="spinner"><span class="glyphicon glyphicon-refresh fa-spin"></span></span><span
                id="refresh-text">Refresh</span></button>

            <label for="autorefresh-interval">Auto-refresh every:</label>
            <select id="autorefresh-interval" class="form-control input-sm">
              <option value="5">5 s</option>
              <option value="15" selected="selected">15 s</option>
              <option value="30">30 s</option>
              <option value="60">60 s</option>
              <option value="0">(Disabled)</option>
            </select>
          </div>
        </h3>

        <p class="margin-below">
          Running jobs from all Chembench users are displayed below. Use the <b>Refresh</b> button to update the list.
        </p>

        <div id="unassigned-jobs" class="panel panel-warning">
          <div class="panel-heading">
            <h4>Unassigned Jobs</h4>
          </div>
          <table class="table table-hover table-bordered datatable job-table"
                 data-url="<s:url action="getUnassignedJobs" namespace="/api" />" data-object-type="job"
                 data-queue-name="unassigned">
            <thead>
            <tr>
              <th data-property="jobName">Name</th>
              <th data-property="userName">Owner</th>
              <th data-property="jobType">Job Type</th>
              <th data-property="timeCreated" data-sort-direction="desc" class="date-created">Date</th>
              <th data-property="cancel" data-transient="data-transient" class="unsortable"></th>
            </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>

        <div class="panel panel-default">
          <div class="panel-heading">
            <h4>Jobs on Local Queue</h4>
          </div>
          <table class="table table-hover table-bordered datatable job-table"
                 data-url="<s:url action="getLocalJobs" namespace="/api" />" data-object-type="job"
                 data-queue-name="local">
            <thead>
            <tr>
              <th data-property="jobName">Name</th>
              <th data-property="userName">Owner</th>
              <th data-property="jobType">Job Type</th>
              <th data-property="timeCreated" data-sort-direction="desc" class="date-created">Date</th>
              <th data-property="message">Status</th>
              <th data-property="cancel" data-transient="data-transient" class="unsortable"></th>
            </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>

        <div class="panel panel-default">
          <div class="panel-heading">
            <h4>Jobs on LSF Queue</h4>
          </div>
          <table class="table table-hover table-bordered datatable job-table"
                 data-url="<s:url action="getLsfJobs" namespace="/api" />" data-object-type="job" data-queue-name="LSF">
            <thead>
            <tr>
              <th data-property="jobName">Name</th>
              <th data-property="userName">Owner</th>
              <th data-property="jobType">Job Type</th>
              <th data-property="timeCreated" data-sort-direction="desc" class="date-created">Date</th>
              <th data-property="message">Status</th>
              <th data-property="cancel" data-transient="data-transient" class="unsortable"></th>
            </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>

        <div id="jobs-with-errors" class="panel panel-danger">
          <div class="panel-heading">
            <h4>Jobs with Errors</h4>
          </div>
          <table class="table table-hover table-bordered datatable job-table"
                 data-url="<s:url action="getErrorJobs" namespace="/api" />" data-object-type="job"
                 data-queue-name="error">
            <thead>
            <tr>
              <th data-property="jobName">Name</th>
              <th data-property="userName">Owner</th>
              <th data-property="jobType">Job Type</th>
              <th data-property="timeCreated" data-sort-direction="desc" class="date-created">Date</th>
              <th data-property="cancel" data-transient="data-transient" class="unsortable"></th>
            </tr>
            </thead>
            <tbody></tbody>
          </table>
        </div>
      </div>

      <s:set name="showAll">true</s:set>
      <div id="datasets" class="tab-pane">
        <h3>Datasets</h3>
        <p>An asterisk (*) in the <b>Descriptors</b> field indicates an uploaded descriptor type.</p>
        <%@ include file="mybench-datasets-wrap.jsp" %>
      </div>

      <div id="models" class="tab-pane">
        <h3>Models</h3>
        <%@ include file="mybench-models-wrap.jsp" %>
      </div>

      <div id="predictions" class="tab-pane">
        <h3>Predictions</h3>
        <%@ include file="mybench-predictions-wrap.jsp" %>
      </div>
    </div>
  </section>

  <%@ include file="/jsp/main/footer.jsp" %>
</div>

<%@ include file="/jsp/main/tail.jsp" %>
<script src="${pageContext.request.contextPath}/assets/js/mybench.js"></script>
</body>
</html>
