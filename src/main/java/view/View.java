package view;

import model.Model;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.server.Server;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.util.concurrent.PriorityBlockingQueue;

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

    private static int TIMER_INTERVAL = 80;

    public void showMainStage(Server server) {
        Display display = new Display();
        Shell shell = new Shell(display, SWT.BORDER | SWT.CLOSE | SWT.MIN | SWT.MAX);

        shell.addListener(SWT.Close, event -> server.shutdown());
        shell.setText(Model.getInstance().getPortName());
        Image title = new Image(display,"src\\main\\resources\\pic\\tortuga-icon.png");
        shell.setImage(title);

        final Point shellSize = new Point(1200, 700);
        shell.setMinimumSize(shellSize);
        shell.setSize(shellSize);
        shell.setLayout(new GridLayout(2, true));

        final Text portState = new Text(shell, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.WRAP);
        portState.setLayoutData(new GridData(GridData.FILL_BOTH));

        final List queue = new List(shell, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        queue.setLayoutData(new GridData(GridData.FILL_BOTH));

        shell.open();
        Runnable runnable = new Runnable() {
            public void run() {
                portState.setText(server.getPort().toString());
                queue.removeAll();
                final PriorityBlockingQueue<MaritimeCarrier<Cargo>> shipsQueue = server.getPort().getShipsQueue();
                for (MaritimeCarrier<Cargo> carrier : shipsQueue) {
                    queue.add(carrier.toString());
                }
                queue.redraw();
                display.timerExec(TIMER_INTERVAL, this);
            }
        };
        display.timerExec(TIMER_INTERVAL, runnable);

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        // Kill the timer
        display.timerExec(-1, runnable);
        display.dispose();
    }
}
