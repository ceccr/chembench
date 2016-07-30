(function() {
    'use strict';

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-prediction').addClass('active');

        // XXX draw callback must be declared _before_ DataTable() is called
        $('#prediction-values').on('draw.dt', function() {
            $(this).closest('.dataTables_scroll').doubleScroll();
        }).DataTable($.extend({
            'order': [[0, 'asc']],
            'scrollX': true
        }, Chembench.DATATABLE_OPTIONS));
    });
})();
