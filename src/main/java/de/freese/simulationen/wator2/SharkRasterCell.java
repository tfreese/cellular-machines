// Created: 09.03.2021
package de.freese.simulationen.wator2;

import java.awt.Color;

/**
 * @author Thomas Freese
 */
public class SharkRasterCell extends AbstractWatorRasterCell
{
    /**
     * Erstellt ein neues {@link SharkRasterCell} Object.
     *
     * @param simulation {@link WaterRasterSimulation}
     */
    public SharkRasterCell(final WaterRasterSimulation simulation)
    {
        super(simulation, Color.BLUE);
    }

    /**
     * @see de.freese.simulationen.wator2.RasterCell#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // Verhindert Wellenfronten.
        // if (isEdited())
        // {
        // return;
        // }

        // Im alten Raster die Nachbarn suchen.
        ermittleNachbarn(getSimulation().getRasterOld());

        boolean hasEaten = false;
        boolean hasMoved = false;

        final int oldX = getX();
        final int oldY = getY();

        decrementEnergy();

        // Versuchen zu fressen.
        final FishRasterCell fisch = getFischNachbar(getSimulation().getRasterOld());

        if (fisch != null)
        {
            final int fischX = fisch.getX();
            final int fischY = fisch.getY();

            // Im alten Raster den Fisch löschen.
            getSimulation().setCell(fischX, fischY, (RasterCell) null, getSimulation().getRasterOld());

            // Diesen Hai ins neue Raster an die Position des Fisches setzen.
            moveTo(fischX, fischY, getSimulation().getRaster());

            setEnergy(fisch.getEnergy() + getEnergy());

            getSimulation().getObjectPoolFish().returnObject(fisch);

            hasEaten = true;
        }
        else
        {
            // Nicht gefressen, dann bewegen.
            final int[] frei = getFreierNachbar(getSimulation().getRaster());

            if (frei != null)
            {
                final int freiX = frei[0];
                final int freiY = frei[1];

                // Diesen Hai ins neue Raster an die freie Position setzen.
                moveTo(freiX, freiY, getSimulation().getRaster());

                hasMoved = true;
            }
        }

        if (hasEaten || hasMoved)
        {
            if ((getEnergy() >= getSimulation().getSharkBreedEnergy()) && (getSimulation().getRaster().getCell(oldX, oldY) == null))
            {
                // Nachwuchs einfach auf den alten Platz setzen.
                SharkRasterCell child = getSimulation().getObjectPoolShark().borrowObject();

                child.moveTo(oldX, oldY, getSimulation().getRaster());

                // Energie aufteilen.
                child.setEnergy(getEnergy() / 2);
                setEnergy(getEnergy() - child.getEnergy());

                // child.setEdited(true);
            }
        }
        else if (getEnergy() <= getSimulation().getSharkStarveEnergy())
        {
            // Sterben
            getSimulation().setCell(oldX, oldY, (SharkRasterCell) null, getSimulation().getRasterOld());
            getSimulation().getObjectPoolShark().returnObject(this);
        }

        // setEdited(true);
    }
}
