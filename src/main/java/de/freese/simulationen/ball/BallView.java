// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.ball;

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
     * @see de.freese.simulationen.AbstractSimulationView#createModel(int, int)
     */
    @Override
    protected BallSimulation createModel(final int fieldWidth, final int fieldHeight)
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

        // Die Bälle würden ewig am Boden weiter rollen, wenn die Simulation nicht gestoppt wird.
        BooleanSupplier exitCondition = getSimulation()::isFinished;
        Runnable task = () -> {
            if (exitCondition.getAsBoolean())
            {
                stop();
            }
        };

        ScheduledFutureAwareRunnable futureAwareRunnable = new ScheduledFutureAwareRunnable(task, exitCondition, "Ball-Simulation");

        ScheduledFuture<?> scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(futureAwareRunnable, 3, 3, TimeUnit.SECONDS);
        futureAwareRunnable.setScheduledFuture(scheduledFuture);
    }

    // /**
    // * @see de.freese.simulationen.AbstractSimulationView#step()
    // */
    // @Override
    // protected void step()
    // {
    // super.step();
    //
    // if (getSimulation().isFinished())
    // {
    // stop();
    // }
    // }

    // /**
    // * Die Bälle mögen es etwas schneller.
    // *
    // * @see de.freese.simulationen.AbstractSimulationView#getDelay()
    // */
    // @Override
    // protected int getDelay()
    // {
    // int delay = super.getDelay() / 5;
    //
    // return Math.max(delay, 1);
    // }
}
