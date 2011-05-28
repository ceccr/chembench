/* Contains several functions that are used by the Dataset, Modeling, Prediction, and Jobs pages
 * Put functions here only if they apply to multiple pages.
 * Functions that are used by, e.g., only the modeling page would go in modeling.js, not here.
 */

function setTabToHome(){
	document.getElementById("homeButton").innerHTML = "<img src='/theme/navbar/button-home-grey.jpg' border='0' />";
} 
function setTabToMyBench(){
	document.getElementById("myBenchButton").innerHTML = "<img src='/theme/navbar/button-mybench-grey.jpg' border='0' />";
} 
function setTabToDataset(){
	document.getElementById("datasetButton").innerHTML = "<img src='/theme/navbar/button-dataset-grey.jpg' border='0' />";
} 
function setTabToModeling(){
	document.getElementById("modelingButton").innerHTML = "<img src='/theme/navbar/button-modeling-grey.jpg' border='0' />";
} 
function setTabToPrediction(){
	document.getElementById("predictionButton").innerHTML = "<img src='/theme/navbar/button-prediction-grey.jpg' border='0' />";
} 
function setTabToCeccrBase(){
	document.getElementById("ceccrBaseButton").innerHTML = "<img src='/theme/navbar/button-ceccrbase-grey.jpg' border='0' />";
} 

function enlargeImage(me){
	me.height *= 2;
}
function shrinkImage(me){
	me.height /= 2;
}
	
function confirmDelete(objectType){
	var x=window.confirm("Are you sure you want to delete this " + objectType + "?");
	if (x)
		return true;
	else
		return false;
}

function validateObjectNames(name, usedDatasetNames, usedPredictorNames, usedPredictionNames, usedTaskNames){
	 // Check for white space; replace with underscores if it's there.
	 name = name.replace(/ /g,"_");

	 linuxChars = new RegExp("(\\\\|\"|'|,|`|:|\\?|\\*|~|!|@|#|%|\\$|\\^|\\+|;|\\[|\\])");
     if (linuxChars.test(name)) {
		alert("You cannot use special characters (quotes, asterisks, punctuation, slashes, etc.) in your job name. You might want to replace them with underscores or dashes.");
		return false;
     }
	 
     if(name.length > 250){
 		alert("The name you have entered is too long. Please choose another.");
 		return false;
 	}

     if(name.length == 0){
  		alert("Please enter a name for this job.");
  		return false;
  	}

    if(name[0] == '.' || name[0] == '~' || name[0] == '?'){
  		alert("Your job name should start with a number or letter.");
  		return false;
  	}
 	
	for (var i=0; i < usedDatasetNames.length - 1; i++){
		if(name == usedDatasetNames[i]){
			alert("You already have a Dataset named " + usedDatasetNames[i] + ". Please choose a different name.");
			return false;
		}
	}
	
	for (var i=0; i < usedPredictorNames.length - 1; i++){
		if(name == usedPredictorNames[i]){
			alert("You already have a Predictor named " + usedPredictorNames[i] + ". Please choose a different name.");
			return false;
		}
	}
	
	for (var i=0; i < usedPredictionNames.length - 1; i++){
		if(name == usedPredictionNames[i]){
			alert("You already have a Prediction named " + usedPredictionNames[i] + ". Please choose a different name.");
			return false;
		}
	}
	
	for (var i=0; i < usedTaskNames.length - 1; i++){
		if(name == usedTaskNames[i]){
			alert("You already have a running job named " + usedTaskNames[i] + ". Please choose a different name.");
			return false;
		}
	}
	
	return true;
}


function submitForm(button, textarea)
{
	if(!validate(button.form))	
	{return false;}
	else{
	button.disabled=true;
	button.form.submit();
	textarea.innerHTML="Your workflow is being submitted, please wait";
	return true;
	}
}

