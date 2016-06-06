import edu.unc.ceccr.chembench.utilities.ParseConfigurationXML;
import org.junit.BeforeClass;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {
    private static boolean hasInitialized = false;

    @BeforeClass
    public static void init() {
        if (!hasInitialized) {
            Path systemConfigXml = Paths.get(System.getenv("CHEMBENCH_HOME"), "config", "systemConfig.xml");
            ParseConfigurationXML.initializeConstants(systemConfigXml.toString());
            hasInitialized = true;
        }
    }
}
