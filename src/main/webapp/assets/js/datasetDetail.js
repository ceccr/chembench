(function() {
    'use strict';

    function composeRow(object) {
        var imageParams = {
            user: $('input#username').val(),
            projectType: 'dataset',
            compoundId: object.compoundId,
            datasetName: $('input#dataset-name').val()
        };
        var image = '<img src="imageServlet?' + $.param(imageParams) +
                    '" class="compound-structure img-thumbnail" width="125" height="125" alt="Compound structure">';

        return [object.compoundId, image, object.activityValue];
    }

    function updatePages(clicked) {
        var parent = clicked.closest('ul.pagination');

        // update new active fold number
        parent.children('li').removeClass('active');
        clicked.closest('li').addClass('active');

        // enable or disable previous and next buttons
        var foldNumber = parseInt(clicked.text(), 10);
        var firstFold = parseInt($('li.first-fold > a', parent).text(), 10);
        var lastFold = parseInt($('li.last-fold > a', parent).text(), 10);

        var previous = parent.children('li.previous');
        var next = parent.children('li.next');
        if (foldNumber === firstFold) {
            previous.addClass('disabled');
            next.removeClass('disabled');
        } else if (foldNumber === lastFold) {
            next.addClass('disabled');
            previous.removeClass('disabled');
        } else {
            previous.removeClass('disabled');
            next.removeClass('disabled');
        }
    }

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-datasets').addClass('active');
        $('td.name').each(function() {
            var cell = $(this);
            cell.html(cell.text().split('_').join('_<wbr>'));
        });

        $('.compound-structure').popover(Chembench.THUMBNAIL_CONFIG);

        $('.modi-help').popover({
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
        });

        $('ul.pagination a').click(function(e) {
            e.preventDefault();

            var clicked = $(this);
            var target = clicked.attr('href');
            var table = $('#folds').find('table.datatable').DataTable();
            $.get(target).success(function(data) {
                // replace table body with new fold data
                table.clear();
                for (var i = 0; i < data.length; i++) {
                    table.row.add(composeRow(data[i]));
                }
                table.$().find('.compound-structure').popover(Chembench.THUMBNAIL_CONFIG);
                updatePages(clicked);
                table.draw();
            }).fail(function() {
                bootbox.alert('Error retrieving fold data.');
            });
        });

        $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
            if (e.currentTarget.hash === '#folds') {
                var firstFold = $('ul.pagination > li:not(".previous, .next")').first();
                firstFold.children('a').click();
            } else if (e.currentTarget.hash === '#heatmap') {
                swfobject.embedSWF('assets/swf/heatmap.swf',
                        'heatmapSwfContainer',
                        '924',
                        '924',
                        '9.0.28',
                        false,
                        Chembench.Heatmap.flashvars,
                        Chembench.Heatmap.params,
                        Chembench.Heatmap.attributes);
            }
        });

        $('table.datatable').DataTable(Chembench.DATATABLE_OPTIONS);
    });
})();
