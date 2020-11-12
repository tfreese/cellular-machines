// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.simulationen.ant.AntView;
import de.freese.simulationen.ball.BallView;
import de.freese.simulationen.gameoflife.GofView;
import de.freese.simulationen.wator.WaTorDiagrammPanel;
import de.freese.simulationen.wator.WaTorView;

/**
 * Hauptfenster der Simulation-Demos.
 *
 * @author Thomas Freese
 */
public class SimulationGUI extends JFrame
{
    /**
     * new ForkJoinPool()
     */
    public static final ForkJoinPool FORK_JOIN_POOL = ForkJoinPool.commonPool();

    /**
     *
     */
    public static final Properties PROPERTIES = new Properties();

    /**
     *
     */
    public static ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE;

    /**
     *
     */
    private static final long serialVersionUID = -8931412063622174282L;

    /**
     *
     */
    static
    {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("simulation.properties"))
        {
            PROPERTIES.load(inputStream);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable != null)
            {
                throwable.printStackTrace();
            }

            System.exit(-1);
        });

        ThreadGroup threadGroup = new ThreadGroup("Simulationen");
        threadGroup.setMaxPriority(Thread.NORM_PRIORITY + 1);

        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Single-Monitor
        // int width = (int) screenSize.getWidth() - 75;
        // int height = (int) screenSize.getHeight() - 75;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); // Multi-Monitor
        // GraphicsDevice gd = ge.getDefaultScreenDevice(); // Haupt-Monitor
        GraphicsDevice[] gds = ge.getScreenDevices();

        int maxWidth = 0;
        int maxHeight = 0;

        for (GraphicsDevice gd : gds)
        {
            Rectangle r = gd.getDefaultConfiguration().getBounds();
            maxWidth = Math.max(maxWidth, (int) r.getWidth());
            maxHeight = Math.max(maxHeight, (int) r.getHeight());

            // DisplayMode displayMode = gd.getDisplayMode();
            // maxWidth = Math.max(maxWidth, displayMode.getWidth());
            // maxHeight = Math.max(maxHeight, displayMode.getHeight());
        }

        int width = maxWidth - 75;
        int height = maxHeight - 75;

        Thread thread = new Thread(threadGroup, () -> {
            SwingUtilities.invokeLater(() -> {
                SimulationGUI demo = new SimulationGUI();
                demo.setSize(width, height);
                // demo.setPreferredSize(new Dimension(width, height));
                demo.setResizable(true);
                // demo.pack();
                demo.initialize();
                demo.setLocationRelativeTo(null);
                // demo.setExtendedState(Frame.MAXIMIZED_BOTH); // Full-Screen
                demo.setVisible(true);
            });
        }, "Simulationen Startup");
        thread.start();
    }

    /**
     * Erstellt ein neues {@link SimulationGUI} Object.
     *
     * @throws HeadlessException Falls was schief geht.
     */
    public SimulationGUI() throws HeadlessException
    {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(8);
    }

    /**
     * Aufbau der GUI.
     */
    public void initialize()
    {
        setTitle("Simulationen");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e)
            {
                SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
                FORK_JOIN_POOL.shutdownNow();
                System.exit(0);
            }
        });

        int fieldWidth = Integer.parseInt(PROPERTIES.getProperty("simulation.field.width", "100"));
        int fieldHeight = Integer.parseInt(PROPERTIES.getProperty("simulation.field.height", "100"));

        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        // Ants
        AntView antView = new AntView();
        antView.initialize(fieldWidth, fieldHeight);
        tabbedPane.addTab("Langton-Ameise", antView.getMainPanel());

        // GoF: Game of Life
        GofView gofView = new GofView();
        gofView.initialize(fieldWidth, fieldHeight);
        tabbedPane.addTab("Game of Life", gofView.getMainPanel());

        // HopAlong
        // HopAlongView hopAlongView = new HopAlongView();
        // hopAlongView.initialize(fieldWidth, fieldHeight);
        // tabbedPane.addTab("Hop along", hopAlongView.getMainPanel());

        // Bälle
        BallView ballView = new BallView();
        ballView.initialize(fieldWidth, fieldHeight);
        tabbedPane.addTab("Bälle", ballView.getMainPanel());

        // WaTor: Water Torus
        WaTorDiagrammPanel waTorDiagrammPanel = new WaTorDiagrammPanel();
        WaTorView waTorView = new WaTorView(waTorDiagrammPanel);
        waTorView.initialize(fieldWidth, fieldHeight);
        tabbedPane.addTab("Water Torus", waTorView.getMainPanel());

        waTorView.getSimulation().addWorldListener(waTorDiagrammPanel);
        tabbedPane.addTab("WaTor-Diagramm", waTorDiagrammPanel);
    }
}
