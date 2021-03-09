// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.wator2;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;
import java.util.stream.IntStream;
import de.freese.simulationen.model.AbstractSimulation;

/**
 * BasisModel für Simulationen mit Zellen als Pixeln.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRasterSimulation extends AbstractSimulation
{
    /**
     *
     */
    private final Image image;

    /**
     *
     */
    private final MemoryImageSource imageSource;

    /**
     *
     */
    private final int[] pixelsRGB;

    /**
     *
     */
    private Raster raster;

    /**
     * Erstellt ein neues {@link AbstractRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    protected AbstractRasterSimulation(final int width, final int height)
    {
        super(width, height);

        this.pixelsRGB = new int[width * height];
        this.raster = new Raster(width, height);

        // Arrays.fill(this.pixels, getNullCellColor().getRGB());
        Arrays.parallelSetAll(this.pixelsRGB, i -> getNullCellColor().getRGB());

        this.imageSource = new MemoryImageSource(width, height, this.pixelsRGB, 0, width);
        this.imageSource.setAnimated(true);
        this.imageSource.setFullBufferUpdates(false);

        this.image = Toolkit.getDefaultToolkit().createImage(this.imageSource);
        // java.awt.Component.createImage(this.imageSource);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#getImage()
     */
    @Override
    public Image getImage()
    {
        return this.image;
    }

    /**
     * Farbe für nicht vorhandene Zellen.
     *
     * @return {@link Color}
     */
    protected abstract Color getNullCellColor();

    /**
     * Pixel-Backend für {@link MemoryImageSource} und {@link Image}.
     *
     * @return int[]
     */
    public int[] getPixelsRGB()
    {
        return this.pixelsRGB;
    }

    /**
     * @return {@link Raster}
     */
    protected Raster getRaster()
    {
        return this.raster;
    }

    /**
     * Initialisierung des Simulationsfeldes.
     */
    protected void initialize()
    {
        // @formatter:off
        IntStream.range(0, getHeight())
            .parallel()
            .forEach(y ->
            {
                for (int x = 0; x < getWidth(); x++)
                {
                    initialize(x, y);
                }
            })
            ;
        // @formatter:on
    }

    /**
     * Initialisierung einer Zelle des Simulationsfeldes.
     *
     * @param x int
     * @param y int
     */
    protected abstract void initialize(int x, int y);

    /**
     * @see de.freese.simulationen.model.Simulation#reset()
     */
    @Override
    public void reset()
    {
        // @formatter:off
        IntStream.range(0, getHeight())
            .parallel()
            .forEach(y ->
            {
                for (int x = 0; x < getWidth(); x++)
                {
                    reset(x, y);
                    getRaster().setCell(x, y, null);
                }
            })
            ;
        // @formatter:on

        initialize();
        fireCompleted();
    }

    /**
     * Reset einer Zelle des Simulationsfeldes.
     *
     * @param x int
     * @param y int
     */
    protected abstract void reset(int x, int y);

    /**
     * Ändert die Pixel-Farbe an den Koordinaten.
     *
     * @param x int
     * @param y int
     * @param color {@link Color}
     */
    protected void setCellColor(final int x, final int y, final Color color)
    {
        getPixelsRGB()[x + (y * getWidth())] = color.getRGB();
    }

    /**
     * Ändert die Pixel-Farbe an den Koordinaten.
     *
     * @param x int
     * @param y int
     * @param cell {@link RasterCell}
     */
    protected void setCellColor(final int x, final int y, final RasterCell cell)
    {
        setCellColor(x, y, cell != null ? cell.getColor() : getNullCellColor());
    }

    /**
     * @param raster {@link Raster}
     */
    protected void setRaster(final Raster raster)
    {
        this.raster = raster;
    }

    /**
     * @see de.freese.simulationen.model.AbstractSimulation#updateImage()
     */
    @Override
    protected void updateImage()
    {
        this.imageSource.newPixels();
        // this.imageSource.newPixels(0, 0, getWidth(), getHeight());
    }
}
