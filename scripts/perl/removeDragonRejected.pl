#removes molecules that contain errors from an SDF.

if($#ARGV != 2){
	print "Usage: dragonErrorFile.err input.sdf output.sdf";
}

$index = 0;
@badMolecules = ();
open(ERRFILE, $ARGV[0]);
while($line = <ERRFILE>){
	if($line =~ m/Molecule\s+(\S+) '(\S+)'\s+rejected/){
		#print "$2\n";
		$badMolecules[$index] = $2;
		$index++;
	}
}
close(ERRFILE);

open(SDFILE, $ARGV[1]);
open(SDFOUT, ">$ARGV[2]");

$compound = "";
$compound_name = "";
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
			if($badMolecules[$i] eq $compound_name){
				$i = $index;
				$isBadMolecule = 1;
			}
		}

		if($isBadMolecule == 0){
			print SDFOUT $compound;
		}

		$compound_name = "";
		$compound = "";
	}


}