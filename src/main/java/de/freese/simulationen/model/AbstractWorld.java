// Created: 18.09.2009
/**
 * 18.09.2009
 */
package de.freese.simulationen.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import de.freese.simulationen.SimulationGUI;

/**
 * BasisModel für Simulationen mit Zellen als Pixeln.
 *
 * @author Thomas Freese
 */
public abstract class AbstractWorld extends AbstractSimulation
{
    /**
     *
     */
    private final ICell[][] cells;

    /**
    *
    */
    private final MemoryImageSource imageSource;

    /**
     *
     */
    private final int[] pixelsRGB;

    /**
     * Erstellt ein neues {@link AbstractWorld} Object.
     *
     * @param width int
     * @param height int
     */
    protected AbstractWorld(final int width, final int height)
    {
        super(width, height);

        this.cells = new ICell[width][height];
        this.pixelsRGB = new int[width * height];
        // Arrays.fill(this.pixels, getNullCellColor().getRGB());
        Arrays.parallelSetAll(this.pixelsRGB, i -> getNullCellColor().getRGB());

        this.imageSource = new MemoryImageSource(width, height, this.pixelsRGB, 0, width);
        this.imageSource.setAnimated(true);
        this.imageSource.setFullBufferUpdates(false);
        setImage(Toolkit.getDefaultToolkit().createImage(this.imageSource));
        // java.awt.Component.createImage(this.imageSource);
    }

    /**
     * Ändert die Pixel-Farbe an der Position der Zelle.
     *
     * @param x int
     * @param y int
     * @param cell {@link ICell}
     */
    public void cellColorChanged(final int x, final int y, final ICell cell)
    {
        Color color = cell != null ? cell.getColor() : getNullCellColor();
        getPixelsRGB()[x + (y * getWidth())] = color.getRGB();
    }

    /**
     * Liefert die Zelle der Koordinaten.
     *
     * @param x int
     * @param y int
     * @return {@link ICell}
     */
    public ICell getCell(final int x, final int y)
    {
        return getCells()[x][y];
    }

    /**
     * @return {@link ICell}[][]
     */
    protected ICell[][] getCells()
    {
        return this.cells;
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
     * @see #getImage()
     * @return int[]
     */
    public int[] getPixelsRGB()
    {
        return this.pixelsRGB;
    }

    /**
     * Initialisierung des Simulationsfeldes.
     */
    protected void initialize()
    {
        ForkJoinPool forkJoinPool = SimulationGUI.FORK_JOIN_POOL;

        ForkJoinInitWorldAction action = new ForkJoinInitWorldAction(this, 0, getHeight() - 1);
        forkJoinPool.invoke(action);

        // Warten bis fertich.
        action.join();

        // for (int x = 0; x < getWidth(); x++)
        // {
        // for (int y = 0; y < getHeight(); y++)
        // {
        // initialize(x, y);
        // }
        // }
    }

    /**
     * Initialisierung einer Zelle des Simulationsfeldes.
     *
     * @param x int
     * @param y int
     */
    protected abstract void initialize(int x, int y);

    /**
     * @see de.freese.simulationen.model.ISimulation#reset()
     */
    @Override
    public void reset()
    {
        ForkJoinPool forkJoinPool = SimulationGUI.FORK_JOIN_POOL;

        ForkJoinResetWorldAction action = new ForkJoinResetWorldAction(this, 0, getHeight() - 1);
        forkJoinPool.invoke(action);

        // Warten bis fertich.
        action.join();

        // for (int x = 0; x < getWidth(); x++)
        // {
        // for (int y = 0; y < getHeight(); y++)
        // {
        // reset(x, y);
        // setCell(null, x, y);
        // // this.cells[x][y] = null;
        // // initialize(x, y);
        // }
        // }

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
     * @param cell {@link ICell}
     * @param x int
     * @param y int
     */
    public void setCell(final ICell cell, final int x, final int y)
    {
        getCells()[x][y] = cell;

        cellColorChanged(x, y, cell);
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
