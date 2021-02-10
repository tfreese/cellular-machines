// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.gameoflife;

import java.awt.Color;
import de.freese.simulationen.model.AbstractCell;

/**
 * Zelle für die "Spiel des Lebens" Simulation.
 *
 * @author Thomas Freese
 */
public class GoFCell extends AbstractCell<GoFWorld>
{
    /**
     *
     */
    private boolean alive = true;

    /**
     *
     */
    private int lebendeNachbarn;

    /**
     * Ermittelt die Anzahl der lebenden Nachbarn.<br>
     * Quadrat von 3x3 Zellen prüfen, mit dieser Zelle in der Mitte.
     */
    void ermittleLebendeBachbarn()
    {
        int anzahlLebendeNachbarn = 0;

        // Startpunkt unten links.
        int startX = getWorld().getXTorusKoord(getX(), -1);
        int startY = getWorld().getYTorusKoord(getY(), -1);

        for (int offsetX = 0; offsetX < 3; offsetX++)
        {
            int x = getWorld().getXTorusKoord(startX, offsetX);

            for (int offsetY = 0; offsetY < 3; offsetY++)
            {
                // Diese Zelle (this) ausnehmen.
                if ((offsetX == 1) && (offsetY == 1))
                {
                    continue;
                }

                int y = getWorld().getYTorusKoord(startY, offsetY);
                GoFCell cell = getWorld().getCell(x, y);

                if ((cell != null) && cell.isAlive())
                {
                    anzahlLebendeNachbarn++;
                }
            }
        }

        this.lebendeNachbarn = anzahlLebendeNachbarn;
    }

    /**
     * @see de.freese.simulationen.model.ICell#getColor()
     */
    @Override
    public Color getColor()
    {
        return isAlive() ? Color.BLACK : Color.WHITE;
    }

    /**
     * @return int
     */
    protected int getLebendeNachbarn()
    {
        return this.lebendeNachbarn;
    }

    /**
     * @return boolean
     */
    protected boolean isAlive()
    {
        return this.alive;
    }

    /**
     * <ol>
     * <li>Eine tote Zelle mit genau drei lebenden Nachbarn wird in der nächsten Generation neu geboren.
     * <li>Lebende Zellen mit weniger als zwei lebenden Nachbarn sterben in der nächsten Generation an Einsamkeit.
     * <li>Eine lebende Zelle mit zwei oder drei lebenden Nachbarn bleibt in der nächsten Generation lebend.
     * <li>Lebende Zellen mit mehr als drei lebenden Nachbarn sterben in der nächsten Generation an Überbevölkerung.
     * </ol>
     *
     * @see de.freese.simulationen.model.ICell#nextGeneration(java.lang.Object[])
     */
    @Override
    public void nextGeneration(final Object...params)
    {
        if (!isAlive() && (getLebendeNachbarn() == 3))
        {
            // 1.
            setAlive(true);
        }
        else if (isAlive() && (getLebendeNachbarn() < 2))
        {
            // 2.
            setAlive(false);
        }
        else if (isAlive() && ((getLebendeNachbarn() == 2) || (getLebendeNachbarn() == 3)))
        {
            // 3.
            setAlive(true);
        }
        else if (isAlive() && (getLebendeNachbarn() > 3))
        {
            // 4.
            setAlive(false);
        }

        getWorld().cellColorChanged(getX(), getY(), this);
    }

    /**
     * @param alive boolean
     */
    void setAlive(final boolean alive)
    {
        this.alive = alive;
    }
}
