package edu.unc.ceccr.chembench.jobs;

import java.util.Scanner;

public class LsfJobStatus {
    //the object representing each line of a sbatch output

    public String jobid = "";
    public String user = "";
    public String stat = "";
    public String partition = "";
    public String nodes = "";
    public String nodeList = "";
    public String job_name = "";
    public String submit_time = "";

    //744782  ceccr   PEND  week       chembench-d             bsubKnn.sh Mar 31 02:47
    //744771  ceccr   DONE  week       chembench-d bc14-n04    bsubKnn.sh Mar 31 02:27
    //744779  ceccr   DONE  week       chembench-d bc16-n06    bsubKnn.sh Mar 31 02:36
    //744780  ceccr   DONE  week       chembench-d bc16-n06    bsubKnn.sh Mar 31 02:39

//    JOBID      PARTITION    NAME        USER ST       TIME        NODES NODELIST(REASON)
//    3600559    bigmem       oh.ortho    isai  R       1-19:29:18      1 t0602
//    3622655    general      oh.compr    isai  R       1-00:16:05      1 c0922

    LsfJobStatus(String bjobsLine) {
        if (!bjobsLine.trim().isEmpty()) {
            Scanner s = new Scanner(bjobsLine);
            jobid = s.next();
            partition = s.next();
            job_name = s.next();
            user = s.next();
            stat = s.next();
            submit_time = "";
            nodes = s.next();
            nodeList = s.next();
            s.close();
        }
    }

}
