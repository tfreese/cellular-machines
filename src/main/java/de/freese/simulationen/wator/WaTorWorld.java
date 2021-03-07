// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import de.freese.simulationen.ObjectPool;
import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.Cell;

/**
 * Model der WaTor-Simulation.<br>
 * http://de.academic.ru/dic.nsf/dewiki/1492493
 *
 * @author Thomas Freese
 */
public class WaTorWorld extends AbstractWorld
{
    /**
     * Richtung der Berechnung.
     */
    private int direction;

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
    private final ObjectPool<FishCell> objectPoolFish;

    /**
     *
     */
    private final ObjectPool<SharkCell> objectPoolShark;

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
     * Erstellt ein neues {@link WaTorWorld} Object.
     *
     * @param width int
     * @param height int
     */
    public WaTorWorld(final int width, final int height)
    {
        super(width, height);

        this.objectPoolFish = new ObjectPool<>()
        {
            /**
             * @see de.freese.simulationen.ObjectPool#activate(java.lang.Object)
             */
            @Override
            protected void activate(final FishCell object)
            {
                object.setXY(-1, -1);
                object.setEnergy(getFishStartEnergy());
                object.setEdited(false);
            }

            /**
             * @see de.freese.simulationen.ObjectPool#create()
             */
            @Override
            protected FishCell create()
            {
                return new FishCellOld(WaTorWorld.this);
            }
        };

        this.objectPoolShark = new ObjectPool<>()
        {
            /**
             * @see de.freese.simulationen.ObjectPool#activate(java.lang.Object)
             */
            @Override
            protected void activate(final SharkCell object)
            {
                object.setXY(-1, -1);
                object.setEnergy(getSharkStartEnergy());
                object.setEdited(false);
            }

            /**
             * @see de.freese.simulationen.ObjectPool#create()
             */
            @Override
            protected SharkCell create()
            {
                return new SharkCellOld(WaTorWorld.this);
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
     * @return int
     */
    public int getFishCounter()
    {
        return getObjectPoolFish().getNumActive();
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
     * @see de.freese.simulationen.model.AbstractWorld#getNullCellColor()
     */
    @Override
    protected Color getNullCellColor()
    {
        return Color.BLACK;
    }

    /**
     * @return {@link ObjectPool}
     */
    ObjectPool<FishCell> getObjectPoolFish()
    {
        return this.objectPoolFish;
    }

    /**
     * @return {@link ObjectPool}
     */
    ObjectPool<SharkCell> getObjectPoolShark()
    {
        return this.objectPoolShark;
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
     * @return int
     */
    public int getSharkCounter()
    {
        return getObjectPoolShark().getNumActive();
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
     * Start-Energie der Haie.
     *
     * @return int
     */
    public int getSharkStarveEnergy()
    {
        return this.sharkStarveEnergy;
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#initialize(int, int)
     */
    @Override
    protected void initialize(final int x, final int y)
    {
        // Zufällige Platzierung.
        int type = getRandom().nextInt(10);

        Cell cell = switch (type)
        {
            case 1 -> getObjectPoolFish().borrowObject();
            case 2 -> getObjectPoolShark().borrowObject();

            default -> null;
        };

        if (cell != null)
        {
            cell.moveTo(x, y);
        }
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // @formatter:off
        Stream.of(getCells())
            .parallel()
            .flatMap(Stream::of)
            .filter(Objects::nonNull)
            .map(WatorCell.class::cast)
            .forEach(c -> {
                c.setEdited(false);

                if(c instanceof AbstractWatorCellNew)
                {
                    ((AbstractWatorCellNew) c).ermittleNachbarn();
                }
              }
            );

        Stream.of(getCells())
            .parallel()
            .flatMap(Stream::of)
            .filter(Objects::nonNull)
            .forEach(Cell::nextGeneration);
        // @formatter:on

        // nextGenerationNestedFor();
        // nextGenerationStreams();

        fireCompleted();
    }

    /**
     * Alte Berechnung.<br>
     * Richtung der Berechnung ändern, um Wellenfronten zu vermeiden.<br>
     * Siehe auch AbstractWatorCell.ermittleNachbarn.
     */
    void nextGenerationNestedFor()
    {
        if (this.direction == 0)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                for (int y = 0; y < getHeight(); y++)
                {
                    Cell cell = getCell(x, y);

                    if (cell != null)
                    {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (this.direction == 1)
        {
            for (int x = getWidth() - 1; x >= 0; x--)
            {
                for (int y = 0; y < getHeight(); y++)
                {
                    Cell cell = getCell(x, y);

                    if (cell != null)
                    {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (this.direction == 2)
        {
            for (int x = getWidth() - 1; x >= 0; x--)
            {
                for (int y = getHeight() - 1; y >= 0; y--)
                {
                    Cell cell = getCell(x, y);

                    if (cell != null)
                    {
                        cell.nextGeneration();
                    }
                }
            }
        }
        else if (this.direction == 3)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                for (int y = getHeight() - 1; y >= 0; y--)
                {
                    Cell cell = getCell(x, y);

                    if (cell != null)
                    {
                        cell.nextGeneration();
                    }
                }
            }
        }

        this.direction++;

        if (this.direction == 4)
        {
            this.direction = 0;
        }
    }

    /**
     * Alte Berechnung.<br>
     * Richtung der Berechnung ändern, um Wellenfronten zu vermeiden.<br>
     * Siehe auch AbstractWatorCell.ermittleNachbarn.
     */
    void nextGenerationStreams()
    {
        if (this.direction == 0)
        {
            // @formatter:off
            IntStream.range(0, getWidth())
                .parallel()
                .forEach(x ->
                {
                    for (int y = 0; y < getHeight(); y++)
                    {
                        Cell cell = getCell(x, y);

                        if (cell != null)
                        {
                            cell.nextGeneration();
                        }
                    }
                });
            // @formatter:on
        }
        else if (this.direction == 1)
        {
            // @formatter:off
            IntStream.rangeClosed(getWidth() - 1, 0)
                .parallel()
                .forEach(x ->
                {
                    for (int y = 0; y < getHeight(); y++)
                    {
                        Cell cell = getCell(x, y);

                        if (cell != null)
                        {
                            cell.nextGeneration();
                        }
                    }
                });
            // @formatter:on
        }
        else if (this.direction == 2)
        {
            // @formatter:off
            IntStream.rangeClosed(getWidth() - 1, 0)
                .parallel()
                .forEach(x ->
                {
                    for (int y = getHeight() - 1; y >= 0; y--)
                    {
                        Cell cell = getCell(x, y);

                        if (cell != null)
                        {
                            cell.nextGeneration();
                        }
                    }
                });
            // @formatter:on
        }
        else if (this.direction == 3)
        {
            // @formatter:off
            IntStream.range(0, getWidth())
                .parallel()
                .forEach(x ->
                {
                    for (int y = getHeight() - 1; y >= 0; y--)
                    {
                        Cell cell = getCell(x, y);

                        if (cell != null)
                        {
                            cell.nextGeneration();
                        }
                    }
                });
            // @formatter:on
        }

        this.direction++;

        if (this.direction == 4)
        {
            this.direction = 0;
        }
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        Cell cell = getCell(x, y);

        if (cell instanceof FishCell)
        {
            getObjectPoolFish().returnObject((FishCell) cell);
        }
        else if (cell instanceof SharkCell)
        {
            getObjectPoolShark().returnObject((SharkCell) cell);
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
     * Start-Energie der Haie.
     *
     * @param sharkStarveEnergy int
     */
    public void setSharkStarveEnergy(final int sharkStarveEnergy)
    {
        this.sharkStarveEnergy = sharkStarveEnergy;
    }
}
