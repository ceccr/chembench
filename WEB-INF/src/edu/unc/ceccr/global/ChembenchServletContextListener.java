package edu.unc.ceccr.global;

import com.opensymphony.xwork2.ActionContext;
import edu.unc.ceccr.jobs.CentralDogma;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.utilities.ActiveUser;
import edu.unc.ceccr.utilities.FileAndDirOperations;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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
        // TODO destroy queues & db connections
    }
}
