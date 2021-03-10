// Created: 09.03.2021
package de.freese.simulationen.wator2;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Freese
 */
public class Raster
{
    /**
    *
    */
    private final RasterCell[][] cellArray;

    /**
     *
     */
    private final Set<RasterCell> cellSet = new HashSet<>();

    /**
     * Erstellt ein neues {@link Raster} Object.
     *
     * @param width int
     * @param height int
     */
    public Raster(final int width, final int height)
    {
        super();

        this.cellArray = new RasterCell[width][height];
    }

    /**
     * Liefert die Zelle der Koordinaten.
     *
     * @param x int
     * @param y int
     * @return {@link RasterCell}
     */
    public RasterCell getCell(final int x, final int y)
    {
        // if ((x < 0) || (y < 0))
        // {
        // return null;
        // }

        return this.cellArray[x][y];
    }

    /**
     * @return {@link Set}<Cell>
     */
    public Set<RasterCell> getCells()
    {
        return this.cellSet;
    }

    /**
     * @param x int
     * @param y int
     * @param cell {@link RasterCell}
     */
    void setCell(final int x, final int y, final RasterCell cell)
    {
        // if ((x < 0) || (y < 0))
        // {
        // return;
        // }

        RasterCell old = this.cellArray[x][y];
        this.cellArray[x][y] = cell;

        if (old != null)
        {
            this.cellSet.remove(old);
        }

        if (cell != null)
        {
            this.cellSet.add(cell);
        }
    }
}
