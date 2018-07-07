// Created: 13.09.2009
/**
 * 13.09.2009
 */
package de.freese.simulationen.ant;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingConstants;
import de.freese.simulationen.ObjectPool;
import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.EmptyCell;
import de.freese.simulationen.model.ICell;

/**
 * Model der Langton-Ameisen Simulation.<br>
 * http://www.mathematische-basteleien.de/ameise.htm<br>
 * http://de.academic.ru/dic.nsf/dewiki/828009
 *
 * @author Thomas Freese
 */
public class AntWorld extends AbstractWorld
{
    /**
     *
     */
    private final Set<AntCell> antCells = new HashSet<>();

    /**
     *
     */
    private final int numberOfAnts;

    /**
     *
     */
    private final ObjectPool<EmptyCell<AntWorld>> objectPoolEmpty;

    /**
     * Mögliche Richtungen.
     */
    private final int[] orientations;

    /**
     * Erstellt ein neues {@link AntWorld} Object.<br>
     * Anzahl Ameinsen bei 640x480 = Math.sqrt(width * height) / 3 ≈ 185
     *
     * @param width int
     * @param height int
     */
    public AntWorld(final int width, final int height)
    {
        this(width, height, (int) Math.sqrt(width * height) / 3);
    }

    /**
     * Erstellt ein neues {@link AntWorld} Object.
     *
     * @param width int
     * @param height int
     * @param numberOfAnts int
     */
    public AntWorld(final int width, final int height, final int numberOfAnts)
    {
        super(width, height);

        if (numberOfAnts < 1)
        {
            throw new IllegalArgumentException("numberOfAnts < 1");
        }

        this.numberOfAnts = numberOfAnts;

        Supplier<EmptyCell<AntWorld>> create = () -> {
            EmptyCell<AntWorld> cell = new EmptyCell<>();
            cell.setWorld(AntWorld.this);

            return cell;
        };
        Consumer<EmptyCell<AntWorld>> activate = (cell) -> {
            cell.setXY(-1, -1);
            cell.setColor(null);
        };

        this.objectPoolEmpty = createObjectPool(create, activate);

        this.orientations = new int[]
        {
                SwingConstants.NORTH, SwingConstants.EAST, SwingConstants.SOUTH, SwingConstants.WEST
        };

        initialize();
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#getNullCellColor()
     */
    @Override
    protected Color getNullCellColor()
    {
        return Color.LIGHT_GRAY;
    }

    /**
     * @return {@link ObjectPool}<EmptyCell<AntWorld>>
     */
    ObjectPool<EmptyCell<AntWorld>> getObjectPoolEmpty()
    {
        return this.objectPoolEmpty;
    }

    /**
     * Liefert eine zufällige Marsch-Richtung.
     *
     * @return int
     */
    int getRandomOrientation()
    {
        return this.orientations[getRandom().nextInt(4)];
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#initialize()
     */
    @Override
    protected void initialize()
    {
        this.antCells.clear();

        // Etwas zentriert ansiedeln.
        // int minX = (getWidth() / 2) - 25;
        // int minY = (getHeight() / 2) - 25;

        for (int i = 0; i < this.numberOfAnts; i++)
        {
            // int x = getRandom().nextInt(50) + minX;
            // int y = getRandom().nextInt(50) + minY;
            int x = getRandom().nextInt(getWidth());
            int y = getRandom().nextInt(getHeight());

            initialize(x, y);
        }
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#initialize(int, int)
     */
    @Override
    protected void initialize(final int x, final int y)
    {
        AntCell cell = new AntCell();
        cell.setWorld(this);
        cell.setOrientation(getRandomOrientation());
        cell.moveTo(x, y);

        this.antCells.add(cell);
    }

    /**
     * @see de.freese.simulationen.model.ISimulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        this.antCells.forEach(AntCell::nextGeneration);

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#reset(int, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void reset(final int x, final int y)
    {
        ICell cell = getCell(x, y);

        if (cell instanceof EmptyCell)
        {
            getObjectPoolEmpty().returnObject((EmptyCell<AntWorld>) cell);
        }
    }
}
