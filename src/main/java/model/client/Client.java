package model.client;

import model.cargo2.Cargo;
import model.client.ship.Ship;
import model.server.interfaces.remote.ArrivalService;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.unit.SI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ilya Ivanov
 */
@XmlRootElement
public class Client implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Client.class);

    /** host to access to */
    @XmlAttribute
    private String portName;

    /** list of ships running at the same time */
    @XmlElement(name="ship", required = true)
    private List<Ship> ships = new ArrayList<>();

    /** thread pool of ships */
    private ExecutorService shipPool;

    /** RMI registry */
    private Registry registry;

    /** number of port, where registry was created */
    @XmlAttribute
    private int portNumber;

    /** default constructor for JAXB */
    public Client() {}

    /**
     * Constructor for manual instantiation
     * @param portName name of the port
     * @throws IllegalArgumentException if portName is null or empty string
     */
    public Client(String portName) {
        if (portName == null || portName.isEmpty())
            throw new IllegalArgumentException("Illegal arguments passed in Client constructor");
        this.portName = portName;
    }

    {
        // log RMI-events
        System.setProperty("java.rmi.server.logCalls", "true");
        System.setProperty("sun.rmi.server.logLevel", "VERBOSE");
    }

    /** Set up client settings */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        try {
            importRemote();
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            log.fatal("Client failed on start!", e);
            throw new RuntimeException("Client failed on start", e);
        }
    }

    /** Import remote interface. */
    private ArrivalService<Cargo> importRemote() throws RemoteException, NotBoundException, MalformedURLException {
        registry = LocateRegistry.getRegistry( portNumber);

        ArrivalService<Cargo> service = (ArrivalService<Cargo>) registry.lookup(portName);
        shipPool = Executors.newFixedThreadPool(ships.size());
        return service;
    }

    @Override
    public void run() {
        for (Ship ship : ships) {
            final ArrivalService<Cargo> service;
            try {
                service = importRemote();
                ship.setService(service);
                shipPool.submit(ship);
            } catch (RemoteException | NotBoundException | MalformedURLException e) {
                log.fatal("Client server failed while starting", e);
                throw new RuntimeException("Client server failed while starting", e);
            }
        }
    }

    public static void main(String[] args) {
        Client client = null;
        try {
            // create JAXB context and initializing Marshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(Client.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // specify the location and name of xml file to be read
            File XMLFile = new File("./src/main/resources/xml/client.xml");

            // this will create Java object - port from the XML file
            client = (Client) jaxbUnmarshaller.unmarshal(XMLFile);
        } catch (JAXBException e) {
            log.fatal("Unable to instantiate client", e);
            e.printStackTrace();
        }
        // run in current thread
        client.run();
    }
}
