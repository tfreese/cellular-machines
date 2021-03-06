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
@FunctionalInterface
public interface SimulationListener
{
    /**
     * Wird aufgerufen, wenn ein Simulations-Durchgang beendet ist.
     *
     * @param simulation {@link Simulation}
     */
    public void completed(Simulation simulation);
}
