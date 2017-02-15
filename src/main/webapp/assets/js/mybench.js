(function() {
    'use strict';

    function clearRefreshingState() {
        var button = $('#jobs-queue-refresh');
        button.removeClass('active disabled').blur().find('#refresh-text').text('Refresh');
    }

    function refreshJobQueues() {
        var button = $('#jobs-queue-refresh');
        button.addClass('active disabled').find('#refresh-text').text('Refreshing...');

        var xhrs = [];
        var start = Date.now();
        $('.job-table').DataTable().one('xhr', function(e, settings, json, xhr) {
            xhrs.push(xhr);
        }).ajax.reload();

        $.when.apply(this, xhrs).done(function() {
            var elapsed = Date.now() - start;
            if (elapsed < 1000) {
                setTimeout(clearRefreshingState, 1000 - elapsed);
            } else {
                clearRefreshingState();
            }
        });
    }

    function fetchAllBodyRows(node) {
        var selector = $(node);
        var dataTable = selector.hasClass('datatable') ? selector : selector.find('.datatable');
        return dataTable.DataTable().rows().nodes().to$();
    }

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-mybench').addClass('active');

        // if given a hash, activate that tab on page load
        var url = document.location.toString();
        if (url.match('#')) {
            $('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
        }

        // change page hash when a tab is clicked
        $('.nav-tabs a').on('shown.bs.tab', function(e) {
            window.location.hash = e.target.hash;
        });

        $('#jobs-queue-refresh').click(function() {
            refreshJobQueues();
        });

        var intervalSelect = $('#autorefresh-interval');
        var refreshTask = setInterval(refreshJobQueues, intervalSelect.val() * 1000);
        intervalSelect.change(function() {
            var newInterval = parseInt($(this).val());
            if (refreshTask) {
                clearInterval(refreshTask);
            }
            if (newInterval !== 0) {
                refreshTask = setInterval(refreshJobQueues, newInterval * 1000);
            }
        });

        $('form#delete-dataset, form#delete-model, form#delete-predictions').submit(function(e) {
            e.preventDefault();
            var form = $(this);
            var id = form.attr('id');
            // var errorTotal = 'Error deleting ';
            // var errorDeleting = false

            var selection;
            if (id == 'delete-dataset'){
                selection = '#mybench-dataset-selection';
            }
            else if (id == 'delete-model'){
                selection = '#mybench-model-selection';
            }
            else if (id == 'delete-predictions'){
                selection = '#mybench-predictions-selection';
            }

            fetchAllBodyRows(selection).filter(':has(:checked)').each(function() {
                var row = $(this);
                var name = row.find('.object-name').text();
                // var objectType = 'dataset';
                var link = row.find('.delete a');

                $.post(link.attr('href'), function() {
                        row.fadeOut(400, function() {
                            $(selection).find('table').DataTable().row(row).remove().draw();
                        });
                }).fail(function(xhr) {
                    var errors = $(xhr.responseText).find('#errors').html();
                    // errorTotal += name;
                    // errorDeleting = true;
                    bootbox.alert('Error deleting ' + name + ':<br><br>' + errors);
                });

            });


            // if (errorDeleting){
            //     bootbox.alert(errorTotal);
            // }
            $.post(form.attr('action') + '?' + form.serialize());
        });

        $('#mybench-dataset-selection, #mybench-model-selection, #mybench-predictions-selection').find('table').DataTable().one('draw',
            function() {
                var table = $(this);
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
                    var id = table.attr('id');
                    var row = checkbox.closest('tr');
                    var objectName = row.find('td').find('.object-name').first().text();

                    var objectList;
                    if (id == 'DataTables_Table_4'){
                        objectList =  $('#mybench-dataset-list');
                    }
                    else if (id == 'DataTables_Table_5'){
                        objectList =  $('#mybench-model-list');
                    }
                    else if (id == 'DataTables_Table_6'){
                        objectList =  $('#mybench-predictions-list');
                    }

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
                });
            });
    });
})();
