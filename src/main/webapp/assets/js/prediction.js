function jsmeOnLoad() {
    var jsmeApplet = new JSApplet.JSME("jsme-container", "380px", "300px");
    document.JME = jsmeApplet;
}

$(document).ready(function() {
    $(".nav-tabs a").on("shown.bs.tab", function(e) {
        $(e.target.hash).find("table.dataTable").DataTable().columns.adjust();
    });

    $("#prediction-model-selection").find(".dataTables_scrollBody").find("table").DataTable().on("draw", function() {
        $(this).find("tr").click(function() {
            var row = $(this);
            var modelName = row.find("td").first().find(".object-name").text();
            var counter = $("#selected-model-count");
            var count = parseInt(counter.text());
            row.toggleClass("selected").toggleClass("info");
            if (row.hasClass("selected")) {
                counter.text(++count);
                $("#model-list").append("<li>" + modelName + "</li>")
            } else {
                counter.text(--count);
                $("#model-list").find('li:contains("' + modelName + '")').remove();
            }

            if (count === 0) {
                $("#minimum-model-warning").show();
                $("#make-prediction").hide();
            } else {
                $("#minimum-model-warning").hide();
                $("#make-prediction").show();
            }
        });
    });
});
