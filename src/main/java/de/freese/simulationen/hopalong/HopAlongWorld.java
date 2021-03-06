// Created: 13.09.2009
/**
 * 13.09.2009
 */
package de.freese.simulationen.hopalong;

import java.awt.Color;
import java.awt.Point;
import de.freese.simulationen.ObjectPool;
import de.freese.simulationen.model.AbstractWorld;
import de.freese.simulationen.model.EmptyCell;

/**
 * Model fuer die "Hop along"-Simulation.<br>
 * http://www.mathematische-basteleien.de/huepfer.htm
 *
 * @author Thomas Freese
 */
public class HopAlongWorld extends AbstractWorld
{
    /**
     *
     */
    private final Point center;

    /**
     *
     */
    private final ObjectPool<EmptyCell<HopAlongWorld>> objectPool;

    /**
     *
     */
    private double x;

    /**
     *
     */
    private double y;

    /**
     * Erstellt ein neues {@link HopAlongWorld} Object.
     *
     * @param width int
     * @param height int
     */
    public HopAlongWorld(final int width, final int height)
    {
        super(width, height);

        this.center = new Point(width / 2, height / 2);

        this.objectPool = new ObjectPool<>()
        {
            /**
             * @see de.freese.simulationen.ObjectPool#activate(java.lang.Object)
             */
            @Override
            protected void activate(final EmptyCell<HopAlongWorld> object)
            {
                object.setXY(-1, -1);
            }

            /**
             * @see de.freese.simulationen.ObjectPool#create()
             */
            @Override
            protected EmptyCell<HopAlongWorld> create()
            {
                EmptyCell<HopAlongWorld> cell = new EmptyCell<>(HopAlongWorld.this, Color.BLACK);

                return cell;
            }
        };

        initialize();
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#getCell(int, int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public EmptyCell<HopAlongWorld> getCell(final int x, final int y)
    {
        return (EmptyCell<HopAlongWorld>) super.getCell(x, y);
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
     * @see de.freese.simulationen.model.AbstractWorld#initialize(int, int)
     */
    @Override
    protected void initialize(final int x, final int y)
    {
        this.x = this.center.x;
        this.y = this.center.y;
        // this.x = 100.1d;
        // this.y = 100.1d;
        // this.x = 1d;
        // this.y = 1d;
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // INPUT num
        // INPUT a, b, c
        // x = 0
        // y = 0
        // PLOT(x, y)
        // FOR i = 1 TO num
        // xx = y - SIGN(x) * [ABS(b*x - c)]^0.5
        // yy = a - x
        // x = xx
        // y = yy

        // double a = 0.01d;
        // double b = -0.3d;
        // double c = 0.003d;
        double a = -14D;
        double b = 0.9F;
        double c = 0.1D;

        double xx = this.y - (Math.signum(this.x) * Math.sqrt(Math.abs((b * this.x) - c)));
        // // double xx = y - (FastMath.signum(x) * FastMath.pow(Math.abs((b * x) - c), 0.5D));
        // // double xx = this.y + (FastMath.signum(this.x) * Math.abs((b * this.x) - c));
        double yy = a - this.x;
        this.x = xx;
        this.y = yy;

        // double a = 80.0d;
        // this.x = (this.x * Math.cos(a)) + (((this.x * this.x) - this.y) * Math.sin(a));
        // this.y = (this.x * Math.sin(a)) - (((this.x * this.x) - this.y) * Math.cos(a));

        // Gingerbreadman
        // this.x = (1 - this.y) + Math.abs(this.x);
        // this.y = this.x;
        //
        // int newX = this.x + this.center.x;
        // int newY = this.y + this.center.y;
        //

        // if (Double.isNaN(xx) || Double.isNaN(yy))
        // {
        // return;
        // }

        // int newX = (int) this.x + this.center.x;
        // int newY = (int) this.y + this.center.y;
        //
        // if ((newX < 0) || (newY < 0))
        // {
        // return;
        // }
        //
        // if ((newX >= getWidth()) || (newY >= getHeight()))
        // {
        // return;
        // }

        int newX = (int) this.x;
        int newY = (int) this.y;

        if (newX < 0)
        {
            newX = getXTorusKoord(0, newX);
            // return;
        }
        else if (newX >= getWidth())
        {
            newX = getXTorusKoord(getWidth(), getWidth() - newX);
            // return;
        }

        if (newY < 0)
        {
            newY = getYTorusKoord(0, newY);
            // return;
        }
        else if (newX >= getWidth())
        {
            newY = getYTorusKoord(getHeight(), getHeight() - newY);
            // return;
        }

        EmptyCell<HopAlongWorld> cell = this.objectPool.borrowObject();

        reset(newX, newY);
        cell.moveTo(newX, newY);

        fireCompleted();
    }

    /**
     * @see de.freese.simulationen.model.AbstractWorld#reset(int, int)
     */
    @Override
    protected void reset(final int x, final int y)
    {
        EmptyCell<HopAlongWorld> cell = getCell(x, y);

        if (cell != null)
        {
            this.objectPool.returnObject(cell);
        }
    }
}
