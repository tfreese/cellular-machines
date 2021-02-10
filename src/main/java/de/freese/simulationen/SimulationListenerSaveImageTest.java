/**
 * Created: 26.01.2014
 */
package de.freese.simulationen;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.ISimulation;
import de.freese.simulationen.model.ISimulationListener;

/**
 * Speichert die Bilder der Simulation.
 *
 * @author Thomas Freese
 */
public class SimulationListenerSaveImageTest implements ISimulationListener
{
    /**
     * @author Thomas Freese
     */
    private final class QueueWorker implements Runnable
    {
        /**
         *
         */
        private final BlockingQueue<Path> queue;

        /**
         * Erzeugt eine neue Instanz von {@link QueueWorker}.
         *
         * @param queue {@link BlockingQueue}
         */
        private QueueWorker(final BlockingQueue<Path> queue)
        {
            super();

            this.queue = Objects.requireNonNull(queue, "queue required");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    // Path rawFile = this.queue.take();
                    Path rawFile = this.queue.poll(10000, TimeUnit.MILLISECONDS);

                    if (rawFile == null)
                    {
                        LOGGER.info("exit");
                        break;
                    }

                    // LOGGER.info("retrieve {}", rawFile);
                    convertToPicture(rawFile);
                }
                catch (Exception ex)
                {
                    // getLogger().error(null, ex);
                }
            }
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationListenerSaveImageTest.class);

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
     */
    private final String format;

    /**
     *
     */
    private final LinkedBlockingQueue<Path> queue = new LinkedBlockingQueue<>();

    /**
     * Erstellt ein neues {@link SimulationListenerSaveImageTest} Object.
     *
     * @param format String; JPEG, PNG, BMP, WBMP, GIF
     * @param directory {@link Path}
     * @param filePrefix String
     * @param executor {@link Executor}
     */
    public SimulationListenerSaveImageTest(final String format, final Path directory, final String filePrefix, final Executor executor)
    {
        super();

        this.format = Objects.requireNonNull(format, "format required");
        this.directory = Objects.requireNonNull(directory, "directory required");
        this.filePrefix = Objects.requireNonNull(filePrefix, "filePrefix required");
        this.executor = Objects.requireNonNull(executor, "executor required");
        this.counter = new AtomicInteger(0);

        for (int i = 0; i < (Runtime.getRuntime().availableProcessors() - 1); i++)
        {
            this.executor.execute(new QueueWorker(this.queue));
        }
    }

    /**
     * @see de.freese.simulationen.model.ISimulationListener#completed(de.freese.simulationen.model.ISimulation)
     */
    @Override
    public void completed(final ISimulation simulation)
    {
        Path rawFile = this.directory.resolve(String.format("%s-%05d.%s", this.filePrefix, this.counter.incrementAndGet(), "pixels"));

        LOGGER.info("Write {}", rawFile);

        // // Test: Keine fertigen Bilder, sondern erst mal nur die Rohdaten schreiben.
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(rawFile))))
        {
            // Größe schreiben: Breite x Höhe
            output.writeInt(simulation.getWidth());
            output.writeInt(simulation.getHeight());

            int[] pixelsRGB = ((AbstractWorld) simulation).getPixelsRGB();

            for (int pixel : pixelsRGB)
            {
                output.writeInt(pixel);
            }
            //
            // ByteBuffer byteBuffer = ByteBuffer.allocate(pixelsRGB.length * 4);
            // IntBuffer intBuffer = byteBuffer.asIntBuffer();
            // intBuffer.put(pixelsRGB);
            //
            // byte[] pixels = byteBuffer.array();
            // output.write(pixels);
            //
            //
            // Arrays.stream(pixelsRGB).forEach(pixel -> {
            // try
            // {
            // output.writeInt(pixel);
            // }
            // catch (IOException ioex)
            // {
            // throw new RuntimeException(ioex);
            // }
            // });

            output.flush();
        }
        catch (IOException ioex)
        {
            LOGGER.error(ioex.getMessage());
        }

        this.queue.add(rawFile);
    }

    /**
     * @param rawFile {@link Path}
     */
    private void convertToPicture(final Path rawFile)
    {
        // String fileName = rawFile.getFileName().toString().split("[.]")[0];
        // Path imageFile = rawFile.resolveSibling(fileName + "." + this.format);

        Path imageFile = Paths.get(rawFile.toString().replace("pixels", this.format));

        LOGGER.info("Write {}", imageFile);

        try (DataInputStream input = new DataInputStream(new BufferedInputStream(Files.newInputStream(rawFile))))
        {
            int width = input.readInt();
            int height = input.readInt();

            int[] pixels = new int[input.available() / 4];

            for (int i = 0; i < pixels.length; i++)
            {
                pixels[i] = input.readInt();
            }

            // byte[] pixels = new byte[input.available()];
            // input.readFully(pixels);

            MemoryImageSource imageSource = new MemoryImageSource(width, height, pixels, 0, width);
            Image image = Toolkit.getDefaultToolkit().createImage(imageSource);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            try (OutputStream os = Files.newOutputStream(imageFile))
            {
                // JPEG, PNG, BMP, WBMP, GIF
                ImageIO.write(bufferedImage, this.format, os);

                os.flush();
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
