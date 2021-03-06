// Created: 04.03.2021
package de.freese.simulationen;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class SimulationEnvironment
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SimulationEnvironmentHolder
    {
        /**
         *
         */
        private static final SimulationEnvironment INSTANCE = new SimulationEnvironment();

        /**
         * Erstellt ein neues {@link SimulationEnvironmentHolder} Object.
         */
        private SimulationEnvironmentHolder()
        {
            super();
        }
    }

    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationEnvironment.class);

    /**
     * @return {@link SimulationEnvironment}
     */
    public static SimulationEnvironment getInstance()
    {
        return SimulationEnvironmentHolder.INSTANCE;
    }

    /**
     * Shutdown des {@link ExecutorService}.
     *
     * @param executorService {@link ExecutorService}
     * @param logger {@link Logger}
     */
    public static void shutdown(final ExecutorService executorService, final Logger logger)
    {
        logger.info("shutdown ExecutorService");

        if (executorService == null)
        {
            logger.warn("ExecutorService is null");

            return;
        }

        executorService.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                logger.warn("Timed out while waiting for ExecutorService");

                // Cancel currently executing tasks.
                for (Runnable remainingTask : executorService.shutdownNow())
                {
                    if (remainingTask instanceof Future)
                    {
                        ((Future<?>) remainingTask).cancel(true);
                    }
                }

                // Wait a while for tasks to respond to being cancelled.
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS))
                {
                    logger.error("ExecutorService did not terminate");
                }
                else
                {
                    logger.info("ExecutorService terminated");
                }
            }
            else
            {
                logger.info("ExecutorService terminated");
            }
        }
        catch (InterruptedException iex)
        {
            logger.warn("Interrupted while waiting for ExecutorService");

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    /**
    *
    */
    private Properties properties = new Properties();

    /**
    *
    */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * @param property String
     * @param nullDefault boolean
     * @return boolean
     */
    public boolean getAsBoolean(final String property, final boolean nullDefault)
    {
        String value = this.properties.getProperty(property);

        return value != null ? Boolean.parseBoolean(value) : nullDefault;
    }

    /**
     * @param property String
     * @param nullDefault int
     * @return int
     */
    public int getAsInt(final String property, final int nullDefault)
    {
        String value = this.properties.getProperty(property);

        return value != null ? Integer.parseInt(value) : nullDefault;
    }

    /**
     * @return {@link ScheduledExecutorService}
     */
    public ScheduledExecutorService getScheduledExecutorService()
    {
        return this.scheduledExecutorService;
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    public void init() throws Exception
    {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("simulation.properties"))
        {
            this.properties.load(inputStream);
        }

        this.scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     *
     */
    public void shutdown()
    {
        shutdown(this.scheduledExecutorService, LOGGER);
    }
}
