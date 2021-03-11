// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.wator3;

import java.awt.Color;

/**
 * Dummy-Zelle für leere Flächen.
 *
 * @author Thomas Freese
 */
public class EmptyRasterCell extends AbstractRasterCell
{
    /**
     * Erstellt ein neues {@link EmptyRasterCell} Object.
     *
     * @param simulation {@link AbstractRasterSimulation}
     */
    public EmptyRasterCell(final AbstractRasterSimulation simulation)
    {
        super(simulation);
    }

    /**
     * Erstellt ein neues {@link EmptyRasterCell} Object.
     *
     * @param simulation {@link AbstractRasterSimulation}
     * @param color {@link Color}
     */
    public EmptyRasterCell(final AbstractRasterSimulation simulation, final Color color)
    {
        super(simulation);

        setColor(color);
    }

    /**
     * @see de.freese.simulationen.wator3.RasterCell#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // Empty
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(": ");
        sb.append("Color[r=").append(getColor().getRed()).append(",g=").append(getColor().getGreen()).append(",b=").append(getColor().getBlue()).append("]");

        return sb.toString();
    }
}
