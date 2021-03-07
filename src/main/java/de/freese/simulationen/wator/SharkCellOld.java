// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;
import de.freese.simulationen.model.Cell;

/**
 * Hai-Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class SharkCellOld extends AbstractWatorCellOld implements SharkCell
{
    /**
     * Erstellt ein neues {@link SharkCellOld} Object.
     *
     * @param world {@link WaTorWorld}
     */
    SharkCellOld(final WaTorWorld world)
    {
        super(world, Color.BLUE);
    }

    /**
     * <ol>
     * <li>Findet ein Hai keinen Fisch auf einem angrenzenden Feld, so schwimmt er zufällig auf eines der vier Felder.
     * <li>Für jeden Zyklus, während dessen der Hai keinen Fisch findet, verliert er einen Energiepunkt.
     * <li>Findet der Hai einen Fisch, wird seine Energie um den Energiewert des Fisches erhöht.
     * <li>Übersteigt die Energie den Wert für die Erzeugung eines Nachkommen ("Breed Energy"), so wird ein neuer Hai auf einem angrenzenden freien Feld
     * geboren. Die vorhandene Energie wird gleichmässig zwischen Eltern- und Kind-Hai verteilt.
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

        ermittleNachbarn();
        int[][] freie = getFreieNachbarn();
        int[][] fische = getFischNachbarn();

        int oldX = getX();
        int oldY = getY();

        if ((fische.length > 0) || (freie.length > 0))
        {
            if (fische.length > 0)
            {
                // Fressen
                int[] fisch = fische[getWorld().getRandom().nextInt(fische.length)];
                int fischX = fisch[0];
                int fischY = fisch[1];

                Cell cell = getWorld().getCell(fischX, fischY);

                if (!(cell instanceof FishCell))
                {
                    decrementEnergy();
                    setEdited(true);
                    return;
                }

                FishCell fishCell = (FishCell) cell;

                moveTo(fischX, fischY);

                setEnergy(fishCell.getEnergy() + getEnergy());

                getWorld().getObjectPoolFish().returnObject(fishCell);
            }
            else
            {
                // Bewegen
                int[] frei = freie[getWorld().getRandom().nextInt(freie.length)];
                int freiX = frei[0];
                int freiY = frei[1];

                moveTo(freiX, freiY);
                decrementEnergy();
            }

            if (getEnergy() >= getWorld().getSharkBreedEnergy())
            {
                // Nachwuchs einfach auf den alten Platz setzen.
                WatorCell child = getWorld().getObjectPoolShark().borrowObject();
                child.moveTo(oldX, oldY);

                // Energie aufteilen.
                child.setEnergy(getEnergy() / 2);
                setEnergy(getEnergy() - child.getEnergy());

                child.setEdited(true);
            }
        }
        else
        {
            decrementEnergy();
        }

        if (getEnergy() <= getWorld().getSharkStarveEnergy())
        {
            // Sterben
            getWorld().setCell(null, getX(), getY());
            getWorld().getObjectPoolShark().returnObject(this);
        }

        setEdited(true);
    }
}
