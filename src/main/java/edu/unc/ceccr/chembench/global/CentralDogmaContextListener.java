package edu.unc.ceccr.chembench.global;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import edu.unc.ceccr.chembench.jobs.CentralDogma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

public class CentralDogmaContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(CentralDogmaContextListener.class);

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
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            logger.error("Failed to stop MySQL abandoned connection cleanup thread", e);
        }
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.error("Failed to deregister SQL driver", e);
            }
        }
    }
}
