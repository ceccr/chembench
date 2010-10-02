#removes molecules that contain errors from an SDF.

if($#ARGV != 2){
	print "Usage: molconnErrorFile.err input.sdf output.sdf";
}

$index = 0;
@badMolecules = ();
open(ERRFILE, $ARGV[0]);
$moleculeResult = "";
while($line = <ERRFILE>){
	$moleculeResult .= $line;
	if($line =~ m/Mol\. (\d+)/){
		$moleculeNum = $1 - 1;
		#finished reading molecule.
		if($moleculeResult =~ m/No paths/){
			$badMolecules[$index] = $moleculeNum;
			$index++;
		}
		$moleculeResult = $line;
	}
}
close(ERRFILE);

open(SDFILE, $ARGV[1]);
open(SDFOUT, ">$ARGV[2]");

$compound = "";
$compound_name = "";
$compoundNum = 0;
while($line = <SDFILE>){
	
	$compound = $compound . $line;

	if($compound_name eq "" && $line ne ""){
		$line =~ m/(\s*)(\S*)(\s*)/;
		$compound_name = $2;
		#print $compound_name . "\n";
	}
	if($line =~ m/\$\$\$\$/){
		#end of compound

		$isBadMolecule = 0;
		for($i = 0; $i < $index; $i++){
			if($badMolecules[$i] == $compoundNum){
				$i = $index;
				$isBadMolecule = 1;
			}
		}

		if($isBadMolecule == 0){
			print SDFOUT $compound;
		}

		$compound_name = "";
		$compound = "";
		$compoundNum++;
	}


}