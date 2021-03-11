// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;
import java.util.function.BiConsumer;

/**
 * Basisklasse einer Zelle.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCell implements Cell
{
    /**
     *
     */
    private Color color;

    /**
     *
     */
    private final AbstractRasterSimulation simulation;

    /**
     *
     */
    private int x = -1;

    /**
     *
     */
    private int y = -1;

    /**
     * Erstellt ein neues {@link AbstractCell} Object.
     *
     * @param simulation {@link Simulation}
     */
    protected AbstractCell(final AbstractRasterSimulation simulation)
    {
        super();

        this.simulation = simulation;
    }

    /**
     * @see de.freese.simulationen.model.Cell#getColor()
     */
    @Override
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @return {@link AbstractRasterSimulation}
     */
    protected AbstractRasterSimulation getSimulation()
    {
        return this.simulation;
    }

    /**
     * @see de.freese.simulationen.model.Cell#getX()
     */
    @Override
    public int getX()
    {
        return this.x;
    }

    /**
     * @see de.freese.simulationen.model.Cell#getY()
     */
    @Override
    public int getY()
    {
        return this.y;
    }

    /**
     * @param color Color
     */
    public void setColor(final Color color)
    {
        this.color = color;

        getSimulation().setCellColor(getX(), getY(), color);
    }

    /**
     * @param x int
     * @param y int
     */
    void setXY(final int x, final int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" ");
        sb.append("[x=").append(getX()).append(",y=").append(getY()).append("]");

        return sb.toString();
    }

    /**
     * Liefert nur die Nord, Ost, Süd und West Nachbarn dieser Zelle.
     *
     * @param biConsumer {@link BiConsumer}
     */
    protected void visitNeighbours(final BiConsumer<Integer, Integer> biConsumer)
    {
        int xWest = getSimulation().getXTorusKoord(getX(), -1);
        int xOst = getSimulation().getXTorusKoord(getX(), +1);
        int ySued = getSimulation().getYTorusKoord(getY(), -1);
        int yNord = getSimulation().getYTorusKoord(getY(), +1);

        // Nord
        biConsumer.accept(getX(), yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd
        biConsumer.accept(getX(), ySued);

        // West
        biConsumer.accept(xWest, getY());

    }

    /**
     * Liefert alle Nachbarn dieser Zelle.
     *
     * @param biConsumer {@link BiConsumer}
     */
    protected void visitNeighboursAll(final BiConsumer<Integer, Integer> biConsumer)
    {
        int xWest = getSimulation().getXTorusKoord(getX(), -1);
        int xOst = getSimulation().getXTorusKoord(getX(), +1);
        int ySued = getSimulation().getYTorusKoord(getY(), -1);
        int yNord = getSimulation().getYTorusKoord(getY(), +1);

        // Nord
        biConsumer.accept(getX(), yNord);

        // Nord-Ost
        biConsumer.accept(xOst, yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd-Ost
        biConsumer.accept(xOst, ySued);

        // Süd
        biConsumer.accept(getX(), ySued);

        // Süd-West
        biConsumer.accept(xWest, ySued);

        // West
        biConsumer.accept(xWest, getY());

        // Nord-West
        biConsumer.accept(xWest, yNord);
    }
}
