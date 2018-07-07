/**
 * Created: 08.02.2014
 */
package de.freese.simulationen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.simulationen.ant.AntWorld;
import de.freese.simulationen.gameoflife.GoFWorld;
import de.freese.simulationen.model.ISimulation;
import de.freese.simulationen.wator.WaTorWorld;

/**
 * Consolenprogramm für Bilderstellung.<br>
 * -cycles 1500 -type wator -dir /mnt/sonstiges/simulationen<br>
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
public class SimulationConsole
{

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationConsole.class);

    /**
     * Liefert die möglichen Optionen der Kommandozeile.<br>
     * Dies sind die JRE Programm Argumente.
     *
     * @return {@link Options}
     */
    private static Options getCommandOptions()
    {
        Options options = new Options();

        // Simulation
        Option option = new Option("type", "Simulation: ants, gof, wator");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);

        // Durchläufe
        option = new Option("cycles", "Anzahl der Simulations-Durchgänge");
        option.setRequired(true);
        option.setArgs(1);
        options.addOption(option);

        // Durchläufe
        option = new Option("size", "arg = Breite  Höhe");
        option.setRequired(true);
        option.setArgs(2);
        options.addOption(option);

        // Verzeichnis
        option = new Option("dir", "Basisverzeichnis für die Bilder. Default: tmp");
        option.setRequired(false);
        option.setArgs(1);
        options.addOption(option);

        return options;
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        // StartParameter auslesen.
        Options options = getCommandOptions();
        CommandLine line = null;

        try
        {
            CommandLineParser parser = new DefaultParser();
            line = parser.parse(options, args);
        }
        catch (Exception ex)
        {
            LOGGER.error(ex.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options, true);

            System.exit(-1);
            return;
        }

        int cpus = Runtime.getRuntime().availableProcessors();
        // ExecutorService executorService = Executors.newCachedThreadPool();
        // ExecutorService executorService =
        // new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(true), new ThreadPoolExecutor.AbortPolicy());
        ExecutorService executorService =
                new ThreadPoolExecutor(cpus, cpus, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(cpus), new ThreadPoolExecutor.CallerRunsPolicy());

        try
        {
            String type = line.getOptionValue("type");
            int cycles = Integer.parseInt(line.getOptionValue("cycles")); // 25 Bilder/Sekunde = 1500/Minute
            String dir = line.getOptionValue("dir");
            String[] size = line.getOptionValues("size");

            int fieldWidth = Integer.parseInt(size[0]);
            int fieldHeight = Integer.parseInt(size[1]);

            // int fieldWidth = Integer.parseInt(SimulationGUI.PROPERTIES.getProperty("simulation.field.width", "100"));
            // int fieldHeight = Integer.parseInt(SimulationGUI.PROPERTIES.getProperty("simulation.field.height", "100"));

            Path directory = null;

            if ((dir == null) || (dir.trim().length() == 0))
            {
                directory = Paths.get(System.getProperty("java.io.tmpdir"), type);
            }
            else
            {
                directory = Paths.get(dir, type);
            }

            ISimulation simulation = null;

            switch (type)
            {
                case "ants":
                    simulation = new AntWorld(fieldWidth, fieldHeight);
                    break;
                case "gof":
                    simulation = new GoFWorld(fieldWidth, fieldHeight);
                    break;
                case "wator":
                    simulation = new WaTorWorld(fieldWidth, fieldHeight);
                    break;

                default:
                    throw new IllegalStateException("invalid type: " + type);
            }

            if (!Files.exists(directory))
            {
                Files.createDirectories(directory);
            }

            if (!Files.isWritable(directory))
            {
                throw new IllegalArgumentException("directory not writeable: " + directory);
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
            // simulation.addWorldListener(new SimulationListenerSaveImageTest("png", directory, type, executorService));

            for (int cycle = 0; cycle < cycles; cycle++)
            {
                simulation.nextGeneration();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        finally
        {
            try
            {
                executorService.awaitTermination(120, TimeUnit.SECONDS);
            }
            catch (Exception ex2)
            {
                // Ignore
            }

            executorService.shutdown();
        }

        System.exit(0);
    }
}
