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
import de.freese.simulationen.ant.AntView;
import de.freese.simulationen.balls.BallView;
import de.freese.simulationen.gameoflife.GofView;
import de.freese.simulationen.wator.WaTorDiagrammPanel;
import de.freese.simulationen.wator.WaTorView;

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

        int fieldWidth = SimulationEnvironment.getInstance().getAsInt("simulation.field.width", 100);
        int fieldHeight = SimulationEnvironment.getInstance().getAsInt("simulation.field.height", 100);

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
