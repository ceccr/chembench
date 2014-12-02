var guestMessage = "A guest account allows a user to explore the functionality of Chembench using publicly "
        + "available datasets, predictions on single molecules, and modeling using Random Forests. "
        + "<br><br> All guest data is deleted when you leave the site or become inactive for 90 minutes. "
        + "For additional functionality, please register an account.";

$(document).ready(function() {
    // navigation button handlers
    $(".nav-list li").mouseup(function(event) {
        if (event.which === 1) {
            window.location = $(this).find("a").attr("href");
        }
    });
    $(".nav-list li").on("mouseenter mouseleave", function(event) {
        $(this).find("a").toggleClass("hovered", event.type === "mouseenter");
    });

    // default highlighted button should be Home
    $("#nav-button-home").addClass("active");

    // login & logout button handlers
    $(document).ready(function() {
        $('input[name="username"]').focus();
    });
    $("#guest-login").click(function(event) {
        event.preventDefault();
        bootbox.confirm(guestMessage, function(response) {
            if (response === true) {
                window.location = $("#guest-login").attr("href");
            }
        });
    });
    $(".logout-button").click(function() {
        window.location = "logout";
    });

    // replace ugly capitalization for constants
    $(".available-descriptors").each(function() {
        var descriptors = $(this).text().trim().split(/\s+/);
        for (var i = 0; i < descriptors.length; i++) {
            if (descriptors[i] === "DRAGONH") {
                descriptors[i] = "Dragon (with hydrogens)";
            } else if (descriptors[i] === "DRAGONNOH") {
                descriptors[i] = "Dragon (no hydrogens)";
            } else if (descriptors[i] === "UPLOADED") {
                descriptors[i] = "Uploaded descriptors";
            }
        }
        $(this).text(descriptors.join(", "));
    });
    $(".modeling-method").each(function() {
        var modelingMethod = $(this).text();
        $(this).text(modelingMethod.replace(/RANDOMFOREST/, "Random Forest"));
    });
    $(".split-type").each(function() {
        var splitType = $(this).text();
        $(this).text(splitType
                .replace(/NFOLD/, "N-fold")
                .replace(/RANDOM/, "Random Split")
                .replace(/USERDEFINED/, "User-defined"));
    });
    $(".dataset-type").each(function() {
        var datasetType = $(this).text();
        $(this).text(datasetType
                .replace(/MODELING/, "Modeling")
                .replace(/PREDICTION/, "Prediction")
                .replace(/WITHDESCRIPTORS/, ", with descriptors"));
    });

    $(".generate-modi").click(function() {
        $(this).text("Generating...").prop("disabled", "disabled");
        var parent = $(this).parent();
        $.ajax({
            type: "POST",
            url: "/generateModi",
            data: { id: parent.children('input[name="dataset-id"]').val() },
        }).success(function(modiValue) {
            parent.text(modiValue.toFixed(2)); // round to two decimal places
        }).fail(function() {
            parent.html('<span class="text-danger">MODI generation failed</span>');
        });
    });

    $.tablesorter.themes.bootstrap = {
            sortNone: "glyphicon glyphicon-sort",
            sortAsc: "glyphicon glyphicon-sort-by-attributes",
            sortDesc: "glyphicon glyphicon-sort-by-attributes-alt",
        };

    $(".tablesorter").tablesorter({
        sortStable: true,

        theme: "bootstrap",
        headerTemplate: "{content} {icon}",
        widgets: ["uitheme"],
    });
});
