/*
 * JTip | Tooltips for image maps
 * Cody Lindley (http://www.codylindley.com) original author
 * Jason Guo (http://www.xing.net.au) rewritten by
 * Under an Attribution, Share Alike License
 * JTip is built on top of the very light weight jquery library.
 */

$(document).ready(function(){
	var toolTipActive = false;
	$("area.jTip").hover(
		function() {
			var offsetX = 10;
			var offsetY = 0;
			var areaCoords = this.coords.split(',');
			var mapPosition = $('img#mapImage').offset();			
			var tipTop = mapPosition.top + (areaCoords[1] * 1) + offsetY;;
			var tipLeft = mapPosition.left + (areaCoords[2] * 1) + offsetX;
			if (!toolTipActive)
				JT_show(this.href,this.id,this.alt,tipLeft,tipTop);
			toolTipActive = true;
		}, 
		function() {			
			JT_destroy();
			toolTipActive =false;
		}
	);

}); 
 
function JT_destroy(){
	$('div#JT').remove();
}

function JT_show(url,linkId,title,posX,posY){
	if(title == false)title="&nbsp;";
	var de = document.documentElement;
	var w = self.innerWidth || (de&&de.clientWidth) || document.body.clientWidth;
	var hasArea = w - getAbsoluteLeft(linkId);
	var clickElementy = posY; //set y position/
	var queryString = url.replace(/^[^\?]+\??/,'');/
	var params = parseQuery( queryString );/
	if(params['width'] === undefined){params['width'] = 250};/
	if(params['link'] !== undefined){/
	$('#' + linkId).bind('click',function(){window.location = params['link']});/
	$('#' + linkId).css('cursor','pointer');/
	}

	if(hasArea>((params['width']*1)+75)){/
		$("body").append("<div id='JT' style='width:"+params['width']*1+"px'><div id='JT_arrow_left'></div><div id='JT_close_left'>"+title+"</div><div id='JT_copy'><div class='JT_loader'><div></div></div>");//right side
		var clickElementx = posX; //set x position
	}else{
		$("body").append("<div id='JT' style='width:"+params['width']*1+"px'><div id='JT_arrow_right' style='left:"+((params['width']*1)+1)+"px'></div><div id='JT_close_right'>"+title+"</div><div id='JT_copy'><div class='JT_loader'><div></div></div>");//left side
		var clickElementx = getAbsoluteLeft(linkId) - ((params['width']*1) + 15); //set x position
	}
	$('#JT').css({left: clickElementx+"px", top: clickElementy+"px"});
	$('#JT_copy').load(url);
	$('#JT').show();
}

function getElementWidth(objectId) {
	x = document.getElementById(objectId);
	return x.offsetWidth;
}

function getAbsoluteLeft(objectId) {
	o = document.getElementById(objectId)
	oLeft = o.offsetLeft // Get left position from the parent object
	return oLeft
}

function parseQuery ( query ) {
   var Params = new Object ();
   if ( ! query ) return Params; // return empty object
  var Pairs = query.split(/[;&]/);
  for ( var i = 0; i < Pairs.length; i++ ) {
      var KeyVal = Pairs[i].split('=');
     if ( ! KeyVal || KeyVal.length != 2 ) continue;
     var key = unescape( KeyVal[0] );
     var val = unescape( KeyVal[1] );
      val = val.replace(/\+/g, ' ');
     Params[key] = val;
   }
   return Params;
}
