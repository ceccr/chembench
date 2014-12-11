function toggleForm() {
    $("form#updateDataset, #description-reference-buttons").toggle();
    $("#description-reference-text, button#edit-description-reference").toggle();
}

function composeRow(object) {
    var imageParams = {
        user : $("input#username").val(),
        projectType : "dataset",
        compoundId : object["compoundId"],
        datasetName : $("input#dataset-name").val(),
    };

    var r = '<tr><td class="name">' + object["compoundId"] + '</td>';
    if ($("input#has-structures").val() === "true") {
        r += '<td><img src="imageServlet?'+ $.param(imageParams)
                + '" class="img-thumbnail" width="125px" height="125px" alt="Compound structure"></td>';
    }
    r += '<td>' + object["activityValue"] + '</td></tr>';
    return r;
}

function updatePages(clicked) {
    var parent = clicked.closest("ul.pagination");

    // update new active fold number
    parent.children("li").removeClass("active");
    clicked.closest("li").addClass("active");

    // enable or disable previous and next buttons
    var foldNumber = parseInt(clicked.text(), 10);
    var firstFold = parseInt($("li.first-fold > a", parent).text(), 10);
    var lastFold = parseInt($("li.last-fold > a", parent).text(), 10);

    var previous = parent.children("li.previous");
    var next = parent.children("li.next");
    if (foldNumber === firstFold) {
        previous.addClass("disabled");
        next.removeClass("disabled");
    } else if (foldNumber === lastFold) {
        next.addClass("disabled");
        previous.removeClass("disabled");
    } else {
        previous.removeClass("disabled");
        next.removeClass("disabled");
    }
}

$(document).ready(function() {
    var popOverConfig = {
        html : true,
        template: '<div class="popover popover-image" role="tooltip">' +
                  '<div class="arrow"></div><div class="popover-content"></div></div>',
        content : function() {
            return '<img src="' + $(this).attr("src") + '">';
        },
        trigger : "hover",
        placement : "right",
    };

    $(".nav-list li").removeClass("active");
    $("#nav-button-datasets").addClass("active");

    $("form#updateDataset, #description-reference-buttons").hide();

    $("button#edit-description-reference").click(function() {
        toggleForm();
    });

    $("button#cancel-changes").click(function() {
        // reset form values
        var originalDescription = $('input[type="hidden"]#description').val();
        var originalPaperReference = $('input[type="hidden"]#paperReference').val();

        $("form#updateDataset").find('textarea[name="datasetDescription"]').val(originalDescription);
        $("form#updateDataset").find('textarea[name="datasetReference"]').val(originalPaperReference);

        toggleForm();
    });

    $("button#save-changes").click(function() {
        var form = $("form#updateDataset");
        var newDescription = $('textarea[name="datasetDescription"]').val();
        var newPaperReference = $('textarea[name="datasetReference"]').val();
        $.ajax({
            url : form.attr("action"),
            method : "POST",
            data : form.serialize(),
        }).success(function() {
            if (newDescription) {
                $("#description").text(newDescription);
            } else {
                $("#description").text("(No description given.)");
            }
            if (newPaperReference) {
                $("#paper-reference").text(newPaperReference);
            } else {
                $("#paper-reference").text("(No paper reference given.)");
            }

            toggleForm();
        }).fail(function() {
            bootbox.alert("Error updating dataset.");
        });
    });

    $(".img-thumbnail").popover(popOverConfig);

    $("ul.pagination a").click(function(e) {
        e.preventDefault();

        var clicked = $(this);
        var target = clicked.attr("href");
        var tbody = $("#folds table.compound-list > tbody");
        $.get(target).success(function(data) {
            // replace table body with new fold data
            tbody.empty();
            for (var i = 0; i < data.length; i++) {
                tbody.append(composeRow(data[i]));
            }
            tbody.find(".img-thumbnail").popover(popOverConfig);

            updatePages(clicked);
            tbody.closest("table").trigger("update");
        }).fail(function() {
            bootbox.alert("Error retrieving fold data.");
        });
    });

    $('a[data-toggle="tab"]').on("shown.bs.tab", function(e) {
        if (e.currentTarget.hash === "#folds") {
            var firstFold = $('ul.pagination > li:not(".previous, .next")').first();
            firstFold.children("a").click();
        } else if (e.currentTarget.hash === "#heatmap") {
            swfobject.embedSWF("assets/swf/heatmap.swf", "heatmapSwfContainer", "924", "924", "9.0.28", false,
                    flashvars, params, attributes);
        }
    });

    $("table.compound-list th:first-child").trigger("sort");
});
