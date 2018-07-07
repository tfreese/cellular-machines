// Created: 17.10.2009
/**
 * 17.10.2009
 */
package de.freese.simulationen.model;

/**
 * Listener für Veränderungen einer Simulation.
 *
 * @author Thomas Freese
 */
public interface ISimulationListener
{
    // /**
    // * Wird vom Model aufgerufen, wenn an den Koordinaten eine neue Zelle verändert wird.
    // *
    // * @param x int
    // * @param y int
    // * @param color {@link Color}
    // */
    // public void cellChanged(int x, int y, Color color);

    /**
     * Wird aufgerufen, wenn ein Simulations-Durchgang beendet ist.
     *
     * @param simulation {@link ISimulation}
     */
    public void completed(ISimulation simulation);
}
