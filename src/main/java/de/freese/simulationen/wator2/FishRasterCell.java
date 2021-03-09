// Created: 09.03.2021
package de.freese.simulationen.wator2;

import java.awt.Color;
import de.freese.simulationen.model.Simulation;

/**
 * @author Thomas Freese
 */
public class FishRasterCell extends AbstractWatorRasterCell
{
    /**
     * Erstellt ein neues {@link FishRasterCell} Object.
     */
    public FishRasterCell()
    {
        super(Color.GREEN);
    }

    /**
     * @see de.freese.simulationen.wator2.RasterCell#nextGeneration(de.freese.simulationen.model.Simulation)
     */
    @Override
    public void nextGeneration(final Simulation simulation)
    {
        WaterRasterSimulation watorSimulation = (WaterRasterSimulation) simulation;

        // // Verhindert Wellenfronten.
        // if (isEdited())
        // {
        // return;
        // }

        // Im alten Raster die Nachbarn suchen.
        ermittleNachbarn(watorSimulation, watorSimulation.getRasterOld());

        incrementEnergy();

        // Bewegen: Im neuen Raster nach freien Nachbarn suchen.
        int[] frei = getFreierNachbar(watorSimulation, watorSimulation.getRaster());

        if (frei != null)
        {
            final int freiX = frei[0];
            final int freiY = frei[1];

            final int oldX = getX();
            final int oldY = getY();

            // Im alten Raster diese Zelle löschen.
            watorSimulation.getRasterOld().setCell(oldX, oldY, null);

            // Zelle ins neue Raster setzen.
            moveTo(freiX, freiY, watorSimulation.getRaster());
            watorSimulation.setCellColor(freiX, freiY, this);

            // Nachwuchs einfach auf den alten Platz setzen, wenn dieser noch frei ist.
            if ((getEnergy() >= watorSimulation.getFishBreedEnergy()) && (watorSimulation.getRaster().getCell(oldX, oldY) == null))
            {
                FishRasterCell child = watorSimulation.getObjectPoolFish().borrowObject();
                child.moveTo(oldX, oldY, watorSimulation.getRaster());

                // Energie aufteilen.
                child.setEnergy(getEnergy() / 2);
                setEnergy(getEnergy() - child.getEnergy());

                // child.setEdited(true);
            }
        }

        // setEdited(true);
    }
}
