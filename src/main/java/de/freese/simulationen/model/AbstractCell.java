// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;
import java.util.Objects;

/**
 * Basisklasse einer Zelle.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ der Welt
 */
public abstract class AbstractCell<T extends AbstractWorld> implements Cell
{
    /**
     *
     */
    private Color color;

    /**
     *
     */
    private T world;

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
     * @param world {@link AbstractWorld}
     */
    protected AbstractCell(final T world)
    {
        this(world, null);
    }

    /**
     * Erstellt ein neues {@link AbstractCell} Object.
     *
     * @param world {@link AbstractWorld}
     * @param color {@link Color}
     */
    protected AbstractCell(final T world, final Color color)
    {
        super();

        this.world = world;
        this.color = color;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof AbstractCell))
        {
            return false;
        }

        AbstractCell<?> other = (AbstractCell<?>) obj;

        return Objects.equals(this.color, other.color) && (this.x == other.x) && (this.y == other.y)
                && Objects.equals(getClass().getSimpleName(), obj.getClass().getSimpleName());
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
     * @return {@link AbstractWorld}
     */
    protected T getWorld()
    {
        return this.world;
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.color, this.x, this.y, getClass().getSimpleName());
    }

    /**
     * @see de.freese.simulationen.model.Cell#moveTo(int, int)
     */
    @Override
    public void moveTo(final int x, final int y)
    {
        // Alte Position auf null setzen.
        if ((getX() >= 0) && (getY() >= 0))
        {
            getWorld().setCell(getX(), getY(), null);
        }

        setXY(x, y);
        getWorld().setCell(x, y, this);
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
}
