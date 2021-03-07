// Created: 07.03.2021
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import de.freese.simulationen.model.Cell;

/**
 * Fisch-Zelle der WaTor-Simulation mit neuem Algorithmus.
 *
 * @author Thomas Freese
 */
public class FishCellNew extends AbstractWatorCellNew implements FishCell
{
    /**
    *
    */
    private List<int[]> freie = new ArrayList<>();

    /**
     * Erstellt ein neues {@link FishCellNew} Object.
     *
     * @param world {@link WaTorWorld}
     */
    public FishCellNew(final WaTorWorld world)
    {
        super(world, Color.GREEN);
    }

    /**
     * @see de.freese.simulationen.wator.AbstractWatorCellNew#ermittleNachbarn()
     */
    @Override
    public void ermittleNachbarn()
    {
        this.freie.clear();

        // visitNeighbours((x, y) -> {
        // Cell cell = getWorld().getCell(x, y);
        //
        // if (cell == null)
        // {
        // this.freie.add(new int[]
        // {
        // x, y
        // });
        // }
        // });
    }

    /**
     * <ol>
     * <li>Jeder Fisch schwimmt zufällig auf eines der vier angrenzenden Felder, sofern es leer ist.
     * <li>Mit jedem Durchgang gewinnt der Fisch einen Energiepunkt.
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Fisch auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Fisch verteilt.
     * </ol>
     *
     * @see de.freese.simulationen.model.Cell#nextGeneration(java.lang.Object[])
     */
    @Override
    public void nextGeneration(final Object...params)
    {
        // Verhindert Wellenfronten.
        if (isEdited())
        {
            return;
        }

        // this.freie.clear();

        visitNeighbours((x, y) -> {
            Cell cell = getWorld().getCell(x, y);

            if (cell == null)
            {
                this.freie.add(new int[]
                {
                        x, y
                });
            }
        });

        final List<int[]> freieList = this.freie;

        incrementEnergy();

        int oldX = getX();
        int oldY = getY();

        boolean hasMoved = false;

        if (!freieList.isEmpty())
        {
            // Bewegen

            int[] frei = getFreeCell(freieList);

            if (frei != null)
            {
                int freiX = frei[0];
                int freiY = frei[1];

                moveTo(freiX, freiY);

                hasMoved = true;
            }
        }

        // Ohne Bewegung keinen Nachwuchs.
        if (hasMoved && (getEnergy() >= getWorld().getFishBreedEnergy()))
        {
            // Nachwuchs einfach auf den alten Platz setzen.
            FishCell child = getWorld().getObjectPoolFish().borrowObject();
            child.moveTo(oldX, oldY);

            // Energie aufteilen.
            child.setEnergy(getEnergy() / 2);
            setEnergy(getEnergy() - child.getEnergy());

            child.setEdited(true);
        }

        setEdited(true);
    }
}
