function jsmeOnLoad() {
    var jsmeApplet = new JSApplet.JSME("jsme-container", "380px", "300px");
    document.JME = jsmeApplet;
}

$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-prediction").addClass("active");

    $(".nav-tabs a").on("shown.bs.tab", function(e) {
        $(e.target.hash).find("table.dataTable").DataTable().columns.adjust();
    });

    $("#prediction-model-selection").find(".dataTables_scrollBody").find("table").DataTable().on("draw", function() {
        var table = $(this);

        table.find("tr").click(function() {
            var checkbox = $(this).find('input[type="checkbox"]');
            checkbox.prop("checked", !(checkbox.prop("checked"))).change();
        });

        table.find('input[type="checkbox"]').change(function() {
            var checkbox = $(this);
            var row = checkbox.closest("tr");
            var modelName = row.find("td").find(".object-name").first().text();
            var counter = $("#selected-model-count");
            var count = parseInt(counter.text());
            row.toggleClass("selected").toggleClass("info");
            if (row.hasClass("selected")) {
                row.find('input[type="checkbox"]').prop("checked", true);
                counter.text(++count);
                $("#model-list").append("<li>" + modelName + "</li>")
            } else {
                row.find('input[type="checkbox"]').prop("checked", false);
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
