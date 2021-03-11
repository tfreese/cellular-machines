// Created: 09.03.2021
package de.freese.simulationen.wator3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import de.freese.simulationen.wator3.WatorCell.CellType;

/**
 * Model der WaTor-Simulation.<br>
 * http://de.academic.ru/dic.nsf/dewiki/1492493
 *
 * @author Thomas Freese
 */
public class WatorRasterSimulation extends AbstractRasterSimulation
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
     * Erstellt ein neues {@link WatorRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    public WatorRasterSimulation(final int width, final int height)
    {
        super(width, height);

        fillRaster(() -> new WatorCell(this));
        reset();
    }

    /**
     * @return int[]; 0 = Anzahl Fische, 1 = Anzahl Haie
     */
    public int[] countFishesAndSharks()
    {
        AtomicInteger fische = new AtomicInteger(0);
        AtomicInteger haie = new AtomicInteger(0);

        getCellStream().map(WatorCell.class::cast).forEach(cell -> {
            if (cell.isFish())
            {
                fische.incrementAndGet();
            }
            else if (cell.isShark())
            {
                haie.incrementAndGet();
            }
        });

        return new int[]
        {
                fische.intValue(), haie.intValue()
        };
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterSimulation#getCell(int, int)
     */
    @Override
    protected WatorCell getCell(final int x, final int y)
    {
        return (WatorCell) super.getCell(x, y);
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
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        getCellStream().forEach(RasterCell::nextGeneration);
        // nextGenerationNestedFor();
        // nextGenerationStreams();

        fireCompleted();
    }

    /**
     * Alte Berechnung.<br>
     * Richtung der Berechnung ändern, um Wellenfronten zu vermeiden.<br>
     */
    void nextGenerationNestedFor()
    {
        if (this.direction == 0)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                for (int y = 0; y < getHeight(); y++)
                {
                    WatorCell cell = getCell(x, y);

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
                    WatorCell cell = getCell(x, y);

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
                    WatorCell cell = getCell(x, y);

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
                    WatorCell cell = getCell(x, y);

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
                        WatorCell cell = getCell(x, y);

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
                        WatorCell cell = getCell(x, y);

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
                        WatorCell cell = getCell(x, y);

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
                        WatorCell cell = getCell(x, y);

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
     * @see de.freese.simulationen.wator3.AbstractRasterSimulation#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        // Zufällige Platzierung.
        int rand = getRandom().nextInt(10);

        WatorCell cell = getCell(x, y);

        switch (rand)
        {
            case 3 ->
            {
                cell.setCellType(CellType.FISH);
                cell.setEnergy(getFishStartEnergy());

            }
            case 6 ->
            {
                cell.setCellType(CellType.SHARK);
                cell.setEnergy(getSharkStartEnergy());
            }

            default -> cell.setCellType(CellType.EMPTY);
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
