// Created: 11.03.2021
package de.freese.simulationen.gameoflife;

import de.freese.simulationen.wator3.AbstractRasterSimulation;
import de.freese.simulationen.wator3.RasterCell;

/**
 * @author Thomas Freese
 */
public class GofRasterSimulation extends AbstractRasterSimulation
{
    /**
     * Erstellt ein neues {@link GofRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    public GofRasterSimulation(final int width, final int height)
    {
        super(width, height);

        fillRaster(() -> new GofRasterCell(this));
        reset();
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterSimulation#getCell(int, int)
     */
    @Override
    protected GofRasterCell getCell(final int x, final int y)
    {
        return (GofRasterCell) super.getCell(x, y);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        getCellStream().map(GofRasterCell.class::cast).forEach(GofRasterCell::ermittleLebendeBachbarn);
        getCellStream().forEach(RasterCell::nextGeneration);

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterSimulation#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        getCell(x, y).setAlive(getRandom().nextBoolean());
    }
}
