/**
 * Created: 07.07.2018
 */

package de.freese.simulationen;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple ObjectPool.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class ObjectPool<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectPool.class);

    /**
     *
     */
    private final Queue<T> queue;

    /**
     *
     */
    private final Supplier<T> createFunction;

    /**
     *
     */
    private final Consumer<T> activateFunction;

    /**
     *
     */
    private int counterActive = 0;
    /**
     *
     */

    private final String objectClassName;

    /**
     * Erstellt ein neues {@link ObjectPool} Object.
     *
     * @param createFunction {@link Supplier}
     * @param activateFunction {@link Consumer}
     */
    public ObjectPool(final Supplier<T> createFunction, final Consumer<T> activateFunction)
    {
        super();

        this.queue = new LinkedList<>();

        this.createFunction = Objects.requireNonNull(createFunction, "createFunction required");
        this.activateFunction = Objects.requireNonNull(activateFunction, "activateFunction required");

        this.objectClassName = createFunction.get().getClass().getSimpleName();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ObjectPool-" + this.objectClassName));
    }

    /**
     * Liefert ein Objekt aus dem Pool.
     *
     * @return Object
     */
    public synchronized T borrowObject()
    {
        T object = getQueue().poll();

        if (object == null)
        {
            object = getCreateFunction().get();
        }

        getActivateFunction().accept(object);
        this.counterActive++;

        return object;
    }

    /**
     * @return {@link Consumer}
     */
    protected Consumer<T> getActivateFunction()
    {
        return this.activateFunction;
    }

    /**
     * @return {@link Supplier}
     */
    protected Supplier<T> getCreateFunction()
    {
        return this.createFunction;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * Liefert die Anzahl der aktiven, dem Pool entnommenen, Objekte.
     *
     * @return int
     */
    public int getNumActive()
    {
        return this.counterActive;
    }

    /**
     * Liefert die Anzahl der zur Verfügung stehenden, im Pool vorhandene, Objekte.
     *
     * @return int
     */
    public int getNumIdle()
    {
        return getQueue().size();
    }

    /**
     * @return {@link Queue}<T>
     */
    protected Queue<T> getQueue()
    {
        return this.queue;
    }

    /**
     * Übergibt ein Objekt zurück in den Pool.
     *
     * @param object Object
     */
    public synchronized void returnObject(final T object)
    {
        getQueue().offer(object);
        this.counterActive--;
    }

    /**
     * Herunterfahren des Pools, alternativ auch über ShutdownHook.
     */
    private void shutdown()
    {
        getLogger().info("Close Pool<{}> with {} idle and {} aktive Objects", this.objectClassName, getNumIdle(), getNumActive());

        getQueue().clear();
        // while (getQueue().size() > 0)
        // {
        // T object = getQueue().poll();
        //
        // if (object == null)
        // {
        // continue;
        // }
        //
        // // Object destroy
        // }
    }
}
