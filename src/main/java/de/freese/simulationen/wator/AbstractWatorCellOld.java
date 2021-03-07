// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.awt.Color;
import java.util.Arrays;
import de.freese.simulationen.model.AbstractCell;
import de.freese.simulationen.model.Cell;

/**
 * Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public abstract class AbstractWatorCellOld extends AbstractCell<WaTorWorld> implements WatorCell
{
    /**
     *
     */
    private boolean edited;

    /**
     *
     */
    private int energy;

    /**
     *
     */
    private int[][][] nachbarn;

    /**
     * Erstellt ein neues {@link AbstractWatorCellOld} Object.
     *
     * @param world {@link WaTorWorld}
     * @param color {@link Color}
     */
    protected AbstractWatorCellOld(final WaTorWorld world, final Color color)
    {
        super(world, color);
    }

    /**
     * Erniedrigt den Energiewert um 1.
     */
    void decrementEnergy()
    {
        this.energy--;
    }

    /**
     * Ermittelt die Nachbarn dieser Zelle.<br>
     * Es werden nur vertikale und horizontale Nachbarn berücksichtigt.
     */
    void ermittleNachbarn()
    {
        int xWest = getWorld().getXTorusKoord(getX(), -1);
        int xOst = getWorld().getXTorusKoord(getX(), +1);
        int ySued = getWorld().getYTorusKoord(getY(), -1);
        int yNord = getWorld().getYTorusKoord(getY(), +1);

        int[][] freie = new int[0][2];
        int[][] fische = new int[0][2];
        // int[][] haie = new int[0][2];

        int[][] cells = new int[4][2];

        // Nord
        cells[0][0] = getX();
        cells[0][1] = yNord;

        // Ost
        cells[1][0] = xOst;
        cells[1][1] = getY();

        // Süd
        cells[2][0] = getX();
        cells[2][1] = ySued;

        // West
        cells[3][0] = xWest;
        cells[3][1] = getY();

        for (int[] richtung : cells)
        {
            int x = richtung[0];
            int y = richtung[1];

            Cell cell = getWorld().getCell(x, y);

            if (cell == null)
            {
                freie = Arrays.copyOf(freie, freie.length + 1);
                freie[freie.length - 1] = richtung;
            }
            else if (cell instanceof FishCell)
            {
                fische = Arrays.copyOf(fische, fische.length + 1);
                fische[fische.length - 1] = richtung;

                // Workaround: Verhindert falsche Koordinaten bei paralleler Verarbeitung.
                // Verhindert auch zusätzlich das Auftreten von Wellenfronten.
                ((FishCell) cell).setEdited(true);
            }
            // else if (cell instanceof SharkCell)
            // {
            // haie = Arrays.copyOf(haie, haie.length + 1);
            // haie[haie.length - 1] = cell2;
            // }
        }

        this.nachbarn = new int[][][]
        {
                freie, fische
                // , haie
        };
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#getEnergy()
     */
    @Override
    public int getEnergy()
    {
        return this.energy;
    }

    /**
     * Liefert die Koordinaten der Fisch Nachbar-Zellen.
     *
     * @return int[][]; Index 0=x, 1=y
     */
    protected int[][] getFischNachbarn()
    {
        return this.nachbarn[1];
    }

    /**
     * Liefert die Koordinaten der freien Nachbar-Zellen.
     *
     * @return int[][]; Index 0=x, 1=y
     */
    protected int[][] getFreieNachbarn()
    {
        return this.nachbarn[0];
    }

    /**
     * Erhoeht den Energiewert um 1.
     */
    void incrementEnergy()
    {
        this.energy++;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#isEdited()
     */
    @Override
    public boolean isEdited()
    {
        return this.edited;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#setEdited(boolean)
     */
    @Override
    public void setEdited(final boolean edited)
    {
        this.edited = edited;
    }

    /**
     * @see de.freese.simulationen.wator.WatorCell#setEnergy(int)
     */
    @Override
    public void setEnergy(final int energy)
    {
        this.energy = energy;
    }
}
