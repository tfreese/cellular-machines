// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import de.freese.simulationen.ObjectPool;

/**
 * BasisModel für die Simulationen.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSimulation implements ISimulation
{
    /**
     *
     */
    private final int height;

    /**
    *
    */
    private Image image;

    /**
     *
     */
    private final Random random;

    /**
     *
     */
    private final List<ISimulationListener> simulationListeners;

    /**
     *
     */
    private final int width;

    /**
     * Erstellt ein neues {@link AbstractSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    public AbstractSimulation(final int width, final int height)
    {
        super();

        this.width = width;
        this.height = height;
        this.random = new Random();
        this.simulationListeners = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * @see de.freese.simulationen.model.ISimulation#addWorldListener(de.freese.simulationen.model.ISimulationListener)
     */
    @Override
    public void addWorldListener(final ISimulationListener simulationListener)
    {
        if (!this.simulationListeners.contains(simulationListener))
        {
            this.simulationListeners.add(simulationListener);
        }
    }

    /**
     * @param creator {@link Supplier}
     * @param activator {@link Consumer}
     * @param <T> Konkreter Typ
     * @return {@link ObjectPool}
     */
    protected <T> ObjectPool<T> createObjectPool(final Supplier<T> creator, final Consumer<T> activator)
    {
        return new ObjectPool<>(creator, activator);
        // FunctionalObjectFactory<T> objectFactory = new FunctionalObjectFactory<>(creator);
        // objectFactory.setActivateConsumer(activator);
        //
        // return ObjectPoolBuilder.create().min(5000).max(getWidth() * getHeight()).registerShutdownHook(true).buildSimplePool(objectFactory);
    }

    /**
     * Feuert das Event, wenn ein Simulations-Durchgang beendet ist.
     */
    protected void fireCompleted()
    {
        updateImage();

        for (ISimulationListener listener : this.simulationListeners)
        {
            listener.completed(this);
        }
    }

    /**
     * @see de.freese.simulationen.model.ISimulation#getHeight()
     */
    @Override
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @see de.freese.simulationen.model.ISimulation#getImage()
     */
    @Override
    public Image getImage()
    {
        return this.image;
    }

    /**
     * @return {@link Random}
     */
    public Random getRandom()
    {
        return this.random;
    }

    /**
     * Liefert die entsprechende Torus-Koordinate.
     *
     * @param size int, Grösse des Simulationsfeldes
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     * @return int
     */
    protected int getTorusKoord(final int size, final int pos, final int offSet)
    {
        if ((pos == 0) && (offSet < 0))
        {
            return size + offSet;
        }

        return ((size + 1) * (pos + offSet)) % size;
    }

    /**
     * @see de.freese.simulationen.model.ISimulation#getWidth()
     */
    @Override
    public int getWidth()
    {
        return this.width;
    }

    /**
     * Liefert die entsprechende Torus-Koordinate.
     *
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsaenderung
     * @return int
     */
    public int getXTorusKoord(final int pos, final int offSet)
    {
        return getTorusKoord(getWidth(), pos, offSet);
    }

    /**
     * Liefert die entsprechende Torus-Koordinate.
     *
     * @param pos int, Aktuelle Position
     * @param offSet int, Positionsänderung
     * @return int
     */
    public int getYTorusKoord(final int pos, final int offSet)
    {
        return getTorusKoord(getHeight(), pos, offSet);
    }

    /**
     * @param image {@link Image}
     */
    protected void setImage(final Image image)
    {
        this.image = image;
    }

    /**
     * Aktualisiert die ImageSource mit den neuen Pixeln.<br>
     * Image wird auf den neuesten Stand gebracht.<br>
     * Wird in der Methode {@link #fireCompleted()} aufgerufen.
     */
    protected abstract void updateImage();
}