function createCriteria(table, num)
{
	var i = table.rows.length
	var row = table.insertRow(i)
	
	var cell = row.insertCell(0);
	cell.innerHTML = "<select><option value=\"RSquared\">r&sup2;</option>" +
			"<option value=\"QSquared\">q&sup2;</option>" +
			"</select>";
	cell = row.insertCell(1);
	cell.innerHTML = "<select><option value=\">\">&gt;</option>" +
			"<option value=\"<\">&lt;</option>" +
			"</select>";
	cell = row.insertCell(2);
	cell.innerHTML = "<input type=\"text\"/>";
	cell = row.insertCell(0);
	cell.innerHTML = "<input type=\"checkbox\" name=\"checkbox\" id=\""+num+"\"></input>";
}

function deleteCriteria(table)
{
	var i = 0;
	var row = table.rows;
	for(i=row.length-1;i>=0;i--) {
		
		var input = row[i].cells[0].childNodes[0];
		if(input.checked){
			table.deleteRow(i);
		}
	}
}


function ElementRow(els) {
	if(els==null)
		this.els=[];
	else
		this.els = els;
} 
ElementRow.prototype.draw = function(pos) {
	var html_frag=[];
	for(var i in this.els) {
		if(this.els[i] instanceof ElementWrapper)
			html_frag[i]=this.els[i].draw();
		else
			html_frag[i]=new ElementWrapper(this.els[i]).draw();	
	}
	return createTableRow(pos, html_frag);
}
ElementRow.prototype.createToolTip = function() {
	for(var i in this.els) {
		var e = this.els[i];
		if(e instanceof ElementWrapper)
			e.createToolTip();
		else
			new ElementWrapper(e).createToolTip();	
	}
}
function ElementWrapper(data) {
	this.tag = data.tag;
	this.html = data.html;
	this.data = data.attributes;
}
ElementWrapper.prototype.draw = function() {
	if(this.tag==null)
		return this.html;
	var html = "<"+this.tag+" ";
	for(var i in this.data)
	{
		if(i!="html")
			html+=i+"=\""+this.data[i]+"\" ";
	}
	if(this.html==null)
		html+="/>";
	else
		html+=">"+this.html+"</"+this.tag+">";
		return html;
}
ElementWrapper.prototype.createToolTip = function() {
		if(this.data.id!=null && this.data.toolTip!=null) {
			createToolTip(this.data.id, this.data.toolTip);
		}
}
function createTextElement(label_text, lid, indent, input_name, id, value, toolTip) {
	var str = new ElementRow([{tag:"span",attributes:{id:lid,toolTip:toolTip},html:label_text},{tag:"input", attributes:{type:"text", id:id, name:input_name,value:value}}]);
	return [str,indent];
}
function createFileElement(label_text, lid, input_name, id, toolTip,cl) {
	var str = new ElementRow([{tag:"span",attributes:{id:lid, toolTip:toolTip},html:label_text},{tag:"input", attributes:{type:"file", id:id, name:input_name,onchange:cl}}]);
	return str;
}
function createButtonElement(label_text, lid, input_name, id, value, eh, toolTip) {
	var str = new ElementRow([{tag:"span",attributes:{id:lid, toolTip:toolTip},html:label_text},{tag:"input", attributes:{type:"button", id:id, name:input_name,value:value, onclick:eh}}]);
	return str;
}
function createSubmitElement(value) {
	var str = new ElementRow([{tag:"span",attributes:{toolTip:toolTip},html:label_text},{tag:"input", attributes:{type:"submit",name:"userAction",value:value}}]);
	return str;
}

function createSubmitElement1(label_text, tooltip,value,eh) {
	var str = new ElementRow([{tag:"span",attributes:{toolTip:tooltip},html:label_text},{tag:"input", attributes:{type:"submit",name:"v2button",value:value,onclick:eh}}]);
	return str;
}

