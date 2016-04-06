(function() {
    'use strict';

    $(document).ready(function() {
        $('.nav-list li').removeClass('active');
        $('#nav-button-prediction').addClass('active');
        $('.compound-structure').popover(Chembench.THUMBNAIL_CONFIG);
        $('#predictions').DataTable($.extend({'order': [[0, 'asc']]}, Chembench.DATATABLE_OPTIONS));
    });
})();
