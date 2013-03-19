<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page language="java" import="java.util.*"%>

<html>
<head>
    <sx:head debug="false" cache="false" compressed="true" />
    <title>CHEMBENCH | Select Predictors</title>
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
    <script language="JavaScript"
            src="javascript/sortableTable.js"></script>
</head>
<body onload="setTabToPrediction();">
<div class="outer">
<div class="includesHeader"><%@include file="/jsp/main/header.jsp" %></div>
<div class="includesNavbar"><%@include file="/jsp/main/centralNavigationBar.jsp" %></div>

<div
        class="StandardTextDarkGrayParagraph predictionBackground benchAlign">
    <div class="homeLeft">
        <br /> <br />
        <p style="margin-left:20px">
            <b>Chembench Predictor Selection</b> <br /> <br /> Here you may
            use predictors to identify computational hits in external compound
            libraries. Predictors generated and validated by UNC's Molecular
            Modeling Laboratory are available under <b>Drug Discovery
            Predictors</b>, <b>ADME Predictors</b>, and <b>Toxicity Predictors</b>.
            Predictors you create using the MODELING tab appear under <b>Private
            Predictors</b>. <br /> <br />For more information about making
            predictions, see the <a href="/help-prediction">Prediction help
            page</a>. <br /> <br />Click the checkboxes to the left of each
            predictor you want to use, and hit the "Select Predictors" button.
            Then you may predict the activity of a dataset, a SMILES string, or
            a molecule sketch. <br /> <br /> If you wish to share predictors
            you have developed with the Chembench community, please contact us
            at <a href="mailto:ceccr@email.unc.edu">ceccr@email.unc.edu</a>.
        </p>
    </div>
</div>

