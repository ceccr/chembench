function jsmeOnLoad() {
    document.JME = new JSApplet.JSME("jsme", "348px", "300px");
}

$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-prediction").addClass("active");

    $(".nav-tabs a").on("shown.bs.tab", function(e) {
        $(e.target.hash).find("table.dataTable").DataTable().columns.adjust();
    });

    $("#jsme-clear").click(function() {
        document.JME.reset();
    });

    $("#jsme-smiles-predict").click(function() {
        $("#smiles").val(document.JME.smiles()).closest("form").submit();
    });

    $("#copy-smiles").zclip({
        path: "/assets/swf/ZeroClipboard.swf",
        copy: $("#smiles").val()
    }).tooltip({
        animation: false,
        placement: "bottom"
    }).click(function() {
        var button = $(this);
        var oldTitle = button.attr("data-original-title");
        button.attr("data-original-title", "Copied!").tooltip("hide").tooltip("show").one("hidden.bs.tooltip",
            function() {
                button.attr("data-original-title", oldTitle);
            });
    });

    $("form#predict-compound").submit(function(e) {
        e.preventDefault();
        var form = $(this);
        if (!form.find("#smiles").val()) {
            return false;
        }

        // TODO spinny
        form.find('button[type="submit"]').text("Predicting...").addClass("disabled");

        var selectedModelIds = $("#prediction-model-selection").find("tbody").find(":checked").siblings('[name="id"]').map(function() {
            return $(this).val();
        }).get();
        var url = form.attr("action") + "?" + form.serialize() + "&predictorIds=" + selectedModelIds.join(" ");
        $.get(url, function(data) {
            var predictionResults = $("#prediction-results");
            predictionResults.find(".help-block").remove();
            predictionResults.prepend(data);
        }).fail(function() {
            bootbox.alert("Error occurred during prediction.");
        }).always(function() {
            form.find('button[type="submit"]').text("Predict").removeClass("disabled");
        });
    });

    $("form#predict-dataset").submit(function(e) {
        e.preventDefault();
        var form = $(this);
        var jobName = form.find("#jobName");
        var selectedDatasetIds = $("#prediction-dataset-selection").find("tbody").find(":checked").siblings('[name="id"]').map(function() {
            return $(this).val();
        }).get();
        if (!jobName.val() || !selectedDatasetIds.length) {
            return false;
        }

        // TODO spinny
        form.find('button[type="submit"]').text("Predicting...").addClass("disabled");

        // need id-name pairs so we can utilize model names for jobName editing
        var selectedModels = [];
        $("#prediction-model-selection").find("tbody").find("tr:has(:checked)").each(function() {
            var row = $(this);
            var model = {};
            model["id"] = row.find('[name="id"]').val();
            model["name"] = row.find(".object-name").text();
            selectedModels.push(model);
        });
        form.find("#selectedPredictorIds").val($.map(selectedModels, function(m) { return m["id"]; }).join(" "));

        var originalJobName = jobName.val();
        $.each(selectedDatasetIds, function(index, id) {
            form.find("#selectedDatasetId").val(id);
            // change jobName only if we need to (multiple datasets to predict)
            if (selectedDatasetIds.length > 1) {
                jobName.val(originalJobName + " " + selectedModels[index]["name"]);
            }
            $.post(form.attr("action") + "?" + form.serialize());
        });

        window.location = Chembench.MYBENCH_URL;
    });

    $("#prediction-model-selection, #prediction-dataset-selection").find("table").DataTable().on("draw", function() {
        var table = $(this);
        var tableType = (table.parents("#prediction-model-selection").length) ? "model" : "dataset";

        table.find("tr").click(function() {
            var checkbox = $(this).find('input[type="checkbox"]');
            checkbox.prop("checked", !(checkbox.prop("checked"))).change();
        }).find("a").click(function(e) {
            e.stopPropagation();
        });

        table.find('input[type="checkbox"]').click(function() {
            var checkbox = $(this);
            checkbox.prop("checked", !(checkbox.prop("checked"))).change();
        }).change(function() {
            var checkbox = $(this);
            var row = checkbox.closest("tr");
            var objectName = row.find("td").find(".object-name").first().text();

            var objectList = (tableType === "model") ? $("#model-list") : $("#dataset-list");
            var objectsMatchingName = objectList.find("li").filter(function() {
                return $(this).text().trim() === objectName;
            });
            if (checkbox.prop("checked")) {
                var match = /(danger|warning|success)/.exec(row.attr("class"));
                if (match != null) {
                    var color = match[1];
                    row.data("oldClass", color);
                    row.removeClass(color);
                }
                row.addClass("info");
                if (objectsMatchingName.length === 0) {
                    objectList.append("<li>" + objectName + "</li>")
                }
            } else {
                row.removeClass("info");
                row.addClass(row.data("oldClass"));
                objectsMatchingName.remove();
            }

            // XXX don't use closest("table") or the header checkbox will be included too
            var count = row.closest("tbody").find('input[type="checkbox"]:checked').length;
            var warning = (tableType === "model") ? $("#minimum-model-warning") : $("#minimum-dataset-warning");
            var counter = (tableType === "model") ? $("#selected-model-count") : $("#selected-dataset-count");
            counter.text(count);
            if (count === 0) {
                warning.show();
                if (tableType === "model") {
                    $("#make-prediction").hide();
                    $("#model-list-message").hide();
                } else if (tableType === "dataset") {
                    $("#dataset-list-message").hide();
                }
            } else {
                warning.hide();
                if (tableType === "model") {
                    $("#make-prediction").show();
                    $("#model-list-message").show();
                } else if (tableType === "dataset") {
                    $("#dataset-list-message").show();
                }
            }
        });
    });
});
