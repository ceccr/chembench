import edu.unc.ceccr.chembench.workflows.descriptors.GenerateDescriptors;
import junitx.framework.FileAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class GenerateDescriptorsTest extends BaseTest {
    private static final Path resourcesDirPath = Paths.get("src", "test", "resources");
    private static final Path sdfFilePath = resourcesDirPath.resolve("hdac59.sdf");
    private static Path tempDirPath;

    @BeforeClass
    public static void setUpClass() throws IOException {
        tempDirPath = Files.createTempDirectory(GenerateDescriptorsTest.class.getName());
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.walkFileTree(tempDirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test
    public void generateCdkDescriptors() throws Exception {
        Path tempSdfPath = Files.copy(sdfFilePath, tempDirPath.resolve(sdfFilePath.getFileName()));
        GenerateDescriptors.generateCdkDescriptors(tempSdfPath.toString(), "cdk.out");
        FileAssert.assertEquals(tempDirPath.resolve("Descriptors").resolve("cdk.out").toFile(),
                resourcesDirPath.resolve("hdac59.cdk").toFile());
    }
}
