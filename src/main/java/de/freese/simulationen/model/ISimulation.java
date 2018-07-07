/**
 * Created: 26.01.2014
 */

package de.freese.simulationen.model;

import java.awt.Image;

/**
 * Interface für eine Simulationsumgebung.
 *
 * @author Thomas Freese
 */
public interface ISimulation
{
    /**
     * Fügt einen neuen Listener hinzu.
     *
     * @param simulationListener {@link ISimulationListener}
     */
    public void addWorldListener(final ISimulationListener simulationListener);

    /**
     * Höhe in Pixeln.
     *
     * @return int
     */
    public int getHeight();

    /**
     * Liefert das Bild der zuletzt berechneten Generation.
     *
     * @return {@link Image}
     */
    public Image getImage();

    /**
     * Breite in Pixeln.
     *
     * @return int
     */
    public int getWidth();

    /**
     * Berechnet die nächste Generation.
     */
    public void nextGeneration();

    /**
     * Neustart der Simulation.
     */
    public void reset();
}
