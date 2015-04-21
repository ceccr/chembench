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

        table.find('input[type="checkbox"]').click(function() {
            var checkbox = $(this);
            checkbox.prop("checked", !(checkbox.prop("checked"))).change();
        }).change(function() {
            var checkbox = $(this);
            var row = checkbox.closest("tr");
            var modelName = row.find("td").find(".object-name").first().text();

            var modelList = $("#model-list");
            var modelsMatchingName = modelList.find("li").filter(function() {
                return $(this).text().trim() === modelName;
            });
            if (checkbox.prop("checked")) {
                row.addClass("selected info");
                if (modelsMatchingName.length === 0) {
                    modelList.append("<li>" + modelName + "</li>")
                }
            } else {
                row.removeClass("selected info");
                modelsMatchingName.remove();
            }

            // XXX don't use closest("table") or the header checkbox will be included too
            var count = row.closest("tbody").find('input[type="checkbox"]:checked').length;
            $("#selected-model-count").text(count);
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
