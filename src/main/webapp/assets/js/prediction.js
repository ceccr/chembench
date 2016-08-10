(function() {
    'use strict';

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
        $('#nav-button-prediction').addClass('active');

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

            var selectedModelIds = fetchAllBodyRows('#prediction-model-selection').find(':checked').siblings(
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
            var selectedModelIds = fetchAllBodyRows('#prediction-model-selection').find(':checked').siblings(
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
            var goings = Array();
            var originalJobName = jobName.val();
            $.each(selectedDatasets, function(index, dataset) {
                form.find('#selectedDatasetId').val(dataset.id);
                // change jobName only if we need to (multiple datasets to predict)
                if (selectedDatasets.length > 1) {
                    jobName.val(originalJobName + ' ' + dataset.name);
                }

                var going = $.post(form.attr('action') + '?' + form.serialize()).done(function( ) {

                });
                goings.push(going);

            });

            $.when.apply($,goings).then(function(){
                window.location = Chembench.MYBENCH_URL;
            });

        });

        $('#prediction-model-selection, #prediction-dataset-selection').find('table').DataTable().one('draw',
                function() {
                    var table = $(this);
                    var tableType = (table.parents('#prediction-model-selection').length) ? 'model' : 'dataset';
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
                                $('#make-prediction, #model-list-message').hide();
                            } else if (tableType === 'dataset') {
                                $('#predict-dataset, #dataset-list-message').hide();
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
    });
})();
