(function() {
    'use strict';

    function updateModelingForm(dataset) {
        $('#uploaded-descriptors-scaled').val(dataset.hasBeenScaled);
        $('#num-compounds').val(dataset.numCompound);
        $('#activity-type').val(dataset.modelType);
        $('input[name="selectedDatasetId"]').val(dataset.id);

        if (dataset.numCompound < 40) {
            $('#small-dataset-warning').show();
        } else {
            $('#small-dataset-warning').hide();
        }
    }

    window.jsmeOnLoad = function() {
        document.JME = new JSApplet.JSME('jsme', '348px', '300px');
    };

    function fetchAllBodyRows(node) {
        var selector = $(node);
        var dataTable = selector.hasClass('datatable') ? selector : selector.find('.datatable');
        return dataTable.DataTable().rows().nodes().to$();
    }


    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-mcra').addClass('active');

        // if given a hash, activate that tab on page load
        var url = document.location.toString();
        if (url.match('#')) {
            $('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
        }

        // change page hash when a tab is clicked
        $('.nav-tabs a').on('shown.bs.tab', function(e) {
            window.location.hash = e.target.hash;
        });


        $('#jsme-clear').click(function() {
            document.JME.reset();
        });

        $('#jsme-smiles-predict').click(function() {
            $('#smiles').val(document.JME.smiles()).closest('form').submit();
        });


        $('form#predict-compound').submit(function(e) {
            e.preventDefault();
            var form = $(this);
            if (!form.find('#smiles').val()) {
                return false;
            }

            // TODO spinny
            form.find('button[type="submit"]').text('Predicting...').addClass('disabled');

            var selectedModelIds = fetchAllBodyRows('#modeling-dataset-selection').find(':checked').siblings(
                '[name="id"]').map(function() {
                return $(this).val();
            }).get();
            form.find('#compound-selectedPredictorIds').val(selectedModelIds.join(' '));

            var url = form.attr('action') + '?' + form.serialize();
            $.get(url, function(data) {
                var predictionResults = $('#prediction-results');
                predictionResults.find('.help-block').remove();
                predictionResults.prepend(data);
            }).fail(function() {
                bootbox.alert('Error occurred during prediction.');
            }).always(function() {
                form.find('button[type="submit"]').text('Predict').removeClass('disabled');
            });
        });

        $('form#predict-dataset').submit(function(e) {
            e.preventDefault();
            var form = $(this);
            var jobName = form.find('#jobName');
            var selectedModelIds = fetchAllBodyRows('#modeling-dataset-selection').find(':checked').siblings(
                '[name="id"]').map(function() {
                return $(this).val();
            }).get();
            form.find('#dataset-selectedPredictorIds').val(selectedModelIds.join(' '));

            // TODO spinny
            form.find('button[type="submit"]').text('Predicting...').addClass('disabled');

            // get both dataset ids and names so we can append dataset names to jobName
            var selectedDatasets = [];
            fetchAllBodyRows('#prediction-dataset-selection').filter(':has(:checked)').each(function() {
                var row = $(this);
                var dataset = {};
                dataset.id = row.find('[name="id"]').val();
                dataset.name = row.find('.object-name').text();
                selectedDatasets.push(dataset);
            });
            if (!jobName.val() || !selectedDatasets.length) {
                return false;
            }

            var originalJobName = jobName.val();
            $.each(selectedDatasets, function(index, dataset) {
                form.find('#selectedDatasetId').val(dataset.id);
                // change jobName only if we need to (multiple datasets to predict)
                if (selectedDatasets.length > 1) {
                    jobName.val(originalJobName + ' ' + dataset.name);
                }
                $.post(form.attr('action') + '?' + form.serialize());
            });

            window.location = Chembench.MYBENCH_URL;
        });


        $('#prediction-dataset-selection').find('table').DataTable().one('draw',
            function() {
                var table = $(this);
                var tableType = 'dataset';
                var allBodyRows = fetchAllBodyRows(table);

                table.find('thead').find('input[type="checkbox"]').click(function() {
                    var globalCheckedState = $(this).prop('checked');
                    allBodyRows.find('input[type="checkbox"]').prop('checked', globalCheckedState).change();
                });

                allBodyRows.click(function(e) {
                    var checkbox = $(this).find('input[type="checkbox"]');
                    checkbox.prop('checked', !(checkbox.prop('checked'))).change();
                }).find('a').click(function(e) {
                    e.stopPropagation();
                });

                allBodyRows.find('input[type="checkbox"]').click(function(e) {
                    e.stopPropagation();
                }).change(function() {
                    var checkbox = $(this);
                    var row = checkbox.closest('tr');
                    var objectName = row.find('td').find('.object-name').first().text();

                    var objectList = (tableType === 'model') ? $('#model-list') : $('#dataset-list');
                    var objectsMatchingName = objectList.find('li').filter(function() {
                        return $(this).text().trim() === objectName;
                    });
                    if (checkbox.prop('checked')) {
                        var match = /(danger|warning|success)/.exec(row.attr('class'));
                        if (match !== null) {
                            var color = match[1];
                            row.data('oldClass', color);
                            row.removeClass(color);
                        }
                        row.addClass('info');
                        if (objectsMatchingName.length === 0) {
                            objectList.append('<li>' + objectName + '</li>');
                        }
                    } else {
                        row.removeClass('info');
                        row.addClass(row.data('oldClass'));
                        objectsMatchingName.remove();
                    }

                    var count = allBodyRows.find('input[type="checkbox"]:checked').length;
                    var warning = (tableType === 'model') ? $('#minimum-model-warning') :
                        $('#minimum-dataset-warning');
                    var counter = (tableType === 'model') ? $('#selected-model-count') :
                        $('#selected-dataset-count');
                    counter.text(count);
                    if (count === 0) {
                        warning.show();
                        if (tableType === 'model') {
                           // $('#make-prediction, #model-list-message').hide();
                        } else if (tableType === 'dataset') {
                           // $('#predict-dataset, #dataset-list-message').hide();
                        }
                    } else {
                        warning.hide();
                        if (tableType === 'model') {
                            $('#make-prediction, #model-list-message').show();
                        } else if (tableType === 'dataset') {
                            $('#predict-dataset, #dataset-list-message').show();
                        }
                    }
                });
            });


        var table = $('#modeling-dataset-selection').find('table.datatable');
        table.DataTable().on('init', function() {
            
        }).on('draw', function() {
			table.find('input[type="radio"]').change(function() {
                var rowSelector = $(this).closest('tr');
                var dataset = rowSelector.closest('table').DataTable().row(rowSelector).data();
                updateModelingForm(dataset);
            });
            // XXX don't use find('tbody').find('tr'), or non-active pages won't be modified
            var allBodyRows = table.DataTable().rows().nodes().to$();
            allBodyRows.click(function() {
                $(this).find('input[type="radio"]').prop('checked', 'checked').change();
            }).find('a').click(function(e) {
                e.stopPropagation();
            });

            table.find('input[type="radio"]').change(function() {
                var radio = $(this);
                var selectedRow = radio.closest('tr');
                allBodyRows.each(function() {
                    var row = $(this).removeClass('info');
                    row.addClass(row.data('oldClass'));
                    if (!row.is(selectedRow)) {
                        row.find('input[type="radio"]').prop('checked', false);
                    }
                });

                var match = /(danger|warning|success)/.exec(selectedRow.attr('class'));
                if (match !== null) {
                    var color = match[1];
                    selectedRow.data('oldClass', color);
                    selectedRow.removeClass(color);
                }
                selectedRow.addClass('info');
            });
        });
    });
})();
