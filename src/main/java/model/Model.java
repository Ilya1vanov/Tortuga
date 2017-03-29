package model;

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
}
