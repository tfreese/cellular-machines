// Created: 04.10.2009
/**
 * 04.10.2009
 */
package de.freese.simulationen.wator;

import java.util.Arrays;
import de.freese.simulationen.model.AbstractCell;
import de.freese.simulationen.model.ICell;

/**
 * Zelle der WaTor-Simulation.
 *
 * @author Thomas Freese
 */
public abstract class AbstractWatorCell extends AbstractCell<WaTorWorld>
{
    /**
     *
     */
    private boolean edited;

    /**
     *
     */
    private int energy = 0;

    /**
     *
     */
    private int[][][] nachbarn;

    /**
     * Erstellt ein neues {@link AbstractWatorCell} Object.
     */
    public AbstractWatorCell()
    {
        super();
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

        // Norden
        cells[0][0] = getX();
        cells[0][1] = yNord;

        // Osten
        cells[1][0] = xOst;
        cells[1][1] = getY();

        // Süden
        cells[2][0] = getX();
        cells[2][1] = ySued;

        // Westen
        cells[3][0] = xWest;
        cells[3][1] = getY();

        for (int[] cell2 : cells)
        {
            int x = cell2[0];
            int y = cell2[1];

            ICell cell = getWorld().getCell(x, y);

            if (cell == null)
            {
                freie = Arrays.copyOf(freie, freie.length + 1);
                freie[freie.length - 1] = cell2;
            }
            else if (cell instanceof FishCell)
            {
                fische = Arrays.copyOf(fische, fische.length + 1);
                fische[fische.length - 1] = cell2;

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
     * Liefert den Energiewert der Zelle.
     *
     * @return int
     */
    int getEnergy()
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
     * True, wenn diese Zelle in einem Zyklus schon mal verarbeitet wurde.
     *
     * @return boolean
     */
    boolean isEdited()
    {
        return this.edited;
    }

    /**
     * True, wenn diese Zelle in einem Zyklus schon mal verarbeitet wurde.
     *
     * @param edited boolean
     */
    void setEdited(final boolean edited)
    {
        this.edited = edited;
    }

    /**
     * @param energy int
     */
    void setEnergy(final int energy)
    {
        this.energy = energy;
    }
}
