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
import view.View;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.lang.reflect.Modifier;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Ilya Ivanov
 */
@XmlRootElement
public class Server implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Server.class);

    /** port logger */
    private static final Logger portLog = Logger.getLogger(Port.class);

    /** system that cares about producing, delivering and collecting production */
    private PDCSystem pdcSystem;

    /** sea port */
    @XmlElement
    private Port port;

    /** thread factory for background tasks */
    private final ThreadFactory daemonFactory = Model.getInstance().getDaemonThreadFactory();

    /** scheduled background tasks executor */
    private final ScheduledExecutorService Background = Executors.newSingleThreadScheduledExecutor(daemonFactory);

    /** delay before start background tasks */
    @XmlAttribute
    private long backgroundStartDelay = 100;

    /** background tasks period */
    @XmlAttribute(required = true)
    private long backgroundTasksPeriod = 5000;

    /** number of port, where registry was created */
    @XmlAttribute(required = true)
    private int portNumber;

    /** RMI port name */
    @XmlAttribute(required = true)
    private String portName;

    /** RMI registry */
    private Registry registry;

    /** default constructor for JAXB */
    public Server() {}

    /**
     * @param portName number of port to listen to
     * @param backgroundTasksPeriod period with which tasks will be performed
     * @param portNumber number of remote port
     */
    public Server(String portName, long backgroundTasksPeriod, long backgroundStartDelay, int portNumber) throws IllegalArgumentException {
        if (portNumber < 0 || backgroundTasksPeriod <= 0 || portName == null || portName.isEmpty() || backgroundStartDelay < 0) {
            log.fatal("Server failed on start!");
            throw new IllegalArgumentException("Wrong arguments passed in constructor of Server");
        }
        this.portName = portName;
        this.backgroundTasksPeriod = backgroundTasksPeriod;
        this.backgroundStartDelay = backgroundStartDelay;
        this.portNumber = portNumber;
    }

    {
        // log RMI-events
//        System.setProperty("java.rmi.server.logCalls", "true");
//        System.setProperty("sun.rmi.server.logLevel", "VERBOSE");
    }

    /**
     * Export port to the remote server
     * @throws RemoteException if some remote exceptions occurred
     */
    private void exportRemote() throws RemoteException {
        // exportRemote remote interface to RMI-registry
        final ArrivalService stub = (ArrivalService) port.exportRemote();
        registry = LocateRegistry.createRegistry(portNumber);
        registry.rebind(portName, stub);

        pdcSystem = new CargoPDCSystem(port, port);
    }

    public Port getPort() {
        return port;
    }

    @Override
    public void run() {
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
        Background.scheduleAtFixedRate(() -> portLog.warn(gson.toJson(port)), backgroundStartDelay, backgroundTasksPeriod, TimeUnit.MILLISECONDS);

        // run delivery system
        Background.scheduleAtFixedRate(pdcSystem, backgroundStartDelay + backgroundTasksPeriod / 4, backgroundTasksPeriod / 2, TimeUnit.MILLISECONDS);
        log.info("PDCSystem was started\n");

        // run port in the current thread
        port.run();
        log.info("Port was launched");
    }

    /** Shutdown server. */
    public void shutdown() {
        try {
            // unbind port
            registry.unbind(portName);

            // force unexport
            port.unexportRemote();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }

        Background.shutdown();

        log.info("Server successfully shut down");
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            // create JAXB context and initializing Marshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(Server.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // specify the location and name of xml file to be read
            File XMLFile = new File("./src/main/resources/xml/server.xml");

            // this will create Java object - port from the XML file
            server = (Server) jaxbUnmarshaller.unmarshal(XMLFile);
            server.exportRemote();
        } catch (JAXBException | RemoteException e) {
            log.fatal("Unable to instantiate server", e);
            throw new RuntimeException("Unable to instantiate server", e);
        }
        // run server in the current thread
        server.run();
        // show GUI
        View.getInstance().showMainStage(server);
    }
}