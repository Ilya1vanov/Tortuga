package model;

import com.sun.javafx.geom.IllegalPathStateException;
import org.apache.log4j.Logger;
import temps.TempDirectory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * MVC Model class.
 * Created by Илья on 30.03.2017.
 */
public class Model {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Model.class);

    private static Model ourInstance = new Model();

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {}

    /* current log session temp dir */
    private TempDirectory logSessionDir = new TempDirectory();

    /* logs path */
    private final String LOGS_PATH = "logs";

    /** port name */
    private final String PORT_NAME = "Tortuga";

    /** daemon thread factory */
    private final DaemonThreadFactory factory = new DaemonThreadFactory();

    private final class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        }
    }

    public File getLogSessionDir() {
        try {
            return logSessionDir.getTempDir(LOGS_PATH);
        } catch (IOException e) {
            log.error(LOGS_PATH + " - log directory does not exist", e);
            throw new IllegalPathStateException(LOGS_PATH + " - log directory does not exist");
        }
    }

    public String getPortName() {
        return PORT_NAME;
    }

    /** @return daemon thread factory */
    public ThreadFactory getDaemonThreadFactory() {
        return factory;
    }
}
