// Created: 28.09.2009
/**
 * 28.09.2009
 */
package de.freese.simulationen.ant;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import de.freese.simulationen.model.AbstractCell;
import de.freese.simulationen.model.EmptyCell;
import de.freese.simulationen.model.ICell;

/**
 * Zelle der Langton-Ameisen Simulation.
 *
 * @author Thomas Freese
 */
public class AntCell extends AbstractCell<AntWorld>
{
    /**
     * @author Thomas Freese
     */
    private enum Direction
    {
        /**
         *
         */
        EAST(+1, 0),
        /**
         *
         */
        NORTH(0, +1),
        /**
         *
         */
        SOUTH(0, -1),
        /**
         *
         */
        WEST(-1, 0);

        /**
         *
         */
        private static final List<Direction> DIRECTIONS;

        static
        {
            DIRECTIONS = new ArrayList<>();
            DIRECTIONS.add(NORTH);
            DIRECTIONS.add(EAST);
            DIRECTIONS.add(SOUTH);
            DIRECTIONS.add(WEST);
        }

        /**
         *
         */
        private final int frontOffsetX;

        /**
         *
         */
        private final int frontOffsetY;

        /**
         * Erstellt ein neues {@link Direction} Object.
         *
         * @param frontOffsetX int
         * @param frontOffsetY int
         */
        Direction(final int frontOffsetX, final int frontOffsetY)
        {
            this.frontOffsetX = frontOffsetX;
            this.frontOffsetY = frontOffsetY;
        }

        /**
         * Relative Koordinaten für die Zelle vor der Ameise.
         *
         * @return int[]
         */
        public int[] getFrontOffsets()
        {
            return new int[]
            {
                    this.frontOffsetX, this.frontOffsetY
            };
        }

        /**
         * Dreht die Richtung nach links.
         *
         * @return {@link Direction}
         */
        public Direction turnLeft()
        {
            int index = DIRECTIONS.indexOf(this);
            index = index == 0 ? DIRECTIONS.size() - 1 : --index;

            return DIRECTIONS.get(index);
        }

        /**
         * Dreht die Richtung nach rechts.
         *
         * @return {@link Direction}
         */
        public Direction turnRight()
        {
            int index = DIRECTIONS.indexOf(this);
            index = index == (DIRECTIONS.size() - 1) ? 0 : ++index;

            return DIRECTIONS.get(index);
        }
    }

    /**
     *
     */
    private Direction direction = Direction.NORTH;

    /**
     * Erstellt ein neues {@link AntCell} Object.
     */
    public AntCell()
    {
        super();

        setColor(Color.RED);
    }

    /**
     * <ol>
     * <li>Ist das Feld vor ihr weiss, so stellt sie sich drauf, färbt es schwarz und dreht sich um 90 Grad nach rechts.
     * <li>Ist das Feld vor ihr schwarz, so stellt sie sich drauf, färbt es weiss und dreht sich um 90 Grad nach links.
     * </ol>
     *
     * @see de.freese.simulationen.model.ICell#nextGeneration(java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    @Override
    public void nextGeneration(final Object...params)
    {
        int[] frontOffsets = this.direction.getFrontOffsets();
        int newX = getWorld().getXTorusKoord(getX(), frontOffsets[0]);
        int newY = getWorld().getYTorusKoord(getY(), frontOffsets[1]);
        int oldX = getX();
        int oldY = getY();
        ICell frontCell = getWorld().getCell(newX, newY);

        if (frontCell instanceof AntCell)
        {
            // Nicht auf eine andere Ameise treten.
            setOrientation(getWorld().getRandomOrientation());
            return;
        }

        EmptyCell<AntWorld> emptyCell = getWorld().getObjectPoolEmpty().borrowObject();

        if ((frontCell == null) || frontCell.getColor().equals(Color.WHITE))
        {
            emptyCell.setColor(Color.BLACK);
            this.direction = this.direction.turnRight();
        }
        else
        {
            emptyCell.setColor(Color.WHITE);
            this.direction = this.direction.turnLeft();
        }

        if (frontCell instanceof EmptyCell)
        {
            getWorld().getObjectPoolEmpty().returnObject((EmptyCell<AntWorld>) frontCell);
        }

        moveTo(newX, newY);
        emptyCell.moveTo(oldX, oldY);
    }

    /**
     * Setzt die Ausrichtung.
     *
     * @param orientation SwingConstants.NORTH, EAST, SOUTH, WEST
     */
    public void setOrientation(final int orientation)
    {
        switch (orientation)
        {
            case SwingConstants.NORTH:
                this.direction = Direction.NORTH;
                break;
            case SwingConstants.EAST:
                this.direction = Direction.EAST;
                break;
            case SwingConstants.SOUTH:
                this.direction = Direction.SOUTH;
                break;
            case SwingConstants.WEST:
                this.direction = Direction.WEST;
                break;

            default:
                break;
        }
    }
}
