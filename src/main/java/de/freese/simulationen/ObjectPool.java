/**
 * Created: 07.07.2018
 */

package de.freese.simulationen;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple ObjectPool.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public abstract class ObjectPool<T>
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectPool.class);

    /**
     *
     */
    private int counterActive;

    // /**
    // *
    // */
    // private final ReentrantLock lock = new ReentrantLock(true);

    /**
     *
     */
    private final Queue<T> queue;

    /**
     * Erstellt ein neues {@link ObjectPool} Object.
     */
    protected ObjectPool()
    {
        super();

        // this.queue = new LinkedList<>();
        this.queue = new LinkedBlockingDeque<>(Integer.MAX_VALUE);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ObjectPool"));
    }

    /**
     * @param object Object
     */
    protected abstract void activate(final T object);

    /**
     * Liefert ein Objekt aus dem Pool.
     *
     * @return Object
     */
    public T borrowObject()
    {
        // this.lock.lock();
        //
        // try
        // {
        T object = this.queue.poll();

        if (object == null)
        {
            object = create();
        }

        activate(object);
        this.counterActive++;

        return object;
        // }
        // finally
        //
        // {
        // this.lock.unlock();
        // }
    }

    /**
     * @return Object
     */
    protected abstract T create();

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
     * Übergibt ein Objekt zurück in den Pool.
     *
     * @param object Object
     */
    public void returnObject(final T object)
    {
        if (object == null)
        {
            return;
        }

        // this.lock.lock();
        //
        // try
        // {
        this.queue.offer(object);
        this.counterActive--;
        // }
        // finally
        // {
        // this.lock.unlock();
        // }
    }

    /**
     * Herunterfahren des Pools, alternativ auch über ShutdownHook.
     */
    protected void shutdown()
    {
        // this.lock.lock();
        //
        // try
        // {
        String objectClazzName = borrowObject().getClass().getSimpleName();

        LOGGER.info("Close Pool<{}> with {} idle and {} active Objects", objectClazzName, this.queue.size(), getNumActive());

        this.queue.clear();
        this.counterActive = 0;
        // }
        // finally
        // {
        // this.lock.unlock();
        // }
    }
}
