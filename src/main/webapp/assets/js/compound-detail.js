(function() {
    'use strict';

    $('.compound-structure').click(function() {
        var img = $(this);
        var compoundName = img.closest('tr').find('.name').text();
        var datasetId = $('#dataset-id').val();

        var content = $('<div class="row text-center">' + '<div id="2d-structure" class="col-xs-6">' + '<img src="' +
                        img.attr('src') + '" width="260" height="260"></div>' +
                        '<div id="3d-structure" class="col-xs-6"><canvas id="3d-structure-canvas"></canvas></div>' +
                        '</div>');
        $.get(window.Chembench.COMPOUND_3D_URL + '?' + $.param({
                'compoundName': compoundName,
                'datasetId': datasetId
            }), function(data) {
            var canvas = new ChemDoodle.TransformCanvas3D('3d-structure-canvas', 260, 260);
            canvas.specs.set3DRepresentation('Ball and Stick');
            var molecule = ChemDoodle.readMOL(data, 1);
            canvas.loadMolecule(molecule);
        });
        bootbox.alert({
            message: content,
            title: "Compound: " + compoundName
        });
    });
})();
