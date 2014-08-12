package edu.unc.ceccr.global;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.utilities.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

public class ChembenchServletContextListener implements ServletContextListener {
    private static Logger logger
            = Logger.getLogger(ChembenchServletContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // read $CHEMBENCH_HOME, then append config directory / filename
        String ENV_CHEMBENCH_HOME = null;
        try {
            ENV_CHEMBENCH_HOME = System.getenv("CHEMBENCH_HOME");
        } catch (SecurityException e) {
            logger.error("Couldn't read $CHEMBENCH_HOME environment variable", e);
        }
        if (ENV_CHEMBENCH_HOME == null) {
            logger.error("Environment variable $CHEMBENCH_HOME doesn't exist");
        }

        String configFilePath = Paths.get(ENV_CHEMBENCH_HOME, "config", "systemConfig.xml").toString();
        try {
            Utility.readBuildDateAndSystemConfig(configFilePath);
        } catch (IOException e) {
            logger.error("Couldn't parse system config", e);
        }

        // start up the job queues
        CentralDogma.getInstance();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (InterruptedException e) {
            logger.error("Shutdown of MySQL abandoned connection cleanup thread failed", e);
        }

        Set<Thread> jobThreads = CentralDogma.getInstance().getThreads();
        for (Thread t : jobThreads) {
            t.stop();
        }

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.error("Driver deregistration failed", e);
            }
        }
    }
}
