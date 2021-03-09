// Created: 09.03.2021
package de.freese.simulationen.wator2;

import java.awt.Color;
import de.freese.simulationen.model.Simulation;

/**
 * @author Thomas Freese
 */
public class SharkRasterCell extends AbstractWatorRasterCell
{
    /**
     * Erstellt ein neues {@link SharkRasterCell} Object.
     */
    public SharkRasterCell()
    {
        super(Color.BLUE);
    }

    /**
     * @see de.freese.simulationen.wator2.RasterCell#nextGeneration(de.freese.simulationen.model.Simulation)
     */
    @Override
    public void nextGeneration(final Simulation simulation)
    {
        // TODO Auto-generated method stub

    }
}
