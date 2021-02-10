/**
 * Created: 26.01.2014
 */
package de.freese.simulationen;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.simulationen.model.ISimulation;
import de.freese.simulationen.model.ISimulationListener;

/**
 * Speichert die Bilder der Simulation.
 *
 * @author Thomas Freese
 */
public class SimulationListenerSaveImage implements ISimulationListener
{
    /**
     * @author Thomas Freese
     */
    private final class WriteImageTask implements Runnable
    {
        /**
         *
         */
        private final BufferedImage bufferedImage;

        /**
         *
         */
        private final Path file;

        /**
         * Erstellt ein neues {@link WriteImageTask} Object.
         *
         * @param bufferedImage {@link BufferedImage}
         * @param file {@link Path}
         */
        public WriteImageTask(final BufferedImage bufferedImage, final Path file)
        {
            super();

            this.bufferedImage = Objects.requireNonNull(bufferedImage, "bufferedImage required");
            this.file = Objects.requireNonNull(file, "file required");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            write(this.bufferedImage, this.file);
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationListenerSaveImage.class);

    /**
     *
     */
    private final AtomicInteger counter;

    /**
     *
     */
    private final Path directory;

    /**
     *
     */
    private final Executor executor;

    /**
     *
     */
    private final String filePrefix;

    /**
     *
     */
    private final String format;

    /**
     * Erstellt ein neues {@link SimulationListenerSaveImage} Object.
     *
     * @param format String; JPEG, PNG, BMP, WBMP, GIF
     * @param directory {@link Path}
     * @param filePrefix String
     * @param executor {@link Executor}
     */
    public SimulationListenerSaveImage(final String format, final Path directory, final String filePrefix, final Executor executor)
    {
        super();

        this.format = Objects.requireNonNull(format, "format required");
        this.directory = Objects.requireNonNull(directory, "directory required");
        this.filePrefix = Objects.requireNonNull(filePrefix, "filePrefix required");
        this.executor = Objects.requireNonNull(executor, "executor required");
        this.counter = new AtomicInteger(0);
    }

    /**
     * @see de.freese.simulationen.model.ISimulationListener#completed(de.freese.simulationen.model.ISimulation)
     */
    @Override
    public void completed(final ISimulation simulation)
    {
        Image image = simulation.getImage();
        BufferedImage bufferedImage = null;

        // if (image instanceof ToolkitImage)
        // {
        // bufferedImage = ((ToolkitImage) image).getBufferedImage();
        // }

        if (bufferedImage == null)
        {
            bufferedImage = new BufferedImage(simulation.getWidth(), simulation.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
        }

        Path file = this.directory.resolve(String.format("%s-%05d.%s", this.filePrefix, this.counter.incrementAndGet(), this.format));
        // LOGGER.info("Write {}", file);

        Runnable task = new WriteImageTask(bufferedImage, file);

        if (this.executor != null)
        {
            this.executor.execute(task);
        }
        else
        {
            task.run();
        }
    }

    /**
     * @param bufferedImage {@link BufferedImage}
     * @param file {@link Path}
     */
    private void write(final BufferedImage bufferedImage, final Path file)
    {
        LOGGER.info("Write {}", file);

        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(file)))
        {
            // JPEG, PNG, BMP, WBMP, GIF
            ImageIO.write(bufferedImage, this.format, os);

            os.flush();
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
