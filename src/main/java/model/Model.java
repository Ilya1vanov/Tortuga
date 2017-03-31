package model;

import com.sun.javafx.geom.IllegalPathStateException;
import temps.TempDirectory;

import java.io.File;
import java.io.IOException;

/**
 * MVC Model class.
 * Created by Илья on 30.03.2017.
 */
public class Model {
    private static Model ourInstance = new Model();

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {}

    /* current log session temp dir */
    private TempDirectory logSessionDir = new TempDirectory();

    /* logs path */
    private static String LOGS_PATH = "logs";

    public File getLogSessionDir() {
        try {
            return logSessionDir.getTempDir(LOGS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalPathStateException(LOGS_PATH + " log directory does not exist");
        }
    }
}
