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
public interface Cell
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
     * Berechnet die nächste Generation.
     */
    public void nextGeneration();
}
