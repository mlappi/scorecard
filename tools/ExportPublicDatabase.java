import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Comparator;
import java.util.List;

public final class ExportPublicDatabase {

    private static final List<String> SOURCE_SUFFIXES = List.of(
            "properties", "script", "data", "backup", "log");
    private static final List<String> SNAPSHOT_SUFFIXES = List.of(
            "properties", "script", "data", "backup");

    private ExportPublicDatabase() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: ExportPublicDatabase <source-prefix> <destination-prefix>");
            System.exit(2);
        }

        Path sourcePrefix = Path.of(args[0]).toAbsolutePath().normalize();
        Path destinationPrefix = Path.of(args[1]).toAbsolutePath().normalize();
        Path sourceProperties = withSuffix(sourcePrefix, "properties");
        if (!Files.isRegularFile(sourceProperties)) {
            throw new IllegalArgumentException("HSQL database not found: " + sourceProperties);
        }
        if (Files.exists(withSuffix(sourcePrefix, "lck"))) {
            throw new IllegalStateException(
                    "Database is still open. Stop the local application and try again: "
                            + withSuffix(sourcePrefix, "lck"));
        }

        Path temporaryDirectory = Files.createTempDirectory("scorecard-public-seed-");
        Path temporaryPrefix = temporaryDirectory.resolve("devdb");
        try {
            for (String suffix : SOURCE_SUFFIXES) {
                copyIfPresent(withSuffix(sourcePrefix, suffix), withSuffix(temporaryPrefix, suffix));
            }

            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            String url = "jdbc:hsqldb:file:" + temporaryPrefix.toString().replace('\\', '/');
            try (Connection connection = DriverManager.getConnection(url, "sa", "");
                    Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE PLAYER SET EMAIL = NULL, EXTERNAL_USER_ID = NULL");
                statement.executeUpdate("UPDATE SCORECARD SET EXTERNAL_COMPETITOR_ID = NULL");
                statement.execute("SHUTDOWN SCRIPT");
            }

            Files.createDirectories(destinationPrefix.getParent());
            for (String suffix : SOURCE_SUFFIXES) {
                Files.deleteIfExists(withSuffix(destinationPrefix, suffix));
            }
            for (String suffix : SNAPSHOT_SUFFIXES) {
                copyIfPresent(withSuffix(temporaryPrefix, suffix), withSuffix(destinationPrefix, suffix));
            }

            System.out.println("Public database snapshot updated: " + destinationPrefix.getParent());
        } finally {
            deleteRecursively(temporaryDirectory);
        }
    }

    private static Path withSuffix(Path prefix, String suffix) {
        return prefix.resolveSibling(prefix.getFileName() + "." + suffix);
    }

    private static void copyIfPresent(Path source, Path destination) throws IOException {
        if (Files.isRegularFile(source)) {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteRecursively(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }
}
