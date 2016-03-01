(function() {
    'use strict';

    function resetForm() {
        // reset form values
        var originalDescription = $('#description-wrapper').find('.value').text();
        var originalPaperReference = $('#paper-reference-wrapper').find('.value').html();
        var datasetForm = $('form#dataset-form');

        datasetForm.find('textarea[name="datasetDescription"]').val(originalDescription);
        datasetForm.find('textarea[name="datasetReference"]').val(originalPaperReference);
    }

    function toggleForm() {
        $('form#dataset-form, #description-reference-buttons').toggle();
        $('#description-reference-text, button#edit-description-reference').toggle();
    }

    function composeRow(object) {
        var imageParams = {
            user: $('input#username').val(),
            projectType: 'dataset',
            compoundId: object.compoundId,
            datasetName: $('input#dataset-name').val()
        };
        var image = '<img src="imageServlet?' + $.param(imageParams) +
                    '" class="img-thumbnail" width="125" height="125" alt="Compound structure">';

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

        $('form#dataset-form, #description-reference-buttons').hide();

        var descriptionWrapper = $('#description-wrapper');
        var description = descriptionWrapper.find('.value');
        if (description.text()) {
            descriptionWrapper.find('.placeholder').hide();
        }

        var paperReferenceWrapper = $('#paper-reference-wrapper');
        var paperReference = paperReferenceWrapper.find('.value');
        // autolink urls within paper reference
        if (paperReference.text()) {
            paperReference.html(paperReference.text().autoLink({target: '_blank'}));
            paperReferenceWrapper.find('.placeholder').hide();
        }

        $('td.name').each(function() {
            var cell = $(this);
            cell.html(cell.text().split('_').join('_<wbr>'));
        });

        $('button#edit-description-reference').click(function() {
            toggleForm();
        });

        $('button#cancel-changes').click(function() {
            resetForm();
            toggleForm();
        });

        $('button#save-changes').click(function() {
            var form = $('form#dataset-form');
            var newDescription = $('textarea[name="datasetDescription"]').val();
            var newPaperReference = $('textarea[name="datasetReference"]').val();
            $.ajax({
                url: form.attr('action'),
                method: 'POST',
                data: form.serialize()
            }).success(function() {
                if (newDescription) {
                    description.show();
                    descriptionWrapper.find('.placeholder').hide();
                    description.text(newDescription);
                } else {
                    description.hide();
                    descriptionWrapper.find('.placeholder').show();
                }
                if (newPaperReference) {
                    paperReference.show();
                    paperReferenceWrapper.find('.placeholder').hide();
                    // XXX needs html() and not text() for autoLink to work
                    paperReference.html(newPaperReference.autoLink({target: '_blank'}));
                } else {
                    paperReference.hide();
                    paperReferenceWrapper.find('.placeholder').show();
                }

                toggleForm();
            }).fail(function() {
                bootbox.alert('Error updating dataset.');
            });
        });

        var thumbnailPopoverConfig = {
            html: true,
            template: '<div class="popover popover-image" role="tooltip">' +
                      '<div class="arrow"></div><div class="popover-content"></div></div>',
            content: function() {
                return '<img src="' + $(this).attr('src') + '">';
            },
            trigger: 'hover',
            placement: 'right'
        };
        $('.img-thumbnail').popover(thumbnailPopoverConfig);

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
                table.$().find('.img-thumbnail').popover(thumbnailPopoverConfig);
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