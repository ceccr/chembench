package edu.unc.ceccr.chembench.jobs;

import java.util.Scanner;

public class LsfJobStatus {
    //the object representing each line of a sbatch output

    public String jobid = "";
    public String partition = "";
    public String job_name = "";
    public String user = "";
    public String stat = "";
    public String running_time = "";
    public String time_limit = "";
    public String nodes = "";
    public String nodeList = "";



//           JOBID PARTITION     NAME     USER    STATE       TIME TIME_LIMI  NODES NODELIST(REASON)
//           8685136   general cbench_w    ceccr  R       1:48     1:00:00  1 c0808
//           8685135   general cbench_w    ceccr  R       1:59     1:00:00  1 c0803


//    Job state, compact form: PD (pending), R (running), CA (cancelled),
//    CF(configuring), CG (completing), CD (completed), F (failed), TO (timeout),
//    NF (node failure), RV (revoked) and SE (special exit state).

    LsfJobStatus(String slurmLine) {
        if (!slurmLine.trim().isEmpty()) {
            Scanner s = new Scanner(slurmLine);
            jobid = s.next();
            partition = s.next();
            job_name = s.next();
            user = s.next();
            stat = s.next();
            running_time = s.next();
            time_limit = s.next();
            nodes = s.next();
            nodeList = s.next();
            s.close();
        }
    }

}
