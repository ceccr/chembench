<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>

<script src="javascript/script.js"></script>
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
<td align="center">

  <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0" width="924" height="924">
    <param name="movie" value="/visFlash/heatmap.swf" />
    <param name="quality" value="high" />
   <param name="FlashVars" value="web-addr=<s:property value="webAddress" />/&dataset=<s:property value='dataset.fileName' />&ncom=<s:property value='dataset.numCompound' />&type_=<s:property value='dataset.modelType' />&creation_date='12/3/2010'&desc=<s:property value='dataset.description' />&actFile=<s:property value='dataset.actFile' />&sdfFile=<s:property value='dataset.sdfFile' />&user=<s:property value='dataset.userName' />" />
   <embed src="/visFlash/heatmap.swf" width="924" height="924" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" flashvars="web-addr=<s:property value="webAddress" />/&dataset=<s:property value='dataset.fileName' />&ncom=<s:property value='dataset.numCompound' />&type_=<s:property value='dataset.modelType' />&creation_date='12/3/2010'&desc=<s:property value='dataset.description' />&actFile=<s:property value='dataset.actFile' />&sdfFile=<s:property value='dataset.sdfFile' />&user=<s:property value='dataset.userName' />">
  </embed>
  </object>

</td>
</tr>
</table>
