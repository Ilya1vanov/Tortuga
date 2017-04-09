package model.client;

import model.Model;
import model.cargo2.Cargo;
import model.client.ship.Ship;
import model.server.interfaces.remote.ArrivalService;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.plist.ParseException;
import org.apache.log4j.Logger;
import properties.PropertiesLoader;

import java.io.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Ilya Ivanov
 */
public class Client implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Client.class);

    /** host to access to */
    private final String PORT_NAME;

    /** thread pool of ships */
    private final ExecutorService shipPool;

    /** ships configuration */
    private final LinkedHashMap<String, Configuration> configuration;

    /** remote service */
    private final ArrivalService<Cargo> service;

    public Client(String portName) throws IOException, NotBoundException, ParseException, ConfigurationException {
        this.PORT_NAME = portName;
        service = (ArrivalService<Cargo>) Naming.lookup(PORT_NAME);

        configuration = PropertiesLoader.loadXMLConfiguration(new File(""), "name", "ship");
        shipPool = Executors.newFixedThreadPool(configuration.size());
    }

    @Override
    public void run() {
        for (String name : configuration.keySet()) {
//            shipPool.submit(new Ship(service, name));
        }
    }

    public static void main(String[] args) {
        Client client = null;
        try {
            client = new Client(Model.getInstance().getPortName());
        } catch (IOException | NotBoundException | ParseException | ConfigurationException e) {
            log.fatal("Client failed of start!", e);
            e.printStackTrace();
            assert false : "Bug report: Client failed!";
        }
        // run in current thread
        client.run();
    }
}
