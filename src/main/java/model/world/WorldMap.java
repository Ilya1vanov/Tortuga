package model.world;

import org.apache.log4j.Logger;

/**
 * @author Ilya Ivanov
 */
public class WorldMap {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(WorldMap.class);

    private static WorldMap ourInstance = new WorldMap();

    public static WorldMap getInstance() {
        return ourInstance;
    }

    private WorldMap() {}


}
