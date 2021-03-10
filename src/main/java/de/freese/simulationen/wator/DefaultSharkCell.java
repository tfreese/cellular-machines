// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;

/**
 * Hai-Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class DefaultSharkCell extends AbstractWatorCell implements SharkCell
{
    /**
     * Erstellt ein neues {@link DefaultSharkCell} Object.
     *
     * @param world {@link WaTorWorld}
     */
    DefaultSharkCell(final WaTorWorld world)
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

        int oldX = getX();
        int oldY = getY();

        decrementEnergy();

        if (hatFischNachbarn() || hatFreieNachbarn())
        {
            if (hatFischNachbarn())
            {
                // Fressen
                final FishCell fishCell = getFischNachbar();

                if (fishCell == null)
                {
                    decrementEnergy();
                }
                else
                {
                    final int fischX = fishCell.getX();
                    final int fischY = fishCell.getY();

                    moveTo(fischX, fischY);

                    setEnergy(fishCell.getEnergy() + getEnergy());

                    getWorld().getObjectPoolFish().returnObject(fishCell);
                }
            }
            else
            {
                // Bewegen
                final int[] frei = getFreierNachbar();

                if (frei != null)
                {
                    final int freiX = frei[0];
                    final int freiY = frei[1];

                    moveTo(freiX, freiY);
                }
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

        if (getEnergy() <= getWorld().getSharkStarveEnergy())
        {
            // Sterben
            getWorld().setCell(getX(), getY(), null);
            getWorld().getObjectPoolShark().returnObject(this);
        }

        setEdited(true);
    }
}
