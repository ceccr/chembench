$(document).ready(function() {
    $("table.datatable[data-url]").each(function() {
        var table = $(this);
        if (table.closest("#prediction-model-selection, #prediction-dataset-selection").length) {
            var checkboxHeader = $('<th data-property="checkbox" class="unsortable"><input type="checkbox"></th>');
            checkboxHeader.prependTo(table.find("thead").find("tr"));
            checkboxHeader.find('input[type="checkbox"]').click(function() {
                var checkAll = $(this);
                checkAll.closest(".dataTables_scroll").find(".dataTables_scrollBody").find('input[type="checkbox"]').prop("checked",
                    checkAll.prop("checked")).change();
            });
        }

        var objectType = table.attr("data-object-type");
        if (objectType === "dataset") {
            var popoverConfig = {
                html: true,
                container: "body",
                template: '<div class="popover" role="tooltip">' +
                          '<div class="arrow"></div><div class="popover-content"></div></div>',
                content: "The <strong>Modelability Index</strong> (MODI) is a measure of how modelable a dataset is. " +
                         "Datasets with a MODI above " + Chembench.MODI_MODELABLE +
                         " are considered modelable, and those below that threshold are considered not modelable.<br><br>" +
                         "For more information, see " +
                         '<a href="http://www.ncbi.nlm.nih.gov/pubmed/24251851" target="_blank">this citation</a>.',
                trigger: "focus",
                placement: "top"
            };
            table.find(".modi-help").popover(popoverConfig).click(function(e) {
                e.stopPropagation(); // prevents sorting when clicking popover trigger
            });
        }

        var columns = [];
        table.find("th").each(function() {
            var th = $(this);
            var column = {};
            var property = th.attr("data-property");
            column["data"] = property;
            switch (property) {
                case "checkbox":
                    column["data"] = function(row) {
                        return '<input type="checkbox"><input type="hidden" name="id" value="' + row["id"] + '">';
                    };
                    break;
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
                            var r = '<div class="name-cell">' + viewLink + '<span class="object-name">' + nameDisplay +
                                    "</span></a><br>" + '<div class="object-action-group">' +
                                    '<div class="download object-action">' +
                                    '<span class="glyphicon glyphicon-save"></span>&nbsp;' + downloadLink +
                                    "Download</a></div>";
                            var currentUser = Chembench.CURRENT_USER;
                            if (currentUser &&
                                (currentUser.isAdmin === "YES" || currentUser.userName === row["userName"])) {
                                r += '<div class="delete object-action">' +
                                     '<span class="glyphicon glyphicon-remove"></span>&nbsp;' + '<a href="' +
                                     deleteAction + "?" + $.param({"id": row["id"]}) + '">Delete</a></div>';
                            }
                            r += "</div>";
                            return r;
                        }
                        return data;
                    };
                    break;
                case "datasetDisplay": // modeling/prediction dataset
                    column["render"] = function(data, type, row) {
                        if (type === "display") {
                            var nameDisplay = data.split("_").join("_<wbr>");
                            return '<div class="name-cell"><a href="viewDataset?' + $.param({"id": row["datasetId"]}) +
                                   '" target="_blank">' + nameDisplay + "</a></div>";
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
                    column["render"] = function(data, type, row) {
                        var r = data;
                        if (row["childType"] === "NFOLD") {
                            r = row["externalPredictionAccuracyAvg"];
                        }
                        if (r === "0.0" || r === "0.0 ± 0.0") {
                            r = "N/A";
                        }

                        if (type === "display") {
                            return formatExternalPredictionAccuracy(r);
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
                addRowHighlighting(row);

                if (objectType === "dataset") {
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
});
