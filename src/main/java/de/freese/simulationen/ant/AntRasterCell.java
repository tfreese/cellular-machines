// Created: 11.03.2021
package de.freese.simulationen.ant;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import de.freese.simulationen.wator3.AbstractRasterCell;

/**
 * Zelle der Langton-Ameisen Simulation.
 *
 * @author Thomas Freese
 */
public class AntRasterCell extends AbstractRasterCell
{
    /**
     * @author Thomas Freese
     */
    public enum CellType
    {
        /**
         *
         */
        ANT,

        /**
         *
         */
        BLACK,

        /**
        *
        */
        EMPTY,

        /**
         *
         */
        WHITE,
    }

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
    private CellType cellType;

    /**
    *
    */
    private Direction direction = Direction.NORTH;

    /**
     * Erstellt ein neues {@link AntRasterCell} Object.
     *
     * @param simulation {@link AntRasterSimulation}
     */
    public AntRasterCell(final AntRasterSimulation simulation)
    {
        super(simulation);
    }

    /**
     * @see de.freese.simulationen.wator3.AbstractRasterCell#getSimulation()
     */
    @Override
    protected AntRasterSimulation getSimulation()
    {
        return (AntRasterSimulation) super.getSimulation();
    }

    /**
     * @return boolean
     */
    public boolean isAnt()
    {
        return CellType.ANT.equals(this.cellType);
    }

    /**
     * @return boolean
     */
    public boolean isEmpty()
    {
        return CellType.EMPTY.equals(this.cellType);
    }

    /**
     * <ol>
     * <li>Ist das Feld vor ihr weiss, so stellt sie sich drauf, färbt es schwarz und dreht sich um 90 Grad nach rechts.
     * <li>Ist das Feld vor ihr schwarz, so stellt sie sich drauf, färbt es weiss und dreht sich um 90 Grad nach links.
     * </ol>
     *
     * @see de.freese.simulationen.wator3.RasterCell#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        if (!isAnt())
        {
            return;
        }

        final int[] frontOffsets = this.direction.getFrontOffsets();
        final int newX = getSimulation().getXTorusKoord(getX(), frontOffsets[0]);
        final int newY = getSimulation().getYTorusKoord(getY(), frontOffsets[1]);

        final AntRasterCell frontCell = getSimulation().getCell(newX, newY);

        if (frontCell.isAnt())
        {
            // Nicht auf eine andere Ameise treten.
            setDirection(getSimulation().getRandomDirection());

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addAntNextGeneration(this);

            return;
        }

        if (Color.WHITE.equals(frontCell.getColor()) || frontCell.isEmpty())
        {
            frontCell.setCellType(CellType.ANT);
            setCellType(CellType.BLACK);
            frontCell.direction = this.direction.turnRight();

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addAntNextGeneration(frontCell);
        }
        else if (Color.BLACK.equals(frontCell.getColor()) || frontCell.isEmpty())
        {
            frontCell.setCellType(CellType.ANT);
            setCellType(CellType.WHITE);
            frontCell.direction = this.direction.turnLeft();

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addAntNextGeneration(frontCell);
        }
        else
        {
            // Verhindert 'fest steckende' Ameisen.
            setDirection(getSimulation().getRandomDirection());

            // Performance-Optimierung: Nur die Ameisen verarbeiten lassen.
            getSimulation().addAntNextGeneration(this);
        }
    }

    /**
     * @param cellType {@link CellType}
     */
    public void setCellType(final CellType cellType)
    {
        this.cellType = cellType;

        switch (cellType)
        {
            case ANT -> setColor(Color.RED);
            case BLACK -> setColor(Color.BLACK);
            case WHITE -> setColor(Color.WHITE);

            default -> setColor(Color.LIGHT_GRAY);
        }
    }

    /**
     * Setzt die Ausrichtung.
     *
     * @param orientation int; 0 - 3
     */
    public void setDirection(final int orientation)
    {
        switch (orientation)
        {
            case 0:
                this.direction = Direction.NORTH;
                break;
            case 1:
                this.direction = Direction.EAST;
                break;
            case 2:
                this.direction = Direction.SOUTH;
                break;
            case 3:
                this.direction = Direction.WEST;
                break;

            default:
                break;
        }
    }
}