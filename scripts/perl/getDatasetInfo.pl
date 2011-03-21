#depends on an "infile.txt" which is generated from MySQL using the command:
#select username, name from cbench_dataset;
#Descriptor generation is then run on each dataset.


print "username, datasetname, sdfName, xName, actName, numCompounds, timeCreated, numExternalFolds, numCompoundsExternal, splitType, datasetType, availableDescriptors\n";


open(IFH, "infile.txt") or die "can't open infile";
while($line = <IFH>){
	#read $username and $datasetname from line.	
	$line =~ s/\|//g;
	$line =~ m/(\S+)\s+(\S+)\s+(\S+)\s+(\S+\s+\S+)/;
	$username = $1;
	$datasetname = $2;
	$numCompounds = $3;
	$timeCreated = $4;
	#print "user: $username dataset: $datasetname\n";
	$dir = "$username/DATASETS/$datasetname";
	$visDir = $dir . "/Visualization";
	$success = "true";
	
	$sdfName = "";
	$xName = "";
	$actName = "";
	$numExternalFolds = 0;
	$numCompoundsExternal = 0;
	$splitType = "RANDOM";
	#find SDF, ACT, X, number of folds
	opendir(my $dh, $dir) || next;
	while($filename = readdir($dh)){
		if($filename =~ m/\.sdf$/i){
			$sdfName = $filename;
		}
		if($filename =~ m/\.x$/i){ 
			if(($filename ne "mdlext_mdl0.x" ) && ($filename ne "train_0.x" ) && ($filename ne "ext_0.x" ) && ($filename ne "mdlext_ext0.x" ) ){
				$xName = $filename;
			}
		}
		if($filename =~ m/\.act$/i){
			$actName = $filename;
		}
		if($filename =~ m/fold(\d+)$/){
			if($1 > $numExternalFolds){
				$numExternalFolds = $1;
				$splitType = "NFOLD";
			}
		}	
		if($filename =~ m/ext_0.x$/){
			print `cat $dir/ext_0.x | wc -l > lines.txt`;
			open(FH, "lines.txt");
			$numLines = <FH>;
			chomp($numLines); 
			$numCompoundsExternal = $numLines;
			close FH;
		}	
	}
	
	#determine datasetType
	$datasetType = "";
	if($xName eq ""){
		if($actName eq ""){
			$datasetType = "PREDICTION";
		}
		else{
			$datasetType = "MODELING";
		}
	}
	else{
		if($actName eq ""){
			$datasetType = "PREDICTIONWITHDESCRIPTORS";
		}
		else{
			$datasetType = "MODELINGWITHDESCRIPTORS";
		}
	}

	#find available descriptors
	$availableDescriptors = "";
	opendir(my $descdh, "$dir/Descriptors");
	while($descfilename = readdir($descdh)){
		if($descfilename =~ m/\.sdf.molconnz$/i){
			$availableDescriptors .= "MOLCONNZ ";
		}
		if($descfilename =~ m/\.sdf.dragonH$/i){
			$availableDescriptors .= "DRAGONH ";
		}
		if($descfilename =~ m/\.sdf.dragonNoH$/i){
			$availableDescriptors .= "DRAGONNOH ";
		}
		if($descfilename =~ m/\.sdf.maccs$/i){
			$availableDescriptors .= "MACCS ";
		}
		if($descfilename =~ m/\.sdf.moe2D$/i){
			$availableDescriptors .= "MOE2D";
		}
	}
	
	print "$username, $datasetname, $sdfName, $xName, $actName, $numCompounds, $timeCreated, $numExternalFolds, $numCompoundsExternal, $splitType, $datasetType, $availableDescriptors\n";
}
	