// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.wator2;

import java.awt.Color;

/**
 * Einzelne Zelle einer Welt.
 *
 * @author Thomas Freese
 */
public interface RasterCell
{
    /**
     * @return {@link Color}
     */
    public Color getColor();

    /**
     * @return int
     */
    public int getX();

    /**
     * @return int
     */
    public int getY();

    /**
     * Setzt die Position und verschiebt die Zelle.<br>
     * Die alte Position wird auf null gesetzt.
     *
     * @param x int
     * @param y int
     * @param raster {@link Raster}
     */
    public void moveTo(int x, int y, Raster raster);

    /**
     * Berechnet die nächste Generation.<br>
     */
    public void nextGeneration();
}
