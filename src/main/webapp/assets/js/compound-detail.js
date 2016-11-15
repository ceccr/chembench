(function() {
    'use strict';

    $('.compound-structure').click(function() {
        var img = $(this);
        var compoundName = img.closest('tr').find('.name').text();
        var datasetId = $('#dataset-id').val();

        var content = $('<div class="row text-center">' + '<div id="2d-structure" class="col-xs-6">' + '<canvas id="view-canvas"></canvas></div>' +
                        '<div id="3d-structure" class="col-xs-6"><canvas id="3d-structure-canvas"></canvas></div>' +
                        '</div>');

        $.get(window.Chembench.COMPOUND_2D_URL + '?' + $.param({
                'compoundName': compoundName,
                'datasetId': datasetId
            }), function(data){
            var canvas2 = new ChemDoodle.ViewerCanvas('view-canvas',260,260);
            //canvas2.specs.bonds_width_2D = .6 * 10;
            //canvas2.specs.bonds_saturationWidth_2D = .18 * 10;
            //canvas2.specs.bonds_hashSpacing_2D = 2.5 * 10;
            //canvas2.specs.atoms_font_size_2D = 10 * 10;
            //canvas2.specs.atoms_font_families_2D = ['Helvetica', 'Arial', 'sans-serif'];
            //data = 'Molecule Name\n  CHEMDOOD08070920033D 0   0.00000     0.00000     0\n[Insert Comment Here]\n 14 15  0  0  0  0  0  0  0  0  1 V2000\n   -0.3318    2.0000    0.0000   O 0  0  0  1  0  0  0  0  0  0  0  0\n   -0.3318    1.0000    0.0000   C 0  0  0  1  0  0  0  0  0  0  0  0\n   -1.1980    0.5000    0.0000   N 0  0  0  1  0  0  0  0  0  0  0  0\n    0.5342    0.5000    0.0000   C 0  0  0  1  0  0  0  0  0  0  0  0\n   -1.1980   -0.5000    0.0000   C 0  0  0  1  0  0  0  0  0  0  0  0\n   -2.0640    1.0000    0.0000   C 0  0  0  4  0  0  0  0  0  0  0  0\n    1.4804    0.8047    0.0000   N 0  0  0  1  0  0  0  0  0  0  0  0\n    0.5342   -0.5000    0.0000   C 0  0  0  1  0  0  0  0  0  0  0  0\n   -2.0640   -1.0000    0.0000   O 0  0  0  1  0  0  0  0  0  0  0  0\n   -0.3318   -1.0000    0.0000   N 0  0  0  1  0  0  0  0  0  0  0  0\n    2.0640   -0.0000    0.0000   C 0  0  0  2  0  0  0  0  0  0  0  0\n    1.7910    1.7553    0.0000   C 0  0  0  4  0  0  0  0  0  0  0  0\n    1.4804   -0.8047    0.0000   N 0  0  0  1  0  0  0  0  0  0  0  0\n   -0.3318   -2.0000    0.0000   C 0  0  0  4  0  0  0  0  0  0  0  0\n  1  2  2  0  0  0  0\n  3  2  1  0  0  0  0\n  4  2  1  0  0  0  0\n  3  5  1  0  0  0  0\n  3  6  1  0  0  0  0\n  7  4  1  0  0  0  0\n  4  8  2  0  0  0  0\n  9  5  2  0  0  0  0\n 10  5  1  0  0  0  0\n 10  8  1  0  0  0  0\n  7 11  1  0  0  0  0\n  7 12  1  0  0  0  0\n 13  8  1  0  0  0  0\n 13 11  2  0  0  0  0\n 10 14  1  0  0  0  0\nM  END\n> <DATE>\n07-08-2009\n';
            var mol2 = ChemDoodle.readMOL(data);
            console.log("can we see this");
            canvas2.loadMolecule(mol2);

        });


        $.get(window.Chembench.COMPOUND_3D_URL + '?' + $.param({
                'compoundName': compoundName,
                'datasetId': datasetId
            }), function(data) {

            //canvas2.specs.atoms_displayTerminalCarbonLabels_2D = true;
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
