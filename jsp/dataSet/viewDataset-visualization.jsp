<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %> 
<%@ page language="java" import="java.util.*" %>


<%DataSet ds = (DataSet)session.getAttribute("ds"); %>

<table width="924px" align="center" border="0">
<tr align="center" id="download" style="display:none;">
<td align="center">
</td>
</tr>
<tr>
<td align="center">
<script type="text/javascript">
AC_FL_RunContent( 'codebase','http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0','width','924','height','924','src','/visFlash/heatmap','quality','high','pluginspage','http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash','flashvars','web-addr=<%=Constants.WEBADDRESS%>/&dataset=<%=ds.getFileName()%>&ncom=<%=ds.getNumCompound()%>&type_=<%=ds.getModelType()%>&creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>&desc=<%=ds.getDescription()%>&actFile=<%=ds.getActFile()%>&sdfFile=<%=ds.getSdfFile()%>&user=<%=ds.getUserName()%>','movie','/visFlash/heatmap' ); //end AC code
</script>

  <noscript>
  <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0" width="924" height="924">
    <param name="movie" value="/visFlash/heatmap.swf" />
    <param name="quality" value="high" />
    <param name="FlashVars" value="web-addr=<%=Constants.WEBADDRESS%>/&dataset=<%=ds.getFileName()%>&ncom=<%=ds.getNumCompound()%>&type_=<%=ds.getModelType()%>&creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>&desc=<%=ds.getDescription()%>&actFile=<%=ds.getActFile()%>&sdfFile=<%=ds.getSdfFile()%>&user=<%=ds.getUserName()%>" />
    <embed src="/visFlash/heatmap.swf" width="924" height="924" quality="high" pluginspage="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" flashvars="web-addr=<%=Constants.WEBADDRESS%>/&dataset=<%=ds.getFileName()%>&ncom=<%=ds.getNumCompound()%>&type_=<%=ds.getModelType()%>&creation_date=<%=ds.getCreatedTime().toString().substring(0,ds.getCreatedTime().toString().indexOf(" "))%>&desc=<%=ds.getDescription()%>&actFile=<%=ds.getActFile()%>&sdfFile=<%=ds.getSdfFile()%>&user=<%=ds.getUserName()%>">
    </embed>
  </object>
  </noscript>  

</td>
</tr>
</table>
