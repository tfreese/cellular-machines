// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.hopalong;

import java.awt.BorderLayout;
import de.freese.simulationen.AbstractSimulationView;
import de.freese.simulationen.SimulationCanvas;

/**
 * Model fuer die "Hop along"-Simulation.
 *
 * @author Thomas Freese
 */
public class HopAlongView extends AbstractSimulationView<HopAlongWorld>
{
    /**
     * @see de.freese.simulationen.AbstractSimulationView#createSimulation(int, int)
     */
    @Override
    protected HopAlongWorld createSimulation(final int fieldWidth, final int fieldHeight)
    {
        return new HopAlongWorld(fieldWidth, fieldHeight);
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
