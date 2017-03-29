package view;

/**
 * MCV View class.
 * Created by Илья on 30.03.2017.
 */
public class View {
    private static View ourInstance = new View();

    public static View getInstance() {
        return ourInstance;
    }

    private View() {}
}
