<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page language="java" import="java.util.*"%>
<html>
<head>
<sx:head debug="false" cache="false" compressed="true" />
<title>CHEMBENCH | View Dataset</title>

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

<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js"></script>
<link rel="stylesheet" type="text/css"
  href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.min.css"></link>

<script language="javascript" src="javascript/chembench.js" />
<script src="javascript/AC_RunActiveContent.js"></script>
<script src="javascript/hookMouseWheel.js"></script>

<script language="javascript">

  function loadAllCompoundsTab(newUrl){
    //When the user changes which page they're on in the All Compounds tab
    //or changes the sorted element, run this function to update the tab's content

    //prepare the AJAX object
    var ajaxObject = GetXmlHttpObject();
    ajaxObject.onreadystatechange=function(){
      if(ajaxObject.readyState==4){
        hideLoading();
                $('div[aria-labelledby="AllCompounds"]').html(ajaxObject.responseText);
      }
    }
    showLoading("LOADING. PLEASE WAIT.")

    //send request
    ajaxObject.open("GET",newUrl,true);
    ajaxObject.send(null);

    return true;
  }

  function loadNFoldCompoundsTab(newUrl){
    //When the user changes which page they're on in the N-Fold External Compounds tab
    //or changes the sorted element, run this function to update the tab's content

    //prepare the AJAX object
    var ajaxObject = GetXmlHttpObject();
    ajaxObject.onreadystatechange=function(){
      if(ajaxObject.readyState==4){
        hideLoading();
          document.getElementById("externalCompoundsNFoldDiv").innerHTML=ajaxObject.responseText;
      }
    }
    showLoading("LOADING. PLEASE WAIT.")

    //send request
    ajaxObject.open("GET",newUrl,true);
    ajaxObject.send(null);

    return true;
  }

  function loadExternalCompoundsTab(newUrl){
    //When the user changes which page they're on in the External Compounds tab
    //or changes the sorted element, run this function to update the tab's content

    //prepare the AJAX object
    var ajaxObject = GetXmlHttpObject();
    ajaxObject.onreadystatechange=function(){
      if(ajaxObject.readyState==4){
          $('div[aria-labelledby="ExternalSet"]').html(ajaxObject.responseText);
      }
    }

    //send request
    ajaxObject.open("GET",newUrl,true);
    ajaxObject.send(null);

    return true;
  }

  </script>
<!-- FIXME jQuery test stuff -->
<script type="text/javascript">
        function getURLParameter(name) {
            return decodeURI((RegExp(name + '=' + '(.+?)(&|$)').exec(
                location.search)||[,null])[1]);
        }

        $(function() {
            // set links with id
            var id = getURLParameter("id");

            $("#AllCompounds").attr("href",
                "/viewDatasetCompoundsSection" + "?id=" + id);
            $("#ExternalSet").attr("href",
                "/viewDatasetExternalCompoundsSection" + "?id=" + id);
            $("#ActivityHistogram").attr("href",
                "/viewDatasetActivityChartSection" + "?id=" + id);
            $("#Heatmap").attr("href",
                "/viewDatasetVisualizationSection" + "?id=" + id);
            $("#DescriptorWarnings").attr("href",
                "/viewDatasetDescriptorsSection" + "?id=" + id);

            // activate tabs widget
            $("#tabs").tabs();
        });
    </script>

</head>

