function formatAvailableDescriptors(container) {
    var descriptorList = $(container).text().trim().split(/\s+/);
    var newDescriptorList = [];
    var dragonsPresent = false;
    if ($.inArray("DRAGONNOH", descriptorList) >= 0 && $.inArray("DRAGONH", descriptorList) >= 0) {
        newDescriptorList.push("Dragon");
        dragonsPresent = true;
    }

    for (var i = 0; i < descriptorList.length; i++) {
        var curr = descriptorList[i];
        if (curr === "UPLOADED") {
            var toAdd = ["Uploaded descriptors"];
            while (!/\)$/.test(toAdd[toAdd.length - 1]) && i < descriptorList.length) {
                toAdd.push(descriptorList[++i]);
            }
            newDescriptorList.push(toAdd.join(" "));
        } else if (curr === "MOLCONNZ") {
            newDescriptorList.push("MolconnZ");
        } else if (curr === "DRAGONH" || curr === "DRAGONNOH") {
            if (dragonsPresent === false) {
                newDescriptorList.push(curr.replace("DRAGON", "Dragon").replace("NOH", "NoH"));
            }
        } else {
            newDescriptorList.push(curr);
        }
    }
    $(container).text(newDescriptorList.join(", "));
}

function formatModi() {
    $(".modi-value").each(function() {
        var element = $(this);
        var value = element.text();
        if (isNaN(value)) {
            // XXX don't combine this conditional with the top one!
            if (element.has(".generate-modi").length === 0) {
                element.addClass("text-muted");
            }
        } else {
            var valueNumeric = parseFloat(value);
            var tooltip;
            if (valueNumeric >= Chembench.MODI_MODELABLE) {
                element.addClass("text-success");
                tooltip = "Modelable";
            } else {
                element.addClass("text-danger");
                tooltip = "Not modelable";
            }
            element.html('<span title="' + tooltip + '">' + valueNumeric.toFixed(2) + "</span>");
        }
    });
}

$(document).ready(function() {
    // navigation button handlers
    $(".nav-list li").mouseup(function(event) {
        if (event.which === 1) {
            window.location = $(this).find("a").attr("href");
        }
    }).on("mouseenter mouseleave", function(event) {
        $(this).find("a").toggleClass("hovered", event.type === "mouseenter");
    });

    // default highlighted button should be Home
    $("#nav-button-home").addClass("active");

    // login & logout button handlers
    $(document).ready(function() {
        $('input[name="username"]').focus();
    });
    $(".guest-login").click(function(event) {
        event.preventDefault();

        var guestMessage = "A guest account allows a user to explore the functionality of Chembench using publicly " +
                           "available datasets, predictions on single molecules, and modeling using Random Forests. " +
                           "<br><br> All guest data is deleted when you leave the site or become inactive for 90 minutes. " +
                           "For additional functionality, please register an account.";

        bootbox.confirm(guestMessage, function(response) {
            if (response === true) {
                window.location = $(".guest-login").attr("href");
            }
        });
    });

    // replace ugly capitalization for constants
    $(".available-descriptors").each(function(_, element) {
        formatAvailableDescriptors(element);
    });

    $(".modeling-method").each(function() {
        var modelingMethod = $(this).text();
        $(this).text(modelingMethod.replace("RANDOMFOREST", "Random Forest"));
    });
    $(".split-type").each(function() {
        var splitType = $(this).text();
        $(this).text(splitType.replace("NFOLD", "N-fold").replace("RANDOM", "Random Split").replace("USERDEFINED",
            "User-defined"));
    });
    $(".dataset-type").each(function() {
        var datasetType = $(this).text();
        $(this).text(datasetType.replace("MODELING", "Modeling").replace("PREDICTION",
            "Prediction").replace("WITHDESCRIPTORS", ", with descriptors"));
    });

    $(".generate-modi").click(function() {
        var button = $(this).text("Generating...").prop("disabled", "disabled");
        var parent = button.parent(".modi-value");
        $.ajax({
            type: "POST",
            url: "/generateModi",
            data: {id: parent.children('input[name="dataset-id"]').val()}
        }).success(function(modiValue) {
            parent.text(modiValue);
            formatModi();
        }).fail(function() {
            parent.html('<span class="text-danger">MODI generation failed</span>');
        });
    });

    $("table.datatable").each(function() {
        var table = $(this);
        var options = {
            columnDefs: [{ orderable: false, targets: "unsortable" }]
        };
        var dateColumnIndex = table.find('th:contains("Date")').index();
        if (dateColumnIndex > 0) {
            options["order"] = [[dateColumnIndex, "desc"]];
        }
        table.dataTable(options);
    });
});
