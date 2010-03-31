package edu.unc.ceccr.jobs;

import java.util.Scanner;

public class LsfJobStatus{
	//the object representing each line of a bjobs output
	
	public String jobid = "";
	public String user = "";
	public String stat = "";
	public String queue = "";
	public String from_host = "";
	public String exec_host = "";
	public String job_name = "";
	public String submit_time = "";

	//744782  ceccr   PEND  week       chembench-d             bsubKnn.sh Mar 31 02:47
	//744771  ceccr   DONE  week       chembench-d bc14-n04    bsubKnn.sh Mar 31 02:27
	//744779  ceccr   DONE  week       chembench-d bc16-n06    bsubKnn.sh Mar 31 02:36
	//744780  ceccr   DONE  week       chembench-d bc16-n06    bsubKnn.sh Mar 31 02:39


	LsfJobStatus(String bjobsLine){
		if(! bjobsLine.trim().isEmpty()){
			Scanner s = new Scanner(bjobsLine);
			jobid = s.next();
			user = s.next();
			stat = s.next();
			queue = s.next();
			from_host = s.next();
			exec_host = "";
			if(! stat.equals("PEND")){
				exec_host = s.next();
			}
			job_name = s.next();
			submit_time = "";
			while(s.hasNext()){
				submit_time += s.next();
			}
		}
	}
	
}