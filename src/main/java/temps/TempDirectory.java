package temps;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provide creation of temp directories. Directory exists until virtual machine terminates.
 * @author Ilya Ivanov
 */
public class TempDirectory {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(TempDirectory.class);

    /*  temporary directory path */
    File tempDir;

    /** Default constructor. */
    public TempDirectory() {}

    /**
     * Constructs object and invoke {@link #getTempDir(String, boolean)}.
     * {@code deleteOnExit} is defaults to false.
     * @see #getTempDir(String, boolean) getTempDir()
     */
    public TempDirectory(String dir) throws IOException {
        this.getTempDir(dir, false);
    }

    /**
     * Constructs object and invoke {@link #getTempDir(String, boolean) getTempDir()}.
     * @see #getTempDir(String, boolean) getTempDir()
     */
    public TempDirectory(String dir, boolean deleteOnExit) throws IOException {
        this.getTempDir(dir, deleteOnExit);
    }

    /**
     * Invoke {@link #getTempDir(String, boolean)}.
     * {@code deleteOnExit} is defaults to false.
     * @see #getTempDir(String, boolean) getTempDir()
     */
    public File getTempDir(String dir) throws IOException {
        return getTempDir(dir, false);
    }

    /**
     * Creates a new directory that did not exist before in the specified directory.
     * Repeated invocation doesn't create new instance. Just return previously created dir.
     * Returns {@link File} object of created temp directory. This method is thread safe.
     * @param dir the path to directory in which to create the directory
     * @param deleteOnExit if true directory will be deleted when the virtual machine terminates
     * @return {@link File} object of created temp directory.
     * @throws IOException if an I/O error occurs or dir does not exist
     */
    public synchronized File getTempDir(String dir, boolean deleteOnExit) throws IOException {
        if (tempDir == null) {
            final Path path = Files.createTempDirectory(Paths.get(dir), null);
            tempDir = new File(path.toString());
            if (deleteOnExit)
                tempDir.deleteOnExit();
            log.info("Temp directory " + tempDir.getPath() + " successfully created.");
        }

        return tempDir;
    }
}