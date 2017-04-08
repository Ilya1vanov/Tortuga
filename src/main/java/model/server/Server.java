package model.server;

import model.Model;
import model.server.pdcs.CargoPDCSystem;
import model.server.pdcs.PDCSystem;
import model.server.port.Port;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.rmi.Naming;
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
    private final PDCSystem pdcSystem;

    /** sea port */
    private final Port port;

    /** scheduled PDCSystem executor */
    private final ScheduledExecutorService PDCSExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * @param portName number of port to listen to
     * @throws IOException if an socket I/O error occur
     */
    public Server(String portName) throws IOException {
        port = new Port(portName);
        pdcSystem = new CargoPDCSystem(port, port);

        Naming.rebind("rmi://localhost/" + portName, port);
    }

    @Override
    public void run() {
        // run delivery system
        PDCSExecutor.scheduleWithFixedDelay(pdcSystem, 1, 10, TimeUnit.SECONDS);
        // run port in the current thread
        port.run();
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server(Model.getInstance().getPortName());
        } catch (IOException e) {
            // fail on start
            // report bug
            e.printStackTrace();
            assert false : "Bug report: Server failed!";
        }
        // run server in the current thread
        server.run();
    }
}
