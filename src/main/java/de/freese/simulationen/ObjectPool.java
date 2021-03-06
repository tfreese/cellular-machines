/**
 * Created: 07.07.2018
 */

package de.freese.simulationen;

import java.lang.reflect.ParameterizedType;
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

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "ObjectPool-" + getClass().getSimpleName()));
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
        // getLock().lock();

        // try
        // {
        T object = getQueue().poll();

        if (object == null)
        {
            object = create();
        }

        activate(object);
        this.counterActive++;

        return object;
        // }
        // finally
        // {
        // getLock().unlock();
        // }
    }

    /**
     * @return Object
     */
    protected abstract T create();

    // /**
    // * @return {@link ReentrantLock}
    // */
    // protected ReentrantLock getLock()
    // {
    // return this.lock;
    // }

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
     * @return Class<T>
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getObjectClazz()
    {
        Class<T> objectClazz = null;

        try
        {
            objectClazz = tryDetermineObjectClazz();
        }
        catch (ClassCastException ccex)
        {
            T object = borrowObject();

            if (object != null)
            {
                objectClazz = (Class<T>) object.getClass();

                returnObject(object);
            }
        }

        return objectClazz;
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
    public void returnObject(final T object)
    {
        if (object == null)
        {
            return;
        }

        // getLock().lock();
        //
        // try
        // {
        getQueue().offer(object);
        this.counterActive--;
        // }
        // finally
        // {
        // getLock().unlock();
        // }
    }

    /**
     * Herunterfahren des Pools, alternativ auch über ShutdownHook.
     */
    protected void shutdown()
    {
        // getLock().lock();
        //
        // try
        // {
        String objectClazzName = getObjectClazz().getSimpleName();

        getLogger().info("Close Pool<{}> with {} idle and {} aktive Objects", objectClazzName, getNumIdle(), getNumActive());

        getQueue().clear();
        this.counterActive = 0;
        // }
        // finally
        // {
        // getLock().unlock();
        // }
    }

    /**
     * Das hier funktioniert nur, wenn die erbende Klasse nicht auch generisch ist !<br>
     * Z.B.: public class MyObjectPool extends AbstractObjectPool<Integer><br>
     *
     * @return Class
     * @throws ClassCastException Falls was schief geht.
     */
    @SuppressWarnings("unchecked")
    protected Class<T> tryDetermineObjectClazz() throws ClassCastException
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
}
