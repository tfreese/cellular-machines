// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import de.freese.simulationen.wator.WaTorDiagrammPanel;
import de.freese.simulationen.wator.WaTorView;
import de.freese.simulationen.wator.WaTorWorld;
import de.freese.simulationen.wator2.WaterRasterSimulation;

/**
 * Hauptfenster der Simulation-Demos.
 *
 * @author Thomas Freese
 */
class SimulationSwing extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = -8931412063622174282L;

    /**
     * Erstellt ein neues {@link SimulationSwing} Object.
     *
     * @throws HeadlessException Falls was schief geht.
     */
    public SimulationSwing() throws HeadlessException
    {
        super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    }

    /**
     * Aufbau der GUI.
     */
    public void initialize()
    {
        setTitle("Simulationen");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        add(tabbedPane);

        int delay = SimulationEnvironment.getInstance().getAsInt("simulation.delay", 40);
        int fieldWidth = SimulationEnvironment.getInstance().getAsInt("simulation.field.width", 100);
        int fieldHeight = SimulationEnvironment.getInstance().getAsInt("simulation.field.height", 100);

        // Ants: Die Ameisen mögen es etwas schneller.
        // SimulationView<AntWorld> antView = new SimulationView<>();
        // antView.initialize(new AntWorld(fieldWidth, fieldHeight), Math.max(delay / 5, 1));
        // tabbedPane.addTab("Langton-Ameise", antView.getMainPanel());

        // GoF: Game of Life
        // SimulationView<GoFWorld> gofView = new SimulationView<>();
        // gofView.initialize(new GoFWorld(fieldWidth, fieldHeight), delay);
        // tabbedPane.addTab("Game of Life", gofView.getMainPanel());

        // HopAlong
        // SimulationView<HopAlongWorld> hopAlongView = new SimulationView<>();
        // hopAlongView.initialize(new HopAlongWorld(fieldWidth, fieldHeight), delay);
        // tabbedPane.addTab("Hop along", hopAlongView.getMainPanel());

        // Bälle
        // BallView ballView = new BallView();
        // ballView.initialize(new BallSimulation(fieldWidth, fieldHeight, delay * 4), delay);
        // tabbedPane.addTab("Bälle", ballView.getMainPanel());

        // WaTor: Water Torus
        WaTorView waTorView = new WaTorView();
        waTorView.initialize(new WaTorWorld(fieldWidth, fieldHeight), delay);
        tabbedPane.addTab("Water Torus", waTorView.getMainPanel());

        WaTorDiagrammPanel waTorDiagrammPanel = new WaTorDiagrammPanel();
        waTorView.getSimulation().addWorldListener(waTorDiagrammPanel);
        tabbedPane.addTab("WaTor-Diagramm", waTorDiagrammPanel);

        SimulationView<WaterRasterSimulation> waTorView2 = new SimulationView<>();
        waTorView2.initialize(new WaterRasterSimulation(fieldWidth, fieldHeight), delay);
        tabbedPane.addTab("Water Torus 2", waTorView2.getMainPanel());
    }
}
