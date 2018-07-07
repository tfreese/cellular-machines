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

import de.freese.simulationen.model.ISimulation;

/**
 * BasisView fuer die Simulationen.
 *
 * @author Thomas Freese
 * @param <S> Konkreter Typ der Welt
 */
public abstract class AbstractSimulationView<S extends ISimulation>
{
    /**
     *
     */
    private JPanel buttonPanel = null;

    /**
     *
     */
    private JButton buttonStart = null;

    /**
     *
     */
    private JPanel controlPanel = null;

    /**
     * [ms]
     */
    private final int delay;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private JPanel mainPanel = null;

    /**
     *
     */
    private ScheduledFuture<?> scheduledFuture = null;

    /**
     *
     */
    private S simulation = null;

    /**
     * Erstellt ein neues {@link AbstractSimulationView} Object.
     */
    public AbstractSimulationView()
    {
        super();

        this.delay = Integer.parseInt(SimulationGUI.PROPERTIES.getProperty("simulation.delay", "40"));
    }

    /**
     * @return {@link ISimulation}
     */
    public S getSimulation()
    {
        return this.simulation;
    }

    /**
     * Aufbau der GUI.
     *
     * @param fieldWidth int
     * @param fieldHeight int
     */
    public void initialize(final int fieldWidth, final int fieldHeight)
    {
        this.simulation = createModel(fieldWidth, fieldHeight);

        getControlPanel().setLayout(new BorderLayout());
        getControlPanel().setPreferredSize(new Dimension(180, 10));
        getControlPanel().add(getButtonPanel(), BorderLayout.NORTH);

        getMainPanel().setLayout(new BorderLayout());
        getMainPanel().add(getControlPanel(), BorderLayout.EAST);
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
     * Erzeugt das Model.
     *
     * @param fieldWidth int
     * @param fieldHeight int
     * @return {@link ISimulation}
     */
    protected abstract S createModel(final int fieldWidth, final int fieldHeight);

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
        return SimulationGUI.SCHEDULED_EXECUTOR_SERVICE;
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
            getSimulation().nextGeneration();
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
