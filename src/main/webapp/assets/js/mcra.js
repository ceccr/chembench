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

    function updateModelingForm(dataset, inputName) {
        $('input[name="'+inputName+'"]').val(dataset.id);
        //$('input[name="selectedModelingDatasetId"]').val(dataset.id);
    }


    function updateDataTable(table, inputName) {
        table.find('input[type="radio"]').change(function() {
            var rowSelector = $(this).closest('tr');
            var dataset = rowSelector.closest('table').DataTable().row(rowSelector).data();
            updateModelingForm(dataset, inputName);
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
            if (!form.find('#smiles').val() || !form.find('input[name="selectedModelingDatasetId"]').val()) {
                return false;
            }

            form.find('button[type="submit"]').text('Predicting...').addClass('disabled');

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

            if (!form.find('input[name="selectedPredictingDatasetId"]').val()
                || !form.find('input[name="selectedModelingDatasetId"]').val()) {
                return false;
            }

            var jobName = form.find('#jobName');

            form.find('button[type="submit"]').text('Predicting...').addClass('disabled');

            // get both dataset ids and names so we can append dataset names to jobName
            // var selectedDatasets = [];
            // fetchAllBodyRows('#prediction-dataset-selection').filter(':has(:checked)').each(function() {
            //     var row = $(this);
            //     var dataset = {};
            //     dataset.id = row.find('[name="id"]').val();
            //     dataset.name = row.find('.object-name').text();
            //     selectedDatasets.push(dataset);
            // });
            // if (!jobName.val() || !selectedDatasets.length) {
            //     return false;
            // }

            var originalJobName = jobName.val();
            // $.each(selectedDatasets, function(index, dataset) {
            //     form.find('#selectedModelingDatasetId').val(dataset.id);
            //     // change jobName only if we need to (multiple datasets to predict)
            //     if (selectedDatasets.length > 1) {
            //         jobName.val(originalJobName + ' ' + dataset.name);
            //     }
              //  $.post(form.attr('action') + '?' + form.serialize());
          //  });

            var url = form.attr('action') + '?' + form.serialize();
            $.get(url, function(data) {
                var predictionResults = $('#dataset-prediction-results');
                predictionResults.find('.help-block').remove();
                predictionResults.prepend(data);
            }).fail(function() {
                bootbox.alert('Error occurred during prediction.');
            }).always(function() {
                form.find('button[type="submit"]').text('Predict').removeClass('disabled');
            });

           // window.location = Chembench.MYBENCH_URL;
        });


        var table = $('#modeling-dataset-selection').find('table.datatable');
        table.DataTable().on('init', function() { }).on('draw', function(){
            updateDataTable(table, "selectedModelingDatasetId");
        });

        var table2 = $('#prediction-dataset-selection').find('table.datatable');
        table2.DataTable().on('init', function() { }).on('draw', function(){
            updateDataTable(table2, "selectedPredictingDatasetId");
        });
    });
})();
