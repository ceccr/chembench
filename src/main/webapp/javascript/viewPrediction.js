function loadPredictionValuesTab(newUrl) {
    // When the user changes which page they're on in the Prediction Values tab
    // or changes the sorted element, run this function to update the tab's
    // content

    // prepare the AJAX object
    var ajaxObject = GetXmlHttpObject();
    ajaxObject.onreadystatechange = function() {
        if (ajaxObject.readyState == 4) {
            hideLoading();
            document.getElementById("predictionValuesDiv").innerHTML = ajaxObject.responseText;
        }
    };
    showLoading("LOADING. PLEASE WAIT.");

    // send request
    ajaxObject.open("GET", newUrl, true);
    ajaxObject.send(null);

    return true;
}

$(document).ready(function() {
    // adding a bigger compound image on mouse enter
    $('.compound_img_a').mouseover(function() {
        $("img", "#image_hint").attr("src", $("img", this).attr("src"));
        var position = $("img", this).offset();
        $("#image_hint").show();
        $("#image_hint").css({
            "left" : position.left + 155,
            "top" : position.top - 75
        });
    });

    $('.compound_img_a').mouseout(function() {
        $("#image_hint").hide();
    });
});
