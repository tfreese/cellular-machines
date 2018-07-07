// Created: 13.09.2009
/**
 * 13.09.2009
 */
package de.freese.simulationen.gameoflife;

import java.awt.Color;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import de.freese.simulationen.ObjectPool;
import de.freese.simulationen.model.AbstractWorld;

/**
 * Model für die "Game of Life"-Simulation.<br>
 * http://www.mathematische-basteleien.de/gameoflife.htm<br>
 * http://de.academic.ru/dic.nsf/dewiki/279011
 *
 * @author Thomas Freese
 */
public class GoFWorld extends AbstractWorld
{
    /**
     *
     */
    private final ObjectPool<GoFCell> objectPool;

    /**
     * Erstellt ein neues {@link GoFWorld} Object.
     *
     * @param width int
     * @param height int
     */
    public GoFWorld(final int width, final int height)
    {
        super(width, height);

        Supplier<GoFCell> create = () -> {
            GoFCell cell = new GoFCell();
            cell.setWorld(GoFWorld.this);

            return cell;
        };
        Consumer<GoFCell> activate = (cell) -> {
            cell.setXY(-1, -1);
            cell.setAlive(getRandom().nextBoolean());
        };

        this.objectPool = createObjectPool(create, activate);

        initialize();
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#getCell(int, int)
     */
    @Override
    public GoFCell getCell(final int x, final int y)
    {
        return (GoFCell) super.getCell(x, y);
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#getNullCellColor()
     */
    @Override
    protected Color getNullCellColor()
    {
        return Color.WHITE;
    }

    /**
     * @return {@link ObjectPool}<GoFCell>
     */
    ObjectPool<GoFCell> getObjectPool()
    {
        return this.objectPool;
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#initialize(int, int)
     */
    @Override
    protected void initialize(final int x, final int y)
    {
        GoFCell cell = getObjectPool().borrowObject();
        cell.moveTo(x, y);

        // Gleiter
        // getCells()[21][20] = true;
        // getCells()[22][21] = true;
        // getCells()[20][20] = true;
        // getCells()[20][21] = true;
        // getCells()[20][22] = true;
    }

    /**
     * @see de.freese.simulationen.model.ISimulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // @formatter:off
        Stream.of(getCells())
            .parallel()
            .flatMap(Stream::of)
            //.parallel()
            .filter(Objects::nonNull)
            .map(c -> (GoFCell) c)
            .forEach(GoFCell::ermittleLebendeBachbarn);

        Stream.of(getCells())
            .parallel()
            .flatMap(Stream::of)
            //.parallel()
            .filter(Objects::nonNull)
            .map(c -> (GoFCell) c)
            .forEach(GoFCell::nextGeneration);
        // @formatter:on

        // for (int x = 0; x < getWidth(); x++)
        // {
        // for (int y = 0; y < getHeight(); y++)
        // {
        // GoFCell cell = getCell(x, y);
        //
        // if (cell != null)
        // {
        // cell.ermittleLebendeBachbarn();
        // }
        // }
        // }

        // for (int x = 0; x < getWidth(); x++)
        // {
        // for (int y = 0; y < getHeight(); y++)
        // {
        // GoFCell cell = getCell(x, y);
        //
        // if (cell != null)
        // {
        // cell.nextGeneration();
        // }
        // }
        // }

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        GoFCell cell = getCell(x, y);
        getObjectPool().returnObject(cell);
    }
}
