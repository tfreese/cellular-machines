// Created: 07.03.2021
package de.freese.simulationen.wator;

import de.freese.simulationen.model.Cell;

/**
 * @author Thomas Freese
 */
public interface WatorCell extends Cell
{
    /**
     * Liefert den Energiewert der Zelle.
     *
     * @return int
     */
    public int getEnergy();

    /**
     * True, wenn diese Zelle in einem Zyklus schon mal verarbeitet wurde.
     *
     * @return boolean
     */
    public boolean isEdited();

    /**
     * True, wenn diese Zelle in einem Zyklus schon mal verarbeitet wurde.
     *
     * @param edited boolean
     */
    public void setEdited(final boolean edited);

    /**
     * @param energy int
     */
    public void setEnergy(final int energy);

    /**
     * Setzt die Position ohne die Zelle zu verschieben.
     *
     * @param x int
     * @param y int
     */
    public void setXY(final int x, final int y);
}
