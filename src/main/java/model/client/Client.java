package model.client;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import model.Model;
import model.cargo2.Cargo;
import model.client.ship.Ship;
import model.server.remote.ArrivalService;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import static org.jscience.physics.amount.Constants.c;

/**
 * @author Ilya Ivanov
 */
public class Client implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Client.class);

    /** host to access to */
    private final String PORT_NAME;

    /** wandering ship */
    private final Ship ship;

    public Client(String portName) throws IOException, NotBoundException {
        this.PORT_NAME = portName;
        ArrivalService<Cargo> service = (ArrivalService<Cargo>) Naming.lookup(PORT_NAME);

        ship = new Ship("some ship", service);
    }

    @Override
    public void run() {
        // run in current thread
        ship.run();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Client client = null;
        try {
            client = new Client(Model.getInstance().getPortName());
        } catch (IOException | NotBoundException e) {
            // fail on start
            // report bug
            e.printStackTrace();
            assert false : "Bug report: Client failed!";
        }
        // run in current thread
        client.run();
    }
}
