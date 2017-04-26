<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="showRoundedPrediction" value="%{mcraPredictions[0].roundedPredictedActivity != -1}"/>

<div class="">
    <s:url var="csvDownloadUrl" action="fileServlet" escapeAmp="false">
        <s:param name="downloadPath" value="downloadPath" />
        <s:param name="jobType" value="@edu.unc.ceccr.chembench.actions.McraAction@JOB_TYPE" />
    </s:url>
    <s:a href="%{csvDownloadUrl}" cssClass="btn btn-sm btn-default"
          role="button" id="download-csv" >
        <span class="glyphicon glyphicon-save"></span> Download (.csv)
    </s:a>
</div>

<table id="prediction-values" class="table table-bordered compound-list datatable">

    <thead>
    <tr role="row">
        <th colspan="<s:property value='%{showRoundedPrediction ? 4 : 3}'/>" rowspan="1"></th>
        <s:iterator value="mcraPredictions[0].descriptors">
            <th colspan="3" rowspan="1"><s:property value="name"/></th>
        </s:iterator>
    </tr>

    <tr>
        <th>Compound</th>
        <th>Prediction</th>

        <s:if test="%{showRoundedPrediction}"> <th>Rounded Prediction</th> </s:if>

        <th class="unsortable"># Neighbors</th>
        <s:iterator value="mcraPredictions[0].descriptors">
            <th>Activity</th>
            <th>Similarity</th>
            <th>Neighbors</th>
        </s:iterator>
    </tr>
    </thead>

    <tbody>
    <s:iterator value="mcraPredictions">
        <tr>
            <td><s:property value="name" /></td>

            <td><s:property value="getText('{0,number,#,##0.##}',{predictedActivity})" /></td>
            <%--<s:property value="getText('{0,number,#,##0.00}',{profit})"/>--%>

            <s:if test="%{showRoundedPrediction}">
                <td><s:property value="roundedPredictedActivity" /></td>
            </s:if>

            <td><s:property value="numNearestNeighbors" /></td>
            <s:iterator value="descriptors">
                <td><s:property value="getText('{0,number,#,##0.##}',{averageActivity})" /></td>
                <td><s:property value="getText('{0,number,#,##0.##}',{averageSimilarity})" /></td>
                <td><s:property value="neighborIds" /></td>
            </s:iterator>
        </tr>
    </s:iterator>
    </tbody>
</table>

<script>
    // XXX draw callback must be declared _before_ DataTable() is called
    $('#prediction-values').on('draw.dt', function() {
        $(this).closest('.dataTables_scroll').doubleScroll();
    }).DataTable($.extend({
        'order': [[0, 'asc']],
        'scrollX': true
    }, Chembench.DATATABLE_OPTIONS));

</script>