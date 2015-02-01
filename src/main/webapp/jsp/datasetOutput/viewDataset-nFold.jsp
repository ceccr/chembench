<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" %>

<!-- External Compounds -->

<script language="JavaScript" src="javascript/sortableTable.js"></script>
<p class="StandardTextDarkGray"><u><b>Compounds Chosen for Each External Fold</b></u></p>
<table width="924" align="center">
  <tr>
    <td>
      <p class="StandardTextDarkGray" width="550">View Fold:
        <s:iterator value="foldNums" status="foldNumsStatus">
          <s:if test="foldNums[#foldNumsStatus.index]==currentFoldNumber"><s:property /></s:if>
          <s:else><a href="#tabs" onclick=loadNFoldCompoundsTab("viewDatasetNFoldSection?id=<s:property
              value='dataset.id' />&currentFoldNumber=<s:property />&orderBy=<s:property
              value='orderBy' />&sortDirection=<s:property value='sortDirection' />")><s:property /></a></s:else>
        </s:iterator>
      </p>
    </td>
  </tr>
  <tr>
    <td>
      <s:if test="externalFold.size()!=0">
        <!-- body for left side table -->
        <p class="StandardTextDarkGray" width="550">External Fold <s:property value="currentFoldNumber" />:</p>
        <table width="924" align="center" class="sortable" id="nfoldCompounds">
          <tr>
            <th class="TableRowText01">Compound ID
              <a href="#tabs" onclick=loadNFoldCompoundsTab("viewDatasetNFoldSection?id=<s:property
                  value='dataset.id' />&currentFoldNumber=<s:property
                  value='currentFoldNumber' />&orderBy=compoundId&sortDirection=asc")><img
                  src="theme/img/sortArrowUp.png" /></a>
              <a href="#tabs" onclick=loadNFoldCompoundsTab("viewDatasetNFoldSection?id=<s:property
                  value='dataset.id' />&currentFoldNumber=<s:property
                  value='currentFoldNumber' />&orderBy=compoundId&sortDirection=desc")><img
                  src="theme/img/sortArrowDown.png" /></a>
            </th>
            <s:if test="!dataset.sdfFile.isEmpty()">
              <th class="TableRowText01_unsortable">Structure</th>
            </s:if>
            <th class="TableRowText01">Activity
              <a href="#tabs" onclick=loadNFoldCompoundsTab("viewDatasetNFoldSection?id=<s:property
                  value='dataset.id' />&currentFoldNumber=<s:property
                  value='currentFoldNumber' />&orderBy=activityValue&sortDirection=asc")><img
                  src="theme/img/sortArrowUp.png" /></a>
              <a href="#tabs" onclick=loadNFoldCompoundsTab("viewDatasetNFoldSection?id=<s:property
                  value='dataset.id' />&currentFoldNumber=<s:property
                  value='currentFoldNumber' />&orderBy=activityValue&sortDirection=desc")><img
                  src="theme/img/sortArrowDown.png" /></a>
            </th>
          </tr>
          <s:iterator value="externalFold" status="externalFoldStatus">
            <tr>
              <td class="TableRowText02"><s:property value="compoundId" /></td>
              <s:if test="!dataset.sdfFile.isEmpty()">
                <td class="TableRowText02">
                  <a class="compound_img_a" href="#" onclick="window.open('compound3D?compoundId=<s:property
                      value="url_friendly_id" />&projectType=dataset&user=<s:property
                      value="dataset.userName" />&datasetName=<s:property
                      value="dataset.name" />', '','width=350, height=350'); return false;">
                    <img
                        src="imageServlet?user=<s:property value="dataset.userName" />&projectType=dataset&compoundId=<s:property value='url_friendly_id' />&datasetName=<s:property value="dataset.name" />"
                        border="0" height="150" onmouseover='enlargeImage(this);' onmouseout='shrinkImage(this)' />
                  </a>
                </td>
              </s:if>
              <td class="TableRowText02"><s:property value="activityValue" /></td>
            </tr>
          </s:iterator>
        </table>
      </s:if>
      <s:else>
        <br />

        <p class="StandardTextDarkGray">There are no compounds in your dataset's external validation set.</p>
        <br /><br />
      </s:else>
    </td>
  </tr>
</table>
<!-- End External Compounds -->