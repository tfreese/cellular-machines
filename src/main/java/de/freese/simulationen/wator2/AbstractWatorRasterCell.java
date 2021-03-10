// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import de.freese.simulationen.wator.AbstractWatorCell;
import de.freese.simulationen.wator.FishCell;

/**
 * Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public abstract class AbstractWatorRasterCell extends AbstractRasterCell implements WatorRasterCell
{
    /**
     *
     */
    private boolean edited;

    /**
     *
     */
    private int energy;

    /**
    *
    */
    private final List<int[]> fischNachbarnList = new ArrayList<>(8);

    /**
     *
     */
    private final List<int[]> freieNachbarnList = new ArrayList<>(8);

    /**
     * Erstellt ein neues {@link AbstractWatorCell} Object.
     *
     * @param simulation {@link WaterRasterSimulation}
     * @param color {@link Color}
     */
    protected AbstractWatorRasterCell(final WaterRasterSimulation simulation, final Color color)
    {
        super(simulation, color);
    }

    /**
     * Erniedrigt den Energiewert um 1.
     */
    void decrementEnergy()
    {
        this.energy--;
    }

    /**
     * Ermittelt die Nachbarn dieser Zelle.<br>
     * Es werden nur vertikale und horizontale Nachbarn berücksichtigt.
     *
     * @param raster {@link Raster}
     */
    void ermittleNachbarn(final Raster raster)
    {
        this.freieNachbarnList.clear();
        this.fischNachbarnList.clear();

        visitNeighbours((x, y) -> {
            RasterCell cell = raster.getCell(x, y);

            if (cell == null)
            {
                this.freieNachbarnList.add(new int[]
                {
                        x, y
                });
            }
            else if (cell instanceof FishRasterCell)
            {
                this.fischNachbarnList.add(new int[]
                {
                        x, y
                });

                // Workaround: Verhindert falsche Koordinaten bei paralleler Verarbeitung.
                // Verhindert auch zusätzlich das Auftreten von Wellenfronten.
                // ((FishCell) cell).setEdited(true);
            }
        });
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#getEnergy()
     */
    @Override
    public int getEnergy()
    {
        return this.energy;
    }

    /**
     * Liefert einen Fisch in der Nachbarschaft oder keinen.
     *
     * @param raster {@link Raster}
     * @return {@link FishRasterCell}
     */
    protected FishRasterCell getFischNachbar(final Raster raster)
    {
        if (!hatFischNachbarn())
        {
            return null;
        }

        FishRasterCell fishCell = null;

        // int size = this.fischNachbarn.length;
        // int[] koords = this.fischNachbarn[getWorld().getRandom().nextInt(size)];

        // int size = this.fischNachbarnList.size();
        // int[] koords = this.fischNachbarnList.get(simulation.getRandom().nextInt(size));
        //
        // int x = koords[0];
        // int y = koords[1];
        //
        // RasterCell cell = simulation.getRaster().getCell(x, y);
        //
        // if (cell instanceof FishRasterCell)
        // {
        // fishCell = (FishRasterCell) cell;
        // }

        while ((fishCell == null) && !this.fischNachbarnList.isEmpty())
        {
            // try
            // {
            final int size = this.fischNachbarnList.size();
            final int[] koords = this.fischNachbarnList.remove(getSimulation().getRandom().nextInt(size));

            final int x = koords[0];
            final int y = koords[1];

            final RasterCell cell = raster.getCell(x, y);

            if (cell instanceof FishCell)
            {
                fishCell = (FishRasterCell) cell;
            }
            // }
            // catch (IndexOutOfBoundsException ex)
            // {
            // // Kommt vor, warum auch immer.
            // }
        }

        return fishCell;
    }

    /**
     * Liefert die Kooordinaten einer freien Zelle in der Nachbarschaft oder keine.
     *
     * @param raster {@link Raster}
     * @return int[]
     */
    protected int[] getFreierNachbar(final Raster raster)
    {
        if (!hatFreieNachbarn())
        {
            return null;
        }

        int[] koords = null;

        // int size = this.freieNachbarn.length;
        // koords = this.freieNachbarn[simulation.getRandom().nextInt(size)];

        // int size = this.freieNachbarnList.size();
        // koords = this.freieNachbarnList.get(simulation.getRandom().nextInt(size));

        while ((koords == null) && !this.freieNachbarnList.isEmpty())
        {
            // try
            // {
            final int size = this.freieNachbarnList.size();
            final int[] xy = this.freieNachbarnList.remove(getSimulation().getRandom().nextInt(size));

            final int x = xy[0];
            final int y = xy[1];

            // Ist das Feld noch frei ?
            final RasterCell cell = raster.getCell(x, y);

            if (cell != null)
            {
                continue;
            }

            koords = xy;
            // }
            // catch (IndexOutOfBoundsException ex)
            // {
            // // Kommt vor, warum auch immer.
            // }
        }

        return koords;
    }

    /**
     * @see de.freese.simulationen.wator2.AbstractRasterCell#getSimulation()
     */
    @Override
    protected WaterRasterSimulation getSimulation()
    {
        return (WaterRasterSimulation) super.getSimulation();
    }

    /**
     * @return boolean
     */
    protected boolean hatFischNachbarn()
    {
        return !this.fischNachbarnList.isEmpty();
        // return this.fischNachbarn.length > 0;
    }

    /**
     * @return boolean
     */
    protected boolean hatFreieNachbarn()
    {
        return !this.freieNachbarnList.isEmpty();
        // return this.freieNachbarn.length > 0;
    }

    /**
     * Erhoeht den Energiewert um 1.
     */
    void incrementEnergy()
    {
        this.energy++;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#isEdited()
     */
    @Override
    public boolean isEdited()
    {
        return this.edited;
    }

    /**
     * @see de.freese.simulationen.wator2.AbstractRasterCell#moveTo(int, int, de.freese.simulationen.wator2.Raster)
     */
    @Override
    public void moveTo(final int x, final int y, final Raster raster)
    {
        // Im alten Raster diese Zelle löschen.
        if ((getX() >= 0) && (getY() >= 0))
        {
            getSimulation().setCell(getX(), getY(), null, getSimulation().getRasterOld());
        }

        super.moveTo(x, y, raster);
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#setEdited(boolean)
     */
    @Override
    public void setEdited(final boolean edited)
    {
        this.edited = edited;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#setEnergy(int)
     */
    @Override
    public void setEnergy(final int energy)
    {
        this.energy = energy;
    }
}
