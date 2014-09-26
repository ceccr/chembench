<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" import="java.util.*"%>

<script src="javascript/chembench.js"></script>
<script src="javascript/AC_RunActiveContent.js"></script>
<script src="javascript/hookMouseWheel.js"></script>
<script type="text/javascript">

function show_vis_panel()
{
  if(document.getElementById("vis_panel").style.display=='inline')
{
	  document.getElementById("vis_panel").style.display='none';
	  document.getElementById("panel_link").innerHTML = "Show visualization control panel";
}
else{
	document.getElementById("panel_link").innerHTML = "Hide visualization control panel";
	document.getElementById("vis_panel").style.display='inline';
	}

}

if(!(document.attachEvent)) {
    window.addEventListener("DOMMouseScroll", handleWheel, false);
}
function handleWheel(event) {
    var app = window.document["${application}"];
    if (app) {
        var o = {x: event.screenX, y: event.screenY, 
            delta: event.detail,
            ctrlKey: event.ctrlKey, altKey: event.altKey, 
            shiftKey: event.shiftKey}
        
        app.handleWheel(o);
    }
}

</script>

<table width="924px" align="center" border="0">
  <tr>
    <td align="left"><br />
      <p class="StandardTextDarkGray">
        <b><u>Heatmap Application</u></b>
      </p>
      </b>
      <p class="StandardTextDarkGray">The Heatmap tool is useful for visualizing similarity between all pairs of
        compounds in your dataset. MACCS keys are generated and compared to produce the heatmap. The similarity is based
        on Tanimoto similarity or Mahalanobis distance; you can select either using the buttons on the Heatmap.</p>
      <p class="StandardTextDarkGray">You can zoom in and out of the heatmap using the mouse wheel. Click and drag
        the top of a column to shift it left or right. You may also drag rows up and down. To reset the rows and columns
        to their original configuration, use the "Reset" button.</p>
      <p class="StandardTextDarkGray">The "Keep Diagonal" button will shift rows and columns at the same time. Just
        below "Keep Diagonal" are four arrow buttons. These four arrows sort the rows / columns based on the activity
        values of the compounds. You can see the activity of each compound by hovering the mouse over a compound ID. The
        activity of each compound is also represented by the blue shading under each compound; darker blues indicate
        lower activity values.</p>
      <p class="StandardTextDarkGray">Note: For very large datasets (500 or more compounds), the heatmap generation
        step will be skipped.</p> <s:if test="dataset.hasVisualization==0">
        <p class="StandardTextDarkGray">
          <b> No Mahalanobis distance measure heatmap has been generated. You can generate Mahalanobis distance
            measure heatmap by clicking the button below. </b>
        </p>
      </s:if> <s:if test="dataset.hasVisualization==0">
        <s:form action="generateMahalanobis" theme="simple">
          <s:hidden id="objectId" name="objectId" />
          <input type="button" value="Generate Mahalanobis heatmap" name="userAction" id="userAction"
            onclick="showLoading('CALCULATING MAHALANOBIS DISTANCE MEASURE. PLEASE WAIT.');this.form.submit()" />
        </s:form>
      </s:if> <br /> <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
        codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0" width="924"
        height="924">
        <param name="movie" value="/visFlash/heatmap.swf" />
        <param name="quality" value="high" />
        <param name="FlashVars"
          value="web-addr=<s:property value="webAddress" />/&dataset=<s:property value='dataset.name' />&ncom=<s:property value='dataset.numCompound' />&type_=<s:property value='dataset.modelType' />&creation_date=<s:property value='dataset.createdTime' />&desc=<s:property value='dataset.description' />&actFile=<s:property value='dataset.actFile' />&sdfFile=<s:property value='dataset.sdfFile' />&user=<s:property value='dataset.userName' />" />
        <embed src="/visFlash/heatmap.swf" width="924" height="924" quality="high"
          pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash"
          type="application/x-shockwave-flash"
          flashvars="web-addr=<s:property value="webAddress" />/&dataset=<s:property value='dataset.name' />&ncom=<s:property value='dataset.numCompound' />&type_=<s:property value='dataset.modelType' />&creation_date=<s:property value='dataset.createdTime' />&desc=<s:property value='dataset.description' />&actFile=<s:property value='dataset.actFile' />&sdfFile=<s:property value='dataset.sdfFile' />&user=<s:property value='dataset.userName' />">
        </embed>
      </object></td>
  </tr>
</table>
