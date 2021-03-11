// Created: 11.03.2021
package de.freese.simulationen.ant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import de.freese.simulationen.ant.AntRasterCell.CellType;
import de.freese.simulationen.wator3.AbstractRasterSimulation;
import de.freese.simulationen.wator3.RasterCell;

/**
 * @author Thomas Freese
 */
public class AntRasterSimulation extends AbstractRasterSimulation
{
    /**
     * Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
     */
    private final Set<AntRasterCell> ants = new HashSet<>();

    /**
     * Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
     */
    private final Set<AntRasterCell> antsNextGeneration = Collections.synchronizedSet(new HashSet<>());

    /**
     *
     */
    private final int numberOfAnts;

    /**
     * Erstellt ein neues {@link AntRasterSimulation} Object.<br>
     * Anzahl Ameinsen bei 640x480 = Math.sqrt(width * height) / 3 ≈ 185
     *
     * @param width int
     * @param height int
     */
    public AntRasterSimulation(final int width, final int height)
    {
        this(width, height, (int) Math.sqrt((double) width * height) / 3);
    }

    /**
     * Erstellt ein neues {@link AntRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     * @param numberOfAnts int
     */
    public AntRasterSimulation(final int width, final int height, final int numberOfAnts)
    {
        super(width, height);

        this.numberOfAnts = numberOfAnts;

        fillRaster(() -> new AntRasterCell(this));
        reset();
    }

    /**
     * @param cell {@link AntRasterCell}
     */
    void addAntNextGeneration(final AntRasterCell cell)
    {
        this.antsNextGeneration.add(cell);
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterSimulation#getCell(int, int)
     */
    @Override
    protected AntRasterCell getCell(final int x, final int y)
    {
        return (AntRasterCell) super.getCell(x, y);
    }

    /**
     * Liefert eine zufällige Marsch-Richtung.
     *
     * @return int; 0 - 3
     */
    int getRandomDirection()
    {
        return getRandom().nextInt(4);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // Hier würden sämtliche Zellen verarbeitet werden, ist bei den Ameisen jedoch unnötig.
        // getCellStream().forEach(RasterCell::nextGeneration);

        // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
        this.ants.clear();
        this.ants.addAll(this.antsNextGeneration);
        this.antsNextGeneration.clear();

        this.ants.forEach(RasterCell::nextGeneration);

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterSimulation#reset()
     */
    @Override
    public void reset()
    {
        getCellStream().map(AntRasterCell.class::cast).forEach(c -> c.setCellType(CellType.EMPTY));

        for (int i = 0; i < this.numberOfAnts; i++)
        {
            // int x = getRandom().nextInt(50) + minX;
            // int y = getRandom().nextInt(50) + minY;
            int x = getRandom().nextInt(getWidth());
            int y = getRandom().nextInt(getHeight());

            AntRasterCell cell = getCell(x, y);
            cell.setCellType(CellType.ANT);
            cell.setDirection(getRandomDirection());

            addAntNextGeneration(cell);
        }

        fireCompleted();
    }
}
