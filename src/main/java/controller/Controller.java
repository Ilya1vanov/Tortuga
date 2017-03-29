package controller;

/**
 * MVC Controller class.
 * Created by Илья on 30.03.2017.
 */
public class Controller {
    private static Controller ourInstance = new Controller();

    public static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {}
}
