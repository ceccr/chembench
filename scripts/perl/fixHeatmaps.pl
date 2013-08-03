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
	$visDir = $dir . "/Visualization";
	$success = "true";
	
	opendir(my $dh, $dir) || next;
	while($filename = readdir($dh)){
		if($filename =~ m/sdf$/){
			$sdfName = $filename;
			
			#if matrix files exist, fix them
			$matFileExists = "false";
			opendir(my $visdh, $visDir) || next;
			while($visfilename = readdir($visdh)){
				print "$visfilename\n";
				if($visfilename =~ m/mah.mat$/){
					$matFileExists = "true";
					$cmd = "mv $visDir/$visfilename $visDir/" . "$sdfName" . "_mah.mat";
					print "$cmd\n";
					print `$cmd`;
				}
				if($visfilename =~ m/tan.mat$/){
					$matFileExists = "true";
					$cmd = "mv $visDir/$visfilename $visDir/" . "$sdfName" . "_tan.mat";
					print "$cmd\n";
					print `$cmd`;
				}
				if($visfilename =~ m/mah.xml$/){
					$matFileExists = "true";
					$cmd = "mv $visDir/$visfilename $visDir/" . "$sdfName" . "_mah.xml";
					print "$cmd\n";
					print `$cmd`;
				}
				if($visfilename =~ m/tan.xml$/){
					$matFileExists = "true";
					$cmd = "mv $visDir/$visfilename $visDir/" . "$sdfName" . "_tan.xml";
					print "$cmd\n";
					print `$cmd`;
				}
			}
			
			if($matFileExists eq "false"){
				#if no matrix files exist, generate them
				opendir(my $visdh, $visDir) || next;
				while($visfilename = readdir($visdh)){
					if($visfilename =~ m/maccs$/){
						$cmd = "convert_maccs_to_X2.pl $visDir/$visfilename $visDir/$sdfName" . ".x";
						print "$cmd\n";
						print `$cmd`;

						$cmd = "run_heatmap_tree.sh $visDir/$sdfName" . ".x $visDir/$sdfName" . "_tan.mat $visDir/$sdfName" . "_tan.xml e";
						print "$cmd\n";
						print `$cmd`;

						$cmd = "run_heatmap_tree.sh $visDir/$sdfName" . ".x $visDir/$sdfName" . "_mah.mat $visDir/$sdfName" . "_mah.xml m";
						print "$cmd\n";
						print `$cmd`;
					}
				}
			}

		}
	}
	

	
}
	