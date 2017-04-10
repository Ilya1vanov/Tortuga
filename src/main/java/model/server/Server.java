package model.server;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gson.AmountSerializer;
import gson.GSONExclude;
import gson.NumberPropertiesSerializer;
import javafx.beans.property.IntegerProperty;
import model.Model;
import model.server.interfaces.remote.ArrivalService;
import model.cargo2.CargoPDCSystem;
import model.server.pdcsystem.PDCSystem;
import model.server.portsystem.Port;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Ilya Ivanov
 */
public class Server implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Server.class);

    /** port logger */
    private static final Logger portLog = Logger.getLogger(Port.class);

    /** system that cares about producing, delivering and collecting production */
    private PDCSystem pdcSystem;

    /** sea port */
    private Port port;

    /** thread factory for background tasks */
    private final ThreadFactory daemonFactory = Model.getInstance().getDaemonThreadFactory();

    /** scheduled background tasks executor */
    private final ScheduledExecutorService Background = Executors.newSingleThreadScheduledExecutor(daemonFactory);

    /** RMI port name */
    private final String portName;

    /** RMI registry */
    private final Registry registry;

    /**
     * @param portName number of port to listen to
     * @throws IOException if an socket I/O error occur
     */
    public Server(String portName) throws IOException, JAXBException {
        this.portName = portName;
        // create JAXB context and initializing Marshaller
        JAXBContext jaxbContext = JAXBContext.newInstance(Port.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        // specify the location and name of xml file to be read
        File XMLFile = new File("./src/main/resources/xml/port.xml");

        // this will create Java object - port from the XML file
        port = (Port) jaxbUnmarshaller.unmarshal(XMLFile);

        pdcSystem = new CargoPDCSystem(port, port);
        // log RMI-events
        System.setProperty("java.rmi.server.logCalls", "true");
        System.setProperty("sun.rmi.server.logLevel", "VERBOSE");

        // export remote interface to RMI-registry
        ArrivalService stub = (ArrivalService) UnicastRemoteObject.exportObject(port, 0);
        registry = LocateRegistry.createRegistry(2002);
        registry.rebind(portName, stub);
    }

    @Override
    public void run() {
        // run delivery system
        Background.scheduleAtFixedRate(pdcSystem, 100, 5000, TimeUnit.MILLISECONDS);
        log.info("PDCSystem was started\n");
        // 5 sec period port logger
        final Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return fieldAttributes.getAnnotation(GSONExclude.class) != null;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return aClass.getAnnotation(GSONExclude.class) != null;
            }
        })
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .registerTypeAdapter(IntegerProperty.class, new NumberPropertiesSerializer())
                .registerTypeAdapter(Amount.class, new AmountSerializer())
                .setPrettyPrinting().serializeNulls().create();
        Background.scheduleAtFixedRate(() -> portLog.info(gson.toJson(port)), 2600, 5000, TimeUnit.MILLISECONDS);
        // run port in the current thread
        port.run();
        log.info("Port was started");
    }

    /** Shutdown server. */
    public void shutdown() throws RemoteException, NotBoundException {
        // unbind port
        registry.unbind(portName);

        // force unexport
        UnicastRemoteObject.unexportObject(port, true);

        Background.shutdown();
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