function createCheckboxElement(label_text, lid, toolTip, data) {
	data.tag="input";
	data.attributes.type="checkbox";
	var str = new ElementRow([{tag:"span",attributes:{id:lid,toolTip:toolTip},html:label_text},data]);
	return str;
}
function createRadiobuttonElement(label_text, lid, indent, toolTip, data) {
data.tag="input";
	data.attributes.type="radio";
	var str = new ElementRow([{tag:"span",attributes:{id:lid,toolTip:toolTip},html:label_text},data]);
	return [str,indent];
}
function drawElement(tag, data, html) {
	var str = new ElementWrapper({tag:tag, attributes:data, html:html});
	return str.draw();
}
function createTableData(html_frag, attributes) {
			return new ElementWrapper({tag:"td",data:attributes, html:html_frag}).draw();
}
function createTableRow(pos, html_frag) {
	var str = "<tr>";
	for(var i=0;i<pos;i++) {
		str += "<td></td>";
	}
	if(html_frag instanceof Array) {
		for(var i=0;i<html_frag.length;i++) {
			var hf = html_frag[i];
			str += createTableData(hf);
		}
	} else {
			str += createTableData(html_frag);
	}
	str+="</tr>";
	return str;
}
function drawTable(table,rows) {
	var html="";
	for(i in rows) {
		if(rows[i] instanceof Array)
			html+=rows[i][0].draw(rows[i][1]);
		else
			html+=rows[i].draw(0);
	}
	var elem=new ElementWrapper({tag:"table", attributes:table, html:html});
	return elem.draw();
}
function createListElement1(label_text, lid, indent, options, values, data,toolTip) {
	var str="";
	for(var i = 0;i<options.length;i++) {
	if(i==1)
	{
	str+=drawElement("option",{value:values[i],disabled:true,onmouseover: "javascript: this.disabled=true",onmouseout:" javascript: this.disabled=false"},options[i]);
	}else{
	str+=drawElement("option",{value:values[i]},options[i]);
	}
		
	}
	data.tag="select";
	data.html=str;
	return [new ElementRow([{tag:"span",attributes:{id:lid,toolTip:toolTip},html:label_text},data
	]),indent];
}

function createListElement(label_text, lid, indent, options, values, data,toolTip) {
	var str="";
	for(var i = 0;i<options.length;i++) {
	str+=drawElement("option",{value:values[i]},options[i]);
	
	}
	data.tag="select";
	data.html=str;
	return [new ElementRow([{tag:"span",attributes:{id:lid,toolTip:toolTip},html:label_text},data
	]),indent];
}

function resetErrorMessages() {
	document.getElementById("messageDiv").innerHTML = "";
}

function logout() {
	self.location = "logout";
}
   
function showLoading(text){
	//var shaded_ = "<div id='shade' style='background:url(/theme/img/SHADE_BG.png );z-index:30; position:absolute; top:250px; width:100%;height:300%;'><div align='center' style='background:url(/theme/ccbTheme/images/ccbLogoWorking.png ); width:750px; height:200px; margin: 0 auto;'> <div style='position:absolute; top:100px; right:270px; width:350px;'><div align='left'><strong class='StandardTextDarkGrayParagraph'>"+text+"</strong></div></div></div> ";
	if(text.length > 100){
		text=text.substring(0,97)+"...";
	}
	var shaded_ ="" +
			"<div id='shade' style='background:url(/theme/img/SHADE_BG.png );z-index:5000; position:absolute; top:0px;  width:100%;height:300%;'>" +
			"<div style='width:750px; height:200px;  margin: 0 auto;position: fixed;top:30%;left:20%;z-index: 100; '> " +
				"<table cellpadding='0' cellspacing='0'; border='0' background='/theme/ccbTheme/images/ccbLogoWorking.png' style='height:200px;width:750px'>" +
					"<tr>" +
						"<td style='width:350px'></td>" +
						"<td style='width:50px'>" +
							"<div align='center'><img src='/theme/img/shade-loader.gif' /></div></td>" +
						"<td style='height:100px; width:250px'><strong class='StandardTextDarkGrayParagraphBlue'>"+text+"</strong></td>" +
						"<td style='width:50px'></td>" +
					"</tr>" +
				"</table>" +
			"</div>" +
			"</div>";
	document.getElementById('bodyDIV').innerHTML = shaded_;
}

function hideLoading(){
 var d = document.getElementById('bodyDIV');
  var olddiv = document.getElementById("shade");
  d.removeChild(olddiv);
}


function GetXmlHttpObject() { 
	var objXMLHttp=null;
	if (window.XMLHttpRequest) {
		objXMLHttp=new XMLHttpRequest();
	}
	else if (window.ActiveXObject) {
		objXMLHttp=new ActiveXObject("Microsoft.XMLHTTP");
	}
	return objXMLHttp;
} 