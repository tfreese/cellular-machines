// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.wator3;

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
     * Berechnet die nächste Generation.<br>
     */
    public void nextGeneration();
}