<body onload="setTabToMyBench();">
  <div id="bodyDIV"></div>
  <!-- used for the "Please Wait..." box. Do not remove. -->
  <div class="outer">

    <div class="includesHeader"><%@include file="/jsp/main/header.jsp"%></div>
    <div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp"%></div>

    <span class="Errors"><b>
        <!-- errors go here..? -->
    </b></span> <span class="StandardTextDarkGray"></span> <span id="maincontent">
      <table width="924" align="center">
        <tr>
          <td>
            <div class="StandardTextDarkGray">
              <br /> <b>Dataset Name: </b>
              <s:property value="dataset.name" />
              <br /> <b>Number of Compounds: </b>
              <s:property value="dataset.numCompound" />
              <br /> <b>Dataset Type: </b>
              <s:property value="datasetTypeDisplay" />
              <br /> <b>Date Created: </b>
              <s:date name="dataset.createdTime" format="yyyy-MM-dd HH:mm" />
              <br />
              <s:if test="dataset.datasetType=='MODELING'||dataset.datasetType=='MODELINGWITHDESCRIPTORS'">
                <b>Number of External Compounds: </b>
                <s:property value="externalCompoundsCount" />
                <br />
              </s:if>
              <br />
            </div> <s:if test="editable=='YES'">
              <s:form action="updateDataset" enctype="multipart/form-data" theme="simple">
                <div class="StandardTextDarkGray">
                  <b>Description: </b>
                </div>
                <s:textarea id="datasetDescription" name="datasetDescription" align="left"
                  style="height: 50px; width: 50%" />
                </div>
                <br />
                <div class="StandardTextDarkGray">
                  <b>Paper Reference: </b>
                </div>
                <s:textarea id="datasetReference" name="datasetReference" align="left" style="height: 50px; width: 50%" />
                </div>
                <br />
                <input type="button" name="userAction" id="userAction" onclick="this.form.submit()" value="Save Changes" />
                <s:hidden id="objectId" name="objectId" />
              </s:form>
            </s:if> <s:else>
              <div class="StandardTextDarkGray">
                <br /> <b>Description: </b>
                <s:property value="dataset.description" />
                <br /> <b>Paper Reference: </b>
                <s:property value="dataset.paperReference" />
                <br />
                <s:if test="dataset.userName!='all-users'||user.isAdmin=='YES'">
                  <!-- display edit link -->
                  <a href="viewDataset?id=<s:property value="objectId" />&editable=YES">Edit description and
                    reference</a>
                  <br />
                </s:if>
                <br />
              </div>
            </s:else> <br /> <a href="jobs#datasets">Back to Datasets</a> <!-- End Header Info --> <!-- Page description --> <s:if
              test="dataset.datasetType=='PREDICTION'||dataset.datasetType=='PREDICTIONWITHDESCRIPTORS'">
              <p class="StandardTextDarkGray">The compounds in your dataset are below.</p>
            </s:if> <s:elseif test="dataset.datasetType=='MODELING'||dataset.datasetType=='MODELINGWITHDESCRIPTORS'">
              <p class="StandardTextDarkGray">The compounds in your dataset are below, with the activity values you
                supplied. The compounds of the external set are shown in the second tab.</p>
            </s:elseif> <s:if test="dataset.sdfFile.isEmpty()">
              <p class="StandardTextDarkGray">
                <b> No VISUALIZATION has been made as there was no SDF file provided for this particular DATASET. </b>
              </p>
            </s:if> <!-- End page description -->
          </td>
        </tr>
        <tr>
          <td><s:url id="datasetCompoundsLinkTwo" value="/viewDatasetCompoundsSection" includeParams="none">
              <s:param name="currentPageNumber" value='3' />
              <s:param name="orderBy" value='orderBy' />
              <s:param name="id" value='objectId' />
            </s:url> <s:url id="datasetCompoundsLink" value="/viewDatasetCompoundsSection" includeParams="none">
              <s:param name="currentPageNumber" value='currentPageNumber' />
              <s:param name="orderBy" value='orderBy' />
              <s:param name="id" value='objectId' />
            </s:url> <s:if test="dataset.datasetType!='MODELING'&&dataset.datasetType!='MODELINGWITHDESCRIPTORS'">
              <script>
      $(function() {
        $( "#tabs" ).tabs( { disabled: [2] } );
      });
    </script>
            </s:if> <!-- load tabs -->
            <div id="tabs">
              <ul>
                <li><a id="AllCompounds">All Compounds</a></li>
                <li><a id="ExternalSet">External Set</a></li>
                <li><a id="ActivityHistogram">Activity Histogram</a></li>
                <li><a id="Heatmap">Heatmap</a></li>
                <li><a id="DescriptorWarnings">Descriptor Warnings</a></li>
              </ul>
            </div> <!-- end load tabs --> </span>
            <div id="image_hint"
              style="display: none; border: #FFF solid 1px; width: 300px; height: 300px; position: absolute">
              <img src="" width="300" height="300" />
            </div>
      </table>
    <script>
    $(document).ready(function() {
        //adding a bigger compound image on mouse enter

        $('.compound_img_a').on("mouseover",function(e){
          $("img","#image_hint").attr("src", $("img", this).attr("src"));
            var position = $("img", this).offset();
            $("#image_hint").show();
            $("#image_hint").css({"left":position.left+155,"top":position.top-75});
          });

        $('.compound_img_a').on("mouseout",function(){
            $("#image_hint").hide();
        });
    });
    </script>
    <div class="includes"><%@include file="/jsp/main/footer.jsp"%></div>
  </div>
</body>
