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
	
	LsfJobStatus(String bjobsLine){
		if(! bjobsLine.trim().isEmpty()){
			Scanner s = new Scanner(bjobsLine);
			String jobid = s.next();
			String user = s.next();
			String stat = s.next();
			String queue = s.next();
			String from_host = s.next();
			String exec_host = "";
			if(! stat.equals("PEND")){
				exec_host = s.next();
			}
			String job_name = s.next();
			String submit_time = "";
			String temp;
			while((temp = s.next()) != null){
				submit_time += temp;
			}
		}
	}
	
}