(function() {
    'use strict';

    $(document).ready(function() {
        var currentPagename = $('#help-content').find('h3').first().text();
        $('#help-navigation').children(':contains("' + currentPagename + '")').addClass('active');
    });
})();
