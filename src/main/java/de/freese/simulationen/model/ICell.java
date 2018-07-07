// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;

/**
 * Einzelne Zelle einer Welt.
 *
 * @author Thomas Freese
 */
public interface ICell
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
     */
    public void moveTo(int x, int y);

    /**
     * Berechnet die nächste Generation.<br>
     * 
     * @param params Object[], optionale Parameter
     */
    public void nextGeneration(Object... params);
}
