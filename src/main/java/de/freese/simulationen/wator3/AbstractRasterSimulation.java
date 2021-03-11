// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.wator3;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
    private Set<RasterCell> cells;

    /**
     *
     */
    private final Image image;

    /**
     *
     */
    private final MemoryImageSource imageSource;

    /**
     * Pixel-Backend für {@link MemoryImageSource} und {@link Image}.
     */
    private final int[] pixelsRGB;

    /**
     *
     */
    private RasterCell[][] raster;

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
        this.raster = new RasterCell[width][height];

        // Arrays.fill(this.pixelsRGB, getNullCellColor().getRGB());
        // Arrays.parallelSetAll(this.pixelsRGB, i -> getNullCellColor().getRGB());

        this.imageSource = new MemoryImageSource(width, height, this.pixelsRGB, 0, width);
        this.imageSource.setAnimated(true);
        this.imageSource.setFullBufferUpdates(false);

        this.image = Toolkit.getDefaultToolkit().createImage(this.imageSource);
        // java.awt.Component.createImage(this.imageSource);
    }

    /**
     * Einmaliges befüllen des Rasters.
     *
     * @param cellSupplier {@link Supplier}
     */
    protected final void fillRaster(final Supplier<RasterCell> cellSupplier)
    {
        Set<RasterCell> set = Collections.synchronizedSet(new HashSet<>());

        // @formatter:off
        IntStream.range(0, getHeight())
            .parallel()
            .forEach(y ->
            {
                for (int x = 0; x < getWidth(); x++)
                {
                    RasterCell cell = cellSupplier.get();

                    if(cell instanceof AbstractRasterCell)
                    {
                        ((AbstractRasterCell) cell).setXY(x, y);
                    }

                    this.raster[x][y] = cell;

                    set.add(cell);
                }
            })
            ;
        // @formatter:on

        this.cells = Collections.unmodifiableSet(new HashSet<>(set));
    }

    /**
     * @param x int
     * @param y int
     * @return {@link RasterCell}
     */
    protected RasterCell getCell(final int x, final int y)
    {
        return this.raster[x][y];
    }

    /**
     * Liefert einen parallelen {@link Stream} für die Zellen.
     *
     * @return {@link Stream}
     */
    protected Stream<RasterCell> getCellStream()
    {
        // Der Stream vom Raster bildet Wellenfronten, da immer von oben links angefangen wird zu rechnen.
        // return Stream.of(this.raster).parallel().flatMap(Stream::of).parallel();

        return this.cells.stream().parallel();
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
                }
            })
            ;
        // @formatter:on

        fireCompleted();
    }

    /**
     * Reset einer Zelle des Rasters.
     *
     * @param x int
     * @param y int
     */
    protected void reset(final int x, final int y)
    {
        // Empty
    }

    /**
     * Ändert die Pixel-Farbe an den Koordinaten.
     *
     * @param x int
     * @param y int
     * @param color {@link Color}
     */
    protected void setCellColor(final int x, final int y, final Color color)
    {
        this.pixelsRGB[x + (y * getWidth())] = color.getRGB();
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
