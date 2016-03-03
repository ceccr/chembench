(function() {
    'use strict';

    $(document).ready(function() {
        $('.pagination a').click(function(e) {
            e.preventDefault();
            var target = $(this);
            var targetParent = target.parent();
            var pagination = target.parents('.pagination');
            var active = pagination.children('.active');
            var lastIndex = pagination.children().last().index();
            if (targetParent.hasClass('previous')) {
                if (!targetParent.hasClass('disabled')) {
                    $(pagination.children().get(active.index() - 1)).find('a').click();
                }
            } else if (targetParent.hasClass('next')) {
                if (!targetParent.hasClass('disabled')) {
                    $(pagination.children().get(active.index() + 1)).find('a').click();
                }
            } else {
                pagination.children().removeClass('active');
                targetParent.addClass('active');
                if (targetParent.index() === 1) {
                    pagination.children('.previous').addClass('disabled');
                } else if (targetParent.index() === lastIndex - 1) {
                    pagination.children('.next').addClass('disabled');
                } else {
                    pagination.children('.previous, .next').removeClass('disabled');
                }
                var nav = pagination.parent('nav');
                var table = nav.siblings('table.datatable');
                if (!table.attr('data-prepared')) {
                    Chembench.prepareAjaxDatatable(table, {'order': [[0, 'desc']]});
                }
                table.on('preXhr.dt', function(e) {
                    var colspan = table.find('th').size();
                    table.children('tbody').html('<tr><td class="text-center" colspan="' + colspan + '">Loading...</td></tr>');
                });
                var baseUrl = nav.siblings('.fold-base-url').val();
                var currentFold = target.text();
                var params = {
                    'id': nav.siblings('.object-id').val(),
                    'foldNumber': currentFold
                };
                var isYRandomInput = nav.siblings('.is-y-random');
                if (isYRandomInput.exists()) {
                    params['isYRandom'] = isYRandomInput.val();
                }
                var foldUrl = nav.siblings('.fold-url');
                foldUrl.val(baseUrl + '?' + $.param(params)).change();
            }
        });

        $('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
            var pagination = $(e.currentTarget.hash).find('.pagination');
            if (pagination.exists()) {
                var firstFold = pagination.children(':not(".previous, .next")').first();
                firstFold.children('a').click();
            }
        });

        $('.fold-url').change(function(e) {
            var foldUrlInput = $(this);
            var dataTable = foldUrlInput.siblings().find('table.datatable').DataTable();
            dataTable.clear();
            dataTable.ajax.url(foldUrlInput.val()).load();
        });
    });
})();
