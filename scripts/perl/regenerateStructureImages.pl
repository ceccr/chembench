#use something like find . | grep sdf | grep Visualization > individual-sdfs.txt first.
open(FH, "individual-sdfs.txt");
while($line = <FH>){
    chop($line);
    $line =~ m/(.*)Structures\/(\S+).sdf/;

    $path = $1;
    $basename = $2;

    #$x = substr($basename,-1,1) . "\n";

    #if($x == 0){

        $cmd = "molconvert -2 jpeg:w300,Q50 ";
        $cmd .= $line . " -o ";
        $cmd .= $path . "Sketches/" . $basename . ".jpg\n";
        print "running: $cmd";
        print `$cmd`;
    #}
}

