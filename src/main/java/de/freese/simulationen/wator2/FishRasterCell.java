// Created: 09.03.2021
package de.freese.simulationen.wator2;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public class FishRasterCell extends AbstractWatorRasterCell
{
    /**
     * Erstellt ein neues {@link FishRasterCell} Object.
     *
     * @param simulation {@link WaterRasterSimulation}
     */
    public FishRasterCell(final WaterRasterSimulation simulation)
    {
        super(simulation, Color.GREEN);
    }

    /**
     * @see de.freese.simulationen.wator2.RasterCell#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // // Verhindert Wellenfronten.
        // if (isEdited())
        // {
        // return;
        // }

        // Im alten Raster die Nachbarn suchen.
        ermittleNachbarn(getSimulation().getRasterOld());

        incrementEnergy();

        // Bewegen: Im neuen Raster nach freien Nachbarn suchen.
        final int[] frei = getFreierNachbar(getSimulation().getRaster());

        if (frei != null)
        {
            final int freiX = frei[0];
            final int freiY = frei[1];

            final int oldX = getX();
            final int oldY = getY();

            // Diesen Fisch ins neue Raster setzen.
            moveTo(freiX, freiY, getSimulation().getRaster());

            // Nachwuchs einfach auf den alten Platz setzen, wenn dieser noch frei ist.
            if ((getEnergy() >= getSimulation().getFishBreedEnergy()) && (getSimulation().getRaster().getCell(oldX, oldY) == null))
            {
                FishRasterCell child = getSimulation().getObjectPoolFish().borrowObject();
                child.moveTo(oldX, oldY, getSimulation().getRaster());

                // Energie aufteilen.
                child.setEnergy(getEnergy() / 2);
                setEnergy(getEnergy() - child.getEnergy());

                // child.setEdited(true);
            }
        }

        // setEdited(true);
    }
}
