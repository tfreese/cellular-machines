// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;

/**
 * Fisch-Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public class DefaultFishCell extends AbstractWatorCell implements FishCell
{
    /**
     * Erstellt ein neues {@link DefaultFishCell} Object.
     *
     * @param world {@link WaTorWorld}
     */
    DefaultFishCell(final WaTorWorld world)
    {
        super(world, Color.GREEN);
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

        ermittleNachbarn();

        incrementEnergy();

        // Bewegen
        int[] frei = getFreierNachbar();

        if (frei != null)
        {
            final int freiX = frei[0];
            final int freiY = frei[1];

            final int oldX = getX();
            final int oldY = getY();

            moveTo(freiX, freiY);

            if (getEnergy() >= getWorld().getFishBreedEnergy())
            {
                // Nachwuchs einfach auf den alten Platz setzen.
                WatorCell child = getWorld().getObjectPoolFish().borrowObject();
                child.moveTo(oldX, oldY);

                // Energie aufteilen.
                child.setEnergy(getEnergy() / 2);
                setEnergy(getEnergy() - child.getEnergy());

                child.setEdited(true);
            }
        }

        setEdited(true);
    }
}
