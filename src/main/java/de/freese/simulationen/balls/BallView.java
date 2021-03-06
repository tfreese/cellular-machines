// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.balls;

import java.awt.BorderLayout;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import de.freese.simulationen.AbstractSimulationView;
import de.freese.simulationen.ScheduledFutureAwareRunnable;
import de.freese.simulationen.SimulationCanvas;

/**
 * View für die Ball Simulation.
 *
 * @author Thomas Freese
 */
public class BallView extends AbstractSimulationView<BallSimulation>
{
    /**
     * @see de.freese.simulationen.AbstractSimulationView#createSimulation(int, int)
     */
    @Override
    protected BallSimulation createSimulation(final int fieldWidth, final int fieldHeight)
    {
        return new BallSimulation(fieldWidth, fieldHeight, super.getDelay() * 5);
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#initialize(int, int)
     */
    @Override
    public void initialize(final int fieldWidth, final int fieldHeight)
    {
        super.initialize(fieldWidth, fieldHeight);

        SimulationCanvas canvas = new SimulationCanvas(getSimulation());
        getMainPanel().add(canvas, BorderLayout.CENTER);
    }

    /**
     * @see de.freese.simulationen.AbstractSimulationView#start()
     */
    @Override
    protected void start()
    {
        super.start();

        // Die Simulation würde ewig weitergehen, auch wenn die Bälle schon aml Boden liegen.
        BooleanSupplier exitCondition = getSimulation()::isFinished;
        Runnable task = this::stop;

        ScheduledFutureAwareRunnable futureAwareRunnable = new ScheduledFutureAwareRunnable(exitCondition, task, "Ball-Simulation");

        ScheduledFuture<?> scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(futureAwareRunnable, 3, 3, TimeUnit.SECONDS);
        futureAwareRunnable.setScheduledFuture(scheduledFuture);
    }
}