<s:form theme="simple" action="selectPredictor"
        enctype="multipart/form-data" method="post">

    <s:if test="user.showPublicPredictors!='NONE'">
        <div class="border benchAlign bottomMargin">
            <p class="StandardTextDarkGrayParagraph">
                <b>Drug Discovery Predictors</b>
            </p>
            <p align="justify" class="StandardTextDarkGrayParagraph">These
                are public predictors useful for virtual screening. * - predictor
                based on the uploaded dataset</p>
            <table width="100%" class="sortable" id="drugdisc">
                <tr>
                    <th class="TableRowText01narrow_unsortable">Select</th>
                    <th class="TableRowText01narrow">Name</th>
                    <th class="TableRowText01narrow">Date Created</th>
                    <th class="TableRowText01narrow">Modeling Method</th>
                    <th class="TableRowText01narrow">Descriptor Type</th>
                    <th class="TableRowText01narrow_unsortable" colspan="2">Description</th>
                </tr>
                <s:iterator value="userPredictors">
                    <s:if test="predictorType=='DrugDiscovery'">
                        <tr>
                            <td class="TableRowText02narrow"><s:checkbox
                                    name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                            <td class="TableRowText02narrow"><s:property value="name" /></td>
                            <td class="TableRowText02narrow"><s:date
                                    name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                            <td class="TableRowText02narrow"><s:property
                                    value="modelMethod" /></td>
                            <s:if test="descriptorGeneration=='UPLOADED'">
                                <td class="TableRowText02">*<s:property
                                        value="uploadedDescriptorType" /></td>
                            </s:if>
                            <s:else>
                                <td class="TableRowText02"><s:property
                                        value="descriptorGeneration" /></td>
                            </s:else>
                            <td class="TableRowText02narrow" colspan="2"><s:property
                                    value="description" /></td>
                        </tr>
                    </s:if>
                </s:iterator>

            </table>
            <br /> <br />
        </div>
        <div class="border benchAlign bottomMarginAdme">
            <p class="StandardTextDarkGrayParagraph">
                <b>ADME Predictors</b>
            </p>
            <p align="justify" class="StandardTextDarkGrayParagraph">These
                are public predictors useful for prediction of absorption,
                distribution, metabolism, and excretion properties. * - predictor
                based on the uploaded dataset</p>
            <table width="100%" class="sortable" id="adme">
                <tr>
                    <th class="TableRowText01narrow_unsortable">Select</th>
                    <th class="TableRowText01narrow">Name</th>
                    <th class="TableRowText01narrow">Date Created</th>
                    <th class="TableRowText01narrow">Modeling Method</th>
                    <th class="TableRowText01narrow">Descriptor Type</th>
                    <th class="TableRowText01narrow_unsortable" colspan="2">Description</th>
                </tr>
                <s:iterator value="userPredictors">
                    <s:if test="predictorType=='ADME'">
                        <tr>
                            <td class="TableRowText02narrow"><s:checkbox
                                    name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                            <td class="TableRowText02narrow"><s:property value="name" /></td>
                            <td class="TableRowText02narrow"><s:date
                                    name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                            <td class="TableRowText02narrow"><s:property
                                    value="modelMethod" /></td>
                            <s:if test="descriptorGeneration=='UPLOADED'">
                                <td class="TableRowText02">*<s:property
                                        value="uploadedDescriptorType" /></td>
                            </s:if>
                            <s:else>
                                <td class="TableRowText02"><s:property
                                        value="descriptorGeneration" /></td>
                            </s:else>
                            <td class="TableRowText02narrow" colspan="2"><s:property
                                    value="description" /></td>
                        </tr>
                    </s:if>
                </s:iterator>
            </table>
        </div>
        <br />
        <br />

        <div class="border benchAlign bottomMargin">
            <p class="StandardTextDarkGrayParagraph">
                <b>Toxicity Predictors</b>
            </p>
            <p align="justify" class="StandardTextDarkGrayParagraph">These
                are public predictors useful for toxicity prediction. * -
                predictor based on the uploaded dataset</p>
            <table width="100%" class="sortable" id="toxicity">
                <tr>
                    <th class="TableRowText01narrow_unsortable">Select</th>
                    <th class="TableRowText01narrow">Name</th>
                    <th class="TableRowText01narrow">Date Created</th>
                    <th class="TableRowText01narrow">Modeling Method</th>
                    <th class="TableRowText01narrow">Descriptor Type</th>
                    <th class="TableRowText01narrow_unsortable" colspan="2">Description</th>
                </tr>
                <s:iterator value="userPredictors">
                    <s:if test="predictorType=='Toxicity'">
                        <tr>
                            <td class="TableRowText02narrow"><s:checkbox
                                    name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                            <td class="TableRowText02narrow"><s:property value="name" /></td>
                            <td class="TableRowText02narrow"><s:date
                                    name="dateCreated" format="yyyy-MM-dd HH:mm" /></td>
                            <td class="TableRowText02narrow"><s:property
                                    value="modelMethod" /></td>
                            <s:if test="descriptorGeneration=='UPLOADED'">
                                <td class="TableRowText02">*<s:property
                                        value="uploadedDescriptorType" /></td>
                            </s:if>
                            <s:else>
                                <td class="TableRowText02"><s:property
                                        value="descriptorGeneration" /></td>
                            </s:else>
                            <td class="TableRowText02narrow" colspan="2"><s:property
                                    value="description" /></td>
                        </tr>
                    </s:if>
                </s:iterator>
            </table>
            <br /> <br />
        </div>
    </s:if>
    <div class="border benchAlign bottomMargin">
        <p class="StandardTextDarkGrayParagraph">
            <b>Private Predictors</b>
        </p>
        <p align="justify" class="StandardTextDarkGrayParagraph">These
            are private predictors you have created. Other users cannot access
            them. * - predictor based on the uploaded dataset</p>
        <table width="100%" class="sortable" id="private">
            <tr>
                <th class="TableRowText01narrow_unsortable">Select</th>
                <th class="TableRowText01narrow">Name</th>
                <th class="TableRowText01narrow">Date Created</th>
                <th class="TableRowText01narrow">Modeling Method</th>
                <th class="TableRowText01narrow">Descriptor Type</th>
                <th class="TableRowText01">Dataset</th>
            </tr>
            <s:iterator value="userPredictors">
                <s:if test="predictorType=='Private'">
                    <tr>
                        <td class="TableRowText02"><s:checkbox
                                name="predictorCheckBoxes" fieldValue="%{id}" /></td>
                        <td class="TableRowText02"><s:property value="name" /></td>
                        <td class="TableRowText02"><s:date name="dateCreated"
                                                           format="yyyy-MM-dd HH:mm" /></td>
                        <td class="TableRowText02"><s:property value="modelMethod" /></td>
                        <s:if test="descriptorGeneration=='UPLOADED'">
                            <td class="TableRowText02">*<s:property
                                    value="uploadedDescriptorType" /></td>
                        </s:if>
                        <s:else>
                            <td class="TableRowText02"><s:property
                                    value="descriptorGeneration" /></td>
                        </s:else>
                        <td class="TableRowText02"><s:property
                                value="datasetDisplay" /></td>
                    </tr>
                </s:if>
            </s:iterator>
        </table>
    </div>
    <br />

    <br />
    <div class="border benchAlign Choosepredictors" style="margin-top:-20px">
        <p class="StandardTextDarkGrayParagraph">
            <b>Choose Predictors</b>
        </p>
        <p align="justify" class="StandardTextDarkGrayParagraph">When
            you have checked the boxes next to the predictors you want to use,
            click on the button below.</p>
        <table>
            <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;<s:submit
                        value="Select Predictors" />
                </td>
            </tr>
        </table>
    </div>
</s:form>
<br />
<div class="includes"><%@include file ="/jsp/main/footer.jsp" %></div>
</div>
</body>
</html>