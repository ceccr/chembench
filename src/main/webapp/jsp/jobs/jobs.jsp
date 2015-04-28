<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="jobs" class="tab-pane active">
  <h3>
    Job Queue
    <button id="jobs-queue-refresh" class="btn btn-primary">
      <span class="glyphicon glyphicon-refresh"></span> Refresh
    </button>
  </h3>
  <p class="margin-below">
    Running jobs from all Chembench users are displayed below. Use the <b>Refresh</b> button to update the list.
    Other users can see your jobs while they are running, but only you can access your completed datasets,
    models, and predictions.
  </p>

  <s:if test="!incomingJobs.isEmpty()">
    <div class="panel panel-default">
      <div class="panel-heading">
        <h4>Unassigned Jobs</h4>
      </div>
      <div class="panel-body">
        <table class="table table-hover datatable">
          <thead>
          <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Job Type</th>
            <th class="date-created">Date</th>
            <th>Status</th>
            <th class="unsortable">Cancel</th>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="incomingJobs">
            <s:if test="adminUser || userName==user.userName">
              <tr>
                <td>
                  <div class="name-cell">
                    <span class="object-name"><s:property value="jobName" /></span>
                  </div>
                </td>
                <td>
                  <s:property value="userName" />
                </td>
                <td class="job-type">
                  <s:property value="jobType" />
                </td>
                <td class="date-created">
                  <s:date name="timeCreated" format="yyyy-MM-dd" />
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
        <table class="table table-hover datatable">
          <thead>
          <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Job Type</th>
            <th class="date-created">Date</th>
            <th>Status</th>
            <th class="unsortable">Cancel</th>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="localJobs">
            <s:if test="adminUser || userName==user.userName">
              <tr>
                <td>
                  <div class="name-cell">
                    <span class="object-name"><s:property value="jobName" /></span>
                  </div>
                </td>
                <td>
                  <s:property value="userName" />
                </td>
                <td class="job-type">
                  <s:property value="jobType" />
                </td>
                <td class="date-created">
                  <s:date name="timeCreated" format="yyyy-MM-dd" />
                </td>
                <td>
                  <s:property value="message" />
                </td>
                <td class="delete">
                  <s:if test="adminUser || user.userName.equals(userName)">
                    <s:url var="deleteUrl" action="deleteJob">
                      <s:param name="id" value="id" />
                    </s:url>
                    <s:a href="%{deleteUrl}">cancel</s:a>
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
        <table class="table table-hover datatable">
          <thead>
          <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Job Type</th>
            <th class="date-created">Date</th>
            <th>Status</th>
            <th class="unsortable">Cancel</th>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="lsfJobs">
            <s:if test="adminUser || userName==user.userName">
              <tr>
                <td>
                  <div class="name-cell">
                    <span class="object-name"><s:property value="jobName" /></span>
                  </div>
                </td>
                <td>
                  <s:property value="userName" />
                </td>
                <td class="job-type">
                  <s:property value="jobType" />
                </td>
                <td class="date-created">
                  <s:date name="timeCreated" format="yyyy-MM-dd" />
                </td>
                <td>
                  <s:property value="message" />
                </td>
                <td class="delete">
                  <s:if test="adminUser || user.userName.equals(userName)">
                    <s:url var="deleteUrl" action="deleteJob">
                      <s:param name="id" value="id" />
                    </s:url>
                    <s:a href="%{deleteUrl}">cancel</s:a>
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
        <table class="table table-hover datatable">
          <thead>
          <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Job Type</th>
            <th class="date-created">Date</th>
            <th class="unsortable">Cancel</th>
          </tr>
          </thead>
          <tbody>
          <s:iterator value="errorJobs">
            <s:if test="adminUser || userName==user.userName">
              <tr>
                <td>
                  <div class="name-cell">
                    <span class="object-name"><s:property value="jobName" /></span>
                  </div>
                </td>
                <td>
                  <s:property value="userName" />
                </td>
                <td class="job-type">
                  <s:property value="jobType" />
                </td>
                <td class="date-created">
                  <s:date name="timeCreated" format="yyyy-MM-dd" />
                </td>
                <td class="delete">
                  <s:if test="adminUser">
                    <s:url var="deleteUrl" action="deleteJob">
                      <s:param name="id" value="id" />
                    </s:url>
                    <s:a href="%{deleteUrl}">cancel</s:a>
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
