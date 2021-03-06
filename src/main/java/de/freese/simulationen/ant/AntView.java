// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.ant;

import java.awt.BorderLayout;
import de.freese.simulationen.AbstractSimulationView;
import de.freese.simulationen.SimulationCanvas;

/**
 * View fuer die Langton-Ameisen Simulation.
 *
 * @author Thomas Freese
 */
public class AntView extends AbstractSimulationView<AntWorld>
{
    /**
     * @see de.freese.simulationen.AbstractSimulationView#createSimulation(int, int)
     */
    @Override
    protected AntWorld createSimulation(final int fieldWidth, final int fieldHeight)
    {
        return new AntWorld(fieldWidth, fieldHeight);
    }

    /**
     * Die Ameisen mögen es etwas schneller.
     *
     * @see de.freese.simulationen.AbstractSimulationView#getDelay()
     */
    @Override
    protected int getDelay()
    {
        int delay = super.getDelay() / 10;

        return Math.max(delay, 1);
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#initialize(int, int)
     */
    @Override
    public void initialize(final int fieldWidth, final int fieldHeight)
    {
        super.initialize(fieldWidth, fieldHeight);

        SimulationCanvas canvas = new SimulationCanvas(getSimulation());
        getMainPanel().add(canvas, BorderLayout.CENTER);
    }
}
