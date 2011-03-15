#depends on an "infile.txt" which is generated from MySQL using the command:
#select username, name from cbench_dataset;
#Descriptor generation is then run on each dataset.

    open(FH, "infile.txt") or die "can't open infile";
while($line = <FH>){
    #read $username and $datasetname from line.                                                                                        
    print "$line\n";
    $line =~ s/\|//g;
    $line =~ m/(\S+)\s+(\S+)/;
    $username = $1;
    $datasetname = $2;
    print "user: $username dataset: $datasetname\n";
    $dir = "$username/DATASETS/$datasetname";
    $success = "true";
    opendir(my $dh, $dir) || die "can't open $dir\n";
    if($dh){                                                                                                                          
	while($filename = readdir($dh)){
	    if($filename =~ m/sdf$/){
		$mzfilename = $filename . ".molconnz";
                $dragonfilename = $filename . ".dragon";
                $maccsfilename = $filename . ".maccs";
                $moefilename = $filename . ".moe2D";

		print `mkdir $dir/Descriptors/`;

		#create descriptors of each type. capture error output too.

		#MOLCONNZ
		$cmd = "molconnz /usr/local/ceccr/ParameterFiles/MZ405Prediction.dat $dir/$filename $dir/Descriptors/" . $mzfilename . " > $dir/Descriptors/molconnz.out 2> $dir/Descriptors/molconnz.err";
		print "$cmd\n";
		print `$cmd`;

		#DRAGON
		$stupidfile = $dir . "/Descriptors/fileContainingSDFname";
		open(OFH, ">$stupidfile");
		print OFH  $dir . "/" . $filename . "\n";
		close(OFH);

		$dragonscriptfile = $dir . "/Descriptors/" . "dragon-script.txt";
		open(OFH, ">$dragonscriptfile");
		print OFH "DRAGON script Ver 2\n" . 
"/d GetB1 All /PCno\n" . 
"/d GetB2 All /PCno\n" . 
"/d GetB3 All /PCno\n" . 
"/d GetB4 All /PCno\n" . 
"/d GetB5 All /PCno\n" . 
"/d GetB6 All /PCno\n" . 
"/d GetB7 All /PCno\n" . 
"/d GetB8 All /PCno\n" . 
"/d GetB9 All /PCno\n" . 
"/d GetB10 All /PCno\n" . 
"/d GetB11 None /PCno\n" . 
"/d GetB12 None /PCno\n" . 
"/d GetB13 None /PCno\n" . 
"/d GetB14 None /PCno\n" . 
"/d GetB15 None /PCno\n" . 
"/d GetB16 None /PCno\n" . 
"/d GetB17 All /PCno\n" . 
"/d GetB18 All /PCno\n" . 
"/d GetB19 None /PCno\n" . 
"/d GetB20 All /PCno\n" . 
"/d GetB21 All /PCno\n" . 
"/d GetB22 All /PCno\n" . 
"/fm $stupidfile -f4 -i2 -Hy -2D\n" . 
"/fy None\n" . 
"/fo /usr/local/ceccr/workflow-users/$dir/Descriptors/" . $dragonfilename . " -f1 -k -m -999\n";
		close(OFH);
		$cmd = "/usr/local/ceccr/dragon/dragonX -s $dir/Descriptors/dragon-script.txt  > $dir/Descriptors/dragon.out 2> $dir/Descriptors/dragon.err";
		print "$cmd\n";
		print `$cmd`;

		#MOE2D
		$cmd = "moe2D.sh $dir/$filename $dir/Descriptors/" .  $moefilename . " /usr/local/ceccr/mmlsoft/SVL_DIR/batch_sd_2Ddesc.svl  > $dir/Descriptors/moe2D.out 2> $dir/Descriptors/moe2D.err";
                print "$cmd\n";
                print `$cmd`;


		#MACCS
		$cmd = "maccs.sh $dir/$filename $dir/Descriptors/" .  $maccsfilename . " /usr/local/ceccr/mmlsoft/SVL_DIR/batch_sd_MACCSFP.svl > $dir/Descriptors/maccs.out 2> $dir/Descriptors/maccs.err";
                print "$cmd\n";
                print `$cmd`;

	    }
	}                                                                                                                     } 
    closedir $dh;
}
