package edu.unc.ceccr.chembench.global;

import edu.unc.ceccr.chembench.jobs.CentralDogma;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Set;

public class CentralDogmaContextListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // start up the job queues
        CentralDogma.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Set<Thread> jobThreads = CentralDogma.getInstance().getThreads();
        for (Thread t : jobThreads) {
            t.interrupt();
        }
    }
}
