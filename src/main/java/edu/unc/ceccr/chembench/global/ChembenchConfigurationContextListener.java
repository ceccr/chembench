package edu.unc.ceccr.chembench.global;

import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.nio.file.Paths;

public class ChembenchConfigurationContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(ChembenchConfigurationContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // read $CHEMBENCH_HOME, then append config directory / filename
        String ENV_CHEMBENCH_HOME = null;
        try {
            ENV_CHEMBENCH_HOME = System.getenv("CHEMBENCH_HOME");
        } catch (SecurityException e) {
            throw new RuntimeException("Couldn't read $CHEMBENCH_HOME environment variable", e);
        }
        if (ENV_CHEMBENCH_HOME == null) {
            throw new RuntimeException("Environment variable $CHEMBENCH_HOME doesn't exist");
        }

        String configFilePath = Paths.get(ENV_CHEMBENCH_HOME, "config", "systemConfig.xml").toString();
        try {
            Utility.readBuildDateAndSystemConfig(configFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't parse system config", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
