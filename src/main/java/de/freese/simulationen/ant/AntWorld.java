// Created: 13.09.2009
/**
 * 13.09.2009
 */
package de.freese.simulationen.ant;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SwingConstants;
import de.freese.simulationen.ObjectPool;
import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.Cell;
import de.freese.simulationen.model.EmptyCell;

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
        this(width, height, (int) Math.sqrt((double) width * height) / 3);
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

        this.objectPoolEmpty = new ObjectPool<>()
        {
            /**
             * @see de.freese.simulationen.ObjectPool#activate(java.lang.Object)
             */
            @Override
            protected void activate(final EmptyCell<AntWorld> object)
            {
                object.setXY(-1, -1);
                object.setColor(null);
            }

            /**
             * @see de.freese.simulationen.ObjectPool#create()
             */
            @Override
            protected EmptyCell<AntWorld> create()
            {
                return new EmptyCell<>(AntWorld.this);
            }
        };

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
        AntCell cell = new AntCell(this);
        cell.setOrientation(getRandomOrientation());
        cell.moveTo(x, y);

        this.antCells.add(cell);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
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
        Cell cell = getCell(x, y);

        if (cell instanceof EmptyCell)
        {
            getObjectPoolEmpty().returnObject((EmptyCell<AntWorld>) cell);
        }
    }
}
