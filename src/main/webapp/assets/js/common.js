String.prototype.toProperCase = function() {
    return this.replace(/\b\w+/g, function(s) {
        return s.charAt(0).toUpperCase() + s.substr(1).toLowerCase();
    });
};

String.prototype.contains = function(needle) {
    return this.indexOf(needle) > -1;
};

function canGenerateModi(dataset) {
    var actFile = dataset["actFile"];
    var availableDescriptors = dataset["availableDescriptors"];
    return actFile && actFile.length > 0 &&
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
        if (value >= Chembench.MODI_MODELABLE) {
            cssClass = "text-success";
            tooltip = "Modelable";
        }
        html = '<span title="' + tooltip + '" class="' + cssClass + ' modi-value">' + value.toFixed(2) + '</span>';
    }
    return html;
}

function addDatasetRowHighlighting(row) {
    // add contextual highlighting for dataset rows with MODI values
    var match = /text-(danger|warning|success)/.exec(row.find(".modi-value").attr("class"));
    if (match !== null) {
        row.addClass(match[1]);
    }
}

$(document).ready(function() {
    $.get("api/getCurrentUser", function(data) {
        Chembench.CURRENT_USER = data;
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

    $("table.datatable[data-url]").each(function() {
        var table = $(this);
        var columns = [];
        var objectType = table.attr("data-object-type");
        table.find("th").each(function() {
            var th = $(this);
            var column = {};
            var property = th.attr("data-property");
            column["data"] = property;
            switch (property) {
                case "name":
                    column["render"] = function(data, type, row) {
                        if (type === "display") {
                            var downloadLink;
                            if (objectType === "dataset") {
                                downloadLink = '<a href="datasetFilesServlet?' + $.param({
                                    "datasetName": data,
                                    "user": row["userName"]
                                }) + '">';
                            } else if (objectType === "model") {
                                downloadLink = '<a href="projectFilesServlet?' + $.param({
                                    "project": data,
                                    "user": row["userName"],
                                    "projectType": "modeling"
                                }) + '">';
                            } else if (objectType === "prediction") {
                                downloadLink = '<a href="fileServlet?' + $.param({
                                    "id": row["id"],
                                    "user": row["userName"],
                                    "jobType": "PREDICTION",
                                    "file": "predictionAsCsv"
                                }) + '">';
                            }

                            var viewAction = "view";
                            var deleteAction = "delete";
                            if (objectType === "model") {
                                viewAction += "Predictor";
                                deleteAction += "Predictor";
                            } else {
                                viewAction += objectType.toProperCase();
                                deleteAction += objectType.toProperCase();
                            }
                            var viewLink = '<a href="' + viewAction + "?" + $.param({"id": row["id"]}) +
                                           '" target="_blank">';

                            var nameDisplay = data.split("_").join("_<wbr>");
                            var r = viewLink + '<span class="object-name">' + nameDisplay+ "</span></a><br>" +
                                    '<div class="button-group">' + '<div class="download">' +
                                    '<span class="glyphicon glyphicon-save"></span>&nbsp;' + downloadLink +
                                    "Download</a></div>";
                            var currentUser = Chembench.CURRENT_USER;
                            if (currentUser &&
                                (currentUser.isAdmin === "YES" || currentUser.userName === row["userName"])) {
                                r += '<div class="delete">' + '<span class="glyphicon glyphicon-remove"></span>&nbsp;' +
                                     '<a href="' + deleteAction + "?" + $.param({"id": row["id"]}) +
                                     '">Delete</a></div>';
                            }
                            return r;
                        }
                        return data;
                    };
                    break;
                case "datasetDisplay": // modeling/prediction dataset
                    column["render"] = function(data, type, row) {
                        if (type === "display") {
                            var nameDisplay = data.split("_").join("_<wbr>");
                            return '<a href="viewDataset?' + $.param({"id": row["datasetId"]}) + '" target="_blank">' +
                                   '<span class="object-name">' + nameDisplay + "</span></a>";
                        }
                        return data;
                    };
                    break;
                case "datasetType":
                    column["render"] = function(data, _, row) {
                        var r = formatDatasetType(data);
                        if (r.toLowerCase().contains("modeling")) {
                            r += " (" + row["modelType"].toLowerCase() + ")";
                        }
                        return r;
                    };
                    break;
                case "modelMethod":
                    column["render"] = function(data) {
                        return formatModelingMethod(data);
                    };
                    break;
                case "availableDescriptors": // datasets
                case "descriptorGeneration": // models
                    column["render"] = function(data, _, row) {
                        var r = data;
                        var uploadedIndex = r.toLowerCase().indexOf("uploaded");
                        if (uploadedIndex > -1) {
                            // 8 being the number of characters in "uploaded"
                            r = r.substring(0, uploadedIndex + 8) + ' ("' + row["uploadedDescriptorType"] + '") ' +
                                r.substring(uploadedIndex + 8);
                        }
                        return formatAvailableDescriptors(r);
                    };
                    break;
                case "modi":
                    column["render"] = function(data, type, row) {
                        if (type === "display") {
                            return formatModi(data, row);
                        }
                        return data;
                    };
                    break;
                case "externalPredictionAccuracy":
                    // for single-fold datasets the property is "externalPredictionAccuracy",
                    // but for N-fold datasets the property is "externalPredictionAccuracyAvg"
                    column["render"] = function(data, _, row) {
                        var r = data;
                        if (row["childType"] === "NFOLD") {
                            r = row["externalPredictionAccuracyAvg"];
                        }
                        if (r === "0.0" || r === "0.0 ± 0.0") {
                            r = "N/A";
                        }
                        return r;
                    };
                    break;
                case "createdTime": // datasets
                case "dateCreated": // everything else
                    column["render"] = function(data, type) {
                        if (type === "display") {
                            var date = data.split("T")[0];
                            return '<span class="text-nowrap">' + date + "</span>";
                        }
                        return data;
                    };
                    break;
                case "userName":
                    if (th.hasClass("public-private")) {
                        column["render"] = function(data, type) {
                            if (type === "display") {
                                if (data === "all-users") {
                                    return '<span class="public-private text-primary">' +
                                           '<span class="glyphicon glyphicon-eye-open"></span> Public</span>';
                                } else {
                                    return '<span class="public-private text-muted">' +
                                           '<span class="glyphicon glyphicon-eye-close"></span> Private</span>';
                                }
                            } else if (type === "filter") {
                                if (data === "all-users") {
                                    return "Public";
                                } else {
                                    return "Private";
                                }
                            }
                            return data;
                        }
                    }
                    break;
            }
            columns.push(column);
        });
        var options = $.extend({
            "ajax": table.attr("data-url"),
            "columns": columns,
            "scrollY": "300px",
            "scrollCollapse": true,
            "createdRow": function(row, data) {
                var row = $(row);

                if (objectType === "dataset") {
                    addDatasetRowHighlighting(row);

                    row.find(".generate-modi").click(function() {
                        var button = $(this).text("Generating...").prop("disabled", "disabled");
                        var parent = button.closest("td");
                        $.post("generateModi", {"id": data["id"]}, function(modiValue) {
                            parent.html(formatModi(modiValue));
                            addDatasetRowHighlighting(row);
                        }).fail(function() {
                            parent.html('<span class="text-danger">MODI generation failed</span>');
                        });
                    });
                }

                row.find(".delete a").click(function(e) {
                    e.preventDefault();
                    var link = $(this).blur();
                    var verb = (objectType === "job" ? "cancel" : "delete");
                    var message = "Are you sure you want to " + verb + " the " + objectType + ' "' + data["name"] +
                                  '"?';
                    bootbox.confirm(message, function(response) {
                        if (response === true) {
                            $.post(link.attr("href"), function() {
                                if (objectType === "job") {
                                    window.location.reload();
                                } else {
                                    link.closest("tr").fadeOut();
                                }
                            }).fail(function(xhr) {
                                var errorText = $(xhr.responseText).find("#errors").text().trim()
                                bootbox.alert("Error deleting " + objectType + ":<br><br>" + errorText);
                            });
                        }
                    });
                });
            }
        }, Chembench.DATATABLE_OPTIONS);
        var dateIndex = table.find("th").filter(".date-created").index();
        if (dateIndex > -1) {
            options["order"] = [[dateIndex, "desc"]];
        }
        table.DataTable(options);
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
