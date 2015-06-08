$.fn.exists = function () {
    return this.length !== 0;
};

String.prototype.toProperCase = function() {
    return this.replace(/\b\w+/g, function(s) {
        return s.charAt(0).toUpperCase() + s.substr(1).toLowerCase();
    });
};

String.prototype.contains = function(needle) {
    return this.indexOf(needle) > -1;
};

Array.prototype.contains = function(needle) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] === needle) {
            return true;
        }
    }
    return false;
};

/**
 * Checks if a dataset can generate a MODI value.
 *
 * Note: this is a direct copy of persistence.Dataset#canGenerateModi(); attempting to reuse that code by making an
 * AJAX call per dataset would incur too much of a speed penalty, so it is reproduced here in JS.
 *
 * @param dataset - the dataset to check
 * @returns true if MODI can be generated, false otherwise
 */
function canGenerateModi(dataset) {
    var actFile = dataset["actFile"];
    var availableDescriptors = dataset["availableDescriptors"];
    return !!actFile && actFile.length > 0 &&
           (availableDescriptors.contains("DRAGONH") || availableDescriptors.contains("CDK"));
}

function formatJobType(text) {
    return text.toProperCase();
}

function formatActivityType(text) {
    return text.toProperCase();
}

function formatDatasetType(text) {
    return text.replace("MODELING", "Modeling").replace("PREDICTION", "Prediction").replace("WITHDESCRIPTORS",
        ", with descriptors");
}

function formatSplitType(text) {
    return text.replace("NFOLD", "N-fold").replace("RANDOM", "Random Split").replace("USERDEFINED", "User-defined");
}

function formatModelingMethod(text) {
    return text.replace("RANDOMFOREST", "Random Forest");
}

function formatAvailableDescriptors(text) {
    var descriptorList = text.trim().split(/\s+/);
    var newDescriptorList = [];
    var dragonsPresent = false;
    if (descriptorList.contains("DRAGONNOH") && descriptorList.contains("DRAGONH")) {
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
    return newDescriptorList.join(", ");
}

function formatModi(text, dataset) {
    var html;
    if (dataset && canGenerateModi(dataset) === false) {
        html = '<span class="text-muted">Not available</span>';
    } else if (dataset && dataset["modiGenerated"] === false) {
        html = '<span class="text-warning">Not generated</span>' +
               '<button class="btn btn-primary btn-xs generate-modi">Generate MODI</button>';
    } else {
        var value = parseFloat(text);
        var cssClass = "text-danger";
        var tooltip = "Not modelable";
        if (value >= window.Chembench.MODI_MODELABLE) {
            cssClass = "text-success";
            tooltip = "Modelable";
        }
        html = '<span title="' + tooltip + '" class="' + cssClass + ' modi-value">' + value.toFixed(2) + '</span>';
    }
    return html;
}

function formatExternalPredictionAccuracy(fullValue) {
    if (fullValue) {
        var value = (fullValue.contains(" ± ")) ? fullValue.split(" ± ")[0] : fullValue;
        var cssClass;
        if (value >= 0.7) {
            cssClass = "text-success";
        } else if (value > 0.5) {
            cssClass = "text-warning";
        } else {
            cssClass = "text-danger";
        }
    }
    return '<span class="' + cssClass + ' external-acc-value">' + fullValue + "</span>";
}

function addRowHighlighting(row) {
    // add contextual highlighting for rows with MODI or R^2/CCR values
    var match = /text-(danger|warning|success)/.exec(row.find(".modi-value, .external-acc-value").attr("class"));
    if (match !== null) {
        row.addClass(match[1]);
    }
}

$(document).ready(function() {
    $.get("api/getCurrentUser", function(data) {
        window.Chembench.CURRENT_USER = data;
    });

    $(".nav-tabs li a").click(function() {
        history.pushState(null, null, $(this).attr('href'));
    });

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
    $(".available-descriptors").each(function() {
        var element = $(this);
        element.text(formatAvailableDescriptors(element.text()));
    });

    $(".modeling-method").each(function() {
        var element = $(this);
        element.text(formatModelingMethod(element.text()));
    });

    $(".split-type").each(function() {
        var element = $(this);
        element.text(formatSplitType(element.text()));
    });

    $(".dataset-type").each(function() {
        var element = $(this);
        element.text(formatDatasetType(element.text()));
    });

    $(".activity-type").each(function() {
        var element = $(this);
        element.text(formatActivityType(element.text()));
    });

    $(".job-type").each(function() {
        var element = $(this);
        element.text(formatJobType(element.text()));
    });

    $(".modi-value").each(function() {
        var element = $(this);
        // XXX n.b. use of html(), not text()
        element.html(formatModi(element.text()));
    });
});
