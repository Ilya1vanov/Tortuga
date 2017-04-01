/**
 * Created by Илья on 26.03.2017.
 */

import javafx.application.Application;
import javafx.stage.Stage;
import model.cargo.Gold;
import model.cargo2.Cargo;
import model.procuring.customer.CargoCustomer;
import model.procuring.customer.Customer;

import java.util.Observable;
import java.util.Observer;

public class Tortuga extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Customer customer = new CargoCustomer();
        customer.makeAnOrder();
    }

    @Override
    public void stop() {

    }
}
