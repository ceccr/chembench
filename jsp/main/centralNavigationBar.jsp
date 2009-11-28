
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>CCB Central Navigation</title>

<script language="javascript">
 
function setActiveButton(){
	alert("hi");
	alert(document.location.href);
    alert(document.location);
    aObj[i].className='active';
        
    //check if it's the Home button that should be lit
 	var homePageNames = new Array("home.do");
 	for(i=0; i < homePageNames.length; i++) {
	 	if(document.location.href.indexOf(homePageNames[i])>=0){
	 		document.getElementById("homeButton").innerHTML = "<img src='/theme/navbar/button-home-blue.jpg' />"
	 	}
	}
    
    //check if it's the MyBench button that should be lit
 	var myBenchPageNames = new Array("jobs");
 	for(i=0; i < myBenchPageNames.length; i++) {
	 	if(document.location.href.indexOf(myBenchPageNames[i])>=0){
	 		document.getElementById("myBenchButton").innerHTML = "<img src='/theme/navbar/button-home-blue.jpg' />"
	 	}
	}
 	
    //check if it's the Dataset button that should be lit
    var datasetPageNames = new Array("dataset");
 	for(i=0; i < datasetPageNames.length; i++) {
	 	if(document.location.href.indexOf(datasetPageNames[i])>=0){
	 		document.getElementById("datasetButton").innerHTML = "<img src='/theme/navbar/button-home-blue.jpg' />"
	 	}
	}
    
    //check if it's the Modeling button that should be lit
    var modelingPageNames = new Array("modeling");
 	for(i=0; i < modelingPageNames.length; i++) {
	 	if(document.location.href.indexOf(modelingPageNames[i])>=0){
	 		document.getElementById("modelingButton").innerHTML = "<img src='/theme/navbar/button-home-blue.jpg' />"
	 	}
	}
    
    //check if it's the Prediction button that should be lit
    var predictionPageNames = new Array("prediction", "selectPredictors");
 	for(i=0; i < predictionPageNames.length; i++) {
	 	if(document.location.href.indexOf(predictionPageNames[i])>=0){
	 		document.getElementById("predictionButton").innerHTML = "<img src='/theme/navbar/button-home-blue.jpg' />"
	 	}
	}
    
    //check if it's the CeccrBase button that should be lit
    var ceccrBasePageNames = new Array("ceccrbase");
 	for(i=0; i < ceccrBasePageNames.length; i++) {
	 	if(document.location.href.indexOf(ceccrBasePageNames[i])>=0){
	 		document.getElementById("ceccrBaseButton").innerHTML = "<img src='/theme/navbar/button-home-blue.jpg' />"
	 	}
	}
    
}

setActiveButton();
	
</script>
</head>

<body onload="setActiveButton();">
<table width="924" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
  <td valign="top"><a href="home.do"><div id="homeButton"><img src="/theme/navbar/button-home-grey.jpg" border="0" /></div></a></p></td>
  <td valign="top"><a href="jobs"><div id="myBenchButton"><img src="/theme/navbar/button-mybench-grey.jpg" border="0" /></div></a></td>
  <td valign="top"><a href="dataset"><div id="datasetButton"><img src="/theme/navbar/button-dataset-grey.jpg" border="0" /></div></a></td>
  <td valign="top"><a href="modeling"><div id="modelingButton"><img src="/theme/navbar/button-modeling-grey.jpg" border="0" /></div></a></td>
  <td valign="top"><a href="prediction"><div id="predictionButton"><img src="/theme/navbar/button-prediction-grey.jpg" border="0" /></div></a></td>
  <td valign="top"><a href="vpubchem.do"><div id="ceccrBaseButton"><img src="/theme/navbar/button-ceccrbase-grey.jpg" border="0" /></div></a></td>
  </tr>
</table>

</body>
</html>



