// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.wator2;

import java.awt.Color;
import java.util.function.BiConsumer;
import de.freese.simulationen.model.AbstractSimulation;

/**
 * Basisklasse einer Zelle.
 *
 * @author Thomas Freese
 * @param <S> Type
 */
public abstract class AbstractRasterCell<S extends AbstractRasterSimulation> implements RasterCell
{
    /**
     *
     */
    private Color color;

    /**
     *
     */
    private int x = -1;

    /**
     *
     */
    private int y = -1;

    /**
     * Erstellt ein neues {@link AbstractRasterCell} Object.
     *
     * @param color {@link Color}
     */
    protected AbstractRasterCell(final Color color)
    {
        super();

        this.color = color;
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
     * @see de.freese.simulationen.wator2.RasterCell#moveTo(int, int, de.freese.simulationen.wator2.Raster)
     */
    @Override
    public void moveTo(final int x, final int y, final Raster raster)
    {
        // Alte Position auf null setzen.
        raster.setCell(getX(), getY(), null);

        setXY(x, y);
        raster.setCell(x, y, this);
    }

    /**
     * @param color Color
     */
    public void setColor(final Color color)
    {
        this.color = color;
    }

    /**
     * Setzt die Position ohne die Zelle zu verschieben.
     *
     * @param x int
     * @param y int
     */
    public void setXY(final int x, final int y)
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
     * @param simulation {@link AbstractSimulation}
     * @param biConsumer {@link BiConsumer}
     */
    protected void visitNeighbours(final AbstractSimulation simulation, final BiConsumer<Integer, Integer> biConsumer)
    {
        int xWest = simulation.getXTorusKoord(getX(), -1);
        int xOst = simulation.getXTorusKoord(getX(), +1);
        int ySued = simulation.getYTorusKoord(getY(), -1);
        int yNord = simulation.getYTorusKoord(getY(), +1);

        // Die Diagonalen verursachen stehende Haie und andere Fehler ???

        // Nord
        biConsumer.accept(getX(), yNord);

        // Nord-Ost
        // biConsumer.accept(xOst, yNord);

        // Ost
        biConsumer.accept(xOst, getY());

        // Süd-Ost
        // biConsumer.accept(xOst, ySued);

        // Süd
        biConsumer.accept(getX(), ySued);

        // Süd-West
        // biConsumer.accept(xWest, ySued);

        // West
        biConsumer.accept(xWest, getY());

        // Nord-West
        // biConsumer.accept(xWest, yNord);
    }
}
