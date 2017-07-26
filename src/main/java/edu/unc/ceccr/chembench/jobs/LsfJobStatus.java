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

//    JOBID PARTITION     NAME     USER ST       TIME  NODES NODELIST(REASON)
//           8685136   general cbench_w    ceccr  R       1:48      1 c0808
//           8685135   general cbench_w    ceccr  R       1:59      1 c0803
//           8685134   general cbench_w    ceccr  R       2:09      1 c0824
//           8685133   general cbench_w    ceccr  R       2:20      1 c0934
//           8685131   general cbench_w    ceccr  R       2:30      1 c0929

//    Job state, compact form: PD (pending), R (running), CA (cancelled),
//    CF(configuring), CG (completing), CD (completed), F (failed), TO (timeout),
//    NF (node failure), RV (revoked) and SE (special exit state).

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
