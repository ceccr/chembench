(function() {
    'use strict';

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-prediction').addClass('active');
        $('.compound-structure').popover(Chembench.POPOVER_CONFIG);
        var predictions = $('#predictions');
        predictions.DataTable($.extend({
            'order': [[0, 'asc']],
            'scrollX': true
        }, Chembench.DATATABLE_OPTIONS));
        predictions.show().parents('.dataTables_scrollBody').doubleScroll();
    });
})();
