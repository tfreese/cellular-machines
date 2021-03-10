// Created: 09.03.2021
package de.freese.simulationen.wator2;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import de.freese.simulationen.ObjectPool;
import de.freese.simulationen.wator.SharkCell;

/**
 * Model der WaTor-Simulation.<br>
 * http://de.academic.ru/dic.nsf/dewiki/1492493
 *
 * @author Thomas Freese
 */
public class WaterRasterSimulation extends AbstractRasterSimulation
{
    /**
     * Brut-Energie der Fische.
     */
    private int fishBreedEnergy = 5;

    /**
     * Start-Energie der Fische.
     */
    private int fishStartEnergy = 1;

    /**
    *
    */
    private final ObjectPool<FishRasterCell> objectPoolFish;

    /**
        *
        */
    private final ObjectPool<SharkRasterCell> objectPoolShark;

    /**
    *
    */
    private Raster rasterOld;

    /**
     * Brut-Energie der Haie.
     */
    private int sharkBreedEnergy = 15;

    /**
     * Start-Energie der Haie.
     */
    private int sharkStartEnergy = 10;

    /**
     * Start-Energie der Haie.
     */
    private int sharkStarveEnergy = 0;

    /**
     * Erstellt ein neues {@link WaterRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    public WaterRasterSimulation(final int width, final int height)
    {
        super(width, height);

        this.rasterOld = new Raster(width, height);

        this.objectPoolFish = new ObjectPool<>()
        {
            /**
             * @see de.freese.simulationen.ObjectPool#activate(java.lang.Object)
             */
            @Override
            protected void activate(final FishRasterCell object)
            {
                object.setXY(-1, -1);
                object.setEnergy(getFishStartEnergy());
                object.setEdited(false);
            }

            /**
             * @see de.freese.simulationen.ObjectPool#create()
             */
            @Override
            protected FishRasterCell create()
            {
                return new FishRasterCell(WaterRasterSimulation.this);
            }
        };

        this.objectPoolShark = new ObjectPool<>()
        {
            /**
             * @see de.freese.simulationen.ObjectPool#activate(java.lang.Object)
             */
            @Override
            protected void activate(final SharkRasterCell object)
            {
                object.setXY(-1, -1);
                object.setEnergy(getSharkStartEnergy());
                object.setEdited(false);
            }

            /**
             * @see de.freese.simulationen.ObjectPool#create()
             */
            @Override
            protected SharkRasterCell create()
            {
                return new SharkRasterCell(WaterRasterSimulation.this);
            }
        };

        initialize();
    }

    /**
     * Brut-Energie der Fische.
     *
     * @return int
     */
    public int getFishBreedEnergy()
    {
        return this.fishBreedEnergy;
    }

    /**
     * Start-Energie der Fische.
     *
     * @return int
     */
    public int getFishStartEnergy()
    {
        return this.fishStartEnergy;
    }

    /**
     * @see de.freese.simulationen.wator2.AbstractRasterSimulation#getNullCellColor()
     */
    @Override
    protected Color getNullCellColor()
    {
        return Color.BLACK;
    }

    /**
     * @return {@link ObjectPool}<FishRasterCell>
     */
    protected ObjectPool<FishRasterCell> getObjectPoolFish()
    {
        return this.objectPoolFish;
    }

    /**
     * @return {@link ObjectPool}<SharkRasterCell>
     */
    protected ObjectPool<SharkRasterCell> getObjectPoolShark()
    {
        return this.objectPoolShark;
    }

    /**
     * @return {@link Raster}
     */
    protected Raster getRasterOld()
    {
        return this.rasterOld;
    }

    /**
     * Brut-Energie der Haie.
     *
     * @return int
     */
    public int getSharkBreedEnergy()
    {
        return this.sharkBreedEnergy;
    }

    /**
     * Start-Energie der Haie.
     *
     * @return int
     */
    public int getSharkStartEnergy()
    {
        return this.sharkStartEnergy;
    }

    /**
     * Sterbe-Energie der Haie.
     *
     * @return int
     */
    public int getSharkStarveEnergy()
    {
        return this.sharkStarveEnergy;
    }

    /**
     * @see de.freese.simulationen.wator2.AbstractRasterSimulation#initialize(int, int)
     */
    @Override
    protected void initialize(final int x, final int y)
    {
        // Zufällige Platzierung.
        int type = getRandom().nextInt(10);

        RasterCell cell = switch (type)
        {
            case 3 -> getObjectPoolFish().borrowObject();
            case 6 -> getObjectPoolShark().borrowObject();

            default -> null;
        };

        if (cell != null)
        {
            cell.moveTo(x, y, getRaster());
        }
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        Arrays.parallelSetAll(getPixelsRGB(), i -> getNullCellColor().getRGB());

        // Raster tauschen
        Raster r = this.rasterOld;
        this.rasterOld = getRaster();
        setRaster(r);

        // Zuerst nur die Haie verarbeiten
        // @formatter:off
//        new HashSet<>(this.rasterOld.getCells()).stream()
//            .parallel()
//            .filter(SharkRasterCell.class::isInstance)
//            .forEach(RasterCell::nextGeneration)
//            ;
        // @formatter:on

        // Dann erst die Fische
        // @formatter:off
        new HashSet<>(this.rasterOld.getCells()).stream()
            .parallel()
            .filter(FishRasterCell.class::isInstance)
            .forEach(RasterCell::nextGeneration)
            ;
        // @formatter:on

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.wator2.AbstractRasterSimulation#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        RasterCell cell = getRaster().getCell(x, y);

        if (cell instanceof FishRasterCell)
        {
            getObjectPoolFish().returnObject((FishRasterCell) cell);
        }
        else if (cell instanceof SharkCell)
        {
            getObjectPoolShark().returnObject((SharkRasterCell) cell);
        }
    }

    /**
     * Brut-Energie der Fische.
     *
     * @param fishBreedEnergy int
     */
    public void setFishBreedEnergy(final int fishBreedEnergy)
    {
        this.fishBreedEnergy = fishBreedEnergy;
    }

    /**
     * Start-Energie der Fische.
     *
     * @param fishStartEnergy int
     */
    public void setFishStartEnergy(final int fishStartEnergy)
    {
        this.fishStartEnergy = fishStartEnergy;
    }

    /**
     * Brut-Energie der Haie.
     *
     * @param sharkBreedEnergy int
     */
    public void setSharkBreedEnergy(final int sharkBreedEnergy)
    {
        this.sharkBreedEnergy = sharkBreedEnergy;
    }

    /**
     * Start-Energie der Haie.
     *
     * @param sharkStartEnergy int
     */
    public void setSharkStartEnergy(final int sharkStartEnergy)
    {
        this.sharkStartEnergy = sharkStartEnergy;
    }

    /**
     * Sterbe-Energie der Haie.
     *
     * @param sharkStarveEnergy int
     */
    public void setSharkStarveEnergy(final int sharkStarveEnergy)
    {
        this.sharkStarveEnergy = sharkStarveEnergy;
    }
}
