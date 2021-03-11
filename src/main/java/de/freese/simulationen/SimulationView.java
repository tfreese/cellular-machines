// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.simulationen.model.Simulation;

/**
 * BasisView fuer die Simulationen.
 *
 * @author Thomas Freese
 * @param <S> Konkreter Typ der Welt
 */
public class SimulationView<S extends Simulation>
{
    /**
     *
     */
    private JPanel buttonPanel;

    /**
     *
     */
    private JButton buttonStart;

    /**
     *
     */
    private JPanel controlPanel;

    /**
     * 40 ms = 25 Bilder/Sekunde
     */
    private int delay = 40;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private JPanel mainPanel;

    /**
     *
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     *
     */
    private S simulation;

    /**
     * Erstellt ein neues {@link SimulationView} Object.
     */
    protected SimulationView()
    {
        super();
    }

    /**
     * @return {@link JPanel}
     */
    private JPanel getButtonPanel()
    {
        if (this.buttonPanel == null)
        {
            this.buttonPanel = new JPanel();
            this.buttonPanel.setLayout(new BorderLayout());

            this.buttonStart = new JButton("Start");
            this.buttonStart.addActionListener(event -> start());
            this.buttonPanel.add(this.buttonStart, BorderLayout.WEST);

            JButton buttonStop = new JButton("Stop");
            buttonStop.addActionListener(event -> stop());
            this.buttonPanel.add(buttonStop, BorderLayout.EAST);

            JButton buttonStep = new JButton("Step");
            buttonStep.addActionListener(event -> step());
            this.buttonPanel.add(buttonStep, BorderLayout.NORTH);

            JButton buttonReset = new JButton("Reset");
            buttonReset.addActionListener(event -> reset());
            this.buttonPanel.add(buttonReset, BorderLayout.SOUTH);
        }

        return this.buttonPanel;
    }

    /**
     * @return {@link JPanel}
     */
    protected JPanel getControlPanel()
    {
        if (this.controlPanel == null)
        {
            this.controlPanel = new JPanel();
        }

        return this.controlPanel;
    }

    /**
     * Liefert das Delay für das ScheduledFuture zum Berechnen der Simulationen.
     *
     * @return int [ms]
     */
    protected int getDelay()
    {
        return this.delay;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link JPanel}
     */
    protected JPanel getMainPanel()
    {
        if (this.mainPanel == null)
        {
            this.mainPanel = new JPanel();
            this.mainPanel.setDoubleBuffered(true);
        }

        return this.mainPanel;
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    protected ScheduledExecutorService getScheduledExecutorService()
    {
        return SimulationEnvironment.getInstance().getScheduledExecutorService();
    }

    /**
     * @return {@link Simulation}
     */
    public S getSimulation()
    {
        return this.simulation;
    }

    /**
     * Aufbau der GUI.
     *
     * @param simulation {@link Simulation}
     * @param delay int
     */
    public void initialize(final S simulation, final int delay)
    {
        this.simulation = simulation;
        this.delay = delay;

        getControlPanel().setLayout(new BorderLayout());
        getControlPanel().setPreferredSize(new Dimension(180, 10));
        getControlPanel().add(getButtonPanel(), BorderLayout.NORTH);

        getMainPanel().setLayout(new BorderLayout());
        getMainPanel().add(getControlPanel(), BorderLayout.EAST);

        SimulationCanvas canvas = new SimulationCanvas(simulation);
        getMainPanel().add(canvas, BorderLayout.CENTER);
    }

    /**
     * Zurücksetzen der Simulation.
     */
    protected void reset()
    {
        stop();
        getSimulation().reset();
        start();
    }

    /**
     * Startet die Simulation.
     */
    protected void start()
    {
        Runnable runnable = this::step;

        this.scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(runnable, 0, getDelay(), TimeUnit.MILLISECONDS);

        this.buttonStart.setEnabled(false);
    }

    /**
     * Aktualisiert die Simulation.
     */
    protected void step()
    {
        try
        {
            // long start = System.currentTimeMillis();
            getSimulation().nextGeneration();
            // System.out.printf("%d ms%n", System.currentTimeMillis() - start);
        }
        catch (Exception ex)
        {
            stop();

            getLogger().error(null, ex);

            StringWriter sw = new StringWriter();

            try (PrintWriter pw = new PrintWriter(sw))
            {
                ex.printStackTrace(pw);
            }

            JOptionPane.showMessageDialog(getMainPanel(), sw);
        }
    }

    /**
     * Stoppt die Simulation.
     */
    protected void stop()
    {
        if ((this.scheduledFuture != null))
        {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }

        this.buttonStart.setEnabled(true);
    }
}
