function updateDatasetInfo(idString) {
    var id = parseInt(idString);
    var datasetInfo = $("#dataset-info");
    var datasetInfoHelp = $("#dataset-info-help");

    if (id === 0) {
        datasetInfoHelp.show();
        datasetInfo.hide();
    } else if (id > 0) {
        datasetInfoHelp.hide();
        datasetInfo.show();
        $.get("/ajaxGetDataset", {"id": id}, function (dataset) {
            datasetInfo.html(
                "<h4>Dataset: " + dataset.name + "</h4>" +
                '<dl class="dl-horizontal properties-list">' +
                "<dt>Number of compounds</dt>" +
                "<dd>" + dataset.numCompound + "</dd>" +
                "<dt>Activity type</dt>" +
                "<dd>" + (dataset.continuous === true ? "Continuous" : "Category") + "</dd>" +
                '<dt class="availableDescriptors">Available descriptors</dt>' +
                '<dd class="available-descriptors">' + dataset.availableDescriptors + "</dd>" +
                "</dl>"
            );

            if ($.inArray("UPLOADED", datasetInfo.find(".available-descriptors").text().trim().split(/\s+/)) >= 0) {
                datasetInfo.find("dl").append(
                    "<dt>Uploaded descriptor type</dt>" +
                    "<dd>" + dataset.uploadedDescriptorType + "</dd>"
                );
            }
            formatAvailableDescriptors(datasetInfo.find(".available-descriptors"));
        }).fail(function () {
            datasetInfo.html(
                '<h4 class="text-danger">Error fetching dataset info</h4>' +
                "<p>A server error occurred while fetching dataset information for the selected dataset."
            );
        });
    }
}

$(document).ready(function() {
    $(".nav-list li").removeClass("active");
    $("#nav-button-modeling").addClass("active");

    var datasetSelect = $("#dataset-selection");
    var viewDatasetDetailButton = $("#view-dataset-detail");

    datasetSelect.prepend('<option selected="selected" value="0">(Select a dataset)</option>');
    datasetSelect.change(function() {
        var datasetId = parseInt(this.value);
        if (datasetId === 0) {
            viewDatasetDetailButton.addClass("disabled");
        } else {
            viewDatasetDetailButton.removeClass("disabled");
        }
        updateDatasetInfo(datasetId);
    });

    $("#view-dataset-detail").click(function(e) {
        e.preventDefault();

        var datasetId = parseInt(datasetSelect.val());
        if (datasetId !== 0) {
            window.open("/viewDataset.action?id=" + datasetId, "_blank");
        }
    });
});
