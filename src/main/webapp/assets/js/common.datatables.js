(function() {
    'use strict';

    $(document).ready(function() {
        $('table.datatable[data-url]').each(function() {
            var table = $(this);
            if (table.closest('.checkbox-table').exists()) {
                var checkboxHeader = $('<th data-property="checkbox" data-transient="data-transient" class="unsortable">' +
                                       '<input type="checkbox"></th>');
                checkboxHeader.prependTo(table.find('thead').find('tr'));
                checkboxHeader.find('input[type="checkbox"]').click(function() {
                    var checkAll = $(this);
                    checkAll.closest('.dataTables_scroll').find('.dataTables_scrollBody').find('input[type="checkbox"]').prop('checked',
                            checkAll.prop('checked')).change();
                });
            } else if (table.closest('.radio-table').exists()) {
                var radioHeader = $('<th data-property="radio" data-transient="data-transient" class="unsortable"></th>');
                radioHeader.prependTo(table.find('thead').find('tr'));
            }

            var objectType = table.attr('data-object-type');
            if (objectType === 'dataset') {
                var popoverConfig = {
                    html: true,
                    container: 'body',
                    template: '<div class="popover" role="tooltip">' +
                              '<div class="arrow"></div><div class="popover-content"></div></div>',
                    content: 'The <strong>Modelability Index</strong> (MODI) is a measure of how modelable a dataset is. ' +
                             'Datasets with a MODI above ' + Chembench.MODI_MODELABLE +
                             ' are considered modelable, and those below that threshold are considered not modelable.<br><br>' +
                             'For more information, see ' +
                             '<a href="http://www.ncbi.nlm.nih.gov/pubmed/24251851" target="_blank">this citation</a>.',
                    trigger: 'focus',
                    placement: 'top'
                };
                table.find('.modi-help').popover(popoverConfig).click(function(e) {
                    e.stopPropagation(); // prevents sorting when clicking popover trigger
                });
            }

            var columns = [];
            table.find('th').each(function() {
                var th = $(this);
                var column = {};
                var property = th.attr('data-property');

                if (th.not('[data-transient]').exists()) {
                    column.data = property;
                }
                switch (property) {
                    // transient properties
                    case 'checkbox':
                        column.data = function(row) {
                            return '<input type="checkbox"><input type="hidden" name="id" value="' + row.id + '">';
                        };
                        break;
                    case 'radio':
                        column.data = function(row) {
                            return '<input type="radio" name="' + table.attr('id') +
                                   '"><input type="hidden" name="id" value="' + row.id + '">';
                        };
                        break;
                    case 'cancel':
                        column.data = function(row) {
                            if (Chembench.CURRENT_USER.isAdmin === 'YES' ||
                                (Chembench.CURRENT_USER.userName === row.userName)) {
                                return '<a class="delete-link" href="deleteJob?id=' + row.id + '">cancel</a>';
                            }
                            return '';
                        };
                        break;
                    case 'public-private':
                        column.data = 'userName';
                        column.render = function(data, type) {
                            var visibility = (data === 'all-users') ? 'Public' : 'Private';
                            if (type === 'display') {
                                if (data === 'all-users') {
                                    return '<span class="public-private text-primary">' +
                                           '<span class="glyphicon glyphicon-eye-open"></span>&nbsp;' + visibility +
                                           '</span>';
                                } else {
                                    return '<span class="public-private text-muted">' +
                                           '<span class="glyphicon glyphicon-eye-close"></span>&nbsp;' + visibility +
                                           '</span>';
                                }
                            }
                            return visibility;
                        };
                        break;
                    // end transient properties
                    case 'name':
                        column.render = function(data, type, row) {
                            if (type === 'display') {
                                var downloadLink;
                                if (objectType === 'dataset') {
                                    downloadLink = '<a href="datasetFilesServlet?' + $.param({
                                                'datasetName': data,
                                                'user': row.userName
                                            }) + '">';
                                } else if (objectType === 'model') {
                                    downloadLink = '<a href="projectFilesServlet?' + $.param({
                                                'project': data,
                                                'user': row.userName,
                                                'projectType': "modeling"
                                            }) + '">';
                                } else if (objectType === 'prediction') {
                                    downloadLink = '<a href="fileServlet?' + $.param({
                                                'id': row.id,
                                                'user': row.userName,
                                                'jobType': 'PREDICTION',
                                                'file': "predictionAsCsv"
                                            }) + '">';
                                }

                                var detailAction = objectType + 'Detail';
                                var deleteAction = 'delete';
                                if (objectType === 'model') {
                                    deleteAction += 'Predictor';
                                } else {
                                    deleteAction += objectType.toProperCase();
                                }
                                var detailLink = '<a href="' + detailAction + "?" + $.param({'id': row.id}) +
                                                 '" target="_blank">';

                                var nameDisplay = data.split('_').join('_<wbr>');
                                var r = '<div class="name-cell">' + detailLink + '<span class="object-name">' +
                                        nameDisplay + "</span></a><br>" + '<div class="object-action-group">' +
                                        '<div class="download object-action">' +
                                        '<span class="glyphicon glyphicon-save"></span>&nbsp;' + downloadLink +
                                        'Download</a></div>';
                                var currentUser = Chembench.CURRENT_USER;
                                if (currentUser &&
                                    (currentUser.isAdmin === 'YES' || currentUser.userName === row.userName)) {
                                    r += '<div class="delete object-action">' +
                                         '<span class="glyphicon glyphicon-remove"></span>&nbsp;' + '<a href="' +
                                         deleteAction + "?" + $.param({'id': row.id}) + '">Delete</a></div>';
                                }
                                r += '</div>';
                                return r;
                            }
                            return data;
                        };
                        break;
                    case 'predictorNames':
                        column.render = function(data, type) {
                            if (data) {
                                var models = [];
                                $.each(data.split(';'), function(_, p) {
                                    // raw format: "MRP3x-DragonH-RF (DRAGONH,RANDOMFOREST)"
                                    var match = /(.+) \((.+),(.+)\)/.exec(p);
                                    if (match !== null) {
                                        models.push(match[1] + ' (' + Chembench.formatAvailableDescriptors(match[2]) +
                                                    ', ' + Chembench.formatModelingMethod(match[3]) + ')');
                                    }
                                });
                                return models.join((type === 'display') ? '<br>' : ' ');
                            }
                            return data;
                        };
                        break;
                    case 'jobType':
                        column.render = function(data, type) {
                            if (type === 'display') {
                                return Chembench.formatJobType(data);
                            }
                            return data;
                        };
                        break;
                    case 'datasetDisplay': // modeling/prediction dataset
                        column.render = function(data, type, row) {
                            if (type === 'display') {
                                var nameDisplay = data.split('_').join('_<wbr>');
                                return '<div class="name-cell"><a href="datasetDetail?' +
                                       $.param({'id': row.datasetId}) + '" target="_blank">' + nameDisplay +
                                       '</a></div>';
                            }
                            return data;
                        };
                        break;
                    case 'datasetType':
                        column.render = function(data, _, row) {
                            var r = Chembench.formatDatasetType(data);
                            if (r.toLowerCase().contains('modeling')) {
                                r += ' (' + row.modelType.toLowerCase() + ')';
                            }
                            return r;
                        };
                        break;
                    case 'modelMethod':
                        column.render = function(data) {
                            return Chembench.formatModelingMethod(data);
                        };
                        break;
                    case 'availableDescriptors': // datasets
                    case 'descriptorGeneration': // models
                        column.render = function(data, _, row) {
                            var r = data;
                            var uploadedIndex = r.toLowerCase().indexOf('uploaded');
                            if (uploadedIndex > -1) {
                                // 8 being the number of characters in "uploaded"
                                r = r.substring(0, uploadedIndex + 8) + ' ("' + row.uploadedDescriptorType + '") ' +
                                    r.substring(uploadedIndex + 8);
                            }
                            return Chembench.formatAvailableDescriptors(r);
                        };
                        break;
                    case 'modi':
                        column.render = function(data, type, row) {
                            if (type === 'display') {
                                return Chembench.formatModi(data, row);
                            }
                            return data;
                        };
                        break;
                    case 'similarityCutoff':
                        column.render = function(data, type) {
                            if (data === 99999) {
                                if (type === 'display') {
                                    return '<span class="text-muted">Not used</span>';
                                }
                                return 'Not used';
                            }
                            return data + '&sigma;';
                        };
                        break;
                    case 'externalPredictionAccuracy':
                        // for single-fold datasets the property is "externalPredictionAccuracy",
                        // but for N-fold datasets the property is "externalPredictionAccuracyAvg"
                        column.render = function(data, type, row) {
                            var r = data;
                            if (row.childType === 'NFOLD') {
                                r = row.externalPredictionAccuracyAvg;
                            }
                            if (r === '0.0' || r === '0.0 ± 0.0') {
                                r = 'N/A';
                            }

                            if (type === 'display') {
                                return Chembench.formatExternalPredictionAccuracy(r);
                            }
                            return r;
                        };
                        break;
                    case 'timeCreated': // jobs
                    case 'createdTime': // datasets
                    case 'dateCreated': // everything else
                        column.render = function(data, type) {
                            if (type === 'display') {
                                var date = data.split('T')[0];
                                return '<span class="text-nowrap">' + date + '</span>';
                            }
                            return data;
                        };
                        break;
                }
                columns.push(column);
            });
            var options = $.extend({
                'ajax': table.attr('data-url'),
                'columns': columns,
                'scrollY': '300px',
                'scrollCollapse': true,
                'createdRow': function(tr, data) {
                    var row = $(tr);
                    Chembench.addRowHighlighting(row);

                    if (objectType === 'dataset') {
                        row.find('.generate-modi').click(function() {
                            var button = $(this).text('Generating...').prop('disabled', 'disabled');
                            var parent = button.closest('td');
                            $.post('generateModi', {'id': data.id}, function(modiValue) {
                                parent.html(Chembench.formatModi(modiValue));
                                Chembench.addRowHighlighting(row);
                            }).fail(function() {
                                parent.html('<span class="text-danger">MODI generation failed</span>');
                            });
                        });
                    }

                    row.find('.delete a').click(function(e) {
                        e.preventDefault();
                        var link = $(this).blur();
                        var verb = (objectType === 'job' ? 'cancel' : 'delete');
                        var message = 'Are you sure you want to ' + verb + ' the ' + objectType + ' "' + data.name +
                                      '"?';
                        bootbox.confirm(message, function(response) {
                            if (response === true) {
                                $.post(link.attr('href'), function() {
                                    if (objectType === 'job') {
                                        window.location.reload();
                                    } else {
                                        var table = link.closest('table').DataTable();
                                        var row = link.closest('tr');
                                        row.fadeOut(400, function() {
                                            table.row(row).remove().draw();
                                        });
                                    }
                                }).fail(function(xhr) {
                                    var errorText = $(xhr.responseText).find('#errors').text().trim();
                                    bootbox.alert('Error deleting ' + objectType + ':<br><br>' + errorText);
                                });
                            }
                        });
                    });
                }
            }, Chembench.DATATABLE_OPTIONS);
            var dateIndex = table.find('th').filter('.date-created').index();
            if (dateIndex > -1) {
                options.order = [[dateIndex, 'desc']];
            }

            options.drawCallback = function() {
                var wrapper = $(this.api().table().container());
                var queue = wrapper.find('table').attr('data-queue-name');
                var isErrorJobQueue = (objectType === 'job' && queue === 'error');
                if (!wrapper.siblings('.no-objects-message').exists()) {
                    if (!isErrorJobQueue) {
                        var message;
                        if (objectType === 'job') {
                            message = '(The ' + queue + ' queue is empty.)';
                        } else {
                            message = '(There are no ' + objectType + 's to display.)';
                        }
                        var messageSpan = $('<span class="no-objects-message text-muted">' + message + '</span>');
                        messageSpan.insertBefore(wrapper).hide();
                    }
                }

                if (wrapper.find('.dataTables_empty').exists()) {
                    if (isErrorJobQueue) {
                        $('#jobs-with-errors').hide();
                    } else {
                        wrapper.hide().siblings('.no-objects-message').show();
                    }
                } else {
                    if (isErrorJobQueue) {
                        $('#jobs-with-errors').show();
                    } else {
                        wrapper.show().siblings('.no-objects-message').hide();
                    }
                }
            };

            table.DataTable(options);
        });
    });
})();
