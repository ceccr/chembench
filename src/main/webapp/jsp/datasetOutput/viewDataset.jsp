<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page import="edu.unc.ceccr.chembench.global.Constants"%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="/jsp/main/head.jsp"%>
<title>Chembench | View Dataset</title>
</head>
<body>
  <div id="main" class="container">
    <%@ include file="/jsp/main/header.jsp"%>

    <section id="content">
      <h2>
        Dataset Details:
        <s:property value="dataset.name" />
        <s:a action="jobs" anchor="datasets">
          <button class="btn btn-primary">Back to Datasets</button>
        </s:a>
      </h2>
      <s:hidden id="username" value="%{dataset.userName}" />
      <s:hidden id="dataset-name" value="%{dataset.name}" />

      <div class="list-group">
        <div class="list-group-item">
          <h4 class="list-group-item-heading">General information</h4>
          <dl class="dl-horizontal properties-list">
            <dt>Dataset type</dt>
            <dd>
              <div class="dataset-type">
                <s:property value="dataset.datasetType" />
              </div>
            </dd>
            <dt>Number of compounds</dt>

            <dd>
              <s:property value="dataset.numCompound" />
            </dd>

            <dt>Activity type</dt>
            <dd>
              <s:if test="!dataset.hasActivities()">
                None
              </s:if>
              <s:else>
                <div class="activity-type">
                  <s:property value="dataset.modelType" />
                </div>
              </s:else>
            </dd>

            <dt>Modelability index</dt>
            <dd>
              <s:if test="!dataset.canGenerateModi()">
                <span class="text-muted">MODI cannot be generated for this dataset.</span>
              </s:if>
              <s:else>
                <s:if test="dataset.modiGenerated">
                  <s:if test="dataset.modi >= @edu.unc.ceccr.chembench.global.Constants@MODI_MODELABLE">
                    <span class="modi-value text-success" title="Modelable"> <s:property
                        value="getText('{0, number, #, ##0.00}', {dataset.modi})" /></span>
                  </s:if>
                  <s:else>
                    <span class="modi-value text-warning" title="Not modelable"> <s:property
                        value="getText('{0, number, #, ##0.00}', {dataset.modi})" /></span>
                  </s:else>
                </s:if>
                <s:else>
                  <input type="hidden" name="dataset-id" value="<s:property value="dataset.id" />">
                  <span class="text-warning">Not generated</span>
                  <button class="btn btn-primary btn-xs generate-modi">Generate MODI</button>
                </s:else>
              </s:else>
            </dd>

            <dt>Date created</dt>
            <dd>
              <s:date name="dataset.createdTime" format="yyyy-MM-dd HH:mm" />
            </dd>
          </dl>
        </div>

        <div class="list-group-item">
          <h4 class="list-group-item-heading">Descriptors</h4>
          <dl class="dl-horizontal properties-list">
            <dt>Descriptors available</dt>
            <dd class="available-descriptors">
              <s:property value="dataset.availableDescriptors" />
            </dd>

            <s:if test="!dataset.uploadedDescriptorType.isEmpty()">
              <dt>Uploaded descriptor type</dt>
              <dd>
                <s:property value="dataset.uploadedDescriptorType" />
              </dd>
            </s:if>
          </dl>
        </div>

        <div class="list-group-item">
          <h4 class="list-group-item-heading">
            Description and paper reference
            <s:if test="editable">
              <button id="edit-description-reference" class="btn btn-primary btn-xs">
                <span class="glyphicon glyphicon-pencil"></span> Edit
              </button>
            </s:if>
            <span id="description-reference-buttons">
              <button id="cancel-changes" class="btn btn-default btn-xs">
                <span class="glyphicon glyphicon-remove"></span> Cancel
              </button>
              <button id="save-changes" type="submit" class="btn btn-primary btn-xs">
                <span class="glyphicon glyphicon-floppy-disk"></span> Save
              </button>
            </span>
          </h4>
          <dl id="description-reference-text" class="properties-list">
            <dt>Description</dt>
            <dd id="description">
              <s:if test="dataset.description.isEmpty()">
                (No description given.)
              </s:if>
              <s:else>
                <s:property value="dataset.description" />
              </s:else>
            </dd>

            <dt>Paper reference</dt>
            <dd id="paper-reference">
              <s:if test="dataset.paperReference.isEmpty()">
                (No paper reference given.)
              </s:if>
              <s:else>
                <s:property value="dataset.paperReference" />
              </s:else>
            </dd>
          </dl>

          <s:if test="editable">
            <s:form action="updateDataset" enctype="multipart/form-data" theme="simple">
              <div class="form-group">
                <label for="datasetDescription">Description:</label>
                <s:textarea id="datasetDescription" name="datasetDescription" value="%{dataset.description}"
                  cssClass="form-control" />
              </div>

              <div class="form-group">
                <label for="datasetReference">Paper reference:</label>
                <s:textarea id="datasetReference" name="datasetReference" value="%{dataset.paperReference}"
                  cssClass="form-control" />
              </div>

              <s:hidden name="id" value="%{id}" />
            </s:form>
          </s:if>
        </div>
      </div>

      <hr>

      <ul class="nav nav-tabs">
        <li class="active"><a href="#all-compounds" data-toggle="tab">All Compounds</a></li>
        <s:if test="dataset.hasActivities()">
          <s:if test="dataset.splitType.equals(@edu.unc.ceccr.chembench.global.Constants@NFOLD)">
            <li><a href="#folds" data-toggle="tab">Folds</a></li>
          </s:if>
          <s:else>
            <li><a href="#external-set" data-toggle="tab">External Set</a></li>
          </s:else>
          <li><a href="#activity-histogram" data-toggle="tab">Activity Histogram</a></li>
        </s:if>
        <li><a href="#descriptors" data-toggle="tab">Descriptors</a></li>
        <li><a href="#heatmap" data-toggle="tab">Heatmap</a></li>
      </ul>

      <div class="tab-content">
        <div id="all-compounds" class="tab-pane active">
          <h3>All Compounds</h3>
          <p class="tab-description">All compounds in your dataset are listed in the table below, including those in
            your external set.</p>
          <table class="table table-hover tablesorter compound-list">
            <thead>
              <tr>
                <th class="name">Compound Name</th>
                <s:if test="dataset.hasStructures()">
                  <th class="sorter-false">Structure</th>
                </s:if>
                <s:if test="dataset.hasActivities()">
                  <th>Activity</th>
                </s:if>
              </tr>
            </thead>
            <tbody>
              <s:iterator value="datasetCompounds">
                <tr>
                  <td class="name"><s:property value="compoundId" /></td>
                  <s:if test="!dataset.sdfFile.isEmpty()">
                    <td><s:url var="imageUrl" value="imageServlet" escapeAmp="false">
                        <s:param name="user" value="%{dataset.userName}" />
                        <s:param name="projectType" value="'dataset'" />
                        <s:param name="compoundId" value="%{compoundId}" />
                        <s:param name="datasetName" value="%{dataset.name}" />
                      </s:url> <img src=<s:property value="imageUrl" /> class="img-thumbnail" width="125px" height="125px">
                    </td>
                  </s:if>
                  <s:if test="dataset.hasActivities()">
                    <td><s:property value="activityValue" /></td>
                  </s:if>
                </tr>
              </s:iterator>
            </tbody>
          </table>
        </div>

        <s:if test="dataset.splitType.equals(@edu.unc.ceccr.chembench.global.Constants@NFOLD)">
          <div id="folds" class="tab-pane">
            <h3>Folds</h3>
            <p>The compounds in each fold of your dataset are listed below. Use the fold navigation buttons to
              switch between folds.</p>
            <nav class="text-center">
              <ul class="pagination">
                <s:iterator value="foldNumbers" status="s">
                  <s:url var="foldUrl" action="viewDatasetFold">
                    <s:param name="id" value="dataset.id" />
                    <s:param name="foldNumber" value="%{#s.count}" />
                  </s:url>
                  <s:if test="%{#s.first}">
                    <li class="first-fold active"><s:a href="%{foldUrl}"><s:property /></s:a></li>
                  </s:if>
                  <s:elseif test="%{#s.last}">
                    <li class="last-fold"><s:a href="%{foldUrl}"><s:property /></s:a></li>
                  </s:elseif>
                  <s:else>
                    <li><s:a href="%{foldUrl}"><s:property /></s:a></li>
                  </s:else>
                </s:iterator>
              </ul>
            </nav>

            <table class="table table-hover tablesorter compound-list">
              <thead>
                <tr>
                  <th class="name">Compound Name</th>
                  <s:if test="dataset.hasStructures()">
                    <th>Structure</th>
                  </s:if>
                  <s:if test="dataset.hasActivities()">
                    <th>Activity</th>
                  </s:if>
                </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
          </div>
        </s:if>
        <s:else>
          <div id="external-set" class="tab-pane">
            <h3>External Set</h3>
            <p class="tab-description">The compounds in your dataset's external test set are listed in the table
              below.</p>
            <table class="table table-hover tablesorter compound-list">
              <thead>
                <tr>
                  <th class="name">Compound Name</th>
                  <s:if test="dataset.hasStructures()">
                    <th class="sorter-false">Structure</th>
                  </s:if>
                  <s:if test="dataset.hasActivities()">
                    <th>Activity</th>
                  </s:if>
                </tr>
              </thead>
              <tbody>
                <s:iterator value="externalCompounds">
                  <tr>
                    <td class="name"><s:property value="compoundId" /></td>
                    <s:if test="!dataset.sdfFile.isEmpty()">
                      <td><s:url var="imageUrl" value="imageServlet" escapeAmp="false">
                          <s:param name="user" value="%{dataset.userName}" />
                          <s:param name="projectType" value="'dataset'" />
                          <s:param name="compoundId" value="%{compoundId}" />
                          <s:param name="datasetName" value="%{dataset.name}" />
                        </s:url> <img src=<s:property value="imageUrl" /> class="img-thumbnail" width="125px" height="125px">
                      </td>
                    </s:if>
                    <s:if test="dataset.hasActivities()">
                      <td><s:property value="activityValue" /></td>
                    </s:if>
                  </tr>
                </s:iterator>
              </tbody>
            </table>
          </div>
        </s:else>

        <div id="descriptors" class="tab-pane">
          <h3>Descriptors</h3>

          <p class="tab-description">Boudin ad laboris, jowl cillum in excepteur doner. Tempor et velit tail, in
            corned beef aliquip est tongue ut qui cupidatat frankfurter. Lorem elit quis capicola ut nulla flank tempor
            voluptate consectetur corned beef brisket labore mollit andouille. Pork belly tempor exercitation tongue
            cupidatat consectetur andouille lorem et aute short ribs ham hock. Ea veniam adipisicing occaecat, strip
            steak tail sunt cow alcatra laboris aute proident eu.</p>
        </div>

        <div id="activity-histogram" class="tab-pane">
          <h3>Activity Histogram</h3>
          <p class="tab-description">Mollit pig brisket, shankle id commodo qui. Ut est aliqua, commodo in ham hock
            exercitation short ribs flank. Ex non cupim, brisket id sed aliquip ipsum kevin pork belly dolore proident
            tri-tip prosciutto flank. Incididunt pig quis ut dolore veniam tenderloin tri-tip dolore in cupidatat esse
            tail andouille. Irure rump laboris, do turkey shank ullamco shoulder pork chop.</p>
        </div>

        <div id="heatmap" class="tab-pane">
          <h3>Heatmap</h3>
          <p class="tab-description">Voluptate non jowl ribeye irure sirloin ullamco adipisicing alcatra ham hock
            beef. Boudin corned beef labore, salami minim qui occaecat. Cow enim magna meatloaf reprehenderit capicola.
            Culpa frankfurter chicken dolore, ribeye pork loin ea cupidatat fatback labore andouille rump tongue eiusmod
            ad.</p>
        </div>

      </div>
    </section>

    <%@include file="/jsp/main/footer.jsp"%>
  </div>

  <%@ include file="/jsp/main/tail.jsp"%>
  <script src="assets/js/viewDataset.js"></script>
</body>
</html>
