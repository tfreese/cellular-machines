// Created: 11.03.2021
package de.freese.simulationen.gameoflife;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import de.freese.simulationen.wator3.AbstractRasterCell;

/**
 * @author Thomas Freese
 */
public class GofRasterCell extends AbstractRasterCell
{
    /**
    *
    */
    private boolean alive = true;

    /**
    *
    */
    private final AtomicInteger lebendeNachbarn = new AtomicInteger(0);

    /**
     * Erstellt ein neues {@link GofRasterCell} Object.
     *
     * @param simulation {@link GofRasterSimulation}
     */
    public GofRasterCell(final GofRasterSimulation simulation)
    {
        super(simulation);
    }

    /**
     * Ermittelt die Anzahl der lebenden Nachbarn.<br>
     * Quadrat von 3x3 Zellen prüfen, mit dieser Zelle in der Mitte.
     */
    void ermittleLebendeBachbarn()
    {
        this.lebendeNachbarn.set(0);

        visitNeighboursAll((x, y) -> {
            GofRasterCell cell = getSimulation().getCell(x, y);

            if ((cell != null) && cell.isAlive())
            {
                this.lebendeNachbarn.incrementAndGet();
            }
        });

        // int anzahlLebendeNachbarn = 0;
        //
        // // Startpunkt unten links.
        // int startX = getSimulation().getXTorusKoord(getX(), -1);
        // int startY = getSimulation().getYTorusKoord(getY(), -1);
        //
        // for (int offsetX = 0; offsetX < 3; offsetX++)
        // {
        // int x = getSimulation().getXTorusKoord(startX, offsetX);
        //
        // for (int offsetY = 0; offsetY < 3; offsetY++)
        // {
        // // Diese Zelle (this) ausnehmen.
        // if ((offsetX == 1) && (offsetY == 1))
        // {
        // continue;
        // }
        //
        // int y = getSimulation().getYTorusKoord(startY, offsetY);
        // GofRasterCell cell = getSimulation().getCell(x, y);
        //
        // if ((cell != null) && cell.isAlive())
        // {
        // anzahlLebendeNachbarn++;
        // }
        // }
        // }
        //
        // this.lebendeNachbarn = anzahlLebendeNachbarn;
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterCell#getColor()
     */
    @Override
    public Color getColor()
    {
        return isAlive() ? Color.BLACK : Color.WHITE;
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterCell#getSimulation()
     */
    @Override
    protected GofRasterSimulation getSimulation()
    {
        return (GofRasterSimulation) super.getSimulation();
    }

    /**
     * @return boolean
     */
    boolean isAlive()
    {
        return this.alive;
    }

    /**
     * <ol>
     * <li>1. Eine tote Zelle mit genau drei lebenden Nachbarn wird in der nächsten Generation neu geboren.
     * <li>2. Lebende Zellen mit weniger als zwei lebenden Nachbarn sterben in der nächsten Generation an Einsamkeit.
     * <li>3. Eine lebende Zelle mit zwei oder drei lebenden Nachbarn bleibt in der nächsten Generation lebend.
     * <li>4. Lebende Zellen mit mehr als drei lebenden Nachbarn sterben in der nächsten Generation an Überbevölkerung.
     * </ol>
     *
     * @see de.freese.simulationen.wator3.RasterCell#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        final int lebendNachbarn = this.lebendeNachbarn.intValue();

        if (!isAlive() && (lebendNachbarn == 3))
        {
            // 1.
            setAlive(true);
        }
        else if (isAlive() && (lebendNachbarn < 2))
        {
            // 2.
            setAlive(false);
        }
        else if (isAlive() && ((lebendNachbarn == 2) || (lebendNachbarn == 3)))
        {
            // 3.
            setAlive(true);
        }
        else if (isAlive() && ((lebendNachbarn) > 3))
        {
            // 4.
            setAlive(false);
        }
    }

    /**
     * @param alive boolean
     */
    void setAlive(final boolean alive)
    {
        this.alive = alive;

        if (this.alive)
        {
            setColor(Color.BLACK);
        }
        else
        {
            setColor(Color.WHITE);
        }
    }
}
