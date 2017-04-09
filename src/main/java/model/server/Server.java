package model.server;

import model.Model;
import model.server.interfaces.remote.ArrivalService;
import model.server.pdcs.CargoPDCSystem;
import model.server.pdcs.PDCSystem;
import model.server.portsystem.Port;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ilya Ivanov
 */
public class Server implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Server.class);

    /** system that cares about producing, delivering and collecting production */
    private PDCSystem pdcSystem;

    /** sea port */
    private Port port;

    /** scheduled PDCSystem executor */
    private final ScheduledExecutorService PDCSExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * @param portName number of port to listen to
     * @throws IOException if an socket I/O error occur
     */
    public Server(String portName) throws IOException, JAXBException {
        // create JAXB context and initializing Marshaller
        // create JAXB context and initializing Marshaller
        JAXBContext jaxbContext = JAXBContext.newInstance(Port.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        // specify the location and name of xml file to be read
        File XMLFile = new File("./src/main/resources/xml/port.xml");

        // this will create Java object - country from the XML file
        port = (Port) jaxbUnmarshaller.unmarshal(XMLFile);

        pdcSystem = new CargoPDCSystem(port, port);
        // log RMI-events
        System.setProperty("java.rmi.server.logCalls", "true");
        System.setProperty("sun.rmi.server.logLevel", "VERBOSE");

        ArrivalService stub = (ArrivalService) UnicastRemoteObject.exportObject(port, 0);
        final Registry registry = LocateRegistry.createRegistry(2002);
        registry.rebind(portName, stub);
    }

    @Override
    public void run() {
        // run delivery system
        PDCSExecutor.scheduleAtFixedRate(pdcSystem, 1, 5, TimeUnit.SECONDS);
        log.info("PDCSystem was started");
        // 5 sec period logger

        // run port in the current thread
        port.run();
        log.info("Port was started");
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server(Model.getInstance().getPortName());
        } catch (IOException | JAXBException e) {
            log.fatal("Server failed on start!", e);
            e.printStackTrace();
            assert false : "Bug report: Server failed!";
        }
        // run server in the current thread
        server.run();
    }
}
