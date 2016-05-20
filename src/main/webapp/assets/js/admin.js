(function() {
    'use strict';

    $(document).ready(function() {
        $('.impersonate-user').click(function(e) {
            e.preventDefault();
            $.post(Chembench.LOGIN_URL, {
                'username': $(this).closest('tr').find('.username').text()
            }, function(data) {
                document.open();
                document.write(data);
                document.close();
            });
        });

        var usersTable = $('#users.datatable');
        usersTable.find('tbody').children().each(function() {
            var row = $(this);
            row.find('[name="can-download-descriptors"]').prop('checked',
                    row.find('[name="can-download-descriptors-string"]').val() === 'YES');
            row.find('[name="is-admin"]').prop('checked', row.find('[name="is-admin-string"]').val() === 'YES');
        });

        $('[name="can-download-descriptors"], [name="is-admin"]').change(function() {
            var row = $(this).closest('tr');
            $.post(Chembench.CHANGE_USER_FLAGS_URL, {
                'userName': row.find('.username').text(),
                'canDownloadDescriptors': row.find('[name="can-download-descriptors"]').prop('checked'),
                'isAdmin': row.find('[name="is-admin"]').prop('checked')
            });
        });

        usersTable.DataTable(Chembench.DATATABLE_OPTIONS);
    });
})();
