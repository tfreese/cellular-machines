/**
 * Created: 08.02.2014
 */
package de.freese.simulationen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.simulationen.ant.AntWorld;
import de.freese.simulationen.ball.BallSimulation;
import de.freese.simulationen.gameoflife.GoFWorld;
import de.freese.simulationen.model.ISimulation;
import de.freese.simulationen.model.SimulationType;
import de.freese.simulationen.wator.WaTorWorld;

/**
 * Consolenprogramm für Bilderstellung.<br>
 * -type wator -cycles 1500 -dir /mnt/sonstiges/simulationen<br>
 * Umwandlung in Film:<br>
 *
 * <pre>
 * ffmpeg -f image2 -r 25  -i gof-%05d.png   -c:v png -r 25 -an             gof.avi
 * ffmpeg -f image2 -r 25  -i wator-%05d.png -c:v png -r 25 -an -f matroska wator.mkv
 * ffmpeg -f image2 -r 250 -i ants-%05d.png  -c:v png -r 25 -an -f matroska ants.mkv
 * </pre>
 *
 * @author Thomas Freese
 */
class SimulationConsole
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationConsole.class);

    /**
     * @param type {@link SimulationType}
     * @param cycles int
     * @param width int
     * @param height int
     * @param path {@link Path}
     */
    public void start(final SimulationType type, final int cycles, final int width, final int height, final Path path)
    {
        int cpus = Runtime.getRuntime().availableProcessors();

        // Jeder CPU-Kern soll ausgelastet werden, wenn die Queue voll ist wird die Grafik im Caller verarbeitet.
        ExecutorService executorService =
                new ThreadPoolExecutor(cpus, cpus, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(cpus), new ThreadPoolExecutor.CallerRunsPolicy());

        try
        {
            ISimulation simulation = switch (type)
            {
                case ANTS -> new AntWorld(width, height);
                case GAME_OF_LIFE -> new GoFWorld(width, height);
                case WATER_TORUS -> new WaTorWorld(width, height);
                case BOUNCING_BALLS -> new BallSimulation(width, height, SimulationEnvironment.getInstance().getAsInt("simulation.delay", 40));

                default -> throw new IllegalStateException("invalid type: " + type);
            };

            Path directory = path.resolve(type.getNameShort());

            if (!Files.exists(directory))
            {
                Files.createDirectories(directory);
            }

            if (Files.exists(directory))
            {
                // Alten Inhalt löschen.
                // Files.list(directory).forEach(System.out::println);
                Files.list(directory).forEach(file -> {
                    try
                    {
                        Files.delete(file);
                    }
                    catch (IOException ex)
                    {
                        throw new RuntimeException(ex);
                    }
                });
            }

            LOGGER.info("Create {} Pictures in {}", cycles, directory);

            simulation.addWorldListener(new SimulationListenerSaveImage("png", directory, type, executorService));

            for (int cycle = 0; cycle < cycles; cycle++)
            {
                simulation.nextGeneration();
            }
        }
        catch (Exception ex)
        {
            LOGGER.error(null, ex);

            System.exit(-1);
        }
        finally
        {
            SimulationEnvironment.shutdown(executorService, LOGGER);
        }

        System.exit(0);
    }
}
