(function() {
    'use strict';

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-modeling').addClass('active');

        $('.compound-structure').popover(Chembench.THUMBNAIL_CONFIG);

        var tabLinks = $('a[data-toggle="tab"]');
        tabLinks.on('shown.bs.tab', function(e) {
            var tab = $(e.currentTarget.hash);

            // XXX datatable _must_ be initialized before click event is fired
            var table = tab.find('table.datatable');
            if (table.exists() && !table.attr('data-prepared')) {
                var direction = (e.currentTarget.hash === "#external-validation") ? 'asc' : 'desc';
                Chembench.prepareAjaxDatatable(table, {'order': [[0, direction]]});
            }

            var foldLinks = tab.find('.fold-navigation');
            var allFolds = foldLinks.find('.all-folds');
            if (allFolds.exists()) {
                allFolds.click();
            } else {
                foldLinks.find('li:not(.previous,.next)').find('a').first().click();
            }
        });
        tabLinks.first().trigger('shown.bs.tab');

        var externalValidationSection = $('#external-validation');
        externalValidationSection.find('.fold-navigation').find('a').click(function(e) {
            e.preventDefault();
            var target = $(this);
            var nav = target.parents('.fold-navigation');
            var pagination = nav.find('.pagination');
            var newIndex;
            if (target.hasClass('all-folds')) {
                newIndex = 0;
                target.addClass('active');
                pagination.find('.previous, .next').addClass('disabled');
                pagination.find('.active').removeClass('active');
            } else {
                nav.find('.all-folds').removeClass('active');
                newIndex = Chembench.updatePages(target);
            }
            externalValidationSection.find('[data-fold-number]').hide();
            externalValidationSection.find('[data-fold-number=' + newIndex + ']').show();
        });

        var modelTabs = $('#trees, #y-randomized-trees, #models, #y-randomized-models');
        modelTabs.find('.fold-navigation').find('a').click(function(e) {
            e.preventDefault();
            var nav = $(this).parents('.pagination').parent('nav');
            var currentIndex = nav.find('.pagination').find('.active').index();
            var newIndex = Chembench.updatePages(this);
            if (newIndex !== currentIndex) {
                var table = nav.parent().find('table.datatable');
                table.on('preXhr.dt', function(e) {
                    var colspan = table.find('th').size();
                    table.children('tbody').html('<tr><td class="text-center" colspan="' + colspan +
                                                     '">Loading...</td></tr>');
                });
                var baseUrl = nav.siblings('.fold-base-url').val();
                var params = {
                    'id': nav.siblings('.object-id').val(),
                    'foldNumber': newIndex
                };
                var isYRandomInput = nav.siblings('.is-y-random');
                if (isYRandomInput.exists()) {
                    params['isYRandom'] = isYRandomInput.val();
                }
                var dataTable = table.DataTable();
                dataTable.clear();
                dataTable.ajax.url(baseUrl + '?' + $.param(params)).load();
            }
        });
    });
})();
